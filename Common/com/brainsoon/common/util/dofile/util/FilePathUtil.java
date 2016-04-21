package com.brainsoon.common.util.dofile.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @ClassName: FilePathUtil 
 * @Description: 文件预览处理总配置类
 * @author tanghui 
 * @date 2014-5-8 下午3:31:48 
 *
 */
public class FilePathUtil {
	public static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
	public static final String FILE_TEMP = StringUtils.replace(WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp),"\\", "/");

	/**
	 * 
	 * @Title: getConverFileSaveRelPath 
	 * @Description: 文件转换后存在的根目录(绝对路径)
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String getConverFileSaveRelPath(String filePath){
		// 文件类型
		filePath = filePath.replaceAll("\\\\", "/").replaceAll("//", "/");
		String[] filePaths = filePath.split("/");
		String fileType = DoFileUtils.getExtensionName(filePath);// 获取不带扩展名的文件名
		if(filePaths.length > 1){
			return DoFileUtils.getParentFileDir(filePath)  + "/";
		}else{
			return  fileType + "/";
		}
    }
	
	
	
	/**
	 * 
	 * @Title: getFileRelPath 
	 * @Description: 文件存在的根目录(相对路径)-截取根路径
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String getFileRelPath(String basePath,String filePath){
		if (StringUtils.isNotBlank(filePath)){
			filePath = filePath.replaceAll("\\\\", "/");
			if (StringUtils.isNotBlank(basePath)){
				basePath = basePath.replaceAll("\\\\", "/");
				int num = basePath.length();
				filePath = filePath.substring(num, filePath.length());
			}
		}
		return filePath;
    }

	/**
	 * 
	 * @Title: getFileViewerBaseDir 
	 * @Description: 在线阅读临时根目录(绝对路径)
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String getFileViewerBaseDir(){
		return WebAppUtils.getWebAppRoot() + getFileViewerRelDir();
    }
	
	
	/**
	 * 
	 * @Title: getFileViewerRelFileDir 
	 * @Description: 在线阅读临时根目录(相对路径)
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String getFileViewerRelDir(){
		return WebAppUtils.getWebAppRelFileDir() + "viewer" + File.separator;
    }
	
	
	/**
	 * 
	 * @Title: getViewerBasePathByType 
	 * @Description: 预览路径(绝对路径)
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String getViewerBasePathByType(String fileType,String dirChar){
		//获取pdf阅读的临时目录
		String bookTempPath = getFileViewerBaseDir() + fileType + File.separator +  dirChar;
		File zipTempFile = new File(bookTempPath);
		if (!zipTempFile.exists()) {
			DoFileUtils.mkdir(bookTempPath); //创建目录
		}
		return bookTempPath.replaceAll("\\\\", "/");
	}
	
	/**
	 * 
	 * @Title: getViewerRelPathByType 
	 * @Description: 预览路径(相对路径)
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String getViewerRelPathByType(String fileType,String dirChar){
		//获取pdf阅读的临时目录
		String bookTempPath = getFileViewerRelDir() + File.separator + fileType + File.separator +  dirChar;
		File zipTempFile = new File(bookTempPath);
		if (!zipTempFile.exists()) {
			DoFileUtils.mkdir(bookTempPath); //创建目录
		}
		return bookTempPath.replaceAll("\\\\", "/");
	}
	
	/**
	 * 
	* @Title: filePreViewTempPath
	* @Description: 预览时在temp目录下创建临时文件 供预览用
	* @param filePath 文件原路径	形如：1/G2/07df0897-7ae9-4a75-8af5-e6ec9baec5fb/图片/123.jpg
	* @param objectId 文件objectId 作为新的文件名	形如：urn:file-a5c96a9a-66ef-42ed-b016-5b0fd1ae9076
	* @return    参数
	* @return String    返回类型	形如：1/G2/07df0897-7ae9-4a75-8af5-e6ec9baec5fb/file-a5c96a9a-66ef-42ed-b016-5b0fd1ae9076.jpg
	* @throws
	 */
	public static String filePreViewTempPath(String filePath,String objectId){
		/*
		//获取文件绝对路径
		HttpClientUtil http = new HttpClientUtil();
		Gson gson = new Gson();
		String fileDetail = http.executeGet(WebappConfigUtil.getParameter("CA_FILERES_DETAIL_URL") + "?id=" + objectId);
		File file = gson.fromJson(fileDetail, File.class);
		String filePath = FILE_ROOT+file.getPath();*/
		objectId = objectId.substring(4);//截取掉urn:
		
		//拷贝到新路径 返回新路径
		String prefix=filePath.substring(filePath.lastIndexOf(".")+1);//文件后缀
		String uuidPath = getFileUUIDPath(filePath.replaceAll("\\\\", "/"));//文件到UUID一级的相对目录	1/G2/07df0897-7ae9-4a75-8af5-e6ec9baec5fb/
		String newFilePath = getFileViewerBaseDir() + uuidPath + objectId + "." + prefix;//文件新路径
		
		java.io.File file = new java.io.File(FILE_ROOT+filePath);//文件原路径
		java.io.File newFile = new java.io.File(newFilePath);//文件新路径
		
		if (!newFile.exists()) {
			try {
				FileUtils.copyFile(file, newFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return newFilePath.replace(getFileViewerBaseDir(), "");//返回相对路径 getFileViewerBaseDir
	}
	
	/**
	 * 
	* @Title: getFilePath
	* @Description: 获取文件到uuid以及的目录
	* @param filePath	形如：1/G2/07df0897-7ae9-4a75-8af5-e6ec9baec5fb/视频/C++学习笔记与开发技巧与典型列子.doc
	* @return    参数
	* @return String    返回类型	形如：1/G2/07df0897-7ae9-4a75-8af5-e6ec9baec5fb/
	* @throws
	 */
	public static String getFileUUIDPath(String filePath){
		try {
			while (true) {
				filePath = filePath.substring(0, filePath.lastIndexOf("/"));//当前要判断的路径
				String thisDir = filePath.substring(filePath.lastIndexOf("/")+1);//当前要判断的目录
				String upperDirPath = filePath.substring(0, filePath.lastIndexOf("/"));//当前要判断的上一级路径
				String upperDir = upperDirPath.substring(upperDirPath.lastIndexOf("/")+1);//当前要判断的上一级目录
				if (thisDir.length()==36 && upperDir.indexOf("G")!=-1) {//判断逻辑 该目录长度为36并且上一级目录含G
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!filePath.endsWith("/")) {
			filePath = filePath+"/";
		}
		return filePath;
	}
	
}
