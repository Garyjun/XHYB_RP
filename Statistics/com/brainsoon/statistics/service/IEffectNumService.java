package com.brainsoon.statistics.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.statistics.po.vo.ResultList;

public interface IEffectNumService extends IBaseService {

	/**
	 * 绩效管理统计：操作记录（计件）
	 * @param userName 用户ID
	 * @param operateType 操作类型  （见 SystemConstants.OperatType 字典类）
	 * @param maturityType 资源成熟度类别 （见 SystemConstants.LibType 字典类）
	 * @param starRating 评价星级 （见SystemConstants.StarLevel 字典类）
	 * @param countNum 资源数量
	 * @return
	 * @see com.brainsoon.system.support. SystemConstants
	 */
	public boolean doPiecework(Long userId, String operateType, String maturityType, String starRating, int countNum);

	File exportRes(List datas);

	ResultList doStatistic(Map<String, String> paramsMap);
}
