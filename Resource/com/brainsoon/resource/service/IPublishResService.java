package com.brainsoon.resource.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.util.dofile.view.CatalogDTO;
import com.brainsoon.resource.po.ModifyLog;
import com.brainsoon.resource.support.SemanticResponse;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CaList;
import com.brainsoon.semantic.ontology.model.DoFileHistory;
import com.brainsoon.semantic.ontology.model.SearchResult;

public interface IPublishResService {
	 public ModelMap jsonArray(Ca bookCa,String objectId,ModelMap model) ;
	   /**
	    * 根据资源ID获得改资源上一次的修改记录
	    * @param resId
	    */
	   public List<ModifyLog> queryModifyLogsByResId(String resId);
	    /**
	    * 创建组合资源
	    * @param ca
	    * @param jsonTree
	    * @param nodeAsset
	    * @param ogId
	    * @throws Exception
	    */
	   public void saveCollectRes(Ca ca,String jsonTree,String nodeAsset,String ogId,String thumbFile) throws Exception;
		 /**
			 * 分页查询,带有动态生成的条件的查询
			 * @param request
			 * @param conditionList
			 * @return String json
			 */
	   public String queryResource4PageByParam(HttpServletRequest request,int page,Long size,String queryUrl,String pageSize,QueryConditionList conditionList,String targetNames);
	   
	   /**
	    * 创建图书资源
	    * @param ca
	    * @param uploadFile
	    * @throws Exception
	    */
	   public String savePublishRes(Ca ca,String uploadFile,String repeatType,String publishType,String targetNames) throws Exception;
	   
	   /**
	    * 修改组合资源
	    * @param ca
	    * @throws Exception
	    */
	   public void updateCollectRes(Ca ca,String thumbFile) throws Exception;
	   
	   /**
	    * 更新目录节点
	    * @param nodeJson
	    */
	   public String updateNode(String oldPath,String treeEditOldName,String fileFlag,File file,String object,String nodeId,String resObjectId,String newFilePath,String title) throws Exception;
	   
	   /**
	    *  删除目录节点
	    * @param caId
	    * @param nodeId
	    * @throws Exception
	    */
	   public void deleteNode(String caId, String nodeId,String path,String treeDeleteName,String deleteFile) throws Exception;
	   
	   /**
		 * 分页查询，智能添加前台参数，读取rdf库
		 * @param request
		 * @param conditionList
		 * @return String json
		 */
		public String queryResource4Page(HttpServletRequest request,QueryConditionList conditionList,String queryUrl);
		
		 /**
		    * 增加版权信息
		    * @param ca
		    * @param uploadFile
		    * @throws Exception
		    */
		 public String saveBookCopyright(Ca ca) throws Exception;
		 
