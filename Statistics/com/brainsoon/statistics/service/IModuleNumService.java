package com.brainsoon.statistics.service;

import java.io.File;
import java.util.List;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResultForTNum;
import com.brainsoon.common.service.IBaseService;

public interface IModuleNumService extends IBaseService{
	public File exportRes(List datas,String encryptPwd);
	
	public List queryListByIds(String ids);
	
	public void doStatistics(int platformId);
	
	public PageResultForTNum queryForPage(Class poClass,QueryConditionList conditionList,int queryType);
}
