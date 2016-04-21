package com.brainsoon.system.service;

import java.util.LinkedHashMap;
import java.util.Map;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.DictName;

public interface IDictNameService extends IBaseService {
	public void addDictName(DictName dictName);
	public void updDictName(DictName dictName);
	public void deleteByIds(String ids);
	public DictName getDictNameByValueId(Long valueId);
	
	public String getValueNameByIndex(String dictName,String index);
	//根据nodeType查询数据字典中的键值对
	public LinkedHashMap<String,String> getSelectValue(String nodeType);
	public String getValueKeyByIndex(String dictName, String index);
	//根据数据字典的id查找值dictValue
	public String getValueNameById(String id);
	//根据name查询数据字典的列表项
	public LinkedHashMap<String,String> getDictMapByName(String name);
	public String getDictIndexByName(String name,String vname);
	public Long getDictNamePKByIndex(String index);
	//根据indexTag找到字典的id值，
	public String getDictValuePKByIndex(String index);
}
