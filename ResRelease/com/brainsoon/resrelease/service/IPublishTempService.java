package com.brainsoon.resrelease.service;

import java.util.List;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.resrelease.po.ParamsTempEntity;
import com.brainsoon.resrelease.po.ProdParamsTemplate;

public interface IPublishTempService extends IBaseService {
	public ParamsTempEntity convertEntity(ProdParamsTemplate prodParamsTemplate);
	public ProdParamsTemplate entity2ProdTemplate(ParamsTempEntity entity);
	//过滤模板参数
	public void filterParams(ParamsTempEntity entity);
	//模板参数json添加ID属性
	public void addJsonId(ProdParamsTemplate ppt);
	//判断模板名称是否重复
	public String checkTemplateName(String templateName, String templateId); 
	//添加新图片时，删除原图片
	public void delImage(String id);
	
	/**
	 * 根据resType返回对应的字段权限字符串
	 * 			    元数据Id   资源ID
	 * 核心元数据		1		0
	 * 文件元数据		2		0
	 * 书目元数据		61		1
	 * 组图元数据		66		36
	 * 基本元数据		67		37
	 * 有声元数据		68		38
	 * 漫画源数据		69		40
	 * 动画元数据		70		41
	 * 基础元数据		71		44
	 * 
	 * @param resTypeId
	 * @return
	 */
	public String getCheckedCoreMetadataByresId(String resTypeId);
	public List<String> getCheckedCoreMetadataByres(String resTypeId);
}
