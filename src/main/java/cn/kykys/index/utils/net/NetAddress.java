package cn.kykys.index.utils.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;

/**
 * NetAddress
 *
 * @author liwei
 * @date 16/8/10
 * @description
 */
public class NetAddress {
    private static Logger logger = LogManager.getLogger(NetAddress.class);

    /**
     * 获取本机的IP地址
     * @return
     */
    public static String getLocalIP(){
        String localIP = null;
        try{
            InetAddress inetAddress = InetAddress.getLocalHost();
            if(inetAddress!= null){
                localIP = inetAddress.getHostAddress();
            }
        }
        catch (Exception ex){
            logger.error("getLocalAddress error!",ex);
        }
        return localIP;
    }
}
