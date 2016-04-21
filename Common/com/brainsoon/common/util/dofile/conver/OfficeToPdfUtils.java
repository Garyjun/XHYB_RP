package com.brainsoon.common.util.dofile.conver;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PinyingUtil;
import com.brainsoon.common.util.dofile.util.PropertiesReader;

/**
 * 
 * @ClassName: OfficeToPdfUtils
 * @Description:office系列文档转pdf工具类
 * @author tanghui
 * @date 2014-4-30 上午10:29:15
 * 
 */
public class OfficeToPdfUtils {
	protected static final Logger logger = Logger
			.getLogger(OfficeToPdfUtils.class);
	
	
	
	/**
	 * @throws Exception 
	 * 
	 * @Title: convertOfficeToPdf
	 * @Description: 转换office到pdf
	 * @param officePath office绝对路径
	 * @return String
	 * @throws
	 */
	public static String convertOfficeToPdf(String officePath) throws Exception {
		//获取不带扩展名的文件名
	    String fileName = DoFileUtils.getFileNameNoEx(officePath); 
	    //文件类型
	    String fileType = DoFileUtils.getExtensionName(officePath); 
	    String outPdfTempFile = DoFileUtils.getFileConverTempDir();
	    //判断是否是office系列的，如果是，则需要转换成pdf
	    if("doc".contains(fileType.toLowerCase()) || "docx".contains(fileType.toLowerCase())){
	    	outPdfTempFile += "doc2pdf/";
	    }else if("ppt".contains(fileType.toLowerCase()) || "pptx".contains(fileType.toLowerCase())){
	    	outPdfTempFile += "ppt2pdf/";
	    }else if("xls".contains(fileType.toLowerCase()) || "xlsx".contains(fileType.toLowerCase())){
	    	outPdfTempFile += "xls2pdf/";
	    }else{
	    	outPdfTempFile += "other2pdf/";
	    }
	    outPdfTempFile += PinyingUtil.spellNoneTone(fileName) + ".pdf";
		//转换doc为pdf,将其做为临时文件放置到指定位置
		OfficeToPdfUtils.convertToPdf(officePath,outPdfTempFile);
		//返回转后的pdf临时位置路径
		return outPdfTempFile;
	}
	
	
	/**
	 * 
	 * @Title: convertToPdf 
	 * @Description: office 转 pdf
	 * @param officePath
	 *            office文件绝对路径
	 * @param targetPdfPath
	 *            转换后的pdf绝对路径
	 * @return void 
	 * @throws
	 */
	public static String convertToPdf(String officePath, String targetPdfPath)
			throws Exception {
		OpenOfficeConnection connection = null;
		long begin_time = new Date().getTime();
		File inputFile = null;
		try {
			inputFile = new File(officePath);
			if(!inputFile.exists()){
				throw new DoFileException("office转pdf<执行方法名：convertToPdf>要转换的文件：{" + officePath +"}不存在，请检查.");
			}
			File outputFile = new File(targetPdfPath);
			if(!new File(outputFile.getParent()+"/").exists()){
				DoFileUtils.mkdir(outputFile.getParent()+ "/");
			}
			//如果pdf不存在，则准换，否则不转换
			if(!outputFile.exists()){
				connection = new SocketOpenOfficeConnection(
						Integer.parseInt(PropertiesReader.getInstance()
								.getProperty("openOfficePort"))); //配置端口
				connection.connect(); //开始连接
				DocumentConverter converter = new OpenOfficeDocumentConverter(
						connection);
				DefaultDocumentFormatRegistry formatReg = new DefaultDocumentFormatRegistry();
				DocumentFormat df = formatReg.getFormatByFileExtension("pdf");
				converter.convert(inputFile, outputFile, df);
			}else{
				logger.info("文件已存在，无需转换");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(inputFile != null){
					 long end_time = new Date().getTime();
					 logger.info("转PDF文件{" + inputFile.getName() +"}共耗时：[" + (end_time - begin_time) + "]ms");
				}
				if (connection != null) {
					connection.disconnect(); //关闭连接
					connection = null;
				}
			} catch (Exception e) {
				logger.error("office转pdf<执行方法名：convertToPdf> 失败异常信息：" + e.getMessage());
			}
		}
		return targetPdfPath;
	}

	// test
	public static void main(String[] args) {
		String officePath = "D:/2222/小说欣赏/dafasdfsdf/cvasfsadf/qeqweqwe/ewqrwqrwqe/hfhfdhgsdgs/工作-学习任务安排.xlsx";
		String targetPdfPath = "D:/2222/小说欣赏/dafasdfsdf/cvasfsadf/qeqweqwe/ewqrwqrwqe/hfhfdhgsdgs/工作-学习任务安排.pdf";
		try {
			convertToPdf(officePath, targetPdfPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
