package cn.kykys.index.utils.weixin;

import cn.kykys.index.utils.LogUtil;
import cn.kykys.index.utils.web.HttpRequestHelper;
import cn.kykys.index.utils.web.HttpResponseModel;
import cn.kykys.index.utils.weixin.exception.ErrorCode;
import cn.kykys.index.utils.weixin.exception.WeiXinException;
import cn.kykys.index.utils.weixin.model.WechatUserInfo;
import cn.kykys.index.utils.weixin.properties.AccessToken;
import cn.kykys.index.utils.weixin.properties.WeixinProperties;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kuangye on 2016/11/14.
 */
public class WeixinUtil {

    private static String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}";

    private static String USERINFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token={0}&openid={1}&lang=zh_CN";


    private static JSONObject send(String url, String... args) throws WeiXinException {

        HttpResponseModel responseModel = HttpRequestHelper.get(MessageFormat.format(url, args));

        JSONObject jsonObject = (JSONObject) JSON.parse(responseModel.getContent());

        LogUtil.debug(jsonObject.toJSONString());

        if (jsonObject.get("errcode") != null) {
            throw new WeiXinException(jsonObject.get("errcode").toString());
        }

        return jsonObject;
    }


    private static JSONObject weixinAPIRequest(String url, String... args) throws Exception {
        JSONObject jsonObject = null;
        try {
            jsonObject = WeixinUtil.send(url, args);

            return jsonObject;

        } catch (WeiXinException e) {
            LogUtil.error(e);
            if (e.getHandle().equals(ErrorCode.Handle.OK)) {
                return jsonObject;
            } else if (e.getHandle().equals(ErrorCode.Handle.FUNCTION)) {
                return WeixinUtil.send(url, args);
            } else {
                e.printStackTrace();
            }
        }

        return null;
    }


    @Test
    public static AccessToken getNewAccessToken() throws Exception {
//        https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET

        JSONObject jsonObject = WeixinUtil.weixinAPIRequest(TOKEN_URL, WeixinProperties.getAppID(), WeixinProperties.getAppSecret());

        String accessToken = (String) jsonObject.get("access_token");
        Integer expireIn = jsonObject.getIntValue("expires_in");

        if (accessToken != null) {
            //有效期2小时
            return new AccessToken(accessToken, expireIn);
        }

        return null;
    }


    //需要交钱 fuxk
    public static WechatUserInfo getUserByOpenId(String openId) throws Exception {
//          https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN

//    参数	说明
//    subscribe	用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
//    openid	用户的标识，对当前公众号唯一
//    nickname	用户的昵称
//    sex	用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
//    city	用户所在城市
//    country	用户所在国家
//    province	用户所在省份
//    language	用户的语言，简体中文为zh_CN
//    headimgurl	用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
//    subscribe_time	用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
//    unionid	只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
//    remark	公众号运营者对粉丝的备注，公众号运营者可在微信公众平台用户管理界面对粉丝添加备注
//    groupid	用户所在的分组ID（兼容旧的用户分组接口）
//    tagid_list	用户被打上的标签ID列表


        JSONObject jsonObject = WeixinUtil.weixinAPIRequest(USERINFO_URL, AccessTokenThread.getAccessToken().getToken(), openId);

        String subscribe = jsonObject.getString("subscribe");
        if (subscribe.equals("0")) {
            //未关注
            return null;
        }

        WechatUserInfo wechatUserInfo = new WechatUserInfo();
        wechatUserInfo.setSubscribe(subscribe);
        wechatUserInfo.setOpenid(jsonObject.getString("openid"));
        wechatUserInfo.setNickname(jsonObject.getString("nickname"));
        wechatUserInfo.setSex(jsonObject.getString("sex"));
        wechatUserInfo.setCity(jsonObject.getString("city"));
        wechatUserInfo.setCountry(jsonObject.getString("country"));
        wechatUserInfo.setProvince(jsonObject.getString("province"));
        wechatUserInfo.setLanguage(jsonObject.getString("language"));
        wechatUserInfo.setHeadimgurl(jsonObject.getString("headimgurl"));
        wechatUserInfo.setSubscribe_time(jsonObject.getString("subscribe_time"));
        wechatUserInfo.setUnionid(jsonObject.getString("unionid"));
        wechatUserInfo.setRemark(jsonObject.getString("remark"));
        wechatUserInfo.setGroupid(jsonObject.getString("groupid"));
        wechatUserInfo.setTagid_list(jsonObject.getString("tagid_list"));

        return wechatUserInfo;
    }


    /**
     * 判断是否是QQ表情
     */
    public static boolean isQqFace(String content) {
        boolean result = false;

        // 判断QQ表情的正则表达式
        String qqfaceRegex = "/::\\)|/::~|/::B|/::\\||/:8-\\)|/::<|/::$|/::X|/::Z|/::'\\(|/::-\\||/::@|/::P|/::D|/::O|/::\\(|/::\\+|/:--b|/::Q|/::T|/:,@P|/:,@-D|/::d|/:,@o|/::g|/:\\|-\\)|/::!|/::L|/::>|/::,@|/:,@f|/::-S|/:\\?|/:,@x|/:,@@|/::8|/:,@!|/:!!!|/:xx|/:bye|/:wipe|/:dig|/:handclap|/:&-\\(|/:B-\\)|/:<@|/:@>|/::-O|/:>-\\||/:P-\\(|/::'\\||/:X-\\)|/::\\*|/:@x|/:8\\*|/:pd|/:<W>|/:beer|/:basketb|/:oo|/:coffee|/:eat|/:pig|/:rose|/:fade|/:showlove|/:heart|/:break|/:cake|/:li|/:bome|/:kn|/:footb|/:ladybug|/:shit|/:moon|/:sun|/:gift|/:hug|/:strong|/:weak|/:share|/:v|/:@\\)|/:jj|/:@@|/:bad|/:lvu|/:no|/:ok|/:love|/:<L>|/:jump|/:shake|/:<O>|/:circle|/:kotow|/:turn|/:skip|/:oY|/:#-0|/:hiphot|/:kiss|/:<&|/:&>";
        Pattern p = Pattern.compile(qqfaceRegex);
        Matcher m = p.matcher(content);
        if (m.matches()) {
            result = true;
        }
        return result;
    }
}
