package com.brainsoon.resource.action;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.jbpm.service.IJbpmExcutionService;
import com.brainsoon.resource.service.IAreaService;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.IBatchImportResService;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.support.ExcelUtil;
import com.brainsoon.resource.support.ResourceTypeUtils;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.resource.util.MD5Util;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.Organization;
import com.brainsoon.semantic.ontology.model.OrganizationItem;
import com.brainsoon.semantic.ontology.model.SearchParamCa;
import com.brainsoon.statistics.service.IEffectNumService;
import com.brainsoon.system.model.ResTarget;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.IBookService;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants.OperatType;
import com.brainsoon.system.support.SystemConstants.ResourceMoudle;
import com.google.gson.Gson;

/**
 * 基础资源
 * 
 * @author zuo
 */
@Controller
public class BresAction extends BaseAction {

	/** 默认命名空间 **/
	private final String baseUrl = "/bres/";

	public  static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
	public final static String FILE_TEMP = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp);
	public  static final String FILE_DOWN = StringUtils.replace(WebAppUtils.getWebAppBaseFileDown(),"\\", "/");
	/** 注入业务类 **/
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	private IBookService bookService;
	
	private List<ResTarget> useResTargets = new LinkedList<ResTarget>();
	@Autowired
	private IJbpmExcutionService jbpmExcutionService;
	@Autowired
	private ISysOperateService sysOperateService;
	@Autowired
	private IEffectNumService iEffectNumService;
	@Autowired
	private IBatchImportResService batchImportResService;
	@Autowired
	private ISysParameterService sysParameterService;
	@Autowired
	private IResOrderService resOrderService;
	/**
	 * 图片提取封面更更新数据库
	 * 
	 * @param request
	 * @param response
	 * @param params
	 * @return json
	 */
	@RequestMapping(value = baseUrl + "doFetchCover")
	public String doFetchCover() {
//		Vector<InputStream> v = new Vector<InputStream>();
//		Enumeration<InputStream> e = v.elements();
//        //将Enumeration对象中的流合并（创建一个序列流，用于合并多个字节流文件s1,s2,s3）
//        SequenceInputStream se = new SequenceInputStream(e);
//		baseSemanticSerivce.getImgFiles();
		resourceService.doResFile();
		return baseUrl+"list";
	}
	/**
	 * 列表查询
	 * 
	 * @param request
	 * @param response
	 * @param params
	 * @return json
	 */
	@RequestMapping(value = baseUrl + "query")
	public void list(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		logger.info("进入查询方法");
		HttpSession session = getSession();
		session.removeAttribute("goBack");
		QueryConditionList conditionList = getQueryConditionList();
		outputResult(baseSemanticSerivce.queryResource4Page(request, conditionList));
	}
	/**
	 * 列表查询
	 * 
	 * @param request
	 * @param response
	 * @param params
	 * @return json
	 */
	@RequestMapping(value = baseUrl + "list")
	public String query(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		logger.info("进入查询方法");
		HttpSession session = getSession();
		List<SysParameter> enforceDelete = sysParameterService.findParaValue("enforceDelete");
		if(enforceDelete!=null && enforceDelete.size()>0&&enforceDelete.get(0).getParaStatus().toString().equals("1")){
			session.setAttribute("enforceDelete", enforceDelete.get(0).getParaValue());
		}else{
			session.removeAttribute("enforceDelete");
		}
		return baseUrl+"list";
	}
	/**
	 * paper试卷阅读
	 * @param response
	 * @param type
	 * @param ext
	 * @param objectId
	 * @throws UnsupportedEncodingException 
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value = baseUrl + "papersRead")
	public @ResponseBody  Map<String, Object> papersRead(HttpServletRequest request,HttpServletResponse response,@RequestParam("type") String type) throws UnsupportedEncodingException{
		Map<String, Object> rtn = new HashMap<String, Object>();
		String objectId = request.getParameter("objectId");
		String json = request.getParameter("json");
		String ids[] = objectId.split(",");
		String content_json= "";
		JSONArray jsonArray1 = new JSONArray();
		if(!json.equals("")&&json!=null){
			json = java.net.URLDecoder.decode(json,"UTF-8").toString();
			JSONObject jb=new JSONObject();
			JSONObject obj= (JSONObject) jb.fromObject(json); 
			JSONArray  parseSize= (JSONArray) obj.get("parse");
			JSONObject jsonObj = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			if(parseSize!=null){
			for(int i=0;i<parseSize.size();i++){
				JSONObject parseObject = parseSize.getJSONObject(i);
				if(parseObject.toString()!="null"){
				String name = (String) parseObject.get("name");
				jsonObj.put("name", name);
				jsonArray.clear();
				JSONArray  arrayIdSize= (JSONArray) parseObject.get("data");
				for(int j = 0;j<arrayIdSize.size();j++){
					String id = arrayIdSize.get(j).toString();
					id = id.replace("[\"", "");
					id = id.replace("\"]", "");
					String resourceJson = baseSemanticSerivce.getResourceDetailById(id);
					Gson gson = new Gson();
					if(!resourceJson.equals("")||resourceJson!=null){
						Asset asset = gson.fromJson(resourceJson, Asset.class);
//						content_json =asset.getExtendMetaData().getExtendMetaDatas().get("content_json"); 
						jsonArray.add(content_json);
						jsonObj.put("data", jsonArray);
					}
				}
				jsonArray1.add(jsonObj);
			}
			jsonArray1.toString();
		}
			}
		}else{
			for(int i=0;i<ids.length;i++){
				String resourceJson = baseSemanticSerivce.getResourceDetailById(ids[i]);
				Gson gson = new Gson();
				if(!resourceJson.equals("")||resourceJson!=null){
				Asset asset = gson.fromJson(resourceJson, Asset.class);
//				content_json+=asset.getExtendMetaData().getExtendMetaDatas().get("content_json")+"+"; 
			}
		//	resourceJson = resourceJson.replaceAll("\\", "");
			//转换成对象
			}
			content_json = content_json.substring(0, content_json.length()-1);
		}
		if(json.equals("") ){
			rtn.put("resourceJson", content_json);
		}else{
			rtn.put("allPaper", jsonArray1);
		}
		return rtn;
	}
	/**
	 * 跳转到编辑页面
	 * @param asset
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"edit/{libType}") 
	public String edit(HttpServletRequest request,@ModelAttribute("frmAsset") Asset asset,@RequestParam("objectId") String objectId,@RequestParam("module") String module,@RequestParam("type") String type,@PathVariable("libType") String libType,ModelMap model) throws Exception{
		//查询省
		String goBackTask = request.getParameter("goBackTask");
		model.addAttribute("goBackTask", goBackTask);
		String target = request.getParameter("target");
		String operateFrom = request.getParameter("operateFrom");
		model.addAttribute("operateFrom", operateFrom);
		model.addAttribute("target", target);
		model.addAttribute("provinces", areaService.getProvince());
		model.addAttribute("objectId", objectId);
		model.addAttribute("libType", libType);
		model.addAttribute("module", module);
		model.addAttribute("type", type);
		if(StringUtils.isNotBlank(objectId)){
			String resourceJson = baseSemanticSerivce.getResourceDetailById(objectId);
			logger.info(resourceJson);
			Gson gson = new Gson();
			asset = gson.fromJson(resourceJson, Asset.class);
			if(asset != null && asset.getCommonMetaData() !=null){
				resourceJson = resourceJson.replaceAll("\"", "\\\\\"");
				logger.info("0000000000============="+resourceJson);
				model.addAttribute("resourceDetail", resourceJson);
				model.addAttribute("status",asset.getCommonMetaData().getStatus());
				List<Task> tasks = jbpmExcutionService.getCurrentTasks(WorkFlowUtils.getExecuId(objectId, libType));
				if (tasks != null && tasks.size() > 0) {
					model.addAttribute("wfTaskId",tasks.get(0).getId());
				}
			}else{
				return "/error/errorUnRes";
			}
		}
			return baseUrl + "edit";
		
	}
	/**
	 * 转换
	 * @param response
	 * @param objectId
	 */
	@RequestMapping(baseUrl+"toChange")
	public void toChange(HttpServletRequest request,HttpServletResponse response,@RequestParam("objectId") String objectId){
		
		
	}
	/**
	 * 跳转到编辑页面
	 * @param asset
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"edit") 
	public String editNotLibType(@ModelAttribute("frmAsset") Asset asset,@RequestParam("objectId") String objectId,@RequestParam("module") String module,@RequestParam("type") String type,ModelMap model) throws Exception{
		//查询省
		model.addAttribute("provinces", areaService.getProvince());
		model.addAttribute("objectId", objectId);
		model.addAttribute("module", module);
		model.addAttribute("type", type);
		if(StringUtils.isNotBlank(objectId)){
			String resourceJson = baseSemanticSerivce.getResourceDetailById(objectId);
			Gson gson = new Gson();
			asset = gson.fromJson(resourceJson, Asset.class);
			if(asset != null && asset.getCommonMetaData() !=null){
				resourceJson = resourceJson.replaceAll("\"", "\\\\\"");
				model.addAttribute("resourceDetail", resourceJson);
				model.addAttribute("status",asset.getCommonMetaData().getStatus());
//				List<Task> tasks = jbpmExcutionService.getCurrentTasks(WorkFlowUtils.getExecuId(objectId, libType));
//				if (tasks != null && tasks.size() > 0) {
//					model.addAttribute("wfTaskId",tasks.get(0).getId());
//				}
			}else{
				return "/error/errorUnRes";
			}
		}
		return baseUrl + "edit";
	}
	/**
	 * 详细
	 * @param asset
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"detail") 
	public String detail(@RequestParam("objectId") String objectId,ModelMap model) throws Exception{
		//查询省
		System.out.println(objectId+"111111");
		model.addAttribute("provinces", areaService.getProvince());
		model.addAttribute("objectId", objectId);
		if(StringUtils.isNotBlank(objectId)){
			Asset res = baseSemanticSerivce.getResourceById(objectId);
			if(null != res){
				model.addAttribute("module", res.getCommonMetaData().getModule());
				model.addAttribute("type", res.getCommonMetaData().getType());
				String libType = res.getCommonMetaData().getLibType();
				model.addAttribute("libType", libType);
				Gson gson = new Gson();
				String returnValue = gson.toJson(res); 
				logger.info("ppppppppppp========="+returnValue);
				returnValue = returnValue.replaceAll("\"", "\\\\\"");
				model.addAttribute("resourceDetail", returnValue);
				model.addAttribute("execuId",WorkFlowUtils.getExecuId(objectId, libType));
			}else{
				return "/error/errorUnRes";
			}
		}
		return baseUrl + "detail";
	}
	/**
	 * 弹出窗口详细
	 * @param asset
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"openDetail") 
	public String openDetail(@RequestParam("objectId") String objectId,ModelMap model) throws Exception{
		//查询省
		model.addAttribute("provinces", areaService.getProvince());
		model.addAttribute("objectId", objectId);
		if(StringUtils.isNotBlank(objectId)){
			Asset res = baseSemanticSerivce.getResourceById(objectId);
			if(null != res){
				model.addAttribute("module", res.getCommonMetaData().getModule());
				model.addAttribute("type", res.getCommonMetaData().getType());
				String libType = res.getCommonMetaData().getLibType();
				model.addAttribute("libType", libType);
				Gson gson = new Gson();
				model.addAttribute("resourceDetail", gson.toJson(res));
				model.addAttribute("execuId",WorkFlowUtils.getExecuId(objectId, libType));
			}else{
				return "/error/errorUnResOpen";
			}
		}
		return baseUrl + "openDetail";
	}
	/**
	 * 保存页面问题json
	 * @param asset
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"testPapers") 
	public @ResponseBody  Map<String, Object> testPapers(HttpServletRequest request,HttpServletResponse response,ModelMap model) throws Exception{
		//查询省
		//model.addAttribute("provinces", areaService.getProvince());
		//model.addAttribute("objectId", objectId);
		Map<String, Object> rtn = new HashMap<String, Object>();
		String content_json = request.getParameter("content_json");
		String analysis_description = request.getParameter("analysis_description");
		String answer_description = request.getParameter("answer_description");
		String answer_json = request.getParameter("answer_json");
		HttpSession session = getSession();
		String value = "0";
			session.setAttribute("content_json", content_json);
			session.setAttribute("analysis_description", analysis_description);
			session.setAttribute("answer_description", answer_description);
			session.setAttribute("answer_json", answer_json);
			value = "1";
		rtn.put("value", value);
		return rtn;
	}
	/**
	 * 清空session
	 * 
	 */
	@RequestMapping(baseUrl+"removeSession")
	public @ResponseBody  Map<String, Object> removeSession(){
		Map<String, Object> rtn = new HashMap<String, Object>();
		HttpSession session = getSession();
		session.removeAttribute("content_json");
		session.removeAttribute("analysis_description");
		session.removeAttribute("answer_description");
		session.removeAttribute("answer_json");
		return rtn;
	}
	/**
	 * 获得市，区 ，市和区 列表
	 * @param type
	 * @param code
	 * 
	 */
	@RequestMapping("/getPlaceList")
	public @ResponseBody List<?> getPlaceList(@RequestParam("type") String type,@RequestParam("code") String code){
		if(StringUtils.equals(type, "1")){
			return areaService.getProvince();
		}else if(StringUtils.equals(type, "2")){
			return areaService.getCity(code);
		}else if(StringUtils.equals(type, "3")){
			return areaService.getArea(code);
		}
		return null;
	}
	
	/**
	 * 获取当前资源类型的取值范围
	 * @param response
	 * @param module
	 * @param type
	 * @param educational_phase
	 * @return
	 */
	@RequestMapping("/getDomainType")
	public @ResponseBody int getDomainType(HttpServletResponse response,@RequestParam("module") String module,@RequestParam("educational_phase") String educational_phase){
		return ResourceTypeUtils.getDomainType(module, "", educational_phase);
	}
	
	/**
	 * 获取分类树的下一个节点
	 * @param response
	 * @param codes
	 * @param domainType
	 */
	@RequestMapping("/getDomainNode")
	public void getDomainNode(HttpServletResponse response,@RequestParam("codes") String codes,@RequestParam("domainType") String domainType){
		outputResult(baseSemanticSerivce.getDomainNode(codes, domainType));
	}
	/**
	 * 获取当前资源类型的扩展元数据
	 * @param response
	 * @param type
	 * @param ext
	 * @param objectId
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/getExtendMetaHtml")
	public @ResponseBody  Map<String, Object> getExtendMetaHtml(HttpServletRequest request,HttpServletResponse response,@RequestParam("type") String type,@RequestParam("ext") String ext,@RequestParam("objectId") String objectId,@RequestParam(value = "educational_phaseName", required = false) String educational_phaseName,@RequestParam(value = "subjectName", required = false) String subjectName) throws UnsupportedEncodingException{
		String metaId = ResourceTypeUtils.getExtendMetaDataFlag(type, ext);
		Set<String> notInsertSet = new HashSet<String>();
		Map<String, Object> rtn = new HashMap<String, Object>();
		if(educational_phaseName!=null){
			educational_phaseName=java.net.URLDecoder.decode(educational_phaseName,"UTF-8");
		}
		if(subjectName!=null){
			subjectName=java.net.URLDecoder.decode(subjectName,"UTF-8");
		}
		String textPer = request.getParameter("textPer");
		String module = request.getParameter("module");
		notInsertSet.add("meta1");
		notInsertSet.add("meta2");
		notInsertSet.add("meta3");
		notInsertSet.add("meta4");
		rtn.put("textPer", textPer);
		if(module !=null && module.equals("SJ")){
			metaId="meta11";
		}
		//图片 1,音频 2,视频 3,动画 4 这四种元数据在新建的时候不允许用户填充，所以返回null
		if(StringUtils.isBlank(objectId) && notInsertSet.contains(metaId)){
			rtn.put("outputResult", "");
		}else{
			String outputResult = ResourceTypeUtils.getHtmlContent(metaId,educational_phaseName,subjectName,request);
			rtn.put("outputResult", outputResult);
		}
		return rtn;
	}
	/**
	 * 获取资源详细
	 * @param response
	 * @param objectId
	 */
	@RequestMapping("/getResourceDetailById")
	public void getResourceDetailById(HttpServletResponse response,@RequestParam("objectId") String objectId){
		outputResult(baseSemanticSerivce.getResourceDetailById(objectId));
	}
	/**
	 * 获取资源的文件列表
	 * @param response
	 * @param objectId
	 * @return
	 */
	@RequestMapping("/getResourceFilesById")
	public @ResponseBody Map<String, Object> getResourceFilesById(HttpServletResponse response,@RequestParam("objectId") String objectId){
		List<com.brainsoon.semantic.ontology.model.File> files = baseSemanticSerivce.getResourceFilesById(objectId);
		Map<String, Object> rtn = new HashMap<String, Object>();
		if(files != null){
			rtn.put("total", files.size());
		}else{
			rtn.put("total", 0);
		}
		rtn.put("rows", files);
		return rtn;
	}
	/**
	 * 上传文件到临时目录,返回UUID标识,为文件夹
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping("/uploadFileToTemp")
	public @ResponseBody HashMap<String, Object> uploadFileToTemp(HttpServletRequest request, HttpServletResponse response,ModelMap model){
		logger.info("---------------------------------进入文件上传路径方法-----------------------------------------------");
		HashMap<String, Object> rtn = new HashMap<String, Object>();
		int status = 0;
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		MultipartFile multipartFile = null;
		for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
			multipartFile = set.getValue();// 文件名
		}
		String typeStatus = "1";
		String uploadFile = "";
		String fileName = multipartFile.getOriginalFilename();
//		String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
//		if(fileType.equals("zip")){
//			typeStatus = "0";
//		}else if(fileType.equals("rar")){
//			typeStatus = "0";
//		}else if(fileType.equals("Rar")){
//			typeStatus = "0";
//		}
		if (multipartFile.isEmpty()||multipartFile.getSize()<=0) {
			status = 1;
		} else {
			/**使用UUID生成文件名称**/
			String flag = UUID.randomUUID().toString();
			uploadFile = flag + File.separator + fileName;
			String folder = FILE_TEMP + flag;
			new File(folder).mkdirs();
			File restore = new File(FILE_TEMP + uploadFile);
			try {
				multipartFile.transferTo(restore);
				rtn.put("md5", MD5Util.getFileMD5String(restore));
			} catch (Exception e) {
				status = -1;
			}
		}
		logger.info("---------------------------------进入文件上传路径uploadFile---"+uploadFile+"--------------------------------------------");
		rtn.put("typeStatus", typeStatus);
		rtn.put("status", status);
		rtn.put("uploadFile", uploadFile);
		return rtn;
	}
	/**
	 * 获取单元树
	 * @param response
	 * @param code
	 * @throws IOException
	 */
	@RequestMapping("/getUnitTree")
	public void getUnitTree(HttpServletResponse response,@RequestParam("code") String code,@RequestParam("domainType") String domainType) throws IOException{
		//code截掉前两段
		if(StringUtils.equals(domainType, "0")){
			code = StringUtils.substringAfter(StringUtils.substringAfter(code, ","),",");
			code = baseSemanticSerivce.transPosition(code);
			outputResult(bookService.getCatalogByCode(code,domainType));
		}else{
			//根据不同资源模块获取不同数据
			String [] strs = StringUtils.split(code,",");
			String module = strs[0];
			if(StringUtils.equalsIgnoreCase(module, ResourceMoudle.TYPE0)){
				outputResult(bookService.getCatalogByCode(code,domainType));
			}else if(StringUtils.equalsIgnoreCase(module, ResourceMoudle.TYPE2)){
				outputResult(bookService.getKnowledgeByParam(strs[2]+"," + strs[3]));
			}else{
				outputResult(baseSemanticSerivce.domainsByMoudle(module));
			}
		}
	}
	
	/**
	 * 获取知识点
	 * @param response
	 * @param code
	 * @throws IOException
	 */
	@RequestMapping("/getKnowledgeTree")
	public void getKnowledgeTree(HttpServletResponse response,@RequestParam("code") String code) throws IOException{
		outputResult(bookService.getKnowledgeByParam(code));
	}
	/**
	 * 保存资源
	 * 2014年5月12日 修改上传文件逻辑，选择文件就新添文件，修改操作靠删除，再上传完成
	 * @param request
	 * @param response
	 * @param model
	 * @param asset
	 * @param uploadFile
	 */
	@RequestMapping(baseUrl + "saveRes")
	public void saveRes(HttpServletRequest request, HttpServletResponse response,ModelMap model,@ModelAttribute("frmAsset") Asset asset) {
		String operateDesc = "导入";
		String operateKey = "res_add_";
		String operateType = OperatType.IMPORT_OPERATE_TYPE;
		if(StringUtils.isNotBlank(asset.getObjectId())){
			operateDesc = "编辑";
			operateType = OperatType.EDIT_OPERATE_TYPE;
			operateKey = "res_upd_";
		}
		String objectId = resourceService.saveResource(asset,request.getParameter("uploadFile"),request.getParameter("thumbFile"),LoginUserUtil.getLoginName(),request.getParameter("repeatType"),request.getParameter("relationIds"));
		
		if(StringUtils.isNotBlank(objectId)){
			Long userId = LoginUserUtil.getLoginUser().getUserId();
			String libType = asset.getCommonMetaData().getLibType();
			//记录日志
			iEffectNumService.doPiecework(userId, operateType, libType,asset.getCommonMetaData().getRating(), 1);
			//记录操作历史
			String checkOpinion = "";
			sysOperateService.saveHistory(
					WorkFlowUtils.getExecuId(objectId,libType), 
					checkOpinion, 
					asset.getCommonMetaData().getStatusDesc(), 
					operateDesc, new Date(),userId);
			
			SysOperateLogUtils.addLog(operateKey+libType, "资源标题："+ asset.getCommonMetaData().getTitle(), LoginUserUtil.getLoginUser());
		}
	}
	/**
	 * 更新资源
	 * @param request
	 * @param response
	 * @param model
	 * @param asset
	 * @param uploadFile
	 */
	@RequestMapping(baseUrl + "updateRes")
	public void updateRes(HttpServletRequest request, HttpServletResponse response,ModelMap model,@ModelAttribute("frmAsset") Asset asset) {
		String operateDesc = "编辑";
		String operateType = OperatType.EDIT_OPERATE_TYPE;
		String operateKey = "res_upd_";
		String objectId = resourceService.updateResource(asset,request.getParameter("uploadFile"),request.getParameter("thumbFile"),LoginUserUtil.getLoginName(),request.getParameter("relationIds"));
		Long userId = LoginUserUtil.getLoginUser().getUserId();
		String libType = asset.getCommonMetaData().getLibType();
		//记录日志
		iEffectNumService.doPiecework(userId, operateType, libType,asset.getCommonMetaData().getRating(), 1);
		//记录操作历史
		String checkOpinion = "";
		sysOperateService.saveHistory(
				WorkFlowUtils.getExecuId(objectId,libType), 
				checkOpinion, 
				asset.getCommonMetaData().getStatusDesc(), 
				operateDesc, new Date(),userId);
		SysOperateLogUtils.addLog(operateKey+libType, "资源标题："+ asset.getCommonMetaData().getTitle(), LoginUserUtil.getLoginUser());
	}
	/**
	 * 检查重复
	 * @param request(不需要MD5查重了)
	 * @param response
	 * @return Map<String,Object>
	 */
	@RequestMapping(baseUrl + "checkRepeat")
	public @ResponseBody Map<String,Object> checkRepeat(HttpServletRequest request, HttpServletResponse response){
		String source = request.getParameter("source");
		String type = request.getParameter("type");
		String title = request.getParameter("title");
		String creator = request.getParameter("creator");
		String module = request.getParameter("module");
		String md5 = request.getParameter("md5");
		List<Asset> rs = baseSemanticSerivce.getResourceByMoreCondition(source, type, title, creator, module,md5);
		Map<String,Object> rtn = new HashMap<String, Object>();
		int status = 0;//不重复
		if(rs != null && rs.size() > 0){
			status = 1;
			rtn.put("res", rs);
		}
		rtn.put("status", status);
		return rtn;
	}
