package cn.kykys.index.utils.weixin.message.req;

/**
 * 音频消息
 * Created by kuangye on 2016/11/14.
 */
public class VoiceMessage extends BaseMessage {
    // 媒体ID
    private String MediaId;
    // 语音格式
    private String Format;

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }
}