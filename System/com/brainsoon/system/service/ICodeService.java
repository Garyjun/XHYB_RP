package com.brainsoon.system.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.Code;
public interface ICodeService extends IBaseService {

	/**
	 * 增加编码
	 * @param privilege
	 * @throws ServiceException
	 */
	public void createCode(HttpServletRequest request,HttpServletResponse response,Code code,String dicName)throws ServiceException;
	/**
	 * 编码类型查询
	 * @param resTargetId
	 * @param libType
	 * @param request
	 * @param conditionList
	 * @return
	 * @throws ServiceException
	 */
	public String codeQuery(Map codeType) throws ServiceException;
	void deleteAll(Object code, String ids) throws ServiceException;
	
	/**
	 * 接口查询
	 * @param pageInfo
	 * @param privilege
	 * @return
	 */
	public String selectCode(String codeType,String code) throws ServiceException;
	
	public String selectCodeByName(String codeType, String name);
}