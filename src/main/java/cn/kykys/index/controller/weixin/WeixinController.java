package cn.kykys.index.controller.weixin;

import cn.kykys.index.ibusiness.weixin.IWechat;
import cn.kykys.index.utils.weixin.MessageUtil;
import cn.kykys.index.utils.weixin.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 微信核心处理器
 * Created by kuangye on 2016/11/14.
 */
@RestController
@RequestMapping("/weixin")
public class WeixinController {

    @Autowired
    IWechat iWechat;

    @RequestMapping(method = RequestMethod.GET)
    public String check(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");

        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {
            return echostr;
        }
        return null;
    }


    @RequestMapping(method = RequestMethod.POST)
    public String message(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //改为拦截器处理
        Map<String, String> requestMap = (Map<String, String>) request.getAttribute("inputMap");

        return iWechat.processRequest(requestMap);
    }

}
