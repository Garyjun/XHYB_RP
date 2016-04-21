package com.brainsoon.system.action;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

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
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.web.controller.BaseController;
import com.brainsoon.system.model.Module;
import com.brainsoon.system.model.Privilege;
import com.brainsoon.system.service.IModuleService;
import com.brainsoon.system.service.IPrivilegeService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)  
public class PrivilegeAction extends BaseAction{
     @Autowired
     private IPrivilegeService  privilegeService ;
     @Autowired
	 private IModuleService moduleService;
     @RequestMapping(value="/privilege/list")
  	 public String list(Model model,Privilege privilege) {
  	    	 logger.debug("to list ");
  	    	 List<Module> modules=moduleService.getAllModulesOptions();
  	         model.addAttribute("modules",modules);
  	         model.addAttribute(Constants.COMMAND, privilege);
  	  	     return "system/privilege/privilegeList";
  	 }
     @RequestMapping(value="/privilege/query")
	 @ResponseBody
	 public PageResult  query(Privilege privilege) {
    	 return   privilegeService.query(getPageInfo(), privilege);
	}
     
     @RequestMapping(value = "/privilege/toEdit")
     @Token(save=true)
	 public String toEdit(Model model,@RequestParam Long id) {
    	  if(!model.containsAttribute(Constants.COMMAND)){
			   if(id>0) {
				    Privilege privilege=(Privilege)baseService.getByPk(Privilege.class, id);
				    privilege.setUrls(privilegeService.getPriviUrls(id));
		            model.addAttribute(Constants.COMMAND,privilege);
		            model.addAttribute("xpath",privilege.getModule().getXpath());
		       }else{
		    	   model.addAttribute(Constants.COMMAND,new Privilege());
		       } 
		   }
    	   model.addAttribute(Constants.ID,id);
	       return "system/privilege/privilegeEdit";
	 }
	 
    @RequestMapping(value = "/privilege/add")
    @Token(remove=true)
    public void add(Model model, @ModelAttribute("command")Privilege command,HttpServletResponse response) {
        try {
 	        privilegeService.doCreatePrivilege(command);
 			UserInfo userInfo = LoginUserUtil.getLoginUser();
 			String logName = userInfo.getPlatformId()==1?"privilege_add":"pud_privilege_add";
 			SysOperateLogUtils.addLog(logName, command.getPrivilegeName(), userInfo);
		} catch (Exception e) {
			    logger.error(e.getMessage());
			    addActionError(e);
		}
    }
    @RequestMapping(value = "/privilege/update")
    @Token(remove=true)
    public void update(Model model, @ModelAttribute("command")Privilege command,HttpServletResponse response) {
        try {
        	privilegeService.doUpdatePrivilege(command);
 			UserInfo userInfo = LoginUserUtil.getLoginUser();
 			String logName = userInfo.getPlatformId()==1?"privilege_modify":"pud_privilege_modify";
 			SysOperateLogUtils.addLog(logName, command.getPrivilegeName(), userInfo);        	
		} catch (Exception e) {
			    logger.error(e.getMessage());
			    addActionError(e);
		}
    }
    
    @RequestMapping(value = "/privilege/delete/{id}")
    @ResponseBody
    public String delete(@PathVariable Long id) {
    	String result = "0";
    	try {
    		Privilege privilege = (Privilege) privilegeService.getByPk(Privilege.class, id);
    		String name = privilege.getPrivilegeName();
    		privilegeService.doDeletePrivilege(id);
    		UserInfo userInfo = LoginUserUtil.getLoginUser();
    		String logName = userInfo.getPlatformId()==1?"privilege_delete":"pud_privilege_delete";
    		SysOperateLogUtils.addLog(logName, name, userInfo); 
		} catch (Exception e) {
			result = "-1";
		}
        return result;
    }
    
    @RequestMapping(value = "/privilege/batchDelete/{ids}")
    @ResponseBody
    public String batchDelete(@PathVariable String ids) {
    	logger.debug("*********batchDelete*********");
    	String result = "0";
    	try {
    		UserInfo userInfo = LoginUserUtil.getLoginUser();
    		privilegeService.doBatchDeletePrivilege(ids);
    		String logName = userInfo.getPlatformId()==1?"privilege_delete":"pud_privilege_delete";
    		SysOperateLogUtils.addLog(logName, "批量删除", userInfo); 
		} catch (Exception e) {
			result = "-1";
		}
        return result;
    }
    
    @RequestMapping(value="/privilege/view")
    public String view(Model model,@RequestParam Long id) {
    	Privilege privilege=(Privilege)baseService.getByPk(Privilege.class, id);
    	privilege.setUrls(privilegeService.getPriviUrls(id));
    	model.addAttribute(Constants.COMMAND,privilege);
        return "system/privilege/privilegeView";
    }
	    
}