//	/**
//	 * 执行删除操作
//	 * @param response
//	 * @param ids
//	 * @throws IOException
//	 */
//	@RequestMapping(baseUrl + "delRes/{libType}")
//	public void delRes(HttpServletResponse response,@RequestParam("ids") String ids,@PathVariable("libType") String libType) throws IOException{
//		resourceService.deleteByIds(ids);
//		outputResult("删除成功");
//	}
	/**
	 * 执行强制删除操作
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(baseUrl + "enforceDelete/{libType}")
	public @ResponseBody Map<String,Object> enforceDelete(HttpServletResponse response,@RequestParam("ids") String ids,@PathVariable("libType") String libType) throws IOException{
		Map<String,Object> rtn = new HashMap<String, Object>();
		String orderId = resOrderService.canDelByResId(ids);
		String status = "1";
		if(orderId.equals("")){
			resourceService.deleteByIds(ids);
		}else{
			status = "0";
			rtn.put("status", status);
		}
			rtn.put("status", status);
			return rtn;
	}
	/**
	 * 删除资源对应的文件
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(baseUrl + "delFile")
	public void delFile(HttpServletResponse response,@RequestParam("ids") String ids,@RequestParam("paths") String paths,@RequestParam("resId") String resId) throws IOException{
		resourceService.deleteFileByIds(ids, paths,resId);
		outputResult("删除成功");
	}
	
	/**
	 * 下载资源
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping(baseUrl + "downloadRes/{libType}")
	public void downloadRes(HttpServletRequest request,HttpServletResponse response,@RequestParam("ids") String ids){
		String libType = request.getParameter("libType");
		String encryptPwd = request.getParameter("encryptPwd");
		resourceService.downloadRes(request, response, ids, encryptPwd,libType);
	}
	
	/**
	 * 查询关联资源列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "listRelationRes")
	public void listRelationRes(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		outputResult(baseSemanticSerivce.queryRelationsResource4Page(request, conditionList));
	}
	
	/**
	 * 关联资源
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "addRelationRes")
	public void addRelationRes(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String relationIds = request.getParameter("relationIds");
		baseSemanticSerivce.assetRelation(id, relationIds);
	}
	
	/**
	 * 删除关联
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "delRelationRes")
	public void delRelationRes(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String relationIds = request.getParameter("relationIds");
		baseSemanticSerivce.delRelation(id, relationIds);
	}
	
	/**
	 * 获取资源的所有文件相对路径
	 * @param response
	 * @param objectId
	 * @throws IOException
	 */
	@RequestMapping(value = baseUrl +"/getResFilePaths")
	public @ResponseBody List<com.brainsoon.semantic.ontology.model.File> getResFilePaths(HttpServletResponse response,@RequestParam("objectId") String objectId) throws IOException{
		List<com.brainsoon.semantic.ontology.model.File> files = baseSemanticSerivce.getResourceFilesById(objectId);
		return files;
	}
	
	/**
	 * 下载模板
	 * @return
	 * @throws IOException
	 */
    @RequestMapping(value = baseUrl + "downloadTemplete/{module}")
    public ResponseEntity<byte[]> download(HttpServletRequest request,@PathVariable("module") String module) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = "资源导入模板.xls";
        if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
        	filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
        }else{
        	filename = URLEncoder.encode(filename, "UTF-8");
        }
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(batchImportResService.getExcelTemplete(module)),headers, HttpStatus.OK);
    }
    
    /**
	 * 下载模板
	 * @return
	 * @throws IOException
	 */
    @RequestMapping(value = baseUrl + "downloadTempleteBook/book")
    public ResponseEntity<byte[]> downloadBook() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = URLEncoder.encode("资源导入模板.xls", "UTF-8");
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(batchImportResService.getExcelTemplete("Book")),headers, HttpStatus.OK);
    }
    /**
     * 获取不同模块下的学段
     * @param response
     * @param module
     * @return String
     */
    @RequestMapping(value = baseUrl + "getEducationalPhaseOptions")
    public void getEducationalPhaseOptions(HttpServletResponse response,@RequestParam("module") String module){
    	outputResult(baseSemanticSerivce.getEducationalPhaseOptions(module));
    }
    public List<ResTarget> getUseResTargets() {
		return useResTargets;
	}
	public void setUseResTargets(List<ResTarget> useResTargets) {
		this.useResTargets = useResTargets;
	}
	
	/**
	 * 下载模版
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping(value = baseUrl + "downloadFile")
	public void downloadFile(HttpServletRequest request,HttpServletResponse response,@RequestParam("filePath") String filePath){
		try {	
			filePath = FILE_ROOT + filePath;
			resourceService.downloadFile(request, response, filePath, false);
		} catch (Exception e) {
			logger.info("下载资源文件失败："+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 下载模版
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping(value = baseUrl + "downloadAbsFile")
	public void downloadAbsFile(HttpServletRequest request,HttpServletResponse response,@RequestParam("filePath") String filePath){
		try {	
			resourceService.downloadFile(request, response, filePath, false);
		} catch (Exception e) {
			logger.info("下载资源文件失败："+e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		SearchParamCa searchParamCa = new SearchParamCa();
		String field = "field";
		String fieldValue = "2";
		try {
			BeanUtils.setProperty(searchParamCa, field, fieldValue);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("");
	}
}
