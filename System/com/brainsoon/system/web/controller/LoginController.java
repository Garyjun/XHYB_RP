/**
 * 
 */
package com.brainsoon.system.web.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.support.SysOperateLogUtils;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)  
public class LoginController extends BaseAction{
	
	@RequestMapping(value="/system/login",method=RequestMethod.GET)
	public String login(@RequestParam String error){
		if(null!=error && "1".equals(error)){
			logger.debug("--------------------------验证失败，重新登录中.......");
		}
		return "system/login";
	}
	
//	@RequestMapping(value="/system/denied")
//	public String denied(){
//		logger.debug("run at denied ");
//		return "security/denied";
//	}
	
	@RequestMapping(value="/system/denied")
	public String denied(HttpServletResponse response){
		logger.debug("****run at denied *********");
		addActionError("denied");
		return "security/denied";
	}
	
	@RequestMapping(value="/system/logout")
	public String logout(){
		logger.debug("退出");
		try {
			UserInfo loginUser = LoginUserUtil.getLoginUser();
			SysOperateLogUtils.addLog("user_loginout",loginUser.getUsername(),loginUser);
			LoginUserUtil.removeLoginUser();
			// 过期标识,默认为不过期  --> 0：过期 1：不过期 
			GlobalAppCacheMap.putKey(Constants.EXPIRE, "0");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "system/login";
	}
	
	@RequestMapping(value="/system/timedout",method=RequestMethod.GET)
	public String timedout(){
		
		return "system/login";
	}
	
}
