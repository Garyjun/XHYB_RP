package com.brainsoon.resource.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.resource.service.IchangeSolrQueneService;


@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ChangeSolrQueneStatus extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/changeSolrQueneStatus/";
	@Autowired
	private IchangeSolrQueneService changeSolrQueneService;
	/**
	 * 执行更新操作
	 * @param request
	 * @param response
	 * @param order
	 * @param model
	 */
	@RequestMapping(baseUrl + "updAction")
	@Token(remove=true)
	public void updAction(HttpServletRequest request,HttpServletResponse response){
		logger.info("进入更改状态方法");
		String objectId = (String)request.getParameter("objectId");
		changeSolrQueneService.updStatus(objectId);
		
	}

}
