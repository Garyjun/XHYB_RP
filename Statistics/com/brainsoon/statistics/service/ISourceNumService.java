package com.brainsoon.statistics.service;

import java.io.File;
import java.util.List;

import com.brainsoon.common.service.IBaseService;

public interface ISourceNumService extends IBaseService {
	public List<String> getListGroupId(int sysResMetadataTypeIdd);
	public String queryFormList(String id,String metadataMap,String startTime,String endTime,String page,String size,String allTypes);
	public String queryPieList(String page,String size);
	File exportRes(List datas);
}
