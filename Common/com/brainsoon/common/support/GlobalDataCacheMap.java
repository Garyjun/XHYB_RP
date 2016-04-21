package com.brainsoon.common.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.brainsoon.system.model.Company;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.model.Staff;


/**
 * 
 * @ClassName: GlobalDataCacheMap 
 * @Description: 数据字典应用级缓存集合
 * @author tanghui 
 * @date 2014-6-12 下午4:50:10 
 *
 */
public class GlobalDataCacheMap {
	
	protected static final Logger logger = Logger.getLogger(GlobalDataCacheMap.class);
	
	private static Map<Object, LinkedHashMap<Object, String>> map;
	
	public GlobalDataCacheMap() {
	}
	
	static{
		if(map == null){
			map = new HashMap<Object, LinkedHashMap<Object, String>>();
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
	public static void putKey(Object key, LinkedHashMap<Object, String> val) {
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
	
	
	/**
	 * 
	 * @Title: getValueByKey 
	 * @Description:根据select type key 查询map对象
	 * @param   
	 * @return LinkedHashMap<Object,String> 
	 * @throws
	 */
	public static LinkedHashMap<Object, String> getValueByKey(Object key) {
		LinkedHashMap<Object,String> childMap = null;
		if(key != null){
			if(map == null){
				map = new HashMap<Object, LinkedHashMap<Object, String>>();
			}
			if(containsKey(key)){
				childMap = map.get(key);
			}else{
				childMap = queryDataByIndexTag(key);
				map.put(key, childMap);
			}
		}
		return childMap;
	}
	
	/**
	 * 
	 * @Title: putOrUpdate 
	 * @Description:刷新单个key缓存：根据key插入或更新map对象
	 * @param   
	 * @return void 
	 * @throws
	 */
	public static void refresh(Object indexTag) {
		if(indexTag != null){
			if(map == null){
				map = new HashMap<Object, LinkedHashMap<Object, String>>();
			}else{
				if(map.containsKey(indexTag)){
					map.remove(indexTag);
				}
			}
			//put
			if(queryDataByIndexTag(indexTag) != null){
				putKey(indexTag, queryDataByIndexTag(indexTag));
			}
				
		}
	}
	
	
	/**
	 * 
	 * @Title: putOrUpdate 
	 * @Description:刷新全部key缓存：查询库中的所有list对象，进行更新
	 * @param   
	 * @return void 
	 * @throws
	 */
	public static void refreshAll() {
		if(map == null){
			map = new HashMap<Object, LinkedHashMap<Object, String>>();
		}else{
			map.clear(); //清空所有
		}
		List<DictName> sysSelectTypes = OperDbUtils.queryDictNameList();
		if(sysSelectTypes != null && sysSelectTypes.size() > 0){
			for (DictName sysSelectType : sysSelectTypes) {
				String indexTag = sysSelectType.getIndexTag();
				putKey(indexTag, queryDataByIndexTag(indexTag));
			}
		}
	}
	
	/**
	 * 
	 * @Title: getNameValueWithIdByKeyAndChildKey 
	 * @Description:通过select TYPE key和 OPTION key 查询 option value
	 * @param   
	 * @return String 
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getNameValueWithIdByKeyAndChildKey(Object key,Object childKey) {
		String obj = "";
		if(key != null && key != "" && childKey != null){
			LinkedHashMap<Object, String> childMap = getValueByKey(key);
			if(childMap != null){
			    for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
		            Entry<Object, String> entry = (Entry<Object, String>)it.next();  
		            if (childKey.toString().equals(entry.getKey().toString())) {
		            	obj = entry.getValue();
		            	String[] arr = obj.split(",");
		            	obj = arr[0];
		            	break;
					}
		        }  
			}
		}
		
		return obj;
	}
	
	
	/**
	 * 
	 * @Title: getNameValueWithNameByKeyAndChildKey 
	 * @Description:通过select TYPE key和 OPTION key 查询 option value
	 * @param   
	 * @return String 
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getNameValueWithNameByKeyAndChildKey(Object key,Object childKey) {
		String obj = "";
		if(key != null  && key != "" && childKey != null){
			LinkedHashMap<Object, String> childMap = getValueByKey(key);
			if(childMap != null){
			    for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
		            Entry<Object, String> entry = (Entry<Object, String>)it.next();  
		            if (childKey.toString().equals(entry.getKey().toString())) {
		            	obj = entry.getValue();
		            	String[] arr = obj.split(",");
		            	obj = arr[1];
		            	break;
					}
		        }
			}
		}
		
		return obj;
	}

	
	/**
	 * 
	 * @Title: getNameValueWithIdByKeyAndChildKey 
	 * @Description:通过select TYPE key和 OPTION key 查询 option value
	 * @param   
	 * @return String 
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getNameValueWithIdByKeyAndChildValue(Object key,Object childValue) {
		String obj = "";
		boolean falg = false;
		if(key != null && key != "" && childValue != null){
			LinkedHashMap<Object, String> childMap = getValueByKey(key);
			if(childMap != null){
			    for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
		            Entry<Object, String> entry = (Entry<Object, String>)it.next();  
		            obj = entry.getValue();
	            	String[] arr = obj.split(",");
	            	obj = arr[1];
		            if (childValue.toString().equals(obj)) {
		            	falg = true;
		            	obj = arr[0];
		            	break;
					}
		        }  
			}
			if(!falg){
				obj = "";
			}
		}
		return obj;
	}
	/**
	 * 
	 * @Title: getNameValueWithIdByKeyAndChildKey 
	 * @Description:通过select TYPE key和 OPTION id 查询 option value
	 * @param   
	 * @return String 
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getNameValueWithIdByIdAndChildValue(Object key,Object childValue) {
		String obj = "";
		boolean falg = false;
		if(key != null && key != "" && childValue != null){
			LinkedHashMap<Object, String> childMap = getValueByKey(key);
			if(childMap != null){
			    for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
		            Entry<Object, String> entry = (Entry<Object, String>)it.next();  
		            obj = entry.getValue();
	            	String[] arr = obj.split(",");
	            	obj = arr[0];
		            if (childValue.toString().equals(obj)) {
		            	falg = true;
		            	obj = arr[1];
		            	break;
					}
		        }  
			}
			if(!falg){
				obj = "";
			}
		}
		return obj;
	}
	
		
	/**
	 * 
	 * @Title: queryDataByIndexTag 
	 * @Description:通过select TYPE indexTag（key） 查询  OPTION 对象map
	 * @param   
	 * @return LinkedHashMap<Object,String> 
	 * @throws
	 */
	public static LinkedHashMap<Object,String> queryDataByIndexTag(Object indexTag) {
		LinkedHashMap<Object,String> childMap = null; 
		if(indexTag != null){
			List<DictValue> sysSelectOpts = OperDbUtils.queryDictValueListByIndexTag(String.valueOf(indexTag));
			if(sysSelectOpts != null && sysSelectOpts.size() > 0){
				for (DictValue sysSelectOpt : sysSelectOpts) {
					if(childMap == null){
						childMap = new LinkedHashMap<Object,String>(); 
					}
					childMap.put(sysSelectOpt.getIndexTag(),sysSelectOpt.getId() + ","+ sysSelectOpt.getName());
				}
			}
		}
		return childMap;
	}
	
	/**
	 * 
	 * @Title: getChildKeyByKeyAndChildValue 
	 * @Description: 通过select TYPE key和 OPTION VALUE 查询 option key
	 * @param   
	 * @return String 
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getChildKeyByKeyAndChildValue(Object key,Object childValue) {
		String obj = "";
		if(key != null && key != "" && childValue != null){
			LinkedHashMap<Object, String> childMap = getValueByKey(key);
		    for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
	            Entry<Object, String> entry = (Entry<Object, String>)it.next();  
	            if (childValue.toString().equals(entry.getValue().toString())) {
	            	obj = (String) entry.getKey();
	            	//System.out.println(entry.getKey()+ "=" + entry.getValue()); 
	            	break;
				}
	        }  
		}
		
		return obj;
	}
	/**
	 * 
	 * @Title: getChildKeyByKeyAndChildValue 
	 * @Description: 通过select TYPE key和 OPTION VALUE 查询 option key
	 * @param   
	 * @return String 
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getChildCodeByIdAndChildValue(Object key,Object childKey) {
			String obj = "";
			if(key != null && key != "" && childKey != null){
				LinkedHashMap<Object, String> childMap = getValueByKey(key);
				if(childMap != null){
				    for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
			            Entry<Object, String> entry = (Entry<Object, String>)it.next();  
			            if (entry.getKey().toString().contains(childKey.toString())) {
			            	obj = (String) entry.getValue();
			            	String[] arr = obj.split(",");
			            	obj = arr[0];
			            	break;
						}
			        }  
				}
			}
			
			return obj;
		}
	/**
	 * 
	 * @Title: getChildKeyByKeyAndChildValue 
	 * @Description: 通过select TYPE key和 OPTION VALUE 查询 option key
	 * @param   
	 * @return String 
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getChildCodeByCodeAndChildValue(Object key,Object childCode) {
			String obj = "";
			if(key != null && key != "" && childCode != null){
				LinkedHashMap<Object, String> childMap = getValueByKey(key);
				if(childMap != null){
				    for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
			            Entry<Object, String> entry = (Entry<Object, String>)it.next(); 
			            String arr[] = entry.getValue().toString().split(",");
			            if (arr[0].contains(childCode.toString())) {
			            	obj = (String) entry.getKey();
			            	break;
						}
			        }  
				}
			}
			
			return obj;
		}
	//test
	public static void main(String[] args){}
	/**
	 * 
	 * @Title: peopleUnit缓存
	 * @Description:刷新全部key缓存：查询库中的所有list对象，进行更新
	 * @param   
	 * @return void 
	 * @throws
	 */
	public static void refreshPeopleUnit() {
		LinkedHashMap<Object, String> mapList = new LinkedHashMap<Object, String>();
		if(map == null){
			map = new HashMap<Object, LinkedHashMap<Object, String>>();
		}
//		else{
//			map.clear(); //清空所有
//		}
		List<Staff> staff = OperDbUtils.queryPeopleList();
		if(staff != null && staff.size() > 0){
			for (Staff sta : staff) {
				String indexTag = sta.getId()+"";
				mapList.put(indexTag,sta.getName());
				putKey(indexTag+"staff", mapList);
			}
		}
		List<Company> company = OperDbUtils.queryUnitList();
		if(company != null && company.size() > 0){
			for (Company com : company) {
				String indexTag = com.getId()+"";
				mapList.put(indexTag,com.getName());
				putKey(indexTag+"company", mapList);
			}
		}
	}
	/**
	 * 
	 * @Title: getNameValueWithNameByKeyAndChildKey 
	 * @Description:通过select TYPE key和寻找人员名称
	 * @param   
	 * @return String 
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getNameStaffWithNameByKeyAndChildKey(Object key,Object childKey) {
		String obj = "";
		if(key != null  && key != "" && childKey != null){
			LinkedHashMap<Object, String> childMap = getValueByKey(key);
			if(childMap != null){
			    for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
		            Entry<Object, String> entry = (Entry<Object, String>)it.next(); 
		            String childKeyArr[] = childKey.toString().split(",");
		            boolean falg = false;
		            for(String ch:childKeyArr){
			            if (ch.equals(entry.getKey().toString())&&key.toString().contains(ch)) {
			            	obj = entry.getValue();
			            	falg = true;
			            	break;
						}
		            }
		            if(falg){
		            	break;
		            }
		        }  
			}
		}
		return obj;
	}
	/**
	 * 
	 * @Title: getNameValueWithNameByKeyAndChildKey 
	 * @Description:通过select TYPE key和寻找单位名称
	 * @param   
	 * @return String 
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getNameCompanyWithNameByKeyAndChildKey(Object key,Object childKey) {
		String obj = "";
		if(key != null  && key != "" && childKey != null){
			LinkedHashMap<Object, String> childMap = getValueByKey(key);
			if(childMap != null){
			    for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
		            Entry<Object, String> entry = (Entry<Object, String>)it.next(); 
		            String childKeyArr[] = childKey.toString().split(",");
		            boolean falg = false;
		            for(String ch:childKeyArr){
			            if (ch.equals(entry.getKey().toString())&&key.toString().contains(ch)) {
			            	obj = entry.getValue();
			            	falg = true;
			            	break;
						}
		            }
		            if(falg){
		            	break;
		            }
		        }  
			}
		}
		return obj;
	}
}
