package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

import com.brainsoon.semantic.schema.ontology.CustomMetaData;

/**
 * @ClassName: CustomMetaDataList
 * @Description: TODO
 * @author 
 * @date 2016年3月24日 下午4:49:17
 *
 */
public class CustomMetaDataList extends BaseObjectList{
	List<CustomMetaData> customMetaDatas = new ArrayList<CustomMetaData>();
	public void addCustomMetaData(CustomMetaData customMetaData){
		if(customMetaDatas==null){
			customMetaDatas = new ArrayList<CustomMetaData>();
		}
		customMetaDatas.add(customMetaData);
	}
	public List<CustomMetaData> getCustomMetaDatas() {
		return customMetaDatas;
	}

	public void setCustomMetaDatas(List<CustomMetaData> customMetaDatas) {
		this.customMetaDatas = customMetaDatas;
	}
	
}
