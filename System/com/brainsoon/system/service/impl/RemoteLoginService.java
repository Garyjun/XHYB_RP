package com.brainsoon.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.po.RemoteResponse;
import com.brainsoon.system.model.PrivilegeUrlMapping;
import com.brainsoon.system.model.Role;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IRemoteLoginService;
import com.brainsoon.system.service.IRoleService;
import com.brainsoon.system.service.IUserService;
import com.brainsoon.system.vo.RemoteUser;
@Service
public class RemoteLoginService implements IRemoteLoginService{
	@Autowired
	private IUserService userService;
	@Autowired
    private IRoleService roleService;
	@Override
	public RemoteResponse remoteAuthUser(String loginName, String password) {
		// TODO Auto-generated method stub
		RemoteResponse response=new RemoteResponse();
		String msg="成功";
		Integer status=0;
		User loginUser=null;
		RemoteUser remoteUser=null;
		try {
			loginUser=userService.getUser(loginName,password);
			if(loginUser==null){
				status=-1;
				msg="登录名或密码不正确";
			}else if(loginUser.getStatus()==0){
				status=-1;
				msg="该用户已被禁用！";				
			}else{
				remoteUser=new RemoteUser();
				BeanUtils.copyProperties(remoteUser, loginUser);
				List<Role> roleList=new ArrayList<Role>();
				roleList=roleService.getRolesByUserId(loginUser.getId());
				String resAuthCodes="";//获取用户可以访问的资源code
				String resTypes="";//获取用户可以访问的资源类型
				for(Role role:roleList)
				{
					if(StringUtils.isNotBlank(role.getResCodes())){
						String [] array = role.getResCodes().split(";");
						for(String item : array){
							if(resAuthCodes.indexOf(item)==-1)
								resAuthCodes=resAuthCodes+item+";";
						}
					}
					if(StringUtils.isNotBlank(role.getResTypes())){
						String [] array = role.getResTypes().split(",");
						for(String item : array){
							if(resTypes.indexOf(item)==-1)
								resTypes=resTypes+item+",";
						}
					}
				   
				}
				if(resAuthCodes.length()>1){
					resAuthCodes=resAuthCodes.substring(0,resAuthCodes.length()-1);
				}
				if(resTypes.length()>1){
					resTypes=resTypes.substring(0,resTypes.length()-1);
				}
				if(StringUtils.isBlank(resAuthCodes)){
					resAuthCodes = "-1";
				}
				if(StringUtils.isBlank(resTypes)){
					resTypes = "-1";
				}
				remoteUser.setResAuthCodes(resAuthCodes);
				remoteUser.setResTypes(resTypes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			status=-1;
			msg="调用接口异常:"+e.getMessage();
		}
		response.setStatus(status);
		response.setMsg(msg);
		response.setData(remoteUser);
		return response;
	}

}
