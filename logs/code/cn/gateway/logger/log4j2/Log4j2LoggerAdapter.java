package cn.gateway.logger.log4j2;

import org.apache.logging.log4j.LogManager;

import cn.gateway.logger.Logger;
import cn.gateway.logger.LoggerAdapter;

public class Log4j2LoggerAdapter implements LoggerAdapter {

	public Log4j2LoggerAdapter() {
	}

	public Logger getLogger(Class<?> key) {
		return new Log4j2Logger(LogManager.getLogger(key));
	}

	public Logger getLogger(String key) {
		return new Log4j2Logger(LogManager.getLogger(key));
	}

}