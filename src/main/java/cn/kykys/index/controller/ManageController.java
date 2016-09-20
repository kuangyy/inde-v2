package cn.kykys.index.controller;

import cn.kykys.index.annotations.MustLogin;
import cn.kykys.index.constants.Constants;
import cn.kykys.index.model.page.PageWeb;
import cn.kykys.index.utils.security.SecurityHelper;
import cn.kykys.index.utils.web.WebHelper;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by kuangye on 2016/9/18.
 */
@Controller
@RequestMapping("/manage")
public class ManageController extends BaseController {


    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("/manage/login");
    }

    @RequestMapping(value = "/access", method = RequestMethod.POST)
    public ModelAndView access(HttpServletRequest request, HttpServletResponse response, String password, Boolean remember) throws Exception {

        if (StringUtils.hasText(password) && password.equals("0132352487ky.com")) {

            request.getSession().setAttribute(Constants.SESSION_LOGIN_USER_KEY, password);
            //设置cookie  格式 DES("id,useragent,ip")
            String cookieValue = SecurityHelper.desEncrypt(
                    password + "," + WebHelper.getUseAgent(request) + "," + WebHelper.getIpAddr(request)
                    , Constants.DES_ENCODE_KEY);

            if (remember != null && remember) {
                //cookie 7天
                WebHelper.setCookie(response, Constants.COOKIE_LOGIN_USER_KEY, cookieValue, 7 * 24 * 3600, null, "/");
            }

            return new ModelAndView("redirect:/manage/index");
        }

        return new ModelAndView("redirect:/404");
    }


    @MustLogin
    @RequestMapping("/index")
    public ModelAndView list() {

        ModelAndView mav = new ModelAndView("/manage/index");


        return mav;
    }



}
