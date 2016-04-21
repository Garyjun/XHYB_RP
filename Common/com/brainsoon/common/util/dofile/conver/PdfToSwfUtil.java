package com.brainsoon.common.util.dofile.conver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brainsoon.common.util.dofile.content.PdfUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
  
/**
 * 
 * @ClassName: PdfToSwfUtil 
 * @Description:pdf转swf文件   
 * @author tanghui 
 * @date 2014-11-03 下午5:46:44 
 *
 */
public class PdfToSwfUtil {  
	private static Log log = LogFactory.getLog(PdfToSwfUtil.class);
    private final static String CONVERTFILETYPE = "pdf,jpg,jpeg,font,gif,png,wav";  
    private static int FZ_NUM = 30;//定义默认分组大小
    private static String swftoolsPath;
    
    static{
    	 swftoolsPath = PropertiesReader.getInstance().getProperty("swftoolsPath");
    }
    
    /** 
     * @param swftoolsPath 
     *            用于进行把文件转化为swf的工具地址 
     */  
    private PdfToSwfUtil() {} 
    
    
    
    /** 
     * 把文件转化为swf格式，仅支持"pdf" 
     *  pdfConvertToSwf
     * @param pdfPath 
     *            待转换的pdf文件地址 
     * @param swfPath 
     *            要进行转化为swf文件地址
     * @param isCover 
     *            转化后的swf的文件地址 
     *  @param pageNumBuffer 
     *           如：
     *           1-20 表示转换从第1页到20页
     *           1,4,6,9-11 表示转换第1页、第4页，第6页，并且第9到11页
     *           2  表示转换第2页
     *  @param converSwfType 
     *           是否覆盖（true = 一个  false 多个） 
     * @return 
     */  
    public static boolean pdfConvertToSwf(String pdfPath, String swfPath,String pageNumBuffer,boolean converSwfType,boolean hasFileName) throws DoFileException{  
        log.info("开始转化文件到swf格式");  
        boolean boo = true;
        boolean b = true;
        try {
    	   if(StringUtils.isBlank(swftoolsPath)) {    
               if (log.isWarnEnabled()) {  
                   log.warn("未找到swftools工具的路径");  
                   b = false;
               }  
           }  
           String filetype = pdfPath.substring(pdfPath  
                   .lastIndexOf(".") + 1);  
           // 判读上传文件类型是否符合转换为pdf   
           log.info("判断文件类型通过");  
           if (CONVERTFILETYPE.indexOf(filetype.toLowerCase()) == -1) {  
               if (log.isWarnEnabled()) {  
                   log.warn("当前文件不符合要转化为SWF的文件类型！！！");  
                   b = false;
               }  
           }  
           File sourceFile = new File(pdfPath);  
           if (!sourceFile.exists()) {  
               if (log.isWarnEnabled()) {  
                   log.warn("要进行swf的文件不存在！！！");  
               }  
           }  
           log.info("准备转换的文件路径存在");  
           
           //判断转换后的文件夹是否存在
           //根据源文件或者转换后的swf文件名称，不含文件扩展名
           String swfName = DoFileUtils.getFileNameNoEx(pdfPath);
           File swfFile = new File(swfPath);  
           if (!swfFile.exists()) {  
               swfFile.mkdirs();  
           } 
           //转换后的文件路径为：
           swfPath = swfPath.replaceAll("\\\\", "/");
           if(swfPath.endsWith("/")){
           		swfPath += swfName;
           }else{
           		swfPath += "/" + swfName;
           }
           
           if(converSwfType){//生成单个swf
           	if(new File(swfPath + ".swf").exists()){
           		boo = false;
           		log.info("文件存在无须转换.");
           	}
           }
           
           if (filetype.toLowerCase().equals("jpg")) {  
               filetype = "jpeg";  
           }  
           
           //如果需要转换则执行一下逻辑，否则不执行
           if(boo){
           	//设置转换的页码相关参数，具体参数请参照 pageNumBuffer说明
           	//pageNumBuffer = "1,3,5-7";
           	if(StringUtils.isNotBlank(pageNumBuffer)){
           		if(pageNumBuffer.startsWith(",")){
           			pageNumBuffer = pageNumBuffer.substring(1,pageNumBuffer.length());
           		}
           		if(pageNumBuffer.endsWith(",")){
           			pageNumBuffer = pageNumBuffer.substring(0,pageNumBuffer.length()-1);
           		}
           		log.info("待转换的pageNumBuffer为：" + pageNumBuffer);
           		String cmd = swftoolsPath;
   				cmd += " -p \"" + pageNumBuffer + "\"";
   				cmd += " \"" + pdfPath + "\"";
   				cmd += " -F \"" + PropertiesReader.getInstance().getProperty("xpdfFontPath") + "\"";
   				cmd += " -s languagedir=\"" + PropertiesReader.getInstance().getProperty("xpdfPath")+"\"";
   				if(converSwfType){//生成单个swf
   					cmd += " -s poly2bitmap";
   				}
   				cmd += " -s flashversion=9";
   				if(converSwfType){//生成单个swf
   					cmd += " -o \"" + swfPath + ".swf\"";
   				}else{//生成多个swf
   					if(hasFileName){//如果需要包含原文件的名字，生成的swf名字 如：xxx_1.swf
   						cmd += " -o \"" + swfPath + "_%.swf\"";
   					}else{//生成的swf名字 如：1.swf
   						swfPath = swfPath.substring(0, swfPath.lastIndexOf("/"));
   						cmd += " -o \"" + swfPath + "%.swf\"";
   					}
   				}
   				
   				//执行转换
   				b = DoFileUtils.exeShell(cmd.toString());
   				log.info("转换SWF文件成功!!!"); 
           	}else{
           		b = false;
           		throw new DoFileException("未找到要转换的页数!!!");
           	}
           }
       } catch (Exception e) {
    	   e.printStackTrace();
    	   b = false;
       }
        return b;  
    }  
    
