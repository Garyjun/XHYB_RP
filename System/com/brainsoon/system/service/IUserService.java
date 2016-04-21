package com.brainsoon.system.service;

import java.util.List;

import net.sf.json.JSONObject;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.User;

public interface IUserService extends IBaseService{
	/**
	 * 根据登录名查询用户
	 * @param loginName
	 * @return
	 * @throws ServiceException
	 */
	public User getUserByLoginName(String loginName) throws ServiceException;
	
	/**
	 * 根据用户名，密码查询用户
	 * @param loginName
	 * @param password
	 * @return
	 * @throws ServiceException
	 */
	public User getUser(String loginName,String password) throws ServiceException;
   /**
	 * 分页查询用户
	 * @param pageInfo
	 * @param user
	 * @return
	 */
	public PageResult queryUsers(PageInfo pageInfo, User user) throws ServiceException;
	
	public void doCreateUser(User user) throws ServiceException;
	
	public void doUpdateUser(User user) throws ServiceException;
	
	/**
	 * 用本地sql查询用户信息
	 * @param pageInfo
	 * @param user
	 * @return
	 */
	public PageInfo queryUsersByNativeSql(PageInfo pageInfo, User user) throws ServiceException;
	
	/**
	 * 用本地sql查询
	 */
	public void queryForPo() throws ServiceException;
	
	/**
	 * 用本地sql查询
	 */
	public void queryForVo() throws ServiceException;

	/**
	 * 重置密码
	 * @param userId
	 */
	public void doResetPassword(Long userId) throws ServiceException;
	/**
	 * 修改密码
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 */
	public void updatePassword(Long userId,String oldPassword,String newPassword) throws ServiceException;
	/**
	 * 禁用用户
	 * @param userId
	 */
	public void doDisabled(String[] ids);
	/**
	 * 启用用户
	 * @param userId
	 */
	public void doEnabled(String[] ids);
	/**
	 * 根据用户查询用户数据权限
	 */
	public JSONObject getDataPrivilegeByUser(User user);
	/**
	 * 根据用户查询字段权限范围
	 */
	public String getDataRangePreJson(User user);
	/**
	 * 获取加工员列表
	 */
	public List<User> getMakerList();
	/**
	 * 获取部门用户
	 */
	public String getDepartmentUser(String deptIds);
	
	/**
	 * 根据Id获取登录名
	 * @param UserId
	 * @return
	 */
	public String getUserNameByUserId(Long UserId);
	
	/**
	 * 根据用户id获取所在用户组
	 * @param userId
	 * @return
	 */
	public String findidByGroupName(Long userId);
}
