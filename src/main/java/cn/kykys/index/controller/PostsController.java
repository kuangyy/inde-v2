package cn.kykys.index.controller;

import cn.kykys.index.annotations.MustLogin;
import cn.kykys.index.constants.Constants;
import cn.kykys.index.utils.security.SecurityHelper;
import cn.kykys.index.utils.web.WebHelper;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by kuangye on 2016/9/20.
 */
@Controller
public class PostsController extends BaseController {

    @MustLogin
    @RequestMapping("/posts")
    public ModelAndView posts() {

        ModelAndView mav = new ModelAndView("/manage/posts/list");


        return mav;
    }

    @MustLogin
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception {


        return new ModelAndView("redirect:/manage/index");
    }


    @MustLogin
    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws Exception {


        return new ModelAndView("redirect:/manage/index");
    }


    @MustLogin
    @RequestMapping("/delete")
    public
    @ResponseBody
    JSONObject list() {

        JSONObject result = new JSONObject();


        return result;
    }


}
