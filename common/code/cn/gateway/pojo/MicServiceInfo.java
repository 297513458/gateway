package cn.gateway.pojo;

public class MicServiceInfo {
	private String clazz;
	private String method;
	private String beanId;
	private String allowHost;
	private String url;
	private String clients;
	private String path;
	private Integer maxClient;
	private String status;

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getAllowHost() {
		return allowHost;
	}

	public void setAllowHost(String allowHost) {
		this.allowHost = allowHost;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getClients() {
		return clients;
	}

	public void setClients(String clients) {
		this.clients = clients;
	}

	public Integer getMaxClient() {
		return maxClient;
	}

	public void setMaxClient(Integer maxClient) {
		this.maxClient = maxClient;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getBeanId() {
		return beanId;
	}

	public void setBeanId(String beanid) {
		this.beanId = beanid;
	}
}
