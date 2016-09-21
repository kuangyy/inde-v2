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

public class DocUpload extends FileAbstract {


    static final String HOST = "http://localhost:8080/party-manage/file/doc/";


    public DocUpload(CommonsMultipartFile file) {
        format = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        try {
            inputStream = file.getInputStream();
        } catch (Exception e) {

        }
        maxSize = 5000 * 1024 * 1024;

    }

    public DocUpload(InputStream inputStream, String fileName) {
        format = fileName.substring(fileName.lastIndexOf(".") + 1);
        try {
            this.inputStream = inputStream;
        } catch (Exception e) {

        }
        maxSize = 5000 * 1024 * 1024;
    }

    /**
     * 上传逻辑
     *
     * @return
     * @throws IOException
     */
    @Override
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


    private ExecuteResultModel upload(InputStream is, String filePath) {
        ExecuteResultModel resultModel = new ExecuteResultModel();
        resultModel.setIsSuccess(false);
        try {

            if (is.available() > this.maxSize) {
                resultModel.setMessage("文件超过上传大小限制");
            } else {

                StringBuffer path;

                try {
                    path = new StringBuffer(FileHelper.getPicturePath());
                    String sep = System.getProperty("file.separator");
                    path.append(sep).append("doc").append(sep);

                    path.append(filePath);

                    //写入文件
                    File file = new File(path.toString());

                    File parent = new File(file.getParent());
                    parent.mkdirs();

                    if(!file.exists())
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
