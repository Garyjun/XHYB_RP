package com.brainsoon.common.util.fltx.webpage.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 *
 * @ClassName: HtmlPage
 * @Description:   本类用来获取网页代码，获取服务器返回各种信息，修补链接，保存网页功能。
 * @author: tanghui
 * @date:2015-4-14 上午11:39:20
 */
public class HtmlPage {

	//当网页抓取失败时，重新抓取的次数
	private int retryCount=3;
	//链接超时时间
	private int connectTimeout = 5000;
	//读取超时时间
	private int readTimeout = 20000;

	private static LinkedHashMap<String,String> map= new LinkedHashMap<String,String>();
	//请求网页的链接URL
	private URL url=null;
	//设置的代理
	private Proxy proxy=null;
	//请求网页的基链接
	private String baseLink=null;
	// 设置是否允许在服务器内自动跳转
	private boolean followRedirect=true;
	//请求网页的链接的HttpURLConnection对象
	private HttpURLConnection httpConn=null;

	//获取目标地址的网页，要设置的一些请求头参数名和其值。
	private Map<String,String> requestHeader=new HashMap<String, String>();
	//HTTP访问某个页面返回响应HTTP头
	private Map<String,String> responseHeader=new HashMap<String, String>();

	//网页代码
	private String htmlCode = "";
	//网页内容编码
	private String encoding = "gb2312";
	//用于字符串相加时使用。
	private StringBuffer buffer=new StringBuffer();

	private static Logger logger = LogFactory.getLogger(HtmlPage.class);


	/**
	 * 创建一个新的网页获取对象。
	 */
	public HtmlPage() {
		super();
	}

	/**
	 * 使用指定链接创建一个新的网页获取对象。
	 *
	 * @param link 指定的链接。
	 * @throws UnsupportedEncodingException 不支持的编码异常。
	 * @throws MalformedURLException 链接格式错误异常。
	 * @throws IOException 输入输出异常。
	 */
	public HtmlPage(String link) throws UnsupportedEncodingException,
			MalformedURLException, IOException {
		this(new URL(link), (String)null,(Proxy)null);
	}

	/**
	 * 使用指定链接、指定编码创建一个新的网页获取对象。
	 *
	 * @param link 指定的链接。
	 * @param encoding 指定的网页编码。
	 * @throws UnsupportedEncodingException 不支持的编码异常。
	 * @throws MalformedURLException 链接格式错误异常。
	 * @throws IOException 输入输出异常。
	 */
	public HtmlPage(String link, String encoding)
			throws UnsupportedEncodingException, MalformedURLException,IOException {
		this(new URL(link), encoding,(Proxy)null);
	}

	/**
	 * 使用指定链接、指定编码、指定代理服务器创建一个新的网页获取对象。
	 *
	 * @param link 指定的链接。
	 * @param encoding 指定的网页编码。
	 * @param proxy 指定代理服务器。
	 * @throws UnsupportedEncodingException 不支持的编码异常。
	 * @throws MalformedURLException 链接格式错误异常。
	 * @throws IOException 输入输出异常。
	 */
	public HtmlPage(String link, String encoding, Proxy proxy)
			throws UnsupportedEncodingException, MalformedURLException,IOException {
		this(new URL(link), encoding, proxy);
	}

	/**
	 * 使用指定链接对象创建一个新的网页获取对象。
	 *
	 * @param url 指定的链接对象。
	 * @throws UnsupportedEncodingException 不支持的编码异常。
	 * @throws IOException 输入输出异常。
	 */
	public HtmlPage(URL url) throws UnsupportedEncodingException, IOException {
		this(url, (String)null, (Proxy)null);
	}

	/**
	 * 使用指定链接对象、指定编码创建一个新的网页获取对象。
	 *
	 * @param url 指定的链接对象。
	 * @param encoding 指定的网页编码。
	 * @throws UnsupportedEncodingException 不支持的编码异常。
	 * @throws IOException 输入输出异常。
	 */
	public HtmlPage(URL url, String encoding)
			throws UnsupportedEncodingException, IOException {
		this(url, encoding, (Proxy)null);
	}

	/**
	 * 使用指定链接对象、指定编码、代理服务器创建一个新的网页获取对象。
	 *
	 * @param url 指定的链接对象。
	 * @param encoding 指定的网页编码。
	 * @param proxy 指定代理服务器。
	 * @throws UnsupportedEncodingException 不支持的编码异常。
	 * @throws IOException 输入输出异常。
	 */
	public HtmlPage(URL url, String encoding, Proxy proxy)
			throws UnsupportedEncodingException, IOException {

		this.setUrl(url);//设置网页链接URL

		this.setEncoding(encoding);//设置获取网页的编码

		this.setProxy(proxy);//设置代理

		this.httpGetMethod();// GET方式获取网页代码
	}

