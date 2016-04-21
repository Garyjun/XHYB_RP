package com.brainsoon.common.util;

import java.util.List;

import org.apache.log4j.Logger;

import com.brainsoon.common.dao.IBaseJdbcDao;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.SysDoi;



/**
 * 
 * @ClassName: SysMetaDataUtils 
 * @Description: 元数据工具类
 * @author tanghui 
 * @date 2013-5-2 下午4:07:35 
 *
 */
public class SysMetaDataUtils {
	
	public static final String CBBOOK = "cbbook";
	public static final String JOURNAl = "journal";
	public static final String ARTICLE = "article";
	public static final String SOUND = "sound";
	public static final String AUB = "AUB";
	public static final int NUMSPREGROUP = 1000;

	public static final Logger logger = Logger.getLogger(SysMetaDataUtils.class);
	
	
	private static IBaseService baseService = null;
	private static IBaseJdbcDao baseJdbcDao = null;
//	(IBaseJdbcDao)BeanFactoryUtil.getBean("baseJdbcDao");
	/**
	 * 根据中图分类号ID查询code
	 * @param fieldName
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	public static ResCategory queryCategoryById(String id){
//		ResCategory resCategory  =  (ResCategory) baseQueryService.getByPk(ResCategory.class, Long.parseLong(id));
//		if(resCategory!=null){
//			return resCategory;
//		}else{
//			return null;
//		}
//	}
	/**
	 * 生成民国书号的序列号
	 * @param fieldEnName
	 * @return
	 */
//	public static String createMGSerialNumber(long sysMetadataTypeId,SysResDirectory resDirectory){
//		String mgField = SysMetaDataUtils.queryMetadataFieldByFieldEnName(CommonConstants.MG, sysMetadataTypeId);
//		String fieldValue ="";
//		try {
//			fieldValue = BeanUtils.getProperty(resDirectory, mgField);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		String hql = "select count(id) from SysResDirectory where "+mgField+"= '"+fieldValue +"'and sysMetadataType.sysMetadataTypeId=" + sysMetadataTypeId;
//		List mgSerialNumbers = baseQueryService.query(hql);
//		if(mgSerialNumbers !=null &&  mgSerialNumbers.size()>0){
//			String mgSerialNumber = "";
//			Integer number = Integer.parseInt(mgSerialNumbers.get(0)+"");
//			String serialNumberTemp=  (number+1)+"";
//			for(int i=0;i<4-serialNumberTemp.length();i++){
//				mgSerialNumber += "0";
//			}
//			mgSerialNumber = mgSerialNumber+serialNumberTemp;
//			return mgSerialNumber;
//		}else{
//			return "0001";
//		}
//	}
	/**
	 * 生成流水号
	 * @param fieldEnName
	 * @return
	 */
	public static String createSerialNumber(String publishType){
		List serialNumbers = null;
		try{
			baseService = (IBaseService) BeanFactoryUtil.getBean("baseService");
			String hql = "select oresNums from RunNumber where publishType='"+publishType+"'";
			serialNumbers = baseService.query(hql);
		}catch (Exception e) {
			logger.info("生成流水号"+e.getMessage());
			e.printStackTrace();
		}
		if(serialNumbers !=null &&  serialNumbers.size()>0){
			String serialNumber = "";
			Integer number = Integer.parseInt(serialNumbers.get(0)+"");
			String serialNumberTemp=  (number+1)+"";
			for(int i=0;i<8-serialNumberTemp.length();i++){
				serialNumber += "0";
			}
			serialNumber = serialNumber+serialNumberTemp;
			logger.info("------------++++++++++++++==----------"+serialNumber+"-------------++++++++++++++++++-------------");
			return serialNumber;
		}else{
			return "00000001";
		}
	}
	/**
	 * 查询DOI定义对象
	 * @param fieldEnName
	 * @return
	 */
	public static SysDoi querySysDoiDefinition(long publishType){
		List<SysDoi> sysDoi = null;
		try{
			baseService = (IBaseService) BeanFactoryUtil.getBean("baseService");
			String hql = "from SysDoi where publishType="+publishType;
				sysDoi = baseService.query(hql);
		}catch (Exception e) {
			logger.info("查询Doi数据"+e.getMessage());
			e.printStackTrace();
		}
		if(sysDoi !=null &&  sysDoi.size()>0){
			return (SysDoi)sysDoi.get(0);
		}else{
			return null;
		}
	}
	/**
	 * 给资源文件分组（文件太多为了查找快而分组）
	 * @return
	 */
//	public static String getOresDirGroup(){
//		String hql = "from OresCount";
//		List<OresCount> oresCounts = baseQueryService.query(hql);
//		if(oresCounts !=null && oresCounts.size()>0){
//			OresCount oresCount = oresCounts.get(0);
//			int oresNums = oresCount.getOresNums();
//			int n = oresNums/NUMSPREGROUP;
//			String groups= n+"";
//			if(groups.length() ==1){
//				return "000"+n;
//			}else if(groups.length() ==2){
//				return "00"+n;
//			}else if(groups.length() ==3){
//				return "0"+n;
//			}else{
//				return n+"";
//			}
//		}else{
//			return "0000";
//		}
//	}
//	 /**
//     * 检测ZIP中的目录是否符合定义规范
//     * @return
//     */
//    public static boolean checkDir(File[] files,long sysMetadataTypeId){
//    	boolean checkResult = true;
//    	List<SysDir> sysDirs = queryAllDir(sysMetadataTypeId);
//    	List<String>  dirs = new ArrayList<String>();
//    	for(SysDir sysDir:sysDirs){
//			dirs.add(sysDir.getDirEnName());
//		}
//    	dirs.add("other");
//    	for(File file:files){
//    		if(file.isDirectory()){
//    			String dirName = file.getName();
//    			if(!dirs.contains(dirName)){
//    				checkResult = false;
//    				break;
//    			}
//    		}
//    	}
//    	return checkResult;
//    }
//	/**
//	 * 得到资源类型下的所有的资源目录
//	 * @param sysMetadataTypeId
//	 */
//	public static List<SysDir> queryAllDir(long sysMetadataTypeId){
//		String hql = "from SysDir where sysMetadataType.sysMetadataTypeId = "+ sysMetadataTypeId;
//		List<SysDir> sysDirs = baseQueryService.query(hql);
//		return sysDirs;
//	}
//	/**
//	 * 得到资源类型下的所有的资源目录 ,以Map的形式
//	 * 
//	 * @param sysMetadataTypeId
//	 * @return map
//	 */
//	public static LinkedHashMap<Object, String> queryAllDirMap(
//			String sysMetadataTypeId) {
//		LinkedHashMap<Object, String> map = null;
//		if (StringUtils.isBlank(sysMetadataTypeId)) {
//			sysMetadataTypeId = WebappConfigUtil.getParameter("cbbook");
//		}
//		String hql = "from SysDir where sysMetadataType.sysMetadataTypeId = "
//				+ sysMetadataTypeId;
//		List<SysDir> sysDirs = baseQueryService.query(hql);
//		if (sysDirs != null && sysDirs.size() > 0) {
//			if (map == null) {
//				map = new LinkedHashMap<Object, String>();
//			}
//			for (SysDir dir : sysDirs) {
//				map.put(dir.getDirEnName(), dir.getDirCnName());
//			}
//		}
//		return map;
//	}
//	
//	/*
//	 * 查询元数据定义的所有的DOI元数据和管理元数据的总数不包括元数据组供生成excel模版时使用
//	 */
//	public static int  queryNum(long sysMetadataTypeId){
//		int num = 0;
//		String queryHql = "select count(*) from SysMetadataDefinition where";
//		queryHql +=" (isGroup  is null or isGroup = '' ) and  sysMetadataTypeId = "+ sysMetadataTypeId;
//	    @SuppressWarnings("unchecked")
//		List sysMetadataDefinitionNum =  baseQueryService.query(queryHql);
//	    if(sysMetadataDefinitionNum!=null && sysMetadataDefinitionNum.size()>0){
//	    	num = Integer.parseInt(sysMetadataDefinitionNum.get(0).toString());
//	    }
//		return num;
//	}
//	
//	/*
//	 * 查询元数据定义的所有的DOI元数据和管理元数据的总数不包括元数据组供生成excel模版时使用
//	 */
//	public static int  queryNumByParentId(long sysMetadataTypeId){
//		Set<SysMetadataDefinition> sysMetadataDefinition = queryAllDefinitions(-3l,sysMetadataTypeId);
//		return sysMetadataDefinition.size();
//	}
//	/*
//	 * 查询元数据定义的所有定义不包括组（-3DOi元数据  -4资源元数据 -7 文件元数据）
//	 */
//	public static List<SysMetadataDefinition>  queryAllListDefinitions(long metadataGroup,long sysMetadataTypeId){
//		List<SysMetadataDefinition> sysMetadataDefinitions = new ArrayList<SysMetadataDefinition>();
//		 List<SysMetadataDefinition> definitionGroups = SysMetaDataUtils.querySysMetadataDefinitionGroups(metadataGroup, sysMetadataTypeId);
//		 if(definitionGroups != null){
//             	for(SysMetadataDefinition sysMetadataDefinitionGroup:definitionGroups){
//             		List<SysMetadataDefinition> definitions = querySysMetadataDefinitionsByParentId(sysMetadataDefinitionGroup.getDefinitionId(), sysMetadataTypeId);
//             		if(definitions !=null){
//             			sysMetadataDefinitions.addAll(definitions);
//             		}
//             	}
//		 }
//		 List<SysMetadataDefinition> definitionWithoutGroups = querySysMetadataDefinitionsByParentId(metadataGroup, sysMetadataTypeId);
//		 if(definitionWithoutGroups != null){
//			 sysMetadataDefinitions.addAll(definitionWithoutGroups);
//		 }
//		 return sysMetadataDefinitions;
//	}
//	/*
//	 * 查询元数据定义的所有定义不包括组（-3DOi元数据  -4资源元数据 -7 文件元数据）
//	 */
//	public static Set<SysMetadataDefinition>  queryAllDefinitions(long metadataGroup,long sysMetadataTypeId){
//		Set<SysMetadataDefinition> sysMetadataDefinitions = new LinkedHashSet<SysMetadataDefinition>();
//		 List<SysMetadataDefinition> definitionGroups = SysMetaDataUtils.querySysMetadataDefinitionGroups(metadataGroup, sysMetadataTypeId);
//		 if(definitionGroups != null){
//             	for(SysMetadataDefinition sysMetadataDefinitionGroup:definitionGroups){
//             		List<SysMetadataDefinition> definitions = querySysMetadataDefinitionsByParentId(sysMetadataDefinitionGroup.getDefinitionId(), sysMetadataTypeId);
//             		if(definitions !=null){
//             			sysMetadataDefinitions.addAll(definitions);
//             		}
//             	}
//		 }
//		 List<SysMetadataDefinition> definitionWithoutGroups = querySysMetadataDefinitionsByParentId(metadataGroup, sysMetadataTypeId);
//		 if(definitionWithoutGroups != null){
//			 sysMetadataDefinitions.addAll(definitionWithoutGroups);
//		 }
//		 return sysMetadataDefinitions;
//	}
//	/*
//	 * 查询元数据定义的所有组（-3DOi元数据  -4资源元数据 -7 文件元数据）
//	 */
//	public static List<SysMetadataDefinition> querySysMetadataDefinitionsByParentId(long metadataDefinitionParentId,long sysMetadataTypeId){
//		String queryHql = "from SysMetadataDefinition where";
//		queryHql += " parentSysMetadataDefinition.definitionId="+metadataDefinitionParentId ;
//		queryHql +=" and (isGroup  is null or isGroup = '' ) and  sysMetadataTypeId = "+ sysMetadataTypeId;
//	    @SuppressWarnings("unchecked")
//		List<SysMetadataDefinition> sysMetadataDefinitions = (List<SysMetadataDefinition>) baseQueryService.query(queryHql);
//	    if(sysMetadataDefinitions!=null && sysMetadataDefinitions.size()>0){
//	    	return sysMetadataDefinitions;
//	    }
//	    return null;
//	}
//	
//	/*
//	 * 查询元数据定义的所有组（-3DOi元数据  -4资源元数据 -7 文件元数据）
//	 */
//	public static List<SysMetadataDefinition> querySysMetadataDefinitionGroups(long metadataDefinitionType,long sysMetadataTypeId){
//		String queryHql = "from SysMetadataDefinition where";
//		queryHql += " parentSysMetadataDefinition.definitionId="+metadataDefinitionType ;
//		queryHql +=" and isGroup ='1' and  sysMetadataTypeId = "+ sysMetadataTypeId;
//	    @SuppressWarnings("unchecked")
//		List<SysMetadataDefinition> sysMetadataDefinitions = (List<SysMetadataDefinition>) baseQueryService.query(queryHql);
//	    if(sysMetadataDefinitions!=null && sysMetadataDefinitions.size()>0){
//	    	return sysMetadataDefinitions;
//	    }
//	    return null;
//	}
//	/**
//	 * 查询元数据类型
//	 * @param fieldEnName
//	 * @return
//	 */
//	public static SysMetadataType querySysMetadateType(long sysMetadataTypeId){
//		SysMetadataType sysMetadataType = (SysMetadataType)baseQueryService.getByPk(SysMetadataType.class, sysMetadataTypeId);
//    	return sysMetadataType;
//	}
//	/**
//	 * 查询共有字段filed
//	 * @param fieldEnName
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static List queryPublicField(long sysMetadataTypeId){
//		SysMetadataType sysMetadataType = (SysMetadataType)baseQueryService.getByPk(SysMetadataType.class, sysMetadataTypeId);
//		Set<SysMetadataDefinition> metadataSysMetadataDefinitions = sysMetadataType.getSysWorkMetadataGeneral();
//		List<String> publicFieldArray = new ArrayList<String>();
//    	for (Iterator iterator = metadataSysMetadataDefinitions.iterator(); iterator
//				.hasNext();) {
//    		SysMetadataDefinition metadateSysMetadataDefinition = (SysMetadataDefinition) iterator.next();
//    		if(metadateSysMetadataDefinition.isPublicType()){
//    			publicFieldArray.add(metadateSysMetadataDefinition.getField());
//    		}
//    	}
//    	return publicFieldArray;
//	}
//	/**
//	 * 查询共有字段filed
//	 * @param fieldEnName
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static List queryPrivateField(long sysMetadataTypeId){
//		SysMetadataType sysMetadataType = (SysMetadataType)baseQueryService.getByPk(SysMetadataType.class, sysMetadataTypeId);
//		Set<SysMetadataDefinition> metadataSysMetadataDefinitions = sysMetadataType.getSysWorkMetadataGeneral();
//		List<String> privateFieldArray = new ArrayList<String>();
//    	for (Iterator iterator = metadataSysMetadataDefinitions.iterator(); iterator
//				.hasNext();) {
//    		SysMetadataDefinition metadateSysMetadataDefinition = (SysMetadataDefinition) iterator.next();
//    		if(!metadateSysMetadataDefinition.isPublicType()){
//    			privateFieldArray.add(metadateSysMetadataDefinition.getField());
//    		}
//    	}
//    	return privateFieldArray;
//	}
//	/**
//	 * 根据英文名称查找资源元数据定义字段filed
//	 * @param fieldEnName
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static SysResDirectory querySysResDirectoryByDOI(String doiValue,long sysMetadataTypeId){
//		String doiFiled = queryMetadataFieldByFieldName(CommonConstants.DOI_CN,sysMetadataTypeId);
//		String hql = "from SysResDirectory where "+ doiFiled+"= '" + doiValue + "' and sysMetadataTypeId=" + sysMetadataTypeId;
//		List<SysResDirectory> result = baseQueryService.query(hql);
//		if(result.size()>0){
//			SysResDirectory sysResDirectory = (SysResDirectory) result.get(0);
//			return sysResDirectory;
//		}else{
//			return null;
//		}
//	}
//	/**
//	 * 根据英文名称查找资源元数据定义字段filed
//	 * @param fieldEnName
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static String queryMetadataFieldByFieldEnName(String fieldEnName,long sysMetadataTypeId){
//		String hql = "from SysMetadataDefinition where fieldEnName= '" + fieldEnName + "' and sysMetadataTypeId=" + sysMetadataTypeId;
//		List<SysMetadataDefinition> result = baseQueryService.query(hql);
//		if(result.size()>0){
//			SysMetadataDefinition sysMetadataDefinition = (SysMetadataDefinition) result.get(0);
//			return sysMetadataDefinition.getField();
//		}else{
//			return "";
//		}
//	}
//	
//	/**
//	 * 根据英文名称查找资源元数据定义字段filed的默认值
//	 * @param fieldEnName
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static String queryMetadataDefauleValueByFieldEnName(String fieldEnName,long sysMetadataTypeId){
//		String hql = "from SysMetadataDefinition where fieldEnName= '" + fieldEnName + "' and sysMetadataTypeId=" + sysMetadataTypeId;
//		List<SysMetadataDefinition> result = baseQueryService.query(hql);
//		if(result.size()>0){
//			SysMetadataDefinition sysMetadataDefinition = (SysMetadataDefinition) result.get(0);
//			return sysMetadataDefinition.getDefaultValue();
//		}else{
//			return "";
//		}
//	}
//	/**
//	 * 根据英文名称查找资源元数据定义字段中文名称
//	 * @param fieldName
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static String queryMetadataNameByFieldEnName(String fieldEnName,long sysMetadataTypeId){
//		String hql = "from SysMetadataDefinition where fieldEnName= '" + fieldEnName + "' and sysMetadataTypeId=" + sysMetadataTypeId;
//		List<SysMetadataDefinition> result = baseQueryService.query(hql);
//		if(result.size()>0){
//			SysMetadataDefinition sysMetadataDefinition = (SysMetadataDefinition) result.get(0);
//			return sysMetadataDefinition.getFieldName();
//		}else{
//			return "";
//		}
//	}
//	/**
//	 * 根据英文名称查找资源元数据定义字段中文名称
//	 * @param fieldName
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static SysMetadataDefinition queryMetadataByFieldEnName(String fieldEnName,long sysMetadataTypeId){
//		String hql = "from SysMetadataDefinition where fieldEnName= '" + fieldEnName + "' and sysMetadataTypeId=" + sysMetadataTypeId;
//		List<SysMetadataDefinition> result = baseQueryService.query(hql);
//		if(result.size()>0){
//			SysMetadataDefinition sysMetadataDefinition = (SysMetadataDefinition) result.get(0);
//			return sysMetadataDefinition;
//		}else{
//			return null;
//		}
//	}
//	
//	/**
//	 * 根据英文名称查找资源元数据定义字段中文名称
//	 * @param fieldName
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static SysMetadataDefinition queryMetadataByField(String field,long sysMetadataTypeId){
//		String hql = "from SysMetadataDefinition where field= '" + field + "' and sysMetadataTypeId=" + sysMetadataTypeId;
//		List<SysMetadataDefinition> result = baseQueryService.query(hql);
//		if(result.size()>0){
//			SysMetadataDefinition sysMetadataDefinition = (SysMetadataDefinition) result.get(0);
//			return sysMetadataDefinition;
//		}else{
//			return null;
//		}
//	}
//	/**
//	 * 根据中文名称查找资源元数据定义字段filed
//	 * @param fieldName
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static String queryMetadataFieldByFieldName(String fieldName,long sysMetadataTypeId){
//		String hql = "from SysMetadataDefinition where fieldName='" + fieldName + "' and sysMetadataTypeId=" + sysMetadataTypeId;
//		List<SysMetadataDefinition> result = baseQueryService.query(hql);
//		if(result.size()>0){
//			SysMetadataDefinition sysMetadataDefinition = (SysMetadataDefinition) result.get(0);
//			return sysMetadataDefinition.getField();
//		}else{
//			return "";
//		}
//	}
//	
//	
//	/**
//	 * 根据中文名称查找资源元数据定义对象
//	 * @param fieldName
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static List<SysMetadataDefinition> queryMetadataDefinitions(long sysMetadataTypeId){
//		String hql = "from SysMetadataDefinition where 1=1 and sysMetadataTypeId=" + sysMetadataTypeId;
//		List<SysMetadataDefinition> result = baseQueryService.query(hql);
//		if(result.size()>0){
//			return result;
//		}else{
//			return null;
//		}
//	}
//	
//	
//	/**
//	 * 根据查询DOI元数据值
//	 * @param fieldName
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static SysResDirectory queryMetadataData(String field,String fieldValue,long sysMetadataTypeId,String resType,int doiLength){
//		String hql = "";
//		if("o".equals(resType)){
//			hql = "from OresPrimitive o where o.sysResDirectory." + field + " = '" + fieldValue + "' and o.sysMetadataType.sysMetadataTypeId=" + sysMetadataTypeId;
//		}else if("c".equals(resType)){
//			hql = "from CrtAuthProduct c where c.doi= '" + fieldValue + "'  and c.status = 1";
//		}else if("p".equals(resType)){
//			String hql1 = "from SysResDirectory s where s." + field + " = '" + fieldValue + "' and sysMetadataTypeId=" + sysMetadataTypeId;
//			List<SysResDirectory> result1 = baseQueryService.query(hql1);
//			if(result1 != null && result1.size()>0){
//				SysResDirectory srd =  (SysResDirectory) result1.get(0);
//				//查询产品库查重逻辑
//				
//				
//			}else{
//				return null;
//			}
//		}else{
//			if(doiLength == 0){
//				hql = "from SysResDirectory s where s." + field +" = '" + fieldValue + "' and sysMetadataTypeId=" + sysMetadataTypeId;
//			}else{
//				hql = "from SysResDirectory s where substring(s." + field + ",1,"+doiLength+" )= '" + fieldValue + "' and sysMetadataTypeId=" + sysMetadataTypeId;
//			}
//		}
//		
//		List result = baseQueryService.query(hql);
//		if(result.size()>0){
//			if("o".equals(resType)){
//				OresPrimitive oresPrimitive = (OresPrimitive) result.get(0);
//				return oresPrimitive.getSysResDirectory();
//			}else{
//				return (SysResDirectory) result.get(0);
//			}
//		}else{
//			return null;
//		}
//	}
//	
//	
//	
//	/**
//	 * 判断生成DOI的字段是否可编辑
//	 * @return
//	 */
//	public static boolean isMetadateCanEdit(SysMetadataDefinition sysMetadataDefinition){
//		SysMetadataType sysMetadataType=SysMetaDataUtils.querySysMetadateType(sysMetadataDefinition.getSysMetadataType().getSysMetadataTypeId());
//		SysDoiDefinition sysDoiDefinition= null;
//		sysDoiDefinition = sysMetadataType.getSysDoiDefinition();
//		//验证是否已经配置了DOI
//		if(sysDoiDefinition!=null){
//			String secondOpt1 = sysDoiDefinition.getSecondOpt1();
//			if(secondOpt1 != null && !"".equals(secondOpt1)){
//				secondOpt1 = secondOpt1.substring(0,secondOpt1.indexOf("/"));
//				if(sysMetadataDefinition.getFieldEnName().equals(secondOpt1)){
//					return false;
//				}
//			}
//			String secondOpt2 = sysDoiDefinition.getSecondOpt2();
//			if(secondOpt2 != null && !"".equals(secondOpt2)){
//				secondOpt2 = secondOpt2.substring(0,secondOpt2.indexOf("/"));
//				if(sysMetadataDefinition.getFieldEnName().equals(secondOpt2)){
//					return false;
//				}
//			}
//			String secondOpt3 = sysDoiDefinition.getSecondOpt3();
//			if(secondOpt3 != null && !"".equals(secondOpt3)){
//				secondOpt3 = secondOpt3.substring(0,secondOpt3.indexOf("/"));
//				if(sysMetadataDefinition.getFieldEnName().equals(secondOpt3)){
//					return false;
//				}
//			}
//			String secondOpt4 = sysDoiDefinition.getSecondOpt4();
//			if(secondOpt4 != null && !"".equals(secondOpt4)){
//				secondOpt4 = secondOpt4.substring(0,secondOpt4.indexOf("/"));
//				if(sysMetadataDefinition.getFieldEnName().equals(secondOpt4)){
//					return false;
//				}
//			}
//			String thirdPathSymbol = sysDoiDefinition.getThirdPartSymbol();
//			if(thirdPathSymbol != null && !"".equals(thirdPathSymbol)){
//				if(sysMetadataDefinition.getFieldEnName().equals(thirdPathSymbol)){
//					return false;
//				}
//			}
//			String thirdPartValue1 = sysDoiDefinition.getThirdPartValue1();
//			if(thirdPartValue1 != null && !"".equals(thirdPartValue1)){
//				if(sysMetadataDefinition.getFieldEnName().equals(thirdPartValue1)){
//					return false;
//				}
//			}
//			String thirdPartValue2 = sysDoiDefinition.getThirdPartValue2();
//			if(thirdPartValue2 != null && !"".equals(thirdPartValue2)){
//				if(sysMetadataDefinition.getFieldEnName().equals(thirdPartValue2)){
//					return false;
//				}
//			}
//			String thirdPartValue3 = sysDoiDefinition.getThirdPartValue3();
//			if(thirdPartValue3 != null && !"".equals(thirdPartValue3)){
//				if(sysMetadataDefinition.getFieldEnName().equals(thirdPartValue3)){
//					return false;
//				}
//			}
//			String thirdPartValue4 = sysDoiDefinition.getThirdPartValue4();
//			if(thirdPartValue4 != null && !"".equals(thirdPartValue4)){
//				if(sysMetadataDefinition.getFieldEnName().equals(thirdPartValue4)){
//					return false;
//				}
//			}
//		}
//		return true;
//	}
//	
//	
//	/**
//	 * 判断资源元数据是否能删除
//	 * @return
//	 */
//	public static boolean isMetadateCanDelete(long resDirectoryId){
//		boolean canEdit = true;
//		String hql = "from OresPrimitive o where o.sysResDirectory.id = " + resDirectoryId;
//		List result = baseQueryService.query(hql);
//		if(result != null && result.size()>0){
//			return false;
//		}
//		return canEdit;
//	}
//	
//	/**
//	 * 查询元数据列表
//	 * @param field
//	 * @param fieldValue
//	 * @param sysMetadataTypeId
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static List<SysResDirectory> queryMetadataDataList(String field,String fieldValue,long sysMetadataTypeId){
//		String hql = "from SysResDirectory s where s." + field + " = '" + fieldValue + "' and sysMetadataTypeId=" + sysMetadataTypeId;
//		List<SysResDirectory> result = baseQueryService.query(hql);
//		return result;
//	}
//	
//	
//	/**
//	 * 查询元数据列表
//	 * @param titleValue
//	 * @param isbnValue
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static List<ResDMappingData> queryResDMappingDataList(String bookName,String isbnValue){
//		List<ResDMappingData> list = null;
//		boolean b = false;
//		Long  sysMetadataTypeId = Long.parseLong(WebappConfigUtil.getParameter(CBBOOK));
//		String isbnFiled = queryMetadataFieldByFieldEnName(CommonConstants.ISBN,sysMetadataTypeId);
//		StringBuffer hql = new StringBuffer("from SysResDirectory s where 1=1 and s.sysMetadataType.sysMetadataTypeId=" + sysMetadataTypeId);
//		
//		if(StringUtils.isNotBlank(bookName)){
//			b = true;
//			hql.append(" and s.bookName like '%" + bookName + "'");
//		}
//		
//		if(StringUtils.isNotBlank(isbnFiled) && StringUtils.isNotBlank(isbnValue)){
//			b = true;
//			hql.append(" and s." + isbnFiled + " = '" + isbnValue + "'");
//		}
//		
//		if(b){
//			//执行查询
//			List<SysResDirectory> result = baseQueryService.query(hql.toString());
//			if(result != null && result.size() > 0){
//				//拼接查询的sql条件
//				StringBuffer fileds = new StringBuffer();
//				fileds.append(CommonConstants.ISBN + ",");
//				fileds.append(CommonConstants.AUTHOR + ",");
//				fileds.append(CommonConstants.CONTORAUT + ",");
//				fileds.append(CommonConstants.CONTOREDT + ",");
//				fileds.append(CommonConstants.WORKWAY + ",");
//				fileds.append(CommonConstants.PUBMANNER + ",");
//				fileds.append(CommonConstants.DOI_EN + ",");
//				fileds.append(CommonConstants.PRTCPY + ",");
//				fileds.append(CommonConstants.IMPSUBJECT + ",");
//				fileds.append(CommonConstants.PRINCIPAL + ",");
//				fileds.append(CommonConstants.PPRICE);
//				
//				//查询自定义字段的 str filed
//				String[] strFileds = queryMetaDataFieldAndEnNametByFieldEnNames(String.valueOf(sysMetadataTypeId),fileds.toString());
//				//循环赋值
//				for (SysResDirectory sysResDirectory : result) {
//					ResDMappingData resDMappingData = null;
//					if(list == null){
//						list = new ArrayList<ResDMappingData>();
//					}
//					if(resDMappingData == null){
//						resDMappingData = new ResDMappingData();
//					}
//					if(strFileds != null && strFileds.length > 0){
//						for (int i = 0; i < strFileds.length; i++) {
//							String[] filedStrs = strFileds[i].split(",");
//							try {
//								String filedStr0 = filedStrs[0];
//								//赋值
//								if(filedStr0.equals("date")){
//									filedStr0 = "pubDate";
//								}
//								BeanUtils.setProperty(resDMappingData, filedStr0, BeanUtils.getProperty(sysResDirectory, filedStrs[1]));
//							} catch (IllegalAccessException e) {
//								e.printStackTrace();
//							} catch (InvocationTargetException e) {
//								e.printStackTrace();
//							} catch (NoSuchMethodException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//					resDMappingData.setId(sysResDirectory.getId());
//					resDMappingData.setSysMetadataTypeId(sysMetadataTypeId);
//					resDMappingData.setBookName(sysResDirectory.getBookName());
//					list.add(resDMappingData);
//				}
//			 }
//		}
//		
//		return list;
//	}
//	
//	
//	/**
//	 * 拷贝元数据
//	 * @param sysResList
//	 * @return
//	 */
//	public static List<ResDMappingData> cloneResDMappingDataList(List<SysResDirectory> sysResList){
//		List<ResDMappingData> list = null;
//		if(sysResList != null && sysResList.size() > 0){
//			Long sysMetadataTypeId = Long.parseLong(WebappConfigUtil.getParameter(CBBOOK));
//			if(sysResList != null && sysResList.size() > 0){
//				//拼接查询的sql条件
//				StringBuffer fileds = new StringBuffer();
//				fileds.append(CommonConstants.ISBN + ",");
//				fileds.append(CommonConstants.AUTHOR + ",");
//				fileds.append(CommonConstants.CONTORAUT + ",");
//				fileds.append(CommonConstants.CONTOREDT + ",");
//				fileds.append(CommonConstants.WORKWAY + ",");
//				fileds.append(CommonConstants.PUBMANNER + ",");
//				fileds.append(CommonConstants.DOI_EN + ",");
//				fileds.append(CommonConstants.PRTCPY + ",");
//				fileds.append(CommonConstants.PUBDATE + ",");
//				fileds.append(CommonConstants.IMPSUBJECT + ",");
//				fileds.append(CommonConstants.PRINCIPAL + ",");
//				fileds.append(CommonConstants.PPRICE);
//				
//				//查询自定义字段的 str filed
//				String[] strFileds = queryMetaDataFieldAndEnNametByFieldEnNames(String.valueOf(sysMetadataTypeId),fileds.toString());
//				//循环赋值
//				for (SysResDirectory sysResDirectory : sysResList) {
//					ResDMappingData resDMappingData = null;
//					if(list == null){
//						list = new ArrayList<ResDMappingData>();
//					}
//					if(resDMappingData == null){
//						resDMappingData = new ResDMappingData();
//					}
//					if(strFileds != null && strFileds.length > 0){
//						for (int i = 0; i < strFileds.length; i++) {
//							String[] filedStrs = strFileds[i].split(",");
//							try {
//								String filedStr0 = filedStrs[0];
//								//赋值
//								if(filedStr0.equals("date")){
//									filedStr0 = "pubDate";
//								}
//								BeanUtils.setProperty(resDMappingData, filedStr0, BeanUtils.getProperty(sysResDirectory, filedStrs[1]));
//							} catch (IllegalAccessException e) {
//								e.printStackTrace();
//							} catch (InvocationTargetException e) {
//								e.printStackTrace();
//							} catch (NoSuchMethodException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//					resDMappingData.setId(sysResDirectory.getId());
//					resDMappingData.setSysMetadataTypeId(sysMetadataTypeId);
//					resDMappingData.setBookName(sysResDirectory.getBookName());
//					list.add(resDMappingData);
//				}
//			 }
//		}
//		return list;
//	}
//	
//	
//	
//	/**
//	 * 查询元数据列表(支持按照一个或多个元数据id查询)
//	 * @param resIds
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static List<ResDMappingData> queryResDMappingDataListById(String resIds){
//		List<ResDMappingData> list = null;
//		boolean b = false;
//		Long  sysMetadataTypeId = Long.parseLong(WebappConfigUtil.getParameter(CBBOOK));
//		StringBuffer hql = new StringBuffer("from SysResDirectory s where 1=1 and s.sysMetadataType.sysMetadataTypeId=" + sysMetadataTypeId);
//		
//		if(StringUtils.isNotBlank(resIds)){
//			b = true;
//			if(resIds.endsWith(",")){
//				resIds = resIds.substring(0, resIds.length()-1);
//			}
//			if(resIds.indexOf(",") != -1){
//				StringBuffer sb = new StringBuffer();
//				String[] str = resIds.split(",");
//				for (int i = 0; i < str.length; i++) {
//					sb.append("'" + str[i] + "'" + ","); 
//				}
//				resIds = sb.toString().substring(0, sb.toString().length()-1);
//			}else{
//				resIds = "'" + resIds + "'";
//			}
//			hql.append(" and s.id  in (" + resIds + ")");
//		}
//		
//		if(b){
//			//执行查询
//			List<SysResDirectory> result = baseQueryService.query(hql.toString());
//			if(result != null && result.size() > 0){
//				//拼接查询的sql条件
//				StringBuffer fileds = new StringBuffer();
//				fileds.append(CommonConstants.ISBN + ",");
//				fileds.append(CommonConstants.AUTHOR + ",");
//				fileds.append(CommonConstants.CONTORAUT + ",");
//				fileds.append(CommonConstants.CONTOREDT + ",");
//				fileds.append(CommonConstants.WORKWAY + ",");
//				fileds.append(CommonConstants.PUBMANNER + ",");
//				fileds.append(CommonConstants.DOI_EN + ",");
//				fileds.append(CommonConstants.PRTCPY + ",");
//				fileds.append(CommonConstants.PUBDATE + ",");
//				fileds.append(CommonConstants.IMPSUBJECT + ",");
//				fileds.append(CommonConstants.PRINCIPAL + ",");
//				fileds.append(CommonConstants.PPRICE);
//				
//				//查询自定义字段的 str filed
//				String[] strFileds = queryMetaDataFieldAndEnNametByFieldEnNames(String.valueOf(sysMetadataTypeId),fileds.toString());
//				//循环赋值
//				for (SysResDirectory sysResDirectory : result) {
//					ResDMappingData resDMappingData = null;
//					if(list == null){
//						list = new ArrayList<ResDMappingData>();
//					}
//					if(resDMappingData == null){
//						resDMappingData = new ResDMappingData();
//					}
//					if(strFileds != null && strFileds.length > 0){
//						for (int i = 0; i < strFileds.length; i++) {
//							String[] filedStrs = strFileds[i].split(",");
//							try {
//								String filedStr0 = filedStrs[0];
//								//赋值
//								if(filedStr0.equals("date")){
//									filedStr0 = "pubDate";
//								}
//								BeanUtils.setProperty(resDMappingData, filedStr0, BeanUtils.getProperty(sysResDirectory, filedStrs[1]));
//							} catch (IllegalAccessException e) {
//								e.printStackTrace();
//							} catch (InvocationTargetException e) {
//								e.printStackTrace();
//							} catch (NoSuchMethodException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//					resDMappingData.setId(sysResDirectory.getId());
//					resDMappingData.setSysMetadataTypeId(sysMetadataTypeId);
//					resDMappingData.setBookName(sysResDirectory.getBookName());
//					list.add(resDMappingData);
//				}
//			 }
//		}
//		
//		return list;
//	}
//	
//	
//	
//	/**
//	 * 
//	 * @Title: queryMetaDataFieldtByFieldEnNames 
//	 * @Description: 根据配置的参数（英文字段）查询对应的field字段列表
//	 * @param   
//	 * @return String[]
//	 * @throws
//	 */
//	@SuppressWarnings({ "unchecked"})
//	public static String[] queryMetaDataFieldAndEnNametByFieldEnNames(String sysMetadataTypeId,String filedEnNames){
//		String[] arrStr =null;
//		
//		if(StringUtils.isNotEmpty(filedEnNames)){
//			if(filedEnNames.endsWith(",")){
//				filedEnNames = filedEnNames.substring(0, filedEnNames.length()-1);
//			}
//			if(filedEnNames.indexOf(",") != -1){
//				StringBuffer sb = new StringBuffer();
//				String[] str = filedEnNames.split(",");
//				for (int i = 0; i < str.length; i++) {
//					sb.append("'" + str[i] + "'" + ","); 
//				}
//				filedEnNames = sb.toString().substring(0, sb.toString().length()-1);
//			}else{
//				filedEnNames = "'" + filedEnNames + "'";
//			}
//			
//			List<SysMetadataDefinition> smdd =  baseQueryService.query(" from SysMetadataDefinition s where s.sysMetadataType.sysMetadataTypeId =" + Long.parseLong(sysMetadataTypeId) + " and s.fieldEnName in (" + filedEnNames + ")");
//			if(smdd != null && smdd.size() > 0){
//				arrStr = new String[smdd.size()];
//				for (int i = 0; i < smdd.size(); i++) {
//					SysMetadataDefinition sd = smdd.get(i);
//					arrStr[i] = sd.getFieldEnName() + "," + sd.getField();
//				  }
//			  }
//		  }
//		return arrStr;
//	}
//	
//	/**
//	 * 
//	 * @Title: queryMetaDataFieldtByFieldEnNames 
//	 * @Description: 根据配置的参数（英文字段）查询对应的field字段列表
//	 * @param   
//	 * @return String[]
//	 * @throws
//	 */
//	@SuppressWarnings({ "unchecked"})
//	public static String[] queryMetaDataFieldtByFieldEnNames(String sysMetadataTypeId,String filedEnNames){
//		String[] arrStr =null;
//		
//		if(StringUtils.isNotEmpty(filedEnNames)){
//			if(filedEnNames.endsWith(",")){
//				filedEnNames = filedEnNames.substring(0, filedEnNames.length()-1);
//			}
//			if(filedEnNames.indexOf(",") != -1){
//				StringBuffer sb = new StringBuffer();
//				String[] str = filedEnNames.split(",");
//				for (int i = 0; i < str.length; i++) {
//					sb.append("'" + str[i] + "'" + ","); 
//				}
//				filedEnNames = sb.toString().substring(0, sb.toString().length()-1);
//			}else{
//				filedEnNames = "'" + filedEnNames + "'";
//			}
//			
//			List<SysMetadataDefinition> smdd =  baseQueryService.query(" from SysMetadataDefinition s where s.sysMetadataType.sysMetadataTypeId =" + Long.parseLong(sysMetadataTypeId) + " and s.fieldEnName in (" + filedEnNames + ")");
//			if(smdd != null && smdd.size() > 0){
//				arrStr = new String[smdd.size()];
//				for (int i = 0; i < smdd.size(); i++) {
//					SysMetadataDefinition sd = smdd.get(i);
//					arrStr[i] = sd.getField();
//				  }
//			  }
//		  }
//		return arrStr;
//	}
//	
//	
//	
//	/**
//	 * 
//	 * @Title: queryMetaDataFieldtByFieldCnNames 
//	 * @Description: 根据配置的参数（中文字段）查询对应的field字段列表
//	 * @param   
//	 * @return String[]
//	 * @throws
//	 */
//	@SuppressWarnings({ "unchecked"})
//	public static String[] queryMetaDataFieldAndCnNametByFieldCnNames(String sysMetadataTypeId,String filedCnNames){
//		String[] arrStr =null;
//		
//		if(StringUtils.isNotEmpty(filedCnNames)){
//			if(filedCnNames.endsWith(",")){
//				filedCnNames = filedCnNames.substring(0, filedCnNames.length()-1);
//			}
//			if(filedCnNames.indexOf(",") != -1){
//				StringBuffer sb = new StringBuffer();
//				String[] str = filedCnNames.split(",");
//				for (int i = 0; i < str.length; i++) {
//					sb.append("'" + str[i] + "'" + ","); 
//				}
//				filedCnNames = sb.toString().substring(0, sb.toString().length()-1);
//			}else{
//				filedCnNames = "'" + filedCnNames + "'";
//			}
//			
//			List<SysMetadataDefinition> smdd =  baseQueryService.query(" from SysMetadataDefinition s where s.sysMetadataType.sysMetadataTypeId =" + Long.parseLong(sysMetadataTypeId) + " and s.fieldName in (" + filedCnNames + ")");
//			if(smdd != null && smdd.size() > 0){
//				arrStr = new String[smdd.size()];
//				for (int i = 0; i < smdd.size(); i++) {
//					SysMetadataDefinition sd = smdd.get(i);
//					arrStr[i] =sd.getFieldName() + "," +  sd.getField();
//				  }
//			  }
//		  }
//		return arrStr;
//	}
//	
//	/**
//	 * 
//	 * @Title: queryMetaDataFieldtByFieldCnNames 
//	 * @Description: 根据配置的参数（中文字段）查询对应的field字段列表
//	 * @param   
//	 * @return String[]
//	 * @throws
//	 */
//	@SuppressWarnings({ "unchecked"})
//	public static String[] queryMetaDataFieldtByFieldCnNames(String sysMetadataTypeId,String filedCnNames){
//		String[] arrStr =null;
//		
//		if(StringUtils.isNotEmpty(filedCnNames)){
//			if(filedCnNames.endsWith(",")){
//				filedCnNames = filedCnNames.substring(0, filedCnNames.length()-1);
//			}
//			if(filedCnNames.indexOf(",") != -1){
//				StringBuffer sb = new StringBuffer();
//				String[] str = filedCnNames.split(",");
//				for (int i = 0; i < str.length; i++) {
//					sb.append("'" + str[i] + "'" + ","); 
//				}
//				filedCnNames = sb.toString().substring(0, sb.toString().length()-1);
//			}else{
//				filedCnNames = "'" + filedCnNames + "'";
//			}
//			
//			List<SysMetadataDefinition> smdd =  baseQueryService.query(" from SysMetadataDefinition s where s.sysMetadataType.sysMetadataTypeId =" + Long.parseLong(sysMetadataTypeId) + " and s.fieldName in (" + filedCnNames + ")");
//			if(smdd != null && smdd.size() > 0){
//				arrStr = new String[smdd.size()];
//				for (int i = 0; i < smdd.size(); i++) {
//					SysMetadataDefinition sd = smdd.get(i);
//					arrStr[i] = sd.getField();
//				  }
//			  }
//		  }
//		return arrStr;
//	}
//	
//	
//	
//	/**
//	 * 拼接说明
//	 * @param sysMetadataDefinition
//	 * @return
//	 */
//	public static SuperJSONDataObject queryStatusByDefine(List<SysMetadataDefinition> sysMetadataDefinitions,String filedName,String fieldValue){
//		SuperJSONDataObject reMsg = new SuperJSONDataObject();
//		String instructions = "【"+filedName+"】"; //默认无消息
//		String status = "1"; // 1：成功  0：失败 默认校验成功
//		String description = "";
//		if(sysMetadataDefinitions != null){
//		 for (SysMetadataDefinition sysMetadataDefinition: sysMetadataDefinitions) {
//			 String curFieldName = sysMetadataDefinition.getFieldName();
//			    if(filedName.equals(curFieldName)){
//			    	//字段类型 1:text 文本、2:select 选择框、3:checkBox 复选框、4:radio 单选框 5：textarea 文本域
//				    //6:byte[] 图片附件 7 date 日期 8 分类lookup 9url
//					String fieldType = sysMetadataDefinition.getFieldType().toString(); 
//					
//					// 是否允许空值 默认1 允许为空 0 不允许为空
//				    boolean boo = sysMetadataDefinition.isAllowNull();
//
//				    // 值格式效验模式 默认1:无 2:数字 3:字母 4:数字及字母 5:汉字 6:邮箱
//				    String validateModel = sysMetadataDefinition.getValidateModel().toString();
//
//				    // 值范围 已,分割多个值 只有当字段类型为2:select 选择框、3:checkBox 复选框、4:radio 单选框 时有效
//				    // 如果为分类查找类型则为获取分类的url
//				    String valueRange = sysMetadataDefinition.getValueRange();
//
//
//				    // 值长度范围 以双闭区间限定取值范围 如 0,10表示长度在0和10之间 如为5则长度必须大于5 如为,10表示长度必须小于10
//				    String valueLength = sysMetadataDefinition.getValueLength();
//				    
//				    // 是否启用 默认1 启用 0 不启用
////				    boolean status = sysMetadataDefinition.isStatus();
//				    
//				    description = sysMetadataDefinition.getDescription();
//				    
//				    String bitian = "必填项;";
//				    String shuzi = "必须为数字;";
//				    String zimu = "必须为字母;";
//				    String shuziAndzimu = "数字及字母;";
//				    String hanzi = "必须为汉字;";
//				    String email = "必须为邮箱;";
//				   // String date = "必须为日期，格式形如：【 yyyy-MM-dd 】;";
//				    
//				    
//				    
//				    String vLength = "64";
//				    //定义数据长度项
//				    String wzcd = "长度未在元数据中明确定义.";
//				    if(fieldType.equals("1")){
//				    	wzcd += "此处为输入框，请确认"+ vLength +"位以内;";
//				    }else if(fieldType.equals("5")){
//				    	vLength ="50000";
//				    	wzcd += "此处为文本域，支持大段文本;";
//				    }else{
//				    	wzcd += "请确认在"+ vLength +"位以内;";
//				    }
//				    
//				    //取值范围
//				    if(StringUtils.isNotBlank(valueLength)){
//				    	if(valueLength.indexOf(",")>=0){
//				    		vLength = valueLength.substring(1,valueLength.length());
//					    	valueLength = "取值范围为：【" + "0-" + vLength + "】位;";
//				    	}else{
//				    		vLength = valueLength;
//					    	valueLength = "取值范围为：【" +  vLength + "】位;";
//					    }
//				    }else{
//				    	valueLength = wzcd;
//				    }
//				    
//				    //可选项
////				    if(StringUtils.isNotBlank(valueRange)){
////				    	valueRange = "可选项为：【" +  valueRange + "】;";
////				    }
//				   
//				    //第一步：判读是否为空
//				    if(!boo){
//					    if(StringUtils.isBlank(fieldValue)){
//			    			status = "0";
//			    		}
//				    }
//				    
//				    //1:text 文本  5：textarea 文本域
//				    if(fieldType.equals("1") || fieldType.equals("5")){
//				    	if(validateModel.equals("2")){
//				    		if(!boo){
//				    			instructions = instructions+bitian  + shuzi + valueLength +  doTsx(sysMetadataDefinition);
//				    		 }else{
//				    			instructions =instructions+ shuzi +  valueLength  +  doTsx(sysMetadataDefinition);
//				    		 }
//				    		
//				    		//第二步：校验是否符合数据类型
//				    		if(!DataValidator.isNum(fieldValue)){
//				    			status = "0";
//				    		}
//				    		
//				    	}else if(validateModel.equals("3")){
//				    		if(!boo){
//				    			instructions =instructions+  bitian +  zimu +  valueLength  +  doTsx(sysMetadataDefinition);
//				    		}else{
//				    			instructions =instructions+ zimu + valueLength  +  doTsx(sysMetadataDefinition);
//				    		}
//				    		
//				    		//第二步：校验是否符合数据类型,如果是字母 则只需要校验长度
//				    		if(!DataValidator.isLetter(fieldValue)){
//				    			status = "0";
//				    		}
//				    		
//				    	}else if(validateModel.equals("4")){  //数字及字母
//				    		if(!boo){
//				    			instructions = instructions+ bitian +  shuziAndzimu + valueLength  + doTsx(sysMetadataDefinition);
//				    		}else{
//				    			instructions = instructions+shuziAndzimu +  valueLength  + doTsx(sysMetadataDefinition);
//				    		}
//				    		
//				    		//第二步：校验是否符合数据类型
//				    		if(!DataValidator.isLetter(fieldValue) || !DataValidator.isNum(fieldValue)){
//				    			status = "0";
//				    		}
//				    		
//				    	}else if(validateModel.equals("5")){  //汉字
//				    		if(!boo){
//				    			instructions = instructions+ bitian +  hanzi + valueLength + doTsx(sysMetadataDefinition);
//				    		}else{
//				    			instructions =instructions+ hanzi + valueLength +  doTsx(sysMetadataDefinition);
//				    		}
//				    		
//				    		//第二步：校验是否符合数据类型,汉字
//				    		if(!DataValidator.isChinese(fieldValue)){
//				    			status = "0";
//				    		}
//				    		
//				    	}else if(validateModel.equals("6")){  //邮箱
//				    		if(!boo){
//				    			instructions =  bitian + email;
//				    		}else{
//				    			instructions = email;
//				    		}
//				    		
//				    		//第二步：校验是否符合数据类型 - 邮箱
//				    		if(!DataValidator.isZipcode(fieldValue)){
//				    			status = "0";
//				    		}
//				    		
//				    	}else if(validateModel.equals("1")){
//				    		if(!boo){
//				    			instructions = instructions+ bitian+ valueLength +  doTsx(sysMetadataDefinition);
//				    		}else{
//				    			instructions = instructions+ valueLength +  doTsx(sysMetadataDefinition);
//				    		}
//				    	}else{
//				    		//
//				    	}
//				    }else if(fieldType.equals("2") || fieldType.equals("4")){//2:select 选择框   4:radio 单选框
//				    	if(!boo){
//			    			instructions =instructions+ bitian + ",且为单选项," + valueRange + "(注：只能选其一);";
//			    		}else{
//			    			instructions =instructions+ "为单选项,可选项为：【" + valueRange + "】,(注：只能选其一);";
//			    		}
//				    	if(!DataValidator.existValue(fieldValue, valueRange)){
//				    		status = "0";
//				    	}
//				    }else if(fieldType.equals("3")){
//				    	if(!boo){
//			    			instructions =instructions+ bitian + ",且为多选项," + valueRange + "(注：能选其一个或多个项);";
//			    		}else{
//			    			instructions =instructions+ "为多选项," + valueRange + "(注：能选一个或多个);";
//			    		}
//				    	if(!DataValidator.existCheckValue(fieldValue, valueRange)){
//				    		status = "0";
//				    	}
//				    }else if(fieldType.equals("7")){   //日期
//				    	if(!boo){
//			    			instructions =instructions+ bitian; // + date;
//			    		}else{
//			    			//instructions = instructions;//+date;
//			    		}
//				    	//第二步：校验是否符合数据类型 - 日期
////			    		if(!DataValidator.isDate(fieldValue)){
////			    			status = "0";
////			    		}
//				    }else{
//				    	if(!boo){
//			    			instructions =instructions+ bitian +  valueLength +  doTsx(sysMetadataDefinition);
//			    		}else{
//			    			instructions =instructions+ valueLength +  doTsx(sysMetadataDefinition);
//			    		}
//				    }
//				    //第三步：校验输入长度
//		    		long len = DataValidator.howLength(fieldValue);
//		    		if(DataValidator.vsLength(len+"",vLength)){
//		    			status = "0";
//		    		}
//				  }
//			   }
//		   }
//			
//		
//		reMsg.setMsg(instructions+ " " + description);
//		reMsg.setStatus(status);
//		return reMsg;
//	}
//	
//	/**
//	 * 拼接说明
//	 * @param sysMetadataDefinition
//	 * @return
//	 */
//	public static SuperJSONDataObject queryStatusByDefineFieldEnName(List<SysMetadataDefinition> sysMetadataDefinitions,String filedEnName,String fieldValue){
//		SuperJSONDataObject reMsg = new SuperJSONDataObject();
//		String instructions = "【"+filedEnName+"】"; //默认无消息
//		String status = "1"; // 1：成功  0：失败 默认校验成功
//		String description = "";
//		if(sysMetadataDefinitions != null){
//		 for (SysMetadataDefinition sysMetadataDefinition: sysMetadataDefinitions) {
//			 String curFieldName = sysMetadataDefinition.getFieldEnName();
//			    if(filedEnName.equals(curFieldName)){
//			    	//字段类型 1:text 文本、2:select 选择框、3:checkBox 复选框、4:radio 单选框 5：textarea 文本域
//				    //6:byte[] 图片附件 7 date 日期 8 分类lookup 9url
//					String fieldType = sysMetadataDefinition.getFieldType().toString(); 
//					
//					// 是否允许空值 默认1 允许为空 0 不允许为空
//				    boolean boo = sysMetadataDefinition.isAllowNull();
//
//				    // 值格式效验模式 默认1:无 2:数字 3:字母 4:数字及字母 5:汉字 6:邮箱
//				    String validateModel = sysMetadataDefinition.getValidateModel().toString();
//
//				    // 值范围 已,分割多个值 只有当字段类型为2:select 选择框、3:checkBox 复选框、4:radio 单选框 时有效
//				    // 如果为分类查找类型则为获取分类的url
//				    String valueRange = sysMetadataDefinition.getValueRange();
//
//
//				    // 值长度范围 以双闭区间限定取值范围 如 0,10表示长度在0和10之间 如为5则长度必须大于5 如为,10表示长度必须小于10
//				    String valueLength = sysMetadataDefinition.getValueLength();
//				    
//				    // 是否启用 默认1 启用 0 不启用
////				    boolean status = sysMetadataDefinition.isStatus();
//				    
//				    description = sysMetadataDefinition.getDescription();
//				    
//				    String bitian = "必填项;";
//				    String shuzi = "必须为数字;";
//				    String zimu = "必须为字母;";
//				    String shuziAndzimu = "数字及字母;";
//				    String hanzi = "必须为汉字;";
//				    String email = "必须为邮箱;";
//				   // String date = "必须为日期，格式形如：【 yyyy-MM-dd 】;";
//				    
//				    
//				    
//				    String vLength = "64";
//				    //定义数据长度项
//				    String wzcd = "长度未在元数据中明确定义.";
//				    if(fieldType.equals("1")){
//				    	wzcd += "此处为输入框，请确认"+ vLength +"位以内;";
//				    }else if(fieldType.equals("5")){
//				    	vLength ="50000";
//				    	wzcd += "此处为文本域，支持大段文本;";
//				    }else{
//				    	wzcd += "请确认在"+ vLength +"位以内;";
//				    }
//				    
//				    //取值范围
//				    if(StringUtils.isNotBlank(valueLength)){
//				    	if(valueLength.indexOf(",")>=0){
//				    		vLength = valueLength.substring(1,valueLength.length());
//					    	valueLength = "取值范围为：【" + "0-" + vLength + "】位;";
//				    	}else{
//				    		vLength = valueLength;
//					    	valueLength = "取值范围为：【" +  vLength + "】位;";
//					    }
//				    }else{
//				    	valueLength = wzcd;
//				    }
//				    
//				    //可选项
////				    if(StringUtils.isNotBlank(valueRange)){
////				    	valueRange = "可选项为：【" +  valueRange + "】;";
////				    }
//				   
//				    //第一步：判读是否为空
//				    if(!boo){
//					    if(StringUtils.isBlank(fieldValue)){
//			    			status = "0";
//			    		}
//				    }
//				    
//				    //1:text 文本  5：textarea 文本域
//				    if(fieldType.equals("1") || fieldType.equals("5")){
//				    	if(validateModel.equals("2")){
//				    		if(!boo){
//				    			instructions = instructions+bitian  + shuzi + valueLength +  doTsx(sysMetadataDefinition);
//				    		 }else{
//				    			instructions =instructions+ shuzi +  valueLength  +  doTsx(sysMetadataDefinition);
//				    		 }
//				    		
//				    		//第二步：校验是否符合数据类型
//				    		if(!DataValidator.isNum(fieldValue)){
//				    			status = "0";
//				    		}
//				    		
//				    	}else if(validateModel.equals("3")){
//				    		if(!boo){
//				    			instructions =instructions+  bitian +  zimu +  valueLength  +  doTsx(sysMetadataDefinition);
//				    		}else{
//				    			instructions =instructions+ zimu + valueLength  +  doTsx(sysMetadataDefinition);
//				    		}
//				    		
//				    		//第二步：校验是否符合数据类型,如果是字母 则只需要校验长度
//				    		if(!DataValidator.isLetter(fieldValue)){
//				    			status = "0";
//				    		}
//				    		
//				    	}else if(validateModel.equals("4")){  //数字及字母
//				    		if(!boo){
//				    			instructions = instructions+ bitian +  shuziAndzimu + valueLength  + doTsx(sysMetadataDefinition);
//				    		}else{
//				    			instructions = instructions+shuziAndzimu +  valueLength  + doTsx(sysMetadataDefinition);
//				    		}
//				    		
//				    		//第二步：校验是否符合数据类型
//				    		if(!DataValidator.isLetter(fieldValue) || !DataValidator.isNum(fieldValue)){
//				    			status = "0";
//				    		}
//				    		
//				    	}else if(validateModel.equals("5")){  //汉字
//				    		if(!boo){
//				    			instructions = instructions+ bitian +  hanzi + valueLength + doTsx(sysMetadataDefinition);
//				    		}else{
//				    			instructions =instructions+ hanzi + valueLength +  doTsx(sysMetadataDefinition);
//				    		}
//				    		
//				    		//第二步：校验是否符合数据类型,汉字
//				    		if(!DataValidator.isChinese(fieldValue)){
//				    			status = "0";
//				    		}
//				    		
//				    	}else if(validateModel.equals("6")){  //邮箱
//				    		if(!boo){
//				    			instructions =  bitian + email;
//				    		}else{
//				    			instructions = email;
//				    		}
//				    		
//				    		//第二步：校验是否符合数据类型 - 邮箱
//				    		if(!DataValidator.isZipcode(fieldValue)){
//				    			status = "0";
//				    		}
//				    		
//				    	}else if(validateModel.equals("1")){
//				    		if(!boo){
//				    			instructions = instructions+ bitian+ valueLength +  doTsx(sysMetadataDefinition);
//				    		}else{
//				    			instructions = instructions+ valueLength +  doTsx(sysMetadataDefinition);
//				    		}
//				    	}else{
//				    		//
//				    	}
//				    }else if(fieldType.equals("2") || fieldType.equals("4")){//2:select 选择框   4:radio 单选框
//				    	if(!boo){
//			    			instructions =instructions+ bitian + ",且为单选项," + valueRange + "(注：只能选其一);";
//			    		}else{
//			    			instructions =instructions+ "为单选项,可选项为：【" + valueRange + "】,(注：只能选其一);";
//			    		}
//				    	if(!DataValidator.existValue(fieldValue, valueRange)){
//				    		status = "0";
//				    	}
//				    }else if(fieldType.equals("3")){
//				    	if(!boo){
//			    			instructions =instructions+ bitian + ",且为多选项," + valueRange + "(注：能选其一个或多个项);";
//			    		}else{
//			    			instructions =instructions+ "为多选项," + valueRange + "(注：能选一个或多个);";
//			    		}
//				    	if(!DataValidator.existCheckValue(fieldValue, valueRange)){
//				    		status = "0";
//				    	}
//				    }else if(fieldType.equals("7")){   //日期
//				    	if(!boo){
//			    			instructions =instructions+ bitian; // + date;
//			    		}else{
//			    			//instructions = instructions;//+date;
//			    		}
//				    	//第二步：校验是否符合数据类型 - 日期
////			    		if(!DataValidator.isDate(fieldValue)){
////			    			status = "0";
////			    		}
//				    }else{
//				    	if(!boo){
//			    			instructions =instructions+ bitian +  valueLength +  doTsx(sysMetadataDefinition);
//			    		}else{
//			    			instructions =instructions+ valueLength +  doTsx(sysMetadataDefinition);
//			    		}
//				    }
//				    //第三步：校验输入长度
//		    		long len = DataValidator.howLength(fieldValue);
//		    		if(DataValidator.vsLength(len+"",vLength)){
//		    			status = "0";
//		    		}
//				  }
//			   }
//		   }
//			
//		
//		reMsg.setMsg(instructions+ " " + description);
//		reMsg.setStatus(status);
//		return reMsg;
//	}
//	
//	/**
//	 * 拼接说明
//	 * @param sysMetadataDefinition
//	 * @return
//	 */
//	public static String queryMetadataFieldByDefine(SysMetadataDefinition sysMetadataDefinition){
//		String instructions = "";
//		String description = "";
//		if(sysMetadataDefinition != null){
//			//字段类型 1:text 文本、2:select 选择框、3:checkBox 复选框、4:radio 单选框 5：textarea 文本域
//		    //6:byte[] 图片附件 7 date 日期 8 分类lookup 9url
//			String fieldType = sysMetadataDefinition.getFieldType().toString(); 
//			
//			// 是否允许空值 默认1 允许为空 0 不允许为空
//		    boolean boo = sysMetadataDefinition.isAllowNull();
//
//		    // 值格式效验模式 默认1:无 2:数字 3:字母 4:数字及字母 5:汉字 6:邮箱
//		    String validateModel = sysMetadataDefinition.getValidateModel().toString();
//
//		    // 值范围 已,分割多个值 只有当字段类型为2:select 选择框、3:checkBox 复选框、4:radio 单选框 时有效
//		    // 如果为分类查找类型则为获取分类的url
//		    String valueRange = sysMetadataDefinition.getValueRange();
//
//
//		    // 值长度范围 以双闭区间限定取值范围 如 0,10表示长度在0和10之间 如为5则长度必须大于5 如为,10表示长度必须小于10
//		    String valueLength = sysMetadataDefinition.getValueLength();
//		    
//		    // 是否启用 默认1 启用 0 不启用
////		    boolean status = sysMetadataDefinition.isStatus();
//		    
//		    description = sysMetadataDefinition.getDescription();
//		    
//		    String bitian = "必填项;";
//		    String shuzi = "必须为数字;";
//		    String zimu = "必须为字母;";
//		    String shuziAndzimu = "数字及字母;";
//		    String hanzi = "必须为汉字;";
//		    String email = "必须为邮箱;";
//		    //String date = "必须为日期，格式形如：【 yyyy-MM-dd 】;";
//		    
//		    //定义数据长度项
//		    String wzcd = "长度未在元数据中明确定义.";
//		    if(fieldType.equals("1")){
//		    	wzcd += "此处为输入框，请确认64位以内;";
//		    }else if(fieldType.equals("5")){
//		    	 wzcd += "此处为文本域，支持大段文本;";
//		    }else{
//		       wzcd += "请确认在64位以内;";
//		    }
//		    
//		    //取值范围
//		    if(StringUtils.isNotBlank(valueLength)){
//		    	if(valueLength.startsWith(",")){
//		    		valueLength = valueLength.substring(1,valueLength.length());
//			    	valueLength = "取值范围为：【" + "0-" + valueLength + "】位;";
//		    	}else{
//			    	valueLength = "取值范围为：【" +  valueLength + "】位;";
//			    }
//		    }else{
//		    	valueLength = wzcd;
//		    }
//		    
//		    //可选项
//		    if(StringUtils.isNotBlank(valueRange)){
//		    	valueRange = "可选项为：【" +  valueRange + "】;";
//		    }
//		    
//		    
//		    if(fieldType.equals("1") || fieldType.equals("5")){
//		    	if(validateModel.equals("2")){
//		    		if(!boo){
//		    			instructions = bitian  + shuzi + valueLength +  doTsx(sysMetadataDefinition);
//		    		}else{
//		    			instructions = shuzi +  valueLength  +  doTsx(sysMetadataDefinition);
//		    		}
//		    	}else if(validateModel.equals("3")){
//		    		if(!boo){
//		    			instructions =  bitian +  zimu +  valueLength  +  doTsx(sysMetadataDefinition);
//		    		}else{
//		    			instructions = zimu + valueLength  +  doTsx(sysMetadataDefinition);
//		    		}
//		    	}else if(validateModel.equals("4")){
//		    		if(!boo){
//		    			instructions =  bitian +  shuziAndzimu + valueLength  + doTsx(sysMetadataDefinition);
//		    		}else{
//		    			instructions = shuziAndzimu +  valueLength  + doTsx(sysMetadataDefinition);
//		    		}
//		    	}else if(validateModel.equals("5")){
//		    		if(!boo){
//		    			instructions =  bitian +  hanzi + valueLength + doTsx(sysMetadataDefinition);
//		    		}else{
//		    			instructions = hanzi + valueLength +  doTsx(sysMetadataDefinition);
//		    		}
//		    	}else if(validateModel.equals("6")){
//		    		if(!boo){
//		    			instructions =  bitian + email;
//		    		}else{
//		    			instructions = email;
//		    		}
//		    	}else if(validateModel.equals("1")){
//		    		if(!boo){
//		    			instructions =  bitian+ valueLength +  doTsx(sysMetadataDefinition);
//		    		}else{
//		    			instructions =  valueLength +  doTsx(sysMetadataDefinition);
//		    		}
//		    	}else{
//		    		//
//		    	}
//		    }else if(fieldType.equals("2") || fieldType.equals("4")){
//		    	if(!boo){
//	    			instructions = bitian + ",且为单选项," + valueRange + "(注：只能选其一);";
//	    		}else{
//	    			instructions = "为单选项," + valueRange + "(注：只能选其一);";
//	    		}
//		    }else if(fieldType.equals("3")){
//		    	if(!boo){
//	    			instructions = bitian + ",且为多选项," + valueRange + "(注：能选其一个或多个项);";
//	    		}else{
//	    			instructions = "为多选项," + valueRange + "(注：能选一个或多个);";
//	    		}
//		    }else if(fieldType.equals("7")){
//		    	if(!boo){
//	    			instructions = bitian;// + date;
//	    		}else{
//	    			//instructions = date;
//	    		}
//		    }else{
//		    	if(!boo){
//	    			instructions = bitian +  valueLength +  doTsx(sysMetadataDefinition);
//	    		}else{
//	    			instructions = valueLength +  doTsx(sysMetadataDefinition);
//	    		}
//		    }
//		}
//		if(description == null){
//			return instructions;
//		}
//		return instructions + " " +  description;
//	}
//	
//	
//	
//	//对特殊项处理
//	public static String doTsx(SysMetadataDefinition sysMetadataDefinition){
//		String tsxStr = "";
////		String fieldName = sysMetadataDefinition.getFieldName();
////		if(fieldName.equals("纸质图书价格")){
////			tsxStr = "【合法输入如：CNY18.00】;";
////		}else if(fieldName.equals("ISBN")){
////			tsxStr = "【合法输入如：10位或13】;";
////		}
//		return tsxStr;
//	}
//	/**
//	 * 通过提供方代码 获得提供方对象
//	 * @return
//	 */
//	public static ResProvided queryProviderByPressCode(String pressCode){
//		if(!StringUtils.isEmpty(pressCode)){
//			String hql = "from ResProvided r where r.pressCode = '" + pressCode+"'";
//			List result = baseQueryService.query(hql);
//			if(result != null && result.size()>0){
//				return (ResProvided) result.get(0);
//			}
//		}
//		return null;
//	}
//	
//
//	/**
//	 * 查询自定义字段
//	 * @param sysMetadataTypeId
//	 * @param filedEnNames
//	 * @return Map<String,String>
//	 */
//	public static Map<String,String> queryMetaDataFieldAndEnNametByFieldEnNames2(String sysMetadataTypeId,String filedEnNames){
//		Map<String,String> arrStr = new HashMap<String, String>();
//		
//		if(StringUtils.isNotEmpty(filedEnNames)){
//			if(filedEnNames.endsWith(",")){
//				filedEnNames = filedEnNames.substring(0, filedEnNames.length()-1);
//			}
//			if(filedEnNames.indexOf(",") != -1){
//				StringBuffer sb = new StringBuffer();
//				String[] str = filedEnNames.split(",");
//				for (int i = 0; i < str.length; i++) {
//					sb.append("'" + str[i] + "'" + ","); 
//				}
//				filedEnNames = sb.toString().substring(0, sb.toString().length()-1);
//			}else{
//				filedEnNames = "'" + filedEnNames + "'";
//			}
//			
//			List<SysMetadataDefinition> smdd =  baseQueryService.query(" from SysMetadataDefinition s where s.sysMetadataType.sysMetadataTypeId =" + Long.parseLong(sysMetadataTypeId) + " and s.fieldEnName in (" + filedEnNames + ")");
//			if(smdd != null && smdd.size() > 0){
//				for (int i = 0; i < smdd.size(); i++) {
//					SysMetadataDefinition sd = smdd.get(i);
//					arrStr.put(sd.getFieldEnName(), sd.getField());
//				  }
//			  }
//		  }
//		return arrStr;
//	}
//	
//	/**
//	 * 
//	 * @Title: queryResCategoryIdByCode 
//	 * @Description: 查询自定义分类主键Id
//	 * @param  code 
//	 * @param  type 
//	 * @return Long
//	 * @throws
//	 */
//	public static String queryResCategoryIdByCode(String code,String type){
//		String ids = "";
//		if(StringUtils.isNotEmpty(code) && StringUtils.isNotEmpty(type)){
//		   List<ResCategory> resCategorys = null;
//		   String[] codeArray = code.split(",");
//		   for (int k = 0;k < codeArray.length; k++) {
//			  String subCode = codeArray[k].trim();
//			  if(StringUtils.isNotBlank(subCode)){
//				 int subCodeLen = subCode.length();
//				 for(int i=0;i< subCodeLen;i++){
//					 resCategorys =  queryResCategoryPo(subCode, type);
//					 if(resCategorys == null || resCategorys.size() <= 0){
//						 if(subCodeLen>1){
//							 subCode = subCode.substring(0,subCode.length()-1);
//							 subCodeLen--;
//							 i=0;
//							 if(subCodeLen==1){
//								 resCategorys = queryResCategoryPo(subCode, type);
//							 }
//						 }
//					 }
//				 }
//				 
//				 if(resCategorys != null && resCategorys.size() > 0){
//					 //如果只查询到了一条数据，那么说明准确的定位到了分类
//					 ids += resCategorys.get(0).getId() + ",";
//				 }
//			  }
//		   }
//		   
//		 //截取最后一个","
//		 if(StringUtils.isNotBlank(ids)){
//			 String returnIds = "";
//			 String[] idItem = ids.split(",");
//			 Set<String> idSet = new HashSet<String>();
//			 for(int i=0;i<idItem.length;i++){
//				 idSet.add(idItem[i]);
//			 }
//			 for(String id:idSet){
//				 returnIds += id+",";
//			 }
//			 if(StringUtils.isNotBlank(returnIds)){
//				 ids = ","+returnIds;
//			 }
//		 }
//	  }
//		return ids;
//	}
//
//	
//	/**
//	 * 
//	 * @Title: queryResCategoryPo 
//	 * @Description: 查询资源分类（先精确查询再模糊查询）
//	 * @param   
//	 * @return List<ResCategory> 
//	 * @throws
//	 */
//	public static List<ResCategory> queryResCategoryPo(String subCode,String type){
//		List<ResCategory>  resCategorys = new ArrayList<ResCategory>();
//		if("1".equals(type)){
//			//1.首先精确查询
//			resCategorys= baseQueryService.query(" from ResCategory s where s.code  = '"  + subCode +  "' and s.type =" + type);
//			if(resCategorys == null || resCategorys.size() <= 0){
//				//2.然后全匹配-模糊查询
//				resCategorys = baseQueryService.query(" from ResCategory s where s.code  like '%"  + subCode +  "%' and s.type =" + type);
//			}
//		}else{
//			resCategorys = baseQueryService.query(" from ResCategory s where s.name  = '"  + subCode +  "' and s.type =" + type);
//		}
//		return resCategorys;
//	}
//	/**
//	 * 根据ISBN和资源id查询出每个ISBN对应的数据条数
//	 * @param str
//	 * @return
//	 */
//	public static Map<String, Integer> countNumByISBN(String str){
//		String isbnField = SysMetaDataUtils.queryMetadataFieldByFieldEnName(
//				CommonConstants.ISBN, 21);
//		String[] isbn = queryISBNById(str);
//		String isbnArrayToString = "";
//		for(String isbns:isbn){
//			isbnArrayToString += "'"+isbns+"',";
//		}
//		isbnArrayToString = isbnArrayToString.substring(0, isbnArrayToString.length()-1);
//		//System.out.println(isbnField+"             ++++++++++++++++++++++++++++++++++");
//    	//String sql = "SELECT srd.str004 ISBN,COUNT(srd.str004) ISBNNum FROM ores_primitive op INNER JOIN sys_res_directory srd ON op.sysResDirectoryId=srd.id and srd.str004 is not null and srd.str004!='' GROUP BY srd.str004";
//		//String sql = "SELECT srd.str004 ISBN,COUNT(srd.str004) ISBNNum FROM ores_primitive op INNER JOIN sys_res_directory srd ON op.sysResDirectoryId=srd.id AND  srd.str004 IS NOT NULL AND op.sysMetadataTypeId=21 AND srd.sysMetadataTypeId=21 GROUP BY srd.str004";
//		String sql = "SELECT srd."+isbnField+" ISBN,COUNT(srd."+isbnField+") ISBNNum FROM ores_primitive op INNER JOIN " +
//				"sys_res_directory srd ON op.sysResDirectoryId=srd.id AND  srd."+isbnField+" IS NOT NULL AND op.sysMetadataTypeId=21 " +
//						"AND srd.sysMetadataTypeId=21 AND srd."+isbnField+" in("+isbnArrayToString+") GROUP BY srd."+isbnField;
//		Map<String, Integer> map = new HashMap<String, Integer>();
//    	List list = baseJdbcDao.queryForList(sql);
//    	for(int count=0;count<list.size();count++){
//    	    Map maps = (Map) list.get(count);
//    	    String key = (String) maps.get("ISBN");
//    	    System.out.print(key+"     ");
//    	    Integer value = Integer.valueOf((maps.get("ISBNNum").toString()));
//    	    System.out.print(value);
//    	    System.out.println();
//    		map.put(key, value);
//    	}
//    	return map;
//    }
//	
//	/**
//	 * 根据id号查询出对应的ISBN
//	 */
//	public static String[] queryISBNById(String id){
//		String isbnField = SysMetaDataUtils.queryMetadataFieldByFieldEnName(
//				CommonConstants.ISBN, 21);
//		String hql = "select distinct srd."+isbnField+" from SysResDirectory srd where srd.id in("+id+")";
//		List<String> lists = baseQueryService.query(hql);
//		String[] str = new String[lists.size()];
//		for(int i=0;i<lists.size();i++){
//			str[i] = lists.get(i);
//		}
//		return str;
//	}
//	/**
//	 * 根据ISBN和id查出每种ISBN所对应的记录
//	 * @param list
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static Map<String,List<String[]>> querySysResDirectoryByISBN(Map<String, String> exportFields,String[] isbn,String id){
//		String isbnField = SysMetaDataUtils.queryMetadataFieldByFieldEnName(
//				CommonConstants.EXCEL_ISBN, 21);
////		if(!(exportFields.containsKey("bookName")&&exportFields.containsKey("sysMetadataType.typeName"))){
////			exportFields.put("bookName", "书名");
////			exportFields.put("sysMetadataType.typeName", "资源类型");
////		}
//		Map<String, List<String[]>> hmap =  new HashMap<String, List<String[]>>();
//		//根据ISBN的值查询出改值对应的所有记录
//		// TODO Auto-generated method stub
//		//改写成查询出来的
////		if(exportFields.size()==0){
////			
////			List<String[]> strList = new ArrayList<String[]>();
////			for(int i=0;i<1;i++){
////				String[] s = new String[2];
////				s[0] = "资源类型";
////				s[1] = "书名";
////				strList.add(s);
////			}
////			
////		}
//		//else{
//		for(int j=0;j<isbn.length;j++){
//			//String hql = "from SysResDirectory where str004='"+isbn[j]+"'";
//			
//			String op = "from OresPrimitive op where op.sysMetadataType=21";
//			
//			//";
//			//String sql = "SELECT s.* FROM sys_res_directory s WHERE s.id IN (SELECT sysResDirectoryId FROM ores_primitive  op WHERE op.sysMetadataTypeId=21 ) AND s.sysMetadataTypeId=21 AND s.str004 IS NOT NULL and s.str004='";
//			//List<SysResDirectory> lists = baseQueryService.query(hql);
//			//List<SysResDirectory> lists = new ArrayList<SysResDirectory>();
//			//List<SysResDirectory> sysResDirectoryList = lists;
////			List<OresPrimitive> lits = baseQueryService.query(op);
////			String so = "";
////			for(OresPrimitive orp:lits){
////				so += orp.getSysResDirectory().getId().toString()+",";
////			}
////			so = so.substring(0, so.length()-1);
//			//String newWords = changeWords(so);
//			String newWords = id;
//			//String hql = "from SysResDirectory s where s.id in("+newWords+")"+" and s.sysMetadataType.sysMetadataTypeId=21 and s.str004 is not null and s.str004='"+isbn[j]+"'";
//			String hql = "from SysResDirectory s where s.id in("+newWords+")"+" and s.sysMetadataType.sysMetadataTypeId=21 and s."+isbnField +" is not null and s."+isbnField+"='"+isbn[j]+"'";
//			List<SysResDirectory> sysResDirectoryList = baseQueryService.query(hql);
////			for(SysResDirectory srd:sysResDirectoryList){
////				System.out.println(srd.getBookName()+"         bookName");
////			}
//			//List<SysResDirectory> sysResDirectoryList = baseJdbcDao.queryForList(sql, isbn[j]);
//			List<String[]> li = new ArrayList<String[]>();
//			Collection col = sysResDirectoryList;
//			//String[] strArray = {};
//			Iterator<SysResDirectory> it = col.iterator();
//			SysResDirectory t = (SysResDirectory)it.next();
//			Set<String> set = exportFields.keySet();
//			for(int i=0;i<sysResDirectoryList.size();i++){
//				String[] s = new String[set.size()+2];
//				if(set.size()==0){
//					try {
//						s[0] = BeanUtils.getProperty(sysResDirectoryList.get(i), "sysMetadataType.typeName");
//					} catch (IllegalAccessException e) {
//						e.printStackTrace();
//					} catch (InvocationTargetException e) {
//						e.printStackTrace();
//					} catch (NoSuchMethodException e) {
//						e.printStackTrace();
//					}
//					s[1] = sysResDirectoryList.get(i).getBookName();
//					li.add(s);
//				}else{
//					
//					Iterator its = set.iterator();
//					
//					int flagCount = 2;
//					while(its.hasNext()){
//						try {
//							//Object fieldValue = BeanUtils.getProperty(sysResDirectoryList.get(i), "str004");
//							Method method1 = t.getClass().getMethod("getBookName", new Class[] {});
//							Object value1 = method1.invoke(t, new Object[] {});
//							s[0] = BeanUtils.getProperty(sysResDirectoryList.get(i), "sysMetadataType.typeName");
//							s[1] = sysResDirectoryList.get(i).getBookName();
//						} catch (SecurityException e1) {
//							e1.printStackTrace();
//						} catch (IllegalArgumentException e1) {
//							e1.printStackTrace();
//						} catch (NoSuchMethodException e1) {
//							e1.printStackTrace();
//						} catch (IllegalAccessException e1) {
//							e1.printStackTrace();
//						} catch (InvocationTargetException e1) {
//							e1.printStackTrace();
//						}
//						String fieldName = (String) its.next();
//						String getMethodName = "get"+ fieldName.substring(0, 1).toUpperCase()+ fieldName.substring(1);
//						try {
//							Method method = t.getClass().getMethod(getMethodName, new Class[] {});
//							try {
//								Object value= method.invoke(t, new Object[] {});
//								//s[flagCount] = (String) value;
//								s[flagCount] = BeanUtils.getProperty(sysResDirectoryList.get(i), fieldName);
//							} catch (IllegalArgumentException e) {
//								e.printStackTrace();
//							} catch (IllegalAccessException e) {
//								e.printStackTrace();
//							} catch (InvocationTargetException e) {
//								e.printStackTrace();
//							}
//						} catch (SecurityException e) {
//							e.printStackTrace();
//						} catch (NoSuchMethodException e) {
//							e.printStackTrace();
//						}
//						flagCount++;
//					}
//					li.add(s);
//					
//				}
//			}
//			
//			hmap.put(isbn[j], li);
//		}//}
//		return hmap;
//	}
//	
//	public static String changeWords(String str){
//		String newStr = "";
//		String[] arr = str.split(",");
//		for(String s:arr){
//			newStr += "'"+s+"'"+", ";
//		}
//		return newStr.substring(0,newStr.length()-2);
//	}
//	//test
//	public static void main(String[] args){
////		SysMetaDataUtils m = new SysMetaDataUtils();
////		queryMetadataData("str020", "到的", 21,"2323");
//	}
	
}
