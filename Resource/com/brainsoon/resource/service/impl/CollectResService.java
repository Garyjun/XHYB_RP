package com.brainsoon.resource.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.processing.Filer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.UID;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.dofile.view.CatalogDTO;
import com.brainsoon.common.util.dofile.zip.ZipOrRarUtil;
import com.brainsoon.common.util.dofile.zip.ZipUtil;
import com.brainsoon.common.util.excel.ExcelImportUtil;
import com.brainsoon.docviewer.model.ResConverfileTask;
import com.brainsoon.docviewer.service.IResConverfileTaskService;
import com.brainsoon.resource.action.BresAction;
import com.brainsoon.resource.po.UploadTaskDetail;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.ICollectResService;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.support.ImportResExcelFile;
import com.brainsoon.resource.support.ResourceTypeUtils;
import com.brainsoon.resource.support.SemanticResponse;
import com.brainsoon.resource.util.FTPClientUtils;
import com.brainsoon.resource.util.RandomNumberGenerator;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CommonMetaData;
import com.brainsoon.semantic.ontology.model.ExtendMetaData;
import com.brainsoon.semantic.ontology.model.Organization;
import com.brainsoon.semantic.ontology.model.OrganizationItem;
import com.brainsoon.semantic.vocabulary.CommonDCTerms;
import com.brainsoon.semantic.vocabulary.ExtendDCTerms;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.IBookService;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants.ConsumeType;
import com.brainsoon.system.support.SystemConstants.EducationPeriod;
import com.brainsoon.system.support.SystemConstants.ImportStatus;
import com.brainsoon.system.support.SystemConstants.Language;
import com.brainsoon.system.support.SystemConstants.NodeType;
import com.brainsoon.system.support.SystemConstants.OpeningRate;
import com.brainsoon.system.support.SystemConstants.ResourceMoudle;
import com.brainsoon.system.support.SystemConstants.ResourceStatus;
import com.brainsoon.system.support.SystemConstants.ResourceType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
@Service
public class CollectResService  extends BaseService implements ICollectResService{
   private final static String FTP_LOCAL_MAPPING = WebappConfigUtil.getParameter("FTP_LOCAL_MAPPING");
   private static final String CA_SAVE_URL=WebappConfigUtil.getParameter("CA_SAVE_URL");
   private static final String CA_OVERRIDE_URL=WebappConfigUtil.getParameter("CA_OVERRIDE_URL");
   private static final String CA_SAVE_NODE_URL=WebappConfigUtil.getParameter("CA_SAVE_NODE_URL");
   private static final String CA_DEL_NODE_URL=WebappConfigUtil.getParameter("CA_DEL_NODE_URL");
   public  static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
   private static final String dateFormat="yyyy-MM-dd HH:mm:ss:SSS";
   @Autowired 
   private IResourceService resourceService;
   @Autowired 
   private IResConverfileTaskService resConverfileTaskService;
   @Autowired
   private IBaseSemanticSerivce baseSemanticSerivce;
   @Autowired
   private IBookService  bookService;
   @Autowired
   private ISysParameterService sysParameterService;
   /**
    * 创建组合资源
    * @param ca
    * @param jsonTree
    * @param nodeAsset
    * @param ogId
    * @throws Exception
    */
   public void saveCollectRes(Ca ca,String jsonTree,String nodeAsset,String ogId,String thumbFile,String repeatType) throws Exception{
		ca.setType("1");
		if(ca.getObjectId().equals("-1")){
			ca.setObjectId("");
		}
		Map<String,String> nodeAssetMap=new HashMap<String, String>();
		if(nodeAsset.length()>1){
			nodeAsset=nodeAsset.substring(0,nodeAsset.length()-1);
			String[] array=nodeAsset.split(";");
			for(int i=0;i<array.length;i++){
				String[] tmp=array[i].split(",");
				nodeAssetMap.put(tmp[0], tmp[1]);
			}
		}
		
		Gson gson=new Gson();
		List<OrganizationItem> items = gson.fromJson(jsonTree, new TypeToken<List<OrganizationItem>>(){}.getType());
		List<OrganizationItem> endItems=new ArrayList<OrganizationItem>();
		if(items!=null){
		for(OrganizationItem item:items){
			if(nodeAssetMap.containsKey(item.getNodeId())){
				List<String> assets=new ArrayList<String>();
				assets.add(nodeAssetMap.get(item.getNodeId()));
				item.setAssets(assets);
			}
			if(!ca.getObjectId().equals("")){
				item.setCaId(ca.getObjectId());
			}
			if(!ogId.equals("")){
				item.setOgId(ogId);
			}
			
			endItems.add(item);
		}
		}
		Organization org=new Organization();
		org.setName("");
		org.setOrganizationItems(endItems);
		org.setCaId(ca.getObjectId());
		if(!ogId.equals("")){
			org.setObjectId(ogId);
		}
		List<Organization> organizations=new ArrayList<Organization>();
		organizations.add(org);
	//	ca.setOrganizations(organizations);
		Gson gsonFormat=new GsonBuilder().setPrettyPrinting().create();
		String formateJson=gsonFormat.toJson(ca);
		logger.debug(formateJson);
		Asset asset=new Asset();
		CommonMetaData  commonMetaData =ca.getCommonMetaData();
		asset.setCommonMetaData(commonMetaData);
//		asset.setExtendMetaData(ca.getExtendMetaData());
		String doi = ResourceTypeUtils.getDOIByAsset(asset);
		commonMetaData.setIdentifier(doi);
		commonMetaData.setStatus(ResourceStatus.STATUS0);
		commonMetaData.setModified_time(DateUtil.convertDateToString(dateFormat, new Date()));
		if(commonMetaData.getResVersion() == null || "".equals(commonMetaData.getResVersion())){
			commonMetaData.setResVersion("1");
		}
		String keyword = commonMetaData.getKeywords();
		if(StringUtils.isNotBlank(keyword) && keyword.indexOf("，")>=0){
			keyword = keyword.replaceAll("，", ",");
			commonMetaData.setKeywords(keyword);
		}
		if(ca.getObjectId() == null || "".equals(ca.getObjectId())){
			commonMetaData.putCommonMetaData("create_time", DateUtil.convertDateToString(dateFormat, new Date()));
			String module = commonMetaData.getModule();
			String type = commonMetaData.getType();
			//按要求生成目录
			String parentPath = resourceService.createParentPath(module, type, doi);
			//创建封面
			String relatePath = createThumb(parentPath,thumbFile);
			commonMetaData.setPath(relatePath);
		}else{
			if(thumbFile!=null && !"".equals(thumbFile)){
				String module = commonMetaData.getModule();
				String type = commonMetaData.getType();
				//按要求生成目录
				String parentPath = resourceService.createParentPath(module, type, doi);
				File parentPathDir = new File(parentPath);
				if(parentPathDir.exists() && parentPathDir.isDirectory()){
					File[] files = parentPathDir.listFiles();
					if(files!=null && files.length>0){
						for(File file:files){
							file.delete();
						}
						String relatePath = createThumb(parentPath,thumbFile);
						commonMetaData.setPath(relatePath);
					}
				}
			}
		}
		ca.setCommonMetaData(commonMetaData);
		String paraJson=gson.toJson(ca);
		logger.debug(paraJson);
	    HttpClientUtil http = new HttpClientUtil();
	    if("2".equals(repeatType)){
	    	http.postJson(CA_OVERRIDE_URL,paraJson);
		}else{
			http.postJson(CA_SAVE_URL,paraJson);
		}
	    
   }
   public void downloadBookRes(HttpServletRequest request,HttpServletResponse response, String objectIds, String encryptPwd){
		if(StringUtils.isNotBlank(objectIds)){
			String [] ids = StringUtils.split(objectIds,",");
			String parentPath = BresAction.FILE_TEMP + UUID.randomUUID().toString() + File.separator;
			String title = "资源";
			String dictFile = "fileSize";
			Long srcSizeNum = (long) 0;
			Long dictSizeNum = (long) 0;
			List<SysParameter> dictSize = sysParameterService.findParaValue(dictFile);
			if(dictSize!=null && dictSize.size()>0){
				dictSizeNum =  Long.parseLong(dictSize.get(0).getParaValue());
			}
			for (String objectId : ids) {
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail= http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL")+"?id="+objectId);
				Gson gson=new Gson();
				Ca ca=gson.fromJson(resourceDetail, Ca.class);
				
				title = ca.getCommonMetaData().getTitle();
//				List<Organization>  organizations= ca.getOrganizations();
//				File parent = new File(parentPath + title + "_" + RandomNumberGenerator.generateNumber3());
//				parent.mkdirs();
//				for (Organization organization : organizations) {
//					List<OrganizationItem> organizationItems = organization.getOrganizationItems();
//					for(OrganizationItem organizationItem:organizationItems){
//						List<com.brainsoon.semantic.ontology.model.File> files = organizationItem.getFiles();
//						if(files!=null){
//							for(com.brainsoon.semantic.ontology.model.File file : files){
//								try {
//									File srcFile = new File(FILE_ROOT + file.getPath());
//									if(srcFile.exists() && !srcFile.isDirectory()){
//										srcSizeNum =srcSizeNum +srcFile.length();
//										FileUtils.copyFileToDirectory(new File(FILE_ROOT + file.getPath()), parent);
//									}
//								} catch (IOException e) {
//									logger.error(e.getMessage());
//								}
//							}
//						}
//					}
//				}
			}
			String zipName = "";
			if(ids.length>1){
				//压缩parent目录
				zipName = BresAction.FILE_TEMP + "资源包.zip";
			}else{
				zipName = BresAction.FILE_TEMP + title + ".zip";
			}
			if(srcSizeNum<= dictSizeNum){
			try {
				ZipUtil.zipFileOrFolder(parentPath, zipName, null);
				if(StringUtils.isNotBlank(encryptPwd)){
					String encryptZip = BresAction.FILE_TEMP + "资源加密.zip";
					ZipUtil.encryptZipFile(new File(zipName), encryptZip, encryptPwd);
					resourceService.downloadFile(request, response, encryptZip, false);
				}else{
					resourceService.downloadFile(request, response, zipName, false);
				}
			} catch (Exception e) {
				logger.error("压缩出现问题" + e.getMessage());
			}
			}else{
				logger.error("下载文件超过限制大小");
			}
		}
	}
   
