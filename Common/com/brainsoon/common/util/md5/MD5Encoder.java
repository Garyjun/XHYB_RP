package com.brainsoon.common.util.md5;

import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.PasswordEncoder;

public class MD5Encoder implements PasswordEncoder {

	public String encodePassword(String origPwd, Object salt)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return Md5Tool.getMD5ofStr(origPwd);  
	}

	public boolean isPasswordValid(String encPwd, String origPwd, Object salt)
			throws DataAccessException {
		// TODO Auto-generated method stub
		 return encPwd.equals(encodePassword(origPwd, salt));  
	}

}
