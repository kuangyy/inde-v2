package cn.kykys.index.utils.weixin;

import cn.kykys.index.utils.web.HttpRequestHelper;
import cn.kykys.index.utils.web.HttpResponseModel;
import cn.kykys.index.utils.weixin.properties.AccessToken;
import cn.kykys.index.utils.weixin.properties.WeixinProperties;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kuangye on 2016/11/14.
 */
public class WeixinUtil {

    private static String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}";

    @Test
    public static AccessToken getNewAccessToken() {
//        https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET

        HttpResponseModel responseModel = HttpRequestHelper.get(MessageFormat.format(TOKEN_URL, WeixinProperties.getAppID(), WeixinProperties.getAppSecret()));

        JSONObject jsonObject = (JSONObject) JSON.parse(responseModel.getContent());

        String accessToken = (String) jsonObject.get("access_token");
        Long expireIn = Long.parseLong((String) jsonObject.get("expires_in"));

        if (accessToken != null) {
            //有效期2小时
            return new AccessToken(accessToken, expireIn);
        }

        return null;
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
