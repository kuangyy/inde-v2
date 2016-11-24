package cn.kykys.index.controller.game;

import cn.kykys.index.ibusiness.game.IDrama;
import cn.kykys.index.model.dto.gooflow.GooflowModel;
import cn.kykys.index.model.page.PageWeb;
import cn.kykys.index.model.wechat.DramaModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by kuangye on 2016/11/14.
 */
@Controller
@RequestMapping("/game")
public class GameController {

    @Autowired
    IDrama iDrama;


    @RequestMapping("/setting")
    public ModelAndView setting(HttpServletRequest request, PageWeb pageWeb) {
        ModelAndView mav = new ModelAndView("/game/admin/setting");

        mav.addAllObjects(iDrama.searchByPage(null, pageWeb));
        return mav;
    }


    @RequestMapping("/setting/editDrama/{id}")
    public ModelAndView editDrama(HttpServletRequest request, @PathVariable("id") Integer id) {
        ModelAndView mav = new ModelAndView("/game/admin/editDrama");

        mav.addObject("drama", iDrama.getById(id));
        return mav;
    }

    @ResponseBody
    @RequestMapping(value = "/setting/editDramaDo/{id}",method = RequestMethod.POST)
    public JSONObject editDramaDo(HttpServletRequest request, @PathVariable("id") Integer id,DramaModel dramaModel) {
        JSONObject result = new JSONObject();

        GooflowModel gooflowModel = JSON.parseObject(dramaModel.getData(),GooflowModel.class);

        iDrama.updateDrama(id,gooflowModel,dramaModel);

        result.put("success",1);



        return result;
    }
}
