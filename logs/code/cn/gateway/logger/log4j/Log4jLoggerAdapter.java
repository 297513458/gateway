package cn.gateway.logger.log4j;

import org.apache.log4j.LogManager;

import cn.gateway.logger.Logger;
import cn.gateway.logger.LoggerAdapter;

public class Log4jLoggerAdapter implements LoggerAdapter {
	
	public Logger getLogger(Class<?> key) {
		return new Log4jLogger(LogManager.getLogger(key));
	}

	public Logger getLogger(String key) {
		return new Log4jLogger(LogManager.getLogger(key));
	}

}