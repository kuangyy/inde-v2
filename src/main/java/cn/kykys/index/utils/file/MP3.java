package cn.kykys.index.utils.file;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by kuangye on 2016/10/20.
 */
public class MP3 {


    public static void main(String[] args) throws Exception {

        File file = new File("E:\\CloudMusic\\双笙 - 采茶纪.mp3");

        FileInputStream fis = new FileInputStream(file);

        AudioFormat baseFormat = null;
        SourceDataLine line = null;


        AudioInputStream ais = AudioSystem.getAudioInputStream(file);
//        if (ais != null) {
//            baseFormat = ais.getFormat();
//            line = getLine(baseFormat);
//        }


        /**
         *  如果音频数据是压缩格式的，如MP3或Ogg，必须先进行一次转换——把MP3/Ogg解码成PCM。解码主要包括三个步骤：
         *  1、创建一个解压缩结果的定制AudioFormat（PCM编码），但保留和原压缩流一样的取样率、通道信息等。
         *  2、创建一个AudioInputStream把原来的AudioInputStream转换成新的AudioFormat格式。
         *  3、获得一个处理解码后格式的SourceDataLine。
         */
        AudioFormat decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false);


        ais = AudioSystem.getAudioInputStream(decodedFormat, ais);
        line = getLine(decodedFormat);


//
//        int inBytes = 0;
//        while ((inBytes != -1) && (!stopped) && (!threadExit)) {
//            try {
//                inBytes = ais.read(audioData, 0, BUFFER_SIZE);
//            }
//            catch (IOException e) { e.printStackTrace(); }
//            if (inBytes >= 0) {
//                int outBytes = line.write(audioData, 0, inBytes);
//            }
//            if (paused) waitforSignal();
//        }



    }




    private static SourceDataLine getLine(AudioFormat audioFormat) {
        SourceDataLine res = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                audioFormat);
        try {
            res = (SourceDataLine) AudioSystem.getLine(info);
            res.open(audioFormat);
        } catch (Exception e) {
        }
        return res;
    }


}
