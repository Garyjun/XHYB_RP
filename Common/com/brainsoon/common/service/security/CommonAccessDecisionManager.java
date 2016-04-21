package com.brainsoon.common.service.security;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class CommonAccessDecisionManager implements AccessDecisionManager {
	//In this method,need to compare authentication with configAttributes
	//1 a object is a URL, a filter was find permission configuration by this URL,and pass to here
	//3 if not match corresponding authentication, throw a AccessDeniedException
	protected  Logger logger = LoggerFactory.getLogger(getClass());
	public void decide(Authentication authentication, Object object,
			Collection<ConfigAttribute> configAttributes) throws AccessDeniedException,
			InsufficientAuthenticationException {
		logger.debug("run at CommonAccessDecisionManager*******");
		if(configAttributes==null)
		{ 
			return;
		}
		Iterator<ConfigAttribute> ite=configAttributes.iterator();
		while(ite.hasNext())
		{
			ConfigAttribute ca=ite.next();
			String needRole=((SecurityConfig)ca).getAttribute();
			logger.debug("-----------------------------needRole------------"+needRole+"--------*******");
			//authentication.getAuthorities()  用户所有的权限  
			for(GrantedAuthority ga:authentication.getAuthorities())
			{
				if(needRole.equals(ga.getAuthority()))
				{
					return;
				}
			}
		}
		return;
//		logger.debug("no right");
//		throw new AccessDeniedException("no right");
		
	}

	public boolean supports(ConfigAttribute attribute) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean supports(Class<?> arg0) {
		// TODO Auto-generated method stub
		return true;
	}

}
