package com.brainsoon.resource.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.support.ImportResExcelFile;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;

public interface IResourceService extends IBaseService{
	/**
	 * 获取当前正在使用的文件夹
	 * @param module
	 * @param type
	 * @return String
	 */
	public String doGetCurrentUseFolder(String module,String type);
	
	
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
	public String saveResource(Asset asset,String uploadFile,String thumbFile,String loginName,String repeatType,String relationIds);
	
	/**
	 * 删除资源对象
	 * @param objectId
	 * @throws IOException 
	 */
	public void deleteResourceById(String objectId) throws IOException;
	
	/**
	 * 批量删除资源对象，逗号分隔
	 * @param ids
	 * @throws IOException
	 */
	public void deleteByIds(String ids) throws IOException;
	
	/**
	 * 批量删除资源对应的文件，逗号分隔
	 * @param ids
	 * @param paths
	 * @throws IOException
	 */
	public void deleteFileByIds(String ids,String paths,String resId) throws IOException;
	
	/**
	 * 批量删除任务信息
	 * @param ids
	 */
	public void delTaskInfo(String ids);
	
	/**
	 * 下载资源
	 * @param request
	 * @param response
	 * @param objectIds
	 * @param encryptPwd
	 */
	public void downloadRes(HttpServletRequest request,HttpServletResponse response, String objectIds, String encryptPwd,String libType);
	
	/**
	 * 下载文件
	 * @param request
	 * @param response
	 * @param filePath
	 * @param isOnLine
	 * @return 
	 * @throws Exception
	 */
	public void downloadFile(HttpServletRequest request,HttpServletResponse response,String filePath, boolean isOnLine)
			throws Exception;
	
	/**
	 * 创建资源存储的父路径，计数加1
	 * @param module
	 * @param type
	 * @param doi
	 * @return String
	 */
	public String createParentPath(String module,String type,String doi);
	
	/**
	 * 拷贝文件到指定目录
	 * @param parentPath
	 * @param srcFile
	 * @param leave 是否保留原文件
	 * @return File
	 */
	public File saveFile(String parentPath,File srcFile,boolean leave);
	
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
	public String doParseUnit(String code, String chapter,String section, String lesson, Map<String, String> commonMetaDatas,int domainType);
	
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
	public String saveResourceAndThumb(Asset asset,String uploadFile,String uploadFileFullPath,String thumbFile,String loginName,String repeatType);
	
	/**
	 * 回写导入状态
	 * @param task
	 * @param successNum
	 * @param totalNum
	 * @param taskData
	 */
	public void backWriteStatus(UploadTask task, int successNum, int totalNum,ImportResExcelFile taskData);
	/**
	 * 出版库 创建资源存储的父路径，计数加1
	 * @param module
	 * @param type
	 * @param doi
	 * @return String
	 */
	public String createPublishParentPath(String publishType,String doi);
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
	public String updateResource(Asset asset,String uploadFile,String thumbFile,String loginName,String relationIds);
	
	/**
	 * 处理不存在的资源文件
	 * @param module
	 * @param type
	 * @return
	 */
	public void doResFile();
	/**
	 * 下载日志
	 * @return
	 */
	public Map<String,File> downloadlog(HttpServletRequest request,HttpServletResponse response,String id, String filePath,String excelNum,String failExcelPath) throws Exception;
	/**
	 * 批量导入日志，详细页导出数据
	 * 
	 * @return
	 * @throws IOException
	 */
	public File beachGetDetaillExcel(HttpServletResponse response,String status,String excelPath,String id) throws Exception;
}
