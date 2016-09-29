package cn.kykys.index.controller;

import cn.kykys.index.ibusiness.IPhoto;
import cn.kykys.index.model.page.PageWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by kuangye on 2016/9/28.
 */
@Controller
public class PhotoController extends BaseController {

    @Autowired
    IPhoto iPhoto;

    @RequestMapping("/photos")
    public ModelAndView books(PageWeb pageWeb) {

        ModelAndView mav = new ModelAndView("photo/list");

        Map<String, ?> map = iPhoto.searchByPage(pageWeb);
        mav.addAllObjects(map);

        return mav;
    }


}
