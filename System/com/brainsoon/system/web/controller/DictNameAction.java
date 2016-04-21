package com.brainsoon.system.web.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.IDictValueService;
import com.brainsoon.system.support.SysOperateLogUtils;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DictNameAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/system/dataManagement/dataDict/";
	@Autowired
	private IDictNameService dictNameService;
	
	@Autowired
	private IDictValueService dictValueService;
	UserInfo userInfo =  LoginUserUtil.getLoginUser();
	@RequestMapping(baseUrl + "list")
	public @ResponseBody PageResult list(HttpServletRequest request,HttpServletResponse response){
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		return dictNameService.query4Page(DictName.class, conditionList);
	}
	
	@RequestMapping(baseUrl + "listValue")
	public @ResponseBody PageResult listValue(HttpServletRequest request,HttpServletResponse response){
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		Long pid = Long.parseLong(request.getParameter("dictNameId"));
		conditionList.addCondition(new QueryConditionItem("pid", Operator.EQUAL, pid));
		return dictValueService.query4Page(DictValue.class, conditionList);
	}
	
	
	
	@RequestMapping(baseUrl + "validateDictName")
	public @ResponseBody String validateDictName(HttpServletRequest request){
		logger.info("进入查询方法");
		int platformId = userInfo.getPlatformId();
		String id = null;
		String dictName = request.getParameter("fieldValue");
		String dictNameId = request.getParameter("dictNameId");
		String sql = "select id,name from dict_name where name='"+dictName+"' and platformId="+platformId+"";
		List nameList = dictValueService.validateDictName(sql);
		Iterator it = nameList.iterator();    
		while(it.hasNext()) {    
		    Map map = (Map) it.next();
		    id = map.get("id").toString();
		   
		} 
		if(nameList.size()==1){
			if(dictNameId.equals(id)) {
				return "{\"jsonValidateReturn\": [\"dictName\",true]}";
			} else {
				return "{\"jsonValidateReturn\": [\"dictName\",false]}";
			}
		}
		return "{\"jsonValidateReturn\": [\"dictName\",true]}";
	}
	
	@RequestMapping(baseUrl + "validateDictNameAdd")
	public @ResponseBody String validateDictNameAdd(HttpServletRequest request){
		logger.info("进入查询方法");
		int platformId = userInfo.getPlatformId();
		String dictName = request.getParameter("fieldValue");
		String dictId = request.getParameter("fieldId");
		String sql = "select name from dict_name where name='"+dictName+"' and platformId="+platformId+"";
		List nameList = dictValueService.validateDictName(sql);
		
		if(nameList.size()<1){
			return "{\"jsonValidateReturn\": [\"dictName\",true]}";
		}
		return "{\"jsonValidateReturn\": [\"dictName\",false]}";
	}
	
	/**
	 * 跳转到添加页面
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"add") 
	@Token(save=true)
	public String add(@ModelAttribute("frmDictName") DictName dictName,ModelMap model) throws Exception{
		return baseUrl + "dictNameAdd";
	}
	
	@RequestMapping(baseUrl+"addDictValue") 
	@Token(save=true)
	public String addDictValue(@RequestParam("pid") Long pid,HttpServletResponse response,ModelMap model) throws Exception{
		DictValue dictValue = new DictValue();
		dictValue.setPid(pid);
		model.addAttribute("frmDictValue", dictValue);
		return baseUrl + "dictValueAdd";
	}
	
	/**
	 * 执行添加操作
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "addAction")
	@Token(remove=true)
	public @ResponseBody String addAction(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmDictName") DictName dictName,Model model){
		logger.info("进入添加方法");
		try {
			int platformId = userInfo.getPlatformId();
			dictName.setPlatformId(userInfo.getPlatformId());
			dictNameService.addDictName(dictName);
			if(platformId==1) {
				SysOperateLogUtils.addLog("dictName_add", dictName.getName(), userInfo);
			}else{
				SysOperateLogUtils.addLog("pud_dictName_add", dictName.getName(), userInfo);
			}
			
		} catch (Exception e) {
			addActionError(e);
		}
		
		String id = dictName.getId().toString();
		return id;
	}
	
	@RequestMapping(baseUrl + "addDictValueAction")
	@Token(remove=true)
	public String addDictValueAction(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmDictValue") DictValue dictValue,ModelMap model){
		logger.info("进入添加方法");
		try {
			DictName dictName = (DictName) dictNameService.getByPk(DictName.class, dictValue.getPid());
			dictValue.setPlatformId(userInfo.getPlatformId());
			dictValueService.addDictValue(dictValue,dictName.getIndexTag());
			GlobalDataCacheMap.refresh(dictName.getIndexTag());   //刷新某每个数据字典
		} catch (Exception e) {
			addActionError(e);
		}
		return "redirect:"+baseUrl + "upd.action?id=" + dictValue.getPid();
	}
	
	/**
	 * 跳转到更新页面
	 * @param id
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "upd")
	@Token(save=true)
	public String upd(@RequestParam("id") Long id,HttpServletResponse response,ModelMap model){
		logger.info("进入修改页面");
		DictName dictName = (DictName)dictNameService.getByPk(DictName.class, id);
		model.addAttribute("frmDictName", dictName);
		return baseUrl + "dictNameEdit";
	}
	
	@RequestMapping(baseUrl + "dictValueUpd")
	@Token(save=true)
	public String dictValueUpd(@RequestParam("id") Long id,HttpServletResponse response,ModelMap model){
		logger.info("进入修改页面");
		DictValue dictValue = (DictValue)dictValueService.getByPk(DictValue.class, id);
		model.addAttribute("frmDictValue", dictValue);
		return baseUrl + "dictValueEdit";
	}
	
	/**
	 * 执行更新操作
	 * @param request
	 * @param response
	 * @param order
	 * @param model
	 */
	@RequestMapping(baseUrl + "updAction")
	@Token(remove=true)
	public String updAction(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmDictName") DictName dictName,ModelMap model){
		logger.info("进入修改方法");
		try {
			int platformId = userInfo.getPlatformId();
			dictName.setPlatformId(userInfo.getPlatformId());
			dictNameService.updDictName(dictName);
			if(platformId==1){
				SysOperateLogUtils.addLog("dictName_update", dictName.getName(), userInfo);
			}else{
				SysOperateLogUtils.addLog("pub_dictName_update", dictName.getName(), userInfo);
			}
		} catch (Exception e) {
			addActionError(e);
		}
		return baseUrl + "dictNameList";
	}
	
	@RequestMapping(baseUrl + "updDictValue")
	@Token(remove=true)
	public void updDictValue(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmDictName") DictValue dictValue,ModelMap model){
		logger.info("进入修改方法");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		try {
			dictValue.setPlatformId(userInfo.getPlatformId());
			DictName dictName = (DictName)dictNameService.getByPk(DictName.class, dictValue.getPid());
			dictValueService.updDictValue(dictValue,dictName.getIndexTag());
			GlobalDataCacheMap.refresh(dictName.getIndexTag());   //刷新某每个数据字典
		} catch (Exception e) {
			addActionError(e);
		}
		outputResult("ok");
	}
	
	/**
	 * 执行删除操作
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(baseUrl + "delAction")
	public void delAction(HttpServletResponse response,@RequestParam("ids") String ids) throws IOException{
		logger.info("进入删除方法");
		dictNameService.deleteByIds(ids);
		outputResult("删除成功");
	}
	
	@RequestMapping(baseUrl + "delDictValueAction")
	public void delDictValueAction(HttpServletResponse response,@RequestParam("ids") String ids) throws IOException{
		logger.info("进入删除方法");
		String[] idArray = ids.split(",");
		DictName dictName = dictNameService.getDictNameByValueId(Long.parseLong(idArray[0]));
		dictValueService.deleteByIds(ids,dictName.getIndexTag());
		GlobalDataCacheMap.refresh(dictName.getIndexTag());   //刷新某每个数据字典
		outputResult("删除成功");
	}
	
	@RequestMapping(baseUrl + "dictNameDetail")
	@Token(save=true)
	public String dictNameDetail(@RequestParam("id") Long id,HttpServletResponse response,ModelMap model){
		logger.info("进入查看页面");
		DictName dictName = (DictName)dictNameService.getByPk(DictName.class, id);
		model.addAttribute("frmDictName", dictName);
		return baseUrl + "dictNameDetail";
	}
	
	@RequestMapping(baseUrl + "dictValueDetail")
	@Token(save=true)
	public String dictValueDetail(@RequestParam("id") Long id,HttpServletResponse response,ModelMap model){
		logger.info("进入查看页面");
		DictValue dictValue = (DictValue)dictValueService.getByPk(DictValue.class, id);
		model.addAttribute("frmDictValue", dictValue);
		return baseUrl + "dictValueDetail";
	}
	
	@RequestMapping(baseUrl + "getPKByIndex")
	@ResponseBody
	public Long getPKByIndex(String indexTag){
		Long id =  dictNameService.getDictNamePKByIndex(indexTag);
		return id;
	}
	
	@RequestMapping(baseUrl + "getDictValuesByPId")
	@ResponseBody
	public String getDictValuesByPId(Long pid){
		String dictValues = dictValueService.getDictValuesByPId(pid);
		return dictValues;
	}
	
	/**
	 * 下载批量添加数据字典项的模板
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = baseUrl + "downloadTemplete")
    public ResponseEntity<byte[]> download() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = URLEncoder.encode("数据字典项模板.txt", "UTF-8");
        headers.setContentDispositionFormData("attachment", filename);
        File excel = new File(WebAppUtils.getWebAppRoot() + "system/dataManagement/dataDict/Dir.txt");
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(excel),headers, HttpStatus.OK);
    }
    
    @RequestMapping(baseUrl+"importDir")
    @ResponseBody
    public String importDir(HttpServletRequest request,HttpServletResponse response){
    	//数据字典的id
    	String pid = request.getParameter("dictName");
    	//状态
    	String status = request.getParameter("status");
    	String result ="";
    	MultipartHttpServletRequest multipatrRequest = (MultipartHttpServletRequest) request;
    	Map<String, MultipartFile> fileMap = multipatrRequest.getFileMap();
    	MultipartFile multipartFile = null;
    	for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
			multipartFile = set.getValue();
		}
    	String fileName = multipartFile.getOriginalFilename();
    	File txt = new File(WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp)+fileName);
    	try{
    		multipartFile.transferTo(txt);
    		dictValueService.addDictValueTxt(txt, status,pid);
    		DictName dictName = (DictName) dictValueService.getByPk(DictName.class, Long.parseLong(pid));
    		GlobalDataCacheMap.refresh(dictName.getIndexTag());  //刷新每个数据字典
    	}catch(Exception e){
    		e.printStackTrace();
    		result = "error";
    	}
    	return result;
    }
}
