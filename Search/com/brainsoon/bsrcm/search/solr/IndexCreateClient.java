package com.brainsoon.bsrcm.search.solr;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.SolrInputDocument;

import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.bsrcm.search.util.DateUtil;
import com.brainsoon.semantic.vocabulary.CommonDCTerms;
import com.brainsoon.semantic.vocabulary.ExtendDCTerms;

public class IndexCreateClient {
    private static Logger logger = Logger.getLogger(IndexCreateClient.class
		  .getName());    

    private static String solrResourceUrl = "http://10.130.29.26:9000/solr/core1";
    
	private static class IndexCreateClientHolder {
		static IndexCreateClient instance = new IndexCreateClient();
	}
	
	public static IndexCreateClient buildSingleton() {
		return IndexCreateClientHolder.instance;
	}

	public static IndexCreateClient getInstance() {
		return IndexCreateClientHolder.instance;
	}
	
	public void setSolrResourceUrl(String solrResourceUrl) {
		IndexCreateClient.solrResourceUrl = solrResourceUrl;
	}
	
	public static String getSolrResourceUrl() {
		return solrResourceUrl;
	}
	
//	public void addCopyrightIndex(Ca ca,SolrInputDocument doc){
//		if(ca.getCopyRightMetaData() !=null){
//			String crtType = ca.getCopyRightMetaData().getCrtType();
//	        if(crtType != null)
//	        	doc.addField("crt_type", crtType);
//	        String crtPerson = ca.getCopyRightMetaData().getCrtPerson();
//	        if(crtPerson != null)
//	        	doc.addField("crt_person", crtPerson);
//	        String authorizer = ca.getCopyRightMetaData().getAuthorizer();
//	        if(authorizer != null)
//	        	doc.addField("authorizer", authorizer);
//	        String authArea = ca.getCopyRightMetaData().getAuthArea();
//	        if(authArea != null)
//	        	doc.addField("auth_area", authArea);
//	        String authChannel = ca.getCopyRightMetaData().getAuthChannel();
//	        if(authChannel != null)
//	        	doc.addField("auth_channel", authChannel);
//	        String authTimeLimit = ca.getCopyRightMetaData().getAuthTimeLimit();
//	        if(authTimeLimit != null)
//	        	doc.addField("auth_timelimit", authTimeLimit);
//	        String authLanguage = ca.getCopyRightMetaData().getAuthLanguage();
//	        if(authLanguage != null)
//	        	doc.addField("auth_language", authLanguage);
//	        String permitRight = ca.getCopyRightMetaData().getPermitRight();
//	        if(permitRight != null)
//	        	doc.addField("permit_right", permitRight);
//	        String collaPattern = ca.getCopyRightMetaData().getCollaPattern();
//	        if(collaPattern != null)
//	        	doc.addField("colla_pattern", collaPattern);
//	        String contractCode = ca.getCopyRightMetaData().getContractCode();
//	        if(contractCode != null)
//	        	doc.addField("contract_code", contractCode);
//			String authStartDate_date;
//	        String authStartDate_time;
//			try {
//				authStartDate_date = ca.getCopyRightMetaData().getAuthStartDate();		
//				if(StringUtils.isNotBlank(authStartDate_date)) {
//					authStartDate_date += " 00:00:00:000";
//					authStartDate_time = DateUtil.getDateTime(DateUtil.parseTime(authStartDate_date),  "yyyy-MM-dd'T'HH:mm:ss'Z'");
//					doc.addField("auth_startdate",authStartDate_time);
//				}
//			} catch (ParseException e1) {
//				logger.error("更新时间格式错误，" + e1.getMessage());
//			}
//			String authEndDate_date;
//	        String authEndDate_time;
//			try {
//				authEndDate_date = ca.getCopyRightMetaData().getAuthEndDate();		
//				if(StringUtils.isNotBlank(authEndDate_date)) {		
//					authEndDate_date += " 00:00:00:000";
//					authEndDate_time = DateUtil.getDateTime(DateUtil.parseTime(authEndDate_date),  "yyyy-MM-dd'T'HH:mm:ss'Z'");
//					doc.addField("auth_enddate",authEndDate_time);
//				}
//			} catch (ParseException e1) {
//				logger.error("更新时间格式错误，" + e1.getMessage());
//			}
//		}
//	}
    
//	public void addCopyrightIndex(Ca ca,ContentStreamUpdateRequest up){
//		if(ca.getCopyRightMetaData() !=null){
//			String crtType = ca.getCopyRightMetaData().getCrtType();
//	        if(crtType != null)	        	
//	        	up.setParam("literal.crt_type", crtType);
//	        String crtPerson = ca.getCopyRightMetaData().getCrtPerson();
//	        if(crtPerson != null)
//	        	up.setParam("literal.crt_person", crtPerson);
//	        String authorizer = ca.getCopyRightMetaData().getAuthorizer();
//	        if(authorizer != null)	        	
//	        	up.setParam("literal.authorizer", authorizer);
//	        String authArea = ca.getCopyRightMetaData().getAuthArea();
//	        if(authArea != null)	        	
//	        	up.setParam("literal.auth_area", authArea);
//	        String authChannel = ca.getCopyRightMetaData().getAuthChannel();
//	        if(authChannel != null)	        	
//	        	up.setParam("literal.auth_channel", authChannel);
//	        String authTimeLimit = ca.getCopyRightMetaData().getAuthTimeLimit();
//	        if(authTimeLimit != null)
//	        	up.setParam("literal.auth_timelimit", authTimeLimit);
//	        String authLanguage = ca.getCopyRightMetaData().getAuthLanguage();
//	        if(authLanguage != null)
//	        	up.setParam("literal.auth_language", authLanguage);
//	        String permitRight = ca.getCopyRightMetaData().getPermitRight();
//	        if(permitRight != null)
//	        	up.setParam("literal.permit_right", permitRight);
//	        String collaPattern = ca.getCopyRightMetaData().getCollaPattern();
//	        if(collaPattern != null)
//	        	up.setParam("literal.colla_pattern", collaPattern);
//	        String contractCode = ca.getCopyRightMetaData().getContractCode();
//	        if(contractCode != null)
//	        	up.setParam("literal.contract_code", contractCode);
//			String authStartDate_date;
//	        String authStartDate_time;
//			try {
//				authStartDate_date = ca.getCopyRightMetaData().getAuthStartDate();		
//				if(StringUtils.isNotBlank(authStartDate_date)) {
//					authStartDate_date += " 00:00:00:000";
//					authStartDate_time = DateUtil.getDateTime(DateUtil.parseTime(authStartDate_date),  "yyyy-MM-dd'T'HH:mm:ss'Z'");
//					up.setParam("literal.auth_startdate",authStartDate_time);
//				}
//			} catch (ParseException e1) {
//				logger.error("更新时间格式错误，" + e1.getMessage());
//			}
//			String authEndDate_date;
//	        String authEndDate_time;
//			try {
//				authEndDate_date = ca.getCopyRightMetaData().getAuthEndDate();		
//				if(StringUtils.isNotBlank(authEndDate_date)) {		
//					authEndDate_date += " 00:00:00:000";
//					authEndDate_time = DateUtil.getDateTime(DateUtil.parseTime(authEndDate_date),  "yyyy-MM-dd'T'HH:mm:ss'Z'");
//					up.setParam("literal.auth_enddate",authEndDate_time);
//				}
//			} catch (ParseException e1) {
//				logger.error("更新时间格式错误，" + e1.getMessage());
//			}
//		}
//	}
	
