package cn.kykys.index.utils.game;

/**
 * Created by kuangye on 2016/11/16.
 */
public final class Settings {

    public static final Integer COINS_SEED = 10, POINTS_SEED = 10;






    public static final String MAIN_MENU = new StringBuffer()
            .append("您好，我是kyy，请回复数字选择服务：").append("\n")
            .append("\n")
            .append("1  开始游戏/继续游戏").append("\n")
            .append("2  个人属性").append("\n")
            .append("3  游戏规则").append("\n")
            .append("\n")
            .append("5  kyy的歌（随机一首）").append("\n")
            .append("\n")
            .append("回复 ? 显示此帮助菜单")
            .toString();



    public static final String EXPLAIN = new StringBuffer()
            .append("游戏指南").append("\n")
            .append("\n")
            .append("等待完善").append("\n")
            .toString();




    public static final String GAME_REALTIME_INFO = new StringBuffer()
            .append("姓名:{0} ").append("\n")
            .append("金币:{1} ").append("\n")
            .append("积分:{2} ").append("\n")
            .append("等级:{3} ").append("\n")
            .append("上次登录时间:{4} ").append("\n").append("\n")
            .append("输入 ? 查看更多命令.")
            .toString();
}
