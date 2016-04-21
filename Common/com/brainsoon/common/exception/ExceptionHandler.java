package com.brainsoon.common.exception;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @ClassName: ExceptionHandler 
 * @Description:  实现自己的HandlerExceptionResolver，
 * HandlerExceptionResolver是一个接口，
 * springMVC本身已经对其有了一个自身的实现——DefaultExceptionResolver,
 * 该解析器只是对其中的一些比较典型的异常进行了拦截处理 
 * @author tanghui 
 * @date 2013-8-16 上午9:11:14 
 *
 */
public class ExceptionHandler implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
			Map<String, Object> model = new HashMap<String, Object>();  
	        model.put("ex", ex);  
	        // 根据不同错误转向不同页面  
	        if(ex instanceof BusinessException) {  
	            return new ModelAndView("error/errorpage", model);  
	        }else if(ex instanceof DaoException) {  
	            return new ModelAndView("error/errorpage", model);  
	        } else {  
	            return new ModelAndView("error/error_all", model);  
	        }  
	}

}
