package cn.kykys.index.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LogUtil {
	private static final transient Logger  logger = LogManager.getLogger(LogUtil.class);
	
	public static void error(Object message){
		logger.error(message);
	}
	public static void error(Object message, Throwable e){
		logger.error(message,e);
	}
	
	public static void debug(Object message){
		logger.debug(message);
	}
	
	public static void debug(Object message, Throwable e){
		logger.debug(message, e);
	}
	
	public static void info(Object message){
		logger.info(message);
	}
	
	public static void info(Object message, Throwable e){
		logger.info(message, e);
	}
	
	public static void fatal(Object message){
		logger.fatal(message);
	}
	
	public static void fatal(Object message, Throwable e){
		logger.fatal(message, e);
	}
	
	public static void warn(Object message){
		logger.warn(message);
	}
	
	public static void warn(Object message, Throwable e){
		logger.warn(message, e);
	}
}
