package cn.kykys.index.business.file;


import cn.kykys.index.model.dto.ExecuteResultModel;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by john on 15/8/13.
 */
public abstract class FileAbstract {

    protected static String BASE_PATH = "https://kykys.cn";

    protected String path;


    /**
     * 格式
     */
    protected String format;
    /**
     * 文件流
     */
    protected InputStream inputStream;

    /**
     * 最大文件体积（Byte）上传校验使用
     */
    protected Integer maxSize = 10 * 1024 * 1024;



    public FileAbstract(CommonsMultipartFile file) {
        format = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        try {
            inputStream = file.getInputStream();
        } catch (Exception e) {

        }
    }

    public FileAbstract(InputStream inputStream, String fileName) {
        format = fileName.substring(fileName.lastIndexOf(".") + 1);
        try {
            this.inputStream = inputStream;
        } catch (Exception e) {

        }
    }

    /**
     * 上传文件
     *
     * @return
     * @throws IOException
     */
    public final ExecuteResultModel uploadFile() throws IOException {

        ExecuteResultModel resultModel = new ExecuteResultModel();


        return this.upload();

    }

    /**
     * 上传逻辑
     *
     * @return
     * @throws IOException
     */
    protected ExecuteResultModel upload() throws IOException {
        ExecuteResultModel resultModel;
        //文件上传地址
        Calendar clDate = Calendar.getInstance();
        String filePath = String.format("%s/%s/%s/%s.%s", clDate
                        .get(Calendar.YEAR), clDate.get(Calendar.MONTH) + 1,
                clDate.get(Calendar.DATE), java.util.UUID.randomUUID()
                        .toString(), this.format);

        resultModel = upload(this.inputStream, filePath);
        return resultModel;
    }


    abstract protected ExecuteResultModel upload(InputStream is, String filePath);

}
