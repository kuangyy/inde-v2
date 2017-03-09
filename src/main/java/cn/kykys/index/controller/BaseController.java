package cn.kykys.index.controller;

import cn.kykys.index.model.exception.BusinessException;
import cn.kykys.index.utils.DateUtils;
import cn.kykys.index.utils.web.WebHelper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * BaseController是所有Controller的基类,用于一些统一/公共部分的处理
 *
 * @date 16/6/27
 * @description 所有Controller的基类
 */
//@ControllerAdvice
public abstract class BaseController {
    private static Logger logger = LogManager.getLogger(BaseController.class);

    @Resource
    private Validator validator;

    @ModelAttribute
    private void initAttribute(ModelMap model) {
        model.put("sys_time", DateUtils.getCalendar().getTimeInMillis());
    }

    protected void output(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Object object) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.output(httpRequest, httpResponse, object, dateFormat);
    }

    /**
     * API输出
     *
     * @param httpRequest  HttpServletRequest
     * @param httpResponse HttpServletResponse
     * @param object       需要输出的Object
     */
    protected void output(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Object object, DateFormat dateFormat) {
        OutputStream ps = null;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(dateFormat);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        if (WebHelper.isAjax(httpRequest)) {
            try {
                ps = httpResponse.getOutputStream();
                ps.write(objectMapper.writeValueAsString(object).getBytes("UTF-8"));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new BusinessException(e.getMessage(), -1);
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            try {

                if (httpRequest.getRequestURL().indexOf("/wechat") > 0) {
                    ps = httpResponse.getOutputStream();
                    ps.write("success" .getBytes());
                    ps.flush();
                    ps.close();
                }else{
                    httpResponse.sendRedirect("/404");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 异常处理,当捕获异常时统一使用此方法处理
     *
     * @param request
     * @param response
     * @param ex
     */
    @ExceptionHandler
    private void exceptionProcess(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml; charset=utf-8");

        output(request, response, ex);
    }

    protected void validate(Object obj, Class<?>... groups) throws Exception {
        String errorMsg = null;
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj, groups);
        if (constraintViolations.size() > 0) {
            Map<String, String> errorMap = new HashMap<String, String>();
            for (ConstraintViolation<Object> cv : constraintViolations) {
                errorMap.put(cv.getPropertyPath().toString(), cv.getMessage());
            }
            errorMsg = JSON.toJSONString(errorMap);
        }
        if (errorMsg != null) {
            throw new BusinessException(errorMsg, -1);
        }
    }

    protected void success(Object obj, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
        this.output(httpRequest, httpResponse, obj);
    }

    protected void success(Object obj, HttpServletRequest httpRequest, HttpServletResponse httpResponse, DateFormat dateFormat) throws Exception {
        this.output(httpRequest, httpResponse, obj, dateFormat);
    }


    public ModelAndView goError() {
        return new ModelAndView("redirect:/404");
    }

    public JSONObject getJSON() {
        JSONObject json = new JSONObject();
        json.put("status", -1);
        return json;
    }
}
