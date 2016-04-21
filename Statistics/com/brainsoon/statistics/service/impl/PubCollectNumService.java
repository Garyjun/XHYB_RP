package com.brainsoon.statistics.service.impl;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Service;

import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.statistics.service.IPubCollectNumService;
import com.brainsoon.statistics.support.StatisticsExcelUtils;

@Service
public class PubCollectNumService extends BaseService implements IPubCollectNumService {
	@Override
	public File exportRes(List datas) {
		File resExcel = StatisticsExcelUtils.getExcelFile("PubCollectNumExportTemplete.xls", datas);
		return resExcel;
	}

}
