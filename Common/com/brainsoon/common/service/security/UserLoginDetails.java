package com.brainsoon.common.service.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserLoginDetails implements UserDetails {

	private static final long serialVersionUID = -3812970988916880228L;  
    /**用户名*/  
    private String username;  
    /**密码*/  
    private String password;  
    /**平台编号*/  
    private int platformId;
 
    public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	/**用户的授权信息*/  
    private Collection<? extends GrantedAuthority> authorities;
    
    @Override  
    public Collection<? extends GrantedAuthority> getAuthorities() {  
        return authorities;  
    }  
  
    @Override  
    public String getPassword() {  
        return password;  
    }  
  
    @Override  
    public String getUsername() {  
        return username;  
    }   
  
    public void setUsername(String username) {  
        this.username = username;  
    }  
  
    public void setPassword(String password) {  
        this.password = password;  
    }   
  
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {  
        this.authorities = authorities;  
    }

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	} 

}
