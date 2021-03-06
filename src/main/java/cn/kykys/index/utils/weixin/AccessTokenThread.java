//package cn.kykys.index.utils.weixin;
//
//import cn.kykys.index.utils.LogUtil;
//import cn.kykys.index.utils.weixin.properties.AccessToken;
//
//import java.text.MessageFormat;
//
///**
// * 定时获取微信access_token的线程
// * Created by kuangye on 2016/11/14.
// */
//public class AccessTokenThread implements Runnable {
//
//    private static AccessToken accessToken = null;
//
//    static {
//        new Thread(new AccessTokenThread()).start();
//    }
//
//
//    @Override
//    public void run() {
//
//        while (true) {
//            try {
//                this.getToken();
//            } catch (Exception e) {
//                try {
//                    Thread.sleep(60 * 1000);
//                } catch (InterruptedException e1) {
//                    LogUtil.error("{}", e1);
//                }
//                LogUtil.error("{}", e);
//            }
//        }
//    }
//
//
//    private static void getToken() throws Exception {
//        accessToken = WeixinUtil.getNewAccessToken();
//        if (null != accessToken) {
//            LogUtil.info(MessageFormat.format("获取access_token成功，有效时长{0}秒 token:{1}", accessToken.getExpiresIn(), accessToken.getToken()));
//            // 休眠7000秒
//            //冗余 200秒
//            Thread.sleep((accessToken.getExpiresIn() - 200) * 1000);
//        } else {
//            // 如果access_token为null，60秒后再获取
//            Thread.sleep(60 * 1000);
//        }
//    }
//
//    public static AccessToken getAccessToken() {
//        if (accessToken == null) {
//            try {
//                AccessTokenThread.getToken();
//            } catch (Exception e1) {
//                LogUtil.error("{}", e1);
//            }
//        }
//
//        return accessToken;
//    }
//}
