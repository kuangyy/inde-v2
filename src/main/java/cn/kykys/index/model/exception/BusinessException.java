package cn.kykys.index.model.exception;

/**
 * BusinessException
 *
 * @author liwei
 * @date 16/6/28
 * @description
 * 异常类,所有捕获的业务逻辑异常均抛出此类
 */
public class BusinessException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5003525132681742692L;

	/**
     * 显示给用户看的Message
     */
    private String showMessage;

    /**
     * 自定义的异常Code
     */
    private Integer code;

    public String getShowMessage() {
        return showMessage;
    }

    public void setShowMessage(String showMessage) {
        this.showMessage = showMessage;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
    
	public BusinessException(String showMessage, Integer code) {
		super();
		this.showMessage = showMessage;
		this.code = code;
	}

    public BusinessException(String showMessage) {
        super();
        this.showMessage = showMessage;
        this.code = -1;
    }
}
