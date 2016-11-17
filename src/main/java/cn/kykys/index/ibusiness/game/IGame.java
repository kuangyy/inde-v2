package cn.kykys.index.ibusiness.game;

/**
 * Created by kuangye on 2016/11/16.
 */
public interface IGame {

    String startOrContinue(String openId);

    String rename(String openId,String name);

    String chooseDrama(String openId,Integer dramaId);

    String exitDrama(String openId,Integer dramaId);

}
