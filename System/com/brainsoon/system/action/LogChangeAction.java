package com.brainsoon.system.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.brainsoon.appframe.action.BaseAction;

@Controller
public class LogChangeAction extends BaseAction {
	@RequestMapping(value = "/system/workLog/list")
	public String gotoMain(HttpServletRequest request,Model model) {
		String workLog = request.getParameter("workLog");
		model.addAttribute("workLog", workLog);
		return "/system/log/list";
	}

}
