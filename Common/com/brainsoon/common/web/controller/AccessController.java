package com.brainsoon.common.web.controller;

import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class AccessController {

	 private static final Logger LOGGER = LoggerFactory.getLogger(AccessController.class);
	
	 /**
	 * 异常页面控制
	 * 当这个Controller中任何一个方法发生异常，一定会被这个方法拦截到，然后，输出日志。
	 * 封装Map并返回，页面上得到status为false。
	 * @param runtimeException
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(RuntimeException.class)
	public @ResponseBody
	Map<String,Object> runtimeExceptionHandler(RuntimeException runtimeException) {
		LOGGER.error(runtimeException.getLocalizedMessage());
		Map model = new TreeMap();
		model.put("status", false);
		return model;
	}

}