	/**
	 * 读取网页文件中代码。
	 * @param file 网页文件。
	 * @throws FileNotFoundException 找不到文件异常。
	 * @throws IOException 出入输出异常。
	 */
	public HtmlPage(File file) throws FileNotFoundException, IOException {
		String newLine = System.getProperty("line.separator");

		BufferedReader br = null;
		try {
			StringBuffer sb = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file)));

			String line = br.readLine();
			while (line != null) {
				sb.append(line + newLine);
				line = br.readLine();
			}

			this.htmlCode = sb.toString();

		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				throw e;
			}
		}
	}

	/**
	 * 提交指定POST数据，并且返回服务器返回响应头和响应体。
	 *
	 * @param paramMap 登录需要的用户名密码等键值对。
	 * @param postLink 指定接受POST数据的链接。
	 * @param encoding 指定网页编码。
	 */
	public HtmlPage(String postLink,String encoding,String postData) throws MalformedURLException,
		UnsupportedEncodingException, ProtocolException, IOException {

		this(new URL(postLink),encoding,postData,(Proxy)null);
	}

	/**
	 * 提交指定POST数据，并且返回服务器返回响应头和响应体。
	 *
	 * @param paramMap 登录需要的用户名密码等键值对。
	 * @param postLink 指定接受POST数据的链接。
	 * @param encoding 指定网页编码。
	 * @param proxy 指定代理服务器。
	 */
	public HtmlPage(String postLink,String encoding,String postData,Proxy proxy) throws MalformedURLException,
			UnsupportedEncodingException, ProtocolException, IOException {

		this(new URL(postLink),encoding,postData,(Proxy)null);
	}

	/**
	 * 提交指定POST数据，并且返回服务器返回响应头和响应体。
	 *
	 * @param paramMap 登录需要的用户名密码等键值对。
	 * @param postLink 指定接受POST数据的URL。
	 * @param encoding 指定网页编码。
	 */
	public HtmlPage(URL postUrl,String encoding,String postData)
		throws MalformedURLException,UnsupportedEncodingException, ProtocolException, IOException {
		this(postUrl,encoding,postData,(Proxy)null);
	}

	/**
	 * 提交指定POST数据，并且返回服务器返回响应头和响应体。
	 *
	 * @param paramMap 登录需要的用户名密码等键值对。
	 * @param postLink 指定接受POST数据的URL。
	 * @param encoding 指定网页编码。
	 * @param proxy 指定代理服务器。
	 */
	public HtmlPage(URL postUrl,String encoding,String postData,Proxy proxy)
		throws MalformedURLException,UnsupportedEncodingException, ProtocolException, IOException {

		this.setUrl(postUrl);//设置接受POST数据的URL

		this.setEncoding(encoding);//设置跳转后网页的编码

		this.setProxy(proxy);//设置代理服务器

		Map<String, String> reqHeaderMap=new HashMap<String, String>();
		//设置HTTP协议头，设置内容类型为经过编码的URL
		reqHeaderMap.put("Content-Type", "application/x-www-form-urlencoded");
		//模拟IE浏览提交登录请求
		reqHeaderMap.put("User-Agent","Mozilla/5.0 (compatible; MSIE 6.0; Windows NT)");

		//设置请求头参数
		this.setRequestHeader(reqHeaderMap);

		//获取POST方式提交数据后服务器返回的结果。
		this.httpPostMethod(postData);
	}

	/**
	 * 根据用户名密码等键值对登录到指定网页并且保存登录成功后的网页代码。
	 *
	 * @param requestHeaderMap 登录需要设置的HTTP请求协议头。
	 * @param postData 需要的用户名密码等键值对。
	 * @param postLink 指定的接受POST参数的地址。
	 * @param forwardLink 登录成功后的地址：跳转链接，需要时可以填写。
	 * @param encoding 指定网页的编码。
	 */
	public void httpPostMethod(String postData) throws MalformedURLException,
			UnsupportedEncodingException, ProtocolException, IOException {

		//获取网页是否成功
		boolean successed=false;
		//保存重试指定最多次数的获取失败的异常
		IOException exception=null;

		if(encoding==null){
			this.encoding = "gb2312";
		}

		for (int i = 0; i < this.retryCount ; i++) {
			if(successed){
				break;
			}
			try{

				//设置基链接。
				this.baseLink=this.url.toExternalForm();

				// 判断是否用代理
				if(proxy==null){
					this.httpConn = (HttpURLConnection) this.url.openConnection();
				}
				else{
					this.httpConn = (HttpURLConnection) this.url.openConnection(proxy);
				}

				//设置本次HTTP请求以POST(必须大写)方式提交
				this.httpConn.setRequestMethod("POST");

				//设置所有HTTP请求的页面不自动跳转，默认为自动
				HttpURLConnection.setFollowRedirects(false);
				//设置本次HTTP请求的页面不自动跳转，默认为自动
				this.httpConn.setInstanceFollowRedirects(false);

				//设置本次HTTP链接既有输出流也有输入流
				this.httpConn.setDoInput(true);
				this.httpConn.setDoOutput(true);
				//设置不用使用缓存
				this.httpConn.setUseCaches(false);

				//设置HTTP协议请求头
				this.initRequestParam();

				//简历HTTP连接
				this.httpConn.connect();

				//创建输出流，如果没有连接则隐含调用httpConn.connect();
				PrintStream sendPost = new PrintStream(httpConn.getOutputStream());
				//把登录参数写到输出流，也就是发送给登录服务器
				sendPost.print(postData != null ? postData : "");
//				sendPost.write(postData.getBytes());
				//关闭输出流
				sendPost.close();

				//得到HTTP响应头
				this.initResponseParam();

				//获取这次连接的cookie
				String cookie=this.getCookie();

				//获取跳转链接
				String location=this.httpConn.getHeaderField("Location");
				while(location!=null){
					// 循环获取服务器内部跳转地址，以便获取正确的cookie。例如 人人网 有2次服务器内部转向。
					if(!isLink(location)){
						location=repairLink(location);
						this.baseLink=location;//设置基链接
					}

					this.httpConn.disconnect();//关闭HTTP连接

					this.url=new URL(location);//创建新的链接

					if(proxy==null){
						this.httpConn = (HttpURLConnection) this.url.openConnection();
					}
					else{
						this.httpConn = (HttpURLConnection) this.url.openConnection(proxy);
					}

					//设置本次HTTP链接有输入流
					this.httpConn.setDoInput(true);
					//设置不用使用缓存
					this.httpConn.setUseCaches(false);
					//设置本次HTTP请求的页面不自动跳转
					this.httpConn.setInstanceFollowRedirects(false);

					//向HTTP协议头发送Cookie
					this.httpConn.setRequestProperty("Cookie", cookie);

					//设置登录请求HTTP协议头
					this.initRequestParam();

					this.httpConn.connect();//建立HTTP连接

					//获得跳转页面的链接
					location=this.httpConn.getHeaderField("Location");
					if(location!=null){
						//保存跳转前获取的有效HTTP响应头
						this.initResponseParam();

						//保存跳转前获取的有效HTTP响应头中的cookie
						cookie=this.getCookie();
					}
				}

				//得到登录成功之后的网页代码
				this.getInputString(this.httpConn.getInputStream(), this.encoding);

				successed=true;//获取网页成功，则无需多次重试。
			}
			catch(MalformedURLException e){
				throw e;
			}
			catch(UnsupportedEncodingException e){
				throw e;
			}
			catch(ProtocolException e){
				throw e;
			}
			catch(IOException e){
				exception=e;
			}
		}

		//当发生IO异常时，重新获取指定次数时不成功则抛出该IO异常
		if(!successed){
			throw exception;//抛出IO异常。
		}
	}

	/**
	 * 获取登录网页需要参数组成的字符串。
	 *
	 * @param paramMap 包含登录参数的键值对。
	 * @param encoding 网页编码。
	 * @return 登录网页需要参数组成的字符串。
	 */
	public String getPostData(Map<String,String> paramMap,String encoding){
		StringBuffer sb=new StringBuffer("");
		try {

			if(paramMap!=null && encoding!=null){
				Iterator<String>iter=paramMap.keySet().iterator();
				for(int i=0;iter.hasNext();i++){
					String key=iter.next();
					String value=paramMap.get(key);
					if(i==0){
						sb.append(key+"="+URLEncoder.encode(value, encoding));
					}
					else{
						sb.append("&"+key+"="+URLEncoder.encode(value, encoding));
					}
				}
			}

		} catch (Exception e) {
			logger.log(Level.WARNING,"对URL参数进行编码时异常",e);
		}

		return sb.toString();
	}

	/**
	 * 通过指定链接对象、指定编码、代理服务器获取一张网页的内容。
	 *
	 * 注：当使用有参构造方法时，将自动调用本方法，所以要避免重复调用。
	 *
	 * @throws UnsupportedEncodingException 不支持的编码异常。
	 * @throws MalformedURLException 链接格式错误异常。
	 * @throws IOException 输入输出异常。
	 */
	public void httpGetMethod() throws UnsupportedEncodingException,
			ProtocolException, IOException {

		//获取网页是否成功
		boolean successed = false;
		//保存重试指定最多次数的获取失败的异常
		IOException exception = null;

		if(encoding==null){
			// 已经关闭了HTTP连接
			this.encoding = getContentEncoding(url, proxy);
		}

		for (int i = 0; i < retryCount; i++) {
			if (successed) {
				break;
			}
			try {
				//设置获取网页HTTP对象和编码
				this.getInputString(this.getInputStream(), this.encoding);

				successed = true;//获取网页成功。

			} catch (UnsupportedEncodingException e) {
				// 当发生编码异常时，则直接抛出异常，不再重新读取
				throw e;
			} catch (IOException e) {
				// 当发生IO异常时，则尝试重新最多指定次数的读取
				exception = e;
				// 输出错误消息
				logger.log(Level.WARNING, "第 " + (i + 1) + " 请求失败,尝试下次请求：" + httpConn.getURL().toString());
			}
		}

		if (!successed) {
			// 当发生IO异常时，重新获取指定次数时不成功则抛出IO异常
			throw exception;
		}
	}

	/**
	 * 获取指定输入流中指定编码格式的字符串。
	 *
	 * @param io 读入字符流。
	 * @param encoding 指定字符编码。
	 * @throws IOException 输入输出异常。
	 */
	public void getInputString(InputStream io, String encoding)
			throws UnsupportedEncodingException, IOException {
		BufferedReader br =new BufferedReader(new InputStreamReader(io,encoding));
		//获取系统的换行符
		String newLine=System.getProperty("line.separator");

		String line = br.readLine();
		while (line != null) {
			buffer.append(line+newLine);
			line = br.readLine();
		}

		if(buffer.length()>0){
			htmlCode = buffer.toString();
			//及时清空buffer
			buffer.delete(0, buffer.length());
		}

		// 获取网页的基链接，用于补全该网页下的相对链接。
		String baseLink=this.fetchBaseLink();

		// 获取网页中的基链接
		if(baseLink!=null && isLink(baseLink)){
			this.baseLink=baseLink;
		}
	}

	/**
	 * 获取指定链接对象的输入流。本方法包含建立HTTP连接，设置HTTP请求协议头，返回HTTP响应协议头过程。
	 * @return 指定链接对象的输入流。
	 * @throws IOException 输入输出异常。
	 */
	public InputStream getInputStream() throws IOException{
		// 设置是否发生跳转，ture 自动跳转，false 不自动跳转
		HttpURLConnection.setFollowRedirects(followRedirect);

		// 初始化HTTP连接
		this.initHttpParam();

		//获取输入流
		InputStream inputStream = null;

		// 判断HTTP内容是否使用压缩格式压缩内容
		String contentEncoding = this.httpConn.getContentEncoding();
        if ((null != contentEncoding)
        		// 使用gzip压缩
            && (-1 != contentEncoding.indexOf ("gzip"))){
        	inputStream = new GZIPInputStream (
        			this.httpConn.getInputStream ());
        }
        else if ((null != contentEncoding)
        		// 使用deflate压缩
            && (-1 != contentEncoding.indexOf ("deflate"))){
        	inputStream = new InflaterInputStream (
        			this.httpConn.getInputStream (), new Inflater (true));
        }
        else{
        	inputStream = this.httpConn.getInputStream();
        }

        // 设置回为自动跳转
		HttpURLConnection.setFollowRedirects(true);

		return inputStream;
	}

	/**
	 * 建立HTTP连接，初始化HTTP协议请求和响应头。
	 *
	 * 注：当调用getHtmlPage()和getInputStream()方法时，将自动调用本方法，避免重复调用。
	 */
	public void initHttpParam(){
		try {
			if (this.proxy == null) {
				this.httpConn = ((HttpURLConnection) this.url.openConnection());
			} else {
				this.httpConn = ((HttpURLConnection) this.url.openConnection(this.proxy));
			}

			//设置请求头
			this.initRequestParam();

			//建立连接
			this.httpConn.connect();

			//返回请求头
			this.initResponseParam();

		} catch (Exception e) {
			logger.log(Level.WARNING,"初始HTTP协议请求和响应头异常",e);
		}
	}

	private void initRequestParam() {
		//设置连接超时
		this.httpConn.setConnectTimeout(this.connectTimeout);
		//设置读取超时
		this.httpConn.setReadTimeout(this.readTimeout);

		//设置其他请求参数
		if (this.requestHeader != null && this.httpConn != null ) {
			Iterator<String> iter = requestHeader.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				String value = requestHeader.get(key);
				this.httpConn.setRequestProperty(key, value);
			}
		}
	}

	private void initResponseParam(){
		if (this.responseHeader != null && this.httpConn != null ) {

			// 清空上次的保存的信息。
			this.responseHeader.clear();

			String field = null;// 键名
			for (int i = 1; (field = httpConn.getHeaderFieldKey(i)) != null; i++) {
				String value = httpConn.getHeaderField(i);
				if ("set-cookie".equalsIgnoreCase(field)) {
//					//防止每个cookie的值紧接在一起，不能识别器键 和 值。例如 开心网 的cookie
					buffer.append(value+"; ");
				} else {
					this.responseHeader.put(field, value);
				}
			}

			if (buffer.length() > 0) {
				this.responseHeader.put("Set-Cookie", buffer.toString());
				// 及时清空buffer
				buffer.delete(0, buffer.length());
			}
		}
	}


	/**
	 * 获取网页的基础链接，为链接修复做准备。
	 *
	 * @throws MalformedURLException 非法格式的链接。
	 */
	public String fetchBaseLink() throws MalformedURLException{
		String tag=null,attrName="href",baseLink=null;

		int bIndex=Utilities.ignoreCaseIndexOf(htmlCode,"<base");
		if(bIndex>=0){
			int eIndex=htmlCode.indexOf(">",bIndex);
			if(eIndex>0){
				tag=htmlCode.substring(bIndex,eIndex+1);
			}
		}

		// 当标签为空或不存在时，属性名为空时，返回空
		if (tag == null || tag.trim().equals("") || tag.indexOf("=") < 0) {
			return baseLink;
		}

		// 属性值结尾位置
		int attrValueEndIndex = -1;

		// 标签的开始位置，防止标签前有非标签内字符
		int index = tag.indexOf("<", -1);

		while (index < tag.length()) {
			int equalIndex = tag.indexOf("=", index);
			if (equalIndex < 0) {
				break;
			}

			// “=”右边的位置
			int equalLeftIndex = equalIndex - 1;

			// 跳过“=”右边的空格、制表符、全角空格；“=”右边位置要大于上一个属性值的结尾位置
			for (; (tag.charAt(equalLeftIndex) == ' '
					|| tag.charAt(equalLeftIndex) == '\t'
					|| tag.charAt(equalLeftIndex) == '　')
					&& equalLeftIndex > attrValueEndIndex; equalLeftIndex--);

			// 保存需要比较的位置
			int compareIndex[] = new int[5];

			// 获取“=”左边的“"”的值
			compareIndex[0] = tag.lastIndexOf("\"", equalLeftIndex);
			if (compareIndex[0] > 0 && compareIndex[0] > attrValueEndIndex) {
				// 判断是否为一个转义的“"”
				if (tag.charAt(compareIndex[0] - 1) == '\\') {
					compareIndex[0] = -1;
				}
			}
			// 获取“=”左边的“'”的值
			compareIndex[1] = tag.lastIndexOf("\'", equalLeftIndex);
			if (compareIndex[1] > 0 && compareIndex[1] > attrValueEndIndex) {
				// 判断是否为一个转义的“'”
				if (tag.charAt(compareIndex[1] - 1) == '\\') {
					compareIndex[1] = -1;
				}
			}
			// 获取“=”左边的属性名前半角空格的值
			compareIndex[2] = tag.lastIndexOf(" ", equalLeftIndex);
			// 获取“=”左边的属性名前制表符的值
			compareIndex[3] = tag.lastIndexOf("\t", equalLeftIndex);
			// 获取“=”左边的属性名前全角空格的值
			compareIndex[4] = tag.lastIndexOf("　", equalLeftIndex);

			// 对这些位置进行升序排序
			Arrays.sort(compareIndex);


			// 判断截取的字符串是否为指定的属性名
			boolean equalsAttrName = false;

			// 取最大的位置，作为属性名的开始位置
			int attrNameBeginIndex = compareIndex[4] + 1;
			if (attrNameBeginIndex > 0 && attrNameBeginIndex >= attrValueEndIndex) {
				// String attrName=tag.substring(attrNameBeginIndex,equalIndex);
				// 对比属性名是否相同
				equalsAttrName = attrName.equalsIgnoreCase(tag.substring(
						attrNameBeginIndex, equalIndex).trim());
			} else {

				break;
			}

			/////////////////////////////////////////////

			// 开始计算“=”的右边，即计算属性值的结束位置
			int equalRightIndex = equalIndex + 1;
			// 跳过“=”右边的半角空格、制表符、全角空格
			for (; (tag.charAt(equalRightIndex) == ' '
					|| tag.charAt(equalRightIndex) == '\t'
					|| tag.charAt(equalLeftIndex) == '　')
					&& equalRightIndex < tag.length() - 1; equalRightIndex++);

			// 获取“=”右边属性值的开始字符
			char nextChar = tag.charAt(equalRightIndex++);
			// 判断为“"”时，属性值使用双引号扩住
			if (nextChar == '"') {
				for (; equalRightIndex < tag.length() - 1; equalRightIndex++) {
					if (tag.charAt(equalRightIndex) == '"') {
						// 判断是否为一个转义的“"”
						if (tag.charAt(equalRightIndex - 1) != '\\') {
							break;
						}
					}
				}
			}
			// 判断为“'”时,属性值使用单引号扩住
			else if (nextChar == '\'') {
				for (; equalRightIndex < tag.length() - 1; equalRightIndex++) {
					if (tag.charAt(equalRightIndex) == '\'') {
						// 判断是否为一个转义的“'”
						if (tag.charAt(equalRightIndex - 1) != '\\') {
							break;
						}
					}
				}
			}
			// 判断为空字符，属性值结尾使用空格分开
			else {
				for (; (tag.charAt(equalRightIndex) != ' '
							&& tag.charAt(equalRightIndex) != '\t'
							&& tag.charAt(equalLeftIndex) != '　'
							&& tag.charAt(equalLeftIndex) != '>')
						&& equalRightIndex < tag.length() - 1; equalRightIndex++);
			}

			// 保存这次查找的“=”右边属性值结尾位置后一位
			index = equalRightIndex + 1;
			// 保存上一个属性值的结束位置
			attrValueEndIndex = equalRightIndex;

			// 判断本次查找的属性名是否为指定的属性名
			if (equalsAttrName==true) {
				// 截取属性值
				String attrValue = tag.substring(equalIndex + 1,attrValueEndIndex + 1);
				if (attrValue != null) {

					// 去除属性值前后半角空格
					attrValue = attrValue.trim();

					// 判断属性值是否被半角单、双引号括住
					if ((attrValue.endsWith("'") && attrValue.startsWith("'"))
							|| (attrValue.endsWith("\"") && attrValue.startsWith("\""))) {

						attrValue = attrValue.substring(1,attrValue.length() - 1);
					}
					// 判断属性值后一个字符是标签的“>”
					else if (attrValue.endsWith(">")) {

						attrValue = attrValue.substring(0,attrValue.length() - 1);
					}

					if(!attrValue.trim().equals("")){

						//保证基本链接是个正确的链接
						baseLink=attrValue;
					}
				}
			}
		}

		return baseLink;
	}

	/**
	 * 设置HTTP请求协议头信息。
	 * @param requestHeader HTTP请求协议头信息。
	 */
	public void setRequestHeader(Map<String, String> requestHeader) {
		this.requestHeader = requestHeader;
	}

	/**
	 * 得到HTTP请求协议头信息。
	 * @return 当次HTTP请求协议头信息。
	 */
	public Map<String, String> getRequestHeader() {
		return requestHeader;
	}

	/**
	 * 设置服务器返回的HTTP协议头信息。
	 * @param httpConn 服务器返回的HTTP协议头信息。
	 */
	public void setResponseHeader(Map<String, String> responseHeader){
		// 得到HTTP返回协议头名称和值的集合
		this.responseHeader = responseHeader;
	}

	/**
	 * 得到服务器返回的HTTP协议头信息。
	 * @return 服务器返回的HTTP协议头信息。
	 */
	public Map<String, String> getResponseHeader(){
		return responseHeader;
	}
	/**
	 * 获取指定网页链接对象的编码。
	 *
	 * @param url 指定网页链接对象。
	 * @return 指定网页链接对象。
	 */
	public String getContentEncoding(URL url,Proxy proxy){
		boolean successed=false;
		BufferedReader reader =null;
		HttpURLConnection httpConn=null;
		//获取最后一个“meta”标签之前的所有代码

		for (int i = 0; i < retryCount; i++) {

			if(successed){
				break;
			}
			try {

				if(proxy==null){
					httpConn=(HttpURLConnection)url.openConnection();
				}
				else{
					httpConn=(HttpURLConnection)url.openConnection(proxy);
				}

				String newLine=System.getProperty("line.separator");

				reader =new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
				String line = reader.readLine();
				while (line != null) {
					//转换成小写，方便比较
					line = line.toLowerCase();

					//保存获取网页代码
					buffer.append(line+newLine);

					if (line.contains("meta")) {
						if (line.contains("content")) {
							if (line.contains("charset")) {
								break;
							}
						}
					}
					line = reader.readLine();
				}
				successed = true;
			} catch (IOException e) {
				//发生异常时清空上次保存的部分数据
				buffer.delete(0, buffer.length());
				logger.log(Level.WARNING,"第 "+(i+1)+" 请求失败,尝试下次请求："+url.toString());
			}
			finally{
				try {
					if(reader!=null){
						reader.close();
					}
				} catch (IOException e) {
					//发生异常时清空上次保存的部分数据
					buffer.delete(0, buffer.length());
				}
			}
		}

		//获取“meta”标签中的代码
		int index1=0,index2=0;

		String code = null;

		if (buffer.length() > 0) {
			code = buffer.toString().trim();
			//及时清空buffer
			buffer.delete(0, buffer.length());
		}

		//提取编码的正则表达式
		String regex="charset {0,4}= {0,4}([0-9a-zA-Z-]{3,10}) {0,4}\"? {0,4}/? {0,4}>";
		while(true && code!=null){
			index1=code.indexOf("<meta",index2);
			if(index1<0){
				break;
			}

			index2=code.indexOf(">",index1+5);
			if(index2<0){
				break;
			}

			String codeTemp=code.substring(index1,index2+1);
			if(codeTemp.indexOf("charset")>0){
				Matcher m=Pattern.compile(regex).matcher(codeTemp);
				if(m.find()){
					return m.group(1);
				}
			}
		}

		//获取“Content-Type”中URL引用的资源的内容编码
		String ct=httpConn.getContentType();
		if(ct==null){
			//获取“Content-Encoding”中URL引用的资源的内容编码
			return httpConn.getContentEncoding();
		}
		else{
			int index=ct.lastIndexOf("=");
			if(index>0){
				return ct.substring(index+1).trim();
			}
		}

		if(httpConn!=null){//关闭HTTP连接
			httpConn.disconnect();
		}

		//返回默认值编码
		return "gb2312";
	}

	/**
	 * 获得COOKIE的值。
	 * @return 返回COOKIE的值。
	 */
	public String getCookie() {
		return this.responseHeader != null ? this.responseHeader.get("Set-Cookie") : null;
	}

	/**
	 * 设置单个链接获取失败时重复获取的次数
	 */
	public int getRetryCount() {
		return retryCount;
	}

	/**
	 * 返回单个链接获取失败时重复获取的次数
	 */
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	/**
	 * 获取连接超时，默认是5秒的连接超时。
	 *
	 * @return 返回链接超时的时间，单位为毫秒。
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * 设置连接超时。
	 *
	 * @param connectOutTime 连接超时时间，单位为毫秒。
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * 获取读取超时的时间,默认是20秒的读取超时。
	 * @return 读取超时，单位为毫秒。
	 */
	public int getReadTimeout() {
		return readTimeout;
	}

	/**
	 * 设置读取超时时间。
	 * @param readOutTime 读取超时的时间。
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	/**
	 * 返回是否在服务器内部自动跳转。
	 * @return 是否在服务器内部自动跳转，是为true，否为false。
	 */
	public boolean isFollowRedirect() {
		return followRedirect;
	}

	/**
	 * 返回是否在服务器内部自动跳转。
	 * @param followRedirect 是否在服务器内部自动跳转，是为true，否为false。
	 */
	public void setFollowRedirect(boolean followRedirect) {
		this.followRedirect = followRedirect;
	}

	/**
	 * 设置网页编码。
	 * @param encoding 网页编码。
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * 返回网页编码。
	 * @return 网页编码。
	 */
	public String getEncoding() {
		return this.encoding;
	}

	/**
	 * 返回需采集的URL对象。
	 * @return 需采集的URL对象。
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * 设置需采集的URL对象。
	 * @param url 需采集的URL对象。
	 */
	public void setUrl(URL url) {
		this.url = url;
		// 设置父链接，用于补全本网页相对链接
		this.baseLink=url.toString();
	}

	/**
	 * 返回用于补全相对链接的基链接。
	 * @return 用于补全相对链接的基链接。
	 */
	public String getBaseLink() {
		return baseLink;
	}

	/**
	 * 设置用于补全相对链接的基链接。
	 * @param baseLink 用于补全相对链接的基链接。
	 */
	public void setBaseLink(String baseLink) {
		this.baseLink = baseLink;
	}

	/**
	 * 设置网页代码。
	 * @param htmlCode 网页代码。
	 */
	public void setHtmlCode(String htmlCode) {
		this.htmlCode = htmlCode;
	}

	/**
	 * 返回网页代码。
	 * @return 网页代码。
	 */
	public String getHtmlCode() {
		return htmlCode;
	}

	/**
	 * 获取网页父路径。
	 *
	 * @return 网页父路径。
	 */
	public String getFilePath(){
		if(this.url!=null){
			return this.url.getPath();
		}

		return null;
	}



	/**
	 * 获取指定链接中的文件路径。
	 *
	 * @param link 指定链接。
	 * @return 链接中的文件名。
	 */
	public static String getFilePath(String link){
		try {
			return new URL(link).getPath();
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING, "获取指定链接文件路径异常", e);
		}

		return null;
	}

	/**
	 * 获取链接中的文件名，文件名不包含链接中的查询字符串，如果链接的文件没有后缀，则使用默认的后缀名。
	 *
	 * @param defaultPostfix 默认后缀名。
	 * @return 链接中的文件名。
	 */
	public String getFileName(String defaultPostfix){
		String fileName=null;

		if(this.url!=null){
			fileName = getFileName(this.url.toString(), defaultPostfix);
		}

		return fileName;
	}

	/**
	 * 获取链接中的文件名，文件名不包含链接中的查询字符串，如果链接的文件没有后缀，则使用默认的后缀名。
	 *
	 * @param link 需要获取文件名的完整链接。
	 * @param defaultPostfix 默认的文件后缀。
	 * @return 存在则返回文件名，不存在则返回null。
	 */
	public static String getFileName(String link,String defaultPostfix){
		String fileName=null;//文件名

		int index=link.lastIndexOf("/");
		if(index>0){
			// eg:http://www.baidu.com/file/
			if(index+1==link.length()){

				index=link.lastIndexOf("/",index-1);
				if(index>0){
					// 根据指定的后缀得到文件名
					fileName=link.substring(index+1,link.length()-1)+"."+defaultPostfix;
				}
			}
			else{
				// eg:http://www.baidu.com/file/index.html
				String name=link.substring(index+1);

				int point=name.indexOf(".");
				if(point>0){
					// eg:http://www.baidu.com/file/index.html?ip=192.168.0.3

					// 这里拆成两部分：防止“?”后面也有“.”字符
					int ind=name.indexOf("?",point)>0?name.indexOf("?"):name.length();
					// 得到文件名
					fileName=name.substring(0,ind);
				}
				else{
					// eg:http://www.sse.com.cn/ETF180Bulletin
					// eg:http://www.sse.com.cn/ETF180Bulletin?reportName=etf180bulletin

					int ind=name.indexOf("?")>0?name.indexOf("?"):name.length();
					// 根据指定的后缀得到文件名
					fileName=name.substring(0, ind)+"."+defaultPostfix;
				}
			}
		}

		return fileName;
	}

	/**
	 * 获取链接中的文件名，文件名可能包含链接中的查询字符串，如果有则直接在文件名后面追加。
	 * 如果链接的文件没有后缀，则使用默认的后缀名。
	 *
	 * @param link 需要获取文件名的完整链接。
	 * @param defualtPostfix 默认的文件后缀。
	 * @return 存在则返回文件名，不存在则返回null。
	 */
	public static String getQueryFileName(String link,String defualtPostfix){
		String fileName=null;//文件名

		try {
			URL url=new URL(link);
			//得到文件路径
			String path=url.getPath();
			//得到查询字符串
			String query=url.getQuery();

			int index=path.lastIndexOf("/");
			if(index>=0){
				if(index+1==path.length()){

					index=path.lastIndexOf("/",index-1);
					if(index>0){
						fileName=query!=null?path.substring(index+1,path.length()-1)+query+"."+defualtPostfix:path.substring(index+1,path.length()-1)+"."+defualtPostfix;
					}
				}
				else{
					int point=path.indexOf(".");
					if(point>0){
						fileName=query!=null?path.substring(index+1,point)+query+path.substring(point):path.substring(index+1,point)+path.substring(point);
					}
					else{
						fileName=query!=null?path.substring(index+1,path.length())+query+"."+defualtPostfix:path.substring(index+1,path.length())+"."+defualtPostfix;
					}
				}
			}

		} catch (Exception e) {
			logger.log(Level.WARNING, "得到指定链接的文件名异常", e);
		}

		return fileName;
	}

	/**
	 * 获取指定链接文件的后缀名，如果没有，则返回默认的后缀名。
	 *
	 * @param link 指定的链接。
	 * @param defaultPostfix 默认的后缀名。
	 * @return 指定链接文件的后缀名，如果没有，则返回默认的后缀名。
	 */
	public String getPostfix(String link,String defaultPostfix){
		String postfix=null;//文件后缀名

		if(link!=null){
			int index=link.lastIndexOf("/");
			if(index>0){

				if(index+1==link.length()){
					postfix = defaultPostfix;
				}
				else{
					String name=link.substring(index+1);
					//得到后缀名的开始位置
					int point=name.indexOf(".");
					if(point>0){
						int ind=name.indexOf("?",point);
						if(ind<0){
							//得到文件的后缀名
							postfix = name.substring(point+1);
						}
						else{
							//得到文件的后缀名
							postfix = name.substring(point+1,ind);
						}
					}
					else{
						postfix = defaultPostfix;
					}
				}
			}
		}

		return postfix;
	}

	/**
	 * 保存本次请求的数据，并且以字符串形式保存到文本中。
	 *
	 * @param filePath 保存路径。
	 * @param fileName 保存的文件名。
	 * @return 是否保存成功，成功为true，失败为false。
	 */
	public boolean saveToFile(String filePath, String fileName) {
		BufferedWriter bw = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			// 判断filePath尾部是否含有“/”，并且创建一个文件对象
			File file = new File(filePath.endsWith("/") ? (filePath + fileName) : (filePath + "/" + fileName));

			bw = new BufferedWriter(new FileWriter(file));
			bw.write(htmlCode);// 写入文件
			bw.flush();

			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, "保存网页时发生异常", e);
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "关闭输出流时发生异常", e);
			}
		}

		return false;
	}

	/**
	 * 根据链接保存文件，如pdf，doc，xls等文件。本方法不能post方式下载。
	 *
	 * @param filePath 保存路径。
	 * @param fileName 保存的文件名。
	 * @param link 获取资源的链接。
	 * @return 是否保存成功，成功为true，失败为false。
	 */
	public boolean saveToFile(String filePath,String fileName,String link) {
		FileOutputStream fos = null;
		try {
			File file=new File(filePath);
			if(!file.exists()){
				file.mkdirs();
			}

			//判断filePath尾部是否含有“/”，并且创建一个文件对象
			file = new File(filePath.endsWith("/") ? (filePath + fileName) : (filePath + "/" + fileName));
			//创建文件输出流对象
			fos=new FileOutputStream(file);

			this.setUrl(new URL(link));
			this.initRequestParam();
			this.getResponseHeader();

			byte[] buf=new byte[1024];
			InputStream is=this.getInputStream();
			//开始保存信息
			for(int length=-1;(length=is.read(buf))!=-1;){
				fos.write(buf,0,length);
			}
			fos.flush();

			//当文件存在时也认为保存成功
			return true;

		} catch (Exception e) {
			logger.log(Level.WARNING,"保存网页时发生异常", e);
		} finally {
			try {
				if(fos!=null){
					fos.close();
				}
			} catch (Exception e) {
				logger.log(Level.WARNING,"关闭输出流时发生异常", e);
			}
		}

		return false;
	}

	/**
	 * 判断链接是否符合格式，链接不能为空。
	 *
	 * @param link 需判断的链接，此链接不能为空，否则抛空指针异常。
	 * @return 符合返回true，否则返回false。
	 * @exception 抛出 传入的参数“link”为空 异常。
	 */
	public static boolean isLink(String link){
		if(link==null){
			throw new NullPointerException("传入的参数“link”为空");
		}
		try {
			new URL(link);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 修复非完整链接。
	 * @param partLink 非完整链接，即相对链接，且前后不能有空格。
	 * @return 成功返回补全后的链接，失败返回null。
	 */
	public String repairLink(String partLink){
		String fullLink=null;

		try {
			fullLink=repairLink(baseLink, partLink);
		} catch (Exception e) {
			logger.log(Level.WARNING,"修复相对链接异常",e);
		}

		return fullLink;
	}

	public static String repairLink(String baseLink,String partLink){
		return repairLink(baseLink, partLink, false);
	}

	/**
	  * 根据指定父链接修复非完整链接。
	  *
	  * @param baseLink 基链接。
	  * @param partLink 相对链接。
	  * @return 成功返回补全后的链接，失败返回null。
	  */
	public static String repairLink(String baseLink,String partLink,boolean strict){
		String fullLink = null;

		try {
			URL url = null; //链接对象
			int index = -1;
			// 这个是一个Bug，相对链接以“?”开始。
			if (!strict && ('?' == partLink.charAt (0))){
				// 删除基链接中的查询子字符串
				if (-1 != (index = baseLink.lastIndexOf('?'))) {
					baseLink = baseLink.substring(0, index);
				}
				url = new URL(baseLink + partLink);
			}
			else {
				//使用基链接和相对链接创建一个链接对象
				url = new URL(new URL(baseLink), partLink);
			}

			//获取此 URL 的路径部分
			String path = url.getFile();

			// 判断基链接是否已修改
			boolean modified = false;
			// 判断非完整链接是否为绝对
			boolean absolute = partLink.startsWith("/");
			if (!absolute) {
				// 不能修复所有，只能修复这一种开始的。
				while (path.startsWith("/.")) {
					if (path.startsWith("/../")) {
						path = path.substring(3);
						modified = true;
					}
					else if (path.startsWith("/./") || path.startsWith("/.")) {
						path = path.substring(2);
						modified = true;
					}
					else{
						break;
					}
				}
			}

			// fix backslashes
			while (-1 != (index = path.indexOf("/\\"))) {
				path = path.substring(0, index + 1) + path.substring(index + 2);
				modified = true;
			}

			if (modified) {
				url = new URL(url, path);
			}
			// 字符串形式的链接
			fullLink = (url.toExternalForm());

		} catch (Exception e) {
			logger.log(Level.WARNING,"补全相对链接异常",e);
		}

		return fullLink;
	}


	public static void main(String[] args) throws Exception{
		try {
			HtmlPage page=new HtmlPage(new URL("http://dict.cn/login.php"),"gbk","username=leohua&password=3438191985");
			System.out.println(page.getHtmlCode());
		} catch (Exception e) {
			e.printStackTrace();
		}

//		try {
//			HtmlPage htmlPage=new HtmlPage();
//
//			Map<String, String> requestHeaderMap=new HashMap<String, String>();
//
//			//设置HTTP协议头，设置内容类型为经过编码的URL
//			requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded");
//			//模拟IE浏览提交登录请求
//			requestHeaderMap.put("User-Agent","Mozilla/5.0 (compatible; MSIE 6.0; Windows NT)");
//
//			htmlPage.setRequestHeader(requestHeaderMap);
//
//			htmlPage.setUrl(new URL("http://dict.cn/login.php?url=%2Fbdc%2F"));
//
//			htmlPage.httpPostMethod("username=leohua&password=3438191985");
//
//			String cookie=htmlPage.getCookie();
//
//			htmlPage=new HtmlPage();
//			htmlPage.setUrl(new URL("http://www4.dict.cn/bdc/141-3"));
//
//			Map<String, String> param=new HashMap<String, String>();
//
//			param.put("cookie", cookie);
//			param.put("User-Agent","Mozilla/5.0 (compatible; MSIE 6.0; Windows NT)");
//
//			htmlPage.setRequestHeader(param);
//
//			htmlPage.httpGetMethod();
//
////			System.out.println(htmlPage.getHtmlCode());
//			System.out.println(cookie);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//		try {
//			HtmlPage htmlPage=new HtmlPage();
//
//			Map<String, String> requestHeaderMap=new HashMap<String, String>();
//
//			htmlPage.setUrl(new URL("http://www.kaixin001.com/login/login_api.php"));
//
//			//设置HTTP协议头，设置内容类型为经过编码的URL
//			requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded");
//			//模拟IE浏览提交登录请求
//			requestHeaderMap.put("User-Agent","Mozilla/5.0 (compatible; MSIE 6.0; Windows NT)");
//			requestHeaderMap.put("Referer", "http://www.kaixin001.com/");
//			requestHeaderMap.put("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
//
//			htmlPage.setRequestHeader(requestHeaderMap);
//
//			htmlPage.httpPostMethod("ver=1&email=hellohualong%40sina.com&rpasswd=c83497b56d2f2bbf3f55ed88c7410d730e393d0d&encypt=fUGYKJVZiTGvBPe&url=%2Fhome%2F");
//
//			String cookie=htmlPage.getCookie();
//
//			System.out.println(htmlPage.getHtmlCode());
//
//			htmlPage=new HtmlPage();
//
//			htmlPage.setEncoding("UTF-8");
//
//			htmlPage.setUrl(new URL("http://www.kaixin001.com/home/?uid=7516984"));
//
//			Map<String, String> param=new HashMap<String, String>();
//			param.put("User-Agent","Mozilla/5.0 (compatible; MSIE 6.0; Windows NT)");
//			param.put("Cookie", cookie);
////			param.put("Cookie", "_ref=f7816ba10b4f6ee29b8ed7f6e2e1c7e0..CA76.4c4eb14fca70e; _user=e6a048dccd5484d3866635e88ac3e06f_7516984_1280225615; _uid=7516984; _email=hellohualong%40sina.com; _sso=7516984; SERVERID=_srv103-172_;");
//			param.put("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/QVOD, application/QVOD, application/x-ms-application, application/x-ms-xbap, application/vnd.ms-xpsdocument, application/xaml+xml, */*");
//
//			System.out.println(cookie);
//			System.out.println("_ref=ab1114d6840b00d586c6c1a853b7dfdc..B6D3.4c4eac354cff5; _user=171d5e42c4275699ecd871aeac514862_7516984_1280224379; _uid=7516984; _email=hellohualong%40sina.com; _sso=; onlinenum=c%3A0; presence=X9fBNywfF7W2WZq9lfdlkMase_6luzIETE6smw.NzUxNjk4NA; SERVERID=_srv61-107_");
//
//
//			htmlPage.setRequestHeader(param);
//
//			htmlPage.httpGetMethod();
//
//			System.out.println(htmlPage.getHtmlCode());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//		try {
//			HtmlPage htmlPage=new HtmlPage();
//
//			Map<String, String> requestHeaderMap=new HashMap<String, String>();
//			htmlPage.setEncoding("utf-8");
//			htmlPage.setUrl(new URL("http://www.renren.com/PLogin.do"));
//
//			//设置HTTP协议头，设置内容类型为经过编码的URL
//			requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded");
//			//模拟IE浏览提交登录请求
//			requestHeaderMap.put("User-Agent","Mozilla/5.0 (compatible; MSIE 6.0; Windows NT)");
//			requestHeaderMap.put("Referer", "http://www.kaixin001.com/");
//			requestHeaderMap.put("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
//
//			htmlPage.setRequestHeader(requestHeaderMap);
//
//			htmlPage.httpPostMethod("email=hellohualong%40sina.com&password=3438191985");
//
//			String cookie=htmlPage.getCookie();
//
////			System.out.println(htmlPage.getHtmlCode());
//
//			htmlPage=new HtmlPage();
//
//			htmlPage.setEncoding("utf-8");
//
//			htmlPage.setUrl(new URL("http://www.renren.com/Home.do"));
//
//			Map<String, String> param=new HashMap<String, String>();
//			param.put("User-Agent","Mozilla/5.0 (compatible; MSIE 6.0; Windows NT)");
//			param.put("Cookie", cookie);
////			param.put("Cookie", "t=307a45934aa99f85224692567ef266a07; domain=.renren.com; path=/; societyguester=307a45934aa99f85224692567ef266a07; domain=.renren.com; path=/; id=274806937; domain=.renren.com; path=/; xnsid=ec3ab744; domain=.renren.com; path=/; kl=kl_274806937; domain=.renren.com; path=/; loginfrom=syshome; domain=.renren.com; path=/;");
////			param.put("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/QVOD, application/QVOD, application/x-ms-application, application/x-ms-xbap, application/vnd.ms-xpsdocument, application/xaml+xml, */*");
//
//			System.out.println(cookie);
////			System.out.println("_ref=ab1114d6840b00d586c6c1a853b7dfdc..B6D3.4c4eac354cff5; _user=171d5e42c4275699ecd871aeac514862_7516984_1280224379; _uid=7516984; _email=hellohualong%40sina.com; _sso=; onlinenum=c%3A0; presence=X9fBNywfF7W2WZq9lfdlkMase_6luzIETE6smw.NzUxNjk4NA; SERVERID=_srv61-107_");
//
////			System.out.println("\r\n\r\n");
//
//
//			htmlPage.setRequestHeader(param);
//
//			htmlPage.httpGetMethod();
//
//			System.out.println(htmlPage.getHtmlCode());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
