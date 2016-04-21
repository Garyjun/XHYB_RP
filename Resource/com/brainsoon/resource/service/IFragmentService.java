package com.brainsoon.resource.service;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.semantic.ontology.model.AssetList;

/**
 * @ClassName: IFragmentService
 * @Description: TODO
 * @author 
 * @date 2016年2月1日 上午9:34:09
 *
 */
public interface IFragmentService extends IBaseService{
	public AssetList queryList(String name,String wordName,String fragType,String treeId);
}
