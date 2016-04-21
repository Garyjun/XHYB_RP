package com.brainsoon.article.service;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.semantic.ontology.model.AssetList;
import com.brainsoon.semantic.ontology.model.Sco;

public interface IArticleService extends IBaseService{
	
	/**
	 * 根据期刊分类查询文章
	 * @param preType	所属的期开分类
	 * @param page		当前页的起始值
	 * @param sum		每页显示的总个数
	 * @param joulName	文章所属期刊名称
	 * @return
	 */
	public AssetList queryList(String preType,int page,int sum,String joulName);
	
	/**
	 * 修改文章信息
	 * @param sco
	 * @param publishType
	 * @return
	 */
	public String updateSco(Sco sco,String publishType);
}
