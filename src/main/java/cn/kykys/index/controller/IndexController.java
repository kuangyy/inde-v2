package cn.kykys.index.controller;

import cn.kykys.index.model.page.PageWeb;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by kuangye on 2016/9/18.
 */
@Controller
public class IndexController extends BaseController {


    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
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



    @RequestMapping("/404")
    public ModelAndView err404() {
        return new ModelAndView("404");
    }
}
