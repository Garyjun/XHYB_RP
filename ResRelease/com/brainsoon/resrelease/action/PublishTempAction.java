package com.brainsoon.resrelease.action;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.content.PdfUtil;
import com.brainsoon.common.util.dofile.conver.ConverUtils;
import com.brainsoon.common.util.dofile.image.ImageUtils;
import com.brainsoon.common.util.dofile.util.ColorUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.po.SubjectStore;
import com.brainsoon.resource.service.ISubjectService;
import com.brainsoon.resrelease.po.ParamsTempEntity;
import com.brainsoon.resrelease.po.ProdParamsTemplate;
import com.brainsoon.resrelease.service.IPublishTempService;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.support.SysParamsTemplateConstants;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.MetadataDefinitionGroup;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.IDictValueService;
import com.brainsoon.system.service.IInDefinitionService;
import com.brainsoon.system.service.IMetaDataModelService;
import com.brainsoon.system.service.ISysDirService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants;
import com.brainsoon.system.util.MetadataSupport;
@Controller
/**
 * 
 * @ClassName: ResOrderAction 
 * @Description:发布模板相关功能 
 * @author 谢贺伟 
 * @date 2015-7-8 下午3:32:55 
 *
 */
public class PublishTempAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/resRelease/orderPublishTemplate/";
	public final static String TEMPLATEFILE_TEMP = WebAppUtils.getWebRootBaseDir(ConstantsDef.sysUpLoadFile);
	public final static String TEMPLATEFILE_IMAGE = WebAppUtils.getWebAppRoot() + "resRelease" + File.separator + "orderPublishTemplate" + File.separator + "sl.jpg";
	public final static String TEMPLATEFILE_PDF = WebAppUtils.getWebAppRoot() + "resRelease" + File.separator + "orderPublishTemplate" + File.separator + "sl.pdf";
	public final static String TEMPLATEFILE_VIDEO = WebAppUtils.getWebAppRoot() + "resRelease" + File.separator + "orderPublishTemplate" + File.separator + "sl.mp4";
	@Autowired
	private IPublishTempService publishTempService;
	@Autowired
	private IResOrderService resOrderService;
	@Autowired
	private ISubjectService subjectService;
	@Autowired
	private IDictNameService dictNameService;
	@Autowired
	private IDictValueService dictValueService;
	@Autowired
	private IInDefinitionService inDefinitionService;
	@Autowired
	private ISysDirService sysDirService;
	@Autowired
	private IMetaDataModelService metaDataModelService;
	@RequestMapping(baseUrl + "list")
	public @ResponseBody PageResult list(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "createUser", required = false) String createUser,
			@RequestParam(value = "posttype", required = false) String posttype) throws ParseException, UnsupportedEncodingException{
		logger.info("查询资源发布模板列表");
		QueryConditionList conditionList = getQueryConditionList();
		if (name != null) {
			conditionList.addCondition(new QueryConditionItem("name", Operator.LIKE, "%"+URLDecoder.decode(name,"UTF-8")+"%"));
		}
		if (status != null) {
			conditionList.addCondition(new QueryConditionItem("status", Operator.EQUAL, status));
		}
		if (createUser != null) {
			conditionList.addCondition(new QueryConditionItem("createUser.userName", Operator.LIKE, "%"+URLDecoder.decode(createUser,"UTF-8")+"%"));
		}
		if (startDate != null) {
			conditionList.addCondition(new QueryConditionItem("creatTime", Operator.GT, DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", URLDecoder.decode(startDate,"UTF-8"))));
		}
		if (endDate != null) {
			conditionList.addCondition(new QueryConditionItem("creatTime", Operator.LT, DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", URLDecoder.decode(endDate,"UTF-8"))));
		}
		if (posttype != null ) {
			conditionList.addCondition(new QueryConditionItem("posttype", Operator.EQUAL, posttype));
		}
		/*if (publishType != null) {
			conditionList.addCondition(new QueryConditionItem("type", Operator.EQUAL, publishType));
		}*/
		
		/*UserInfo userInfo = LoginUserUtil.getLoginUser();
		if(userInfo!=null){
			Map<String, String> resMap = userInfo.getResTypes();
			if(resMap!=null){
				Set<String> set = resMap.keySet();
				Iterator<String> it = set.iterator();
				String resTypes = "";
				while(it.hasNext()){
					resTypes += "'" + it.next() + "'" + ",";
				}
				if(StringUtils.isNotEmpty(resTypes)){
					resTypes = resTypes.substring(0, resTypes.length()-1);
					conditionList.addCondition(new QueryConditionItem("type", Operator.IN, resTypes));
				}
			}
			String userIds = userInfo.getDeptUserIds();
			int isPrivate = userInfo.getIsPrivate();
			if(StringUtils.isNotEmpty(userIds)){
				conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.IN, userIds.substring(0, userIds.length()-1)));
			}else if(1==isPrivate){
				if(1==isPrivate){
					conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.IN, userIds.substring(0, userIds.length()-1)));
				}else {
					conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.EQUAL, userInfo.getUserId()));
				}
				
			}else {
				conditionList.addCondition(new QueryConditionItem("taskDetail.taskProcess.createUser.id", Operator.IN, "-2"));
			}
		}*/
		PageResult pageResult = new PageResult();
		pageResult = publishTempService.query4Page(ProdParamsTemplate.class, conditionList);
		List<ProdParamsTemplate> list=pageResult.getRows();
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		List<ProdParamsTemplate> Zlist = new ArrayList<ProdParamsTemplate>();
		int size = 0;
		//获取当前登录账户的资源类型
			Map<String, String> resMap = userInfo.getResTypes();
			Set<String> set = resMap.keySet();
			Iterator<String> it = set.iterator();
			List<String> listtype = new ArrayList<String>();
			while(it.hasNext()){
				listtype.add(it.next());
			}
			//循环模板的资源类型
			for (ProdParamsTemplate prodParamsTemplate : list) {
				String types = prodParamsTemplate.getType();
				String type[] = types.split(",");
				List<String> listrestype = new ArrayList<String>();
				for (int j = 0; j < type.length; j++) {
					listrestype.add(type[j]);
				}
				if(listtype.containsAll(listrestype)){
					size++;
					Zlist.add(prodParamsTemplate);
				}
			}
			HttpServletRequest requests = getRequest();
			int pageSize = StringUtil.obj2Int(requests.getParameter("rows"));

			int pageNo = StringUtil.obj2Int(requests.getParameter("page"));
			int startIndex = (pageNo - 1) * pageSize > 0 ? (pageNo - 1) * pageSize : 0;
			List<ProdParamsTemplate> paramsTemplates = new ArrayList<ProdParamsTemplate>();
			for (int i=startIndex; i < startIndex+pageSize; i++) {
				if(i<Zlist.size()){
					paramsTemplates.add(Zlist.get(i));
				}else{
					break;
				}
			}	
			
		PageResult pageResults = new PageResult();
		pageResults.setRows(paramsTemplates);
		pageResults.setTotal(size);
		return pageResults;
	}
	
	/**
	 * 创建/修改发布模版 将数据库数据转换为页面数据，展示在编辑页面
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "/*/edit")
	public String bookTemplateEdit(@RequestParam(value="posttype",required=false) String posttype,HttpServletRequest request,HttpServletResponse response,ModelMap model){
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		logger.info("编辑资源发布模板");
		String id = request.getParameter("id");
		ParamsTempEntity paramsTempEntity = new ParamsTempEntity();
		if(id!=null&&Long.valueOf(id)>0){
			ProdParamsTemplate prodParamsTemplate = (ProdParamsTemplate) publishTempService.getByPk(
					ProdParamsTemplate.class, Long.parseLong(id));
			paramsTempEntity = publishTempService.convertEntity(prodParamsTemplate);
			model.addAttribute("checkedResType", prodParamsTemplate.getType());
		}
		String metaDatasCode = paramsTempEntity.getMetaDatasCode();
		if(StringUtils.isNotEmpty(metaDatasCode)){
			String[] codes =  metaDatasCode.split("!");
			String metadatasDesc="";
			for (int i = 0; i < codes.length; i++) {
				String codesrestype=codes[i].toString().substring(2, codes[i].toString().indexOf("\":"));
				MetaDataModelGroup dataModelGroup=(MetaDataModelGroup) publishTempService.getByPk(MetaDataModelGroup.class, Long.decode(codesrestype));
				JSONObject jsonObject2=JSONObject.fromObject(codes[i].toString());
				String code =(String) jsonObject2.get(codesrestype);
				String[] coderes =  code.split(",");
				String metadatas="";
				for(String codess : coderes){
					MetadataDefinition define = MetadataSupport.getMetadateDefineByUri(codess);
					if(define!=null){
						String zhName = define.getFieldZhName();
						if(zhName!=null){
							metadatas += zhName + ",";
						}
					}
				}
				if(metadatas.length()>1){
					metadatas = metadatas.substring(0, metadatas.length()-1);
				}
				metadatasDesc +="【"+dataModelGroup.getTypeName()+"】\\n"+metadatas+"\\n";
			}
			
			//String resType = paramsTempEntity.getResourceType();
			/*String metadatasDesc = "";
			for(String code : codes){
				MetadataDefinition define = MetadataSupport.getMetadateDefineByUri(code);
				if(define!=null){
					String zhName = define.getFieldZhName();
					if(zhName!=null){
						metadatasDesc += zhName + ",";
					}
				}
			}*/
			/*if(metadatasDesc.length()>1){
				metadatasDesc = metadatasDesc.substring(0, metadatasDesc.length()-1);
			}*/
			model.addAttribute("metadatasDesc", metadatasDesc);
			//type元数据
		}
		model.addAttribute("templateId", id==null?"-1":id);
		int platformId = LoginUserUtil.getLoginUser().getPlatformId();
		model.addAttribute("platformId", platformId);
		/*String abc = paramsTempEntity.getMetaDatasCode().replaceAll("\"", "\\\"");
		paramsTempEntity.setMetaDatasCode(abc);*/
		
		model.addAttribute("paramsTempEntity", paramsTempEntity);
		Map<String, String> resTypeMap = userInfo.getResTypes();
		
		//该用户拥有的资源类型权限
		String result = "";
		if(MapUtils.isNotEmpty(resTypeMap)){
			Set<Entry<String, String>> entrySet = resTypeMap.entrySet();
			Iterator it = resTypeMap.keySet().iterator();
			while(it.hasNext()){
				String key = (String) it.next();
				String value = (String) resTypeMap.get(key);
				result += key + ":" + value + ",";
			}
		}
		if(result.trim().length()>1){
			result = result.substring(0, result.length()-1);
		}
		model.addAttribute("resType", result);
		model.addAttribute("posttype", posttype);
		return baseUrl + "orderTemplateEdit";
	}
	
	@RequestMapping(baseUrl + "getAppInfo")
	@ResponseBody
	public String getAppInfo(HttpServletRequest request,HttpServletResponse response){
		JSONObject result = new JSONObject();
		JSONObject supplies = new JSONObject();
		JSONObject	metaInfo = new JSONObject();
		LinkedHashMap<String,String> suppliesMap = inDefinitionService.getAppList();
		for (Map.Entry<String,String> entry : suppliesMap.entrySet()){
			supplies.put(entry.getKey(), entry.getValue());
		}
		LinkedHashMap<String,String> metaInfoMap = dictNameService.getDictMapByName("发布内容");
		for (Map.Entry<String,String> entry : metaInfoMap.entrySet()){
			metaInfo.put(entry.getKey(), entry.getValue());
		}
		result.put("supplies", supplies);
		result.put("metaInfo", metaInfo);
		return result.toString();
	}
	//查询发布途径
	@RequestMapping(baseUrl + "getposttypes")
	@ResponseBody
	public String getposttypes(HttpServletRequest request,HttpServletResponse response){
		JSONObject result = new JSONObject();
		JSONObject	posttype = new JSONObject();
		LinkedHashMap<String,String> metaInfoMap = dictNameService.getDictMapByName("发布途径");
		for (Map.Entry<String,String> entry : metaInfoMap.entrySet()){
			posttype.put(entry.getKey(), entry.getValue());
		}
		result.put("posttype", posttype);
		return result.toString();
	}
	
	
	/**
	 * 查询发布模版详情
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "detail")
	public String bookTemplateDetail(HttpServletRequest request,HttpServletResponse response,ModelMap model){
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		logger.info("查看资源发布模板");
		String id = request.getParameter("id");
		String opFrom = request.getParameter("opFrom");
		ParamsTempEntity paramsTempEntity = new ParamsTempEntity();
		ProdParamsTemplate prodParamsTemplate = (ProdParamsTemplate) publishTempService.getByPk(ProdParamsTemplate.class, Long.parseLong(id));
		
		//将模版表中的数据转换为压面数据 ppt->entity
		if(id!=null&&Long.valueOf(id)>0){
			paramsTempEntity = publishTempService.convertEntity(prodParamsTemplate);
		}
		
		//如果ParamsJson没有值，则水印信息不显示
		if (prodParamsTemplate.getParamsJson().isEmpty() || "{}".equals(prodParamsTemplate.getParamsJson())) {
			model.addAttribute("waterMarkFlag", "0");//1:显示,0:不显示
		}else {
			model.addAttribute("waterMarkFlag", "1");
		}
		model.addAttribute("paramsTempEntity", paramsTempEntity);
		model.addAttribute("image","");
		model.addAttribute("video","");
		model.addAttribute("text","");
		
		Map<String, String> resTypeMap = userInfo.getResTypes();
		String resourceTypes=paramsTempEntity.getResourceType();
		String resourceType[]=resourceTypes.split(",");
		String resTypeDesc="";
		for (int i = 0; i < resourceType.length; i++) {
			resTypeDesc += (String)resTypeMap.get(resourceType[i])+",";
		}
		resTypeDesc = resTypeDesc.substring(0, resTypeDesc.length()-1);
		model.addAttribute("resTypeDesc", resTypeDesc);
		String markType = paramsTempEntity.getWaterMarkType();
		if("图片".equals(markType)){
			model.addAttribute("markType","img");
		}else{
			model.addAttribute("markType","word");
		}
		String waterMarkFileType = paramsTempEntity.getWaterMarkFileType();
		if(StringUtils.isNotEmpty(waterMarkFileType)){
			String[] types= StringUtil.strToArray(waterMarkFileType, ",");
			for(String type:types){
				if("image".equals(type)){
					model.addAttribute("image","image");
				}else if("video".equals(type)){
					model.addAttribute("video","video");
				}else{
					model.addAttribute("text","text");
				}
			}
		}
		String metaInfo = paramsTempEntity.getMetaInfo();
		if(metaInfo.indexOf("元数据")>-1){
			model.addAttribute("metaInofFlag", 1);
			String metaDatasCode = paramsTempEntity.getMetaDatasCode();
			if(StringUtils.isNotEmpty(metaDatasCode)){
				String[] codes =  metaDatasCode.split("!");
				String metadatasDesc="";
				for (int i = 0; i < codes.length; i++) {
					String codesrestype=codes[i].toString().substring(2, codes[i].toString().indexOf("\":"));
					MetaDataModelGroup dataModelGroup=(MetaDataModelGroup) publishTempService.getByPk(MetaDataModelGroup.class, Long.decode(codesrestype));
					JSONObject jsonObject2=JSONObject.fromObject(codes[i].toString());
					String code =(String) jsonObject2.get(codesrestype);
					String[] coderes =  code.split(",");
					String metadatas="";
					for(String codess : coderes){
						MetadataDefinition define = MetadataSupport.getMetadateDefineByUri(codess);
						if(define!=null){
							String zhName = define.getFieldZhName();
							if(zhName!=null){
								metadatas += zhName + ",";
							}
						}
					}
					if(metadatas.length()>1){
						metadatas = metadatas.substring(0, metadatas.length()-1);
					}
					metadatasDesc +="【"+dataModelGroup.getTypeName()+"】\n"+metadatas+"\n";
				}
				model.addAttribute("metadatasDesc", metadatasDesc);
				//type元数据
			}
			
			/*if(StringUtils.isNotEmpty(metaDatasCode)){
				String[] codes =  metaDatasCode.split(",");
				//String resType = paramsTempEntity.getResourceType();
				String metadatasDesc = "";
				for(String code : codes){
					MetadataDefinition define = MetadataSupport.getMetadateDefineByUri(code);
					if(define!=null){
						String zhName = define.getFieldZhName();
						if(zhName!=null){
							metadatasDesc += zhName + ",";
						}
					}
				}
				if(metadatasDesc.length()>1){
					metadatasDesc = metadatasDesc.substring(0, metadatasDesc.length()-1);
				}
				model.addAttribute("metadatasDesc", metadatasDesc);
			}*/
		}
		String remark = paramsTempEntity.getRemark();
		if(StringUtils.isNotEmpty(remark)){
			model.addAttribute("remarkFlag", 1);
		}else{
			model.addAttribute("remarkFlag", 0);
		}
		
		if ("resTemp".equals(opFrom)) {
			return "/resRelease/resOrder/modelDetail";
		}
		return baseUrl + "orderTemplateDetail";
	}	
	
	@RequestMapping(baseUrl + "getSysDir")
	public @ResponseBody String getSysDir(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		int platformId = LoginUserUtil.getLoginUser().getPlatformId();
		String type = request.getParameter("publishType");
		JSONObject json = new JSONObject();
		if(platformId==1){
			List<String> dirs = sysDirService.getDirByResType("cbbook");
			String result = "";
			for(String s : dirs){
				result += s + ",";
			}
			if(!"".equals(result)&&result.length()>1){
				json.put("list", result.substring(0, result.length()-1));
			}else{
				json.put("list", result);
			}
			
		}
		model.addAttribute("flag", false);
		String id = request.getParameter("id");
		if(!StringUtils.isBlank(id)&&Long.valueOf(id)>0){
			ProdParamsTemplate prodParamsTemplate = (ProdParamsTemplate) publishTempService.getByPk(
					ProdParamsTemplate.class, Long.parseLong(id));
			ParamsTempEntity paramsTempEntity = publishTempService.convertEntity(prodParamsTemplate);
			json.put("value", paramsTempEntity.getFileType());
			json.put("checkedType", prodParamsTemplate.getType());
			model.addAttribute("flag", true);
		}
		if(platformId==2){
			 LinkedHashMap<String, String> map = OperDbUtils.queryValueByKey("publishType");
			 Set<String> keys = map.keySet();
			 String types = "";
			 Iterator it = keys.iterator();
			 while (it.hasNext()) {
				types += map.get(it.next())+",";
			 }
			 if(types.length()>1)
				 json.put("publishType", types.substring(0, types.length()-1));
			 else
				 json.put("publishType", "");
		}
		model.addAttribute("platformId", platformId);
		return json.toString();
	}
	
	@RequestMapping(baseUrl + "getResourType")
	public @ResponseBody String getResourType(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		int platformId = LoginUserUtil.getLoginUser().getPlatformId();
		model.addAttribute("platformId", platformId);
		String name = request.getParameter("name");
		String indexTag = dictValueService.getIndexTagByName(name);
		List<String> list = sysDirService.getDirByResType(indexTag);
		
		String id = request.getParameter("id");
		JSONObject json = new JSONObject();
		if(!StringUtils.isBlank(id)&&Long.valueOf(id)>0){
			ProdParamsTemplate prodParamsTemplate = (ProdParamsTemplate) publishTempService.getByPk(
					ProdParamsTemplate.class, Long.parseLong(id));
			ParamsTempEntity paramsTempEntity = publishTempService.convertEntity(prodParamsTemplate);
			json.put("value", paramsTempEntity.getFileType());
		}
		String dirs = "";
		for(String li:list){
			dirs += li+",";
		}
		if(dirs.length()>0&&!"".equals(dirs)){
			dirs = dirs.substring(0, dirs.length()-1);
		}
		json.put("list", dirs);
		return json.toString();
//		String dirs = "";
//		for(String li:list){
//			dirs += li+",";
//		}
//		if(dirs.length()>0)
//			dirs = dirs.substring(0, dirs.length()-1);
//		return dirs;
	}
	/**
	 * 发布模版保存方法 创建保存/发布保存
	 * @param request
	 * @param response
	 * @param paramsTempEntity
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "save")
	public String bookTemplateSave(HttpServletRequest request,HttpServletResponse response, 
			@ModelAttribute("paramsTempEntity") ParamsTempEntity paramsTempEntity, ModelMap model){
		publishTempService.filterParams(paramsTempEntity);
		ProdParamsTemplate prodParamsTemplate = publishTempService.entity2ProdTemplate(paramsTempEntity);
		prodParamsTemplate.setCreatTime(new Date());
		User user = (User) publishTempService.getByPk(User.class, LoginUserUtil.getLoginUser().getUserId());
		prodParamsTemplate.setCreateUser(user);
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		int platformId = userInfo.getPlatformId();
		prodParamsTemplate.setPlatformId(platformId);
		String flag = "1";
		try {
			boolean hasId = prodParamsTemplate.getId()!=null?true:false;  
			if(!hasId){
				publishTempService.create(prodParamsTemplate);
			}
			publishTempService.update(prodParamsTemplate);
			if(platformId==1){
				if(hasId){
					SysOperateLogUtils.addLog("orderTemplate_update", prodParamsTemplate.getName(), userInfo);
				}else{
					SysOperateLogUtils.addLog("orderTemplate_add", prodParamsTemplate.getName(), userInfo);
				}
			}else{
				if(hasId){
					SysOperateLogUtils.addLog("porderTemplate_update", prodParamsTemplate.getName(), userInfo);
				}else{
					SysOperateLogUtils.addLog("porderTemplate_add", prodParamsTemplate.getName(), userInfo);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return baseUrl + "orderTemplateList";
	}
	
	@RequestMapping(baseUrl + "delete")
	public @ResponseBody String delete(HttpServletRequest request,HttpServletResponse response){
		String result = "";
		String ids = request.getParameter("ids");
		String [] idArray = ids.split(",");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		int platformId = userInfo.getPlatformId();
		for(String id : idArray){
			ProdParamsTemplate ppt = (ProdParamsTemplate) publishTempService.
					getByPk(ProdParamsTemplate.class, Long.parseLong(id));
			List list = resOrderService.getResOrderByTemplateId(Long.parseLong(id));
			List<SubjectStore> list2 = subjectService.getSubjectByTemplateId(Long.parseLong(id));
			if(list!=null&&list.size()>0){
				result += "模板[" + ppt.getName() + "]正在使用中，不能删除。\n";
				continue;
			}
			if(list2 != null && list2.size()>0){
				result += "模板[" + ppt.getName() + "]正在使用中，不能删除。\n";
				continue;
			}
			publishTempService.delete(ppt);
			if(platformId==1){
				SysOperateLogUtils.addLog("orderTemplate_delete", ppt.getName(), userInfo);
			}else{
				SysOperateLogUtils.addLog("porderTemplate_delete", ppt.getName(), userInfo);
			}
		}
		if(StringUtils.isBlank(result))
			result = "ok";
		return result;
	}
	
	@RequestMapping(baseUrl + "batchDisabled")
	public @ResponseBody String doBatchDisabled(HttpServletRequest request,HttpServletResponse response){
		String result = "ok";
		String ids = request.getParameter("ids");
		String [] idArray = ids.split(",");
		try {
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			int platformId = userInfo.getPlatformId();
			for(String id : idArray){
				ProdParamsTemplate ppt = (ProdParamsTemplate) publishTempService.
						getByPk(ProdParamsTemplate.class, Long.parseLong(id));
				ppt.setStatus(SystemConstants.Status.STATUS0);
				publishTempService.update(ppt);
				if(platformId==1){
					SysOperateLogUtils.addLog("orderTemplate_disabled", ppt.getName(), userInfo);
				}else{
					SysOperateLogUtils.addLog("porderTemplate_disabled", ppt.getName(), userInfo);
				}
				
			}
		} catch (Exception e) {
			result = "批量设置状态出错";
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping(baseUrl + "batchEnabled")
	public @ResponseBody String doBatchEnabled(HttpServletRequest request,HttpServletResponse response){
		String result = "ok";
		String ids = request.getParameter("ids");
		String [] idArray = ids.split(",");
		try {
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			int platformId = userInfo.getPlatformId();
			for(String id : idArray){
				ProdParamsTemplate ppt = (ProdParamsTemplate) publishTempService.
						getByPk(ProdParamsTemplate.class, Long.parseLong(id));
				ppt.setStatus(SystemConstants.Status.STATUS1);
				publishTempService.update(ppt);
				if(platformId==1){
					SysOperateLogUtils.addLog("orderTemplate_enabled", ppt.getName(), userInfo);
				}else{
					SysOperateLogUtils.addLog("porderTemplate_enabled", ppt.getName(), userInfo);
				}
			}
		} catch (Exception e) {
			result = "批量设置状态出错";
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping(baseUrl + "uploadImage")
	public @ResponseBody String uploadImage(HttpServletRequest request){
		String result = "";
		String name = request.getParameter("name");
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		MultipartFile multipartFile = null;
		for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
			multipartFile = set.getValue();// 文件名
		}
		String fileName = multipartFile.getOriginalFilename();
		File dir = new File(TEMPLATEFILE_TEMP + "waterMark");
		if(dir.exists())
			dir.delete();
		dir.mkdir();
		File image = new File(TEMPLATEFILE_TEMP + "waterMark" + File.separator + fileName);
		try {
			multipartFile.transferTo(image);
		} catch (Exception e) {
			e.printStackTrace();
			result = "error";
		}
		//result = image.getAbsolutePath();
		result = "waterMark" + File.separator + fileName;//图片路径中存相对路径
		return result;
	}
	
	
	/**
	 * 加水印-在线预览功能（图片、视频、PDF）
	 * @param request
	 * @return
	 */
	@RequestMapping(baseUrl + "showImage")
	public @ResponseBody String showImage(HttpServletRequest request){
		String webPath = "";  //预览路径
		try {
			String wmPosition = request.getParameter("waterMarkPos");
			String wmTextSize = request.getParameter("waterMarkTextSize");
			String wmColor = request.getParameter("waterMarkColor");
			String wmValue = URLDecoder.decode(request.getParameter("waterMarkText")==null?"":request.getParameter("waterMarkText"), "UTF-8");
			String imgWaterMarkURL = request.getParameter("imgWaterMarkURL");
			if(StringUtils.isNotBlank(imgWaterMarkURL)){
				imgWaterMarkURL = URLDecoder.decode(imgWaterMarkURL,"UTF-8");
				imgWaterMarkURL = WebAppUtils.getWebRootBaseDir("sysUpLoadFile") + imgWaterMarkURL;
			}
			
			String wmFont = request.getParameter("waterMarkTextFont");
			String wmAlpha = request.getParameter("waterMarkOpacity");
			String isBold = request.getParameter("waterMarkTextBold");
			String waterType = request.getParameter("waterType");
			String waterFileType = request.getParameter("waterFileType");
			boolean wmIsBold = true;
			if(StringUtils.isNotBlank(isBold) && !isBold.equals("是")){
				wmIsBold = false;
			}
			wmAlpha = DoFileUtils.getAlpha(Integer.parseInt(wmAlpha));
			wmFont = SysParamsTemplateConstants.WmFont.getValueByKey(wmFont);
			
			//相对应用的路径&预览文件名
			String realPath = "resRelease/" +  System.nanoTime();
			//绝对路径
			String basePath = WebAppUtils.getWebRootBaseDir("fileTemp") + realPath;
			DoFileUtils.mkdir(basePath);
			//预览路径
		    webPath =  WebAppUtils.getWebRootRelDir("fileTemp") + realPath;
		    
			try {
				//图片预览
				if(waterFileType.equals("image")){
					basePath +=  ".jpg";
					webPath +=  ".jpg";
					//图片宽高处理
					String hight = request.getParameter("hight");
					String width = request.getParameter("width");
					int realHight = 400;
					int realWidth = 0;
					if(!StringUtils.isBlank(hight))
						realHight = Integer.parseInt(hight);
					if(!StringUtils.isBlank(width))
						realWidth = Integer.parseInt(width);
					
					if(waterType.equals("word")){
						ImageUtils.createNewImgByMark(
								wmValue,
								TEMPLATEFILE_IMAGE,
								basePath,
								Integer.parseInt(wmPosition),
								ImageUtils.getFontSize(Integer.parseInt(wmTextSize)),
								ColorUtil.parseToColor(wmColor),
								wmFont,
								wmIsBold,
								wmAlpha,
								realWidth,
								realHight
								);
					}else{
						ImageUtils.createNewImgByImg(
								imgWaterMarkURL, 
								TEMPLATEFILE_IMAGE, 
								basePath, 
								Integer.valueOf(wmPosition), 
								wmAlpha, 
								Integer.valueOf(realHight), 
								Integer.valueOf(realWidth));
					}
				}else if(waterFileType.equals("video")){ //视频预览
					basePath +=  ".flv";
					webPath +=  ".flv";
					if(waterType.equals("word")){
						ConverUtils.processFfmpegWatermkByFont(TEMPLATEFILE_VIDEO, basePath,
								Integer.parseInt(wmPosition), 
								wmAlpha, 
								wmValue, 
								ImageUtils.getFontSize(Integer.parseInt(wmTextSize)),
								ColorUtil.parseToColor(wmColor),
								wmIsBold, 
								wmFont, 
								"");
					}else{
						ConverUtils.processFfmpegWatermarkByImg(
								TEMPLATEFILE_VIDEO, 
								basePath, 
								imgWaterMarkURL,
								Integer.valueOf(wmPosition), 
								wmAlpha);
					}
				}else{//文本（PDF）预览
					basePath +=  ".pdf";
					webPath +=  ".pdf";
					if(waterType.equals("word")){
						PdfUtil.addPdfTxtMark(TEMPLATEFILE_PDF, basePath, wmValue);
					}else{
						PdfUtil.addPdfImgMark(TEMPLATEFILE_PDF, basePath, imgWaterMarkURL);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return webPath;
	}
	
	
	@RequestMapping(baseUrl + "getResType")
	@ResponseBody
	public String getResType(HttpServletRequest request,HttpServletResponse response){
		String id = request.getParameter("id");
		String resType = dictValueService.getIndexTagByName(((ProdParamsTemplate)publishTempService.getByPk(ProdParamsTemplate.class, Long.valueOf(id))).getType());
		return resType;
	}
	
	
	@RequestMapping(baseUrl + "checkTemplateName")
	@ResponseBody
	public String checkTemplateName(String templateName, String templateId){
		return publishTempService.checkTemplateName(templateName, templateId);
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(baseUrl + "getMetaDatasTree")
	public String getMetaDatasTree(@RequestParam(value = "resTypeId")  String resTypeId, Model model){
		JSONArray array = new JSONArray();
		//   1\  核心元数据
		List<MetadataDefinition> coreMetaDataList = MetadataSupport.getCommonMetadatas();
		
		//查询核心元数据的权限
		List<String> checkedComonMetadata = publishTempService.getCheckedCoreMetadataByres("0");
		
		resTypeId=resTypeId.substring(0,resTypeId.length()-1);
		String typeId[]=resTypeId.split(",");
		for(int i=0;i<typeId.length;i++){
			int sysResMetadataTypeIdd = Integer.parseInt(typeId[i]);
			
			JSONObject jsons = new JSONObject();
			MetaDataModelGroup dataModelGroup = (MetaDataModelGroup) publishTempService.getByPk(MetaDataModelGroup.class, Long.decode(typeId[i]));
			jsons.put("id", typeId[i]);
			jsons.put("pId", "-1");
			jsons.put("name", dataModelGroup.getTypeName());
			jsons.put("flag", "no");
			array.add(jsons);
			//核心元数据
			JSONObject coreMetadataJson = new JSONObject();
			coreMetadataJson.put("id", "core_a"+i);
			coreMetadataJson.put("pId", typeId[i]);
			coreMetadataJson.put("name", "通用元数据");
			coreMetadataJson.put("flag", "no");
			array.add(coreMetadataJson);
			if(coreMetaDataList!=null) {
				for (MetadataDefinition metadataDefinition : coreMetaDataList) {
					String uri = metadataDefinition.getUri();
					JSONObject jsonChild = new JSONObject();
					if (StringUtils.isNotEmpty(checkedComonMetadata.get(0)) && checkedComonMetadata.get(0).indexOf(metadataDefinition.getFieldName())!=-1) {
						//只有在权限列表中的数据才会被显示
						jsonChild.put("id", uri);
						jsonChild.put("pId", "core_a"+i);
						jsonChild.put("name", metadataDefinition.getFieldZhName());
						array.add(jsonChild);
					}
				}
			}
			//    2\  type元数据
			List<MetadataDefinitionGroup> listChild = metaDataModelService.doTypeChildList(sysResMetadataTypeIdd); 

			//查询type元数据的权限
			List<String> checkedTypeMetadatas = publishTempService.getCheckedCoreMetadataByres(typeId[i]);
			
			for (int j = 0;j < listChild.size(); j++) {
				JSONObject json = new JSONObject();
				json.put("id", listChild.get(j).getId());
				json.put("pId", typeId[i]);
				json.put("name", listChild.get(j).getFieldZhName());
				json.put("flag", "no");
				array.add(json);
				for (int k = 0;k < checkedTypeMetadatas.size(); k++) {
					String groupId = listChild.get(j).getId() + "";
					List<MetadataDefinition> 	list = MetadataSupport.getMetadataByGroupId(groupId);
					for (MetadataDefinition metadataDefinition : list) {
						String uri = metadataDefinition.getUri();
						JSONObject jsonChild = new JSONObject();
						if (StringUtils.isNotEmpty(checkedTypeMetadatas.get(k)) && checkedTypeMetadatas.get(k).indexOf(metadataDefinition.getFieldName())!=-1) {
							jsonChild.put("id", uri);
							jsonChild.put("pId", listChild.get(j).getId());
							jsonChild.put("name", metadataDefinition.getFieldZhName());
							array.add(jsonChild);
						}
					}
				}
			}
			
		}
		JSONObject metadataJson = new JSONObject();
		metadataJson.put("id", "-1");
		metadataJson.put("pId", "-2");
		String name = "元数据项";
		metadataJson.put("name", name);
		metadataJson.put("flag", "no");
		array.add(metadataJson);
		model.addAttribute("metadataTree", array.toString());
		model.addAttribute("typeid", resTypeId);
		return baseUrl + "metadataTree";
	}
	
	public static void main(String[] args) {
//		String regex = "slimgNew[0-9]+.jpg";
//		System.out.println("slimgNew1.jpg".matches(regex));
//		String regex1 = "^(slimgNew)\\d{14}.(jpg)$";
//		System.out.println("slimgNew20141017091921.jpg".matches(regex1));
		String s = "";
		String t = null;
		System.out.println(s + t);
	}
}
