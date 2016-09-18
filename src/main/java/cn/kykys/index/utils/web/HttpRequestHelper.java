package cn.kykys.index.utils.web;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * HttpRequest 帮助类,用于发送和接受Http请求
 */
public class HttpRequestHelper {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestHelper.class);

    /**
     * 以Get方式发起一个请求
     * @param url   请求URL
     * @return
     */
    public static HttpResponseModel get(String url){
        HttpResponseModel model = null;
        //创建默认的httpClient实例
        CloseableHttpClient httpClient = getHttpClient();
        try {
            //用get方法发送http请求
            HttpGet get = new HttpGet(url);
            CloseableHttpResponse httpResponse = null;
            //发送get请求
            httpResponse = httpClient.execute(get);
            try{
                //response实体
                HttpEntity entity = httpResponse.getEntity();
                model = new HttpResponseModel();
                model.setHttpStatusCode(httpResponse.getStatusLine().getStatusCode());
                if (null != entity){
                    model.setContent(EntityUtils.toString(entity));
                }
            }
            finally{
                httpResponse.close();
            }
        } catch (Exception e) {
            logger.error("获取get请求错误",e);
        }
        finally{
            try{
                closeHttpClient(httpClient);
            } catch (IOException e){
                logger.error("获取get请求错误",e);
            }
        }
        return model;
    }


    /**
     * 以POST方式发起http请求
     * @param url       请求URL
     * @param params    参数列表
     * @return
     */
    public static HttpResponseModel post(String url,List<NameValuePair> params){
        HttpResponseModel model = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPost post = new HttpPost(url);
            //url格式编码
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(params,"UTF-8");
            post.setEntity(uefEntity);

            //执行请求
            CloseableHttpResponse httpResponse = httpClient.execute(post);
            try{
                HttpEntity entity = httpResponse.getEntity();
                model.setHttpStatusCode(httpResponse.getStatusLine().getStatusCode());
                if (null != entity){
                    model.setContent(EntityUtils.toString(uefEntity));
                }
            } finally{
                httpResponse.close();
            }

        } catch( UnsupportedEncodingException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            try{
                closeHttpClient(httpClient);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return model;
    }

    /**
     * 获取一个http连接
     * @return
     */
    private static CloseableHttpClient getHttpClient(){
        return HttpClients.createDefault();
    }

    /**
     * 关闭一个http连接
     * @param client
     * @throws IOException
     */
    private static void closeHttpClient(CloseableHttpClient client) throws IOException{
        if (client != null){
            client.close();
        }
    }
	
	
}
