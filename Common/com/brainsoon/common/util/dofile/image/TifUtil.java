package com.brainsoon.common.util.dofile.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.sun.media.jai.codec.BMPEncodeParam;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;

/**
 * 
 * @ClassName: TifUtil 
 * @Description:  实现jpg/png/bmp/tif等互转
 * @author tanghui 
 * @date 2013-5-28 下午2:48:03 
 *
 */
public class TifUtil {
	 protected static final Logger logger = Logger.getLogger(TifUtil.class);
	
	
	/* tif转换到jpg格式 */
	public static boolean tif2Jpg(String srcFile,String tarFile){
		boolean b = true;
		try {
			DoFileUtils.mkdir(tarFile);
			RenderedOp src2 = JAI.create("fileload", srcFile);
			OutputStream os2 = new FileOutputStream(tarFile);
			JPEGEncodeParam param2 = new JPEGEncodeParam();
			//指定格式类型，jpg 属于 JPEG 类型
			ImageEncoder enc2 = ImageCodec.createImageEncoder("JPEG", os2, param2);
			enc2.encode(src2);
			os2.close();
		} catch (Exception e) {
			b= false;
			e.printStackTrace();
		}finally{
        	if(!new File(tarFile).exists() || new File(tarFile).length() <= 0){
            	b = ImgCoverUtil.converToOther(srcFile,tarFile); 
            }
        }
		return b;
	}
	
	
	/*tif转换到bmp格式*/
	public static void tif2Bmp(String srcFile,String tarFile) throws IOException{
		try {
			RenderedOp src = JAI.create("fileload", srcFile);
	        OutputStream os2 = new FileOutputStream(tarFile);
	        BMPEncodeParam param = new BMPEncodeParam();
	        ImageEncoder enc = ImageCodec.createImageEncoder("BMP", os2,param);
	        enc.encode(src);
	        os2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
     * 将TIFF文件转换成BMP
     * @param inputFile
     * @param outputFile
     * @return
     */
    public static boolean tif2Bmp2(String inputFile,String outputFile){
    	  boolean boo = true;
    	  if(StringUtils.isNotEmpty(outputFile) 
    			  && (inputFile.substring(inputFile.lastIndexOf(".")+1, inputFile.length()).toLowerCase().equals("tiff")
    			  ||  inputFile.substring(inputFile.lastIndexOf(".")+1, inputFile.length()).toLowerCase().equals("tif"))
    			  && outputFile.substring(outputFile.lastIndexOf(".")+1, outputFile.length()).toLowerCase().equals("bmp")){
    		    RenderedOp src = JAI.create("fileload", inputFile); 
    	   	    OutputStream os = null;
    			try {
    				  os = new FileOutputStream(outputFile);
    				  BMPEncodeParam param = new BMPEncodeParam();  
    		    	  ImageEncoder enc = ImageCodec.createImageEncoder("BMP", os,param);  
    		    	  enc.encode(src);  
    		    	  os.close();//关闭流 
				} catch (FileNotFoundException e) {
					boo = false;
					e.printStackTrace();
				} catch (IOException e) {
					boo = false;
					e.printStackTrace();
				} finally {
		            if(os != null){
		            	try {
		            		os.close();
						} catch (IOException e) {
						}
		            }
		            if(!new File(outputFile).exists() || new File(outputFile).length() <= 0){
						  boo = false;
					 }
		        }  
    	  }
    			return boo;
    }
	
	
	 /**
	   * 将其他格式转化为tif格式。（支持jpg/png/bmp/gif to  tif）
	   * @param srcFile  需要装换的源文件
	   * @param descFile 装换后的转存文件
	   * @throws Exception
	   */
	public  static void other2Tif(String srcFile, String descFile) {
		RenderedOp src = JAI.create("fileload", srcFile);
		OutputStream os;
		try {
			os = new FileOutputStream(descFile);
			TIFFEncodeParam param = new TIFFEncodeParam();
			param.setCompression(TIFFEncodeParam.COMPRESSION_DEFLATE); 
			ImageEncoder encoder = ImageCodec.createImageEncoder("TIFF", os, param);
			encoder.encode(src);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
    //test
	public static void main(String[] args) throws Exception {
		String input2 = "D:/Project素材/image/2.tif";
		String output2 = "D:/Project素材/image/2222.jpg";
		String outputFile = "D:/Project素材/image/2222.bmp";
		/* tif转换到jpg格式 */
		tif2Jpg(input2, output2);
		/*tif转换到bmp格式*/
		tif2Bmp(input2, outputFile);
        //其他的一样的方式转换

	}

}
