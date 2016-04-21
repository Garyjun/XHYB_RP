package com.brainsoon.system.action;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.service.ISysDoiService;
import com.brainsoon.system.service.ITagUtilService;
import com.brainsoon.system.util.MetadataSupport;
import com.brainsoon.system.util.MetadataUtil;

@Controller
public class TagHtmlAction extends BaseAction{
	@Autowired
	private ITagUtilService iTagUtils;
	@RequestMapping(value = "/tagHtml/getMainPageConditionTag")
	@ResponseBody
	public String getMainPageConditionTag(HttpServletRequest request, ModelMap model){
		String publishType = request.getParameter("publishType");
		String basePath = "http://"  + getRequest().getServerName() + ":" + getRequest().getServerPort()+ getRequest().getContextPath();
		HttpSession session = getSession();
		String htmlValue = "";
		try {
			htmlValue = iTagUtils.getMainPageConditionTag(session, publishType, basePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlValue;
	}
	
	
	@RequestMapping(value = "/tagHtml/getMainPageTargetTag")
	@ResponseBody
	public String getMainPageTargetTag(HttpServletRequest request, ModelMap model){
		String publishType = request.getParameter("publishType");
		HttpSession session = getSession();
//		String basePath = "http://"  + getRequest().getServerName() + ":" + getRequest().getServerPort()+ getRequest().getContextPath();
		String targetField = "";
		try {
			targetField = iTagUtils.getMainPageTargetTag(session, publishType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return targetField;
	}
	
	
}
