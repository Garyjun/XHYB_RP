package com.brainsoon.statistics.service;

import java.io.File;
import java.util.List;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.MetaDataFileModelGroup;
import com.brainsoon.taskprocess.model.TaskDetail;
import com.brainsoon.taskprocess.model.TaskProcess;

public interface ITaskProcessNumService extends IBaseService {
	File exportRes(List datas);
	public List<TaskProcess> dotaskId();

}
