package com.brainsoon.system.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.InDefinition;

import com.brainsoon.system.model.MetaDataGroup;



public interface IInDefinitionService extends IBaseService {
	public void addInDefinition(InDefinition inDefinition);
	public void addMetaDataTerm(MetaDataGroup metaDataGroup);
	public void upMetaDataTerm(MetaDataGroup metaDataGroup);
	public List doList();
	public void deleteById(String id);
	public void deleteMetaGroupById(String id);
	public void updInDefinition(InDefinition inDefinition);
	public LinkedHashMap<String,String> getAppList();
	public String HaveMetaData ();
	public MetaDataGroup metaDataTermdetail(String pid,String version);

}
