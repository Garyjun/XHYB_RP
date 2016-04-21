package com.brainsoon.common.util.dofile.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.web.util.WebUtils;

import com.brainsoon.common.listener.InitSysParameter;

/**
 * <dl>
 * <dt>WebUtils</dt>
 * <dd>Description:web应用工具</dd>
 * <dd>Copyright: Copyright (C) 2008</dd>
 * <dd>Company: 青牛（北京）技术有限公司</dd>
 * <dd>CreateDate: Nov 6, 2008</dd>
 * </dl>
 * 
 * @author tanghui
 */
public abstract class WebAppUtils {
	private static final String WEB_INF_CLASSES = "WEB-INF/classes/";
	private static final Logger logger = Logger.getLogger(WebAppUtils.class);
	/**
	 * Web app root key parameter at the servlet context level
	 * (i.e. a context-param in <code>web.xml</code>): "webAppRootKey".
	 */
	private static final String WEB_APP_ROOT_KEY = "bsfw.webapp.root";
	
	/**
	 *  获取web应用的根目录,可能windows包含WebRoot目录
	 * @return
	 *
	 * @author Fuwenbin
	 * @date Nov 6, 2008 11:49:16 AM 
	 */
	public static String getWebAppRoot() {
		String webAppRoot = System.getProperty(WEB_APP_ROOT_KEY);
		webAppRoot =  StringUtils.isBlank(webAppRoot) ? System
				.getProperty(WebUtils.DEFAULT_WEB_APP_ROOT_KEY) : webAppRoot;
		//针对无法获取到webAppRoot的时候，通过类路径来获取
		if(StringUtils.isEmpty(webAppRoot)){
			webAppRoot = WebAppUtils.class.getClassLoader().getResource("").getPath();
			if(StringUtils.isNotBlank(webAppRoot)){
				if(webAppRoot.startsWith("/")){
					webAppRoot = StringUtils.substring(webAppRoot, 1, webAppRoot.length());
				}
				if(webAppRoot.endsWith(WEB_INF_CLASSES)){
					webAppRoot = StringUtils.substring(webAppRoot, 0, webAppRoot.lastIndexOf(WEB_INF_CLASSES));
				}
			}
		}
		if(StringUtils.isNotBlank(webAppRoot)){
			webAppRoot = webAppRoot.replaceAll("\\\\","/");
		}
		return webAppRoot;
	}
	
	
	
	/**
	 * 获取web应用的WEB-INF目录
	 * @return
	 *
	 * @author Fuwenbin
	 * @date Nov 6, 2008 12:33:21 PM 
	 */
	public static String getWebAppWebInfo() {
		String webAppRoot = getWebAppRoot();
		if (StringUtils.isBlank(webAppRoot)) {
			return null;
		}
		String path = webAppRoot + "WEB-INF" + SystemUtils.FILE_SEPARATOR;
		File tempFile = new File(path);
		if (!tempFile.exists()) {
			DoFileUtils.mkdir(path); //创建目录
		}
		return path;
	}
	
	
	/**
	 * 获取web应用的fileDir目录下的某个目录(相对路径)
	 * @return
	 *
	 * @author tanghui
	 * @date Nov 6, 2014 12:33:21 PM 
	 */
	public static String getWebRootRelDir(String fileDirName) {
		String path = getWebAppRelFileDir();
		if(StringUtils.isNotBlank(fileDirName)){
			 path = getWebAppRelFileDir() + fileDirName + SystemUtils.FILE_SEPARATOR;
		}
		return path.replaceAll("\\\\", "\\/");
	}
	
	
	/**
	 * 获取web应用的fileDir目录下的某个目录(绝对路径)
	 * @return
	 *
	 * @author tanghui
	 * @date Nov 6, 2014 12:33:21 PM 
	 */
	public static String getWebRootBaseDir(String fileDirName) {
		String path = getWebAppBaseFileDir();
		if(StringUtils.isNotBlank(fileDirName)){
			path = getWebAppBaseFileDir() + fileDirName + SystemUtils.FILE_SEPARATOR;
		}
		File tempFile = new File(path);
		if (!tempFile.exists()) {
			DoFileUtils.mkdir(path); //创建目录
		}
		return  path.replaceAll("\\\\", "\\/");
	}
	
	
	/**
	 * 获取web应用的fileDir的目录(相对路径)
	 * @return
	 *
	 * @author tanghui
	 * @date Nov 6, 2014 12:33:21 PM 
	 */
	public static String getWebAppRelFileDir() {
		return  ConstantsDef.fileDir + SystemUtils.FILE_SEPARATOR;
	}
	
	/**
	 * 获取web应用的fileDown的目录(相对路径)
	 * @return
	 *
	 * @author tanghui
	 * @date Nov 6, 2014 12:33:21 PM 
	 */
	public static String getWebAppRelFileDownFr() {
		return  ConstantsDef.fileDown + SystemUtils.FILE_SEPARATOR;
	}
	
