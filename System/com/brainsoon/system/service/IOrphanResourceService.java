package com.brainsoon.system.service;

import javax.servlet.http.HttpServletRequest;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.semantic.ontology.model.Asset;

public interface IOrphanResourceService extends IBaseService {
	/**
	 * 分页查询，智能添加前台参数，读取rdf库
	 * @param request
	 * @param conditionList
	 * @return String json
	 */
	public String query4Page(HttpServletRequest request,QueryConditionList conditionList);
	/**
	 * 孤儿资源挂载节点
	 */
	public String mountResource(String json);
	/**
	 * 根据ID获取孤儿资源,不过滤权限
	 */
	public Asset getResourceById(String objectId);
	
	/**
	 * 孤儿资源下线
	 */
	/**
	 * 删除孤儿资源
	 */
	public void deleteResourceById(String objectId);
	/**
	 * 批量删除孤儿资源
	 */
	public void deleteByIds(String ids);
}
