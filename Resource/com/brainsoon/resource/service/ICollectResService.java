package com.brainsoon.resource.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.common.util.dofile.view.CatalogDTO;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.support.ImportResExcelFile;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;

public interface ICollectResService {
	    /**
	    * 创建组合资源
	    * @param ca
	    * @param jsonTree
	    * @param nodeAsset
	    * @param ogId
	    * @throws Exception
	    */
	   public void saveCollectRes(Ca ca,String jsonTree,String nodeAsset,String ogId,String thumbFile,String repeatType) throws Exception;
	   
	   /**
	    * 创建图书资源
	    * @param ca
	    * @param uploadFile
	    * @throws Exception
	    */
	   public String saveBookRes(Ca ca,String uploadFile,String repeatType) throws Exception;
	   
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
	   public String updateNode(String nodeJson,String nodeAsset,String oldPath) throws Exception;
	   
	   /**
	    *  删除目录节点
	    * @param caId
	    * @param nodeId
	    * @throws Exception
	    */
	   public void deleteNode(String caId, String nodeId,String path) throws Exception;
	   
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
		  * 执行导入任务
		*/
		public UploadTask doImportTask(ImportResExcelFile taskData) throws Exception;
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
		 public void downloadBookRes(HttpServletRequest request,HttpServletResponse response, String objectIds, String encryptPwd);
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
		 public String saveImportBookRes(Asset asset);
		 /**
		    * 批量导入图书覆盖Ca对象
		    * @param ca
		    * @param uploadFile
		    * @throws Exception
		    */
		   public String overrideImportBookRes(Asset asset,Ca ca);
		   /**
		    * 覆盖Ca对象
		    * @param ca
		    * @param uploadFile
		    * @throws Exception
		    */
		   public Ca overrideCaRes(Ca ca);
		   /**
			 * 记录文件大小
			 * @param request
			 * @param response
			 * @param objectIds
			 * @param encryptPwd
			 */
			 public String fileSize(HttpServletRequest request,HttpServletResponse response, String objectIds, String encryptPwd);
}
