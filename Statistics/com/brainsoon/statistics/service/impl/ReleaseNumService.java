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
import com.brainsoon.resrelease.po.ResReleaseDetail;
import com.brainsoon.statistics.po.ResqsOfRelease;
import com.brainsoon.statistics.po.vo.ResultList;
import com.brainsoon.statistics.service.IReleaseNumService;
import com.brainsoon.statistics.support.StatisticsExcelUtils;


@Service
public class ReleaseNumService extends BaseService implements IReleaseNumService {

	private JdbcTemplate jdbcTemplate ;

	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	    
	
	@Override
	@Transactional(readOnly=false,propagation= Propagation.REQUIRED,rollbackFor={Exception.class})
	public ResultList doStatistic(Map<String,String> paramsMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT rl.detail_id,r.id,rl.channel_name AS channelName,r.posttype, rl.create_time AS filingDate,  COUNT(rl.detail_id) AS countNum FROM  res_release_detail rl,res_release r WHERE r.id=rl.release_id ");
		
		if(StringUtils.isNotBlank(paramsMap.get("channelName"))){
			sql.append(" and rl.channel_name like '%"+paramsMap.get("channelName")+"%'");
		}
		if(StringUtils.isNotBlank(paramsMap.get("posttype"))){
			sql.append(" and r.posttype =  '"+paramsMap.get("posttype")+"'");
		}
		if(StringUtils.isNotBlank(paramsMap.get("filingDate_StartTime"))){
			sql.append(" and rl.create_time >=  '"+paramsMap.get("filingDate_StartTime")+"'");
		}
		if(StringUtils.isNotBlank(paramsMap.get("filingDate_EndTime"))){
			sql.append(" and rl.create_time <=  '"+paramsMap.get("filingDate_EndTime")+"'");
		}
		sql.append(" GROUP BY rl.channel_name ORDER BY rl.detail_id");
		List<ResqsOfRelease> allReconds = new ArrayList<ResqsOfRelease>();
		ResultList resultList = new ResultList();
		int totalSum = 0;
		try {
			List rows = jdbcTemplate.queryForList(sql.toString());
			Iterator it = rows.iterator();    
			while(it.hasNext()) {    
			    Map map = (Map) it.next();    
			    ResqsOfRelease release = new ResqsOfRelease();
				release.setId(Long.valueOf(map.get("detail_id").toString()));
				release.setReleaseId(Long.decode(map.get("id").toString()));
				if(map.get("channelName")!=null){
					release.setChannelName(map.get("channelName").toString());
				}
				if(map.get("posttype")!=null){
					release.setPosttype(map.get("posttype").toString());
				}
				if(map.get("countNum")!=null){
					release.setCountNum(Integer.parseInt(map.get("countNum").toString()));
				}
				if(map.get("filingDate")!=null){
					release.setFilingDate((Date)map.get("filingDate"));
				}
				if (map.get("countNum") != null) {
					totalSum = totalSum + Integer.parseInt(map.get("countNum").toString());
				}
				allReconds.add(release);
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
		File resExcel = StatisticsExcelUtils.getExcelFile("ReleaseNumExportTemplete.xls", datas);
		return resExcel;
	}
	
	@Override
	public List<ResReleaseDetail> doSoutceTypeList() {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT distinct file_type FROM res_release_detail");
		List<ResReleaseDetail> sourceTypeList = new ArrayList<ResReleaseDetail>();
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();    
			while(it.hasNext()) {    
			    Map map = (Map) it.next();    
			    ResReleaseDetail resReleaseDetail = new ResReleaseDetail();
			    resReleaseDetail.setFileType(map.get("file_type").toString());
			    sourceTypeList.add(resReleaseDetail);
			}		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return sourceTypeList;
	}

}
