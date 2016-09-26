package cn.kykys.index.controller;

import cn.kykys.index.annotations.MustLogin;
import cn.kykys.index.data.MottoModelMapper;
import cn.kykys.index.ibusiness.IPosts;
import cn.kykys.index.ibusiness.ITags;
import cn.kykys.index.model.MottoModel;
import cn.kykys.index.model.PostsModel;
import cn.kykys.index.model.TagsModel;
import cn.kykys.index.model.dto.ContentModel;
import cn.kykys.index.model.page.PageWeb;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by kuangye on 2016/9/26.
 */
@Controller
public class BookController extends BaseController {

    @Autowired
    IPosts iPosts;
    @Autowired
    ITags iTags;
    @Autowired
    MottoModelMapper mottoModelMapper;


    @RequestMapping("/books")
    public ModelAndView books() {

        ModelAndView mav = new ModelAndView("book");

        PageWeb pageWeb = new PageWeb();
        pageWeb.setPageSize(3);

        //hot tags
        List<TagsModel> hotTagList = iTags.selectHotTagByPage(pageWeb, 3, true);
        mav.addObject("hotTagList", hotTagList);

        //all tags
        List<TagsModel> allTagList = iTags.selectHotTagByPage(null);
        mav.addObject("allTagList", allTagList);

        //motto
        MottoModel mottoModel = mottoModelMapper.selectByRandom();
        mav.addObject("motto", mottoModel);

        return mav;
    }


    //search
    @RequestMapping("/books/s")
    public ModelAndView search(String wd, PageWeb pageWeb) {

        ModelAndView mav = new ModelAndView("list");

        if (StringUtils.hasText(wd)) {
            String word = StringEscapeUtils.unescapeHtml(wd);

            Map<String, ?> map = iPosts.searchByPage(word, pageWeb);
            mav.addAllObjects(map);
        } else {
            mav = this.list(pageWeb);

        }

        mav.addObject("wd", wd);

        return mav;
    }

    //list
    @RequestMapping("/books/list")
    public ModelAndView list(PageWeb pageWeb) {

        ModelAndView mav = new ModelAndView("list");

        Map<String, ?> result = iPosts.selectByPage(null, pageWeb);
        mav.addAllObjects(result);
        return mav;
    }




}
