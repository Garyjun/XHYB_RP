package com.brainsoon.statistics.service.impl;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Service;

import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.statistics.service.ICollectNumService;
import com.brainsoon.statistics.service.IModuleNumService;
import com.brainsoon.statistics.support.StatisticsExcelUtils;

@Service
public class CollectNumService extends BaseService implements ICollectNumService{

	@Override
	public File exportRes(List datas) {
		File resExcel = StatisticsExcelUtils.getExcelFile("CollectNumExportTemplete.xls", datas);
		return resExcel;
	}

}
