package cn.kykys.index.controller.research;

import cn.kykys.index.controller.BaseController;
import cn.kykys.index.data.kykys.MottoModelMapper;
import cn.kykys.index.ibusiness.blog.IPosts;
import cn.kykys.index.ibusiness.blog.ITags;
import cn.kykys.index.model.blog.MottoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by kuangye on 2016/12/1.
 */
@Controller
@RequestMapping("/research")
public class ResearchController extends BaseController {

    @Autowired
    IPosts iPosts;
    @Autowired
    ITags iTags;
    @Autowired
    MottoModelMapper mottoModelMapper;


    //research page
    @RequestMapping("/")
    public ModelAndView index() {

        ModelAndView mav = new ModelAndView("/research/index");

//


        return mav;
    }



    //about
    @RequestMapping("/about")
    public ModelAndView about() {
        return new ModelAndView("/research/about");
    }

    //404
    @RequestMapping("/404")
    public ModelAndView err404() {
        return new ModelAndView("404");
    }
}
