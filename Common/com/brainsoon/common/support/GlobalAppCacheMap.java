package com.brainsoon.common.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * 
 * @ClassName: GlobalAppCacheMap 
 * @Description:应用级缓存集合
 * @author tanghui 
 * @date 2014-6-12 下午4:50:10 
 *
 */
public class GlobalAppCacheMap {
	
	protected static final Logger logger = Logger.getLogger(GlobalAppCacheMap.class);
	
//	private static Map<Object, LinkedHashMap<Object, String>> map;
	private static Map<Object, Object> map;
	
	public GlobalAppCacheMap() {
//		map = new HashMap<Object, LinkedHashMap<Object, String>>();
	}
	
	static{
		if(map == null){
			map = new HashMap<Object, Object>();
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
	public static void putKey(Object key, Object val) {
		if(key != null && val != null){
			map.put(key, val);
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
	public static Object getValue(Object key) {
		if(containsKey(key)){
			return map.get(key);
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
	public static void removeKey(Object key) {
		if(containsKey(key)){
			map.remove(key);
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
	public static boolean containsKey(Object key) {
		return map.containsKey(key);
	}
	
	
	//test
	public static void main(String[] args){}
	
}
