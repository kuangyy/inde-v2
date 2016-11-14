package cn.kykys.index.utils.weixin.exception;

import java.util.HashMap;
import java.util.Map;

public class ErrorCode {
	private static Map<String,ErrorCode> errors = new HashMap<String,ErrorCode>();
	public enum Handle{
		OK,//正常可以过
		ERROR,//错误抛出异常
		FUNCTION,//需要回调重新处理
	}
	private String code;
	private Handle handle;
	private String msg;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Handle getHandle() {
		return handle;
	}
	public void setHandle(Handle handle) {
		this.handle = handle;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	private ErrorCode(String code, Handle handle, String msg) {
		super();
		this.code = code;
		this.handle = handle;
		this.msg = msg;
	}
	public static ErrorCode getErrorCode(String code){
		return errors.get(code);
	}
	private static void setErrorCode(ErrorCode errorCode){
		errors.put(errorCode.getCode(), errorCode);
	}
	static{
		setErrorCode(new ErrorCode("-100", Handle.ERROR,"未知错误"));
		setErrorCode(new ErrorCode("0", Handle.OK,"请求成功"));
		setErrorCode(new ErrorCode("40001", Handle.ERROR,"验证失败"));
		setErrorCode(new ErrorCode("40002", Handle.FUNCTION,"不合法的凭证类型"));
		setErrorCode(new ErrorCode("40003", Handle.ERROR,"不合法的OpenID"));
		setErrorCode(new ErrorCode("40004", Handle.ERROR,"不合法的媒体文件类型"));
		setErrorCode(new ErrorCode("40005", Handle.ERROR,"不合法的文件类型"));
		setErrorCode(new ErrorCode("40006", Handle.ERROR,"不合法的文件大小"));
		setErrorCode(new ErrorCode("40007", Handle.ERROR,"不合法的媒体文件id"));
		setErrorCode(new ErrorCode("40008", Handle.ERROR,"不合法的消息类型"));
		setErrorCode(new ErrorCode("40009", Handle.ERROR,"不合法的图片文件大小"));
		setErrorCode(new ErrorCode("40010", Handle.ERROR,"不合法的语音文件大小"));
		setErrorCode(new ErrorCode("40011", Handle.ERROR,"不合法的视频文件大小"));
		setErrorCode(new ErrorCode("40012", Handle.ERROR,"不合法的缩略图文件大小"));
		setErrorCode(new ErrorCode("40013", Handle.ERROR,"不合法的APPID"));
		setErrorCode(new ErrorCode("40014", Handle.FUNCTION,"不合法的access_token"));
		setErrorCode(new ErrorCode("40015", Handle.ERROR,"不合法的菜单类型"));
		setErrorCode(new ErrorCode("40016", Handle.ERROR,"不合法的按钮个数"));
		setErrorCode(new ErrorCode("40017", Handle.ERROR,"不合法的按钮个数"));
		setErrorCode(new ErrorCode("40018", Handle.ERROR,"不合法的按钮名字长度"));
		setErrorCode(new ErrorCode("40019", Handle.ERROR,"不合法的按钮KEY长度"));
		setErrorCode(new ErrorCode("40020", Handle.ERROR,"不合法的按钮URL长度"));
		setErrorCode(new ErrorCode("40021", Handle.ERROR,"不合法的菜单版本号"));
		setErrorCode(new ErrorCode("40022", Handle.ERROR,"不合法的子菜单级数"));
		setErrorCode(new ErrorCode("40023", Handle.ERROR,"不合法的子菜单按钮个数"));
		setErrorCode(new ErrorCode("40024", Handle.ERROR,"不合法的子菜单按钮类型"));
		setErrorCode(new ErrorCode("40025", Handle.ERROR,"不合法的子菜单按钮名字长度"));
		setErrorCode(new ErrorCode("40026", Handle.ERROR,"不合法的子菜单按钮KEY长度"));
		setErrorCode(new ErrorCode("40027", Handle.ERROR,"不合法的子菜单按钮URL长度"));
		setErrorCode(new ErrorCode("40028", Handle.ERROR,"不合法的自定义菜单使用用户"));
		setErrorCode(new ErrorCode("40029", Handle.ERROR,"不合法的oauth_code"));
		setErrorCode(new ErrorCode("40030", Handle.ERROR,"不合法的refresh_token"));
		setErrorCode(new ErrorCode("40031", Handle.ERROR,"不合法的openid列表"));
		setErrorCode(new ErrorCode("40032", Handle.ERROR,"不合法的openid列表长度"));
		setErrorCode(new ErrorCode("40033", Handle.ERROR,"不合法的请求字符，不能包含\\uxxxx格式的字符"));
		setErrorCode(new ErrorCode("40034", Handle.ERROR,"不合法的模板大小"));
		setErrorCode(new ErrorCode("40035", Handle.ERROR,"不合法的参数"));
		setErrorCode(new ErrorCode("40036", Handle.ERROR,"不合法的模板id大小"));
		setErrorCode(new ErrorCode("40037", Handle.ERROR,"不合法的模板id"));
		setErrorCode(new ErrorCode("40038", Handle.ERROR,"不合法的请求格式"));
		setErrorCode(new ErrorCode("40039", Handle.ERROR,"不合法的URL长度"));
		setErrorCode(new ErrorCode("41001", Handle.ERROR,"缺少access_token参数"));
		setErrorCode(new ErrorCode("41002", Handle.ERROR,"缺少appid参数"));
		setErrorCode(new ErrorCode("41003", Handle.ERROR,"缺少refresh_token参数"));
		setErrorCode(new ErrorCode("41004", Handle.ERROR,"缺少secret参数"));
		setErrorCode(new ErrorCode("41005", Handle.ERROR,"缺少多媒体文件数据"));
		setErrorCode(new ErrorCode("41006", Handle.ERROR,"缺少media_id参数"));
		setErrorCode(new ErrorCode("41007", Handle.ERROR,"缺少子菜单数据"));
		setErrorCode(new ErrorCode("41008", Handle.ERROR,"缺少oauth code"));
		setErrorCode(new ErrorCode("41009", Handle.ERROR,"缺少openid"));
		setErrorCode(new ErrorCode("42001", Handle.ERROR,"access_token超时"));
		setErrorCode(new ErrorCode("42002", Handle.ERROR,"refresh_token超时"));
		setErrorCode(new ErrorCode("42001", Handle.ERROR,"oauth_code超时"));
		setErrorCode(new ErrorCode("43001", Handle.ERROR,"需要GET请求"));
		setErrorCode(new ErrorCode("43002", Handle.ERROR,"需要POST请求"));
		setErrorCode(new ErrorCode("43003", Handle.ERROR,"需要HTTPS请求"));
		setErrorCode(new ErrorCode("43004", Handle.ERROR,"需要接收者关注"));
		setErrorCode(new ErrorCode("43005", Handle.ERROR,"需要好友关系"));
		setErrorCode(new ErrorCode("44001", Handle.ERROR,"多媒体文件为空"));
		setErrorCode(new ErrorCode("44002", Handle.ERROR,"POST的数据包为空"));
		setErrorCode(new ErrorCode("44003", Handle.ERROR,"图文消息内容为空"));
		setErrorCode(new ErrorCode("44004", Handle.ERROR,"文本消息内容为空"));
		setErrorCode(new ErrorCode("45001", Handle.ERROR,"多媒体文件大小超过限制"));
		setErrorCode(new ErrorCode("45002", Handle.ERROR,"消息内容超过限制"));
		setErrorCode(new ErrorCode("45003", Handle.ERROR,"标题字段超过限制"));
		setErrorCode(new ErrorCode("45004", Handle.ERROR,"描述字段超过限制"));
		setErrorCode(new ErrorCode("45005", Handle.ERROR,"链接字段超过限制"));
		setErrorCode(new ErrorCode("45006", Handle.ERROR,"图片链接字段超过限制"));
		setErrorCode(new ErrorCode("45007", Handle.ERROR,"语音播放时间超过限制"));
		setErrorCode(new ErrorCode("45008", Handle.ERROR,"图文消息超过限制"));
		setErrorCode(new ErrorCode("45009", Handle.ERROR,"接口调用超过限制"));
		setErrorCode(new ErrorCode("45010", Handle.ERROR,"创建菜单个数超过限制"));
		setErrorCode(new ErrorCode("45012", Handle.ERROR,"模板大小超过限制"));
		setErrorCode(new ErrorCode("45013", Handle.ERROR,"模板参数超过限制"));
		setErrorCode(new ErrorCode("45014", Handle.ERROR,"模板消息长度超过限制"));
		setErrorCode(new ErrorCode("45015", Handle.ERROR,"回复时间超过限制"));
		setErrorCode(new ErrorCode("46001", Handle.ERROR,"不存在媒体数据"));
		setErrorCode(new ErrorCode("46002", Handle.ERROR,"不存在的菜单版本"));
		setErrorCode(new ErrorCode("46003", Handle.ERROR,"不存在的菜单数据"));
		setErrorCode(new ErrorCode("46004", Handle.ERROR,"不存在的用户"));
		setErrorCode(new ErrorCode("47001", Handle.ERROR,"解析JSON/XML内容错误"));
		setErrorCode(new ErrorCode("48001", Handle.ERROR,"api功能未授权"));
		setErrorCode(new ErrorCode("50001", Handle.ERROR,"用户未授权该api"));
		//商户系统错误
		setErrorCode(new ErrorCode("CH-OTHER-ERROR", Handle.ERROR,"其它错误异常自定义"));
		setErrorCode(new ErrorCode("NOAUTH", Handle.ERROR,"商户未开通此接口权限"));
		setErrorCode(new ErrorCode("NOTENOUGH", Handle.ERROR,"用户帐号余额不足"));
		setErrorCode(new ErrorCode("ORDERPAID", Handle.ERROR,"商户订单已支付，无需重复操作"));
		setErrorCode(new ErrorCode("ORDERCLOSED", Handle.ERROR,"当前订单已关闭，无法支付"));
		setErrorCode(new ErrorCode("SYSTEMERROR", Handle.ERROR,"系统超时"));
		setErrorCode(new ErrorCode("APPID_NOT_EXIST", Handle.ERROR,"参数中缺少APPID"));
		setErrorCode(new ErrorCode("MCHID_NOT_EXIST", Handle.ERROR,"参数中缺少MCHID"));
		setErrorCode(new ErrorCode("APPID_MCHID_NOT_MATCH", Handle.ERROR,"appid和mch_id不匹配"));
		setErrorCode(new ErrorCode("LACK_PARAMS", Handle.ERROR,"缺少必要的请求参数"));
		setErrorCode(new ErrorCode("OUT_TRADE_NO_USED", Handle.FUNCTION,"同一笔交易不能多次提交"));
		setErrorCode(new ErrorCode("SIGNERROR", Handle.ERROR,"参数签名结果不正确"));
		setErrorCode(new ErrorCode("XML_FORMAT_ERROR", Handle.ERROR,"XML格式错误"));
		setErrorCode(new ErrorCode("REQUIRE_POST_METHOD", Handle.ERROR,"未使用post传递参数 "));
		setErrorCode(new ErrorCode("POST_DATA_EMPTY", Handle.ERROR,"post数据不能为空"));
		setErrorCode(new ErrorCode("NOT_UTF8", Handle.ERROR,"未使用指定编码格式"));
	}
}
