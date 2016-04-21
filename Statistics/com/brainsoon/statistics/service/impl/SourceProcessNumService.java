package com.brainsoon.statistics.service.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.statistics.service.ISourceProcessNumService;
import com.brainsoon.statistics.support.StatisticsExcelUtils;
import com.brainsoon.taskprocess.model.TaskDetail;
import com.brainsoon.taskprocess.model.TaskResRelation;
@Service
public class SourceProcessNumService extends BaseService implements ISourceProcessNumService {
	 private JdbcTemplate jdbcTemplate ;
		
		@Autowired
		public void init(DataSource dataSource) {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
	@Override
	public File exportRes(List datas) {
		File resExcel = StatisticsExcelUtils.getExcelFile("SourceProcessNumExportTemplete.xls", datas);
		return resExcel;
	}
	
	@Override
	public List<TaskResRelation> dotaskId() {
		List<TaskResRelation> listChild = new ArrayList<TaskResRelation>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT id FROM task_res_relation");
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();    
			while(it.hasNext()) {    
			    Map map = (Map) it.next();
			    TaskResRelation taskResRelation = new TaskResRelation();
			    taskResRelation.setId(Long.valueOf(map.get("id").toString()));
			    listChild.add(taskResRelation);
			} 
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return listChild;
	}
	
	/**
	 * 批量导出数据，根据输入的页码导出
	 * @throws UnsupportedEncodingException 
	 */
	@Override
	public List<TaskResRelation> findProcessByPage(String resName,
			String status, String taskName, String processName,
			String startTime, String endTime, String pageSize, String page,
			String page1) throws Exception {
	
		String hql = "from TaskResRelation where platformId = 1";
		if(StringUtils.isNotBlank(resName)){
			resName = URLDecoder.decode(resName, "UTF-8");
			hql+=" and resName like '%"+resName+"%'";
		}
		if(StringUtils.isNotBlank(status)){
			status = URLDecoder.decode(status, "UTF-8");
			if("0".equals(status)){
				int stat = Integer.valueOf(status);
				hql+=" and taskDetail.status !="+2;
			}else{
				int sta = Integer.valueOf(status);
				hql+=" and taskDetail.status = "+sta;
			}
		}
		if(StringUtils.isNotBlank(taskName)){
			taskName = URLDecoder.decode(taskName, "UTF-8");
			hql+=" and taskDetail.taskProcess.taskName like '%"+taskName+"%'";
		}
		if(StringUtils.isNotBlank(processName)){
			processName = URLDecoder.decode(processName, "UTF-8");
			hql+=" and taskDetail.user.userName like '%"+processName+"%'";
		}
		if(StringUtils.isNotBlank(startTime)){
			hql+=" and taskDetail.startTime >='"+startTime+"'";
		}
		if(StringUtils.isNotBlank(endTime)){
			hql+=" and taskDetail.endTime <='"+endTime+"'";
		}
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		if(userInfo!=null){
			Map<String, String> resMap = userInfo.getResTypes();
			if(resMap!=null){
				Set<String> set = resMap.keySet();
				Iterator<String> it = set.iterator();
				String resTypes = "";
				while(it.hasNext()){
					resTypes += "'" + it.next() + "'" + ",";
				}
				if(StringUtils.isNotBlank(resTypes)){
					resTypes = resTypes.substring(0, resTypes.length()-1);
					hql+=" and publishType in("+resTypes+")";
				}
			}
			
			
			String userIds = userInfo.getDeptUserIds();
			int isPrivate = userInfo.getIsPrivate();
			if(1==isPrivate){
				if(StringUtils.isNotBlank(userIds)){
					if(StringUtils.isNotEmpty(userIds)){
						String[] ids = userIds.substring(0, userIds.length()-1).split(",");
						Long[] idArr = new Long[ids.length];
						for(int i=0;i<ids.length;i++){
							idArr[i] = Long.valueOf(ids[i]);
						}
						hql+=" and taskDetail.taskProcess.createUser.id in ("+userIds.substring(0, userIds.length()-1)+")";
					}
				}else{
					hql+=" and taskDetail.taskProcess.createUser.id ="+userInfo.getUserId();
				}
			}else{
				if(StringUtils.isNotBlank(userIds)){
					hql+=" and taskDetail.taskProcess.createUser.id in ("+userIds.substring(0, userIds.length()-1)+")";
				}else{
					hql+=" and taskDetail.taskProcess.createUser.id in ("+"-2"+")";
				}
			}
		}
		String startIndex = ( Integer.parseInt(page) - 1 ) * Integer.parseInt(pageSize)+"";
		String rowTotal = ( Integer.parseInt(page1) - Integer.parseInt(page) + 1 ) * Integer.parseInt(pageSize)+"";
		hql+=" ORDER BY id DESC";
		List<TaskResRelation> taskResList = null;
		try{
			taskResList = getBaseDao().query(hql, Integer.valueOf(startIndex), Integer.valueOf(rowTotal));
		}catch(Exception e){
			e.printStackTrace();
		}
		return taskResList;
	}

}
