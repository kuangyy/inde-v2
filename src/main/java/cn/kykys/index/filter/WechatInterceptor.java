package cn.kykys.index.filter;

import cn.kykys.index.annotations.MustLogin;
import cn.kykys.index.constants.Constants;
import cn.kykys.index.ibusiness.weixin.IPeople;
import cn.kykys.index.model.wechat.PeopleModel;
import cn.kykys.index.utils.security.SecurityHelper;
import cn.kykys.index.utils.web.WebHelper;
import cn.kykys.index.utils.weixin.MessageUtil;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;

/**
 * 微信拦截器
 * <p>
 * 安全操作
 * openId 用户校验处理转换
 *
 * TODO 有待改善
 */
public class WechatInterceptor implements HandlerInterceptor {


    @Autowired
    private IPeople iPeople;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String method = request.getMethod();

        if (method.equalsIgnoreCase("get")) {

            // 微信加密签名
            String signature = request.getParameter("signature");
            // 时间戳
            String timestamp = request.getParameter("timestamp");
            // 随机数
            String nonce = request.getParameter("nonce");
            // 随机字符串
            String echostr = request.getParameter("echostr");

            if (!StringUtils.hasText(signature)
                    || !StringUtils.hasText(timestamp)
                    || !StringUtils.hasText(nonce)
                    || !StringUtils.hasText(echostr)) {

                response.setStatus(HttpStatus.SC_FORBIDDEN);
                return false;
            }
        } else {

            // xml请求解析
            Map<String, String> map = MessageUtil.parseXml(request);

            request.setAttribute("inputMap", map);

            // 发送方帐号（open_id）
            String fromUserName = map.get("FromUserName");

            if (StringUtils.hasText(fromUserName)) {
                PeopleModel peopleModel = iPeople.selectByOpenId(fromUserName);

                //如果没有自动注册
                if (peopleModel == null) {
                    peopleModel = new PeopleModel();
                    peopleModel.setOpenid(fromUserName);
                    peopleModel.setCreateTime(new Date());
                    peopleModel.setLastLoginTime(new Date());
                    iPeople.addPeople(peopleModel);
                }
            } else {
                response.setStatus(HttpStatus.SC_FORBIDDEN);
                return false;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }


}
