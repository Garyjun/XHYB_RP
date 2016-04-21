package com.brainsoon.resource.action;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.brainsoon.appframe.action.BaseAction;

import com.brainsoon.system.support.SystemConstants.ResourceCaType;
import com.brainsoon.system.support.SystemConstants.ResourceType;

@Controller
public class OfflinePubResAction  extends BaseAction {
	private final String baseUrl = "/publishRes/";
	@RequestMapping(baseUrl+"offlinePubMain")
	public String gotoMain() {
		return baseUrl + "offlinePubMain";
	}

}
