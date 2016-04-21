package com.brainsoon.statistics.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.statistics.po.vo.ResultList;

public interface IPubReleaseNumService extends IBaseService {
	/**
	 * 资源发布数据采集（主要针对发布需求单通过的发布资源）
	 * @param paramsMap TODO
	 * @param userId 用户ID
	 * @param operateType 操作类型  （见 SystemConstants.OperatType 字典类）
	 * @param starRating 评价星级 （见SystemConstants.StarLevel 字典类）
	 * @return
	 * @see com.brainsoon.system.support. SystemConstants
	 */
	public ResultList doStatistic(Map<String,String> paramsMap);

	public File exportRes(List datas);

}
