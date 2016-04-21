package com.brainsoon.system.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.system.dao.LogoDao;
import com.brainsoon.system.model.Logo;
import com.brainsoon.system.service.ILogoService;
import com.brainsoon.system.support.SysOperateLogUtils;

@Service
public class LogoService extends BaseService implements ILogoService{
	
	@Autowired
	private LogoDao logoDao;
	
	//保存logo的实体
	@Override
	public void doCreateUser(Logo logo) throws ServiceException {
		logoDao.create(logo);
		
	}

	//查询数据库中所有的logo
	@SuppressWarnings("unchecked")
	@Override
	public List<Logo> queueLogo() throws ServiceException {
		String hql = "from Logo";
		List<Logo> logoList = null;
		try {
			 logoList = logoDao.query(hql);
			
		}catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());

		}
		return logoList;
	}

	
	/**
	 * 根据logoId查询对应的logo，返回对应的logo名称
	 */
	@Override
	public Logo queueLogoById(int logoId) throws ServiceException {
		String hql = "from Logo l where l.id = :logoId";
		 Logo logo = null;
		 try{
			 Map<String, Object> paras = new HashMap<String, Object>();
			 paras.put("id", logoId);
			 List<Logo> logoList = logoDao.query(hql, paras);
			 if(logoList.size()>0){
				logo = logoList.get(0); 
			 }
		 }catch(Exception e){
			 logger.error(e.getMessage());
			 throw new ServiceException(e.getMessage());
		 }
		return logo;
	}

	@Override
	public PageResult queryLogos(PageInfo pageInfo, Logo logo)
			throws ServiceException {
		String hql = " from Logo u where 1=1";
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			getBaseDao().query4Page(hql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}

		return pageInfo.getPageResult();
	}

	
	/**
	 * 查询logo的Status（状态）为1的logo的名称
	 */
	@Override
	public String queueStatus() throws ServiceException {
		Logo logo = null;
		String logoName = "";
		String hql = "from Logo where status = 1";
		try {
			List<Logo> logos = logoDao.query(hql);
				if(logos.size()>0){
					logo = logos.get(0);
					logoName = logo.getLogoName();
				}
		}catch(Exception e){
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		
		return logoName;
		
	}

	//将要被启用的logo的status（状态）设置为1，其余的logo的状态设置为0
	@Override
	public void doLogo(int id) throws ServiceException {
		String hql = "from Logo";
		List<Logo> logoList = logoDao.query(hql);
		if(logoList.size()>0){
			for (Logo logo : logoList) {
				if(logo.getId() != id){
					logo.setStatus(0);
				}else{
					//要被启用的logo
					logo.setStatus(1);
					logo.setCreateTime(new Date());
					SysOperateLogUtils.addLog("logo_qiyong", logo.getLogoName(), LoginUserUtil.getLoginUser());
				}
				saveOrUpdate(logo);
			}
		}
	}

	//禁用选中的logo
	@Override
	public void doDisabled() {
		String hql = "from Logo where logoName = 'pubLogo3.png' ";
		List<Logo> logoList = logoDao.query(hql);
		if(logoList.size()>0){
			for (Logo logo : logoList) {
				logo.setStatus(1);
				saveOrUpdate(logo);
			}
		}
	}

}
