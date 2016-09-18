package cn.kykys.index.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by kuangye on 2016/9/18.
 */
@Controller
public class IndexController extends BaseController {


    @RequestMapping("/")
    public ModelAndView index(){
        return new ModelAndView("index");
    }


}
