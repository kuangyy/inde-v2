package cn.kykys.index.utils.weixin.message.req;

/**
 * 图片消息
 * Created by kuangye on 2016/11/14.
 */
public class ImageMessage extends BaseMessage {
    // 图片链接
    private String PicUrl;

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }
}