	public void createCAIndexNoContent(Ca ca) throws SearchException {
        if(ca == null)
        	return;
                  
        SolrServer solr = new HttpSolrServer(solrResourceUrl);

        SolrInputDocument doc = new SolrInputDocument();
        
        String uuid = ca.getObjectId();
        doc.addField("uuid", uuid); 
     
        String title = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_TITLE);
        if(title != null)
        	doc.addField("title", title);        

 //       doc.addField("objectType", "3");  
//        if(ca.getType() != null){
//        	if("1".equals(ca.getType())){
//            	doc.addField("resType", "F12"); 
//            }else if("2".equals(ca.getType())){
//            	doc.addField("resType", "F11"); 
//            }else{
//            	doc.addField("resType", "F10"); 
//            }
//        }
        String publishType = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_PUBLISHTYPE);
        if(publishType != null)
        	doc.addField("publish_type", publishType);
        
//        String libType = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_LIBTYPE);
//        if(libType != null)
//        	doc.addField("libType", libType); 
        
        String status = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_STATUS);
        if(status != null)
        	doc.addField("status", Integer.parseInt(status));
    
//        String module = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_MODULE);
//        if(module != null)
//        	doc.addField("module", module.trim());
        String source = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_SOURCE);
        if(source != null)
        	doc.addField("source", source);
