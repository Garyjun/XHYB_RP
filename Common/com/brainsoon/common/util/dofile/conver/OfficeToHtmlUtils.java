package com.brainsoon.common.util.dofile.conver;

import java.io.File;
import java.net.ConnectException;
import org.apache.log4j.Logger;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.PropertiesReader;

/**
 * 
 * @ClassName: OfficeToHtmlUtils
 * @Description: office 转 html
 * @author tanghui
 * @date 2014-5-5 上午10:32:04
 * 
 */
public class OfficeToHtmlUtils {

	private static final Logger logger = Logger.getLogger(OfficeToHtmlUtils.class);

	/**
	 * 
	 * @Title: docToPdfOrHtml
	 * @Description: word文件转换为pdf或html
	 * @param source
	 *            word原文件路径
	 * @param target
	 *            目录文件路径
	 * @return boolean
	 * @throws
	 */
	public static boolean docToPdfOrHtml(String sourceOffceFile,
			String targetHtmlFile) throws Exception {
		boolean flag = false;
		File inputFile = new File(sourceOffceFile);
		if (!inputFile.exists()) {
			throw new DoFileException("要转换的文件：{" + sourceOffceFile
					+ "}不存在，请检查.");
		}
		File outputFile = new File(targetHtmlFile);
		if (!outputFile.exists()) {
			throw new DoFileException("转换后的路径：{" + targetHtmlFile + "}不存在，请检查.");
		}
		// 创建连接对象
		OpenOfficeConnection connection = null;
		try {
			connection = new SocketOpenOfficeConnection(
					Integer.parseInt(PropertiesReader.getInstance()
							.getProperty("openOfficePort"))); // 配置端口
			connection.connect(); // 开始连接
			DocumentConverter converter = new OpenOfficeDocumentConverter(
					connection);
			// 数据转换
			converter.convert(new File(sourceOffceFile), new File(
					targetHtmlFile));
			flag = true;

		} catch (ConnectException e) {
			logger.error("文件转换出错，请检查OpenOffice服务是否启动。" + e);
			throw new ConnectException("Word转换到PDF格式失败！");
		} finally {
			if (connection != null) {
				try {
					connection.disconnect(); // 关闭连接
					connection = null;
				} catch (Exception ex) {
					logger.error("文件转换出错，请检查OpenOffice服务是否启动。" + ex);
					throw new ConnectException("服务未启动，Word转换到PDF格式失败！");
				}
			}
		}
		return flag;
	}

	// test
	public static void main(String[] args) throws Exception {
		// word2003转换为html文件
		System.out.println(docToPdfOrHtml("E:/pdf2/2003.doc", "E:/2003.html"));

		// word2007转换为html文件
		// docToPdfOrHtml("E:/2007.dock","E:/2007.html");

		// word2003转换为pdf文件
		// docToPdfOrHtml("E:/2003.doc","E:/2003.pdf");

		// word2007转换为pdf文件
		// docToPdfOrHtml("E:/pdf2/2007.docx","E:/pdf2/2007.pdf");
	}

}
