package com.brainsoon.common.service.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalAppCacheMap;


/**
 * 
 * @ClassName: CommonUserNamePasswordAuthenticationFilter 
 * @Description:带平台id、用户名、密码认证过滤器  
 * @author tanghui 
 * @date 2014-9-5 下午1:41:54 
 *
 */
public class CommonUserNamePasswordAuthenticationFilter extends
		UsernamePasswordAuthenticationFilter {
	//注：只支持post提交的方法
	private boolean postOnly = true;
	 
	/**
	 * 重写attemptAuthentication方法
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		if (postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		 //获取父类里面的用户名、密码
		 String username = obtainUsername(request);
         String password = obtainPassword(request);
         if(username == null)  username = "";
         if(password == null)  password = "";
         username = username.trim();
         
		//构造未认证的UsernamePasswordAuthenticationToken
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
				username, password);
		
		// Place the last username attempted into HttpSession for views  
//	    HttpSession session = request.getSession(false); 
//		//如果session不为空，添加username到session中  
//	    if (session != null || getAllowSessionCreation()) {  
//	        request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, TextEscapeUtils.escapeEntities(username));  
//	    }
	    
		//设置details，这里就是设置org.springframework.security.web.authentication.WebAuthenticationDetails实例到details中  
		setDetails(request, authRequest);
		
		// 校验平台类型：platformId
		String platformId = request.getParameter(Constants.PLATFORM_ID);
		if(StringUtils.isBlank(platformId)){
			throw new ServiceException("Without access to the platform of ID [platformId]");
		}
		GlobalAppCacheMap.putKey(Constants.USER_CHECKED_PLATFORM_ID, platformId);
			
		//通过AuthenticationManager:ProviderManager完成认证任务
		return this.getAuthenticationManager().authenticate(authRequest);
	}
	
	
	public boolean isPostOnly() {
		return postOnly;
	}

	@Override
	public void setPostOnly(boolean postOnly) {
		this.postOnly = postOnly;
	}


}
