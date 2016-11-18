package cn.kykys.index.business.game;

import cn.kykys.index.ibusiness.game.IDrama;
import cn.kykys.index.ibusiness.game.IGame;
import cn.kykys.index.ibusiness.weixin.IPeople;
import cn.kykys.index.model.enumeration.DramaStatusEnum;
import cn.kykys.index.model.wechat.DramaModel;
import cn.kykys.index.model.wechat.PeopleModel;
import cn.kykys.index.utils.DateUtils;
import cn.kykys.index.utils.game.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

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
        }

        return MessageFormat.format(Settings.GAME_REALTIME_INFO, "未设置", 0, 0, 0, "无");
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

        String info = MessageFormat.format(Settings.GAME_REALTIME_INFO, peopleModel.getName() == null ? "未设置" : peopleModel.getName(),
                peopleModel.getCoins(), peopleModel.getPoints(), peopleModel.getLevel(), peopleModel.getLastLoginTime());


        peopleModel.setLastLoginTime(new Date());

        iPeople.updatePeople(peopleModel);

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
        iPeople.updatePeople(peopleModel);

        return "修改成功";
    }


    public String chooseDrama(String openId, Integer dramaId) {


        DramaModel dramaModel = iDrama.getById(dramaId);

        if (dramaModel == null) {
            return "无该剧本，请重新选择~";
        }

        if (dramaModel.getData() == null || !dramaModel.getStatus().equals(DramaStatusEnum.ON.getStatus())) {
            return "该剧本暂未启用";
        }


        //select first node


        return null;
    }

    public String exitDrama(String openId, Integer dramaId) {


        return null;
    }

}
