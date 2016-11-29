package cn.kykys.index.controller.index;

import cn.kykys.index.controller.BaseController;
import cn.kykys.index.data.kykys.MottoModelMapper;
import cn.kykys.index.ibusiness.blog.IPosts;
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

/**
 * Created by kuangye on 2016/9/18.
 */
@Controller
@RequestMapping("blog")
public class BlogController extends BaseController {

    @Autowired
    IPosts iPosts;
    @Autowired
    ITags iTags;
    @Autowired
    MottoModelMapper mottoModelMapper;


    //home page
    @RequestMapping("/")
    public ModelAndView index() {

        ModelAndView mav = new ModelAndView("blog/index");

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
    @RequestMapping("/s")
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
    @RequestMapping("/list")
    public ModelAndView list(PageWeb pageWeb) {

        ModelAndView mav = new ModelAndView("list");

        Map<String, ?> result = iPosts.selectByPage(null, pageWeb);
        mav.addAllObjects(result);
        return mav;
    }

    //article
    @RequestMapping("/p/{id}")
    public ModelAndView detail(@PathVariable("id") Long id) {

        ModelAndView mav = new ModelAndView("detail");

        mav.addObject("posts", iPosts.getByIdAddViewCount(id));
        return mav;
    }

    //tag
    @RequestMapping("/tag/{w}")
    public ModelAndView tag(@PathVariable("w") String w, PageWeb pageWeb) {

        ModelAndView mav = new ModelAndView("list");

        TagsModel tagsModel = iTags.getByName(w);

        mav.addAllObjects(iPosts.selectByTagWithPage(tagsModel.getId(), pageWeb));

        mav.addObject("pageWeb", pageWeb);
        mav.addObject("tag", tagsModel);

        return mav;
    }

    //tags
    @RequestMapping("/tags")
    public ModelAndView tag() {

        ModelAndView mav = new ModelAndView("tags");

       List<TagsModel> tagsModelList = iTags.selectHotTagByPage(null);

        mav.addObject("tagsModelList", tagsModelList);

        return mav;
    }


    //about
    @RequestMapping("/about")
    public ModelAndView about() {
        return new ModelAndView("blog/about");
    }

    //404
    @RequestMapping("/404")
    public ModelAndView err404() {
        return new ModelAndView("404");
    }
}
