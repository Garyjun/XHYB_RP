package com.brainsoon.system.web.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.po.tree.TreeNode;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.system.model.DeleteRelationInfo;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.model.StopWord;
import com.brainsoon.system.service.IBookService;
import com.brainsoon.system.service.IStopWordService;
import com.brainsoon.system.support.BookExcelUtil;
import com.brainsoon.system.support.ExcelUtil;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.google.gson.Gson;
@Controller
public class BookAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/system/book/";
	public final static String FILE_TEMP = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp);
	private String postUrl = WebappConfigUtil.getParameter("RES_ROOT_URL");
	@Autowired
	private IBookService bookService;
//	private UserInfo userInfo = LoginUserUtil.getLoginUser();
	
	//查询学科分类树中的数据
	@RequestMapping(baseUrl + "*/getSelectValues")
	public @ResponseBody String getSelectValues(HttpServletRequest request,HttpServletResponse response){
		logger.info("查询节点方法");
		String nodeType = request.getParameter("nodeType");
		String code = request.getParameter("code");
		LinkedHashMap<String,String> map = bookService.getTreeValue(nodeType,code);
		if(map.isEmpty())
			return "";
		String json = "[";
		for(Map.Entry<String, String> entry : map.entrySet()){
			json += "{'nodeKey':'" + entry.getKey() + "','nodeValue':'" + entry.getValue() + "'},";
		}
		json = json.substring(0, json.length()-1) + "]";
		return json;
	}
	
	//查询数据字典的数据
	@RequestMapping(baseUrl + "getDictValues")
	public @ResponseBody String getDictValues(HttpServletRequest request,HttpServletResponse response){
		logger.info("查询节点方法");
		String nodeType = request.getParameter("nodeType");
		LinkedHashMap<String,String> map = bookService.getSelectValue(nodeType);
		if(map.isEmpty())
			return "";
		String json = "[";
		for(Map.Entry<String, String> entry : map.entrySet()){
			json += "{'nodeKey':'" + entry.getKey() + "','nodeValue':'" + entry.getValue() + "'},";
		}
		json = json.substring(0, json.length()-1) + "]";
		return json;
	}
	
	//创建图书或其节点
	@RequestMapping(baseUrl + "createBook")
	public @ResponseBody String createBook(HttpServletRequest request,HttpServletResponse response){
		logger.info("创建教材版本");
		String s = request.getParameter("domains");
		JSONArray domains = JSONArray.fromObject(s);
		JSONObject node = domains.getJSONObject(0);
		HttpClientUtil http = new HttpClientUtil();
		String json=http.postJson(postUrl + "createOntology/domainSingle", node.toString());
		UserInfo userInfo = LoginUserUtil.getLoginUser();
//		http.executeGet(postUrl + "semanticClient/doReasonerJob?type=0");
		SysOperateLogUtils.addLog("book_updateNode", "", userInfo);
		return json;
	}
	
	@RequestMapping(baseUrl + "listBook")
	public @ResponseBody String listBook(HttpServletRequest request,HttpServletResponse response){
		logger.info("教材版本列表");
		HttpClientUtil http = new HttpClientUtil();
		String json = http.executeGet(postUrl + "ontologyListQuery/domainNodesByType?nodeType=0");
		return json;
	}
	
	@RequestMapping(baseUrl + "listBookInfo")
	public @ResponseBody String listBookInfo(HttpServletRequest request,HttpServletResponse response){
		logger.info("教材版本信息");
		String version = request.getParameter("version");
		HttpClientUtil http = new HttpClientUtil();
		String json = http.executeGet(postUrl + "ontologyListQuery/domainTree?version="+version);
		return json;
	}
	
	@RequestMapping(baseUrl + "addBook")
	public @ResponseBody String addBook(HttpServletRequest request,HttpServletResponse response){
		logger.info("添加教材版本");
		String s = request.getParameter("domains");
		JSONArray domains = JSONArray.fromObject(s);
		JSONObject node = domains.getJSONObject(0);
//		JSONObject books = new JSONObject();
//		
//		books.put("@type", "domainList");
//		books.put("domainType", "0");
//		books.put("domains", domains);
		
		HttpClientUtil http = new HttpClientUtil();
		String json=http.postJson(postUrl + "createOntology/domainSingle", node.toString());
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		SysOperateLogUtils.addLog("book_addBook", domains.getJSONObject(0).getString("label"), userInfo);
		return json;
	}
	
	@RequestMapping(baseUrl + "dojob")
	public void dojob(HttpServletRequest request,HttpServletResponse response){
		String type = request.getParameter("type");
		HttpClientUtil http = new HttpClientUtil();
		http.executeGet(postUrl + "semanticClient/doReasonerJob?type="+type);
	}
	
	@RequestMapping(baseUrl + "listBookCatalog")
	public @ResponseBody String listBookCatalog(HttpServletRequest request,HttpServletResponse response){
		logger.info("教材版本目录");
		String version = request.getParameter("version");
		String fromId = request.getParameter("fromId");
		HttpClientUtil http = new HttpClientUtil();
		String json = http.executeGet(postUrl + "ontologyListQuery/domainTree?version="+version+"&fromId="+fromId);
		return json;
	}
	
	@RequestMapping(baseUrl + "deleteNode")
	public @ResponseBody String deleteNode(HttpServletRequest request,HttpServletResponse response){
		logger.info("教材版本信息");
		String objectId = request.getParameter("objectId");
		objectId = objectId.substring(objectId.indexOf("#")+1, objectId.length());
		HttpClientUtil http = new HttpClientUtil();
		String json = http.executeGet(postUrl + "ontologyDelete/domain?id="+objectId);
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		SysOperateLogUtils.addLog("book_deleteNode", "", userInfo);
		return json;
	}
	
	//导入excel文件
	@RequestMapping(baseUrl + "importExcel")
	public @ResponseBody String importExcel(HttpServletRequest request,HttpServletResponse response){
		logger.info("导入excel");
		String json = "";
		String fromId = request.getParameter("objectId");
		String pid = request.getParameter("pid");
		String xpath = request.getParameter("xpath");
		try {
			BookExcelUtil excelUtil = new BookExcelUtil();
			List<TreeNode> tree = excelUtil.importNode(getExcel(request),pid); 
			JSONArray treeArray = bookService.convertBookJSON(tree,xpath);
			JSONObject books = new JSONObject();
			books.put("@type", "domainList");
			books.put("domainType", "0");
			books.put("fromId", fromId);
			books.put("domains", treeArray);
			HttpClientUtil http = new HttpClientUtil();
			System.out.println(books.toString());
			json=http.postJson(postUrl + "createOntology/domain", books.toString());
			//删除相关节点
//			deleteRelativeNodes();
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			SysOperateLogUtils.addLog("classification_importVersion", "", userInfo);
		} catch (Exception e) {
			e.printStackTrace();
			json = "error";
		}
		return json;
	}

	//导入教材目录excel
	@RequestMapping(baseUrl + "importCatalogExcel")
	public @ResponseBody String importCatalogExcel(HttpServletRequest request,HttpServletResponse response){
		logger.info("导入excel");
		String json = "";
		try {
			String fromId = request.getParameter("fromId");
			String pid = request.getParameter("pid");
			ExcelUtil excelUtil = new ExcelUtil();
			List<TreeNode> catalog = excelUtil.importNode(getExcel(request));
			JSONArray catalogArray = bookService.convertCatalogJson(catalog,pid);
			JSONObject books = new JSONObject();
			books.put("@type", "domainList");
			books.put("domainType", "0");
			books.put("fromId", fromId);
			books.put("domains", catalogArray);
			HttpClientUtil http = new HttpClientUtil();
			json=http.postJson(postUrl + "createOntology/domain", books.toString());
		} catch (Exception e) {
			e.printStackTrace();
			json = "error";
		}
		return returnJsonResult(json);
	}
	
	//创建相关节点
	@RequestMapping(baseUrl + "createRelation")
	public @ResponseBody String createRelation(HttpServletRequest request,HttpServletResponse response){
		logger.info("创建相关节点");
		String json = "";
		String id = request.getParameter("id");
		String relativeId = request.getParameter("relativeId");
		HttpClientUtil http = new HttpClientUtil();
		json = http.executeGet(postUrl + "createOntology/domainRelation?id="+id+"&relationIds="+relativeId);
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		SysOperateLogUtils.addLog("book_createRelation", "", userInfo);
		return json;
	}
	
	//相关节点选项
	@RequestMapping(baseUrl + "selectRelative")
	public @ResponseBody String selectRelative(HttpServletRequest request,HttpServletResponse response,ModelMap model){
		logger.info("相关节点选项");
		String type = request.getParameter("type");
		String code = request.getParameter("code");
		String json = "";
		HttpClientUtil http = new HttpClientUtil();
		if(type!=null&&type.equals("version")){
			json = http.executeGet(postUrl + "ontologyListQuery/domainNodesByType?nodeType=0");
		}else{
			json = http.executeGet(postUrl + "ontologyListQuery/domainNodesByCode?codes="+code+"&domainType=0");
		}
		return json;
	}
	//相关节点列表
	@RequestMapping(baseUrl + "listRelative")
	public @ResponseBody String listRelative(HttpServletRequest request,HttpServletResponse response,ModelMap model){
		logger.info("相关节点列表");
		String json = "";
		HttpClientUtil http = new HttpClientUtil();
		String objectId = request.getParameter("objectId");
		json = http.executeGet(postUrl + "ontologyListQuery/domainRelations?objectId="+objectId);
		return json;
	}
	//删除相关节点
	@RequestMapping(baseUrl + "delRelativeNode")
	public @ResponseBody String delRelativeNode(HttpServletRequest request,HttpServletResponse response,ModelMap model){
		logger.info("删除相关节点");
		String json = "";
		String id = request.getParameter("id");
		String relativeId = request.getParameter("relativeId");
		HttpClientUtil http = new HttpClientUtil();
		json = http.executeGet(postUrl + "ontologyDelete/domainRelation?id="+id+"&relationIds="+relativeId);
//		if(json.equals("0")){
//			DeleteRelationInfo dri = new DeleteRelationInfo();
//			dri.setObjectId1(id);
//			dri.setObjectId2(relativeId);
//			dri.setCreateTime(new Date());
//			bookService.create(dri);
//		}
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		SysOperateLogUtils.addLog("book_deleteRelation", "", userInfo);
		return json;
	}
	
	@RequestMapping(baseUrl + "checkJobStatus")
	public @ResponseBody String checkJobStatus(){
		HttpClientUtil http = new HttpClientUtil();
		String json = http.executeGet(postUrl + "semanticClient/resonerJobStatus");
		return json;
	}
	
	@RequestMapping(baseUrl + "checkJobTime")
	public @ResponseBody String checkJobTime(){
		HttpClientUtil http = new HttpClientUtil();
		String json = http.executeGet(postUrl + "semanticClient/lastReasonerTime");
		return json;
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
        String filename = URLEncoder.encode("教材版本模板.xls", "UTF-8");
        headers.setContentDispositionFormData("attachment", filename);
        File excel = new File(WebAppUtils.getWebAppRoot() + "system/dataManagement/excelTemplate/tbzy.xlsx");
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(excel),headers, HttpStatus.CREATED);
    }
    
