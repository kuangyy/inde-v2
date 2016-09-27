package cn.kykys.index.controller;

import cn.kykys.index.annotations.MustLogin;
import cn.kykys.index.data.MottoModelMapper;
import cn.kykys.index.ibusiness.IBook;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by kuangye on 2016/9/26.
 */
@Controller
public class BookController extends BaseController {

    @Autowired
    IBook iBook;
    @Autowired
    ITags iTags;
    @Autowired
    MottoModelMapper mottoModelMapper;


    @RequestMapping("/books")
    public ModelAndView books() {

        ModelAndView mav = new ModelAndView("book/books");

        PageWeb pageWeb = new PageWeb();
        pageWeb.setPageSize(10);

        //hot tags
        List<TagsModel> hotTagList = iTags.selectHotTagByPage(pageWeb, 0, false);
        mav.addObject("hotTagList", hotTagList);


        //motto
        MottoModel mottoModel = mottoModelMapper.selectByRandom();
        mav.addObject("motto", mottoModel);


        //随机书目
        PageWeb pageWebRandom = new PageWeb();
        pageWebRandom.setPageIndex(new Random().nextInt(720) + 1);
        Map<String, ?> result = iBook.searchByPage(null, pageWebRandom);
        mav.addAllObjects(result);

        return mav;
    }


    //book
    @RequestMapping("/b/{id}")
    public ModelAndView detail(@PathVariable("id") Long id) {

        ModelAndView mav = new ModelAndView("/book/detail");

        mav.addObject("book", iBook.getByIdAddViewCount(id));
        return mav;
    }


    //search
    @RequestMapping("/books/s")
    public ModelAndView search(String wd, PageWeb pageWeb) {

        ModelAndView mav = new ModelAndView("book/list");

        if (StringUtils.hasText(wd)) {
            String word = StringEscapeUtils.unescapeHtml(wd);

            Map<String, ?> map = iBook.searchByPage(word, pageWeb);
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

        ModelAndView mav = new ModelAndView("/book/list");

        Map<String, ?> result = iBook.searchByPage(null, pageWeb);
        mav.addAllObjects(result);
        return mav;
    }


}
