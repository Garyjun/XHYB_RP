package com.brainsoon.rp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.rp.service.IResCenterTextService;

/**
 * @ClassName: ResCenterText
 * @Description: TODO
 * @author 
 * @date 2016年3月14日 下午3:52:12
 *
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@RequestMapping(value = "/resCenterText/")
public class ResCenterTextController extends BaseAction {
	@Autowired
	private IResCenterTextService resCenterTextService;
	
	@RequestMapping(value="detail")
	@ResponseBody
	public String findByResIdaAndType(@RequestParam(value="resId")String resId,@RequestParam(value="resType")String resType){
		String result = "";
		try {
			result = resCenterTextService.findByResIdaAndType(resId,resType);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
