package com.brainsoon.common.util.dofile.conver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PinyingUtil;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * 
 * @ClassName: TxtToPDFUtils
 * @Description:将txt等文本文件转为PDF
 * @author tanghui
 * @date 2014-5-15 下午3:53:31
 * 
 */
public class TxtToPdfUtils {
	protected static final Logger logger = Logger
			.getLogger(TxtToPdfUtils.class);

	/**
	 * @throws IOException
	 * @throws DocumentException
	 * 
	 * @Title: generatePDF
	 * @Description: 将目录下的所有(txt)文本文件->pdf
	 * @param
	 * @return void
	 * @throws
	 */
	static void generatePDF(File dir) throws DocumentException, IOException {
		try {
			String[] contents = dir.list();// 将文件目录中的文件提取出来
			for (int i = 0; i < contents.length; i++) {
				String filePath = dir + "\\" + contents[i];
				// 判断是否是文本文件，转换成pdf
				String txtFormat = PropertiesReader.getInstance().getProperty(
						"txtFormat");
				// 文件类型
				String fileType = DoFileUtils.getExtensionName(filePath); // 获取文件扩展名;
				if (txtFormat.toLowerCase().contains(fileType.toLowerCase())) {
					convertToPdf(filePath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws IOException
	 * @throws DocumentException
	 * 
	 * @Title: createPDF
	 * @Description: 生成PDF文件(生成后的pdf临时路径不用指定)
	 * @param filePath
	 *            文本文件路径
	 * @return void
	 * @throws
	 */
	public static String convertToPdf(String filePath)
			throws DocumentException, IOException {
		Document document = null;
		BufferedReader in = null;
		// 获取不带扩展名的文件名
		String fileName = DoFileUtils.getFileNameNoEx(filePath);
		String outPdfTempFile = DoFileUtils.getFileConverTempDir() + "txt2pdf/"
				+ PinyingUtil.spellNoneTone(fileName) + ".pdf";
		try {
			File outputFile = new File(outPdfTempFile);
			// 不存在目录则创建
			if (!new File(outputFile.getParent()+"/").exists()) {
				DoFileUtils.mkdir(outputFile.getParent() +"/");
			}
			// 如果pdf不存在，则准换，否则不转换
			if (!outputFile.exists()) {
				// 首先创建一个字体
				BaseFont bfChinese = BaseFont.createFont("STSong-Light",
						"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
				Font FontChinese = new Font(bfChinese, 12, Font.NORMAL);
				String line = null;
				document = new Document(PageSize.A4, 50, 50, 50, 50);
				//获取编码
				String fileCharset = DoFileUtils.getFileCharset(filePath);
				if(StringUtils.isEmpty(fileCharset)){
					fileCharset = "UTF-8";
				}
				File file = new File(filePath);
				InputStreamReader is = new InputStreamReader(new FileInputStream(file),fileCharset);
			    in = new BufferedReader(is);
				PdfWriter.getInstance(document, new FileOutputStream(new File(
						outPdfTempFile)));
				document.open();
				while ((line = in.readLine()) != null)
					document.add(new Paragraph(12, line, FontChinese));
			}
		} finally {
			try {
				if (document != null) {
					document.close();
				}
				if(in != null){
					in.close();
				}
			} catch (Exception e) {
				logger.error("txt转pdf<执行方法名：convertToPdf> 失败异常信息：" + e.getMessage());
			}
		}
		logger.info("===PDF创建成功！path： " + outPdfTempFile);
		return outPdfTempFile;
	}
	
	
	
	/**
	 * @throws IOException
	 * @throws DocumentException
	 * 
	 * @Title: createPDF
	 * @Description: 生成PDF文件(生成后的pdf临时路径要指定)
	 * @param filePath
	 *            文本文件路径
	 * @return void
	 * @throws
	 */
	public static String convertToPdf(String filePath,String outPdfTempFile)
			throws DocumentException, IOException {
		Document document = null;
		BufferedReader in = null;
		try {
			File outputFile = new File(outPdfTempFile);
			// 不存在目录则创建
			if (!new File(outputFile.getParent()+"/").exists()) {
				DoFileUtils.mkdir(outputFile.getParent() +"/");
			}
			// 如果pdf不存在，则准换，否则不转换
			if (!outputFile.exists()) {
				// 首先创建一个字体
				BaseFont bfChinese = BaseFont.createFont("STSong-Light",
						"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
				Font FontChinese = new Font(bfChinese, 12, Font.NORMAL);
				String line = null;
				document = new Document(PageSize.A4, 50, 50, 50, 50);
				//获取编码
				String fileCharset = DoFileUtils.getFileCharset(filePath);
				if(StringUtils.isEmpty(fileCharset)){
					fileCharset = "UTF-8";
				}
				File file = new File(filePath);
				InputStreamReader is = new InputStreamReader(new FileInputStream(file),fileCharset);
			    in = new BufferedReader(is);
				PdfWriter.getInstance(document, new FileOutputStream(new File(
						outPdfTempFile)));
				document.open();
				while ((line = in.readLine()) != null)
					document.add(new Paragraph(12, line, FontChinese));
			}
		} finally {
			try {
				if (document != null) {
					document.close();
				}
				if(in != null){
					in.close();
				}
			} catch (Exception e) {
				logger.error("txt转pdf<执行方法名：convertToPdf> 失败异常信息：" + e.getMessage());
			}
		}
		return outPdfTempFile;
	}

	// test
	public static void main(String[] args) throws IOException,
			DocumentException {
//		convertToPdf("D:\\project35\\BSFW\\WebRoot\\fileDir\\fileRoot\\TB\\T06\\G00001\\hsjc_TB_K_V15_XB-_SL5_T06_F10_\\新中版测试数据\\尼金斯基手记\\txt\\book_ncx.txt");
		// String sInput = "";
		// try {
		// // 输入
		// InputStreamReader fp = new InputStreamReader(System.in);
		// BufferedReader br = new BufferedReader(fp);
		// sInput = br.readLine();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// String path = sInput;
		// if (args.length != 0) {
		// path = args[0];
		// }
		// File dir = new File(path);
		// System.out.print("path" + path);
		// // 判断是否是有此文件目录
		// if (!dir.isDirectory()) {
		// System.out.println("没有此目录");
		// return;
		// }
		// generatePDF(dir);
	}
}