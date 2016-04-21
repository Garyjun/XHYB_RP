package com.brainsoon.common.util.dofile.metadata;

import java.io.File;
import java.util.Date;
import com.brainsoon.common.util.dofile.metadata.fo.FileObject;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PropertiesReader;

/**
 * 
 * @ClassName: FileMetadataFactory 
 * @Description: 获取文件元数据工厂类
 * @author tanghui 
 * @date 2014-4-17 上午11:31:37 
 *
 */
public class FileMetadataFactory {

	public FileMetadataFactory() {
		super();
		// tanghui Auto-generated constructor stub
	}
	  
	public synchronized static FileObject getMetadata(String fileUrl){
			File file = new File(fileUrl);
			if(file.exists()){
				String fileType = DoFileUtils.getExtensionName(fileUrl).toLowerCase();
		        IFileMetadata fileMetadata = null;
				if(PropertiesReader.getInstance().getProperty(ConstantsDef.pictureFormat).contains(fileType)) {//图片
		        	fileMetadata = new PictureMetadata(fileUrl);
				}else if(
							PropertiesReader.getInstance().getProperty(ConstantsDef.videoFormat).contains(fileType)
						 || PropertiesReader.getInstance().getProperty(ConstantsDef.audioFormat).contains(fileType)
						 || PropertiesReader.getInstance().getProperty(ConstantsDef.animaFormat).contains(fileType)
						 ) {  //视频 | 动画  | 音频
		        	fileMetadata = new MultimediaMetadata(fileUrl);
		        }
				return getFileObjectMetadata(fileMetadata.getFileMetadata(), fileUrl);
			}else{
				throw new DoFileException("文件不存在!");
			}
			
	}
	
	
	private static FileObject getFileObjectMetadata(FileObject fileObject,String fileUrl){
		File file = new File(fileUrl); //创建文件对象 
		fileObject.setFileName(file.getName());
		fileObject.setExists(file.exists());
		fileObject.setPath(file.getPath());
		fileObject.setAbsolutePath(file.getAbsolutePath());
		fileObject.setCanRead(file.canRead());
		fileObject.setCanWrite(file.canWrite());
		fileObject.setParentPath(file.getParent());
		fileObject.setLength(file.length());
		//SimpleDateFormat smf = new SimpleDateFormat("yyyy");
		fileObject.setLastModified(new Date(file.lastModified()).toLocaleString());
		fileObject.setFile(file.isFile());
		fileObject.setDirectory(file.isDirectory());
		String format = fileUrl.substring(fileUrl.lastIndexOf(".")+1, fileUrl.length());
		fileObject.setFormat(format);
		//----------------show--------------------
//		System.out.println("文件名称：" + file.getName());  
//        System.out.println("文件是否存在：" + file.exists());  
//        System.out.println("文件的相对路径：" + file.getPath());  
//        System.out.println("文件的绝对路径：" + file.getAbsolutePath());  
//        System.out.println("文件可以读取：" + file.canRead());  
//        System.out.println("文件可以写入：" + file.canWrite());  
//        System.out.println("文件上级路径：" + file.getParent());  
//        System.out.println("文件大小：" + file.length() + "B");  
//        System.out.println("文件最后修改时间：" + new Date(file.lastModified()));  
//        System.out.println("是否是文件类型：" + file.isFile());  
//        System.out.println("是否是文件夹类型：" + file.isDirectory());  
//        System.out.println("文件扩展名：" + format); 
        return fileObject;
	}
}
