package cn.gateway.util;

import java.util.Properties;

import cn.gateway.logger.Logger;
import cn.gateway.logger.LoggerFactory;

public class ProfileUtil {
	private static Logger logger = LoggerFactory.getLogger(ProfileUtil.class);
	private static String ZookeeperServers = null;
	private static String authinfoPassword = null;
	private static String authinfoScheme = null;
	private static Boolean enableKafkalog = Boolean.FALSE;
	private static String servicePath = null;
	private static String serverId = null;

	static {
		Properties p = new Properties();
		try {
			p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("scheduling.pro"));
		} catch (Exception e) {
			logger.warn("获取配置文件错误", e);
		}
		ZookeeperServers = p.getProperty("zookeeper.servers", "");
		authinfoScheme = p.getProperty("zookeeper.authinfo.scheme", "digest");
		serverId = p.getProperty("servers.id", "");
		authinfoPassword = p.getProperty("zookeeper.authinfo.password");
		try {
			enableKafkalog = Boolean.parseBoolean(p.getProperty("kafka.log", "false"));
		} catch (Exception e) {
		}
		servicePath = p.getProperty("zookeeper.serverPath", "");
	}

	public static Logger getLogger() {
		return logger;
	}

	public static String getZookeeperServers() {
		return ZookeeperServers;
	}

	public static String getAuthinfoPassword() {
		return authinfoPassword;
	}

	public static String getAuthinfoScheme() {
		return authinfoScheme;
	}

	public static Boolean getEnableKafkalog() {
		return enableKafkalog;
	}

	public static String getServicePath() {
		return servicePath;
	}

	public static String getServerId() {
		return serverId;
	}

}