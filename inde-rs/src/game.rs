use serde::{Deserialize, Serialize};
use std::collections::{HashSet, VecDeque};

/// Progress status values stored in `game_progress.status`.
pub const STATUS_IN: i64 = 0;
pub const STATUS_FINISHED: i64 = 1;
pub const STATUS_INTERRUPTED: i64 = 2;

/// Coins and points awarded per elapsed game day.
pub const COINS_PER_DAY: i64 = 10;
pub const POINTS_PER_DAY: i64 = 10;
/// The in-game day rolls over at 07:00 Asia/Shanghai.
pub const REFRESH_HOUR: i64 = 7;
pub const DAY_SECS: i64 = 86_400;
pub const TZ_OFFSET_SECS: i64 = 8 * 3600;

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Script {
    pub start: String,
    pub nodes: Vec<Node>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Node {
    pub id: String,
    /// `scene` or `end`
    pub kind: String,
    pub description: String,
    #[serde(default)]
    pub choices: Vec<Choice>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Choice {
    pub description: String,
    pub next: String,
    #[serde(default, skip_serializing_if = "Option::is_none")]
    pub weight: Option<f64>,
}

impl Script {
    pub fn parse(raw: &str) -> Result<Script, String> {
        serde_json::from_str::<Script>(raw).map_err(|e| format!("剧本 JSON 解析失败：{e}"))
    }

    pub fn node(&self, id: &str) -> Option<&Node> {
        self.nodes.iter().find(|n| n.id == id)
    }

    pub fn validate(&self) -> Result<(), String> {
        if self.start.trim().is_empty() {
            return Err("缺少起始节点 start".to_string());
        }
        if self.nodes.is_empty() {
            return Err("剧本至少需要一个节点".to_string());
        }
        let mut ids = HashSet::new();
        for node in &self.nodes {
            if node.id.trim().is_empty() {
                return Err("节点 id 不能为空".to_string());
            }
            if !ids.insert(node.id.as_str()) {
                return Err(format!("节点 id 重复：{}", node.id));
            }
        }
        if !ids.contains(self.start.as_str()) {
            return Err(format!("起始节点 {} 不存在", self.start));
        }
        for node in &self.nodes {
            match node.kind.as_str() {
                "scene" => {
                    if node.description.trim().is_empty() {
                        return Err(format!("场景 {} 的描述不能为空", node.id));
                    }
                    if node.choices.is_empty() {
                        return Err(format!("场景 {} 至少需要一个选项", node.id));
                    }
                    for choice in &node.choices {
                        if choice.description.trim().is_empty() {
                            return Err(format!("场景 {} 存在空选项", node.id));
                        }
                        if !ids.contains(choice.next.as_str()) {
                            return Err(format!(
                                "场景 {} 的选项「{}」指向不存在的节点 {}",
                                node.id, choice.description, choice.next
                            ));
                        }
                    }
                }
                "end" => {
                    if !node.choices.is_empty() {
                        return Err(format!("结局节点 {} 不能包含选项", node.id));
                    }
                }
                other => {
                    return Err(format!(
                        "节点 {} 的类型必须是 scene 或 end，当前为 {}",
                        node.id, other
                    ))
                }
            }
        }

        // Reachability: every node must be reachable, and at least one ending must be reachable.
        let mut seen: HashSet<&str> = HashSet::new();
        let mut queue: VecDeque<&str> = VecDeque::new();
        seen.insert(self.start.as_str());
        queue.push_back(self.start.as_str());
        let mut reachable_end = false;
        while let Some(id) = queue.pop_front() {
            let Some(node) = self.node(id) else {
                continue;
            };
            if node.kind == "end" {
                reachable_end = true;
            }
            for choice in &node.choices {
                if seen.insert(choice.next.as_str()) {
                    queue.push_back(choice.next.as_str());
                }
            }
        }
        for node in &self.nodes {
            if !seen.contains(node.id.as_str()) {
                return Err(format!("节点 {} 从起点不可达", node.id));
            }
        }
        if !reachable_end {
            return Err("剧本没有可达的结局节点".to_string());
        }
        Ok(())
    }

    /// Resolve a 1-based choice index to the next node.
    pub fn pick<'a>(&'a self, node: &Node, index: usize) -> Result<&'a Node, String> {
        if index == 0 || index > node.choices.len() {
            return Err("选项无效，请重新选择".to_string());
        }
        let choice = &node.choices[index - 1];
        self.node(&choice.next)
            .ok_or_else(|| "剧本数据缺失，请联系管理员".to_string())
    }
}

