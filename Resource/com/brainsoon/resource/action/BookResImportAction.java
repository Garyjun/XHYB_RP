package com.brainsoon.resource.action;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.service.ICollectResService;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.support.ImportResExcelFile;
import com.brainsoon.resource.support.TaskQueue;
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE) 
public class BookResImportAction extends BaseAction{
	/** 默认命名空间 **/
	private final String baseUrl = "/bookRes/import/";
	public final static String FILE_TEMP = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp);
	/** 注入业务类 **/
	@Autowired
	private IResourceService resourceService;
	
	@Autowired
	private ICollectResService collectResService;
	
	/**
	 * 下载模板
	 * @return
	 * @throws IOException
	 */
    @RequestMapping(value = baseUrl + "downBookTemplete")
    public ResponseEntity<byte[]> download() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = URLEncoder.encode("图书资源导入模板.xls", "UTF-8");
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(this.getClass().getResource("/").getPath()+"bookResImportTemplete.xls")),headers, HttpStatus.CREATED);
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
	 * 批量上传
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = baseUrl + "importRes")
	public @ResponseBody HashMap<String, Object> importRes(HttpServletRequest request, HttpServletResponse response,ModelMap model){
		HashMap<String, Object> rtn = new HashMap<String, Object>();
		int status = 0;
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		MultipartFile multipartFile = null;
		for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
			multipartFile = set.getValue();// 文件名
		}
		String flag = "";
		String fileName = multipartFile.getOriginalFilename();
		if (multipartFile.isEmpty()) {
			logger.error("文件未上传");
			status = 1;
		} else {
			/**使用UUID生成文件名称**/
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
	//		String repeatRelevanceType = request.getParameter("repeatRelevanceType");
			String libType = request.getParameter("libType");
			logger.debug(repeatType);
			
			//添加解析任务
			ImportResExcelFile taskData = new ImportResExcelFile();
			taskData.setExcel(restore);
			taskData.setModule(module);
			taskData.setRepeatType(repeatType);
			taskData.setRemark(remark);
	//		taskData.setRepeatRelevanceType(repeatRelevanceType);
			taskData.setLibType(libType);
			try {
				collectResService.doImportTask(taskData);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				status = -1;
				e.printStackTrace();
			}
		}
		rtn.put("status", status);
		rtn.put("path", flag);
		return rtn;
	}
}
