package cn.kykys.index.business.weixin;

import cn.kykys.index.ibusiness.game.IGame;
import cn.kykys.index.ibusiness.weixin.IPeople;
import cn.kykys.index.ibusiness.weixin.IWechat;
import cn.kykys.index.utils.LogUtil;
import cn.kykys.index.utils.game.Settings;
import cn.kykys.index.utils.weixin.MessageUtil;
import cn.kykys.index.utils.weixin.message.req.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kuangye on 2016/11/14.
 */
@Service
public class WechatBusiness implements IWechat {

    @Autowired
    private IGame iGame;

    @Override
    public String processRequest(Map<String, String> requestMap) {


        // 发送方帐号（open_id）
        String fromUserName = requestMap.get("FromUserName");
        // 公众帐号
        String toUserName = requestMap.get("ToUserName");
        // 消息类型
        String msgType = requestMap.get("MsgType");


        String respMessage = null;
        try {
            // 默认返回的文本消息内容
            String respContent = "请求处理异常，请稍候尝试！";

            // 回复文本消息
            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(new Date().getTime());
            textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);

            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {

                respContent = this.doTextMessage(fromUserName, requestMap.get("Content"));

//                respContent = "您发送的是文本消息！";
            }
            // 图片消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                respContent = "您发送的是图片消息！";
            }
            // 地理位置消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
                respContent = "您发送的是地理位置消息！";
            }
            // 链接消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
                respContent = "您发送的是链接消息！";
            }
            // 音频消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
                respContent = "您发送的是音频消息！";
            }
            // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型
                String eventType = requestMap.get("Event");
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    respContent = "谢谢您的关注！";
                }
                // 取消订阅
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    // TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                }
                // 自定义菜单点击事件
                else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                    // TODO 自定义菜单权没有开放，暂不处理该类消息
                }
            }

            textMessage.setContent(respContent);
            respMessage = MessageUtil.textMessageToXml(textMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return respMessage;
    }


    private String doTextMessage(String openID, String text) throws Exception {

        LogUtil.debug("openid : " + openID + " | send: " + text);

        if (StringUtils.hasText(text)) {

            // total match
            switch (text) {


                //start / continue   [tell people intime state


                case "说明":
                    return Settings.EXPLAIN;
                case "0":
                    //当前状态
                    return iGame.startOrContinue(openID);
//                case "0":
//                    WechatUserInfo wechatUserInfo = WeixinUtil.getUserByOpenId(openID);
//                    return wechatUserInfo == null ? "请先关注本订阅号~" : wechatUserInfo.toString();

                case "?":
                    return Settings.MAIN_MENU;


                default:
                    //regex match
                    return this.regexMatch(openID, text);
            }
        }
        return Settings.MAIN_MENU;
    }

    private String regexMatch(String openId, String text) {

        String matchText;

        //choose
        if (Pattern.matches(Settings.REGEX_CHOOSE, text)) {
            Long choice = Long.parseLong(text);
            return iGame.choose(openId, choice);
        }

        //reset
        matchText = this.regexHandler(text, Settings.REGEX_RESET);
        if (StringUtils.hasText(matchText)) {
            Integer dramaId = Integer.parseInt(matchText);
            return iGame.reset(openId, dramaId);
        }

        //rename
        matchText = this.regexHandler(text, Settings.REGEX_RENAME);
        if (StringUtils.hasText(matchText)) {

            return iGame.rename(openId, matchText);

        }


        //choose
        matchText = this.regexHandler(text, Settings.REGEX_DRAMA_CHOOSE);
        if (StringUtils.hasText(matchText)) {
            try {

                Integer dramaId = Integer.parseInt(matchText);
                return iGame.chooseDrama(openId, dramaId);

            } catch (NumberFormatException e) {
                return "输入错误请检查后重试！";
            }
        }


        //exit
        matchText = this.regexHandler(text, Settings.REGEX_DRAMA_EXIT);
        if (StringUtils.hasText(matchText)) {
            try {

                Integer dramaId = Integer.parseInt(matchText);
                return iGame.exitDrama(openId, dramaId);

            } catch (NumberFormatException e) {
                return "输入错误请检查后重试！";
            }
        }


        return Settings.MAIN_MENU;
    }


    private String regexHandler(String text, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        if (m.matches()) {
            String name = m.group(1);
            if (StringUtils.hasText(name)) {
                LogUtil.debug("匹配 ：" + pattern);
                return name;
            }
        }
        return null;
    }


}
