package com.brainsoon.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.system.model.Code;
import com.brainsoon.system.model.InDefinition;
import com.brainsoon.system.model.ResTarget;
import com.brainsoon.system.service.ICodeService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants.ResourceType;
@Service
public class CodeService extends BaseService implements ICodeService {
/**
 * 
 * 
 * 添加修改
 */
	@Override
	public void createCode(HttpServletRequest request,
			HttpServletResponse response, Code code,String dicName) throws ServiceException {
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		Long dicName1 = Long.valueOf(dicName);
		InDefinition inDefinition = (InDefinition) baseDao.getByPk(InDefinition.class, dicName1);
		// 修改
		try {
			if (code.getCodeId()!= null) {
				if(code.getCodeName().substring(code.getCodeName().lastIndexOf(',')+1).equals("无")){
					code.setCodeName("");
				}
				if(code.getCodeName().substring(code.getCodeName().lastIndexOf(',')+1).equals("请选择")){
					code.setCodeName("");
				}
				if(code.getCodeDefault().equals("无")){
					code.setCodeDefault("");
				}
				code.setAdapterVer("V1.0");
				code.setName(inDefinition.getName());
				String codeName = code.getCodeName();
				codeName = codeName.substring(codeName.lastIndexOf(',')+1);
				code.setCodeName(codeName);
				code.setPlatformId(userInfo.getPlatformId());
				baseDao.update(code);
				SysOperateLogUtils.addLog("code_updCode", "修改编码为:"+ code.getCodeName(), LoginUserUtil.getLoginUser());
		// 添加
			} else {
					if(code.getCodeName().substring(code.getCodeName().lastIndexOf(',')+1).equals("无")){
						code.setCodeName("");
					}
					if(code.getCodeName().substring(code.getCodeName().lastIndexOf(',')+1).equals("请选择")){
						code.setCodeName("");
					}
					if(code.getCodeDefault().equals("无")){
						code.setCodeDefault("");
					}
					code.setName(inDefinition.getName());
					String name = code.getCodeName();
					name = name.substring(name.lastIndexOf(',')+1);
					code.setAdapterVer("V1.0");
					code.setCodeName(name);
					code.setPlatformId(userInfo.getPlatformId());
					baseDao.create(code);
					//SysOperateLogUtils.addLog("code_saveCode", "添加编码为:"+ code.getCodeName(), LoginUserUtil.getLoginUser());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public String codeQuery(Map codeType)
			throws ServiceException {
		JSONObject jsonObject = JSONObject.fromObject(codeType);
		return jsonObject.toString();
	}
	/**
	 * 
	 * 批量删除
	 */
	@Override
	public void deleteAll(Object code, String ids) throws ServiceException {
		String codeIds[] = ids.split(",");
		for (int i = 0; i < codeIds.length; i++) {
		baseDao.delete(Code.class, codeIds[i]);
		}
	}
	/**
	 * 
	 * 接口查询 
	 */
	@Override
	public String selectCode(String codeType, String code)
			throws ServiceException {
		String hql = "select adapterCode from Code where adapterType='"+codeType+"' and codeDefault='"+code+"'";
		return (String) baseDao.query(hql).get(0);
	}
	
	@Override
	public String selectCodeByName(String codeType, String name)
			throws ServiceException {
		String hql = "select adapterCode from Code where adapterType='"+codeType+"' and adapterName='"+name+"'";
		return (String) baseDao.query(hql).get(0);
	}
}
