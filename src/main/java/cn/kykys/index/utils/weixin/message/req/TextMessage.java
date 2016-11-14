package cn.kykys.index.utils.weixin.message.req;

/**
 * 文本消息
 * Created by kuangye on 2016/11/14.
 */
public class TextMessage extends BaseMessage {
    // 消息内容
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

}