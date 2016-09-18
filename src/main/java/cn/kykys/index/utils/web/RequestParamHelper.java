package cn.kykys.index.utils.web;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * WebHelper
 *
 * @author liwei
 * @date 16/8/4
 * @description
 *
 * Web中Request相关常用方法帮助类
 */
public class RequestParamHelper {
	public static String getString(HttpServletRequest request, String paramName){
		return getString(request, paramName, null);
	}
	
	public static String getString(HttpServletRequest request, String paramName, String defaultValue){
		if(request.getParameter(paramName) != null){
			defaultValue = request.getParameter(paramName);
		}
		return defaultValue;
	}
	
	public static Integer getInteger(HttpServletRequest request, String paramName){
		return getInteger(request, paramName,null);
	}
	
	public static Integer getInteger(HttpServletRequest request, String paramName, Integer defalutValue){
		if(request.getParameter(paramName) != null){
			try{
				defalutValue = Integer.parseInt(request.getParameter(paramName));
			}
			catch(Exception ex){;}
		}
		return defalutValue;
	}

	public static Short getShort(HttpServletRequest request, String paramName){
		return getShort(request, paramName,null);
	}

	public static Short getShort(HttpServletRequest request, String paramName, Short defalutValue){
		if(request.getParameter(paramName) != null){
			try{
				defalutValue = Short.parseShort(request.getParameter(paramName));
			}
			catch(Exception ex){;}
		}
		return defalutValue;
	}

	public static BigDecimal getBigDecimal(HttpServletRequest request, String paramName){
		BigDecimal result = null;
		if(request.getParameter(paramName) != null){
			try{
				result = new BigDecimal(request.getParameter(paramName));
			}
			catch(Exception ex){;}
		}
		return result;
	}
	
	public static Float getFloat(HttpServletRequest request, String paramName){
		Float result = null;
		if(request.getParameter(paramName) != null){
			try{
				result = Float.parseFloat(request.getParameter(paramName));
			}
			catch(Exception ex){;}
		}
		return result;
	}
	
	public static Byte getByte(HttpServletRequest request, String paramName){
		Byte result = null;
		if(request.getParameter(paramName) != null){
			try{
				result = Byte.parseByte(request.getParameter(paramName));
			}
			catch(Exception ex){;}
		}
		return result;
	}
	
	public static Date getDate(HttpServletRequest request, String paramName){
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Date result = null;
		if(request.getParameter(paramName) != null){
			try{
				result = sdf.parse(request.getParameter(paramName));
			}
			catch(Exception ex){;}
		}
		return result;
	}
	
	public static Boolean getBoolean(HttpServletRequest request, String paramName){
		return getBoolean(request, paramName,null);
	}
	
	public static Boolean getBoolean(HttpServletRequest request, String paramName, Boolean defaultValue){
		if(request.getParameter(paramName) != null){
			try{
				defaultValue = Boolean.parseBoolean(request.getParameter(paramName));
			}
			catch(Exception ex){;}
		}
		return defaultValue;
	}
	
	public static Integer[] getIntegerArray(HttpServletRequest request, String paramName){
		Integer[] result = null;
		String[] idArray = request.getParameterValues(paramName);
		if(idArray!=null){
			if(idArray != null && idArray.length > 0){
				result = new Integer[idArray.length];
				for(int i=0;i<idArray.length;i++){
					try{
						result[i] = Integer.parseInt(idArray[i]);
					}
					catch(Exception ex){
						;
					}
				}
			}
			
		}
		return result;
	}

	public static HashMap getParameter(String url){
		HashMap map = new HashMap();
		int flag = url.indexOf("?");
		String[] paras = null;
		String[] p = null;
		if(flag > 0){
			url = url.substring(flag+1);
			paras = url.split("&");
			for(String s :paras ){
				p = s.split("=");
				if(p.length > 1){
					map.put(p[0],p[1]);
				}
				else{
					map.put(p[0],null);
				}
			}
		}
		return map;
	}
}
