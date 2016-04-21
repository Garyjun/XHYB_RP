//package com.brainsoon.docviewer.support;
//
//import java.io.File;
//import java.io.FilenameFilter;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.log4j.Logger;
//
//import com.djnet.utils.AddStamp;
//import com.djnet.utils.ConvertSwf;
//import com.djnet.utils.SplitUtil;
//import com.djnet.utils.SystemProperties;
//import com.djnet.utils.WatchThread;
//import com.itextpdf.text.pdf.PdfReader;
//
//public class PDFConvert {
//	private static final Logger logger = Logger.getLogger(PDFConvert.class);
//	
//	private String sourcePath = "";
//    private String fileName = "";
//    
//    private String addWaterMarkPath = "";
//    private String splitPath = "";
//    private String destPath = "";
//    private String imgFilePath = "";
//    private String imgFileCoverPath = "";
//    private String ncxFile = "";
//    private String bookCatalogFile = "";
//    private String ncxNamespace = ""; 
//    
//    private String inputResultPath="";   
//    private String inputSourcePath=""; 
//    
//    private long largeSize=0;
//    private int fileSizeMax =0;
//    
//    private boolean addWaterMarkState = true;
//    
//    public PDFConvert() {
//		// TODO Auto-generated constructor stub
//	}
//    
//	public PDFConvert(String sourcePath,String fileName) {
//		try {
//			this.sourcePath = sourcePath;
//			this.fileName = fileName;
//		} catch (Exception e) {
//			logger.equals(fileName+"init.error");
//		}
//	}
//	
//	public PDFConvert(String sourcePath) {
//		try {
//			this.sourcePath = new File(sourcePath).getParent();
//			this.fileName = new File(sourcePath).getName();
//			init();
//		} catch (Exception e) {
//			logger.equals(fileName+"init.error");
//		}
//	}
//	
//	
//	private void init(){
//		ncxNamespace = SystemProperties.getInstance().getNcxNamespace();
//		inputResultPath = SystemProperties.getInstance().getInputResultPath();
//		inputSourcePath = SystemProperties.getInstance().getInputSourcePath();
//		imgFilePath = SystemProperties.getInstance().getImgFilePath();
//		imgFileCoverPath = SystemProperties.getInstance().getImgFileCoverPath();
//		largeSize = SystemProperties.getInstance().getLargeSize();
//		fileSizeMax = SystemProperties.getInstance().getFileSizeMax();
//		fileSizeMax = SystemProperties.getInstance().getFileSizeMax();
//	}
//	
//	private void setPathParamter(){
//		addWaterMarkPath = sourcePath+File.separatorChar+SystemProperties.getInstance().getAddWaterMarkPath();
//		splitPath = sourcePath+File.separatorChar+SystemProperties.getInstance().getSplitPath();		
//		
//		ncxFile = sourcePath+File.separatorChar+SystemProperties.getInstance().getNcxFile();
//		bookCatalogFile = sourcePath+File.separatorChar+SystemProperties.getInstance().getBookCatalogFile();
//		//ncxNamespace = sourcePath+File.separatorChar+SystemProperties.getInstance().getNcxNamespace();
//		inputResultPath =  sourcePath+File.separatorChar+SystemProperties.getInstance().getInputResultPath();
//		inputSourcePath = sourcePath+File.separatorChar+SystemProperties.getInstance().getInputSourcePath();
//		destPath = sourcePath+File.separatorChar+fileName.substring(0, fileName.lastIndexOf("."))+
//		           SystemProperties.getInstance().getConvertDirectory();
//	}
//	
//	public void generateSwfFile()
//	{
//		try {
////			if(!checkFile()){
////				return; 
////			}
//			//添加水印
//			boolean result = true;
//			result = addWaterMark(sourcePath,fileName,addWaterMarkPath,imgFileCoverPath,imgFilePath);
//			
//			if(!result){
//				logger.error(fileName +"文件转换失败，失败原因：添加水印失败");
//				return;
//			}
//			//PDF文件拆分
//			result = pdfFSplit(addWaterMarkPath,fileName,splitPath);
//			if(!result){
//				logger.error(fileName +"文件转换失败，失败原因：文件拆分失败");
//				return;
//			}
//			//生成swf文件
//			convertSwf(splitPath, destPath);
//			
//			//XmlFileUtil.NcxCvtXml(ncxFile, bookCatalogFile, getPageCount(addWaterMarkPath+File.separatorChar+fileName), ncxNamespace);
//			
//			//renameFlie(sourcePath+File.separatorChar+fileName);		
//			
//			deleteFile(new File(addWaterMarkPath));
//			deleteFile(new File(splitPath));
//			logger.error(fileName +"文件转成功；swf文件路径："+destPath);
//		} catch (Exception e) {
//			e.printStackTrace();
//            logger.error("generateSwfFile error! errorMsg:"+e.getMessage());
//		}
//	}
//	
//	public void generateSwfFile_Large()
//	{
//		try {
////			if(!checkFile()){
////				return; 
////			}
//			//添加水印
//			boolean result = true;
//			
//			//PDF文件拆分
//			result = pdfFSplit(sourcePath,fileName,splitPath);
//			if(!result){
//				logger.error(fileName +"文件转换失败，失败原因：文件拆分失败");
//				return;
//			}
//			
//			File dir = new File(splitPath);
//	         File file[] = dir.listFiles();
//	         for (int i = 0; i < file.length; i++) {
//	             if (!file[i].isFile()){
//	            	 continue;
//	             }
//	             if(i==0){
//	            	 result = addWaterMark(splitPath,file[i].getName(),addWaterMarkPath,imgFileCoverPath); 
//	             }else{
//	            	 result = addWaterMark(splitPath,file[i].getName(),addWaterMarkPath,imgFilePath);
//	             }
//	             if(!result){
//	            	 logger.error(fileName +"文件转换失败，失败原因：添加水印失败");
//	 				return;
//	 			}
//	         }		  
//			
//			//生成swf文件
//			convertSwf(addWaterMarkPath, destPath);
//			
//			//XmlFileUtil.NcxCvtXml(ncxFile, bookCatalogFile, getPageCount(sourcePath+File.separatorChar+fileName), ncxNamespace);
//			
//			//renameFlie(sourcePath+File.separatorChar+fileName);
//		
//			deleteFile(new File(addWaterMarkPath));
//			deleteFile(new File(splitPath));
//			logger.error(fileName +"文件转成功；swf文件路径："+destPath);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("generateSwfFile_Large error! errorMsg:"+e.getMessage());
//		}
//	}
//	
//	public void generateSwfFile(String fileName) throws IOException
//	{
//		File pdfFile = new File(fileName);
//		if(!pdfFile.exists()){
//			logger.error(fileName +"文件转换失败，失败原因：文件不存在");
//			return;
//		}
//		this.fileName = pdfFile.getName();		
//		sourcePath = pdfFile.getParent();
//		setPathParamter();
//		if(isLargeFile(pdfFile)){
//			generateSwfFile_Large();
//		}else{
//			generateSwfFile();
//		}		
//	}
//	
//	
//	private void renameFlie(String fileName){
//		File pdfFile = new File(fileName);
//        String c=pdfFile.getParent();   
//        File mm=new File(pdfFile.getPath()+".tmp");   
//        if(!pdfFile.renameTo(mm))   
//        {   
//        	logger.info(fileName+" rename fail");
//        }
//	}
//	
//	private boolean checkFile(){
//		String checkDir = new File(sourcePath).getParent();
//		File xmlDir = new File(checkDir+File.separatorChar+"xml");
//		if(xmlDir.exists()){
//		   logger.info(fileName+" xml File already exists");
//		   return false	;
//		}
//		return true; 
//	}
//	
//	private void deleteFile(File file){ 
//		FileUtils.forceDelete(file);
//	}
//
//	
//	private boolean addWaterMark(String sourceDir,String sourceFileName,String destDir,String imgCoverPath,String imgPath)
//	{
//		return AddStamp.addWaterMark(sourceDir, sourceFileName,destDir,imgCoverPath,imgPath,addWaterMarkState);
//	}
//	
//	private boolean addWaterMark(String sourceDir,String sourceFileName,String destDir,String imgPath)
//	{
//		return AddStamp.addWaterMark(sourceDir, sourceFileName,destDir,imgPath);
//	}
//	
//	private boolean pdfFSplit(String sourceDir,String sourceFileName, String destDir) throws IOException{
//		try {
//			String sourceFilePath = sourceDir+File.separatorChar+sourceFileName;
//			int pageSize = getPageSize(sourceFilePath);
//			return SplitUtil.split(sourceFilePath, destDir, pageSize);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(fileName+"pdfFSplit error");
//			return false;
//		}
//		
//	}
//	
//	private void convertSwf(String sourceDir, String destDir) throws IOException, InterruptedException{
//		ConvertSwf.pdf2swfProcess(sourceDir, destDir); 
//	}
//	
//	private boolean isLargeFile(File pdfFile) throws IOException{
//        long fileSize = pdfFile.length();
//        
//        if(fileSize>largeSize){
//            return true;	
//        }
//        return false;
//	}
//	
//	private int getPageSize(String flieName) throws IOException{
//		int result = 1;
//		PdfReader reader = new PdfReader(flieName);       
//        int total = reader.getNumberOfPages() + 1;
//        int fileSize = reader.getFileLength();
//        int pageSize = fileSize/total/1000;
//        result = fileSizeMax/pageSize;
//        if(result<=0){
//        	result = 1;
//        }
//		return result;
//	}
//	
//	private static int getPageCount(String flieName) throws IOException{
//		PdfReader reader = new PdfReader(flieName);       
//		int result = reader.getNumberOfPages();		
//		return result;
//	}
//	
//	
//	
//	public void convertPDF(){
//		try {
//			init();
//			ArrayList<String> inputFileList = FileUtil.getInputFileList(inputSourcePath);
//			for(int i=0;i<inputFileList.size();i++){
//				String inputFileName = inputFileList.get(i);
//				if(inputFileName.equals(""))
//				{
//					continue;
//				}
//				convertPDF(inputFileName);
//			}
//		} catch (IOException e) {
//			logger.error("convertPDF erro,message ="+e.getMessage());
//		}
//	}
//	
//	public void convertPDF(String inputFileName) throws IOException{
//		ArrayList<String> convetFileList =FileUtil.getConvertFileList(inputFileName);
//		FileUtils.copyFile(new File(inputFileName), new File(inputResultPath));
//		FileUtil.deleteFile(new File(inputFileName));
//		
//		String addWMStat = convetFileList.get(0).trim();	
//		
//		if(addWMStat.equals("1")){
//			addWaterMarkState = false;
//		}
//		
//		for(int i=1;i<convetFileList.size();i++){
//			String fileName = convetFileList.get(i).trim();			
//			logger.info("fileName ="+fileName);
//			//PDFConvert pdfConvert = new PDFConvert(fileName);//;args[0],args[1]);
//			init();
//			generateSwfFile(fileName);
//		}		
//	}
//	
//	
//	
//	public void countFileNumber(String path){
//		File dir = new File(path);
//        File file[] = dir.listFiles();
//        for (int i = 0; i < file.length; i++) {
//        	//System.out.println("路径："+dir.getPath()+" 合计："+file.length);
//        	//logger.error("路径："+dir.getPath()+" 合计："+file.length);
//            if (!file[i].isFile()){
//            	countFileNumber(file[i].getPath());
//            }else{
//            	logger.error("路径："+dir.getPath()+" 合计："+file.length);
//            	break;
//            }
//        }
//	}
//	
//	
//	
//	public static void main(String[] args)
//    {
////		ArrayList<String> convetFileList =FileUtil.getConvertFileList("covertfile.log");		
////		for(int i=0;i<convetFileList.size();i++){
////			String fileName = convetFileList.get(i);
////			logger.info("fileName ="+fileName);
////			PDFConvert pdfConvert = new PDFConvert(fileName);//;args[0],args[1]);
////			pdfConvert.generateSwfFile();
////		}
//		
//		//logger.info("fileName ="+fileName);
//		PDFConvert pdfConvert = new PDFConvert();//,args[1]);
//		pdfConvert.convertPDF();
//		//pdfConvert.countFileNumber("G://本社文件//02基础文件//图像//20130806");
//		
//    }
//	
//}
