package cn.kykys.index.controller.research;

import cn.kykys.index.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by kuangye on 2016/12/1.
 */
@Controller
@RequestMapping("/research")
public class ResearchController extends BaseController {


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

}
