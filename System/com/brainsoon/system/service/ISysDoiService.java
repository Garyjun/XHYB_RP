package com.brainsoon.system.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.ResTarget;
import com.brainsoon.system.model.SysDoi;
public interface ISysDoiService extends IBaseService {
	/**
	 * 增加Doi
	 * @param privilege
	 * @throws ServiceException
	 */
	public boolean createDoi(HttpServletRequest request,HttpServletResponse response,SysDoi sysDoi)throws ServiceException;
	/**
	 * DOI子菜单查询
	 * @return
	 * @throws ServiceException
	 */
	public List queryDoiSub()throws ServiceException;
}