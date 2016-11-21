package cn.kykys.index.model.enumeration;

/**
 * Created by kuangye on 2016/11/17.
 */
public enum GooflowNodeTypeEnum {


    START("start", "开始节点"),

    TASK("task", "中继节点"),

    NODE("node", "中继节点"),

    END("end", "结束节点"),;


    GooflowNodeTypeEnum(String status, String message) {
        this.status = status;
        this.message = message;
    }

    private String status;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
