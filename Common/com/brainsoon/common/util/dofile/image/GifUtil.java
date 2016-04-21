package com.brainsoon.common.util.dofile.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.brainsoon.common.util.dofile.util.DoFileUtils;


/**
 * 
 * @ClassName: GifUtil 
 * @Description:  Gif处理工具类
 * @author tanghui 
 * @date 2013-5-29 下午1:07:15 
 *
 */
public class GifUtil {

	
	 /*
     * 将GIF转JPG
     * */
     public synchronized static boolean gif2Jpg(String srcPath,String tarPath){
    	boolean b = true;
    	try {
    		 DoFileUtils.mkdir(tarPath);
    		 GifDecoder decoder = new GifDecoder();
//    		 InputStream is = null;
//    	     try {
//    	    	 is = new FileInputStream(srcPath); 
//    	    	 if(decoder.read(is) != 0){
//    	        	 System.out.println("GIF图片读取有错误");
//    	         }
//			} catch (Exception e) {
//				b = false;
//				e.printStackTrace();
//			} finally{
//				if(is != null){
//					is.close();  
//				}
//			}
	         System.out.println("GIF图片帧是数量: "+ decoder.frameCount);   
	         if(decoder != null && decoder.frameCount > 0){
	        	 for(int i=0 ; i<decoder.frameCount; i++){      
			          BufferedImage frame = decoder.getFrame(i);  
			          OutputStream out = new FileOutputStream(tarPath);
			          ImageIO.write(frame, "JPEG", out);//将frame 按jpeg格式  写入out中
//			          JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out); 
			          out.flush();
			          frame.flush();
			          out.close();
		         }
	         }else{
	        	 b = false;
	         }
		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		} finally{
        	if(!new File(tarPath).exists() || new File(tarPath).length() <= 0){
        		b = ImgCoverUtil.converToOther(srcPath,tarPath); 
            }
        }
       return b;
     }
     
	/** 
	 * @Title: main 
	 * @Description: 
	 * @param   
	 * @return void 
	 * @throws 
	 */
	public static void main(String[] args) {
		// tanghui Auto-generated method stub

	}

}
