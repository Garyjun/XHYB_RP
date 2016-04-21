package com.brainsoon.common.util.dofile.util;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.view.WatchThread;
import com.brainsoon.semantic.ontology.model.DoFileQueue;

/**
 *
 * @ClassName: tools
 * @Description: 文件处理工具类
 * @author tanghui
 * @date 2014-4-17 下午3:25:48
 *
 */
public class DoFileUtils {

	protected static final Logger logger = Logger.getLogger(DoFileUtils.class);

	private static final String pictureFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.pictureFormat);

	private static final String videoFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.videoFormat);

	private static final String audioFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.audioFormat);

	private static final String animaFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.animaFormat);

	private static final String documentFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.documentFormat);

	/*
	 * Java文件操作 获取文件扩展名
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			if(filename.lastIndexOf('.') != -1){
				int dot = filename.lastIndexOf('.');
				if ((dot > -1) && (dot < (filename.length() - 1))) {
					return filename.substring(dot + 1).toLowerCase();
				}
			}else{
				filename = "";
			}
		}else{
			filename = "";
		}
		return filename;
	}

	/*
	 * Java文件操作 获取不带扩展名的文件名
	 */
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			filename = filename.replaceAll("\\\\", "/");
			int stt = filename.lastIndexOf('/')+1;
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(stt, dot);
			}
		}else{
			filename = "";
		}
		return filename;
	}


	/*
	 * Java文件操作 获取带扩展名的文件名
	 */
	public static String getFileNameHasEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			filename = filename.replaceAll("\\\\", "/");
			int stt = filename.lastIndexOf('/')+1;
			int dot = filename.length();
			if ((dot > -1) && (stt < (filename.length()))) {
				return filename.substring(stt, dot);
			}
		}else{
			filename = "";
		}
		return filename;
	}


	/*
	 * 获取上级目录
	 */
	public static String getParentFileDir(String filePath) {
		filePath = filePath.replaceAll("\\\\", "/");
		if ((filePath != null) && (filePath.length() > 0)) {
			filePath = filePath.substring(0, filePath.lastIndexOf("/"));
		}
		return filePath;
	}


	public static String htmlspecialchars(String str) {
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("\"", "&quot;");
		return str;
	}

	// 过滤特殊字符
	public static String StringFilter(String str) throws PatternSyntaxException {
		// 只允许字母和数字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}




	/**
	 * @param 获取水印的位子
	 *            0:左上角 1:右上角 2:左下角 3:右下角 4:居中 5:平铺
	 * @return
	 */
	public static int getWmDegree(int wmPosition) {
		int position = 3; // 默认为右下角
		if (wmPosition == 01) {
			position = 0;
		} else if (wmPosition == 02) {
			position = 1;
		} else if (wmPosition == 03) {
			position = 2;
		} else if (wmPosition == 04) {
			position = 3;
		} else if (wmPosition == 05) {
			position = 4;
		} else if (wmPosition == 06) {
			position = 5;
		}
		return position;
	}

	/**
	 * 判断字符串数组中是否包含某字符串元素
	 *
	 * @param substring
	 *            某字符串
	 * @param source
	 *            源字符串数组
	 * @return 包含则返回true，否则返回false
	 */
	public static boolean isIn(String substring, String[] source) {
		if (StringUtils.isNotEmpty(substring)) {
			if (source == null || source.length == 0) {
				return false;
			}
			for (int i = 0; i < source.length; i++) {
				String aSource = source[i];
				if (aSource.toUpperCase().equals(substring.toUpperCase())) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * @param fontSizeType
	 *            通过水印方位来设置水印的角度 01：完全透明 02：半透明 03 : 模糊
	 * @return
	 */
	public static String getAlpha(int wmAlpha) {
		String alpha = "0.5"; // 默认为半透明
		if (wmAlpha == 01) {
			alpha = "1";
		} else if (wmAlpha == 02) {
			alpha = "0.5";
		} else if (wmAlpha == 03) {
			alpha = "0.2";
		}
		return alpha;
	}

	/**
	 * 将路径中包含的空格转换成 "%20"
	 *
	 * @param path
	 * @return
	 */
	public static String getChRealPath(String path) {
		if (path != null && !path.equals("")) {
			path = path.replaceAll("%20", " ");
		}
		return path;
	}

	/**
	 * 判断是否是文件
	 *
	 * @param path
	 * @return
	 */
	public static boolean exitFile(String path) {
		File file = new File(path);
		if (!file.isFile()) {
			return false;
		} else {
			return true;
		}
	}

	// 创建文件上传路径
	public static void mkdir(String path) {
		File fd = null;
		try {
			if (StringUtils.isNotEmpty(path)) {
				path = replaceFliePathStr(path);
				path = path.substring(0,path.lastIndexOf("/"));
				fd = new File(path);
				if (!fd.exists()) {
					fd.mkdirs();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fd = null;
		}
	}

	public static void createdir(String directory, String subDirectory) {
		String dir[];
		File fl = new File(directory);
		try {
			if ("".equals(subDirectory) && !fl.exists()) {
				if (!fl.mkdirs()) {
					throw new DoFileException("创建文件目录失败！");
				}
			} else if (!"".equals(subDirectory)) {
				dir = subDirectory.replace('\\', '/').split("/");
				for (int i = 0; i < dir.length; i++) {
					File subFile = new File(directory + File.separator + dir[i]);
					if (!subFile.exists() && !subFile.mkdir()) {
						throw new DoFileException("创建文件目录失败！");
					}
					directory += File.separator + dir[i];
				}
			}
		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	/**
	 * @return
	 * @throws IOException
	 *
	 * @Title: getFileConverTempDir
	 * @Description: 获取系统临时目录,默认最后一级目录会自动带上 '/'
	 * @param
	 * @return String
	 * @throws
	 */
	public static String getFileConverTempDir() throws IOException {
		String fileConverTempDir = PropertiesReader.getInstance().getProperty(
				ConstantsDef.fileConverTempDir);
		if (StringUtils.isEmpty(fileConverTempDir)) {
			throw new DoFileException("未配置{fileConverTempDir}临时目录路径.");
		}
		if (!StringUtils.endsWith(fileConverTempDir.replaceAll("\\\\", "/"),
				"/")) {
			fileConverTempDir += "/";
		}
		File file = new File(fileConverTempDir);
		if (!file.exists()) {
			mkdir(fileConverTempDir); //创建目录
		}
		return fileConverTempDir;
	}


	/**
	 * @return
	 * @throws IOException
	 *
	 * @Title: getTempFileDir
	 * @Description: 获取系统相对于应用的临时目录,默认最后一级目录会自动带上 '/'
	 * @param
	 * @return String
	 * @throws
	 */
	public static String getTempFileDir() throws IOException {
		String fileConverTempDir = WebAppUtils.getTempDir();
		if (StringUtils.isEmpty(fileConverTempDir)) {
			throw new DoFileException("未配置{fileConverTempDir}临时目录路径.");
		}
		if (!StringUtils.endsWith(fileConverTempDir.replaceAll("\\\\", "/"),
				"/")) {
			fileConverTempDir += "/";
		}
		File file = new File(fileConverTempDir);
		if (!file.exists()) {
			mkdir(fileConverTempDir); //创建目录
		}
		return fileConverTempDir;
	}

	/**
	 * 替换 \ 为 /
	 *
	 * @param filePath
	 * @return
	 */
	public static String replaceFliePathStr(String filePath) {
		if (StringUtils.isNotEmpty(filePath))
			return filePath.replaceAll("\\\\", "\\/");
		else
			return filePath;
	}




	/**
	 * 拼接文件路径及文件名
	 * @Title: pjFilePath
	 * @Description:
	 * @param
	 * @return String
	 * @throws
	 */
	public static String connectFilePath(String filePath,String fileName) {
		if (StringUtils.isNotEmpty(filePath)){
			filePath = replaceFliePathStr(filePath);
			if(filePath.endsWith("/")){
				filePath += fileName;
			}else{
				filePath += "/" + fileName;
			}
		}
		File tempFile = new File(filePath);
		if (!tempFile.exists()) {
			mkdir(filePath); //创建目录
		}
		return filePath;
	}

	/**
	 * 取得汉字的长度
	 *
	 * @param text
	 * @return
	 */
	public static float getLength(String text) {
		int length = 0;
		for (int i = 0; i < text.length(); i++) {
			if (new String(text.charAt(i) + "").getBytes().length > 1) {
				length += 2;
			} else {
				length += 1;
			}
		}
		return length / 2;
	}

	/**
	 *
	 * @Title: isMyWantFile
	 * @Description: 判断文件是否符合格式
	 * @param sourceFile
	 * @param suffix
	 * @return boolean
	 * @throws
	 */
	public static boolean isMyWantType(String sourceFile, String suffix) {
		boolean b = false;
		try {
			if (StringUtils.isNotBlank(sourceFile)
					&& sourceFile.indexOf(".") != -1
					&& StringUtils.isNotBlank(suffix)) {
				suffix = suffix.toLowerCase();
				// 根据类型，进行相应的解压缩
				String type = sourceFile
						.substring(sourceFile.lastIndexOf(".") + 1);
				if (StringUtils.isNotBlank(type)) {
					if (type.toLowerCase().equals(suffix)) {
						b = true;
					}
				} else {
					throw new Exception("无法判断文件的扩展名！");
				}
			} else {
				throw new Exception("无法判断文件的扩展名！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}


	/**
	 * 递归查找文件
	 *
	 * @param baseDirName
	 *            查找的文件夹路径
	 * @param targetFileName
	 *            需要查找的文件名
	 * @param fileList
	 *            查找到的文件集合
	 */
	public static void findFiles(String baseDirName, String targetFileName,
			List fileList) {
		/**
		 * 算法简述： 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件，
		 * 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。 队列不空，重复上述操作，队列为空，程序结束，返回结果。
		 */
		String tempName = null;
		// 判断目录是否存在
		File baseDir = new File(baseDirName);
		if (!baseDir.exists() || !baseDir.isDirectory()) {
			logger.debug("文件查找失败：" + baseDirName + "不是一个目录！");
		} else {
			String[] filelist = baseDir.list();
			for (int i = 0; i < filelist.length; i++) {
				File readfile = new File(baseDirName + File.separator + filelist[i]);
				if (!readfile.isDirectory()) {
					tempName = readfile.getName();
					if (wildcardMatch(targetFileName, tempName)) {
						// 匹配成功，将文件添加到结果集
						fileList.add(readfile);
					}
				} else if (readfile.isDirectory()) {
					findFiles(baseDirName + File.separator + filelist[i], targetFileName,
							fileList);
				}
			}
		}
	}


	/**
	 * 查找指定类型文件
	 * @param baseDirName
	 * @param targetFileName
	 * @param jumpDir 迭代时跳过的路径，全路径
	 * @param fileList
	 */
	public static void findFiles(String baseDirName, String targetFileName,String jumpDir,List fileList) {
		String tempName = null;
		// 判断目录是否存在
		File baseDir = new File(baseDirName);
		File jumpFile = new File(jumpDir);
		if(!baseDir.equals(jumpFile)){
			if (!baseDir.exists() || !baseDir.isDirectory()) {
				logger.debug("文件查找失败：" + baseDirName + "不是一个目录！");
			} else {
				String[] filelist = baseDir.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(baseDirName + File.separator + filelist[i]);
					if (!readfile.isDirectory()) {
						tempName = readfile.getName();
						if (wildcardMatch(targetFileName, tempName)) {
							// 匹配成功，将文件添加到结果集
							fileList.add(readfile);
						}
					} else if (readfile.isDirectory()) {
						findFiles(baseDirName + File.separator + filelist[i], targetFileName,jumpDir,fileList);
					}
				}
			}
		}
	}



	/**
	 * 递归查找指定类型文件
	 * @param baseDirName 遍历目录
	 * @param filterTypes 指定类型，多个以逗号分隔 如 jpg,gif,png  ,所有文件类型 *
	 * @param jumpDir 跳过的路径
	 * @param fileList 装载文件对象集合
	 */
	public static void findFiles2(String baseDirName, String filterTypes,String jumpDir,List fileList) {
		String tempName = null;
		// 判断目录是否存在
		File baseDir = new File(baseDirName);
		File jumpFile = new File(jumpDir);
		if(!baseDir.equals(jumpFile)){
			if (!baseDir.exists() || !baseDir.isDirectory()) {
				logger.debug("文件查找失败：" + baseDirName + "不是一个目录！");
			} else {
				String[] filelist = baseDir.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(baseDirName + File.separator + filelist[i]);
					if (!readfile.isDirectory()) {
						tempName = readfile.getName();
						if(StringUtils.equals(filterTypes, "*")){
							fileList.add(readfile);
						}else{
							String fileType = StringUtils.substringAfterLast(tempName, ".");
							if(!StringUtils.isEmpty(fileType)
									&&StringUtils.containsIgnoreCase(filterTypes,fileType)){
								fileList.add(readfile);
							}
						}
					} else if (readfile.isDirectory()) {
						findFiles2(baseDirName + File.separator + filelist[i], filterTypes,jumpDir,fileList);
					}
				}
			}
		}
	}


	/**
	 * 通配符匹配
	 *
	 * @param pattern
	 *            通配符模式
	 * @param str
	 *            待匹配的字符串
	 * @return 匹配成功则返回true，否则返回false
	 */
	private static boolean wildcardMatch(String pattern, String str) {
		int patternLength = pattern.length();
		int strLength = str.length();
		int strIndex = 0;
		char ch;
		for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
			ch = pattern.charAt(patternIndex);
			if (ch == '*') {
				// 通配符星号*表示可以匹配任意多个字符
				while (strIndex < strLength) {
					if (wildcardMatch(pattern.substring(patternIndex + 1),
							str.substring(strIndex))) {
						return true;
					}
					strIndex++;
				}
			} else if (ch == '?') {
				// 通配符问号?表示匹配任意一个字符
				strIndex++;
				if (strIndex > strLength) {
					// 表示str中已经没有字符匹配?了。
					return false;
				}
			} else {
				if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
					return false;
				}
				strIndex++;
			}
		}
		return (strIndex == strLength);
	}



	/**
	 * 对路径进行GBK encode编码
	 * @param srcUrl
	 * @return
	 */
	public static String encondeFullUrl(String srcUrl){
		StringBuffer descUrl = new StringBuffer();
		srcUrl = StringUtils.replace(srcUrl, "\\", "/");
		String [] urls = StringUtils.split(srcUrl,"/");
		for (int i = 0; i < urls.length; i++) {
			String url = urls[i];
			try {
				url = URLEncoder.encode(url, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("编码异常"+e.getMessage());
			}
			if(i > 0 && i < urls.length){
				descUrl.append("/");
			}
			descUrl.append(url);
		}
		String rtn = descUrl.toString();
		if(srcUrl.startsWith("/")){
			rtn = "/" + rtn;
		}
		if(srcUrl.endsWith("/")){
			rtn = rtn + "/";
		}
		rtn = StringUtils.replace(rtn, "+", "%20");
		return rtn;
	}


	/**
	        替换规则:
	       空格        		&nbsp;     &#160;
	    <           &lt;       &#60;
	    >           &gt;       &#62;
	    &           &amp;      &#38;
	    "           &quot;     &#34;
	    ?(版权)     &copy;     &#169;
	    ?(注册)     &reg;      &#174;
	    *           &times;    &#215;
	    ÷           &divide;   &#247;
	         ￥           		&yen;      &#165;
	 */
	public static final Map<String,String> transSigns = new HashMap<String,String>();
	static{
		transSigns.put("&nbsp;", "&#160;");
		transSigns.put("&lt;", "&#60;");
		transSigns.put("&gt;", "&#62;");
		transSigns.put("&amp;", "&#38;");
		transSigns.put("&quot;", "&#34;");
		transSigns.put("&copy;", "&#169;");
		transSigns.put("&reg;", "&#174;");
		transSigns.put("&times;", "&#215;");
		transSigns.put("&divide;", "&#247;");
		transSigns.put("&yen;", "&#165;");
	}



	/**
	 * xml文本转换为html文本
	 * @param content
	 * @return
	 */
	public static String xml2Html(String content){
		if(!StringUtils.isEmpty(content)){
			for(Iterator itr = transSigns.entrySet().iterator();itr.hasNext();){
				Map.Entry<String, String> entry = (Map.Entry<String, String>)itr.next();
				content = StringUtils.replace(content, entry.getValue(), entry.getKey());
			}
		}
		return content;
	}
	/**
	 * html文本转换为xml文本
	 * @param content
	 * @return
	 */
	public static String html2Xml(String content){
		if(!StringUtils.isEmpty(content)){
			for(Iterator itr = transSigns.entrySet().iterator();itr.hasNext();){
				Map.Entry<String, String> entry = (Map.Entry<String, String>)itr.next();
				content = StringUtils.replace(content, entry.getKey(),entry.getValue());
			}
		}
		return content;
	}



	/**
	 * Null转换为""
	 * @param s
	 * @return
	 */
	public static String Null2Empty(String s) {
		if (s != null) {
			return s;
		} else if("null".equals(s)) {
			return "";
		}
		return "";
	}

	/**
	 * Null转换为""
	 * @param s
	 * @return
	 */
	public static String Null2Empty(Integer s) {
		if (s != null) {
			return String.valueOf(s);
		} else if(s==0) {
			return "";
		}
		return "";
	}

	/**
	 * Null转换为""
	 * @param s
	 * @return
	 */
	public static String Null2Empty(Date s) {
		if (s != null) {
			try {
				return DateUtil.convertDateTimeToString(s);
			}catch(Exception ex) {
				return "";
			}
		} else if("null".equals(s)) {
			return "";
		}
		return "";
	}


	/**
	 *
	 * @Title: getFileCharset
	 * @Description:获取文件的编码格式
	 * 如：utf8、gbk、 gb2312等，获取不到返回空
	 * (可用于检查txt、HTML、XML等文本文件或字符流的编码)
	 * @param
	 * @return String
	 * @throws
	 */
	public static String getFileCharset(String filePath) {
		String result = "";
		try {
			/*------------------------------------------------
			detector是探测器，它把探测任务交给具体的探测实现类的实例完成。
			cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法
			加进来，如ParsingDetector、 JChardetFacade、ASCIIDetector、UnicodeDetector。
			detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的
			字符集编码。
			---------------------------------------------------*/

			CodepageDetectorProxy proxy = CodepageDetectorProxy.getInstance();

			/*------------------------------------------------------
			  ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于
			  指示是否显示探测过程的详细信息，为false不显示。
			-------------------------------------------------------*/

			proxy.add(new ParsingDetector(false));
			proxy.add(JChardetFacade.getInstance());
			proxy.add(ASCIIDetector.getInstance());
			proxy.add(UnicodeDetector.getInstance());

			/*----------------------------------------------
			   JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码
			   测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以
			   再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。
			 ---------------------------------------------------------*/
			Charset cset = null;
			File f = new File(filePath);
			cset = proxy.detectCodepage(f.toURL());
			if (cset != null) {
				result = cset.name();
			} else {
				System.out.println("查不到对应的编码格式");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String stringFilter(String str) {
		if (StringUtils.isEmpty(str))
			return "";
		// 只允许字母和数字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？:\\s*|\t|\r|\n:\":\']";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}


	/**
	    * @param file File 起始文件夹
	    * @param p Pattern 匹配类型
	    * @return ArrayList 其文件夹下的文件夹
	    */
	public static ArrayList<File> filePattern(File file, Pattern p) {
	     if (file == null) {
	       return null;
	     }
	     else if (file.isFile()) {
	       Matcher fMatcher = p.matcher(file.getName());
	       if (fMatcher.matches()) {
	         ArrayList<File> list = new ArrayList<File>();
	         list.add(file);
	         return list;
	       }
	     }
	     else if (file.isDirectory()) {
	       File[] files = file.listFiles();
	       if (files != null && files.length > 0) {
	         ArrayList<File> list = new ArrayList<File>();
	         for (int i = 0; i < files.length; i++) {
	           ArrayList<File> rlist = filePattern(files[i], p);
	           if (rlist != null) {
	             list.addAll(rlist);
	           }
	         }
	         return list;
	       }
	     }
	     return null;
	   }
	
	
	/**
	 * @param file File 起始文件夹
	 * @param p  Pattern 匹配类型
	 * @return ArrayList 其文件夹下的文件夹
	 * @param p 是否递归
	 */
	public static List<File> filePattern(File file, Pattern p,boolean dg) {
		List<File>  list = new ArrayList<File>();
		if (file == null) {
			return null;
		} else if (file.isFile()) {
			if(p != null){
				Matcher fMatcher = p.matcher(file.getName());
				if (fMatcher.matches()) {
					list.add(file);
					return list;
				}
			}else{
				list.add(file);
				return list;
			}
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					if(dg){
						if(p != null){
							List<File> rlist = (ArrayList<File>) filePattern(files[i],p,dg);
							if (rlist != null) {
								list.addAll(rlist);
							}
						}else{
							if (files[i] != null) {
								list.add(files[i]);
							}
						}
					}else{
						if(p != null){
							Matcher fMatcher = p.matcher(files[i].getName());
							if (fMatcher.matches()) {
								list.add(files[i]);
							}
						}else{
							list.add(files[i]);
						}
					}
				}
				return list;
			}
		}
		return null;
	}


	public static void deleteDir(String path){
		try {
			File file = new File(path);
			if (file.exists()) {
				if (file.isDirectory()) {
					File[] files = file.listFiles();
					for (File subFile : files) {
						if (subFile.isDirectory())
							deleteDir(subFile.getPath());
						else
							subFile.delete();
					}
				}
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 执行window or linux 命令（不带参数），如果 cmd输入参数太长，那么则会抛出：输入行太长，暂时未使用
	 *
	 * @Title: exeShell
	 * @Description:
	 * @param
	 * @return boolean
	 * @throws
	 */
	public static boolean exeShell(String cmd) {
		boolean b = true;
		Process p = null;
		WatchThread wt = null;
		WatchThread wtError = null;
		try {
			String[] command = new String[3];
			if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") > -1) {
				command[0] = "cmd";
				command[1] = "/c";
			} else {
				command[0] = "/bin/sh";
				command[1] = "-c";
			}
			command[2] = cmd.toString();
			logger.info("执行的命令为：" + cmd.toString());
			p = Runtime.getRuntime().exec(command);
			logger.info("开始执行命令..");
			wt = new WatchThread(p.getInputStream());
			wtError = new WatchThread(p.getErrorStream());
			wt.start();
			wtError.start();
			p.waitFor(); // 等待子进程的结束，子进程就是系统调用文件转换这个新进程
		} catch (IOException e) {
			e.printStackTrace();
			b = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			b = false;
		} finally {
			if (p != null) {
				p.destroy();
			}
			if (wt != null) {
				wt.setOver(true);
			}
			if (wtError != null) {
				wtError.setOver(true);
			}
		}
		return b;
	}
	
	
	
	/**
	 * 执行 linux 命令（带参数）
	 *
	 * @Title: exeShellWithParams
	 * @Description:
	 * @param
	 * @return boolean
	 * @throws
	 */
	public static boolean exeShellWithParams(String cmd) {
		boolean b = true;
		Process p = null;
		WatchThread wt = null;
		WatchThread wtError = null;
		try {
			logger.info("执行的命令为：" + cmd.toString());
			p = Runtime.getRuntime().exec(cmd);
			logger.info("开始执行命令..");
			wt = new WatchThread(p.getInputStream());
			wtError = new WatchThread(p.getErrorStream());
			wt.start();
			wtError.start();
			p.waitFor(); // 等待子进程的结束，子进程就是系统调用文件转换这个新进程
		} catch (IOException e) {
			e.printStackTrace();
			b = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			b = false;
		} finally {
			if (p != null) {
				p.destroy();
			}
			if (wt != null) {
				wt.setOver(true);
			}
			if (wtError != null) {
				wtError.setOver(true);
			}
		}
		return b;
	}
	
	
	

	/**
	 *
	 * @Title: exeCmd
	 * @Description: 执行命令并返回结果
	 * @param
	 * @return void
	 * @throws
	 */
	public static String exeCmd(String commandStr) {
		BufferedReader br = null;
		String rStr = "";
		try {
			Process p = Runtime.getRuntime().exec(commandStr);
			p.waitFor();
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			rStr = sb.toString();
			p.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return rStr;
	}

	/**
	 *
	 * @Title: exeWinKillCmd
	 * @Description: windows 结束进程方法
	 * @param processName
	 *            进程名称 如：java.exe
	 * @return boolean
	 * @throws
	 */
	public static boolean exeWinKillCmd(String processName) {
		boolean b = true;
		boolean b0 = false; // 用于判断b0是否存在
		try {
			logger.info("要结束的进程名称为：" + processName);
			java.lang.Process process = Runtime.getRuntime().exec("tasklist");
			Scanner in = new Scanner(process.getInputStream());
			while (in.hasNextLine()) {
				String p = in.nextLine();
				if (p.contains(processName)) {
					b0 = true;
					StringBuffer buf = new StringBuffer();
					for (int i = 0; i < p.length(); i++) {
						char ch = p.charAt(i);
						if (ch != ' ') {
							buf.append(ch);
						}
					}
					String processCmd = "tskill "
							+ buf.toString().split("Console")[0]
									.substring(processName.length());
					logger.info("执行的命令为：" + processCmd);
					Runtime.getRuntime().exec(processCmd);
					break;
				}
			}
			in.close();
			process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			b = false;
			throw new DoFileException("结束【" + processName + "】进程异常。"
					+ e.getMessage());
		} finally {
			if (!b0) {
				logger.info("要结束的进程名称为：" + processName + "不存在，无需处理。");
			}
		}
		return b;
	}


	/**
	 *
	 * @Title: exeQueryCmd
	 * @Description: windows 结束进程方法
	 * @param processName
	 *            进程名称 如：java.exe
	 * @return boolean
	 * @throws
	 */
	public static boolean exeQueryCmd(String processName) {
		boolean b = false;
		try {
			logger.info("要查询的进程名为：" + processName);
			java.lang.Process process = Runtime.getRuntime().exec("tasklist");
			Scanner in = new Scanner(process.getInputStream());
			while (in.hasNextLine()) {
				String p = in.nextLine();
				if (p.contains(processName)) {
					b = true;
					break;
				}
			}
			in.close();
			process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			b = false;
		} finally {
			if (!b) {
				logger.info("进程名为：" + processName + "不存在");
			}
		}
		return b;
	}

	/**
	 *
	 * @Title: readTxt
	 * @Description: 读取txt数据
	 * @param  file
	 * @ppStr ppStr  包含匹配的字符串
	 * @return void
	 * @throws
	 */
	public synchronized static List<String> readTxt(String file,String ppStr) {
		List<String> list = new ArrayList<String>();
		try {
			FileReader read = new FileReader(file);
			BufferedReader br = new BufferedReader(read);
			String row = "";
			while ((row = br.readLine()) != null) {
				//必须不为空，并且包含匹配的字符串
				if(StringUtils.isNotBlank(row)){
					if(StringUtils.isNotBlank(ppStr)){
						if(row.contains(ppStr)){
							list.add(row);
						}
					}else{
						list.add(row);
					}
				}
				//System.out.println(row);
			}
			read.close();
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return list;
	}


	/**
	 *
	 * @Title: createTxt
	 * @Description: 写txt文件
	 * @param
	 * @return void
	 * @throws
	 */
	public synchronized static void createTxt(String file, String str) {
		XMLWriter xmlWriter = null;
		FileOutputStream fos = null;
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			format.setExpandEmptyElements(true);
			format.setTrimText(false);
			fos = new FileOutputStream(file);
			xmlWriter = new XMLWriter(fos, format);
			xmlWriter.write(str);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (xmlWriter != null) {
				try {
					xmlWriter.close();
				} catch (IOException e) {
					// tanghui Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// tanghui Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	/**
	 *
	 * @Title: checkArrContainsSoStr
	 * @Description:判断某字符串是否包含某个字符串
	 * @param
	 * @return boolean
	 * @throws
	 */
	public static boolean checkArrContainsSoStr(String strArr,String soStr) {
		boolean b = false;
		if(StringUtils.isNotBlank(strArr) && StringUtils.isNotBlank(soStr)){
			String[] strArray = strArr.split(",");
			for (int i = 0; i < strArray.length; i++) {
				if(strArray[i].toLowerCase().equals(soStr.toLowerCase())){
					b = true;
					break;
				}
			}
		}
		return b;
	}


	/**
	 *
	 * @Title: checkFileIsSaveTo
	 * @Description:判断某文件是否需要进行转换
	 * @param
	 * @return boolean
	 * @throws
	 */
	public static boolean checkFileIsSaveTo(String fileType){
		if(checkArrContainsSoStr(documentFormat,fileType)// 文档类
				|| checkArrContainsSoStr(animaFormat,fileType)// 动画
				|| checkArrContainsSoStr(audioFormat,fileType)// 动画
				|| checkArrContainsSoStr(videoFormat,fileType) // 视频
			){
			return true;
		}else{
			return false;
		}
	}



	/**
	 *
	 * @Title: getDoHahType
	 * @Description:判断某文件是否需要进行转换
	 * @param
	 * @return boolean 0 转换,1 抽封面,2 抽文本,3抽取元数据,4加图片,5水印
	 * @throws
	 */
	public static String getDoHahType(String fileType) {
		String doHahType = "";
		if(!"".equals(fileType)){
			// 文本、视频需要：转换、抽封面、抽文本（除flv/swf文件外）
			if (checkArrContainsSoStr(documentFormat,fileType)
					|| (checkArrContainsSoStr(videoFormat,fileType) && !fileType.equals("flv"))) {
				doHahType = "0,3";
				// flv不用转换，只需要：抽封面、 抽文本
			} else if (fileType.equals("flv")) {
				doHahType = "1,2,3";
				// 音频只需要转换、抽文本
			} else if (checkArrContainsSoStr(audioFormat,fileType) && !fileType.equals("mp3")) {
				doHahType = "0,2,3";
				// mp3、swf只需要抽文本、swf暂时无法抽取封面文件
			} else if (fileType.equals("mp3") || checkArrContainsSoStr(animaFormat,fileType)) {
				doHahType = "2,3";
			}
		}

		return doHahType;
	}
	/**
	 *
	 * @Title: getDoHahType
	 * @Description:判断某文件是否需要进行转换
	 * @param
	 * @return boolean 0 转换,1 抽封面,2 抽文本,3抽取元数据,4加图片,5水印
	 * @throws
	 */
	public static DoFileQueue getConverPath(String converPath,String fileType,DoFileQueue doFile) {
		logger.info("----------------进入doFileUtils--- getConverPath方法---------------");
		converPath = converPath.replaceAll("\\\\", "/");
		if(!"".equals(fileType)){
			// 文本、视频需要：转换、抽封面、抽文本（除flv/swf文件外）
			if (checkArrContainsSoStr(documentFormat,fileType)) {
				logger.info("----------------进入文本---------------");
				doFile.setFileFormat("nswf");
				converPath = converPath.substring(0,converPath.lastIndexOf("/"));
				logger.info("----------------打印文本路径"+converPath+"---------------");
			}else if(checkArrContainsSoStr(videoFormat,fileType) && !fileType.equals("flv")){ //视频资源转换成flv
				logger.info("----------------进入视频flv---------------");
				converPath = converPath.substring(0,converPath.lastIndexOf("."))+".flv";
				logger.info("----------------打印视频路径"+converPath+"---------------");
			} else if (checkArrContainsSoStr(audioFormat,fileType) && !fileType.equals("mp3")) {
				logger.info("----------------进入音频mp3---------------");
				converPath = converPath.substring(0,converPath.lastIndexOf("."))+".mp3";
				logger.info("----------------打印音频路径"+converPath+"---------------");
			} 
		}
		doFile.setResultConveredfilePath(converPath);
		logger.info("----------------退出doFileUtils--- getConverPath方法---------------");
		return doFile;
	}
	/**
	 *
	 * @Title: checkFileHasChange
	 * @Description:判断某文件是否需要进行转换
	 * @param
	 * @return boolean
	 * @throws
	 */
	public static boolean checkFileHasChange(String fileType) {
		if (checkArrContainsSoStr(documentFormat,fileType) // 文档类
				|| (!fileType.equals("mp3") && checkArrContainsSoStr(audioFormat,fileType)) // 音频
				|| (!fileType.equals("flv") && checkArrContainsSoStr(videoFormat,fileType))// 视频
		) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 *
	 * @Title: main
	 * @Description: test
	 * @param
	 * @return void
	 * @throws
	 */
	public static void main(String[] args) {
//		String filePath = "D:\\project35\\BSFW\\WebRoot\\fileDir\\fileRoot\\TB\\T06\\G00001\\hsjc_TB_K_V15_XB-_SL5_T06_F10_\\新中版测试数据\\尼金斯基手记\\txt\\[失控：全人类的最终命运和结局].（美）凯文·凯利着.影印版.pdf";
//		System.out.println(getFileCharset(filePath));
		// 生成一个Pattern,同时编译一个正则表达式 ,判断是否存在swf文件，如果没有则说明未转换成功。
//		String tarPath = "D:\\conver";
//		Pattern p = Pattern.compile(".+\\.(swf)$");
//		ArrayList<File> files = DoFileUtils.filePattern(new File(tarPath),p);
//		if(files == null || files.size() <= 0){
//			System.out.println("--------1-----");
//		}else{
//			System.out.println("--------2-----");
//		}
		createTxt("E:/1/jsonLog/2.txt", "");
	}


}