   public List<CatalogDTO> getResDirAndFile(String id){
	   List<CatalogDTO> catalogDTOs = new ArrayList<CatalogDTO>();
		if(StringUtils.isNotBlank(id)){
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail= "";
				if(id.indexOf("publish")>0){
					resourceDetail= http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL")+"?id="+id);
				}else{
					resourceDetail= http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL")+"?id="+id);
				}
				Gson gson=new Gson();
				Ca ca=gson.fromJson(resourceDetail, Ca.class);
//				List<Organization>  organizations= ca.getOrganizations();
//				for (Organization organization : organizations) {
//					List<OrganizationItem> organizationItems = organization.getOrganizationItems();
//					for(OrganizationItem organizationItem:organizationItems){
//								if(id.indexOf("book")>0){
//									CatalogDTO catalogDTO= new CatalogDTO(organizationItem.getNodeId(),organizationItem.getPid(),organizationItem.getName(),organizationItem.getPath());
//									catalogDTOs.add(catalogDTO);
//								}else{
//									List<com.brainsoon.semantic.ontology.model.File> files = organizationItem.getFiles();
//									String path = "";
//									if(files !=null && files.size()>0){
//										path = files.get(0).getPath();
//									}
//									CatalogDTO catalogDTO= new CatalogDTO(organizationItem.getNodeId(),organizationItem.getPid(),organizationItem.getName(),path);
//									catalogDTOs.add(catalogDTO);
//								}
//								
//					}
//				}
		}
		return catalogDTOs;
	}
   /**
    * 创建图书资源
    * @param ca
    * @param uploadFile
    * @throws Exception
    */
   public String saveBookRes(Ca ca,String uploadFile,String repeatType) throws Exception{
		ca.setType("2");
		if(ca.getObjectId()!=null&&ca.getObjectId().equals("-1")){
			ca.setObjectId("");
		}
		Asset asset=new Asset();
		CommonMetaData  commonMetaData =ca.getCommonMetaData();
		String keyword = commonMetaData.getKeywords();
		if(StringUtils.isNotBlank(keyword) && keyword.indexOf("，")>=0){
			keyword = keyword.replaceAll("，", ",");
			commonMetaData.setKeywords(keyword);
		}
		commonMetaData.setFascicule("");
		asset.setCommonMetaData(commonMetaData);
//		asset.setExtendMetaData(ca.getExtendMetaData());
		String doi = ResourceTypeUtils.getDOIByAsset(asset);
		//文件存储
		String module = commonMetaData.getModule();
		String type = commonMetaData.getType();
		
		//按要求生成目录
		String parentPath = resourceService.createParentPath(module, type, doi);
		File realFile = new File(BresAction.FILE_TEMP + uploadFile);
		File destFile = resourceService.saveFile(parentPath, realFile,false);
		String[] str = destFile.getName().split("\\.");
		String filetype = str[str.length - 1];
		String unZipDir=parentPath+File.separator+destFile.getName().substring(0,destFile.getName().lastIndexOf("."));
		ZipOrRarUtil.unzip(destFile.getAbsolutePath(),unZipDir , filetype);
		String coverDir = unZipDir+"/cover";
		File coverDirFile = new File(coverDir);
		if(coverDirFile.exists()){
			File[] listFile = coverDirFile.listFiles();
			if(listFile != null && listFile.length>0){
				String coverPath = listFile[0].getAbsolutePath();
				String fileType = coverPath.substring(coverPath.lastIndexOf(".")+1,coverPath.length());
				if("jpg".equalsIgnoreCase(fileType) || "png".equalsIgnoreCase(fileType) || "gif".equalsIgnoreCase(fileType)){
					coverPath = coverPath.replaceAll("\\\\", "/");
					commonMetaData.setPath(coverPath.replace(FILE_ROOT, ""));
				}
			}
		}
		destFile.delete();
		OrganizationItem rootItem=new OrganizationItem();
		rootItem.setName("文件列表");
		rootItem.setNodeId("-1");
		rootItem.setXpath("-1");
		List<OrganizationItem> items=new ArrayList<OrganizationItem>();
		items=getItems(new File(unZipDir), rootItem, items);
		Organization org=new Organization();
		org.setOrganizationItems(items);
		List<Organization> organizations=new ArrayList<Organization>();
		organizations.add(org);
	//	ca.setOrganizations(organizations);
		commonMetaData.setIdentifier(doi);
		commonMetaData.setStatus(ResourceStatus.STATUS0);
		commonMetaData.setModified_time(DateUtil.convertDateToString(dateFormat, new Date()));
		if(commonMetaData.getResVersion() == null || "".equals(commonMetaData.getResVersion())){
			commonMetaData.setResVersion("1");
		}
		ca.setCommonMetaData(commonMetaData);
		Gson gson=new Gson();
		String paraJson=gson.toJson(ca);
		logger.debug(paraJson);
	    HttpClientUtil http = new HttpClientUtil();
	    String result="";
	    if("2".equals(repeatType)){
	    	result = http.postJson(CA_OVERRIDE_URL,paraJson);
		}else{
			result = http.postJson(CA_SAVE_URL,paraJson);
		}
	    SemanticResponse rtn = gson.fromJson(result, SemanticResponse.class);
	    if(rtn.getState() != 0){
			throw new ServiceException("资源报错异常");
		}else{
			ResConverfileTask rcft = new ResConverfileTask();
			rcft.setResId(rtn.getObjectId());
			rcft.setSrcPath(unZipDir.replace(FILE_ROOT, ""));
			rcft.setPlatformId(1);
			rcft.setDoHasType("0"); //处理类型：0，文档或者视频转换（转换服务）  1，抽取封面文件，2抽取文本
			resConverfileTaskService.saveResConverfileTask(rcft);
		    return rtn.getObjectId();
		}
  }
   /**
    * 批量导入图书创建Ca对象
    * @param ca
    * @param uploadFile
    * @throws Exception
    */
   public String saveImportBookRes(Asset asset){
		Ca ca = new Ca();
		ca = createCaObject(asset, ca);
		Gson gson=new Gson();
		String paraJson=gson.toJson(ca);
		logger.debug(paraJson);
	    HttpClientUtil http = new HttpClientUtil();
	    String result=http.postJson(CA_SAVE_URL,paraJson);
	    SemanticResponse rtn = gson.fromJson(result, SemanticResponse.class);
	    ResConverfileTask rcft = new ResConverfileTask();
		rcft.setSrcPath(ca.getConvertPath());
		rcft.setResId(rtn.getObjectId());
		rcft.setPlatformId(1);
		rcft.setDoHasType("0"); //处理类型：0，文档或者视频转换（转换服务）  1，抽取封面文件，2抽取文本
		resConverfileTaskService.saveResConverfileTask(rcft);
	//    SysOperateLogUtils.addLog("res_imp_book_"+ca.getCommonMetaData().getLibType(), "资源标题："+ ca.getCommonMetaData().getTitle(), LoginUserUtil.getLoginUser());
	    return rtn.getObjectId();
  }
   /**
    * 批量导入图书覆盖Ca对象
    * @param ca
    * @param uploadFile
    * @throws Exception
    */
   public String overrideImportBookRes(Asset asset,Ca ca){
		ca = createCaObject(asset, ca);
		Gson gson=new Gson();
		String paraJson=gson.toJson(ca);
		logger.debug(paraJson);
	    HttpClientUtil http = new HttpClientUtil();
	    String result=http.postJson(CA_OVERRIDE_URL,paraJson);
	    SemanticResponse rtn = gson.fromJson(result, SemanticResponse.class);
	    ResConverfileTask rcft = new ResConverfileTask();
		rcft.setSrcPath(ca.getConvertPath());
		rcft.setResId(rtn.getObjectId());
		rcft.setPlatformId(1);
		rcft.setDoHasType("0"); //处理类型：0，文档或者视频转换（转换服务）  1，抽取封面文件，2抽取文本
		resConverfileTaskService.saveResConverfileTask(rcft);
	    return rtn.getObjectId();
  }
   
