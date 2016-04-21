//package com.brainsoon.common.util.dofile.conver;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.net.ConnectException;
//
//import org.apache.log4j.Logger;
//
//import com.itextpdf.text.DocumentException;
//
///**
// * 
// * @ClassName: PdfToOfficeUtils
// * @Description: 将 pdf转成 office文件通过 openoffice 工具转换为
// * @author tanghui
// * @date 2014-7-11 上午10:31:21
// * 
// */
//public class PdfToOfficeUtils {
//	private static final Logger logger = Logger.getLogger(OfficeToHtmlUtils.class);
//	
//	
//	/**
//	 * @throws ConnectException 
//	 * 
//	 * @Title: pdf2Office 
//	 * @Description: 将文档转换为pdf格式 可转换doc,xls
//	 * @param inputFileName 转换源文件，全路径
//	 * @param outPutFileName 转换目的文件，全路径
//	 * @return 是否成功   
//	 * @throws
//	 */
//	private static boolean pdf2Office(File inputFile, File outputFile) throws ConnectException {
//		 File file1 = new File("G:\\chen\\培训课件\\chen.doc");
//
//	        FileOutputStream Fout = new FileOutputStream(file1);
//	        BufferedOutputStream Bout = new BufferedOutputStream(Fout);
//	        OutputStreamWriter write = new OutputStreamWriter(Bout, "UTF-8");
//
//
//	        File file2 = new File("G:\\chen\\培训课件\\Core-Java-第一册.pdf");
//	        FileInputStream Fin = new FileInputStream(file2);
//	        BufferedInputStream Bin = new BufferedInputStream(Fin);
//	        InputStreamReader iread = new InputStreamReader(Bin, "UTF-8");
//
//
//	        char[] buf = new char[1024 * 32];
//	        int length = 0;
//	        while ((length = iread.read(buf)) > -1) {
//	            owrite.write(buf, 0, length);
//	        }
//	        owrite.close();
//	        iread.close();
//
//	    }
//	}
//
//
//		public static void main(String[] args) throws IOException, DocumentException {
//			PdfToOfficeUtils abc = new PdfToOfficeUtils();
//		    abc.pdf2Office();
//		}
//	
//
//}
