/**
 * Copyright(c)2012 Beijing PeaceMap Co.,Ltd.
 * All right reserved. 
 */
package com.brainsoon.common.service.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.OSUtil;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.Module;
import com.brainsoon.system.model.PrivilegeUrlMapping;
import com.brainsoon.system.model.Role;
import com.brainsoon.system.service.IModuleService;
import com.brainsoon.system.service.IPrivilegeUrlService;
import com.brainsoon.system.service.IRoleService;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.service.IUserService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.util.MetadataSupport;

/**
 * @description
 * @author jiaoyongjie
 * @date 2014-04-08
 */
public class LoginAuthenticationSuccesssHandler implements AuthenticationSuccessHandler {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private String defaultUrl;
	private String updatePwdUrl;
	@Autowired
	private IModuleService moduleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IPrivilegeUrlService privilegeUrlService;
	@Autowired
	private ISysOperateService sysOperateService;

	@Override
	@Transactional(readOnly=false,propagation= Propagation.REQUIRED,rollbackFor={Exception.class}) 
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		logger.debug("----------LoginAuthenticationSuccesssHandler.onAuthenticationSuccess()----------验证完成!!!");
		List<Module> modules = moduleService.getModulesByParentId(new Long(-1));
		request.getSession().setAttribute("modules", modules);
		String platformId = GlobalAppCacheMap.getValue(Constants.USER_CHECKED_PLATFORM_ID) == null?"":GlobalAppCacheMap.getValue(Constants.USER_CHECKED_PLATFORM_ID).toString();
		if(platformId.equals("1")){
			request.getSession().setAttribute("platformName","edu");
		}else if(platformId.equals("2")){
			request.getSession().setAttribute("platformName","pub");
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User securityUser = (User) auth.getPrincipal();
		logger.debug("当前登录用户的账号：" + securityUser.getUsername());
		logger.debug("当前登录用户的权限：" + securityUser.getAuthorities());
		com.brainsoon.system.model.User loginUser = userService.getUserByLoginName(securityUser.getUsername());
		
		//向session中的userInfo中设置登录信息
		UserInfo user = LoginUserUtil.refshUserInfo(loginUser,request);
		Date updatePassword = user.getPasswordLastestModifiedTime();
		String loginMsg ="";
		if(updatePassword == null){
			loginMsg = user.getName() +",您好! 系统检测到您为第一次登录,需要修改初始密码";
		}else{
			//若当前时间距离上次修改密码时间已经超过90天,要提示修改密码
			int dateDiff = DateUtil.getOffsetDays(updatePassword, new Date());
			if(dateDiff > 90 ){
				loginMsg = user.getName() +",您好! 系统检测到您距上次修改密码超过90天,需要重新修改密码";
			}
		}
		//判断是否需要修改密码
//		if(StringUtils.isNotBlank(loginMsg)){
//			request.getSession().setAttribute("mastUpdatePwdMsg", loginMsg);
//			response.sendRedirect(request.getContextPath() + defaultUrl);
//		}else{
//			UserInfo user = new UserInfo();
//			user.setUserId(loginUser.getId());
//			user.setOrgId(loginUser.getOrgId());
//			user.setUsername(loginUser.getUserName());
//			user.setName(loginUser.getLoginName());
//			user.setPassword(loginUser.getPassword());
//			user.setIsPrivate(loginUser.getIsPrivate());
//			user.setDataPreRangeArray(loginUser.getDataPreRangeArray());
//			if(!StringUtils.isBlank(loginUser.getOrganization())){
//				user.setDeptUserIds(userService.getDepartmentUser(loginUser.getOrganization()));
//			}
//			loginUser.getResType();	//ziyuanku	数据字典	
//			loginUser.getOrganization(); //bumen		部门sys_organization
//			loginUser.getIsPrivate(); //gerenyonghushouquan
//			
//			//给userInfo添加字段权限
//			addFieldPreToUserInfo(loginUser,user);
//			user.setLoginIp(OSUtil.getIpAddr(request));
//			user.setPlatformId(Integer.parseInt(platformId));
//			//给userInfo添加资源库
//			setResTypesToUserInfo(loginUser,user);
//			// 保存当前登录信息
//			LoginUserUtil.saveLoginUser(request.getSession(), user);
			
			
			
			String wfPrivis = "";// 用户流程相关权限
			Set<String> wfPriviSet = new HashSet<String>();// 用户流程相关权限
			List<Role> roleList = new ArrayList<Role>();
			roleList = roleService.getRolesByUserId(loginUser.getId());
			String resCodes = "";// 获取用户可以访问的资源code   现在的role表中res_codes和res_types均为空
			String resTypes = "";// 获取用户可以访问的资源类型
			for (Role role : roleList) {
				if (StringUtils.isNotBlank(role.getResCodes())) {
					String[] codeArray = role.getResCodes().split(";");
					for(String code : codeArray){
						if(resCodes.indexOf(code + ";")==-1){
							resCodes = resCodes + code + ";";
						}
					}
				}
				if (StringUtils.isNotBlank(role.getResTypes())) {
					if(resTypes.indexOf(role.getResTypes())==-1){
						resTypes = resTypes + role.getResTypes() + ",";
					}
				}
				//给admin角色添加工作流权限
				if(role.getRoleKey().equals("ROLE_ADMIN")){
					List<PrivilegeUrlMapping> adminPriUrllist = privilegeUrlService.getPrivUrlsByPlatformId(platformId);
					for (PrivilegeUrlMapping priUrl : adminPriUrllist) {
						// 只提取流程相关权限
						if (priUrl.getUrl().startsWith("WF_")) {
							wfPriviSet.add(priUrl.getUrl());
						}
					}
				}else{
					List<PrivilegeUrlMapping> priUrllist = privilegeUrlService.getPrivUrls(role);
					for (PrivilegeUrlMapping priUrl : priUrllist) {
						// 只提取流程相关权限
						if (priUrl.getUrl().startsWith("WF_")) {
							wfPriviSet.add(priUrl.getUrl());
						}
					}
				}
				
			}
			if (wfPriviSet.size() > 0) {
				for (String wfPri : wfPriviSet) {
					wfPrivis = wfPrivis + "'" + wfPri + "'" + ",";
				}
				wfPrivis = wfPrivis.substring(0, wfPrivis.length() - 1);
			}
			if (resCodes.length() > 1) {
				resCodes = resCodes.substring(0, resCodes.length() - 1);
			}
			if (resTypes.length() > 1) {
				resTypes = resTypes.substring(0, resTypes.length() - 1);
			}
			request.getSession().setAttribute(LoginUserUtil.AUTH_RES_CODES, resCodes);
			request.getSession().setAttribute(LoginUserUtil.AUTH_RES_TYPES, resTypes);
			request.getSession().setAttribute(LoginUserUtil.WF_PRIVIS, wfPrivis);
			SysOperateLogUtils.addLog("user_loginin", user.getUsername(), user);
			response.sendRedirect(request.getContextPath() + defaultUrl);
//		}
	}