   /**
    * 覆盖Ca对象
    * @param ca
    * @param uploadFile
    * @throws Exception
    */
   public Ca overrideCaRes(Ca ca){
		Gson gson=new Gson();
		String paraJson=gson.toJson(ca);
		logger.debug(paraJson);
	    HttpClientUtil http = new HttpClientUtil();
	    String result=http.postJson(CA_OVERRIDE_URL,paraJson);
	    return ca;
  }
   /**
    * 创建ca对象
    * @param ca
    * @param uploadFile
    * @throws Exception
    */
   public Ca createCaObject(Asset asset,Ca ca){
	    logger.info("******gfffffffffffffffffff**********");
		ca.setType("2");
		if(ca.getObjectId()!=null&&ca.getObjectId().equals("-1")){
			ca.setObjectId("");
		}
		CommonMetaData  commonMetaData =asset.getCommonMetaData();
		CommonMetaData  commonMetaDataTemp =ca.getCommonMetaData();
		if(commonMetaData.getResVersion()==null || "".equals(commonMetaData.getResVersion())){
			if(commonMetaDataTemp==null){
				commonMetaData.setResVersion("1");
			}else{
				if(commonMetaDataTemp.getResVersion()==null || "".equals(commonMetaDataTemp.getResVersion())){
					commonMetaData.setResVersion("1");
				}
			}
		}
		Map<String, String> commonMetaDatas = commonMetaData.getCommonMetaDatas();
		commonMetaData.setFascicule("");
		String doi = ResourceTypeUtils.getDOIByAsset(asset);
		//文件存储
		String module = commonMetaData.getModule();
		String type = commonMetaData.getType();
		//赋值文件路径
		String uploadFile = FTP_LOCAL_MAPPING + commonMetaDatas.get("filePath") + File.separator + commonMetaDatas.get("fileName");
		//按要求生成目录
		String parentPath = resourceService.createParentPath(module, type, doi);
		String newDir =parentPath+File.separator+commonMetaDatas.get("fileName");
		if(!new File(newDir).exists()){
			new File(newDir).mkdirs();
		}
		try{
			FileUtil.copyDir(new File(uploadFile), new File(newDir));
		}catch(Exception e){
			logger.info("文件目录拷贝出错");
		}
		ca.setConvertPath(newDir.replace(FILE_ROOT, ""));
		logger.info("******qqqqqqqqqqqqqqqqqqqqqqqqqqqq**********");
	//	destFile.delete();
		OrganizationItem rootItem=new OrganizationItem();
		rootItem.setName("文件列表");
		rootItem.setNodeId("-1");
		rootItem.setXpath("-1");
		List<OrganizationItem> items=new ArrayList<OrganizationItem>();
		items=getItems(new File(newDir), rootItem, items);
		Organization org=new Organization();
		org.setOrganizationItems(items);
		List<Organization> organizations=new ArrayList<Organization>();
		organizations.add(org);
	//	ca.setOrganizations(organizations);
		commonMetaData.setIdentifier(doi);
		commonMetaData.setStatus(ResourceStatus.STATUS0);
		commonMetaData.setModified_time(DateUtil.convertDateToString(dateFormat, new Date()));
		if(ca.getObjectId() == null || "".equals(ca.getObjectId())){
			commonMetaData.putCommonMetaData("create_time", DateUtil.convertDateToString(dateFormat, new Date()));
		}
		logger.info("******sssssssssssssss**********");
		ca.setCommonMetaData(commonMetaData);
//		ca.setExtendMetaData(asset.getExtendMetaData());
	    return ca;
  }
   //创建封面
   public String createThumb(String path,String thumbFilePath){
	   String fileType = StringUtils.substringAfterLast(thumbFilePath, ".");
	   String thumbPath = path + "/thumb/cover."+fileType;
	   thumbPath = thumbPath.replaceAll("\\\\", "/");
		File thumbFile = new File(thumbPath);
		if(!thumbFile.getParentFile().exists()){
			thumbFile.getParentFile().mkdir();
		}
		//保存缩略图
		File realFile = new File(BresAction.FILE_TEMP + thumbFilePath);
		try {
			FileUtils.copyFile(realFile, thumbFile);
			realFile.getParentFile().delete();
		} catch (IOException e) {
			logger.error("缩略图保存失败" + e.getMessage());
		}
		thumbPath = StringUtils.replace(thumbPath, FILE_ROOT, "");
		return thumbPath;
   }
   /**
    * 增加版权信息
    * @param ca
    * @param uploadFile
    * @throws Exception
    */
   public String saveBookCopyright(Ca ca) throws Exception{
	    logger.debug("******run at saveRes***********");
		logger.debug("jsonTree ");
		Gson gson=new Gson();
		String paraJson=gson.toJson(ca);
		logger.debug(paraJson);
	    HttpClientUtil http = new HttpClientUtil();
	    String result=http.postJson(CA_SAVE_URL,paraJson);
	    logger.debug("result *** "+result);
	    return result;
   }
   /**
    * 递归获取图书目录
    * @param paretFile
    * @param parentItem
    * @param items
    * @return
    */
   public List<OrganizationItem> getItems(File paretFile,OrganizationItem parentItem,List<OrganizationItem> items){
	   OrganizationItem item=null;
	   com.brainsoon.semantic.ontology.model.File itemFile=null;
	   if(paretFile.isDirectory()){
		  File[] children= paretFile.listFiles(); 
		  for(File child:children){
			  item=new OrganizationItem();
			  item.setNodeId(String.valueOf(UID.next()));
			  item.setName(child.getName());
			  item.setPid(parentItem.getNodeId());
			  item.setXpath(parentItem.getXpath()+","+item.getNodeId());
			  item.setPath(child.getAbsolutePath().replace(File.separator, "/").replace(FILE_ROOT, ""));
			  if(child.isFile()){
				  itemFile=new com.brainsoon.semantic.ontology.model.File();
				  itemFile.setName(child.getName());
				  itemFile.setPath(child.getAbsolutePath().replace(File.separator, "/").replace(FILE_ROOT, ""));
				  List files=new ArrayList<com.brainsoon.semantic.ontology.model.File>();
				  files.add(itemFile);
				  item.setFiles(files);
			  }
			  items.add(item);
              if(child.isDirectory()){
            	  getItems(child,item,items);
			  }
		  }
	   }
	   return items;
   }
   
