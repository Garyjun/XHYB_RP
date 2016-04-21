package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AssetList extends BaseObjectList {

	private List<Asset> assets = new ArrayList<Asset>();
	
	public void addAsset(Asset asset) {
		if(assets == null) {
			assets = new ArrayList<Asset>();
		}
		assets.add(asset);
	}

	public void setAssets(List<Asset> assets) {
		this.assets = assets;
	}
	
	public List<Asset> getAssets() {
		return assets;
	}
	
	public String getJsonRes(int status) {
		String json = getJsonResSuccess();		
		if(status != 0)
			json = getJsonResFail(status);
		return json;
	}
	
	private String getJsonResSuccess() {
		String json = "{\"state\":0, \"desc\":\"创建本体描述成功\",\"type\":\"Asset\"";		
		if(getAssets() != null && getAssets().size() > 0) {
			json += ",\"domains\":[";
			for (Asset asset : getAssets()) {
				json += "{\"objectId\":\"" + asset.getObjectId() + "\"},";
			}
			json += "]";
		}
		json += "}";

		return json;
	}
	
	private String getJsonResFail(int status) {
		String json = "{\"state\":-1, \"desc\":\"创建本体描述失败，\",\"type\":\"Asset\"}"; 
		return json;
	}
}
