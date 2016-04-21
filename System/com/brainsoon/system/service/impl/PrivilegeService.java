package com.brainsoon.system.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.gunit.gUnitParser.treeAdaptor_return;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.system.model.Module;
import com.brainsoon.system.model.Privilege;
import com.brainsoon.system.model.PrivilegeUrlMapping;
import com.brainsoon.system.service.IPrivilegeService;

@Service
public class PrivilegeService extends BaseService implements IPrivilegeService{
	/**
	 * 分页查询权限
	 * @param pageInfo
	 * @param privilege
	 * @return
	 */
	public PageResult query(PageInfo pageInfo, Privilege privilege) throws ServiceException{
		String hql=" from Privilege p where 1=1 and platformId = " + LoginUserUtil.getLoginUser().getPlatformId();
    	Map<String, Object> params=new HashMap<String, Object>();
    	if(StringUtils.isNotBlank(privilege.getPrivilegeName())){
    		hql=hql+" and p.privilegeName like :privilegeName ";
    		try {
				String privilegeName = URLDecoder.decode(privilege.getPrivilegeName(), "UTF-8");
				params.put("privilegeName", "%"+privilegeName+"%");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    	}

    	if(privilege.getModule()!=null&&privilege.getModule().getId()!=null){
    		hql=hql+" and (p.module.id =:moduleId or p.module.parentModule.id =:moduleId)";

    		params.put("moduleId",privilege.getModule().getId());
    	}
    	hql=hql+" order by p.module.id desc,p.displayOrder asc ";
    	try {
    		baseDao.query4Page(hql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
    
    	return pageInfo.getPageResult();
	}
	
	 /**增加权限
	 * @param privilege
	 * @throws ServiceException
	 */
	public void doCreatePrivilege(Privilege privilege)throws ServiceException{
       try {
    	    String[] menus=privilege.getMenu();
			String moduleId="";
			for(int i=menus.length-1;i<menus.length;i--){
				if(!menus[i].equals("0")){
					moduleId=menus[i];
					break;
				}
		    }
			if(!moduleId.equals("")){
				Long mId = Long.valueOf(moduleId);
				Module module=new Module();
				module.setId(mId);
				privilege.setModule(module);
			}
			privilege.setPlatformId(LoginUserUtil.getLoginUser().getPlatformId());
    	   baseDao.create(privilege);
    	   List<PrivilegeUrlMapping> privilegeUrlList = new ArrayList<PrivilegeUrlMapping>();
			String[] _urls = privilege.getUrls().split("\n");
//			List<String> list = getRepeatUrl(_urls);
//			if(list.size()>0){
//				throw new ServiceException("存在重复权限值："+list.toString());
//			}
			for(String url : _urls){
				if(!StringUtils.isBlank(url)){
					PrivilegeUrlMapping pu = new PrivilegeUrlMapping();
					pu.setPrivilegeId(privilege.getId());
					pu.setUrl(url);
					pu.setPlatformId(LoginUserUtil.getLoginUser().getPlatformId());
					privilegeUrlList.add(pu);
				}
			}
			privilege.setPrivilegeUrls(privilegeUrlList);
		
	   } catch (Exception e) {
		logger.error(e.getMessage());
		throw new ServiceException(e.getMessage());
	    }
	}
	
	 /**修改权限
	 * @param privilege
	 * @throws ServiceException
	 */
	public void doUpdatePrivilege(Privilege privilege)throws ServiceException{
       try {
    	   String executeHql=" delete from PrivilegeUrlMapping pu where pu.privilegeId=:privilegeId ";
  		   HashMap<String, Object> paras=new HashMap<String, Object>();
  		   paras.put("privilegeId", privilege.getId());
  		   baseDao.executeUpdate(executeHql, paras);
    	   List<PrivilegeUrlMapping> privilegeUrlList = new ArrayList<PrivilegeUrlMapping>();
			String[] _urls = privilege.getUrls().split("\n");
//			List<String> list = getRepeatUrl(_urls);
//			if(list.size()>0){
//				throw new ServiceException("存在重复权限值："+list.toString());
//			}
			for(String url : _urls){
				PrivilegeUrlMapping pu = new PrivilegeUrlMapping();
				pu.setPrivilegeId(privilege.getId());
				pu.setUrl(url);
				pu.setPlatformId(LoginUserUtil.getLoginUser().getPlatformId());
				privilegeUrlList.add(pu);
			}
			privilege.setPrivilegeUrls(privilegeUrlList);
			String[] menus=privilege.getMenu();
				String moduleId="";
				for(int i=menus.length-1;i<menus.length;i--){
					if(!menus[i].equals("0")){
						moduleId=menus[i];
						break;
					}
			    }
				if(!moduleId.equals("")){
					Long mId = Long.valueOf(moduleId);
					Module module=new Module();
					module.setId(mId);
					privilege.setModule(module);
				}
			privilege.setPlatformId(LoginUserUtil.getLoginUser().getPlatformId());
			baseDao.update(privilege);
		
	   } catch (Exception e) {
		logger.error(e.getMessage());
		throw new ServiceException(e.getMessage());
	    }
	}
	
	
	
	private List<String> getRepeatUrl(String[] _urls) {
		List<String> list = new ArrayList<String>();
		
		for (int i = 0; i < _urls.length; i++) {
			
			for (int j = i+1; j < _urls.length; j++) {
				if(_urls[i].equals(_urls[j])){
					list.add(_urls[i]);
					break;
				}
			}
		}
		return list;
	}
	
	/**
	 * 获取权限url串
	 * @param privilegeId
	 * @return
	 * @throws ServiceException
	 */
	public String getPriviUrls(Long  privilegeId)throws ServiceException{
		StringBuffer urls = new StringBuffer();
		try {
			Privilege privilege=(Privilege)baseDao.getByPk(Privilege.class, privilegeId);
			List<PrivilegeUrlMapping> urlList = privilege.getPrivilegeUrls();
			if(null == urlList || urlList.size() == 0){
				return null;
			}
			int size = urlList.size();
			for(int i = 0; i < size; i++){
				PrivilegeUrlMapping pu = urlList.get(i);
				urls.append(pu.getUrl());
				urls.append("\r\n");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return urls.toString();
	}
	
	/**
	 * 删除权限
	 * @param id
	 * @throws ServiceException
	 */
	public void doDeletePrivilege(Long id)throws ServiceException{
		try {
			   String executeHql=" delete from PrivilegeUrlMapping pu where pu.privilegeId=:privilegeId ";
			   HashMap<String, Object> paras=new HashMap<String, Object>();
			   paras.put("privilegeId", id);
			   baseDao.executeUpdate(executeHql, paras);
			   baseDao.delete(Privilege.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		  
	}
	
	/**
	 * 批量删除权限
	 * @param ids
	 * @throws ServiceException
	 */
	public void doBatchDeletePrivilege(String ids)throws ServiceException{
		try {
			String[] idArray=ids.split(",");
			for(int i=0;i<idArray.length;i++){
				doDeletePrivilege(new Long(idArray[i]));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	

}
