package com.brainsoon.system.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.common.po.RemoteResponse;
import com.brainsoon.system.service.IRemoteLoginService;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RemoteLoginController {
	 @Autowired
	 private IRemoteLoginService remoteLoginService;
	 @RequestMapping(value="/remoteLogin")
	 @ResponseBody
	 public RemoteResponse remoteLogin(@RequestParam String loginName,@RequestParam String password) {
		RemoteResponse response=new RemoteResponse();
	    try {
	    	response=remoteLoginService.remoteAuthUser(loginName, password);
		} catch (Exception e) {
			e.printStackTrace();
		}   
	    return response;
		    
	}
}
