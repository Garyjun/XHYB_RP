package com.brainsoon.system.service;

import com.brainsoon.common.po.RemoteResponse;

public interface IRemoteLoginService {
	/**
	 * 认证接口
	 * @param userName
	 * @param password
	 * @return
	 */
	public RemoteResponse remoteAuthUser(String loginName,String password);
}