		 /**
		 * 批量删除资源对象，逗号分隔
		 * @param ids
		 * @throws IOException
		 */
		public void deleteByIds(String ids) throws IOException;
		/**
		 * 下载图书资源
		 * @param request
		 * @param response
		 * @param objectIds
		 * @param encryptPwd
		 */
		 public String downloadBookRes(HttpServletRequest request,String encryptZip, String objectIds, String encryptPwd,String ftpFlag,String isComplete);
		 /**
		  * ca阅读需要的 目录
		  * @param id
		  * @return
		  */
		 public List<CatalogDTO> getResDirAndFile(String id);
		 /**
		  * 批量导入图书生成ca对象
		  * @param asset
		  * @return
		  * @throws Exception
		  */
		 public Ca saveImportBookRes(Asset asset);
		 /**
		    * 批量导入图书覆盖Ca对象
		    * @param ca
		    * @param uploadFile
		    * @throws Exception
		    */
		   public Ca overrideImportBookRes(Asset asset,Ca ca);
		   /**
		    * 查重
		    * @param source
		    * @param type
		    * @param title
		    * @param creator
		    * @param isbn
		    * @return
		    */
		   public List<Ca> getCaResourceByMoreCondition(Map<String,String> checkRepeatMetadate);
		   /**
		    * 带版本号查重
		    * @param source
		    * @param type
		    * @param title
		    * @param creator
		    * @param resVersion
		    * @param isbn
		    * @return
		    */
		   public List<Ca> getCaResourceByResVersion(String publishtype,String title,String creator,String resVersion,String isbn);
		   /**
		    * 创建批量导入出版图书
		    * @param ca
		    * @param uploadFile
		    * @throws Exception
		    */
		   public String saveImportPublishRes(Ca ca,String processTask,String repeatType,Map<String, Map<String,String>> fileMetadataFlag) throws Exception;
		   /**
			 * 记录文件大小
			 * @param request
			 * @param response
			 * @param objectIds
			 * @param encryptPwd
			 */
		   public String fileSize(HttpServletRequest request,HttpServletResponse response, String objectIds, String encryptPwd);
		   /**
			 * 批量保存标签
			 * @param request
			 * @param response
			 * @param objectIds
			 * @param encryptPwd
			 */
		   public String updateBeachSaveTarget(Ca ca,String targetName,String targetField);
		   /**
			 * 目录重命名（在录入资源的时候本地文件目录已经改名只需要更新rdf数据库即可）
			 * 
			 * @param nodeJson
			 */
			public String doRenameNode(String caObjectId,String fileObjectId, String newName,String oldName) throws Exception;
			 /**
			    * 保存文件
			    * @param uploadFile
			    * @throws Exception
			    */
			public SemanticResponse saveFile(com.brainsoon.semantic.ontology.model.File file) throws Exception;
			 /**
			    * 批量导入查重
			    * @param uploadFile
			    * @throws Exception
			    */
			public CaList beachImportCheckRepeat(String id,String excelPath,String publishType) throws Exception;
			 /**
			    * 解析excel生成CA
			    * @param uploadFile
			    * @throws Exception
			    */
			public Ca installCa(String id,Ca ca,String excelPath) throws Exception;
			 /**
			    * 修改Detaill状态
			    * @param uploadFile
			    * @throws Exception
			    */
			public boolean updateStatus(String id,String taskId) throws Exception;
			
			/**
			 * 批量批量导入详细列表分页查询
			 * @param poClass
			 * @param conditionList
			 * @return PageResult
			 */
			public PageResult queryBeachImportDetaill(Class poClass,QueryConditionList conditionList);
			/**
			 * HttpFtp批量下载
			 * @param poClass
			 * @param conditionList
			 * @return PageResult
			 */
			public String createHttpFtpDownload(String ids,String encryptPwd,String ftpFlag,String parentPath,String downloadType);

			/**
			 * 资源预览 资源树
			 * @param bookCa
			 * @param objectId
			 * @return
			 */
			public ModelMap getZtreeJson(Ca bookCa, String objectId, ModelMap model);
			
			/**
			 * 分页查询文件
			 */
			public List<SearchResult> queryFile4Page(String page, String size, String startDate, String endDate, String createUser, String resName, String publishType,String fileExtensionName);
			
			/**
			 * 批量替换字段
			 */
			public Map<String,String> updateBeachReplace(List<Ca> cas, String field,String fieldValue);
			/**
			 * 
			* @Title: getTMJson
			* @Description: 根据批量导入的路径获取下面的批次信息以及批次下面的资源信息
			* @param path	批量导入的路径
			* @return    参数
			* @return String    返回类型	json数据
			* @throws
			 */
			public String getTMJson();
			/**
			 * 
			* @Title: saveResByTM
			* @Description: 保存批量导入xml方式数据
			* @param paths	批次的路径
			* @param remark	备注信息
			* @param repeatType	重复策略
			* @param publishType 资源类型
			* @return    参数
			* @return String    返回类型
			* @throws
			 */
			public String saveResByTM(String paths, String publishType);
			/**
			 * 
			* @Title: 前台调用返回列表详细
			* @return    参数
			* @return String    返回类型
			* @throws
			 */
			public StringBuffer fileDetail(String id,String name);
			
			
			public void updateFCS();
			public void doCovertFailToQueue();
			public String queryEntry(String url);
}