//        String eduPhaseId = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_EDUCATIONAL_PHASE);
//        String eduPhaseName = StoreService.getMetaNameByCode(eduPhaseId, 1);
//        if(eduPhaseId != null)
//        	doc.addField("educational_phaseId", eduPhaseId.trim());
//        if(eduPhaseName != null)
//        	doc.addField("educational_phaseName", eduPhaseName);
        
//        String subjectId = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_SUBJECT);
//        String subjectName = StoreService.getMetaNameByCode(subjectId, 1);
//        if(subjectId != null)
//        	doc.addField("subjectId", subjectId.trim());
//        if(subjectName != null)
//        	doc.addField("subjectName", subjectName);
 
        
//        String versionId = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_VERSION);
//        String versionName = StoreService.getMetaNameByCode(versionId, 0);
//        if(versionId != null)
//        	doc.addField("versionId", versionId.trim());
//        if(versionName != null)
//        	doc.addField("versionName", versionName);
        
//        String gradeId = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_GRADE);
//        String gradeName = StoreService.getMetaNameByCode(gradeId, 1);
//        if(gradeId != null)
//        	doc.addField("gradeId", gradeId.trim());
//        if(gradeName != null)
//        	doc.addField("gradeName", gradeName);
       
//        String fasciculeId = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_FASCICULE);
//        String fasciculeName = StoreService.getMetaNameByCode(fasciculeId, 0);
//        if(fasciculeId != null)
//        	doc.addField("fasciculeId", fasciculeId.trim());
//        if(fasciculeName != null)
//        	doc.addField("fasciculeName", fasciculeName);
        
//        String unitId = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_UNIT);
//        String unitName = StoreService.getMetaNameById(unitId, 0);
//        if(unitName == null){
//			unitName = StoreService.getMetaNameById(unitId, 1);
//		}
//        if(unitId != null)
//        	doc.addField("unitId", unitId.trim());
//        if(unitName != null)
//        	doc.addField("unitName", unitName);
        
        String keywords = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_KEYWORDS);
        if(StringUtils.isNotBlank(keywords)) {
        	String[] keywordArr = keywords.split(",");
	        for(String keyword : keywordArr) {
	        	doc.addField("keyword", keyword);
	        }
        }
                
        String creator = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_CREATOR);
        if(creator != null)
        	doc.addField("creator", creator);
        String resVersion = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_RES_VERSION);
        if(resVersion != null)
        	doc.addField("res_version", resVersion);
        String modified_date;
        String modified_time;
		try {
			modified_date = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_MODIFIED_TIME);		
			if(StringUtils.isNotBlank(modified_date)) {					
				modified_time = DateUtil.getDateTime(DateUtil.parseTime(modified_date),  "yyyy-MM-dd'T'HH:mm:ss'Z'");
				doc.addField("modified_time",modified_time);
			}
		} catch (ParseException e1) {
			logger.error("更新时间格式错误，" + e1.getMessage());
		}
		
        String description = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_DESCRIPTION);
        if(description != null)
        	doc.addField("description", description);  
        if(ca.getExtendMetaData()!=null){
        	String viewType = ca.getExtendMetaData().getExtendMetaValue(ExtendDCTerms.METADATA_VIEW_TYPE);
            if(viewType != null)
            	doc.addField("view_type", viewType);
            String viewValue = ca.getExtendMetaData().getExtendMetaValue(ExtendDCTerms.METADATA_VIEW_VALUE);
            if(viewType != null){
            	doc.addField("view_value", viewValue);
            }else{
            	doc.addField("view_value", "0");
            }
        }
           
        
        String rating = ca.getCommonMetaData().getCommonMetaValue(CommonDCTerms.METADATA_RATING);
        if(rating != null){
        	doc.addField("rating", rating); 
        }else{
        	doc.addField("rating", "0"); 
        }
    	//添加版权
