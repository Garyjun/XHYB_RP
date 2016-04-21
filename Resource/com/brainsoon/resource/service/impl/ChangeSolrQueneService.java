package com.brainsoon.resource.service.impl;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.resource.service.IchangeSolrQueneService;
@Service
public class ChangeSolrQueneService extends BaseService implements IchangeSolrQueneService{
	private JdbcTemplate jdbcTemplate ;
	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	@Override
	public void updStatus(String objectId){
		logger.info("进入更改状态服务方法");
		StringBuffer sql = new StringBuffer();
		sql.append("update solr_queue set status=5 where res_id = '"+objectId+"'");//状态为5时表示文件转换完毕
		jdbcTemplate.execute(sql.toString());
		logger.info("退出更改状态服务方法");
	}
}
