package com.brainsoon.system.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.support.MetadataDefineCacheMap;
import com.brainsoon.common.support.SysResTypeCacheMap;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resource.po.SubjectStore;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.semantic.schema.store.jena.service.MetaService;
import com.brainsoon.system.service.ICompanyService;
import com.brainsoon.system.service.IDictValueService;
import com.brainsoon.system.service.IFLTXService;
import com.brainsoon.system.service.IStaffService;
import com.google.gson.Gson;

public class MetadataSupport {
	private static final Logger logger = Logger
			.getLogger(MetadataSupport.class);
	private static final String PUBLISH_METADATA_URL = WebappConfigUtil
			.getParameter("PUBLISH_METADATA_URL");
	private static final String PUBLISH_UPDMETADATA_URL = WebappConfigUtil
			.getParameter("PUBLISH_UPDMETADATA_URL");
	private static final String PUBLISH_DELMETADATA_URL = WebappConfigUtil
			.getParameter("PUBLISH_DELMETADATA_URL");
	private static final String PUBLISH_DELFILEMETADATA_URL = WebappConfigUtil
			.getParameter("PUBLISH_DELFILEMETADATA_URL");
	private static final String PUBLISH_DELMETADATABYGROUP_URL = WebappConfigUtil
			.getParameter("PUBLISH_DELMETADATABYGROUP_URL");
	private static IFLTXService fLTXService = null;
	private static IStaffService staffService = null;
	private static ICompanyService companyService = null;
	private static IDictValueService dictValueService = null;

	public static IFLTXService getFLTXService() {
		try {
			fLTXService = (IFLTXService) BeanFactoryUtil.getBean("FLTXService");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fLTXService;
	}

	public static IStaffService getStaffService() {
		try {
			staffService = (IStaffService) BeanFactoryUtil
					.getBean("staffService");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return staffService;
	}

	public static ICompanyService getCompanyService() {
		try {
			companyService = (ICompanyService) BeanFactoryUtil
					.getBean("companyService");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return companyService;
	}

	// 获得数据字典服务service
	public static IDictValueService getDictValueService() {
		try {
			dictValueService = (IDictValueService) BeanFactoryUtil
					.getBean("dictValueService");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dictValueService;
	}

	/**
	 * 获得定义的元数据的应用的所有的分类
	 * 
	 * @return
	 */
	public static JSONArray getCatagoryInMetadata(List<String> groupIds) {
		List<MetadataDefinition> metadataDefinitions = getAllMetadateDefineList();
		JSONArray array = new JSONArray();
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
			String groupId = metadataDefinition.getGroupId();
			if (metadataDefinition.getFieldType() == 6
					&& groupIds.contains(groupId)) {
				JSONObject obj = new JSONObject();
				obj.put("id", metadataDefinition.getValueRange());
				obj.put("name", metadataDefinition.getFieldZhName());
				obj.put("fieldName", metadataDefinition.getFieldName());
				array.add(obj);
			}
		}
		return array;
	}
	/**
	 * 获得定义的时间分类
	 * 
	 * @return
	 */
	public static JSONArray getCatagoryWithTime(List<String> groupIds) {
		List<MetadataDefinition> metadataDefinitions = getAllMetadateDefineList();
		JSONArray array = new JSONArray();
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
			String groupId = metadataDefinition.getGroupId();
			if (metadataDefinition.getIdentifier() == 19
					&& groupIds.contains(groupId)) {
				JSONObject obj = new JSONObject();
				obj.put("id", metadataDefinition.getValueRange());
				obj.put("name", metadataDefinition.getFieldZhName());
				obj.put("fieldName", metadataDefinition.getFieldName());
				array.add(obj);
			}
		}
		return array;
	}
	/**
	 * 获得定义的期刊分类
	 * 
	 * @return
	 */
	public static JSONArray getCatagoryWithJourna(List<String> groupIds) {
		List<MetadataDefinition> metadataDefinitions = getAllMetadateDefineList();
		JSONArray array = new JSONArray();
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
			String groupId = metadataDefinition.getGroupId();
			if (metadataDefinition.getIdentifier() == 20
					&& groupIds.contains(groupId)) {
				JSONObject obj = new JSONObject();
				obj.put("id", metadataDefinition.getValueRange());
				obj.put("name", metadataDefinition.getFieldZhName());
				obj.put("fieldName", metadataDefinition.getFieldName());
				array.add(obj);
			}
		}
		return array;
	}
	/**
	 * 根据选择的分类类型或得相应的字段
	 * 
	 * @return
	 */
	public static String getFieldNameeByCategoryType(String categotyType) {
		List<MetadataDefinition> metadataDefinitions = getAllMetadateDefineList();
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
			String groupId = metadataDefinition.getGroupId();
			if (metadataDefinition.getFieldType() == 6
					&& categotyType.equals(metadataDefinition.getValueRange())) {
				return metadataDefinition.getFieldName();
			}
		}
		return "";
	}

