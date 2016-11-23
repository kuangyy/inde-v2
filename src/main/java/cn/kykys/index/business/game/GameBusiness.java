package cn.kykys.index.business.game;

import cn.kykys.index.ibusiness.game.IDrama;
import cn.kykys.index.ibusiness.game.IGame;
import cn.kykys.index.ibusiness.weixin.IPeople;
import cn.kykys.index.model.enumeration.DramaPeopleStatusEnum;
import cn.kykys.index.model.enumeration.DramaStatusEnum;
import cn.kykys.index.model.enumeration.NodeTypeEnum;
import cn.kykys.index.model.ext.ChooseModel;
import cn.kykys.index.model.ext.NodeDetail;
import cn.kykys.index.model.wechat.DramaModel;
import cn.kykys.index.model.wechat.DramaPlayModelKey;
import cn.kykys.index.model.wechat.PeopleModel;
import cn.kykys.index.utils.DateUtils;
import cn.kykys.index.utils.LogUtil;
import cn.kykys.index.utils.game.Settings;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by kuangye on 2016/11/16.
 */
@Service
public class GameBusiness implements IGame {

    @Autowired
    IPeople iPeople;
    @Autowired
    IDrama iDrama;


    public String startOrContinue(String openId) {

        PeopleModel peopleModel = iPeople.selectByOpenId(openId);

        if (peopleModel != null) {
            //continue
            if (peopleModel.getLastLoginTime() != null) {
                return this.continueGame(peopleModel);
            }

            return this.startGame(peopleModel, false);

        } else {
            //start
            peopleModel = new PeopleModel();
            peopleModel.setOpenid(openId);

            return this.startGame(peopleModel, true);
        }
    }


    //start
    private String startGame(PeopleModel peopleModel, boolean isNew) {

        if (isNew) {
            peopleModel.setCreateTime(new Date());
            peopleModel.setLastLoginTime(new Date());
            iPeople.addPeople(peopleModel);

            return MessageFormat.format(Settings.GAME_REAL_TIME_INFO, "未设置", 0, 0, 0, "无", "你还在现充中");
        }

        return MessageFormat.format(Settings.GAME_REAL_TIME_INFO,
                peopleModel.getName() == null ? "未设置" : peopleModel.getName(),
                peopleModel.getCoins(), peopleModel.getPoints(), peopleModel.getLevel(), "无", "你还在现充中");
    }


    //continue
    private String continueGame(PeopleModel peopleModel) {

        //get last add time
        Date lastAddTime = peopleModel.getLastIncreaseTime();
        //first add
        if (lastAddTime == null) {
            lastAddTime = peopleModel.getCreateTime();
        }

        int day = this.calculatePassedDay(lastAddTime);

        if (day > 0) {
            peopleModel.setCoins(peopleModel.getCoins() + Settings.COINS_SEED * day);
            peopleModel.setPoints(peopleModel.getPoints() + Settings.POINTS_SEED * day);
            peopleModel.setLastIncreaseTime(new Date());
        }

        int i = 0;
        DramaModel dramaModel = null;
        String[] story = new String[]{"你还在现充中", "正处于故事：{0} 中"};
        List<DramaPlayModelKey> dramaPlayModelKeyList = iPeople.getInPlayDramaByPeopleId(peopleModel.getId());
        if (dramaPlayModelKeyList != null && dramaPlayModelKeyList.size() > 0) {
            i = 1;
            dramaModel = iDrama.getById(dramaPlayModelKeyList.get(0).getDramaId());
            story[i] = MessageFormat.format(story[i], dramaModel.getName());
        }

        String info = MessageFormat.format(Settings.GAME_REAL_TIME_INFO, peopleModel.getName() == null ? "未设置" : peopleModel.getName(),
                peopleModel.getCoins(), peopleModel.getPoints(), peopleModel.getLevel(), peopleModel.getLastLoginTime(),
                story[i]);

        peopleModel.setLastLoginTime(new Date());

        iPeople.updatePeople(peopleModel);

        if (i > 0) {
            //插入当前剧本 当前节点信息
            DramaPlayModelKey dramaPlayModelKey = dramaPlayModelKeyList.get(0);
            NodeDetail nodeDetail = iDrama.getNodeByNodeId(dramaPlayModelKey.getNodeId());

            info += ("\n" + MessageFormat.format(Settings.DRAMA_PLAY,
                    dramaPlayModelKey.getDramaId(), dramaModel.getName(), dramaModel.getDescription(),
                    nodeDetail.getDescription(), Settings.formatChoice(nodeDetail.getChooseModelList())));
        }


        return info;
    }

