package com.brainsoon.resource.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.content.DocUtil;
import com.brainsoon.common.util.dofile.content.PdfUtil;
import com.brainsoon.common.util.dofile.content.PptUtils;
import com.brainsoon.common.util.dofile.conver.ConverUtils;
import com.brainsoon.common.util.dofile.image.ImgCoverUtil;
import com.brainsoon.common.util.dofile.metadata.FileMetadataFactory;
import com.brainsoon.common.util.dofile.metadata.fo.Multimedia;
import com.brainsoon.common.util.dofile.metadata.fo.Picture;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.dofile.zip.ZipUtil;
import com.brainsoon.docviewer.model.ResConverfileTask;
import com.brainsoon.docviewer.service.IResConverfileTaskService;
import com.brainsoon.resource.action.BresAction;
import com.brainsoon.resource.po.FileDataInfo;
import com.brainsoon.resource.po.UploadTaskDetail;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.support.ExcelUtil;
import com.brainsoon.resource.support.ImportResExcelFile;
import com.brainsoon.resource.support.ResourceTypeUtils;
import com.brainsoon.resource.support.SemanticResponse;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.resource.util.MD5Util;
import com.brainsoon.resource.util.RandomNumberGenerator;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CommonMetaData;
import com.brainsoon.semantic.ontology.model.Domain;
import com.brainsoon.semantic.ontology.model.ResBaseObject;
import com.brainsoon.statistics.service.IEffectNumService;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants.ImportStatus;
import com.brainsoon.system.support.SystemConstants.OperatType;
import com.google.gson.Gson;

@Service
public class ResourceService extends BaseService implements IResourceService {
	
	public  static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
	public  static final String FILE_TEMP = StringUtils.replace(WebAppUtils.getWebRootBaseDir("fileTemp"),"\\", "/");
	private final static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
	private final static SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private final static SimpleDateFormat dateformat3 = new SimpleDateFormat("yyyyMMdd");
	
	private final static int maxFileNum = 1000;
	
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired 
	private IResConverfileTaskService resConverfileTaskService;
	@Autowired
	private IEffectNumService iEffectNumService;
	/**
	 * 处理不存在的资源文件
	 * @param module
	 * @param type
	 * @return
	 */
	public void doResFile(){
		List<String> resIdAndPaths = ExcelUtil.readTxtFile(FILE_ROOT+"9.txt");
		if(resIdAndPaths!=null && resIdAndPaths.size()>0){
			for(String resIdAndPath:resIdAndPaths){
				String[] idAndPath = resIdAndPath.split(",");
				String resId = idAndPath[0];
				String path = idAndPath[1];
				String fileType =path.substring(path.lastIndexOf(".")+1,path.length());
				String identitfy = path.substring(0,path.lastIndexOf("/"));
				String newFileName =  identitfy.substring(identitfy.lastIndexOf("/")+1,identitfy.length())+"."+fileType;
				if(!ExcelUtil.isImage(fileType)){
					ResConverfileTask rcft = new ResConverfileTask();
					rcft.setResId(resId);
					rcft.setSrcPath(path);
					rcft.setPlatformId(1);
					resConverfileTaskService.saveResConverfileTask(rcft);
				}
				String newDir = FILE_TEMP+"resFileDir";
				File newFileDir = new File(newDir);
				if(!newFileDir.exists()){
					newFileDir.mkdirs();
				}
				newDir = newDir.replaceAll("\\\\", "/");
				File srcFile = new File(FILE_ROOT+path);
				File destFile = new File(newDir+"/"+newFileName);
				logger.info("222222222222222222====="+FILE_ROOT+path);
				logger.info("33333333333333========"+newDir+"/"+newFileName);
				if(srcFile.exists()){
					try {
						FileUtils.copyFile(srcFile, destFile);
					} catch (IOException e) {
						logger.error("保存失败" + e.getMessage());
					}
				}
			}
			
		}
	}
	
