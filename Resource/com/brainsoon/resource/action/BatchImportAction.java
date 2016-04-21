package com.brainsoon.resource.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.po.UploadTaskDetail;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.support.ExcelUtil;
import com.brainsoon.resource.support.ImportResExcelFile;
import com.brainsoon.resource.support.ImportResThread;
import com.brainsoon.resource.support.TaskQueue;

/**
 * 批量导入资源相关
 * @author zuo
 */
@Controller
public class BatchImportAction extends BaseAction {

	/** 默认命名空间 **/
	private final String baseUrl = "/bres/import/";

	public final static String FILE_TEMP = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp);
	public  static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
	/** 注入业务类 **/
	@Autowired
	private IResourceService resourceService;
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
	 * 处理Excel数据
	 * @param request
	 * @param response
	 * @param params
	 * @return json
	 */
	@RequestMapping(value = baseUrl + "doExcelData")
	public String doExcelData(){
		logger.info("tttttttttttttttttttttt");
		List<Long> ids = new ArrayList<Long>();
//		ids.add(84l);
//		ids.add(73l);
//		ids.add(74l);
//		ids.add(75l);
		for(Long id:ids){
			logger.info("111111111111111111111");
			UploadTask task = (UploadTask) resourceService.getByPk(UploadTask.class, id);
			String excelFailPath = task.getFailExcelPath();
			String uuid = UUID.randomUUID().toString();
			String excelDir = FILE_ROOT+"failExcelDir"+File.separator+uuid+File.separator;
			Map<Integer,String> resultLog = new HashMap<Integer,String>();
			Map<Integer,String> fileNotExistLog = new HashMap<Integer,String>();
			File excelDirFile = new File(excelDir);
			if(!excelDirFile.exists()){
				excelDirFile.mkdirs();
			}
			logger.info("22222222222222222222222222");
			int allRows = ExcelUtil.parseExcelFile(excelFailPath, resultLog, fileNotExistLog);
			logger.info("3333333333333333333333");
			if(resultLog.size()>0){
				uuid = UUID.randomUUID().toString();
				String failExcelPath = excelDir+"失败日志(不包含文件不存在).xls";
				ExcelUtil.insertFileNotExistLog(excelFailPath,failExcelPath,resultLog,allRows-3);
				logger.info("444444444444444444444444444");
			//	ExcelUtil.removeBlankRow(tempExcelPath,failExcelPath);
				logger.info("5555555555555555555555");
				failExcelPath = failExcelPath.replaceAll("\\\\", "/");
				task.setFailExcelPath(failExcelPath);
			}
			if(fileNotExistLog.size()>0){
				uuid = UUID.randomUUID().toString();
				String fileNotExistPath = excelDir+"文件不存在日志.xls";
				ExcelUtil.insertFileNotExistLog(excelFailPath,fileNotExistPath,fileNotExistLog,allRows-3);
				logger.info("666666666666666666666666");
				uuid = UUID.randomUUID().toString();
			//	ExcelUtil.removeBlankRow(fileNotExistTemp,fileNotExistPath);
				fileNotExistPath = fileNotExistPath.replaceAll("\\\\", "/");
				task.setFileNotExistPath(fileNotExistPath);
			}
			resourceService.update(task);
			logger.info("777777777777777777777777");
		}
		return baseUrl + "list";
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
	public String detail(@RequestParam("id") Long id,ModelMap model) throws Exception{
		model.addAttribute("task",resourceService.getByPk(UploadTask.class, id));
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
		QueryConditionList conditionList = getQueryConditionListWithNoPlat();
		return resourceService.query4Page(UploadTaskDetail.class, conditionList);
	}
	/**
	 * 批量上传
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = baseUrl + "saveRes")
	public @ResponseBody HashMap<String, Object> saveRes(HttpServletRequest request, HttpServletResponse response,ModelMap model){
		HashMap<String, Object> rtn = new HashMap<String, Object>();
		int status = 0;
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		MultipartFile multipartFile = null;
		for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
			multipartFile = set.getValue();
		}
		String flag = "";
		String fileName = multipartFile.getOriginalFilename();// 文件名
		if (multipartFile.isEmpty()||multipartFile.getSize()<=0) {
			logger.error("文件未上传");
			status = 1;
		} else {
			/**使用UUID生成文件名称**/
			logger.info("添加批量导入队列==============================");
			flag = UUID.randomUUID().toString();
			String folder = FILE_TEMP + flag;
			new File(folder).mkdir();
			File restore = new File(folder + File.separator + fileName);
			try {
				multipartFile.transferTo(restore);
			} catch (Exception e) {
				status = -1;
			}
			
			String repeatType = request.getParameter("repeatType");
			String module = request.getParameter("module");
			String remark = request.getParameter("remark");
			String repeatRelevanceType = request.getParameter("repeatRelevanceType");
			String libType = request.getParameter("libType");
			logger.debug(repeatType+"=====================");
			
			//添加解析任务
			ImportResExcelFile message = new ImportResExcelFile();
			message.setExcel(restore);
			message.setModule(module);
			message.setRepeatType(repeatType);
			message.setRemark(remark);
			message.setRepeatRelevanceType(repeatRelevanceType);
			message.setLibType(libType);
			logger.debug("导入111=====================");
			UserInfo user = LoginUserUtil.getLoginUser();
			message.setUserInfo(user);
			message.setUserId(user.getUserId());
			message.setLoginIp(user.getLoginIp());
			message.setUsername(user.getUsername());
			message.setPlatformId(user.getPlatformId());
			message.setFileDir(FILE_ROOT);
			logger.debug("导入2222====================="+LoginUserUtil.getLoginUser());
			TaskQueue.getInst().addMessage(message);
		}
		rtn.put("status", status);
		rtn.put("path", flag);
		return rtn;
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
}
