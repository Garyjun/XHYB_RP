package com.brainsoon.statistics.service.impl;

import java.io.File;
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
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.statistics.service.ISourceNumService;
import com.brainsoon.statistics.support.StatisticsExcelUtils;
import com.brainsoon.system.model.MetadataDefinitionGroup;

@Service
public class SourceNumService extends BaseService implements ISourceNumService{
private JdbcTemplate jdbcTemplate ;
	
	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	@Override
	public List<String> getListGroupId(int sysResMetadataTypeIdd){
		List<MetadataDefinitionGroup> listChild = new ArrayList<MetadataDefinitionGroup>();
		List<String> listGroupId = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT id,fieldZhName FROM sys_ResMetadata_Definition_Group where sysResMetadataTypeId='"+sysResMetadataTypeIdd+"'");
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();    
			while(it.hasNext()) {    
			    Map map = (Map) it.next();
			    MetadataDefinitionGroup metadataDefinitionGroup = new MetadataDefinitionGroup();
			    metadataDefinitionGroup.setId(Long.valueOf(map.get("id").toString()));
			    metadataDefinitionGroup.setFieldZhName(map.get("fieldZhName").toString());
			    listChild.add(metadataDefinitionGroup);
			} 
			for(MetadataDefinitionGroup metadataDefinitionGroup : listChild){
				String groupId = metadataDefinitionGroup.getId()+"";
				listGroupId.add(groupId);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return listGroupId;
		
	}
	
	@Override
	public String queryFormList(String idStr,String metadataMap,String startTime,String endTime,String page,String size,String allTypes) {
		HttpClientUtil http = new HttpClientUtil();
		String url = WebappConfigUtil.getParameter("PUBLISH_SOURCENUM_URL");
		if(startTime==null) {
			startTime="";
		}
		if(endTime==null) {
			endTime="";
		}
		if(page==null) {
			page="";
		}
		if(size==null) {
			size="";
		}
		//判断当前登录人组织部门的id(返回的是已经判断好了组织部门和数据部门相同的)
		String userIds = LoginUserUtil.getLoginUser().getDeptUserIds();
		String hql = "";
		//判断是不是个人用户授权，1表示已经授权
		int isPrivate = LoginUserUtil.getLoginUser().getIsPrivate();
		if (isPrivate == 1) {
			if(StringUtils.isNotBlank(userIds)){
			}else{
				userIds = LoginUserUtil.getLoginUser().getUserId()+"";
			}
		}
		if (StringUtils.isNotBlank(userIds)) {
			if(userIds.endsWith(",")){
				userIds = userIds.substring(0,userIds.length()-1);
			}
			hql="&creator=" + userIds;
		}else{
			hql="&creator=-2";
		}
		url+="?publishType="+idStr+"&metadataMap="+metadataMap+"&createStartTime="+startTime+"&createEndTime="+endTime+"&page="+page+"&size="+size+"&allTypes="+allTypes+hql;
		String formList = http.executeGet(url);
		return formList;
	}
	@Override
	public File exportRes(List datas) {
		File resExcel = StatisticsExcelUtils.getExcelFile("SourceNumExportTemplete.xls", datas);
		return resExcel;
	}
	
	
	@Override
	public String queryPieList(String page,String size) {
		HttpClientUtil http = new HttpClientUtil();
		String url = WebappConfigUtil.getParameter("PUBLISH_fileCount_URL");
		if(page==null) {
			page="";
		}
		if(size==null) {
			size="";
		}
		url+="?page="+page+"&size="+size;
		String pieList = http.executeGet(url);
		return pieList;
	}
}
