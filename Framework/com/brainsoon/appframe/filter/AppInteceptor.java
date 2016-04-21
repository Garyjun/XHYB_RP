package com.brainsoon.appframe.filter;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.util.StringUtil;


/**
 * 系统拦截器,封装系统级别的拦截
 * 预处理preHandle、后处理postHandle（调用了Service并返回ModelAndView，但未进行页面渲染）、返回处理afterCompletion（已经渲染了页面）  
 * @author zuohl
 */
public class AppInteceptor extends HandlerInterceptorAdapter {
	private Logger logger = Logger.getLogger(AppInteceptor.class);
	
	private static final String token_key = "token"; 
	private static final String token_temp_key = "session_temp_token"; 
	private static final String revert_session_key = "revert_session_token"; 
	
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            //判断重复提交
            Token annotation = method.getAnnotation(Token.class);
            if (annotation != null) {
                HttpSession session = request.getSession(false);
                if(null != session){
                	boolean needSaveSession = annotation.save();
                	if (needSaveSession) {
                    	session.setAttribute(token_key, UUID.randomUUID().toString());
                    }
                    boolean needRemoveSession = annotation.remove();
                    if (needRemoveSession) {
                        if (isRepeatSubmit(request)) {
                        	logger.debug("重复提交,已拦截");
                            return false;
                        }
                        //临时存放
                        session.setAttribute(token_temp_key,session.getAttribute(token_key));
                        session.removeAttribute(token_key);
                    }
                }
            }
            return true;
        } else {
            return super.preHandle(request, response, handler);
        }
	}
	public void postHandle(HttpServletRequest request,HttpServletResponse response, Object handler, ModelAndView view) throws Exception {
		logger.info("过滤器拦截的方法---------------------------"+handler);
		String msg = StringUtil.nvl(request.getAttribute(BaseAction.ACTION_MSGKEY));
		String error = StringUtil.nvl(request.getAttribute(BaseAction.ACTION_ERRORKEY));
		String result = StringUtil.nvl(request.getAttribute(BaseAction.RESULT_MSGKEY));
		if(StringUtils.isNotBlank(result)){
			response.getWriter().write(result);
		}
		if(StringUtils.isNotBlank(msg)){
			response.setStatus(2013);
			msg = "{code:2013,msg:'"+URLEncoder.encode(msg, "utf-8")+"'}";
			request.setAttribute("ErrorJson", msg);
			response.addHeader("ErrorJson", msg);
			request.setAttribute(revert_session_key, "yes");
		}
		if(StringUtils.isNotBlank(error)){
			response.setStatus(2014);
			error = "{code:2014,msg:'"+URLEncoder.encode(error, "utf-8")+"'}";
			request.setAttribute("ErrorJson", error);
			response.addHeader("ErrorJson", error);
			request.setAttribute(revert_session_key, "yes");
		}
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8"); 
	}
	public void afterCompletion(HttpServletRequest request,HttpServletResponse response, Object handler, Exception exp) throws Exception {
		if(exp != null){
			logger.debug(exp.getMessage());
		}
		//页面完成
		String revert = StringUtil.nvl(request.getAttribute(revert_session_key));
		if(StringUtils.isNotBlank(revert) && StringUtils.equalsIgnoreCase(revert, "yes")){
			if (handler instanceof HandlerMethod) {
	            HandlerMethod handlerMethod = (HandlerMethod) handler;
	            Method method = handlerMethod.getMethod();
	            //判断重复提交
	            Token annotation = method.getAnnotation(Token.class);
	            if (annotation != null) {
	                HttpSession session = request.getSession(false);
	                if(null != session){
	                	 boolean needRemoveSession = annotation.remove();
	                     if (needRemoveSession) {
	                    	 session.setAttribute(token_key, session.getAttribute(token_temp_key));
	                     }
	                }
	            }
			}
		}
	}
	
	private boolean isRepeatSubmit(HttpServletRequest request) {
        String serverToken = (String) request.getSession(false).getAttribute(token_key);
        if (serverToken == null) {
            return true;
        }
        String clinetToken = request.getParameter(token_key);
        if (clinetToken == null) {
            return true;
        }
        if (!serverToken.equals(clinetToken)) {
            return true;
        }
        return false;
    }
}
