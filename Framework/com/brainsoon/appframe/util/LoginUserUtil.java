package com.brainsoon.appframe.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.dofile.util.OSUtil;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.IModuleService;
import com.brainsoon.system.service.IUserService;
import com.brainsoon.system.util.MetadataSupport;


/**
 * 
 * 登录工具类
 * 
 * @author zuo
 *
 */
public class LoginUserUtil {
	public final static String USER_SESSION_KEY = "APP_USER_SESSION_KEY";
	public static final String AUTH_RES_CODES ="AUTH_RES_CODES";//已经授权的资源code形如:TB,T01,K;TB,T01,P,S02;ZT
	public static final String AUTH_RES_TYPES ="AUTH_RES_TYPES";//已经授权的资源类型形如:T01,T02,T03,T04
	public static final String WF_PRIVIS ="WF_PRIVIS";//流程相关权限

	
	@Autowired
	private static IModuleService moduleService;
	/**
	 * 保存当前登录用户
	 * @param user
	 */
	public static void saveLoginUser(UserInfo user) {
		HttpSession session = getSession();
		if (session == null) {
			throw new ServiceException("session为空！");
		}

		session.setAttribute(USER_SESSION_KEY, user);
	}
	/**
	 * 保存当前登录用户
	 * @param session
	 * @param user
	 */
	public static void saveLoginUser(HttpSession session,UserInfo user) {
		if (session == null) {
			throw new ServiceException("session为空！");
		}
		
		session.setAttribute(USER_SESSION_KEY, user);
	}

	/**
	 * 获取当前登录用户
	 * @return UserInfo
	 */
	public static UserInfo getLoginUser() {
		HttpSession session = getSession();
		if (session == null) {
			return null;
		}

		return (UserInfo) session.getAttribute(USER_SESSION_KEY);
	}

	public static String getLoginName(){
		return getLoginUser().getName();
	}
	/**
	 * 移除当前登录用户
	 */
	public static void removeLoginUser() {
		HttpSession session = getSession();
		if (session == null) {
			throw new ServiceException("session为空！");
		}
		session.removeAttribute(USER_SESSION_KEY);
	}
	
	/**
	 * 获取用户可以访问的资源code
	 * 
	 */
	public static String getWfPrivis() {
		HttpSession session = getSession();
		if (session == null) {
			return "";
		}
		return(String)session.getAttribute(WF_PRIVIS);
	}
	
	/**
	 * 获取用户可以访问的资源code
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	public static String getAuthResCodes()  {
		HttpSession session = getSession();
		if (session == null) {
			return "";
		}
		String authCodes=(String)session.getAttribute(AUTH_RES_CODES);
		try {
			authCodes=URLEncoder.encode(authCodes, "utf8");
		} catch (Exception e) {
			return "";
		}
		return authCodes;
	}
	
	/**
	 * 获取用户可以访问的资源类型
	 * 
	 */
	public static String getAuthResTypes() {
		HttpSession session = getSession();
		if (session == null) {
			return "";
		}
		return(String)session.getAttribute(AUTH_RES_TYPES);
	}


	public static HttpServletRequest getRequest(){
 		HttpServletRequest request = null;
		if(((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())!=null){
			request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		}
		return request;
	}
	public static HttpSession getSession(){
		HttpServletRequest request = getRequest();
		if(request==null){
			return null;
		}
		return request.getSession();
	}
	
	
	/**
	 * 更新保存登录信息的session的用户的资源库的信息
	 * fengda 2015年11月13日
	 * @param user  数据库中存在的真实信息
	 * @param userInfo  session中保存的登录信息，缓存
	 */
	public static void setResTypesToUserInfo(com.brainsoon.system.model.User user, UserInfo userInfo){
		String resTypeStr = user.getResType();
		Map<String,String> map = new HashMap<String,String>();
	    IModuleService  moduleService = null;
		try {
			moduleService = (IModuleService)BeanFactoryUtil.getBean("moduleService");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	
	/**
	 * 当修改用户信息时，session中保存的登录信息和数据库中的就会有差别，
	 * 所以在将用户信息保存之后，将session中的登录信息也要进行更新
	 * 更新保存登录信息的session的用户的基本信息
	 * fengda 2015年11月13日
	 * @param user  更改用户信息后数据库中的实时的信息
	 * @param userInfo
	 */
	public static UserInfo refshUserInfo(com.brainsoon.system.model.User user1,HttpServletRequest request){
		IUserService userService = null;
		UserInfo userInfo = new UserInfo(); 
		try {
			userService = (IUserService)BeanFactoryUtil.getBean("userService");
			User user = (User) userService.getByPk(User.class, user1.getId());
			userInfo.setUserId(user.getId());
			userInfo.setOrgId(user.getOrgId());  //修改页面上面的所属部门
			userInfo.setUsername(user.getUserName());
			userInfo.setName(user.getLoginName());
			userInfo.setPassword(user.getPassword());
			userInfo.setIsPrivate(user.getIsPrivate());
			userInfo.setPasswordLastestModifiedTime(user.getModifiedTime());
			if(!StringUtils.isBlank(user.getOrganization())){   //设置数据权限的组织部门
				userInfo.setDeptUserIds(userService.getDepartmentUser(user.getOrganization()));
			}
			
			
			//给userInfo添加字段权限
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
			
			
			//更新保存登录信息的session的用户的资源库的信息
			String resTypeStr = user.getResType();
			Map<String,String> map = new LinkedHashMap<String,String>();
		    IModuleService  moduleService = null;
			try {
				moduleService = (IModuleService)BeanFactoryUtil.getBean("moduleService");
			} catch (Exception e) {
				e.printStackTrace();
			}
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
			
			
			String platformId = GlobalAppCacheMap.getValue(Constants.USER_CHECKED_PLATFORM_ID) == null?"":GlobalAppCacheMap.getValue(Constants.USER_CHECKED_PLATFORM_ID).toString();
			userInfo.setLoginIp(OSUtil.getIpAddr(request));
			userInfo.setPlatformId(Integer.parseInt(platformId));
			
			//移除Session中USER_SESSION_KEY原有的userInfo得value值
			try{
				removeLoginUser();
			}catch(Exception e){
				e.printStackTrace();
			}
			//保存当前登录信息,将session中的登录信息更新
			request.getSession().setAttribute(USER_SESSION_KEY, userInfo);
//			saveLoginUser(userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}

}
