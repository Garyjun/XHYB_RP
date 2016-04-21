package com.brainsoon.common.support;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
public class SysResTypeCacheMap {
	
	protected static final Logger logger = Logger.getLogger(SysResTypeCacheMap.class);
	
//	private static Map<Object, LinkedHashMap<Object, String>> map;
	private static Map<Object, Object> resTypeMap;
	
	public SysResTypeCacheMap() {
//		map = new HashMap<Object, LinkedHashMap<Object, String>>();
	}
	
	static{
		if(resTypeMap == null){
			resTypeMap = new LinkedHashMap<Object, Object>();
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
			resTypeMap.put(key, val);
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
			return resTypeMap.get(key);
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
	public static Map<Object, Object> getMapValue() {
		if(resTypeMap != null && resTypeMap.size()>0){
			return resTypeMap;
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
			resTypeMap.remove(key);
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
		return resTypeMap.containsKey(key);
	}
	
	
//	/**
//	 * 
//	 * @Title: getValueByKey 
//	 * @Description:根据select type key 查询map对象
//	 * @param   
//	 * @return LinkedHashMap<Object,String> 
//	 * @throws
//	 */
//	public static LinkedHashMap<Object, String> getValueByKey(Object key) {
//		LinkedHashMap<Object,String> childMap = null;
//		if(key != null){
//			if(map == null){
//				map = new HashMap<Object, LinkedHashMap<Object, String>>();
//			}
//			if(containsKey(key)){
//				childMap = map.get(key);
//			}else{
//				childMap = queryDataByIndexTag(key);
//				map.put(key, childMap);
//			}
//		}
//		return childMap;
//	}
//	
//	/**
//	 * 
//	 * @Title: putOrUpdate 
//	 * @Description:刷新单个key缓存：根据key插入或更新map对象
//	 * @param   
//	 * @return void 
//	 * @throws
//	 */
//	public static void refresh(Object indexTag) {
//		if(indexTag != null){
//			if(map == null){
//				map = new HashMap<Object, LinkedHashMap<Object, String>>();
//			}else{
//				if(map.containsKey(indexTag)){
//					map.remove(indexTag);
//				}
//			}
//			//put
//			putKey(indexTag, queryDataByIndexTag(indexTag));
//		}
//	}
//	
//	
//	/**
//	 * 
//	 * @Title: putOrUpdate 
//	 * @Description:刷新全部key缓存：查询库中的所有list对象，进行更新
//	 * @param   
//	 * @return void 
//	 * @throws
//	 */
//	public static void refreshAll() {
//		if(map == null){
//			map = new HashMap<Object, LinkedHashMap<Object, String>>();
//		}else{
//			map.clear(); //清空所有
//		}
//		List<SysSelectType> sysSelectTypes = BaseOperDbUtils.querySelectTypesList();
//		if(sysSelectTypes != null && sysSelectTypes.size() > 0){
//			for (SysSelectType sysSelectType : sysSelectTypes) {
//				String indexTag = sysSelectType.getIndexTag();
//				//put
//				putKey(indexTag, queryDataByIndexTag(indexTag));
//			}
//		}
//	}
//	
//	/**
//	 * 
//	 * @Title: getValueByKeyAndChildKey 
//	 * @Description:通过select TYPE key和 OPTION key 查询 option value
//	 * @param   
//	 * @return String 
//	 * @throws
//	 */
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public static String getValueByKeyAndChildKey(Object key,Object childKey) {
//		String obj = "";
//		if(key != null && childKey != null){
//			LinkedHashMap<Object, String> childMap = getValueByKey(key);
//			if(childMap != null){
//			    for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
//		            Entry<Object, String> entry = (Entry<Object, String>)it.next();  
//		            if (childKey.toString().equals(entry.getKey().toString())) {
//		            	obj = entry.getValue();
//		            	//System.out.println(entry.getKey()+ "=" + entry.getValue()); 
//		            	break;
//					}
//		        }  
//			}
//		}
//		
//		return obj;
//	}
//	
//	/**
//	 * 
//	 * @Title: queryDataByIndexTag 
//	 * @Description:通过select TYPE indexTag（key） 查询  OPTION 对象map
//	 * @param   
//	 * @return LinkedHashMap<Object,String> 
//	 * @throws
//	 */
//	public static LinkedHashMap<Object,String> queryDataByIndexTag(Object indexTag) {
//		LinkedHashMap<Object,String> childMap = null; 
//		if(indexTag != null){
//			List<SysSelectOpts> sysSelectOpts = BaseOperDbUtils.querySelectOptsByIndexTag(String.valueOf(indexTag));
//			if(sysSelectOpts != null && sysSelectOpts.size() > 0){
//				for (SysSelectOpts sysSelectOpt : sysSelectOpts) {
//					if(childMap == null){
//						childMap = new LinkedHashMap<Object,String>(); 
//					}
//					childMap.put(sysSelectOpt.getOptValue(),sysSelectOpt.getOptName());
//				}
//			}
//		}
//		
//		return childMap;
//	}
//	
//	/**
//	 * 
//	 * @Title: getChildKeyByKeyAndChildValue 
//	 * @Description: 通过select TYPE key和 OPTION VALUE 查询 option key
//	 * @param   
//	 * @return String 
//	 * @throws
//	 */
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public static String getChildKeyByKeyAndChildValue(Object key,Object childValue) {
//		String obj = "";
//		if(key != null && childValue != null){
//			LinkedHashMap<Object, String> childMap = getValueByKey(key);
//		    for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
//	            Entry<Object, String> entry = (Entry<Object, String>)it.next();  
//	            if (childValue.toString().equals(entry.getValue().toString())) {
//	            	obj = (String) entry.getKey();
//	            	//System.out.println(entry.getKey()+ "=" + entry.getValue()); 
//	            	break;
//				}
//	        }  
//		}
//		
//		return obj;
//	}

	
	
	
	
	//test
	public static void main(String[] args){}
	
}
