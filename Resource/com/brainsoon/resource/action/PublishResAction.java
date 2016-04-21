package com.brainsoon.resource.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.support.SysResTypeCacheMap;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.image.ImageUtils;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.FilePathUtil;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.dofile.zip.ZipOrRarUtil;
import com.brainsoon.common.util.isbn.ISBNChecker;
import com.brainsoon.docviewer.model.ResConverfileTask;
import com.brainsoon.docviewer.model.ResConverfileTaskHistory;
import com.brainsoon.docviewer.service.IResConverfileTaskService;
import com.brainsoon.jbpm.service.IJbpmExcutionService;
import com.brainsoon.resource.po.FileDownName;
import com.brainsoon.resource.po.FileDownValue;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.po.UploadTaskDetail;
import com.brainsoon.resource.service.IAreaService;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.IBatchImportResService;
import com.brainsoon.resource.service.IPublishResService;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.support.ExcelUtil;
import com.brainsoon.resource.support.SemanticResponse;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.resource.util.MD5Util;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.resource.util.publishResConstants;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.search.service.ISearchIndexService;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CaList;
import com.brainsoon.semantic.ontology.model.CopyRightMetaData;
import com.brainsoon.semantic.ontology.model.DoFileHistory;
import com.brainsoon.semantic.ontology.model.DoFileQueue;
import com.brainsoon.semantic.ontology.model.DoFileQueueList;
import com.brainsoon.semantic.ontology.model.Entry;
import com.brainsoon.semantic.ontology.model.SearchParamCa;
import com.brainsoon.semantic.ontology.model.SearchResult;
import com.brainsoon.semantic.ontology.model.SearchResultCa;
import com.brainsoon.semantic.ontology.model.SearchResultEventy;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.Company;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.Staff;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.IBookService;
import com.brainsoon.system.service.ICompanyService;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.IFLTXService;
import com.brainsoon.system.service.IResTargetService;
import com.brainsoon.system.service.IStaffService;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.service.IZTFLService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants.BatchImportDetaillType;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE) 
public class PublishResAction extends BaseAction{
	public  static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
	public  static final String FILE_DOWN = StringUtils.replace(WebAppUtils.getWebAppBaseFileDown(),"\\", "/");
	public final static String FILE_COVER = WebAppUtils.getWebAppRelFileDir()+ConstantsDef.fileCover+"/";
	 public  static final String TMP_DIR = StringUtils.replace(WebAppUtils.getTempDir(),"\\", "/");
		private final static String PUBLISH_FILE_WRITE_QUEUE = WebappConfigUtil.getParameter("PUBLISH_FILE_WRITE_QUEUE");
		public  static final String FILE_TEMP = StringUtils.replace(WebAppUtils.getWebRootBaseDir("fileTemp"),"\\", "/");
		private final static String CA_FILERES_DETAIL_URL = WebappConfigUtil.getParameter("CA_FILERES_DETAIL_URL");
		private final static String CA_FILERES_SAVE_URL = WebappConfigUtil.getParameter("CA_FILERES_SAVE_URL");
		private static final String PUBLISH_SAVE_URL = WebappConfigUtil.getParameter("PUBLISH_SAVE_URL");
		private static final String PUBLISH_RENAME_FILE = WebappConfigUtil.getParameter("PUBLISH_RENAME_FILE");
		private static final String COPYRIGHT_METADATA_DELALL_URL = WebappConfigUtil.getParameter("COPYRIGHT_METADATA_DELALL_URL");
		private static final String CA_FILES_BY_PUBLISHTYP = WebappConfigUtil.getParameter("CA_FILES_BY_PUBLISHTYP");
		private static final String SEARCH_ENTRY_DETAIL = WebappConfigUtil.getParameter("SEARCH_ENTRY_DETAIL");
		public  static final String CONVER_FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirCFR(),"\\", "/");
		private static final String PUBLISH_OVERRIDE_URL = WebappConfigUtil
				.getParameter("PUBLISH_OVERRIDE_URL");
		private final static SimpleDateFormat dateformat2 = new SimpleDateFormat(
				"yyyyMMddHHmmssSSS");
		//资源明细列表接口
		private final static String PUBLISH_DETAILLIST_URL = WebappConfigUtil.getParameter("PUBLISH_DETAILLIST_URL");
	 /** 默认命名空间 **/
	 private final String baseUrl = "/publishRes/";
	 UserInfo userInfo =  LoginUserUtil.getLoginUser();
	 private static final String BOOK_CREATE="publish_save";
	 private static final String BOOK_UPDATE="publish_update";
	 @Autowired
	 private IResourceService resourceService;
	 @Autowired
	 private IResTargetService resTargetService;
	 @Autowired
	 private IAreaService areaService;
	 @Autowired
	 private IPublishResService publishResService;
	 @Autowired
	 private IBookService bookService;
	 @Autowired
	 private IBaseSemanticSerivce baseSemanticSerivce;
	 @Autowired
	 private IBatchImportResService batchImportResService;
	 @Autowired
	 private IResOrderService resOrderService;
	 @Autowired
	 private IZTFLService zTFLService;
	 @Autowired
	 private ISysParameterService sysParameterService;
	 @Autowired
	 private IDictNameService dictNameService;
	 @Autowired
	 private ISysOperateService sysOperateService;
	 @Autowired
	 private IJbpmExcutionService jbpmExcutionService;
	 @Autowired
	 private ISearchIndexService searchIndexService;
	 @Autowired
	@Qualifier("resConverfileTaskService")
	private IResConverfileTaskService resConverfileTaskService;
	 
	 @Autowired
	 private IStaffService staffService;
	 @Autowired
	 private ICompanyService companyService;
	 @Autowired
	 private IFLTXService fltxService;
	 
	 @RequestMapping(value=baseUrl+"toDetail")
	public String toDetail(HttpServletRequest request, ModelMap model) {
		String objectId = request.getParameter("objectId");
		String searchFlag = request.getParameter("searchFlag");
		String publishType = request.getParameter("publishType");
		String flagSta = request.getParameter("flagSta");
		String status = request.getParameter("status");
		String targetField = request.getParameter("targetField");
		String targetNames = request.getParameter("targetNames");
		String cbclassFieldValue = request.getParameter("cbclassFieldValue");
		String cbclassField = request.getParameter("cbclassField");
		String copyrightWaring = request.getParameter("copyrightWaring");
		HttpClientUtil http = new HttpClientUtil();
		String resourceDetail = http.executeGet(WebappConfigUtil
				.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
//		String returnCopyRightMetaData=  http.executeGet(WebappConfigUtil// + objectId
//				.getParameter("COPYRIGHT_METADATA_DELALL_URL") + "&identifer="+objectId);
		Gson gson = new Gson();
		Ca bookCa = gson.fromJson(resourceDetail, Ca.class);
		if(bookCa!=null){
			model = publishResService.jsonArray(bookCa, objectId, model);
			model.put("publishType", bookCa.getPublishType());
		}
//		if(StringUtils.isNotBlank(returnCopyRightMetaData)){
//			JSONObject obj = new JSONObject();
//			JSONObject ob= (JSONObject) obj.fromObject(returnCopyRightMetaData);
//			Integer  status1 = (Integer)ob.get("status");
//			SemanticResponse rtn = gson.fromJson(returnCopyRightMetaData, SemanticResponse.class);
//			String dataValue = rtn.getStatus();
//			if(status1!=-1){
//				JSONObject objData = ResUtils.copyrigntMedata(returnCopyRightMetaData);
//				CopyRightMetaData  copyRightMetaData = (CopyRightMetaData) JSONObject.toBean(objData, new CopyRightMetaData(), new JsonConfig());
//				if(copyRightMetaData!=null){
//					bookCa.setCopyRightMetaData(copyRightMetaData);
//				}
//			}
//		}
		model.put("detailFlag", "detail");
		String execuId = WorkFlowUtils.getExecuId(objectId, "pubresCheck");
		model.put("execuId", execuId);
		model.put("objectId", objectId);
		model.put("bookCa", bookCa);
		model.put("flagSta", flagSta);
		model.put("searchFlag", searchFlag);
		model.put("status", status);
		model.put("targetField", targetField);
		model.put("targetNames", targetNames);
		model.put("cbclassFieldValue", cbclassFieldValue);
		model.put("cbclassField", cbclassField);
		if(StringUtils.isNotBlank(copyrightWaring)){
			model.put("copyrightWaring", copyrightWaring);
		}else{
			model.put("copyrightWaring", "");
		}
		logger.debug(resourceDetail);
		return baseUrl + "createDetail";
	}
	 @RequestMapping(value=baseUrl+"openDetail")
		public String openDetail(@RequestParam(value="datatype",required=false)String datatype,HttpServletRequest request, ModelMap model) {
			String objectId = request.getParameter("objectId");
			String searchFlag = request.getParameter("searchFlag");
			String flagSta = request.getParameter("flagSta");
			String pathDetail = (String)request.getParameter("path");
			String fieldName = (String)request.getParameter("fieldName");
			String startTime = (String)request.getParameter("startTime");
			String endTime = (String)request.getParameter("endTime");
			String returnBack = (String)request.getParameter("returnBack");
			// String objectId = "urn:publish-9bca49b0-5c66-4cbd-a0c3-df7fffe3745b";
			HttpClientUtil http = new HttpClientUtil();
			String resourceDetail = http.executeGet(WebappConfigUtil
					.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
			Gson gson = new Gson();
			Ca bookCa = gson.fromJson(resourceDetail, Ca.class);
			if(bookCa!=null){
				model = publishResService.jsonArray(bookCa, objectId, model);
				model.put("publishType", bookCa.getPublishType());
				model.put("publishType", bookCa.getPublishType());
			}
			model.put("detailFlag", "detail");
			String execuId = WorkFlowUtils.getExecuId(objectId, "pubresCheck");
			model.put("execuId", execuId);
			model.put("objectId", objectId);
			model.put("bookCa", bookCa);
			model.put("flagSta", flagSta);
			model.put("searchFlag", searchFlag);
			model.put("pathDetail", pathDetail);
			model.put("fieldName", fieldName);
			model.put("startTime", startTime);
			model.put("endTime", endTime);
			model.put("returnBack", returnBack);
			model.put("datatype", datatype);
			logger.debug(resourceDetail);
			return baseUrl + "openDetail";
		}
	@RequestMapping(value = baseUrl + "toEdit")
	public String toEdit(HttpServletRequest request, ModelMap model) {
		ResUtils.CreateAliasName();
		String targetField = "";
		String objectId = request.getParameter("objectId");
		String publishType = request.getParameter("publishType");
		String cbclassFieldValue = request.getParameter("cbclassFieldValue");
		String cbclassField = request.getParameter("cbclassField");
		String isTarget = request.getParameter("isTarget");
//		String taskFlagAddFile = request.getParameter("taskFlagAddFile");
		HttpClientUtil http = new HttpClientUtil();
		String resourceDetail = "";
		String targetNames ="";
		String  returnCopyRightMetaData= "";
		Gson gson = new Gson();
		String data = "";
		Ca bookCa = null;
		if (!objectId.equals("-1")) {
			try{
				resourceDetail = http.executeGet(WebappConfigUtil
						.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
				returnCopyRightMetaData=  http.executeGet(WebappConfigUtil
					.getParameter("COPYRIGHT_METADATA_DELALL_URL") + "&identifer=" + objectId);
			} catch (Exception e) {
				bookCa = gson.fromJson(resourceDetail, Ca.class);
			}
		}
		bookCa = gson.fromJson(resourceDetail, Ca.class);
		//版权元数据
		if(bookCa!=null&&bookCa.getHasCopyright()==null&&!objectId.equals("-1")){
			String isHave=  http.executeGet(WebappConfigUtil
					.getParameter("COPYRIGHT_ISHAVE_URL") + "&identifer=" + objectId);
			SemanticResponse isHaveReturn = gson.fromJson(isHave, SemanticResponse.class);
			data = isHaveReturn.getData();
			if(data.equals("true")){
				bookCa.setHasCopyright("1");
			}
			if(bookCa.getMetadataMap().get("cover")!=null && !"".equals(bookCa.getMetadataMap().get("cover")))
			model.put("cover", bookCa.getMetadataMap().get("cover"));
		}
		if(StringUtils.isNotBlank(returnCopyRightMetaData)){
			JSONObject obj = new JSONObject();
			JSONObject ob= (JSONObject) obj.fromObject(returnCopyRightMetaData);
			Integer  status = (Integer)ob.get("status");
//			SemanticResponse rtn = gson.fromJson(returnCopyRightMetaData, SemanticResponse.class);
//			String dataValue = rtn.getStatus();
			if(status!=-1){
				JSONObject objData = ResUtils.copyrigntMedata(returnCopyRightMetaData);
				CopyRightMetaData  copyRightMetaData = (CopyRightMetaData) JSONObject.toBean(objData, new CopyRightMetaData(), new JsonConfig());
				if(copyRightMetaData!=null){
					bookCa.setCopyRightMetaData(copyRightMetaData);
				}
			}
		}
		String creatTime ="";
		String creator ="";
		if(StringUtils.isNotBlank(isTarget)){
			targetField = MetadataSupport.getTargetFieldName();
			if (bookCa != null) {
				if(bookCa.getMetadataMap().get(targetField)!=null||!"".equals(bookCa.getMetadataMap().get(targetField))){
					 targetNames = bookCa.getMetadataMap().get(targetField);
				}
				
			}
		}else if(bookCa != null&&bookCa.getCreateTime()!=null&&bookCa.getCreator()!=null){
			creatTime = bookCa.getCreateTime();
			creator= bookCa.getCreator();
			model = publishResService.jsonArray(bookCa, objectId, model);
			if(bookCa.getStatus()!=null &&!"".equals(bookCa.getStatus())){
				//status状态添加
				model.put("status", bookCa.getStatus());
			}
		}
		List<Task> tasks = jbpmExcutionService.getCurrentTasks(WorkFlowUtils.getExecuId(objectId, "pubresCheck"));
		if (tasks != null && tasks.size() > 0) {
			model.addAttribute("wfTaskId",tasks.get(0).getId());
		}
		model.put("cbclassFieldValue", cbclassFieldValue);
		model.put("cbclassField", cbclassField);
		model.put("bookCa", bookCa);
		model.put("creator", creator);
		model.put("creatTime", creatTime);
		model.put("creatTime", creatTime);
//		model.put("bookCa", bookCa);
		model.put("targetNames", targetNames);
		if(bookCa!=null){
			String rootPath = bookCa.getRootPath();
			if(StringUtils.isNotBlank(rootPath)){
				rootPath = rootPath.replaceAll("\\\\", "/");
				model.put("rootPath", rootPath);
			}
			model.put("rootPath", rootPath);
		}
		model.put("publishType", publishType);
		model.put("objectId", objectId);
//		model.put("taskFlagAddFile", taskFlagAddFile);
		return baseUrl + "create";
	}
	@RequestMapping(value = baseUrl + "checkISBN")
	public @ResponseBody String validateISBN(HttpServletRequest request){
		logger.info("进入查询方法");
		String isbnValue = request.getParameter("fieldValue");
		String fieldId = request.getParameter("fieldId");
		if (StringUtils.isNotBlank(isbnValue)) {
			try {
				ISBNChecker good = new ISBNChecker(isbnValue);
			} catch (Exception e) {
				return "{\"jsonValidateReturn\": [\""+fieldId+"\",false]}";
			}
		}
		return "{\"jsonValidateReturn\": [\""+fieldId+"\",true]}";
	}
	
		/**
		 * 下载日志
		 * @param request
		 * @param response
		 * @param ids
		 * @throws Exception 
		 * @throws IOException 
		 */
		@RequestMapping(value = baseUrl + "downloadAbsFile")
		public  ResponseEntity<byte[]> downloadAbsFile(HttpServletRequest request,HttpServletResponse response) throws IOException, Exception{
				String filePath = request.getParameter("filePath");
				String excelNum = request.getParameter("allNum");
				String failExcelPath = request.getParameter("failExcelPath");
				String numIng = "";
				File file=null;
				String id = request.getParameter("id");
				 try {
					 filePath=  URLDecoder.decode(filePath,"UTF-8");
					 failExcelPath=  URLDecoder.decode(failExcelPath,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				 Map<String,File> mapFile = new HashMap<String, File>();
				 mapFile = resourceService.downloadlog(request, response,id,filePath,excelNum,failExcelPath);
				 Iterator it = mapFile.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pairs = (Map.Entry) it.next();
						numIng = (String) pairs.getKey();
						file = (File) pairs.getValue();
					}
				 
				 HttpHeaders headers = new HttpHeaders();
			     headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			     String filename = null;
				try {
					filename = URLEncoder.encode("已处理"+numIng+"条日志.xls", "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			     headers.setContentDispositionFormData("attachment", filename);
			     return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),headers, HttpStatus.OK);
		}
		/**
		 * 执行强制删除操作
		 * @param response
		 * @param ids
		 * @throws IOException
		 */
		@RequestMapping(baseUrl + "enforceDelete")
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
	 @RequestMapping(value=baseUrl+"toCreate")
	 public String toCreate(HttpServletRequest request,ModelMap model) {
			 Ca bookCa=new Ca();
	         model.put("bookCa", bookCa);
	  	     return baseUrl+"create";
	 }
	 
	 /**
	  * 批量检索
	  * @param request
	  * @param response
	  * @return
	  */
	 @RequestMapping(value="/publishRes/query")
	 @ResponseBody
	 public String  query(HttpServletRequest request, HttpServletResponse response) {
		 QueryConditionList conditionList = getQueryConditionList();
		 String publishType = "";
		 List<QueryConditionItem> priveilegeItems = null;
		 
		 logger.info("+++++++++++++++++++++++++++++++++进入query查询方法publishType"+publishType+"+++++++++++++++++++++++++++++++++++++++++++++=");
		 if (null != conditionList) {
			 
			 logger.info("+++++++++++++++++++++++++++++++++判断conditionList是否为空"+conditionList+"+++++++++++++++++++++++++++++++++++++++++++++=");
			 List<String> fieldNames = new ArrayList<String>();
			 List<QueryConditionItem> items = conditionList.getConditionItems();
			 if(items!=null && items.size()>0){
				 for(QueryConditionItem item:items){
					 if(item.getFieldName().equals("publishType")){
						 publishType = item.getValue()+ "";
					 }
					 fieldNames.add(item.getFieldName());
				 }
			 }
			 
			 if(StringUtils.isNotBlank(publishType)){
				 priveilegeItems = getUserPrivilege(publishType);
				 if(priveilegeItems!=null){
					 List<QueryConditionItem> priveilegeItemTemps = getUserPrivilege("common");
					 if(priveilegeItemTemps!=null){
						 priveilegeItems.addAll(priveilegeItemTemps);
					 }
				 }
			 } 
			 
			 if(priveilegeItems!=null && priveilegeItems.size()>0){
				 for(QueryConditionItem privilegeItem:priveilegeItems){
					 if(!fieldNames.contains(privilegeItem.getFieldName())){
						 items.add(privilegeItem);
					 }
				 }
			 }
		 }
		// String result = resTargetService.query(request.getParameter("resTargetId"),"",request,conditionList);
		 String result= publishResService.queryResource4Page(request, conditionList,WebappConfigUtil.getParameter("PUBLISH_QUERYBYPOST_URL"));
		 logger.info("+++++++++++++++++++++++++++++++++返回result是否为空"+result+"+++++++++++++++++++++++++++++++++++++++++++++=");
		 boolean hasCatagory = false;//是否在列表页展示分类信息（如中图分类）
		 List<MetadataDefinition> metadataDefinitions = MetadataSupport.getAllMetadataDefinitionByResType(publishType);
		 Map<String,String> categoryFields = new HashMap<String,String>();
		 Map<String,String> dateType = new HashMap<String,String>();
		 for(MetadataDefinition metadataDefinition:metadataDefinitions){
			 int fieldType = metadataDefinition.getFieldType();
			 hasCatagory = true;
			 String viewPriority = metadataDefinition.getViewPriority();
			 if(viewPriority != null){
				 if(fieldType == 6 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
				 }else if(fieldType == 9 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
				 }else if(fieldType == 10 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
				 }else if(fieldType == 11 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
				 }else if(fieldType == 2 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", "9");
				 }else if(fieldType == 3 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", "9");
				 }else if(fieldType == 4 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", "9");
				 }else if(fieldType == 7 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
					 dateType.put(metadataDefinition.getFieldName(), metadataDefinition.getValueRange());

				 }
			 }
		 }
		 if(hasCatagory){
			 logger.info("+++++++++++++++++++++++++++++++++hasCatagory++++++++++++++++++++++++++++++++++++++++++++=");
			 Gson gson=new Gson();
			 SearchResultCa caList = gson.fromJson(result, SearchResultCa.class);
			 if(caList!=null && !caList.getRows().isEmpty()){
				 List<Ca> cas = caList.getRows();
				 if(cas!=null && cas.size()>0){
					 for(int i=0;i<cas.size();i++){
						 Ca ca = cas.get(i);
						 Map<String,String> metadataMap = ca.getMetadataMap();
						 boolean hasCategoryName =false;
						 Iterator it = categoryFields.entrySet().iterator();
							while (it.hasNext()) {
								Map.Entry pairs = (Map.Entry) it.next();
								if(StringUtils.isNotBlank(pairs.getKey().toString())&&StringUtils.isNotBlank(pairs.getValue().toString())){
									if(pairs.getValue().toString().equals("6")){
										 String value = metadataMap.get(pairs.getKey().toString());
										 if(StringUtils.isNotBlank(value)){
											 logger.info("+++++++++++++++++++++++++++++++++zTFLService.queryCatagoryCnName(value)1++++++++++++++++++++++++++++++++++++++++++++=");
											 String categoryName ="";
											 try {
												 categoryName = fltxService.queryCatagoryCnName(value);
												 metadataMap.put(pairs.getKey().toString(), categoryName);
											} catch (Exception e) {
												metadataMap.put(pairs.getKey().toString(), value);
											}
											 hasCategoryName = true;
										 }
									}else if(pairs.getValue().toString().equals("9")){
										 String value = metadataMap.get(pairs.getKey().toString());
										 if(StringUtils.isNotBlank(value)){
											 logger.info("+++++++++++++++++++++++++++++++++zTFLService.queryCatagoryCnName(value)2++++++++++++++++++++++++++++++++++++++++++++=");
											 String categoryName = "";
											 try {
												 categoryName = zTFLService.queryDictValue(value);
												 metadataMap.put(pairs.getKey().toString(), categoryName);
											} catch (Exception e) {
												 metadataMap.put(pairs.getKey().toString(), value);
											}
											 hasCategoryName = true;
										 }
									}else if(pairs.getValue().toString().equals("10")){
										 String value = metadataMap.get(pairs.getKey().toString());
										 if(StringUtils.isNotBlank(value)){
											 logger.info("+++++++++++++++++++++++++++++++++zTFLService.queryCatagoryCnName(value)2++++++++++++++++++++++++++++++++++++++++++++=");
											 String categoryName = "";
											 try {
												 String arrayStaff[] = value.split(",");
												 for(String sta:arrayStaff){
													 categoryName = categoryName + GlobalDataCacheMap.getNameStaffWithNameByKeyAndChildKey(sta+"staff",value)+",";
												 }
												 if(categoryName.endsWith(",")){
													 categoryName = categoryName.substring(0,categoryName.length()-1);
												 }
												 metadataMap.put(pairs.getKey().toString(), categoryName);
											} catch (Exception e) {
												 metadataMap.put(pairs.getKey().toString(), value);
											}
											 hasCategoryName = true;
										 }
									}else if(pairs.getValue().toString().equals("11")){
										 String value = metadataMap.get(pairs.getKey().toString());
										 if(StringUtils.isNotBlank(value)){
											 logger.info("+++++++++++++++++++++++++++++++++zTFLService.queryCatagoryCnName(value)2++++++++++++++++++++++++++++++++++++++++++++=");
											 String categoryName = "";
											 try {
												 String arrayCompany[] = value.split(",");
												 for(String com:arrayCompany){
													 categoryName = categoryName + GlobalDataCacheMap.getNameCompanyWithNameByKeyAndChildKey(com+"company",value)+",";
												 }
												 if(categoryName.endsWith(",")){
													 categoryName = categoryName.substring(0,categoryName.length()-1);
												 }
												 metadataMap.put(pairs.getKey().toString(), categoryName);
											} catch (Exception e) {
												 metadataMap.put(pairs.getKey().toString(), value);
											}
											 hasCategoryName = true;
										 }
									}else if(pairs.getValue().toString().equals("7")){
										 String value = metadataMap.get(pairs.getKey().toString());
										 String format = dateType.get(pairs.getKey().toString());
										 if (StringUtils.isNotBlank(value)) {
											Date dateOld = new Date(Long.parseLong(value)); // 根据long类型的毫秒数生命一个date类型的时间
											value = new SimpleDateFormat(format).format(dateOld);
											metadataMap.put(pairs.getKey().toString(), value);
										 }
									}
								}
							}
//						 for(String categoryField:categoryFields){
//							 String value = metadataMap.get(categoryField);
//							 logger.info("+++++++++++++++++++++++++++++++++categoryName+++++++++++++++++++++++++++++++++++++++++++++++++=");
//							 if(StringUtils.isNotBlank(value)){
//								 String categoryName = zTFLService.queryCatagoryCnName(value);
//								 logger.info("+++++++++++++++++++++++++++++++++categoryName"+categoryName+"+++++++++++++++++++++++++++++++++++++++++++++=");
//								 metadataMap.put(categoryField, categoryName);
//								 hasCategoryName = true;
//							 }
//						 }
						 if(hasCategoryName){
							 ca.setMetadataMap(metadataMap);
							 cas.set(i, ca);
						 }
						 if(StringUtils.isNotBlank(ca.getCreator())){
							 ca.setCreateName(OperDbUtils.getUserNameById(ca.getCreator()));
						 }
						 if(StringUtils.isNotBlank(ca.getCreateTime())){
							 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					    		String date = format.format(Long.parseLong(ca.getCreateTime()));
//							 Date  da =null;
//					    		try {
//									da = format.parse(ca.getCreateTime());
//								} catch (ParseException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
					    		ca.setCreateTime(date);
						 }
					 }
				 }
			 }
			 result = gson.toJson(caList);
		 }else{
			 logger.info("+++++++++++++++++++++++++++++++++else++++++++++++++++++++++++++++++++++++++++++++=");
			 Gson gson=new Gson();
			 SearchResultCa caList = gson.fromJson(result, SearchResultCa.class);
			 if(caList!=null){
				 List<Ca> cas = caList.getRows();
				 if(cas!=null && cas.size()>0){
					 for(int i=0;i<cas.size();i++){
						 Ca ca = cas.get(i);
						 if(StringUtils.isNotBlank(ca.getCreator())){
							 ca.setCreateName(OperDbUtils.getUserNameById(ca.getCreator()));
						 }
						 if(StringUtils.isNotBlank(ca.getCreateTime())){
							 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					    		String date = format.format(Long.parseLong(ca.getCreateTime()));
//							 Date  da =null;
//					    		try {
//									da = format.parse(ca.getCreateTime());
//								} catch (ParseException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
					    		ca.setCreateTime(date);
						 }
					 }
				 }
			 }
			 result = gson.toJson(caList);
		 }
		 return result;
	}
	 /**
	  * 列表查询
	  * @param request
	  * @param response
	  * @return
	  */
	 @RequestMapping(value=baseUrl+"publishResList")
	 public String  publishResList(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
//		 QueryConditionList conditionList = getQueryConditionList();
		 HttpSession session = getSession();
		 String publishType = request.getParameter("publishType");
		 String flag = request.getParameter("flag");
		 String status = request.getParameter("status");
		 String taskFlagAddFile = request.getParameter("taskFlagAddFile");
		 String cbclassField = request.getParameter("cbclassField");
		 String classTimeName = request.getParameter("classTimeName");
		 String cbclassFieldValue = request.getParameter(cbclassField);
		 if(StringUtils.isNotBlank(status)){
			 model.put("status", status);
		 }
		 if(StringUtils.isNotBlank(classTimeName)){
			 try {
				classTimeName = URLDecoder.decode(classTimeName,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		 }
		 List<SysParameter> enforceDelete = sysParameterService.findParaValue("enforceDelete");
		 if(enforceDelete!=null && enforceDelete.size()>0&&enforceDelete.get(0).getParaStatus().toString().equals("1")){
		 	session.setAttribute("enforceDelete", enforceDelete.get(0).getParaValue());
		 }else{
		 	session.removeAttribute("enforceDelete");
		 }
		 model.put("publishType", publishType);
		 model.put("taskFlagAddFile", taskFlagAddFile);
		 model.put("cbclassField", cbclassField);
		 model.put("cbclassFieldValue", cbclassFieldValue);
		 model.put("classTimeName", classTimeName);
		 
		MetaDataModelGroup mmg = (MetaDataModelGroup) baseService.getByPk(MetaDataModelGroup.class, Long.parseLong(publishType));
		String resShortName = mmg.getShortName();
		String isTM = "0";//非条目资源
		if (resShortName.endsWith("TM")) {
			isTM = "1";//条目资源
		}
		model.put("isTM", isTM);
		 
		 if(StringUtils.isNotBlank(flag)){
			 return "articleResource/articleResourceList";
		 }else{
			 return baseUrl+"publishResList";
		 }
	}
	 /**
	  * 下线列表查询
	  * @param request
	  * @param response
	  * @return
	  */
	 @RequestMapping(value=baseUrl+"offlinePublishResList")
	 public String  offlinePublishResList(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		 QueryConditionList conditionList = getQueryConditionList();
		 String publishType = request.getParameter("publishType");
		 HttpSession session = getSession();
		 List<SysParameter> enforceDelete = sysParameterService.findParaValue("enforceDelete");
		 if(enforceDelete!=null && enforceDelete.size()>0&&enforceDelete.get(0).getParaStatus().toString().equals("1")){
		 	session.setAttribute("enforceDelete", enforceDelete.get(0).getParaValue());
		 }else{
		 	session.removeAttribute("enforceDelete");
		 }
		 model.put("publishType", publishType);
		 return baseUrl+"offlinePubList";
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
	  * 出版标签List资源查询
	  * @param request
	  * @param response
	  * @return
	  */
	 @RequestMapping(value="/publishRes/queryTargetRes")
	 @ResponseBody
	 public String  queryTargetRes(HttpServletRequest request, HttpServletResponse response) {
		 QueryConditionList conditionList = getQueryConditionList();
		 String result = resTargetService.query(request.getParameter("resTargetId"),"",request,conditionList);
		 return result;
	}
	 /**
	  * 出版标签List资源查询
	  * @param request
	  * @param response
	  * @return
	  */
	 @RequestMapping(value="/publishRes/queryListTargetRes")
	 @ResponseBody
	 public String  queryListTargetRes(HttpServletRequest request, HttpServletResponse response) {
		 QueryConditionList conditionList = getQueryConditionList();
		 String targetNames = request.getParameter("targetNames");
		 String targetField = request.getParameter("targetField");
		 try {
			targetNames=  URLDecoder.decode(targetNames,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(StringUtils.isNotBlank(targetField) && StringUtils.isNotBlank(targetNames));{
			 targetNames = targetField +","+targetNames;
		 }
		 String status = request.getParameter("status");
		 String publishType = request.getParameter("publishType");
		 String result = resTargetService.queryList(targetNames,status,publishType,request,conditionList);
		 //String result = publishResService.queryResource4PageByParam(request, pageSize, size, WebappConfigUtil.getParameter("PUBLISH_QUERY_URL"));
		 Gson gson=new Gson();
		 SearchResultCa caList = gson.fromJson(result, SearchResultCa.class);
		 List<MetadataDefinition> metadataDefinitions = MetadataSupport.getAllMetadataDefinitionByResType(publishType);
		 Map<String,String> categoryFields = new HashMap<String,String>();
		 Map<String,String> dateType = new HashMap<String,String>();
		 boolean hasCatagory = false;
		 for(MetadataDefinition metadataDefinition:metadataDefinitions){
			 int fieldType = metadataDefinition.getFieldType();
			 hasCatagory = true;
			 String viewPriority = metadataDefinition.getViewPriority();
			 if(viewPriority != null){
				 if(fieldType == 6 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
				 }else if(fieldType == 9 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
				 }else if(fieldType == 10 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
				 }else if(fieldType == 11 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
				 }else if(fieldType == 2 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", "9");
				 }else if(fieldType == 3 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", "9");
				 }else if(fieldType == 4 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", "9");
				 }else if(fieldType == 7 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
					 dateType.put(metadataDefinition.getFieldName(), metadataDefinition.getValueRange());
				 }
			 }
		 }
		 if(hasCatagory){
			 if(caList!=null){
				 List<Ca> cas = caList.getRows();
				 if(cas!=null && cas.size()>0){
					 for(int i=0;i<cas.size();i++){
						 Ca ca = cas.get(i);
						 Map<String,String> metadataMap = ca.getMetadataMap();
						 boolean hasCategoryName =false;
						 Iterator it = categoryFields.entrySet().iterator();
						 while (it.hasNext()) {
							 Map.Entry pairs = (Map.Entry) it.next();
								if(StringUtils.isNotBlank(pairs.getKey().toString())&&StringUtils.isNotBlank(pairs.getValue().toString())){
									if(pairs.getValue().toString().equals("6")){
										String value = metadataMap.get(pairs.getKey().toString());
										 if(StringUtils.isNotBlank(value)){
											 logger.info("+++++++++++++++++++++++++++++++++zTFLService.queryCatagoryCnName(value)1++++++++++++++++++++++++++++++++++++++++++++=");
											 String categoryName ="";
											 try {
												 categoryName = fltxService.queryCatagoryCnName(value);
												 metadataMap.put(pairs.getKey().toString(), categoryName);
											} catch (Exception e) {
												metadataMap.put(pairs.getKey().toString(), value);
											}
											 hasCategoryName = true;
										 }
									}
									if(pairs.getValue().toString().equals("9")){
										 String value = metadataMap.get(pairs.getKey().toString());
										 if(StringUtils.isNotBlank(value)){
											 logger.info("+++++++++++++++++++++++++++++++++zTFLService.queryCatagoryCnName(value)2++++++++++++++++++++++++++++++++++++++++++++=");
											 String categoryName = "";
											 try {
												 categoryName = zTFLService.queryDictValue(value);
												 metadataMap.put(pairs.getKey().toString(), categoryName);
											} catch (Exception e) {
												 metadataMap.put(pairs.getKey().toString(), value);
											}
											 hasCategoryName = true;
										 }
									}
									if(pairs.getValue().toString().equals("10")){
										String value = metadataMap.get(pairs.getKey().toString());
										 if(StringUtils.isNotBlank(value)){
											 logger.info("+++++++++++++++++++++++++++++++++zTFLService.queryCatagoryCnName(value)2++++++++++++++++++++++++++++++++++++++++++++=");
											 String categoryName = "";
											 try {
												 String arrayStaff[] = value.split(",");
												 for(String sta:arrayStaff){
													 categoryName = categoryName + GlobalDataCacheMap.getNameStaffWithNameByKeyAndChildKey(sta+"staff",value)+",";
												 }
												 if(categoryName.endsWith(",")){
													 categoryName = categoryName.substring(0,categoryName.length()-1);
												 }
												 metadataMap.put(pairs.getKey().toString(), categoryName);
											} catch (Exception e) {
												 metadataMap.put(pairs.getKey().toString(), value);
											}
											 hasCategoryName = true;
										 }
									}
									if(pairs.getValue().toString().equals("11")){
										String value = metadataMap.get(pairs.getKey().toString());
										 if(StringUtils.isNotBlank(value)){
											 logger.info("+++++++++++++++++++++++++++++++++zTFLService.queryCatagoryCnName(value)2++++++++++++++++++++++++++++++++++++++++++++=");
											 String categoryName = "";
											 try {
												 String arrayCompany[] = value.split(",");
												 for(String com:arrayCompany){
													 categoryName = categoryName + GlobalDataCacheMap.getNameCompanyWithNameByKeyAndChildKey(com+"company",value)+",";
												 }
												 if(categoryName.endsWith(",")){
													 categoryName = categoryName.substring(0,categoryName.length()-1);
												 }
												 metadataMap.put(pairs.getKey().toString(), categoryName);
											} catch (Exception e) {
												 metadataMap.put(pairs.getKey().toString(), value);
											}
											 hasCategoryName = true;
										 }
									}else if(pairs.getValue().toString().equals("7")){
										 String value = metadataMap.get(pairs.getKey().toString());
										 String format = dateType.get(pairs.getKey().toString());
										 if (StringUtils.isNotBlank(value)) {
											Date dateOld = new Date(Long.parseLong(value)); // 根据long类型的毫秒数生命一个date类型的时间
											value = new SimpleDateFormat(format).format(dateOld);
											metadataMap.put(pairs.getKey().toString(), value);
											
										 }
									}
									 if(hasCategoryName){
										 ca.setMetadataMap(metadataMap);
										 cas.set(i, ca);
									 }
								    if(StringUtils.isNotBlank(ca.getCreator())){
								    	ca.setCreateName(OperDbUtils.getUserNameById(ca.getCreator()));
								    }
							 }
						 }
						 if(StringUtils.isNotBlank(ca.getCreateTime())){
							 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					    		String date = format.format(Long.parseLong(ca.getCreateTime()));
					    		ca.setCreateTime(date);
						 }
					 }
				 }
			 }
			 result = gson.toJson(caList);
		 }else{
			 logger.info("+++++++++++++++++++++++++++++++++else++++++++++++++++++++++++++++++++++++++++++++=");
			 if(caList!=null){
				 List<Ca> cas = caList.getRows();
				 if(cas!=null && cas.size()>0){
					 for(int i=0;i<cas.size();i++){
						 Ca ca = cas.get(i);
						 if(StringUtils.isNotBlank(ca.getCreator())){
							 ca.setCreateName(OperDbUtils.getUserNameById(ca.getCreator()));
						 }
						 if(StringUtils.isNotBlank(ca.getCreateTime())){
							 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					    		String date = format.format(Long.parseLong(ca.getCreateTime()));
					    		ca.setCreateTime(date);
						 }
					 }
				 }
			 }
			 result = gson.toJson(caList);
		 }
		 return result;
	}
	
	@RequestMapping(baseUrl + "saveRes")
	public void saveRes(HttpServletRequest request, HttpServletResponse response,ModelMap model,Ca ca,String uploadFile) {
		logger.debug("******run at saveRes***********");
		try {
			String repeatType = request.getParameter("repeatType");
			String targetNames = request.getParameter("targetNames");
			String publishType = request.getParameter("publishType");
			String creatTime = request.getParameter("creatTime");
			String creator = request.getParameter("creator");
			ca.setCreator(creator);
			ca.setCreateTime(creatTime);
			ca.setUpdateTime(creatTime);
//			uploadFile = FILE_TEMP+uploadFile;
//			DoiGenerateUtil.generateDoiByResDirectory(ca);
			String objectId = publishResService.savePublishRes(ca, uploadFile,repeatType,publishType,targetNames);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		    addActionError(e);
		}
	}
	
	@RequestMapping(baseUrl + "updateRes")
	@ResponseBody
	public String updateRes(HttpServletRequest request, HttpServletResponse response,ModelMap model,@ModelAttribute("frmAsset") Ca ca,@RequestParam(value="operateFrom",required=false) String operateFrom) {
		logger.debug("******run at updateRes***********");
		try {
			publishResService.updateCollectRes(ca,"");
			UserInfo user = LoginUserUtil.getLoginUser();
			SysOperateLogUtils.addLog(BOOK_UPDATE, ca.getCommonMetaData().getTitle(), user);
		} catch (Exception e) {
			logger.error(e.getMessage());
		    addActionError(e);
		}
		if (StringUtils.equals(operateFrom, "TASK_LIST_PAGE")) {
			return "/TaskAction/toList.action";
		} else {
			return "";
	//		return "/publishRes/publishResList.jsp?publishType=" + ca.getCommonMetaData().getPublishType();
		}
	}
	/**
	 * 出版资源检查重复
	 * @param request
	 * @param response
	 * @return Map<String,Object>
	 */
	@RequestMapping(baseUrl + "checkRepeat")
	public @ResponseBody Map<String,Object> checkRepeat(HttpServletRequest request, HttpServletResponse response,@ModelAttribute("frmAsset") Ca ca){
		String status1 = request.getParameter("status");
		String publishType = request.getParameter("publishType");
		Map<String,String> checkRepeatMetadate = new HashMap<String, String>();
//		if(!"".equals(status1)){
//			checkRepeatMetadate.put("status", status1);
//		}
//		if(!"".equals(publishType)){
//		checkRepeatMetadate.put("publishType", publishType);
//		}
		Map<String,MetadataDefinition> metadataDefinitionMap =  MetadataSupport.getAllMetadataDefinition(publishType);
		Iterator it = ca.getMetadataMap().entrySet().iterator();
		Map<String,Object> rtn = new HashMap<String, Object>();
		int num = 0;
		int num1 = 0;
		String checkField = "";
		String zhN = "";
		//查重字段
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			MetadataDefinition metadataDefinition = metadataDefinitionMap
					.get(pairs.getKey());
			if (metadataDefinition != null) {
				if (pairs.getKey() != null
						&& metadataDefinition.getDuplicateCheck() != null
						&& "true".equals(metadataDefinition.getDuplicateCheck())) {
					if (pairs.getKey() != null && !pairs.getValue().equals("")
							|| !"".equals(pairs.getKey())) {
						//记录查重字段个数
						num = num+1;
						if(StringUtils.isNotBlank(pairs.getKey().toString())&&StringUtils.isNotBlank(pairs.getValue().toString())){
						checkRepeatMetadate.put(pairs.getKey().toString().trim(),pairs.getValue().toString().trim());
						}else{
							MetadataDefinition zhName = MetadataSupport.getMetadataDefinitionByName(pairs.getKey().toString());
							zhN = zhName.getFieldZhName();
							checkField = checkField+zhN+"或";
							//记录查重字段为空个数
							num1 = num1+1;
						}
					}
				}
			}
		}
		CaList  repeatCas =null;
		if(num ==num1){
			if(!"".equals(checkField)){
				checkField = checkField.substring(0,checkField.length()-1);
				rtn.put("checkField", checkField);
			}else{
				rtn.put("checkField", "");	
			}
		}else{
			repeatCas = ResUtils.checkRepeat(checkRepeatMetadate,publishType,"");
		}
		int status = 0;//不重复
		if(repeatCas != null && repeatCas.getTotle() > 0){
			status = 1;
			rtn.put("res", repeatCas);
		}
		rtn.put("status", status);
		rtn.put("modifly", status1);
		return rtn;
	}
	/**
	 * 统计下载文件大小
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping(baseUrl + "downFileSize/{libType}")
	public @ResponseBody Map<String,Object> downFileSize(HttpServletRequest request,HttpServletResponse response,@RequestParam("ids") String ids){
		Map<String,Object> rtn = new HashMap<String, Object>();
		String encryptPwd = request.getParameter("encryptPwd");
		String boo = publishResService.fileSize(request, response, ids, encryptPwd);
		rtn.put("boo", boo);
		return rtn;
	}
	
	/**
	 * 添加文件
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping(value = baseUrl + "addFile")
	public @ResponseBody HashMap<String, Object> addFile(HttpServletRequest request) {
		HashMap<String, Object> rtn = new HashMap<String, Object>();
		String parentPath = request.getParameter("parentPath");
		String MD5 = request.getParameter("MD5");
		String nodeId = request.getParameter("nodeId");
		String isCompress = request.getParameter("isCompress");
		String isCover= request.getParameter("isCover");
//		String root = request.getParameter("root");
		String pid = nodeId;
		String objectId = request.getParameter("objectId");
		File restore = null;
		String md5 = "";
		int status = 0;
		if(StringUtils.isNotBlank(parentPath)){
			try {
				nodeId = URLDecoder.decode(nodeId,"utf-8");
				parentPath = URLDecoder.decode(parentPath,"utf-8");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		MultipartFile multipartFile = null;
		for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
			multipartFile = set.getValue();
		}
		String fileName = multipartFile.getOriginalFilename();
		Long fileByte =  multipartFile.getSize();
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
		String absDir = FILE_ROOT+parentPath;
		logger.info("++++++++++++++++++++++++++absDir替换前+++"+absDir+"+++++++++++++++++++++++++++++");
		absDir = absDir.replaceAll("\\\\", "/");
		logger.info("++++++++++++++++++++++++++a替换\\后absDir+++"+absDir+"+++++++++++++++++++++++++++++");
//		String level = "1";
		int num = 0;
		String newFileName = "";
		String absPath = "";
		String tempRestore = "";
		if(StringUtils.isNotBlank(nodeId)){
			nodeId = nodeId.replaceAll("\\\\", "/");
			if(nodeId.endsWith("/")){
				nodeId = nodeId.substring(0,nodeId.length()-1);
			}
			String[] nodes = nodeId.split("/");
//			level = (nodes.length+1)+"";
			//循环遍历存储路径的文件
			File file = new File(absDir);
			List<String> fileNames = null;
			if(file.exists()){
				File[] childFiles =  file.listFiles();
				if(childFiles!=null && childFiles.length>0){
					fileNames = new ArrayList();
					for(File childFile:childFiles){
						if(childFile.isFile()){
							fileNames.add(childFile.getName());
						}
					}
					for(String oldfileName:fileNames){
						if(oldfileName.equals(fileName)){
							num++;
						}
					}
					if(num!=0){
//						newFileName =  fileName+num;
						String temName = fileName.substring(0,fileName.lastIndexOf("."));
						newFileName = temName+num+"."+fileType;
					}else{
						newFileName =  fileName;
					}
					//老做法，改名
//					newFileName =  createRandom(fileNames,level,fileType);
				}else{
//					newFileName = level+"1"+"."+fileType;
					newFileName = fileName;
				}
			}
		}
		logger.info("++++++++++++++++++++++++++newFileName++++++"+newFileName+"++++++++++++++++++++++++++");
		if (multipartFile.isEmpty()) {
			logger.info("++++++++++++++++++++++++++判断multipartFile是否为空，status=1++++++++++++++++++++++++++++++++");
			status = 1;
		} else {
			if(absDir.endsWith("/")){
				absPath = absDir;
			}else{
				absPath = absDir+"/";
			}
			//生成md5
			tempRestore = FILE_TEMP+UUID.randomUUID()+"/";
			restore = new File(tempRestore);
			if(!restore.exists()){
				restore.mkdirs();
			}
			try {
				tempRestore = tempRestore+newFileName;
				multipartFile.transferTo(new File(tempRestore));
				md5 = MD5Util.getFileMD5String(new File(tempRestore));
			} catch (Exception e) {
				status = -1;
			}
		}
		//md5校验
		String checkMd5[] = MD5.split(",");
		boolean isRepeat = false;
		if(StringUtils.isNotBlank(md5)){
			for (int i = 0; i < checkMd5.length; i++) {
				if (checkMd5[i].equals(md5)) {
					isRepeat = true;
					break;
				}
			}
		}
		//解压
		if (!isRepeat) {
			if(isCompress.equals("true")&&fileType.equals("zip") || isCompress.equals("true")&&fileType.equals("rar")){
				if(StringUtils.isNotBlank(objectId)){
					HttpClientUtil http = new HttpClientUtil();
					Gson gson = new Gson();
					String resourceDetail = http.executeGet(WebappConfigUtil
							.getParameter("PUBLISH_DETAIL_URL")
							+ "?id="+objectId);
					Ca ca = gson.fromJson(resourceDetail, Ca.class);
					String tempPath = FILE_TEMP+UUID.randomUUID();
					if(!new File(tempPath).exists()){
						new File(tempPath).mkdirs();
					}
					//临时目录
					String tempPath1 = tempPath+"/"+newFileName;
					tempPath = tempPath+"/"+UUID.randomUUID();
					if(!new File(tempPath).exists()){
						new File(tempPath).mkdirs();
					}
					//临时Ca
					Ca tempCa = new Ca();
					try {
//						multipartFile.transferTo(new File(tempPath1));
						FileUtils.copyFile(new File(tempRestore), new File(tempPath1));
						//输出tempPath路径
						ZipOrRarUtil.unzipCa(tempPath1, tempPath, fileType, tempCa, "");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						if (ca.getRealFiles() != null) {
							List<com.brainsoon.semantic.ontology.model.File> oldFiles = ca.getRealFiles();
							Map<String, String> dirMap = new TreeMap<String, String>();
							Map<String, List<String>> md5Map = new HashMap<String, List<String>>();
							for (com.brainsoon.semantic.ontology.model.File oldFile : oldFiles) {
								String isDir = oldFile.getIsDir();
								if ("1".equals(isDir)) {
									String id = oldFile.getId();
									id = id.replaceAll("\\\\", "/");
									if (id.endsWith("/")) {
										id = id.substring(0, id.length() - 1);
									}
									logger.info(id + "==========" + oldFile.getName());
									dirMap.put(id, oldFile.getName());
								}
							}
							Set<String> set = new HashSet<String>();
							for (String key : dirMap.keySet()) {
								String value = dirMap.get(key);
								if (key.indexOf("/") > 0) {
									String name = dirMap.get(key);
									String lastDir = key.substring(0, key.lastIndexOf("/"));
									String lastName = dirMap.get(lastDir);
									if (StringUtils.isNotBlank(lastName)) {
										dirMap.put(key, lastName + "/" + name);
										set.add(lastName + "/" + name);
									}
								} else {
									set.add(value);
								}
							}
							for (com.brainsoon.semantic.ontology.model.File oldFile : oldFiles) {
								String isDir = oldFile.getIsDir();
								if ("2".equals(isDir)) {
									String tempPid = oldFile.getPid();
									List<String> md5List = null;
									if ("-1".equals(tempPid)) {
										md5List = md5Map.get(tempPid);
										if (md5List == null) {
											md5List = new ArrayList<>();
										}
										md5List.add(oldFile.getMd5());
										md5Map.put("-1", md5List);
									} else {
										tempPid = tempPid.replaceAll("\\\\", "/");
										if (tempPid.endsWith("/")) {
											tempPid = tempPid.substring(0,
													tempPid.length() - 1);
										}
										md5List = md5Map.get(dirMap.get(tempPid));
										if (md5List == null) {
											md5List = new ArrayList<>();
										}
										md5List.add(oldFile.getMd5());
										md5Map.put(dirMap.get(tempPid), md5List);
									}
								}
							}
							parentPath = FILE_ROOT+parentPath;
							ca = ResUtils.getOverFileLists(parentPath, new File(tempPath), nodeId+"//",ca, set, md5Map, "", "");
						}
//						parentPath = FILE_ROOT+parentPath;
//						ca = ResUtils.getFileLists(parentPath, new File(tempPath), nodeId+"//", ca, "", false);
					} catch (IOException e) {
						e.printStackTrace();
					}
					String paraJson = gson.toJson(ca);
					String result = http.postJson(PUBLISH_OVERRIDE_URL, paraJson);
					if(ca.getRealFiles()!=null&&!ca.getRealFiles().isEmpty()){
						JSONObject ztreeObj = new JSONObject();
						JSONArray ztreeArray = new JSONArray();
						resourceDetail = http.executeGet(WebappConfigUtil
								.getParameter("PUBLISH_DETAIL_URL")
								+ "?id="+objectId);
						Ca caNow = gson.fromJson(resourceDetail, Ca.class);
						List<com.brainsoon.semantic.ontology.model.File> nowRealFiles = caNow.getRealFiles();
						try {
							if (nowRealFiles != null && nowRealFiles.size() > 0) {
								DoFileQueueList doFileList = ResUtils.converPath(nowRealFiles,caNow.getObjectId());
								logger.debug("文件转换-----111111111111-------------------");
								if(doFileList !=null && doFileList.getDoFileQueueList().size()>0){
									//result = http.postJson(PUBLISH_FILE_WRITE_QUEUE,gson.toJson(doFileList));
									resConverfileTaskService.insertQueue(doFileList);
								}
								logger.debug("文件转换结束 -----end-------------------");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						for(int j=0;j<nowRealFiles.size();j++){
							com.brainsoon.semantic.ontology.model.File file = nowRealFiles
									.get(j);
							if (file.getId() != null) {
								nodeId = file.getId();
								nodeId = nodeId.replaceAll("\\\\", "/");
								nodeId = replace(nodeId);
								ztreeObj.put("nodeId", nodeId);
							}
							if (file.getPid() != null) {
								pid = file.getPid();
								pid = pid.replaceAll("\\\\", "/");
								pid = replace(pid);
								ztreeObj.put("pid", pid);
							}
							if (file.getName() != null) {
								fileName = file.getName();
								fileName = replace(fileName);
								ztreeObj.put("name", fileName);
							}
							if (file.getPath() != null) {
								String path = file.getPath();
								path = path.replaceAll("\\\\", "/");
								path = replace(path);
								ztreeObj.put("path", path);
							}
							if (file.getObjectId() != null) {
								String object = file.getObjectId();
								object = replace(object);
								ztreeObj.put("object", object);
							}
							if (file.getIsDir() != null) {
								String isDir = file.getIsDir();
								isDir = replace(isDir);
								ztreeObj.put("isDir", isDir);
							}
							if (file.getMd5() != null) {
								md5 = file.getMd5();
								md5 = replace(md5);
								ztreeObj.put("Md5", md5);
							}
							ztreeArray.add(ztreeObj);
						}
						ztreeArray.toString();
						rtn.put("ztreeJson", ztreeArray);
					}
					rtn.put("status", "0");
					rtn.put("noCompress", "1");
				}
		}else{
			//单个文件存储
			try {
				//存储文件
				restore = new File(absPath);
				if(!restore.exists()){
					restore.mkdirs();
				}
				com.brainsoon.semantic.ontology.model.File uplodeFile = new com.brainsoon.semantic.ontology.model.File();
				SemanticResponse returnValue = null;
				
				if ("true".equals(isCover)) {//文件作为封面
					if (!absPath.endsWith("/")) {
						absPath = absPath+"/";
					}
					if (nodeId.toLowerCase().contains("cover")) {
	        			if ("jpg".equals(fileType) || "png".equals(fileType)) {
	        				//处理原来的cover文件
	        				HttpClientUtil http = new HttpClientUtil();
	        				Gson gson = new Gson();
	        				String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
	        				Ca returnCa = gson.fromJson(resourceDetail, Ca.class);//保存完后返回的Ca
	        				List<com.brainsoon.semantic.ontology.model.File> realFiles = returnCa.getRealFiles();
	        				for (com.brainsoon.semantic.ontology.model.File file2 : realFiles) {
								if ("2".equals(file2.getIsDir()) && file2.getPid().contains("cover")) {
									if ("cover.jpg".equals(file2.getName())) {
										//删除资源文件
										String realPath = FILE_ROOT + file2.getPath();
										String minPath = (FILE_ROOT + file2.getPath()).replace("fileRoot", "viewer").replace("cover.jpg", "cover_min.jpg");
										File file = new File(realPath);
										File minFile = new File(minPath);
										if (file.exists()) {
											FileUtils.forceDelete(file);
											logger.info("删除原来资源封面--文件--realPath："+realPath);
										}
										if (minFile.exists()) {
											FileUtils.forceDelete(minFile);
											logger.info("删除原来资源封面--说略图--minPath："+minPath);
										}
										
										/*//删除jeno中的信息
										String result = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DEL_NODE_URL")
												+ "?caId=" + file2.getCaId() + "&nodeId=" + file2.getObjectId());
										logger.info("删除原来资源封面--jeno--result："+result);*/
										
										
										//将上传的文件作为封面 并获取缩略图
				        				File tempFile = new File(tempRestore);
				        				File newCoverfile = new File(absPath + File.separator + "cover.jpg");
				        				File newMinFile = new File(absPath.replace("fileRoot", "viewer") + File.separator +"cover_min.jpg");
										if (!newCoverfile.exists() && tempFile.exists()) {
											try {
												logger.info("新的cover文件 上传文件"+tempRestore);
												FileUtils.copyFile(tempFile, file);
												logger.info("新的cover文件 file新路径"+file.getAbsolutePath()); 
												UserInfo userInfo = LoginUserUtil.getLoginUser();
												String userId = userInfo.getUserId() + "";
												String time = new Date().getTime()+"";
												String path = file.getAbsolutePath().replaceAll("\\\\", "/").replace(FILE_ROOT, "");
												uplodeFile.setPath(path);
												uplodeFile.setMd5(md5);
												uplodeFile.setCaId(objectId);
												uplodeFile.setAliasName("cover.jpg");
												uplodeFile.setName("cover.jpg");
												uplodeFile.setCreate_time(time);
												uplodeFile.setCreator(userId);
												uplodeFile.setModified_time(time);
												uplodeFile.setModifieder(userId);
												uplodeFile.setVersion("01");
												uplodeFile.setFileByte(fileByte+"");
												uplodeFile.setIsDir("2");
												if(!nodeId.endsWith("/")){
													nodeId = nodeId + "/";
												}
												uplodeFile.setId(nodeId+"cover.jpg");
												uplodeFile.setPid(nodeId);
												uplodeFile.setFileType(fileType);
												uplodeFile.setObjectId(file2.getObjectId());
												returnValue = publishResService.saveFile(uplodeFile);
												logger.info("新的cover文件 修改jeno信息"+returnValue.getObjectId());
											} catch (Exception e) {
												e.printStackTrace();
												throw new ServiceException("cover封面改名出错 ！");
											}
											try {
												if (!minFile.getParentFile().exists()) {
													FileUtils.forceMkdir(minFile.getParentFile());
												}
												ImageUtils.zoomImg(file.getAbsolutePath(), minFile.getAbsolutePath(),60, 80);
											} catch (Exception e) {
												e.printStackTrace();
												throw new ServiceException("获取封面缩略图出错 ！");
											}
										}else if(!minFile.exists()){
											try {
												if (!minFile.getParentFile().exists()) {
													FileUtils.forceMkdir(minFile.getParentFile());
												}
												ImageUtils.zoomImg(file.getAbsolutePath(), minFile.getAbsolutePath(),60, 80);
												logger.info("【ResUtils】  修改cover封面图片 modifyCoverImage()->>>封面缩略图路径："+minFile.getAbsolutePath());
											} catch (Exception e) {
												e.printStackTrace();
												throw new ServiceException("获取封面缩略图出错 ！");
											}
										}
										
									}
								}
							}
	        				
	        				
	        			}
	        		}
					
				}else {
					
					if(absPath.endsWith("/")){
						absPath = absPath+newFileName;
					}else{
						absPath = absPath+"/"+newFileName;
					}

					FileUtils.copyFile(new File(tempRestore), new File(absPath));
//					multipartFile.transferTo(new File(absPath));
					UserInfo userInfo = LoginUserUtil.getLoginUser();
					String userId = userInfo.getUserId() + "";
					String time = new Date().getTime()+"";
					String path = absPath.replace(FILE_ROOT, "");
					uplodeFile.setPath(path);
					uplodeFile.setMd5(md5);
					uplodeFile.setCaId(objectId);
					uplodeFile.setAliasName(newFileName);
					uplodeFile.setName(newFileName);
					uplodeFile.setCreate_time(time);
					uplodeFile.setCreator(userId);
					uplodeFile.setModified_time(time);
					uplodeFile.setModifieder(userId);
					uplodeFile.setVersion("01");
					uplodeFile.setFileByte(fileByte+"");
					uplodeFile.setIsDir("2");
					if(!nodeId.endsWith("/")){
						nodeId = nodeId+"/";
					}
					uplodeFile.setId(nodeId+newFileName);
					uplodeFile.setPid(nodeId);
					uplodeFile.setFileType(fileType);
					
					returnValue = publishResService.saveFile(uplodeFile);
				}
				
				
				if(returnValue.getState() == 0){
					Gson gson = new Gson();
					HttpClientUtil http = new HttpClientUtil();
					String fileObjectId = returnValue.getObjectId();
					List<com.brainsoon.semantic.ontology.model.File> realFiles = new ArrayList<com.brainsoon.semantic.ontology.model.File>();
					uplodeFile.setObjectId(fileObjectId);
					realFiles.add(uplodeFile);
					logger.debug("文件转换----------start--------------");
					if (realFiles != null && realFiles.size() > 0) {
						DoFileQueueList doFileList = ResUtils.converPath(realFiles,
								returnValue.getObjectId());
						logger.debug("文件转换-----111111111111-------------------");
						if(doFileList !=null && doFileList.getDoFileQueueList().size()>0){
							//result = http.postJson(PUBLISH_FILE_WRITE_QUEUE,gson.toJson(doFileList));
							resConverfileTaskService.insertQueue(doFileList);
						}
						logger.debug("文件转换结束 -----end-------------------");
					}
					rtn.put("nodeId", uplodeFile.getId());
					rtn.put("path", uplodeFile.getPath());
					rtn.put("parentPath", nodeId);
					rtn.put("fileName", newFileName);
					rtn.put("object", fileObjectId);
					rtn.put("Md5", md5);
					rtn.put("noCompress", "0");
					rtn.put("status", "0");
				}else{
					rtn.put("status", "-1");
				}
				rtn.put("isRepeat", isRepeat);
				return rtn;
			} catch (Exception e) {
				logger.error(e.getMessage());
				rtn.put("status", "-1");
			}
		}
		} else {
			rtn.put("status", "-1");
		}
		rtn.put("isRepeat", isRepeat);
		return rtn;
	}
	/**
	 * 将json串中的特殊字符替换成空
	 */
	public String replace(String name){
		if(name.contains("'")){
			name = name.replaceAll("'", "’");
		}
		return name;
	}
	/**
	 * 下载资源等待
	 * @param request
	 * @param response
	 * @param ids
	 */
	@SuppressWarnings("finally")
	@RequestMapping(value =baseUrl+"downloadPublishRes", method = {RequestMethod.POST })
	public  @ResponseBody String downloadPublishRes(HttpServletRequest request,HttpServletResponse response,@RequestParam("gotoFtpHttp") String gotoFtpHttp){
		Gson gson = new Gson();
		SearchParamCa spc = gson.fromJson(gotoFtpHttp, SearchParamCa.class);
		String ids = "";
		String encryptPwd = "";
		String ftpFlag = "";
		String isComplete = "";
		if(spc!=null){
			ids = spc.getIds();
			encryptPwd = spc.getEncryptPwd();
			ftpFlag = spc.getFtpFlag();
			isComplete = spc.getIsComplete();
		}
		Date date = new Date();
		String time =  DateUtil.convertDateToString("yyyyMMddHHmmss",date);
//		String parentPath = request.getParameter("parentPath");
		String isOk = "";
		try {
			String encryptZip =time+"/";
			//http下载
			if(ftpFlag.equals("1")){
				isOk = publishResService.downloadBookRes(request, encryptZip, ids, encryptPwd,ftpFlag,isComplete);
				if(StringUtils.isNotBlank(isOk)){
					isOk = isOk +","+ftpFlag;
				}
			}else{
				//ftp下载
				isOk = publishResService.createHttpFtpDownload(ids, encryptPwd, ftpFlag, encryptZip,isComplete);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			return isOk;
		}
	}
	

	/**
	 * 按照分页导出资源文件(查询统计中使用)
	 * fengda 2015年11月9日
	 * @param request
	 * @param response
	 * gotoFtpHttp   页面的查询条件
	 * ftpFlag ：2  标记为ftp下载
	 * @return
	 */
	@RequestMapping(value =baseUrl+"downloadPageResource", method = {RequestMethod.POST })
	@ResponseBody
	public String downloadPageResource(HttpServletRequest request,HttpServletResponse response){
		
		String gotoFtpHttp = request.getParameter("gotoFtpHttp");
		String startPage = "";      //  导出文件的起始页
		String endPage = "";		//  导出文件的结束页
		String pageSize = "";		//	每页显示的记录的条数
		String targetNames = "";	// 此方法中没有作用，但因为service是公用的
		String result = "";			// 返回资源结果集的字符串
		String publicType = "";		//	资源类型
		String encryptPwd = "";		//	加密密匙
		String isComplete ="";		//	是否进行压缩  1是  2 否
		String data = "";			//	最后返回值，供前台判断
		List<Ca> cas = new ArrayList<Ca>();
		Date date = new Date();
		String time =  DateUtil.convertDateToString("yyyyMMddHHmmss",date);
		String encryptZip =time + "/";
		
		Gson gson = new Gson();
		SearchParamCa spc = gson.fromJson(gotoFtpHttp, SearchParamCa.class);
		QueryConditionList conditionList = getQueryConditionList();
		if(spc != null){
			startPage = spc.getPage();
			endPage = spc.getPage1();
			pageSize = spc.getSize()+"";
			encryptPwd = spc.getEncryptPwd();
			isComplete = spc.getIsComplete();
			publicType = spc.getPublishType();
		}
		
		if(StringUtils.isNotBlank(publicType)){
			request.setAttribute("publishType", publicType);
		}
		
		String flag = request.getParameter("ftpFlag");
		String isOk = "";
		String ids = "";
		
		try{
			//计算共导出多少条记录
			Long num = (long) ((Integer.parseInt(endPage) - Integer.parseInt(startPage)+1)*Integer.parseInt(pageSize));
			//返回要导出的资源列表转换成String类型
			result= publishResService.queryResource4PageByParam(request, Integer.parseInt(startPage), num, WebappConfigUtil.getParameter("PUBLISH_QUERY_URL"),pageSize,conditionList,targetNames);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(StringUtils.isNotBlank(result)){
			try{
				SearchResultCa caList = gson.fromJson(result, SearchResultCa.class);
				//获取资源的列表
				cas = caList.getRows();
				//根据资源列表信息，循环资源根据资源id查询详细获取资源下的文件信息
				//data = searchIndexService.createByPageFtpDownload(cas, flag, encryptZip, encryptPwd, isComplete);
				for (Ca ca : cas) {
					ids += ca.getObjectId()+",";
				}
				if(StringUtils.isNotBlank(ids)){
					ids = ids.substring(0, ids.length()-1);
					isOk = publishResService.createHttpFtpDownload(ids, encryptPwd, flag, encryptZip,isComplete);
					//isOk = publishResService.downloadBookRes(request, encryptZip, ids, encryptPwd,flag,isComplete);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		 return data;
	}
	/**
	 * 下载资源
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping(baseUrl + "downloadFile")
	public  void downloadFile(HttpServletRequest request,HttpServletResponse response){
		String zipName = request.getParameter("zipName");
		String flag = request.getParameter("flag");
		if(StringUtils.isNotBlank(flag)){
			zipName = FILE_ROOT+zipName;
		}
		try {
			zipName=  URLDecoder.decode(zipName,"UTF-8");
			resourceService.downloadFile(request, response, zipName,
					false);
			if(StringUtils.isNotBlank(flag) && StringUtils.isNotBlank(zipName)){
				zipName = zipName.replaceAll("\\\\", "/");
				String fileName = zipName.substring(zipName.lastIndexOf("/")+1,zipName.length());
				SysOperateLogUtils.addLog("res_file_down", fileName,
						LoginUserUtil.getLoginUser());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 更新节点
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping(value = baseUrl + "updateNode")
	public @ResponseBody HashMap<String, Object> updateNode(
			HttpServletRequest request, @RequestParam("title") String title) {
		HashMap<String, Object> rtn = new HashMap<String, Object>();
		String parentPath = request.getParameter("parentPath");
		String nodeId = request.getParameter("nodeId");
		String MD5 = request.getParameter("MD5");
		String objectId = request.getParameter("objectId");
		String jsonFile = request.getParameter("jsonFile");
		String newFilePath = request.getParameter("newFilePath");
		File restore = null;
		int status = 0;
		String md5 = "";
		String fileObjectId = request.getParameter("fileObjectId");
		try {
			if (parentPath != null) {
				parentPath = URLDecoder.decode(parentPath, "utf8");
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (parentPath != null) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
			MultipartFile multipartFile = null;
			for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
				multipartFile = set.getValue();// 文件名
			}
			String uploadFile = "";
			String fileName = multipartFile.getOriginalFilename();
			if (multipartFile.isEmpty()) {
				status = 1;
			} else {
				uploadFile = FILE_ROOT+parentPath + File.separator + fileName;
				restore = new File(uploadFile);
				try {
					multipartFile.transferTo(restore);
					md5 = MD5Util.getFileMD5String(restore);
				} catch (Exception e) {
					status = -1;
				}
			}
			rtn.put("fileName", fileName);
			rtn.put("status", status);
			rtn.put("Md5", md5);
		}
		String treeEditOldName = request.getParameter("treeEditOldName");
		String fileFlag = request.getParameter("fileFlag");
		String checkMd5[] = MD5.split(",");
		boolean isRepeat = false;
		for (int i = 0; i < checkMd5.length; i++) {
			if (checkMd5[i].equals(md5)) {
				isRepeat = true;
			}
		}
		if (!isRepeat) {
			try {
				objectId = publishResService.updateNode(jsonFile,
						treeEditOldName, fileFlag, restore, fileObjectId,
						nodeId, objectId, newFilePath, title);
				String Id[] = objectId.split(",");
				objectId = Id[0];
				String isDir = Id[1];
				String path = Id[2];
				rtn.put("objectId", objectId);
				rtn.put("nodeId", UUID.randomUUID().toString());
				rtn.put("isDir", isDir);
				rtn.put("path", path);
			} catch (Exception e) {
				logger.error(e.getMessage());
				objectId = "-1";
			}
		}
			rtn.put("isRepeat", isRepeat);
		
		return rtn;
	}
	
	/**
	 * 更新节点
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping(value = baseUrl + "renameNode")
	public @ResponseBody String renameNode(HttpServletRequest request) {
		String newName = request.getParameter("newName");
		String fileObjectId = request.getParameter("fileObjectId");
		String caObjectId = request.getParameter("caObjectId");
		String oldName = request.getParameter("oldName");
		try {
//			newName=  URLDecoder.decode(newName,"UTF-8");
			newName = URLEncoder.encode(newName, "UTF-8");
			return publishResService.doRenameNode(caObjectId, fileObjectId, newName, oldName);
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
	}
	
	public String createRandom(List dirNames,String level,String fileType){
		Random random = new Random();
        int value = random.nextInt(1000);
        String dirName = level+value;
        if(StringUtils.isNotBlank(fileType)){
        	dirName+="."+fileType;
        }
        if(dirNames.contains(dirName)){
        	return createRandom(dirNames,level,fileType);
        }
        return dirName;
	}
	/**
	 * 增加节点
	 * @param request
	 * @param response
	 * @param ids
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = baseUrl + "addNode")
	public @ResponseBody HashMap<String, String> addNode(HttpServletRequest request) {
		HashMap<String, String> returnValue = new HashMap<String, String>();
		String caObjectId = request.getParameter("caObjectId");
		String rootPath = request.getParameter("rootPath");
		String name = request.getParameter("name");
		String parentPath = request.getParameter("parentPath");
		String time = new Date().getTime()+"";
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String userId = userInfo.getUserId() + "";
		String level = "1";
		try {
			String absPath="";
			String relatePath = "";
			if(StringUtils.isBlank(parentPath)){
				absPath = FILE_ROOT+rootPath;
				relatePath = rootPath;
			}else{
				absPath = FILE_ROOT+parentPath;
				String tempPath = parentPath.replace(rootPath+"/", "");
				if(StringUtils.isNotBlank(tempPath) && tempPath.indexOf("/")>0){
					String[] dirValue = tempPath.split("/");
					level = (dirValue.length+1)+"";
				}else{
					level = "2";
				}
				relatePath = parentPath;
			}
			if(!relatePath.endsWith("/")){
				relatePath+="/";
			}
			File file = new File(absPath);
			String path = "";
			String id = "";
			if(file.exists()){
				File[] childFiles =  file.listFiles();
				if(childFiles!=null && childFiles.length>0){
					List dirNames = new ArrayList();
					for(File childFile:childFiles){
						if(childFile.isDirectory()){
							dirNames.add(childFile.getName());
						}
					}
					path =  createRandom(dirNames,level,null);
					File newDir = new File(absPath+File.separator+path);
					if(!newDir.exists()){
						newDir.mkdirs();
					}
				}else{
					path = level+"1";
					File newDir = new File(absPath+File.separator+path);
					if(!newDir.exists()){
						newDir.mkdirs();
					}
				}
			}
			com.brainsoon.semantic.ontology.model.File uplodeFile = new com.brainsoon.semantic.ontology.model.File();
			uplodeFile.setPath(relatePath+path);
			uplodeFile.setCaId(caObjectId);
			uplodeFile.setAliasName(path);
			uplodeFile.setName(name);
			uplodeFile.setCreate_time(time);
			uplodeFile.setCreator(userId);
			uplodeFile.setModified_time(time);
			uplodeFile.setModifieder(userId);
			uplodeFile.setVersion("01");
			uplodeFile.setIsDir("1");
			if(StringUtils.isBlank(parentPath)){
				id = path;
				uplodeFile.setId(id);
				uplodeFile.setPid("-1");
			}else{
				if(parentPath.endsWith("/")){
					id = (parentPath+path).replace(rootPath+"/", "");
				}else{
					id = (parentPath+"/"+path).replace(rootPath+"/", "");
				}
				uplodeFile.setId(id);
				String pid = parentPath.replace(rootPath+"/", "");
				uplodeFile.setPid(pid);
			}
			SemanticResponse rtn = publishResService.saveFile(uplodeFile);
			if(rtn.getState() == 0){
				String objectId = rtn.getObjectId();
				returnValue.put("nodeId", id);
				returnValue.put("path", relatePath+path);
				returnValue.put("objectId", objectId);
				returnValue.put("status", "1");
			}else{
				returnValue.put("status", "-1");
			}
			return returnValue;
		} catch (Exception e) {
			e.printStackTrace();
			returnValue.put("status", "-1");
			returnValue.put("msg", e.getMessage());
			return returnValue;
		}
	}
//	 /**
//		 * 执行删除操作
//		 * @param response
//		 * @param ids
//		 * @throws IOException
//		 */
//	@RequestMapping(baseUrl + "delRes")
//	public void delRes(HttpServletResponse response,@RequestParam("ids") String ids) throws IOException{
//		publishResService.deleteByIds(ids);
//		outputResult("删除成功");
//	}
	/**
	 * 执行强制删除操作
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(value = baseUrl + "enforceDeletes", method = {RequestMethod.POST })
	public @ResponseBody Map<String,Object> enforceDelete(HttpServletResponse response,@RequestParam("enforceDel") String enforceDel) throws IOException{
		Map<String,Object> rtn = new HashMap<String, Object>();
		Gson gson = new Gson();
		SearchParamCa spc = gson.fromJson(enforceDel, SearchParamCa.class);
		String ids = "";
		if(spc!=null){
			ids = spc.getIds();
		}
		String orderId = resOrderService.canDelByResId(ids);
		String status = "1";
		if(orderId.equals("")){
			try {
				publishResService.deleteByIds(ids);
			} catch (Exception e) {
				// TODO: handle exception
				status = "0";
				e.printStackTrace();
			}
		}else{
			status = "0";
			rtn.put("status", status);
		}
		rtn.put("status", status);
		return rtn;
	}
	@RequestMapping(value = baseUrl + "deleteNode")
	@ResponseBody
	public String deleteNode(HttpServletRequest request,@RequestParam("caId") String caId,
			@RequestParam("path") String path) {
		logger.debug("******run at saveRes***********");
		String succ = "0";
		try {
			String treeDeleteName = request.getParameter("oldName");
			String deleteFile = request.getParameter("deleteType");
			String fileObjectId = request.getParameter("fileObjectId");
			publishResService.deleteNode(caId, fileObjectId, path,treeDeleteName,deleteFile);
		} catch (Exception e) {
			logger.error(e.getMessage());
			succ = "-1";
		}
		return succ;
	}
	 
	
	 @RequestMapping(value=baseUrl+"toSelFile")
	 public String toSelFile(@RequestParam("nodeId") String nodeId,@RequestParam("parentName") String parentName,@RequestParam("parentPath") String parentPath,ModelMap model) {
	    	 logger.debug("*********toSelFile********** ");
	    	 System.out.println("parentPath **********"+parentPath);
	    	 try {
				parentPath=URLDecoder.decode(parentPath,"UTF-8");
				parentName=URLDecoder.decode(parentName,"UTF-8");
		    	model.addAttribute("nodeId", nodeId);
		    	String realPath=FILE_ROOT+parentPath;
		    	File realFile=new File(realPath);
		    	if(!realFile.exists()){
		    		realFile.mkdirs();
		    	}
		    	model.put("parentPath", realPath);
		    	model.put("parentName", parentName);
			} catch (Exception e) {
				logger.error(e);
			} 
	  	    return baseUrl+"selFile";
	 }
	 
	
	/**
	 * 上传文件到临时目录,返回UUID标识,为文件夹
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(baseUrl+"uploadBookFile")
	public @ResponseBody HashMap<String, Object> uploadBookFile(HttpServletRequest request, HttpServletResponse response,ModelMap model){
		HashMap<String, Object> rtn = new HashMap<String, Object>();
		int status = 0;
		String parentPath=request.getParameter("parentPath");
		try {
			parentPath=URLDecoder.decode(parentPath,"utf8"); 
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		MultipartFile multipartFile = null;
		for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
			multipartFile = set.getValue();// 文件名
		}
		String uploadFile = "";
		String fileName = multipartFile.getOriginalFilename();
		if (multipartFile.isEmpty()) {
			status = 1;
		} else {
			uploadFile = parentPath + File.separator + fileName;
			File restore = new File(uploadFile);
			try {
				multipartFile.transferTo(restore);
			} catch (Exception e) {
				status = -1;
			}
		}
		rtn.put("fileName", fileName);
		rtn.put("filePath", uploadFile.replace(File.separator, "/").replace(FILE_ROOT, ""));
		rtn.put("status", status);
		return rtn;
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
	public void saveTarget(HttpServletRequest request, HttpServletResponse response,ModelMap model,@RequestParam("selectResId") String selectResId,@RequestParam("canSelectTargetIds") String canSelectTargetIds,@RequestParam("hasSelectTargetIds") String hasSelectTargetIds,@RequestParam("resIds") String resIds) {
		logger.debug("******run at saveRes***********");
		resTargetService.doBatchSaveDeleteTarget(selectResId, canSelectTargetIds, hasSelectTargetIds,resIds);
	}
	/**
	 * 下载模板
	 * @return
	 * @throws IOException
	 */
    @RequestMapping(value = baseUrl + "downloadTemplete")
    public ResponseEntity<byte[]> download(HttpServletRequest request) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        String publishType=request.getParameter("publishType");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = SysResTypeCacheMap.getValue(publishType)+"资源模板.xls";
        if (request.getHeader("User-Agent").toLowerCase().indexOf("filefox") > 0) {
        	filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
        }else{
        	filename = URLEncoder.encode(filename, "UTF-8");
        }
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(batchImportResService.getExcelTemplete(publishType)),headers, HttpStatus.OK);
    }
	/**
	 * 导出元数据生成excel文件Ajax传参返回excel路径
	 * 
	 * @return
	 * @throws Exception 
	 */
    @RequestMapping(value = baseUrl + "getExportExcel", method = {RequestMethod.POST })
	@ResponseBody
	public String getExportExcel(HttpServletRequest request,
			HttpServletResponse response,@RequestParam("searchParamCa") String searchParamCa) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String filename = URLEncoder.encode("元数据导出.xls", "UTF-8");
		headers.setContentDispositionFormData("attachment", filename);
		
		Gson gson = new Gson();
		SearchParamCa spc = gson.fromJson(searchParamCa, SearchParamCa.class);
		QueryConditionList conditionList = getQueryConditionList();
		String ids = "";
		String batch = "";
		String level = "";
		String publishType = "";
		String page = "";
		String page1 = "";
		String pageSize = "";
		String targetNames = "";
		String targetField = "";
		String queryModel = "";
		String field = "";
		String fieldValue = "";
		String status = "";
		Long resSizeNum = 0L;
		if(spc != null){
			ids = spc.getIds();
			batch = spc.getBatch();
			level = spc.getLevel();
			publishType = spc.getPublishType();
			page = spc.getPage();
			page1 = spc.getPage1();
			pageSize = spc.getSize() + "";
			targetNames = spc.getTargetNames();
			targetField = spc.getTargetField();
			field = spc.getField();
			fieldValue = spc.getFieldValue();
			queryModel = spc.getQueryModel();
			status = spc.getStatus();
			if(StringUtils.isNotBlank(publishType))
				request.setAttribute("publishType", publishType);
			if(StringUtils.isNotBlank(status))
				request.setAttribute("status", status);
			if(StringUtils.isNotBlank(field)&&StringUtils.isNotBlank(fieldValue)){
				targetNames = fieldValue;
				targetField = field;
//				conditionList.addCondition(new QueryConditionItem(field, Operator.LIKE, fieldValue));
			}
			if(StringUtils.isNotBlank(queryModel)){
				try {
					queryModel = URLDecoder.decode(queryModel,"utf-8"); 
					queryModel = URLDecoder.decode(queryModel,"utf-8"); 
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} 
				String[] queryModelParams = queryModel.split("&");
				for (int i = 0; i < queryModelParams.length; i++) {
					String[] queryValueParams = queryModelParams[i].split("==");
					String queryValueStr = queryValueParams[1];
					queryValueStr = queryValueStr.substring(1, queryValueStr.length()-1);
					String[] queryValue = StringUtil.strToArray(queryValueStr, ",");
					setQueryConditionListParam(conditionList, queryValueParams[0], queryValue, "1");
				 }
			}
		}
		String resSize = "";
		List<SysParameter> sr = sysParameterService.findParaValue("resDownloadSize");
		if (sr != null && sr.size() > 0) {
			if (sr.get(0) != null && sr.get(0).getParaValue() != null) {
				resSize = sr.get(0).getParaValue();
			}
		}
		if(StringUtils.isNotBlank(resSize)){
			resSizeNum = Long.parseLong(resSize);
		}
	try {
			if(StringUtils.isNotBlank(targetNames)&&StringUtils.isNotBlank(targetField)){
		 		targetNames=  URLDecoder.decode(targetNames,"UTF-8");
		 		targetNames = targetField +","+targetNames;
		    }
		 } catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		}
		HttpClientUtil http = new HttpClientUtil();
		List<Ca> cas = new ArrayList<Ca>();
		String result= "";
		//用来判断是否下载数超过字典定义大小
		boolean isNoDown = true;
		if (ids != null && !"".equals(ids)) {
			if (ids.endsWith(",")) {
				ids = ids.substring(0, ids.length()-1);
			}
			Long idsLength = (long) ids.split(",").length;
			//用接口批量查询Ca（参数Ids） 2015年8月20日 10:42:41 huangjun
			if(idsLength<=resSizeNum){
				SearchParamCa searchParamCa1 = new SearchParamCa();
				searchParamCa1.setIds(ids);
				ids = gson.toJson(searchParamCa1);
				String resource = http.postJson(PUBLISH_DETAILLIST_URL, ids);
				CaList caList = gson.fromJson(resource, CaList.class);
				cas = caList.getCas();
			}else{
				isNoDown = false;
			}
		}else{
			if(!"".equals(page)&&!"".equals(page1)&&!"".equals(pageSize)){
				//计算共导出多少条记录
				Long num = (long) ((Integer.parseInt(page1) - Integer.parseInt(page)+1)*Integer.parseInt(pageSize));
				if(StringUtils.isNotBlank(resSize)&&num<=Integer.parseInt(resSize)){
					result= publishResService.queryResource4PageByParam(request, Integer.parseInt(page), num, WebappConfigUtil.getParameter("PUBLISH_QUERY_URL"),pageSize,conditionList,targetNames);
				}else{
					isNoDown = false;
				}
//			}else if(!"".equals(page)&&!"".equals(pageSize)){
//				result= publishResService.queryResource4PageByParam(request, Integer.parseInt(page), Integer.parseInt(resSize), WebappConfigUtil.getParameter("PUBLISH_QUERY_URL"),pageSize,conditionList,targetNames);
			}else{
				result= publishResService.queryResource4PageByParam(request, 1, Long.parseLong(result), WebappConfigUtil.getParameter("PUBLISH_QUERY_URL"),pageSize,conditionList,targetNames);
			}
			if(StringUtils.isNotBlank(result)){
			 SearchResultCa caList = gson.fromJson(result, SearchResultCa.class);
			 cas = caList.getRows();
			}
		}
		File excelFile = null;
		String returnPath = "";
		if(isNoDown){
			excelFile = ExcelUtil.createExcelByRes(cas,level,publishType);
			returnPath = excelFile.getAbsolutePath();
			if(StringUtils.isNotBlank(batch)) {
				SysOperateLogUtils.addLog("batch_exportRes","批量检索导出元数据", userInfo);
			}else{
				SysOperateLogUtils.addLog("res_exportRes",SysResTypeCacheMap.getValue(publishType)+"导出元数据", userInfo);
			}
		}
		return returnPath;
	}
	
	/**
	 * 导出元数据
	 * 
	 * @return
	 * @throws Exception 
	 */
    @RequestMapping(baseUrl + "getExportExcelDown")
	@ResponseBody
	public ResponseEntity<byte[]> getExportExcelDown(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String excelFilePath = request.getParameter("excelFilePath");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String filename = URLEncoder.encode("元数据导出.xls", "UTF-8");
		headers.setContentDispositionFormData("attachment", filename);
		File excelFile = new File(excelFilePath);
		return new ResponseEntity<byte[]>(
				FileUtils.readFileToByteArray(excelFile), headers,
				HttpStatus.OK);
	}
	
	
	/**
	 * 李明那边详细列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "openList")
	public void openListRes(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入查询方法");
		String id = (String)request.getParameter("sysResMetadataTypeId");
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
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		String startTime = (String)request.getParameter("startTime");
		String endTime = (String)request.getParameter("endTime");
		HttpClientUtil http = new HttpClientUtil();
		String url = WebappConfigUtil.getParameter("PUBLISH_QUERY_URL");
		if(startTime==null) {
			startTime="";
		}
		if(endTime==null) {
			endTime="";
		}
		if(page==null) {
			page="";
		}
		if(size==null) {
			size="";
		}
		
		
		
		//获取当前登录人所属的组织部门和所拥有的数据部门相同的id
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String userIds = userInfo.getDeptUserIds();
		String hql = "";
		//表示当前用户已被授权
		if (userInfo.getIsPrivate() == 1) {
			if(StringUtils.isNotBlank(userIds)){
			}else{
				userIds = userInfo.getUserId()+"";
			}
		}
		if (StringUtils.isNotBlank(userIds)) {
			if(userIds.endsWith(",")){
				userIds = userIds.substring(0,userIds.length()-1);
			}
			hql="&creator=" + userIds;
		}else{
			hql="&creator=-2";
		}
		url+="?publishType="+id+"&metadataMap="+metadataMap+"&createStartTime="+startTime+"&createEndTime="+endTime+"&page="+page+"&size="+size+hql;
		String formList = http.executeGet(url);
		outputResult(formList);
	}
	/**
	 * 查询关联资源列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "listRelationRes")
	public void listRelationRes(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		String returnValue = baseSemanticSerivce.queryRelationsResource4Page(request, conditionList);
		Gson gson = new Gson();
		SearchResultCa list = gson.fromJson(returnValue, SearchResultCa.class);
		if(list !=null){
			List<Ca> cas = list.getRows();
			if(cas!=null && cas.size()>0){
				for(Ca ca:cas){
					if (StringUtils.isNotBlank(ca.getCreateTime())) {
						try {
							ca.setCreateTimeFormat(DateUtil.convertLongToString(ca.getCreateTime()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		outputResult(gson.toJson(list));
	}
	
	/**
	 * 查询资源衍生列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "listReriveRes")
	public void listReriveRes(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		String returnValue = baseSemanticSerivce.queryRerivesResource4Page(
				request, conditionList,"narrow");
		Gson gson = new Gson();
		SearchResultCa list = gson.fromJson(returnValue, SearchResultCa.class);
		if(list !=null){
			List<Ca> cas = list.getRows();
			if(cas!=null && cas.size()>0){
				for(Ca ca:cas){
					if (StringUtils.isNotBlank(ca.getCreateTime())) {
						try {
							ca.setCreateTimeFormat(DateUtil.convertLongToString(ca.getCreateTime()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		outputResult(gson.toJson(list));
	}
	
	/**
	 * 查询上位
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "listBorderRes")
	public void listBorderRes(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		String returnValue = baseSemanticSerivce.queryRerivesResource4Page(
				request, conditionList,"border");
		Gson gson = new Gson();
		SearchResultCa list = gson.fromJson(returnValue, SearchResultCa.class);
		if(list !=null){
			List<Ca> cas = list.getRows();
			if(cas!=null && cas.size()>0){
				for(Ca ca:cas){
					if (StringUtils.isNotBlank(ca.getCreateTime())) {
						try {
							ca.setCreateTimeFormat(DateUtil.convertLongToString(ca.getCreateTime()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		outputResult(gson.toJson(list));
	}
	/**
	 * 添加关联资源
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
	 * 添加来源资源
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "addSourceRes")
	public void addSourceRes(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String relationIds = request.getParameter("relationIds");
		baseSemanticSerivce.addSourceRes(id, relationIds);
	}
	/**
	 * 添加来源资源
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "addReriveRes")
	public void addReriveRes(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String relationIds = request.getParameter("relationIds");
		baseSemanticSerivce.addReriveRes(id, relationIds);
	}
	/**
	 * 删除关联
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "delRelationRes")
	public @ResponseBody String delRelationRes(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String relationIds = request.getParameter("relationIds");
		String stag = baseSemanticSerivce.delRelation(id, relationIds);
		return stag;
	}
	/**
	 * 删除衍生资源
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "delDerivesRes")
	public @ResponseBody String delDerivesRes(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String derivesIds = request.getParameter("derivesIds");
		String stag = baseSemanticSerivce.delDerives(id, derivesIds);
		return stag;
	}
	/**
	 * 删除来源资源
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "delSourceRes")
	public @ResponseBody String delSourceRes(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String sourceIds = request.getParameter("sourceIds");
		String stag = baseSemanticSerivce.delSource(id, sourceIds);
		return stag;
	}
	/**
	 * 生命周期
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "lifeCycle")
	public @ResponseBody LinkedHashMap<String,String> lifeCycle(HttpServletRequest request, HttpServletResponse response) {
		List<SysParameter> lc = sysParameterService.findParaValue("lifeCycle");
		LinkedHashMap<String,String> map =null;
		if(lc!=null && lc.size()>0){
			if(lc.get(0)!=null && lc.get(0).getParaValue()!=null && lc.get(0).getParaValue().equals("1")){
				map = dictNameService.getDictMapByName("生命周期");
			}
		}
		return map;
	}
	/**
	 * 批量标签保存
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value =baseUrl+"beachTargetSave", method = {RequestMethod.POST })
	public @ResponseBody String beachTargetSave(HttpServletRequest request, ModelMap model,@RequestParam("gotoTarget") String gotoTarget) {
		Gson gson = new Gson();
		SearchParamCa spc = gson.fromJson(gotoTarget, SearchParamCa.class);
		String resIds = "";
		String targetName = "";
		String targetField = "";
		if(spc!=null){
			resIds = spc.getIds();
			targetName = spc.getTargetNames();
			targetField = spc.getTargetField();
		}
		String returnValue ="";
		 try {
				targetName=  URLDecoder.decode(targetName,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		HttpClientUtil http = new HttpClientUtil();
		String ids[] = resIds.split(",");
		String resourceDetail ="";
		for(int i=0;i<ids.length;i++){
			Ca ca = new Ca();
			resourceDetail = http.executeGet(WebappConfigUtil
			.getParameter("PUBLISH_DETAIL_URL") + "?id=" + ids[i]);
			 ca = gson.fromJson(resourceDetail, Ca.class);
			 returnValue =  publishResService.updateBeachSaveTarget(ca, targetName,targetField);
			 returnValue = "1";
			 SysOperateLogUtils.addLog("target_publicSave",  MetadataSupport.getTitle(ca)+":"+targetName, LoginUserUtil.getLoginUser());
		}

		return returnValue;
	}
	 /**
		 * 批量导入详细查重
		 * @param request
		 * @param response
		 * @return Map<String,Object>
		 * @throws Exception 
		 */
		@RequestMapping(baseUrl + "importDetailcheckRepeat")
		public @ResponseBody Map<String,Object> importDetailcheckRepeat(HttpServletRequest request, HttpServletResponse response) throws Exception{
			String id = request.getParameter("id");
			String excelPath = request.getParameter("excelPath");
			String publishType = request.getParameter("publishType");
			if(StringUtils.isNotBlank(excelPath)){
			 	   excelPath=  URLDecoder.decode(excelPath,"UTF-8");
			}
			CaList repeatCas = publishResService.beachImportCheckRepeat(id,excelPath,publishType);
			Map<String,Object> rtn = new HashMap<String, Object>();
			int status = 0;//不重复
			if(repeatCas != null && repeatCas.getTotle() > 0){
				status = 1;
				rtn.put("res", repeatCas);
			}
			if(repeatCas.getUploadFilePath()!=null){
				rtn.put("uploadFilePath", repeatCas.getUploadFilePath());
			}
				rtn.put("status", status);
				return rtn;
		}
		/**
		 * 批量详细查重保存
		 * @param request
		 * @param model
		 * @return
		 * @throws Exception 
		 */
		@RequestMapping(value=baseUrl+"beachDetailCheckrepeatSave")
		public @ResponseBody String beachDetailCheckrepeatSave(HttpServletRequest request, ModelMap model) throws Exception {
			String id = request.getParameter("id");
			String excelPath = request.getParameter("excelPath");
			String publishType = request.getParameter("publishType");
			String objectId = request.getParameter("objectId");
			String repeatType = request.getParameter("repeatType");
			String taskId = request.getParameter("taskId");
			Long userId = LoginUserUtil.getLoginUser().getUserId();
			String returnValue ="";
			 try {
				 excelPath=  URLDecoder.decode(excelPath,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			 	Ca ca = new Ca();
			 	if(!"".equals(objectId)){
			 		ca.setObjectId(objectId);
			 	}
			 	ca.setPublishType(publishType);
			 ca = publishResService.installCa(id, ca, excelPath);
			 Date now = new Date();
			 String time = now.getTime() + "";
			 ca.setCreateTime(time);
			 ca.setPublishType(publishType);
			 ca.setCreator(userId+"");
			 String versionField = "";
				List<MetadataDefinition> metadataDefinitions = MetadataSupport.getMetadateDefines(ca.getPublishType());
				for (MetadataDefinition metadataDefinition : metadataDefinitions) {
					if (metadataDefinition.getIdentifier() != null && metadataDefinition.getIdentifier() == 11) {
						versionField = metadataDefinition.getFieldName();
					}
				}
				String resVersion = ca.getMetadataMap().get(versionField);
				if (resVersion == null&&StringUtils.isNotBlank(versionField) || "".equals(resVersion)&&StringUtils.isNotBlank(versionField)) {
					ca.putMetadataMap(versionField, "00");
				}
				String returnObjectId ="";
				if(StringUtils.isNotBlank(versionField)){
					Map<String, Map<String,String>> fileMetadataFlag = null;
					returnObjectId = batchImportResService.saveImportPublishRes(ca,repeatType,fileMetadataFlag,null,null);
				 if(!"".equals(returnObjectId)){
					 publishResService.updateStatus(id,taskId);
				 }
				}
				if(StringUtils.isBlank(versionField)){
					return "版本为空请检查权限设置是否勾选版本项";
				}else{
					return "";
			}
		}
		/**
		 * 批量导入日志，详细页导出数据
		 * 
		 * @return
		 * @throws Exception 
		 */
		@RequestMapping(baseUrl + "beachDetaillLogExportExcel")
		public ResponseEntity<byte[]> beachDetaillLogExportExcel(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			HttpHeaders headers = new HttpHeaders();
			String status=request.getParameter("status");
			String excelPath=request.getParameter("excelPath");
			String id=request.getParameter("id");
			 try {
				 excelPath=  URLDecoder.decode(excelPath,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			String filename = URLEncoder.encode("批量导入日志导出数据.xls", "UTF-8");
			headers.setContentDispositionFormData("attachment", filename);
			return new ResponseEntity<byte[]>(
					FileUtils.readFileToByteArray(resourceService.beachGetDetaillExcel(response,status,excelPath,id)), headers,
					HttpStatus.OK);
		}
		/**
		 * 批量导入日志，详细页xml文件导出数据
		 */
		@RequestMapping(value=baseUrl+"beachDetaillLogExportXml")
		public ResponseEntity<byte[]> beachDetaillLogExportXml(HttpServletRequest request,HttpServletResponse response) throws Exception{
			HttpHeaders headers = new HttpHeaders();
			String status=request.getParameter("status");
			String id=request.getParameter("id");
			String hql1=null;
			if(!"".equals(status) && null!=status){
				hql1="from UploadTaskDetail t where t.task.id="+Integer.parseInt(id)+" and t.status="+Integer.parseInt(status)+" order by t.createTime desc";
			}else{
				hql1="from UploadTaskDetail t where t.task.id="+Integer.parseInt(id)+" order by t.createTime desc";
			}
			List<UploadTaskDetail> lists=resourceService.query(hql1);
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+"\n");
			for(int i=0;i<lists.size();i++){
				UploadTaskDetail uploadTaskDetail = lists.get(i);
				String body = uploadTaskDetail.getBody();
				
				String title =  "";
				try {
					title =  getTitleByBody(body);
				} catch (Exception e) {
					title = "[标题未获取]";
				}
				
				sb.append("<result>"+"\n");
				sb.append("\t"+"<resname>"+title+"</resname>"+"\n");
				sb.append("\t"+"<path>"+uploadTaskDetail.getPaths()+"</path>"+"\n");
				sb.append("\t"+"<status>"+BatchImportDetaillType.getValueByKey(uploadTaskDetail.getStatus())+"</status>"+"\n");
				sb.append("\t"+"<remark>"+uploadTaskDetail.getRemark().replace("第【0】行", "")+"</remark>"+"\n");
				sb.append("</result>"+"\n");
			}
			String files = "标准资源批量导入(xml方式)日志.xml";
			String filename = null;
			String fileEncode = System.getProperty("file.encoding");
			File xmls=new File(new String( (FILE_TEMP+files).getBytes("UTF-8") , fileEncode));
			if(xmls.exists()){
				xmls.delete();
			}
			
			xmls.createNewFile();
			byte bytes[]=new byte[1024];
			bytes=sb.toString().getBytes();
			FileOutputStream fos=new FileOutputStream(xmls);
			fos.write(bytes,0,bytes.length);
			fos.close();
			headers.setContentType(MediaType.TEXT_XML);
			String users =request.getHeader("USER-AGENT");
			if(StringUtils.contains(users, "MSIE")){//IE浏览器
				filename = URLEncoder.encode(files,"UTF8");
            }else if(StringUtils.contains(users, "Mozilla")){//google,火狐浏览器
            	filename = new String(files.getBytes(), "ISO8859-1");
            }else{
            	filename = URLEncoder.encode(files,"UTF8");//其他浏览器
            }
			headers.setContentDispositionFormData("attachment", filename);
			return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(xmls), headers, HttpStatus.OK);
		}
		/**
		 * 文件元数据查询
		 * @param request
		 * @param response
		 * @return Map<String,Object>
		 * @throws Exception 
		 */
		@RequestMapping(baseUrl + "toEditFileRes")
		public String toEditFileRes(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception{
			String fileObjectId = request.getParameter("fileObjectId");
			String publishType = request.getParameter("publishType");
			HttpClientUtil http = new HttpClientUtil();
			String fileDetail = http.executeGet(WebappConfigUtil
					.getParameter("CA_FILERES_DETAIL_URL") + "?id=" + fileObjectId);
			Gson gson = new Gson();
			com.brainsoon.semantic.ontology.model.File fileMetadata = gson.fromJson(fileDetail, com.brainsoon.semantic.ontology.model.File.class);
			model.put("fileMetadata", fileMetadata);
			model.put("fileType", fileMetadata.getFileType());
			model.put("flag", "1");
			model.put("publishType", publishType);
			model.put("fileObjectId", fileObjectId);
			String fileName = fileMetadata.getName();
			fileName = fileName.substring(0,fileName.lastIndexOf("."));
			model.put("fileName", fileName);
			model.put("fileSize", fileMetadata.getFileByte());
			
			return baseUrl+"import/editFileRes";
		}
		/**
		 * 文件元数据保存
		 * @param request
		 * @param response
		 * @return Map<String,Object>
		 * @throws Exception 
		 */
		@RequestMapping(baseUrl + "saveFileMetadata")
		public @ResponseBody Map<String,String> saveFileMetadata(HttpServletRequest request, HttpServletResponse response, ModelMap model,@ModelAttribute("frmWord") com.brainsoon.semantic.ontology.model.File file) throws Exception{
			String fileObjectId = request.getParameter("fileObjectId");
			String fileType = request.getParameter("fileType");
			Map<String,String> fileStatus = new HashMap<String, String>();
			HttpClientUtil http = new HttpClientUtil();
			String newName = file.getName()+"."+fileType;
			file.setObjectId(fileObjectId);
			Gson gson = new Gson();
			String flag = "1";
			try {
				newName = URLEncoder.encode(newName, "UTF-8");
				String result = http.postJson(
						CA_FILERES_SAVE_URL,
						gson.toJson(file));
				String changeName = http.executeGet(PUBLISH_RENAME_FILE + "?objectId="
						+ fileObjectId + "&name=" + newName);
			} catch (Exception e) {
				flag = "0";
			}
			fileStatus.put("flag", flag);
			fileStatus.put("newName", newName);
			return fileStatus;
		}
		/**
		 * 文件重试
		 * @param request
		 * @param response
		 */
		@RequestMapping(baseUrl + "doTaskHistoryByPath")
		public @ResponseBody String doTaskHistoryByPath(HttpServletRequest request, HttpServletResponse response){
			String objectId = request.getParameter("id");//文件Id
			String reVal = "0";
			List<DoFileQueue> doFileQueues = resConverfileTaskService.queryByfileId(objectId);
			if (doFileQueues.size()>0) {//待转换表中已存在
				reVal = "2";
				return reVal;
			}
			
			HttpClientUtil http = new HttpClientUtil();
			String fileDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_FILEDETAIL_URL") + "?id=" + objectId);
			if(StringUtils.isNotEmpty(fileDetail)){
				Gson gson = new Gson();
				com.brainsoon.semantic.ontology.model.File file = gson.fromJson(fileDetail, com.brainsoon.semantic.ontology.model.File.class);
				
				DoFileQueueList doFileList =new DoFileQueueList();
				DoFileQueue doFile= new DoFileQueue();
				doFile.setFileFormat(file.getFileType());
				doFile.setFileId(file.getId());
				doFile.setObjectId(file.getObjectId());
				
				String converPath = "";
				String fileObjectId = file.getObjectId().substring(4);//截取掉urn:
				converPath = CONVER_FILE_ROOT + FilePathUtil.getFileUUIDPath(file.getPath()) +fileObjectId+"/";
				
				doFile = DoFileUtils.getConverPath(converPath, file.getFileType(), doFile);
				doFile.setResId(objectId);
				doFile.setSrcPath( (FILE_ROOT+file.getPath()).replaceAll("\\\\", "/") );
				doFile.setPendingType("0");
				logger.info("----------------------------原路径"+FILE_ROOT+file.getPath()+"---------------------");
				doFileList.addDoFileQueue(doFile);
				
				if(doFileList.getDoFileQueueList().size()>0){//不存在
					try{
						String result = "";
					if(doFileList !=null && doFileList.getDoFileQueueList().size()>0){
						//result = http.postJson(PUBLISH_FILE_WRITE_QUEUE,gson.toJson(doFileList));
						result = resConverfileTaskService.insertQueue(doFileList);
					}
					if(!result.equals("-1")){
						reVal = "1";
					}
					}catch (Exception e) {
						reVal = "0";
					}
				}
			}
			return reVal;
		}
		/**
		 * FtpHttp列表查询页
		 * @param request
		 * @param response
		 * @return
		 */
		@RequestMapping(baseUrl + "LookList")
		public @ResponseBody PageResult list(HttpServletRequest request,HttpServletResponse response){
			logger.info("查询标签列表");
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			QueryConditionList conditionList = getQueryConditionList();
			return resTargetService.query4Page(FileDownName.class, conditionList);
		}
		/**
		 * 查询FtpHttp详细转到ftpHttpDetail页面
		 * @param id
		 * @param model
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(baseUrl+"ftpHttpDetail")
		public String detail(HttpServletRequest request,ModelMap model) throws Exception{
			 Long id = (long) 0;
//			 String excelPath = "";
			 id =Long.parseLong(request.getParameter("id"));
			 String flag = request.getParameter("flag");
//			 try {
				 
//				 excelPath =request.getParameter("excelPath");
//				 excelPath=  URLDecoder.decode(excelPath,"UTF-8");
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			model.addAttribute("excelPath",excelPath);
//			model.addAttribute("taskIdAgain",id);
			model.addAttribute("task",resourceService.getByPk(FileDownName.class, id));
			model.addAttribute("flag", flag);
			return "system/ftpHttpDetail/ftpHttpDetail";
		}
		/**
		 * 进入FtpHttp详细列表
		 * @param request
		 * @param response
		 * @return
		 */
		@RequestMapping(value = baseUrl + "ftpDetailList")
		public @ResponseBody PageResult detailList(HttpServletRequest request,HttpServletResponse response){
			logger.info("进入查询方法");
			QueryConditionList conditionList = getQueryConditionListWithNoPlat();
			return resourceService.query4Page(FileDownValue.class, conditionList);
		}
		
		/**
		 * 资源预览
		 * @param request
		 * @param response
		 * @return
		 */
		@RequestMapping(value = baseUrl + "preview")
		public String preview(HttpServletRequest request, ModelMap model){
			logger.info("进入资源预览方法");
			String objectId = request.getParameter("objectId");
			HttpClientUtil http = new HttpClientUtil();
			String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
			Gson gson = new Gson();
			Ca bookCa = gson.fromJson(resourceDetail, Ca.class);
			if(bookCa!=null){
				model = publishResService.getZtreeJson(bookCa, objectId,model);
			}
			model.put("objectId", objectId);
			return baseUrl + "publishPreview";
		}
		
	/**
	 * 下载资源
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping(baseUrl + "download")
	public  void download(HttpServletRequest request,HttpServletResponse response){
		String filePath = request.getParameter("filePath");
		String name = request.getParameter("name");
		try {
			filePath=  URLDecoder.decode(filePath,"UTF-8");
			String sourcePath = FILE_ROOT + filePath;//原地址
			String targetPath = TMP_DIR + name;//临时地址（用于下载的时候将别名改成文件原名称）
			sourcePath = sourcePath.replaceAll("\\\\", "/");
			
			FileUtils.copyFile(new File(sourcePath), new File(targetPath));
			logger.info("资源预览-文件下载 文件原地址："+sourcePath +"--> 文件下载临时地址："+targetPath);
			resourceService.downloadFile(request, response, targetPath,false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 文件视图 列表
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping(baseUrl + "file/fileResList")
	public @ResponseBody String fileResList(HttpServletRequest request,HttpServletResponse response){
		String page = request.getParameter("page");
		String size = request.getParameter("size");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
//		String createUser = request.getParameter("createUser");
		String createUser = "";
		String resName = request.getParameter("resName");
		String publishType = request.getParameter("publishType");
		String fileExtensionName = request.getParameter("fileExtensionName");
		 UserInfo userInfo =  LoginUserUtil.getLoginUser();
		 if(!userInfo.isAdmin()){
			 createUser = userInfo.getUserId()+"";
		 }
		if(StringUtils.isBlank(publishType)){
			 Map<Object, Object> resTypeMap = SysResTypeCacheMap.getMapValue();
			 Iterator it = resTypeMap.entrySet().iterator();
			 while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry) it.next();
					if(pairs.getKey()!=null && pairs.getValue()!=null){
						publishType = publishType+pairs.getKey()+",";
					}
					logger.info("----------------输出字段"+pairs.getKey()+"---"+pairs.getValue()+"--------------");
				}
			 if(publishType.endsWith(",")){
				publishType = publishType.substring(0,publishType.length()-1);
			 }
		}
		List<SearchResult> result = publishResService.queryFile4Page(page,size,startDate,endDate,createUser,resName,publishType,fileExtensionName);
//		String result = "";
//		if ("1".equals(page)) {
//			result = "{'startRow':0,'maxRow':10,'total':25,'rows':["
//				+ "{'caId':'1111','isDir':'2','path':'/fileDir/fileRoot/test/res_default.jpg','fileType':'jpg','create_time':'2015-5-19','creator':'系统管理员','aliasName':'res_default.jpg','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//				+ "{'caId':'2222','isDir':'2','path':'/fileDir/fileRoot/test/1234.flv','fileType':'flv','create_time':'2015-5-19','creator':'系统管理员','aliasName':'第八集牛肉陶板烧_标清.flv','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//				+ "{'caId':'3333','isDir':'2','path':'/fileDir/fileRoot/test/重构的代码.java','fileType':'java','create_time':'2015-5-19','creator':'系统管理员','aliasName':'重构的代码.java','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//				+ "{'caId':'4444','isDir':'2','path':'/fileDir/fileRoot/test/C++学习笔记与开发技巧与典型列子.doc','fileType':'doc','create_time':'2015-5-19','creator':'系统管理员','aliasName':'C++学习笔记与开发技巧与典型列子.doc','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//				+ "{'caId':'5555','isDir':'2','path':'/fileDir/fileRoot/test/java从零基础到精通的整个详细笔记.pdf','fileType':'pdf','create_time':'2015-5-19','creator':'系统管理员','aliasName':'java从零基础到精通的整个详细笔记.pdf','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//				+ "{'caId':'6666','isDir':'2','path':'/fileDir/fileRoot/test/Linux常用命令大全.txt','fileType':'txt','create_time':'2015-5-19','creator':'系统管理员','aliasName':'Linux常用命令大全.txt','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//				+ "{'caId':'7777','isDir':'2','path':'/fileDir/fileRoot/test/fileList.css','fileType':'css','create_time':'2015-5-19','creator':'系统管理员','aliasName':'fileList.css','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//				+ "{'caId':'8888','isDir':'2','path':'/fileDir/fileRoot/test/res_default_img.jpg','fileType':'jpg','create_time':'2015-5-19','creator':'系统管理员','aliasName':'res_default_img.jpg','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//				+ "{'caId':'9999','isDir':'2','path':'/fileDir/fileRoot/test/yanshi1.jpg','fileType':'jpg','create_time':'2015-5-19','creator':'系统管理员','aliasName':'yanshi1.jpg','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//				+ "{'caId':'1010','isDir':'2','path':'/fileDir/fileRoot/test/MobileHandbook.epub','fileType':'epub','create_time':'2015-5-19','creator':'系统管理员','aliasName':'MobileHandbook.epub','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'}"
//				+ "]}";
//		}else if ("2".equals(page)) {
//			result = "{'startRow':0,'maxRow':10,'total':25,'rows':["
//					+ "{'caId':'1111','isDir':'2','path':'/fileDir/fileRoot/test/2015.png','fileType':'png','create_time':'2015-5-19','creator':'系统管理员','aliasName':'2015-08-05_191322.png','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//					+ "{'caId':'2222','isDir':'2','path':'/fileDir/fileRoot/test/5555.mp4','fileType':'mp4','create_time':'2015-5-19','creator':'系统管理员','aliasName':'演示视频.mp4','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//					+ "{'caId':'3333','isDir':'2','path':'/fileDir/fileRoot/test/Hibernate中文参考文档 V3.2.chm','fileType':'chm','create_time':'2015-5-19','creator':'系统管理员','aliasName':'Hibernate中文参考文档 V3.2.chm','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//					+ "{'caId':'4444','isDir':'2','path':'/fileDir/fileRoot/test/jquery.js','fileType':'js','create_time':'2015-5-19','creator':'系统管理员','aliasName':'jquery-1.8.2.min.js','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//					+ "{'caId':'9999','isDir':'2','path':'/fileDir/fileRoot/test/yanshi1.jpg','fileType':'jpg','create_time':'2015-5-19','creator':'系统管理员','aliasName':'yanshi1.jpg','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//					+ "{'caId':'5555','isDir':'2','path':'/fileDir/fileRoot/test/java从零基础到精通的整个详细笔记.pdf','fileType':'pdf','create_time':'2015-5-19','creator':'系统管理员','aliasName':'java从零基础到精通的整个详细笔记.pdf','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//					+ "{'caId':'6666','isDir':'2','path':'/fileDir/fileRoot/test/Linux常用命令大全.txt','fileType':'txt','create_time':'2015-5-19','creator':'系统管理员','aliasName':'Linux常用命令大全.txt','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//					+ "{'caId':'7777','isDir':'2','path':'/fileDir/fileRoot/test/shell常用命令.txt','fileType':'txt','create_time':'2015-5-19','creator':'系统管理员','aliasName':'shell常用命令.txt','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//					+ "{'caId':'8888','isDir':'2','path':'/fileDir/fileRoot/test/res_default_img.jpg','fileType':'jpg','create_time':'2015-5-19','creator':'系统管理员','aliasName':'res_default_img.jpg','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//					+ "{'caId':'1010','isDir':'2','path':'/fileDir/fileRoot/test/MobileHandbook.epub','fileType':'epub','create_time':'2015-5-19','creator':'系统管理员','aliasName':'MobileHandbook.epub','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'}"
//					+ "]}";
//		}else{
//			result = "{'startRow':0,'maxRow':10,'total':25,'rows':["
//					+ "{'caId':'2222','isDir':'2','path':'/fileDir/fileRoot/test/1234.flv','fileType':'flv','create_time':'2015-5-19','creator':'系统管理员','aliasName':'第八集牛肉陶板烧_标清.flv','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//					+ "{'caId':'7777','isDir':'2','path':'/fileDir/fileRoot/test/fileList.css','fileType':'css','create_time':'2015-5-19','creator':'系统管理员','aliasName':'fileList.css','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//					+ "{'caId':'4444','isDir':'2','path':'/fileDir/fileRoot/test/C++学习笔记与开发技巧与典型列子.doc','fileType':'doc','create_time':'2015-5-19','creator':'系统管理员','aliasName':'C++学习笔记与开发技巧与典型列子.doc','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//					+ "{'caId':'9999','isDir':'2','path':'/fileDir/fileRoot/test/yanshi1.jpg','fileType':'jpg','create_time':'2015-5-19','creator':'系统管理员','aliasName':'yanshi1.jpg','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//					+ "{'caId':'1111','isDir':'2','path':'/fileDir/fileRoot/test/res_default.jpg','fileType':'jpg','create_time':'2015-5-19','creator':'系统管理员','aliasName':'res_default.jpg','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'},"
//					+ "{'caId':'1010','isDir':'2','path':'/fileDir/fileRoot/test/MobileHandbook.epub','fileType':'epub','create_time':'2015-5-19','creator':'系统管理员','aliasName':'MobileHandbook.epub','resName':'西游记','resObjectId':'urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb'}"
//					+ "]}";
//		}
		Gson gson=new Gson();
		String fileResult = "";
		SearchResult fileList = null;
		String imageFileType = "jpgpngbmpjpeggif";
		for(int i=0;i<result.size();i++){
			fileList = result.get(i); 
			if(fileList!=null && !fileList.getRows().isEmpty()){
				List<com.brainsoon.semantic.ontology.model.File> files = fileList.getRows();
				if(files!=null && files.size()>0){
					for(int j=0;j<files.size();j++){
						com.brainsoon.semantic.ontology.model.File file = files.get(j);
						if(file.getFileType()!=null && file.getPath()!=null&&!"".equals(file.getPath())){
							String fileType = file.getFileType();
							if (imageFileType.contains(fileType) && file.getPath()!=null) {
								file.setCoverPath(file.getPath());
							}else{
								file.setCoverPath( getCoverPath(fileType));
							}
						}
					}
				}
			}
		}
//		fileList = gson.fromJson(result, SearchResult.class);
		fileResult = gson.toJson(fileList);
		return fileResult;
	}
	
	/**
	 * 标签分类
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "targetType")
	public @ResponseBody LinkedHashMap<String,String> targetType(HttpServletRequest request, HttpServletResponse response) {
		LinkedHashMap<String,String> map =null;
				map = dictNameService.getDictMapByName("标签分类");
		return map;
	}
	
	/**
	 * 获取对应扩展名的图片地址
	 * @return.
	 * 
	 */
	
	public String getCoverPath(String extensionName){
		String coverPath = publishResConstants.coverPath.get(extensionName.toLowerCase());
		if (StringUtils.isBlank(coverPath)) {
			coverPath = FILE_COVER+"other.png";
		}
		return coverPath;
	}
	

	
	@RequestMapping(value=baseUrl+"getSecondQuery")
	@ResponseBody
	public String getSecondQuery(String publishType){
		String queryContion = ""; 
		UserInfo user = LoginUserUtil.getLoginUser();
		List<CustomMetaData>  customMetaDatas = MetadataSupport.getAllMetadateList(user,publishType);
		for(CustomMetaData customMetaData:customMetaDatas){
			List<MetadataDefinition> metadataDefinitions = customMetaData.getCustomPropertys();
			for(MetadataDefinition metadataDefinition:metadataDefinitions){
				if(StringUtils.isNotBlank(metadataDefinition.getSecondSearch()) && metadataDefinition.getSecondSearch().equals("0")){
						int fieldType = metadataDefinition.getFieldType();
						//元数据英文名称+文本框类型+查询模式
						queryContion +=metadataDefinition.getFieldName()+":"+fieldType+":"+metadataDefinition.getQueryModel()+",";
				}
			}
		}
		if(queryContion != "" && queryContion.length()>0){
			try{
				queryContion = queryContion.substring(0, queryContion.length()-1);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return queryContion;
	}

	/*
	 * 方法名称:transStringToMap 
	 * 传入参数:mapString 形如 username:chenziwen,password:1234 
	 * 返回值:Map 
	 */  
	public static String getTitleByBody(String mapString){  
		String[] strings = mapString.split(",");
	  	String title = "";
	  	for (String string : strings) {
		  string = string.trim();
		  if (string.contains("title")) {
			  title = string.substring(6);
		  }
	  	}
	  	return title;  
	} 
	/**
	 * 返回路径
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "returnPath")
	public @ResponseBody String returnPath(HttpServletRequest request, HttpServletResponse response) {
		String temPath =FILE_DOWN;
		return temPath;
	}
	
	
	/**
	 * 按照页码下载资源文件计算文件大小
	 * 只针对以Http下载方式进行比较
	 * fengda 2015年12月9日
	 * @param request
	 * @param response
	 * @param searchParamCa
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value=baseUrl+"getFileSizePage")
	@ResponseBody
	public String getFileSizePage(HttpServletRequest request,
			HttpServletResponse response,@RequestParam("gotoFtpHttp") String searchParamCa) throws Exception {
		Map<String,Object> rtn = new HashMap<String, Object>();
		Gson gson = new Gson();
		SearchParamCa spc = gson.fromJson(searchParamCa, SearchParamCa.class);
		QueryConditionList conditionList = getQueryConditionList();
		String publishType = "";
		String page = "";
		String page1 = "";
		String pageSize = "";
		String queryModel = "";
		String status = "";
		if(spc != null){
			publishType = spc.getPublishType();				//资源类型
			page = spc.getPage();							//起始页
			page1 = spc.getPage1();							//结束页
			pageSize = spc.getSize() + "";					//当前页面显示的条数
			queryModel = spc.getQueryModel();				//二次查询以及main页面用于查询的字段的id
			if(StringUtils.isNotBlank(publishType))
				request.setAttribute("publishType", publishType);
			if(StringUtils.isNotBlank(status))
				request.setAttribute("status", status);
			if(StringUtils.isNotBlank(queryModel)){
				try {
					queryModel = URLDecoder.decode(queryModel,"utf-8"); 
					queryModel = URLDecoder.decode(queryModel,"utf-8"); 
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} 
				
				//将用于查询的字段和输入的值封装到conditionList中
				String[] queryModelParams = queryModel.split("&");
				for (int i = 0; i < queryModelParams.length; i++) {
					String[] queryValueParams = queryModelParams[i].split("==");
					String queryValueStr = queryValueParams[1];
					queryValueStr = queryValueStr.substring(1, queryValueStr.length()-1);
					String[] queryValue = StringUtil.strToArray(queryValueStr, ",");
					setQueryConditionListParam(conditionList, queryValueParams[0], queryValue, "1");
				 }
			}
		}
		
		List<Ca> cas = new ArrayList<Ca>();
		String result= "";
		if(!"".equals(page)&&!"".equals(page1)&&!"".equals(pageSize)){
			//计算共导出多少条记录
			Long num = (long) ((Integer.parseInt(page1) - Integer.parseInt(page)+1)*Integer.parseInt(pageSize));
			result= publishResService.queryResource4PageByParam(request, Integer.parseInt(page), num, WebappConfigUtil.getParameter("PUBLISH_QUERY_URL"),pageSize,conditionList,null);
		}
		
		//获取要进行下载资源文件的所有资源
		if(StringUtils.isNotBlank(result)){
			 SearchResultCa caList = gson.fromJson(result, SearchResultCa.class);
			 cas = caList.getRows();
		}
		
		//将要进行下载的所有资源文件大小与系统参数进行对比. 0 未超过下载允许大小    1 超过系统参数设置的允许下载大小，前台提示
		String boo = "";
		if(cas != null && cas.size()>0){
			String ids = "";
			for (Ca ca : cas) {
				ids += ca.getObjectId()+",";
			}
			if(StringUtils.isNotBlank(ids)){
				ids = ids.substring(0, ids.length()-1);
			}
			boo = publishResService.fileSize(request, response, ids, null);
		}
		
		rtn.put("boo", boo);
		return boo;
	}
	
	
	/**
	 * 下载资源等待按照分页下载
	 * fengda 2015年12月9日
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("finally")
	@RequestMapping(value =baseUrl+"downloadPublishResPage", method = {RequestMethod.POST })
	public  @ResponseBody String downloadPublishResPage(HttpServletRequest request,HttpServletResponse response,@RequestParam("gotoFtpHttp") String gotoFtpHttp){
		
		//获取前台传入参数，封装到SearchParamCa对应属性中
		Gson gson = new Gson();
		SearchParamCa spc = gson.fromJson(gotoFtpHttp, SearchParamCa.class);
		QueryConditionList conditionList = getQueryConditionList();
		String publishType = "";
		String page = "";
		String page1 = "";
		String pageSize = "";
		String queryModel = "";
		String status = "";
		String encryptPwd = "";
		String ftpFlag = "";
		String isComplete = "";
		
		if(spc != null){
			publishType = spc.getPublishType();  	//资源类型
			page = spc.getPage();					//起始页
			page1 = spc.getPage1();					//结束页
			encryptPwd = spc.getEncryptPwd();		//加密密钥
			ftpFlag = spc.getFtpFlag();				//下载方式 1.http 2.ftp
			isComplete = spc.getIsComplete();		//是否压缩 1.压缩  2.不压缩
			pageSize = spc.getSize() + "";			//当前页面上显示的条数
			queryModel = spc.getQueryModel();		//二次查询以及main页面的查询条件的id
			if(StringUtils.isNotBlank(publishType))
				request.setAttribute("publishType", publishType);
			if(StringUtils.isNotBlank(status))
				request.setAttribute("status", status);
			if(StringUtils.isNotBlank(queryModel)){
				try {
					queryModel = URLDecoder.decode(queryModel,"utf-8"); 
					queryModel = URLDecoder.decode(queryModel,"utf-8"); 
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} 
				String[] queryModelParams = queryModel.split("&");
				for (int i = 0; i < queryModelParams.length; i++) {
					String[] queryValueParams = queryModelParams[i].split("==");
					String queryValueStr = queryValueParams[1];
					queryValueStr = queryValueStr.substring(1, queryValueStr.length()-1);
					String[] queryValue = StringUtil.strToArray(queryValueStr, ",");
					//将而二次查询以及main页面的查询条件封装到conditionList中
					setQueryConditionListParam(conditionList, queryValueParams[0], queryValue, "1");
				 }
			}
		}
		
		//计算要导出共多少条数据，以及组装查询条件，查询资源
		List<Ca> cas = new ArrayList<Ca>();
		String result= "";
		if(!"".equals(page)&&!"".equals(page1)&&!"".equals(pageSize)){
			//计算共导出多少条记录
			Long num = (long) ((Integer.parseInt(page1) - Integer.parseInt(page)+1)*Integer.parseInt(pageSize));
			result= publishResService.queryResource4PageByParam(request, Integer.parseInt(page), num, WebappConfigUtil.getParameter("PUBLISH_QUERY_URL"),pageSize,conditionList,null);
		}
		
		//获取要导出资源文件的所有资源
		if(StringUtils.isNotBlank(result)){
			 SearchResultCa caList = gson.fromJson(result, SearchResultCa.class);
			 cas = caList.getRows();
		}
		
		
		Date date = new Date();
		String time =  DateUtil.convertDateToString("yyyyMMddHHmmss",date);
		String isOk = "";
		try {
			String ids = "";
			for (Ca ca : cas) {
				ids += ca.getObjectId()+",";
			}
			String encryptZip =time+"/";
			//http下载
			if(ftpFlag.equals("1")){
				if(cas != null && cas.size()>0){
					
					if(StringUtils.isNotBlank(ids)){
						ids = ids.substring(0, ids.length()-1);
						isOk = publishResService.downloadBookRes(request, encryptZip, ids, encryptPwd,ftpFlag,isComplete);
					}
				}
				if(StringUtils.isNotBlank(isOk)){
					isOk = isOk +","+ftpFlag;
				}
			}else{
				//ftp下载
				isOk = publishResService.createHttpFtpDownload(ids, encryptPwd, ftpFlag, encryptZip,isComplete);
				//isOk = searchIndexService.createByPageFtpDownload(cas, ftpFlag, encryptZip, encryptPwd, isComplete);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			return isOk;
		}
	}
	/**
	 * 返回元数据ztree
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = baseUrl + "returnZtreeMetadata")
	public @ResponseBody String returnZtreeMetadata(HttpServletRequest request, HttpServletResponse response) {
		UserInfo user = LoginUserUtil.getLoginUser();
		String publishType = request.getParameter("publishType");
		List<MetadataDefinition>  metadataDefines =user.getMetadataList();
//		List<CustomMetaData>  customMetaDatas = MetadataSupport.getAllMetadateList(user,"1");
		String json = zTFLService.getMetadataTree(user, publishType);
		return json;
	}
	/**
	 * beachReplace批量替换
	 * post请求
	 * @return
	 * @throws Exception 
	 */
    @RequestMapping(value = baseUrl + "beachReplace", method = {RequestMethod.POST })
	@ResponseBody
	public String beachReplace(HttpServletRequest request,
			HttpServletResponse response,@RequestParam("beachReplaceCa") String beachReplaceCa) throws Exception {
    	Gson gson = new Gson();
		SearchParamCa spc = gson.fromJson(beachReplaceCa, SearchParamCa.class);
		QueryConditionList conditionList = getQueryConditionList();
		Map<String,String> map = null;
		String result= "";
		String ids = "";
		String publishType = "";
		String page = "";
		String page1 = "";
		String pageSize = "";
		String queryModel = "";
		String field = "";
		String fieldValue = "";
		Long resSizeNum = 0L;
		String status = "1";
		if(spc != null){
			ids = spc.getIds();
			publishType = spc.getPublishType();
			page = spc.getPage();
			page1 = spc.getPage1();
			pageSize = spc.getSize() + "";
			field = spc.getField();
			fieldValue = spc.getFieldValue();
			queryModel = spc.getQueryModel();
		}
		if(StringUtils.isNotBlank(queryModel)){
			try {
				queryModel = URLDecoder.decode(queryModel,"utf-8"); 
				queryModel = URLDecoder.decode(queryModel,"utf-8"); 
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} 
			String[] queryModelParams = queryModel.split("&");
			for (int i = 0; i < queryModelParams.length; i++) {
				String[] queryValueParams = queryModelParams[i].split("==");
				String queryValueStr = queryValueParams[1];
				queryValueStr = queryValueStr.substring(1, queryValueStr.length()-1);
				String[] queryValue = StringUtil.strToArray(queryValueStr, ",");
				setQueryConditionListParam(conditionList, queryValueParams[0], queryValue, "1");
			 }
		}
		if(!"".equals(pageSize)){
			//计算共导出多少条记录
			Long num = (long) ((Integer.parseInt(page1) - Integer.parseInt(page))*Integer.parseInt(pageSize));
				result= publishResService.queryResource4PageByParam(request, Integer.parseInt(page), num, WebappConfigUtil.getParameter("PUBLISH_QUERY_URL"),pageSize,conditionList,"");
		}
		List<Ca> cas = new ArrayList<Ca>();
		try {
			 SearchResultCa caList = gson.fromJson(result, SearchResultCa.class);
			 cas = caList.getRows();
		} catch (Exception e) {
			
			status = "0";
		}
		if(cas!=null && !cas.isEmpty()){
			map =  publishResService.updateBeachReplace(cas, field, fieldValue);
//			String retValue = map.get("status").toString();
//			 if(retValue.equals("0")){
//				 status = "2";
//			 }
		}
		String json = gson.toJson(map);
    	return json;
    }
	
	/**
	 * 
	* @Title: targetType
	* @Description: 导入失败的重新导入
	* @param request
	* @param response
	* @return    参数
	* @return LinkedHashMap<String,String>    返回类型
	* @throws
	 */
	@RequestMapping(value = baseUrl + "reImport")
	public @ResponseBody String reImport(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		UploadTaskDetail uploadTaskDetail = (UploadTaskDetail) batchImportResService.getByPk(UploadTaskDetail.class, Long.parseLong(id));
		if (uploadTaskDetail.getId() > 0) {
			int detailStatus = uploadTaskDetail.getStatus();
			if (detailStatus==4) {
				
			}else {
				return "";
			}
		}
		
		return "";
	}
	
	
	@RequestMapping(value=baseUrl+"getTMJson")
	@ResponseBody
	public String getTMJson(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String returnJson = publishResService.getTMJson();
        return returnJson;
	}
	
	@RequestMapping(value=baseUrl+"saveResByTM")
	public @ResponseBody String saveResByTM(HttpServletRequest request,HttpServletResponse response)throws Exception{
		String paths = request.getParameter("paths");
		String publishType = request.getParameter("publishType");
		
		paths=URLDecoder.decode(paths,"UTF-8");
		if (paths.endsWith(",")) {
			paths=paths.substring(0, paths.length()-1);
		}
		return publishResService.saveResByTM(paths,publishType);
		
	}
	
	
	
	/**
	 * 用于批量检索
	 */
	
	@RequestMapping(baseUrl+"gotoBatchNum")
	public String gotoBatchNum(HttpServletRequest request,HttpServletResponse response,Model model) {
		String publishType = request.getParameter("publishType");
		if(StringUtils.isNotBlank(publishType)){
			request.setAttribute("publishType", publishType);
		}
		return "/statistics/batchNum/batchNum";
	}
	
	
	@RequestMapping(baseUrl+"initQueryColumn")
	@ResponseBody
	public String initQueryColumn(HttpServletRequest request,HttpServletResponse response){
		String publishType = request.getParameter("publishType");
		UserInfo user = LoginUserUtil.getLoginUser();
		List<CustomMetaData>  customMetaDatas = MetadataSupport.getAllMetadateList(user,publishType);
		String queryColumn = "[";
		for(CustomMetaData customMetaData:customMetaDatas){
			List<MetadataDefinition> metadataDefinitions = customMetaData.getCustomPropertys();
			for(MetadataDefinition metadataDefinition:metadataDefinitions){
				String viewPriority = metadataDefinition.getViewPriority();
				if(StringUtils.isNotBlank(viewPriority) && viewPriority.contains("2")){
					queryColumn +="{field:'metadataMap."+metadataDefinition.getFieldName()+"',title:'"+metadataDefinition.getFieldZhName()+"',width:fillsize(0.17),align:'center'},";
				}
			}
		}
		
		
		queryColumn +="{field:'createTime',title:'创建日期',width:fillsize(0.10),align:'center'},"+"{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}]";
		return queryColumn;
	}
	
	
	/**
	 * 用于查询统计的批量检索
	 * 将输入查询条件转换成资源存储时对应的id（主要针对于数据字典项的转换）
	 * @param req
	 * @return
	 */
	@RequestMapping(baseUrl+"changeMetadata")
	@ResponseBody
	public String changeMetadata(HttpServletRequest req){
		//fileType 1单文本 5文本域 4单选 3多选 2下拉选择 7日期 8URL 6树形 11单位 10人员
		String queryName = req.getParameter("condition"); //获取输入的查询内容
		String fileType = req.getParameter("fileType");   //获取要查询的项的fileType
		String dictKey = req.getParameter("dictKey");    //获取数据字典的索引值
		String queryIds = "";
		
	 if(StringUtils.isNotBlank(fileType) && (fileType.equals("2") || fileType.equals("3") || fileType.equals("4"))){
			if(StringUtils.isNotBlank(dictKey)){
				if(StringUtils.isNotBlank(queryName)){
					String coditions[] = queryName.split(",-");
					for(int i=0;i<coditions.length;i++){
						String impt = GlobalDataCacheMap.getChildCodeByIdAndChildValue(dictKey, coditions[0]);
						if(StringUtils.isBlank(impt)){
							impt = GlobalDataCacheMap.getNameValueWithIdByKeyAndChildValue(dictKey, coditions[0]);
						}
						if(impt.endsWith(",")){
							impt = impt.substring(0,impt.length()-1);
						}
						queryIds += impt+",-";
					}
				}
			}
			if(StringUtils.isNotBlank(queryIds)){
				queryIds = queryIds.substring(0,queryIds.length()-2);
			}
		}else if(StringUtils.isNotBlank(fileType) && fileType.equals("6")){
			if(StringUtils.isNotBlank(dictKey)){
				if(StringUtils.isNotBlank(queryName)){
					String coditions[] = queryName.split(",-");
					for(int i=0;i<coditions.length;i++){
						String graph = fltxService.getFLTXNodeByCode(Long.parseLong(dictKey), coditions[i]);
						if(StringUtils.isNotBlank(graph)){
							queryIds += graph + ",-";
						}
					}
				}
			}
			if(StringUtils.isNotBlank(queryIds)){
				queryIds = queryIds.substring(0,queryIds.length()-2);
			}
			
		}else if(StringUtils.isNotBlank(fileType) && fileType.equals("10")){
			if(StringUtils.isNotBlank(queryName)){
				String conditions[] = queryName.split(",-");
				for(int i=0;i<conditions.length;i++){
					 Staff staff = new Staff();
					 staff.setName(conditions[i]);
					 String Ids  = staffService.doSaveOrUpdate(staff);
					 if(StringUtils.isNotBlank(Ids)){
						 queryIds += Ids + ",-";
					 }
				}
				if(StringUtils.isNotBlank(queryIds)){
					queryIds = queryIds.substring(0,queryIds.length()-2);
				}
			}
		}else if(StringUtils.isNotBlank(fileType) && fileType.equals("11")){
			if(StringUtils.isNotBlank(queryName)){
				String conditions[] = queryName.split(",-");
				for(int i=0;i<conditions.length;i++){
					 Company company = new Company();
					 company.setName(conditions[i]);
					 String Ids  = companyService.doSaveOrUpdate(company);
					 if(StringUtils.isNotBlank(Ids)){
						 queryIds += Ids + ",-";
					 }
				}
				if(StringUtils.isNotBlank(queryIds)){
					queryIds = queryIds.substring(0,queryIds.length()-2);
				}
			}
		}
		return queryIds;
	}
	/**
	 * 进入FtpHttp详细列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = baseUrl + "fileDetailList")
	public @ResponseBody StringBuffer fileDetailList(HttpServletRequest request,HttpServletResponse response){
		logger.info("进入查询方法");
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		StringBuffer returnValue = publishResService.fileDetail(id,name);
		return returnValue;
	}
	
	/**
	 * 
	* @Title: updateFCS
	* @Description: 本方法是解决转换历史表中的id与文件id不对应的情况，请勿调用
	* @param request
	* @param response
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	@RequestMapping(value = baseUrl + "updateFCS")
	public @ResponseBody String updateFCS(HttpServletRequest request,HttpServletResponse response){
		publishResService.updateFCS();
		return "SUCCESS";
	}
	
	/**
	 * 
	* @Title: covertFailToQueue
	* @Description: 本方法是解决转换历史表中有些没有转换，判断转换后的目录存不存在，若不存在就放到转换表中
	* @param request
	* @param response
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	@RequestMapping(value = baseUrl + "covertFailToQueue")
	public @ResponseBody String covertFailToQueue(HttpServletRequest request,HttpServletResponse response){
		publishResService.doCovertFailToQueue();
		return "SUCCESS";
	}
	/**
	 * 测试
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = baseUrl + "test")
	public @ResponseBody String test(HttpServletRequest request,HttpServletResponse response){
		JSONArray ja = new JSONArray();
		for(int i=1;i<10;i++){
			JSONObject jb1 = new JSONObject();
			JSONObject jb = new JSONObject();
			jb.put("200"+i+"年11"+"月"+i+"日", "第六次全国人 口普查标准时点在今 日"
					+ "零时开始 ， 全国600 多万名普查 员和普查指导 员走 进"
					+ " 4 亿 多户住 户 ， 查 清查实全国人 口状况 。 "
					+ "这 次人 口普 查采用按现住地登记的原则。");
			jb1.put("200"+i, jb.toString());
			ja.add(jb1);
		}
		return ja.toString();
	}
	 /**
	  * 出版标签List资源查询
	  * @param request
	  * @param response
	  * @return
	  */
	 @RequestMapping(value="/publishRes/queryListArticleRes")
	 @ResponseBody
	 public String  queryListArticleRes(HttpServletRequest request, HttpServletResponse response) {
		 QueryConditionList conditionList = getQueryConditionList();
		 String publishType = request.getParameter("publishType");
		 //年代
		 String years = request.getParameter("years");
		 String year = request.getParameter("year");
		 String month = request.getParameter("month");
		 String day = request.getParameter("day");
		 String page = request.getParameter("page");
		 String keyWords = "";
		 String category = "";
		 String pageSize = conditionList.getPageSize()+"";
		 
		 List<QueryConditionItem> items = conditionList.getConditionItems();
		 if(items!=null && items.size()>0){
			 for(QueryConditionItem item:items){
				 if(item.getFieldName().equals("category")){
					 category = item.getValue()+ "";
				 }
				 if(item.getFieldName().equals("keyWords")){
					 keyWords = item.getValue()+ "";
				 }
			 }
		 }
//		 try {
//			 if(StringUtils.isNotBlank(targetNames))
//				 targetNames=  URLDecoder.decode(targetNames,"UTF-8");
//			 if(StringUtils.isNotBlank(classTimeName))
//				 classTimeName =  URLDecoder.decode(classTimeName,"UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		 if(StringUtils.isNotBlank(targetField) && StringUtils.isNotBlank(targetNames));{
//			 targetNames = targetField +","+targetNames;
//		 }
		 String result = "";
//		 SearchResultCa caList = new SearchResultCa();
//		 List<Ca> rows = new ArrayList<Ca>();
//		 for(int i=0;i<10;i++){
//			 Ca ca = new Ca();
//			 ca.getMetadataMap().put("TmTimeClass", i+1+"月");
//			 ca.getMetadataMap().put("tmdate", "1日");
//			 ca.getMetadataMap().put("tmJournalClass", "2015年11月号/第22期");
//			 ca.getMetadataMap().put("tmevent", "第六次全国人 口普查标准时点在今 日零时开始 ， 全国 "
//					+"600 多万名普查 员和普查指导 员走 进 4 亿 多户住 户 ， 查 清查实全"
//					+"国人 口状况 。 这 次人 口普 查采用按现住地登记的原则。");
//			 rows.add(ca);
//			 }
//		 caList.setRows(rows);
		 if(StringUtils.isNotBlank(month) && Integer.parseInt(month)<10){
			 month = "0"+month;
		 }
		 if(StringUtils.isNotBlank(day) && Integer.parseInt(day)<10){
			 day = "0"+day;
		 }
		 Gson gson=new Gson();
		 result= publishResService.queryEntry(WebappConfigUtil.getParameter("SEARCH_ENTRY")+"?year="+year+"&month="+month+"&day="+day+"&years="+years+"&page="+page+"&pageSize="+pageSize+"&category="+category+"&keyWords="+keyWords);
		 SearchResultEventy caList = gson.fromJson(result, SearchResultEventy.class);
		 result = gson.toJson(caList);
		 return result;
	}
	 /**
	  * 大事迹列表查询
	  * @param request
	  * @param response
	  * @return
	  */
	 @RequestMapping(value=baseUrl+"entryList")
	 public String  entryList(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
//		 QueryConditionList conditionList = getQueryConditionList();
		 HttpSession session = getSession();
		 String publishType = request.getParameter("publishType");
		 String years = request.getParameter("years");
		 String year = request.getParameter("year");
		 String month = request.getParameter("month");
		 String day = request.getParameter("day");
		 model.put("publishType", publishType);
		 model.put("years", years);
		 model.put("year", year);
		 model.put("month", month);
		 model.put("day", day);
//		 model.put("classTimeName", classTimeName);
//		 String result= publishResService.queryEntry(WebappConfigUtil.getParameter("SEARCH_ENTRY")+"?year="+year+"&month="+month+"&day="+day);
	    return "articleResource/articleResourceList";
	}
	 @RequestMapping(value=baseUrl+"toArticleDetail")
	public String toArticleDetail(HttpServletRequest request, ModelMap model) {
		String objectId = request.getParameter("objectId");
		HttpClientUtil http = new HttpClientUtil();
		String resourceDetail = http.executeGet(SEARCH_ENTRY_DETAIL + "?id=" + objectId);
		Gson gson = new Gson();
		Entry en = gson.fromJson(resourceDetail, Entry.class);
		if(en!=null){
//			model = publishResService.jsonArray(en, objectId, model);
			model.put("publishType", "1");
		}
		model.put("detailFlag", "detail");
//		String execuId = WorkFlowUtils.getExecuId(objectId, "pubresCheck");
//		model.put("execuId", execuId);
		model.put("objectId", objectId);
		model.put("articleEn", en);
		logger.debug(resourceDetail);
		return "articleResource/articleDetail";
	}
}