	private void addFieldPreToUserInfo(com.brainsoon.system.model.User user, UserInfo userInfo){
		String dataPreStr = user.getDataPreJson();
		List<MetadataDefinition> result = new ArrayList<MetadataDefinition>();
		if(!StringUtils.isBlank(dataPreStr)){
			JSONArray dataPreJson = JSONArray.fromObject(dataPreStr);
			for(int i=0; i<dataPreJson.size(); i++){
				JSONObject json = dataPreJson.getJSONObject(i);
				String groupId = json.getString("id");
				String fields = json.getString("field");
				List<MetadataDefinition> metadataList = 
						MetadataSupport.getMetadataByGroupId(groupId);
				for(MetadataDefinition md : metadataList){
					if(fields.indexOf(md.getFieldName())!=-1){
						result.add(md);
					}
				}
			}
		}
		if(result.size()>0){
			userInfo.setMetadataList(result);
		}
	}
	
	private void setResTypesToUserInfo(com.brainsoon.system.model.User user, UserInfo userInfo){
		String resTypeStr = user.getResType();
		Map<String,String> map = new HashMap<String,String>();
		if(!StringUtils.isBlank(resTypeStr)){
			for(String res : resTypeStr.split(",")){
				MetaDataModelGroup mmg = (MetaDataModelGroup) moduleService.getByPk(
						MetaDataModelGroup.class, Long.parseLong(res));
				if(mmg !=null){
					map.put(mmg.getId()+"", mmg.getTypeName());
				}
			}
		}
		if(map.size()>0){
			userInfo.setResTypes(map);
		}
	}
	
	
	public String getUpdatePwdUrl() {
		return updatePwdUrl;
	}

	public void setUpdatePwdUrl(String updatePwdUrl) {
		this.updatePwdUrl = updatePwdUrl;
	}
	
	/**
	 * @param defaultUrl the defaultUrl to set
	 */
	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}
	

	public String getDefaultUrl() {
		return defaultUrl;
	}
	
}
