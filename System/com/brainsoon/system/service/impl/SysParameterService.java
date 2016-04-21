package com.brainsoon.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.system.model.InDefinition;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.ISysParameterService;


/**
 * 
 * @ClassName: SysParameterService 
 * @Description:  系统参数操作service
 * @author tanghui 
 * @date 2014-5-7 上午10:37:00 
 *
 */
@Service
public class SysParameterService extends BaseService  implements ISysParameterService{

	private JdbcTemplate jdbcTemplate ;
	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List validateParamKey(String sql) {
		return jdbcTemplate.queryForList(sql);
	}
	/**
	 * 查询列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SysParameter> getSysList() {
		List<SysParameter> sysList = null;
		try {
			sysList = query("from SysParameter");
		} catch (DaoException e) {
			if (logger.isErrorEnabled()) {
				logger.error("查询参数出现异常", e);
			}
			throw new ServiceException("查询参数出现异常");
		}
		return sysList;
		
	}

	@Override
	public List findParaValue(String paraKey){
//		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String sql="select para_value,para_status from sys_parameter where para_key='"+paraKey+"' and para_status =1";
		List<SysParameter> allReconds = new ArrayList<SysParameter>();
		try {
		  if(paraKey!=null){
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();    
			while(it.hasNext()) {    
			    Map map = (Map) it.next();   
			    SysParameter sysParameter = new SysParameter();
			    sysParameter.setParaValue(map.get("para_value").toString());
			    sysParameter.setParaStatus(Long.parseLong(map.get("para_status")+""));
				allReconds.add(sysParameter);
			}
		 }
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return allReconds;
	}
	
	@Override
	public void save(SysParameter sysParameter) {
		this.getBaseDao().create(sysParameter);
		
	}
	
	/**
	 * 分页查询
	 * @param pageInfo
	 * @param sysParameter
	 * @return
	 */
	public PageResult querySysParameter(PageInfo pageInfo, SysParameter sysParameter) throws ServiceException{
		int platformId = sysParameter.getPlatformId();
		String hql=" from SysParameter sp where 1=1 and platformId="+platformId+"";
    	Map<String, Object> params=new HashMap<String, Object>();
     	if(StringUtils.isNotBlank(sysParameter.getParaKey())){
    		hql=hql+" and sp.paraKey like :paraKey ";
   		params.put("paraKey", "%"+sysParameter.getParaKey()+"%");
    	}
     	if(StringUtils.isNotBlank(sysParameter.getParaValue())){
    		hql=hql+" and sp.paraValue like :paraValue ";
   		params.put("paraValue", "%"+sysParameter.getParaValue()+"%");
    	}
    	try {
    		baseDao.query4Page(hql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
    
    	return pageInfo.getPageResult();
	}

}
