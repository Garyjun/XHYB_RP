package com.brainsoon.resource.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.bsrcm.search.util.DateUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.image.ImgCoverUtil;
import com.brainsoon.resource.action.BresAction;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.AssetList;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CaList;
import com.brainsoon.semantic.ontology.model.Domain;
import com.brainsoon.semantic.ontology.model.DomainList;
import com.brainsoon.semantic.ontology.model.File;
import com.brainsoon.semantic.ontology.model.FileList;
import com.brainsoon.semantic.ontology.model.MetaDataDC;
import com.brainsoon.semantic.ontology.model.MetadataResult;
import com.brainsoon.system.model.User;
import com.brainsoon.system.support.SystemConstants.ResourceType;
import com.google.gson.Gson;

/**
 * 封装底层与jenal交互接口
 * @author zuo
 *
 */
@Service
public class BaseSemanticSerivce implements IBaseSemanticSerivce {
	protected  Logger logger = LoggerFactory.getLogger(getClass());
	private static final String ASSET_CREATE_URL = WebappConfigUtil.getParameter("ASSET_CREATE_URL");
	private static final String ASSET_OVERRIDE_URL = WebappConfigUtil.getParameter("ASSET_OVERRIDE_URL");
	private static final String DOMAINNODESBYCODE_URL = WebappConfigUtil.getParameter("DOMAINNODESBYCODE_URL");
	private static final String ASSET_LIST_URL = WebappConfigUtil.getParameter("ASSET_LIST_URL");
	private static final String CARELATIONS_URL = WebappConfigUtil.getParameter("CARELATIONS_URL");
	private static final String CARERIVS_QUERY_URL = WebappConfigUtil.getParameter("CARERIVS_QUERY_URL");
	private static final String CABORDER_QUERY_URL = WebappConfigUtil.getParameter("CABORDER_QUERY_URL");
	private static final String CARERIVS_CREATE_URL = WebappConfigUtil.getParameter("CARERIVS_CREATE_URL");
	private static final String CASOURCES_CREATE_URL = WebappConfigUtil.getParameter("CASOURCES_CREATE_URL");
	private static final String ASSET_DETAIL_URL = WebappConfigUtil.getParameter("ASSET_DETAIL_URL");
	private static final String ASSET_FILE_URL = WebappConfigUtil.getParameter("ASSET_FILE_URL");
	private static final String ASSET_DELETE_URL = WebappConfigUtil.getParameter("ASSET_DELETE_URL");
	private static final String CA_DELETE_URL = WebappConfigUtil.getParameter("CA_DELETE_URL");
	private static final String ASSET_REPEAT_URL = WebappConfigUtil.getParameter("ASSET_REPEAT_URL");
	private static final String ASSET_REPEATWITHRESVERSION_URL = WebappConfigUtil.getParameter("ASSET_REPEATWITHRESVERSION_URL");
	private static final String ASSET_IMPORT_REPEAT = WebappConfigUtil.getParameter("ASSET_IMPORT_REPEAT");
	private static final String ASSET_IMPORT_REPEAT_VERSION = WebappConfigUtil.getParameter("ASSET_IMPORT_REPEAT_VERSION");
	private static final String XPATHCODE_URL = WebappConfigUtil.getParameter("XPATHCODE_URL");
	private static final String BOOKNODESBYCODE_URL = WebappConfigUtil.getParameter("BOOKNODESBYCODE_URL");
	private static final String DELETE_FILE_URL = WebappConfigUtil.getParameter("DELETE_FILE_URL");
	private static final String CA_RELATION_URL = WebappConfigUtil.getParameter("CA_RELATION_URL");
	private static final String CARERIVS_URL = WebappConfigUtil.getParameter("CARERIVS_URL");
	private static final String CA_RELATION_DEL_URL = WebappConfigUtil.getParameter("CA_RELATION_DEL_URL");
	private static final String PUBLISH_COPYRIGHTWARNING_URL = WebappConfigUtil.getParameter("PUBLISH_COPYRIGHTWARNING_URL");
	private static final String RES_DETAIL_URL = WebappConfigUtil.getParameter("RES_DETAIL_URL");
	private static final String CA_REPEAT_URL = WebappConfigUtil.getParameter("CA_REPEAT_URL");
	private static final String CA_REPEAT_RESVERSION_URL = WebappConfigUtil.getParameter("CA_REPEAT_RESVERSION_URL");
	private static final String ADVANCE_SEARCH_RESOURCE_LIST = WebappConfigUtil.getParameter("ADVANCE_SEARCH_RESOURCE_LIST");
	private static final String ADVANCE_SEARCH_RESOURCE_FILE_LIST = WebappConfigUtil.getParameter("ADVANCE_SEARCH_RESOURCE_FILE_LIST");
	private static final String STATISTICS_RES_COLLECTION = WebappConfigUtil.getParameter("STATISTICS_RES_COLLECTION");
	private static final String STATISTICS_RES_PUBCOLLECTION = WebappConfigUtil.getParameter("STATISTICS_RES_PUBCOLLECTION");
	private static final String RESMODULESTATISTICS_URL = WebappConfigUtil.getParameter("RESMODULESTATISTICS_URL");
	private static final String DOMAINSBYMOUDLE_URL = WebappConfigUtil.getParameter("DOMAINSBYMOUDLE_URL");
	private static final String CUSTOMMETADATA_URL = WebappConfigUtil.getParameter("CUSTOMMETADATA_URL");
	private static final String QUERYIMPORTCODE_URL = WebappConfigUtil.getParameter("QUERYIMPORTCODE_URL");
	private static final String EDUCATIONALPHASE_URL = WebappConfigUtil.getParameter("EDUCATIONALPHASE_URL");
	private static final String QUERY_KNOWLEDGEID_URL = WebappConfigUtil.getParameter("QUERY_KNOWLEDGEID_URL");
	private static final String QUERY_DOMAIN_XPATHNAMES = WebappConfigUtil.getParameter("QUERY_DOMAIN_XPATHNAMES");
	private static final String QUERY_FILE_IMAGE = WebappConfigUtil.getParameter("QUERY_FILE_IMAGE");
	private static final String UPDATE_COVER_PATH = WebappConfigUtil.getParameter("UPDATE_COVER_PATH");
	private static final String CA_DERIVE_DEL_URL = WebappConfigUtil.getParameter("CA_DERIVE_DEL_URL");
	private static final String CA_SOURCE_DEL_URL = WebappConfigUtil.getParameter("CA_SOURCE_DEL_URL");
	/**
	 * 处理没有资源的文件
	 * @param objectId
	 * @return String
	 */
	public void doResFile(){
		
	}
	/**
	 * 获取资源详细
	 * @param objectId
	 * @return String
	 */
	public void getImgFiles(){
		HttpClientUtil http = new HttpClientUtil();
		String fileList =  http.executeGet(QUERY_FILE_IMAGE);
		Gson gson = new Gson();
		FileList fileLists = gson.fromJson(fileList, FileList.class);
		if(fileLists != null){
			List<File> files = fileLists.getFiles();
			if(files != null && files.size()>0){
				for(File file:files){
					String filePath = file.getPath();
					filePath = filePath.replaceAll("\\\\", "/");
					String fileDir = filePath.substring(0, filePath.lastIndexOf("/"));
					String coverpath = fileDir+"/thumb/cover.jpg";
					String absCoverpath = BresAction.FILE_ROOT+coverpath;
					java.io.File coverFile = new java.io.File(absCoverpath);
					if(!coverFile.exists()){
						try{
							java.io.File srcFile = new java.io.File(BresAction.FILE_ROOT+filePath);
							if(srcFile.exists()){
								ImgCoverUtil.conver2Other(BresAction.FILE_ROOT+filePath, absCoverpath);
//								String updateCoverUrl = UPDATE_COVER_PATH+"?resourceId=" + file.getResource() +"&path=" + URLEncoder.encode(coverpath, "utf8");
//					        	//执行操作
//					       	 	String reTurnStr = http.executeGet(updateCoverUrl);
//					       	 	if(StringUtils.isBlank(reTurnStr) || !reTurnStr.equals("0")){
//					       	 		logger.error("更新到RDF元数据失败！");
//					       	 	}else{
//					       	 		logger.info("更新到RDF元数据成功。");
//					       	 	}
							}else{
								logger.info("源文件不存在YYYYYYYYYYYYYYYYYYYYYYYY==========");
							}
						}catch(Exception e){
							logger.info("9999999999999999999999图片提取封面出错================"+e.getMessage());
						}
					}
				}
			}
		}
	}
	/**
	 * 创建资源
	 * @param body
	 * @return
	 */
	public String createResource(String body){
		HttpClientUtil http = new HttpClientUtil();
		return http.postJson(ASSET_CREATE_URL,body);
	}
	/**
	 * 覆盖资源
	 * @param body
	 * @return
	 */
	public String assetOverride(String body){
		HttpClientUtil http = new HttpClientUtil();
		return http.postJson(ASSET_OVERRIDE_URL,body);
	}
	
