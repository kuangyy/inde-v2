package cn.kykys.index.utils.web;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * ResponseHelper
 *
 * @author liwei
 * @date 16/8/11
 * @description
 */
public class ResponseHelper {
    private static ObjectMapper objectMapper = new ObjectMapper();
    public static void responseJSON(HttpServletResponse response ,Object object){
        OutputStream ps = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            ps = response.getOutputStream();
            ps.write(objectMapper.writeValueAsString(object).getBytes("UTF-8"));
        } catch (IOException e) {
            //
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
