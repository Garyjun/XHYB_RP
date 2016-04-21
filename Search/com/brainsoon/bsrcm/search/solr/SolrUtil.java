package com.brainsoon.bsrcm.search.solr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

public class SolrUtil {
	
	private static final Logger logger = Logger.getLogger(SolrUtil.class);
	
	
	public static void main(String[] args) throws Exception  {
//		logger.info("______over_______" + pdfTextStripper("D:\\cdp\\287815_____原文件_248__北大清华学生求职故事.pdf"));
		try {
		  String sssString = Html2Text(FileUtils.readFileToString(new File("D:\\channelSpace\\bres\\type15\\30\\20120327195456\\xml\\chapter009.xml"), "UTF-8"));
		  
		  Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		  
		  Matcher m = p.matcher(sssString);
		  
		  String after = m.replaceAll("");

		}catch (Exception e) {
			
		}
		
	}
	/**
	 * 分割后文件命名 规则：
	 */
	public static String getName(int n) {
		String name = String.valueOf(n);
		int len = 5 - name.length();
		for (int i = 0; i < len; i++) {
			name = "0" + name;
		}
		return name;
	}
	/**
	 * pdf是否可以提取出文本
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static boolean isCanPdfToTXT(String source ) throws Exception {
		
		boolean flag = true;
		
		PDDocument document = null;
		
		String temp = new File(source).getParent() + File.separator + "temp" ;
		
		try {
			
			int count = 0;
			int pageCnt = splitPDF50(source);
			
			PDFTextStripper stripper = new PDFTextStripper("UTF-8");
			
			stripper.setSortByPosition(false);
			
			for(int i=0;i<pageCnt && (i<50);i++) {
				document = PDDocument.load( temp + File.separator + getName(i+1)+ ".pdf");
				//logger.info("页"+i+"文本：" +stripper.getText(document).length());
				int txtLen = stripper.getText(document).length();
				 // 单页小于10个字符，进行统计
				if(txtLen <10) count ++;
			}
			
			if(pageCnt<50) {
				//如果实际页数就于50，则如果实际页数的一半都小于10个字符，由返回false;
				int midlPageCnt = pageCnt/2;
				if(count > midlPageCnt) {
					flag = false;
				}
			} else {
				 //总页数为50，统计结果中有25页都小于10个字符，则返回flase
				if(count > 25) flag = false;
			}
		}catch(Exception ex) {
			logger.error(ex);
		}finally {
			try {
				if (document != null) {
					document.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
			try {
	        	
	        	FileUtils.deleteDirectory(new File(temp));
	        	new File(temp).delete();
			}catch( IOException ioe) {
				logger.error("删除临时目录失败："+temp);
			}
		}
		return flag;
	}
	/**
	* 创建目录
	* @param destDirName 目标目录名
	* @return 目录创建成功返回true，否则返回false
	*/
	public static boolean createDir(String destDirName) {
	   File dir = new File(destDirName);
	   if(dir.exists()) {
		   return false;
	   }
	   if(!destDirName.endsWith(File.separator))
		   destDirName = destDirName + File.separator;
	   // 创建单个目录
	   if(dir.mkdirs()) {
		   return true;
	   } else {
		   return false;
	   }
	}
	/**
	 * 按页拆分PDF的前50页
	 * 
	 * @return 返回总
	 * @throws Exception
	 */
	public static int splitPDF50(String source) throws Exception {
		
		String pdfPath = new File(source).getParent() + File.separator + "temp" ;
		createDir(pdfPath);
		/* 读取总的PDF文件 */
		PdfReader reader = new PdfReader(source);  
        int pageCnt = reader.getNumberOfPages();  
        for (int i = 0; i < pageCnt && (i<50); i++) {  
        	com.itextpdf.text.Document document = new  com.itextpdf.text.Document(reader.getPageSizeWithRotation(i+1));  
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(pdfPath + File.separator + getName(i+1)+ ".pdf"));
            copy.setCompressionLevel(9);
            copy.setFullCompression();
            document.open();  
            PdfImportedPage page = copy.getImportedPage(reader, i+1);
            copy.addPage(page);  
            document.close();  
        } 
        reader.close();
        return pageCnt;
	}
	public static boolean pdfTextStripper( String source ,String txtPath) {
		
		 // 文件名称，包括格式
		String fileNameSource = new File(source).getName();
		 // 原文件名称 没有格式名称
		String fileName = fileNameSource.substring(0,fileNameSource.lastIndexOf("."));
		 // 文件的类型
		String fileType = fileNameSource.substring(fileNameSource.lastIndexOf(".")+1);
		
		if("pdf".equals(fileType)) {
			logger.info("支持pdf文件格式！");
		} else {
			logger.info("不支持非pdf文件格式！");
			return false;
		}
		
		boolean flag = false;
		
		String sourcePath =  new File(source).getParent();
		
		String pdfPath = sourcePath + File.separator + "pdf" + File.separator ;
		
	//	String txtPath = sourcePath + File.separator + fileName +".txt";
		
		PDDocument document = null;
		
		FileWriter fw = null;
		
		try {
			 // 拆分PDF
			String[] fileNames = pdfSplit(source);
			
			 // 如果有该文件则删除，避免增量增加文本
			if(new File(txtPath).isFile()) {
				new File(txtPath).delete();
			}
			 // 写文件
			fw = new FileWriter( txtPath );
			 // 写缓存
			BufferedWriter bw = new BufferedWriter(fw);
			 // 提取文本对象
			PDFTextStripper stripper = new PDFTextStripper("UTF-8");
			stripper.setSortByPosition(false);
			 // 增量提文件
			for(int i=0;i<fileNames.length;i++) {
				 // 加载pdf文件
				document = PDDocument.load(pdfPath + fileNames[i] +".pdf");
				 // 放入缓存
				bw.write(stripper.getText(document));
			}
			 // 开始写文件
			bw.flush();
			 // 关闭缓存
			bw.close();
			flag = true;
		}catch(Exception ex) {
			logger.error(ex);
		} finally {
			try {
				if (document != null) {
					document.close();
				}
				if(fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
			try {
				FileUtils.deleteDirectory(new File(pdfPath));
				new File(pdfPath).delete();
				// 如果提取文件失败，则删除文本文件
				if(!flag) {
					new File(txtPath).delete();
				}
			}catch( IOException ioe) {
				logger.error("删除临时目录失败："+pdfPath);
			}
		}
		
		return flag;
	}
	
	/**
	 * xml去掉特殊字符
	 * @param source
	 * @return
	 */
	public static boolean xml2Text(String source,String txtPath) {
		
		 // 文件名称，包括格式
	//	String fileNameSource = new File(source).getName();
		 // 原文件名称 没有格式名称
	//	String fileName = fileNameSource.substring(0,fileNameSource.lastIndexOf("."));
		
//		String sourcePath =  new File(source).getParent();
		
	//	String txtPath = sourcePath + File.separator + fileName +".txt";
		try {
			String fileContent = FileUtils.readFileToString(new File(source), "UTF-8");
			
			String fileTxt = Html2Text(fileContent);
			if(StringUtils.isNotEmpty(fileTxt.trim()) && fileTxt.length()>1) {
				FileUtils.writeStringToFile(new File(txtPath), fileTxt, "UTF-8");
			} else {
				return false;
			}
		} catch (IOException ie) {
			logger.error(ie);
			return false;
		}
		return true;
	}
	
	/**
	 * PDF文件拆分
	 * @param source pdf原文件路径
	 * @param target 图片保存目录
	 * @return
	 */
	public static String[] pdfSplit( String source ) throws Exception {
		
		String fileName = new File(source).getName();
		fileName = fileName.substring(0,fileName.lastIndexOf("."));
		
		String pdfPath = new File(source).getParent() + File.separator + "pdf" + File.separator;
		
		 // pages文件夹路径 
		File pagesDirectory = new File(pdfPath);
		
		 // 创建pages文件夹 
		if (!pagesDirectory.exists()) {
			pagesDirectory.mkdir();
		}
		 // 文件的页数
		int pageNum = 20;
		
		try {
			
			  // 读取总的PDF文件 
	        PdfReader reader = new PdfReader(source);
	        
	         // 总文件的页数
	        int pageTotalNum = reader.getNumberOfPages();
	        
	         // 拆分的文件数
	        int fileNum = pageTotalNum/pageNum;
	         // 文件余下的页数
	        int fileRemainPage = pageTotalNum%pageNum;
	         // 如果文件余下的页数不为零，则在增加一页
	        if(fileRemainPage != 0) {
	        	fileNum = fileNum + 1;
	        }
	         // 文件名的数组
	        String[] fileNameArr = new String[fileNum];
	         // 按规则设置文件名的数组 
	        for(int i=0;i<fileNum;i++) {
	        	fileNameArr[i] = fileName + "_" + i;
	        	//logger.debug("拆分后的文件名为：" + fileNameArr[i]);
	        	
	        	 // 开始页码
	        	int startPageNum = i*pageNum;
	        	 // 结束页码
	        	int endPageNum = (i+1)*pageNum;
	        	 //如果文件余下的页数不为零，则修改页码
	        	if(fileRemainPage != 0 && fileNum == (i+1)) {
	        		endPageNum = (startPageNum + fileRemainPage);
	        	}
	        	
	        	com.itextpdf.text.Document documentiText = new  com.itextpdf.text.Document(reader.getPageSizeWithRotation(startPageNum+1));
	        	
	        	PdfCopy copy = new PdfCopy(documentiText, new FileOutputStream(pdfPath + fileNameArr[i] + ".pdf"));
	        	
	        	copy.setCompressionLevel(9);
	            copy.setFullCompression();
	            documentiText.open();
	            
	            for (int j=startPageNum; j < endPageNum; j++) { 
	            	 PdfImportedPage page = copy.getImportedPage(reader, j+1);
			         copy.addPage(page); 
	            }
	             // 写文件
	            documentiText.close();
	        }
	        
	        // 释放资源
	        reader.close();
	        
	        return fileNameArr;
	        
		} catch(Exception ex) {
			
			logger.error("资源加工，拆分pdf文件异常："+ex);
			return null;
		}
	}
	
	/**
	 * 过滤html标签
	 * @param input
	 * @return
	 */
	public static String Html2Text(String input) { 
		
		/* 含html标签的字符串  */
		String htmlStr = input; 
		String textStr =""; 
		java.util.regex.Pattern p_script; 
		java.util.regex.Matcher m_script; 
		java.util.regex.Pattern p_style; 
		java.util.regex.Matcher m_style; 
		java.util.regex.Pattern p_html; 
		java.util.regex.Matcher m_html;
		
		java.util.regex.Pattern p_escape; 
		java.util.regex.Matcher m_escape; 
		
		try { 
			  /* 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> */
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; 
			  /* 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> */
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";  
			  /* 定义HTML标签的正则表达式  */
			String regEx_html = "<[^>]+>"; 
			 /* 定义转义正则表达式*/
			String regEx_escape = "\\s*|\t|\r|\n";
			
			  /* 过滤script标签  */
			p_script = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE); 
			m_script = p_script.matcher(htmlStr); 
			htmlStr  = m_script.replaceAll(""); 

			  /* 过滤style标签  */
			p_style = Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE); 
			m_style = p_style.matcher(htmlStr); 
			htmlStr = m_style.replaceAll(""); 
      
			  /* 过滤html标签  */
			p_html  = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE); 
			m_html  = p_html.matcher(htmlStr); 
          	htmlStr = m_html.replaceAll(""); 
          	 /* 去除字符串中的空格、回车、换行符、制表符转 */
          	p_escape =  Pattern.compile(regEx_escape,Pattern.CASE_INSENSITIVE);
          	m_escape =  p_escape.matcher(htmlStr);
        	htmlStr  = m_escape.replaceAll(""); 
      
          	textStr = htmlStr; 
      
		}catch(Exception e) { 
           logger.error("Html2Text: " + e.getMessage()); 
		} 
		  /* 返回文本字符串  */
		return textStr;
      }   

	/**
	 * 处理CDP格式文件之PDF
	 * @param source
	 */
	public void fileCDPTypeOfPDF( String source ) {
		
	}
	

}
