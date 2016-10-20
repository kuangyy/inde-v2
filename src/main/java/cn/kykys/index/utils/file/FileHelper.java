package cn.kykys.index.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by kuangye on 2016/4/13.
 */
public class FileHelper {

    public static String getPicturePath() throws Exception {
        StringBuffer path = new StringBuffer(System.getProperty("catalina.home") == null ? "" : System.getProperty("catalina.home"));
        if (path.length() == 0) {
            path.append(System.getProperty("catalina.base") == null ? "" : System.getProperty("catalina.base"));
        }
        if (path.length() == 0) {
            throw new Exception("+++++++++++system error: server home is not found!+++++++++++");
        } else {
            return path.toString();
        }
    }


    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    public static FileTypeEnum getFileType(File file, int length) throws IOException {

        FileInputStream is = new FileInputStream(file);
        byte[] b = new byte[length];
        is.read(b);

        String prefix = bytesToHexString(b);

        for (FileTypeEnum fileTypeEnum : FileTypeEnum.values()) {
            if (prefix.startsWith(fileTypeEnum.getBytePrefix().toString())) {
                return fileTypeEnum;
            }
        }
        return null;
    }

    public static FileTypeEnum getFileType(File file) throws IOException {
        return FileHelper.getFileType(file, 16);
    }

    public static void main(String[] args) throws Exception {

       File file = new File("E:\\CloudMusic\\双笙 - 采茶纪.mp3");

    }


}
