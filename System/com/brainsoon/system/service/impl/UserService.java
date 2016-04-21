package com.brainsoon.system.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.md5.Md5Tool;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.dao.IUserDao;
import com.brainsoon.system.model.Group;
import com.brainsoon.system.model.GroupRole;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.MetadataDefinitionGroup;
import com.brainsoon.system.model.Role;
import com.brainsoon.system.model.User;
import com.brainsoon.system.model.UserGroup;
import com.brainsoon.system.model.UserGroupId;
import com.brainsoon.system.model.UserRole;
import com.brainsoon.system.model.UserRoleId;
import com.brainsoon.system.service.IUserService;
import com.brainsoon.system.support.RandomString;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants;
import com.brainsoon.system.util.MetadataSupport;
import com.brainsoon.system.vo.UserVo;

@Service
public class UserService extends BaseService implements IUserService {
	@Autowired
	private IUserDao userDao;
	
	private JdbcTemplate jdbcTemplate;
	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public User getUserByLoginName(String loginName) throws ServiceException {
		User user = null;
		String hql = " from User u where u.loginName=:loginName";
		try {
			List<User> users = userDao.query(hql, "loginName", loginName);
			if (users.size() > 0) {
				user = users.get(0);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());

		}
		return user;
	}

	/**
	 * 根据用户名，密码查询用户
	 * 
	 * @param loginName
	 * @param password
	 * @return
	 * @throws ServiceException
	 */
	public User getUser(String loginName, String password) throws ServiceException {
		User user = null;
		String hql = " from User u where u.loginName=:loginName and u.password=:password ";
		try {
			Map<String, Object> paras = new HashMap<String, Object>();
			paras.put("loginName", loginName);
			paras.put("password", password);
			List<User> users = userDao.query(hql, paras);
			if (users.size() > 0) {
				user = users.get(0);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());

		}
		return user;
	}

