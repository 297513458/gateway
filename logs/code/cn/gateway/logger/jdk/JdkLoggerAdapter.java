package cn.gateway.logger.jdk;

import cn.gateway.logger.Logger;
import cn.gateway.logger.LoggerAdapter;

public class JdkLoggerAdapter implements LoggerAdapter {

	public Logger getLogger(Class<?> key) {
		return new JdkLogger(java.util.logging.Logger.getLogger(key == null ? "" : key.getName()));
	}

	public Logger getLogger(String key) {
		return new JdkLogger(java.util.logging.Logger.getLogger(key));
	}
}