package com.brainsoon.system.support;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.system.model.Privilege;
import com.brainsoon.system.model.Role;

/**
 * <dl>
 * <dt>OperateDescUtils</dt>
 * <dd>Description:系统操作描述工具类</dd>
 * <dd>Copyright: Copyright (C) 2009</dd>
 * <dd>Company: 博云易迅</dd>
 * <dd>CreateDate:2014-05-16</dd>
 * </dl>
 * 
 * @author xujie
 */
public class OperateDicUtils {
	/**
	 * 用户
	 * 
	 * @param user
	 * @return
	 */
	public static String getUserDesc(UserInfo user) {
		if (user != null) {
			return user.getName();
		}
		return "";
	}

	/**
	 * 角色
	 * 
	 * @param role
	 * @return
	 */
	public static String getRoleDesc(Role role) {
		if (role != null) {
			return role.getRoleName();
		}
		return "";
	}

}
