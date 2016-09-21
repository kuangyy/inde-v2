/**   
 * @Title: utils.java 
 * @Package com.yiminbang.business 
 * @Description: 业务操作工具类
 * @author liwei 
 * @date 2015年5月4日 下午6:54:52 
 * @version V1.0   
 */
package cn.kykys.index.business;

import cn.kykys.index.business.file.FileAbstract;
import cn.kykys.index.business.file.FileUploadBusinessFactory;
import cn.kykys.index.model.dto.ExecuteResultModel;
import cn.kykys.index.model.exception.BusinessException;
import org.springframework.web.multipart.commons.CommonsMultipartFile;


/**
 * @ClassName: utils
 * @Description: 业务操作工具类
 * @date 2015年5月4日 下午6:54:52
 * 
 */
public class UtilsBusiness {


	/**
	 * 获取sub 在str中出现的次数
	 * 
	 * @param str
	 * @param sub
	 * @return
	 */
	public static int getCount(String str, String sub) {
		int index = 0;
		int count = 0;
		while ((index = str.indexOf(sub, index)) != -1) {

			index = index + sub.length();
			count++;
		}
		return count;
	}



    /**
     * 文件上传
     * @date 2015/08/13
     * @param file
     * @return
     */
    public static String fileUpload(CommonsMultipartFile file) {
        String fileUlr="";

        try {
            if(file!=null) {
                FileAbstract ifile = FileUploadBusinessFactory.create(file.getInputStream(), file.getOriginalFilename());

                ExecuteResultModel executeResult = ifile.uploadFile();

                if (executeResult.isSuccess()) {

                    fileUlr = executeResult.getMessage();
                }
            }

        } catch (Exception e) {
            throw new BusinessException("文件出错了");
        }
        return fileUlr;
    }
}
