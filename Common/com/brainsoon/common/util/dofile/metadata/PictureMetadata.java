package com.brainsoon.common.util.dofile.metadata;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import com.brainsoon.common.util.dofile.metadata.fo.FileObject;
import com.brainsoon.common.util.dofile.metadata.fo.Picture;
/**
 * 
 * @ClassName: ImageMetadata 
 * @Description:  图片元数据元数据分类
 * @author tanghui 
 * @date 2014-4-17 下午12:37:10 
 *
 */
public class PictureMetadata implements IFileMetadata {

	protected static final Logger logger = Logger.getLogger(PictureMetadata.class);
	private String fileUrl; //文件绝对路径
	
	public PictureMetadata(String fileUrl) {
		super();
		this.fileUrl = fileUrl;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	@Override
	public FileObject getFileMetadata() {
		File file = new File(fileUrl);
		FileInputStream fis = null;
		Picture picture = new Picture();
		try {
			fis = new FileInputStream(file);
			BufferedImage srcImg = ImageIO.read(fis); 
			int imgWidth = srcImg.getWidth();
			int imgHeight = srcImg.getHeight();
			picture.setResolution(imgWidth + "x" + imgHeight); //分辨率
		} catch (FileNotFoundException e) {
			// tanghui Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if(fis != null){
            	try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
		return picture;
	}

}
