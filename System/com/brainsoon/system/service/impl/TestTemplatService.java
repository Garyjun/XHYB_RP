package com.brainsoon.system.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;

import com.brainsoon.system.model.TestTemplate;
import com.brainsoon.system.model.TestTemplateItem;
import com.brainsoon.system.service.ITestTemplatService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.google.gson.Gson;
@Service
public class TestTemplatService extends BaseService implements ITestTemplatService {
	private JdbcTemplate jdbcTemplate ;
	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	@Override
	public void addTestTemplatItem(TestTemplateItem testTemplateItem){
		create(testTemplateItem);
	}
	@Override
	public int doMaxCount(int pid){
		int count=0;
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT max(count) as ver FROM t_templateitem ");
		sql.append(" where pid =  '"+pid+"'" );
		try {
			count = jdbcTemplate.queryForInt(sql.toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
		return count;
	}
	@Override
	public void addTemplate(TestTemplate testTemplate){
		create(testTemplate);
	}
	@Override
	public void updTestTemplatService(TestTemplate testTemplate) {
		saveOrUpdate(testTemplate);
	}
	public void delItemById(int id) {
		StringBuffer sql = new StringBuffer();
		sql.append("Delete FROM t_templateitem ");
		sql.append(" where pid =  '"+id+"'" );
		jdbcTemplate.execute(sql.toString());
	}
	
	@Override
	public void deleteByIds(String ids) {
		UserInfo userInfo =  LoginUserUtil.getLoginUser();
		if(StringUtils.isNotBlank(ids)){
			String[] idArray = ids.split(",");
			for(String idStr : idArray){
				int id = Integer.parseInt(idStr);
				TestTemplate testTemplate = (TestTemplate) getByPk(TestTemplate.class,id);
				delItemById(id);
				delete(testTemplate);
			}
		}
	}
	
	@Override
	public void deleteItemByIds(String ids) {
		UserInfo userInfo =  LoginUserUtil.getLoginUser();
		if(StringUtils.isNotBlank(ids)){
			String[] idArray = ids.split(",");
			for(String idStr : idArray){
				int id = Integer.parseInt(idStr);
				TestTemplateItem testTemplateItem  = (TestTemplateItem)getByPk(TestTemplateItem.class, id);
				delete(testTemplateItem);
			}
		}
	}
	@Override
	public String doItemList(String idStr) {
		int id = Integer.parseInt(idStr);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT testTypeKey,testType FROM t_templateitem where pid ='"+id+"' order by id");
		List list = jdbcTemplate.queryForList(sql.toString());
		Gson gson = new Gson(); 
		String reslut = gson.toJson(list); 
//		try {
//			List list = jdbcTemplate.queryForList(sql.toString());
//			Iterator it = list.iterator();    
//			while(it.hasNext()) {    
//			    Map map = (Map) it.next();    
//			    TestTemplateItem testTemplateItem = new TestTemplateItem();
//			    testTemplateItem.setTestType(map.get("testType").toString());
//			    testTemplateItem.setTestTypeKey(map.get("testTypeKey").toString());
//			    itemList.add(testTemplateItem);
//			} 
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			throw new ServiceException(e.getMessage());
//		}
		return reslut;
	}
	@Override
	public String getTemplatName(String id) {
		// TODO Auto-generated method stub
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT name FROM test_templat where id ='"+id+"'");
		List list = jdbcTemplate.queryForList(sql.toString());
		Iterator it = list.iterator();
		String templatName = "";
		while(it.hasNext()) { 
			Map map = (Map) it.next();
			templatName = map.get("name").toString();
		}
		
		return templatName;
	} 
}
