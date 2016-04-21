package com.brainsoon.resource.action;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.brainsoon.appframe.action.BaseAction;

import com.brainsoon.system.support.SystemConstants.ResourceCaType;
import com.brainsoon.system.support.SystemConstants.ResourceType;

@Controller
public class OfflineResAction  extends BaseAction {
	private final String baseUrl = "/bres/";
	@RequestMapping(baseUrl+"offlineMain")
	public String gotoMain(Model model) {
		model.addAttribute("resTypeMap",ResourceType.map.getEntryMap());
		model.addAttribute("resCaTypeMap",ResourceCaType.map.getEntryMap());
		model.addAttribute("libType","1");
		return baseUrl + "offlineMain";
	}

}
