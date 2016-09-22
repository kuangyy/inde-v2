package cn.kykys.index.controller;

import cn.kykys.index.annotations.MustLogin;
import cn.kykys.index.business.UtilsBusiness;
import cn.kykys.index.model.exception.BusinessException;
import cn.kykys.index.utils.file.FileHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kuangye on 2016/4/16.
 */
@Controller
@RequestMapping("/file")
public class FileController extends BaseController {


    /**
     * 文件上传均使用该方法，不论是mp3或png
     */
    @RequestMapping(value = "/fileupload", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> fileUpload(HttpServletRequest httpRequest, HttpServletResponse httpResponse, @RequestParam CommonsMultipartFile file) throws BusinessException {
        Map<String, Object> map = new HashMap<>();

        String fileUrl = UtilsBusiness.fileUpload(file);

        if (fileUrl != null && !fileUrl.equals("")) {
            map.put("success", true);
            map.put("url", fileUrl);
        } else {
            map.put("success", false);
        }

        return map;
    }

    /**
     * 多文件上传
     */
    @RequestMapping(value = "/fileuploads", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> fileUpload(HttpServletRequest httpRequest, HttpServletResponse httpResponse, @RequestParam CommonsMultipartFile[] files) throws BusinessException {
        Map<String, Object> map = new HashMap<>();

        List<String> fileUrls = new ArrayList<>();

        for(CommonsMultipartFile file :files){
            String fileUrl = UtilsBusiness.fileUpload(file);
            fileUrls.add(fileUrl);
        }


        if (fileUrls.size()>0) {
            map.put("success", true);
            map.put("urls", fileUrls);
        } else {
            map.put("success", false);
        }

        return map;
    }

    @MustLogin
    @RequestMapping(value = "/pic/{y}/{m}/{d}/{name}", method = RequestMethod.GET)
    public void pic(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                    @PathVariable("y") String y, @PathVariable("m") String m, @PathVariable("d") String d,
                    @PathVariable("name") String name) throws Exception {

        try {
            //获取后缀
            String uri = httpServletRequest.getRequestURI();
            String suffix = uri.substring(uri.lastIndexOf("."), uri.length());

            InputStream is = new FileInputStream(new File(FileHelper.getPicturePath()
                    + "/pic/" + y + File.separator + m + File.separator + d
                    + File.separator + name + suffix));

            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("image/*");

            this.uploadStream(httpServletResponse, is);

        } catch (Exception e) {
            throw new BusinessException("获取图片失败");
        }


    }


    @MustLogin
    @RequestMapping(value = "/video/{y}/{m}/{d}/{name}", method = RequestMethod.GET)
    public void video(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                      @PathVariable("y") String y, @PathVariable("m") String m, @PathVariable("d") String d,
                      @PathVariable("name") String name) throws Exception {

        try {
            //获取后缀
            String uri = httpServletRequest.getRequestURI();
            String suffix = uri.substring(uri.lastIndexOf("."), uri.length());

            InputStream is = new FileInputStream(new File(FileHelper.getPicturePath()
                    + "/video/" + y + File.separator + m + File.separator + d
                    + File.separator + name + suffix));

            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("video/*");

            this.uploadStream(httpServletResponse, is);

        } catch (Exception e) {
            throw new BusinessException("获取视频失败");
        }

    }

    @MustLogin
    @RequestMapping(value = "/doc/{y}/{m}/{d}/{name}", method = RequestMethod.GET)
    public void doc(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                    @PathVariable("y") String y, @PathVariable("m") String m, @PathVariable("d") String d,
                    @PathVariable("name") String name) throws Exception {

        try {
            //获取后缀
            String uri = httpServletRequest.getRequestURI();
            String suffix = uri.substring(uri.lastIndexOf("."), uri.length());

            InputStream is = new FileInputStream(new File(FileHelper.getPicturePath()
                    + "/doc/" + y + File.separator + m + File.separator + d
                    + File.separator + name + suffix));

            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/*");

            this.uploadStream(httpServletResponse, is);

        } catch (Exception e) {
            throw new BusinessException("获取文件失败");
        }


    }


    private void uploadStream(HttpServletResponse httpServletResponse, InputStream is) throws EOFException {

        byte[] buffer = new byte[1024];
        OutputStream os = null;
        try {
            os = httpServletResponse.getOutputStream();
            int i;
            while ((i = is.read(buffer)) > 0) {
                os.write(buffer, 0, i);
            }
            os.flush();
        } catch (IOException e) {
            //
        } finally {
            if (os != null) {
                try {
                    is.close();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
