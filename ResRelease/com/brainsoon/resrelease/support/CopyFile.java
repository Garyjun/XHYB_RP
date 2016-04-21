package com.brainsoon.resrelease.support;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.image.ImgCoverUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.FileToolkit;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.MetaDataDC;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.support.SystemConstants;
import com.google.gson.Gson;

/**
 * @ClassName: CopyFile
 * @Description: 拷贝文件工具类
 * @author xiehewei
 * @date 2014年9月19日 上午11:28:33
 *
 */
public class CopyFile {
	private static Logger log = Logger.getLogger(CopyFile.class);
	
	//获得转换后文件的根目录
	private static final String converFileRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.converFileRoot).replace("\\\\", "\\/");
	private static final String publishRootDir = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile).replace("\\\\", "\\/");
	
	/**
	 * 针对中国移动定制路径
	 * 拷贝源文件封面到移动指定目录
	 * 格式：png
	 * 大小：164*123
	 * @param sourceRoot 源文件根路径
	 * @param publishRoot 发布后文件根路径
	 * @param dc 元数据
	 * @param resOrder 需求单
	 */
	public static void copyThumbToPubDir(String sourceRoot, String publishRoot, MetaDataDC dc, ResOrder resOrder){
		try {
			String time2Str = DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replace(":", "").replace(" ", "");
			String publishDir = publishRoot + time2Str + "/"
					+ resOrder.getOrderId() + "/" + "resouce_new/releasedJpgs";
			String identifier = "";
			identifier = dc.getCommonMetaData().getIdentifier();
			String thumb = dc.getCommonMetaData().getCommonMetaValue("thumbPath");
			String filePath = dc.getCommonMetaData().getCommonMetaValue("filePath").replace("\\\\", "\\/");
			String fileType = DoFileUtils.getExtensionName(filePath).toLowerCase();
			log.debug("thumbPath:" + thumb + "    *************    fileType:" + fileType);
			if (!PropertiesReader.getInstance().getProperty(ConstantsDef.audioFormat).contains(fileType)
					&& !"swf".equals(fileType)) { // 音频没有封面 swf没有封面
				java.io.File destDir = new java.io.File(publishDir);
				if (!destDir.exists()) {
					destDir.mkdirs();
				}
				String absolutThumbPathDir = sourceRoot + filePath.substring(0, filePath.lastIndexOf("/") + 1) + "thumb/cover.jpg";
				java.io.File thumbPathDirFile = new java.io.File(absolutThumbPathDir);
				String publishThumbPath = publishDir + "/" + identifier + ".png";
				// 如果是图片，则判断缩略图存不存在，不存在则自动抓取
				if (PropertiesReader.getInstance().getProperty(ConstantsDef.pictureFormat).contains(fileType)) {
					if (!thumbPathDirFile.exists()) { // 原封面文件不存在
						String absoluPath = sourceRoot + filePath; // 找源文件路径
						java.io.File sourceFile = new java.io.File(absoluPath);
						log.debug("beforcopypicture ----------  " + sourceFile);
						String fileName = DoFileUtils.getFileNameHasEx(absoluPath);
						log.debug("fileName&&&&&&&&&   " + fileName);
						try {
							log.debug("sourceFile1:  " + sourceFile + "    destDir1:  " + destDir);
							FileUtils.copyFileToDirectory(sourceFile, destDir);
							deleteFileByFlag(publishRoot, dc, resOrder,
									publishDir, publishThumbPath, fileName);
						} catch (IOException e) {
							writeLog(publishRoot, resOrder, dc, "3");
							e.printStackTrace();
						}

					} else { // 原封面文件存在
						try {
							log.debug("sourceFile2:  "+ absolutThumbPathDir + "    destDir2:  " + publishThumbPath);
							deleteFileByFlag(publishRoot, dc, resOrder,
									absolutThumbPathDir, publishThumbPath);
						} catch (Exception e) {
							writeLog(publishRoot, resOrder, dc, "3");
							e.printStackTrace();
						}
					}
				} else {
					// 针对不是图片的资源，则直接去封面目录或者封面文件，仅能处理原封面文件存在的资源
					try {
						log.debug("sourceFile3:  " + absolutThumbPathDir + "    destDir3:  " + publishThumbPath);
						if (thumbPathDirFile.exists()) { // 原封面文件不存在
							deleteFileByFlag(publishRoot, dc, resOrder,
									absolutThumbPathDir, publishThumbPath);
						}
					} catch (Exception e) {
						writeLog(publishRoot, resOrder, dc, "3");
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void deleteFileByFlag(String publishRoot, MetaDataDC dc,
			ResOrder resOrder, String absolutThumbPathDir,
			String publishThumbPath) {
		boolean converFlag = ImgCoverUtil.converAndResizeImage(absolutThumbPathDir, 
			publishThumbPath,164, 123);
		if(!converFlag){
			java.io.File srcPFile = new java.io.File(absolutThumbPathDir);
			java.io.File conPFile = new java.io.File(publishThumbPath);
			if(srcPFile.exists()){
				DoFileUtils.deleteDir(absolutThumbPathDir);
			}
			if(conPFile.exists()){
				DoFileUtils.deleteDir(publishThumbPath);
			}
			writeLog(publishRoot, resOrder, dc, "3");
		}
	}

	private static void deleteFileByFlag(String publishRoot, MetaDataDC dc,
			ResOrder resOrder, String publishDir, String publishThumbPath,
			String fileName) {
		boolean converFlag = ImgCoverUtil.converAndResizeImage(publishDir+"/"+fileName, 
				publishThumbPath,164, 123);
		if(!converFlag){
			java.io.File srcPFile = new java.io.File(publishDir + "/" + fileName);
			java.io.File conPFile = new java.io.File(publishThumbPath);
			if(srcPFile.exists()){
				DoFileUtils.deleteDir(publishDir + "/" + fileName);
				//srcPFile.delete();
			}
			if(conPFile.exists()){
				DoFileUtils.deleteDir(publishThumbPath);
				//conPFile.delete();
			}
			writeLog(publishRoot, resOrder, dc, "3");
		}
	}
		
	/**
	 * 对缩略图缩放处理
	 * @param	absolutThumbPathDir 图片位置
	 * @param	publishThumbPath 处理后图片位置
	 * @param	identifier 标识
	 * @param	publishDir 发布路径
	 * @param	thumbNameNoEx 文件名
	 * @param	publishRoot 发布根路径
	 * @param	dc 元数据
	 * @param	resOrder 需求单
	 * 
	 */
	public static void resizeThumbFile(String absolutThumbPathDir,String publishThumbPath, String identifier ,String publishDir,String thumbNameNoEx, String publishRoot, MetaDataDC dc, ResOrder resOrder){
		try {
			boolean converFlag = ImgCoverUtil.resizeImage(absolutThumbPathDir,
				publishThumbPath,164, 123);
			log.debug("resizeThumbFile method converFlag%%%%%%%%%% "+converFlag);
			if(!converFlag){
				java.io.File srcPFile = new java.io.File(absolutThumbPathDir);
				java.io.File conPFile = new java.io.File(publishDir+"/"+identifier+".png");
				if(srcPFile.exists()){
					DoFileUtils.deleteDir(publishDir+"/"+thumbNameNoEx+".png");
				}
				if(conPFile.exists()){
					DoFileUtils.deleteDir(publishDir+"/"+identifier+".png");
				}
				writeLog(publishRoot, resOrder, dc, "3");
			 }
		 } catch (Exception e) {
			 writeLog(publishRoot, resOrder, dc, "3");
			 e.printStackTrace();
		}
	}
	
	public static List<MetaDataDC> getMetaData(List<String> resIdList){
		List<MetaDataDC> metaDataList = new ArrayList<MetaDataDC>();
		HttpClientUtil http = new HttpClientUtil();
		String resource = "";
		Gson gson = new Gson();
		for(String resId : resIdList){
			resource = http.executeGet(WebappConfigUtil.getParameter("RES_DETAIL_URL")+ "?id=" + resId);
			JSONObject json = JSONObject.fromObject(resource);
			String type = (String) json.get("type");
			if("CA".equals(type)){
				Ca ca = gson.fromJson(resource, Ca.class);
				MetaDataDC dc = new MetaDataDC();
				dc.setCommonMetaData(ca.getCommonMetaData());
				dc.setExtendMetaData(ca.getExtendMetaData());
				dc.setCopyRightMetaData(ca.getCopyRightMetaData());
				dc.setId(resId);
				dc.getCommonMetaData().putCommonMetaData("resourceId", resId);
				dc.setImportXpathName(ca.getImportXpathName());
				metaDataList.add(dc);
			}else{
				log.debug("444+++++++++++");
				Asset asset = gson.fromJson(resource, Asset.class);
				log.debug("afterconverasset###########");
				MetaDataDC dc = new MetaDataDC();
				log.debug("1*****&&&&&&&&&&&");
				dc.setCommonMetaData(asset.getCommonMetaData());
				log.debug("2*****&&&&&&&&&&&");
//				dc.setExtendMetaData(asset.getExtendMetaData());
				log.debug("3*****&&&&&&&&&&&");
//				dc.setCopyRightMetaData(asset.getCopyRightMetaData());
				log.debug("4*****&&&&&&&&&&&");
				dc.setId(resId);
				dc.getCommonMetaData().putCommonMetaData("resourceId", resId);
				log.debug("5*****&&&&&&&&&&&");
				dc.setImportXpathName(asset.getImportXpathName());
				log.debug("6*****&&&&&&&&&&&");
//				asset.getFiles()
				List<com.brainsoon.semantic.ontology.model.File> list = null;
				log.debug("7*****&&&&&&&&&&&");
				if(list!=null&&list.size()>=1){
					dc.getCommonMetaData().putCommonMetaData("filePath", list.get(0).getPath());
					dc.getCommonMetaData().putCommonMetaData("fileByte", list.get(0).getFileByte());
				}
				log.debug("8*****&&&&&&&&&&&");
				metaDataList.add(dc);
			}
		}
		return metaDataList;
	}
	
	public static List<String> getResIdList(String filePath) {
		List<String> resIdList = new ArrayList<String>();
		try {
			String encoding = "UTF-8";
			java.io.File file = new java.io.File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					String[] arr = lineTxt.split(",");
					if(arr!=null&&arr.length>0)
					resIdList.add(arr[0]);
					System.out.println(lineTxt);
				}
				bufferedReader.close();
				read.close();
				if(bufferedReader!=null){
					bufferedReader = null;
				}
				if(read!=null){
					read = null;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resIdList;
	}
	
	/**
	 * 只针对封面特殊处理程序
	 * @param	sourceRoot 源文件根路径
	 * @param	publishRoot	发布根路径
	 * @param	resOrder
	 * 
	 */
	public static void copyThumb(String sourceRoot, String publishRoot, ResOrder resOrder){
		try{
			String time2Str = DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replace(":", "").replace(" ", "");
			String publishDir = publishRoot + time2Str + "/"
					+ resOrder.getOrderId() + "/" + "resouce_new/releasedJpgs";
			java.io.File pubFile = new java.io.File(publishDir);
			if(!pubFile.exists()){
				pubFile.mkdirs();
			}
			String classPath = WebAppUtils.getWebAppClassesPath().replaceAll("\\\\", "\\/");
			//String resBaseRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileRoot).replace("\\\\", "\\/");
			String filePath = classPath + "/miss.txt";
			List<String> resIdList = getResIdList(filePath);
			List<MetaDataDC> metaDataDcList = getMetaData(resIdList);
			for(MetaDataDC dc : metaDataDcList){
				String path = dc.getCommonMetaData().getCommonMetaValue("filePath");
				String basePath = sourceRoot + path;
				java.io.File baseFile = new java.io.File(basePath);
				String sufix = DoFileUtils.getExtensionName(basePath).toLowerCase();
				if(path!=null){
					path = path.replace("\\\\", "\\/");
					log.debug("1111&&&&&&& "+path);
					String thumbPath = sourceRoot + path.substring(0, path.lastIndexOf("/") + 1) +"thumb/cover.jpg";
					log.debug("copyThumbPath *********"+thumbPath);
					String identifier = dc.getCommonMetaData().getIdentifier();
					log.debug("2222identifier&&&&&&& "+identifier);
					java.io.File file = new java.io.File(thumbPath);
					if(file.exists()&&file.isFile()){
						try {
							log.debug("3333path&&&&&&& " + thumbPath);
							FileToolkit.copyFile(thumbPath, publishDir);
							log.debug("4444path&&&&&&& " + publishDir + "/cover.jpg");
							log.debug("5555path&&&&&&& " + publishDir + "/" + identifier + ".png");
							resizeThumbFile(publishDir + "/cover.jpg", 
									publishDir + "/" + identifier + ".png", 
									identifier, publishDir, 
									"cover", 
									publishRoot, 
									dc, 
									resOrder
							);
						} catch (IOException e) {
							writeLog(publishRoot, resOrder, dc, "3");
							e.printStackTrace();
						}
						
					}else{
						log.debug("@@@@111no exist########");
						if(PropertiesReader.getInstance().getProperty(ConstantsDef.pictureFormat).contains(sufix)){
							log.debug("@@@@222no exist picture######## " + sufix);
							String name = DoFileUtils.getFileNameHasEx(basePath);
							log.debug("@@@@333no exist picture######## " + name);
							String thumbNameNoEx = DoFileUtils.getFileNameNoEx(basePath);
							log.debug("@@@@444no exist picture######## " + thumbNameNoEx);
							String publishThumbPath = publishDir + "/" + identifier +".png" ;
							log.debug("@@@@555no exist picture######## " + publishThumbPath);
							if(baseFile.exists()&&baseFile.isFile()){
								try {
									FileToolkit.copyFile(basePath, publishDir);
									log.debug("@@@@666no exist picture######## " + publishDir + "/" + name);
									resizeThumbFile(publishDir + "/" + name, 
											publishThumbPath, 
											identifier, publishDir,
											thumbNameNoEx, 
											publishRoot,
											dc,
											resOrder
									);
								} catch (IOException e) {
									writeLog(publishRoot, resOrder, dc, "3");
									e.printStackTrace();
								}
								
							}else{
								log.debug("@@@@777no exist no picture######## ");
								writeLog(publishRoot, resOrder, dc, "3");
							}
						}
					}
				}else{
					log.debug("@@@@8888no exist no picture######## ");
					writeLog(publishRoot, resOrder, dc, "3");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 针对中国移动定制路径
	 * 拷贝asset源文件到移动指定目录
	 * @param fileRoot 源文件根目录
	 * @param publishRoot 发布目录
	 * @param resOrder 需求单
	 * @param dc 元数据
	 * @return int  1:成功  0：失败
	 * 
	 */
	public static int copyBaseAssetToDir(String fileRoot, String publishRoot, ResOrder resOrder, MetaDataDC dc){
		int flag = 1;
		try {
			String filePath = dc.getCommonMetaData().getCommonMetaValue("filePath");
			resOrder.getTemplate().getSupplier();
			String identifier = dc.getCommonMetaData().getIdentifier();
				String eduSrcPath = fileRoot.replace("\\\\", "\\/") + filePath.replace("\\\\", "\\/");
				java.io.File srcEduFile = new java.io.File(eduSrcPath);
				String time2Str = DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replace(":", "").replace(" ", "");
				String publishDir = publishRoot + time2Str +"/"+ resOrder.getOrderId() 
						+ "/" + "resouce_new/releasedResources";
				java.io.File publishFile = new java.io.File(publishDir);
				if(!publishFile.exists()){
					publishFile.mkdirs();
				}
				if(srcEduFile.exists() && !srcEduFile.isDirectory()){
					try {
						org.apache.commons.io.FileUtils.copyFileToDirectory(srcEduFile, publishFile);
						//重命名文件 
						String fileName = new java.io.File(eduSrcPath).getName();
						renameFile(identifier, publishDir+"/"+fileName);
						//flag = 0;
					} catch (IOException e) {
						flag = 0;
						writeLog(publishRoot, resOrder, dc, "1");
						e.printStackTrace();
					}
				}else{
					flag = 0;
					writeLog(publishRoot, resOrder, dc, "1");
				}
			//}
		} catch (Exception e) {
			flag = 0;
			writeLog(publishRoot, resOrder, dc, "1");
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 针对中国移动定制路径
	 * 拷贝转换后的asset文件到移动指定目录
	 *（1）文档：doc,docx,ppt,pptx,ppsx,pdf->swf		图片类源文件全部需要转换程序转换   全部需要拷贝到发布后resouce_new/releasedSwfs目录下
	 *（2）图片：gif,jpg,png,jpeg,bmp->png		图片类源文件png格式转换程序不转换   但是需要本发布程序转换成png格式  并拷贝到发布后resouce_new/releasedSwfs目录下
	 *（3）音频： mp3,wav,wma->mp3		音频类源文件mp3格式转换程序不转换   也不需要拷贝到发布后resouce_new/releasedSwfs目录下
	 *（4）视频： rmvb,avi,wmv,asf,mpeg,mpeg2,3gp,flv,mp4,f4v,f5v,f6v->flv  视频类源文件flv格式转换程序不转换 也不需要拷贝到发布后resouce_new/releasedSwfs目录下
	 *（5）动画： swf->swf   动画类源文件全部为swf格式转换程序不转换 也不需要拷贝到发布后resouce_new/releasedSwfs目录下
	 * @param fileRoot 源文件根目录
	 * @param publishRoot 发布目录
	 * @param resOrder 需求单
	 * @param dc 元数据
	 */
	public static String copyConverAssetToDir(String fileRoot, String publishRoot, ResOrder resOrder, MetaDataDC dc){
		String identifier = dc.getCommonMetaData().getIdentifier();
		String time2Str = DateUtil.convertDateTimeToString(resOrder.getCreateTime());
		time2Str = time2Str.replace(":", "").replace(" ", "");
		//转换后文件发布路径: 发布根路径/需求单创建日期/需求单id/资源id/resouce_new/releasedSwfs/转换后文件
		String publishDir = publishRoot + time2Str +"/"+ resOrder.getOrderId()
				+ "/" + "resouce_new/releasedSwfs";
		java.io.File publishFile = new java.io.File(publishDir);
		if(!publishFile.exists()){
			publishFile.mkdirs();
		}
//		for(com.brainsoon.semantic.ontology.model.File file:files){
			//String filePath = file.getPath().replace("\\\\", "\\/");
			String filePath = dc.getCommonMetaData().getCommonMetaValue("filePath").replace("\\\\", "\\/");
			String fileType = DoFileUtils.getExtensionName(filePath).toLowerCase();
			String fileName = DoFileUtils.getFileNameHasEx(filePath);
			String fileNameNoSufix = DoFileUtils.getFileNameNoEx(filePath);
			String pfilePath = filePath.substring(0, filePath.lastIndexOf("/"));
			//转换后源文件全路径：  转换根路径/源文件路径（文件所在文件夹）/文件扩名（小写）/文件名
			String converfilePath = converFileRoot  + pfilePath + "/" + fileType + "/" ;
			String coverFileNosufix = converfilePath+fileNameNoSufix;
			try {
				if (PropertiesReader.getInstance().getProperty(ConstantsDef.videoFormat).contains(fileType)) { //视频
					if(!fileType.equals("flv")){  //从转换文件中取
						java.io.File flvFile = new java.io.File(coverFileNosufix+".flv");
						if(flvFile.exists()){
							FileToolkit.copyFile(coverFileNosufix+".flv", publishDir);
							renameFile(identifier, publishDir+"/"+fileNameNoSufix+".flv");
						}else{
							writeLog(publishRoot, resOrder, dc, "2");
						}
					}
				}else if (PropertiesReader.getInstance().getProperty(ConstantsDef.audioFormat).contains(fileType)) { //音频
					if(!fileType.equals("mp3")){//从转换文件中取
						java.io.File flvFile = new java.io.File(coverFileNosufix+".mp3");
						if(flvFile.exists()){
							FileToolkit.copyFile(coverFileNosufix+".mp3", publishDir);
							renameFile(identifier, publishDir+"/"+fileNameNoSufix+".mp3");
						}else{
							writeLog(publishRoot, resOrder, dc, "2");
						}
					}
				}else if (PropertiesReader.getInstance().getProperty(ConstantsDef.documentFormat).contains(fileType)) {//文档
					//全部从转换文件中取
					java.io.File flvFile = new java.io.File(coverFileNosufix+".swf");
					if(flvFile.exists()){
						FileToolkit.copyFile(coverFileNosufix+".swf", publishDir);
						renameFile(identifier, publishDir+"/"+fileNameNoSufix+".swf");
					}else{
						writeLog(publishRoot, resOrder, dc, "2");
					}
				}else if(PropertiesReader.getInstance().getProperty(ConstantsDef.pictureFormat).contains(fileType)){  //图片  
					if(!"png".equals(fileType)){
						String fileRootPath = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileRoot) + filePath;
						if(new java.io.File(fileRootPath).exists()){
							FileToolkit.copyFile(fileRootPath, publishDir);
							boolean flag = ImgCoverUtil.conver2Other(publishDir+"/"+fileName, publishDir+"/"+identifier+".png");
							
							if(flag){
								
							}else{
								writeLog(publishRoot, resOrder, dc, "2");
							}
							DoFileUtils.deleteDir(publishDir+"/"+fileName);
						}else{
							writeLog(publishRoot, resOrder, dc, "2");
						}
					}
				}
			} catch (IOException e) {
				writeLog(publishRoot, resOrder, dc, "2");
				e.printStackTrace();
			}
//		}
		return "";
	}
	
	/**
	 * 重命名文件
	 * @param identifier  资源标识
	 * @param path 资源路径
	 */
	public static boolean renameFile(String identifier, String path){
		boolean flag = true;
		log.debug(path+"      rename1&&&&&&&&&");
		java.io.File srcFile = new java.io.File(path);
		String name = DoFileUtils.getFileNameNoEx(path);
		log.debug(name+"      rename2&&&&&&&&&");
		String sufix = DoFileUtils.getExtensionName(path);
		log.debug(sufix+"      rename3&&&&&&&&&");
		flag = srcFile.renameTo(new java.io.File(path.substring(0, path.lastIndexOf("/"))+"/"+identifier+"."+sufix));
		return flag;
	}
	
	/**
	 * 向日志记录中写日志 
	 * 日志格式   资源名称=资源ID,资源路径
	 * @param publishRoot 发布根路径
	 * @param resOrder 需求单对象
	 * @param dc 元数据对象
	 * @param type 日志类型  1：源文件日志   2：转换后文件日志    3：封面图片日志
	 */
	public static void writeLog(String publishRoot, ResOrder resOrder, MetaDataDC dc, String type){
		try {
			String time2Str = DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replace(":", "").replace(" ", ""); 
			//转换后文件路径: 发布根路径/需求单创建日期/需求单id/资源id/resouce_new/releasedSwfs/转换后文件
			String logDir = publishRoot + time2Str +"/"+ resOrder.getOrderId()
					+ "/log/";
			FileWriter fileWriter = null;
			java.io.File logFile = new java.io.File(logDir);
			if(!logFile.exists()){
				logFile.mkdirs();
			}
			String filePath = dc.getCommonMetaData().getCommonMetaValue("filePath");
			//fileWriter.write("资源名称="+"资源ID"+",资源路径"+"\r\n");
			if(type.equals("1")){//源文件
				String pathName = logDir+"baseResource.txt";
				java.io.File logPath = new java.io.File(pathName);
				if(!logPath.exists()){
					logPath.createNewFile();
				}
				fileWriter = new FileWriter(pathName, true);
				
				log.debug("1%%%%%%%%%%%%%%%%%baseFile:     "+filePath);
				fileWriter.write(dc.getCommonMetaData().getTitle()+"="+dc.getCommonMetaData().getCommonMetaValue("resourceId")+","+filePath+"\r\n");
			}else if(type.equals("2")){//转换后文件
				String pathName = logDir+"/"+"converResource.txt";
				java.io.File logPath = new java.io.File(pathName);
				if(!logPath.exists()){
					logPath.createNewFile();
				}
				fileWriter = new FileWriter(pathName, true);
				//String filePath = dc.getCommonMetaData().getCommonMetaValue("filePath");
				String nameWithSufix = DoFileUtils.getFileNameNoEx(filePath);
				String nameNosufix = DoFileUtils.getFileNameNoEx(filePath);
				String sufix = DoFileUtils.getExtensionName(filePath);
				String converPath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" + sufix+"/";
				log.debug("2%%%%%%%%%%%%%%%converFile:    "+converPath);
				log.debug("2%%%%%%%%%%%%%%%%%baseFile:    "+filePath);
				//fileWriter.write(dc.getCommonMetaData().getTitle()+"="+dc.getCommonMetaData().getCommonMetaValue("resourceId")+","+converPath+"\r\n");
				fileWriter.write(dc.getCommonMetaData().getTitle()+"="+dc.getCommonMetaData().getCommonMetaValue("resourceId")+","+filePath+"\r\n");
			}else{//封面图片
				String pathName = logDir+"/"+"thumbResource.txt";
				java.io.File logPath = new java.io.File(pathName);
				if(!logPath.exists()){
					logPath.createNewFile();
				}
				fileWriter = new FileWriter(pathName, true);
				fileWriter.write(dc.getCommonMetaData().getTitle()+"="+dc.getCommonMetaData().getCommonMetaValue("resourceId")+","+filePath+"\r\n");
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据需求单目录挑选文件
	 * @param fileDir 文件目录
	 * @param sysDirs 需要挑选的目录
	 */
	public static String getFileDirFromDirs(String fileDir, String[] sysDirs){
		String[] pathArr = fileDir.split("/");
		String str = "";
		for(String sysDir :sysDirs){
			if(str!=""){
				break;
			}
			for(String arr: pathArr){
				if(sysDir.equals(arr)){
					str = sysDir;
					break;
				}
			}
		}
		if(!"".equals(str)){
			return fileDir;
		}
		return "";
	}
	
	/**
	 * 
	 * 针对华师京城按目录发布
	 * @param fileRoot 资源根路径
	 * @param publishRoot 发布根路径
	 * @param resOrder 需求单
	 * @param dc 元数据
	 * 
	 */
	public static void copyFileByResourceDir(String fileRoot, String publishRoot, ResOrder resOrder, MetaDataDC dc){
		String time2Str = DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replace(":", "").replace(" ", ""); 
		int platformId= resOrder.getPlatformId();
		String dir = getResourceDir(dc, platformId);
		//发布后文件路径: 发布根路径/需求单创建日期/需求单id/资源目录结构/资源文件
		String publishedDir = publishRoot + time2Str +"/" + resOrder.getOrderId() + "/" + dir;
		java.io.File distFolder = new java.io.File(publishedDir);
		if(!distFolder.exists()){
			distFolder.mkdirs();
		}
		log.debug(platformId+"   platformId**********");
		if(platformId==1){
			String filePath = dc.getCommonMetaData().getCommonMetaValue("filePath");
			String resFilePath = fileRoot.replace("\\\\", "\\/") + filePath.replace("\\\\", "\\/");
			try {
				log.debug("resFilePath:  "+resFilePath+"         publishedDir:   "+publishedDir+"     +++++++++++++++++++++");
				if(FileUtil.isFileExist(resFilePath)){
					FileToolkit.copyFile(resFilePath, publishedDir);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			
		}
	}
	
	/**
	 * 
	 * 教育库教育资源目录：    资源模块\资源类型\教材版本\学段\学科\年级\分册\章\节\资源类型 
	 * 教育库数字图书目录：    资源模块\数字图书\学段\资源类型 
	 * 出版库数字图书目录：
	 */
	public static String getResourceDir(MetaDataDC dc, int platformId){
		String dir = "";
		//教育库
		if(platformId==1){
			String module = dc.getCommonMetaData().getCommonMetaValue("module"); //资源模块   
			String moduleName = SystemConstants.ResourceMoudle.getValueByKey(module);
			String educational_phase = dc.getCommonMetaData().getCommonMetaValue("educational_phase");  //学段
			String educational_phase_name = dc.getCommonMetaData().getCommonMetaValue("educational_phase_name");
			String resType = dc.getCommonMetaData().getCommonMetaValue("type");  //资源类型
			String resTypeName = SystemConstants.ResourceType.getValueByKey(resType);
			if(!resType.equals(SystemConstants.ResourceType.TYPE5)){//教育资源
				String versionName = dc.getCommonMetaData().getCommonMetaValue("versionName");  //教材版本
				String subject = dc.getCommonMetaData().getCommonMetaValue("subject");  //学科
				//String subjectName = dc.getCommonMetaData().getCommonMetaValue("subjectName");
				String subjectName = getDictNameService().getDictMapByName("学科").get(subject);
				String grade = dc.getCommonMetaData().getCommonMetaValue("grade");  //年级
				String gradeName = dc.getCommonMetaData().getCommonMetaValue("gradeName");  
				String fascicule = dc.getCommonMetaData().getCommonMetaValue("fascicule");  //分册
				String fasciculeName = dc.getCommonMetaData().getCommonMetaValue("fasciculeName");
				String chapter_name = dc.getCommonMetaData().getCommonMetaValue("chapter_name");  //章
				String node_name = dc.getCommonMetaData().getCommonMetaValue("node_name");  //节
				if(StringUtils.isNotBlank(node_name)){
					dir = moduleName + "/" + resTypeName + "/" + versionName + "/" + educational_phase_name + 
							"/" + subjectName + "/" + gradeName +"/"+ fasciculeName + 
							"/" + chapter_name + "/" + node_name + "/" + resTypeName +"/";
				}else{
					dir = moduleName + "/" + resTypeName + "/" + versionName + "/" + educational_phase_name + 
							"/" + subjectName + "/" + gradeName +"/"+ fasciculeName + 
							"/" + chapter_name + "/" + resTypeName +"/";
				}
			}else{//数字图书
				dir = moduleName + "/数字图书/" + educational_phase_name + "/" + resTypeName + "/";
			}
		}else{//出版库
			//TODO
			dir = "";
		}
		return dir;
	}
	
	public static IDictNameService getDictNameService(){
		IDictNameService dictNameService = null;
		try {
			dictNameService = (IDictNameService) BeanFactoryUtil.getBean("dictNameService");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dictNameService;
	}
	
	/**
	 * 针对中版集团定制路径
	 * 拷贝源文件到移动指定目录
	 * @param fileRoot 源文件根目录
	 * @param publishRoot 发布目录（拷贝后改变文件名称）
	 * @param publishFileDir 
	 * @param resOrder 需求单
	 * @param arr 资源文件
	 * @return int  1:成功  0：失败
	 * 
	 */
	public static int copyFileToFile(String fileRoot, String publishRoot, String publishFileDir, ResOrder resOrder, String[] arr){
		int flag = ResReleaseConstant.PublishResFileDesc.FILE_COPY_SUCCESS;
		try {
			String time2Str = DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replace(":", "").replace(" ", "");
			//资源发布路径为： 发布根路径/需求单创建时间/需求单id/releasedResources/资源id/资源名称/文件
			String publishDir = publishRootDir + time2Str +"/"+ resOrder.getOrderId() + "/releasedResources/";//源文件发布路径
			java.io.File publishFile = new java.io.File(publishDir + publishFileDir);
			if(!publishFile.exists()){
				publishFile.mkdirs();
			}
			String path = arr[2];
			String eduSrcPath = fileRoot.replace("\\\\", "\\/") + path.replace("\\\\", "\\/");//文件的绝对路径
			java.io.File srcEduFile = new java.io.File(eduSrcPath);
			if(srcEduFile.exists() && !srcEduFile.isDirectory()){
				try{
					//org.apache.commons.io.FileUtils.copyFileToDirectory(srcEduFile, publishFile);
					java.io.File file = new java.io.File(publishDir + publishRoot);
					FileUtils.copyFile(srcEduFile, file);
				}catch(Exception e){
					flag = ResReleaseConstant.PublishResFileDesc.FILE_COPY_FAILED;
				}
				
			}else{
				flag = ResReleaseConstant.PublishResFileDesc.FILE_MISSING;
			}
		} catch (Exception e) {
			flag = ResReleaseConstant.PublishResFileDesc.OTHER_FAILED;
			log.error("拷贝文件失败..........");
			e.printStackTrace();
		}
		return flag;
	}
	
	public static void main(String[] args) {
		/*String srcPath = "C:/Users/root/Desktop/converfile";
		String traPath = "C:/Users/root/Desktop/converfile/temp";
		java.io.File file = new java.io.File(srcPath);
		java.io.File[] files = 	file.listFiles();
		int succ = 0;
		int fail = 0;
		for(java.io.File f1:files){
			String srcFilePaths = f1.getAbsolutePath();
			String fileName = DoFileUtils.getFileNameNoEx(srcFilePaths);
			boolean converFlag = 
					ImgCoverUtil.converAndResizeImage(srcFilePaths, 
							traPath+"/"+fileName+".png", 
					164, 123);
			if(converFlag){
				succ++;
			}else{
				fail++;
			}
		}
		System.out.println("succ:"+succ+"fail:"+fail);
		ResOrder resOrder = new ResOrder();
		resOrder.setOrderId(10000L);
		resOrder.setCreateTime(new Date());
		String sourceRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileRoot).replace("\\\\", "\\/");
		String publishRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile).replace("\\\\", "\\/");
		copyThumb(sourceRoot, publishRoot, resOrder);*/
		try{
			int i=0;
			try{
				int k = 2/i;
			}catch(Exception e){
				System.out.println("343434");
			}
		}catch(Exception e){
			System.out.println("aaaaaaaaaaa");
		}
	}
}