pub fn pretty_script(raw: &str) -> Result<String, String> {
    let script = Script::parse(raw)?;
    script.validate()?;
    serde_json::to_string_pretty(&script).map_err(|e| format!("剧本 JSON 序列化失败：{e}"))
}

/// The in-game day index (rolls over at 07:00 UTC+8).
pub fn game_day(ts: i64) -> i64 {
    (ts + TZ_OFFSET_SECS - REFRESH_HOUR * 3600).div_euclid(DAY_SECS)
}

/// Number of elapsed reward days since the last award.
pub fn elapsed_days(last_signin_at: i64, created_at: i64, now: i64) -> i64 {
    let last = if last_signin_at > 0 {
        last_signin_at
    } else {
        created_at
    };
    let days = game_day(now) - game_day(last);
    days.max(0)
}

pub fn status_label(status: i64) -> &'static str {
    match status {
        STATUS_IN => "进行中",
        STATUS_FINISHED => "已完结",
        STATUS_INTERRUPTED => "已中断",
        _ => "未知",
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use chrono::{FixedOffset, TimeZone};

    fn seed() -> Script {
        Script::parse(
            r#"{
              "start": "s1",
              "nodes": [
                {"id":"s1","kind":"scene","description":"场景1","choices":[
                  {"description":"不，选我","next":"e1"},
                  {"description":"选我","next":"e2"}]},
                {"id":"e1","kind":"end","description":"失败","choices":[]},
                {"id":"e2","kind":"end","description":"成功","choices":[]}
              ]
            }"#,
        )
        .unwrap()
    }

    #[test]
    fn seed_script_is_valid() {
        seed().validate().unwrap();
    }

    #[test]
    fn duplicate_node_ids_rejected() {
        let mut script = seed();
        script.nodes.push(script.nodes[0].clone());
        assert!(script.validate().is_err());
    }

    #[test]
    fn missing_next_node_rejected() {
        let mut script = seed();
        script.nodes[0].choices[0].next = "nope".into();
        assert!(script.validate().is_err());
    }

    #[test]
    fn unreachable_node_rejected() {
        let mut script = seed();
        script.nodes.push(Node {
            id: "orphan".into(),
            kind: "end".into(),
            description: "孤岛".into(),
            choices: vec![],
        });
        assert!(script.validate().is_err());
    }

    #[test]
    fn choice_index_is_one_based() {
        let script = seed();
        let start = script.node("s1").unwrap();
        assert!(script.pick(start, 0).is_err());
        assert_eq!(script.pick(start, 1).unwrap().description, "失败");
        assert_eq!(script.pick(start, 2).unwrap().description, "成功");
        assert!(script.pick(start, 3).is_err());
    }

    fn ts(y: i32, m: u32, d: u32, h: u32, min: u32) -> i64 {
        FixedOffset::east_opt(8 * 3600)
            .unwrap()
            .with_ymd_and_hms(y, m, d, h, min, 0)
            .unwrap()
            .timestamp()
    }

    #[test]
    fn day_rolls_over_at_seven_am() {
        let late = ts(2024, 1, 1, 23, 59);
        let early = ts(2024, 1, 2, 6, 59);
        let morning = ts(2024, 1, 2, 7, 0);
        assert_eq!(game_day(late), game_day(early));
        assert_eq!(game_day(morning), game_day(late) + 1);
    }

    #[test]
    fn elapsed_days_matches_legacy_behaviour() {
        // Created just before the 07:00 rollover, checked the next morning: two days.
        let created = ts(2024, 1, 1, 6, 0);
        let now = ts(2024, 1, 2, 8, 0);
        assert_eq!(elapsed_days(0, created, now), 2);
        // Same game day: nothing.
        assert_eq!(elapsed_days(now, created, now), 0);
        // Exactly one rollover later.
        let later = ts(2024, 1, 3, 7, 0);
        assert_eq!(elapsed_days(now, created, later), 1);
    }
}
