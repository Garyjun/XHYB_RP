package com.brainsoon.common.util.dofile.content;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.brainsoon.common.util.dofile.conver.OfficeToPdfUtils;
import com.brainsoon.common.util.dofile.util.DateTools;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PinyingUtil;

/**
 * 
 * @ClassName: docUtil
 * @Description: doc处理工具类
 * @author tanghui
 * @date 2014-4-23 下午16:12:48
 * 
 */
public class DocUtil {

	private static final Logger logger = Logger.getLogger(DocUtil.class);

	/**
	 * 
	 * @Title: getPdfPageTotalNum
	 * @Description: 获取doc总页数
	 * @param
	 * @return int
	 * @throws
	 */
	public static int getAllPageTotalNum(String docPath) throws IOException {
		int pages = 0;
		try {
			if (StringUtils.isNotBlank(docPath)) {
				// 扩展名
				String extensionName = DoFileUtils.getExtensionName(docPath);
				if (StringUtils.isNotBlank(extensionName)) {
					if (extensionName.toLowerCase().equals("docx")) {// 07版本
						pages = parseDoc2007(docPath);
					} else if (extensionName.toLowerCase().equals("doc")) {// 97版本
						pages = parseDoc97(docPath);
					} else {
						throw new DoFileException("不支持该文件类型。");
					}
				} else {
					throw new DoFileException("未获取到文件扩展名，请检查文件是否正确。");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pages;
	}

	// 07版本(2007)
	public static int parseDoc2007(String docPath) throws IOException {
		int pages = 0;
		try {
			XWPFDocument docx = new XWPFDocument(
					POIXMLDocument.openPackage(docPath));
			pages = docx.getProperties().getExtendedProperties()
					.getUnderlyingProperties().getPages();// 总页数
			// int wordCount = docx.getProperties().getExtendedProperties()
			// .getUnderlyingProperties().getCharacters();// 忽略空格的总字符数
			// 另外还有getCharactersWithSpaces()方法获取带空格的总字数。
			// System.out.println("pages=" + pages + " wordCount=" + wordCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pages;
	}

	// 97版本(2003)
	public static int parseDoc97(String docPath) throws IOException {
		int pages = 0;
		WordExtractor doc = null;
		try {
			doc = new WordExtractor(new FileInputStream(docPath));
			pages = doc.getSummaryInformation().getPageCount();// 总页数
			// int wordCount = doc.getSummaryInformation().getWordCount();// 总字符数
			// System.out.println("pages=" + pages + " wordCount=" + wordCount);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(doc != null){
				doc.close();// 关闭
			}
		}
		
		return pages;
	}

	/**
	 * @throws IOException
	 * 
	 * @Title: parseDoc97
	 * @Description: 抽取97版本doc中起止段落的txt文档内容
	 * @param docPath
	 *            doc绝对路径
	 * @param startParagraphsNum
	 *            起始段
	 * @param endParagraphsNum
	 *            结束段
	 * @return String
	 * @throws
	 */
	public static String parseDoc97(String docPath, int startParagraphsNum,
			int endParagraphsNum) throws IOException {
		String txt = "";
		FileInputStream is = null;
		WordExtractor doc = null;
		try {
			is = new FileInputStream(docPath);
			doc = new WordExtractor(is);
			// String text2003 = doc.getText(); //获取总文本记录
			// 按段落读取
			StringBuffer sb = null;
			String[] paragraphs = doc.getParagraphText();

			int pageTotalNum = paragraphs.length; // 总页数

			if (startParagraphsNum < 1) {
				throw new DoFileException("开始段落{" + startParagraphsNum
						+ "}必须大于等于{1}.");
			}

			if (endParagraphsNum < 1) {
				throw new DoFileException("结束段落{" + endParagraphsNum
						+ "}必须大于等于{1}.");
			}

			if (endParagraphsNum < startParagraphsNum) {
				throw new DoFileException("结束段落{" + endParagraphsNum
						+ "}必须大于等于开始段落{" + startParagraphsNum + "}.");
			}

			if (pageTotalNum < endParagraphsNum) {
				throw new DoFileException("结束段落{" + endParagraphsNum
						+ "}不能大于ppt的总段落数{" + pageTotalNum + "}.");
			}

			int count = 1; // 计数器
			for (String paragraph : paragraphs) {

				if (sb == null) {
					sb = new StringBuffer();
				}

				// 获取文本标题及内容
				// 获取文本标题及内容
				if (startParagraphsNum == count
						|| (startParagraphsNum != endParagraphsNum
								&& endParagraphsNum >= count && count >= startParagraphsNum)) {
					String exTxt = doc.stripFields(paragraph);
					if (StringUtils.isNotBlank(exTxt)) {
						if (!"".equals(exTxt.trim()) && !" ".equals(exTxt)) {
							//exTxt = DoFileUtils.StringFilter(exTxt);
							sb.append(exTxt.trim()).append("\n");
						}
					}
					
					// 如果开始==结束或者结束==计数器
					if (endParagraphsNum == startParagraphsNum || endParagraphsNum == count) {
						break;
					}
				}
				
				count++;
			}

			if (sb != null) {
				txt = sb.toString();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (is != null) {
				is.close();
			}
			if(doc != null){
				doc.close();// 关闭
			}
		}

		return txt;
	}

	
	
	/**
	 * 
	 * @Title: parseDoc97
	 * @Description: 通过页码读取03/97 doc
	 * @param docPath
	 *            doc绝对路径
	 * @param paragraphsNum
	 *            段落序号,从1开始
	 * @return String
	 * @throws
	 */
	public static String parseDoc97(String docPath, int paragraphsNum) throws IOException {
		return parseDoc97(docPath, paragraphsNum, paragraphsNum);
	}
	
	
	/**
	 * @throws IOException
	 * 
	 * @Title: parseDoc2007
	 * @Description: 通过起始段落读取07版本word文本
	 * @param docPath
	 *            doc绝对路径
	 * @param startParagraphsNum
	 *            起始段
	 * @param endParagraphsNum
	 *            结束段
	 * @return String
	 * @throws
	 */
	public static String parseDoc2007(String docPath, int startParagraphsNum,
			int endParagraphsNum) throws IOException {
		String txt = "";
		OPCPackage opcPackage = null;
		try {
			opcPackage = POIXMLDocument.openPackage(docPath);
			XWPFDocument doc = new XWPFDocument(opcPackage);
			List<XWPFParagraph> paras = doc.getParagraphs();
			// 按段落读取
			StringBuffer sb = null;

			int pageTotalNum = paras.size(); // 总段落数

			if (startParagraphsNum < 1) {
				throw new DoFileException("开始段落{" + startParagraphsNum
						+ "}必须大于等于{1}.");
			}

			if (endParagraphsNum < 1) {
				throw new DoFileException("结束段落{" + endParagraphsNum
						+ "}必须大于等于{1}.");
			}

			if (endParagraphsNum < startParagraphsNum) {
				throw new DoFileException("结束段落{" + endParagraphsNum
						+ "}必须大于等于开始段落{" + startParagraphsNum + "}.");
			}

			if (pageTotalNum < endParagraphsNum) {
				throw new DoFileException("结束段落{" + endParagraphsNum
						+ "}不能大于ppt的总段落数{" + pageTotalNum + "}.");
			}

			for (int i = 0; i < paras.size(); i++) {

				if (sb == null) {
					sb = new StringBuffer();
				}
				int count = i+1;
				// 获取文本标题及内容
				if (startParagraphsNum == count
						|| (startParagraphsNum != endParagraphsNum
								&& endParagraphsNum >= count && count >= startParagraphsNum)) {
					sb.append(paras.get(i).getText()).append("\n");
					
					// 如果开始==结束或者结束==计数器
					if (endParagraphsNum == startParagraphsNum || endParagraphsNum == count) {
						break;
					}
				}
			}

			if (sb != null) {
				txt = sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(opcPackage != null){
				opcPackage.close();
			}
		}
		

		return txt;
	}
	
	
	
	/**
	 * 
	 * @Title: parseDoc2007
	 * @Description: 通过段落索引读取07 doc
	 * @param docPath
	 *            doc绝对路径
	 * @param paragraphsNum
	 *            段落序号,从1开始
	 * @return String
	 * @throws
	 */
	public static String parseDoc2007(String docPath, int paragraphsNum) throws IOException {
		return parseDoc2007(docPath, paragraphsNum, paragraphsNum);
	}

	
	/**
	 * 
	 * @Title: parseDocFromParagraphNum
	 * @Description: 通过起始段落读取word文本
	 * @param docPath
	 *            doc绝对路径
	 * @param startParagraphsNum
	 *            起始段
	 * @param endParagraphsNum
	 *            结束段
	 * @return String
	 * @throws
	 */
	public static String parseDocFromParagraphNum(String docPath, int startParagraphsNum,
			int endParagraphsNum) throws IOException {
		String txt = "";
		try {
			if (StringUtils.isNotBlank(docPath)) {
				// 扩展名
				String extensionName = DoFileUtils.getExtensionName(docPath);
				if (StringUtils.isNotBlank(extensionName)) {
					if (extensionName.toLowerCase().equals("docx")) {// 07版本
						txt = parseDoc2007(docPath,startParagraphsNum,endParagraphsNum);
					} else if (extensionName.toLowerCase().equals("doc")) {// 97版本
						txt = parseDoc97(docPath,startParagraphsNum,endParagraphsNum);
					} else {
						throw new DoFileException("不支持该文件类型。");
					}
				} else {
					throw new DoFileException("未获取到文件扩展名，请检查文件是否正确。");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txt;
	}
	
	
	
	/**
	 * 
	 * @Title: parseDocFromParagraphNum
	 * @Description: 通过段落索引读取word文本
	 * @param docPath
	 *            doc绝对路径
	 * @param paragraphsNum
	 *            段落序号，从1开始
	 * @return String
	 * @throws
	 */
	public static String parseDocFromParagraphNum(String docPath, int paragraphsNum) throws IOException {
		String txt = "";
		try {
			if (StringUtils.isNotBlank(docPath)) {
				// 扩展名
				String extensionName = DoFileUtils.getExtensionName(docPath);
				if (StringUtils.isNotBlank(extensionName)) {
					if (extensionName.toLowerCase().equals("docx")) {// 07版本
						txt = parseDoc2007(docPath,paragraphsNum);
					} else if (extensionName.toLowerCase().equals("doc")) {// 97版本
						txt = parseDoc97(docPath,paragraphsNum);
					} else {
						throw new DoFileException("不支持该文件类型。");
					}
				} else {
					throw new DoFileException("未获取到文件扩展名，请检查文件是否正确。");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txt;
	}
	
	

	/**
	 * @throws Exception 
	 * 
	 * @Title: parseDocFromPage
	 * @Description: 通过页码索引读取word文本
	 * @param docPath
	 *            doc绝对路径
	 * @param startPageNum
	 *            起始页码,从第一页开始
	 * @param endPageNum
	 *            结束页码
	 * @return String
	 * @throws
	 */
	public static String parseDocFromPage(String docPath,int startPageNum, int endPageNum) throws Exception {
		//第一步：转换doc为pdf,将其做为临时文件放置到指定位置
		String outPdfTempFile = parseDoc2Pdf(docPath);
		//第二步：获取文本
		return PdfUtil.parsePdf(outPdfTempFile, startPageNum, endPageNum);
	}
	
	/**
	 * @throws Exception 
	 * 
	 * @Title: parseDocFromPage
	 * @Description: 通过页码索引读取word文本
	 * @param docPath
	 *            doc绝对路径
	 * @param pageNum
	 *            页码序号，从1开始
	 * @return String
	 * @throws
	 */
	public static String parseDocFromPage(String docPath, int pageNum) throws Exception {
		//第一步：转换doc为pdf,将其做为临时文件放置到指定位置
		String outPdfTempFile = parseDoc2Pdf(docPath);
		//第二步：获取文本
		return PdfUtil.parsePdf(outPdfTempFile, pageNum);
	}
	
	
	/**
	 * @throws Exception 
	 * 
	 * @Title: parseDoc2Pdf
	 * @Description: 转换doc到pdf
	 * @param docPath
	 *            doc绝对路径
	 * @return String
	 * @throws
	 */
	public static String parseDoc2Pdf(String docPath) throws Exception {
		//获取不带扩展名的文件名
	    String fileName = DoFileUtils.getFileNameNoEx(docPath); 
		String outPdfTempFile = DoFileUtils.getFileConverTempDir() + "doc2pdf/" + PinyingUtil.spellNoneTone(fileName) + ".pdf";
		//转换doc为pdf,将其做为临时文件放置到指定位置
		OfficeToPdfUtils.convertToPdf(docPath,outPdfTempFile);
		return outPdfTempFile;
	}

	/**
	 * 
	 * @Title: addImgMark
	 * @Description: 给doc文件添加图片水印
	 * 
	 * @param docPath
	 *            要加水印的原doc文件路径
	 * @param outdocFile
	 *            加了水印后要输出的路径
	 * @param markImagePath
	 *            水印图片绝对路径
	 * @return void
	 * @throws
	 */
	public static void addImgMark(String docPath, String outdocFile,
			String markImagePath) throws Exception {

	}

	/**
	 * 
	 * @Title: addTxtMark
	 * @Description: 给doc文件添加文字水印
	 * 
	 * @param docPath
	 *            要加水印的原doc文件路径
	 * @param outdocFile
	 *            加了水印后要输出的路径
	 * @param markTxt
	 *            水印文字
	 * @return void
	 * @throws
	 */
	public static void addTxtMark(String docPath, String outdocFile,
			String markTxt) throws Exception {

	}

	/**
	 * @throws Exception 
	 * 
	 * @Title: docToImage
	 * @Description:doc将某页转换为图片
	 * @param docPath
	 *            要转换成图片的doc/docx文件路径
	 * @param outImageFile
	 *            转换后的图片路径
	 * @param pageNum
	 *            要转换的doc/docx页码
	 * @return void
	 * @throws
	 */
	public static void docToImage(String docPath, String outImageFile, int pageNum) throws Exception {
		//第一步：转换doc为pdf,将其做为临时文件放置到指定位置
		String outPdfTempFile = parseDoc2Pdf(docPath);
		//第二步：生成缩略图
		PdfUtil.pdfToImage(outPdfTempFile, outImageFile, pageNum);
		//第三步：删除临时文件,暂时不删除，等自动清理,以备不停地抽取图片之用
		//FileUtils.forceDelete(new File(outPdfTempFile));
	}
	

	/**
	 * 
	 * @Title: main
	 * @Description: test
	 * @param
	 * @return void
	 * @throws
	 */
	public static void main(String[] args) throws Exception {
		//测试开始
		long ss = DateTools.getStartTime();
		String filename1 = "D:/资源管理平台/中版集团/需求/内容资源管理系统客户化定制需求规格说明_基线版.docx";
		String filename2 = "D:/资源管理平台/华师京城/文档/1341STC40686-02：附件10：技术及服务方案-tanghui-v0.1.doc";
//		String outfilename = "D:/Project素材/video/22.doc";
//		String markImagePath = "D:/Project素材/video/11.png";
		String docImage = "D:/资源管理平台/华师京城/文档/服务支撑子系统接口调用说明文档.png";
//		System.out.println("====文件总页数为：" + getAllPageTotalNum(filename2));
//		System.out.println("97版本txt为：\n" + parseDoc97(filename2, 33, 36));
//		System.out.println("07版本txt为：\n" + parseDoc2007(filename1, 41, 44));
		System.out.println("===realust====" + parseDocFromPage(filename2, 1,2));
		// 添加图片水印
		// addPdfImgMark(filename, outfilename, markImagePath);
		// 添加文字水印
		// addPdfTxtMark(filename, outfilename, "www.dajianet.com");
		// PDF将某页转换为图片
		//docToImage(filename2, docImage, 2);
		 //测试结束
      	DateTools.getTotaltime(ss);
	}
}
