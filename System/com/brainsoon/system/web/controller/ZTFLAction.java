package com.brainsoon.system.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.system.service.IZTFLService;
@Controller
public class ZTFLAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/system/dataManagement/classification/";
	@Autowired
	private IZTFLService ztflService;
	@RequestMapping(baseUrl + "listZTFL")
	public @ResponseBody String listZTFL(HttpServletRequest request,HttpServletResponse response){
		String path = request.getParameter("path");
		return ztflService.getZTFLJson(path);
	}
	@RequestMapping(baseUrl + "addZTFLNode")
	public @ResponseBody String addZTFLNode(HttpServletRequest request,HttpServletResponse response){
		String node = request.getParameter("node");
		return ztflService.addZTFLNode(node);
	}
	@RequestMapping(baseUrl + "delZTFLNode")
	public void delZTFLNode(HttpServletRequest request,HttpServletResponse response){
		String id = request.getParameter("objectId");
		ztflService.delZTFLNode(id);
	}
}
