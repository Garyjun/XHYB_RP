package com.brainsoon.system.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.system.service.IModuleService;
import com.brainsoon.system.service.impl.ISystemService;

/**
 * 系统相关操作Action
 * 
 * @author Administrator
 * 
 */
@Controller
public class SystemAction extends BaseAction {
	@Autowired
	private ISystemService systemService;
	
	@RequestMapping(value = "/system/getInformMessages")
	@ResponseBody
	public String getInformMessages() {
		return systemService.getInformNums();
	}
}
