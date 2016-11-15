package cn.kykys.index.utils.weixin.exception;


public class WeiXinException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5311761954910198474L;
	private String code;
	private ErrorCode.Handle handle;
	private String msg;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public ErrorCode.Handle getHandle() {
		return handle;
	}
	public void setHandle(ErrorCode.Handle handle) {
		this.handle = handle;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public WeiXinException(String code, ErrorCode.Handle handle, String msg) {
		super();
		this.code = code;
		this.handle = handle;
		this.msg = msg;
	}
	public WeiXinException(String code) {
		super();
		ErrorCode errorCode = ErrorCode.getErrorCode(code);
		this.code = errorCode.getCode();
		this.handle = errorCode.getHandle();
		this.msg = errorCode.getMsg();
	}
	
}
