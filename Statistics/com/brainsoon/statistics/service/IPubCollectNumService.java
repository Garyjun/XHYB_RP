package com.brainsoon.statistics.service;

import java.io.File;
import java.util.List;

import com.brainsoon.common.service.IBaseService;

public interface IPubCollectNumService extends IBaseService {
	File exportRes(List datas);

}
