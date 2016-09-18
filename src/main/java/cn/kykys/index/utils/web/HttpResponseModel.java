package cn.kykys.index.utils.web;

/**
 * HttpResponseModel
 *
 * @author liwei
 * @date 16/8/15
 * @description
 */
public class HttpResponseModel {
    private int httpStatusCode;
    private String content;

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
