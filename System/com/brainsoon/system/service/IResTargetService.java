package com.brainsoon.system.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.ResTarget;
import com.brainsoon.system.model.ResTargetData;
public interface IResTargetService extends IBaseService {
	/**
	 * 检测标签删除操作
	 */
	public Map<String, Object> deleteYes(String id,String targetField,String targetName,String publishType) throws ServiceException;
	/**
	 * 检测标签重复
	 */
	public List checkRepeat(String name,String module) throws ServiceException;
	/**
	 * 标签删除
	 */
	public void deleteAll(Object target,String ids)throws ServiceException;
	/**
	 * 分页查询标签
	 * @param pageInfo
	 * @param privilege
	 * @return
	 */
	public List getAllTargets(String libType) throws ServiceException;
	/**
	 * 分页查询标签
	 * @param pageInfo
	 * @param privilege
	 * @return
	 */
	public List query(String typeTarget,String resId) throws ServiceException;
	/**
	 * 增加标签
	 * @param privilege
	 * @throws ServiceException
	 */
	public void createTarget(HttpServletRequest request,HttpServletResponse response,ResTargetData resTarget)throws ServiceException;
	
	/**前台显示标签
	 * @param privilege
	 * @throws ServiceException
	 */
	public Map<String,Object> bachSelectTarget(String libType,String resId,String module,String type) throws ServiceException;
	
	/**
	 * 查询标签
	 * @param id
	 * @throws ServiceException
	 */
	public List selectTarget() throws ServiceException ;
	
	/**
	 * 查询标签
	 * @param pageInfo
	 * @param privilege
	 * @return
	 */
	public String  query(String resTargetId,String libType,HttpServletRequest request,QueryConditionList conditionList) throws ServiceException;
	/**
	 * 原始资源查询
	 * @param resTargetId
	 * @param libType
	 * @param request
	 * @param conditionList
	 * @return
	 * @throws ServiceException
	 */
	public String  bresQuery(String resTargetId,String libType,HttpServletRequest request,QueryConditionList conditionList,String module,String type) throws ServiceException;
	//public String  query(String resTargetId,String libType,HttpServletRequest request,QueryConditionList conditionList) throws ServiceException;
	
	/**
	 * 公共资源批量删除标签
	 * @param ids
	 * @throws ServiceException
	 */
	public void doBatchSaveDeleteTarget(String id,String canSelectTargetIds,String hasSelectTargetIds,String resIds)throws ServiceException;

	/**
	 * 教育资源批量删除标签
	 * @param typeTarget 
	 * @param ids
	 * @throws ServiceException
	 */
	public void doBatchTeaTarget(String selectResId,
			String canSelectTargetIds, String hasSelectTargetIds, String resIds, String typeTarget,String module,String type)throws ServiceException;
	/**
	 * 查询标签
	 * @param typeTarget 
	 * @param ids
	 * @throws ServiceException
	 */
	public String getAllMainTargets(String module,String targetField) throws ServiceException;
	/**
	 * queryList根据标签名搜索标签
	 * @param typeTarget 
	 * @param ids
	 * @throws ServiceException
	 */
	public String getTargetsForName(String name,String publishType,String flag) throws ServiceException ;
	/**
	 * queryList标签
	 * @param typeTarget 
	 * @param ids
	 * @throws ServiceException
	 */
	public String queryList(String targetNames, String status,
			String publishType,
			HttpServletRequest request, QueryConditionList conditionList)
			throws ServiceException ;
	/**
	 * main页面标签ztree查询
	 * @return
	 */
	public String queryTargetJson(String publishType,String targetName,String targetType) throws ServiceException ;
}