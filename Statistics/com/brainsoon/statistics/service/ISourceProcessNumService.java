package com.brainsoon.statistics.service;

import java.io.File;
import java.util.List;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.taskprocess.model.TaskDetail;
import com.brainsoon.taskprocess.model.TaskResRelation;

public interface ISourceProcessNumService extends IBaseService {
	File exportRes(List datas);
	public List<TaskResRelation> dotaskId();
	
	/**
	 * 批量导出数据，根据输入的页码导出数据
	 * @param resName
	 * @param status
	 * @param taskName
	 * @param processName
	 * @param startTime
	 * @param endTime
	 * @param pageSize
	 * @param page
	 * @param page1
	 * @return
	 */
	public List<TaskResRelation> findProcessByPage(String resName,String status,String taskName,String processName,String startTime,String endTime,String pageSize,String page,String page1) throws Exception;

}
