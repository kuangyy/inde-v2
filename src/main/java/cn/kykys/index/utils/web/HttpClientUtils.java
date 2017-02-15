package cn.kykys.index.utils.web;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpClientUtils {
	
	public static final String ENCODING = "UTF-8";
	
	/**
	 * 将map转换成url
	 * 
	 * @param map
	 * @return
	 */
	public static String getUrlParamsByMap(Map<String, String> map) {
		if (map == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue());
			sb.append("&");
		}
		String s = sb.toString();
		if (s.endsWith("&")) {
			s = StringUtils.substringBeforeLast(s, "&");
		}
		return s;
	}
	
	/**
	 * 对Query参数进行编码
	 * 
	 * @param url
	 * @return
	 */
	public static String encodeUrl(String url) {
		if (StringUtils.isBlank(url)) {
			return url;
		}

		try {
			URL tmpUrl = new URL(url);
			StringBuilder sb = new StringBuilder();
			sb.append(tmpUrl.getProtocol());
			if (StringUtils.isNotBlank(tmpUrl.getProtocol())) {
				sb.append("://");
			}
			sb.append(tmpUrl.getHost());
			if (StringUtils.isNotBlank(tmpUrl.getPath())) {
				sb.append(tmpUrl.getPath());
			}
			if (StringUtils.isNotBlank(tmpUrl.getQuery())) {
				sb.append("?");

				boolean flag = false;
				for (String s : tmpUrl.getQuery().split("\\&")) {
					if (flag) {
						sb.append("&");
					}

					flag = true;
					String key = s.split("\\=")[0];
					String value = s.split("\\=")[1];
					sb.append(key);
					sb.append("=");
					sb.append(URLEncoder.encode(value, ENCODING));
				}
			}

			return sb.toString();
		} catch (MalformedURLException e) {
			return url;
		} catch (UnsupportedEncodingException e) {
			return url;
		}
	}
	
	public static String sendObjectJson(String url,Object obj) throws ClientProtocolException, IOException{
		String jsonString = JSON.toJSONString(obj);
		StringEntity params = new StringEntity(jsonString, Charset.forName("utf-8"));
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("content-type", "application/json; encoding=utf-8");
		httpPost.setEntity(params);
		HttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity entity = httpResponse.getEntity();
		if(entity != null){
			String responseString = EntityUtils.toString(entity,Charset.forName("utf-8"));
			return responseString;
		}
		return null;
	}
	
	public static String sendObjectXml(String url,StringWriter sw) throws ClientProtocolException, IOException{
		StringEntity params = new StringEntity(sw.toString(), Charset.forName("utf-8"));
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("content-type", "text/xml; encoding=utf-8");
		httpPost.setEntity(params);
		HttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity entity = httpResponse.getEntity();
		if(entity != null){
			String responseString = EntityUtils.toString(entity,Charset.forName("utf-8"));
			return responseString;
		}
		return null;
	}
}