//    @RequestMapping(value = baseUrl + "downloadExportVersion")
//    public ResponseEntity<byte[]> downloadExportVersion(HttpServletRequest request) throws IOException {
//    	String objectId = request.getParameter("objectId");
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        String filename = URLEncoder.encode("导出教材版本.zip", "UTF-8");
//        headers.setContentDispositionFormData("attachment", filename);
//        File result = bookService.exportVersion(objectId);
//        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(result),headers, HttpStatus.CREATED);
//    }
    
	
	private File getExcel(HttpServletRequest request) throws IllegalStateException, IOException{
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		MultipartFile multipartFile = null;
		for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
			multipartFile = set.getValue();// 文件名
		}
		String fileName = multipartFile.getOriginalFilename();
		File excel = new File(FILE_TEMP + File.separator + fileName);
		multipartFile.transferTo(excel);
		return excel;
	}
	
	private String returnJsonResult(String json){
		if(json.equals("error"))
			return "{'status':-1}";
		JSONObject result = JSONObject.fromObject(json);
		if(result.getInt("state")==0)
			return "{'status':0}";
		else
			return "{'status':-1}";
	}
	
	//删除相关节点
	private void deleteRelations(){
		HttpClientUtil http = new HttpClientUtil();
		List relations = bookService.loadAll(DeleteRelationInfo.class);
		for(int i=0;i<relations.size();i++){
			DeleteRelationInfo dri = (DeleteRelationInfo) relations.get(i);
			if(StringUtils.isNotBlank(dri.getObjectId1())&&
					StringUtils.isNotBlank(dri.getObjectId2())){
				http.executeGet(postUrl + "ontologyDelete/domainRelation?id="
						+dri.getObjectId1()+"&relationIds="+dri.getObjectId2());
			}
		}
	}
	
	private void deleteRelativeNodes() {
		new Thread() {
			@Override
			public void run() {
				HttpClientUtil http = new HttpClientUtil();
				while(true){
					String status = http.executeGet(postUrl + "semanticClient/resonerJobStatus");
					if(status.equals("0")){
						deleteRelations();
						break;
					}else{
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}
	
	public static void main(String[] args) {
		String test = "fwewq902";
		
	}
	
}
