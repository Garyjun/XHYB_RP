package com.brainsoon.common.web.controller;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
public class BaseController {
	protected  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	protected IBaseService baseService;
	public final static String ACTION_MSGKEY = "_action_msg";
	public final static String ACTION_ERRORKEY = "_action_error";
	public final static String RESULT_MSGKEY = "_result_msg";
	/**
	 * 添加系统提示消息
	 * @param msg
	 */
	public void addActionMsg(String msg){
		getRequest().setAttribute(ACTION_MSGKEY, msg);
	}
	/**
	 * 添加系统提示消息
	 * @param e
	 */
	public void addActionMsg(Exception e){
		getRequest().setAttribute(ACTION_MSGKEY, e.getMessage());
	}
	/**
	 * 添加系统错误提示消息
	 * @param error
	 */
	public void addActionError(String error){
		getRequest().setAttribute(ACTION_ERRORKEY, error);
	}
	/**
	 * 添加系统错误提示消息
	 * @param e
	 */
	public void addActionError(Exception e){
		getRequest().setAttribute(ACTION_ERRORKEY, e.getMessage());
	}
	
	public HttpServletRequest getRequest(){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}
	public PageInfo getPageInfo(){
		PageInfo pageInfo=new PageInfo();
		HttpServletRequest request=getRequest();
		pageInfo.setPage(Integer.parseInt(request.getParameter("page")));
		pageInfo.setSort(request.getParameter("sort"));
		pageInfo.setOrder(request.getParameter("order"));
		return pageInfo;
	}
}
