package com.brainsoon.common.service.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.system.model.Role;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IRoleService;
import com.brainsoon.system.service.IUserService;

public class CommonUserDetailService implements UserDetailsService {

	@Autowired
    private IRoleService roleService;
	@Autowired
	private IUserService userService;

	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		User loginUser=userService.getUserByLoginName(username);
		if(loginUser==null)
		{ 
			throw new UsernameNotFoundException("用户" + username + " 不存在");
		}
		if(loginUser.getStatus()==0){
			throw new UsernameNotFoundException("用户" + username + " 被禁用");
		}
		boolean enabled = true;                //是否可用  
        boolean accountNonExpired = true;        //是否过期  
        boolean credentialsNonExpired = true;     
        boolean accountNonLocked = true;    
	    Collection<GrantedAuthority> auths= new ArrayList<GrantedAuthority>();
	    List<Role> roleList=new ArrayList<Role>();
		roleList=roleService.getRolesByUserId(loginUser.getId());
	    for(Role role:roleList)
	    {
	    	GrantedAuthorityImpl auth= new GrantedAuthorityImpl(role.getRoleKey());
	  	    auths.add(auth);
	    }
	    return new org.springframework.security.core.userdetails.User(  
	    		loginUser.getLoginName(),  
	    		loginUser.getPassword(),   
                enabled,   
                accountNonExpired,   
                credentialsNonExpired,   
                accountNonLocked,   
                auths);  
	}

	

	
	
}
