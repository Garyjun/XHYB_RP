package com.brainsoon.resource.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.Domain;
import com.brainsoon.semantic.ontology.model.File;
import com.brainsoon.semantic.ontology.model.MetaDataDC;

public interface IBaseSemanticSerivce {
	/**
	 * 创建资源
	 * @param body
	 * @return
	 */
	public String createResource(String body);
	/**
	    * 增加版权信息
	    * @param ca
	    * @param uploadFile
	    * @throws Exception
	    */
	   public String saveAssetCopyright(Asset asset) throws Exception;
	
	/**
	 * 覆盖资源
	 * @param body
	 * @return
	 */
	public String assetOverride(String body);
	
	/**
	 * 获取下一级节点
	 * @param codes
	 * @param domainType
	 * @return String
	 */
	public String getDomainNode(String codes,String domainType);
	
	/**
	 * 分页查询，智能添加前台参数，读取rdf库
	 * @param request
	 * @param conditionList
	 * @return String json
	 */
	public String queryResource4Page(HttpServletRequest request,QueryConditionList conditionList);
	
	public String query4Page(String hql, String operate);
	
	/**
	 * 获取资源详细
	 * @param objectId
	 * @return String
	 */
	public String getResourceDetailById(String objectId);
	
	/**
	 * 获取资源关联的文件
	 * @param objectId
	 * @return List<File>
	 */
	public List<File> getResourceFilesById(String objectId);
	
	/**
	 * 删除资源对象，只删除rdf库中的
	 * @param objectId
	 */
	public void deleteResourceById(String objectId);
	
	/**
	 * 获取资源对象
	 * @param objectId
	 * @return Asset
	 */
	public Asset getResourceById(String objectId);
	
	/**
	 * 根据条件获取资源对象
	 * @param source
	 * @param type 为code
	 * @param title
	 * @param creator
	 * @param md5
	 * @return List<Asset>
	 */
	public List<Asset> getResourceByMoreCondition(String source,String type,String title,String creator,String module,String md5);
	/**
	 * 带版本号查重
	 * @param source
	 * @param type
	 * @param title
	 * @param creator
	 * @param md5
	 * @param resVersion
	 * @return
	 */
	public List<Asset> getResourceByResVersion(String source,String type,String title,String creator,String md5,String resVersion,String module);
	
	/**
	 * 根据中文名称解析成key
	 * @param module
	 * @param type
	 * @param educational_phase
	 * @param version
	 * @param subject
	 * @param grade
	 * @param fascicule
	 * @param domainType
	 * @return
	 */
	public String [] xpathCode(String module,String type,String educational_phase,String version,String subject,String grade,String fascicule,int domainType);
	
	/**
	 * 获取单元树
	 * @param code
	 * @param domainType
	 * @return List<Domain>
	 */
	public List<Domain> getUnitTree(String code,String domainType);
	
	/**
	 * 根据中文名字，获取对象
	 * @param domains
	 * @param pid
	 * @param label
	 * @return
	 */
	public Domain getTreeId(List<Domain> domains,String pid,String label);
	
	/**
	 * 删除资源下的文件
	 * @param objectId
	 */
	public void deleteFileByObjectId(String objectId);
	
	/**
	 * 关联资源
	 * @param id
	 * @param relationIds
	 */
	public void assetRelation(String id,String relationIds);
	/**
	 * 添加衍生资源
	 * @param request
	 * @param response
	 */
	public void addReriveRes(String id,String relationIds);
	/**
	 * 查询衍生的资源,必须传入id
	 * @param request
	 * @param conditionList
	 * @return
	 */
	public String queryRerivesResource4Page(HttpServletRequest request,QueryConditionList conditionList,String reriveType);
	
	/**
	 * 删除关联关系
	 * @param id
	 * @param relationIds
	 * @return 
	 */
	public String delRelation(String id,String relationIds);
	
	/**
	 * 查询关联的资源,必须传入id
	 * @param request
	 * @param conditionList
	 * @return
	 */
	public String queryRelationsResource4Page(HttpServletRequest request,QueryConditionList conditionList);
	
	/**
	 * 查询版权预警
	 */
	public String queryCopyrightWarning(String authEndDateBegin,String authEndDateEnd,String authEndDateEndField);
	
	/**
	 * 获取资源详细信息（针对所有资源，包括Asset和Ca）
	 * @param objectId
	 * @return String
	 */
	public String getRessource(String objectId);
	
	/**
	 * 互换学段，版本位置
	 * @param codes
	 * @return
	 */
	public String transPosition(String codes);
	/**
	 * CA查重
	 * @param source
	 * @param type
	 * @param title
	 * @param creator
	 * @return
	 */
	public List<Ca> getCaResourceByMoreCondition(String source,String type,String title,String creator,String isbn,String module);
	
	/**
	 * CA带版本查重
	 * @param source
	 * @param type
	 * @param title
	 * @param creator
	 * @return
	 */
	public List<Ca> getCaResourceByResVersion(String source,String type,String title,String creator,String resVersion,String isbn,String module);
	
	/**
	 * 资源模块统计
	 * @param time
	 * @return
	 */
	public String resModuleStatistics(String time);
	/**
	 * 删除CA对象
	 * @param objectId
	 */
	public void deleteCaResourceById(String objectId);
	
	/**
	 * 根据资源模块获取分类
	 * @param module
	 * @return
	 */
	public String domainsByMoudle(String module);
	
	public String getCustomMetaData(String module);
	public String queryImportCode(String codes,String domainType);
	/**
	 * 根据知识点最后节点名获得知识点ID
	 * @param knowledgeName
	 * @return
	 */
	public String getKnowledgeIDByName(String knowledgeName);
	
	/**
	 * 根据模块获取学段options
	 * @param module
	 * @return
	 */
	public String getEducationalPhaseOptions(String module);
	
	/**
	 * 解析分页查询条件
	 * @param request
	 * @param conditionList
	 * @return
	 */
	public String parseCondition(HttpServletRequest request,QueryConditionList conditionList);
	
	/**
	 * 获取所有高级查询的元数据对象
	 * @param hql
	 * @param downType TODO
	 * @return
	 */
	public List<MetaDataDC> getAllMetaDataDC(String hql, String downType);
	/**
	 * 查询出版资源
	 * @param hql
	 * @param url
	 * @return
	 */
	public String query4PageByPubRes(String hql, String url);
	
	/**
	 * 查询所以的中文XPATH
	 * @param id
	 * @param relationIds
	 */
	public String queryDomainXpathNames();
	/**
	 * 删除衍生资源
	 * @param id
	 * @param derivesIds
	 * @return
	 */
	public String delDerives(String id,String derivesIds);
	/**
	 * 获取资源详细
	 * @param objectId
	 * @return String
	 */
	public void getImgFiles();
	
	public List<Asset> getResourceByMoreConditionAndXpath(String source,String type,String title,String creator,String module,String md5,String xpath);
	
	public List<Asset> getResourceByXpathAndVersion(String source,String type,String title,String creator,String md5,String resVersion,String module,String xpath);
	
	/**
	 * 来源资源
	 * @param id
	 * @param relationIds
	 */
	public void addSourceRes(String id,String reriveIds);
	/**
	 * 删除来源资源
	 * @param id
	 * @param relationIds
	 */
	public String delSource(String id,String sourceIds);
}
