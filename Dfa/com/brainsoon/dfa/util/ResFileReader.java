package com.brainsoon.dfa.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

/**
 * 
 * @ClassName: ResFileReader 
 * @Description:  解析WORD or PDF
 * @author tanghui 
 * @date 2014-1-9 下午1:23:14 
 *
 */
public class ResFileReader {
	private static final Log logger = LogFactory.getLog(ResFileReader.class);
	
	//读取WORD文件 doc 2003
	public static String readWord(String file) throws Exception {
		String bodyText = null;
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			WordExtractor ex = new WordExtractor(is);// is是WORD文件的InputStream
			bodyText = ex.getTextFromPieces();
		} catch (Exception e) {
			logger.error(e);
			throw new Exception("读取word文件失败！",e);
		}finally{
			is.close();
		}
		return bodyText;
	}
	
	//读取WORD文件 doc 2007
	public static String readWord07(String file) throws Exception {
		String text2007 = "";
			try {
				//word 2007 图片不会被读取， 表格中的数据会被放在字符串的最后
				OPCPackage opcPackage = POIXMLDocument.openPackage(file);
				POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
				text2007 = extractor.getText();
				//System.out.println(text2007);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("读取word文件失败！",e);
			}
			return text2007;
	}
	
	
	// 读取PDF文件
	//返回处理后的TXT文件路径
	public static String readFdf(String file) throws Exception {
		// 是否排序
		boolean sort = false;
		// pdf文件名
		String pdfFile = file;
		// 输入文本文件名称
		String textFile = null;
		// 编码方式
		String encoding = "UTF-8";
		// 开始提取页数
		int startPage = 1;
		// 结束提取页数
		int endPage = Integer.MAX_VALUE;
		// 文件输入流，生成文本文件
		Writer output = null;
		// 内存中存储的PDF Document
		PDDocument document = null;
		/*
		 * FileInputStream is = null; COSDocument cosDoc = null; try { is = new
		 * FileInputStream(file); cosDoc = parseDocument(is); } catch
		 * (IOException e) { closeCOSDocument(cosDoc); throw new
		 * Exception("无法处理该PDF文档", e); } if (cosDoc.isEncrypted()) { if (cosDoc
		 * != null) closeCOSDocument(cosDoc); throw new
		 * Exception("该PDF文档是加密文档，无法处理"); }
		 */
		try {
			try {
				// 首先当作一个URL来装载文件，如果得到异常再从本地文件系统//去装载文件
				URL url = new URL(pdfFile);
				// 注意参数已不是以前版本中的URL.而是File。
				document = PDDocument.load(pdfFile);
				
				// 获取PDF的文件名
				String fileName = url.getFile();
				// 以原来PDF的名称来命名新产生的txt文件
				if (fileName.length() > 4) {
					File outputFile = new File(fileName.substring(0, fileName
							.length() - 4)
							+ ".txt");
					textFile = outputFile.getName();
				}
			} catch (MalformedURLException e) {
				// 如果作为URL装载得到异常则从文件系统装载
				// 注意参数已不是以前版本中的URL.而是File。
				document = PDDocument.load(pdfFile);
				if (pdfFile.length() > 4) {
					textFile = pdfFile.substring(0, pdfFile.length() - 4)
							+ ".txt";
				}
			}
			// 文件输入流，写入文件倒textFile
			output = new OutputStreamWriter(new FileOutputStream(textFile),
					encoding);
			// PDFTextStripper来提取文本
			PDFTextStripper stripper = null;
			stripper = new PDFTextStripper();
			// 设置是否排序
			stripper.setSortByPosition(sort);
			// 设置起始页
			stripper.setStartPage(startPage);
			// 设置结束页
			stripper.setEndPage(endPage);
			// 调用PDFTextStripper的writeText提取并输出文本
			stripper.writeText(document, output);
			return textFile;
		} finally {
			if (output != null) {
				// 关闭输出流
				output.close();
			}
			if (document != null) {
				// 关闭PDF Document
				document.close();
			}
		}

	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*ResFileReader pdfReader = new ResFileReader();
		try {
			// 取得E盘下的SpringGuide.pdf的内容
			System.out.println(pdfReader.readFdf("E:\\1.pdf"));
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	    /*try {
			FileInputStream   fis   =   new   FileInputStream("E:/2.pdf"); 
			BufferedWriter writer = new BufferedWriter(new FileWriter("E:/2.txt"));
			PDFParser   p   =   new   PDFParser(fis); 
			p.parse();         
			PDFTextStripper   ts   =   new   PDFTextStripper();         
			String   s   =   ts.getText(p.getPDDocument()); 
			writer.write(s);
			System.out.println(s); 
			fis.close(); 
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		try {
		//	readFdf("E:/1.pdf");
			//readWord("E:/1/word.doc");
			//readWordDocx("E:/1/word.doc");
			//SplitUtil.split("E:/1/word.doc", "E:/1/word");
			SplitUtil.split("E:/1/1.docx", "E:/1/word");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//WordsUtil.checkPdfWordsByPage("E:/1.pdf", null);
		
	}
}