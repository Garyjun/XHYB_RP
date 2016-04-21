package com.brainsoon.statistics.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.statistics.service.ITaskProcessNumService;
import com.brainsoon.statistics.support.StatisticsExcelUtils;
import com.brainsoon.system.model.MetaDataFileModelGroup;
import com.brainsoon.system.model.MetadataDefinitionGroup;
import com.brainsoon.taskprocess.model.TaskDetail;
import com.brainsoon.taskprocess.model.TaskProcess;

@Service
public class TaskProcessNumService extends BaseService implements ITaskProcessNumService {
   private JdbcTemplate jdbcTemplate ;
	
	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	@Override
	public File exportRes(List datas) {
		File resExcel = StatisticsExcelUtils.getExcelFile("TaskProcessNumExportTemplete.xls", datas);
		return resExcel;
	}
	
	@Override
	public List<TaskProcess> dotaskId() {
		List<TaskProcess> listChild = new ArrayList<TaskProcess>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT id FROM task_process");
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();    
			while(it.hasNext()) {    
			    Map map = (Map) it.next();
			    TaskProcess taskProcess = new TaskProcess();
			    taskProcess.setId(Long.valueOf(map.get("id").toString()));
			    listChild.add(taskProcess);
			} 
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return listChild;
	}

}