    private static void dealWith(final Process pro){      
        // 下面是处理堵塞的情况       
        try {      
            new Thread(){      
                public void run(){      
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(pro.getInputStream()));      
                    String text;      
                    try {      
                        while ( (text = br1.readLine()) != null) {      
                            System.out.println(text);      
                        }      
                    } catch (IOException e) {      
                        e.printStackTrace();      
                    }      
                }      
            }.start();      
        } catch (Exception e) {      
            e.printStackTrace();      
        }      
              
        try {      
            new Thread(){      
                public void run(){      
                    BufferedReader br2 = new BufferedReader(new InputStreamReader(pro.getErrorStream()));//这定不要忘记处理出理时产生的信息，不然会堵塞不前的       
                    String text;      
                    try {      
                        while( (text = br2.readLine()) != null){      
                            System.err.println(text);      
                        }      
                    } catch (IOException e) {      
                        e.printStackTrace();      
                    }      
                }      
            }.start();      
        } catch (Exception e) {      
            e.printStackTrace();      
        }      
    } 
    
    
    /**
     * @param filePath
     * @param thisIndex
     * @return
     */
    public static boolean validateExists(String fileName, int thisIndex, String fileTempPath) {
        File tempFile = new File(fileTempPath + fileName + "_" + thisIndex + ".swf");
        if (tempFile.exists()) {
            tempFile.setLastModified(new Date().getTime());
            return true;
        }
        return false;
    }
    
    
    /**
     * 
     * @Title: pdf2Swf 
     * @Description: 
     * @param   indexPage
     * @param   pdfPath
     * @param   swfPath
     * @param   pageNumBuffer 如果传入了pageNumBuffer参数，则直接使用，否则需要拼接参数
     * @return boolean 
     * @throws
     */
    public static boolean pdf2Swf(int indexPage,String pdfPath, String swfPath,List<String> list,int allPage,String isconverFileType,boolean hasFileName) { 
    	boolean b = true;
		try {
		    if(list == null){ //如果传入了pageNumBuffer参数，则直接使用，否则需要拼接参数
    		  list = new ArrayList<String>();
    		  //获取pdf最大页数
			  allPage = PdfUtil.getPdfAllPageTotalNum(pdfPath);
			  System.out.println("<pdf2Swf>pdf总页数为：" + allPage);
	          FZ_NUM = Integer.parseInt(PropertiesReader.getInstance().getProperty("atomPages"));
	          String pageNumBuffer = "";
		      if(allPage != 0){
	    		 int jNum = allPage - indexPage;
				 if(jNum <= FZ_NUM || allPage <= FZ_NUM){ //如果allPage小于30，则为：indexPage-allPage
					 pageNumBuffer = indexPage + "-" + allPage;
					 list.add(pageNumBuffer);
					 //System.out.println("pageNumBuffer0 " + pageNumBuffer);
				 }else{ //如果大于30，则需要分组
					 int countNum = jNum / FZ_NUM; //以30个分组
					 for (int j = 0; j <= countNum; j++) {
						 if(j == 0){
							 pageNumBuffer = indexPage + "-" + (indexPage + FZ_NUM);
							 list.add(pageNumBuffer);
							 //System.out.println("pageNumBuffer1 " + pageNumBuffer);
						 }else if((indexPage + FZ_NUM + j*FZ_NUM) <= allPage){
							 pageNumBuffer = (indexPage + j*FZ_NUM) + "-" + (indexPage + FZ_NUM + j*FZ_NUM);
							 list.add(pageNumBuffer);
							 //System.out.println("pageNumBuffer2 " + pageNumBuffer);
						 }else{
							 pageNumBuffer = (indexPage + j*FZ_NUM) + "-" + allPage;
							 list.add(pageNumBuffer);
							 //System.out.println("pageNumBuffer3 " + pageNumBuffer);
						 }
					 }
				 }
		     }
		     b = doProcess(indexPage,allPage,pdfPath, swfPath,list,isconverFileType,hasFileName);
    	  }
		} catch (IOException e) {
			e.printStackTrace();
			b = false;
		}
		return b;
    }  
    
    
    /**
     * 
     * @Title: doProcess 
     * @Description: 转换处理方法
     * @param   
     * @return boolean 
     * @throws
     */
    public static boolean doProcess(int indexPage,int allPage,String pdfPath, String swfPath,List<String> list,String isconverFileType,boolean hasFileName){
    	boolean b = true;
    	try {
    		if(list != null){
        		//String isconverFileType = PropertiesReader.getInstance().getProperty("converSwfType");
            	if(StringUtils.isBlank(isconverFileType)){
            		isconverFileType = "mswf";
            	}
            	
            	//sswf代表一个pdf生成一个swf文件，mswf代表一个pdf生成多个swf文件
            	if(StringUtils.contains(isconverFileType, "mswf")){
            		log.info("开始转换文档单页SWF文件!!!"); 
            		System.out.println("list.size() : " + list.size());
            		for (int i = 0; i < list.size(); i++) {
            			b = pdfConvertToSwf(pdfPath,swfPath,list.get(i),false,hasFileName); 
            			log.info("转换文档单页SWF文件结束!!!");  
            		}
    			}
            	
            	//sswf代表一个pdf生成一个swf文件，mswf代表一个pdf生成多个swf文件
            	if(StringUtils.contains(isconverFileType, "sswf")){
            		log.info("开始转换整个文档的SWF文件!!!");  
    				b = pdfConvertToSwf(pdfPath, swfPath,indexPage+ "-" +allPage,true,hasFileName); 
    				log.info("转换整个文档的SWF文件结束!!!");  
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
			b = false;
		}
    	return b;
    }
   
    
    /**
     * 
     * @Title: conver2SwfByFolder 
     * @Description: 转换某个目录下的pdf/office/txt文件，仅支持指定的目录下（不包含子目录）
     * @param  srcPath 待转换文件路径
     * @return void 
     * @throws
     */
    public static void conver2SwfByFolder(String srcPath,boolean hasFileName){
       String describe = "";
 	   File file = new File(srcPath);
 	   String isconverFileType = "mswf"; //默认转多个
 	   int s = 0; //成功数
 	   int f = 0; //失败数
 	   if(file.exists()){
 		   String converDocPath = "";
 		   String converTempDocPath = "";
 		   File[] files = file.listFiles();
 		   if(files != null && files.length > 0){
 			   for (int j = 0; j < files.length; j++) {
 				   File docFile = files[j];
 				   if(docFile.isFile()){
 					   try {
 						  String docFilePath = docFile.getAbsolutePath();
 	 					   //转换后的swf路径 
 	 					   converDocPath = docFile.getParent()+ "/conver/";
 	 					   converTempDocPath = docFile.getParent()+ "/temp/";
 	 					   DoFileUtils.mkdir(converDocPath);
 	 					   String fileType = DoFileUtils.getExtensionName(docFilePath);
 	 					   //获取不带扩展名的文件名
 						   String srcFileName = DoFileUtils.getFileNameNoEx(docFilePath);
 						   if (PropertiesReader.getInstance().getProperty(ConstantsDef.documentFormat).contains(fileType)) {
 							   //转换后的pdf路径
 		 					   String targetPdfPath = DoFileUtils.connectFilePath(converTempDocPath,srcFileName + ".pdf");
 							   DoFileUtils.mkdir(converTempDocPath);
 						    	if(!fileType.equals("pdf")){
 						    		try {
 							    		if(fileType.equals("docx") || fileType.equals("doc") 
 								    			|| fileType.equals("xlsx") || fileType.equals("xls")
 								    			|| fileType.equals("ppt") || fileType.equals("pptx")){
 							    			docFilePath = OfficeToPdfUtils.convertToPdf(docFilePath,targetPdfPath);// 返回转换后的临时pdf路径，将其赋值给 filePath
 							    		}else{
 							    			docFilePath = TxtToPdfUtils.convertToPdf(docFilePath,targetPdfPath);// 返回转换后的临时pdf路径，将其赋值给 filePath
 								    	}
 						    		} catch (Exception e) {
 						    			f++;
 										describe +=  "{失败}-【" + srcFileName + "】" + e.getMessage() + "\n";
 										e.printStackTrace();
 										continue;
 									}
 						    	}
 							}
 						   
 							try {
 								//最终pdf是否存在的判断
 								if(!new File(docFilePath).exists() || new File(docFilePath).length() <= 0){
 									f++;
 									describe +=  "{失败}-文档【" + srcFileName + "】转pdf失败。\n";
 								}else{
 									// 获取pdf总页数
 									int maxPages = PdfUtil.getPdfAllPageTotalNum(docFilePath);
 									if(maxPages == 0){
 										f++;
 										describe += "{失败}-文档【" + srcFileName + "】获取PDF总页数失败.\n";
 									}else{
 										pdf2Swf(1,docFilePath,converDocPath,null,maxPages,isconverFileType,hasFileName); 
 										s++;
 									}
 								}
 							} catch (IOException e) {
 								f++;
 								e.printStackTrace();
 								continue;
 							}
					   } catch (Exception e) {
						   f++;
						   e.printStackTrace();
						   continue;
					  }
 				    }
	 			  }  
 			    describe = "\n 共处理：【" + (s+f) + "】个，成功：【" + s + "】个，失败：【" + f + "】个。\n\n" + describe;
 			    DoFileUtils.createTxt(srcPath+"/doLog.log", describe);
 			   //DoFileUtils.deleteDir(converTempDocPath);
 		   }
 	   }else{
 		   log.info("目录不存在");
 	   }
    }
    
    public static void main(String[] args) throws IOException {
    	//pdf2Swf(1,"D:/大佳网服务器与产品部署手册.pdf", "D:/11/22222",null,22); 
    	//conver2SwfByFolder("D:/12/测试文档/pdf/");
    	int num = PdfUtil.getPdfAllPageTotalNum("E:\\test\\LearnPythonTheHardWay.pdf");
    	pdfConvertToSwf("E:\\test\\LearnPythonTheHardWay.pdf",
    			"E:\\123\\test","0-10",false,true);
//    	pdf2Swf(0,"E:\\test\\LearnPythonTheHardWay.pdf", "E:\\123",null,num); 
    }  
    
}  
