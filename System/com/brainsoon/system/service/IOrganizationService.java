package com.brainsoon.system.service;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.Organization;

public interface IOrganizationService extends IBaseService {
	/**
	 * 获取全部组织机构(json)
	 */
	public String getOrganizationJson();
	/**
	 * 组织机构中是否有用户
	 */
	public boolean hasUsers(Organization id);
}