	/**
	    * 增加版权信息
	    * @param ca
	    * @param uploadFile
	    * @throws Exception
	    */
	   public String saveAssetCopyright(Asset asset) throws Exception{
		    logger.debug("******run at saveRes***********");
			logger.debug("jsonTree ");
			Gson gson=new Gson();
			String paraJson=gson.toJson(asset);
			logger.debug(paraJson);
		    HttpClientUtil http = new HttpClientUtil();
		    String result=http.postJson(ASSET_CREATE_URL,paraJson);
		    logger.debug("result *** "+result);
		    return result;
	   }
	
	/**
	 * 获取下一级节点
	 * @param codes
	 * @param domainType
	 * @return String
	 */
	public String getDomainNode(String codes,String domainType){
		if(codes.indexOf("T06")>0){
			domainType = "1";
		}
		if(StringUtils.equals(domainType, "0")){
			//获取版本树的节点，从学段开始，所以截掉头两位
			codes = StringUtils.substringAfter(StringUtils.substringAfter(codes, ","),",");
			codes = transPosition(codes);
		}else{
			String [] codesArray = StringUtils.split(codes,",");
			String module = codesArray[0];
			if(!StringUtils.equalsIgnoreCase(module, "TB")){
				//非同步资源，去掉资源类型
				List<String> ar = new ArrayList<String>(Arrays.asList(codesArray));
				ar.remove(1);
				codes = StringUtils.join(ar,",");
			}
		}
		HttpClientUtil http = new HttpClientUtil();
		return http.executeGet(DOMAINNODESBYCODE_URL+"?codes="+codes+"&domainType="+domainType+"&privilage="+LoginUserUtil.getAuthResCodes()+"&authType="+LoginUserUtil.getAuthResTypes());
		//return http.executeGet(DOMAINNODESBYCODE_URL+"?codes="+codes+"&domainType="+domainType);
	}
	/**
	 * 互换学段，版本位置
	 * @param codes
	 * @return
	 */
	public String transPosition(String codes) {
		int s1 = StringUtils.indexOf(codes, ",");
		//查询时学段，版本互换位置
		if(s1 > 0){
			String xd = StringUtils.substring(codes, 0,s1);
			String last = StringUtils.substring(codes, s1 + 1);
			String bb = StringUtils.substringBefore(last, ",");
			if(StringUtils.isNotBlank(bb)){
				bb = bb + ",";
			}
			last = StringUtils.substringAfter(last, ",");
			if(StringUtils.isNotBlank(last)){
				last = "," + last;
			}
			codes = bb + xd + last;
		}
		return codes;
	}
	