    private void formatTime(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, Settings.REFRESH_TIME);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private int calculatePassedDay(Date lastAddTime) {

        Calendar lastTime = Calendar.getInstance();
        lastTime.setTime(lastAddTime);
        Calendar today = Calendar.getInstance();

        int day;
        int seed = 0;

        boolean sameDay = DateUtils.isSameDay(today, lastTime);
        //现在是指定时间之前 还是之后
        //之后与今天指定时间相比
        //之前和昨天指定时间相比
        if (today.get(Calendar.HOUR_OF_DAY) < Settings.REFRESH_TIME) {
            sameDay = !sameDay;
            //现在在指定时间之前
            today.add(Calendar.DAY_OF_MONTH, -1);
        }
        //如果上次在指定时间之前 那么当天要算上
        //否则不能算上当天
        if (sameDay && lastTime.get(Calendar.HOUR_OF_DAY) < Settings.REFRESH_TIME) {
            seed = 1;
        }
        //设置今天指定时间
        this.formatTime(today);
        this.formatTime(lastTime);
        //间隔天数
        day = DateUtils.getIntervalDays(lastTime.getTime(), today.getTime());

        return day > 0 ? day + seed : seed;
    }

    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY, -11);
        System.out.println(new GameBusiness().calculatePassedDay(c.getTime()));

        c.add(Calendar.DAY_OF_MONTH, -2);
        System.out.println(new GameBusiness().calculatePassedDay(c.getTime()));
    }

    public String rename(String openId, String name) {

        PeopleModel peopleModel = iPeople.selectByOpenId(openId);

        if (peopleModel.getName() != null && peopleModel.getName().equals(name)) {
            return "昵称未发生改变";
        }

        if (iPeople.nameExist(name)) {
            return "昵称已存在，请换一个~";
        }

        PeopleModel model = new PeopleModel();
        model.setId(peopleModel.getId());
        model.setName(name);
        model.setUpdateTime(new Date());
        iPeople.updatePeople(model);

        return "修改成功";
    }


    public String chooseDrama(String openId, Integer dramaId) {

        PeopleModel peopleModel = iPeople.selectByOpenId(openId);
        if (peopleModel != null) {

            DramaPlayModelKey dramaPlayModelKey = iPeople.getPlayDrama(peopleModel.getId(), dramaId);

            if (dramaPlayModelKey != null) {
                if (dramaPlayModelKey.getStatus().equals(DramaPeopleStatusEnum.IN.getStatus())) {
                    // "已参与该剧本，正在进行中";

                    DramaModel dramaModel = iDrama.getById(dramaId);
                    NodeDetail nodeDetail = iDrama.getNodeByNodeId(dramaPlayModelKey.getNodeId());

                    return MessageFormat.format(Settings.DRAMA_PLAY,
                            dramaId, dramaModel.getName(), dramaModel.getDescription(),
                            nodeDetail.getDescription(), Settings.formatChoice(nodeDetail.getChooseModelList()));

                } else {
                    //中断或结束
                    return "你已完成或已中断该剧本，是否重新来过？";
                }
            }

            DramaModel dramaModel = iDrama.getById(dramaId);

            if (dramaModel == null) {
                return "无该剧本，请重新选择~";
            }

            if (dramaModel.getData() == null || !dramaModel.getStatus().equals(DramaStatusEnum.ON.getStatus())) {
                return "该剧本暂未启用";
            }


            //select first node
            NodeDetail nodeDetail = iDrama.getFirstNodeByDramaId(dramaId);

            if (nodeDetail.getChooseModelList() == null) {
                //已到结尾
                return "结尾 END 撒花~";
            }

            dramaPlayModelKey = new DramaPlayModelKey();
            dramaPlayModelKey.setDramaId(dramaId);
            dramaPlayModelKey.setPeopleId(peopleModel.getId());
            dramaPlayModelKey.setNodeId(nodeDetail.getId());
            dramaPlayModelKey.setStatus(DramaPeopleStatusEnum.IN.getStatus());
            iDrama.addDramaPeopleRelation(dramaPlayModelKey);

            return MessageFormat.format(Settings.DRAMA_PLAY,
                    dramaId, dramaModel.getName(), dramaModel.getDescription(),
                    nodeDetail.getDescription(), Settings.formatChoice(nodeDetail.getChooseModelList()));


        }

        return "系统错误";
    }

    public String exitDrama(String openId, Integer dramaId) {

        PeopleModel peopleModel = iPeople.selectByOpenId(openId);
        if (peopleModel != null) {

            DramaPlayModelKey dramaPlayModelKey = iPeople.getPlayDrama(peopleModel.getId(), dramaId);

            if (dramaPlayModelKey != null) {
                if (dramaPlayModelKey.getStatus().equals(DramaPeopleStatusEnum.IN.getStatus())) {

                    dramaPlayModelKey.setStatus(DramaPeopleStatusEnum.INTERRUPT.getStatus());

                    iPeople.updateInDramaStatus(dramaPlayModelKey);

                    return "已中断该剧本。";
                }

                return "已结束该剧本。";
            }

            return "您还未参与该剧本";
        }

        return "系统错误";
    }


    public String choose(String openId, Integer choice) {

        if (choice <= 0) {
            return "选项不能小于0";
        }

        PeopleModel peopleModel = iPeople.selectByOpenId(openId);

        //判断当前用户状态
        List<DramaPlayModelKey> dramaPlayModelKeyList = iPeople.getInPlayDramaByPeopleId(peopleModel.getId());
        if (dramaPlayModelKeyList != null && dramaPlayModelKeyList.size() > 0) {
            DramaPlayModelKey dramaPlayModelKey = dramaPlayModelKeyList.get(0);

            //获取剧本
            DramaModel dramaModel = iDrama.getById(dramaPlayModelKey.getDramaId());
            //获取当前节点
            NodeDetail nodeDetail = iDrama.getNodeByNodeId(dramaPlayModelKey.getNodeId());

            List<ChooseModel> chooseModelList = nodeDetail.getChooseModelList();
            Assert.isTrue(chooseModelList != null, "选项不见了");

            if (chooseModelList != null && dramaPlayModelKeyList.size() > 0) {
                if (chooseModelList.size() > choice) {

                    ChooseModel chooseModel = chooseModelList.get(choice - 1);
                    LogUtil.debug(JSON.toJSONString(chooseModel));

                    // TODO 必须有
                    String nextNodeId = chooseModel.getNextNodeId();
                    Assert.hasText(nextNodeId, "节点不见了 ID");

                    //有下一节点
                    NodeDetail nextNodeDetail = iDrama.getNodeByNodeId(nextNodeId);
                    Assert.isTrue(nextNodeDetail != null, "节点不见了 model");

                    if (nextNodeDetail.getType().equals(NodeTypeEnum.END.getStatus())) {
                        //完结节点

                        //修改状态
                        dramaPlayModelKey.setStatus(DramaPeopleStatusEnum.FINISH.getStatus());
                        iDrama.updateDramaPeopleRelation(dramaPlayModelKey);

                        return MessageFormat.format(Settings.DRAMA_END,
                                dramaPlayModelKey.getDramaId(), dramaModel.getName(), dramaModel.getDescription(),
                                nextNodeDetail.getDescription());

                    } else {
                        //下一节点
                        return MessageFormat.format(Settings.DRAMA_PLAY,
                                dramaPlayModelKey.getDramaId(), dramaModel.getName(), dramaModel.getDescription(),
                                nextNodeDetail.getDescription(), Settings.formatChoice(nextNodeDetail.getChooseModelList()));

                    }
                } else {
                    //选项出错 请重新选择
                    return MessageFormat.format(Settings.DRAMA_PLAY,
                            dramaPlayModelKey.getDramaId(), dramaModel.getName(), dramaModel.getDescription(),
                            nodeDetail.getDescription(), Settings.formatChoice(nodeDetail.getChooseModelList()));

                }
            }

            //TODO 不应该有如此节点
            return "完结，撒花~";
        } else {
            return "您并未开始游戏";
        }
    }


    public String reset(String openId, Integer dramaId) {
        return "";
    }

}
