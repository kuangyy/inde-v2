package cn.kykys.index.controller;

import cn.kykys.index.annotations.MustLogin;
import cn.kykys.index.ibusiness.IMotto;
import cn.kykys.index.model.MottoModel;
import cn.kykys.index.model.page.PageWeb;
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
@RequestMapping("/manage/motto")
public class MottoController extends BaseController {

    @Autowired
    IMotto iMotto;

    @MustLogin
    @RequestMapping
    public ModelAndView posts(MottoModel mottoModel, PageWeb pageWeb) {

        ModelAndView mav = new ModelAndView("/manage/motto/list");

        Map<String, ?> result = iMotto.selectByPage(mottoModel, pageWeb);
        mav.addAllObjects(result);

        return mav;
    }

    @MustLogin
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception {

        return new ModelAndView("/manage/motto/edit");
    }


    @MustLogin
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public ModelAndView update(HttpServletRequest request, HttpServletResponse response,
                               Long id) throws Exception {

        ModelAndView mav = new ModelAndView("/manage/motto/edit");
        if (id == null) {
            return super.goError();
        }
        mav.addObject("motto", iMotto.getById(id));

        return mav;
    }

    @MustLogin
    @RequestMapping(value = "/updateDo", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject updateDo(HttpServletRequest request, HttpServletResponse response,
                               Boolean update, MottoModel model) throws Exception {

        JSONObject jsonObject = super.getJSON();

        if (update != null && update) {
            jsonObject.put("status", iMotto.update(model));
        } else {
            jsonObject.put("status", iMotto.add(model));
        }

        return jsonObject;
    }


    @MustLogin
    @RequestMapping("/delete")
    @ResponseBody
    public JSONObject list(Long id) {
        JSONObject result = super.getJSON();
        result.put("status", iMotto.delete(id));
        return result;
    }


}
