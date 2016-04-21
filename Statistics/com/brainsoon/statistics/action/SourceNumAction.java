package com.brainsoon.statistics.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.support.PageResultForTNum;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.semantic.ontology.model.Statistics;
import com.brainsoon.semantic.ontology.model.StatisticsList;
import com.brainsoon.statistics.service.ISourceNumService;
import com.brainsoon.system.service.IFLTXService;
import com.brainsoon.system.service.IMetaDataModelService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.util.MetadataSupport;


/**
 * 资源数量统计
 * @author 唐辉
 *
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SourceNumAction extends BaseAction {
	/** 默认命名空间 **/
	private final String baseUrl = "/statistics/sourceNum/";
	UserInfo userInfo =  LoginUserUtil.getLoginUser();
	private static Long total = 0L;
	private static  List<Statistics> allRecords = new ArrayList<Statistics>();
	@Autowired
	private ISourceNumService sourceNumService;
	@Autowired
	private IMetaDataModelService metaDataModelService;
	@Autowired
	private IFLTXService iFLTXService;
	/**
	 * 执行查询操作
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "queryType")
	@Token(save=true)
	public @ResponseBody String queryType(HttpServletRequest request,HttpServletResponse response){
		Map<String,String> map = userInfo.getResTypes();
		JSONArray array = new JSONArray();
		if(map!=null) {
			Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				   Map.Entry<String, String> entry = it.next();
				   JSONObject json = new JSONObject();
				   json.put("name",entry.getValue());
				   json.put("id", entry.getKey());
				   array.add(json);
		   }
		}/*else {
			List<MetaDataModelGroup> typeName = metaDataModelService.doTypeName(); 
            for(MetaDataModelGroup metaDataModelGroup : typeName){ 
            	 JSONObject json = new JSONObject(); 
            	 json.put("name",metaDataModelGroup.getTypeName());
				 json.put("id", metaDataModelGroup.getId()); 
				 array.add(json); 
		    }
		}*/
		return array.toString();
	}
	
	
	/**
	 * 执行查询操作
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "queryGetDefiniton")
	@Token(save=true)
	public @ResponseBody String queryGetDefiniton(HttpServletRequest request,HttpServletResponse response){
		String id = (String)request.getParameter("pid");
		JSONArray array = new JSONArray();
		int sysResMetadataTypeIdd = Integer.parseInt(id);
		List<String> listGroupId = sourceNumService.getListGroupId(sysResMetadataTypeIdd);
		if(listGroupId!=null) {
			array = MetadataSupport.getCatagoryInMetadata(listGroupId);
			
		}
		
		return array.toString();
	}
	/**
	 * 执行查询时间分类操作
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "queryTimeClass")
	@Token(save=true)
	public @ResponseBody String queryTimeClass(HttpServletRequest request,HttpServletResponse response){
		String id = (String)request.getParameter("pid");
		JSONArray array = new JSONArray();
		int sysResMetadataTypeIdd = Integer.parseInt(id);
		List<String> listGroupId = sourceNumService.getListGroupId(sysResMetadataTypeIdd);
		if(listGroupId!=null) {
			array = MetadataSupport.getCatagoryWithTime(listGroupId);
			
		}
		return array.toString();
	}
	/**
	 * 执行查询期刊操作
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "queryJournaClass")
	@Token(save=true)
	public @ResponseBody String queryJournaClass(HttpServletRequest request,HttpServletResponse response){
		String id = (String)request.getParameter("pid");
		JSONArray array = new JSONArray();
		int sysResMetadataTypeIdd = Integer.parseInt(id);
		List<String> listGroupId = sourceNumService.getListGroupId(sysResMetadataTypeIdd);
		if(listGroupId!=null) {
			array = MetadataSupport.getCatagoryWithJourna(listGroupId);
			
		}
		return array.toString();
	}
	/**
	 * 执行查询操作
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "showTree")
	@Token(save=true)
	public String showTree(HttpServletRequest request,HttpServletResponse response,ModelMap model){
		String typeId = (String)request.getParameter("typeId");
		model.addAttribute("typeId", typeId);
		return baseUrl + "showTree";
	}
	
	
	/**
	 * 执行查询操作
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "queryGetTree")
	@Token(save=true)
	public @ResponseBody String queryGetTree(HttpServletRequest request,HttpServletResponse response){
		String id = (String)request.getParameter("typeID");
		Long typeId = Long.valueOf(id);
		String treeStr = iFLTXService.getFLTXContent(typeId);
		return treeStr;
	}
	
	/**
	 * 执行查询操作
	 * @param request
	 * @param response 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@RequestMapping(baseUrl + "queryFormList")
	@Token(save=true)
	public @ResponseBody PageResultForTNum queryFormList(HttpServletRequest request,HttpServletResponse response) throws JsonParseException, JsonMappingException, IOException{
		PageResultForTNum pageResult = new PageResultForTNum();
		String id = (String)request.getParameter("sysResMetadataTypeId");
		String idStr ="";
		String allTypes ="";
		if(StringUtils.isNotBlank(id)) {
			idStr = id.substring(0, id.length()-1);
		}
		//获取资源分类
		Map<String, String> map = userInfo.getResTypes();
		String restype="";
		if (map != null) {
			Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				//获取资源分类的id
				restype+=entry.getKey()+",";
				if(restype.length()>0) {
					allTypes = restype.substring(0, restype.length()-1);
				}
			}
			
		}/*else {
			List<MetaDataModelGroup> typeName = metaDataModelService.doTypeName(); 
            for(MetaDataModelGroup metaDataModelGroup : typeName){ 
            	restype+=metaDataModelGroup.getId()+",";
				if(restype.length()>0) {
					allTypes = restype.substring(0, restype.length()-1);
				}
		    }
		}*/
		String path = (String)request.getParameter("path");
		String page = (String)request.getParameter("page");
		String size = (String)request.getParameter("rows");
		int pageSize = Integer.parseInt(size);
		int pageNo = Integer.parseInt(page);
		String fieldName = (String)request.getParameter("fieldName");
		String metadataMap ="";
		if(fieldName!=null&&path!=null) {
			metadataMap= "{\""+fieldName+"\":\""+path+"\"}";
		}
		try {
			metadataMap = URLEncoder.encode(metadataMap, "UTF-8");
		} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		}
		String startTime = (String)request.getParameter("startTime");
		String endTime = (String)request.getParameter("endTime");
		String formList = sourceNumService.queryFormList(idStr,metadataMap,startTime,endTime,page,size,allTypes);
		ObjectMapper objectMapper = new ObjectMapper();
		StatisticsList result = objectMapper.readValue(formList, StatisticsList.class);
		//List<Statistics> allRecords = new ArrayList<Statistics>();
		//表示总结果数
		total = result.getTotal();
		//表示总结果集
		allRecords = result.getRows();
		int startIndex = (pageNo - 1) * pageSize > 0 ? (pageNo - 1) * pageSize : 0;
		List<Statistics> collections = new ArrayList<Statistics>();
		for (int i = 0; i < pageSize; i++) {
			if (startIndex < total && total != 0) {
				Statistics statistics = allRecords.get(startIndex++);
				if (statistics != null) {
					Map<String,String> mapp = userInfo.getResTypes();
					if(mapp!=null) {
						String type = statistics.getType();
						String typeName = mapp.get(type);
						if(typeName!=null) {
							statistics.setTypeName(typeName);
						} else{
							statistics.setTypeName("");
						}
						collections.add(statistics);
					}
				} else {
					break;
				}
			}
		}
		
		pageResult.setStatisticsNum(result.getStatisticsNum());
		pageResult.setTotal(total);
		if(StringUtils.isBlank(allTypes)){
			pageResult.setStatisticsNum(0);
			pageResult.setTotal(0);
		}
		pageResult.setRows(collections);
		return pageResult;
	}
	
	/**
	 * 资源统计导出下载
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = baseUrl + "exportRes")
	public ResponseEntity<byte[]> exportRes(@RequestParam("ids") String ids) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String filename = URLEncoder.encode("资源统计.xls", "UTF-8");
		headers.setContentDispositionFormData("attachment", filename);
		List datas = new ArrayList();
		if (StringUtils.isNotBlank(ids)) {
			String[] idArr = StringUtils.split(ids,",");
			if(allRecords!=null&&total>=idArr.length){
				for (String id : idArr) {
					datas.add(allRecords.get(Integer.parseInt(id)));
					SysOperateLogUtils.addLog("sourceNum_exportRes",allRecords.get(Integer.parseInt(id)).getTypeName(), userInfo);
				}
			}
		} else {
			for(int i=0;i<allRecords.size();i++){
				SysOperateLogUtils.addLog("sourceNum_exportRes",allRecords.get(i).getTypeName(), userInfo);
				datas.add(allRecords.get(i));
			}
		}
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(sourceNumService.exportRes(datas)), headers, HttpStatus.OK);
	}
	
	
	/**
	 * 执行查询操作
	 * @param request
	 * @param response 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@RequestMapping(baseUrl + "queryPieList")
	@Token(save=true)
	public @ResponseBody String queryPieList(HttpServletRequest request,HttpServletResponse response) throws JsonParseException, JsonMappingException, IOException{
		String page = "0";
		String size = "10";
		String pieList = sourceNumService.queryPieList(page,size);
		ObjectMapper objectMapper = new ObjectMapper();
		StatisticsList result = objectMapper.readValue(pieList, StatisticsList.class);
		List<Statistics> pieCountList = result.getRows();
		JSONArray array = new JSONArray();
		Map<String,String> map = userInfo.getResTypes();
		if(map!=null) {
			 for (int i = 0; i <pieCountList.size(); i++) {
			    	Statistics statistics = pieCountList.get(i);
					JSONObject json = new JSONObject();
					if(map.containsKey(statistics.getType())) {
						json.put("fileType",statistics.getSource());
						json.put("count", statistics.getCount());
						json.put("type",statistics.getType());
						array.add(json);
					}
			    }
		}/*else {
			 for (int i = 0; i <pieCountList.size(); i++) {
			    	Statistics statistics = pieCountList.get(i);
					JSONObject json = new JSONObject();
					json.put("fileType",statistics.getSource());
					json.put("count", statistics.getCount());
					json.put("type",statistics.getType());
					array.add(json);
					
			    }
		}*/
	   
		return array.toString();
	}
	
}
