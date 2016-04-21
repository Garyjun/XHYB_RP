package com.brainsoon.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.system.model.InDefinition;
import com.brainsoon.system.model.MetaDataGroup;
import com.brainsoon.system.service.IInDefinitionService;
import com.brainsoon.system.support.SysOperateLogUtils;

@Service
public class InDefinitionService extends BaseService implements IInDefinitionService {
	private JdbcTemplate jdbcTemplate ;
	private static final String SYS_HAVEMETA_URL = WebappConfigUtil.getParameter("SYS_HAVEMETA_URL");

	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void addInDefinition(InDefinition inDefinition) {
		create(inDefinition);
	}

	@Override
	public void addMetaDataTerm(MetaDataGroup metaDataGroup){
		Long pid = metaDataGroup.getDefinition().getId();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT pid,max(version) as ver FROM metaDataGroup ");
		sql.append(" where pid =  '"+pid+"' ORDER BY version " );
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();    
			while(it.hasNext()) { 
				Long version1 = 0L;
			    Map map = (Map) it.next();
			    Object ver = map.get("ver");
			    if(ver==null) {
			    	version1 = 1L;
			    } else {
			    	 Long version =Long.valueOf(ver.toString());
			    	 version1 = version + 1L;
			    }
			    metaDataGroup.setVersion(version1);
				create(metaDataGroup);
			} 
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public void upMetaDataTerm(MetaDataGroup metaDataGroup){
		saveOrUpdate(metaDataGroup);
	}
	
	@Override
	public List doList() {
		StringBuffer sql = new StringBuffer();
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		sql.append("SELECT id,name FROM in_definit where platformId ='"+userInfo.getPlatformId()+"'");
		List<InDefinition> allReconds = new ArrayList<InDefinition>();
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();    
			while(it.hasNext()) {    
			    Map map = (Map) it.next();    
			    InDefinition inDefinition = new InDefinition();
			    inDefinition.setId(Long.valueOf(map.get("id").toString()));
			    inDefinition.setName(map.get("name").toString());
			    /*inDefinition.setNameAbbr(map.get("nameAbbr").toString());
			    inDefinition.setStatus(map.get("status").toString());
			    inDefinition.setUrl(map.get("url").toString());*/
				allReconds.add(inDefinition);
			} 
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return allReconds;
	}
	
	@Override
	public LinkedHashMap<String,String> getAppList(){
		LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		sql.append("SELECT id,name FROM in_definit where status='1' and platformId ='"+userInfo.getPlatformId()+"'");
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();    
			while(it.hasNext()) {    
			    Map mapp = (Map) it.next();    
			    map.put(mapp.get("id").toString(), mapp.get("name").toString());
			} 
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return map;
	}
	
	@Override
	public MetaDataGroup metaDataTermdetail(String pid,String version) {
		Long pid1 = Long.valueOf(pid);
		Long version1 = Long.valueOf(version);
		StringBuffer sql = new StringBuffer();
		MetaDataGroup metaDataGroup = new MetaDataGroup();
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		//UserInfo userInfo = LoginUserUtil.getLoginUser();
		sql.append("SELECT nameMust,nameMay,nameExpand,version,nameMustCN,nameMayCN FROM metaDataGroup");
	    sql.append("where id = "+pid1+"and version="+version1+ "and platformId="+userInfo.getPlatformId());
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();    
			while(it.hasNext()) {    
			    Map map = (Map) it.next();    
			    metaDataGroup.setNameMust(map.get("nameMust").toString());
			    metaDataGroup.setNameMay(map.get("nameMay").toString());
			    metaDataGroup.setNameExpand(map.get("nameExpand").toString());
			    metaDataGroup.setVersion(Long.valueOf(map.get("version").toString()));
			    metaDataGroup.setNameMustCN(map.get("nameMustCN").toString());
			    metaDataGroup.setNameMustCN(map.get("nameMayCN").toString());
			} 
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return metaDataGroup;
	}
	
	@Override
	public String HaveMetaData () {
		HttpClientUtil http = new HttpClientUtil();
		String json = http.executeGet(SYS_HAVEMETA_URL);
		return json;
	}
	
	@Override
	public void deleteById(String id) {
		UserInfo userInfo =  LoginUserUtil.getLoginUser();
		InDefinition inDefinition = (InDefinition) getByPk(InDefinition.class,Long.parseLong(id));
		SysOperateLogUtils.addLog("indefinition_del", inDefinition.getName(), userInfo);
		delete(inDefinition);
	}
	@Override
	public void deleteMetaGroupById(String id) {
		MetaDataGroup metaDataGroup = (MetaDataGroup)getByPk(MetaDataGroup.class, Long.parseLong(id));
		delete(metaDataGroup);
	}
	@Override
	public void updInDefinition(InDefinition inDefinition) {
		saveOrUpdate(inDefinition);
	}
}
