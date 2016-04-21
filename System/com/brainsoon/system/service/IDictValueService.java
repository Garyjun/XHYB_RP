package com.brainsoon.system.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.model.MetaDataModelGroup;

public interface IDictValueService extends IBaseService {
	public void addDictValue(DictValue dictValue,String indexTag);
	public void updDictValue(DictValue dictValue,String indexTag);
	public void deleteByIds(String ids,String indexTag);
	public String getIndexTagByName(String name);
	public String getNameByIndexTag(String indexTag);
	public List validateDictName(String sql);
	public String getIndexTagByNameNoPlatFormId(String name);//针对移动进行处理
	public List<MetaDataModelGroup> getContentByTableName(String name);
	public String getDictValuesByPId(Long pid);
	
	/**
	 * 批量添加数据字典项
	 * @param file
	 * @param status 状态
	 * @param pid 所属数据字典id
	 * @return
	 */
	public boolean addDictValueTxt(File file,String status,String pid);
	public String getDictValueById(String id);
}
