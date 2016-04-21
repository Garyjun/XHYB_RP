package com.brainsoon.system.service;

import java.util.Date;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.User;

/**
 * <dl>
 * <dt>ISysOperateService</dt>
 * <dd>Description:系统操作记录服务：日记记录，操作类别</dd>
 * <dd>Copyright: Copyright (C) 2009</dd>
 * <dd>Company: 博云易迅</dd>
 * <dd>CreateDate:2014-05-16</dd>
 * </dl>
 * 
 * @author xujie
 */
public interface ISysOperateService extends IBaseService {

	/**
	 * 添加系统业务日志
	 * @param operateKey 操作类型标识
	 * @param operateDesc 操作对象说明
	 * @param userInfo
	 */
	public void addLog(String operateKey, String operateDesc, UserInfo userInfo);
	
	/**
	 * 流程实例操作历史记录保存
	 * @param wf_id 实体描述_实体主键 （与流程创建时一样）
	 * @param remark 操作附带信息 （审核意见，或者其他附加信息）
	 * @param statusDsc 状态描述（审核通过，待审核，审核否决等等）
	 * @param operateDesc 操作描述 （创建，修改，增加，审核等等）
	 * @param operateTime 操作时间，null时默认为当前时间
	 * @param operateorId 操作者
	 * @throws ServiceException
	 */
	public void saveHistory(String wf_id, String remark, String statusDsc, String operateDesc, Date operateTime, Long operateorId) throws ServiceException;

	/**
	 * 根据用户名输入，获取相似词
	 * @param inputWords
	 * @return
	 */
	public String getSimilarWord(String inputWords);
	
	
	/**
	 * 根据用户的ids获取用户名称
	 * @param ids
	 * @return
	 */
	public String getNameByid(String ids);
	
}
