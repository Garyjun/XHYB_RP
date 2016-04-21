package com.brainsoon.system.web.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.system.model.ResCategory;
import com.brainsoon.system.model.TreeRelationType;
import com.brainsoon.system.model.ResCategoryType;
import com.brainsoon.system.model.TreeRelationView;
import com.brainsoon.system.service.IFLTXService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.util.MetadataSupport;
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class FLTXAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/system/FLTX/";
	UserInfo userInfo =  LoginUserUtil.getLoginUser();
	@Autowired
	private IFLTXService fltxService;
	
	@RequestMapping(baseUrl + "listMenu")
	public @ResponseBody String listMenu(){
		return fltxService.getFLTXMenu();
	}
	
	@RequestMapping(baseUrl + "listContent")
	public @ResponseBody String listContent(@RequestParam String type,@RequestParam String path){
		return fltxService.getFLTXContent(Long.parseLong(type),path);
	}
	
	@RequestMapping(baseUrl + "add")
	@ResponseBody
	public Long add(HttpServletRequest request){
		String name = request.getParameter("name");
		String indexTag = request.getParameter("indexTag");
		Long status = 0L;
		try {
			ResCategoryType rct = fltxService.addFLTXMenu(name, indexTag);
			status = rct.getId();
		} catch (Exception e) {
			status = -1L;
		}
		SysOperateLogUtils.addLog("addfltx_exportRes",name, userInfo);
		return status;
	}
	
	@RequestMapping(baseUrl + "addRelationType")
	public @ResponseBody String addRelationType(HttpServletRequest request){
		String centerId = request.getParameter("centerId");
		String centerType = request.getParameter("centerType");
		String relationType = request.getParameter("relationType");
		String relationId = request.getParameter("relationId");
		String treeRelationName = request.getParameter("treeRelationName");
		String check  = fltxService.checkRelation(centerId,relationId);
		if(!check.equals("1")) {
			TreeRelationType treeRalationType = new TreeRelationType();
			treeRalationType.setCenterKey(Integer.parseInt(centerId));
			treeRalationType.setRelationKey(Integer.parseInt(relationId));
			treeRalationType.setRelationType(Integer.parseInt(relationType));
			treeRalationType.setCenterType(Integer.parseInt(centerType));
			fltxService.addRelationType(treeRalationType);
			SysOperateLogUtils.addLog("addrelation_exportRes",treeRelationName, userInfo);
			return "0";
		}
		return "1";
	}
	
	@RequestMapping(baseUrl+"queryRelationList") 
	@Token(save=true)
	public @ResponseBody PageResult queryRelationList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		PageResult result = new PageResult();
		PageResult result1 = new PageResult();
		String centerKey = request.getParameter("centerKey");
		if (StringUtils.isNotBlank(centerKey)) {
			QueryConditionList conditionList = getQueryConditionList();
			conditionList.addCondition(new QueryConditionItem("centerId", Operator.EQUAL,Integer.parseInt(centerKey)));
			result = fltxService.query4Page(TreeRelationView.class, conditionList);
			QueryConditionList conditionList1 = getQueryConditionList();
			conditionList1.addCondition(new QueryConditionItem("relativeId", Operator.EQUAL,Integer.parseInt(centerKey)));
			result1 = fltxService.query4Page(TreeRelationView.class, conditionList1);
			List<TreeRelationView> list = result.getRows();
			List<TreeRelationView> list1 = result1.getRows();
			list.addAll(list1);
			result.setRows(list);
		} 
		   
	
		return result;
	}
	
	
	@RequestMapping(baseUrl + "update")
	@ResponseBody
	public String update(@RequestParam Long id, HttpServletRequest request){
		String name = request.getParameter("name");
		String indexTag = request.getParameter("indexTag");
		ResCategoryType rct = (ResCategoryType) fltxService.getByPk(ResCategoryType.class, id);
		rct.setName(name);
		rct.setIndexTag(indexTag);
		rct.setModifiedTime(new Date());
		fltxService.update(rct);
		SysOperateLogUtils.addLog("updatefltx_exportRes",name, userInfo);
		return name;
	}
	
	@RequestMapping(baseUrl + "delete")
	@ResponseBody
	public String delete(@RequestParam String id){
		String status="0";
		ResCategoryType rct = (ResCategoryType) fltxService.getByPk(ResCategoryType.class,Long.parseLong(id));
		try {
			fltxService.delete(ResCategoryType.class, Long.parseLong(id));
		} catch (Exception e) {
			status = "-1";
		}
		SysOperateLogUtils.addLog("deletefltx_exportRes",rct.getName(), userInfo);
		return status;
	}
	
	@RequestMapping(baseUrl + "addNode")
	@ResponseBody
	public String addNode(HttpServletRequest request){
		String name = request.getParameter("name");
		String code = request.getParameter("code");
		String type = request.getParameter("type");
		String pid = request.getParameter("pid");
		String xcode = request.getParameter("xcode");
		String xpath = request.getParameter("xpath");
		if(xpath.startsWith("0,")){
			xpath = xpath.substring(2);
		}
		
		ResCategory rc = new ResCategory();
		rc.setName(name);
		if(StringUtils.isBlank(xpath)){
			if(!type.equals("6")){
				rc.setCode(code);
			}
		}else{
			if(type.equals("6")){
				rc.setCode(code.substring(0, 4));
			}else{
				rc.setCode(code);
			}
		}
		
		rc.setType(Long.parseLong(type));
		if(StringUtils.isNotBlank(pid)){
			rc.setPid(Long.parseLong(pid));
		}
		rc.setCreatedTime(new Date());
		rc.setModifiedTime(new Date());
		ResCategory newNode = (ResCategory) fltxService.create(rc);
		if(StringUtils.isBlank(xpath)){
			if(type.equals("6")){
				newNode.setCode("D"+name.substring(0, 2));
				newNode.setXcode("D"+name.substring(0, 2));
			}
			newNode.setPath(newNode.getId()+"");
		}else{
			if(type.equals("6")){
				newNode.setXcode(xcode+","+code.substring(0, 4));
			}
			newNode.setPath(xpath+","+newNode.getId());
		}
		fltxService.update(newNode);
		
		JSONObject jo = new JSONObject();
		jo.put("id", newNode.getId());
		jo.put("pid", newNode.getPid());
		jo.put("name", newNode.getName());
		jo.put("code", newNode.getCode());
		jo.put("xpath", newNode.getPath());
		if(type.equals("6")){
			jo.put("xcode", newNode.getXcode());
		}
		SysOperateLogUtils.addLog("addnode_exportRes",name, userInfo);
		return jo.toString();
	}
	
	@RequestMapping(baseUrl + "updateNode")
	@ResponseBody
	public String updateNode(@RequestParam Long id,HttpServletRequest request){
		String name = request.getParameter("name");
		String code = request.getParameter("code");
		
		ResCategory rc = (ResCategory) fltxService.getByPk(ResCategory.class, id);
		rc.setName(name);
		rc.setCode(code);
		rc.setModifiedTime(new Date());
		fltxService.update(rc);
		
		JSONObject jo = new JSONObject();
		jo.put("id", rc.getId());
		jo.put("pid", rc.getPid());
		jo.put("name", name);
		jo.put("code", code);
		SysOperateLogUtils.addLog("updatenode_exportRes",name, userInfo);
		return jo.toString();
	}
	
	@RequestMapping(baseUrl + "deleteNode")
	@ResponseBody
	public String deleteNode(@RequestParam Long id){
		String status = "0";
		ResCategory rc = (ResCategory) fltxService.getByPk(ResCategory.class, id);
		try {
			fltxService.delete(rc);
		} catch (Exception e) {
			status = "-1";
		}
		SysOperateLogUtils.addLog("deletenode_exportRes",rc.getName(), userInfo);
		return status;
	}
	
	@RequestMapping(value = baseUrl + "downloadExportExcel")
	public ResponseEntity<byte[]> downloadExportExcel(HttpServletRequest request) throws IOException{
		String typeId = request.getParameter("id");
		String typeName = request.getParameter("name");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = URLEncoder.encode(typeName+".xlsx", "UTF-8");
        headers.setContentDispositionFormData("attachment", filename);
        File result = fltxService.exportExcel(typeId,typeName);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(result),headers, HttpStatus.CREATED);
	}
	
	/**
	 * 跳转到关联树页面
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "showRelationTree")
	public String showTree(HttpServletRequest request,HttpServletResponse response,ModelMap model){
		String type = request.getParameter("type");
		String typeName = request.getParameter("typeName");
		String treeId = request.getParameter("treeId");
		String treeNodeName = request.getParameter("treeNodeName");
		model.addAttribute("type", type);
		model.addAttribute("typeName", typeName);
		model.addAttribute("treeId", treeId);
		model.addAttribute("treeNodeName", treeNodeName);
		return baseUrl + "showRelationTree";
	}
	/**
	 * 删除
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "delRelation")
	public @ResponseBody String delRelation(HttpServletRequest request,HttpServletResponse response){
		String id = request.getParameter("id");
		String centerName = request.getParameter("centerName");
		String relativeName = request.getParameter("relativeName");
		String show = centerName+":"+relativeName;
		try {
			fltxService.delRelation(Long.parseLong(id));
			SysOperateLogUtils.addLog("deleterelation_exportRes",show, userInfo);
		} catch (Exception e) {
			return "0";
		}
		return "1";
		
	}
	
	/**
	 * 查询关联树节点的名字和path
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "queryNameAndPath")
	public  @ResponseBody String queryNameAndPath(HttpServletRequest request,HttpServletResponse response){
		Map<String,List> map = new HashMap<String,List>();
		JSONArray array = new JSONArray();
		String id = request.getParameter("id");
		List<ResCategory> list = fltxService.queryNameAndPath(id);
		//List<ResCategory> list = fltxService.queryNameAndPath(1);
		for(ResCategory resCategory:list){
			String type = resCategory.getType()+"";
			List categorys = map.get(type);
			if(categorys == null){
				List<ResCategory> newCategory = new ArrayList<ResCategory>();
				newCategory.add(resCategory);
				map.put(type, newCategory);
			}else{
				categorys.add(resCategory);
			}
		}
		for(Map.Entry<String, List> entry:map.entrySet()){
			String key = entry.getKey();
			String name ="";
			String path = "";
			List<ResCategory> value = entry.getValue();
			for(ResCategory resCategory:value){
				name+=resCategory.getName()+",";
				path+= ","+resCategory.getPath()+",-";
			}
			if(StringUtils.isNotBlank(name)&&StringUtils.isNotBlank(path)) {
				String subName = name.substring(0,name.length()-1);
				String subPath = path.substring(0,path.length()-1);
				JSONObject json = new JSONObject();
				json.put("filedName", MetadataSupport.getFieldNameeByCategoryType(key));
				json.put("name", subName);
				json.put("path", subPath);
				array.add(json);
			}
		}
		return array.toString();
	}
	/**
	 * 查询关联树类型
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "queryCheckType")
	public  @ResponseBody String queryCheckType(HttpServletRequest request,HttpServletResponse response){
		String centerType =  request.getParameter("centerType");
		return fltxService.queryCheckType(centerType);
	}
	/**
	 * 获得month月份json
	 * @param type
	 * @param path
	 * @return
	 */
	@RequestMapping(baseUrl + "monthJson")
	public @ResponseBody String monthJson(@RequestParam String type,@RequestParam String yearPath,@RequestParam String year,@RequestParam String monthField){
		return fltxService.monthJson(Long.parseLong(type),yearPath,year,monthField);
	}
	/**
	 * 获得date日期json
	 * @param type
	 * @param path
	 * @return
	 */
	@RequestMapping(baseUrl + "dayJson")
	public @ResponseBody String dayJson(@RequestParam String type,@RequestParam String year,@RequestParam String month,@RequestParam String dayField){
		return fltxService.dayJson(Long.parseLong(type),year,month,dayField);
	}
	@RequestMapping(baseUrl + "getEntryMainTime")
	public @ResponseBody String getEntryMainTime(@RequestParam String type,@RequestParam String path){
		return fltxService.getEntryMainTime(Long.parseLong(type),path);
	}
	
}