	/**
	 * 获取web应用的FileDir目录(绝对路径)
	 * @return
	 *
	 * @author tanghui
	 * @date Nov 6, 2014 12:33:21 PM 
	 */
	public static String getWebAppBaseFileDir() {
		String webAppRoot = getWebAppRoot();
		if (StringUtils.isBlank(webAppRoot)) {
			return null;
		}
		String path =  webAppRoot + getWebAppRelFileDir();
		File tempFile = new File(path);
		if (!tempFile.exists()) {
			DoFileUtils.mkdir(path); //创建目录
		}
		return path;
	}
	
	/**
	 * 获取web应用的fileDir目录下的converFileRoot目录(相对路径)
	 * @return
	 *
	 * @author tanghui
	 * @date Nov 6, 2014 12:33:21 PM 
	 */
	public static String getWebAppRelFileDirCFR() {
		return  getWebAppRelFileDir() + ConstantsDef.converFileRoot + SystemUtils.FILE_SEPARATOR;
	}
	
	
	/**
	 * 获取web应用的fileDir目录下的converFileRoot目录(绝对路径)
	 * @return
	 *
	 * @author tanghui
	 * @date Nov 6, 2014 12:33:21 PM 
	 */
	public static String getWebAppBaseFileDirCFR() {
		String webAppRoot = getWebAppRoot();
		if (StringUtils.isBlank(webAppRoot)) {
			return null;
		}
		String path = webAppRoot + getWebAppRelFileDirCFR();
		File tempFile = new File(path);
		if (!tempFile.exists()) {
			DoFileUtils.mkdir(path); //创建目录
		}
		return path;
	}
	
	
	/**
	 * 获取web应用的fileDir目录下的fileRoot目录(相对路径)
	 * @return
	 *
	 * @author tanghui
	 * @date Nov 6, 2014 12:33:21 PM 
	 */
	public static String getWebAppRelFileDirFR() {
		return  getWebAppRelFileDir() + ConstantsDef.fileRoot + SystemUtils.FILE_SEPARATOR;
	}
	
	
	/**
	 * 获取web应用的fileDir目录下的fileRoot目录(绝对路径)
	 * @return
	 *
	 * @author tanghui
	 * @date Nov 6, 2014 12:33:21 PM 
	 */
	public static String getWebAppBaseFileDirFR() {
		String webAppRoot = getWebAppRoot();
		if (StringUtils.isBlank(webAppRoot)) {
			return null;
		}
		String path =  webAppRoot + getWebAppRelFileDirFR();
		File tempFile = new File(path);
		if (!tempFile.exists()) {
			DoFileUtils.mkdir(path); //创建目录
		}
		return path;
	}
	/**
	 * 获取web应用的fileDown目录下的fileDown目录(绝对路径)
	 * @return
	 *
	 * @author tanghui
	 * @date Nov 6, 2014 12:33:21 PM 
	 */
	public static String getWebAppBaseFileDown() {
		String webAppRoot = getWebAppRoot()+getWebAppRelFileDir();
		if (StringUtils.isBlank(webAppRoot)) {
			return null;
		}
		String path =  webAppRoot + getWebAppRelFileDownFr();
		File tempFile = new File(path);
		if (!tempFile.exists()) {
			DoFileUtils.mkdir(path); //创建目录
		}
		return path;
	}
	
	/**
	 * 获取web应用的classes目录
	 * @return
	 *
	 * @author Fuwenbin
	 * @date Nov 6, 2008 12:33:23 PM 
	 */
	public static String getWebAppClassesPath() {
		String webInfo = getWebAppWebInfo();
		if (StringUtils.isBlank(webInfo)) {
			return null;
		}
		String path =  webInfo + "classes" + SystemUtils.FILE_SEPARATOR;
		File tempFile = new File(path);
		if (!tempFile.exists()) {
			DoFileUtils.mkdir(path); //创建目录
		}
		return path;
	}
	
	/**
	 * 获取web应用的临时目录
	 * @return
	 *
	 * @author Fuwenbin
	 * @date Nov 6, 2008 1:16:30 PM 
	 */
	public static String getTempDir() {
		String webInfo = getWebAppWebInfo();
		if (StringUtils.isBlank(webInfo)) {
			return null;
		}
		String path =  webInfo + "tmp" + SystemUtils.FILE_SEPARATOR;
		File tempFile = new File(path);
		if (!tempFile.exists()) {
			DoFileUtils.mkdir(path); //创建目录
		}
		return path;
	}
	
	 //test
	 public static void main(String[] args){
		 String ss = WebAppUtils.getWebAppRoot();
	   	 	System.out.println(ss);
	 }
}