	/**
	 * 分页查询用户
	 * 
	 * @param pageInfo
	 * @param user
	 * @return
	 */
	public PageResult queryUsers(PageInfo pageInfo, User user) throws ServiceException {
		String hql = " from User u where 1=1";
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(user.getLoginName())) {
			hql = hql + " and u.loginName like :loginName ";
			try {
				String loginName = URLDecoder.decode(user.getLoginName(),"UTF-8");
				params.put("loginName", "%" + loginName + "%");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
		}
		if (StringUtils.isNotBlank(user.getUserName())) {
			hql = hql + " and u.userName like :userName ";
			try {
				String username = URLDecoder.decode(user.getUserName(),"UTF-8");
				params.put("userName", "%" + username + "%");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
		}
		//用户组
		if(StringUtils.isNotBlank(user.getGroupName())){
			try {
				String groupName = URLDecoder.decode(user.getGroupName(), "UTF-8");
				String sql = "select p.user_id from sys_group u,sys_user_group p where p.group_id = u.Id and u.name like '%"+groupName+"%'";
				List<Long> result = jdbcTemplate.queryForList(sql, Long.class);
				String userIds = StringUtils.substringBetween(result.toString(), "[", "]");
				hql = hql +" and u.id in ("+userIds+")";   
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		//组织部门
		if(StringUtils.isNotBlank(user.getOrganizeName())){
			try {
				String organizeName = URLDecoder.decode(user.getOrganizeName(), "UTF-8");
				String sql = "select id from sys_organization where name like '%"+organizeName+"%'";
				List<String> organizeId = jdbcTemplate.queryForList(sql,String.class);
				String organizeIds = StringUtils.substringBetween(organizeId.toString(), "[","]");
				if(organizeIds.length()>0){
					String organize[] = organizeIds.split(",");
					for (int i=0;i<organize.length;i++) {
						organize[i] = organize[i].trim();
						if (i==0) {
							hql = hql+ " and ( u.orgId like '%"+organize[i]+"%'";
						}else{
							hql = hql+ " or u.orgId like '%"+organize[i]+"%'";
							
						}
					}
					hql = hql +")";
				}
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (user.getStatus()!=-1){
			hql = hql + " and u.status = :status ";
			int status = Integer.valueOf(user.getStatus());
			params.put("status",status);
		}
		try {
			baseDao.query4Page(hql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return pageInfo.getPageResult();
	}

	
	public void doCreateUser(User user) throws ServiceException {
		try {
			String password = Md5Tool.getMD5ofStr(SystemConstants.DEFAULT_PASS);
			user.setPassword(password);
			setUserResTypes(user);
			getBaseDao().create(user);
			setUserRole(user);
			setUserGroup(user);
			getBaseDao().update(user);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	private void setUserResTypes(User user){
		StringBuilder sb = new StringBuilder();
		for(Long id : user.getResTypeIds()){
			sb.append(id+"");
			sb.append(",");
		}
		user.setResType(sb.toString());
	}

	private void setUserGroup(User user) {
		List<UserGroup> groups = new ArrayList<UserGroup>();
		Long[] groupIds = user.getGroupIds();
		if(groupIds != null && groupIds.length>0  ){
			for(Long groupId : groupIds){
				UserGroupId id = new UserGroupId(user.getId(), groupId);
				UserGroup ug = new UserGroup(id);
				groups.add(ug);
			}
			if(groups.size()>0)
				user.setGroups(groups);
		}
		
	}

	private void setUserRole(User user) {
		List<UserRole> roles = new ArrayList<UserRole>();
		Long[] roleIds = user.getRoleIds();
		for (Long roleId : roleIds) {
			UserRoleId id = new UserRoleId(user.getId(), roleId);
			UserRole role = new UserRole(id);
			role.setPlatformId(LoginUserUtil.getLoginUser().getPlatformId());
			roles.add(role);
		}
		if(roles.size()>0)
			user.setRoles(roles);
	}

	public void doUpdateUser(User user) throws ServiceException {
		try {
			String executeHql = " delete from UserRole ur where ur.id.userId=:userId";
			HashMap<String, Object> paras = new HashMap<String, Object>();
			paras.put("userId", user.getId());
			baseDao.executeUpdate(executeHql, paras);
			String hql = "delete from UserGroup ug where ug.id.userId = :userId";
			baseDao.executeUpdate(hql, paras);
			setUserRole(user);
			setUserGroup(user);
			setUserResTypes(user);
			getBaseDao().update(user);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());

		}
	}

	/**
	 * 用本地sql查询用户信息
	 * 
	 * @param pageInfo
	 * @param user
	 * @return
	 */
	public PageInfo queryUsersByNativeSql(PageInfo pageInfo, User user) throws ServiceException {
		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("id", Hibernate.LONG);
		returnMap.put("userName", Hibernate.STRING);
		String sql = " select u.id as id, u.user_name as userName from sys_user u where 1=1 ";
		String where = "";
		if (StringUtils.isNotBlank(user.getUserName())) {
			where = where + " and u.user_name like '%" + user.getUserName() + "%'";
		}
		List<Object> list = null;
		try {
			list = userDao.query4PageBySql(sql, pageInfo, returnMap, User.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
		for (Object row : list) {
			User tmp = (User) row;
			logger.debug("id --- " + tmp.getId());
			logger.debug("name--- " + tmp.getUserName());
		}

		return pageInfo;
	}

	/**
	 * 用本地sql查询获取多个POJO
	 */
	public void queryForPo() throws ServiceException {
		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("user", User.class);
		returnMap.put("role", Role.class);
		String sql = " select user.*,role.* " + " from sys_user user,sys_user_role sr, sys_role role where user.id=sr.user_id and sr.role_id=role.id ";
		List<Map> list = null;
		try {
			list = getBaseDao().queryBySQL(sql, returnMap);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		for (Map row : list) {
			User user = (User) row.get("user");
			Role role = (Role) row.get("role");
			logger.debug("userName --- " + user.getUserName());
			logger.debug("RoleName--- " + role.getRoleName());
		}

	}

	/**
	 * 用本地sql查询获取VO
	 */
	public void queryForVo() throws ServiceException {
		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("userName", Hibernate.STRING);
		returnMap.put("roleName", Hibernate.STRING);
		String sql = " select user.id as id, user.user_name as userName,role.role_name as roleName "
				+ " from sys_user user,sys_user_role sr, sys_role role where user.id=sr.user_id and sr.role_id=role.id ";
		List<Object> list = null;
		try {
			list = getBaseDao().queryBySQL(sql, returnMap, UserVo.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
		for (Object row : list) {
			UserVo user = (UserVo) row;
			logger.debug("userName --- " + user.getUserName());
			logger.debug("RoleName--- " + user.getRoleName());
		}

	}

	@Override
	public void doResetPassword(Long userId) throws ServiceException {
		try {
			User user = (User) getByPk(User.class, userId);
			String password = Md5Tool.getMD5ofStr(SystemConstants.DEFAULT_PASS);
			user.setPassword(password);
			this.baseDao.update(user);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void updatePassword(Long userId, String oldPassword, String newPassword) throws ServiceException {
		if (logger.isDebugEnabled()) {
			logger.debug("进入UserService.updatePassword()...");
		}
		User currentUser = (User) getByPk(User.class, userId);
		if (currentUser == null) {
			if (logger.isErrorEnabled()) {
				logger.error("修改密码失败，当前用户状态不合法，无权操作");
			}
			throw new ServiceException("修改密码失败，当前用户状态不合法，无权操作");
		}

		if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
			if (logger.isInfoEnabled()) {
				logger.info("修改密码失败，旧密码[oldPassword=" + oldPassword + "]或者新密码[newPassword=" + newPassword + "]为空");
			}
			throw new ServiceException("修改密码失败，旧密码或者新密码为空");
		}

		try {
			if (currentUser == null) {
				if (logger.isErrorEnabled()) {
					logger.error("修改系统用户密码失败，用户[username=" + currentUser.getLoginName() + "]不存在或已被删除");
				}
				throw new ServiceException("修改系统用户密码失败，用户不存在或已被删除");
			}

			if (!currentUser.getPassword().equals(Md5Tool.getMD5ofStr(oldPassword))) {
				if (logger.isErrorEnabled()) {
					logger.error("修改系统用户密码失败，输入的旧密码不匹配");
				}
				throw new ServiceException("修改系统用户密码失败，输入的旧密码不匹配");
			}
			String newMd5Password = Md5Tool.getMD5ofStr(newPassword);
			currentUser.setPassword(newMd5Password);
			currentUser.setModifiedTime(new Date());
			baseDao.update(currentUser);
		} catch (DaoException e) {
			if (logger.isErrorEnabled()) {
				logger.error("修改系统用户密码出现异常", e);
			}

			throw new ServiceException("修改系统用户密码出现异常", e);
		}
	}
	
	//禁用用户
	public void doDisabled(String[] ids){
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		for(String id : ids){
			try {
				User user = (User) getByPk(User.class, Long.parseLong(id));
				user.setStatus(0);
				baseDao.update(user);
				SysOperateLogUtils.addLog("user_disable", user.getUserName(), userInfo);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new ServiceException(e.getMessage());
			}
		}
	}
	
	//启用用户
	public void doEnabled(String[] ids){
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		for(String id : ids){
			try {
				User user = (User) getByPk(User.class, Long.parseLong(id));
				user.setStatus(1);
				baseDao.update(user);
				SysOperateLogUtils.addLog("user_enable", user.getUserName(), userInfo);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new ServiceException(e.getMessage());
			}
		}
	}
	
	/**
	 * 根据用户查询用户的数据权限
	 */
	public JSONObject getDataPrivilegeByUser(User user){
		JSONObject result = new JSONObject();
		Set<Role> roleSet = getRoleByUser(user);
		String organizationIds = getOrganizationByRoleSet(roleSet);
		result.put("organization", organizationIds);
		result.put("isPrivate", 0);
		for(Role role : roleSet){
			//if(role.getIsPrivate()==1){
				//result.put("isPrivate", 1);
				//break;
			//}
		}
		return result;
	}
	
	/**
	 * 获取加工员列表
	 * @param 
	 * @return
	 */
	public List<User> getMakerList(){
		List<User> makers = new ArrayList<User>();
		
		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("user", User.class);
		String sql = "select user.* from sys_user user,sys_user_role sr, sys_role role"
				+ " where user.id=sr.user_id and sr.role_id=role.id and "
				+ " role.role_key = 'ROLE_MAKER'";
		List<Map> list = null;
		try {
			list = getBaseDao().queryBySQL(sql, returnMap);
			for (Map row : list) {
				User user = (User) row.get("user");
				if(user.getStatus()==1){
					makers.add(user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return makers;
	}
	
	//待完成
	public UserInfo refreshUserInfo(){
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		long id = userInfo.getUserId();
		User user = (User) getByPk(User.class, id);
		return userInfo;
	}
	
	private String getOrganizationByRoleSet(Set<Role> roleSet){
		StringBuilder sb = new StringBuilder();
		for(Role r : roleSet){
			String resCodes = r.getResCodes();
			if(!StringUtils.isBlank(resCodes))
				sb.append(resCodes);
		}
		List<String> resCodeList = new ArrayList<String>();
		for(String s : sb.toString().split(",")){
			if(!resCodeList.contains(s))
				resCodeList.add(s);
		}
		StringBuilder result = new StringBuilder();
		for(String s : resCodeList){
			result.append(s + ",");
		}
		return result.toString();
	}
	
	private HashSet<Role> getRoleByUser(User user){
		HashSet<Role> roleSet = new HashSet<Role>();
		for(UserRole ur : user.getRoles()){
			Role role = (Role) getByPk(Role.class, ur.getId().getRoleId());
			roleSet.add(role);
		}
		List<Role> groupRoleList = getGroupRoleListByUser(user);
		for(Role r : groupRoleList){
			roleSet.add(r);
		}
		return roleSet;
	}
	
	private ArrayList<Role> getGroupRoleListByUser(User user){
		ArrayList<Role> groupRoleList = new ArrayList<Role>();
		List<Group> groupList = new ArrayList<Group>();
		for(UserGroup ug : user.getGroups()){
			Group group = (Group) getByPk(Group.class, ug.getId().getGroupId());
			groupList.add(group);
		}
		Set<Long> roleIdSet = new HashSet<Long>();
		for(Group g : groupList){
			for(GroupRole gr : g.getRoles()){
				roleIdSet.add(gr.getId().getRoleId());
			}
		}
		for(Long id : roleIdSet){
			Role role = (Role) getByPk(Role.class, id);
			groupRoleList.add(role);
		}
		return groupRoleList;
	}

	@Override
	public String getDataRangePreJson(User user) {
		JSONObject result = new JSONObject();
		String  dataPreJsonStr = user.getDataPreJson();
		JSONArray nodeArray = new JSONArray();
		if(!StringUtils.isBlank(dataPreJsonStr)){
			JSONArray array = JSONArray.fromObject(dataPreJsonStr);
			String metadataArrays = getMetadataArrays(array);
			createMetadatTree(metadataArrays,nodeArray);
		}
		result.put("nodeArray", nodeArray);
		result.put("dataPreRangeArray", user.getDataPreRangeArray());
		return result.toString();
	}
	
	/**
	 * 根据部门id查询用户
	 * @param array
	 * @return
	 */
	public String getDepartmentUser(String deptIds){
		Set<User> result = new HashSet<User>();
		List<User> users = query("from User");
		String deptIdArray[] = null;
		if(StringUtils.isNotBlank(deptIds)){
			deptIdArray = deptIds.split(",");
		}
		for(User user : users){
			boolean flag = false;
			String orgId = user.getOrgId();
			if(!StringUtils.isBlank(orgId)){
				String[] orgIdArray = orgId.split(",");
				for(int i=0;i<deptIdArray.length;i++){
					for(int j=0;j<orgIdArray.length;j++){
						if(deptIdArray[i].equals(orgIdArray[j])){
							result.add(user);	
							flag = true;
							break;
						}
					}
					if(flag){
						break;
					}
				}
			}
		}
		StringBuilder userIds = new StringBuilder();
		for(User user : result){
			userIds.append(user.getId()+",");
		}
		return userIds.toString();
	}
	
	private String getMetadataArrays(JSONArray array){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<array.size(); i++){
			JSONObject json = array.getJSONObject(i);
			sb.append(json.getString("field"));
		}
		return sb.toString();
	}
	
	private void createMetadatTree(String metadataArrays,JSONArray nodeArray){
		for(String metadata : metadataArrays.split(",")){
			MetadataDefinition md = MetadataSupport.getMetadataDefinitionByName(metadata);
			JSONObject json = new JSONObject();
			json.put("id", RandomString.getRandomString(8));
			json.put("pid", md.getGroupId());
			json.put("name", md.getFieldZhName());
			json.put("enName", md.getFieldName());
			json.put("fieldType", md.getFieldType());
			json.put("valueRange", md.getValueRange());
			nodeArray.add(json);
			
			MetadataDefinitionGroup mdg = (MetadataDefinitionGroup) getByPk(
					MetadataDefinitionGroup.class, Long.parseLong(md.getGroupId()));
			JSONObject mdgJson = new JSONObject();
			mdgJson.put("id", mdg.getId());
			mdgJson.put("pid", mdg.getSysResMetadataTypeId());
			mdgJson.put("name", mdg.getFieldZhName());
			
			if(!containsJSON(nodeArray,mdgJson)){
				nodeArray.add(mdgJson);
			}
			
			MetaDataModelGroup mmg = (MetaDataModelGroup) getByPk(
					MetaDataModelGroup.class, mdg.getSysResMetadataTypeId());
			JSONObject mmgJson = new JSONObject();
			mmgJson.put("id", mmg.getId());
			mmgJson.put("pid", 0);
			mmgJson.put("name", mmg.getTypeName());
			mmgJson.put("open", true);
			
			if(!containsJSON(nodeArray,mmgJson)){
				nodeArray.add(mmgJson);
			}
		}
	}
	
	private boolean containsJSON(JSONArray array, JSONObject json){
		for(int i=0; i<array.size(); i++){
			JSONObject object = array.getJSONObject(i);
			if(!object.has("enName")){
				if(object.getLong("id")==json.getLong("id"))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 根据Id获取登录名
	 * @param UserId
	 * @return
	 */
	public String getUserNameByUserId(Long UserId){
		User user = null;
		String hql = " from User u where u.id=:id";
		try {
			List<User> users = userDao.query(hql, "id", UserId);
			if (users.size() > 0) {
				user = users.get(0);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());

		}
		return user.getUserName();
	}
	
	/**
	 * 根据用户id获取用户所在的用户组
	 */
	public String findidByGroupName(Long userId){
		String sql = "select name from sys_group u,sys_user_group p where u.Id = p.group_id and p.user_id="+userId;
		List<String> result = jdbcTemplate.queryForList(sql, String.class);
		return StringUtils.substringBetween(result.toString(), "[", "]");
	}
}
