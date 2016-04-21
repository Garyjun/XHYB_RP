package com.brainsoon.system.service;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.MetaDataFileModelGroup;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.MetadataDefinitionGroup;

import java.util.List;


public interface IMetaDataModelService extends IBaseService {
	public void addMetaDataModelGroup(MetaDataModelGroup metaDataModelGroup);
	public void addMetaDataFileModelGroup(MetaDataFileModelGroup metaDataFileModelGroup);
	public List doTypeName();
	public List getDictName();
	public List doFileTypeName();
	/*public List editFileType(int id);*/
	public List doTypeChildList(int id);
	public List queryMetaByFormat(String format);
	public void addMetaDefinition(MetadataDefinitionGroup metadataDefinitionGroup);
	public void updMetaDefinition(MetadataDefinitionGroup metadataDefinitionGroup);
	public void updFileMetaDataType(MetaDataFileModelGroup metaDataFileModelGroup);
	public void updMetaDataType(MetaDataModelGroup metaDataModelGroup);
	public void deleteById(String id);
	public void delFileMetaDataType(String id);
	public void delMetaDataType(String id);
	public List queryAllMetaDefinition();
}

