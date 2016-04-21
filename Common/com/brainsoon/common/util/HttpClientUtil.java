package com.brainsoon.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

/**
 * http请求工具类
 * 
 * @author 
 *
 */
public class HttpClientUtil {
	private static Logger logger = Logger.getLogger(HttpClientUtil.class);
	private String encoding = "UTF-8";
	private String connectionUrl;
	private String[] params = null;

	public HttpClientUtil() {
	}

	/**
	 * 初始化
	 * 
	 * @param connectionUrl 发送请求的链接带参数
	 * @param encoding 请求编码默认UTF-8
	 */
	public HttpClientUtil(String connectionUrl, String encoding) {
		this.connectionUrl = connectionUrl;
		this.encoding = encoding;
		initParams();
	}

	public void initParams() {
		if (!StringUtils.isBlank(connectionUrl)) {
			String paramStr = StringUtils.substring(connectionUrl, StringUtils.indexOf(connectionUrl, "?") + 1);
			this.connectionUrl = StringUtils.substring(connectionUrl, 0, StringUtils.indexOf(connectionUrl, "?"));
			params = paramStr.split("&");
		}
	}

	/**
	 * 发送请求,可以指定url
	 * 
	 * @param url
	 * @return
	 */
	public String sendRequest(String url) {
		if (StringUtils.isBlank(connectionUrl)) {
			logger.error("调用失败：连接地址尚未初始化！");
			return "-1";
		}
		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(180 * 1000).setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000).setStaleConnectionCheckEnabled(true).build();

		CloseableHttpClient httpClient = HttpClients.custom()
				.setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
				.setDefaultRequestConfig(defaultRequestConfig).build();

		HttpPost postMethod = new HttpPost(url);

		// 创建参数队列
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair(HTTP.CONTENT_ENCODING, encoding));
		if (null != params) {
			for (int i = 0; i < params.length; i++) {
				String paramsDes = params[i];
				String[] pairs = StringUtils.split(paramsDes, "=", 2);
				String name = pairs[0];
				String value = "";
				if (pairs.length >= 2) {
					value = pairs[1];
				}
				formparams.add(new BasicNameValuePair(name, value));
			}
		}

		try {
			postMethod.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));
			HttpResponse response = httpClient.execute(postMethod);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println(" Method failed:  " + response.getStatusLine());
			} else {
				HttpEntity entity = response.getEntity();
				InputStream in = entity.getContent();
				BufferedReader breader = new BufferedReader(new InputStreamReader(in, encoding));
				return breader.readLine();
			}
		} catch (Exception e) {
			String errInfo = "接口调用失败！请确认接口服务是否正常。";
			logger.error(errInfo + "[" + connectionUrl + "]:" + e.getMessage());
			return "-1";
		}
		return "-1";
	}

	public String executeGet(String url) {
		// 替换参数中的|
		url = StringUtils.replace(url, "|", "%7C");

		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(180 * 1000).setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000).setStaleConnectionCheckEnabled(true).build();

		CloseableHttpClient httpClient = HttpClients.custom()
				.setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
				.setDefaultRequestConfig(defaultRequestConfig).build();

		HttpGet getMethod = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(getMethod);
			int statusCode = response.getStatusLine().getStatusCode();
			/* 4 判断访问的状态码 */
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + response.getStatusLine());
			}

			/* 5 处理 HTTP 响应内容 */
			// HTTP响应头部信息，这里简单打印
			Header[] headers = response.getAllHeaders();
			for (Header h : headers)
				System.out.println(h.getName() + "------------ " + h.getValue());

			InputStream in = response.getEntity().getContent();
			BufferedReader breader = new BufferedReader(new InputStreamReader(in, encoding));
			return breader.readLine();
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return "-1";
	}

	@SuppressWarnings("deprecation")
	public String postJson(String url, String json) {
		if (StringUtils.isBlank(url)) {
			logger.error("调用失败：连接地址尚未初始化！");
			return "-1";
		}
		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(180 * 1000).setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000).setStaleConnectionCheckEnabled(true).build();

		CloseableHttpClient httpClient = HttpClients.custom()
				.setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
				.setDefaultRequestConfig(defaultRequestConfig).build();

		HttpPost postMethod = new HttpPost(url);

		StringEntity entity = new StringEntity(json, "utf-8");
		entity.setContentEncoding(encoding);
		entity.setContentType("application/json;charset=UTF-8");
		postMethod.setEntity(entity);
		try {
			// 发送请求
			HttpResponse response = httpClient.execute(postMethod);
			int statusCode = response.getStatusLine().getStatusCode();
			logger.info("post请求方式 postJson 请求返回状态吗：" + statusCode);

			java.io.InputStream in = response.getEntity().getContent();
			java.io.BufferedReader breader = new BufferedReader(new InputStreamReader(in, encoding));
			String result = breader.readLine();
			logger.debug("breader.readLine()======================" + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			String errInfo = "接口调用失败！请确认接口服务是否正常。";
			logger.error(errInfo + "[" + url + "]:" + e.getMessage());
			return "-1";
		}
	}

	/**
	 * 发送请求，失败返回-1
	 * 
	 * @return
	 */
	public String sendRequest() {
		return sendRequest(connectionUrl);
	}

	public static void main(String[] args) {
		HttpClientUtil http = new HttpClientUtil();
		http.executeGet("http://baiduasdf.com");
	}
}
