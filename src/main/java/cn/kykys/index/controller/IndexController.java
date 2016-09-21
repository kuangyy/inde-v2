package cn.kykys.index.controller;

import cn.kykys.index.ibusiness.IPosts;
import cn.kykys.index.model.page.PageWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by kuangye on 2016/9/18.
 */
@Controller
public class IndexController extends BaseController {

    @Autowired
    IPosts iPosts;

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }


    @RequestMapping("/list")
    public ModelAndView list(PageWeb pageWeb) {

        ModelAndView mav = new ModelAndView("list");

        Map<String, ?> result = iPosts.selectByPage(null, pageWeb);
        mav.addAllObjects(result);
        return mav;
    }


    @RequestMapping("/p/{id}")
    public ModelAndView detail(@PathVariable("id")Long id) {

        ModelAndView mav = new ModelAndView("detail");

        mav.addObject("posts", iPosts.getByIdAddViewCount(id));
        return mav;
    }



    @RequestMapping("/404")
    public ModelAndView err404() {
        return new ModelAndView("404");
    }
}