	/**
	 * 分页查询，智能添加前台参数，读取rdf库
	 * 2014年6月17日 加入参数 privilage 权限过滤
	 * @param request
	 * @param conditionList
	 * @return String json
	 */
	public String queryResource4Page(HttpServletRequest request,QueryConditionList conditionList){
		String flag = request.getParameter("flag");
		String hql = parseCondition(request, conditionList);
		HttpClientUtil http = new HttpClientUtil();
		if(StringUtils.isNotBlank(flag)&& "1".equals(flag)){
			return http.executeGet(ADVANCE_SEARCH_RESOURCE_LIST + "?"+hql+"&privilage="+LoginUserUtil.getAuthResCodes());
		}else{
			return http.executeGet(ASSET_LIST_URL + "?"+hql+"&privilage="+LoginUserUtil.getAuthResCodes());
		}
		
		//加权限后覆盖的资源查不出来
	//	return http.executeGet(ASSET_LIST_URL + "?"+hql.toString());
	}
	
	/**
	 * 分页查询，智能添加前台参数， 读取rdf库 2014年6月17日 加入参数 privilage 权限过滤
	 * 
	 * @param hql
	 * @param operate
	 * @return String json
	 */
	public String query4Page(String hql, String operate) {
		String url = "";
		HttpClientUtil http = new HttpClientUtil();
//		hql = hql + "&privilage=" + LoginUserUtil.getAuthResCodes();
		if (StringUtils.equals(operate, "advance_search")) {
			url = ADVANCE_SEARCH_RESOURCE_LIST;
		} else if (StringUtils.equals(operate, "res_collection")) {
			UserInfo userInfo = LoginUserUtil.getLoginUser();
		    if(userInfo.getPlatformId()==1) {
		    	url = STATISTICS_RES_COLLECTION;
		    } else if(userInfo.getPlatformId()==2) {
		    	url = STATISTICS_RES_PUBCOLLECTION;
		    }
		}
		return http.executeGet(url + "?" + hql);
	}
	@Override
	public String query4PageByPubRes(String hql, String url) {
		HttpClientUtil http = new HttpClientUtil();
		return http.executeGet(url + "?" + hql);
	}
	
