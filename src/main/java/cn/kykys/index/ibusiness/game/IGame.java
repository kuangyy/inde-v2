package cn.kykys.index.ibusiness.game;

/**
 * Created by kuangye on 2016/11/16.
 */
public interface IGame {

    //查看当前状态
    String inTimeInfo(String openId);
    //重命名
    String rename(String openId,String name);
    //选择剧本
    String chooseDrama(String openId,Integer dramaId);
    //退出剧本
    String exitDrama(String openId,Integer dramaId);
    //选择选项
    String choose(String openId,Integer choice);
    //重置剧本
    String reset(String openId,Integer dramaId);
    //升级
    String levelUp(String openId);

}
