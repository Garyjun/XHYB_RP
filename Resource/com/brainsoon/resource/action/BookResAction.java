package com.brainsoon.resource.action;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
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
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.docviewer.model.ResConverfileTask;
import com.brainsoon.resource.service.IAreaService;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.ICollectResService;
import com.brainsoon.resource.util.MD5Util;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.IBookService;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants.EducationPeriod;
import com.brainsoon.system.support.SystemConstants.NodeType;
import com.brainsoon.system.support.SystemConstants.ResourceType;
import com.google.gson.Gson;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE) 
public class BookResAction extends BaseAction{
	 public  static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
	 /** 默认命名空间 **/
	 private final String baseUrl = "/bookRes/";
	 
	 private static final String BOOK_CREATE="book_save";
	 private static final String BOOK_UPDATE="book_update";
	 
	 @Autowired
	 private IAreaService areaService;
	 @Autowired
	 private ICollectResService collectResService;
	 @Autowired
	 private IBookService bookService;
	 @Autowired
	 private IBaseSemanticSerivce baseSemanticSerivce;
	 @Autowired
	 private ISysParameterService sysParameterService;
	 @RequestMapping(value=baseUrl+"list")
	 public String list(Model model,Asset asset ) {
	    	 logger.debug("to list ");
	    	 String authCodes=LoginUserUtil.getAuthResCodes();
			 logger.debug("authCodes****** "+authCodes);
			 String authTypes=LoginUserUtil.getAuthResTypes();
			 logger.debug("authTypes****** "+authTypes);
	         model.addAttribute(Constants.COMMAND, asset);
	  	     return baseUrl+"bookResMain";
	 }
	 @RequestMapping(value=baseUrl+"query")
	 @ResponseBody
	 public String  query(HttpServletRequest request, HttpServletResponse response) {
		 QueryConditionList conditionList = getQueryConditionList();
		 String result= collectResService.queryResource4Page(request, conditionList,WebappConfigUtil.getParameter("BOOK_QUERY_URL"));
		 return result;
		    
	}
	 /**
	  * 列表查询
	  * @param request
	  * @param response
	  * @return
	  */
	 @RequestMapping(value=baseUrl+"bookResList")
	 public String bookResList(HttpServletRequest request, HttpServletResponse response) {
		 QueryConditionList conditionList = getQueryConditionList();
		 HttpSession session = getSession();
			List<SysParameter> enforceDelete = sysParameterService.findParaValue("enforceDelete");
			if(enforceDelete!=null && enforceDelete.size()>0&&enforceDelete.get(0).getParaStatus().toString().equals("1")){
				session.setAttribute("enforceDelete", enforceDelete.get(0).getParaValue());
			}else{
				session.removeAttribute("enforceDelete");
			}
		 return baseUrl+"bookResList";
		    
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
	public String edit(HttpServletRequest request,@ModelAttribute("frmAsset") Ca ca,@RequestParam("type") String type,@RequestParam("module") String module,ModelMap model) throws Exception{
			//查询省
			String goBackTask = request.getParameter("goBackTask");
			model.addAttribute("goBackTask", goBackTask);
			String targetBook = request.getParameter("targetBook");
			model.addAttribute("targetBook", targetBook);
			model.addAttribute("provinces", areaService.getProvince());
			model.addAttribute("module", module);
			String objectId = request.getParameter("objectId");
			model.addAttribute("type", type);
			if(!objectId.equals("-1")){
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail= http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL")+"?id="+objectId);
                Gson gson=new Gson();
                //将resourceDetail转换成json类型
                Ca bookCa=gson.fromJson(resourceDetail, Ca.class);
                if(bookCa!=null && bookCa.getCommonMetaData()!=null){
                	logger.debug(resourceDetail);
                	model.addAttribute("resourceDetail", resourceDetail);
                }else{
    				return "/error/errorUnRes";
    			}
			}
			model.addAttribute("objectId", objectId);
			model.addAttribute("type", type);
			return baseUrl + "bookResEdit";
	}
	/**
	 * 检查重复
	 * @param request
	 * @param response
	 * @return Map<String,Object>
	 */
	@RequestMapping(baseUrl + "checkRepeat")
	public @ResponseBody Map<String,Object> checkRepeat(HttpServletRequest request, HttpServletResponse response){
		String source = request.getParameter("source");
		String type = request.getParameter("type");
		String title = request.getParameter("title");
		String creator = request.getParameter("creator");
		String isbn = request.getParameter("isbn");
		String module = request.getParameter("module");
		List<Ca> rs = baseSemanticSerivce.getCaResourceByMoreCondition(source, type, title, creator,isbn,module);
		Map<String,Object> rtn = new HashMap<String, Object>();
		int status = 0;//不重复
		if(rs != null && rs.size() > 0){
			status = 1;
			rtn.put("res", rs);
		}
		rtn.put("status", status);
		return rtn;
	}
	@RequestMapping(baseUrl + "saveRes")
	public void saveRes(HttpServletRequest request, HttpServletResponse response,ModelMap model,@ModelAttribute("frmAsset") Ca ca,@RequestParam("uploadFile") String uploadFile) {
		logger.debug("******run at saveRes***********");
		try {
			String repeatType = request.getParameter("repeatType");
			collectResService.saveBookRes(ca, uploadFile,repeatType);
			UserInfo user = LoginUserUtil.getLoginUser();
			SysOperateLogUtils.addLog(BOOK_CREATE+"_"+ca.getCommonMetaData().getLibType(), ca.getCommonMetaData().getTitle(), user);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		    addActionError(e);
		}
	}
	
	@RequestMapping(baseUrl + "updateRes")
	public void updateRes(HttpServletRequest request, HttpServletResponse response,ModelMap model,@ModelAttribute("frmAsset") Ca ca) {
		logger.debug("******run at updateRes***********");
		try {
			collectResService.updateCollectRes(ca,"");
			UserInfo user = LoginUserUtil.getLoginUser();
			SysOperateLogUtils.addLog(BOOK_UPDATE+"_"+ca.getCommonMetaData().getLibType(), ca.getCommonMetaData().getTitle(), user);
		
		} catch (Exception e) {
			logger.error(e.getMessage());
		    addActionError(e);
		}
	}
	/**
	 * 下载资源
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping(baseUrl + "downloadBookRes/{libType}")
	public void downloadBookRes(HttpServletRequest request,HttpServletResponse response,@RequestParam("ids") String ids){
		String encryptPwd = request.getParameter("encryptPwd");
		collectResService.downloadBookRes(request, response, ids, encryptPwd);
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
		String boo = collectResService.fileSize(request, response, ids, encryptPwd);
		rtn.put("boo", boo);
		return rtn;
	}
	@RequestMapping(value = baseUrl + "updateNode")
	@ResponseBody
	public String updateNode(HttpServletRequest request,@RequestParam("nodeJson") String nodeJson,
			@RequestParam("title") String title) {
		logger.debug("******run at saveRes***********");
		String objectId = "-1";
		String oldPath = request.getParameter("oldPath");
		try {
			objectId = collectResService.updateNode(nodeJson,"", oldPath);
		} catch (Exception e) {
			logger.error(e.getMessage());
			objectId = "-1";
		}
		return objectId;
	}

	@RequestMapping(value = baseUrl + "deleteNode")
	@ResponseBody
	public String deleteNode(HttpServletRequest request) {
		logger.debug("******run at saveRes***********");
		String succ = "0";
		String nodeId = request.getParameter("nodeId");
		String caId = request.getParameter("caId");
		String path = request.getParameter("path");
		try {
			collectResService.deleteNode(caId, nodeId,path);
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
		    	realPath = realPath.replaceAll("\\\\", "/");
		    	String parentPathTemp = realPath.substring(0,realPath.lastIndexOf("/"));
		    	String newPath = parentPathTemp + File.separator + parentName;
		    	File realFile=new File(newPath);
		    	if(!realFile.exists()){
		    		realFile.mkdirs();
		    	}
		    	model.put("parentPath", newPath);
		    	model.put("parentName", parentName);
			} catch (Exception e) {
				logger.error(e);
			} 
	  	    return baseUrl+"selFile";
	 }
	 @RequestMapping(value=baseUrl+"getKnowledge")
	 @ResponseBody
	 public String getKnowledge(@RequestParam("educational_phase") String educational_phase,@RequestParam("subject") String subject) {
	    	 logger.debug("to getKnowledge ");
	    	 if(educational_phase.equals("")&&!subject.equals("")){
	    		 return "";
	    	 }
	  	     return bookService.getKnowledgeByParam(educational_phase+","+subject);
	 }
	 
	/**
	 * 跳转到信息页面
	 * @param ca
	 * @param objectId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(baseUrl+"detail") 
	public String detail(HttpServletRequest request,@RequestParam("objectId") String objectId,ModelMap model) throws Exception{
			//查询省
			HttpClientUtil http = new HttpClientUtil();
			String resourceDetail= http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL")+"?id="+objectId);
			Gson gson=new Gson();
			Ca ca=gson.fromJson(resourceDetail, Ca.class);
			String flag = request.getParameter("flag");
			if(ca!=null && ca.getCommonMetaData()!=null){
				model.addAttribute("flag", flag);
				model.addAttribute("ca", ca);
	            model.addAttribute("resourceDetail", resourceDetail);
				model.addAttribute("objectId", objectId);
				model.addAttribute("module", ca.getCommonMetaData().getModule());
				model.addAttribute("type", ca.getCommonMetaData().getType());
			}else{
				return "/error/errorUnRes";
			}
			return baseUrl + "bookResDetail";
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
			System.out.println("文件未上传");
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
		rtn.put("status", status);
		rtn.put("filePath", uploadFile.replace(File.separator, "/").replace(FILE_ROOT, ""));
		return rtn;
	}
	
}