   /**
    * 修改组合资源
    * @param ca
    * @throws Exception
    */
   public void updateCollectRes(Ca ca,String thumbFile) throws Exception{
	    logger.debug("******run at updateRes***********");
	    ca.setType("1");
		Gson gson=new Gson();
		Gson gsonFormat=new GsonBuilder().setPrettyPrinting().create();
		String formateJson=gsonFormat.toJson(ca);
		logger.debug(formateJson);
		CommonMetaData  commonMetaData =ca.getCommonMetaData();
		commonMetaData.setStatus(ResourceStatus.STATUS0);
		commonMetaData.setModified_time(DateUtil.convertDateToString(dateFormat, new Date()));
		Asset asset=new Asset();
		asset.setCommonMetaData(commonMetaData);
//		asset.setExtendMetaData(ca.getExtendMetaData());
		String doi = ResourceTypeUtils.getDOIByAsset(asset);
		commonMetaData.setIdentifier(doi);
		if(thumbFile!=null && !"".equals(thumbFile)){
			String module = commonMetaData.getModule();
			String type = commonMetaData.getType();
			//按要求生成目录
			String parentPath = resourceService.createParentPath(module, type, doi);
			File parentPathDir = new File(parentPath);
			if(parentPathDir.exists() && parentPathDir.isDirectory()){
				File[] files = parentPathDir.listFiles();
				if(files!=null && files.length>0){
					for(File file:files){
						file.delete();
					}
				}
				String relatePath = createThumb(parentPath,thumbFile);
				commonMetaData.setPath(relatePath);
			}
		}
		String keyword = commonMetaData.getKeywords();
		if(StringUtils.isNotBlank(keyword) && keyword.indexOf("，")>=0){
			keyword = keyword.replaceAll("，", ",");
			commonMetaData.setKeywords(keyword);
		}
		ca.setCommonMetaData(commonMetaData);
		String paraJson=gson.toJson(ca);
		logger.debug(paraJson);
	    HttpClientUtil http = new HttpClientUtil();
	    String result=http.postJson(CA_SAVE_URL,paraJson);
	    logger.debug("result *** "+result);
   }
   /**
	 * 批量删除资源对象，逗号分隔
	 * @param ids
	 * @throws IOException
	 */
	public void deleteByIds(String ids) throws IOException{
		if(StringUtils.isNotBlank(ids)){
			Map<String, Object> parameters = new HashMap<String, Object>();
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			String [] idsArr = StringUtils.split(ids, ",");
			for (String id : idsArr) {
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail= http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL")+"?id="+id);
				Gson gson=new Gson();
				Ca ca=gson.fromJson(resourceDetail, Ca.class);
				baseSemanticSerivce.deleteCaResourceById(id);
				if(id.indexOf("book")>0){
//					List<Organization> organizations = ca.getOrganizations();
//					if(organizations !=null && organizations.size()>0){
//						Organization organization = organizations.get(0);
//						List<OrganizationItem> organizationItems = organization.getOrganizationItems();
//						if(organizationItems!=null && organizationItems.size()>0){
//							for(OrganizationItem organizationItem:organizationItems){
//								String path = organizationItem.getPath();
//								if(StringUtils.isNotBlank(path)){
//									ResConverfileTask rcft = new ResConverfileTask();
//									rcft.setResId(id);
//									rcft.setSrcPath(path);
//									resConverfileTaskService.deleteConverfileTask(rcft);
//									File f = new File(FILE_ROOT + path);
//									if(f.exists()){
//										//删除文件
//										if(f.isDirectory()){
//											File parentFile = f.getParentFile();
//											if(parentFile.exists()){
//												File pparentFile = parentFile.getParentFile();
//												if(pparentFile.exists()){
//													FileUtils.deleteDirectory(pparentFile);
//												}
//											}
//										}else{
//											try{
//												FileUtils.forceDelete(f.getParentFile());
//											}catch (Exception e) {
//												logger.error("删除文件失败"+e.getMessage()+f.getParentFile().getAbsolutePath());
//											}
//										}
//									}
//								}
//							}
//						}
//					}
				}
				parameters.put("objectId", id);
				String hql = "delete from ResTargetData  where objectId=:objectId and platformId="+userInfo.getPlatformId();
				baseDao.executeUpdate(hql, parameters);
				SysOperateLogUtils.addLog("ca_delete", "资源标题："+ ca.getCommonMetaData().getTitle(), LoginUserUtil.getLoginUser());
			}
		}
	}
   /**
    * 更新目录节点
    * @param nodeJson
    */
   public String updateNode(String nodeJson,String nodeAsset,String oldPath) throws Exception{
	    String objectId="-1";
	    Map<String,String> nodeAssetMap=new HashMap<String, String>();
		if(nodeAsset.length()>1){
			nodeAsset=nodeAsset.substring(0,nodeAsset.length()-1);
			String[] array=nodeAsset.split(";");
			for(int i=0;i<array.length;i++){
				String[] tmp=array[i].split(",");
				nodeAssetMap.put(tmp[0], tmp[1]);
			}
	   }
	   Gson gson=new Gson();
	   OrganizationItem item=gson.fromJson(nodeJson, OrganizationItem.class);
	   String caId = item.getCaId();
	   String realPath = "";
	   if(StringUtils.isNotBlank(item.getPath())){   //文件重命名
		   if(caId.indexOf("book")>0){
//			    String oldRelatePath = item.getPath();
//			   	oldRelatePath = oldRelatePath.replaceAll("\\\\", "/");
			    String oldRealPath=FILE_ROOT+oldPath;
		    	File oldRealFile=new File(oldRealPath);
		    	File newRealFile = new File(FILE_ROOT+item.getPath());
		    	
		    	ResConverfileTask rcft = new ResConverfileTask();
				rcft.setResId(caId);
				rcft.setSrcPath(item.getPath());
				rcft.setOldSrcPath(oldPath);
				String desc = resConverfileTaskService.updateConverfileTask(rcft);
		//		if(StringUtils.isBlank(desc)){
					if(!newRealFile.exists()){
			    		newRealFile.mkdirs();
			    	}
			    	if(!oldPath.equals(item.getPath())){
			    		FileUtils.copyDirectory(oldRealFile, newRealFile);
			    		if(oldRealFile.exists() && oldRealFile.isDirectory()){
			    			FileUtils.deleteDirectory(oldRealFile);
			    		}else if(oldRealFile.exists() && oldRealFile.isFile()){
			    			FileUtils.forceDelete(oldRealFile);
			    		}
			    	}
			//	}else{
					logger.debug(desc);
		//		}
		   }
	   }
	   if(item.getFiles()!=null&&item.getFiles().size()>0){  //添加新文件
		    realPath=FILE_ROOT+item.getFiles().get(0).getPath();
		    if(caId.indexOf("book")>0){
			   ResConverfileTask rcft = new ResConverfileTask();
			   rcft.setResId(caId);
			   rcft.setSrcPath(realPath.replace(FILE_ROOT, ""));
			   resConverfileTaskService.saveResConverfileTask(rcft);
			}
	   }
	   if(nodeAssetMap.containsKey(item.getNodeId())){
			List<String> assets=new ArrayList<String>();
			assets.add(nodeAssetMap.get(item.getNodeId()));
			item.setAssets(assets);
	   }
	   String paraJson=gson.toJson(item);
	   logger.debug(paraJson);
	   HttpClientUtil http = new HttpClientUtil();
	   String result=http.postJson(CA_SAVE_NODE_URL,paraJson);
	   SemanticResponse rtn = gson.fromJson(result, SemanticResponse.class);
	   objectId= rtn.getObjectId();
	   return objectId;
   }
   
