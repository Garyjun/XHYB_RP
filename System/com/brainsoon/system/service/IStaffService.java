package com.brainsoon.system.service;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.Staff;

public interface IStaffService extends IBaseService{

	/**
     * 根据姓名、团体或组织单位名称和地址查询员工表中，该人是否存在
     * 姓名和组织单位必有一项不为空，或都不为空
     * 存在返回此人的id，不存在创建，返回创建后此人的id
     * @param request
     * @return
     */
	public String doSaveOrUpdate(Staff staff);
	/**
	 * 根据ids查询姓名
     * @param request
     * @return
     */
	public String searchName(String ids);
		
}
