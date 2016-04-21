package com.brainsoon.system.web.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.po.tree.TreeNode;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.system.service.IBookService;
import com.brainsoon.system.service.IClassicService;
import com.brainsoon.system.support.ExcelUtil;
import com.brainsoon.system.support.SysOperateLogUtils;
@Controller
public class ClassificationAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/system/dataManagement/";
	private String postUrl =  WebappConfigUtil.getParameter("RES_ROOT_URL");
	public final static String FILE_TEMP = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp);
	@Autowired
	private IBookService bookService;
	@Autowired
	private IClassicService classicService;
	
	//查询学科分类树中的数据
	@RequestMapping(baseUrl + "classification/getSelectValues")
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
	
	@RequestMapping(baseUrl + "classification/listClassification")
	public @ResponseBody String listClassification(HttpServletRequest request,HttpServletResponse response){
		logger.info("查询分类字典树");
		String moudleName = request.getParameter("moudleName");
		return classicService.getMoudleTree(moudleName);
	}
	
	//教材改版信息
	@RequestMapping(baseUrl + "classification/changeVersionInfoList")
	public @ResponseBody String changeVersionInfoList(HttpServletRequest request,HttpServletResponse response){
		logger.info("教材改版信息");
		String objectId = request.getParameter("objectId");
		return classicService.getChangeVersionInfo(objectId);
	}
	
	//教材改版
	@RequestMapping(baseUrl + "classification/changeVersion")
	public @ResponseBody String changeVersion(HttpServletRequest request,HttpServletResponse response){
		logger.info("教材改版");
		String s = request.getParameter("domains");
		String objectId = request.getParameter("objectId");
		JSONArray domains = JSONArray.fromObject(s);
		JSONObject json = classicService.changeVersion(objectId,domains);
		if(json.getInt("state")==0)
			return "success";
		else
			return "error";
	}
	
	@RequestMapping(baseUrl + "classification/createClassification")
	public @ResponseBody String createClassification(HttpServletRequest request,HttpServletResponse response){
		logger.info("添加分类字典树");
		String s = request.getParameter("domains");
		JSONArray domains = JSONArray.fromObject(s);
		JSONObject node = domains.getJSONObject(0);
		HttpClientUtil http = new HttpClientUtil();
		String json=http.postJson(postUrl + "createOntology/domainSingle", node.toString());
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		SysOperateLogUtils.addLog("classification_updateNode", "", userInfo);
		return json;
	}
	
	@RequestMapping(baseUrl + "classification/*/deleteNode")
	public @ResponseBody String deleteNode(HttpServletRequest request,HttpServletResponse response){
		logger.info("教材版本信息");
		String objectId = request.getParameter("objectId");
		String domainType = request.getParameter("domainType");
		HttpClientUtil http = new HttpClientUtil();
		String json = "";
		if(domainType.indexOf("0")!=-1)
			json = http.executeGet(postUrl + "ontologyDelete/domain?id="+objectId);
		else
			json = http.executeGet(postUrl + "ontologyDelete/domain?id="+objectId+"&"+domainType);
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		SysOperateLogUtils.addLog("classification_deleteNode", "", userInfo);
		return json;
	}
	
	//导入学科分类excel文件
	@RequestMapping(baseUrl + "classification/importExcel")
	public @ResponseBody String importExcel(HttpServletRequest request,HttpServletResponse response){
		logger.info("导入excel");
		String json = "";
		try {
			ExcelUtil excelUtil = new ExcelUtil();
			List<TreeNode> tree = excelUtil.importNode(getExcel(request)); 
			JSONArray treeArray = bookService.convertXKFLJSON(tree);
			JSONObject books = new JSONObject();
			books.put("@type", "domainList");
			books.put("domainType", "2");
			books.put("domains", treeArray);
			HttpClientUtil http = new HttpClientUtil();
			json=http.postJson(postUrl + "createOntology/domain", books.toString());
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			SysOperateLogUtils.addLog("classification_import", "", userInfo);
		} catch (Exception e) {
			e.printStackTrace();
			json = "error";
		}
		return returnJsonResult(json);
	}
	//导入分类体系
	@RequestMapping(baseUrl + "classification/importClassicExcel")
	public @ResponseBody String importClassicExcel(HttpServletRequest request,HttpServletResponse response){
		logger.debug("导入分类体系excel");
		String json = "";
		try {
			ExcelUtil excelUtil = new ExcelUtil();
			List<TreeNode> tree = excelUtil.importNode(getExcel(request));
			JSONArray treeArray = bookService.convertClassicJSON(tree);
			JSONObject books = new JSONObject();
			books.put("@type", "domainList");
			books.put("domainType", "1");
			books.put("domains", treeArray);
			HttpClientUtil http = new HttpClientUtil();
			json=http.postJson(postUrl + "createOntology/domain", books.toString());
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			SysOperateLogUtils.addLog("classification_import", "", userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJsonResult(json);
	}
	
	//导入知识点资源
	@RequestMapping(baseUrl + "classification/importKnowledgeExcel")
	public @ResponseBody String importKnowledgeExcel(HttpServletRequest request,HttpServletResponse response){
		logger.info("导入知识点excel");
		String fromId = request.getParameter("objectId");
		String pid = request.getParameter("pid");
		String xpath = request.getParameter("xpath");
		String json = "";
		try {
			ExcelUtil excelUtil = new ExcelUtil();
			List<TreeNode> tree = excelUtil.importNode(getExcel(request));
			JSONArray treeArray = bookService.convertKnowledgeJSON(tree,xpath,pid);
			JSONObject books = new JSONObject();
			books.put("@type", "domainList");
			books.put("domainType", "1");
			books.put("fromId", fromId);
			books.put("domains", treeArray);
			HttpClientUtil http = new HttpClientUtil();
			System.out.println(books.toString());
			json=http.postJson(postUrl + "createOntology/domain", books.toString());
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			SysOperateLogUtils.addLog("classification_import", "", userInfo);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	
	//节点是否关联资源
	@RequestMapping(baseUrl + "classification/hasResource")
	public @ResponseBody String hasResource(HttpServletRequest request,HttpServletResponse response){
		String json = "";
		String objectId = request.getParameter("objectId");
		String domainType = request.getParameter("domainType");
		HttpClientUtil http = new HttpClientUtil();
		
		return json;
	}
	
	//修改节点nodeType
	@RequestMapping(baseUrl + "classification/changeNodeType")
	public @ResponseBody String changeNodeType(HttpServletRequest request,HttpServletResponse response){
		String s = request.getParameter("domains");
		JSONArray domains = JSONArray.fromObject(s);
		JSONObject node = domains.getJSONObject(0);
		HttpClientUtil http = new HttpClientUtil();
		String json=http.postJson(postUrl + "createOntology/domainSingle", node.toString());
		return json;
	}
	
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
	
	/**
	 * 下载模板
	 * @return
	 * @throws IOException
	 */
    @RequestMapping(baseUrl + "classification/downloadTemplete")
    public ResponseEntity<byte[]> download(HttpServletRequest request) throws IOException {
    	String moudleName = request.getParameter("moudleName");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = URLEncoder.encode("分类体系模板.xlsx", "UTF-8");
        headers.setContentDispositionFormData("attachment", filename);
        File excel = getExcelTemplate(moudleName);
//        File excel = new File(WebAppUtils.getWebAppRoot() + "system/dataManagement/excelTemplate/tbzy.xlsx");
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(excel),headers, HttpStatus.CREATED);
    }
    
    private File getExcelTemplate(String moudleName){
    	File excel = null;
    	if(moudleName.equals("knowledge"))
    		excel =  new File(WebAppUtils.getWebAppRoot() + "system/dataManagement/classification/template/knowledge.xlsx");
    	else if(moudleName.equals("ztzy"))
    		excel =  new File(WebAppUtils.getWebAppRoot() + "system/dataManagement/classification/template/ztzy.xlsx");
    	else if(moudleName.equals("tzzy"))
    		excel =  new File(WebAppUtils.getWebAppRoot() + "system/dataManagement/classification/template/tzzy.xlsx");
    	else if(moudleName.equals("jszyfz"))
    		excel =  new File(WebAppUtils.getWebAppRoot() + "system/dataManagement/classification/template/jszyfz.xlsx"); 
    	else if(moudleName.equals("tbzy"))
    		excel =  new File(WebAppUtils.getWebAppRoot() + "system/dataManagement/classification/template/tbzy.xlsx");
    	return excel;
    }
	
}
