package cn.kykys.index.business.file;

import cn.kykys.index.model.dto.ExecuteResultModel;
import cn.kykys.index.model.exception.BusinessException;
import cn.kykys.index.utils.file.FileHelper;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by john on 15/10/27.
 */
public class AudioUpload extends FileAbstract {

    protected Integer maxSize = 30 * 1024 * 1024;

    static final String HOST = BASE_PATH + "/file/audio/";

    public AudioUpload(CommonsMultipartFile file) {
        super(file);
    }

    public AudioUpload(InputStream inputStream, String fileName) {
        super(inputStream,fileName);
    }


    @Override
    protected ExecuteResultModel upload(InputStream is, String filePath) {
        ExecuteResultModel resultModel = new ExecuteResultModel();
        resultModel.setIsSuccess(false);
        try {

            if (is.available() > this.maxSize) {
                resultModel.setMessage("文件超过上传大小限制");
            } else {

                StringBuffer path;

                try {
                    path = new StringBuffer(FileHelper.getPicturePath());

                    path.append(File.separator).append("audio").append(File.separator);

                    path.append(filePath);

                    //写入文件
                    File file = new File(path.toString());

                    File parent = new File(file.getParent());
                    parent.mkdirs();

                    if (!file.exists())
                        file.createNewFile();

                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int i;
                    while ((i = is.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, i);
                    }

                    fileOutputStream.flush();
                    fileOutputStream.close();
                    is.close();

                } catch (Exception e) {
                    throw new BusinessException("文件上传失败");
                }

                resultModel.setIsSuccess(true);
                resultModel.setMessage(HOST + filePath);

            }
        } catch (Exception e) {
            resultModel.setMessage("上传失败");
            e.printStackTrace();
        }
        return resultModel;
    }

}
