package com.brainsoon.resource.action;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.po.CopyrightImportResult;
import com.brainsoon.resource.po.CopyrightRepeat;
import com.brainsoon.resource.po.CopyrightWarning;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.ICollectResService;
import com.brainsoon.resource.service.ICopyrightService;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.semantic.ontology.model.CopyRightMetaData;
import com.brainsoon.semantic.ontology.model.FileList;
import com.brainsoon.semantic.ontology.model.JsonDataObject;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.google.gson.Gson;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * 版权管理
 * 
 * @author zhangpeng
 */
@Controller
public class CopyrightAction extends BaseAction {

	/** 默认命名空间 **/
	private final String baseUrl = "/copyright/";

	public final static String FILE_TEMP = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp);
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	private ICopyrightService copyrightService;
	@Autowired
	private ICollectResService collectResService;
	private static final String COPYRIGHTWARNING_DELALL_URL = WebappConfigUtil.getParameter("COPYRIGHTWARNING_DELALL_URL");
	/**
	 * 详细
	 * @param objectId
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("getFilesByDoi")
	public @ResponseBody String getFilesByDoi(@RequestParam("doi") String doi) throws Exception {
    	FileList fileList = copyrightService.getFilesByDoi(doi);
    	JsonDataObject result = new JsonDataObject();
    	if(fileList !=null && fileList.getFiles()!=null && fileList.getFiles().size()>0){
    		result.setStatus("0");
        	result.setMsg("成功");
        	result.setData(fileList);
    	}else{
    		result.setStatus("-1");
        	result.setMsg("失败：文件不存在");
        	result.setData(fileList);
    	}
    	Gson gson = new Gson();
    	String resultValue = gson.toJson(result);
		return resultValue;
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
	public void list(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		String url = "";
		String type = request.getParameter("type");
		if("T06".equals(type)){
			url = WebappConfigUtil.getParameter("BOOK_QUERY_URL");
		}else{
			url = WebappConfigUtil.getParameter("ASSET_LIST_URL");
		}
		outputResult(collectResService.queryResource4Page(request, conditionList,url));
	}
	
	@RequestMapping(value="/toListWarning")
 	 public String toListWarning(Model model,CopyrightWarning copyrightWarning) {
 	    	 logger.debug("to list ");
 	         model.addAttribute(Constants.COMMAND, copyrightWarning);
 	  	     return "/copyright/warningList";
 	 }
	/**
	 * 重复资源列表查询
	 * 
	 * @param request
	 * @param response
	 * @param params
	 * @return json
	 */
	@RequestMapping(value = baseUrl + "/repeatList")
	@ResponseBody
	public PageResult listRepeat(CopyrightRepeat copyrightRepeat) {
		logger.info("进入查询方法");
		return copyrightService.queryCopyrightRepeats(getPageInfo(), copyrightRepeat);
	}
	/**
	 * 批量导入日志列表查询
	 * @param request
	 * @param response
	 * @param params
	 * @return json
	 */
	@RequestMapping(value = baseUrl + "/importResultList")
	@ResponseBody
	public PageResult importResultList(CopyrightImportResult copyrightImportResult) {
		logger.info("进入查询方法");
		return copyrightService.queryCopyrightImportResults(getPageInfo(), copyrightImportResult);
	}
	/**
	 * 获取资源的文件列表
	 * @param response
	 * @param objectId
	 * @return
	 */
	@RequestMapping(value = baseUrl +"/getRepeatRes")
	public @ResponseBody Map<String, Object> getRepeatRes(HttpServletRequest request,HttpServletResponse response){
		String source = request.getParameter("source");
		String type = request.getParameter("type");
		String title = request.getParameter("title");
		String creator = request.getParameter("creator");
	//	List<Ca> rs = baseSemanticSerivce.getCaResourceByMoreCondition(source, type, title, creator,"");
		Map<String,Object> rtn = new HashMap<String, Object>();
	//	rtn.put("total", rs.size());
	//	rtn.put("rows", rs);
		return rtn;
	}
	
	/**
	 * 资源关联版权
	 * @param objectId
	 * @return
	 */
	@RequestMapping(value = baseUrl +"/doRelateCopyright")
	public @ResponseBody void doRelateCopyright(HttpServletRequest request,HttpServletResponse response){
		String id = request.getParameter("id");
		String objectId = request.getParameter("objectId");
		String rtn = copyrightService.doRelateCopyright(Integer.parseInt(id), objectId);
		SysOperateLogUtils.addLog("copyright_relate", "版权", LoginUserUtil.getLoginUser());
		outputResult(rtn);
	}
	/**
	 * 获取资源的文件列表
	 * @param response
	 * @param objectId
	 * @return
	 */
	@RequestMapping(value = baseUrl +"/gotoRepeatRes")
	public String gotoRepeatRes(@ModelAttribute CopyrightRepeat copyrightRepeat, HttpServletRequest request,HttpServletResponse response,Model model){
		CopyrightRepeat copyrightRepeatTemp = (CopyrightRepeat) baseService.getByPk(CopyrightRepeat.class, copyrightRepeat.getId());
		model.addAttribute("copyrightRepeat",copyrightRepeatTemp);//存储值
		return "/copyright/doRepeatRes";
	}
	/**
	 * 版权预警列表查询
	 * @param request
	 * @param response
	 * @param params
	 * @return json
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value = baseUrl + "/warningList")
	@ResponseBody
	public PageResult listWarning(CopyRightMetaData copyRightMetaData,HttpServletRequest request) {
		String publishType = request.getParameter("publishType");
		String title = request.getParameter("title");
		String type = request.getParameter("type");
		String licenEndTime = request.getParameter("licenEndTime");
		String contractCode = request.getParameter("contractCode");
		String ownership = request.getParameter("ownership");
		String autherName = request.getParameter("autherName");
		HttpSession session = getSession();
		QueryConditionList conditionList = getQueryConditionList();
		HttpClientUtil http = new HttpClientUtil();
		PageInfo pageInfo = getPageInfo();
		 try {
			 if(StringUtils.isNotBlank(title))
				 title=  URLDecoder.decode(title,"UTF-8");
			 if(StringUtils.isNotBlank(autherName))
				 autherName=  URLDecoder.decode(autherName,"UTF-8");
			 if(StringUtils.isNotBlank(contractCode))
				 contractCode=  URLDecoder.decode(contractCode,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		String hql = " from CopyrightWarning where 1=1";
		if(StringUtils.isNotBlank(publishType)){
			hql+=" and publishType='"+publishType+"'";
		}
		if(StringUtils.isNotBlank(title)){
			hql+=" and title='"+title+"'";
		}
		if(StringUtils.isNotBlank(type)){
			hql+=" and type='"+type+"'";
		}
		List<CopyrightWarning> warningList = null;
		List<CopyrightWarning> resultWarning = new ArrayList<CopyrightWarning>();
		int size = pageInfo.getRows();
		int page = pageInfo.getPage();
		int startIndex = (page-1)*size;
			warningList =  copyrightService.query(hql);
			int total = warningList.size();
			int endIndex = 0;
			if(total<size*page){
				endIndex = startIndex+total-(page-1)*size;
			}else{
				endIndex = startIndex+size;
			}
			for(int i=0;i<warningList.size();i++){
				if(i >= startIndex && i< endIndex){
					resultWarning.add(warningList.get(i));
				}
			}
		session.setAttribute("goBack", 1);
		PageResult result = new PageResult();
		result.setRows(resultWarning);
		
		if(warningList!=null){
			result.setTotal(warningList.size());
		}else{
			result.setTotal(0);
		}
		return result;
	}
	/**
	 * 版权详细
	 * @param id
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "detail")
	public String detail(@RequestParam("identifier") String identifier,HttpServletResponse response,ModelMap model,HttpServletRequest request){
		logger.info("查看标签");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String main = request.getParameter("main");
		HttpClientUtil http = new HttpClientUtil();
		String returnCopyRightMetaData = "";
		CopyRightMetaData  copyRightMetaData = null;
		if(StringUtils.isNotBlank(identifier)){
			identifier = "urn:publish-"+identifier;
			returnCopyRightMetaData=  http.executeGet(WebappConfigUtil
					.getParameter("COPYRIGHT_METADATA_DELALL_URL") + "&identifer=" + identifier);
			if(StringUtils.isNotBlank(returnCopyRightMetaData)){
				JSONObject obj = new JSONObject();
				JSONObject ob= (JSONObject) obj.fromObject(returnCopyRightMetaData);
				Integer  status = (Integer)ob.get("status");
				if(status!=-1){
					JSONObject objData = ResUtils.copyrigntMedata(returnCopyRightMetaData);
					copyRightMetaData = (CopyRightMetaData) JSONObject.toBean(objData, new CopyRightMetaData(), new JsonConfig());
				}
			}
		}
//		Gson gson = new Gson();
//		CopyRightMetaData copyRightMetaData = gson.fromJson(jsonData, CopyRightMetaData.class);
//		CopyrightWarning copyRightWarning = (CopyrightWarning) copyrightService.getByPk(CopyrightWarning.class, Integer.parseInt(id));
		model.addAttribute("frmWord", copyRightMetaData);
		model.addAttribute("main", main);
		SysOperateLogUtils.addLog("copyWarning_detail", userInfo.getUsername(),userInfo);
		return baseUrl + "copyrightWarnDetail";
	}
	/**
	 * 下载模版
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping(baseUrl + "downloadCopyrightExcel")
	public void downloadCopyrightExcel(HttpServletRequest request,HttpServletResponse response){
		try {
			String copyrightPath = request.getSession().getServletContext().getRealPath("")+File.separator+ "WEB-INF"+File.separator+"classes"+File.separator+"CopyrightImport.xls";
			resourceService.downloadFile(request, response, copyrightPath, false);
		} catch (Exception e) {
			logger.info("下载版权模版失败："+e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * 下载批量导入日志
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping(baseUrl + "downloadCopyrightResult")
	public void downloadCopyrightResult(HttpServletRequest request,HttpServletResponse response){
		try {
			String id = request.getParameter("id");
			CopyrightImportResult copyrightImportResult = (CopyrightImportResult) baseService.getByPk(CopyrightImportResult.class, Integer.parseInt(id));
			String path =  copyrightImportResult.getVirtualName();
			String filePath = "D://3/"+path+File.separator+path+".xls";
			resourceService.downloadFile(request, response, filePath, false);
		} catch (Exception e) {
			logger.info("下载版权模版失败："+e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * 删除导入日志
	 * @param id
	 * @return
	 */
	@RequestMapping(baseUrl + "delResult")
	public String delete(@RequestParam Integer id) {
		try {
			copyrightService.delete(CopyrightImportResult.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return "redirect:/copyright/list.action";
	}
	
	/**
	 * 删除重复资源信息
	 * @param id
	 * @return
	 */
	@RequestMapping(baseUrl + "delRepeat")
	public String deleteRepeat(@RequestParam Integer id) {
		try {
			copyrightService.delete(CopyrightRepeat.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return "redirect:/copyright/repeatList.action";
	}
	/**
//	 * 批量导入版权
//	 * @param request
//	 * @param response
//	 * @param model
//	 * @return
//	 */
//	@RequestMapping(value = baseUrl + "importCopyright")
//	public @ResponseBody HashMap<String, Object> importCopyright(HttpServletRequest request, HttpServletResponse response,ModelMap model){
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
//			new File(folder).mkdirs();
//			File restore = new File(folder + File.separator + fileName);
//			try {
//				multipartFile.transferTo(restore);
//			} catch (Exception e) {
//				status = -1;
//			}
//			CopyrightTaskQueue.getInst().addMessage(restore);
//		}
//		rtn.put("status", status);
//		rtn.put("path", flag);
//		SysOperateLogUtils.addLog("copyright_import", "版权", LoginUserUtil.getLoginUser());
//		return rtn;
//	}
	public final  static void main(String[] args){
		System.out.println("++++++++++++"+CopyrightAction.class.getClass().getResource("/").getPath()+"+++++++++++++");  
		File file = new File(CopyrightAction.class.getClass().getResource("/").getPath());
		System.out.println(file.list().length);
	}
}
