package cn.kykys.index.utils.weixin.func;

/**
 * Created by kuangye on 2016/11/14.
 */
public class WeixinFunc {

    /**
     * main menu
     */
    public static String getMainMenu() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("您好，我是kyy，请回复数字选择服务：").append("\n\n");

        buffer.append("1  开始游戏").append("\n");
        buffer.append("2  个人属性").append("\n\n");
        buffer.append("3  ").append("\n\n");

        buffer.append("4  kyy的歌（随机一首）").append("\n");
        buffer.append("5  经典游戏").append("\n");
        buffer.append("6  美女电台").append("\n");
        buffer.append("7  人脸识别").append("\n");
        buffer.append("8  聊天唠嗑").append("\n\n");

//        buffer.append("0  我的微信信息").append("\n\n");
        buffer.append("回复“?”显示此帮助菜单");
        return buffer.toString();
    }






}
