package com.brainsoon.resource.action;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.po.UploadTaskDetail;
import com.brainsoon.resource.service.IBatchImportResService;
import com.brainsoon.resource.service.IPublishResService;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.support.ImportResExcelFile;
import com.brainsoon.resource.support.ImportResThread;
import com.brainsoon.resource.support.TaskQueue;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SystemConstants.BatchImportDetaillType;
import com.brainsoon.system.support.SystemConstants.ImportStatus;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * 批量导入资源相关
 * @author zuo
 */
@Controller
public class BatchImportPublishAction extends BaseAction {

	/** 默认命名空间 **/
	private final String baseUrl = "/publishRes/import/";

	public final static String FILE_TEMP = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp);
	public  static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
	/** 注入业务类 **/
	@Autowired
	private IResourceService resourceService;
	
	@Autowired
	private IPublishResService publishResService;
	
	@Autowired
	private IBatchImportResService batchImportResService;
	
	/**
	 * 查看队列里的excel表格
	 * @param request
	 * @param response
	 * @param params
	 * @return json
	 */
	@RequestMapping(value = baseUrl + "lookQueueExcel")
	public @ResponseBody String lookQueueExcel(){
		TaskQueue queue = TaskQueue.getInst();
		String excelNames = "";
		Object[] importResExcelFiles = queue.getQueueObj();
		for(Object importResExcelFile:importResExcelFiles){
			excelNames += ((ImportResExcelFile)importResExcelFile).getExcel().getName()+",";
		}
		if(!"".equals(excelNames)){
			excelNames = excelNames.substring(0,excelNames.length()-1);
		}
		return excelNames;
	}

	/**
	 * 列表查询
	 * @param request
	 * @param response
	 * @param params
	 * @return json
	 */
	@RequestMapping(value = baseUrl + "list")
	public @ResponseBody PageResult list(HttpServletRequest request,HttpServletResponse response){
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		return resourceService.query4Page(UploadTask.class, conditionList);
	}
	/**
	 * 查询详细
	 * @param id
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(baseUrl+"detail") 
	public String detail(HttpServletRequest request,ModelMap model) throws Exception{
		Long id = (long) 0;
		id =Long.parseLong(request.getParameter("id"));
		UploadTask uploadTask = (UploadTask) resourceService.getByPk(UploadTask.class, id);
		model.addAttribute("taskIdAgain",id);
		model.addAttribute("excelPath",uploadTask.getExcelPath());
		model.addAttribute("task",uploadTask);
		return baseUrl + "detail";
	}
	/**
	 * 详细列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = baseUrl + "detailList")
	public @ResponseBody PageResult detailList(HttpServletRequest request,HttpServletResponse response){
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		return publishResService.queryBeachImportDetaill(UploadTaskDetail.class, conditionList);
	}
	/**
	 * 批量上传
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
//	@RequestMapping(value = baseUrl + "saveRes")
//	public @ResponseBody HashMap<String, Object> saveRes(HttpServletRequest request, HttpServletResponse response,ModelMap model){
//		HashMap<String, Object> rtn = new HashMap<String, Object>();
//		int status = 0;
//		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
//		MultipartFile multipartFile = null;
//		for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
//			multipartFile = set.getValue();// 文件名
//		}
//		String flag = "";
//		String fileName = multipartFile.getOriginalFilename();
//		if (multipartFile.isEmpty()) {
//			logger.error("文件未上传");
//			status = 1;
//		} else {
//			/**使用UUID生成文件名称**/
//			flag = UUID.randomUUID().toString();
//			String folder = FILE_TEMP + flag;
//			new File(folder).mkdir();
//			File restore = new File(folder + File.separator + fileName);
//			try {
//				multipartFile.transferTo(restore);
//			} catch (Exception e) {
//				status = -1;
//			}
//			
//			String repeatType = request.getParameter("repeatType");
//			String publishType = request.getParameter("publishType");
//			String remark = request.getParameter("remark");
//			String processTask = request.getParameter("processTask");
//			logger.debug(repeatType);
//			
//			//添加解析任务
//			ImportResExcelFile message = new ImportResExcelFile();
//			message.setName(fileName);
//			message.setModule(publishType);
//			message.setExcel(restore);
//			message.setRepeatType(repeatType);
//			message.setRemark(remark);
//			UserInfo user = LoginUserUtil.getLoginUser();
//			message.setUserInfo(user);
//			message.setUserId(user.getUserId());
//			message.setLoginIp(user.getLoginIp());
//			message.setUsername(user.getUsername());
//			message.setPlatformId(user.getPlatformId());
//			message.setFileDir(FILE_ROOT);
//			message.setProcessTask(processTask);
//			TaskQueue.getInst().addMessage(message);
//			SysOperateLogUtils.addLog("publish_import", fileName, user);
//		}
//		rtn.put("status", status);
//		rtn.put("path", flag);
//		return rtn;
//	}
	/**
	 * 批量上传解析excel数据存放到本地mysql中
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = baseUrl + "saveRes")
	public @ResponseBody String  saveResWithMysql(HttpServletRequest request, HttpServletResponse response,ModelMap model){
		HashMap<String, Object> rtn = new HashMap<String, Object>();
		String status =  batchImportResService.saveRes(request);
		return status;
	}
	/**
	 * 执行删除操作
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(baseUrl + "delTaskInfo")
	public void delTaskInfo(HttpServletResponse response,@RequestParam("ids") String ids) throws IOException{
		resourceService.delTaskInfo(ids);
		outputResult("删除成功");
	}
	
	@RequestMapping(value=baseUrl+"getXmlJson")
	@ResponseBody
	public String getXmlJson(HttpServletRequest request,HttpServletResponse response) throws IOException{
		/*
		try {
			UploadTask uploadTask =new UploadTask();
			uploadTask.setLibType("1");
			batchImportResService.savefile_new("C:/temp/test/1/",uploadTask,new UploadTaskDetail());
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		String returnJson = batchImportResService.getXmlJson();
        return returnJson;
	}
	@RequestMapping(value=baseUrl+"saveResByXml")
	public @ResponseBody String saveResByXml(HttpServletRequest request,HttpServletResponse response)throws Exception{
		String paths = request.getParameter("paths");
		String remark = request.getParameter("remark");
		String repeatType = request.getParameter("repeatType");
		String publishType = request.getParameter("publishType");
		
		paths=URLDecoder.decode(paths,"UTF-8");
		if (paths.endsWith(",")) {
			paths=paths.substring(0, paths.length()-1);
		}
		return batchImportResService.saveResByXml(paths,remark,repeatType,publishType);
		
	}
	
	/**
	 * 批量上传解析saveAndAnalyzeExcel数据存放到本地中
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = baseUrl + "saveAndAnalyzeExcel", method = {RequestMethod.POST })
	public @ResponseBody String  saveAndAnalyzeExcel(HttpServletRequest request, HttpServletResponse response,ModelMap model){
		String excelPath =  batchImportResService.saveAnalyzeExcel(request);
		if(StringUtils.isNotBlank(excelPath)){
			return excelPath;
//			return "redirect:"+baseUrl+"downAnalyzeExcel.action?excelPath="+excelPath;
		}else{
			return null;
		}
	}
	/**
	 * 批量上传解析saveAndAnalyzeExcel数据存放到本地中
	 * @param request
	 * @param response
	 * @throws Exception 
	 * @return
	 */
	@RequestMapping(value = baseUrl + "downAnalyzeExcel")
	public @ResponseBody ResponseEntity<byte[]> downAnalyzeExcel(HttpServletRequest request)  throws Exception {
		HashMap<String, Object> rtn = new HashMap<String, Object>();
		String path = request.getParameter("excelPath");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String filename = URLEncoder.encode("元数据模板.xls", "UTF-8");
		headers.setContentDispositionFormData("attachment", filename);
		File excelFile = new File(path);
		return new ResponseEntity<byte[]>(
				FileUtils.readFileToByteArray(excelFile), headers,
				HttpStatus.OK);
	}

	
	@RequestMapping(value = baseUrl + "batchContinue")
	public @ResponseBody String batchContinue(HttpServletRequest request)  throws Exception {
		new Thread(new ImportResThread()).start();
		return "SUCCESS";
	}
}
