package com.brainsoon.system.service;

import java.util.List;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.Logo;
import com.brainsoon.system.model.User;

public interface ILogoService extends IBaseService{

	//保存上传logo的实体
	public void doCreateUser(Logo logo) throws ServiceException;
	
	//查询sys-logo表中所有的logo
	public List<Logo> queueLogo() throws ServiceException;
	
	//根据logo的id查询相对应的logo
	public Logo queueLogoById(int logoId) throws ServiceException;
	
	//分页查询logo
	public PageResult queryLogos(PageInfo pageInfo, Logo logo) throws ServiceException;
	
	//查询状态为1的（正在被使用的logo）
	public String queueStatus() throws ServiceException;
	
	//启用logo时调用，将除了要被启用的logo的status的状态都设置为0，将要被调用的logo的status设置为1
	public void doLogo(int id) throws ServiceException;
	
	public void doDisabled();
}