   /**
    *  删除目录节点
    * @param caId
    * @param nodeId
    * @throws Exception
    */
	public void deleteNode(String caId, String nodeId,String path) throws Exception {
		HttpClientUtil http = new HttpClientUtil();
		String result = http.executeGet(CA_DEL_NODE_URL + "?caId=" + caId
				+ "&nodeId=" + nodeId);
		if(caId.indexOf("book")>0){
			if(StringUtils.isNotBlank(path)){
				ResConverfileTask rcft = new ResConverfileTask();
				rcft.setSrcPath(path);
				rcft.setResId(caId);
				resConverfileTaskService.deleteConverfileTask(rcft);
				File f = new File(FILE_ROOT + path);
				if(f.exists()){
					//删除文件
					if(f.isDirectory()){
						FileUtils.deleteDirectory(f);
					}else{
						try{
							FileUtils.forceDelete(f);
						}catch (Exception e) {
							logger.error("删除文件失败"+e.getMessage()+f.getParentFile().getAbsolutePath());
						}
					}
				}
			}
		}
	}
   
   
   /**
	 * 分页查询，智能添加前台参数，读取rdf库
	 * @param request
	 * @param conditionList
	 * @return String json
	 */
	public String queryResource4Page(HttpServletRequest request,QueryConditionList conditionList,String queryUrl){
		StringBuffer hql = new StringBuffer();
		hql.append("page=").append(request.getParameter("page")).append("&size=").append(conditionList.getPageSize());
		hql.append("&sort=").append(request.getParameter("sort")).append("&order=").append(request.getParameter("order"));
		//组装查询
		if(null != conditionList){
			List<QueryConditionItem> items = conditionList.getConditionItems();
			for (QueryConditionItem queryConditionItem : items) {
				String filedName = queryConditionItem.getFieldName();
				try {
					hql.append("&").append(filedName).append("=").append(URLEncoder.encode(queryConditionItem.getValue()+"","utf-8"));
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage());
				}
			}
		}
		HttpClientUtil http = new HttpClientUtil();
		String authResCodes=LoginUserUtil.getAuthResCodes();
		String authTypes=LoginUserUtil.getAuthResTypes();
		//hql.append("&privilage=").append(authResCodes).append("&authType=").append(authTypes);
		String result=http.executeGet(queryUrl+"?"+hql.toString());
		logger.debug("result************ ");
		return result;
	}
	/**
	 * 执行导入任务
	 */
	public UploadTask doImportTask(ImportResExcelFile taskData) throws Exception{
		File excel = taskData.getExcel();
		String module = taskData.getModule();
		String repeatType = taskData.getRepeatType();
//		String repeatRelevanceType = taskData.getRepeatRelevanceType();
		String libType = taskData.getLibType();
		UploadTask task = null;
		Map<String, String> poExcelMap=new LinkedHashMap<String, String>();
		poExcelMap.put("title", "标题");
		poExcelMap.put("keywords","关键字");
		poExcelMap.put("knowledge_point", "知识点");
		poExcelMap.put("type", "资源类型");
		poExcelMap.put("source", "来源");
		poExcelMap.put("thumbnail", "缩略图");
		poExcelMap.put("description", "摘要");
		poExcelMap.put("audience", "适用对象");
		poExcelMap.put("down_type", "下载资源消费类型");
		poExcelMap.put("down_value", "下载资源消费值");
		poExcelMap.put("view_type", "观看资源消费类型");
		poExcelMap.put("view_value", "观看消费值");
		poExcelMap.put("language", "语种");
		poExcelMap.put("resPath", "资源文件路径");
		poExcelMap.put("resFileName", "文件名");
		poExcelMap.put("educational_phase", "学段");
		poExcelMap.put("grade", "年级");
		poExcelMap.put("subject", "科目");
		poExcelMap.put("version", "教材版本");
		poExcelMap.put("fascicule", "分册");
		poExcelMap.put("chapter", "章");
		poExcelMap.put("section", "节");
		poExcelMap.put("lesson", "课");
		poExcelMap.put("creator", "制作者姓名");
		poExcelMap.put("email", "制作者邮箱");
		poExcelMap.put("public_or_not", "资源共享度");
		poExcelMap.put("difficulty_level", "难易程度");
		poExcelMap.put("copyright", "版权所属");
		poExcelMap.put("purchase_price", "购买价格");
		poExcelMap.put("rating", "评价等级");
		poExcelMap.put("reviewer", "评价者");
		poExcelMap.put("review", "评价描述");
		poExcelMap.put("ISBN", "ISBN");
		poExcelMap.put("alphabetTitle", "书名拼音");
		poExcelMap.put("altTitle", "交替题名");
		poExcelMap.put("parTitle", "并列题名");
		poExcelMap.put("otherTitle", "其他题名");
		poExcelMap.put("serialname", "丛书名称");
		poExcelMap.put("cbclass", "中图分类号");
		poExcelMap.put("pages", "页数");
		poExcelMap.put("cip", "CIP核字号");
		poExcelMap.put("pprice", "纸质图书价格");
		poExcelMap.put("prtcnt", "印次");
		poExcelMap.put("volnum", "册号");
		poExcelMap.put("edicnt", "版次");
		poExcelMap.put("dctype", "著作方式");
		poExcelMap.put("clb", "丛书作者姓名");
		poExcelMap.put("dcsubject", "主题词");
		poExcelMap.put("dcpublisher", "出版社");
		poExcelMap.put("publishdate", "出版时间");
		ExcelImportUtil excelUtil=new ExcelImportUtil(poExcelMap);
		
		int successNum = 0;
		int realNum = -1;
		if(excel.exists()){
			task = new UploadTask();
			//解析excel文件
			Workbook book = null;
			try{
				book = Workbook.getWorkbook(excel);
				Sheet sheet  =  book.getSheet(0);
				//读取第二行批次号
				Cell cell = sheet.getCell(1, 1);
				String name = cell.getContents().trim();
				cell = sheet.getCell(4, 1);
				String batchNum = cell.getContents().trim();
				logger.debug("清单："+ name + "  批次：" + batchNum);
				task.setName(name);
				task.setBatchNum(batchNum);
				task.setCreateTime(new Date());
				task.setExcelPath(excel.getAbsolutePath());
				task.setStatus(ImportStatus.STATUS1);
				task.setRemark(taskData.getRemark());
				task = (UploadTask)create(task);
				getBaseDao().refresh(task);
				//开始处理数据
				task.setStatus(ImportStatus.STATUS1);
				int rowsLength = sheet.getRows();
				String title = "";
				//String keywords = "";
				//String knowledge_point = "";
				//String format = "";
				String type = "";
				String source = "";
				//String thumbnail = "";
				//String description = "";
				String down_type = "";
				String down_value = "";
				String view_type = "";
				String view_value = "";
				String language = "";
				
				String resPath = "";
				String resFileName = "";
				
				String educational_phase = "";
				String grade = "";
				String subject = "";
				//String version = "";
				//String fascicule = "";
				
				//String chapter = "";
				//String section = "";
				//String lesson = "";
				
				String creator = "";
				//String email = "";
				String public_or_not = "";
				
				Ca ca = null;
				UploadTaskDetail detail = null;
				
				String url = WebappConfigUtil.getParameter("FTP_URL");
				int port = WebappConfigUtil.getInteger("FTP_PORT",0);
				String username = WebappConfigUtil.getParameter("FTP_USERNAME");
				String password = WebappConfigUtil.getParameter("FTP_PASSWORD");
				//String relationIds = "";
				Cell[] headerCells=sheet.getRow(2);
				for (int i = 3; i < rowsLength; i++) {
					Cell [] cells = sheet.getRow(i);
					ca = new Ca();
					CommonMetaData commonMeta = new CommonMetaData();
					commonMeta.setType(ResourceType.TYPE5);
					ca.setCommonMetaData(commonMeta);
					ExtendMetaData extendMeta = new ExtendMetaData();
					ca.setExtendMetaData(extendMeta);
					commonMeta.setModule(module);
					detail = new UploadTaskDetail();
					detail.setCreateTime(new Date());
					detail.setTask(task);
					detail.setExcelNum(i);
					String error = "第【"+i+"】行导入失败";
					Map<String, String> commonMetaDatas = ca.getCommonMetaData().getCommonMetaDatas();
					Map<String, String> extendMetaDatas = extendMeta.getExtendMetaDatas();
					if(null == extendMetaDatas){
						extendMetaDatas = new HashMap<String, String>();
					}
					for(int j=0;j<cells.length;j++){
						Cell valCell=cells[j];
						String key=excelUtil.getPropertyNameByCellHeader(headerCells[j]);
						if(CommonDCTerms.existProperty(key)){
							excelUtil.setPropertyVal(commonMetaDatas, key, valCell);
						}
						else{
							excelUtil.setPropertyVal(extendMetaDatas, key, valCell);
						}
					}
					logger.debug("import row success");
					title = ca.getCommonMetaData().getTitle();
					if (StringUtils.isBlank(title)) {
						detail.setRemark(error+"，标题不合法");
						create(detail);
						continue;
					}
					type =ca.getCommonMetaData().getType();
					creator = ca.getCommonMetaData().getCreator();
					if (StringUtils.isBlank(creator)) {
						detail.setRemark(error+"，制作者姓名不合法");
						create(detail);
						continue;
					}
					source =ca.getCommonMetaData().getSource();
					if (StringUtils.isBlank(source)) {
						detail.setRemark(error+"，来源不合法");
						create(detail);
						continue;
					}
					commonMetaDatas.put("source", source);
					//查重，为了提高效率，先根据元数据查重，如果重复，直接覆盖
					/*List<Asset> repeatRes = baseSemanticSerivce.getResourceByMoreCondition(source, type, title, creator, "");
					if(repeatRes != null && repeatRes.size() > 0){
						if(StringUtils.equalsIgnoreCase(repeatType, "2")){
							//取重复资源第一个覆盖
							Asset ast = repeatRes.get(0);
							asset.setObjectId(ast.getObjectId());
							commonMeta.setObjectId(ast.getCommonMetaData().getObjectId());
							extendMeta.setObjectId(ast.getExtendMetaData().getObjectId());
						}else if(StringUtils.equalsIgnoreCase(repeatType, "3")){
							//忽略
							detail.setRemark("第【"+i+"】行重复，已忽略");
							create(detail);
							continue;
						}else if(StringUtils.equalsIgnoreCase(repeatType, "1")){
							//建立关联关系
							if(StringUtils.equalsIgnoreCase(repeatRelevanceType, "1")){
								relationIds = "";
								for (Asset asset2 : repeatRes) {
									if(StringUtils.isBlank(relationIds)){
										relationIds += asset2.getObjectId();
									}else{
										relationIds += "," + asset2.getObjectId();
									}
								}
							}
						}
					}*/
					
					//处理五个核心元数据
					educational_phase = ca.getCommonMetaData().getEducational_phase();
					educational_phase = EducationPeriod.getValueByDesc(educational_phase);
					if (StringUtils.isBlank(educational_phase)) {
						detail.setRemark(error+"，学段不合法");
						create(detail);
						continue;
					}
					commonMetaDatas.put("educational_phase", educational_phase);
					grade = ca.getCommonMetaData().getGrade();
					grade=bookService.getDictValueByName(grade, NodeType.TYPE2);
					if (StringUtils.isBlank(grade)) {
						detail.setRemark(error+"，年级不合法");
						create(detail);
						continue;
					}
					commonMetaDatas.put("grade", grade);
					subject = ca.getCommonMetaData().getSubject();
					subject=bookService.getDictValueByName(subject, NodeType.TYPE3);
					if (StringUtils.isBlank(subject)) {
						detail.setRemark(error+"，学科不合法");
						create(detail);
						continue;
					}
					commonMetaDatas.put("subject", subject);
					down_type = ca.getExtendMetaData().getDown_type();
					down_type = ConsumeType.getValueByDesc(down_type);
					extendMetaDatas.put("down_type", down_type);
					
					down_value = ca.getExtendMetaData().getDown_value();
					extendMetaDatas.put("down_value", down_value);
					
					view_type = ca.getExtendMetaData().getView_type();
					view_type = ConsumeType.getValueByDesc(view_type);
					extendMetaDatas.put("view_type", view_type);
					
					view_value = ca.getExtendMetaData().getView_value();
					extendMetaDatas.put("view_value", view_value);
					
					language = ca.getCommonMetaData().getLanguage();
					language = Language.getValueByDesc(language);
					commonMetaDatas.put("language", language);
					commonMetaDatas.put("libType", libType);
					
					resPath = extendMetaDatas.get("resPath");
					if (StringUtils.isBlank(resPath)) {
						detail.setRemark(error+"，资源文件路径不合法");
						create(detail);
						continue;
					}
					resFileName = extendMetaDatas.get("resFileName");
					if (StringUtils.isBlank(resFileName)) {
						detail.setRemark(error+"，文件名不合法");
						create(detail);
						continue;
					}
					
					String localPath = UUID.randomUUID().toString();
					//ftp下载文件到临时目录
					if(!FTPClientUtils.downloadFile(url, port, username, password, resPath, resFileName, BresAction.FILE_TEMP +localPath)){
						detail.setRemark(error+"，远程文件下载失败");
						create(detail);
						continue;
					}
					String uploadFile = localPath + File.separator + resFileName;
					//处理单元
					String code = module + "," + type + "," + educational_phase+"," + subject + "," + grade;;
					ca.addXpath(code);
					public_or_not = ca.getCommonMetaData().getPublic_or_not();
					public_or_not = OpeningRate.getValueByDesc(public_or_not);
					commonMetaDatas.put("public_or_not", public_or_not);
				    
					ca.getCommonMetaData().setCommonMetaDatas(commonMetaDatas);
					ca.getExtendMetaData().setExtendMetaDatas(extendMetaDatas);
					ca.setObjectId("");
					//保存资源
					String objectId = saveBookRes(ca, uploadFile,"");
					/*if(StringUtils.isNotBlank(objectId) && StringUtils.isNotBlank(relationIds)){
						//自动关联资源
						baseSemanticSerivce.assetRelation(objectId, relationIds);
					}*/
					detail.setRemark("第【"+i+"】行，导入成功！");
					detail.setStatus(1);
					create(detail);
					successNum ++;
				}
				realNum = rowsLength - 3;
				backWriteStatus(task, successNum, realNum);
			}catch(Exception e){
				e.printStackTrace();
				backWriteStatus(task, successNum, realNum);
			}finally{
				if(book!=null)
					book.close();
			}
		}
		
		return task;
	}
	public void backWriteStatus(UploadTask task, int successNum, int realNum) {
		if(successNum == 0){
			//全部失败
			task.setStatus(ImportStatus.STATUS4);
		}else if(realNum == successNum){
			//全部成功
			task.setStatus(ImportStatus.STATUS2);
		}else{
			//部分成功
			task.setStatus(ImportStatus.STATUS3);
		}
	}
	
	public static void main(String[] args) {
		
		try {
			FileUtils.deleteDirectory(new File("D:/a/b"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public String fileSize(HttpServletRequest request,
			HttpServletResponse response, String objectIds, String encryptPwd) {
			String boo = "0";
			Long srcSizeNum = (long) 0;
			Long dictSizeNum = (long) 0;
		if(StringUtils.isNotBlank(objectIds)){
			String [] ids = StringUtils.split(objectIds,",");
			String dictFile = "fileSize";
			List<SysParameter> dictSize = sysParameterService.findParaValue(dictFile);
			if(dictSize!=null && dictSize.size()>0){
				dictSizeNum =  Long.parseLong(dictSize.get(0).getParaValue());
			}
			for (String objectId : ids) {
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail= http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL")+"?id="+objectId);
				Gson gson=new Gson();
				Ca ca=gson.fromJson(resourceDetail, Ca.class);
//				List<Organization>  organizations= ca.getOrganizations();
//				for (Organization organization : organizations) {
//					List<OrganizationItem> organizationItems = organization.getOrganizationItems();
//					for(OrganizationItem organizationItem:organizationItems){
//						List<com.brainsoon.semantic.ontology.model.File> files = organizationItem.getFiles();
//						if(files!=null){
//							for(com.brainsoon.semantic.ontology.model.File file : files){
//									File srcFile = new File(FILE_ROOT + file.getPath());
//									if(srcFile.exists() && !srcFile.isDirectory()){
//										srcSizeNum =srcSizeNum +srcFile.length();
//									}
//							}
//						}
//					}
//				}
			}
			}else{
				logger.error("下载文件超过限制大小");
			}
		
			if(dictSizeNum>srcSizeNum){
				boo = "1";
			}
		return boo;
	}
}
