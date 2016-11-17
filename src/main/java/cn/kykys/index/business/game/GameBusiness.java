package cn.kykys.index.business.game;

import cn.kykys.index.ibusiness.game.IGame;
import cn.kykys.index.ibusiness.weixin.IPeople;
import cn.kykys.index.model.wechat.PeopleModel;
import cn.kykys.index.utils.DateUtils;
import cn.kykys.index.utils.game.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;

/**
 * Created by kuangye on 2016/11/16.
 */
@Service
public class GameBusiness implements IGame {

    @Autowired
    IPeople iPeople;


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

        Date today = new Date();

        int day = DateUtils.getIntervalDays(lastAddTime, today);

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


    public String chooseDrama(String openId,Integer dramaId){



        return null;
    }

    public String exitDrama(String openId,Integer dramaId){


        return null;
    }

}
