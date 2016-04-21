package com.brainsoon.resource.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.image.ImageUtils;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.FilePathUtil;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.dofile.zip.RarUtil;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CaList;
import com.brainsoon.semantic.ontology.model.CopyRightMetaData;
import com.brainsoon.semantic.ontology.model.DoFileQueue;
import com.brainsoon.semantic.ontology.model.DoFileQueueList;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.Company;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.model.Staff;
import com.brainsoon.system.service.ICompanyService;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.IFLTXService;
import com.brainsoon.system.service.IStaffService;
import com.brainsoon.system.util.FieldValidator;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;

/**
 * 
 * @author
 *
 */
public class ResUtils {
	private static final Logger logger = Logger.getLogger(ResUtils.class);
	private final static String PUBLISH_REPEAT_URL = WebappConfigUtil
			.getParameter("PUBLISH_REPEAT_URL");
	public  static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
	public  static final String CONVER_FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirCFR(),"\\", "/");
	public final static String FILE_TEMP = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp);
	private static final String pictureFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.pictureFormat);

	private static final String videoFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.videoFormat);

	private static final String audioFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.audioFormat);

	private static final String animaFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.animaFormat);

	private static final String documentFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.documentFormat);
	/**
	 * Description: 查重
	 * 
	 * @return
	 */
	public static CaList checkRepeat(Map<String, String> map,
			String publishType,String resVersion) {
		JSONObject jo = new JSONObject();
		String paraJson = "";
		logger.info("----------------输出查重MAP大小"+map.size()+"--------------");
		if(map.size()>0){
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			if(pairs.getKey()!=null && pairs.getValue()!=null){
				String value = solrCodeModify(pairs.getValue().toString());
				jo.put(pairs.getKey(), value);
			}
			logger.info("----------------输出查重字段"+pairs.getKey()+"---"+pairs.getValue()+"--------------");
		}
		paraJson = jo.toString();
		}
		logger.info("----------------查询传值json"+paraJson+"-----------------");
		if(!"".equals(paraJson)){
		try {
			paraJson = URLEncoder.encode(paraJson, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		HttpClientUtil http = new HttpClientUtil();
		Gson gson = new Gson();
		CaList caList = null;
		if(!"".equals(paraJson)){
		String result = http.executeGet(PUBLISH_REPEAT_URL + "?checkRepeat="
				+ paraJson + "&publish_type=" + publishType+"&resVersion="+resVersion);
		logger.info("----------------输出result"+result+"-----------------");
			caList = (CaList) gson.fromJson(result, CaList.class);
		}else{
			caList=null;
		}
		logger.info("----------------输出查询结果集合caList-----------------");
		return caList;
	}
	/**
	 * Description: 根据map返回JSON串
	 * 
	 * @return
	 */
	public static String returnJson(Map<String, String> map) {
		JSONObject jo = new JSONObject();
		String paraJson = "";
		if(map.size()>0){
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			jo.put(pairs.getKey(), pairs.getValue());
		}
		paraJson = jo.toString();
		}
		return paraJson;
	}
	/**
	 * Description: 别名
	 * 
	 * @return
	 */
	public static String CreateAliasName() {
		
		String aliasName = java.util.UUID.randomUUID().toString();
		
		return aliasName;
	}
	/**
	 * 处理转换参数
	 * 
	 * @return
	 */
	public static DoFileQueueList converPath(List<com.brainsoon.semantic.ontology.model.File> realFiles,String objectId) {
		DoFileQueueList doFileList =new DoFileQueueList();
    	if(realFiles!=null && realFiles.size()>0){
    		for(com.brainsoon.semantic.ontology.model.File file:realFiles){
    			if("2".equals(file.getIsDir())){
    				DoFileQueue doFile= new DoFileQueue();
    				String fileId =  file.getId();
    				doFile.setFileId(fileId);
    				
    				String fileFormat = file.getFileType();
    				boolean type = false;
    				
    				
    				if (!DoFileUtils.checkArrContainsSoStr(documentFormat,fileFormat) 
    						&& !DoFileUtils.checkArrContainsSoStr(videoFormat,fileFormat) 
    						&& !DoFileUtils.checkArrContainsSoStr(audioFormat,fileFormat)
    						//&& !DoFileUtils.checkArrContainsSoStr(pictureFormat,fileFormat)
    						&& !DoFileUtils.checkArrContainsSoStr(animaFormat,fileFormat)) {
    					continue;//当不在上述类型之列时，不转换
    				} 
    				
    				String fileType[] = {"flv","mp3","mp4","swf"};
    				for(int i=0;i<fileType.length;i++){
    					if(fileType[i].indexOf(fileFormat)!=-1){
    						type = true;
    					}
    				}
    				
    				if(!type){
						doFile.setFileFormat(fileFormat);
						
						String srcTempPath = FILE_ROOT+file.getPath();
						srcTempPath = srcTempPath.replaceAll("\\\\", "/");
						doFile.setSrcPath(srcTempPath);
						
						String converPath = "";
						String fileObjectId = file.getObjectId().substring(4);//截取掉urn:
						if (DoFileUtils.checkArrContainsSoStr(PropertiesReader.getInstance().getProperty(ConstantsDef.documentFormat),fileFormat)) {
							converPath = CONVER_FILE_ROOT + FilePathUtil.getFileUUIDPath(file.getPath()) +fileObjectId+"/";
						}else {
							converPath = CONVER_FILE_ROOT + FilePathUtil.getFileUUIDPath(file.getPath()) +fileObjectId+"."+file.getFileType();
						}
						
						doFile = DoFileUtils.getConverPath(converPath, file.getFileType(), doFile);
						doFile.setResId(objectId);
						doFile.setObjectId(file.getObjectId());
						doFile.setPendingType("0");
						doFileList.addDoFileQueue(doFile);
    				}
    			}
    		}
    	}
		return doFileList;
	}
	/**
	 * 将javaBean转换成map
	 * @param obj
	 * @return
	 */
	 public static Map ConvertObjToMap(Object obj){
		  Map<String,Object> reMap = new HashMap<String,Object>();
		  if (obj == null) 
		   return null;
		  Field[] fields = obj.getClass().getDeclaredFields();
		  try {
		   for(int i=0;i<fields.length;i++){
		    try {
		     Field f = obj.getClass().getDeclaredField(fields[i].getName());
		     f.setAccessible(true);
		           Object o = f.get(obj);
		           reMap.put(fields[i].getName(), o);
		    } catch (NoSuchFieldException e) {
		     // TODO Auto-generated catch block
		     e.printStackTrace();
		    } catch (IllegalArgumentException e) {
		     // TODO Auto-generated catch block
		     e.printStackTrace();
		    } catch (IllegalAccessException e) {
		     // TODO Auto-generated catch block
		     e.printStackTrace();
		    }
		   }
		  } catch (SecurityException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  } 
		  return reMap;
		 }
		/**
		 * Description: 获得CA
		 * 
		 * @return
		 */
		public static Ca returnCa(String objectId) {
			Ca ca =null;
			HttpClientUtil http = new HttpClientUtil();
			Gson gson = new Gson();
			String resourceDetail = http.executeGet(WebappConfigUtil
					.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
			ca = gson.fromJson(resourceDetail, Ca.class);
				
			return ca;
		}
		
		
		/**
		 * jiaoyan
		 * @param ca
		 * @param name
		 * @param currentData
		 * @param metadataDefinitions
		 * @return
		 * @throws Exception 
		 */
		public static Ca doCheckAndSetValue(Ca ca,String name,String currentData,Map<String,MetadataDefinition> metadataDefinitions,int filetype) throws Exception{
			IFLTXService fLTXService = (IFLTXService)BeanFactoryUtil.getBean("FLTXService");
			IDictNameService dictService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
			IStaffService staffService=(IStaffService)BeanFactoryUtil.getBean("staffService");
			ICompanyService companyService = (ICompanyService)BeanFactoryUtil.getBean("companyService");
			//查重字段
			String inputDictValue = "";
			String errorStr = "";
			MetadataDefinition metadataDefinition = MetadataSupport.getMetadataDefinitionByName(name);
			
			if(metadataDefinition!=null){
				
				if(StringUtils.isBlank(currentData)&& StringUtils.isNotBlank(name)){
					if(StringUtils.isNotBlank(metadataDefinition.getDefaultValue())){
						currentData = metadataDefinition.getDefaultValue();
					}
				}
				
				// fieldType 1单文本 5文本域 4单选 3多选 2下拉选择 7日期 8URL 6树形 11单位 10人员
				//输入类型为树形 需要转换
				if(metadataDefinition!=null && metadataDefinition.getFieldType()==6){
					if(currentData!=null && !"".equals(currentData)){
						if(StringUtils.isNotBlank(metadataDefinition.getValueRange())){
						String graph = fLTXService.getFLTXNodeByCode(Long.parseLong(metadataDefinition.getValueRange()), currentData);
							if("0".equals(graph) && metadataDefinition.getAllowNull() == 0){
								inputDictValue = metadataDefinition.getFieldZhName() +":"+currentData;
							}else{
								ca.getMetadataMap().put(name, graph.trim());
							}
						}
					}
				
				//输入类型为 4单选或  2下拉选择  数据转换
				}else if(metadataDefinition!=null && (metadataDefinition.getFieldType()==4 || metadataDefinition.getFieldType()==2)){
					logger.info("key:"+name+"value:"+currentData);
					 if(StringUtils.isNotBlank(currentData) && StringUtils.isNotBlank(name)){
						 String arr[] = currentData.split(",");
						 if (arr.length != 1) {
							 errorStr = "["+metadataDefinition.getFieldZhName()+"]输入类型为单选或下拉选择,只能输入一个选项 ;";
						 }else {
							String impt = GlobalDataCacheMap.getChildCodeByIdAndChildValue(metadataDefinition.getValueRange(), arr[0]);
							if(StringUtils.isBlank(impt)){
								impt = GlobalDataCacheMap.getNameValueWithIdByKeyAndChildValue(metadataDefinition.getValueRange(), arr[0]);
							}
							if(impt.endsWith(",")){
								impt = impt.substring(0,impt.length()-1);
							}
							if(StringUtils.isBlank(impt)){
								inputDictValue = metadataDefinition.getFieldZhName() +":"+arr[0];
							}else{
								ca.getMetadataMap().put(name, impt.trim());
							}
						}
					 }
					
				//输入类型为3多选 数据转换
				}else if(metadataDefinition!=null && metadataDefinition.getFieldType()==3){
					logger.info("key:"+name+"value:"+currentData);
					String code = "";
					boolean zhName = false;
					if(StringUtils.isNotBlank(currentData) && StringUtils.isNotBlank(name)){
						String arr[] = currentData.split(",");
						if (arr.length > Integer.parseInt(metadataDefinition.getValueLength())) {
							errorStr = "["+metadataDefinition.getFieldZhName()+"]输入类型为多选,最多只能输入"+metadataDefinition.getValueLength()+"个选项 ;";
						}else {
							 for(int i=0;i<arr.length;i++){
								String impt = GlobalDataCacheMap.getChildCodeByIdAndChildValue(metadataDefinition.getValueRange(), arr[0]);
								if(StringUtils.isBlank(impt)){
									impt = GlobalDataCacheMap.getNameValueWithIdByKeyAndChildValue(metadataDefinition.getValueRange(), arr[0]);
								}
								if(StringUtils.isNotBlank(impt)){
										code = code + impt+",";
								}else{
									inputDictValue = inputDictValue+arr[i]+",";
									zhName = true;
								}
							}
							if(zhName==true&&StringUtils.isNotBlank(inputDictValue)){
								inputDictValue = metadataDefinition.getFieldZhName() +":"+inputDictValue;
							}
						}
						if(code.endsWith(","))
							 code = code.substring(0,code.length()-1);
						if(inputDictValue.endsWith(","))
							inputDictValue = inputDictValue.substring(0,inputDictValue.length()-1);
						if(StringUtils.isBlank(inputDictValue)){
							ca.getMetadataMap().put(name, code.trim());
						}
					}
					
				//输入类型为10人员 数据转换
				}else if(metadataDefinition!=null && metadataDefinition.getFieldType()==10 && filetype==1){
					logger.info("key:"+name+"value:"+currentData);
					String staffId="";
					if(currentData!=null && !"".equals(currentData)){
						String arr[] = currentData.split(",");
						if (arr.length > Integer.parseInt(metadataDefinition.getValueLength())) {
							errorStr = "["+metadataDefinition.getFieldZhName()+"]输入类型为人员,最多只能输入"+metadataDefinition.getValueLength()+"个选项 ;";
						}else {
							 for(int i=0;i<arr.length;i++){
								 Staff staff = new Staff();
				            	staff.setName(arr[i]);
				            	String id = staffService.doSaveOrUpdate(staff);
				            	if(StringUtils.isNotBlank(id)){
				            		staffId = staffId + id + ",";
								}
							 }
							 if(staffId.endsWith(","))
								 staffId = staffId.substring(0,staffId.length()-1);
							 if(StringUtils.isNotBlank(staffId)){
								 ca.getMetadataMap().put(name, staffId.trim());
							 }else {
								 errorStr = "["+metadataDefinition.getFieldZhName()+"]输入类型为人员,请确认输入数值的准确性;";
							}
							 
						}
					}
				//输入类型为11单位数据转换
				}else if(metadataDefinition!=null && metadataDefinition.getFieldType()==11 && filetype==1){
					logger.info("key:"+name+"value:"+currentData);
					String companyId="";
					if(currentData!=null && !"".equals(currentData)){
						String arr[] = currentData.split(",");
						if (arr.length > Integer.parseInt(metadataDefinition.getValueLength())) {
							errorStr = "["+metadataDefinition.getFieldZhName()+"]输入类型为单位,最多只能输入"+metadataDefinition.getValueLength()+"个选项 ;";
						}else {
							 for(int i=0;i<arr.length;i++){
								 Company company = new Company();
				            	company.setName(arr[i]);
				            	String id  = companyService.doSaveOrUpdate(company);
				            	if(StringUtils.isNotBlank(id)){
				            		companyId = companyId + id + ",";
								}
							 }
							 if(companyId.endsWith(","))
								 companyId = companyId.substring(0,companyId.length()-1);
							 if(StringUtils.isNotBlank(companyId)){
								 ca.getMetadataMap().put(name, companyId.trim());
							 }else {
								 errorStr = "["+metadataDefinition.getFieldZhName()+"]输入类型为单位,请确认输入数值的准确性;";
							}
							 
						}
					}
				/*}else if(metadataDefinition!=null && metadataDefinition.getFieldType()==9){
					 String code = "";
					 boolean zhName = false;
					 if(StringUtils.isNotBlank(currentData) && StringUtils.isNotBlank(name)){
									if(StringUtils.isNotBlank(currentData)){
										String arr[] = currentData.split(",");
										for(int i=0;i<arr.length;i++){
											String impt = GlobalDataCacheMap.getChildCodeByIdAndChildValue(metadataDefinition.getValueRange(), arr[i]);
											if(StringUtils.isBlank(impt)){
												impt = GlobalDataCacheMap.getNameValueWithIdByKeyAndChildValue(metadataDefinition.getValueRange(), arr[i]);
											}
											if(StringUtils.isNotBlank(impt)){
												code = code + impt+",";
											}else{
												inputDictValue = inputDictValue+arr[i]+",";
												zhName = true;
											}
										}
										if(zhName==true&&StringUtils.isNotBlank(inputDictValue)){
											inputDictValue = metadataDefinition.getFieldZhName() +":"+inputDictValue;
										}
									}
								logger.info("----------------判断数据字典是否符合规范--------------");
						 if(code.endsWith(","))
							 code = code.substring(0,code.length()-1);
						 if(inputDictValue.endsWith(","))
							 inputDictValue = inputDictValue.substring(0,inputDictValue.length()-1);
						 if(StringUtils.isBlank(inputDictValue)){
							 ca.getMetadataMap().put(name, code.trim());
						 }
					 }
					 */
				//处理资源特殊标识11-版本
				}else if(metadataDefinition!=null && metadataDefinition.getIdentifier()==11){
					if(StringUtils.isNotBlank(metadataDefinition.getDefaultValue())){
						ca.getMetadataMap().put(name, metadataDefinition.getDefaultValue());
					}else{
						ca.getMetadataMap().put(name, "00");
					}
					 
				}else if(metadataDefinition.getIdentifier()==12 || metadataDefinition.getIdentifier()==6){
					int num = ca.getNum();
					num++;
					ca.setNum(num);
					ca.getMetadataMap().put(name, currentData);
				}else if(StringUtils.isNotBlank(currentData)&& StringUtils.isNotBlank(name)){
					ca.getMetadataMap().put(name, currentData);
					
				}
			}else{
				if(StringUtils.isNotBlank(currentData)&& StringUtils.isNotBlank(name)){
					ca.getMetadataMap().put(name, currentData.trim());
				}
			}
			
			
			
			
			//验证字段是否符合规范
			 if(StringUtils.isNotBlank(inputDictValue)){
				 errorStr = "输入值【"+inputDictValue+"】不符合数据字典规范";
			 }
			 /*if(ca.getNum()>1){
				 errorStr =errorStr + "生成doi的[ISBN]或[民国图书编号]只能选择一项";
			 }*/
			 if(StringUtils.isNotBlank(name)){
				 errorStr =errorStr + FieldValidator.checkFieldHasError(metadataDefinitions, name, currentData);
			 }
			if ("date".equals(name) || "publisherDate".equals(name) || "authorEndTime".equals(name) || "authorStartTime".equals(name)) {
				if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(currentData)) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Date date = df.parse(currentData);
					ca.getMetadataMap().put(name, date.getTime()+"");
				}
			}
			 
			 if(StringUtils.isNotBlank(errorStr)){
				 throw new ServiceException(errorStr);
			 }
			 return ca;
		}
		
		/**
		 * 返回版权元数据json
		 */
		public static JSONObject copyrigntMedata(String returnCopyRightMetaData){
			JSONObject obj = new JSONObject();
			JSONObject ob= (JSONObject) obj.fromObject(returnCopyRightMetaData);
//			JSONArray arr = JSONArray.fromObject("["+copyright+"]");
			JSONObject  data = (JSONObject)ob.get("data");
//			String jsonData = data.toString();
			Map<String,String> map = new HashMap<String,String>();
	 		for (Iterator<String> keys = data.keys(); keys.hasNext();){
	 			Object key1 = keys.next(); 
				 Object keyValue = data.get(key1);
				 if(key1!=null&&keyValue!=null){
					 map.put(key1.toString(), keyValue.toString());
				 }
			}
	 		//反射获得类属性字段
	 		Field[] fields = CopyRightMetaData.class.getDeclaredFields();
	 		JSONObject objData = new JSONObject();
	 		Iterator it = map.entrySet().iterator();
	 		while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				String key = pairs.getKey().toString();
				for(Field field:fields){
					if(StringUtils.isNotBlank(key)&&StringUtils.isNotBlank(pairs.getValue().toString())){
						if(field.getName().equals(key)){
							Object value = pairs.getValue();
							if(value.equals("[]")){
								value = null;
							}
							objData.put(pairs.getKey(),value);
							break;
						}
					}
				}
			}
			
			return objData;
		}
		
		/**
		 * 递归改名
		 * 
		 * @param paretFile
		 * @param parentItem
		 * @param items
		 * @return
		 * @throws IOException
		 */
		public static Ca getFileLists(String parentPath, File srcFile, String pid, Ca ca,
				int i, int j,int num,String fileDoi,boolean b) throws IOException {
			com.brainsoon.semantic.ontology.model.File realFile = null;
			if (srcFile.exists()) {
				String absPath = "";
				logger.info(srcFile.getAbsolutePath() + "====" + i + "========"
						+ pid);
				List<com.brainsoon.semantic.ontology.model.File> tempRealFiles = ca
						.getRealFiles();
				if (tempRealFiles == null) {
					tempRealFiles = new ArrayList<com.brainsoon.semantic.ontology.model.File>();
				}
				parentPath = parentPath.replaceAll("\\\\", "/");
				if (!parentPath.endsWith("/")) {
					parentPath += "/";
				}
				if (StringUtils.isNotBlank(pid) && !"-1".equals(pid)) {
					pid = pid.replaceAll("\\\\", "/");
					if (pid.endsWith("/")) {
						pid = pid.substring(0, pid.length() - 1);
					}
					absPath = parentPath + pid + "/";
				} else {
					absPath = parentPath;
				}
				String time = new Date().getTime() + "";
				File[] childFiles = srcFile.listFiles();
				if (childFiles != null && childFiles.length > 0) {
					for (File childFile : childFiles) {
						String fileName = childFile.getName();
						if (childFile.isDirectory()) {
							logger.info(srcFile.getAbsolutePath() + "====" + i + "========"
									+ pid);
							String tempPath = absPath + i + "" + j;
							FileUtils
									.forceMkdir(new File(tempPath + File.separator));
							tempPath = tempPath.replace(FILE_ROOT, "");
							realFile = new com.brainsoon.semantic.ontology.model.File();
							realFile.setPath(tempPath);
							realFile.setCreate_time(time);
							realFile.setIsDir("1");
							realFile.setName(fileName);
							realFile.setCreator(ca.getCreator());
							realFile.setCaId(ca.getObjectId());
							realFile.setAliasName(i + "" + j);
							realFile.setPid(pid);
							String tempPid = "";
							if ("-1".equals(pid)) {
								realFile.setId(i + "" + j);
								tempPid = i + "" + j;
							} else {
								realFile.setId(pid + "/" + i + "" + j);
								tempPid = pid + "/" + i + "" + j;
							}
							j++;
							int dirLevel = i + 1;
							tempRealFiles.add(realFile);
							ca.setRealFiles(tempRealFiles);
							getFileLists(parentPath, childFile, tempPid, ca,
									dirLevel, j,num++,fileDoi,b);
						} else {
							num++;
							String fileType = fileName.substring(
									fileName.lastIndexOf(".") + 1,
									fileName.length());
							String tempPath = absPath + i + "" + j + "." + fileType;
							FileUtils.copyFile(childFile, new File(tempPath));
							if(b){
								// 删除文件
								try {
									FileUtils.forceDelete(childFile.getAbsoluteFile());
								} catch (Exception e) {
									logger.error("删除文件失败"
											+ e.getMessage()
											+ childFile.getParentFile()
													.getAbsolutePath());
								}
							}
							tempPath = tempPath.replace(FILE_ROOT, "");
							realFile = new com.brainsoon.semantic.ontology.model.File();
							realFile.setAliasName(i + "" + j + "." + fileType);
							realFile.setName(fileName);
							realFile.setPath(tempPath);
							realFile.setFileType(fileType);
							realFile.setFileByte(new File(tempPath).length() + "");
							realFile.setCreate_time(time);
							realFile.setCreator(ca.getCreator());
							realFile.setMd5(MD5Util.getFileMD5String(new File(FILE_ROOT+tempPath)));
							realFile.setModified_time(time);
							realFile.setVersion("1");
							realFile.setIsDir("2");
							realFile.setCaId(ca.getObjectId());
							String serialNumber = "";
							String serialNumberTemp=  num+"";
							for(int n=0;n<4-serialNumberTemp.length();n++){
								serialNumber += "0";
							}
							serialNumber = serialNumber+serialNumberTemp;
							realFile.setIdentifier(fileDoi+"."+serialNumber);
							if ("-1".equals(pid)) {
								realFile.setId(i + "" + j + "." + fileType);
							} else {
								realFile.setId(pid + "/" + i + "" + j + "."
										+ fileType);
							}
							realFile.setPid(pid);
							j++;
							tempRealFiles.add(realFile);
							ca.setRealFiles(tempRealFiles);
						}
					}
				}
			}
			return ca;
		}
		/**
		 * 覆盖文件递归改名
		 * 
		 * @param paretFile
		 * @param parentItem
		 * @param items
		 * @return
		 * @throws IOException
		 */
			public static  Ca getOverFileLists(String parentPath, File srcFile, String pid,
					Ca ca, int i, Set<String> dirSet, Map<String, List<String>> md5Map,
					Map<String, String> dirMap,String srcPathName,int num,String fileDoi) throws IOException {
				com.brainsoon.semantic.ontology.model.File realFile = null;
				if (srcFile.exists()) {
					logger.info("-------------------覆盖文件，递归改名-----------------");
					String absPath = "";
					String srcPath = srcFile.getAbsolutePath();
					srcPath = srcPath.replaceAll("\\\\", "/") + "/";
					logger.info(srcFile.getAbsolutePath() + "====" + i + "========"
							+ pid);
					List<com.brainsoon.semantic.ontology.model.File> tempRealFiles = ca
							.getRealFiles();
					File[] childFiles = srcFile.listFiles();
					if(tempRealFiles!=null && tempRealFiles.size()>0){
						for(int u=0;u<tempRealFiles.size();u++){
							if(tempRealFiles.get(i).getIsDir().equals("2")){
								num ++;
							}
						}
					}
					if (tempRealFiles == null) {
						tempRealFiles = new ArrayList<com.brainsoon.semantic.ontology.model.File>();
					}
					parentPath = parentPath.replaceAll("\\\\", "/");
					if (!parentPath.endsWith("/")) {
						parentPath += "/";
					}
					if (StringUtils.isNotBlank(pid) && !"-1".equals(pid)) {
						pid = pid.replaceAll("\\\\", "/");
						if (pid.endsWith("/")) {
							pid = pid.substring(0, pid.length() - 1);
						}
						absPath = parentPath + pid + "/";
					} else {
						absPath = parentPath;
					}
					String time = new Date().getTime() + "";
					if (childFiles != null && childFiles.length > 0) {
						for (File childFile : childFiles) {
							String fileName = childFile.getName();
							String realPath = childFile.getAbsolutePath();
							realPath = realPath.replaceAll("\\\\", "/");
							String rootPath = ca.getRootPath();
							rootPath = rootPath.replaceAll("\\\\", "/");
							String relativePath = realPath.replace(srcPath, "");
							if(StringUtils.isNotBlank(srcPathName)){
								relativePath = realPath.replace(srcPathName+"/", "");
								if (relativePath.indexOf(".") > 0) {
									relativePath = relativePath.substring(0,relativePath.lastIndexOf("."));
								} 
							}
							if (childFile.isDirectory()) {
								int dirLevel = i + 1;
								if (dirSet.contains(relativePath)) {
									String Pid = "";
									for (Map.Entry<String, String> entry : dirMap
											.entrySet()) {
										String value = entry.getValue();
										if (relativePath.equals(value)) {
											Pid = entry.getKey();
											break;
										}
									}
									getOverFileLists(parentPath, childFile, Pid, ca,
											dirLevel, dirSet, md5Map, dirMap,srcPathName,num++,fileDoi);
									continue;
								}
								String j = createRandom(absPath, i + "", null);
								String tempPath = absPath + i + "" + j;
								FileUtils
										.forceMkdir(new File(tempPath + File.separator));
								tempPath = tempPath.replace(FILE_ROOT, "");
								realFile = new com.brainsoon.semantic.ontology.model.File();
								realFile.setPath(tempPath);
								realFile.setCreate_time(time);
								realFile.setIsDir("1");
								realFile.setName(fileName);
								realFile.setCreator(ca.getCreator());
								realFile.setCaId(ca.getObjectId());
								realFile.setAliasName(i + "" + j);
								realFile.setPid(pid);
								String tempPid = "";
								if ("-1".equals(pid)) {
									realFile.setId(i + "" + j);
									tempPid = i + "" + j;
								} else {
									realFile.setId(pid + "/" + i + "" + j);
									tempPid = pid + "/" + i + "" + j;
								}
								tempRealFiles.add(realFile);
								ca.setRealFiles(tempRealFiles);
								getOverFileLists(parentPath, childFile, tempPid, ca,
										dirLevel, dirSet, md5Map, dirMap,srcPathName,num,fileDoi);
							} else {
								num++;
								List<String> md5List = null;
								if (relativePath.indexOf("/") > 0) {
									String temp = relativePath.substring(0,
											relativePath.lastIndexOf("/"));
									md5List = md5Map.get(temp);
								} else {
									md5List = md5Map.get("-1");
								}

								String newMd5 = MD5Util.getFileMD5String(childFile);
								if (md5List != null && md5List.contains(newMd5)) {
									continue;
								}
								String fileType = fileName.substring(
										fileName.lastIndexOf(".") + 1,
										fileName.length());
								String j = createRandom(absPath, i + "", fileType);
								String tempPath = absPath + i + "" + j + "." + fileType;
								FileUtils.copyFile(childFile, new File(tempPath));
								tempPath = tempPath.replace(FILE_ROOT, "");
								realFile = new com.brainsoon.semantic.ontology.model.File();
								realFile.setAliasName(i + "" + j + "." + fileType);
								realFile.setName(fileName);
								realFile.setPath(tempPath);
								realFile.setFileType(fileType);
								realFile.setFileByte(childFile.length() + "");
								realFile.setCreate_time(time);
								realFile.setCreator(ca.getCreator());
								realFile.setMd5(newMd5);
								realFile.setModified_time(time);
								realFile.setVersion("1");
								realFile.setIsDir("2");
								realFile.setCaId(ca.getObjectId());
								String serialNumber = "";
								String serialNumberTemp=  num+"";
								for(int n=0;n<4-serialNumberTemp.length();n++){
									serialNumber += "0";
								}
								serialNumber = serialNumber+serialNumberTemp;
								realFile.setIdentifier(fileDoi+"."+serialNumber);
								
								if ("-1".equals(pid)) {
									realFile.setId(i + "" + j + "." + fileType);
								} else {
									realFile.setId(pid + "/" + i + "" + j + "."
											+ fileType);
								}
								realFile.setPid(pid);
								tempRealFiles.add(realFile);
								ca.setRealFiles(tempRealFiles);
							}
						}
					}
				}
				return ca;
			}
			public static  String createRandom(String path, String level, String fileType) {
				Random random = new Random();
				int value = random.nextInt(1000);
				if (StringUtils.isNotBlank(fileType)) {
					path = path + level + value + "." + fileType;
				} else {
					path = path + level + value;
				}
				File file = new File(path);
				if (file.exists()) {
					return createRandom(path, level, fileType);
				}
				return value + "";
			}
			
	/**
	 * solr通配符容错处理方法
	 * @param conditionValue
	 * @return
	 */
	public static String solrCodeModify(String conditionValue) {
		if(StringUtils.isNotBlank(conditionValue)){
			conditionValue = conditionValue.replaceAll("\"", "*");
			conditionValue = conditionValue.replaceAll("'", "*");
			conditionValue = conditionValue.replaceAll(" ", "*");
			conditionValue = conditionValue.replaceAll("　", "*");
			conditionValue = conditionValue.replaceAll("\\?", "*");	
			conditionValue = conditionValue.replaceAll("\\(", "*");	
			conditionValue = conditionValue.replaceAll("\\)", "*");
			conditionValue = conditionValue.replaceAll("\\（", "*");
			conditionValue = conditionValue.replaceAll("\\）", "*");
			conditionValue = conditionValue.replaceAll(":", "*");
		}
		return conditionValue;
	}
	/**
	 * Mysql通配符容错处理方法
	 * @param conditionValue
	 * @return
	 */
	public static String mysqlCodeModify(String conditionValue) {
		if(StringUtils.isNotBlank(conditionValue)){
			conditionValue = conditionValue.replaceAll("'", "\\\\");
			conditionValue = conditionValue.replaceAll("‘", "\\\\");
			conditionValue = conditionValue.replaceAll("’", "\\\\");
			conditionValue = conditionValue.replaceAll("“", "\\\\");
			conditionValue = conditionValue.replaceAll("”", "\\\\");
			conditionValue = conditionValue.replaceAll("、", "\\\\");
			conditionValue = conditionValue.replaceAll(" ", "\\\\");
		}else{
			return conditionValue;
		}
//		conditionValue = conditionValue.replaceAll("\\?", "\\\\");	
//		conditionValue = conditionValue.replaceAll("\\(", "\\\\");	
//		conditionValue = conditionValue.replaceAll("\\)", "\\\\");
//		conditionValue = conditionValue.replaceAll("\\（", "\\\\");
//		conditionValue = conditionValue.replaceAll("\\）", "\\\\");
//		conditionValue = conditionValue.replaceAll("\\?", "*");	
		return conditionValue;
	}
	/**
	 * 日期格式处理类
	 */
	public static Map<String, String> mapDate(Map<String, String> metadataMap,List<String> mapList){
		for(int i=0;i<mapList.size();i++){
			if(metadataMap.get(mapList.get(i))!=null){
				String dateValue = metadataMap.get(mapList.get(i));
				if(StringUtils.isNotBlank(dateValue)){
				if(dateValue.length()<8){
					dateValue = dateValue + "-01 00:00:00";
				}else if(dateValue.length()<11){
					dateValue = dateValue + " 00:00:00";
				}else if(dateValue.length()<14){
					dateValue = dateValue + ":00:00";
				}else if(dateValue.length()<17){
					dateValue = dateValue + ":00";
				}
				metadataMap.put(mapList.get(i), dateValue);
				}
			}
		}
		return metadataMap;
	}
	/**
	 * 日期格式处理类
	 */
	public static String  simpleDate(String date){
		if(StringUtils.isNotBlank(date)){
			if(date.length()<8){
				date = date + "-01 00:00:00";
			}else if(date.length()<11){
				date = date + " 00:00:00";
			}else if(date.length()<14){
				date = date + ":00:00";
			}else if(date.length()<17){
				date = date + ":00";
			}
		}
		return date;
	}

	/**
	 * 
	* @Title: getFileLists
	* @Description:递归 处理文件信息（批量导入中获取文件信息存入Ca中）
	* @param parentPath	要存入文件的路径  形如：/home/szgl/bsfw/fileDir/FileRoot/1/G2/a0a5bd47-0dbd-4da7-8309-17f313588489/
	* @param srcFile	文件原路径	形如：C:/工作/项目/测试数据/资源导入/红烧醋鱼
	* @param ca	Ca
	* @param pid	父节点ID 形如：这是测试数据/other/Hibernate中文参考文档 V3.2.chm	  pid为：这是测试数据/other/
	* @param fileDoi	DOI
	* @param isDelete true:删除 false：不删除
	* @return
	* @throws IOException    参数
	* @return Ca    返回类型
	* @throws
	 */
	public static Ca getFileLists(String parentPath, File srcFile, String pid, Ca ca,String fileDoi,boolean isDelete) throws IOException {
		com.brainsoon.semantic.ontology.model.File realFile = null;
		if (srcFile.exists()) {
			String absPath = "";//当前的路径
			logger.info("【ResUtils】 获取文件getFileLists->>>开始  该资源的原路径："+srcFile.getAbsolutePath()+" 资源路径"+parentPath);
			List<com.brainsoon.semantic.ontology.model.File> tempRealFiles = ca.getRealFiles();
			if (tempRealFiles == null) {
				tempRealFiles = new ArrayList<com.brainsoon.semantic.ontology.model.File>();
			}
			parentPath = parentPath.replaceAll("\\\\", "/");
			if (!parentPath.endsWith("/")) {
				parentPath += "/";
			}
			if (StringUtils.isNotBlank(pid) && !"-1".equals(pid)) {
				pid = pid.replaceAll("\\\\", "/");
				if (pid.endsWith("/")) {
					pid = pid.substring(0, pid.length() - 1);
				}
				absPath = parentPath + pid + "/";
			} else {
				absPath = parentPath;
			}
			String time = new Date().getTime() + "";
			File[] childFiles = srcFile.listFiles();
			
			
			if ("-1".equals(pid)) {
				//-------------------------处理资源目录逻辑
				String srcFilePath = srcFile.getAbsolutePath().replaceAll("\\\\", "/").replaceAll("//", "/");
				if (srcFilePath.endsWith("/")) {
					srcFilePath = srcFilePath.substring(0,srcFilePath.length()-1);
				}
				//资源目录的名称
				String resDirName = srcFilePath.substring(srcFilePath.lastIndexOf("/")+1);
				String targePath = absPath+resDirName;//是资源目录的路径
				FileUtils.forceMkdir(new File(targePath + File.separator));
				targePath = targePath.replace(FILE_ROOT, "");
				realFile = new com.brainsoon.semantic.ontology.model.File();
				realFile.setPath(targePath);
				realFile.setCreate_time(time);
				realFile.setIsDir("1");
				realFile.setName(resDirName);
				realFile.setCreator(ca.getCreator());
				realFile.setCaId(ca.getObjectId());
				realFile.setAliasName(resDirName);
				realFile.setPid(pid);
				realFile.setId(resDirName+"/");
				tempRealFiles.add(realFile);
				ca.setRealFiles(tempRealFiles);
				getFileLists(parentPath, srcFile, resDirName, ca,fileDoi,isDelete);
			}else {
				if (childFiles != null && childFiles.length > 0) {
					for (File childFile : childFiles) {
						String fileName = childFile.getName();
						if (childFile.isDirectory()) {
//							String tempPath = absPath + i + "" + j;
							String tempPath = absPath + fileName;
							logger.info("【ResUtils】 获取文件getFileLists->>>该【文件夹】当前的路径（绝对） ："+tempPath);
							FileUtils.forceMkdir(new File(tempPath + File.separator));
							tempPath = tempPath.replace(FILE_ROOT, "");
							realFile = new com.brainsoon.semantic.ontology.model.File();
							realFile.setPath(tempPath);
							realFile.setCreate_time(time);
							realFile.setIsDir("1");
							realFile.setName(fileName);
							realFile.setCreator(ca.getCreator());
							realFile.setCaId(ca.getObjectId());
							realFile.setAliasName(fileName);
							String tempPid = "";
							if ("-1".equals(pid)) {
								realFile.setPid(pid);
								realFile.setId(fileName+"/");
								tempPid = fileName;
							} else {
								if(!pid.endsWith("/")){
									pid = pid+"/";
								}
								if(!fileName.endsWith("/")){
									fileName = fileName+"/";
								}
								realFile.setPid(pid);
								realFile.setId(pid+fileName);
								tempPid = pid + fileName;
							}
							tempRealFiles.add(realFile);
							ca.setRealFiles(tempRealFiles);
							getFileLists(parentPath, childFile, tempPid, ca,fileDoi,isDelete);
						} else {
							if ("Thumbs.db".equals(fileName)) {
								continue;
							}
							String fileType = fileName.substring(fileName.lastIndexOf(".") + 1,fileName.length());
							String tempPath = absPath + fileName;
							logger.info("【ResUtils】 获取文件getFileLists->>>该【文件】当前的路径（绝对） ："+tempPath);
							FileUtils.copyFile(childFile, new File(tempPath));
							if(isDelete){
								// 删除文件
								try {
									FileUtils.forceDelete(childFile.getAbsoluteFile());
								} catch (Exception e) {
									logger.error("删除文件失败"
											+ e.getMessage()
											+ childFile.getParentFile()
													.getAbsolutePath());
								}
							}
							tempPath = tempPath.replace(FILE_ROOT, "");
							realFile = new com.brainsoon.semantic.ontology.model.File();
							realFile.setAliasName(fileName);
							realFile.setName(fileName);
							realFile.setPath(tempPath);
							realFile.setFileType(fileType);
							realFile.setFileByte(new File(tempPath).length() + "");
							realFile.setCreate_time(time);
							realFile.setCreator(ca.getCreator());
							realFile.setMd5(MD5Util.getFileMD5String(new File(absPath + fileName)));
							realFile.setModified_time(time);
							realFile.setVersion("1");
							realFile.setIsDir("2");
							realFile.setCaId(ca.getObjectId());
							if ("-1".equals(pid)) {
								realFile.setId(fileName);
							} else {
								if(!pid.endsWith("/")){
									pid = pid +"/";
								}
								realFile.setId(pid + fileName);
							}
							if ("cover.jpg".equals(fileName)) {
								ca.getMetadataMap().put("cover", tempPath.replaceAll("\\\\", "/").replaceAll("//", "/"));
							}
							realFile.setPid(pid);
							tempRealFiles.add(realFile);
							ca.setRealFiles(tempRealFiles);
						}
					}
				}
			}
		}
		return ca;
	}
	
	/**
	 * 
	* @Title: getOverFileLists
	* @Description:递归 处理文件信息--覆盖方式（批量导入中获取文件信息存入Ca中）   
	* @param parentPath	要存入文件的路径  形如：/home/szgl/bsfw/fileDir/FileRoot/1/G2/a0a5bd47-0dbd-4da7-8309-17f313588489/
	* @param srcFile	文件原路径	形如：C:/工作/项目/测试数据/资源导入/红烧醋鱼
	* @param ca	Ca
	* @param fileDoi	DOI
	* @param pid	父节点ID 形如：这是测试数据/other/Hibernate中文参考文档 V3.2.chm	  pid为：这是测试数据/other/
	* @param dirSet
	* @param md5Map
	* @param srcPathName
	* @param fileDoi
	* @return
	* @throws IOException    参数
	* @return Ca    返回类型
	* @throws
	 */
	public static  Ca getOverFileLists(String parentPath, File srcFile, String pid,Ca ca, Set<String> dirSet, 
			Map<String, List<String>> md5Map,String srcPathName,String fileDoi) throws IOException {
		com.brainsoon.semantic.ontology.model.File realFile = null;
			if (srcFile.exists()) {
			String absPath = "";
			String srcPath = srcFile.getAbsolutePath();
			srcPath = srcPath.replaceAll("\\\\", "/") + "/";
			List<com.brainsoon.semantic.ontology.model.File> tempRealFiles = ca.getRealFiles();
			File[] childFiles = srcFile.listFiles();
			if (tempRealFiles == null) {
				tempRealFiles = new ArrayList<com.brainsoon.semantic.ontology.model.File>();
			}
			parentPath = parentPath.replaceAll("\\\\", "/");
			if (!parentPath.endsWith("/")) {
				parentPath += "/";
			}
			if (StringUtils.isNotBlank(pid) && !"-1".equals(pid)) {
				pid = pid.replaceAll("\\\\", "/");
				if (pid.endsWith("/")) {
					pid = pid.substring(0, pid.length() - 1);
				}
				absPath = parentPath + pid + "/";
			} else {
				absPath = parentPath;
			}
			logger.info("【ResUtils】 递归覆盖文件getOverFileLists->>>开始  该资源的原路径："+srcFile.getAbsolutePath()+" 目标资源路径"+absPath);
			String time = new Date().getTime() + "";
			if (childFiles != null && childFiles.length > 0) {
				for (File childFile : childFiles) {
					String fileName = childFile.getName();
					String realPath = childFile.getAbsolutePath();
					realPath = realPath.replaceAll("\\\\", "/");
					String rootPath = ca.getRootPath();
					rootPath = rootPath.replaceAll("\\\\", "/");
					String relativePath = realPath.replace(srcPath, "");
					if(StringUtils.isNotBlank(srcPathName)){
						relativePath = realPath.replace(srcPathName+"/", "");
						if (relativePath.indexOf(".") > 0) {
							relativePath = relativePath.substring(0,relativePath.lastIndexOf("."));
						} 
					}
					if (childFile.isDirectory()) {
						if (dirSet.contains(relativePath)) {
							String Pid = relativePath;
							getOverFileLists(parentPath, childFile, Pid, ca, dirSet, md5Map,srcPathName,fileDoi);
							logger.info("【ResUtils】getOverFileLists递归 处理文件信息--覆盖方式->>>dirSet中已有该目录:"+relativePath+"  跳过存储该目录");
							continue;
						}
						//String j = createRandom(absPath, i + "", null);
						String tempPath = absPath + fileName;
						logger.info("【ResUtils】getOverFileLists递归 处理文件信息--覆盖方式->>>存储到Ca 该目录:"+tempPath);
						FileUtils.forceMkdir(new File(tempPath + File.separator));
						tempPath = tempPath.replace(FILE_ROOT, "");
						realFile = new com.brainsoon.semantic.ontology.model.File();
						realFile.setPath(tempPath);
						realFile.setCreate_time(time);
						realFile.setIsDir("1");
						realFile.setName(fileName);
						realFile.setCreator(ca.getCreator());
						realFile.setCaId(ca.getObjectId());
						realFile.setAliasName(fileName);
						realFile.setPid(pid);
						String tempPid = "";
						if ("-1".equals(pid)) {
							realFile.setId(fileName);
							tempPid = fileName;
						} else {
							realFile.setId(pid + "/" + fileName);
							tempPid = pid + "/" + fileName;
						}
						tempRealFiles.add(realFile);
						ca.setRealFiles(tempRealFiles);
						getOverFileLists(parentPath, childFile, tempPid, ca, dirSet, md5Map,srcPathName,fileDoi);
					} else {
						List<String> md5List = null;
						if (relativePath.indexOf("/") > 0) {
							String temp = relativePath.substring(0,relativePath.lastIndexOf("/"));
							md5List = md5Map.get(temp);
						} else {
							md5List = md5Map.get("-1");
						}
						String newMd5 = MD5Util.getFileMD5String(childFile);
						if (md5List != null && md5List.contains(newMd5)) {
							logger.info("【ResUtils】getOverFileLists递归 处理文件信息--覆盖方式->>>md5List中已有该文件:"+fileName+"  跳过存储该文件");
							continue;
						}
						String fileType = fileName.substring(fileName.lastIndexOf(".") + 1,fileName.length());
						logger.info("【ResUtils】getOverFileLists递归 处理文件信息--覆盖方式->>>存储到Ca 该文件:"+fileName);
						//String j = createRandom(absPath, i + "", fileType);
						String tempPath = absPath + fileName;
						FileUtils.copyFile(childFile, new File(tempPath));
						tempPath = tempPath.replace(FILE_ROOT, "");
						realFile = new com.brainsoon.semantic.ontology.model.File();
						realFile.setAliasName(fileName);
						realFile.setName(fileName);
						realFile.setPath(tempPath);
						realFile.setFileType(fileType);
						realFile.setFileByte(childFile.length() + "");
						realFile.setCreate_time(time);
						realFile.setCreator(ca.getCreator());
						realFile.setMd5(newMd5);
						realFile.setModified_time(time);
						realFile.setVersion("1");
						realFile.setIsDir("2");
						realFile.setCaId(ca.getObjectId());
						
						if ("-1".equals(pid)) {
							realFile.setId(fileName);
						} else {
							realFile.setId(pid + "/" + fileName);
							if ("cover.jpg".equals(fileName)) {
								ca.getMetadataMap().put("cover", pid + File.separator + fileName);
							}
						}
						realFile.setPid(pid);
						tempRealFiles.add(realFile);
						ca.setRealFiles(tempRealFiles);
					}
				}
			}
		}
		return ca;
	}
	
	/**
	 * 
	* @Title: modifyCoverImage
	* @Description: 处理封面图片 将资源文件夹下的cover目录下的图片改名为cover.jpg   （在原路径下改名）
	* 	调用该方法的时候，将两个参数写一样就好了
	* 	modifyCoverImage(ResFile,ResFile)
	* @param ResFile 	该资源的路径	例如：C:\工作\项目\测试数据\资源导入\红烧肉
	* @param srcFile	当前要处理的路径（迭代处理要用的参数）	例如：C:\工作\项目\测试数据\资源导入\红烧肉\cover
	* @param parentPath 	C:/workhj/workspace/bsrcm_cciph/WebRoot/fileDir/viewer/1/G2/0024b9bf-1db6-4185-85fd-5f623481e57c/9787502042585
	* @throws IOException    参数
	* @return void    返回类型
	* @throws
	 */
	public static  void modifyCoverImage(File ResFile,File srcFile,String parentPath) throws IOException {
		String resName = ResFile.getName();
		String resPath = ResFile.getAbsolutePath().replaceAll("\\\\", "/");
		if (srcFile.exists()) {
			logger.info("【ResUtils】 修改cover封面图片 modifyCoverImage()->>>开始  该资源的原路径："+srcFile.getAbsolutePath());
			File[] childFiles = srcFile.listFiles();
			if (childFiles != null && childFiles.length > 0) {
				for (File childFile : childFiles) {
					if (childFile.isFile()) {
						String fileName = childFile.getName();
						String fileType = fileName.substring(fileName.lastIndexOf(".") + 1,fileName.length());
						String fileOldPath = childFile.getAbsolutePath();
						String dirPath = childFile.getParent();
						fileOldPath = fileOldPath.replaceAll("\\\\", "/");
						String oppositePath = fileOldPath.replace(resPath, "");
                    	
                    	//importCoverType=2 有cover目录但是没有cover.jpg文件  将cover目录下的第一个文件改名为cover.jpg
						if (oppositePath.contains("cover/") || oppositePath.contains("Cover/")) {
                			if ("jpg".equals(fileType) || "png".equals(fileType)) {
                				File file = new File(dirPath + File.separator + "cover.jpg");
                				File minFile = new File(parentPath.replace("fileRoot", "viewer") + File.separator +resName+ File.separator+"cover"+ File.separator +"cover_min.jpg");
								if (!file.exists()) {
									
									try {
										FileUtils.moveFile(childFile, file);
										logger.info("【ResUtils】  修改cover封面图片 modifyCoverImage()->>>封面原路径："+fileOldPath+" 改名后路径："+file.getAbsolutePath());
									} catch (Exception e) {
										e.printStackTrace();
										throw new ServiceException("cover封面改名出错 ！");
									}
									
									
									try {
										if (!minFile.getParentFile().exists()) {
											FileUtils.forceMkdir(minFile.getParentFile());
										}
										ImageUtils.zoomImg(file.getAbsolutePath(), minFile.getAbsolutePath(),60, 80);
										logger.info("【ResUtils】  修改cover封面图片 modifyCoverImage()->>>封面缩略图路径："+minFile.getAbsolutePath());
									} catch (Exception e) {
										e.printStackTrace();
										throw new ServiceException("获取封面缩略图出错 ！");
									}
									
									return;
								}else if(!minFile.exists()){
									try {
										if (!minFile.getParentFile().exists()) {
											FileUtils.forceMkdir(minFile.getParentFile());
										}
										ImageUtils.zoomImg(file.getAbsolutePath(), minFile.getAbsolutePath(),60, 80);
										logger.info("【ResUtils】  修改cover封面图片 modifyCoverImage()->>>封面缩略图路径："+minFile.getAbsolutePath());
									} catch (Exception e) {
										e.printStackTrace();
										throw new ServiceException("获取封面缩略图出错 ！");
									}
									return;
								}
								
                			}
                		}
						
						//importCoverType=3  没有cover目录但是有cover.jpg文件（但是在资源下 和main。xml同级）  创建cover目录 并将cover.jpg挪到创建的cover目录下
						if ("/cover.jpg".equals(oppositePath) && "cover.jpg".equals(fileName)) {//  没有cover目录，在根目录下就是Cover.jpg
							//创建cover目录
							File cover= new File( dirPath  + File.separator + "cover");
							if (!cover.exists()) {
								FileUtils.forceMkdir(cover);
								logger.info("【ResUtils】 修改cover封面图片 modifyCoverImage() 创建cover目录信息："+ cover.getAbsolutePath());
							}
							//拷贝cover.jpg
							File file = new File(dirPath + File.separator + "cover" + File.separator + fileName);
							File minFile = new File(parentPath.replace("fileRoot", "viewer") + File.separator +resName+ File.separator+"cover"+ File.separator +"cover_min.jpg");
							try {
								FileUtils.moveFile(childFile, file);
								logger.info("【ResUtils】  修改cover封面图片 modifyCoverImage()->>>封面原路径："+fileOldPath+" 改名后路径："+file.getAbsolutePath());
							} catch (Exception e) {
								e.printStackTrace();
								throw new ServiceException("cover封面改名出错 ！");
							}
							
							try {
								if (!minFile.getParentFile().exists()) {
									FileUtils.forceMkdir(minFile.getParentFile());
								}
								ImageUtils.zoomImg(file.getAbsolutePath(), minFile.getAbsolutePath(),60, 80);
								logger.info("【ResUtils】  修改cover封面图片 modifyCoverImage()->>>封面缩略图路径："+minFile.getAbsolutePath());
							} catch (Exception e) {
								e.printStackTrace();
								throw new ServiceException("获取封面缩略图出错 ！");
							}
							return;
						}
					}else {
						modifyCoverImage(ResFile,childFile,parentPath);
					}
				}
			}
		}
	}
	
	/**
	* @param parentPath
	* @param doi
	* @return
	* @throws IOException    参数
	* @return Ca    返回类型
	* @throws
	 */
	public static  Ca getFileLists(String uploadFiles, String parentPath, String fileType,Ca ca,String doi) throws IOException {
		List<com.brainsoon.semantic.ontology.model.File> fileList=new ArrayList<com.brainsoon.semantic.ontology.model.File>();
		 com.brainsoon.semantic.ontology.model.File uploadFile = null;
		 uploadFiles = uploadFiles.replaceAll("\\\\", "/");
		 parentPath = parentPath.replaceAll("\\\\", "/");
		 String path = uploadFiles.substring(uploadFiles.lastIndexOf("/"),uploadFiles.length());
		 String lastPath = parentPath+path;
		 FileUtils.copyFile(new File(FILE_TEMP + uploadFiles), new File(lastPath));
		 lastPath = lastPath.replace(FILE_ROOT, "");
		 Date date = new Date();
		 String time = date.getTime()+"";
		 String fileName = new File(FILE_TEMP + uploadFiles).getName();
		 uploadFile = new com.brainsoon.semantic.ontology.model.File();
			uploadFile.setAliasName(fileName);
			uploadFile.setName(fileName);
			uploadFile.setPath(lastPath);
			uploadFile.setFileType(fileType);
			uploadFile.setFileByte(new File(FILE_TEMP + uploadFiles).length() + "");
			uploadFile.setCreate_time(time);
			uploadFile.setCreator(ca.getCreator());
			uploadFile.setMd5(MD5Util.getFileMD5String(new File(parentPath+path)));
			uploadFile.setModified_time(time);
			uploadFile.setVersion("1");
			uploadFile.setIsDir("2");
			uploadFile.setId(fileName);
			uploadFile.setPid(fileName);
			fileList.add(uploadFile);
			ca.setRealFiles(fileList);
		 
		return ca;
	}
	
	/**
	 * @throws IOException 
	 * 
	* @Title: getTMFileLists
	* @Description: 获取条目资源的文件信息
	* @param ca	Ca
	* @param resPath	资源文件夹
	* @param xmlFile	xml文件
	* @param imgList	xml文件中包含的图片
	* @return    参数
	* @return Ca    返回类型
	* @throws
	 */
	public static  Ca getTMFileLists(Ca ca,File resPath, File xmlFile, ArrayList<String> imgList) throws IOException{
		com.brainsoon.semantic.ontology.model.File realFile = null;
		String resName = resPath.getName();
		if (resPath.exists()) {
			List<com.brainsoon.semantic.ontology.model.File> tempRealFiles = ca.getRealFiles();
			if (tempRealFiles == null) {
				tempRealFiles = new ArrayList<com.brainsoon.semantic.ontology.model.File>();
			}
			String parentPath = resPath.getAbsolutePath().replaceAll("\\\\", "/").replaceAll("//", "/");
			if (!parentPath.endsWith("/")) {
				parentPath += "/";
			}
			String time = new Date().getTime() + "";
			
			//保存资源文件夹
			String targePath = parentPath.replace(FILE_ROOT, "");
			realFile = new com.brainsoon.semantic.ontology.model.File();
			realFile.setPath(targePath);
			realFile.setCreate_time(time);
			realFile.setIsDir("1");
			realFile.setName(resName);
			realFile.setCreator(ca.getCreator());
			realFile.setCaId(ca.getObjectId());
			realFile.setAliasName(resName);
			realFile.setPid("-1");
			realFile.setId(resName+"/");
			tempRealFiles.add(realFile);
			ca.setRealFiles(tempRealFiles);
			
			//保存条目本身xml文件
			if (xmlFile.exists()) {
				String fileName = xmlFile.getName();
				String fileType = fileName.substring(fileName.lastIndexOf(".") + 1,fileName.length());
				String tempPath = parentPath + fileName;
				FileUtils.copyFile(xmlFile, new File(tempPath));
				tempPath = tempPath.replace(FILE_ROOT, "");
				realFile = new com.brainsoon.semantic.ontology.model.File();
				realFile.setAliasName(fileName);
				realFile.setName(fileName);
				realFile.setPath(tempPath);
				realFile.setFileType(fileType);
				realFile.setFileByte(new File(tempPath).length() + "");
				realFile.setCreate_time(time);
				realFile.setCreator(ca.getCreator());
				realFile.setMd5(MD5Util.getFileMD5String(new File(parentPath + fileName)));
				realFile.setModified_time(time);
				realFile.setVersion("1");
				realFile.setIsDir("2");
				realFile.setCaId(ca.getObjectId());
				realFile.setId(resName+"/" + fileName);
				realFile.setPid(resName+"/");
				tempRealFiles.add(realFile);
				ca.setRealFiles(tempRealFiles);
			}
			
			//保存图片信息
			if (imgList!=null && imgList.size()>0) {
				for (String strImg : imgList) {
		    		if (strImg.startsWith(".")) {
		    			strImg = strImg.substring(1);
					}
		    		if (strImg.startsWith("/")) {
		    			strImg = strImg.substring(1);
					}
		    		
		    		String xmlParentPath = xmlFile.getParentFile().getAbsolutePath().replaceAll("\\\\", "/").replaceAll("//", "/");
		    		
		    		if(strImg.indexOf("/")>0){
						String[] dirs = strImg.split("/");
						String dirName = "";
						for(int k=0;k<dirs.length;k++){	
							String realName = dirs[k];	//当前文件
							dirName += dirs[k]+"/";	//相对路径
							String pid = "-1";
							if(k == dirs.length-1){//是不是到文件了  即123.jpg
								//String absPath = dirs[k];//dangqian
								String fileType = realName.substring(realName.lastIndexOf(".")+1,realName.length());
								String tempfile = resName+"/"+dirName.substring(0,dirName.length()-1);
								String lastPath = tempfile.substring(0,tempfile.lastIndexOf("/")+1);
								File newFile = null;
								//String path =  lastPath +realName;
								//正常走的逻辑
								File oldImgFile = new File(xmlParentPath+"/"+strImg);
								newFile = new File(parentPath + File.separator + tempfile.replaceAll(resName, ""));
								FileUtils.copyFile(oldImgFile, newFile);
								String tempPath = newFile.getAbsolutePath().replaceAll("\\\\", "/").replaceAll("//", "/").replace(FILE_ROOT, "");
								realFile = new com.brainsoon.semantic.ontology.model.File();
								realFile.setAliasName(realName);
								realFile.setName(realName);
								realFile.setPath(tempPath);
								realFile.setFileType(fileType);
								realFile.setFileByte(newFile.length() + "");
								realFile.setCreate_time(time);
								realFile.setCreator(ca.getCreator());
								realFile.setMd5(MD5Util.getFileMD5String(newFile));
								realFile.setModified_time(time);
								realFile.setVersion("1");
								realFile.setIsDir("2");
								realFile.setId(tempfile);
								realFile.setPid(lastPath);
								tempRealFiles.add(realFile);
							}else{//到文件夹
								//String path = "";
								String id = "";
								String tempDir = dirName.substring(0,dirName.length()-1);
								if(tempDir.indexOf("/")>0){//不是第一级目录
									String lastPath = tempDir.substring(0,tempDir.lastIndexOf("/")+1);//上一级目录
									pid = resName+"/"+lastPath;
									id = pid+realName+"/";
								}else{//是第一级文件夹
									pid = resName+"/";
									id = pid+realName+"/";
								}
								File tempFile= new File(parentPath + File.separator + id.replaceAll(resName, ""));
								if(!tempFile.exists()){
									FileUtils.forceMkdir(tempFile);
									logger.info("【RarUtil】unRarCaNew 正常逻辑 获取file文件夹信息 拷贝文件夹路径:" + tempFile.getAbsolutePath());
									String tempPath = tempFile.getAbsolutePath().replaceAll("\\\\", "/").replaceAll("//", "/").replace(FILE_ROOT, "");
									realFile = new com.brainsoon.semantic.ontology.model.File();
	        						realFile.setPath(tempPath);
	        						realFile.setCreate_time(time);
	        						realFile.setIsDir("1");
	        						realFile.setCreator(ca.getCreator());
	        						realFile.setCaId(ca.getObjectId());
	        						realFile.setName(realName);
	        						realFile.setAliasName(realName);
	        						realFile.setId(id);
	        						realFile.setPid(pid);
	        						tempRealFiles.add(realFile);
	    			    			ca.setRealFiles(tempRealFiles);
								}
							}
						}
					}else{
			    		File oldImgFile = new File(xmlParentPath+"/"+strImg);
			    		File imgFile = new File(parentPath+"/"+strImg);
			    		if (oldImgFile.exists()) {
			    			String fileName = oldImgFile.getName();
			    			String fileType = fileName.substring(fileName.lastIndexOf(".") + 1,fileName.length());
			    			FileUtils.copyFile(oldImgFile, imgFile);
			    			String tempPath = imgFile.getAbsolutePath().replaceAll("\\\\", "/").replaceAll("//", "/").replace(FILE_ROOT, "");
			    			realFile = new com.brainsoon.semantic.ontology.model.File();
			    			realFile.setAliasName(fileName);
			    			realFile.setName(fileName);
			    			realFile.setPath(tempPath);
			    			realFile.setFileType(fileType);
			    			realFile.setFileByte(new File(tempPath).length() + "");
			    			realFile.setCreate_time(time);
			    			realFile.setCreator(ca.getCreator());
			    			realFile.setMd5(MD5Util.getFileMD5String(new File(parentPath + fileName)));
			    			realFile.setModified_time(time);
			    			realFile.setVersion("1");
			    			realFile.setIsDir("2");
			    			realFile.setCaId(ca.getObjectId());
			    			realFile.setId(resName+"/" + fileName);
			    			realFile.setPid(resName+"/");
			    			tempRealFiles.add(realFile);
			    			ca.setRealFiles(tempRealFiles);
						}
					}
				}
			}
		}
		return ca;
	}
	
	/**
	 * 
	* @Title: getFileList
	* @Description: 当srcFile是个文件时，添加单个文件信息到ca中
	* @param parentPath	C:/workhj/workspace/bsrcm_cciph/WebRoot/fileDir/viewer/1/G2/0024b9bf-1db6-4185-85fd-5f623481e57c/9787502042585
	* @param srcFile 
	* @param ca	
	* @return
	* @throws IOException    参数
	* @return Ca    返回类型
	* @throws
	 */
	public static  Ca getFileList(String parentPath, File srcFile, Ca ca) throws IOException {
 		List<com.brainsoon.semantic.ontology.model.File> realFiles = ca.getRealFiles();
		if (realFiles == null) {
			realFiles = new ArrayList<com.brainsoon.semantic.ontology.model.File>();
		}
		com.brainsoon.semantic.ontology.model.File uploadFile = new com.brainsoon.semantic.ontology.model.File();
		Date date = new Date();
		String time = date.getTime()+"";
		parentPath = parentPath.replaceAll("\\\\", "/").replaceAll("//", "/");
		if (!parentPath.endsWith("/")) {
			parentPath = parentPath+"/";
		}
		
		String fileName = srcFile.getName();
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
		File newFile = new File(parentPath + fileName);
		String path = parentPath.replaceAll(FILE_ROOT, "")+fileName;
		FileUtils.copyFile(srcFile, newFile);
		uploadFile.setAliasName(fileName);
		uploadFile.setName(fileName);
		uploadFile.setPath(path);
		uploadFile.setFileType(fileType);
		uploadFile.setFileByte(srcFile.length() + "");
		uploadFile.setCreate_time(time);
		uploadFile.setCreator(ca.getCreator());
		uploadFile.setMd5(MD5Util.getFileMD5String(srcFile));
		uploadFile.setModified_time(time);
		uploadFile.setVersion("1");
		uploadFile.setIsDir("2");
		uploadFile.setId(fileName);
		uploadFile.setPid("-1");
		realFiles.add(uploadFile);
		ca.setRealFiles(realFiles);
		 
		return ca;
	}
	
	
	/**
    *
    * @Title: xml2Map
    * @Description: 将xml转成Map对象
    * @param @param doc
    * @param @return
    * @return Map<String,Object>
    * @throws
    */
	@SuppressWarnings("unchecked")
	public static Map<String, String> xml2Map(Document doc){
       Map<String, String> map = new HashMap<String, String>();
       if(doc == null)
           return map;
       Element root = doc.getRootElement();
       for (Iterator<Element> iterator = root.elementIterator(); iterator.hasNext();) {
           Element e = (Element) iterator.next();
           List<Element> list = e.elements();
           if(list.size() > 0){
               map = xml2Map(map, e);
           }else
               map.put(e.getName(), e.getText());
       }
       return map;
	}
    @SuppressWarnings("unchecked")
    public static Map<String, String> xml2Map(Map<String, String> map,Element e){
       List<Element> list = e.elements();
       if(list.size() > 0){
           for (int i = 0;i < list.size(); i++) {
               Element iter = (Element) list.get(i);
               if(iter.elements().size() > 0){
               	map = xml2Map(map,iter);
               } else{
                    map.put(iter.getName(), iter.getText());
               }
           }
       }else{
       	 map.put(e.getName(), e.getText());
       }
       return map;
   }

    /**
    *
    * @param url
    * @param charset
    * @return
    * @throws DocumentException
    */
   public static Document read(File file, String charset) {
       if (Objects.isNull(file)) {
           return null;
       }
       SAXReader reader = new SAXReader();
       if (!Objects.isNull(charset)) {
           reader.setEncoding(charset);
       }
       Document document = null;
       try {
           document = reader.read(file);
       } catch (DocumentException e) {
           e.printStackTrace();
       }
       return document;
   }

   
    public static Document read(File file) {
        return read(file, null);
    }
	
    /**
    *
    * @Title: getImgSrc
    * @Description:  找出文本中的img标签的src路径
    * @param content 文本内容
    * @return ArrayList<String>
    * @throws
    */
	public static ArrayList<String> getImgSrc(String content) {
		Pattern pattern = Pattern.compile("<img\\s+(?:[^>]*)src\\s*=\\s*([^>]+)",Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		if (StringUtils.isBlank(content)) {
			return null;
		}
		Matcher matcher = pattern.matcher(content);
		ArrayList<String> list = new ArrayList<String>();
		while (matcher.find()) {
			String group = matcher.group(1);
			if (group == null) {
				continue;
			}
			if (group.startsWith("'")) {
				list.add(group.substring(1, group.indexOf("'", 1)));
			} else if (group.startsWith("\"")) {
				list.add(group.substring(1, group.indexOf("\"", 1)));
			} else {
				list.add(group.split("\\s")[0]);
			}
		}
		return list;
	}

	
	@SuppressWarnings("null")
	public static void main(String[] args) {
		try {
			//modifyCoverImage(new File("C:/工作/项目/测试数据/资源导入/红烧肉/"),new File("C:/工作/项目/测试数据/资源导入/红烧肉/"));
			/*String parentPath="C:/temp/convert/1/G2/12312441233124/"; 
			File srcFile=new File("C:/temp/xml/001/510052012");//原路径
			String pid="-1";
			Ca ca=new Ca();
			ca.setRootPath("/1/G2/12313123-1231213-12e123/");
			
			ca=getFileLists(parentPath,srcFile,pid,ca,"",false);*/
			 
			/*Map<String, List<String>> md5Map = new HashMap<String, List<String>>();
			List<String> md5List = new ArrayList<>();
			md5List.add("b7a5be046cfe5478b766ee9a66375710");
			md5Map.put("txt", md5List);
			Set<String> dirSet = new HashSet<String>();
			dirSet.add("word");*/
			
			//ca = getOverFileLists(parentPath, srcFile, pid, ca, dirSet, md5Map, "C:/工作/项目/测试数据/资源导入/红烧肉", "");
			
			
			
			 //File file = new File("C:/temp/xml/黄俊007/10512008");
			 //modifyCoverImage(file,file,"C:/workhj/workspace/bsrcm_cciph/WebRoot/fileDir/fileRoot/1/G2/0024b9bf-1db6-4185-85fd-5f623481e57c");
			
			
			/*Ca ca=new Ca();
			Map<String,MetadataDefinition> metadataDefinitions = MetadataSupport.getAllMetadataDefinition();
			checkAndSetValue(ca,"dimensions","64开",metadataDefinitions,1);*/
			
			 
			//System.out.println(solrCodeModify("潞安煤矿史:1840～1959"));
			
			
			/*String path = "C:\\Users\\tanghui\\Desktop\\北欧文学\\北欧文学\\“基拉社”.xml";
	    	File file = new File(path);
	    	Document doc = read(file);
	    	Map<String, String> map = xml2Map(doc);
	    	System.out.println(map.toString());*/
	    	/*String str = "瑞典作家。生于斯德哥尔摩。父母都是显赫的瑞典贵族。<img src=\"./images/1/ww0423哈德格里姆松420.jpg\" width=\"111\" />他当过牧师、中学校长、政府机关职员和《晚报》记者。<img src=\"ww0423哈德格里姆松420.jpg\" width=\"111\" />因为对城市文化和生活不满，于1824年迁居韦姆兰农村，买了一小块土地，并与一个农村姑娘结婚，<img src=\"./images/ww0434安徒生011.jpg\" />试图过一种简朴平静的农民生活，但不久又迁回斯德哥尔摩。1851年逃往美国，随后被指控犯有欺骗和谋杀罪。晚年回到欧洲，死在不来梅。其早期作品富于创造性的浪漫主义者，19世纪40年代以后成为现实主义者和社会改良主义者。代表作是理性小说《阿莫丽娜》（1839），描写女主人公阿莫丽娜在爱人死后为了忏悔而四处漫游，最后被关进精神病医院。他的大部分作品都收集在《野蔷薇花集》（1822～1851）里，先后出版多卷，内容涉及音乐、宗教、哲学，形式有论文、故事、诗歌、小说和戏剧。此外还有历史小说《幽宫》、《乡间别墅》、《王后的首饰》（1834），诗剧《拉米多·玛丽纳斯库》、《伊普萨拉岛上的天鹅洞》、《月亮和女神》，散文剧《古鲁姆毕纳》，书信体小说《阿拉敏达·玛伊》，短篇小说《谢尔努拉磨坊》、《小教堂》、《这样也可以》，杂文《瑞典贫困的意义》，讽刺诗《乌尔姆斯和阿利曼》，叙事诗《阿特尔打猎》等。";
	    	ArrayList<String> imgList = getImgSrc(str);
	    	for (String strImg : imgList) {
	    		System.out.println(strImg);
			}
	    	
	    	getTMFileLists(new Ca(), new File("C:/workhj/workspace/bsrcm_cciph/WebRoot/fileDir/fileRoot/1/G2/12333333333333333333333333/“基拉社”"), new File("C:/temp/TM/北欧文学/“基拉社”.xml"), imgList);
			 
	    	*/
	    	
			String parentPath = "C:/workhj/workspace/bsrcm_cciph/WebRoot/fileDir/FileRoot/1/G2/0024b9bf-1db6-4185-85fd-5f623481e57c/9787502042585";
			File file = new File("C:/temp/excel/9787502039936/images/0002-3.jpg");
			getFileList(parentPath, file, new Ca());
	    	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public static Object targetCb(String type) {
		List<DictName> nameList = new ArrayList<DictName>();
		List<DictValue> nameValue = new ArrayList<DictValue>();
		boolean flag = false;
		if(StringUtils.isNotBlank(type)){
			IDictNameService dictService;
			try {
				if(!type.equals("通用标签")){
					dictService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
					String hql = "from DictName where indexTag='targetType' and status=1";
					nameList = dictService.query(hql);
					hql = "select name from DictValue where pid="+nameList.get(0).getId()+" and indexTag='"+type+"'";
					nameValue = dictService.query(hql);
				}else{
					flag = true;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(nameValue!=null && !nameValue.isEmpty()){
			return nameValue.get(0);
		}else{
			return "通用标签";
		}
	}

}
