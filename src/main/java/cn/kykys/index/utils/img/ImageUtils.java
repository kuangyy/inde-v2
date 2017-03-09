package cn.kykys.index.utils.img;

import cn.kykys.index.utils.LogUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Calendar;

/**
 * 图片上传
 */
public class ImageUtils {


    /**
     * 打水印 默认居中
     * @param waterImagePath
     * @param is
     * @param imgFormat
     * @param alpha
     * @param xOffset
     * @param yOffset
     * @return
     * @throws IOException
     */
    public static InputStream waterImage(String waterImagePath, InputStream is, String imgFormat, float alpha, int xOffset, int yOffset) throws IOException {

        BufferedImage uploadImage = ImageIO.read(is);
        //图片源大小
        int sourceImageWidth = uploadImage.getWidth();
        int sourceImageHeight = uploadImage.getHeight();

        //太小打不了水印
        if (sourceImageWidth < 300 && sourceImageHeight < 300) {
            return is;
        }

        int imageType = BufferedImage.TYPE_INT_RGB;
        if (imgFormat != null && imgFormat.toLowerCase().equals("png")) {
            imageType = BufferedImage.TYPE_INT_ARGB;
        }
        BufferedImage bufferedImage = new BufferedImage(sourceImageWidth, sourceImageHeight, imageType);
        Graphics2D g = bufferedImage.createGraphics();
        try {
            //draw 原图
            g.drawImage(uploadImage, 0, 0, sourceImageWidth, sourceImageHeight, null);
            //透明度 start
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            //上传水印图片
            Image waterImg = ImageIO.read(new File(waterImagePath));
            //水印源大小
            int waterImageOriginWidth = waterImg.getWidth(null);
            int waterImageOriginHeight = waterImg.getHeight(null);

            Image smallImg;
            //水印大小
            int width;
            int height;
            //水印坐标
            int x;
            int y;
            //需要判断比例
            // oh/ow = h/w
            if (sourceImageWidth / sourceImageHeight > waterImageOriginWidth / waterImageOriginHeight) {
                //以高为准 居中
                y = sourceImageHeight / 3;
                height = sourceImageHeight / 3;
                //cal
                width = height * waterImageOriginWidth / waterImageOriginHeight;
                //居中
                x = sourceImageWidth / 2 - width / 2;
            } else {
                //以长为准 居中
                x = sourceImageWidth / 3;
                width = sourceImageWidth / 3;
                //cal
                height = width * waterImageOriginHeight / waterImageOriginWidth;
                //居中
                y = sourceImageHeight / 2 - height / 2;
            }
            //如果比原图大就用原图
            if (width > waterImageOriginWidth && height > waterImageOriginHeight) {
                smallImg = waterImg;
                width = waterImageOriginWidth;
                height = waterImageOriginHeight;
                x = sourceImageWidth / 2 - width / 2;
                y = sourceImageHeight / 2 - height / 2;
            } else {
                //缩放
                smallImg = waterImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            }
            //draw 水印
            g.drawImage(smallImg, x, y, width, height, null);
            //透明度 end
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, imgFormat, os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (Exception e) {
            LogUtil.error("图片添加水印失败", e);
        } finally {
            g.dispose();
        }

        return is;
    }

}
