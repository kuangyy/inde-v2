package cn.kykys.index.filter;

import cn.kykys.index.annotations.MustLogin;
import cn.kykys.index.constants.Constants;
import cn.kykys.index.utils.security.SecurityHelper;
import cn.kykys.index.utils.web.WebHelper;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class FilterAll implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if(handler instanceof DefaultServletHttpRequestHandler){
           return true;
        }else{
            MustLogin mustLogin = ((HandlerMethod) handler).getMethod().getAnnotation(MustLogin.class);
            if (mustLogin != null) {//需要验证



            }else{
               return true;
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

    String a() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        String ei;
        //session 中获取
        ei = (String) session.getAttribute(Constants.SESSION_LOGIN_USER_KEY);
        //没有再找 cookie
        if (ei == null) {
            String loginInfo = WebHelper.getCookie(request, Constants.COOKIE_LOGIN_USER_KEY);
            if (StringUtils.hasText(loginInfo)) {
                String decodeString = SecurityHelper.desDecrypt(loginInfo, Constants.DES_ENCODE_KEY);
                String[] strArray = decodeString.split(",");
                if (strArray.length == 3) {
                    //格式为  DES("id,useragent,ip")
                    //User-Agent IP 要一致
                    if (WebHelper.getUseAgent(request).equals(strArray[1])
                            && WebHelper.getIpAddr(request).equals(strArray[2])) {
//						WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
//						A a = wac.getBean(A.class);


                        //存入session
                        session.setAttribute(Constants.SESSION_LOGIN_USER_KEY, ei);
                    }
                }
            }
        }
        return ei;
    }
}
