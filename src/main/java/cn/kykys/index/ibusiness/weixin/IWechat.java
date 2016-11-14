package cn.kykys.index.ibusiness.weixin;

import java.util.Map;

/**
 * Created by kuangye on 2016/11/14.
 */
public interface IWechat {


    /**
     * 微信消息处理
     */
    String processRequest(Map<String, String> requestMap);

}
