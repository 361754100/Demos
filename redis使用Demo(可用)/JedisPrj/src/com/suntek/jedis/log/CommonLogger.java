package com.suntek.jedis.log;

import org.apache.log4j.Logger;

public class CommonLogger {
	private static final Logger logger = org.apache.log4j.Logger.getLogger(CommonLogger.class);
	
	public static void info(String message){
		logger.info(message);
	}
	
	public static void error(String message, Exception e){
		logger.error(message, e);
	}
	
	public static void error(String message){
		logger.error(message);
	}
	
	public static void debug(String message){
		logger.debug(message);
	}
}
