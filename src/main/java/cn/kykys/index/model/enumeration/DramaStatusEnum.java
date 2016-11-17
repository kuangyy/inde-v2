package cn.kykys.index.model.enumeration;

/**
 * Created by kuangye on 2016/11/17.
 */
public enum DramaStatusEnum {


    OFF(0, "未启用"),

    ON(1, "启用"),


    ;



    DramaStatusEnum(Integer status, String message) {
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
