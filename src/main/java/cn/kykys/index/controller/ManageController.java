package cn.kykys.index.controller;

import cn.kykys.index.model.page.PageWeb;
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
    public ModelAndView access(HttpServletRequest request, HttpServletResponse response, String password) {

        if (StringUtils.hasText(password) && password.equals("0132352487ky.com")) {
            return new ModelAndView("/manage/index");
        }

        return new ModelAndView("redirect:/404");
    }


    @RequestMapping("/list")
    public ModelAndView list() {

        ModelAndView mav = new ModelAndView("list");

        //分页
        PageWeb pageWeb = new PageWeb();
        pageWeb.setPageIndex(1);
        pageWeb.setCount(10);
        mav.addObject("pageWeb", pageWeb);
        return mav;
    }


    @RequestMapping("/detail")
    public ModelAndView detail() {

        ModelAndView mav = new ModelAndView("detail");


        return mav;
    }


}
