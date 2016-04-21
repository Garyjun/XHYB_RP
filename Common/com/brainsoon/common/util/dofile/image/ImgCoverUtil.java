package com.brainsoon.common.util.dofile.image;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.common.util.dofile.util.DoFileUtils;

/**
 * 
 * @ClassName: ImgCoverUtil 
 * @Description:  实现jpg/png/bmp/tif等互转
 * @author tanghui 
 * @date 2013-5-29 下午1:12:03 
 *
 */
public class ImgCoverUtil {
	 protected static final Logger logger = Logger.getLogger(ImgCoverUtil.class);
	
   
   /**   
    * <p>Discription:conver2Others [convert GIF->JPG GIF->PNG PNG->GIF(X) PNG->JPG  TIF->JPG]</p> 
    * @param srcPath 
    * @param destPath 
    * @param result  boo
    */  
   public static boolean conver2Other(String srcPath, String destPath) {  
   	boolean boo = true;
       try {   
       	if(DoFileUtils.exitFile(srcPath)){
       		DoFileUtils.mkdir(destPath);
       		String srcFormate = srcPath.substring(srcPath.lastIndexOf(".")+1,srcPath.length());
       		String descFormate = destPath.substring(destPath.lastIndexOf(".")+1,destPath.length());
       		if(srcPath.toLowerCase().equals(destPath.toLowerCase())){
    			
    		}else if(srcFormate.toLowerCase().equals(descFormate.toLowerCase())){
    			FileUtils.copyFile(new File(srcPath), new File(destPath));
    		}else{
	        	if(srcFormate.toLowerCase().equals("bmp")){
	        		BmpUtil.bmp2jpg(srcPath, destPath);
	        	}else if(srcFormate.toLowerCase().equals("tif") || srcFormate.toLowerCase().equals("tiff")){
	        		TifUtil.tif2Jpg(srcPath, destPath);
	        	}else if(srcFormate.toLowerCase().equals("gif")){ //暂时未测试
	        		GifUtil.gif2Jpg(srcPath, destPath);
	        	}else{
	        		converToOther(srcPath,destPath);
	        	}
    		}
         }
       } catch (Exception e) {  
           boo = false;
           e.printStackTrace();
           logger.error("【 " + srcPath + "】图片转换失败！");
       } finally{
    	   if(!new File(destPath).exists() || new File(destPath).length() <= 0){
    		   try {
					Thumbnails.of(srcPath).toFile(destPath);
					 if(boo && (!new File(destPath).exists() || new File(destPath).length() <= 0)){
						 boo = false;
					 }
				} catch (IOException e) {
					boo = false;
					e.printStackTrace();
				}
    	   }
       }
       return boo;
   }  
   
  

