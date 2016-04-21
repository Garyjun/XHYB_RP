package com.brainsoon.resource.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.DocumentException;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.resource.po.UploadTaskDetail;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.support.ExcelData;
import com.brainsoon.resource.support.ImportResExcelFile;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.schema.LomProperty;

/**
 * 批量导入教育资源，图书和组合资源除外
 * @author zuo
 *
 */
public interface IBatchImportResService extends IBaseService{
	
	public String doTask(Asset asset,ImportResExcelFile taskData,UploadTask task,Map<Integer,String> resultLog);
	
	public File getExcelTemplete(String module);
	
	/**
	 * excel文件转换为对象
	 * @param excelPath
	 * @param data
	 */
	public void fillData(String excelPath,ExcelData data,Map<Integer,String> resultLog);
	
	/**
	 * 获取资源模块对应的元数据
	 * @param module
	 * @return
	 */
	public List<LomProperty> getPropertyByModule(String module);
	
	public Object transformKeyForDesc(String name,String value);
	/**
	 * 出版资源执行导入任务
	 */
	/*public String doPublishTask(Ca ca, ImportResExcelFile taskData,
			UploadTask task,Map<Integer,String> resultLog,Map<String,String> checkRepeatMetadate,Map<Integer,String> fileNotExistLog);*/
	/**
	 * 转换数据
	 * @param excelData
	 * @param module 模块code
	 * @param needSaves 需要保存的asset
	 * @param task
	 * @param asset
	 */
	public void doParseData2Asset(ExcelData excelData,String module,List<Asset> needSaves,UploadTask task,Map<Integer,String> resultLog);
	/**
	 * 转换数据
	 * 
	 * @param excelData
	 * @param module
	 *            模块code
	 * @param needSaves
	 *            需要保存的asset
	 * @param task
	 * @param asset
	 */
	public void doParseData2Ca(ExcelData excelData, List<Ca> needSaves,
			UploadTask task,Map<Integer,String> resultLog);
	/**
	 * 解析mysqlWithExcel
	 * 
	 * @param task
	 * @param asset
	 */
	public Map<String,String> doMySqlWithExcel(UploadTaskDetail resTaskDetail);
	
	
	/**
	 * 根据路径解析XML文件
	 * @param path 路径
	 * 放在list中
	 */
	public List<Map<String, String>> savefile(String path,UploadTask uploadTask, UploadTaskDetail uploadTaskDetail) throws DocumentException;
	
	/**
	 * 
	* @Title: validateMetadata
	* @Description: 验证元数据的正确性 （包括必填项，格式等）
	* @param maps	从excel中读取的数据/从xml中读取的数据
	* @param needSaves	验证通过即保存到该list中
	* @param task	当前任务（即主表信息）
	* @param resultLog	日志信息
	* @param batchImportResService
	* @param checkRepeatMetadat	查重字段
	* @param resTaskDetail   子表信息（即要验证的该条资源）
	* @return void    返回类型
	* @throws
	 */
	public void doValidateMetadata(Map<String, String> maps,List<Ca> needSaves,UploadTask task,Map<Integer,String> resultLog,Map<String,String> checkRepeatMetadat,UploadTaskDetail resTaskDetail);
	
	/**
	 * 
	* @Title: doPublishTask
	* @Description: 执行导入任务
	* @param ca 	要保存的该条资源Ca
	* @param task	当前主表的信息（即excel或一批次的xml）
	* @param uploadTaskDetail	当前子表的信息（即excel一行数据或一个xml文件）
	* @param resultLog
	* @param checkRepeatMetadate
	* @param fileNotExistLog
	* @return    参数
	* @return String    返回类型
	* @throws
	 */				
	public String doPublishTask(Ca ca, UploadTask task,UploadTaskDetail uploadTaskDetail,Map<Integer,String> resultLog,Map<String,String> checkRepeatMetadate,Map<Integer,String> fileNotExistLog);
	
	/**
	 * 
	 * 
	* @Title: saveImportPublishRes
	* @Description: 创建批量导入出版图书
	* @param ca	要保存的Ca
	* @param repeatType	重复策略
	* @param fileMetadataFlag	查重字段
	* @return
	* @throws Exception    参数
	* @return String    返回类型
	* @throws
	 */
	public String saveImportPublishRes(Ca ca,String repeatType,Map<String, Map<String,String>> fileMetadataFlag,UploadTask uploadTask,UploadTaskDetail uploadTaskDetail);
	
	/**
	 * 
	* @Title: saveRes
	* @Description: 批量导入入口SV
	* @param request
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String saveRes(HttpServletRequest request);
	
	/**
	 * 
	* @Title: getXmlJson
	* @Description: 根据批量导入的路径(系统参数)获取下面的批次信息以及批次下面的资源信息
	* @return    参数
	* @return String    返回类型	json数据
	* @throws
	 */
	public String getXmlJson();
	
	/**
	 * 
	* @Title: saveResByXml
	* @Description: 保存批量导入xml方式数据
	* @param paths	批次的路径
	* @param remark	备注信息
	* @param repeatType	重复策略
	* @param publishType  资源类型
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String saveResByXml(String paths, String remark,String repeatType, String publishType);
	/**
	 * 
	* @Title: saveRes
	* @Description: 批量导入入口SV
	* @param request
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String saveAnalyzeExcel(HttpServletRequest request);
	 /**
	 * 解析XML文件
	 *//*
	public List<Map<String, String>> savefile_new(String path,UploadTask uploadTask, UploadTaskDetail uploadTaskDetail)
				throws DocumentException;
	*/
}