	/**
	 * 获得定义的元数据的应用的所有的分类
	 * 
	 * @return
	 */
	public static List<MetadataDefinition> getMetadataByGroupId(String groupId) {
		List<MetadataDefinition> metaDataDefineTemps = new ArrayList<MetadataDefinition>();
		CustomMetaData commonMetaData = MetadataDefineCacheMap
				.getValue("CommonMetaData");
		CustomMetaData baseMetaData = MetadataDefineCacheMap
				.getValue("BaseMetaData");
		if (commonMetaData != null) {
			if (commonMetaData.getCustomPropertys() != null
					&& commonMetaData.getCustomPropertys().size() > 0) {
				metaDataDefineTemps.addAll(commonMetaData.getCustomPropertys());
			}
		}
		if (baseMetaData != null) {
			if (baseMetaData.getCustomPropertys() != null
					&& baseMetaData.getCustomPropertys().size() > 0) {
				metaDataDefineTemps.addAll(baseMetaData.getCustomPropertys());
			}
		}
		List<MetadataDefinition> groupMetadataDefinitions = new ArrayList<MetadataDefinition>();
		for (MetadataDefinition metadataDefinition : metaDataDefineTemps) {
			if (groupId.equals(metadataDefinition.getGroupId())) {
				groupMetadataDefinitions.add(metadataDefinition);
			}
		}
		try {
			// 比较器
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//			Collections.sort(groupMetadataDefinitions);
			// 自定义比较器：排序
	        Collections.sort(groupMetadataDefinitions,new Comparator<MetadataDefinition>(){ 
	            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
	            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
	            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
	            	}else{
	            		return 0;
	            	}
	            }
	        });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupMetadataDefinitions;
	}

	/**
	 * 获得定义的文件元数据
	 * 
	 * @return
	 */
	public static List<MetadataDefinition> getFileMetadataById(String id) {
		List<MetadataDefinition> metaDataDefineTemps = new ArrayList<MetadataDefinition>();
		CustomMetaData commonMetaData = MetadataDefineCacheMap
				.getValue("FileMetaData");
		if (commonMetaData != null) {
			if (commonMetaData.getCustomPropertys() != null
					&& commonMetaData.getCustomPropertys().size() > 0) {
				metaDataDefineTemps.addAll(commonMetaData.getCustomPropertys());
			}
		}
		List<MetadataDefinition> groupMetadataDefinitions = new ArrayList<MetadataDefinition>();
		for (MetadataDefinition metadataDefinition : metaDataDefineTemps) {
			if (id.equals(metadataDefinition.getResType())) {
				groupMetadataDefinitions.add(metadataDefinition);
			}
		}
		try {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//			Collections.sort(groupMetadataDefinitions);
			// 自定义比较器：排序
	        Collections.sort(groupMetadataDefinitions,new Comparator<MetadataDefinition>(){ 
	            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
	            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
	            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
	            	}else{
	            		return 0;
	            	}
	            }
	        });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupMetadataDefinitions;
	}

	/**
	 * 获得通用元数据的列表集合
	 * 
	 * @return
	 */
	public static List<MetadataDefinition> getCommonMetadatas() {
		CustomMetaData commonMetaData = MetadataDefineCacheMap
				.getValue("CommonMetaData");
		if (commonMetaData != null) {
			List<MetadataDefinition> groupMetadataDefinitions = commonMetaData
					.getCustomPropertys();
			try {
				System.setProperty("java.util.Arrays.useLegacyMergeSort",
						"true");
//				Collections.sort(groupMetadataDefinitions);
				// 自定义比较器：排序
		        Collections.sort(groupMetadataDefinitions,new Comparator<MetadataDefinition>(){ 
		            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
		            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
		            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
		            	}else{
		            		return 0;
		            	}
		            }
		        });
			} catch (Exception e) {
				e.printStackTrace();
			}
			return groupMetadataDefinitions;
		}
		return null;
	}

	/**
	 * 获得文件元数据的列表集合
	 * 
	 * @return
	 */
	public static List<MetadataDefinition> getFileMetadatas() {
		CustomMetaData fileMetaData = MetadataDefineCacheMap
				.getValue("FileMetaData");
		if (fileMetaData != null) {
			List<MetadataDefinition> groupMetadataDefinitions = fileMetaData
					.getCustomPropertys();
			try {
				System.setProperty("java.util.Arrays.useLegacyMergeSort",
						"true");
//				Collections.sort(groupMetadataDefinitions);
				// 自定义比较器：排序
		        Collections.sort(groupMetadataDefinitions,new Comparator<MetadataDefinition>(){ 
		            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
		            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
		            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
		            	}else{
		            		return 0;
		            	}
		            }
		        });
			} catch (Exception e) {
				e.printStackTrace();
			}
			return groupMetadataDefinitions;
		}
		return null;
	}

	/**
	 * 根据Uri获得元数据对象 isCom 1是核心 2是普通
	 * 
	 * @return
	 */
	public static MetadataDefinition getMetadataDefineByGroupId(String groupId,
			String uri, String isCom) {
		List<MetadataDefinition> metaDataDefineTemps = new ArrayList<MetadataDefinition>();
		CustomMetaData commonMetaData = MetadataDefineCacheMap
				.getValue("CommonMetaData");
		CustomMetaData baseMetaData = MetadataDefineCacheMap
				.getValue("BaseMetaData");
		CustomMetaData fileMetaData = MetadataDefineCacheMap
				.getValue("FileMetaData");
		if ("2".equals(isCom)) {
			if (baseMetaData != null) {
				metaDataDefineTemps.addAll(baseMetaData.getCustomPropertys());
			}
		} else if ("1".equals(isCom)) {
			if (commonMetaData != null) {
				metaDataDefineTemps.addAll(commonMetaData.getCustomPropertys());
			}
		} else if ("3".equals(isCom)) {
			if (fileMetaData != null) {
				metaDataDefineTemps.addAll(fileMetaData.getCustomPropertys());
			}
		}
		if (metaDataDefineTemps != null && metaDataDefineTemps.size() > 0) {
			for (MetadataDefinition metadataDefinition : metaDataDefineTemps) {
				if (uri.equals(metadataDefinition.getUri())) {
					return metadataDefinition;
				}
			}
		}
		return null;
	}

	/**
	 * 获得定义的元数据的应用的所有的分类
	 * 
	 * @return
	 */
	public static Map<String, MetadataDefinition> getAllMetadataDefinition() {
		List<MetadataDefinition> metadataDefinitions = new ArrayList<MetadataDefinition>();
		CustomMetaData commonMetaData = MetadataDefineCacheMap
				.getValue("CommonMetaData");
		CustomMetaData baseMetaData = MetadataDefineCacheMap
				.getValue("BaseMetaData");
		if (commonMetaData != null) {
			if (commonMetaData.getCustomPropertys() != null
					&& commonMetaData.getCustomPropertys().size() > 0) {
				metadataDefinitions.addAll(commonMetaData.getCustomPropertys());
			}
		}
		if (baseMetaData != null) {
			if (baseMetaData.getCustomPropertys() != null
					&& baseMetaData.getCustomPropertys().size() > 0) {
				metadataDefinitions.addAll(baseMetaData.getCustomPropertys());
			}
		}
		Map<String, MetadataDefinition> metadataDefinitionsMap = new HashMap<String, MetadataDefinition>();
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
			metadataDefinitionsMap.put(metadataDefinition.getFieldName(),
					metadataDefinition);
		}
		return metadataDefinitionsMap;
	}

	/**
	 * 根据类型获得定义的元数据的应用的所有的分类
	 * 
	 * @return
	 */
	public static Map<String, MetadataDefinition> getAllMetadataDefinition(
			String publishType) {
		List<MetadataDefinition> metadataDefinitions = new ArrayList<MetadataDefinition>();
		CustomMetaData commonMetaData = MetadataDefineCacheMap
				.getValue("CommonMetaData");
		CustomMetaData baseMetaData = MetadataDefineCacheMap
				.getValue("BaseMetaData");
		if (commonMetaData != null) {
			if (commonMetaData.getCustomPropertys() != null
					&& commonMetaData.getCustomPropertys().size() > 0) {
				metadataDefinitions.addAll(commonMetaData.getCustomPropertys());
			}
		}
		if (baseMetaData != null) {
			if (baseMetaData.getCustomPropertys() != null
					&& baseMetaData.getCustomPropertys().size() > 0) {
				metadataDefinitions.addAll(baseMetaData.getCustomPropertys());
			}
		}
		Map<String, MetadataDefinition> metadataDefinitionsMap = new HashMap<String, MetadataDefinition>();
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
			if (publishType.equals(metadataDefinition.getResType())
					|| "1".equals(metadataDefinition.getGroupId())) {
				metadataDefinitionsMap.put(metadataDefinition.getFieldName(),
						metadataDefinition);
			}
		}
		return metadataDefinitionsMap;
	}

	/**
	 * 获得定义的元数据的应用的所有的分类
	 * 
	 * @return
	 */
	public static List<MetadataDefinition> getAllMetadataDefinitionByResType(
			String publishType) {
		List<MetadataDefinition> metadataDefinitions = new ArrayList<MetadataDefinition>();
		List<MetadataDefinition> returnMetadataDefinitions = new ArrayList<MetadataDefinition>();
		CustomMetaData commonMetaData = MetadataDefineCacheMap
				.getValue("CommonMetaData");
		CustomMetaData baseMetaData = MetadataDefineCacheMap
				.getValue("BaseMetaData");
		if (commonMetaData != null) {
			if (commonMetaData.getCustomPropertys() != null
					&& commonMetaData.getCustomPropertys().size() > 0) {
				metadataDefinitions.addAll(commonMetaData.getCustomPropertys());
			}
		}
		if (baseMetaData != null) {
			if (baseMetaData.getCustomPropertys() != null
					&& baseMetaData.getCustomPropertys().size() > 0) {
				metadataDefinitions.addAll(baseMetaData.getCustomPropertys());
			}
		}
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
			if (publishType.equals(metadataDefinition.getResType())
					|| "".equals(metadataDefinition.getResType())) {
				returnMetadataDefinitions.add(metadataDefinition);
			}
		}
		try {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//			Collections.sort(returnMetadataDefinitions);
			// 自定义比较器：排序
	        Collections.sort(returnMetadataDefinitions,new Comparator<MetadataDefinition>(){ 
	            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
	            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
	            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
	            	}else{
	            		return 0;
	            	}
	            }
	        });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMetadataDefinitions;
	}

	/**
	 * 获得定义的元数据的字段和字段值
	 * 
	 * @return
	 */
	public static Map<String, String> getAllMetaFieldNameAndFieldZhName() {
		List<MetadataDefinition> metadataDefinitions = getAllMetadateDefineList();
		Map<String, String> metadataDefinitionsMap = new HashMap<String, String>();
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
			metadataDefinitionsMap.put(metadataDefinition.getFieldName(),
					metadataDefinition.getFieldZhName());
		}
		return metadataDefinitionsMap;
	}

	/**
	 * 根据类型获得定义的元数据的字段和字段值
	 * 
	 * @return
	 */
	public static Map<String, String> getAllMetaFieldNameAndFieldZhName(
			String publishType) {
		List<MetadataDefinition> metadataDefinitions = getAllMetadateDefineList();
		Map<String, String> metadataDefinitionsMap = new HashMap<String, String>();
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
			if (metadataDefinition.getResType() != null
					&& metadataDefinition.getResType().equals(publishType)) {
				metadataDefinitionsMap.put(metadataDefinition.getFieldName(),
						metadataDefinition.getFieldZhName());
			}
		}
		return metadataDefinitionsMap;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public static List<CustomMetaData> getAllMetadateList(UserInfo user,
			String publishType) {
		List<CustomMetaData> customMetaDatas = new ArrayList<CustomMetaData>();
		CustomMetaData customMetaData = MetadataDefineCacheMap
				.getValue("CommonMetaData");
		CustomMetaData customMetaDataTemp = null;
		if (customMetaData != null) {
			customMetaDataTemp = new CustomMetaData();
			customMetaDataTemp.setNameCN(customMetaData.getNameCN());
		}
		CustomMetaData baseMetaData = MetadataDefineCacheMap
				.getValue("BaseMetaData");
		CustomMetaData baseMetaDataTemp = null;
		if (baseMetaData != null) {
			baseMetaDataTemp = new CustomMetaData();
			baseMetaDataTemp.setNameCN(baseMetaData.getNameCN());
		}
		List<MetadataDefinition> metadataDefines = user.getMetadataList();
		if (metadataDefines != null) {
			if (user != null) {
				Set<String> set = new HashSet<String>();
				for (MetadataDefinition metadataDefinition : metadataDefines) {
					set.add(metadataDefinition.getUri());
				}
				if (metadataDefines != null) {
					if (customMetaData != null) {
						List<MetadataDefinition> tempList = customMetaData
								.getCustomPropertys();
						List<MetadataDefinition> tempMetadataList = new ArrayList<MetadataDefinition>();
						for (MetadataDefinition metadataDefinition : tempList) {
							if (metadataDefinition.getFieldName().equals(
									"abstract")) {
								System.out.println("");
							}
							if (set.contains(metadataDefinition.getUri())) {
								if ("true".equals(metadataDefinition
										.getShowField())) {
									tempMetadataList.add(metadataDefinition);
								}
							}
						}
						try {
							System.setProperty(
									"java.util.Arrays.useLegacyMergeSort",
									"true");
//							Collections.sort(tempMetadataList);
							// 自定义比较器：排序
					        Collections.sort(tempMetadataList,new Comparator<MetadataDefinition>(){ 
					            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
					            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
					            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
					            	}else{
					            		return 0;
					            	}
					            }
					        });
						} catch (Exception e) {
							e.printStackTrace();
						}
						customMetaDataTemp.setCustomPropertys(tempMetadataList);
					}
					if (baseMetaData != null) {
						List<MetadataDefinition> btempList = baseMetaData
								.getCustomPropertys();
						List<MetadataDefinition> btempMetadataList = new ArrayList<MetadataDefinition>();
						for (MetadataDefinition metadataDefinition : btempList) {
							if (metadataDefinition.getFieldName().equals(
									"abstract")) {
								System.out.println("");
							}
							if (set.contains(metadataDefinition.getUri())) {
								if ("true".equals(metadataDefinition
										.getShowField())) {
									if (StringUtils.isNotBlank(publishType)
											&& publishType
													.equals(metadataDefinition
															.getResType())) {
										btempMetadataList
												.add(metadataDefinition);
									}
								}
							}
						}
						try {
							// System.setProperty("java.util.Arrays.useLegacyMergeSort",
							// "true");
							// Collections.sort(btempMetadataList);
//							Collections.sort(btempMetadataList,
//									new PriceComparator());
							baseMetaDataTemp
									.setCustomPropertys(btempMetadataList);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// 自定义比较器：排序
				        Collections.sort(btempMetadataList,new Comparator<MetadataDefinition>(){ 
				            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
				            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
				            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
				            	}else{
				            		return 0;
				            	}
				            }
				        });
						baseMetaDataTemp.setCustomPropertys(btempMetadataList);
					}
					if (customMetaDataTemp != null) {
						customMetaDatas.add(customMetaDataTemp);
					}
					if (baseMetaDataTemp != null) {
						customMetaDatas.add(baseMetaDataTemp);
					}
					return customMetaDatas;
				}

			}
			if (customMetaData != null) {
				customMetaDatas.add(customMetaData);
			}
			if (baseMetaData != null) {
				List<MetadataDefinition> btempList = baseMetaData
						.getCustomPropertys();
				List<MetadataDefinition> btempMetadataList = new ArrayList<MetadataDefinition>();
				for (MetadataDefinition metadataDefinition : btempList) {
					if (!"1".equals(metadataDefinition.getShowField())) {
						if (StringUtils.isNotBlank(publishType)
								&& publishType.equals(metadataDefinition
										.getResType())) {
							btempMetadataList.add(metadataDefinition);
						}
					}
				}
				try {
					System.setProperty("java.util.Arrays.useLegacyMergeSort",
							"true");
					// 自定义比较器：排序
			        Collections.sort(btempMetadataList,new Comparator<MetadataDefinition>(){ 
			            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) { 
			            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
			            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
			            	}else{
			            		return 0;
			            	} 
			            }
			        });
				} catch (Exception e) {
					e.printStackTrace();
				}
				baseMetaDataTemp.setCustomPropertys(btempMetadataList);
				customMetaDatas.add(baseMetaDataTemp);
			}
		}
		return customMetaDatas;
	}

	/**
	 * 该user拥有的权限字段
	 * 
	 * @return
	 */
	public static List<MetadataDefinition> getAllMetadateDefineList() {
		UserInfo user = LoginUserUtil.getLoginUser();
		List<MetadataDefinition> list = null;
		if (user != null) {
			list = LoginUserUtil.getLoginUser().getMetadataList();
			if (list != null) {
				try {
					System.setProperty("java.util.Arrays.useLegacyMergeSort",
							"true");
//					Collections.sort(list);
					// 自定义比较器：排序
			        Collections.sort(list,new Comparator<MetadataDefinition>(){ 
			            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
			            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
			            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
			            	}else{
			            		return 0;
			            	}
			            }
			        });
				} catch (Exception e) {
					e.printStackTrace();
				}
				return list;
			} else {
				list = new ArrayList<MetadataDefinition>();
				getMetaList(list);
			}
		} else {
			list = new ArrayList<MetadataDefinition>();
			getMetaList(list);
		}
		try {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//			Collections.sort(list);
			// 自定义比较器：排序
	        Collections.sort(list,new Comparator<MetadataDefinition>(){ 
	            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
	            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
	            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
	            	}else{
	            		return 0;
	            	}
	            }
	        });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 该user拥有的权限字段(批量检索字段)
	 * 
	 * @return
	 */
	public static List<MetadataDefinition> getBatchQueryMetadateDefineList(
			String publishType) {
		UserInfo user = LoginUserUtil.getLoginUser();
		List<MetadataDefinition> list = null;
		List<MetadataDefinition> returnMetadatas = new ArrayList<MetadataDefinition>();
		if (user != null) {
			list = LoginUserUtil.getLoginUser().getMetadataList();
			if (list != null) {
				for (MetadataDefinition metadataDefinition : list) {
					String openQuery = metadataDefinition.getOpenQuery();
					if (StringUtils.isNotBlank(openQuery)
							&& "0".equals(openQuery)
							&& (publishType.equals(metadataDefinition
									.getResType()) || "1"
									.equals(metadataDefinition.getGroupId()))) {
						returnMetadatas.add(metadataDefinition);
					}
				}
				try {
					System.setProperty("java.util.Arrays.useLegacyMergeSort",
							"true");
//					Collections.sort(returnMetadatas);
					// 自定义比较器：排序
			        Collections.sort(returnMetadatas,new Comparator<MetadataDefinition>(){ 
			            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
			            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
			            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
			            	}else{
			            		return 0;
			            	}
			            }
			        });
				} catch (Exception e) {
					e.printStackTrace();
				}
				return returnMetadatas;
			} else {
				list = new ArrayList<MetadataDefinition>();
				getMetaList(list);
			}
		} else {
			list = new ArrayList<MetadataDefinition>();
			getMetaList(list);
		}
		for (MetadataDefinition metadataDefinition : list) {
			String openQuery = metadataDefinition.getOpenQuery();
			if (StringUtils.isNotBlank(openQuery)
					&& "0".equals(openQuery)
					&& (publishType.equals(metadataDefinition.getResType()) || "1"
							.equals(metadataDefinition.getGroupId()))) {
				returnMetadatas.add(metadataDefinition);
			}
		}
		try {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//			Collections.sort(returnMetadatas);
	        Collections.sort(returnMetadatas,new Comparator<MetadataDefinition>(){ 
	            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
	            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
	            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
	            	}else{
	            		return 0;
	            	}
	            }
	        });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMetadatas;
	}

	/**
	 * 该user拥有的权限字段(批量检索字段)
	 * 
	 * @return
	 */
	public static List<MetadataDefinition> getAllBatchQuery() {
		UserInfo user = LoginUserUtil.getLoginUser();
		List<MetadataDefinition> list = null;
		List<MetadataDefinition> returnMetadatas = new ArrayList<MetadataDefinition>();
		if (user != null) {
			list = LoginUserUtil.getLoginUser().getMetadataList();
			if (list != null) {
				for (MetadataDefinition metadataDefinition : list) {
					String openQuery = metadataDefinition.getOpenQuery();
					if (StringUtils.isNotBlank(openQuery)
							&& "0".equals(openQuery)) {
						returnMetadatas.add(metadataDefinition);
					}
				}
				try {
					System.setProperty("java.util.Arrays.useLegacyMergeSort",
							"true");
//					Collections.sort(returnMetadatas);
			        Collections.sort(returnMetadatas,new Comparator<MetadataDefinition>(){ 
			            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
			            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
			            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
			            	}else{
			            		return 0;
			            	}
			            }
			        });
				} catch (Exception e) {
					e.printStackTrace();
				}
				return returnMetadatas;
			} else {
				list = new ArrayList<MetadataDefinition>();
				getMetaList(list);
			}
		} else {
			list = new ArrayList<MetadataDefinition>();
			getMetaList(list);
		}
		for (MetadataDefinition metadataDefinition : list) {
			String openQuery = metadataDefinition.getOpenQuery();
			if (StringUtils.isNotBlank(openQuery) && "0".equals(openQuery)) {
				returnMetadatas.add(metadataDefinition);
			}
		}
		try {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//			Collections.sort(returnMetadatas);
	        Collections.sort(returnMetadatas,new Comparator<MetadataDefinition>(){ 
	            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
	            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
	            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
	            	}else{
	            		return 0;
	            	}
	            }
	        });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMetadatas;
	}

	/**
	 * 该user拥有的权限字段
	 * 
	 * @return高级查询
	 */
	public static List<MetadataDefinition> getSeniorMetadateDefineList() {
		UserInfo user = LoginUserUtil.getLoginUser();
		List<MetadataDefinition> list = null;
		List<MetadataDefinition> seniorMetadateList = new ArrayList<MetadataDefinition>();
		if (user != null) {
			list = LoginUserUtil.getLoginUser().getMetadataList();
			if (list != null) {
				try {
					System.setProperty("java.util.Arrays.useLegacyMergeSort",
							"true");
//					Collections.sort(list);
			        Collections.sort(list,new Comparator<MetadataDefinition>(){ 
			            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
			            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
			            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
			            	}else{
			            		return 0;
			            	}
			            }
			        });
				} catch (Exception e) {
					e.printStackTrace();
				}
				return list;
			} else {
				list = new ArrayList<MetadataDefinition>();
				getMetaList(list);
			}
		} else {
			list = new ArrayList<MetadataDefinition>();
			getMetaList(list);
		}
		if (list != null) {
			for (MetadataDefinition metadataDefinition : list) {
				if (metadataDefinition.getAllowAdvancedQuery() != null
						&& "true".equals(metadataDefinition
								.getAllowAdvancedQuery())) {
					seniorMetadateList.add(metadataDefinition);
				}
			}
		}
		try {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//			Collections.sort(seniorMetadateList);
	        Collections.sort(seniorMetadateList,new Comparator<MetadataDefinition>(){ 
	            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
	            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
	            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
	            	}else{
	            		return 0;
	            	}
	            }
	        });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return seniorMetadateList;
	}

	/**
	 * 该user拥有的权限通用元数据字段(垮库检索);
	 * 
	 * @return
	 */
	public static List<MetadataDefinition> getCommonMetadateDefineList() {
		UserInfo user = LoginUserUtil.getLoginUser();
		List<MetadataDefinition> list = new ArrayList<MetadataDefinition>();
		CustomMetaData commonMetaData = MetadataDefineCacheMap
				.getValue("CommonMetaData");
		if (commonMetaData != null) {
			list.addAll(commonMetaData.getCustomPropertys());
		}
		for (int i = 0; i < list.size(); i++) {
			MetadataDefinition metadataDefinition = list.get(i);
			if ("1".equals(metadataDefinition.getShowField())) {
				list.remove(i);
			}
		}
		if (user != null) {
			List<MetadataDefinition> userMetadatelist = LoginUserUtil
					.getLoginUser().getMetadataList();
			if (list != null) {
				List<MetadataDefinition> tempList = new ArrayList<MetadataDefinition>();
				for (MetadataDefinition metadataDefinition : userMetadatelist) {
					if (list.contains(metadataDefinition)) {
						tempList.add(metadataDefinition);
					}
				}
				try {
					System.setProperty("java.util.Arrays.useLegacyMergeSort",
							"true");
//					Collections.sort(tempList);
			        Collections.sort(tempList,new Comparator<MetadataDefinition>(){ 
			            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
			            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
			            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
			            	}else{
			            		return 0;
			            	}
			            }
			        });
				} catch (Exception e) {
					e.printStackTrace();
				}
				return tempList;
			}
		}
		try {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//			Collections.sort(list);
	        Collections.sort(list,new Comparator<MetadataDefinition>(){ 
	            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
	            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
	            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
	            	}else{
	            		return 0;
	            	}
	            }
	        });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 该user拥有的权限字段(根据资源类型)
	 * 
	 * @return
	 */
	public static List<MetadataDefinition> getMetadateDefinesByResType(
			String resType) {
		UserInfo user = LoginUserUtil.getLoginUser();
		List<MetadataDefinition> list = null;
		List<MetadataDefinition> resTypeList = new ArrayList<MetadataDefinition>();
		if (user != null) {
			list = new ArrayList<MetadataDefinition>();
			// getMetaList(list);
			list = LoginUserUtil.getLoginUser().getMetadataList();
			if (list != null) {
			} else {
				list = new ArrayList<MetadataDefinition>();
				getMetaList(list);
			}
		} else {
			list = new ArrayList<MetadataDefinition>();
			getMetaList(list);
		}
		if (StringUtils.isNotBlank(resType)) {
			for (MetadataDefinition metadataDefinition : list) {
				String allowAdvancedQuery = metadataDefinition
						.getAllowAdvancedQuery();
				if (StringUtils.isNotBlank(allowAdvancedQuery)
						&& "true".equals(allowAdvancedQuery)
						&& (metadataDefinition.getResType().equals(resType) || "1"
								.equals(metadataDefinition.getGroupId()))) {
					resTypeList.add(metadataDefinition);
				}
			}
		} else {
			if (list != null) {
				for (MetadataDefinition metadataDefinition : list) {
					if (metadataDefinition.getAllowAdvancedQuery() != null
							&& "true".equals(metadataDefinition
									.getAllowAdvancedQuery())) {
						resTypeList.add(metadataDefinition);
					}
				}
			}
			try {
				System.setProperty("java.util.Arrays.useLegacyMergeSort",
						"true");
//				Collections.sort(resTypeList);
		        Collections.sort(resTypeList,new Comparator<MetadataDefinition>(){ 
		            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
		            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
		            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
		            	}else{
		            		return 0;
		            	}
		            }
		        });
			} catch (Exception e) {
				e.printStackTrace();
			}
			return resTypeList;
		}
		try {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//			Collections.sort(resTypeList);
	        Collections.sort(resTypeList,new Comparator<MetadataDefinition>(){ 
	            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
	            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
	            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
	            	}else{
	            		return 0;
	            	}
	            }
	        });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resTypeList;
	}

	/**
	 * 根据资源类型获得相应元数据
	 * 
	 * @return
	 */
	public static List<MetadataDefinition> getMetadateDefines(String resType) {
		logger.info("进入getMetadateDefines方法！");
		UserInfo user = LoginUserUtil.getLoginUser();
		List<MetadataDefinition> list = null;
		logger.info("进入getMetadateDefines方法！");
		List<MetadataDefinition> resTypeList = new ArrayList<MetadataDefinition>();
		if (user != null) {
			logger.info("进入getMetadateDefines方法user不为空！");
			list = LoginUserUtil.getLoginUser().getMetadataList();
			if (list != null) {
			} else {
				list = new ArrayList<MetadataDefinition>();
				getMetaList(list);
			}
		} else {
			logger.info("进入getMetadateDefines方法user为空！");
			list = new ArrayList<MetadataDefinition>();
			logger.info("进入getMetadateDefines方法user为空1！");
			getMetaList(list);
			logger.info("进入getMetadateDefines方法user为空2！");
		}
		logger.info("进入getMetadateDefines方法2！");
		if (StringUtils.isNotBlank(resType)) {
			for (MetadataDefinition metadataDefinition : list) {
				// if(metadataDefinition.getFieldZhName().equals("来源")){
				// System.out.println("");
				// }
				if ((metadataDefinition.getResType() != null
						&& metadataDefinition.getResType().equals(resType) || "1"
							.equals(metadataDefinition.getGroupId()))
						|| "".equals(metadataDefinition.getResType())) {
					resTypeList.add(metadataDefinition);
				}
			}
		} else {
			if (list != null) {
				for (MetadataDefinition metadataDefinition : list) {
					if (metadataDefinition.getAllowAdvancedQuery() != null
							&& "true".equals(metadataDefinition
									.getAllowAdvancedQuery())) {
						resTypeList.add(metadataDefinition);
					}
				}
			}
			try {
				System.setProperty("java.util.Arrays.useLegacyMergeSort",
						"true");
//				Collections.sort(resTypeList);
		        Collections.sort(resTypeList,new Comparator<MetadataDefinition>(){ 
		            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
		            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
		            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
		            	}else{
		            		return 0;
		            	}
		            }
		        });
			} catch (Exception e) {
			}
			return resTypeList;
		}
		logger.info("进入getMetadateDefines方法3！");
		try {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//			Collections.sort(resTypeList);
	        Collections.sort(resTypeList,new Comparator<MetadataDefinition>(){ 
	            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
	            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
	            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
	            	}else{
	            		return 0;
	            	}
	            }
	        });
		} catch (Exception e) {
		}
		return resTypeList;
	}

	public static List<MetadataDefinition> getMetaList(
			List<MetadataDefinition> list) {
		logger.info("进入getMetaList方法！");
		CustomMetaData commonMetaData = MetadataDefineCacheMap
				.getValue("CommonMetaData");
		CustomMetaData baseMetaData = MetadataDefineCacheMap
				.getValue("BaseMetaData");
		if (commonMetaData != null) {
			logger.info("进入getMetaList方法1！" + commonMetaData);
			list.addAll(commonMetaData.getCustomPropertys());
		}
		if (baseMetaData != null) {
			logger.info("进入getMetaList方法2！" + baseMetaData);
			list.addAll(baseMetaData.getCustomPropertys());
		}
		logger.info("进入getMetaList方法3！");
		for (int i = 0; i < list.size(); i++) {
			MetadataDefinition metadataDefinition = list.get(i);
			if ("1".equals(metadataDefinition.getShowField())) {
				list.remove(i);
			}
		}
		try {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//			Collections.sort(list);
	        Collections.sort(list,new Comparator<MetadataDefinition>(){ 
	            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
	            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
	            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
	            	}else{
	            		return 0;
	            	}
	            }
	        });
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("进入getMetaList方法4！" + list.size());
		}
		logger.info("进入getMetaList方法5！");
		return list;
	}

	/**
	 * 获得通用元数据
	 * 
	 * @param list
	 * @return
	 */
	public static List<MetadataDefinition> getCommonMetaList(
			List<MetadataDefinition> list) {
		CustomMetaData commonMetaData = MetadataDefineCacheMap
				.getValue("CommonMetaData");
		if (commonMetaData != null) {
			list.addAll(commonMetaData.getCustomPropertys());
		}
		for (int i = 0; i < list.size(); i++) {
			MetadataDefinition metadataDefinition = list.get(i);
			if ("1".equals(metadataDefinition.getShowField())) {
				list.remove(i);
			}
		}
		try {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//			Collections.sort(list);
			 Collections.sort(list,new Comparator<MetadataDefinition>(){ 
		            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
		            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
		            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
		            	}else{
		            		return 0;
		            	}
		            }
		        });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 所有的字段元数据
	 * 
	 * @return
	 */
	public static List<MetadataDefinition> getMetadateDefineList() {
		List<MetadataDefinition> metaDataDefineTemps = new ArrayList<MetadataDefinition>();
		CustomMetaData commonMetaData = MetadataDefineCacheMap
				.getValue("CommonMetaData");
		CustomMetaData baseMetaData = MetadataDefineCacheMap
				.getValue("BaseMetaData");
		if (commonMetaData != null) {
			metaDataDefineTemps.addAll(commonMetaData.getCustomPropertys());
		}
		if (baseMetaData != null) {
			metaDataDefineTemps.addAll(baseMetaData.getCustomPropertys());
		}
		try {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//			Collections.sort(metaDataDefineTemps);
			 Collections.sort(metaDataDefineTemps,new Comparator<MetadataDefinition>(){ 
		            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
		            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
		            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
		            	}else{
		            		return 0;
		            	}
		            }
		        });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return metaDataDefineTemps;
	}

	/**
	 * 所有的资源类型下的元数据(不包含通用元数据)
	 * 
	 * @return
	 */
	public static List<MetadataDefinition> getPrivilegeMetadateDefineList(
			String resType) {
		List<MetadataDefinition> metaDataDefineTemps = new ArrayList<MetadataDefinition>();
		List<MetadataDefinition> metaDataDefines = new ArrayList<MetadataDefinition>();
		CustomMetaData baseMetaData = MetadataDefineCacheMap
				.getValue("BaseMetaData");
		if (baseMetaData != null) {
			metaDataDefineTemps.addAll(baseMetaData.getCustomPropertys());
		}
		if (StringUtils.isNotBlank(resType)) {
			for (MetadataDefinition metadataDefinition : metaDataDefineTemps) {
				if (metadataDefinition.getResType().equals(resType)) {
					metaDataDefines.add(metadataDefinition);
				}
			}
		}
		try {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//			Collections.sort(metaDataDefines);
			 Collections.sort(metaDataDefines,new Comparator<MetadataDefinition>(){ 
		            public int compare(MetadataDefinition arg0, MetadataDefinition arg1) {
		            	if(arg0.getOrderNum()!=null && arg1.getOrderNum()!=null){
		            		return arg0.getOrderNum().compareTo(arg1.getOrderNum()); 
		            	}else{
		            		return 0;
		            	}
		            }
		        });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return metaDataDefines;
	}

	/**
	 * 所有的字段元数据
	 * 
	 * @return
	 */
	public static MetadataDefinition getMetadateDefineByUri(String uri) {
		List<MetadataDefinition> metaDataDefineTemps = new ArrayList<MetadataDefinition>();
		CustomMetaData commonMetaData = MetadataDefineCacheMap
				.getValue("CommonMetaData");
		CustomMetaData baseMetaData = MetadataDefineCacheMap
				.getValue("BaseMetaData");
		if (commonMetaData != null) {
			metaDataDefineTemps.addAll(commonMetaData.getCustomPropertys());
		}
		if (baseMetaData != null) {
			metaDataDefineTemps.addAll(baseMetaData.getCustomPropertys());
		}
		for (MetadataDefinition metadataDefinition : metaDataDefineTemps) {
			if (uri.equals(metadataDefinition.getUri())) {
				return metadataDefinition;
			}
		}
		return null;
	}

	/**
	 * 增加普通元数据
	 * 
	 * @param metadataDefinition
	 * @return
	 */
	public static boolean addBaseMetadata(
			MetadataDefinition metadataDefinition, String groupName) {
		try {
			CustomMetaData baseMetaData = MetadataDefineCacheMap
					.getValue("BaseMetaData");
			boolean hasMetadata = false;
			if (baseMetaData == null) {
				baseMetaData = new CustomMetaData();
				baseMetaData.setName("BaseMetaData");
				baseMetaData.setNameCN("资源元数据");
				baseMetaData.setClassType("CF");
				hasMetadata = true;
				metadataDefinition.setOrderNum(1);
			} else {
				List<MetadataDefinition> metadataDefinitions = baseMetaData
						.getCustomPropertys();
				if (metadataDefinitions != null
						&& metadataDefinitions.size() > 0) {
					int i = 0;
					String groupId = metadataDefinition.getGroupId();
					for (MetadataDefinition metadataDefinitionTemp : metadataDefinitions) {
						if (groupId.equals(metadataDefinitionTemp.getGroupId())) {
							i++;
						}
					}
					metadataDefinition.setOrderNum(i + 1);
				}
			}
			baseMetaData.addCustomProperty(metadataDefinition);
			// 更新缓存
			if (hasMetadata) {
				MetadataDefineCacheMap.putKey("BaseMetaData", baseMetaData);
			}
			CustomMetaData commonMetaData = MetadataDefineCacheMap
					.getValue("CommonMetaData");
			CustomMetaData fileMetaData = MetadataDefineCacheMap
					.getValue("FileMetaData");
			List<CustomMetaData> customMetaDatas = new ArrayList<CustomMetaData>();
			if (commonMetaData != null) {
				customMetaDatas.add(commonMetaData);
			}
			if (baseMetaData != null) {
				customMetaDatas.add(baseMetaData);
			}
			if (fileMetaData != null) {
				customMetaDatas.add(fileMetaData);
			}
			logger.info("增加本地元数据");
			MetaService.saveBaseMetaSchemas(customMetaDatas);
			logger.info("增加本地元数据结束");
			metadataDefinition.setIsCom("2");
			Gson gson = new Gson();
			String paraJson = gson.toJson(metadataDefinition);
			HttpClientUtil http = new HttpClientUtil();
			String result = http.postJson(PUBLISH_METADATA_URL, paraJson);
			logger.info("同步到后台数据");
		} catch (Exception e) {
			logger.info(e.getStackTrace());
			return false;
		}
		return true;
	}

	/**
	 * 增加文件元数据
	 * 
	 * @param metadataDefinition
	 * @return
	 */
	public static boolean addFileMetadata(MetadataDefinition metadataDefinition) {
		try {
			CustomMetaData fileMetaData = MetadataDefineCacheMap
					.getValue("FileMetaData");
			boolean hasMetadata = false;
			if (fileMetaData == null) {
				fileMetaData = new CustomMetaData();
				fileMetaData.setName("FileMetaData");
				fileMetaData.setNameCN("文件元数据");
				fileMetaData.setClassType("FileCF");
				hasMetadata = true;
				metadataDefinition.setOrderNum(1);
			} else {
				List<MetadataDefinition> metadataDefinitions = fileMetaData
						.getCustomPropertys();
				if (metadataDefinitions != null
						&& metadataDefinitions.size() > 0) {
					int i = 0;
					String resType = metadataDefinition.getResType();
					for (MetadataDefinition metadataDefinitionTemp : metadataDefinitions) {
						if (resType.equals(metadataDefinitionTemp.getResType())) {
							i++;
						}
					}
					metadataDefinition.setOrderNum(i + 1);
				}
			}
			fileMetaData.addCustomProperty(metadataDefinition);
			if (hasMetadata) {
				MetadataDefineCacheMap.putKey("FileMetaData", fileMetaData);
			}
			CustomMetaData baseMetaData = MetadataDefineCacheMap
					.getValue("BaseMetaData");
			CustomMetaData commonMetaData = MetadataDefineCacheMap
					.getValue("CommonMetaData");
			List<CustomMetaData> customMetaDatas = new ArrayList<CustomMetaData>();
			if (commonMetaData != null) {
				customMetaDatas.add(commonMetaData);
			}
			if (baseMetaData != null) {
				customMetaDatas.add(baseMetaData);
			}
			if (fileMetaData != null) {
				customMetaDatas.add(fileMetaData);
			}
			logger.info("增加本地元数据");
			MetaService.saveBaseMetaSchemas(customMetaDatas);
			logger.info("增加本地元数据结束");
			metadataDefinition.setIsCom("3");
			Gson gson = new Gson();
			String paraJson = gson.toJson(metadataDefinition);
			HttpClientUtil http = new HttpClientUtil();
			String result = http.postJson(PUBLISH_METADATA_URL, paraJson);
			logger.info("同步到后台数据");
		} catch (Exception e) {
			logger.info(e.getStackTrace());
			return false;
		}
		return true;
	}

	/**
	 * 增加垮库元数据
	 * 
	 * @param metadataDefinition
	 * @return
	 */
	public static boolean addCommonMetadata(
			MetadataDefinition metadataDefinition) {
		try {
			CustomMetaData baseMetaData = MetadataDefineCacheMap
					.getValue("BaseMetaData");
			CustomMetaData fileMetaData = MetadataDefineCacheMap
					.getValue("FileMetaData");
			CustomMetaData commonMetaData = MetadataDefineCacheMap
					.getValue("CommonMetaData");
			boolean hasMetadata = false;
			if (commonMetaData == null) {
				commonMetaData = new CustomMetaData();
				commonMetaData.setName("CommonMetaData");
				commonMetaData.setNameCN("通用元数据");
				commonMetaData.setClassType("BaseCF");
				hasMetadata = true;
				metadataDefinition.setOrderNum(1);
			} else {
				List<MetadataDefinition> metadataDefinitions = commonMetaData
						.getCustomPropertys();
				if (metadataDefinitions != null
						&& metadataDefinitions.size() > 0) {
					int i = 0;
					String groupId = metadataDefinition.getGroupId();
					for (MetadataDefinition metadataDefinitionTemp : metadataDefinitions) {
						if (groupId.equals(metadataDefinitionTemp.getGroupId())) {
							i++;
						}
					}
					metadataDefinition.setOrderNum(i + 1);
				}
			}
			commonMetaData.addCustomProperty(metadataDefinition);
			if (hasMetadata) {
				MetadataDefineCacheMap.putKey("CommonMetaData", commonMetaData);
			}
			List<CustomMetaData> customMetaDatas = new ArrayList<CustomMetaData>();
			if (commonMetaData != null) {
				customMetaDatas.add(commonMetaData);
			}
			if (baseMetaData != null) {
				customMetaDatas.add(baseMetaData);
			}
			if (fileMetaData != null) {
				customMetaDatas.add(fileMetaData);
			}
			logger.info("增加本地元数据");
			MetaService.saveBaseMetaSchemas(customMetaDatas);
			logger.info("增加本地元数据结束");
			metadataDefinition.setIsCom("1");
			Gson gson = new Gson();
			String paraJson = gson.toJson(metadataDefinition);
			HttpClientUtil http = new HttpClientUtil();
			String result = http.postJson(PUBLISH_METADATA_URL, paraJson);
			logger.info("同步到后台数据");
		} catch (Exception e) {
			logger.info(e.getStackTrace());
			return false;
		}
		return true;
	}

	/**
	 * 删除元数据
	 * 
	 * @param metadataDefinition
	 *            isCom 1是核心 2是普通 3文件元数据
	 * @return
	 */
	public static boolean deleBaseMetadata(
			MetadataDefinition metadataDefinition, String isCom) {
		try {
			CustomMetaData baseMetaData = MetadataDefineCacheMap
					.getValue("BaseMetaData");
			if ("2".equals(isCom)) {
				List<MetadataDefinition> metadataDefinitions = baseMetaData
						.getCustomPropertys();
				for (int i = 0; i < metadataDefinitions.size(); i++) {
					MetadataDefinition tempMetadataDefinition = metadataDefinitions
							.get(i);
					if (metadataDefinition.getUri().equals(
							tempMetadataDefinition.getUri())) {
						metadataDefinitions.remove(i);
						break;
					}
				}
			}
			CustomMetaData commonMetaData = MetadataDefineCacheMap
					.getValue("CommonMetaData");
			if ("1".equals(isCom)) {
				List<MetadataDefinition> metadataDefinitions = commonMetaData
						.getCustomPropertys();
				for (int i = 0; i < metadataDefinitions.size(); i++) {
					MetadataDefinition tempMetadataDefinition = metadataDefinitions
							.get(i);
					if (metadataDefinition.getUri().equals(
							tempMetadataDefinition.getUri())) {
						metadataDefinitions.remove(i);
						break;
					}
				}
			}
			CustomMetaData fileMetaData = MetadataDefineCacheMap
					.getValue("FileMetaData");
			if ("3".equals(isCom)) {
				List<MetadataDefinition> metadataDefinitions = fileMetaData
						.getCustomPropertys();
				for (int i = 0; i < metadataDefinitions.size(); i++) {
					MetadataDefinition tempMetadataDefinition = metadataDefinitions
							.get(i);
					if (metadataDefinition.getUri().equals(
							tempMetadataDefinition.getUri())) {
						metadataDefinitions.remove(i);
						break;
					}
				}
			}
			List<CustomMetaData> customMetaDatas = new ArrayList<CustomMetaData>();
			if (commonMetaData != null) {
				customMetaDatas.add(commonMetaData);
			}
			if (baseMetaData != null) {
				customMetaDatas.add(baseMetaData);
			}
			if (fileMetaData != null) {
				customMetaDatas.add(fileMetaData);
			}
			MetaService.saveBaseMetaSchemas(customMetaDatas);
			logger.info("删除元数据结束");
			metadataDefinition.setIsCom(isCom);
			Gson gson = new Gson();
			String paraJson = gson.toJson(metadataDefinition);
			HttpClientUtil http = new HttpClientUtil();
			String result = http.postJson(PUBLISH_DELMETADATA_URL, paraJson);
			logger.info("同步到后台数据");
		} catch (Exception e) {
			e.getMessage();
			return false;
		}
		return true;
	}

	/**
	 * 删除文件元数据（按照资源类型）
	 * 
	 * @param metadataDefinition
	 *            isCom 1是核心 2是普通 3文件元数据
	 * @return
	 */
	public static boolean deleFileMetadataByRestype(String resType) {
		try {
			CustomMetaData baseMetaData = MetadataDefineCacheMap
					.getValue("BaseMetaData");
			CustomMetaData commonMetaData = MetadataDefineCacheMap
					.getValue("CommonMetaData");
			CustomMetaData fileMetaData = MetadataDefineCacheMap
					.getValue("FileMetaData");
			List<MetadataDefinition> metadataDefinitions = fileMetaData
					.getCustomPropertys();
			for (int i = 0; i < metadataDefinitions.size(); i++) {
				MetadataDefinition tempMetadataDefinition = metadataDefinitions
						.get(i);
				if (tempMetadataDefinition.getResType().equals(resType)) {
					metadataDefinitions.remove(i);
					break;
				}
			}
			List<CustomMetaData> customMetaDatas = new ArrayList<CustomMetaData>();
			if (commonMetaData != null) {
				customMetaDatas.add(commonMetaData);
			}
			if (baseMetaData != null) {
				customMetaDatas.add(baseMetaData);
			}
			if (fileMetaData != null) {
				customMetaDatas.add(fileMetaData);
			}
			MetaService.saveBaseMetaSchemas(customMetaDatas);
			logger.info("删除元数据结束");
			HttpClientUtil http = new HttpClientUtil();
			String result = http.executeGet(PUBLISH_DELFILEMETADATA_URL
					+ "?resType=" + resType);
			if ("0".equals(result)) {
				logger.info("同步到后台数据");
			}
		} catch (Exception e) {
			e.getMessage();
			return false;
		}
		return true;
	}

	/**
	 * 删除元数据(根据组ID，多个组用英文逗号隔开)
	 * 
	 * @param metadataDefinition
	 *            isCom 1是核心 2是普通 3文件元数据
	 * @return
	 */
	public static boolean deleMetadataByGroup(String groupIds) {
		try {
			CustomMetaData baseMetaData = MetadataDefineCacheMap
					.getValue("BaseMetaData");
			List<MetadataDefinition> metadataDefinitions = baseMetaData
					.getCustomPropertys();
			List<String> groupList = new ArrayList<String>();
			if (groupIds.indexOf(",") > 0) {
				String[] groupIdArray = groupIds.split(",");
				groupList = Arrays.asList(groupIdArray);
			} else {
				groupList.add(groupIds);
			}
			for (int i = 0; i < metadataDefinitions.size(); i++) {
				MetadataDefinition tempMetadataDefinition = metadataDefinitions
						.get(i);
				String groupId = tempMetadataDefinition.getGroupId();
				if (groupList.contains(groupId)) {
					metadataDefinitions.remove(i);
					break;
				}
			}
			CustomMetaData commonMetaData = MetadataDefineCacheMap
					.getValue("CommonMetaData");
			CustomMetaData fileMetaData = MetadataDefineCacheMap
					.getValue("FileMetaData");
			List<CustomMetaData> customMetaDatas = new ArrayList<CustomMetaData>();
			if (commonMetaData != null) {
				customMetaDatas.add(commonMetaData);
			}
			if (baseMetaData != null) {
				customMetaDatas.add(baseMetaData);
			}
			if (fileMetaData != null) {
				customMetaDatas.add(fileMetaData);
			}
			MetaService.saveBaseMetaSchemas(customMetaDatas);
			logger.info("删除元数据结束");
			Gson gson = new Gson();
			HttpClientUtil http = new HttpClientUtil();
			String result = http.executeGet(PUBLISH_DELMETADATABYGROUP_URL
					+ "?groupIds=" + groupIds);
			logger.info("同步到后台数据");
		} catch (Exception e) {
			e.getMessage();
			return false;
		}
		return true;
	}

	/**
	 * 修改元数据
	 * 
	 * @param metadataDefinition
	 *            isCom 1是核心 2是普通
	 * @return
	 */
	public static boolean updBaseMetadata(
			MetadataDefinition metadataDefinition, String isCom) {
		try {
			CustomMetaData baseMetaData = MetadataDefineCacheMap
					.getValue("BaseMetaData");
			if ("2".equals(isCom)) {
				List<MetadataDefinition> metadataDefinitions = baseMetaData
						.getCustomPropertys();
				for (int i = 0; i < metadataDefinitions.size(); i++) {
					MetadataDefinition tempMetadataDefinition = metadataDefinitions
							.get(i);
					if (metadataDefinition.getUri().equals(
							tempMetadataDefinition.getUri())) {
						metadataDefinitions.set(i, metadataDefinition);
						break;
					}
				}
			}
			CustomMetaData commonMetaData = MetadataDefineCacheMap
					.getValue("CommonMetaData");
			if ("1".equals(isCom)) {
				List<MetadataDefinition> metadataDefinitions = commonMetaData
						.getCustomPropertys();
				for (int i = 0; i < metadataDefinitions.size(); i++) {
					MetadataDefinition tempMetadataDefinition = metadataDefinitions
							.get(i);
					metadataDefinition.setGroupId("1");
					if (metadataDefinition.getUri().equals(
							tempMetadataDefinition.getUri())) {
						metadataDefinitions.set(i, metadataDefinition);
						break;
					}
				}
			}
			CustomMetaData fileMetaData = MetadataDefineCacheMap
					.getValue("FileMetaData");
			if ("3".equals(isCom)) {
				List<MetadataDefinition> metadataDefinitions = fileMetaData
						.getCustomPropertys();
				for (int i = 0; i < metadataDefinitions.size(); i++) {
					MetadataDefinition tempMetadataDefinition = metadataDefinitions
							.get(i);
					if (metadataDefinition.getUri().equals(
							tempMetadataDefinition.getUri())) {
						metadataDefinitions.set(i, metadataDefinition);
						break;
					}
				}
			}
			List<CustomMetaData> customMetaDatas = new ArrayList<CustomMetaData>();
			if (commonMetaData != null) {
				customMetaDatas.add(commonMetaData);
			}
			if (baseMetaData != null) {
				customMetaDatas.add(baseMetaData);
			}
			// 更新缓存(基本元数据和通用元数据)
			// if(hasMetadata){
			MetadataDefineCacheMap.putKey("BaseMetaData", baseMetaData);
			MetadataDefineCacheMap.putKey("CommonMetaData", commonMetaData);

			// 修改完元数据项后更新session中授权的元数据的属性（是否用于查询，等）
			// fengda 2015年11月17日
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			List<MetadataDefinition> metadaResult = new ArrayList<MetadataDefinition>();
			List<MetadataDefinition> metadaList = userInfo.getMetadataList(); // 获取缓存中原有的元数据的信息
			for (int i = 0; i < metadaList.size(); i++) {
				// 将用户原有的元数据的字段转换成MetadataDefinition实体更新到用户的缓存中
				for (int j = 0; j < customMetaDatas.size(); j++) {
					int length = customMetaDatas.get(j).getCustomPropertys()
							.size();
					for (int k = 0; k < length; k++) {
						if (metadaList
								.get(i)
								.getFieldName()
								.equals(customMetaDatas.get(j)
										.getCustomPropertys().get(k)
										.getFieldName())) {
							metadaResult.add(customMetaDatas.get(j)
									.getCustomPropertys().get(k));
						}
					}
				}
			}
			userInfo.setMetadataList(metadaResult);

			// }
			if (fileMetaData != null) {
				customMetaDatas.add(fileMetaData);
			}
			MetaService.saveBaseMetaSchemas(customMetaDatas);
			logger.info("更新元数据结束");
			metadataDefinition.setIsCom(isCom);
			Gson gson = new Gson();
			String paraJson = gson.toJson(metadataDefinition);
			HttpClientUtil http = new HttpClientUtil();
			String result = http.postJson(PUBLISH_UPDMETADATA_URL, paraJson);
			logger.info("同步到后台数据");
		} catch (Exception e) {
			e.getMessage();
			return false;
		}
		return true;
	}

	/**
	 * 修改元数据(多个)
	 * 
	 * @param metadataDefinition
	 *            isCom 1是核心 2是普通
	 * @return
	 */
	public static boolean updBaseMetadatas(
			Map<String, MetadataDefinition> metadataDefinitionMap, String isCom) {
		try {
			CustomMetaData baseMetaData = MetadataDefineCacheMap
					.getValue("BaseMetaData");
			if ("2".equals(isCom)) {
				List<MetadataDefinition> metadataDefinitions = baseMetaData
						.getCustomPropertys();
				for (int i = 0; i < metadataDefinitions.size(); i++) {
					MetadataDefinition tempMetadataDefinition = metadataDefinitions
							.get(i);
					String oldUri = tempMetadataDefinition.getUri();
					if (metadataDefinitionMap != null
							&& metadataDefinitionMap.size() > 0) {
						MetadataDefinition metadataDefinition = metadataDefinitionMap
								.get(oldUri);
						if (metadataDefinition != null) {
							metadataDefinitions.set(i, metadataDefinition);
						}
					}
				}
			}
			CustomMetaData commonMetaData = MetadataDefineCacheMap
					.getValue("CommonMetaData");
			if ("1".equals(isCom)) {
				List<MetadataDefinition> metadataDefinitions = commonMetaData
						.getCustomPropertys();
				for (int i = 0; i < metadataDefinitions.size(); i++) {
					MetadataDefinition tempMetadataDefinition = metadataDefinitions
							.get(i);
					String oldUri = tempMetadataDefinition.getUri();
					if (metadataDefinitionMap != null
							&& metadataDefinitionMap.size() > 0) {
						MetadataDefinition metadataDefinition = metadataDefinitionMap
								.get(oldUri);
						metadataDefinition.setGroupId("1");
						if (metadataDefinition != null) {
							metadataDefinitions.set(i, metadataDefinition);
						}
					}
				}
			}
			CustomMetaData fileMetaData = MetadataDefineCacheMap
					.getValue("FileMetaData");
			if ("3".equals(isCom)) {
				List<MetadataDefinition> metadataDefinitions = fileMetaData
						.getCustomPropertys();
				for (int i = 0; i < metadataDefinitions.size(); i++) {
					MetadataDefinition tempMetadataDefinition = metadataDefinitions
							.get(i);
					String oldUri = tempMetadataDefinition.getUri();
					if (metadataDefinitionMap != null
							&& metadataDefinitionMap.size() > 0) {
						MetadataDefinition metadataDefinition = metadataDefinitionMap
								.get(oldUri);
						// metadataDefinition.setGroupId("2");
						if (metadataDefinition != null) {
							metadataDefinitions.set(i, metadataDefinition);
						}
					}
				}
			}
			List<CustomMetaData> customMetaDatas = new ArrayList<CustomMetaData>();
			if (commonMetaData != null) {
				customMetaDatas.add(commonMetaData);
			}
			if (baseMetaData != null) {
				customMetaDatas.add(baseMetaData);
			}
			if (fileMetaData != null) {
				customMetaDatas.add(fileMetaData);
			}
			MetaService.saveBaseMetaSchemas(customMetaDatas);
			logger.info("更新元数据结束");
		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 根据英文名称获取定义的元数据
	 */
	public static MetadataDefinition getMetadataDefinitionByName(String name) {
		MetadataDefinition metadataDefinition = null;
		Map<String, MetadataDefinition> map = getAllMetadataDefinition();
		for (Map.Entry<String, MetadataDefinition> entry : map.entrySet()) {
			if (name.equals(entry.getKey())) {
				metadataDefinition = entry.getValue();
				break;
			}
		}
		return metadataDefinition;
	}

	/**
	 * 获得标签字段
	 */
	public static String getTargetFieldName() {
		String targetField = "";
		List<MetadataDefinition> metadataDefinitions = MetadataSupport
				.getAllMetadateDefineList();
		if (metadataDefinitions != null && metadataDefinitions.size() > 0) {
			for (MetadataDefinition metadataDefinition : metadataDefinitions) {
				if (metadataDefinition.getIdentifier() != null
						&& metadataDefinition.getIdentifier() == 10) {
					targetField = metadataDefinition.getFieldName();
				}
			}
		}
		return targetField;
	}

	/**
	 * 获得资源名称字段
	 */
	public static String getTitleFieldName() {
		String titleField = "";
		CustomMetaData commonMetaData = MetadataDefineCacheMap
				.getValue("CommonMetaData");
		if (commonMetaData != null) {
			List<MetadataDefinition> metadataDefinitions = commonMetaData
					.getCustomPropertys();
			if (metadataDefinitions != null && metadataDefinitions.size() > 0) {
				for (int i = 0; i < metadataDefinitions.size(); i++) {
					MetadataDefinition tempMetadataDefinition = metadataDefinitions
							.get(i);
					if (tempMetadataDefinition.getIdentifier() != null
							&& tempMetadataDefinition.getIdentifier() == 3) {
						titleField = tempMetadataDefinition.getFieldName();
					}
				}
			}
		}
		return titleField;
	}

	/**
	 * 获得资源名称的内容
	 */
	public static String getTitle(Ca ca) {
		String title = "";
		if (ca != null) {
			Map metadataMap = ca.getMetadataMap();
			if (metadataMap != null && metadataMap.size() > 0) {
				title = (String) metadataMap.get(getTitleFieldName());
			}
		}
		return title;
	}

	/**
	 * 获得资源名称的内容
	 */
	public static String getTitleByOrder(String id) {
		String title = "";
		IFLTXService fLTXService;
		try {
			fLTXService = (IFLTXService) BeanFactoryUtil.getBean("FLTXService");
			if (StringUtils.isNotBlank(id)) {
				String hql = "from ResOrder where orderId="
						+ Long.parseLong(id);
				List<ResOrder> order = (List<ResOrder>) fLTXService.query(hql);
				if (order != null && order.size() > 0) {
					if (StringUtils.isNotBlank(order.get(0).getChannelName()))
						title = order.get(0).getChannelName();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return title;
	}

	/**
	 * 获得资源名称的内容
	 */
	public static String getTitleBySubject(String id) {
		String title = "";
		IFLTXService fLTXService;
		try {
			fLTXService = (IFLTXService) BeanFactoryUtil.getBean("FLTXService");
			if (StringUtils.isNotBlank(id)) {
				String hql = "from SubjectStore where id=" + Long.parseLong(id);
				List<SubjectStore> subjectStore = (List<SubjectStore>) fLTXService
						.query(hql);
				if (subjectStore != null && subjectStore.size() > 0) {
					if (StringUtils.isNotBlank(subjectStore.get(0).getName()))
						title = subjectStore.get(0).getName();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return title;
	}

	/**
	 * 为资源的分类赋值分类名字
	 * 
	 * @param bookCa
	 * @return
	 */
	public static Ca setCategoryByPath(Ca bookCa) {
		Map<String, String> metadataMap = bookCa.getMetadataMap();
		Map<String, MetadataDefinition> metadataDefinitionMap = getAllMetadataDefinition();
		boolean hasCategoryName = false;
		for (Map.Entry<String, String> entry : metadataMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			MetadataDefinition metadataDefinition = metadataDefinitionMap
					.get(key);
			if (metadataDefinition != null
					&& metadataDefinition.getFieldType() == 6) {
				String categoryName = getFLTXService().queryCatagoryCnName(
						value);
				metadataMap.put(key, categoryName);
				hasCategoryName = true;
			}
		}
		if (hasCategoryName) {
			bookCa.setMetadataMap(metadataMap);
		}
		return bookCa;
	}

	/**
	 * 根据资源类型获得资源分类名称
	 */
	public static String getTitleByPublishType(String publishType) {
		String title = "";
		try {
			Map<Object, Object> resTypeMap = SysResTypeCacheMap.getMapValue();
			Iterator it = resTypeMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				if (pairs.getKey() != null
						&& pairs.getKey().equals(publishType)) {
					title = pairs.getValue().toString();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return title;
	}

	public static void main(String[] args) {
		// java.text.SimpleDateFormat sdf = new
		// java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// System.out.println(sdf.format(java.util.Calendar.getInstance().getTime()));
		// System.out.println(sdf.format(new java.util.Date()));
	}
}
