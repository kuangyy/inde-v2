package cn.kykys.index.controller.fun;

import cn.kykys.index.ibusiness.game.IDrama;
import cn.kykys.index.model.page.PageWeb;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by kuangye on 2017/02/13.
 */
@Controller
@RequestMapping("/fun")
public class FunController {

    @Autowired
    IDrama iDrama;





    @ResponseBody
    @RequestMapping(value = "/more",method = RequestMethod.POST)
    public JSONObject editDramaDo(HttpServletRequest request, Integer page) {
        JSONObject result = new JSONObject();

        return result;
    }
}
