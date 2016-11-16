package cn.kykys.index.controller.blog;

import cn.kykys.index.annotations.MustLogin;
import cn.kykys.index.controller.BaseController;
import cn.kykys.index.ibusiness.blog.IPosts;
import cn.kykys.index.model.blog.PostsModel;
import cn.kykys.index.model.dto.ContentModel;
import cn.kykys.index.model.page.PageWeb;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by kuangye on 2016/9/20.
 */
@Controller
@RequestMapping("/manage/posts")
public class PostsController extends BaseController {

    @Autowired
    IPosts iPosts;

    @MustLogin
    @RequestMapping
    public ModelAndView posts(PostsModel postsModel, PageWeb pageWeb) {

        ModelAndView mav = new ModelAndView("/manage/posts/list");

        Map<String, ?> result = iPosts.selectByPage(postsModel, pageWeb);
        mav.addAllObjects(result);

        return mav;
    }

    @MustLogin
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception {


        return new ModelAndView("/manage/posts/edit");
    }


    @MustLogin
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public ModelAndView update(HttpServletRequest request, HttpServletResponse response,
                               Long id) throws Exception {

        ModelAndView mav = new ModelAndView("/manage/posts/edit");
        if (id == null) {
            return super.goError();
        }
        mav.addObject("posts", iPosts.getById(id));

        return mav;
    }

    @MustLogin
    @RequestMapping(value = "/updateDo", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject updateDo(HttpServletRequest request, HttpServletResponse response,
                               Boolean update, PostsModel model,
                               String publishTimeString, ContentModel contentModel,
                               String tags) throws Exception {

        JSONObject jsonObject = super.getJSON();

        contentModel.setMarkdownContent(contentModel.getMarkdownContent().replaceAll("\\n", ""));
        model.setContent(JSON.toJSONString(contentModel));


        if (update != null && update) {
            jsonObject.put("status", iPosts.update(model, tags));
        } else {
            jsonObject.put("status", iPosts.add(model, tags));
        }

        return jsonObject;
    }


    @MustLogin
    @RequestMapping("/delete")
    @ResponseBody
    public JSONObject list(Long id) {
        JSONObject result = super.getJSON();
        result.put("status", iPosts.delete(id));
        return result;
    }


}
