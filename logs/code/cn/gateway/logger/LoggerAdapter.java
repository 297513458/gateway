package cn.gateway.logger;

public interface LoggerAdapter {
	Logger getLogger(Class<?> key);

	Logger getLogger(String key);
}