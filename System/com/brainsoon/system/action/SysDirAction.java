package com.brainsoon.system.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.SysDir;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.ISysDirService;
import com.brainsoon.system.service.impl.SysParameterService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SysDirAction extends BaseAction {

	@Autowired
	private ISysDirService sysDirService;
	UserInfo userinfo = LoginUserUtil.getLoginUser();
	
	@RequestMapping(value = "/sysDir/list")
	public String list(Model model, SysDir sysDir){
		logger.debug("to list ");
		model.addAttribute(Constants.COMMAND, sysDir);
		return "system/sysDir/sysDirList";
	}
	
	@RequestMapping(value = "/sysDir/query")
	@ResponseBody
	public PageResult query(@RequestParam(value = "resType", required = false) String resType,
			@RequestParam(value = "dirCnName", required = false) String dirCnName) {
//		PageInfo pageInfo = getPageInfo();
//		pageInfo.setItems(items);
//		sysDir.setPlatformId(userinfo.getPlatformId());
//		return sysDirService.querySysDir(pageInfo, sysDir);
		
		QueryConditionList conditionList = getQueryConditionList();
		if (resType != null) {
			conditionList.addCondition(new QueryConditionItem("resType", Operator.EQUAL, resType));
		}
		if (dirCnName != null) {
			conditionList.addCondition(new QueryConditionItem("dirCnName", Operator.LIKE,"%"+dirCnName+"%"));
		}
		return sysDirService.query4Page(SysDir.class, conditionList);

	}
	
	@RequestMapping(value = "/sysDir/validateDirName")
	public @ResponseBody String validateDirName(HttpServletRequest request){
		logger.info("进入查询方法");
		int platformId = userinfo.getPlatformId();
		String id = null;
		String dirEnName = request.getParameter("fieldValue");
		String dirId = request.getParameter("dirId");
		String sql = "select id,dirEnName from sys_dir where dirEnName='"+dirEnName+"' and platformId="+platformId+"";
		List nameList = sysDirService.validateDirName(sql);
		Iterator it = nameList.iterator();    
		while(it.hasNext()) {    
		    Map map = (Map) it.next();
		    id = map.get("id").toString();
		   
		} 
		if(nameList.size()==1){
			if(dirId.equals(id)) {
				return "{\"jsonValidateReturn\": [\"dirEnName\",true]}";
			} else {
				return "{\"jsonValidateReturn\": [\"dirEnName\",false]}";
			}
		}
		return "{\"jsonValidateReturn\": [\"dirEnName\",true]}";
	}
	
	@RequestMapping(value = "/sysDir/validateDirNameAdd")
	public @ResponseBody String validateDirNameAdd(HttpServletRequest request){
		logger.info("进入查询方法");
		int platformId = userinfo.getPlatformId();
		String dirEnName = request.getParameter("fieldValue");
		String sql = "select dirEnName from sys_dir where dirEnName='"+dirEnName+"' and platformId="+platformId+"";
		List nameList = sysDirService.validateDirName(sql);
		
		if(nameList.size()<1){
			return "{\"jsonValidateReturn\": [\"dirEnName\",true]}";
		}
		return "{\"jsonValidateReturn\": [\"dirEnName\",false]}";
	}
	@RequestMapping(value = "/sysDir/delete")
	public String delete(@RequestParam("id") String id) {
		try {
			SysDir sysDir = (SysDir)sysDirService.getByPk(SysDir.class,Long.parseLong(id));
			SysOperateLogUtils.addLog("sysDir_delete", sysDir.getDirEnName(), userinfo);
			sysDirService.delete(SysDir.class, id);
			
		} catch (DaoException e) {
			logger.error(e.getMessage());
		}
		return "redirect:/sysDir/list.action";
	}
	

	@RequestMapping(value = "/sysDir/batchDelete/{ids}")
	public String batchDelete(@PathVariable String ids) {
		logger.debug("*********batchDelete*********");
		try {
			String[] idArray = ids.split(",");
			for (int i = 0; i < idArray.length; i++) {
				SysDir sysDir = (SysDir)sysDirService.getByPk(SysDir.class,Long.parseLong(idArray[i]));
				SysOperateLogUtils.addLog("sysDir_batchDelete", sysDir.getDirEnName(), userinfo);
				sysDirService.delete(SysDir.class, new Long(idArray[i]));
				
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "redirect:/sysDir/list.action";
	}
	
	/*@RequestMapping(value = "/sysDir/delete", method = { RequestMethod.GET })
	public String delete(@RequestParam Long id) {
		try {
			sysDirService.delete(SysDir.class, id);
//			UserInfo userInfo = LoginUserUtil.getLoginUser();
//			SysOperateLogUtils.addLog("sysDir_delete", userInfo.getUsername(), userInfo);
		} catch (DaoException e) {
			logger.error(e.getMessage());
		}
		return "redirect:/sysDir/list.action";
	}
	
	@RequestMapping(value = "/sysDir/batchDelete/{ids}")
	public String batchDelete(@PathVariable String ids) {
		logger.debug("*********batchDelete*********");
		try {
			String[] idArray = ids.split(",");
			for (int i = 0; i < idArray.length; i++) {
				sysDirService.delete(SysDir.class, new Long(idArray[i]));
			}
//			UserInfo userInfo = LoginUserUtil.getLoginUser();
//			SysOperateLogUtils.addLog("sysDir_batchdelete", userInfo.getUsername(), userInfo);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "redirect:/sysDir/list.action";
	}*/
	
	@RequestMapping(value = "/sysDir/toEdit")
	@Token(save = true)
	public String toEdit(Model model, @RequestParam("id") Long id) {
		logger.info("进入修改/添加系统参数页面");
		SysDir sysDir = new SysDir();
		if(id>-1){
			sysDir = (SysDir) sysDirService.getByPk(SysDir.class, id);
		}
		int platformId = userinfo.getPlatformId();
		Map<String, String> resType =OperDbUtils.queryValueByKey("publishType");
		if(platformId==1){
			resType = new HashMap<String, String>();
			resType.put("cbbook", "图书资源");
		}
		model.addAttribute("resType", resType);
		model.addAttribute("status",SystemConstants.Status.map);
		model.addAttribute("platformId", platformId);
 	    model.addAttribute(Constants.ID,id);
		model.addAttribute("sysDir", sysDir);
		
		model.addAttribute("statusValue",sysDir.getStatus());
		return "system/sysDir/sysDirEdit";
	}
	
	@RequestMapping(value = "/sysDir/add")
	@Token(remove=true)
	public void add(Model model, @ModelAttribute("command") SysDir sysDir, HttpServletResponse response) {
		logger.info("进入保存系统参数方法");
		try {
			sysDir.setPlatformId(userinfo.getPlatformId());
			sysDirService.save(sysDir);
//			UserInfo userInfo = LoginUserUtil.getLoginUser();
			SysOperateLogUtils.addLog("sysDir_add", sysDir.getDirEnName(), userinfo);
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(e);
		}
	}
	
	@RequestMapping(value = "/sysDir/view")
	public String view(Model model, @RequestParam("id") Long id) {
		SysDir sysDir = (SysDir) baseService.getByPk(SysDir.class, id);
		model.addAttribute("sysDir", sysDir);
		return "system/sysDir/sysDirDetail";
	}
	
	@RequestMapping(value = "/sysDir/update")
    @Token(remove=true)
    public void update(Model model, @ModelAttribute("command")SysDir sysDir,HttpServletResponse response) {
        try {
        	sysDir.setPlatformId(userinfo.getPlatformId());
        	sysDirService.doUpdateSysDir(sysDir);
			SysOperateLogUtils.addLog("sysDir_update", sysDir.getDirEnName(), userinfo);
		} catch (Exception e) {
			    logger.error(e.getMessage());
			    addActionError(e);
		}
    }
}
