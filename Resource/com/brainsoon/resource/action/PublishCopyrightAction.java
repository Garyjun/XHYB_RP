package com.brainsoon.resource.action;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.OSUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.ICopyrightService;
import com.brainsoon.resource.service.IPublishResService;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.support.CopyrightTaskQueue;
import com.brainsoon.resource.support.ImportData;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.resource.util.CaUtil;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.JsonDataObject;
import com.brainsoon.semantic.ontology.model.SearchResultCa;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

/**
 * 版权管理
 * 
 * @author zhangpeng
 */
@Controller
public class PublishCopyrightAction extends BaseAction {

	/** 默认命名空间 **/
	private final String baseUrl = "/resMgmtService/";

	public final static String FILE_TEMP = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp);
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	private ICopyrightService copyrightService;
	@Autowired
	private IPublishResService publishResService;
	@Autowired
	private ISysParameterService sysParameterService ;
	@Autowired
	private ISysOperateService sysOperateService;
	/**
	 * 关键字查询
	 */
	@RequestMapping(value = baseUrl + "queryResListByAttr")
	public @ResponseBody void queryResListByAttr(HttpServletRequest request){
//		HttpServletRequest re = getRequest();
		String resType = request.getParameter("resType");
		String resTitle = request.getParameter("resTitle");
		String publisher = request.getParameter("publisher");
		String ip = OSUtil.getIpAddr(getRequest());
		Gson gson = new Gson();
		String status = isRequestIp(ip);
		if(StringUtils.isNotBlank(status)&&!status.equals("1")){
			String returnDetail[] = status.split("=");
			outputResult(returnDetail[1]);
			return;
		}
		JsonDataObject resultValue = new JsonDataObject();
		String queryUrl = WebappConfigUtil.getParameter("PUBLISH_QUERY_URL");
		HttpClientUtil http = new HttpClientUtil();
		StringBuffer hql = new StringBuffer();
		hql.append("page=").append(0).append("&size=").append(10);
		String metadataMap = "{";
		if(StringUtils.isNotBlank(resTitle)){
			metadataMap += "\"title\":\""+ resTitle + "\",";
		}
		if(StringUtils.isNoneBlank(publisher)){
			metadataMap += "\"publisherFirst\":\""+ publisher + "\"";
		}
		if(metadataMap.endsWith(",")){
			metadataMap = metadataMap.substring(0,metadataMap.length()-1);
		}
		metadataMap += "}";
		String metadataMapUTF8 = "";
		try {
			metadataMapUTF8 = URLEncoder.encode(metadataMap, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(metadataMap.length()>1&&!metadataMap.equals("{}")){
			hql.append("&metadataMap=").append(metadataMapUTF8);
		}
		if(StringUtils.isNotBlank(resType)){
			hql.append("&publishType=").append(resType);
		}
		String result = http.executeGet(queryUrl + "?" + hql.toString()+"&interfaceQuery=1");
		if(StringUtils.isNotBlank(result)){
			SearchResultCa searchCa = gson.fromJson(result, SearchResultCa.class);
			if(searchCa!=null){
				logger.info("------------------------+++++++++++++++-------------------"+searchCa.getTotal()+"----------------------+++++++++++++--------------------------");
			}
			List<Ca> cas = searchCa.getRows();
			List<Map<String,String>> maps = new ArrayList<>();
			for(Ca ca:cas){
				Map<String,String> map = ca.getMetadataMap();
				if(map!=null && map.size()>0){
					map.put("identifier", ca.getObjectId());
					maps.add(map);
				}
			}
			if(maps.size()>0){
				resultValue.setStatus("0");
				resultValue.setMsg("成功");
				resultValue.setData(gson.toJson(maps));
			}else{
				resultValue.setStatus("-1");
				resultValue.setMsg("失败,资源不存在");
				resultValue.setData("");
			}
		}else{
			resultValue.setStatus("-1");
			resultValue.setMsg("失败,资源不存在");
			resultValue.setData("");
		}
		outputResult(gson.toJson(resultValue));
	}
	/**
	 * 获取资源元数据信息接口
	 */
	@RequestMapping(value = baseUrl + "getResMetaDataByObjectId")
	public @ResponseBody void getResMetaDataByObjectId(@RequestParam("objectId") String objectId){
		String ip = OSUtil.getIpAddr(getRequest());
		Gson gson = new Gson();
		String status = isRequestIp(ip);
		if(StringUtils.isNotBlank(status)&&!status.equals("1")){
			String returnDetail[] = status.split("=");
			outputResult(returnDetail[1]);
			return;
		}
		String queryUrl = WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL");
		HttpClientUtil http = new HttpClientUtil();
		String result = http.executeGet(queryUrl + "?id=" + objectId);
		JsonDataObject resultValue = new JsonDataObject();
		if(StringUtils.isNotBlank(result)){
			Ca bookCa = gson.fromJson(result, Ca.class);
			if(bookCa!=null && bookCa.getObjectId()!=null && bookCa.getMetadataMap()!=null && bookCa.getMetadataMap().size()>0){
				resultValue.setStatus("0");
				resultValue.setMsg("成功");
				resultValue.setData(gson.toJson(bookCa.getMetadataMap()));
			}else{
				resultValue.setStatus("-1");
				resultValue.setMsg("失败,资源不存在");
				resultValue.setData("");
			}
		}else{
			resultValue.setStatus("-1");
			resultValue.setMsg("失败,资源不存在");
			resultValue.setData("");
		}
		outputResult(gson.toJson(resultValue));
	}
	/**
	 * 获取资源文件列表信息接口
	 */
	@RequestMapping(value = baseUrl + "getResFileInfoByObjectId")
	public @ResponseBody void getResFileInfoByObjectId(@RequestParam("objectId") String objectId,HttpServletRequest request){
		String ip = OSUtil.getIpAddr(getRequest());
		Gson gson = new Gson();
		String status = isRequestIp(ip);
		if(StringUtils.isNotBlank(status)&&!status.equals("1")){
			String returnDetail[] = status.split("=");
			outputResult(returnDetail[1]);
			return;
		}
		String queryUrl = WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL");
		HttpClientUtil http = new HttpClientUtil();
		String result = http.executeGet(queryUrl + "?id=" + objectId);
		JsonDataObject resultValue = new JsonDataObject();
		if(StringUtils.isNotBlank(result)){
			Ca bookCa = gson.fromJson(result, Ca.class);
			if(bookCa!=null && bookCa.getObjectId()!=null && bookCa.getRealFiles()!=null && bookCa.getRealFiles().size()>0){
				String src = "";
				List<SysParameter> sr = sysParameterService.findParaValue("commonDir");
				if(sr!=null && sr.size()>0){
					if(sr.get(0)!=null && sr.get(0).getParaValue()!=null){
						src = sr.get(0).getParaValue();
					}
				}
				src = src.replaceAll("\\\\", "/");
				if(!src.endsWith("/")){
					src = src+"/";
				};
				src = src +"resFile/";
				List<com.brainsoon.semantic.ontology.model.File> realFiles =  bookCa.getRealFiles();
				List<com.brainsoon.semantic.ontology.model.File> newFiles =  new ArrayList<com.brainsoon.semantic.ontology.model.File>();
				for(com.brainsoon.semantic.ontology.model.File realFile:realFiles){
					com.brainsoon.semantic.ontology.model.File file = new com.brainsoon.semantic.ontology.model.File();
					if("2".equals(realFile.getIsDir())){
						String path = realFile.getPath();
						path = path.replaceAll("\\\\", "/");
						String relativeDir = path.substring(0,path.lastIndexOf("/"));
						String srcPath = PublishResAction.FILE_ROOT+path;
						String newDir = src+ relativeDir;
						newDir = newDir.replaceAll("\\\\", "/");
						File tempDir = new File(newDir);
						if(!tempDir.exists()){
							tempDir.mkdirs();
						}
						if(!newDir.endsWith("/")){
							newDir +="/"; 
						}
						String newPath = newDir+realFile.getName();
						try {
							if(!new File(newPath).exists()){
								DoFileUtils.mkdir(newPath);
								FileUtils.copyFile(new File(srcPath), new File(newPath));
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						file.setName(realFile.getName());
						file.setFileByte(realFile.getFileByte());
						file.setPath(newPath);
						file.setFileType(realFile.getFileType());
						newFiles.add(file);
					}
				}
				resultValue.setStatus("0");
				resultValue.setMsg("成功");
				resultValue.setData(gson.toJson(newFiles));
			}else{
				resultValue.setStatus("-1");
				resultValue.setMsg("失败,文件不存在");
				resultValue.setData("");
			}
		}else{
			resultValue.setStatus("-1");
			resultValue.setMsg("失败,文件不存在");
			resultValue.setData("");
		}
		
		 String[] keys = { "fileMetadataMap" };  
		 ExclusionStrategy myExclusionStrategy = new ExclusionStrategy() {   
			 @Override   
			 public boolean shouldSkipField(FieldAttributes fa) {   
				 for (String key : keys) {  
			            if (key.equals(fa.getName())) {  
			                return true;  
			            }  
			        }  
				 return false;
			 }   
			 @Override   
			 public boolean shouldSkipClass(Class<?> clazz) {   return false;   }    
		 }; 
		gson = new GsonBuilder().setExclusionStrategies(myExclusionStrategy).create();
		outputResult(gson.toJson(resultValue));
	}
	
	
	/**
	 * 资源入库接口
	 */
	@RequestMapping(value = baseUrl + "resImport", method = {RequestMethod.POST }) 
	public @ResponseBody void resImport(@RequestParam("jsonObj") String jsonObj,HttpServletRequest request){
		String ip = OSUtil.getIpAddr(getRequest());
		Gson gson = new Gson();
		String status = isRequestIp(ip);
		if(StringUtils.isNotBlank(status)&&!status.equals("1")){
			String returnDetail[] = status.split("=");
			outputResult(returnDetail[1]);
			return;
		}
		String objectId = "";
		String src = "";
		List<SysParameter> sr = sysParameterService.findParaValue("commonDir");
		if(sr!=null && sr.size()>0){
			if(sr.get(0)!=null && sr.get(0).getParaValue()!=null){
				src = sr.get(0).getParaValue();
			}
		}
		src = src.replaceAll("\\\\", "/");
		if(!src.endsWith("/")){
			src = src+"/";
		};
		
		JSONObject obj = new JSONObject();
		JSONObject ob= (JSONObject) obj.fromObject(jsonObj);
		JSONArray  da = (JSONArray)ob.get("data");
		String  syncType = (String) ob.get("syncType");
		String  syncJsonFileName =src+"jsonLog/"+(String) ob.get("syncJsonFileName");
		String resType = (String) ob.get("resType");
		String repeatType = (String) ob.get("repeatType");
		JsonDataObject resultJsonValue = new JsonDataObject();
		int num = 0;
		int count = 0;
		boolean b = true;
		StringBuffer jsonStr = new StringBuffer();
		if("1".equals(syncType)){
			try {
				if(jsonObj!=null){
					num = da.size();
					for(int i=0;i<da.size();i++){
						Ca ca = null;
						Map<String,String> map = null;
						try {
							ca = CaUtil.convertJsonToCa(da.get(i).toString(),resType,repeatType);
							map = ca.getMetadataMap();
							Map<String, Map<String,String>> fileMetadataFlag = null;
							if(ca.getFileMetadataFlag()!=null && ca.getFileMetadataFlag().size()>0){
								fileMetadataFlag = ca.getFileMetadataFlag();
							}
							objectId = publishResService.saveImportPublishRes(ca, "",repeatType,fileMetadataFlag);
							String checkOpinion = "";
							sysOperateService.saveHistory(
									WorkFlowUtils.getExecuId(objectId, "pubresCheck"),
									checkOpinion,"资源草稿" , "添加", new Date(), Long.parseLong("180"));
							UserInfo userInfo = new UserInfo();
							userInfo.setUserId(Long.parseLong("180"));
							userInfo.setUsername("systest");
							userInfo.setPlatformId(1);
							if(StringUtils.isNotBlank(objectId)){
								HttpClientUtil http = new HttpClientUtil();
								String resourceDetail = http.executeGet(WebappConfigUtil
										.getParameter("PUBLISH_DETAIL_URL")
										+ "?id="
										+ objectId);
								Ca oldCa = gson.fromJson(resourceDetail, Ca.class);
								JSONObject obj1 = new JSONObject();
								JSONObject ob1= null;
								JSONArray arr = new JSONArray();
								JSONObject obNum= new JSONObject();
								if(oldCa.getRealFiles()!=null && oldCa.getRealFiles().size()>0 && fileMetadataFlag!=null && fileMetadataFlag.size()>0){
									List<com.brainsoon.semantic.ontology.model.File> tempRealFiles = oldCa.getRealFiles();
									Map<String, String> mapName = new HashMap<String, String>();
									int realFileSize = tempRealFiles.size();
									for (int j = 0; j < realFileSize; j++) {
										if (tempRealFiles.get(j).getName() != null
												&& tempRealFiles.get(j).getAliasName() != null) {
											mapName.put(tempRealFiles.get(j).getAliasName(), tempRealFiles.get(j).getName());
										}
									}
									String identifer = "";
									String fileMetadata = "";
									String newPath = "";
									for(com.brainsoon.semantic.ontology.model.File file:tempRealFiles){
										String fileDetail = http.executeGet(WebappConfigUtil
												.getParameter("CA_FILERES_DETAIL_URL") + "?id=" + file.getObjectId());
										ob1= (JSONObject) obj1.fromObject(fileDetail);
										if(fileMetadataFlag.get(ob1.get("md5"))!=null&&!fileMetadataFlag.get(ob1.get("md5")).equals("{}")||fileMetadataFlag.get(file.getObjectId())!=null){
											fileMetadata = ob1.get("fileMetadataMap").toString();
											JSONObject Metadata= (JSONObject) obj1.fromObject(ob1.get("fileMetadataMap").toString());
											identifer = Metadata.get("identifer").toString();
											Metadata.remove("identifer");
											Object  path = null;
											path = Metadata.get("path");
											if(path!=null){
												newPath = path.toString();
												ob1.put("path", newPath);
												newPath = "";
											}
											Metadata.remove("path");
//											String rootPath = ca.getRootPath();
//											rootPath = rootPath.replaceAll("\\\\", "/")+"/";
//											newPath = newPath.substring(46,newPath.length());
//											newPath = ca.getImportXpath()+newPath;
//											String arrPath[] = newPath.split("/");
//											for(int u=1;u<arrPath.length;u++){
//												if(mapName.get(arrPath[u])!=null){
//													newPath = newPath.replace(arrPath[u],mapName.get(arrPath[u]));
//												}
//											}
//											if(newPath.endsWith("/")){
//												newPath = newPath.substring(0,newPath.length()-1);
//											}
											if(!Metadata.isEmpty()){
												Iterator it = Metadata.keys();
												while (it.hasNext()) {  
									                String key = (String) it.next();  
									                String value = Metadata.getString(key);  
									                ob1.put(key, value);
												}
											}
											ob1.put("identifer", identifer);
											ob1.remove("fileMetadataMap");
											ob1.remove("isDir");
											ob1.remove("creator");
											ob1.remove("aliasName");
											ob1.remove("pid");
											ob1.remove("id");
											arr.add(ob1);
//											if(!Metadata.isEmpty()){
//												arr.add(Metadata);
//											}
										}
									}
								}
								if(arr!=null){
									map.put("realFile", arr.toString());
								}
							}
							SysOperateLogUtils.addLog("publish_create",
									MetadataSupport.getTitle(ca), userInfo);
							if(StringUtils.isNotBlank(objectId) && map!=null && map.size()>0){
								map.put("objectId", objectId);
								map.put("msg", "成功");
								map.put("status", "0");
							}else{
								count++;
								map = gson.fromJson(da.get(i).toString(), Map.class);
								map.put("msg","失败,资源创建失败.");
								map.put("status", "-1");
							}
						} catch (Exception e) {
							e.printStackTrace();
							count++;
							map = gson.fromJson(da.get(i).toString(), Map.class);
							map.put("msg","失败原因："+e.getMessage());
							map.put("status", "-1");
						}
						jsonStr.append(gson.toJson(map) + ",");
					}
				}else{
					count++;
					b = false;
					resultJsonValue.setStatus("-1");
					resultJsonValue.setMsg("失败,资源创建失败.无资源数据");
					resultJsonValue.setData("");
				}
			} catch (Exception e) {
				e.printStackTrace();
				count++;
				b = false;
				resultJsonValue.setStatus("-1");
				resultJsonValue.setMsg("失败原因："+e.getMessage());
				resultJsonValue.setData("");
			}
			if(count == num){
				resultJsonValue.setStatus("-1");
				resultJsonValue.setMsg("全部操作失败，总数【"+ num  +"】,失败数【"+ count  +"】");
			}else if(0 < count &&  count < num){
				resultJsonValue.setStatus("-1");
				resultJsonValue.setMsg("部分操作成功，总数【"+ num  +"】，成功数【"+ (num-count) +"】,失败数【" + count  + "】");
			}else{
				resultJsonValue.setStatus("0");
				resultJsonValue.setMsg("全部操作成功，总数【"+ num  +"】,成功数【"+ num +"】");
			}
			
			String json2 = gson.toJson(resultJsonValue);
			if(jsonStr.length() > 0 && b){
				String json1 = jsonStr.toString();
				outputResult("{\"data\":[" + json1.substring(0, json1.length()-1) + "]," + json2.substring(1, json2.length()));
			}else{
				outputResult(json2);
			}
		}else{ //2是异步方式  异步的话把结果返回到指定的文件里  注意线程是守护线程 需要启动程序时就启动  启动代码在【BeanFactoryUtil.java】 我已注释测试需要打开
			String sjName = System.nanoTime()+"";
			for(int i=0;i<da.size();i++){
				ImportData importData = new ImportData();
				importData.setJsonDate(da.get(i).toString());
				importData.setRepeatType(repeatType);
				DoFileUtils.mkdir(syncJsonFileName);
				importData.setSyncJsonFileName(syncJsonFileName);
				importData.setSjName(sjName);
				importData.setAllNum(da.size());
				importData.setPublishType(resType);
				CopyrightTaskQueue.getInst().addMessage(importData);
			}
				resultJsonValue.setStatus("0");
				resultJsonValue.setMsg("成功");
				resultJsonValue.setData("开始导入资源，请稍后去【"+syncJsonFileName+"】文件查看日志");
				outputResult(gson.toJson(resultJsonValue));
		}
		
	}
	//判断是否是请求的IP
	public String isRequestIp(String ip){
		String srcIp = "";
		String status = ip;
		List<SysParameter> sr = sysParameterService.findParaValue("requestInternetIpAddr");
		if (sr != null && sr.size() > 0) {
			if (sr.get(0) != null && sr.get(0).getParaValue() != null) {
				srcIp = sr.get(0).getParaValue();
			}
		}
		if(StringUtils.isNotBlank(srcIp)){
			String ipArray[] = srcIp.split(",");
			for(String arrip:ipArray){
				if(arrip.equals(ip)){
					status = "1";
					break;
				}
			}
		}
		Map<String,String> map = null;
		Gson gson = new Gson();
		if(!status.equals("1")){
			map = new HashMap<String, String>();
			map.put("status", "-1");
			map.put("msg", "Host-IP:"+ip+" is not in the request list!");
			map.put("data", "null");
			gson.toJson(map);
			String json  = gson.toJson(map);
			status = status+"="+json;
		}
		return status;
	}
	/**
	 * 资源入库接口
	 */
	@RequestMapping(value = baseUrl + "deteFileMetada") 
	public @ResponseBody void deteFileMetada(@RequestParam("objectId") String objectId,@RequestParam("fileObjectId") String fileObjectId,HttpServletRequest request){
		HttpClientUtil http = new HttpClientUtil();
		Gson gson = new Gson();
		String result = http.executeGet(WebappConfigUtil
				.getParameter("PUBLISH_DEL_NODE_URL")
				+ "?caId="
				+ objectId
				+ "&nodeId=" + fileObjectId);
		Map<String,String> map = new HashMap<String, String>();
		if(result.equals("0")){
			map.put("objectId", objectId);
			map.put("msg", "成功");
			map.put("status", "0");
		}else{
			map.put("objectId", objectId);
			map.put("msg", "失败");
			map.put("status", "1");
		}
		String json2 = gson.toJson(map);
		outputResult(json2);
	}
//	public final  static void main(String[] args){
//		System.out.println("++++++++++++"+PublishCopyrightAction.class.getClass().getResource("/").getPath()+"+++++++++++++");  
//		File file = new File(PublishCopyrightAction.class.getClass().getResource("/").getPath());
//		System.out.println(file.list().length);
//	}
	/**
	 * 接口测试
	 */
	@RequestMapping(value = baseUrl + "testPort") 
	public @ResponseBody void testPort(@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime,@RequestParam("resType") String resType,HttpServletRequest request){
		HttpClientUtil http = new HttpClientUtil();
		String hql = "startTime="+startTime+"&endTime="+endTime+"&resType="+resType;
		String result = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_SCQUERYBYTIME_URL") + "?"+hql);
		System.out.println("");
	}
}
