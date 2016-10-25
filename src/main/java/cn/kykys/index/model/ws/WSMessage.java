package cn.kykys.index.model.ws;

import java.util.Date;

/**
 * Created by kuangye on 2016/10/25.
 */
public class WSMessage {


    private String name;

    private String content;

    private Date createTime;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
