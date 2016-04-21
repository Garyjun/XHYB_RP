package com.brainsoon.common.service.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;

import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.system.model.PrivilegeUrlAndRoleBo;
import com.brainsoon.system.model.PrivilegeUrlMapping;
import com.brainsoon.system.model.Role;
import com.brainsoon.system.service.IPrivilegeUrlService;
import com.brainsoon.system.service.IRoleService;

/**
 * 
 * @ClassName: CommonSecurityMetadataSource 
 * @Description:  jsp页面 <sec:authorize /> 标签鉴权
 * @author tanghui 
 * @date 2014-9-10 下午3:54:09 
 *
 */ 
public class CommonSecurityMetadataSource implements FilterInvocationSecurityMetadataSource{
	@Autowired
    private IRoleService roleService;
	@Autowired
	private IPrivilegeUrlService privilegeUrlService;
	private String platformId = "";
	private AntPathMatcher  urlMatcher=new AntPathMatcher();
	private String expire = "1";  // 过期标识,默认为不过期  --> 0：过期 1：不过期 
	private static Map<String, Collection<ConfigAttribute>> resourceMap = null;
	

	
	/**
	 * jsp页面 <sec:authorize /> 标签鉴权
	 */
	public Collection<ConfigAttribute> getAttributes(Object obj) throws IllegalArgumentException {
		//获取请求的url地址  
		String url=((FilterInvocation)obj).getRequestUrl();
		platformId = GlobalAppCacheMap.getValue(Constants.USER_CHECKED_PLATFORM_ID) == null?"":GlobalAppCacheMap.getValue(Constants.USER_CHECKED_PLATFORM_ID).toString();
		expire = GlobalAppCacheMap.getValue(Constants.EXPIRE) == null?"1":GlobalAppCacheMap.getValue(Constants.EXPIRE).toString();
		if(StringUtils.isNotBlank(platformId)){
			if(resourceMap == null || expire.equals("0")){//为空或过期则去重新加载
				loadResourceDefine();
				GlobalAppCacheMap.removeKey(Constants.EXPIRE);
			}else{
				Iterator<String> ite=resourceMap.keySet().iterator();
				while(ite.hasNext()){
					String resURL=ite.next();
					/*if(resURL.indexOf("?")!=-1){  
						resURL = resURL.substring(0, resURL.indexOf("?"));  
		            } */ 
					if(urlMatcher.match(resURL,url))
					{
						return resourceMap.get(resURL);
					}
				}
			}
		}
		return null;
	}
	
	
	//预加载函数
	private void loadResourceDefine(){
		platformId = GlobalAppCacheMap.getValue(Constants.USER_CHECKED_PLATFORM_ID) == null?"":GlobalAppCacheMap.getValue(Constants.USER_CHECKED_PLATFORM_ID).toString();
		//配置资源对应的角色
		if(StringUtils.isNotBlank(platformId)){
			//获取平台id
			resourceMap=new HashMap<String, Collection<ConfigAttribute>>();
			@SuppressWarnings("rawtypes")
			List priUrlList = privilegeUrlService.getPrivUrlsAndRoles(platformId);
			if(priUrlList != null && priUrlList.size() > 0){
				for (int i = 0; i < priUrlList.size(); i++) {
					PrivilegeUrlAndRoleBo urlAndRoleBo = (PrivilegeUrlAndRoleBo) priUrlList.get(i);
					String url = urlAndRoleBo.getUrl();//url
					if(StringUtils.isNotBlank(url)){
						if(resourceMap.containsKey(url)){
							continue;
						}
						Collection<ConfigAttribute> atts=new ArrayList<ConfigAttribute>();
						String roleKey = urlAndRoleBo.getRoleKey();//roleKey 如：ROLE_ADMIN,ROLE_USER,ROLE_8
						if(StringUtils.isNotBlank(roleKey)){
							String[] roleKeys = roleKey.split(",");
							for (int j = 0; j < roleKeys.length; j++) {
								ConfigAttribute ca = new SecurityConfig(roleKeys[j]) ;
								atts.add(ca);
							}
						}
						if(!hasRoleAdmin(atts)){
							ConfigAttribute ca=new SecurityConfig("ROLE_ADMIN") ;
							atts.add(ca);
						}
						resourceMap.put(url, atts);
					}
				}
			}
		}
	}
	
	private boolean hasRoleAdmin(Collection<ConfigAttribute> atts){
		for(ConfigAttribute ca : atts){
			if(ca.getAttribute().equals("ROLE_ADMIN"))
				return true;
		}
		return false;
	}
	

	public boolean supports(Class<?> arg0) {
		return true;
	}
	
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}
	
	
	
		
  }

