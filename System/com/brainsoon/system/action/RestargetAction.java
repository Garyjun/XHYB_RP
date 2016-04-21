package com.brainsoon.system.action;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.SearchParamCa;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.model.ResTarget;
import com.brainsoon.system.model.ResTargetData;
import com.brainsoon.system.service.IResTargetService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.google.gson.Gson;

@Controller
public class RestargetAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/target/";
	@Autowired
	private IResTargetService resTargetService;
	@Autowired
	private JdbcTemplate jdbcTemplate ;
	private final static String PUBLISH_AUTOCOMPLATE_URL = WebappConfigUtil.getParameter("PUBLISH_AUTOCOMPLATE_URL");
	
	/**
	 * 标签查询
	 * 
	 * @param request
	 * @param response
	 * @param params
	 * 
	 */
	@RequestMapping(value = baseUrl+ "getAllTargets")
	public @ResponseBody List<ResTarget> getAllTargets(HttpServletRequest request, HttpServletResponse response,Model model) {
		logger.info("进入查询方法");
		List<ResTarget> useResTargets = new LinkedList<ResTarget>();
		String libType =  request.getParameter("libType");
		model.addAttribute("libType", libType);
		useResTargets = resTargetService.getAllTargets(libType);
		return useResTargets;
	}
	/**
	 * 标签查询
	 * 
	 * @param request
	 * @param response
	 * @param params
	 * 
	 */
	@RequestMapping(value = baseUrl+ "getAllMainTargets")
	public @ResponseBody String getAllMainTargets(HttpServletRequest request, HttpServletResponse response,Model model) {
		logger.info("进入查询方法");
		List<ResTarget> useResTargets = new LinkedList<ResTarget>();
		String module =  request.getParameter("module");
		String targetField = request.getParameter("targetField");
		model.addAttribute("module", module);
		String rtn = resTargetService.getAllMainTargets(module,targetField);
		return rtn;
	}
	/**
	 * 根据标签名查询标签
	 * 
	 * @param request
	 * @param response
	 * @param params
	 * 
	 */
	@RequestMapping(value = baseUrl+ "getTargetsForName")
	public @ResponseBody String getTargetsForName(HttpServletRequest request, HttpServletResponse response,Model model) {
		logger.info("进入查询方法");
		String targetName =  request.getParameter("targetName");
		String publishType =  request.getParameter("publishType");
		String flag =  request.getParameter("flag");
		 try {
			targetName=  URLDecoder.decode(targetName,"UTF-8");
			String hql = "";
			hql = "SELECT * FROM sys_complete where name='"+targetName+"' and publishType="+publishType+" and flag='"+flag+"'";
			List<Map<String, Object>> listMap = jdbcTemplate.queryForList(hql);
			if(listMap.size()>0){
				hql = "delete FROM sys_complete where name='"+targetName+"' and publishType="+publishType+" and flag='"+flag+"'";
				jdbcTemplate.execute(hql);
			}
			hql = "insert into sys_complete set name='"+targetName+"', publishType="+publishType +", flag='"+flag+"'";
			jdbcTemplate.execute(hql);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("module", targetName);
		String rtn =  resTargetService.getTargetsForName(targetName,publishType,flag);
		return rtn;
	}
	/**
	 * 检查重复
	 * @param request
	 * @param response
	 * @return Map<String,Object>
	 */
	@RequestMapping(baseUrl + "validateTargetName")
	public @ResponseBody String checkRepeat(HttpServletRequest request, HttpServletResponse response){
		String name = request.getParameter("fieldValue");
		String module = request.getParameter("module");
		 try {
			 name=  URLDecoder.decode(name,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		List<ResTargetData> rs = resTargetService.checkRepeat(name,module);
		if(rs != null && rs.size() > 0){
			return "{\"jsonValidateReturn\": [\"targetName\",false]}";
		}else{
			return "{\"jsonValidateReturn\": [\"targetName\",true]}";
		}
	}
	/**
	 * 公共标签资源查询
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "gotoTargetList")
	public String gotoTargetList(HttpServletRequest request, HttpServletResponse response,@RequestParam("targetNames") String targetNames,@RequestParam("publishType") String publishType,Model model,@RequestParam("isTarget") String isTarget) {
		logger.info("进入查询方法");
	//	QueryConditionList conditionList = getQueryConditionList();
		String offline = request.getParameter("offline");
		String status = request.getParameter("status");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		if(StringUtils.isNotBlank(status)){
			model.addAttribute("status",status);
		}
		model.addAttribute("targetNames",targetNames);
		model.addAttribute("publishType",publishType);
		if(!isTarget.equals("")&&isTarget!=null){
		model.addAttribute("isTarget",isTarget);
		}
		//SysOperateLogUtils.addLog("target_public", userInfo.getUsername(),userInfo);
		if(StringUtils.isNotBlank(offline)){
			return "publishRes/offlinePubList";
		}else{
			return "publishRes/publishResList";
			}
	}
	/**
	 * 保存标签
	 * @param request
	 * @param response
	 * @param model
	 * @param ca
	 * @param uploadFile
	 */
	@RequestMapping(baseUrl + "saveTarget")
	public void saveTarget(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		logger.debug("******run at saveRes***********");
		String selectResId = request.getParameter("selectResId");
		String canSelectTargetIds = request.getParameter("canSelectTargetIds");
		String hasSelectTargetIds = request.getParameter("hasSelectTargetIds");
		String resIds = request.getParameter("resIds");
		String libType = request.getParameter("libType");
		String module = request.getParameter("module");
		String type = request.getParameter("type");
		resTargetService.doBatchTeaTarget(selectResId, canSelectTargetIds, hasSelectTargetIds,resIds,libType,module,type);
		//System.out.println(b);
		
	}

	/**
	 * 弹出窗标签检索
	 * @param request
	 * @param response
	 * @return Map<String,Object>
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(baseUrl + "selectTarget")
	public @ResponseBody Map<String,Object> selectTarget(HttpServletRequest request, HttpServletResponse response,@RequestParam("resId") String resId,@RequestParam("libType") String libType){
		String module = request.getParameter("module");
		String type = request.getParameter("type");
		Map<String,Object> targetWin = new HashMap<String, Object>();
		targetWin=resTargetService.bachSelectTarget(libType, resId, module, type);
		//String s=resId.substring(9);
		return targetWin;
        
	}
	/**
	 * 原始标准标签资源查询
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "gotoBresTargetList")
	public String gotoBresTargetList(HttpServletRequest request, HttpServletResponse response,@RequestParam("resTargetId") String resTargetId,Model model,@RequestParam("isTarget") String isTarget) {
		logger.info("进入查询方法");
	//	QueryConditionList conditionList = getQueryConditionList();
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String libType = request.getParameter("libType");
		String module = request.getParameter("module");
		String type = request.getParameter("type");
		model.addAttribute("resTargetId",resTargetId);
		model.addAttribute("libType",libType);
		model.addAttribute("type",type);
		model.addAttribute("module",module);
		if(!isTarget.equals("")&&isTarget!=null){
		model.addAttribute("isTarget",isTarget);
		}
		SysOperateLogUtils.addLog("target_res", userInfo.getUsername(),
				userInfo);
		if(type.equals("T06")){
		return "bookRes/bookResList";
		}else{
		return "bres/list";
		}
//		outputResult(resTargetService.query(resTargetId,libType,request,conditionList));
		
	}
	/**
	 * 左侧原始main标签信息查询
	 * @param request
	 * @param response
	 * @return
	 */
	 @RequestMapping(value="/target/queryTargetRes")
	 @ResponseBody
	 public String  queryTargetRes(HttpServletRequest request, HttpServletResponse response) {
		 QueryConditionList conditionList = getQueryConditionList();
		 //所选标签id
		 String resTargetId = request.getParameter("resTargetId");
		 String libType = request.getParameter("libType");
		 String type = request.getParameter("type");
		 String module = request.getParameter("module");
		 String result = resTargetService.bresQuery(resTargetId,libType,request,conditionList,module,type);
		 return result;
	}
	/**
	 * 聚合标签资源查询
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "seleCollectTarget")
	public String seleCollectTarget(HttpServletRequest request, HttpServletResponse response,@RequestParam("resTargetId") String resTargetId,@RequestParam("libType") String libType,Model model,@RequestParam("isTarget") String isTarget) {
		logger.info("进入查询方法");
		libType = request.getParameter("libType");
		libType = "3";
		String module = request.getParameter("module");
		String type = request.getParameter("type");
		model.addAttribute("resTargetId",resTargetId);
		model.addAttribute("libType",libType);
		model.addAttribute("type",type);
		model.addAttribute("module",module);
		if(!isTarget.equals("")&&isTarget!=null){
		model.addAttribute("isTarget",isTarget);
		}
		return "collectRes/collectResList";
//		outputResult(resTargetService.query(resTargetId,libType,request,conditionList));
		
	}
	/**
	 * 列表查询页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(baseUrl + "list")
	public @ResponseBody PageResult list(HttpServletRequest request,HttpServletResponse response){
		logger.info("查询标签列表");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		QueryConditionList conditionList = getQueryConditionList();
		List<QueryConditionItem> items = conditionList.getConditionItems();
		String filedValue = "";
		for (int i = 0; i < items.size(); i++) {
			QueryConditionItem queryConditionItem = items.get(i);
			String filedName = queryConditionItem.getFieldName();
			if(filedName.equals("targetType")){
				filedValue = queryConditionItem.getValue().toString();
				try {
					filedValue=  URLDecoder.decode(filedValue,"UTF-8");
					items.get(i).setFieldName("targetType");
					items.get(i).setValue(filedValue);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return resTargetService.query4Page(ResTargetData.class, conditionList);
	}
	/**
	 * 修改添加页
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "upd")
	public String upd(HttpServletRequest request,HttpServletResponse response,ModelMap model,@RequestParam("typeTarget") Long typeTarget){
		logger.info("进入修改/添加标签页面");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String id = request.getParameter("id");
		ResTargetData target = null;
		int statas = 0;
		if(StringUtils.isNotBlank(id)){
			target = (ResTargetData) resTargetService.getByPk(ResTargetData.class, Long.parseLong(id));
			if(StringUtils.isNotBlank(target.getModule())){
				model.addAttribute("module", target.getModule());
				model.addAttribute("statas", target.getStatus());
			}
		}
		
		if(target==null){
			target = new ResTargetData();
			statas = 1;
			model.addAttribute("statas", statas);
		}
		model.addAttribute("frmWord", target);
		model.addAttribute("flagSelect", target.getTargetType());
		if(StringUtils.isNotBlank(id)){
			return baseUrl + "targetUpd";
		}else{
			return baseUrl + "targetEdit";
		}
	}
	/**
	 * 标签查询
	 * 
	 * @param request
	 * @param response
	 * @param params
	 * 
	 */
	@RequestMapping(value = baseUrl + "gotoTarget")
	public String gotoTarget(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.info("进入查询方法");
		List<ResTarget> useResTargets = new LinkedList<ResTarget>();
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		useResTargets = resTargetService.selectTarget();
		model.addAttribute("resTarget",useResTargets);
		return "/target/targetMain";
	}
	/**
	 * 标签保存
	 * @param request
	 * @param response
	 * @param target
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "updtarget")
	@Token(save=true)
	public String updtarget(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmWord") ResTargetData target,ModelMap model){
		logger.info("进入保存标签方法");
		resTargetService.createTarget(request,response,target);
		return "/target/targetMain";
	}
	/**
	 * 标签详细
	 * @param id
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "detail")
	public String detail(@RequestParam("id") Long id,HttpServletResponse response,ModelMap model){
		logger.info("查看标签");
		UserInfo userInfo = LoginUserUtil.getLoginUser();	
		ResTargetData target = (ResTargetData) resTargetService.getByPk(ResTargetData.class, id);
		if(StringUtils.isNotBlank(target.getModule())){
			model.addAttribute("module", target.getModule());
		}
		model.addAttribute("frmWord", target);
		SysOperateLogUtils.addLog("target_detail", userInfo.getUsername(),
				userInfo);
		return baseUrl + "targetDetail";
	}
	/**
	 * 标签删除
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(baseUrl + "delete")
	public String delete(HttpServletRequest request,HttpServletResponse response){
		logger.info("删除标签");
		String ids = request.getParameter("ids");
		resTargetService.deleteAll(ResTargetData.class, ids);
		return baseUrl + "targetList";
	}
	
	/**
	 * 下载模板
	 * @return
	 * @throws IOException
	 */
    @RequestMapping(value = baseUrl + "downloadTemplete")
    public ResponseEntity<byte[]> download() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = URLEncoder.encode("敏感词模板.txt", "UTF-8");
        headers.setContentDispositionFormData("attachment", filename);
        File excel = new File(WebAppUtils.getWebAppRoot() + "system/word/word.txt");
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(excel),headers, HttpStatus.CREATED);
    }
    /**
	 * 删除标签
	 * @return
	 * @throws IOException
	 */
    @RequestMapping(value = baseUrl + "deleteTarget")
    public @ResponseBody  Map<String, Object> deleteTarget(HttpServletRequest request,HttpServletResponse response){
    	String id = request.getParameter("ids");
    	String targetField = request.getParameter("targetField");
    	String targetName = request.getParameter("targetName");
    	String publishType = request.getParameter("publishType");
    	Map<String, Object> reu = new HashMap<String, Object>();
    	if(StringUtils.isNotBlank(targetName)){
    		try {
				targetName=URLDecoder.decode(targetName,"utf8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	reu = resTargetService.deleteYes(id,targetField,targetName,publishType);
    	return reu;
    }
    /**
	 * 自动完成
	 * @return
	 * @throws IOException
	 */
    @RequestMapping(value = baseUrl + "autoCom")
    public @ResponseBody  String autoCom(HttpServletRequest request,HttpServletResponse response){
    	JSONArray ob1 = new JSONArray();
    	JSONObject ob2 = new JSONObject();
    	String fileField = request.getParameter("fileField");
    	String fileFieldValue = request.getParameter("fileFieldValue");
    	String publishType = request.getParameter("publishType");
    	logger.info("----------------------------获得fileField、fileFieldValue、publishType值"+fileField+fileFieldValue+publishType+"---------------------------------------------");
    	HttpClientUtil http = new HttpClientUtil();
    	StringBuffer hql = new StringBuffer();
    	 try {
    		 fileFieldValue=  URLDecoder.decode(fileFieldValue,"UTF-8");
    		 String metadataMap = "{";
    		 metadataMap += "\"" + fileField + "\":\""
						+ fileFieldValue + "\"}";
    		 String metadataMapUTF8 = "";
    		 try {
					metadataMapUTF8 = URLEncoder.encode(metadataMap, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
    		 hql.append("publishType=").append(publishType);
    		 hql.append("&metadataMap=").append(metadataMapUTF8);
    		 logger.info("----------------------------打印hql"+hql+"---------------------------------------------");
 			String result = http.executeGet(PUBLISH_AUTOCOMPLATE_URL + "?" + hql.toString());
 			String jsonArray[] = result.split(",");
 			if(StringUtils.isNotBlank(jsonArray[0])){
 			for(int i=0;i<jsonArray.length;i++){
 				JSONObject ob = new JSONObject();
 				ob.put("title", jsonArray[i]);
 				ob1.add(ob);
 			}
// 			hql = "SELECT * FROM sys_complete where name like'%"+targetName+"%' and publishType="+publishType+" and flag='"+flag+"'";
// 			List<Map<String, Object>> listMap = jdbcTemplate.queryForList(hql);
// 			if(listMap.size()>0){
// 			for(int i=0;i<listMap.size();i++){
// 				if(listMap.get(0).get("name")!=null){
// 					JSONObject ob = new JSONObject();
// 					ob.put("title", listMap.get(i).get("name"));
// 					ob1.add(ob);
// 				}	
// 			}
// 			}
 			ob2.put("data", ob1.toString());
 			}
 		} catch (UnsupportedEncodingException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
    	 logger.info("----------------------------打印ob2"+ob2.toString()+"---------------------------------------------");
    	return ob2.toString();
    }

	/** 批量标签资源查询
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "bachTarget", method = {RequestMethod.POST }) 
	public String bachTarget(HttpServletRequest request, HttpServletResponse response,@RequestParam("batchTarget") String batchTarget,ModelMap model) {
		logger.info("进入查询方法");
		Gson gson = new Gson();
		SearchParamCa spc = gson.fromJson(batchTarget, SearchParamCa.class);
		String ids = "";
		String status = "";
		String publishType = "";
		String targetField = "";
		if(spc!=null){
			ids= spc.getIds();
			publishType = spc.getPublishType();
			targetField = spc.getTargetField();
		}
		
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		model.addAttribute("ids",ids);
		model.addAttribute("publishType",publishType);
		model.addAttribute("targetField",targetField);
		
		return "/system/target/bachTarget";
	}
	/**
	 * main页面标签ztree查询
	 * @return
	 */
	@RequestMapping(baseUrl + "mainTargetSearch")
	@ResponseBody
	public String mainTargetSearch(HttpServletRequest request){
		String publishType = request.getParameter("publishType");
		String targetName = request.getParameter("targetName");
		String targetType = request.getParameter("targetType");
		 try {
			 if(StringUtils.isNotBlank(targetName)){
				 targetName=  URLDecoder.decode(targetName,"UTF-8");
			 }
			 if(StringUtils.isNotBlank(targetType)){
				 targetType=  URLDecoder.decode(targetType,"UTF-8");
			 }
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		return resTargetService.queryTargetJson(publishType,targetName,targetType);
	}
	/**
	 * main页面标签ztree查询
	 * @return
	 */
	@RequestMapping(baseUrl + "checkTarget")
	@ResponseBody
	public String checkTarget(HttpServletRequest request){
		String publishType = request.getParameter("publishType");
		String targetName = request.getParameter("targetName");
		if(StringUtils.isNotBlank(targetName)){
			try {
				targetName = URLDecoder.decode(targetName,"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String status = "0";
		List<String> targetNameList = new LinkedList<String>();
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String hql = "select targetName from ResTargetData  where targetName='"
				+ targetName+ "' and module='"+publishType+"' and userId="+userInfo.getUserId()+" and pid not in('-1')";
		targetNameList = resTargetService.query(hql);
		if (targetNameList!=null && !targetNameList.isEmpty()){
			status = "1";
			return status;
		}else{
			return status;
		}
	}
	/**
	 * 检查数据字典重复
	 * @param request
	 * @param response
	 * @return Map<String,Object>
	 */
	@RequestMapping(baseUrl + "validateDictValue")
	public @ResponseBody String validateDictValue(HttpServletRequest request, HttpServletResponse response){
		String name = request.getParameter("fieldValue");
		String dictNameId =  request.getParameter("dictNameId");
		String status = "0";
		 try {
			 name=  URLDecoder.decode(name,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		String hql = "from DictValue where pid="+dictNameId;
		List<DictValue> dict = resTargetService.query(hql);
		if(dict!=null && !dict.isEmpty()){
			for(DictValue dic:dict){
				if(dic.getName().equals(name)){
					status = "1";
				}
			}
		}
		if(status.equals("0")) {
			return "{\"jsonValidateReturn\": [\"name\",true]}";
		} else {
			return "{\"jsonValidateReturn\": [\"name\",false]}";
		}
//		return "{\"jsonValidateReturn\": [\"targetName\",true]}";
	}
	/**
	 * 检查数据字典重复
	 * @param request
	 * @param response
	 * @return Map<String,Object>
	 */
	@RequestMapping(baseUrl + "validateDictValueOne")
	public @ResponseBody String validateDictValueOne(HttpServletRequest request, HttpServletResponse response){
		String name = request.getParameter("fieldValue");
		String dictNameId =  request.getParameter("dictNameId");
		String status = "0";
		 try {
			 name=  URLDecoder.decode(name,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		String hql = "from DictValue where pid="+dictNameId;
		List<DictValue> dict = resTargetService.query(hql);
		if(dict!=null && !dict.isEmpty()){
			for(DictValue dic:dict){
				if(dic.getIndexTag().equals(name)){
					status = "1";
				}
			}
		}
		if(status.equals("0")) {
			return "{\"jsonValidateReturn\": [\"indexTag\",true]}";
		} else {
			return "{\"jsonValidateReturn\": [\"indexTag\",false]}";
		}
//		return "{\"jsonValidateReturn\": [\"targetName\",true]}";
	}
	/**
	 * 检查重复
	 * @param request
	 * @param response
	 * @return Map<String,Object>
	 */
	@RequestMapping(baseUrl + "validateTargetNameEdit")
	public @ResponseBody String checkRepeatEdit(HttpServletRequest request, HttpServletResponse response){
		String name = request.getParameter("fieldValue");
		String module = request.getParameter("module");
		String targetId =  request.getParameter("targetId");
		String id = "";
		 try {
			 name=  URLDecoder.decode(name,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		List<ResTargetData> rs = resTargetService.checkRepeat(name,module);
		for(int i=0;i<rs.size();i++){
			id = rs.get(i).getId()+"";
		}
		if(rs.size()==1){
			if(targetId.equals(id)) {
				return "{\"jsonValidateReturn\": [\"targetName\",true]}";
			} else {
				return "{\"jsonValidateReturn\": [\"targetName\",false]}";
			}
		}
		return "{\"jsonValidateReturn\": [\"targetName\",true]}";
	}
}