	public FileDataInfo getFileDataInfo(String module,String type){
		String hql = "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		if(module == null || "".equals(module)){
			hql = "FROM FileDataInfo c WHERE c.resType = :resType";
		}else{
			hql = "FROM FileDataInfo c WHERE c.resModule = :resModule and c.resType = :resType";
			parameters.put("resModule", module);
		}
		parameters.put("resType", type);
		List<?> rs = query(hql, parameters);
		FileDataInfo use = null;
		if(null != rs && rs.size() > 0){
			use = (FileDataInfo)rs.get(0);
		}
		return use;
	}
	public String doGetCurrentUseFolder(String module,String type){
		FileDataInfo use = getFileDataInfo(module, type);
		if(null == use){
			use = new FileDataInfo();
			if(module !=null && !"".equals(module)){
				use.setResModule(module);
			}
			use.setResType(type);
			use.setFileFolder("G00001");
			use.setFileNum(0);
			create(use);
		}
		return use.getFileFolder();
	}
	public void doIncreaseNum(String module,String type){
		FileDataInfo use = getFileDataInfo(module, type);
		if(null != use){
			int num = use.getFileNum()+1;
			if(num >= maxFileNum){//一千计数
				//新建文件夹
				String folder = use.getFileFolder();
				folder = StringUtils.substring(folder, 1);
				
				folder = "G" + (Integer.parseInt(folder) + 1);
				use.setFileFolder(folder);
				use.setFileNum(0);
			}else{
				use.setFileNum(num);
			}
			saveOrUpdate(use);
		}
	}
	public void doReduceNum(String module,String type){
		FileDataInfo use = getFileDataInfo(module, type);
		if(null != use){
			int num = use.getFileNum() - 1;
			use.setFileNum(num);
			saveOrUpdate(use);
		}
	}
	/**
	 * 拷贝文件到指定目录
	 * @param parentPath
	 * @param srcFile
	 * @param leave 是否保留原文件
	 * @return File
	 */
	public File saveFile(String parentPath,File srcFile,boolean leave){
		String fileName = srcFile.getName();
		File destFile = new File(parentPath + File.separator + fileName);
		try {
			//判断如果文件已经存在，则重命名
			if(destFile.exists()){
				destFile = getNotExistsFile(destFile);
			}
			logger.info(srcFile+"=======bbbbbbbbbbbbbbbbb========"+destFile);
			FileUtils.copyFile(srcFile, destFile);
			if(!leave){
				srcFile.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return destFile;
	}
	public File getNotExistsFile(File destFile){
		String fileName = destFile.getName();
		if(destFile.exists()){
			fileName = StringUtils.substringBeforeLast(fileName, ".") + "_" + RandomNumberGenerator.generateNumber3() + "." +StringUtils.substringAfterLast(fileName, ".");
			destFile = new File(destFile.getParent() + File.separator + fileName);
			getNotExistsFile(destFile);
		}
		return destFile;
	}
	/**
	 * 创建资源存储的父路径，计数加1
	 * @param module
	 * @param type
	 * @param doi
	 * @return String
	 */
	public String createParentPath(String module,String type,String doi){
		String useFolder = doGetCurrentUseFolder(module, type);
		//把doi中的/替换为-，把doi中的点分隔换成_  ,因为doi规则里，不存在节点时，采用空字符串占位，所有有可能已点结束，创建文件夹会出错
		doi = StringUtils.replace(doi, "/", "-");
		doi = StringUtils.replace(doi, ".", "_");
		String parentPath = FILE_ROOT + module + File.separator + type + File.separator + useFolder + File.separator + doi;
		File parentFile = new File(parentPath);
		if(!parentFile.exists()){
			//增加计数
			doIncreaseNum(module, type);
			parentFile.mkdirs();
		}else{
			try {
				FileUtils.deleteDirectory(parentFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(!parentFile.exists()){
				parentFile.mkdirs();
			}
		}
		return parentPath;
	}
	/**
	 * 出版库 创建资源存储的父路径，计数加1
	 * @param module
	 * @param type
	 * @param doi
	 * @return String
	 */
	public String createPublishParentPath(String publishType,String doi){
		String useFolder = doGetCurrentUseFolder("", publishType);
		//把doi中的/替换为-，把doi中的点分隔换成_  ,因为doi规则里，不存在节点时，采用空字符串占位，所有有可能已点结束，创建文件夹会出错
	//	doi = StringUtils.replace(doi, "/", "-");
	//	doi = StringUtils.replace(doi, ".", "_");
		String uuid = UUID.randomUUID().toString();
		String parentPath = FILE_ROOT+ publishType + File.separator + useFolder + File.separator + uuid;
		File parentFile = new File(parentPath);
		if(!parentFile.exists()){
			//增加计数
			doIncreaseNum("", publishType);
			parentFile.mkdirs();
		}else{
//			try {
//				FileUtils.deleteDirectory(parentFile);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			if(!parentFile.exists()){
//				parentFile.mkdirs();
//			}
		}
		return parentPath;
	}
	/**
	 * 保存资源
	 * 1、生成DOI，并赋值 identifier
	 * 2、归类文件
	 * 3、调用接口创建资源
	 * @param asset
	 * @param uploadFile 上传文件的相对路径(包含文件名，相对于FILE_TEMP)
	 * @param uploadFileFullPath 上传文件的绝对路径
	 * @param thumbFile 上传缩略图的相对路径(包含文件名，相对于FILE_TEMP)
	 * @param loginName
	 * @param repeatType
	 * @return String 资源id
	 */
	public String saveResourceAndThumb(Asset asset,String uploadFile,String uploadFileFullPath,String thumbFile,String loginName,String repeatType){
		com.brainsoon.semantic.ontology.model.File modelFile = null;
		//第一步 生成DOI
		CommonMetaData commonMetaData = asset.getCommonMetaData();
		String doi = ResourceTypeUtils.getDOIByAsset(asset);
		commonMetaData.setIdentifier(doi);
		String title = commonMetaData.getTitle();
		if(commonMetaData.getResVersion()==null || "".equals(commonMetaData.getResVersion())){
			commonMetaData.setResVersion("1");
		}
		String keyword = commonMetaData.getKeywords();
		if(StringUtils.isNotBlank(keyword) && keyword.indexOf("，")>=0){
			keyword = keyword.replaceAll("，", ",");
			commonMetaData.setKeywords(keyword);
		}
		File destFile = null;
		//第二步，根据DOI归类文件
		String objectId = asset.getObjectId();
		
		boolean leave = false;
		File realFile = null;
		if(StringUtils.isNotBlank(uploadFile)){
			realFile = new File(BresAction.FILE_TEMP + uploadFile);
		}
		if(null == realFile && StringUtils.isNotBlank(uploadFileFullPath)){
			realFile = new File(uploadFileFullPath);
			leave = true;//暂时保留ftp上的文件
		}
		String  returnValue = "0";  //1是需要转换的文件   长串是封面的路径  0 是不需要转换也不是封面路径
		String path = "";
		String srcFileType = "";
		String description = "";
		if(StringUtils.isBlank(commonMetaData.getDescription())){   //摘要为空的话提取第一页的内容为摘要
			description =title+","+keyword;
		}else{
			description = commonMetaData.getDescription();
		}
		if(null != realFile){
			//上传文件
			String parentPath = "";
			if(StringUtils.isNotBlank(objectId) && !StringUtils.equalsIgnoreCase(repeatType, "2")){
				//获取之前的文件对象
//				asset.getFiles()
				List<com.brainsoon.semantic.ontology.model.File> files = null;
				if(null != files && files.size() > 0){
					parentPath = files.get(0).getPath();
				}
			}
			if(StringUtils.isBlank(parentPath)){
				String module = commonMetaData.getModule();
				String type = commonMetaData.getType();
				//按要求生成目录
				parentPath = createParentPath(module, type, doi);
			}else{
				parentPath = FILE_ROOT + StringUtils.substringBeforeLast(parentPath, "/");
			}
			logger.info("wwwwwwwwwww==========="+parentPath);
			if(!new File(parentPath).exists()){
				logger.info("lllllllllll==========="+parentPath);
				new File(parentPath).mkdirs();
			}
			destFile = saveFile(parentPath, realFile,leave);
			path = StringUtils.replace(destFile.getAbsolutePath(),"\\", "/");
			srcFileType = srcFileType.substring(srcFileType.lastIndexOf(".")+1,srcFileType.length());
			//提取默认元数据，生成文件缩略图
			String fileName = destFile.getName();
			String ext = "." + StringUtils.substringAfterLast(fileName, ".");
			String metaFlag = ResourceTypeUtils.getExtendMetaDataFlag(asset.getType(), ext);
			String coverFileLType = "";
			if(StringUtils.isNotBlank(thumbFile)){
				coverFileLType = "." + StringUtils.substringAfterLast(thumbFile, ".");
			}else{
				coverFileLType = ".jpg";
			}
//			Map<String, String> extendMetaDatas = asset.getExtendMetaData().getExtendMetaDatas();
//			returnValue = doSetExtendMetasAndCreateThumb(path, coverFileLType,thumbFile,metaFlag, extendMetaDatas);
			logger.info("uuuuuuuuuuuuuuuuuuuuuuuuuuuu"+returnValue);
			if(!"0".equals(returnValue) && !"1".equals(returnValue)){
				commonMetaData.setPath(StringUtils.replace(returnValue, FILE_ROOT, ""));  //封面的路径
			}
			modelFile = new com.brainsoon.semantic.ontology.model.File();
			//设置file
			path = StringUtils.replace(path, FILE_ROOT, "");
			modelFile.setPath(path);  //文件的路径
			try {
				modelFile.setMd5(MD5Util.getFileMD5String(destFile));
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			modelFile.setName(destFile.getName());
			modelFile.setFileByte(destFile.length()+"");
			String now = dateformat.format(new Date());
			modelFile.setModified_time(now);
//			modelFile.setPublisher(loginName);
//			if(!".".equals(ext)){
//				ext = ext.substring(1,ext.length());
//				modelFile.setFormat(ext);
//				commonMetaData.setFormat(ext);
//			}
			List<com.brainsoon.semantic.ontology.model.File> modelFiles = new ArrayList<com.brainsoon.semantic.ontology.model.File>(); 
			modelFiles.add(modelFile);
//			asset.setFiles(modelFiles);
			logger.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		}else {
			if(StringUtils.isBlank(objectId)){
//				asset.setFiles(null);
			}
			if(StringUtils.isBlank(commonMetaData.getDescription())){   //摘要为空的话提取第一页的内容为摘要
				commonMetaData.setDescription(title+","+keyword);
			}
			returnValue = "-1";
		}
		
		//第三步创建对象
		Date now = new Date();
		commonMetaData.setModified_time(dateformat.format(now));
		if(StringUtils.isBlank(objectId)){
			commonMetaData.putCommonMetaData("create_date", dateformat3.format(now));
			commonMetaData.putCommonMetaData("create_time", dateformat.format(now));
		}
		logger.info("==============duuuddddddd==========d");
		//设置状态
		if(StringUtils.isBlank(commonMetaData.getStatus())){
			commonMetaData.setStatus("0");
		}
		commonMetaData.setDescription(description);
		Gson gson = new Gson();
		String json = gson.toJson(asset);
		SemanticResponse rtn = null;
		String responseStr = "";
		if(StringUtils.equalsIgnoreCase(repeatType, "2")){
			//覆盖资源，设置objectId ，不用设置文件ID
			responseStr = baseSemanticSerivce.assetOverride(json);
		}else{
			responseStr =  baseSemanticSerivce.createResource(json);
		}
		logger.info("vvvvvvvvvvvvvvvvvvv====="+responseStr);
		rtn = gson.fromJson(responseStr, SemanticResponse.class);
		if(rtn.getState() != 0){
			throw new ServiceException("资源报错异常");
		}else{
			//调用接口，生成转换文件
			if(!"-1".equals(returnValue)){
				ResConverfileTask rcft = new ResConverfileTask();
				rcft.setResId(rtn.getObjectId());
				rcft.setSrcPath(path);
				logger.info("yyyyyyyyyyyyyyyyyyyyyyyy==============="+path);
				rcft.setTxtStr(description);
				rcft.setPlatformId(1);
				if(!"1".equals(returnValue)){
					rcft.setDoHasType("0"); //处理类型：0，文档或者视频转换（转换服务）  1，抽取封面文件，2抽取文本
				}
				resConverfileTaskService.saveResConverfileTask(rcft);
			}
			return rtn.getObjectId();
		}
	}
	/**
	 * 自动提取元数据，生成缩略图
	 * //一共八种 依次为 图片 1,音频 2,视频 3,动画 4,教学软件 5,网络课程 6,试卷习题 7,学习网站 8  (其中，前四种，可以提取元数据)
	 * @param path
	 * @param fileName
	 * @param thumbFile
	 * @param metaFlag
	 * @param extendMetaDatas
	 */
	private String doSetExtendMetasAndCreateThumb(String path,String fileType, String uploadThumbFile,String metaFlag,Map<String, String> extendMetaDatas) {
		//2014年8月7日 根据需求,把缩略图直接命名为 cover.jpg
		String returnValue = "0";
		String fileName = "cover"+fileType;
		String thumbPath = StringUtils.substringBeforeLast(path, "/") + "/thumb/cover"+fileType ;
		File thumbFile = new File(thumbPath);
		if(!thumbFile.getParentFile().exists()){
			thumbFile.getParentFile().mkdir();
		}
		boolean userUploadThumb = false;
		if(StringUtils.isNotBlank(uploadThumbFile)){
			userUploadThumb = true;
			//保存缩略图
			File realFile = new File(BresAction.FILE_TEMP + uploadThumbFile);
//			File destFile = new File(StringUtils.substringBeforeLast(thumbPath, ".") + "." + StringUtils.substringAfterLast(uploadThumbFile, "."));
			File destFile = new File(thumbPath);
			try {
				FileUtils.copyFile(realFile, destFile);
				realFile.getParentFile().delete();
			} catch (IOException e) {
				logger.error("缩略图保存失败" + e.getMessage());
			}
		}
		if(StringUtils.equalsIgnoreCase(metaFlag, "meta1")){
			Picture picture = (Picture) FileMetadataFactory.getMetadata(path);
			extendMetaDatas.put("resolution", picture.getResolution());
			if(!userUploadThumb){
				//生成缩略图
				try {
					logger.info(path+"===============6666666666===="+thumbPath);
					ImgCoverUtil.conver2Other(path, thumbPath);
			//		Thumbnails.of(path).size(200, 300).toFile(thumbPath);
				} catch (Exception e) {
					logger.error("生成缩略图失败："+e.getMessage());
				}
			}
		}else if(StringUtils.equalsIgnoreCase(metaFlag, "meta2")){
			Multimedia multimedia = (Multimedia) FileMetadataFactory.getMetadata(path);
			extendMetaDatas.put("sampling", multimedia.getAudioSampling());
			extendMetaDatas.put("acoustic_channel", multimedia.getAudioChannel());
			extendMetaDatas.put("duration", multimedia.getDuration());
		}else if(StringUtils.equalsIgnoreCase(metaFlag, "meta3")){
			Multimedia multimedia = (Multimedia) FileMetadataFactory.getMetadata(path);
			extendMetaDatas.put("specification", multimedia.getVideoSpecification());
			extendMetaDatas.put("duration", multimedia.getDuration());
			extendMetaDatas.put("sampling", multimedia.getAudioSampling());
			extendMetaDatas.put("frame_count", multimedia.getVideoFps());
			if(!userUploadThumb){
				returnValue = "1";  //1需要转换
				//生成缩略图
//				try {
//					ConverUtils.processFfmpegImageBySureTime (path,thumbPath);
//				} catch (Exception e) {
//					logger.error("生成缩略图失败" + e.getMessage());
//				}
			}
			
		}else if(StringUtils.equalsIgnoreCase(metaFlag, "meta4")){
			Multimedia multimedia = (Multimedia) FileMetadataFactory.getMetadata(path);
			extendMetaDatas.put("frame_count", multimedia.getVideoFps());
			extendMetaDatas.put("specification", multimedia.getVideoSpecification());
			extendMetaDatas.put("duration", multimedia.getDuration());
			if(!userUploadThumb){
				returnValue = "1";  //1需要转换
				//生成缩略图
//				try {
//					ConverUtils.processFfmpegImageBySureTime (path,thumbPath);
//				} catch (Exception e) {
//					logger.error("生成缩略图失败" + e.getMessage());
//				}
			}
		}else{
			//thumbPath = StringUtils.substringBeforeLast(thumbPath, ".") + ".png";
			if(!userUploadThumb){
				//根据文件名后缀生成缩略图
				
				String ext = path.substring(path.lastIndexOf(".")+1,path.length());
				if(StringUtils.equalsIgnoreCase(ext, "doc") || StringUtils.equalsIgnoreCase(ext, "docx")){
					returnValue = "1";  //1需要转换
//					try {
//						DocUtil.docToImage(path, thumbPath,1);
//					} catch (Exception e) {
//						logger.error("生成缩略图失败" + e.getMessage());
//					}
				}else if(StringUtils.equalsIgnoreCase(ext, "ppt") || StringUtils.equalsIgnoreCase(ext, "pptx")){
					returnValue = "1";  //0需要转换
//					try {
//						PptUtils.pptToImage(path, thumbPath,1);
//					} catch (Exception e) {
//						logger.error("生成缩略图失败" + e.getMessage());
//					}
				}else if(StringUtils.equalsIgnoreCase(ext, "pdf")){
					returnValue = "1";  //0需要转换
//					try {
//						PdfUtil.pdfToImage(path, thumbPath,1);
//					} catch (Exception e) {
//						logger.error("生成缩略图失败" + e.getMessage());
//					}
				}
			}
		}
		File coverFile = new File(thumbPath);
		if(coverFile.exists()){
			returnValue = thumbPath;
		}
		return returnValue;
	}
	/**
	 * 保存资源，并关联资源对象
	 * @param asset
	 * @param uploadFile
	 * @param thumbFile
	 * @param loginName
	 * @param repeatType
	 * @param relationIds 关联的资源id组合
	 * @return
	 */
	public String saveResource(Asset asset,String uploadFile,String thumbFile,String loginName,String repeatType,String relationIds){
		String id = saveResourceAndThumb(asset, uploadFile,"",thumbFile, loginName, repeatType);
		if(StringUtils.isNotBlank(id) && StringUtils.isNotBlank(relationIds)){
			//自动关联资源
			baseSemanticSerivce.assetRelation(id, relationIds);
		}
		return id;
	}
	/**
	 * 修改资源，并关联资源对象
	 * @param asset
	 * @param uploadFile
	 * @param thumbFile
	 * @param loginName
	 * @param repeatType
	 * @param relationIds 关联的资源id组合
	 * @return
	 */
	public String updateResource(Asset asset,String uploadFile,String thumbFile,String loginName,String relationIds){
	//	String id = saveResourceAndThumb(asset, uploadFile,"",thumbFile, loginName, repeatType);
		com.brainsoon.semantic.ontology.model.File modelFile = null;
		//第一步 生成DOI
		CommonMetaData commonMetaData = asset.getCommonMetaData();
		String doi = ResourceTypeUtils.getDOIByAsset(asset);
		commonMetaData.setIdentifier(doi);
		File destFile = null;
		//第二步，根据DOI归类文件
		String objectId = asset.getObjectId();
		
		boolean leave = false;
		File realFile = null;
		if(StringUtils.isNotBlank(uploadFile)){
			realFile = new File(BresAction.FILE_TEMP + uploadFile);
		}
		String  returnValue = "0";  //1是需要转换的文件   长串是封面的路径  0 是不需要转换也不是封面路径
		String path = "";
		if(null != realFile){
			//上传文件
			String parentPath = "";
			if(StringUtils.isBlank(parentPath)){
				String module = commonMetaData.getModule();
				String type = commonMetaData.getType();
				//按要求生成目录
				parentPath = createParentPath(module, type, doi);
			}else{
				parentPath = FILE_ROOT + StringUtils.substringBeforeLast(parentPath, "/");
			}
			
			destFile = saveFile(parentPath, realFile,leave);
			path = StringUtils.replace(destFile.getAbsolutePath(),"\\", "/");
			//提取默认元数据，生成文件缩略图
			String fileName = destFile.getName();
			String ext = "." + StringUtils.substringAfterLast(fileName, ".");
			String metaFlag = ResourceTypeUtils.getExtendMetaDataFlag(asset.getType(), ext);
			String coverFileLType = "";
			if(StringUtils.isNotBlank(thumbFile)){
				metaFlag = "";
				coverFileLType = "." + StringUtils.substringAfterLast(thumbFile, ".");
			}
//			Map<String, String> extendMetaDatas = asset.getExtendMetaData().getExtendMetaDatas();
//			returnValue = doSetExtendMetasAndCreateThumb(path, coverFileLType,thumbFile,metaFlag, extendMetaDatas);
			logger.info("uuuuuuuuuuuuuuuuuuuuuuuuuuuu"+returnValue);
			if(!"0".equals(returnValue) && !"1".equals(returnValue)){
				commonMetaData.setPath(StringUtils.replace(returnValue, FILE_ROOT, ""));  //封面的路径
			}
			modelFile = new com.brainsoon.semantic.ontology.model.File();
			//设置file
			path = StringUtils.replace(path, FILE_ROOT, "");
			modelFile.setPath(path);  //文件的路径
			try {
				modelFile.setMd5(MD5Util.getFileMD5String(destFile));
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			modelFile.setName(destFile.getName());
			modelFile.setFileByte(destFile.length()+"");
			String now = dateformat.format(new Date());
			modelFile.setModified_time(now);
//			modelFile.setPublisher(loginName);
//			if(commonMetaData.getFormat() == null || "".equals(commonMetaData.getFormat())){
//				ext = ext.substring(1,ext.length());
//				modelFile.setFormat(ext);
//				commonMetaData.setFormat(ext);
//			}else{
//				modelFile.setFormat(commonMetaData.getFormat());
//			}
			List<com.brainsoon.semantic.ontology.model.File> modelFiles = new ArrayList<com.brainsoon.semantic.ontology.model.File>(); 
			modelFiles.add(modelFile);
//			asset.setFiles(modelFiles);

		}
			
		if(realFile == null && StringUtils.isNotBlank(thumbFile)){
			String coverFileLType = "." + StringUtils.substringAfterLast(thumbFile, ".");
			String module = commonMetaData.getModule();
			String type = commonMetaData.getType();
			//按要求生成目录
			String parentPath = createParentPath(module, type, doi);
			File parentFile = new File(parentPath+"/thumb");
			if(parentFile.exists()){
				File[] files = parentFile.listFiles();
				if(files !=null && files.length>0){
					for(File file:files){
						file.delete();
					}
				}
			}
			String absPath = parentPath+"/thumb/cover"+coverFileLType;
			absPath = StringUtils.replace(absPath,"\\", "/");
			File thumbTarFile = new File(absPath);
			if(!thumbTarFile.getParentFile().exists()){
				thumbTarFile.getParentFile().mkdir();
			}
			File realThumbFile = new File(BresAction.FILE_TEMP + thumbFile);
			try {
				FileUtils.copyFile(realThumbFile, thumbTarFile);
				realThumbFile.getParentFile().delete();
			} catch (IOException e) {
				logger.error("缩略图保存失败" + e.getMessage());
			}
			commonMetaData.setPath(StringUtils.replace(absPath, FILE_ROOT, ""));  //封面的路径
			returnValue = "-1";
		}
		//第三步创建对象
		Date now = new Date();
		commonMetaData.setModified_time(dateformat.format(now));
		String keyword = commonMetaData.getKeywords();
		if(StringUtils.isNotBlank(keyword) && keyword.indexOf("，")>=0){
			keyword = keyword.replaceAll("，", ",");
			commonMetaData.setKeywords(keyword);
		}
		//设置状态
		if(StringUtils.isBlank(commonMetaData.getStatus())){
			commonMetaData.setStatus("0");
		}
		Gson gson = new Gson();
		String json = gson.toJson(asset);
		logger.debug(json);
		SemanticResponse rtn = null;
		String responseStr = "";
		responseStr =  baseSemanticSerivce.createResource(json);
		rtn = gson.fromJson(responseStr, SemanticResponse.class);
		if(rtn.getState() != 0){
			throw new ServiceException("资源报错异常");
		}else{
			String id =  rtn.getObjectId();
			if(StringUtils.isNotBlank(id) && StringUtils.isNotBlank(relationIds)){
				//自动关联资源
				baseSemanticSerivce.assetRelation(id, relationIds);
			}
			if(!"-1".equals(returnValue)){
				ResConverfileTask rcft = new ResConverfileTask();
				rcft.setResId(rtn.getObjectId());
				rcft.setSrcPath(path);
//				if(!"1".equals(returnValue)){
//					rcft1.setDoHasType("0"); //处理类型：0，文档或者视频转换（转换服务）  1，抽取封面文件，2抽取文本
//				}
				resConverfileTaskService.saveResConverfileTask(rcft);
			}
			return rtn.getObjectId();
		}
	}
	/**
	 * 批量删除资源对象，逗号分隔，相应的标签数据也删除
	 * @param ids
	 * @throws IOException
	 */
	public void deleteByIds(String ids) throws IOException{
		if(StringUtils.isNotBlank(ids)){
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			Map<String, Object> parameters = new HashMap<String, Object>();
			String [] idsArr = StringUtils.split(ids, ",");
			for (String id : idsArr) {
				deleteResourceById(StringUtils.trim(id));
				parameters.put("objectId", id);
				String hql = "delete from ResTargetData  where objectId=:objectId and platformId="+userInfo.getPlatformId();
				baseDao.executeUpdate(hql, parameters);
			}
		}
	}
	/**
	 * 批量删除资源对应的文件，逗号分隔
	 * @param ids
	 * @param paths
	 * @throws IOException
	 */
	public void deleteFileByIds(String ids,String paths,String resId) throws IOException{
		if(StringUtils.isNotBlank(ids)){
			String [] idsArr = StringUtils.split(ids, ",");
			String [] pathsArr = StringUtils.split(paths, ",");
			int i = 0;
			int length = idsArr.length;
			for (;i < length ; i ++ ) {
				baseSemanticSerivce.deleteFileByObjectId(idsArr[i]);
				ResConverfileTask rcft = new ResConverfileTask();
				rcft.setResId(resId);
				rcft.setSrcPath(pathsArr[i]);
				resConverfileTaskService.deleteConverfileTask(rcft);
				//删除文件
				String delPath = FILE_ROOT + pathsArr[i];
				logger.debug("删除文件："+delPath);
				File f = new File(delPath);
				if(f.exists()){
					//删除文件
					try{
						FileUtils.forceDelete(f.getParentFile());
					}catch (Exception e) {
						logger.error("删除文件失败"+e.getMessage()+f.getParentFile().getAbsolutePath());
					}
					//FileUtils.deleteDirectory(f.getParentFile());
				}
			}
		}
	}
	/**
	 * 删除资源对象
	 * @param objectId
	 * @throws IOException 
	 */
	public void deleteResourceById(String objectId) throws IOException{
		Asset asset = baseSemanticSerivce.getResourceById(objectId);
		String libType = asset.getCommonMetaData().getLibType();
//		asset.getFiles()
		List<com.brainsoon.semantic.ontology.model.File> modelFiles = null;
		
		doReduceNum(asset.getCommonMetaData().getModule(), asset.getType());
		baseSemanticSerivce.deleteResourceById(objectId);
		
		for (com.brainsoon.semantic.ontology.model.File file : modelFiles) {
			String path = file.getPath();
			if(StringUtils.isNotBlank(path)){
				ResConverfileTask rcft = new ResConverfileTask();
				rcft.setResId(asset.getObjectId());
				rcft.setSrcPath(path);
				resConverfileTaskService.deleteConverfileTask(rcft);
				File f = new File(FILE_ROOT + path);
				if(f.exists()){
					//删除文件
					try{
						FileUtils.forceDelete(f.getParentFile());
					}catch (Exception e) {
						logger.error("删除文件失败"+e.getMessage()+f.getParentFile().getAbsolutePath());
					}
					//FileUtils.deleteDirectory(f.getParentFile());
				}
			}
		}
		
		SysOperateLogUtils.addLog("res_del_"+libType, "资源标题："+ asset.getCommonMetaData().getTitle(), LoginUserUtil.getLoginUser());
	} 
	
	/**
	 * 回写导入状态
	 * @param task
	 * @param successNum
	 * @param totalNum
	 * @param taskData
	 */
	public void backWriteStatus(UploadTask task, int successNum, int totalNum,ImportResExcelFile taskData) {
		//防止session超时
		task = (UploadTask)getBaseDao().getByPk(UploadTask.class, task.getId());
		logger.info("111111111---------------------");
		if(successNum == 0){
			//全部失败
			task.setStatus(ImportStatus.STATUS4);
		}else if(totalNum == successNum){
			//全部成功
			task.setStatus(ImportStatus.STATUS2);
		}else{
			//部分成功
			task.setStatus(ImportStatus.STATUS3);
		}
		task.setFinishTime(new Date());
		update(task);
		logger.info("32222222222222222222222222---------------------");
		UserInfo user = taskData.getUserInfo();
		logger.info("4444444444444444444444444444---------------------");
		//写导入日志
		iEffectNumService.doPiecework(user.getUserId(), OperatType.IMPORT_OPERATE_TYPE, taskData.getLibType(), "", successNum);
		logger.info("5555555555555555555---------------------");
		//写入业务日志
		SysOperateLogUtils.addLog("res_imp_"+taskData.getLibType(), "批量导入资源，成功 ："+ successNum+"个", user);
	}
	/**
	 * 解析单元ID
	 * @param code
	 * @param chapter
	 * @param section
	 * @param lesson
	 * @param commonMetaDatas
	 * @param domainType
	 * @return
	 */
	public String doParseUnit(String code, String chapter,String section, String lesson, Map<String, String> commonMetaDatas,int domainType) {
		String unitId = "";
		String unitName = "";
		if(StringUtils.isNotBlank(chapter)){
			//处理单元逻辑
			List<Domain> unitTree = baseSemanticSerivce.getUnitTree(code,domainType+"");
			if(null != unitTree && unitTree.size() > 0){
				Domain chapterDomain = baseSemanticSerivce.getTreeId(unitTree, "", chapter);
				if(null != chapterDomain){
					if(StringUtils.isNotBlank(section)){
						Domain sectionDomain = baseSemanticSerivce.getTreeId(unitTree, chapterDomain.getNodeId(), section);
						if(null != sectionDomain){
							if(StringUtils.isNotBlank(lesson)){
								Domain lessonDomain = baseSemanticSerivce.getTreeId(unitTree, sectionDomain.getNodeId(), lesson);
								if(null != lessonDomain){
									unitId = lessonDomain.getObjectId();
									unitName = lesson;
								}
							}else{
								unitId = sectionDomain.getObjectId();
								unitName = section;
							}
						}
					}else{
						unitId = chapterDomain.getObjectId();
						unitName = chapter;
					}
					if(StringUtils.isNotBlank(unitId)){
						commonMetaDatas.put("unit", unitId);
						commonMetaDatas.put("unitName", unitName);
					}
				}
			}
		}
		return unitId;
	}
	
	public void delTaskInfo(String ids){
		//先删除从表
		String executeHql = " delete from UploadTaskDetail d where d.task.id in(:ids)";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ids", StringUtil.strArrayToLongList(ids));
		executeUpdate(executeHql, parameters);
		delete(UploadTask.class, ids);
	}
	public void downloadRes(HttpServletRequest request,HttpServletResponse response, String objectIds, String encryptPwd,String libType){
		if(StringUtils.isNotBlank(objectIds)){
			UserInfo user = LoginUserUtil.getLoginUser();
			String [] ids = StringUtils.split(objectIds,",");
			String parentPath = BresAction.FILE_TEMP + UUID.randomUUID().toString() + File.separator;
			Asset asset = null;
			for (String objectId : ids) {
//				if(objectId.indexOf("book")>0){
//					HttpClientUtil http = new HttpClientUtil();
//					Gson gson = new Gson();
//					res = gson.fromJson(http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + objectId), Ca.class);
//				}else{
//					res = baseSemanticSerivce.getResourceById(objectId);
//				}
				asset = baseSemanticSerivce.getResourceById(objectId);
//				asset.getFiles()
				List<com.brainsoon.semantic.ontology.model.File> modelFiles = null;
				
				String title = asset.getCommonMetaData().getTitle();
				
				File parent = new File(parentPath + title + "_" + RandomNumberGenerator.generateNumber3());
				parent.mkdirs();
				for (com.brainsoon.semantic.ontology.model.File file : modelFiles) {
					try {
						File f = new File(FILE_ROOT + file.getPath());
						if(f.exists()){
							FileUtils.copyFileToDirectory(f, parent);
						}						
					} catch (IOException e) {
						logger.error(e.getMessage());
						e.printStackTrace();
					}
				}
				if (StringUtils.equals(libType, "2")) {
					SysOperateLogUtils.addLog("edu_ores_download", asset
							.getCommonMetaData().getTitle(), user);
				} else if (StringUtils.equals(libType, "1")) {
					SysOperateLogUtils.addLog("edu_bres_download", asset
							.getCommonMetaData().getTitle(), user);
				} else {
					SysOperateLogUtils.addLog("edu_colo_download", asset
							.getCommonMetaData().getTitle(), user);
				}
			}
			
			//压缩parent目录
			String zipName = BresAction.FILE_TEMP + dateformat2.format(new Date()) + ".zip";
			try {
				ZipUtil.zipFileOrFolder(parentPath, zipName, null);
				if(StringUtils.isNotBlank(encryptPwd)){
					String encryptZip = BresAction.FILE_TEMP + dateformat2.format(new Date()) + "encrypt.zip";
					ZipUtil.encryptZipFile(new File(zipName), encryptZip, encryptPwd);
					downloadFile(request, response, encryptZip, false);
				}else{
					downloadFile(request, response, zipName, false);
				}
			} catch (Exception e) {
				logger.error("压缩出现问题" + e.getMessage());
			}
		}
	}
	
	/**
	 * 下载文件
	 * @param request
	 * @param response
	 * @param filePath
	 * @param isOnLine
	 * @throws Exception
	 */
	public void downloadFile(HttpServletRequest request,HttpServletResponse response, String filePath, boolean isOnLine)
			throws Exception {
		File f = new File(filePath);
		if (!f.exists()) {
			response.sendError(404, "File not found!");
			return;
		}
		BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
		byte[] buf = new byte[1024];
		int len = 0;
		response.reset(); // 非常重要
		// 处理文件中，主要中文
		String fileName = f.getName();
		String userAgent = request.getHeader("USER-AGENT");
		String new_filename = URLEncoder.encode(fileName, "UTF-8");
		String rtn = "";
		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();
			// IE浏览器，只能采用URLEncoder编码
			if (userAgent.indexOf("msie") != -1) {
				rtn = "filename=\"" + new_filename + "\"";
			}
			// Opera浏览器只能采用filename*
			else if (userAgent.indexOf("opera") != -1) {
				rtn = "filename*=UTF-8''" + new_filename;
			}
			// Safari浏览器，只能采用ISO编码的中文输出
			else if (userAgent.indexOf("safari") != -1) {
				rtn = "filename=\""
						+ new String(fileName.getBytes("UTF-8"), "ISO8859-1")
						+ "\"";
			}
			// Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
			else if (userAgent.indexOf("applewebkit") != -1) {
				new_filename = MimeUtility.encodeText(fileName, "UTF-8", "B");
				rtn = "filename=\"" + new_filename + "\"";
			}
			// FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
			else if (userAgent.indexOf("mozilla") != -1) {
				rtn = "filename*=UTF-8''" + new_filename;
			}
		}
		// 解决下载时，空格变加号
		fileName = fileName.replace("+", "%20");
		response.setCharacterEncoding("utf-8");
		if (isOnLine) { // 在线打开方式
			URL u = new URL("file:///" + filePath);
			response.setContentType(u.openConnection().getContentType());
			response.setHeader("Content-Disposition", "inline; " + rtn + "");
			// 文件名应该编码成UTF-8
		} else { // 纯下载方式
			response.setContentType("application/x-msdownload");
			// fileName = URLEncoder.encode(fileName, "UTF-8");
			response.setHeader("Content-Disposition", "attachment; " + rtn + "");
		}
		OutputStream out = response.getOutputStream();
		while ((len = br.read(buf)) > 0)
			out.write(buf, 0, len);
		br.close();
		out.close();
	}
	
	/**
	 * 下载文件
	 * @param request
	 * @param response
	 * @param filePath
	 * @param isOnLine
	 * @throws Exception
	 */
	public Map<String,File> downloadlog(HttpServletRequest request,HttpServletResponse response,String id, String filePath,String excelNum,String failExcelPath)
			throws Exception {
		Map<String,File> mapFile = new HashMap<String, File>();
		File f = new File(filePath);
		if (!f.exists()) {
			response.sendError(404, "File not found!");
			return null;
		}
		LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
		List<UploadTaskDetail> list = query("from UploadTaskDetail where task.id="+Integer.parseInt(id)+" order by excelNum");
//		boolean tag = false;
		int num = 0;
		int flagNumRes = 0;
		if(list.size()>0){
//			List<ResTaskDetail> valueList = list.get(0).getValueList();
				for(int i=0;i<list.size();i++){
					num = i+1;
					if(num<list.size() && list.size()-num!=1){
					if(list.get(i+1).getExcelNum()-list.get(i).getExcelNum()==1&&!"".equals(list.get(i).getImportStatus())){
						flagNumRes = i+1;
						map.put(list.get(i).getExcelNum()+"", list.get(i).getImportStatus());
					}else{
//						tag = true;
						break;
					}
				}else{
						map.put(list.get(i).getExcelNum()+"", list.get(i).getImportStatus());
						flagNumRes = i+1;
				}
			}
		}
		FileInputStream is = new FileInputStream(filePath);  
        HSSFWorkbook workbook = new HSSFWorkbook(is);  
        HSSFSheet sheet = workbook.getSheetAt(0); 
        boolean isNeedAddCol = true;
        HSSFRow titleRow = sheet.getRow(0);
        HSSFRow titleRow1 = sheet.getRow(1);
        short total = titleRow.getLastCellNum();
        HSSFCell cell = titleRow.getCell(total-1);
        String cellValue = cell.getStringCellValue();
        if(cellValue!=null && !"".equals(cellValue) && "执行结果".equals(cellValue)){
        	isNeedAddCol = false;
        }else{
        	HSSFCell newCell = titleRow.createCell(total);
        	newCell.setCellValue("执行结果");
        	newCell.setCellStyle(getTitleStyle(workbook));
        	sheet.setColumnWidth(total, (short) 50 * 25 * 16);
        	titleRow.setHeight((short) 280);
        }
        HSSFCell cellTemp = null;
        //在相应的行列添写失败信息
        List<Integer> needNotDelRow = new ArrayList<Integer>();
        for(Entry<String, String> entry:map.entrySet()){ 
        	
        	HSSFRow  row = sheet.getRow(Integer.parseInt(entry.getKey())-1);
        	if(row==null){
        		row = sheet.getRow(Integer.parseInt(entry.getKey()));
        	}
        	if(isNeedAddCol){
        		cellTemp = row.createCell(total);
        	}else{
        		cellTemp = row.getCell(total-1);
        	}
        	if(cellTemp!=null){
        		cellTemp.setCellValue(entry.getValue());
            	needNotDelRow.add(Integer.parseInt(entry.getKey()));
            	logger.info("fffffffff======="+entry.getKey());
        	}
        }
        int exceAllNum = Integer.parseInt(excelNum);
        
        for(int i=4;i<exceAllNum+4;i++){
        	if(!needNotDelRow.contains(i)){
        		HSSFRow removingRow=sheet.getRow(i-1);
        		if(removingRow !=null){
        			sheet.removeRow(removingRow);
        		}
        	}
        }
        titleRow1.setZeroHeight(true);
        FileOutputStream os = new FileOutputStream(failExcelPath);
        workbook.write(os); 
        is.close();  
        os.close(); 
        String excelDir = failExcelPath.substring(0,failExcelPath.lastIndexOf("/")+1);
        String newExcelPath = excelDir+UUID.randomUUID()+".xls";
//        ExcelUtil.removeBlankRow(failExcelPath,newExcelPath);
        
        File outFile = new File(newExcelPath);
        
        OutputStream out = null;
		try {
			out = new FileOutputStream(outFile);
			workbook.write(out);// 写入File
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
		mapFile.put(flagNumRes+"", outFile);
		return mapFile;
	}

	/**
	* 标题的格式
	*/
	private static  HSSFCellStyle getTitleStyle(HSSFWorkbook wb) {
		HSSFCellStyle cellStyle = wb.createCellStyle();
		HSSFDataFormat format = wb.createDataFormat();  
		cellStyle.setDataFormat(format.getFormat("@"));  
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setFont(getFirstHdrFont(wb));
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
	return cellStyle;
	}
	/**
	* 主标题字体
	* 
	* @param wb
	* @return
	*/
	private static  HSSFFont getFirstHdrFont(HSSFWorkbook wb) {
		HSSFFont fontStyle = wb.createFont();
		fontStyle.setFontName("微软雅黑");
		fontStyle.setFontHeightInPoints((short) 15);
		fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		return fontStyle;
	}

	@Override
	public File beachGetDetaillExcel(HttpServletResponse response,String status, String excelPath,String id)
			throws Exception {
		File f = new File(excelPath);
		if (!f.exists()) {
			response.sendError(404, "File not found!");
			return null;
		}
		List<UploadTaskDetail> list =null;
		if(status!=null&&!"".equals(status)){
			list = query("from UploadTaskDetail where status="+Integer.parseInt(status)+" and task.id="+Integer.parseInt(id));
		}else{
			list = query("from UploadTaskDetail where task.id="+Integer.parseInt(id));
		}
		List<Integer> needNotDelRow = new ArrayList<Integer>();
		LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
		if(list!=null){
			for(UploadTaskDetail resTaskDetail:list){
				if(resTaskDetail!=null&&resTaskDetail.getExcelNum()!=0){
					
					needNotDelRow.add(resTaskDetail.getExcelNum());
				}
				if(resTaskDetail.getExcelNum()!=0&&!"".equals(resTaskDetail.getImportStatus())){
					map.put(resTaskDetail.getExcelNum()+"", resTaskDetail.getImportStatus());
				}
			}
		}
		FileInputStream is = new FileInputStream(excelPath);
        HSSFWorkbook workbook = new HSSFWorkbook(is);  
        HSSFSheet sheet = workbook.getSheetAt(0);
        boolean isNeedAddCol = true;
        int totalRow = sheet.getLastRowNum();
        HSSFRow titleRow = sheet.getRow(0);
        short total = titleRow.getLastCellNum();
        HSSFCell cell = titleRow.getCell(total-1);
        String cellValue = cell.getStringCellValue();
        if(cellValue!=null && !"".equals(cellValue) && "失败原因".equals(cellValue)){
        	isNeedAddCol = false;
        }else{
        	HSSFCell newCell = titleRow.createCell(total);
        	newCell.setCellValue("失败原因");
        	newCell.setCellStyle(getTitleStyle(workbook));
        	sheet.setColumnWidth(total, (short) 50 * 25 * 12);
        	titleRow.setHeight((short) 280);
        }
        HSSFCell cellTemp = null;
        //在相应的行列添写失败信息
        for(Entry<String, String> entry:map.entrySet()){ 
        	
        	HSSFRow  row = sheet.getRow(Integer.parseInt(entry.getKey())-1);
        	if(row==null){
        		row = sheet.getRow(Integer.parseInt(entry.getKey()));
        	}
        	if(isNeedAddCol){
        		cellTemp = row.createCell(total);
        	}else{
        		cellTemp = row.getCell(total-1);
        	}
        	if(cellTemp!=null){
        		cellTemp.setCellValue(entry.getValue());
            	needNotDelRow.add(Integer.parseInt(entry.getKey()));
            	logger.info("fffffffff======="+entry.getKey());
        	}
        }
        if(list.size()<=0){
        	total = (short) (3+totalRow);
        }else{
        	total = (short) (list.size()+totalRow);
        }
        for(int i=3;i<=total;i++){
        	if(!needNotDelRow.contains(i)){
        		HSSFRow removingRow=sheet.getRow(i-1);
        		if(removingRow !=null){
        			sheet.removeRow(removingRow);
        		}
        	}
        }
		String newPath = (String) excelPath.subSequence(0, excelPath.lastIndexOf("/")+1);
		newPath = newPath+UUID.randomUUID()+".xls";
		FileOutputStream os = new FileOutputStream(newPath);
        workbook.write(os); 
        is.close();  
        os.close();
		ExcelUtil.removeBlankRow(newPath, newPath);
		File file = new File(newPath);
		return file;
	}
}
