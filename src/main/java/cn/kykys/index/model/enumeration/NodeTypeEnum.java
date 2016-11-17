package cn.kykys.index.model.enumeration;

/**
 * Created by kuangye on 2016/11/17.
 */
public enum NodeTypeEnum {


    START(1, "开始节点"),

    REPALY(2, "中继节点"),

    END(3, "结束节点"),;


    NodeTypeEnum(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    private Integer status;
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
