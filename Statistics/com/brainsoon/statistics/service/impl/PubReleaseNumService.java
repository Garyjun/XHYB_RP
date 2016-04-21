package com.brainsoon.statistics.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.statistics.po.PubResqsOfRelease;
import com.brainsoon.statistics.po.vo.ResultList;
import com.brainsoon.statistics.service.IPubReleaseNumService;
import com.brainsoon.statistics.support.StatisticsExcelUtils;
import com.brainsoon.system.support.SystemConstants;

@Service
public class PubReleaseNumService extends BaseService implements IPubReleaseNumService {
	private JdbcTemplate jdbcTemplate ;

	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	@Transactional(readOnly=false,propagation= Propagation.REQUIRED,rollbackFor={Exception.class})
	public ResultList doStatistic(Map<String,String> paramsMap) {
		StringBuffer sql = new StringBuffer();
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		sql.append("SELECT @ROW := @ROW +1 AS id,  channel_name AS channelName,  pubResType,  create_time as filingDate,  COUNT(detail_id) AS countNum FROM  res_release_detail ,( SELECT @ROW :=-1 ) identity where 1=1");
		if(StringUtils.isNotBlank(paramsMap.get("channelName"))){
			sql.append(" and channel_name like '%"+paramsMap.get("channelName")+"%'");
		}
		if(StringUtils.isNotBlank(paramsMap.get("pubResType"))){
			sql.append(" and pubResType =  '"+paramsMap.get("pubResType")+"'");
		}
		if(StringUtils.isNotBlank(paramsMap.get("startTime"))){
			sql.append(" and create_time >  '"+paramsMap.get("startTime")+"'");
		}
		if(StringUtils.isNotBlank(paramsMap.get("endTime"))){
			sql.append(" and create_time <  '"+paramsMap.get("endTime")+"'");
		}
	    sql.append(" and platformId =  '"+userInfo.getPlatformId()+"'");
	    sql.append("and publish_status = '1'");
		sql.append(" GROUP BY channel_name,  pubResType,  create_time ORDER BY id");
		List<PubResqsOfRelease> allReconds = new ArrayList<PubResqsOfRelease>();
		ResultList resultList = new ResultList();
		int totalSum = 0;
		try {
			List rows = jdbcTemplate.queryForList(sql.toString());
			Iterator it = rows.iterator();    
			while(it.hasNext()) {    
			    Map map = (Map) it.next();    
			    PubResqsOfRelease pubRelease = new PubResqsOfRelease();
			    pubRelease.setId(Long.valueOf(map.get("id").toString()));
			    pubRelease.setChannelName(map.get("channelName").toString());
			    pubRelease.setCountNum(Integer.parseInt(map.get("countNum").toString()));
			    pubRelease.setPubResType(map.get("pubResType").toString());
			    pubRelease.setFilingDate((Date)map.get("filingDate"));
				if (map.get("countNum") != null) {
					totalSum = totalSum + Integer.parseInt(map.get("countNum").toString());
				}
				allReconds.add(pubRelease);
			} 
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		resultList.setList(allReconds);
		resultList.setTotalSum(totalSum);
		return resultList;
	}
	
	@Override
	public File exportRes(List datas) {
		File resExcel = StatisticsExcelUtils.getExcelFile("PubReleaseNumExportTemplete.xls", datas);
		return resExcel;
	}

}
