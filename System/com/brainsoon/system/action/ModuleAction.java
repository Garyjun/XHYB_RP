package com.brainsoon.system.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
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
import com.brainsoon.system.model.Module;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IModuleService;
import com.brainsoon.system.support.SysOperateLogUtils;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ModuleAction extends BaseAction {
	@Autowired
	private IModuleService moduleService;

	@RequestMapping(value = "/module/list")
	public String list(Model model, Module module) {
		logger.debug("to list ");
		model.addAttribute(Constants.COMMAND, module);
		return "system/module/moduleList";
	}

	@RequestMapping(value = "/module/query")
	@ResponseBody
	public PageResult query(Module module) {
		PageInfo pageInfo = getPageInfo();
		return moduleService.query(pageInfo, module);

	}

	@RequestMapping(value = "/module/toEdit")
	@Token(save = true)
	public String toEdit(Model model, @RequestParam("id") Long id) {
		if (!model.containsAttribute(Constants.COMMAND)) {
			if (id > 0) {
				Module module=(Module)baseService.getByPk(Module.class, id);
				model.addAttribute(Constants.COMMAND, module);
				model.addAttribute("xpath",module.getXpath());
			} else {
				model.addAttribute(Constants.COMMAND, new Module());
			}
		}
		model.addAttribute(Constants.ID, id);
		List<Module> modules = moduleService.getModulesOptions(new Long(-1));
		model.addAttribute("parentModules", modules);
		return "system/module/moduleEdit";
	}

	@RequestMapping(value = "/module/add")
	@Token(remove = true)
	public void add(Model model, @ModelAttribute("command") Module command, HttpServletResponse response) {
		try {
			command.setDir("0");
			command.setPlatformId(LoginUserUtil.getLoginUser().getPlatformId());
			String[] menus=command.getLastMenu();
			for(int i=0;i<menus.length;i++){
				if(menus[i].equals("0")){
					menus=(String[]) ArrayUtils.remove(menus, i);
				}
			}
			String xpath=StringUtils.join(menus,",");
			String pId="";
			for(int i=menus.length-1;i<menus.length;i--){
				if(!menus[i].equals("0")){
					pId=menus[i];
					break;
				}
			}
			if(!pId.equals("")){
				Long parentId = Long.valueOf(pId);
				Module parentModule=new Module();
				parentModule.setId(parentId);
				command.setParentModule(parentModule);
				baseService.create(command);
				if(!pId.equals("-1")){
					xpath="-1,"+xpath+","+command.getId();
				}else{
					xpath=xpath+","+command.getId();
				}
				command.setXpath(xpath);
				baseService.update(command);
				if (parentId > -1) {
					Module parent = (Module) moduleService.getByPk(Module.class, parentId);
					parent.setDir("1");
					moduleService.update(parent);

				}
			}
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			String logName = userInfo.getPlatformId()==1?"moudle_add":"pud_moudle_add";
			SysOperateLogUtils.addLog(logName, command.getModuleName(), userInfo);

		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(e);
		}

	}

	@RequestMapping(value = "/module/update")
	@Token(remove = true)
	public void update(Model model, @ModelAttribute("command") Module command, HttpServletResponse response) {
		try {
			command.setPlatformId(LoginUserUtil.getLoginUser().getPlatformId());
			String[] menus=command.getLastMenu();
			for(int i=0;i<menus.length;i++){
				if(menus[i].equals("0")){
					menus=(String[]) ArrayUtils.remove(menus, i);
				}
			}
			String xpath=StringUtils.join(menus,",");
			String pId="";
			for(int i=menus.length-1;i<menus.length;i--){
				if(!menus[i].equals("0")){
					pId=menus[i];
					break;
				}
			}
			if(!pId.equals("-1")){
				xpath="-1,"+xpath+","+command.getId();
			}else{
				xpath=xpath+","+command.getId();
			}
			command.setXpath(xpath);
			if(!pId.equals("")){
				Long parentId = Long.valueOf(pId);
				Module parentModule=new Module();
				parentModule.setId(parentId);
				command.setParentModule(parentModule);
				baseService.update(command);
				if (parentId > -1) {
					Module parent = (Module) moduleService.getByPk(Module.class, parentId);
					parent.setDir("1");
					moduleService.update(parent);
				}
			}
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			String logName = userInfo.getPlatformId()==1?"moudle_update":"pud_moudle_update";
			SysOperateLogUtils.addLog(logName, command.getModuleName(), userInfo);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(e);
		}
	}

	@RequestMapping(value = "/module/delete/{id}", method = { RequestMethod.GET })
	@ResponseBody
	public String delete(@PathVariable Long id) {
		String result = "0";
		try {
			Module module = (Module) baseService.getByPk(Module.class, id);
			String name = module.getModuleName();
			baseService.delete(Module.class, String.valueOf(id));
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			String logName = userInfo.getPlatformId()==1?"moudle_delete":"pud_moudle_delete";
			SysOperateLogUtils.addLog(logName, name, userInfo);
		} catch (Exception e) {
			result = "-1";
		}
		return result;
	}

	@RequestMapping(value = "/module/batchDelete/{ids}", method = { RequestMethod.GET })
	@ResponseBody
	public String batchDelete(@PathVariable String ids) {
		logger.debug("*********batchDelete*********");
		String result = "0";
		try {
			baseService.delete(Module.class, ids);
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			String logName = userInfo.getPlatformId()==1?"moudle_delete":"pud_moudle_delete";
			SysOperateLogUtils.addLog(logName, "批量删除", userInfo);		
		} catch (Exception e) {
			result = "-1";
		}
		return result;
	}

	@RequestMapping(value = "/module/view", method = { RequestMethod.GET })
	public String view(Model model, @RequestParam Long id) {
		Module module = (Module) baseService.getByPk(Module.class, id);
		Module parent = (Module) baseService.getByPk(Module.class, module.getParentModule().getId());
		if (parent != null) {
			module.setParentName(parent.getModuleName());
		}
		model.addAttribute(Constants.COMMAND, module);
		return "system/module/moduleView";
	}
	
	@RequestMapping(value = "/module/getNextMenu")
	@ResponseBody
	public List<Module> getNextModule(String parentId) {
		 List<Module> moduleList=new ArrayList<Module>();
		 Module m=new Module();
		 m.setId(new Long(0));
		 m.setModuleName("..请选择...");
		 moduleList.add(m);
		 moduleList.addAll(moduleService.getModulesOptions(new Long(parentId)));
		return moduleList;
	}

	
	
}
