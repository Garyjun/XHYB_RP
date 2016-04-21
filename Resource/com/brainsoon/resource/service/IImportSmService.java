package com.brainsoon.resource.service;

import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.SearchParamCa;

public interface IImportSmService  extends IBaseService{

	/**
	 * 自动导入书目资源
	 * @param SearchParamCa
	 * @return 
	 * @throws ServiceException
	 */
	public Ca addSm(SearchParamCa searchParamCa) throws ServiceException;
}
