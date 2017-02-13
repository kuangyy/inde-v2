package cn.kykys.index.controller.index;

import cn.kykys.index.controller.BaseController;
import cn.kykys.index.data.kykys.MottoModelMapper;
import cn.kykys.index.ibusiness.blog.IPosts;
import cn.kykys.index.ibusiness.blog.ITags;
import cn.kykys.index.model.blog.MottoModel;
import cn.kykys.index.model.blog.TagsModel;
import cn.kykys.index.model.page.PageWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by kuangye on 2016/9/18.
 */
@Controller
public class IndexController extends BaseController {

    @Autowired
    IPosts iPosts;
    @Autowired
    ITags iTags;
    @Autowired
    MottoModelMapper mottoModelMapper;


    //home page
    @RequestMapping("/")
    public ModelAndView index() {

        ModelAndView mav = new ModelAndView("index");

        //motto
        MottoModel mottoModel = mottoModelMapper.selectByRandom();
        mav.addObject("motto", mottoModel);

        return mav;
    }

    //game page
    @RequestMapping("/game")
    public ModelAndView game() {

        ModelAndView mav = new ModelAndView("/game/index");

        return mav;
    }


    //jpfun page
    @RequestMapping("/fun")
    public ModelAndView fun() {

        ModelAndView mav = new ModelAndView("/fun/index");

        return mav;
    }


    //about
    @RequestMapping("/about")
    public ModelAndView about() {
        return new ModelAndView("about");
    }

    //404
    @RequestMapping("/404")
    public ModelAndView err404() {
        return new ModelAndView("404");
    }
}
