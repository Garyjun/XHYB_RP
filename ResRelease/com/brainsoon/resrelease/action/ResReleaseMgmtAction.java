package com.brainsoon.resrelease.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.resrelease.service.IResReleaseService;

/**
 * @ClassName: ResReleaseMgmtService
 * @Description: 通过发布单id以及资源id更新发布记录状态接口。
 * @author 
 * @date 2015年11月21日 下午1:39:48
 *	
 */
@Controller
public class ResReleaseMgmtAction extends BaseAction {
	@Autowired
	private IResReleaseService resReleaseService;
	@RequestMapping(value="/resReleaseMgmtService/updateReleaseInfo",method = {RequestMethod.POST })
	@ResponseBody
	public String updateReleaseInfo(@RequestParam("jsons") String jsons){
		String statuss="";
		try {
			statuss= resReleaseService.updateReleaseInfo(jsons);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("json有错误!"+jsons);
			return "资源发布回写异常---->"+jsons;
		}
		return statuss;
		
	}
}