	/**
	 * 获取所有高级查询的元数据对象
	 * @param hql
	 * @return
	 */
//	public List<MetaDataDC> getAllMetaDataDC(String hql, String downType) {
//		HttpClientUtil http = new HttpClientUtil();
//		String url = ADVANCE_SEARCH_RESOURCE_FILE_LIST;
//		if (StringUtils.equals("metadata", downType)) {
//			url = ADVANCE_SEARCH_RESOURCE_LIST;
//		}
//		String datas = http.executeGet(url + "?" + hql);
//		if (StringUtils.isNotBlank(datas)) {
//			ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
//			try {
//				MetadataResult list = mapper.readValue(datas, MetadataResult.class);
//				return list.getRows();
//			} catch (JsonParseException e) {
//				e.printStackTrace();
//			} catch (JsonMappingException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//		}
//		return null;
//	}
	public String parseCondition(HttpServletRequest request,
			QueryConditionList conditionList) {
		StringBuffer hql = new StringBuffer();
		hql.append("page=").append(request.getParameter("page")).append("&size=").append(conditionList.getPageSize());
		//hql.append("&sort=").append(request.getParameter("sort")).append("&order=").append(request.getParameter("order"));
		//组装查询
		if(null != conditionList){
			
			List<QueryConditionItem> items = conditionList.getConditionItems();
			
			for (QueryConditionItem queryConditionItem : items) {
				String filedName = queryConditionItem.getFieldName();
				try {
					hql.append("&").append(filedName).append("=").append(URLEncoder.encode(queryConditionItem.getValue()+"","utf-8"));
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage());
				}
			}
			
		}
		return hql.toString();
	}
	/**
	 * 查询关联的资源,必须传入id
	 * @param request
	 * @param conditionList
	 * @return
	 */
	public String queryRelationsResource4Page(HttpServletRequest request,QueryConditionList conditionList){
		String hql = parseCondition(request, conditionList);
		HttpClientUtil http = new HttpClientUtil();
		//http://localhost:8080/semantic_index_server/ontologyListQuery/assetRelations?id=asset-24d7bca8-9f25-4b10-af86-fe8b660f84a5
		//jenal提供的接口现在不支持分页,所以现在自己拼装
		String datas = http.executeGet(CARELATIONS_URL+"?"+hql);
		return datas;
	}
	/**
	 * 查询衍生的资源,必须传入id
	 * @param request
	 * @param conditionList
	 * @return
	 */
	public String queryRerivesResource4Page(HttpServletRequest request,QueryConditionList conditionList,String reriveType){
		String hql = parseCondition(request, conditionList);
		HttpClientUtil http = new HttpClientUtil();
		//jenal提供的接口现在不支持分页,所以现在自己拼装
		String datas = "";
		if("border".equals(reriveType)){
			datas = http.executeGet(CABORDER_QUERY_URL+"?"+hql);
		}else{
			datas = http.executeGet(CARERIVS_QUERY_URL+"?"+hql);
		}
		return datas;
	}
	/**
	 * 获取资源详细
	 * @param objectId
	 * @return String
	 */
	public String getResourceDetailById(String objectId){
		HttpClientUtil http = new HttpClientUtil();
		String res =  http.executeGet(ASSET_DETAIL_URL+"?id="+objectId+"&privilage="+LoginUserUtil.getAuthResCodes()+"&authType="+LoginUserUtil.getAuthResTypes());
//		String res = http.executeGet(ASSET_DETAIL_URL+"?id="+objectId);
		//转译单引号
		res = StringUtils.replace(res, "\'", "\\'");
		return res;
	}
	
	/**
	 * 获取资源对象
	 * @param objectId
	 * @return Asset
	 */
	public Asset getResourceById(String objectId){
		String str = getResourceDetailById(objectId);
		if(StringUtils.indexOf(str, "commonMetaData") > -1){
			Gson gson = new Gson();
			return gson.fromJson(str, Asset.class);
		}else{
			return null;
		}
	}
	/**
	 * 获取资源关联的文件
	 * @param objectId
	 * @return List<File>
	 */
	public List<File> getResourceFilesById(String objectId){
		HttpClientUtil http = new HttpClientUtil();
		String fileList = http.executeGet(ASSET_FILE_URL + "?resourceId="+objectId);
		Gson gson = new Gson();
		FileList list = gson.fromJson(fileList, FileList.class);
		//考虑支持list
		return list.getFiles();
	}
	
	/**
	 * 删除资源对象，只删除rdf库中的
	 * @param objectId
	 */
	public void deleteResourceById(String objectId){
		HttpClientUtil http = new HttpClientUtil();
		http.executeGet(ASSET_DELETE_URL+"?id="+objectId);
	}
	/**
	 * 删除CA对象
	 * @param objectId
	 */
	public void deleteCaResourceById(String objectId){
		HttpClientUtil http = new HttpClientUtil();
		http.executeGet(CA_DELETE_URL+"?id="+objectId);
	}
	public List<Asset> getResourceByMoreCondition(String source,String type,String title,String creator,String module,String md5){
		String params = "";
		try {
			params = "source="+URLEncoder.encode(source,"utf-8")+"&type="+type+"&title="+URLEncoder.encode(title,"utf-8")+"&creator="+URLEncoder.encode(creator,"utf-8")+"&module="+module+"&md5="+md5;
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		HttpClientUtil http = new HttpClientUtil();
		String assetList = http.executeGet(ASSET_REPEAT_URL + "?"+params);
		Gson gson = new Gson();
		AssetList list = gson.fromJson(assetList, AssetList.class);
		return list.getAssets();
	}
	public List<Asset> getResourceByMoreConditionAndXpath(String source,String type,String title,String creator,String module,String md5,String xpath){
		String params = "";
		try {
			params = "source="+URLEncoder.encode(source,"utf-8")+"&type="+type+"&title="+URLEncoder.encode(title,"utf-8")+"&creator="+URLEncoder.encode(creator,"utf-8")+"&module="+module+"&md5="+md5+"&xPath="+xpath;
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		HttpClientUtil http = new HttpClientUtil();
		String assetList = http.executeGet(ASSET_IMPORT_REPEAT + "?"+params);
		Gson gson = new Gson();
		AssetList list = gson.fromJson(assetList, AssetList.class);
		return list.getAssets();
	}
	public List<Asset> getResourceByResVersion(String source,String type,String title,String creator,String md5,String resVersion,String module){
		String params = "";
		try {
			params = "source="+URLEncoder.encode(source,"utf-8")+"&type="+type+"&title="+URLEncoder.encode(title,"utf-8")+"&creator="+URLEncoder.encode(creator,"utf-8")+"&md5="+md5+"&module="+module;
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		HttpClientUtil http = new HttpClientUtil();
		String assetList = http.executeGet(ASSET_REPEATWITHRESVERSION_URL + "?"+params);
		Gson gson = new Gson();
		AssetList list = gson.fromJson(assetList, AssetList.class);
		return list.getAssets();
	}
	
	public List<Asset> getResourceByXpathAndVersion(String source,String type,String title,String creator,String md5,String resVersion,String module,String xpath){
		String params = "";
		try {
			params = "source="+URLEncoder.encode(source,"utf-8")+"&type="+type+"&title="+URLEncoder.encode(title,"utf-8")+"&creator="+URLEncoder.encode(creator,"utf-8")+"&md5="+md5+"&module="+module+"&xPath="+xpath;
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		HttpClientUtil http = new HttpClientUtil();
		String assetList = http.executeGet(ASSET_IMPORT_REPEAT_VERSION + "?"+params);
		Gson gson = new Gson();
		AssetList list = gson.fromJson(assetList, AssetList.class);
		return list.getAssets();
	}
	public List<Ca> getCaResourceByMoreCondition(String source,String type,String title,String creator,String isbn,String module){
		String params = "";
		try {
			params = "source="+URLEncoder.encode(source,"utf-8")+"&type="+type+"&title="+URLEncoder.encode(title,"utf-8")+"&creator="+URLEncoder.encode(creator,"utf-8")+"&isbn="+URLEncoder.encode(isbn,"utf-8")+"&module="+module;
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		HttpClientUtil http = new HttpClientUtil();
		String caList = http.executeGet(CA_REPEAT_URL + "?"+params);
		Gson gson = new Gson();
		CaList list = gson.fromJson(caList, CaList.class);
		return list.getCas();
	}
	
	public List<Ca> getCaResourceByResVersion(String source,String type,String title,String creator,String resVersion,String isbn,String module){
		String params = "";
		try {
			params = "source="+URLEncoder.encode(source,"utf-8")+"&type="+type+"&title="+URLEncoder.encode(title,"utf-8")+"&creator="+URLEncoder.encode(creator,"utf-8")+"&resVersion="+resVersion+"&isbn="+isbn+"&module="+module;
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		HttpClientUtil http = new HttpClientUtil();
		String caList = http.executeGet(CA_REPEAT_RESVERSION_URL + "?"+params);
		Gson gson = new Gson();
		CaList list = gson.fromJson(caList, CaList.class);
		return list.getCas();
	}
	public String [] xpathCode(String module,String type,String educational_phase,String version,String subject,String grade,String fascicule,int domainType){
		StringBuffer params = new StringBuffer();
		try {
			if(domainType == 1){
				params.append(",").append(URLEncoder.encode(module,"utf-8"));
				if(StringUtils.equalsIgnoreCase(module, "TB") || StringUtils.equalsIgnoreCase(module, "同步资源")){
					params.append(",").append(URLEncoder.encode(type,"utf-8"));
				}
				if(StringUtils.isNotBlank(educational_phase)){
					params.append(",").append(URLEncoder.encode(educational_phase,"utf-8"));
				}
				if(StringUtils.isNotBlank(version)){
					params.append(",").append(URLEncoder.encode(version,"utf-8"));
				}
			}else{
				//版本在前
				if(StringUtils.isNotBlank(version)){
					params.append(",").append(URLEncoder.encode(version,"utf-8"));
				}
				if(StringUtils.isNotBlank(educational_phase)){
					params.append(",").append(URLEncoder.encode(educational_phase,"utf-8"));
				}
			}
			if(StringUtils.isNotBlank(subject)){
				params.append(",").append(URLEncoder.encode(subject,"utf-8"));
			}
			if(StringUtils.isNotBlank(grade)){
				params.append(",").append(URLEncoder.encode(grade,"utf-8"));
			}
			if(StringUtils.isNotBlank(fascicule)){
				params.append(",").append(URLEncoder.encode(fascicule,"utf-8"));
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		params = params.delete(0, 1);
		HttpClientUtil http = new HttpClientUtil();
		String rs = http.executeGet(XPATHCODE_URL +"?domainType="+domainType+"&xpathName="+params.toString());
		if(StringUtils.isBlank(rs)){
			return null;
		}
		String[] rsAr = rs.split(",");
		if(domainType == 1){
			if(!StringUtils.equalsIgnoreCase(module, "TB")){
				List<String> codeArray = new ArrayList<String>(Arrays.asList(rsAr));
				codeArray.add(1, ResourceType.getValueByDesc(type));
				rsAr = codeArray.toArray(new String[]{});
			}
		}
		return rsAr;
	}
	
	/**
	 * 获取单元树
	 * @param code
	 * @param domainType
	 * @return List<Domain>
	 */
	public List<Domain> getUnitTree(String code,String domainType){
		HttpClientUtil http = new HttpClientUtil();
		String rs = "";
		if(StringUtils.equals(domainType, "0")){
			code = StringUtils.substringAfter(StringUtils.substringAfter(code, ","),",");
			code = transPosition(code);
			rs = http.executeGet(BOOKNODESBYCODE_URL + "?codes="+code);
		}else{
			rs = http.executeGet(DOMAINNODESBYCODE_URL + "?codes="+code+"&domainType="+domainType);
		}
		Gson gson = new Gson();
		DomainList list = gson.fromJson(rs, DomainList.class);
		return list.getDomains();
	}
	/**
	 * 根据中文名字，获取对象
	 * @param domains
	 * @param pid
	 * @param label
	 * @return
	 */
	public Domain getTreeId(List<Domain> domains,String pid,String label){
		String cLabel = "";
		String cPid = "";
		for (Domain domain : domains) {
			cLabel = domain.getLabel();
			if(StringUtils.equalsIgnoreCase(cLabel, label)){
				return domain;//暂时屏蔽级别
				/*int level = domain.getLevel();
				if(StringUtils.isNotBlank(pid)){
					cPid = domain.getPid();
					if(level > 1 && StringUtils.equalsIgnoreCase(cPid, pid)){
						return domain;
					}
				}else if(level == 1){//根节点
					return domain;
				}*/
			}
		}
		return null;
	}
	
	public void deleteFileByObjectId(String objectId){
		HttpClientUtil http = new HttpClientUtil();
		http.executeGet(DELETE_FILE_URL+"?id="+objectId);
	}
	
	/**
	 * 关联资源
	 * @param id
	 * @param relationIds
	 */
	public void assetRelation(String id,String relationIds){
		HttpClientUtil http = new HttpClientUtil();
		http.executeGet(CA_RELATION_URL + "?id="+id+"&relationIds="+relationIds);
	}
	/**
	 * 衍生资源
	 * @param id
	 * @param relationIds
	 */
	public void addReriveRes(String id,String reriveIds){
		HttpClientUtil http = new HttpClientUtil();
		http.executeGet(CARERIVS_CREATE_URL + "?id="+id+"&reriveIds="+reriveIds);
	}
	/**
	 * 来源资源
	 * @param id
	 * @param relationIds
	 */
	public void addSourceRes(String id,String reriveIds){
		HttpClientUtil http = new HttpClientUtil();
		http.executeGet(CASOURCES_CREATE_URL + "?id="+id+"&sourceIds="+reriveIds);
	}
	/**
	 * 删除关联关系
	 * @param id
	 * @param relationIds
	 */
	public String delRelation(String id,String relationIds){
		HttpClientUtil http = new HttpClientUtil();
		String stag = http.executeGet(CA_RELATION_DEL_URL + "?id="+id+"&relationIds="+relationIds);
		return stag;
	}
	/**
	 * 删除衍生资源
	 * @param id
	 * @param relationIds
	 */
	public String delDerives(String id,String derivesIds){
		HttpClientUtil http = new HttpClientUtil();
		String stag = http.executeGet(CA_DERIVE_DEL_URL + "?id="+id+"&derivesIds="+derivesIds);
		return stag;
	}
	
	/**
	 * 删除来源资源
	 * @param id
	 * @param relationIds
	 */
	public String delSource(String id,String sourceIds){
		HttpClientUtil http = new HttpClientUtil();
		String stag = http.executeGet(CA_SOURCE_DEL_URL + "?id="+id+"&sourceIds="+sourceIds);
		return stag;
	}
	/**
	 * 查询所以的中文XPATH
	 * @param id
	 * @param relationIds
	 */
	public String queryDomainXpathNames(){
		HttpClientUtil http = new HttpClientUtil();
		return http.executeGet(QUERY_DOMAIN_XPATHNAMES);
	}
	/**
	 * 查询版权预警
	 */
	public String queryCopyrightWarning(String authEndDateBegin,String authEndDateEnd,String authEndDateEndField){
		HttpClientUtil http = new HttpClientUtil();
//		try {
			//转换成统一格式
//			authEndDateBegin = ResUtils.simpleDate(authEndDateBegin);
//			authEndDateEnd = ResUtils.simpleDate(authEndDateEnd);
//			authEndDateBegin = DateUtil.parseTimes(authEndDateBegin).getTime()+"";
//			authEndDateEnd = DateUtil.parseTimes(authEndDateEnd).getTime()+"";
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		String hql = "authEndDateBegin="+authEndDateBegin+"&authEndDateEnd="+authEndDateEnd+"&authEndDateEndField="+authEndDateEndField;
		return http.executeGet(WebappConfigUtil.getParameter("PUBLISH_COPYRIGHTWARNING_URL") + "?"+hql);
	}
	
	@Override
	public String getRessource(String objectId){
		HttpClientUtil http = new HttpClientUtil();
		return http.executeGet(RES_DETAIL_URL+"?id="+objectId);
	}
	
	/**
	 * 资源模块统计
	 * @param time
	 * @return
	 */
	public String resModuleStatistics(String time){
		HttpClientUtil http = new HttpClientUtil();
		return http.executeGet(RESMODULESTATISTICS_URL + "?create_date="+time);
	}
	
	/**
	 * 根据资源模块获取分类
	 * @param module
	 * @return
	 */
	public String domainsByMoudle(String module){
		HttpClientUtil http = new HttpClientUtil();
		String data = http.executeGet(DOMAINSBYMOUDLE_URL + "?code="+module+"&containMoudle=1");
		Gson gson = new Gson();
		DomainList list = gson.fromJson(data, DomainList.class);
		List<Domain> domains = list.getDomains();
		for (Domain object : domains) {
			object.setName(object.getLabel());
		}
		return gson.toJson(domains);
	}
	
	public String getCustomMetaData(String module){
		HttpClientUtil http = new HttpClientUtil();
		return http.executeGet(CUSTOMMETADATA_URL + "?name="+module);
	}
	public String queryImportCode(String codes,String domainType){
		HttpClientUtil http = new HttpClientUtil();
		return http.executeGet(QUERYIMPORTCODE_URL + "?xpathName="+codes+"&domainType="+domainType);
	}
	
	public String getEducationalPhaseOptions(String module){
		//return "<option value=\"\">请选择</option><option value=\"K\">学前</option><option value=\"P\">小学</option><option value=\"M\">初中</option><option value=\"H\">高中</option></select>";
		HttpClientUtil http = new HttpClientUtil();
		
		String data = http.executeGet(EDUCATIONALPHASE_URL + "?codes="+module+"&domainType=1"+"&privilage="+LoginUserUtil.getAuthResCodes()+"&authType="+LoginUserUtil.getAuthResTypes());
		
		if(StringUtils.isNotBlank(data)){
			Gson gson = new Gson();
			DomainList list = gson.fromJson(data, DomainList.class);
			List<Domain> domains = list.getDomains();
			StringBuffer str = new StringBuffer(domains.size()*30);
			str.append("<option value=\"\">请选择</option>");
			for (Domain object : domains) {
				str.append("<option value=\""+object.getCode()+"\">"+object.getLabel()+"</option>");
			}
			return str.toString();
		}else{
			return "<option value=\"\">请选择</option>";
		}
	}
	@Override
	public String getKnowledgeIDByName(String knowledgeName) {
		HttpClientUtil http = new HttpClientUtil();
		try {
			knowledgeName = URLEncoder.encode(knowledgeName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String data = http.executeGet(QUERY_KNOWLEDGEID_URL + "?name="+knowledgeName);
		return data;
	}
	public static void main(String[] args) {
//		String dd = "T1,T2,T4,T5,T6";
//		BaseSemanticSerivce ss = new BaseSemanticSerivce();
//		String json = ss.getRessource("urn:asset-00c0d769-e945-4b8b-9d06-555100c9c01b");
//		System.err.println(json);
		
		String o = "http://10.130.29.14:8090/semantic_index_server/ontologyDetailQuery/xpathCode?domainType=1&xpathName=%E5%90%8C%E6%AD%A5%E8%B5%84%E6%BA%90,%E5%88%9D%E4%B8%AD,%E4%BA%BA%E6%95%99%E7%89%88,%E5%8C%96%E5%AD%A6,%E4%B9%9D%E5%B9%B4%E7%BA%A7,%E4%B8%8B%E5%86%8C";
		HttpClientUtil http = new HttpClientUtil();
		String rs = http.executeGet(o);
		System.out.println(rs + "=============");
		String[] rsAr = rs.split(",");
		System.out.println(rsAr.length);
	}
	@Override
	public List<MetaDataDC> getAllMetaDataDC(String hql, String downType) {
		return null;
	}
	
}
