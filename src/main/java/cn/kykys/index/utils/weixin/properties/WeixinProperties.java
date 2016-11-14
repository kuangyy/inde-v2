package cn.kykys.index.utils.weixin.properties;

import cn.kykys.index.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by kuangye on 2016/11/14.
 */
public class WeixinProperties {


    //appid 第三方用户唯一凭证
    private static String AppID;
    //secret 第三方用户唯一凭证密钥
    private static String AppSecret;
    //token Token(令牌)
    private static String token;



    static {
     InputStream is = WeixinProperties.class.getResourceAsStream("weixin.properties");
        try {
            Properties properties = new Properties();
            properties.load(is);

            AppID = properties.getProperty("appid");
            AppSecret = properties.getProperty("secret");
            token = properties.getProperty("token");

        }catch (IOException e){
            LogUtil.error("微信参数配置失败");
        }


    }


    public static String getAppID() {
        return AppID;
    }

    public static void setAppID(String appID) {
        AppID = appID;
    }

    public static String getAppSecret() {
        return AppSecret;
    }

    public static void setAppSecret(String appSecret) {
        AppSecret = appSecret;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        WeixinProperties.token = token;
    }
}
