package cn.kykys.index.utils.game;

import cn.kykys.index.model.ext.ChooseModel;

import java.util.List;

/**
 * Created by kuangye on 2016/11/16.
 */
public final class Settings {

    public static final Integer COINS_SEED = 10, POINTS_SEED = 10;


    public static final Integer REFRESH_TIME = 7;


    public static final String MAIN_MENU = new StringBuffer()
            .append("您好，我是kyy，请回复文字选择服务：").append("\n")
            .append("\n")
            .append("[说明]  查看游戏说明").append("\n")
            .append("[升级]  提升个人等级（需要当前等级对应积分数）").append("\n")
            .append("[0]  个人状态/签到").append("\n")
            .append("\n")
            .append("（暂未开通） kyy的歌（随机一首）").append("\n")
            .append("网站<a href=\"https://kykys.cn/game\">https://kykys.cn/game</a>").append("\n")
            .append("\n")
            .append("回复 ? 显示此帮助菜单")
            .toString();


    public static final String EXPLAIN = new StringBuffer()
            .append("游戏指南:").append("\n")
            .append("\n")
            .append("游戏采用指令的形式进行 （进入剧本后只需回复选项数字即可）").append("\n")
            .append("[指令][改名 张三] 中间为空格 改名").append("\n")
            .append("[指令][进入 1] 中间为空格 进入指定剧本").append("\n")
            .append("[指令][退出 1] 中间为空格 退出某剧本").append("\n")
            .append("[指令][重置 1] 中间为空格 重置某剧本进度（重新开始）").append("\n")
            .append("等待完善")
            .toString();


    public static final String GAME_REAL_TIME_INFO = new StringBuffer()
            .append("姓名:{0} ").append("\n")
            .append("金币:{1} ").append("\n")
            .append("积分:{2} ").append("\n")
            .append("等级:{3} ").append("\n")
            .append("上次登录时间:{4} ").append("\n")
            .append("{5}").append("\n")
            .append("\n")
            .append("输入 ? 查看更多命令.")
            .toString();


    public static final String DRAMA_PLAY = new StringBuffer()
            .append("剧本 No.{0} | {1}").append("\n")
            .append("介绍：{2}").append("\n")
            .append("\n")
            .append("场景：{3}").append("\n")
            .append("\n")
            .append("选项：").append("\n")
            .append("\n")
            .append("{4}")
            .toString();

    public static final String DRAMA_END = new StringBuffer()
            .append("剧本 No.{0} | {1}").append("\n")
            .append("介绍：{2}").append("\n")
            .append("\n")
            .append("场景：{3}").append("\n")
            .append("\n")
            .append("[完结]")
            .toString();


    public static final String
            REGEX_RENAME = "^改名 (.*?)$",
            REGEX_DRAMA_CHOOSE = "^进入 (\\d+)$",
            REGEX_DRAMA_EXIT = "^退出 (\\d+)$",
            REGEX_RESET = "^重置 (\\d+)$",
            REGEX_CHOOSE = "^\\d+$",

    SPACE = "";


    private static String[] LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");


    public static String formatChoice(List<ChooseModel> chooseModelList) {

        StringBuffer sb = new StringBuffer();

        if (chooseModelList != null && chooseModelList.size() > 0) {
            for (int i = 0; i < chooseModelList.size(); i++) {
                ChooseModel chooseModel = chooseModelList.get(i);
                sb.append(i + 1).append(":")
                        .append(chooseModel.getDescription())
                        .append("\n");
            }
        }

        return sb.toString();
    }


}
