package cn.gateway.transmit;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import cn.gateway.logger.Logger;
import cn.gateway.logger.LoggerFactory;

public class HttpProtocolTransmit {
	private static PoolingHttpClientConnectionManager clientConnectionManager = null;
	private static CloseableHttpClient httpClient = null;
	private static RequestConfig config = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
	private final static ReentrantLock lock = new ReentrantLock();

	/**
	 * 创建httpclient连接池并初始化
	 */
	static {
		clientConnectionManager = new PoolingHttpClientConnectionManager();
		clientConnectionManager.setMaxTotal(1000);
		clientConnectionManager.setDefaultMaxPerRoute(25);
	}

	public static CloseableHttpClient getHttpClient() {
		if (httpClient == null) {
			List<Header> list = new ArrayList<Header>();
			list.add(new BasicHeader("x_gateway_trace_id", UUID.randomUUID().toString()));
			list.add(new BasicHeader("x_gateway_trace_index", "0"));
			list.add(new BasicHeader("x_gateway_trace_check", ""));
			httpClient = HttpClients.custom().setConnectionManager(clientConnectionManager).setDefaultHeaders(list)
					.setDefaultRequestConfig(config).build();
		}
		return httpClient;
	}

	/**
	 * get请求
	 * 
	 * @param url
	 * @param headers
	 * @return
	 */
	public static HttpEntity httpGet(String url, Map<String, Object> headers) {
		CloseableHttpClient httpClient = getHttpClient();
		HttpRequest httpGet = new HttpGet(url);
		if (headers != null && !headers.isEmpty()) {
		//	httpGet = setHeaders(headers, httpGet);
		}
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute((HttpGet) httpGet);
			HttpEntity entity = response.getEntity();
			return entity;
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	private static Logger log = LoggerFactory.getLogger(HttpProtocolTransmit.class);
	private static int defaultReadTimeout;
	private static int defaultConnectionTimeout;
	private static String defaultProtocol = "http";
	private static String defaultCharset = "utf-8";

	public static CloseableHttpClient getPool(String url) {
		return getPool(defaultProtocol, url);
	}

	public static CloseableHttpClient getPool(String protocol, String url) {
		CloseableHttpClient httpCilent = HttpClients.createDefault();
		return httpCilent;
	}

	public static String post(String url, Map<String, Object> params, String charsetName) {
		Charset charset;
		try {
			charset = Charset.forName(charsetName);
		} catch (Exception e) {
			charset = Charset.forName(defaultCharset);
		}
		return null;
	}

	public static String get(String url) {
		String result = null;
		CloseableHttpClient client = getPool(url);
		try {
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = client.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
				log.debug(url + result);
			} else if (httpResponse.getStatusLine().getStatusCode() == 400) {
				log.debug(url + result);
			} else if (httpResponse.getStatusLine().getStatusCode() == 500) {
				log.debug(url + result);
			}
		} catch (Exception e) {
			log.warn("操作错误", e);
		} finally {
			try {
				client.close();// 释放资源
			} catch (Exception e) {
				log.warn("释放连接错误", e);
			}
		}
		return result;
	}

	public static String put(String url) {
		return null;
	}

	public static String delete(String url) {
		return null;
	}

	public static String post(String url, Map<String, Object> params) {
		return post(url, params, defaultCharset);
	}

	public static String post(String url, String charset) {
		return post(url, null, charset);
	}

	public static String post(String url) {
		return post(url, null, defaultCharset);
	}

	/**
	 * 不支持
	 * 
	 * @param url
	 * @return
	 */
	public static String options(String url) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 不支持
	 * 
	 * @param url
	 * @return
	 */
	public static String head(String url) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 不支持
	 * 
	 * @param url
	 * @return
	 */
	public static String connect(String url) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 不支持
	 * 
	 * @param url
	 * @return
	 */
	public static String trace(String url) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 不支持
	 * 
	 * @param url
	 * @return
	 */
	public static String patch(String url) {
		throw new UnsupportedOperationException();
	}
}