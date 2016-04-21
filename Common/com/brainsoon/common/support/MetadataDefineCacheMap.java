package com.brainsoon.common.support;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.brainsoon.semantic.schema.ontology.CustomMetaData;


/**
 * 
 * @ClassName: GlobalAppCacheMap 
 * @Description:应用级缓存集合
 * @author tanghui 
 * @date 2014-6-12 下午4:50:10 
 *
 */
public class MetadataDefineCacheMap {
	
	protected static final Logger logger = Logger.getLogger(MetadataDefineCacheMap.class);
	
//	private static Map<Object, LinkedHashMap<Object, String>> map;
	private static Map<String, CustomMetaData> metadataDefineMap;
	
	public MetadataDefineCacheMap() {
//		map = new HashMap<Object, LinkedHashMap<Object, String>>();
	}
	
	static{
		if(metadataDefineMap == null){
			metadataDefineMap = new HashMap<String, CustomMetaData>();
		}
	}
	
	
	/**
	 * 
	 * @Title: put 
	 * @Description:将 val放入集合中map
	 * @param   
	 * @return void 
	 * @throws
	 */
	public static void putKey(String key, CustomMetaData val) {
		if(key != null && val != null){
			metadataDefineMap.put(key, val);
		}
	}
	
	
	/**
	 * 
	 * @Title: containsKey 
	 * @Description: 删除包含该key的map集合
	 * @param   
	 * @return boolean 
	 * @throws
	 */
	public static CustomMetaData getValue(String key) {
		if(containsKey(key)){
			return metadataDefineMap.get(key);
		}else{
			return null;
		}
	}
	/**
	 * 
	 * @Title: containsKey 
	 * @param   
	 * @return boolean 
	 * @throws
	 */
	public static Map<String, CustomMetaData> getMapValue() {
		if(metadataDefineMap != null && metadataDefineMap.size()>0){
			return metadataDefineMap;
		}else{
			return null;
		}
	}
	/**
	 * 
	 * @Title: containsKey 
	 * @Description: 删除包含该key的map集合
	 * @param   
	 * @return boolean 
	 * @throws
	 */
	public static void removeKey(String key) {
		if(containsKey(key)){
			metadataDefineMap.remove(key);
		}
	}
	
	/**
	 * 
	 * @Title: containsKey 
	 * @Description: 判断是否包含该key的map集合
	 * @param   
	 * @return boolean 
	 * @throws
	 */
	public static boolean containsKey(String key) {
		return metadataDefineMap.containsKey(key);
	}
	public static void main(String[] args){}
	
}
