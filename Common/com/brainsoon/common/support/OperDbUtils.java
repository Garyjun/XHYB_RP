package com.brainsoon.common.support;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.system.model.Company;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.MetadataDefinitionGroup;
import com.brainsoon.system.model.Staff;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IBookService;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.IMetaDataModelService;



/**
 * 
 * @ClassName: OperDbUtils 
 * @Description: 操作数据库工具类
 * @author tanghui 
 * @date 2013-5-2 下午4:07:35 
 *
 */
public class OperDbUtils {
	private static final Logger logger = Logger.getLogger(OperDbUtils.class);
	private static IBaseService baseQueryService = null;
	
	/**
	 * 初始化服务
	 */
	static{
		 // 加载数据服务接口
		try {
			if(baseQueryService == null){
				baseQueryService = (IBaseService) BeanFactoryUtil.getBean("baseService");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据typeId查询select标签options  且为有效的
	 * @param typeId
	 * @return
	 */
	@SuppressWarnings({"rawtypes" })
	public static List<DictValue> queryDictValueListByTpyeId(String typeId){
		List<DictValue> dictValueList = null;
		String hql = "from DictName s where s.id = " + typeId;
		List list = baseQueryService.query(hql);
		if(list != null && list.size() > 0){
			DictName dictName = (DictName) list.get(0);
			dictValueList = dictName.getValueList();
		}
		return dictValueList;
	}
	
	
	/**
	 * 根据IndexTag查询select标签options 且为有效的
	 * @param indexTag
	 * @return
	 */
	@SuppressWarnings({"rawtypes" })
	public static List<DictValue> queryDictValueListByIndexTag(String indexTag){
		List<DictValue> dictValueList = null;
		String hql = "from DictName s where s.indexTag = '" + indexTag +"'";
		List list = baseQueryService.query(hql);
		if(list != null && list.size() > 0){
			DictName dictName = (DictName) list.get(0);
			dictValueList = dictName.getValueList();
			hql = "from DictValue s where s.pid = '" + dictName.getId() +"'";
			dictValueList = baseQueryService.query(hql);
		}
		return dictValueList;
	}
	
	
	/**
	 * 查询所有的select,且为有效的
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<DictName> queryDictNameList(){
		return baseQueryService.query("from DictName s where s.status=1");
	}
	
	/**
	 * 根据key查询参数value
	 * @param indexTag
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String queryParamValueByKey(String key){
		String paramValue = "";
	    try {
			String hql = "from SysParameter s where s.paraKey = '" + key + "' and s.paraStatus= 1";
			List<SysParameter> sysParameters = baseQueryService.query(hql);
			if(sysParameters != null && sysParameters.size() > 0){
				paramValue = sysParameters.get(0).getParaValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paramValue;
	}
	
	/**
	 * 根据key查询参数对象
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static SysParameter querySysParameterByKey(String key){
		SysParameter sysParameter = null;
	    try {
			String hql = "from SysParameter s where s.paraKey = '" + key + "' and s.paraStatus= 1";
			List<SysParameter> sysParameters = baseQueryService.query(hql);
			if(sysParameters != null && sysParameters.size() > 0){
				sysParameter = sysParameters.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sysParameter;
	}
	
	/**
	 * 根据userId获得user对象
	 * @param indexTag
	 * @return
	 */
	public static String getUserNameById(String id){
		String userName = "";
	    try {
			User user = (User) baseQueryService.getByPk(User.class, Long.parseLong(id));
			if(user != null){
				userName = user.getUserName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userName;
	}
	
	/**
	 * 根据userId获得user对象
	 * @param indexTag
	 * @return
	 */
	public static String getUserNameByloginName(String name){
		String userName = "";
		String hql = " from User u where u.loginName = '"+name+"'";
		try {
			List<User> users = baseQueryService.query(hql);
			if (users.size() > 0) {
				User user = users.get(0);
				userName = user.getUserName();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return userName;
	}
	
	/**
	 * 根据key查询参数value
	 * @param indexTag
	 * @return
	 */
	public static LinkedHashMap<String, String> queryValueByKey(String key){
		LinkedHashMap<String, String> childMap = null;
	    try {
	    	IBookService bookService = (IBookService) BeanFactoryUtil.getBean("bookService");
	    	childMap = bookService.getDictMapByIndex(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return childMap;
	}
	/**
	 * 根据key查询参数value
	 * @param indexTag
	 * @return
	 */
	public static LinkedHashMap<String, String> queryValueIdByKey(String key){
		LinkedHashMap<String, String> childMap = null;
	    try {
	    	IBookService bookService = (IBookService) BeanFactoryUtil.getBean("bookService");
	    	childMap = bookService.getDictMapIdByIndex(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return childMap;
	}
	/**
	 * 根据value查询参数key
	 * @param indexTag
	 * @return
	 */
	public static LinkedHashMap<String, String> queryValueByValue(String key){
		LinkedHashMap<String, String> childMap = null;
	    try {
	    	IBookService bookService = (IBookService) BeanFactoryUtil.getBean("bookService");
	    	childMap = bookService.getDictMapByValue(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return childMap;
	}
	/**
	 * 根据key查询参数value
	 * @param indexTag
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<MetaDataModelGroup> querySysResTypeList(){
		List<MetaDataModelGroup> typeNames = null;
	    try {
	    	IMetaDataModelService metaDataModelService = (IMetaDataModelService) BeanFactoryUtil.getBean("metaDataModelService");
	    	typeNames = metaDataModelService.doTypeName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return typeNames;
	}
	/**
	 * MetadataDefinitionGroup根据key查询参数value
	 * @param indexTag
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<MetadataDefinitionGroup> queryMetaDataDefinitionGroupList(){
		List<MetadataDefinitionGroup> typeNames = null;
	    try {
	    	IMetaDataModelService metaDataModelService = (IMetaDataModelService) BeanFactoryUtil.getBean("metaDataModelService");
	    	typeNames = metaDataModelService.queryAllMetaDefinition();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return typeNames;
	}
	/**
	 * 根据key查询参数value
	 * @param indexTag
	 * @return
	 */
	public static LinkedHashMap<String, String> queryMapByName(String name){
		LinkedHashMap<String, String> childMap = null;
	    try {
	    	IBookService bookService = (IBookService) BeanFactoryUtil.getBean("bookService");
	    	childMap = bookService.getDictMapByName(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return childMap;
	}
	
	
	/**
	 * 根据key查询参数value
	 * @param indexTag
	 * @return
	 */
	public static String  queryNameByIndexAndKey(String index,String key){
		String value = "";
	    try {
	    	IDictNameService dictNameService = (IDictNameService) BeanFactoryUtil.getBean("dictNameService");
	    	value = dictNameService.getValueNameByIndex(key, index);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	/**
	 * 根据value查询参数key
	 * @param indexTag
	 * @return
	 */
	public static String  queryKeyByIndexAndName(String index,String name){
		String value = "";
	    try {
	    	IDictNameService dictNameService = (IDictNameService) BeanFactoryUtil.getBean("dictNameService");
	    	value = dictNameService.getValueKeyByIndex(name, index);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	
	/**
	 * 根据key查询参数value
	 * @param indexTag
	 * @return
	 */
	public static String  queryNameById(String id){
		String value = "";
	    try {
	    	IDictNameService dictNameService = (IDictNameService) BeanFactoryUtil.getBean("dictNameService");
	    	value = dictNameService.getValueNameById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	
	//test
	public static void main(String[] args){
		System.out.println(OperDbUtils.queryParamValueByKey("prod_base_path"));
		
	}
	/**
	 * 查询所有的人员表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Staff> queryPeopleList(){
		return baseQueryService.query("from Staff");
	}
	/**
	 * 查询所有的单位表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Company> queryUnitList(){
		return baseQueryService.query("from Company");
	}
}
