package com.brainsoon.system.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.support.MetadataDefinitionGroupCacheMap;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.InDefinition;
import com.brainsoon.system.model.MetaDataFileModelGroup;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.MetadataDefinitionGroup;
import com.brainsoon.system.service.IMetaDataModelService;
import com.brainsoon.system.util.MetadataSupport;

@Service
public class MetaDataModelService extends BaseService implements IMetaDataModelService {
	private JdbcTemplate jdbcTemplate ;
	
	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	@Override
	public void addMetaDataModelGroup(MetaDataModelGroup metaDataModelGroup) {
		try {
			create(metaDataModelGroup);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void addMetaDataFileModelGroup(MetaDataFileModelGroup metaDataFileModelGroup) {
		try {
			create(metaDataFileModelGroup);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void addMetaDefinition(MetadataDefinitionGroup metadataDefinitionGroup) {
		try {
			create(metadataDefinitionGroup);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void updMetaDefinition(MetadataDefinitionGroup metadataDefinitionGroup) {
		try {
			saveOrUpdate(metadataDefinitionGroup);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*UserInfo userInfo = LoginUserUtil.getLoginUser();
		String sql = "UPDATE sys_ResMetadata_Definition_Group set sysResMetadataTypeId = '"+metadataDefinitionGroup.getMetaDataModelGroup().getId()+"',fieldName='"+metadataDefinitionGroup.getFieldName()+"' ,fieldZhName='"+metadataDefinitionGroup.getFieldZhName()+"',platformId='"+metadataDefinitionGroup.getPlatformId()+"'where id='"+metadataDefinitionGroup.getId()+"' and platformId ='"+userInfo.getPlatformId()+"'";
		jdbcTemplate.execute(sql);*/
	}
	@Override
	public void updFileMetaDataType(MetaDataFileModelGroup metaDataFileModelGroup){
		try {
			saveOrUpdate(metaDataFileModelGroup);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void updMetaDataType(MetaDataModelGroup metaDataModelGroup){
		try {
			saveOrUpdate(metaDataModelGroup);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public List<MetaDataModelGroup> doTypeName() {
		List<MetaDataModelGroup> typeName = query("from MetaDataModelGroup");
		return typeName;
	}
	@Override
	public List doFileTypeName() {
		List<MetaDataFileModelGroup> fileName = query("from MetaDataFileModelGroup");
		return fileName;
	}
	/*@Override
	public List editFileType(int id) {
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		List<MetaDataFileModelGroup> fileData = query("from MetaDataFileModelGroup where id='"+id+"' and platformId ='"+userInfo.getPlatformId()+"'");
		return fileData;
	}*/
	@Override
	public List doTypeChildList(int id) {
		List<MetadataDefinitionGroup> listChild = new ArrayList<MetadataDefinitionGroup>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT id,fieldZhName FROM sys_ResMetadata_Definition_Group where sysResMetadataTypeId='"+id+"'");
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();    
			while(it.hasNext()) {    
			    Map map = (Map) it.next();
			    MetadataDefinitionGroup metadataDefinitionGroup = new MetadataDefinitionGroup();
			    metadataDefinitionGroup.setId(Long.valueOf(map.get("id").toString()));
			    metadataDefinitionGroup.setFieldZhName(map.get("fieldZhName").toString());
			    listChild.add(metadataDefinitionGroup);
			} 
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return listChild;
	}
	@Override
	public void deleteById(String id){
		MetadataDefinitionGroup metadataDefinitionGroup = (MetadataDefinitionGroup)getByPk(MetadataDefinitionGroup.class,Long.parseLong(id));
		delete(metadataDefinitionGroup);
		MetadataSupport.deleMetadataByGroup(id);
		MetadataDefinitionGroupCacheMap.removeKey(metadataDefinitionGroup.getId()+"");
	}
	@Override
	public void delFileMetaDataType(String id){
		MetaDataFileModelGroup metaDataFileModelGroup = (MetaDataFileModelGroup)getByPk(MetaDataFileModelGroup.class,Long.parseLong(id));
		delete(metaDataFileModelGroup);
		MetadataSupport.deleFileMetadataByRestype(id);
		//调用张鹏接口删除文件分类下的元数据
	}
	@Override
	public void delMetaDataType(String id){
		MetaDataModelGroup metaDataModelGroup = (MetaDataModelGroup)getByPk(MetaDataModelGroup.class,Long.parseLong(id));
		delete(metaDataModelGroup);
		String groupIds ="";
		List<MetadataDefinitionGroup> listGroup = doTypeChildList(Integer.parseInt(id));
		if(listGroup.size()>0) {
			for (MetadataDefinitionGroup metadataDefinitionGroup : listGroup) {
				String groupId = metadataDefinitionGroup.getId() + "";
				delete(metadataDefinitionGroup);
				groupIds+=groupId+",";
				//调用张鹏接口,根据组id删除文件分类下的元数据
			}
		}
		if(StringUtils.isNotBlank(groupIds)) {
			groupIds = groupIds.substring(0, groupIds.length()-1);
			MetadataSupport.deleMetadataByGroup(groupIds);
		}
	}
	
	@Override
	public List queryAllMetaDefinition() {
		List<MetadataDefinitionGroup> listChild = new ArrayList<MetadataDefinitionGroup>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT id,fieldZhName FROM sys_ResMetadata_Definition_Group");
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();    
			while(it.hasNext()) {    
			    Map map = (Map) it.next();
			    MetadataDefinitionGroup metadataDefinitionGroup = new MetadataDefinitionGroup();
			    metadataDefinitionGroup.setId(Long.valueOf(map.get("id").toString()));
			    metadataDefinitionGroup.setFieldZhName(map.get("fieldZhName").toString());
			    listChild.add(metadataDefinitionGroup);
			} 
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return listChild;
	}
	
	@Override
	public List queryMetaByFormat(String format) {
		List<MetadataDefinition> listMeta = new ArrayList<MetadataDefinition>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT id FROM sys_FileMetadata_Type WHERE FIND_IN_SET('"+format+"', formats)");
		try {
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString());
			if(list.size()>0){
				String id = list.get(0).get("id").toString();
				listMeta = MetadataSupport.getFileMetadataById(id);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return listMeta;
	}
	@Override
	public List getDictName(){
		List<DictName> listMeta = new ArrayList<DictName>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT indexTag,name FROM dict_name WHERE status='1' and indexTag LIKE 'relation_%'");
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();    
			while(it.hasNext()) {    
			    Map map = (Map) it.next();
			    DictName dictName = new DictName();
			    dictName.setIndexTag(map.get("indexTag").toString());
			    dictName.setName(map.get("name").toString());
			    listMeta.add(dictName);
			} 
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return listMeta;
	}
	
}
