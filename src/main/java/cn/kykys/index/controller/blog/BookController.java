package cn.kykys.index.controller.blog;

import cn.kykys.index.controller.BaseController;
import cn.kykys.index.data.kykys.MottoModelMapper;
import cn.kykys.index.ibusiness.blog.IBook;
import cn.kykys.index.ibusiness.blog.ITags;
import cn.kykys.index.model.blog.MottoModel;
import cn.kykys.index.model.blog.TagsModel;
import cn.kykys.index.model.page.PageWeb;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
