///**
// * 
// */
//package com.brainsoon.common.util.dofile.view;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Date;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import com.brainsoon.common.util.dofile.conver.PdfToSwfUtil;
//import com.brainsoon.common.util.dofile.util.DoFileException;
//import com.brainsoon.common.util.dofile.util.PropertiesReader;
//
///**
// * 
// * @ClassName: ReadOnlineConverUtils 
// * @Description:  office、pdf在线阅读转换工具类
// * @author tanghui 
// * @date 2013-5-30 上午11:08:55 
// *
// */
//public class ReadOnlineConverUtils {
//	private static Log logger = LogFactory.getLog(ReadOnlineConverUtils.class);
//    public static int createPages;
//
// 
//    /**
//     * @param cmd 命令行
//     * @param thisIndex 开始转换页索引
//     * @param maxPages 最大页数
//     * @param fileTempPath swf临时转换目录
//     * @param pdfFile pdf文件路径
//     * @param fileName 文件名字（即转换后的统一命名，如：xxx_1.swf xxx_2.swf  xxx则为统一命名）
//     * @param createPages  每次转换页数（即每个线程一次性转换多少页数）
//     * @return
//     * @throws IOException
//     */
//    private static boolean initProcess(int thisIndex,
//            int maxPages, String fileTempPath, File pdfFile, String fileName,boolean boo)
//            throws IOException {
//		//转换器路径
//        // cmd.append(PropertiesReader.getInstance().getProperty("swftoolsPath"));
//        //每次转换页数,如果为0，则去配置中读取，否则直接使用传入的页数
//        if(boo){
//        	createPages = maxPages; //全部页面都转换
//        }else{
//        	createPages = Integer.parseInt(PropertiesReader.getInstance().getProperty("atomPages"));
//        }
//        
//        //转换临时目录
//        File parentFile = new File(fileTempPath);
//        if (!parentFile.exists()) {
//            parentFile.mkdirs();
//        }
//        int begin = 1;
//        int end = maxPages;
//        if (thisIndex - createPages > 0) {
//            begin = thisIndex - createPages;
//        }
//        if (thisIndex + createPages < maxPages) {
//            end = thisIndex + createPages;
//        }
//        boolean needPdf2Swf = false;
//        StringBuffer pageNumBuffer = new StringBuffer();
//        for (int i = begin; i <= end; i++) {
//            if (!validateExists(fileName, i, fileTempPath)) {
//                needPdf2Swf = true;
//                pageNumBuffer.append(",").append(i);
//            }
//        }
//        
//        if (needPdf2Swf) {
//        	needPdf2Swf = PdfToSwfUtil.pdf2Swf(pdfFile.getPath(), fileTempPath, pageNumBuffer.toString());
////            cmd.append(" -p ").append(pageNumBuffer.substring(1));
////            if(System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") > -1){
////            	cmd.append(" \"").append(pdfFile.getPath()).append("\"");
////            }else{
////            	cmd.append(" ").append("\"" +StringUtils.replace(pdfFile.getPath()," ","\\ ")+"\"");
////            }
////            cmd.append(" ").append("\"" + parentFile.getPath()).append(File.separatorChar).append(fileName + "_%.swf\"");
////            cmd.append(" -T 6 -f -t  -G -j 25");
////            cmd.append(" -F ").append("\"" + PropertiesReader.getInstance().getProperty("xpdfFontPath")+"\"");
////            cmd.append(" -s languagedir=").append("\"" +PropertiesReader.getInstance().getProperty("xpdfPath")+"\"");
////            cmd.append(" -s poly2bitmap");
////            cmd.append(" -s flashversion=9");
//        }
//        return needPdf2Swf;
//    }
//    
//    /**
//     * 
//     * @Title: pdf2swfProcess 
//     * @Description: 
//     * @param   
//     * @return void 
//     * @throws
//     */
//    public static void pdf2swfProcess(final File pdfFile, int beginIndex,
//            final int maxPages,final String fileTempPath, String fileName)
//            throws IOException, InterruptedException {
//    	//pdf2swfProcess(pdfFile, beginIndex, maxPages, fileTempPath, fileName,false);
//    	initProcess(beginIndex, maxPages, fileTempPath, pdfFile,fileName,false);
//    }
//
//    
//    /**
//     * 
//     * @Title: pdf2swfProcess 
//     * @Description: 
//     * @param   
//     * @return void 
//     * @throws
//     */
//    public static void pdf2swfProcess(final File pdfFile, int beginIndex,
//            final int maxPages,final String fileTempPath, String fileName,boolean boo)
//            throws IOException, InterruptedException,DoFileException {
//    	initProcess(beginIndex, maxPages, fileTempPath, pdfFile,fileName,boo);
////        StringBuffer cmd = new StringBuffer();
////        if (initProcess(cmd, beginIndex, maxPages, fileTempPath, pdfFile,
////        		fileName,boo,true)) {
////        	logger.debug("===========cmd========= " + cmd);
////            Process process = Runtime.getRuntime().exec(cmd.toString());
////            WatchThread wt = new WatchThread(process.getInputStream());
////            WatchThread wtError = new WatchThread(process.getErrorStream());
////            wt.start();
////            wtError.start();
////            process.waitFor();
////            wt.setOver(true);
////            wtError.setOver(true);
////            process.destroy();
////        }
//    }
//
//    /**
//     * @param filePath
//     * @return
//     * @throws IOException
//     */
////    public static int getMaxPages(String filePath) throws Exception{
////        PdfReader reader = null;
////    	File file = new File(filePath);
////    	try {
////    		if(file.exists()){
////        		reader = new PdfReader(filePath);
////                int n = reader.getNumberOfPages();
////                return n;
////        	}else{
////        		logger.error("文件不存在: " + filePath);
////        		throw new FileNotFoundException("文件不存在.");
////        	}
////		}finally{
////			if(reader != null){
////				reader.close();
////			}
////		}
////    	
////    }
//
//    /**
//     * @param filePath
//     * @param thisIndex
//     * @return
//     */
//    public static boolean validateExists(String fileName, int thisIndex, String fileTempPath) {
//        File tempFile = new File(fileTempPath + fileName + "_" + thisIndex + ".swf");
//        if (tempFile.exists()) {
//            tempFile.setLastModified(new Date().getTime());
//            return true;
//        }
//        return false;
//    }
//
//}
