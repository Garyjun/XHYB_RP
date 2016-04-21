package com.brainsoon.system.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.system.model.Organization;
import com.brainsoon.system.service.IOrganizationService;
import com.brainsoon.system.support.SysOperateLogUtils;
@Controller
public class OrganizationAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/system/organization/";
	@Autowired
	private IOrganizationService organizationService;
	
	@RequestMapping(baseUrl + "list")
	@ResponseBody
	public String list(){
		return organizationService.getOrganizationJson();
	}
	
	@RequestMapping(baseUrl + "addNode")
	@ResponseBody
	public String addNode(HttpServletRequest request){
		String name = request.getParameter("name");
		String pid = request.getParameter("pid");
		String xpath = request.getParameter("xpath");
		
		Organization organization = new Organization();
		organization.setName(name);
		organization.setPid(Long.parseLong(pid));
		organization.setCreatedTime(new Date());
		organization.setModifiedTime(new Date());
		organizationService.create(organization);
		organization.setXpath(xpath + "," + organization.getId());
		organizationService.update(organization);
		
		JSONObject jo = new JSONObject();
		jo.put("name", organization.getName());
		jo.put("id", organization.getId());
		jo.put("pid", organization.getPid());
		jo.put("xpath", organization.getXpath());
		SysOperateLogUtils.addLog("add_organization", name, LoginUserUtil.getLoginUser());
		return jo.toString();
	}
	
	@RequestMapping(baseUrl + "updateNode")
	@ResponseBody
	public String updateNode(@RequestParam Long id,HttpServletRequest request){
		String name = request.getParameter("name");
		Organization organization = (Organization) organizationService.getByPk(
				Organization.class, id);
		organization.setName(name);
		organization.setModifiedTime(new Date());
		organizationService.update(organization);
		
		JSONObject jo = new JSONObject();
		jo.put("name", organization.getName());
		jo.put("id", organization.getId());
		jo.put("pid", organization.getPid());
		jo.put("xpath", organization.getXpath());
		SysOperateLogUtils.addLog("upd_organization",name, LoginUserUtil.getLoginUser());
		return jo.toString();
	}
	
	@RequestMapping(baseUrl + "deleteNode")
	@ResponseBody
	public String deleteNode(@RequestParam Long id){
		String status = "0";
		Organization organization = (Organization) organizationService.getByPk(
				Organization.class, id);
		try {
			organizationService.delete(organization);
		} catch (Exception e) {
			status = "-1";
		}
		SysOperateLogUtils.addLog("del_organization",organization.getName(), LoginUserUtil.getLoginUser());
		return status;
	}
}
