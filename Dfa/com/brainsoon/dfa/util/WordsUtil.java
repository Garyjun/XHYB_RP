package com.brainsoon.dfa.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.tika.Tika;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;

import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.dofile.code.Epub2Html;
import com.brainsoon.dfa.service.IWordsService;
import com.brainsoon.resource.util.StringFileToolkit;


/**
 * 
 * @ClassName: WordsUtil 
 * @Description:  敏感词过滤UTIL
 * @author tanghui 
 * @date 2014-1-7 下午2:24:58 
 *
 */
public class WordsUtil {
	private static final Log logger = LogFactory.getLog(WordsUtil.class);
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		List<String> keywordList = new ArrayList();
		keywordList.add("混蛋");
		keywordList.add("三国");
		try {
			File file = new File("E:/2.xml");
			String encode = Epub2Html.getFileCharsetByPath("E:/2.xml");
			if (!"UTF-8".equalsIgnoreCase(encode)) {
				encode = "GBK";
			}
			DFA dfa = new DFA(encode);
			dfa.createKeywordTree(keywordList);
			//System.out.println("文件编码="+Epub2Html.getFileCharsetByPath("E:/1.txt"));
			
			BufferedReader br = null;
			StringBuffer sb = new StringBuffer();
			try {
				// 从文件读取到内存
				FileInputStream fin = new java.io.FileInputStream(file);
				Document doc = null;
				SAXReader reader = new SAXReader();
				try {
					doc = reader.read(file);
				} catch (DocumentException e) {
					logger.error(e);
				}
				br = new java.io.BufferedReader(new java.io.InputStreamReader(
						fin, doc.getXMLEncoding()));
				String line = br.readLine();
				while ((line = br.readLine()) != null) {
					// 过滤敏感词
					String w = dfa.searchKeyword(line);
					if (w.length() != 0) {
						if (sb.length() == 0)
							sb.append(w);
						else
							sb.append(":").append(w);
					}
				}
			} catch (Exception e) {
				logger.error(e);
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
	}

	
	
	/**
	 * 敏感词检测接口
	 * 
	 * @param path 文件路径
	 * @param levels 应用的敏感词等级，为空则是默认级别
	 * @return 返回检测中存在的敏感词数组
	 * @throws IOException
	 * @author zhanglei
	 * @date 2011-10-24下午01:33:03
	 */
	public static String[] checkWords(String path, String[] levels)
			throws IOException {
		if (levels == null) {// 默认敏感词过滤级别是所有
			levels = new String[] { "1", "2", "3" };
		}
		StringBuffer sb = new StringBuffer();
		IWordsService wordsService;
		try {
			wordsService = (IWordsService) BeanFactoryUtil.getBean("wordsService");
			DFA dfa = new DFA();
			dfa.createKeywordTree(wordsService.getWordsByLevel(levels));
			String mimeType = new Tika().detect(path);
			String hz = ""; //后缀名
			if(path.indexOf(".")!= -1){
				hz = path.substring(path.lastIndexOf(".")+1, path.length());
			}
			if ((StringUtils.isNotBlank(hz) && hz.toLowerCase().equals("doc")) || mimeType.equals("application/msword")) { //word 03
				try {
					return dfa.searchKeyword(ResFileReader.readWord(path)).split(":");
				} catch (Exception e) {
					logger.error(e);
				}
				return null;
			}else if ((StringUtils.isNotBlank(hz) && hz.toLowerCase().equals("docx")) || mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){//word 07
				try {
					return dfa.searchKeyword(ResFileReader.readWord07(path)).split(":");
				} catch (Exception e) {
					logger.error(e);
				}
				return null;
			} else if ((StringUtils.isNotBlank(hz) && hz.toLowerCase().equals("pdf")) || mimeType.equals("application/pdf")) {
				try {
					path = ResFileReader.readFdf(path);
				} catch (Exception e) {
					logger.error(e);
					return null;
				}
			}
			File file = new File(path);
			FileInputStream fis = null;
			try {
				// 从文件读取到内存
				fis = new FileInputStream(file);
				// 定义字节数组，以1024为单位读取，可改
				byte[] bs = new byte[1024];
				// 得到实际读取到的字节数，因为事先不知道文件大小
				// 当为-1的时候跳出死循环
				int n = 0;
				while ((n = fis.read(bs)) != -1) {
					// 过滤敏感词
					String w = dfa.searchKeyword(bs);
					if (w.length() != 0) {
						if (sb.length() == 0)
							sb.append(w);
						else
							sb.append(":").append(w);
					}
					// 把字节转成String 这样可以在控制台输出
					// String s = new String(bs, 0, n);
					// 可以看看read帮助文档,n代表实际读取
					// System.out.println(s);
				}
			} catch (Exception e) {
				logger.error(e);
			} finally {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sb.toString().split(":");
	}
	
	

	/**
	 * 返回带页数的敏感词结果 --目前只支持PDF分页处理
	 * 
	 * @param path
	 * @param levels
	 * @return Map<Integer, String[]> key:当前页数 value:当前页存在的敏感词数组
	 * @author zhanglei
	 * @throws Exception 
	 * @date 2011-11-18下午01:37:23
	 */
	public static Map<Integer, String[]> checkWordsByPage(String path,
			String[] levels) throws Exception {
		if (levels == null) {// 默认敏感词过滤级别是所有
			levels = new String[] { "1", "2", "3" };
		}
		DFA dfa = new DFA();
		IWordsService wordsService = (IWordsService) BeanFactoryUtil.getBean("wordsService");
		//获取与选择级别相同的所有敏感词,将敏感词放到树节点上
		dfa.createKeywordTree(wordsService.getWordsByLevel(levels));
		
		String mimeType = new Tika().detect(path);
		String hz = ""; //后缀名
		if(path.indexOf(".")!= -1){
		   hz = path.substring(path.lastIndexOf(".")+1, path.length());
		}
		
		Map<Integer, String[]> map = new HashMap<Integer, String[]>();
		if ((StringUtils.isNotBlank(hz) && hz.toLowerCase().equals("doc")) || mimeType.equals("application/msword")) { //word 03
			try {
				map.put(1, dfa.searchKeyword(ResFileReader.readWord(path)).split(":"));
				return map;
			} catch (Exception e) {
				logger.error(e);
			}
			return null;
		}else if ((StringUtils.isNotBlank(hz) && hz.toLowerCase().equals("docx")) || mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){//word 07
			try {
				map.put(1, dfa.searchKeyword(ResFileReader.readWord07(path)).split(":"));
				return map;
			} catch (Exception e) {
				logger.error(e);
			}
			return null;
		} else if ((StringUtils.isNotBlank(hz) && hz.toLowerCase().equals("pdf")) || mimeType.equals("application/pdf")) {
			return checkPdfWordsByPage(path, levels);
		} 
		
//		else if ((StringUtils.isNotBlank(hz) && hz.toLowerCase().equals("xml")) || mimeType.equals("application/xml")) {// 解析XML 文件
//			return checkXmlWords(path, levels);
//		}
		String encode = Epub2Html.getFileCharsetByPath(path);
		if (!"UTF-8".equalsIgnoreCase(encode)) {
			encode = "GBK";
		}
		dfa = new DFA(encode);
		dfa.createKeywordTree(wordsService.getWordsByLevel(levels));
		StringBuffer sb = new StringBuffer();
		FileInputStream fInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader in = null;
		try {
			fInputStream = new FileInputStream(path);
			logger.info("----------------------------path"+path+"-----------------------------");
			inputStreamReader = new InputStreamReader(fInputStream, encode);
			in = new BufferedReader(inputStreamReader);
			int n = 0;
			char[] charBuffer = new char[1024];
			while ((n = in.read(charBuffer)) != -1) {
				String w1 = new String(charBuffer);
				logger.info("----------------------------w1"+w1+"-----------------------------");
				byte[] byteArray = w1.getBytes();
				logger.info("----------------------------byteArray"+byteArray.length+"-----------------------------");
				// 过滤敏感词
				String w = dfa.searchKeyword(byteArray);
				logger.info("----------------------------获得到过滤值w"+w+"-----------------------------");
				if (w.length() != 0) {
					if (sb.length() == 0)
						sb.append(w);
					else
						sb.append(":").append(w);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (null != fInputStream) {
					fInputStream.close();
				}
				if (null != inputStreamReader) {
					inputStreamReader.close();
				}
				if (null != in) {
					in.close();
					in = null;
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
		map.put(1, sb.toString().split(":"));
		return map;
	}

	/**
	 * PDF文件按页过滤敏感词返回
	 * 
	 * @param path
	 * @param levels
	 * @return
	 * @author zhanglei
	 * @throws Exception 
	 * @date 2011-11-18上午11:37:01
	 */
	public static Map<Integer, String[]> checkPdfWordsByPage(String path,
			String[] levels) throws Exception {
		boolean sort = false;
		// pdf文件名
		String pdfFile = path;
		// 输入文本文件名称
		String textFile = null;
		// 编码方式
		String encoding = "UTF-8";
		//
		Map<Integer, String[]> map = new HashMap<Integer, String[]>();
		PDDocument document = null;
		IWordsService wordsService = (IWordsService) BeanFactoryUtil.getBean("wordsService");
		DFA dfa = new DFA();
		try {
			dfa.createKeywordTree(wordsService.getWordsByLevel(levels));
			document = PDDocument.load(pdfFile);
			PDFTextStripper stripper = new PDFTextStripper();
			int pageNum = document.getNumberOfPages();
			for (int i = 1; i <= pageNum; i++) {
				stripper.setStartPage(i);
				stripper.setEndPage(i + 1);
				String pdfStr = stripper.getText(document);
				String words = dfa.searchKeyword(pdfStr);
				if (words.length() > 0) {
					map.put(i, words.split(":"));
				}
			}

		} catch (IOException e) {
			logger.error(e);
		}

		return map;
	}

	public static Map<Integer, String[]> checkXmlWords(String path,
			String[] levels) throws Exception {
		Map<Integer, String[]> map = new HashMap<Integer, String[]>();
		String encode = Epub2Html.getFileCharsetByPath(path);
		if (!"UTF-8".equalsIgnoreCase(encode)) {
			encode = "GBK";
		}
		DFA dfa = new DFA(encode);
		IWordsService wordsService = (IWordsService) BeanFactoryUtil.getBean("wordsService");
		try {
			dfa.createKeywordTree(wordsService.getWordsByLevel(levels));
			File file = new File(path);
			BufferedReader br = null;
			StringBuffer sb = new StringBuffer();
			try {
				// 从文件读取到内存
				FileInputStream fin = new java.io.FileInputStream(file);
				Document doc = null;
				SAXReader reader = new SAXReader();
//				 String xmla = StringFileToolkit.file2String(new File(path),"UTF-8"); 
//				 byte[] b = xmla.getBytes("UTF-8"); 
//				 String xml = new String(b,3,b.length-3,"UTF-8");
				try {
					doc = reader.read(file);
//					 doc =  DocumentHelper.parseText(xmla);
				} catch (DocumentException e) {
					logger.error(e);
				}
				br = new java.io.BufferedReader(new java.io.InputStreamReader(
						fin, doc.getXMLEncoding()));
				String line = br.readLine();
				while ((line = br.readLine()) != null) {
					// 过滤敏感词
					String w = dfa.searchKeyword(line);
					if (w.length() != 0) {
						if (sb.length() == 0)
							sb.append(w);
						else
							sb.append(":").append(w);
					}
				}
				map.put(1, sb.toString().split(":"));
			} catch (Exception e) {
				logger.error(e);
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		return map;
	}
}