//        addCopyrightIndex(ca,doc);
//        List<String> xPaths = ca.getXpaths();
//        if(xPaths != null) {
//	        for(String xPath : xPaths) {
//	        	doc.addField("xPath", xPath);
//	        }
//        }
//        List<String> xPathNames = ca.getXpathNames();
//        if(xPathNames != null) {
//	        for(String xPathName : xPathNames) {
//	        	doc.addField("xPathName", xPathName);
//	        }
//        }
        try {
			solr.add(doc);
			solr.commit();
//			solr.optimize();
		} catch (SolrServerException e) {
			throw new SearchException("创建索引异常，搜索服务异常，异常[" + e.getMessage() + "]");
		} catch (IOException e) {
			throw new SearchException("创建索引异常，通讯异常，异常[" + e.getMessage() + "]");
		}
                
    }
    
	/**
	 * 根据文件名获取文件的ContentType类型
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFileContentType(String filename) {
		String contentType = "";
		String prefix = filename.substring(filename.lastIndexOf(".") + 1);
		if (prefix.equalsIgnoreCase("xlsx")) {
			contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		} else if (prefix.equalsIgnoreCase("pdf")) {
			contentType = "application/pdf";
		} else if (prefix.equalsIgnoreCase("doc")) {
			contentType = "application/msword";
		} else if (prefix.equalsIgnoreCase("txt")) {
			contentType = "text/plain";
		} else if (prefix.equalsIgnoreCase("xls")) {
			contentType = "application/vnd.ms-excel";
		} else if (prefix.equalsIgnoreCase("docx")) {
			contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		} else if (prefix.equalsIgnoreCase("ppt")) {
			contentType = "application/vnd.ms-powerpoint";
		} else if (prefix.equalsIgnoreCase("pptx")) {
			contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
		}else {
			contentType = "othertype";
		}
		return contentType;
	}
	public void createCAIndexWithContent(Ca ca) throws SearchException {
		if(ca == null)
        	return;
        SolrServer solr = new HttpSolrServer(solrResourceUrl);
        logger.info("solr url ： "+solrResourceUrl);
        ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update/extract");     
        
        List<String> filePaths = ca.getXpaths();
        //遍历索引文件
    	for(String path:filePaths){
    		String contenttype=getFileContentType(path); 
    		if(!contenttype.equals("othertype")){
    			if(!contenttype.equals("othertype")) {
    				try {
    					File file=new File(path);
    					if(file.exists()){  
	    					 logger.info("添加所有文件："+path);  
	    				     up.addFile(file, contenttype); 
	    				 }
					} catch (Exception e) {
						e.printStackTrace();
					}
    			}
    		}
    	}
	    
        String uuid = ca.getObjectId();
        up.setParam("literal.uuid", uuid); 
        
        Map<String, String> metaDatas = ca.getMetadataMap();
  		if(metaDatas!=null && metaDatas.size() > 0){
  			for (Map.Entry<String, String> metaDataEntry : metaDatas.entrySet()) {
  	    		String metaName = metaDataEntry.getKey();
  	    		String metaValue = metaDataEntry.getValue();
  	    		if(StringUtils.isBlank(metaValue))
  	    			continue;
  	    		up.setParam("literal."+metaName, metaValue);
  	    	}	
  		}
  		
        String publishType = ca.getPublishType();
        if(publishType != null)
        	up.setParam("literal.publish_type", publishType);
        
        String status = ca.getStatus();
        if(status != null)
        	up.setParam("literal.status", status);

        String creator = ca.getCreator();
        if(creator != null)
        	up.setParam("literal.creator", creator);
        String updateor = ca.getUpdater();
        if(updateor != null)
        	up.setParam("literal.updater", updateor);
        String hasCopyright = ca.getHasCopyright();
        if(hasCopyright != null)
        	up.setParam("literal.has_copyright", hasCopyright);
        if(ca.getObjectId() != null)
        up.setParam("literal.objectId", ca.getObjectId());
        
        String create_date;
        String create_time;
		try {
			create_date = ca.getCreateTime();		
			if(StringUtils.isNotBlank(create_date)) {					
				create_time = DateUtil.getDateTime(new Date(Long.parseLong(create_date)),  "yyyy-MM-dd'T'HH:mm:ss'Z'");
				up.setParam("literal.create_time",create_time);
			}
		} catch (Exception e1) {
			logger.error("创建时间格式错误，" + e1.getMessage());
		}
        
        String modified_date;
        String modified_time;
		try {
			modified_date = ca.getUpdateTime();		
			if(StringUtils.isNotBlank(modified_date)) {					
				modified_time = DateUtil.getDateTime(new Date(Long.parseLong(modified_date)),  "yyyy-MM-dd'T'HH:mm:ss'Z'");
				up.setParam("literal.modified_time",modified_time);
			}
		} catch (Exception e1) {
			logger.error("更新时间格式错误，" + e1.getMessage());
		}
       
        up.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
        
        try {
        	
        	logger.info("开始执行索引.... "+ ca.getObjectId());
        	
        	solr.request(up);
        	solr.optimize();
        	
        	logger.info("创建索引成功"+ ca.getObjectId());
        	
		} catch (SolrServerException e) {
			e.printStackTrace();
			throw new SearchException("创建索引异常，搜索服务异常，异常[" + e.getMessage() + "]");
		} catch (IOException e) {
			e.printStackTrace();
			throw new SearchException("创建索引异常，通讯异常，异常[" + e.getMessage() + "]");
		}	
	}
	
	public void createCAIndex(Ca ca) throws SearchException {
		List<String> filePaths = ca.getXpaths();
		if(filePaths != null && filePaths.size() > 0)
			createCAIndexWithContent(ca);
		else
			createCAIndexNoContent(ca);
			
	}
	
    public void deleteCAIndex(String assetId) throws SearchException {
    	if(assetId == null)
    		return;
    	SolrServer solr = new HttpSolrServer(solrResourceUrl);
    	try {
    		solr.deleteById(assetId);
    		solr.commit();
    	} catch (SolrServerException e) {
 			throw new SearchException("删除索引异常，搜索服务异常，异常[" + e.getMessage() + "]");
 		} catch (IOException e) {
 			throw new SearchException("删除索引异常，通讯异常，异常[" + e.getMessage() + "]");
 		}
    }
    
    
    
    public static void main(String[] args) {
    	String result = "";
		try {
//				String json = "[{\"uuid\":\"urn:asset-c19e4544-e715-4b2a-9ef3-0b718b12e454\",\"keyword\":{\"set\":[ \"aaa\", \"bbb\" ]}}]";
//			String json = "[{\"uuid\":\"urn:asset-c19e4544-e715-4b2a-9ef3-0b718b12e454\",\"xPath\":{\"set\":\"[\"1\"]\"}}]";
//			System.out.println("1=============================" + json);
			IndexCreateClient icc = new IndexCreateClient();
			Ca ca = new Ca();
			List<String> paths = new ArrayList<String>();
			paths.add("C:/Users/brainsoon/Desktop/这是测试数据/txt/Linux常用命令大全.txt");
			ca.setXpaths(paths);
			ca.setObjectId("urn:publish-5c7bc557-b752-4c80-a057-ad00f8515c5d");
			icc.createCAIndexWithContent(ca);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
