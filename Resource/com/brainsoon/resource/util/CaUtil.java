package com.brainsoon.resource.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CaList;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.IMetaDataModelService;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.util.FieldValidator;
import com.brainsoon.system.util.MetadataSupport;

public class CaUtil {
	public static Ca convertJsonToCa(String json,String publishType,String repeatType){
		IMetaDataModelService metaDataModelService = null;
		ISysParameterService sysParameterService = null;
		try {
			metaDataModelService = (IMetaDataModelService)BeanFactoryUtil.getBean("metaDataModelService");
			sysParameterService = (ISysParameterService)BeanFactoryUtil.getBean("sysParameterService");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Ca ca = new Ca();
		Map<String, String> metadataMap = new HashMap<String, String>();
		Map<String,String> checkRepeatMetadate = new HashMap<String,String>();
		String uploadFile = "";
		String creator = "";
		UserInfo info = LoginUserUtil.getLoginUser();
		JSONObject rsJson = JSONObject.fromObject(json);
		String objectId = "";
		try{
			if(StringUtils.isBlank(publishType)){
				publishType = rsJson.getString("publishType");
			}
			uploadFile = rsJson.getString("realFile");
			objectId = rsJson.getString("objectId");
		}catch(Exception e){
//			e.printStackTrace();
		}finally{
			ca.setPublishType(publishType);
//			ca.setUploadFile(uploadFile);
			ca.setStatus("0");
			ca.setCreator("180");
		}
		String src = "";
		List<SysParameter> sr = sysParameterService.findParaValue("bachImportExcelPath");
		if(sr!=null && sr.size()>0){
			if(sr.get(0)!=null && sr.get(0).getParaValue()!=null){
				src = sr.get(0).getParaValue();
			}
		}
		src = src.replaceAll("\\\\", "/");
		if(!src.endsWith("/")){
			src = src+"/";
//			src = "E:/华师京城资源/";
		}
		Object path ="";
		String fileType = "";
		JSONObject  da = null;
		String uuid = UUID.randomUUID().toString();
		String interimPath = "";
		Object fileObjectId = "";
		String pathFile = "";
		Object fType = "";
		Map<String, Map<String,String>> fileMetadataFlag = new HashMap<String, Map<String,String>>();
		if(StringUtils.isNotBlank(uploadFile)){
			JSONArray obj = new JSONArray();
			JSONArray ob= (JSONArray) obj.fromObject(uploadFile);
			for(int i=0;i<ob.size();i++){
				String md5= "" ;
				da = (JSONObject)ob.get(i);
				path = da.get("path");
				if(path!=null){
					pathFile = path.toString();
					pathFile = pathFile.replaceAll("\\\\", "/");
					if(pathFile.contains(".")){
						fileType = pathFile.substring(pathFile.lastIndexOf(".")+1,pathFile.length());
					}
					interimPath = src+uuid+"/"+pathFile.substring(pathFile.indexOf("/")+1);
					try {
						md5 = MD5Util.getFileMD5String(new File(pathFile));
						FileUtils.copyFile(new File(pathFile), new File(interimPath));
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				fileObjectId = da.get("fileObjectId");
				Object type= null;
				if(fileObjectId!=null){
					JSONObject obj1 = new JSONObject();
					JSONObject ob1= null;
					HttpClientUtil http = new HttpClientUtil();
					String fileDetail = http.executeGet(WebappConfigUtil
							.getParameter("CA_FILERES_DETAIL_URL") + "?id=" + fileObjectId);
					if(StringUtils.isNoneBlank(fileDetail)){
						ob1= (JSONObject) obj1.fromObject(fileDetail);
						type = ob1.get("fileType");
						if(type!=null){
							fileType = type.toString();
						}
					}
					
				}else{
					fType = da.get("fileType");
					if(fType!=null){
						fileType = fType.toString();
					}
				}
				
				Map<String, String> fileMetadataMap = new HashMap<String, String>();
				fileMetadataMap.put("path",pathFile);
				List<MetadataDefinition>  fileMetaDatas = metaDataModelService.queryMetaByFormat(fileType);
				
				for(MetadataDefinition me:fileMetaDatas){
					Object value = da.get(me.getFieldName());
					if(value!=null){
//						移除元数据项
						da.remove(me.getFieldName());
						fileMetadataMap.put(me.getFieldName(), value.toString());
					}
				}
				if(fileObjectId!=null){
					fileMetadataFlag.put(fileObjectId.toString(), fileMetadataMap);
				}else{
					fileMetadataFlag.put(md5, fileMetadataMap);
				}
			}
			ca.setFileMetadataFlag(fileMetadataFlag);
			ca.setRootPath(src+uuid);
			ca.setUploadFile(src+uuid);
			ca.setImportXpath(src);
		}else{
			ca.setUploadFile("-999999");
		}
		List<MetadataDefinition> metadataDefines = MetadataSupport.getAllMetadataDefinitionByResType(publishType);
		Map<String,MetadataDefinition> allMap = MetadataSupport.getAllMetadataDefinition();
		StringBuffer fieldHasError = null;
		for(MetadataDefinition metadataDefinition:metadataDefines){
			String fieldName = metadataDefinition.getFieldName();
			String fieldValue = "";
			try{
				if(rsJson.getString(fieldName)!=null){
					fieldValue = rsJson.getString(fieldName);
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(StringUtils.isNotBlank(fieldValue)){
					try {
						ca = ResUtils.doCheckAndSetValue(ca, fieldName, fieldValue, allMap,1);
					} catch (Exception e) {
						if(fieldHasError==null){
							fieldHasError = new StringBuffer();
						}
						fieldHasError.append(e.getMessage() + ",");
					}
					if(fieldHasError == null){
						//查重字段数据
						if(metadataDefinition.getDuplicateCheck() !=null && "true".equals(metadataDefinition.getDuplicateCheck())){
							checkRepeatMetadate.put(fieldName, ca.getMetadataMap().get(fieldName));
						}
						metadataMap.put(fieldName, ca.getMetadataMap().get(fieldName));
					}
				}
			}
		}
		
		if(fieldHasError!=null){
			throw new ServiceException(fieldHasError.substring(0, fieldHasError.length()-1));
		}
		
		//正式去查重
		if(StringUtils.isBlank(objectId)){
		CaList repeatCas = ResUtils.checkRepeat(checkRepeatMetadate, ca.getPublishType(),"");
		if(StringUtils.isBlank(repeatType)||repeatType.equals("3")){
			if(repeatCas != null && repeatCas.getTotle()> 0){
				throw new ServiceException("存在重复资源");
			}
		}
			if(repeatCas.getTotle()>0&&repeatCas.getCas().get(0).getObjectId()!=null){
				ca.setObjectId(repeatCas.getCas().get(0).getObjectId());
			}
		}
		if(StringUtils.isNotBlank(objectId)){
			ca.setObjectId(objectId);
		}
		ca.setMetadataMap(metadataMap);
		Date now = new Date();
		String time = now.getTime()+"";
		ca.setCreateTime(time);
		//删除临时目录
//		if(StringUtils.isNotBlank(uploadFile)){
//			File file = new File(src+uuid);
//			if (file.exists()) {
//				if (file.isDirectory()) {
//					try {
//						FileUtils.deleteDirectory(file);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				} else {
//					try {
//						FileUtils.forceDelete(file);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}
		return ca;
	}
}