   //简单格式的转换：支持 jpg、gif、png、bmp
   public static boolean converToOther(String srcPath, String destPath){
	   boolean b = true;
	   try {
		   DoFileUtils.mkdir(destPath);
		   String srcFormate = srcPath.substring(srcPath.lastIndexOf(".")+1,srcPath.length());
		   File f = new File(srcPath);  
	       f.canRead();  
	       f.canWrite();
	       BufferedImage src = ImageIO.read(f);  
		   if(srcFormate.toLowerCase().equals("png")){
			   ImageIO.write(src, "PNG", new File(destPath)); 
		   }else{
			   ImageIO.write(src, "JPEG", new File(destPath)); 
		   }
		} catch (Exception e) {
			b = false;
			e.printStackTrace();  
		} finally{
			 if(!new File(destPath).exists() || new File(destPath).length() <= 0){
    		   b = false;
    	   }
       }
	   return b;
   }
   
   
   /*** 
    * 功能 :调整图片大小
    * @param srcImgPath 原图片路径 
    * @param distImgPath  转换大小后图片路径 
    * @param width   转换后图片宽度 
    * @param height  转换后图片高度 
    */  
   public static boolean resizeImage(String srcImgPath, String tarImgPath,int width, int height){  
	   boolean b = true;
	   try {
		   DoFileUtils.mkdir(tarImgPath);
		   File srcFile = new File(srcImgPath);  
		   String srcFormate = srcImgPath.substring(srcImgPath.lastIndexOf(".")+1,srcImgPath.length());
		   Image srcImg = ImageIO.read(srcFile);  
	       BufferedImage buffImg = null;  
	       buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
	       buffImg.getGraphics().drawImage(  
	               srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,  
	               0, null);  
	       if(srcFormate.toLowerCase().equals("png")){
			   ImageIO.write(buffImg, "PNG", new File(tarImgPath)); 
		   }else{
			   ImageIO.write(buffImg, "JPEG", new File(tarImgPath)); 
		   }
		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		} finally{
			 if(!new File(tarImgPath).exists() || new File(tarImgPath).length() <= 0){
				 try {
					Thumbnails.of(srcImgPath).size(width,height).toFile(tarImgPath);
					if(!new File(tarImgPath).exists() || new File(tarImgPath).length() <= 0){
						b = false;
					}
				} catch (IOException e) {
					b = false;
					e.printStackTrace();
				}
    	   }
       }
	   return b;
   }  
   
   
   /**
    * 
    * converAndResizeImage
    * @Description: 转换并改变大小
    * @param   
    * @return void 
    * @throws
    */
   public static boolean converAndResizeImage(String srcPath, String destPath,int width, int height){
	   boolean b = true;
	   String tempPath = "";
	   try {
		   DoFileUtils.mkdir(destPath);
		   //首先用 Thumbnails 类库的方法转换，如果不成功，则调用常规的转换方法
		   Thumbnails.of(srcPath).size(width,height).toFile(destPath);
		   if(!new File(srcPath).exists() || new File(destPath).length() <= 0){
			   tempPath = new File(destPath).getParent() + "/temp/" + DoFileUtils.getFileNameHasEx(destPath);
			   DoFileUtils.mkdir(tempPath);
			   b = conver2Other(srcPath, tempPath);
			   if(b){ //如果转换成功，则调用改变尺寸的方法
				  b = resizeImage(tempPath, destPath, width, height);
			   }
		   }
		  
	   } catch (Exception e) {
		   b = false;
		   e.printStackTrace();
	   }  finally{
			if(b && (!new File(destPath).exists() || new File(destPath).length() <= 0)){
	    		 b = false;
	    	}
			if(StringUtils.isNotBlank(tempPath)){
				DoFileUtils.deleteDir(new File(tempPath).getParent());
			}
	   }
	   return b;
   }
   
   /**
    * 
    * @Title: converImageByFolder 
    * @Description: 转换某个目录下的图片文件，仅支持指定的目录下（不包含子目录）
    * @param   
    * @return void 
    * @throws
    */
   public static void converImageByFolder(String imageFolderPath){
	   File file = new File(imageFolderPath);
	   if(file.exists()){
		   String converImagePath = "";
		   String converTempImagePath = "";
		   File[] files = file.listFiles();
		   if(files != null && files.length > 0){
			   for (int j = 0; j < files.length; j++) {
				   File imageFile = files[j];
				   logger.info("共有：【" +files.length +"】条记录，还有：【" + (files.length-j) +"】条记录待处理。");
				   if(imageFile.isFile()){
					   String imageFilePath = imageFile.getAbsolutePath();
					   converImagePath = imageFile.getParent()+ "/conver/";
					   converTempImagePath = imageFile.getParent()+ "/temp/";
					   DoFileUtils.mkdir(converImagePath);
					   DoFileUtils.mkdir(converTempImagePath);
					   String newTempImagePath = converTempImagePath + DoFileUtils.getFileNameNoEx(imageFilePath) + ".png";
					   String newImagePath = converImagePath + DoFileUtils.getFileNameNoEx(imageFilePath) + ".png";
					   conver2Other(imageFilePath, newTempImagePath);
					   resizeImage(newTempImagePath, newImagePath, 164, 123); 
				   }
			  }
			   DoFileUtils.deleteDir(converTempImagePath);
		   }
	   }else{
		   logger.info("目录不存在");
	   }
	   logger.info("恭喜！处理完成了！");
   }
   
	
	
    //test
	public static void main(String[] args) throws Exception {
		converImageByFolder("F:/名师资源处理/no/");
//		String input2 = "D:/12/测试文档/《中国的地质灾害》图片9.jpg";
//		String output2 = "D:/12/测试文档/cover.jpg";
		//converAndResizeImage(input2,output2,164, 123);
//		String outputFile = "D:/Project素材/image/2222.bmp";
//		/* tif转换到jpg格式 */
		//conver2Other(input2, output2);
		//resizeImage(input2, output2, 500, 600);
		//System.out.println(9 % 10);

	}

}
