package cn.kykys.index.model.enumeration;

/**
 * Created by kuangye on 2016/11/17.
 */
public enum DramaPeopleStatusEnum {


    IN(0, "进行中"),

    INTERRUPT(1, "中途退出"),

    FINISH(2, "完成");


    DramaPeopleStatusEnum(Integer status, String message) {
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
