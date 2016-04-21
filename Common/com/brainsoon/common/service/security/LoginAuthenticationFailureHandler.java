/**
 * Copyright(c)2012 Beijing PeaceMap Co.,Ltd.
 * All right reserved. 
 */
package com.brainsoon.common.service.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * @description
 * @author aokunsang
 * @date 2012-8-17
 */
public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {
	private String defaultUrl;
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.AuthenticationFailureHandler#
	 * onAuthenticationFailure(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse,
	 * org.springframework.security.core.AuthenticationException)
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2) throws IOException, ServletException {
		System.out.println("-------LoginAuthenticationFailureHandler.onAuthenticationFailure()------------验证失败！！！");
		request.getRequestDispatcher(defaultUrl).forward(request,response);
	}

	/**
	 * @param defaultUrl the defaultUrl to set
	 */
	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}
}
