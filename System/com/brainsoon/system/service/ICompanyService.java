package com.brainsoon.system.service;


import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.Company;


public interface ICompanyService extends IBaseService{
	
	/**
	 * 处理出版社，根据出版社的名称和地址查询数据库中是否存在与之相同的出版社
	 * 若有返回已经保存的出版社的id,并且更新实体
	 * 若没有创建一个出版社保存后返回出版社的id
	 * @param request
	 * @return
	 */
	public String doSaveOrUpdate(Company company);
	/**
	 * 根据ids查询姓名
     * @param request
     * @return
     */
	public String searchName(String ids);
}
