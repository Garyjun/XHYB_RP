package com.brainsoon.resource.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.support.SysResTypeCacheMap;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.action.BresAction;
import com.brainsoon.resource.po.Area;
import com.brainsoon.resource.po.City;
import com.brainsoon.resource.po.CopyrightImportResult;
import com.brainsoon.resource.po.CopyrightRepeat;
import com.brainsoon.resource.po.CopyrightWarning;
import com.brainsoon.resource.po.Province;
import com.brainsoon.resource.po.SearchEntryMonth;
import com.brainsoon.resource.po.UploadTaskDetail;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.service.IAreaService;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.ICollectResService;
import com.brainsoon.resource.service.ICopyrightService;
import com.brainsoon.resource.support.ImportResExcelFile;
import com.brainsoon.resource.support.ResourceTypeUtils;
import com.brainsoon.resource.support.SemanticResponse;
import com.brainsoon.resource.util.FTPClientUtils;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.AssetList;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CopyRightMetaData;
import com.brainsoon.semantic.ontology.model.FileList;
import com.brainsoon.semantic.ontology.model.MetaDataDC;
import com.brainsoon.semantic.ontology.model.MetadataResult;
import com.brainsoon.semantic.ontology.model.SearchParamCa;
import com.brainsoon.semantic.ontology.model.SearchResult;
import com.brainsoon.semantic.ontology.model.SearchResultCa;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.ResTarget;
import com.brainsoon.system.model.Role;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants.ConsumeType;
import com.brainsoon.system.support.SystemConstants.EducationPeriod;
import com.brainsoon.system.support.SystemConstants.ImportStatus;
import com.brainsoon.system.support.SystemConstants.Language;
import com.brainsoon.system.support.SystemConstants.OpeningRate;
import com.brainsoon.system.support.SystemConstants.ResourceMoudle;
import com.brainsoon.system.support.SystemConstants.ResourceType;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;

/**
 * 版权管理
 * 
 * @author zhangpeng
 */
@Service
@SuppressWarnings("rawtypes")
public class CopyrightService extends BaseService implements ICopyrightService {
	private static SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd");
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	private ICollectResService collectResService;
	@Autowired
	private ISysParameterService sysParameterService;
	
	private static final String PUBLISH_COPYRIGHTWARNING_URL = WebappConfigUtil
			.getParameter("PUBLISH_COPYRIGHTWARNING_URL");
	private final static String PUBLISH_QUERYBYPOST_URL = WebappConfigUtil.getParameter("PUBLISH_QUERYBYPOST_URL");
	private final static String SEARCH_ENTRY_MONTH = WebappConfigUtil.getParameter("SEARCH_ENTRY_MONTH");
	public FileList getFilesByDoi(String doi){
		return null;
	}
	/**
	 * 教育资源版权更新
	 */
	@Override
	public void updateCopyrightWarning() {

		String copyrightWarningDays = "";
		List<SysParameter> lists = sysParameterService.findParaValue("copyrightWarningDays");
		if(lists!=null && lists.size()>0){
			if(lists.get(0)!=null && lists.get(0).getParaValue()!=null){
				copyrightWarningDays = lists.get(0).getParaValue();
			}
		 }
		 Map<Object, Object> resTypeMap = SysResTypeCacheMap.getMapValue();
		 Iterator it = resTypeMap.entrySet().iterator();
		 String publishType = "";
		 while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				if(pairs.getKey()!=null && pairs.getValue()!=null){
					publishType = publishType+pairs.getKey()+",";
				}
				logger.info("----------------输出字段"+pairs.getKey()+"---"+pairs.getValue()+"--------------");
			}
		 if(publishType.endsWith(",")){
			publishType = publishType.substring(0,publishType.length()-1);
		 }
		 String publishTypeArray[] = publishType.split(",");
		 String authEndDateEndField = "";
		 String authStartDateField = "";
		 Map<String,String> dateType = new HashMap<String,String>();
		for(String type:publishTypeArray){
			List<MetadataDefinition> metadataDefinitions = MetadataSupport.getMetadateDefines(type);
			for (MetadataDefinition metadataDefinition : metadataDefinitions) {
				if (metadataDefinition.getIdentifier() != null && metadataDefinition.getIdentifier() == 15) {
					authEndDateEndField = metadataDefinition.getFieldName();
					dateType.put(metadataDefinition.getFieldName(), metadataDefinition.getValueRange());
				}
				if (metadataDefinition.getIdentifier() != null && metadataDefinition.getIdentifier() == 14) {
					authStartDateField = metadataDefinition.getFieldName();
					dateType.put(metadataDefinition.getFieldName(), metadataDefinition.getValueRange());
				}
			}
			if(StringUtils.isBlank(authEndDateEndField)){
				continue;
			}
			int warningDay = 0;
			if (copyrightWarningDays != null && !"".equals(copyrightWarningDays)) {
				warningDay = Integer.parseInt(copyrightWarningDays);
			}
			String result = "";
			String resultPassDate = "";
			if (warningDay > 0) {
				String delHql = " delete from CopyrightWarning c where platformId=1";
				executeUpdate(delHql, null);
				Date currentDate = new Date();
				try {
					result = baseSemanticSerivce.queryCopyrightWarning(formatter
							.format(currentDate), formatter.format(DateUtils.addDays(
							currentDate, warningDay)),authEndDateEndField);
					resultPassDate = baseSemanticSerivce.queryCopyrightWarning("",formatter
							.format(currentDate),authEndDateEndField);
				} catch (Exception e) {
					continue;
				}
				Gson gson = new Gson();
				SearchResultCa caList = null;
				SearchResultCa caListPass = null;
				try {
					 caList = gson.fromJson(result, SearchResultCa.class);
					 caListPass = gson.fromJson(resultPassDate, SearchResultCa.class);
				} catch (Exception e) {
					continue;
				}
				try {
					 if(caList!=null && !caList.getRows().isEmpty()){
						 List<Ca> cas = caList.getRows();
						 if(cas!=null && cas.size()>0){
							 for(int i=0;i<cas.size();i++){
								 Ca ca = cas.get(i);
								 Map<String,String> metadataMap = ca.getMetadataMap();
								 CopyrightWarning copyrightWarning = new CopyrightWarning();
								 copyrightWarning.setObjectId(ca.getObjectId());
								 if(metadataMap.get(authStartDateField)!=null){
									 String format = dateType.get(authStartDateField);
									 if (metadataMap.get(authStartDateField)!=null) {
										Date dateOld = new Date(Long.parseLong(metadataMap.get(authStartDateField))); // 根据long类型的毫秒数生命一个date类型的时间
										String simpleDate = new SimpleDateFormat(format).format(dateOld);
										copyrightWarning.setAuthStartDate(simpleDate);
									 }
								 }
								 if(metadataMap.get(authEndDateEndField)!=null){
									 if(metadataMap.get(authEndDateEndField)!=null){
										 String format = dateType.get(authEndDateEndField);
										 if (metadataMap.get(authEndDateEndField)!=null) {
											Date dateOld = new Date(Long.parseLong(metadataMap.get(authEndDateEndField))); // 根据long类型的毫秒数生命一个date类型的时间
											String simpleDate = new SimpleDateFormat(format).format(dateOld);
											copyrightWarning.setAuthEndDate(simpleDate);
										 }
									 }
								 }
								 if(ca.getPublishType()!=null){
									 copyrightWarning.setPublishType(ca.getPublishType());
								 }
								 if(metadataMap.get("title")!=null){
									 copyrightWarning.setTitle(metadataMap.get("title"));
								 }
								 copyrightWarning.setPlatformId(1);
								 //状态设置，未过期为1
								 copyrightWarning.setType("1");
								 create(copyrightWarning);
							 }
						 }
					 }
					 if(caListPass!=null && !caListPass.getRows().isEmpty()){
						 List<Ca> casPass = caListPass.getRows();
						 if(casPass!=null && casPass.size()>0){
							 for(int i=0;i<casPass.size();i++){
								 Ca ca = casPass.get(i);
								 Map<String,String> metadataMap = ca.getMetadataMap();
								 CopyrightWarning copyrightWarning = new CopyrightWarning();
								 copyrightWarning.setObjectId(ca.getObjectId());
								 if(metadataMap.get(authStartDateField)!=null){
									 String format = dateType.get(authStartDateField);
									 if (metadataMap.get(authStartDateField)!=null) {
										Date dateOld = new Date(Long.parseLong(metadataMap.get(authStartDateField))); // 根据long类型的毫秒数生命一个date类型的时间
										String simpleDate = new SimpleDateFormat(format).format(dateOld);
										copyrightWarning.setAuthStartDate(simpleDate);
									 }
								 }
								 if(metadataMap.get(authEndDateEndField)!=null){
									 if(metadataMap.get(authEndDateEndField)!=null){
										 String format = dateType.get(authEndDateEndField);
										 if (metadataMap.get(authEndDateEndField)!=null) {
											Date dateOld = new Date(Long.parseLong(metadataMap.get(authEndDateEndField))); // 根据long类型的毫秒数生命一个date类型的时间
											String simpleDate = new SimpleDateFormat(format).format(dateOld);
											copyrightWarning.setAuthEndDate(simpleDate);
										 }
									 }
								 }
								 if(ca.getPublishType()!=null){
									 copyrightWarning.setPublishType(ca.getPublishType());
								 }
								 if(metadataMap.get("title")!=null){
									 copyrightWarning.setTitle(metadataMap.get("title"));
								 }
								 copyrightWarning.setPlatformId(1);
								 //状态设置，未过期为1//0已过期
								 copyrightWarning.setType("0");
								 create(copyrightWarning);
							 }
						 }
					 }
				} catch (Exception e) {
					continue;
				}
//				for (Ca metaData : metaDatas) { 
//					CopyrightWarning copyrightWarning = new CopyrightWarning();
//					copyrightWarning.setObjectId(metaData.getObjectId());
////					copyrightWarning.setCrtPerson(metaData.getCopyRightMetaData()
////							.getCrtPerson());
//					String endDate = metaData.getCopyRightMetaData()
//							.getAuthEndDate();
//					if(StringUtils.isNotBlank(endDate)){
//						endDate = endDate.substring(0,10);
//					}
//					copyrightWarning.setAuthEndDate(endDate);
////					copyrightWarning.setAuthorizer(metaData.getCopyRightMetaData()
////							.getAuthorizer());
//					String startDate = metaData.getCopyRightMetaData()
//							.getAuthStartDate();
//					if(StringUtils.isNotBlank(startDate)){
//						startDate = startDate.substring(0,10);
//					}
//					copyrightWarning.setAuthStartDate(startDate);
//					copyrightWarning.setContractCode(metaData.getCopyRightMetaData()
//							.getContractCode());
//					copyrightWarning.setModule(metaData.getCommonMetaData()
//							.getModule());
//					copyrightWarning.setType(metaData.getCommonMetaData()
//							.getType());
//					copyrightWarning.setTitle(metaData.getCommonMetaData().getTitle());
//					copyrightWarning.setPlatformId(1);
//					create(copyrightWarning);
//				}
			}
			
			
		}
	}
	/**
	 * 出版资源版权更新
	 */
	@Override
	public void updatePubliCopyrightWarning() {
		String copyrightWarningDays = WebappConfigUtil
				.getParameter("copyright_warning_days");
		int warningDay = 0;
		if (copyrightWarningDays != null && !"".equals(copyrightWarningDays)) {
			warningDay = Integer.parseInt(copyrightWarningDays);
		}
		if (warningDay > 0) {
			String delHql = " delete from CopyrightWarning c where platformId=2";
			executeUpdate(delHql, null);
			Date currentDate = new Date();
			HttpClientUtil http = new HttpClientUtil();
			String authEndDateBegin=formatter.format(currentDate);
			String authEndDateEnd = formatter.format(DateUtils.addDays(currentDate, warningDay));
			String hql = "authEndDateBegin="+authEndDateBegin+"&authEndDateEnd="+authEndDateEnd;
			String sr = http.executeGet(PUBLISH_COPYRIGHTWARNING_URL + "?"+hql);
			Gson gson = new Gson();
			MetadataResult list = gson.fromJson(sr, MetadataResult.class);
			List<MetaDataDC> metaDatas = null;
			for (MetaDataDC metaData : metaDatas) {
				CopyrightWarning copyrightWarning = new CopyrightWarning();
				copyrightWarning.setObjectId(metaData.getObjectId());
//				copyrightWarning.setCrtPerson(metaData.getCopyRightMetaData()
//						.getCrtPerson());
				String endDate = metaData.getCopyRightMetaData()
						.getAuthEndDate();
				if(StringUtils.isNotBlank(endDate)){
					endDate = endDate.substring(0,10);
				}
				copyrightWarning.setAuthEndDate(endDate);
//				copyrightWarning.setAuthorizer(metaData.getCopyRightMetaData()
//						.getAuthorizer());
				String startDate = metaData.getCopyRightMetaData()
						.getAuthStartDate();
				if(StringUtils.isNotBlank(startDate)){
					startDate = startDate.substring(0,10);
				}
				copyrightWarning.setAuthStartDate(startDate);
				copyrightWarning.setContractCode(metaData.getCopyRightMetaData()
						.getContractCode());
				copyrightWarning.setTitle(metaData.getCommonMetaData().getTitle());
				copyrightWarning.setPlatformId(2);
				copyrightWarning.setPublishType(metaData.getCommonMetaData()
						.getPublishType());
				create(copyrightWarning);
			}
		}
	}
	/**
	 * 关联版权
	 * 
	 * @param id
	 * @param objectId
	 * @return
	 */
	public String doRelateCopyright(int id, String objectId) {
		String result = baseSemanticSerivce.getRessource(objectId);
		Gson gson = new Gson();
		Ca ca = gson.fromJson(result, Ca.class);
		CopyrightRepeat copyrightRepeatTemp = (CopyrightRepeat) getByPk(
				CopyrightRepeat.class, id);
		if (ca.getCopyRightMetaData() == null) {
			CopyRightMetaData copyRightMetaData = new CopyRightMetaData();
			copyRightMetaData.setContractCode(copyrightRepeatTemp
					.getContractCode());
//			copyRightMetaData.setAuthArea(copyrightRepeatTemp.getAuthArea());
//			copyRightMetaData.setAuthChannel(copyrightRepeatTemp
//					.getAuthChannel());
//			copyRightMetaData.setAuthTimeLimit(copyrightRepeatTemp
//					.getAuthTimeLimit());
//			copyRightMetaData.setAuthStartDate(copyrightRepeatTemp
//					.getAuthStartDate());
//			copyRightMetaData.setAuthEndDate(copyrightRepeatTemp
//					.getAuthEndDate());
//			copyRightMetaData.setAuthLanguage(copyrightRepeatTemp
//					.getAuthLanguage());
//			copyRightMetaData.setPermitRight(copyrightRepeatTemp
//					.getPermitRight());
//			copyRightMetaData
//					.setAuthorizer(copyrightRepeatTemp.getAuthorizer());
//			copyRightMetaData.setCrtType(copyrightRepeatTemp.getCrtType());
//			copyRightMetaData.setCrtPerson(copyrightRepeatTemp.getCrtPerson());
//			copyRightMetaData.setCollaPattern(copyrightRepeatTemp
//					.getCollaPattern());
			ca.setCopyRightMetaData(copyRightMetaData);
		} else {
			ca.getCopyRightMetaData().setContractCode(
					copyrightRepeatTemp.getContractCode());
//			ca.getCopyRightMetaData().setAuthArea(
//					copyrightRepeatTemp.getAuthArea());
//			ca.getCopyRightMetaData().setAuthChannel(
//					copyrightRepeatTemp.getAuthChannel());
//			ca.getCopyRightMetaData().setAuthTimeLimit(
//					copyrightRepeatTemp.getAuthTimeLimit());
//			ca.getCopyRightMetaData().setAuthStartDate(
//					copyrightRepeatTemp.getAuthStartDate());
//			ca.getCopyRightMetaData().setAuthEndDate(
//					copyrightRepeatTemp.getAuthEndDate());
//			ca.getCopyRightMetaData().setAuthLanguage(
//					copyrightRepeatTemp.getAuthLanguage());
//			ca.getCopyRightMetaData().setPermitRight(
//					copyrightRepeatTemp.getPermitRight());
//			ca.getCopyRightMetaData().setAuthorizer(
//					copyrightRepeatTemp.getAuthorizer());
//			ca.getCopyRightMetaData().setCrtType(
//					copyrightRepeatTemp.getCrtType());
//			ca.getCopyRightMetaData().setCrtPerson(
//					copyrightRepeatTemp.getCrtPerson());
//			ca.getCopyRightMetaData().setCollaPattern(
//					copyrightRepeatTemp.getCollaPattern());
		}

		String json = gson.toJson(ca);
		logger.debug(json);
		SemanticResponse rtn = null;
		String responseStr = "";
		try {
			responseStr = collectResService.saveBookCopyright(ca);
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtn = gson.fromJson(responseStr, SemanticResponse.class);
		if (rtn.getState() == 0) {
			this.getBaseDao().delete(copyrightRepeatTemp);
		}
		return responseStr;
	}

	/**
	 * 分页查询版权预警
	 * 
	 * @param pageInfo
	 * @param role
	 * @return
	 */
	public PageResult queryCopyrightWarnings(PageInfo pageInfo,
			CopyrightWarning copyrightWarning,HttpServletRequest request) throws ServiceException {
		String hql = " from CopyrightWarning c where 1=1 ";
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(copyrightWarning.getContractCode())) {
			hql = hql + " and c.contractCode like :contractCode ";
			params.put("contractCode", "%" + copyrightWarning.getContractCode()
					+ "%");
		}
		if (StringUtils.isNotBlank(copyrightWarning.getCrtPerson())) {
			hql = hql + " and c.crtPerson like :crtPerson ";
			params.put("crtPerson", "%" + copyrightWarning.getCrtPerson() + "%");
		}
		if (StringUtils.isNotBlank(copyrightWarning.getAuthorizer())) {
			hql = hql + " and c.authorizer like :authorizer ";
			params.put("authorizer", "%" + copyrightWarning.getAuthorizer()
					+ "%");
		}
		if (StringUtils.isNotBlank(copyrightWarning.getPublishType())) {
			hql = hql + " and c.publishType =:publishType ";
			params.put("publishType", copyrightWarning.getPublishType());
		}
		if (StringUtils.isNotBlank(copyrightWarning.getModule())) {
			hql = hql + " and c.module =:module ";
			params.put("module", copyrightWarning.getModule());
		}
		if (StringUtils.isNotBlank(copyrightWarning.getType())) {
			hql = hql + " and c.type =:type ";
			params.put("type", copyrightWarning.getType());
		}
		String authStartDateBegin = request.getParameter("authStartDateBegin");
		if (StringUtils.isNotBlank(authStartDateBegin)) {
			hql = hql + " and c.authStartDate >=:authStartDateBegin ";
			params.put("authStartDateBegin", authStartDateBegin);
		}
		String authStartDateEnd = request.getParameter("authStartDateEnd");
		if (StringUtils.isNotBlank(authStartDateEnd)) {
			hql = hql + " and c.authStartDate <=:authStartDateEnd ";
			params.put("authStartDateEnd", authStartDateEnd);
		}
		String authEndDateBegin = request.getParameter("authEndDateBegin");
		if (StringUtils.isNotBlank(authEndDateBegin)) {
			hql = hql + " and c.authEndDate >=:authEndDateBegin ";
			params.put("authEndDateBegin", authEndDateBegin);
		}
		String authEndDateEnd = request.getParameter("authEndDateEnd");
		if (StringUtils.isNotBlank(authEndDateEnd)) {
			hql = hql + " and c.authEndDate <=:authEndDateEnd ";
			params.put("authEndDateEnd", authEndDateEnd);
		}
		String countHql = " select count(*) " + hql;
		try {
			baseDao.query4Page(hql, countHql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}

		return pageInfo.getPageResult();
	}

	/**
	 * 分页查询重复资源
	 * 
	 * @param pageInfo
	 * @param role
	 * @return
	 */
	public PageResult queryCopyrightRepeats(PageInfo pageInfo,
			CopyrightRepeat copyrightRepeat) throws ServiceException {
		String hql = " from CopyrightRepeat c where 1=1 ";
		;
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(copyrightRepeat.getSource())) {
			hql = hql + " and c.source like :source ";
			params.put("source", "%" + copyrightRepeat.getSource() + "%");
		}
		if (StringUtils.isNotBlank(copyrightRepeat.getType())) {
			hql = hql + " and c.type like :type ";
			params.put("type", "%" + copyrightRepeat.getType() + "%");
		}
		if (StringUtils.isNotBlank(copyrightRepeat.getTitle())) {
			hql = hql + " and c.title like :title ";
			params.put("title", "%" + copyrightRepeat.getTitle() + "%");
		}
		if (StringUtils.isNotBlank(copyrightRepeat.getCreator())) {
			hql = hql + " and c.creator like :creator ";
			params.put("creator", "%" + copyrightRepeat.getCreator() + "%");
		}
		String countHql = " select count(*) " + hql;
		try {
			baseDao.query4Page(hql, countHql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}

		return pageInfo.getPageResult();
	}

	/**
	 * 分页查询批量导入日志
	 * 
	 * @param pageInfo
	 * @param role
	 * @return
	 */
	public PageResult queryCopyrightImportResults(PageInfo pageInfo,
			CopyrightImportResult copyrightImportResult)
			throws ServiceException {
		String hql = " from CopyrightImportResult c where 1=1 ";
		;
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(copyrightImportResult.getExcelName())) {
			hql = hql + " and c.excelName like :excelName";
			params.put("excelName", "%" + copyrightImportResult.getExcelName()
					+ "%");
		}
		if (copyrightImportResult.getStatus() != null) {
			hql = hql + " and c.status = :status ";
			params.put("status", copyrightImportResult.getStatus());
		}
		String countHql = " select count(*) " + hql;
		try {
			baseDao.query4Page(hql, countHql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}

		return pageInfo.getPageResult();
	}

	/**
	 * 增加日志信息
	 * 
	 * @param sheet0
	 * @param rowHeaderNum
	 */
	private static void addResultCell(HSSFRow row, String msg, HSSFSheet sheet) {
		HSSFCell cell = row.createCell(row.getLastCellNum());
		cell.setCellValue(msg);
		sheet.setColumnWidth(row.getLastCellNum() - 1, 6666);
	}

	/**
	 * 执行导入任务
	 */
	public void doImportCopyright(File excelFile) {
//		CopyrightImportResult copyrightImportResult = null;
//		OutputStream out = null;
//		if (excelFile.exists()) {
//			// 解析excel文件
//			try {
//				BufferedInputStream in = new BufferedInputStream(
//						new FileInputStream(excelFile));
//				POIFSFileSystem fs = new POIFSFileSystem(in);
//				HSSFWorkbook wb = new HSSFWorkbook(fs);
//				HSSFSheet sheet = wb.getSheetAt(0);
//				int rowsLength = sheet.getLastRowNum();
//				int successNum = 0;
//				String title = "";
//				String source = "";
//				String type = "";
//				String creator = "";
//				String msg = "";
//				for (int i = 1; i <= rowsLength; i++) {
//					HSSFRow row = sheet.getRow(i);
//					source = StringUtils.trim(row.getCell(0)
//							.getStringCellValue());
//					if (StringUtils.isBlank(source)) {
//						logger.info("资源来源不能为空");
//						msg = "导入失败：资源来源不能为空";
//						addResultCell(row, msg, sheet);
//						continue;
//					}
//					type = StringUtils
//							.trim(row.getCell(1).getStringCellValue());
//					type = ResourceType.getValueByDesc(type);
//					if (StringUtils.isBlank(type)) {
//						logger.info("资源类型不能为空");
//						msg = "导入失败：资源类型不能为空";
//						addResultCell(row, msg, sheet);
//						continue;
//					}
//					title = StringUtils.trim(row.getCell(2)
//							.getStringCellValue());
//					if (StringUtils.isBlank(title)) {
//						logger.info("资源名称不能为空");
//						msg = "导入失败：资源名称不能为空";
//						addResultCell(row, msg, sheet);
//						continue;
//					}
//					creator = StringUtils.trim(row.getCell(3)
//							.getStringCellValue());
//					if (StringUtils.isBlank(creator)) {
//						logger.info("制作者姓名不能为空");
//						msg = "导入失败：制作者姓名不能为空";
//						addResultCell(row, msg, sheet);
//						continue;
//					}
//					// 查重
////					if ("T06".equals(type)) {
////						List<Ca> repeatRes = baseSemanticSerivce
////								.getCaResourceByMoreCondition(source, type,
////										title, creator,"");
////						if (repeatRes != null && repeatRes.size() > 0) {
////							if (repeatRes.size() == 1) {
////								Ca ca = repeatRes.get(0);
////								CopyRightMetaData copyRightMetaDataCa = ca
////										.getCopyRightMetaData();
////								if (copyRightMetaDataCa == null) {
////									ca.setCopyRightMetaData(this
////											.createCopyMetadata(row));
////								} else {
////									this.updateCopyMetadata(
////											copyRightMetaDataCa, row);
////								}
////								String responseStr = "";
////								responseStr = collectResService
////										.saveBookCopyright(ca);
////								Gson gson = new Gson();
////								SemanticResponse rtn = gson.fromJson(
////										responseStr, SemanticResponse.class);
////								if (rtn.getState() != 0) {
////									msg = "导入失败：" + rtn.getDesc();
////								} else {
////									msg = "导入成功！";
////									successNum++;
////								}
////								addResultCell(row, msg, sheet);
////								continue;
////							} else {
////								this.createCopyMetadataRepeat(creator, source,
////										type, title, row);
////								logger.info("资源重复，无法导入版权");
////								msg = "导入失败：资源重复，无法导入版权";
////								addResultCell(row, msg, sheet);
////								continue;
////							}
////						} else {
////							logger.info("不存在资源，无法导入版权");
////							msg = "导入失败：不存在资源，无法导入版权";
////							addResultCell(row, msg, sheet);
////							continue;
////						}
//					} else {
////						List<Asset> rs = baseSemanticSerivce
////								.getResourceByMoreCondition(source, type,
////										title, creator, null);
////						if (rs != null && rs.size() > 0) {
////							if (rs.size() == 1) {
////								Asset asset = rs.get(0);
////								CopyRightMetaData copyRightMetaDataAsset = asset
////										.getCopyRightMetaData();
////								if (copyRightMetaDataAsset == null) {
////									asset.setCopyRightMetaData(this
////											.createCopyMetadata(row));
////								} else {
////									this.updateCopyMetadata(
////											copyRightMetaDataAsset, row);
////								}
////								String responseStr = "";
////								responseStr = baseSemanticSerivce
////										.saveAssetCopyright(asset);
////								Gson gson = new Gson();
////								SemanticResponse rtn = gson.fromJson(
////										responseStr, SemanticResponse.class);
////								if (rtn.getState() != 0) {
////									msg = "导入失败：" + rtn.getDesc();
////								} else {
////									msg = "导入成功！";
////									successNum++;
////								}
////								addResultCell(row, msg, sheet);
////								continue;
////							} else {
////								this.createCopyMetadataRepeat(creator, source,
////										type, title, row);
////								logger.info("资源重复，无法导入版权");
////								msg = "导入失败：资源重复，无法导入版权";
////								addResultCell(row, msg, sheet);
////								continue;
////							}
////						} else {
////							logger.info("不存在资源，无法导入版权");
////							msg = "导入失败：不存在资源，无法导入版权";
////							addResultCell(row, msg, sheet);
////							continue;
////						}
//
//					}
//				}
//				copyrightImportResult = new CopyrightImportResult();
//				String uuid = UUID.randomUUID().toString();
//				copyrightImportResult.setExcelName(excelFile.getName());
//				copyrightImportResult.setImportTime(new Date());
//				copyrightImportResult.setVirtualName(uuid);
//				if (successNum == 0) {
//					// 全部失败
//					copyrightImportResult.setStatus(ImportStatus.STATUS4);
//				} else if (rowsLength == successNum) {
//					// 全部成功
//					copyrightImportResult.setStatus(ImportStatus.STATUS2);
//				} else {
//					// 部分成功
//					copyrightImportResult.setStatus(ImportStatus.STATUS3);
//				}
//				create(copyrightImportResult);
//				String resultExcelPath = FILE_DIR + File.separator
//						+ "copyright" + File.separator + uuid + File.separator;
//				File file = new File(resultExcelPath);
//				if (!file.exists()) {
//					file.mkdirs();
//				}
//				String resultExcel = resultExcelPath + uuid + ".xls";
//				out = new FileOutputStream(resultExcel);
//				wb.write(out);
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (out != null) {
//					try {
//						out.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//						logger.info(e.getMessage());
//					}
//				}
//			}
//		}
	}

	public CopyRightMetaData createCopyMetadata(HSSFRow row) {
		CopyRightMetaData copyRightMetaData = new CopyRightMetaData();
		copyRightMetaData.setContractCode(StringUtils.trim(row.getCell(4)
				.getStringCellValue()));
//		copyRightMetaData.setAuthArea(StringUtils.trim(row.getCell(5)
//				.getStringCellValue()));
//		copyRightMetaData.setAuthChannel(StringUtils.trim(row.getCell(6)
//				.getStringCellValue()));
//		copyRightMetaData.setAuthTimeLimit(row.getCell(7).getNumericCellValue()
//				+ "");
		copyRightMetaData.setAuthStartDate(DateUtil.convertDateToString(row
				.getCell(8).getDateCellValue()));
		copyRightMetaData.setAuthEndDate(DateUtil.convertDateToString(row
				.getCell(9).getDateCellValue()));
//		copyRightMetaData.setAuthLanguage(StringUtils.trim(row.getCell(10)
//				.getStringCellValue()));
//		copyRightMetaData.setPermitRight(StringUtils.trim(row.getCell(11)
//				.getStringCellValue()));
//		copyRightMetaData.setAuthorizer(StringUtils.trim(row.getCell(12)
//				.getStringCellValue()));
//		copyRightMetaData.setCrtType(StringUtils.trim(row.getCell(13)
//				.getStringCellValue()));
//		copyRightMetaData.setCrtPerson(StringUtils.trim(row.getCell(14)
//				.getStringCellValue()));
//		copyRightMetaData.setCollaPattern(StringUtils.trim(row.getCell(15)
//				.getStringCellValue()));
		return copyRightMetaData;
	}

	public void updateCopyMetadata(CopyRightMetaData copyRightMetaData,
			HSSFRow row) {
		copyRightMetaData.setContractCode(StringUtils.trim(row.getCell(4)
				.getStringCellValue()));
//		copyRightMetaData.setAuthArea(StringUtils.trim(row.getCell(5)
//				.getStringCellValue()));
//		copyRightMetaData.setAuthChannel(StringUtils.trim(row.getCell(6)
//				.getStringCellValue()));
//		copyRightMetaData.setAuthTimeLimit(row.getCell(7).getNumericCellValue()
//				+ "");
		copyRightMetaData.setAuthStartDate(DateUtil.convertDateToString(row
				.getCell(8).getDateCellValue()));
		copyRightMetaData.setAuthEndDate(DateUtil.convertDateToString(row
				.getCell(9).getDateCellValue()));
//		copyRightMetaData.setAuthLanguage(StringUtils.trim(row.getCell(10)
//				.getStringCellValue()));
//		copyRightMetaData.setPermitRight(StringUtils.trim(row.getCell(11)
//				.getStringCellValue()));
//		copyRightMetaData.setAuthorizer(StringUtils.trim(row.getCell(12)
//				.getStringCellValue()));
//		copyRightMetaData.setCrtType(StringUtils.trim(row.getCell(13)
//				.getStringCellValue()));
//		copyRightMetaData.setCrtPerson(StringUtils.trim(row.getCell(14)
//				.getStringCellValue()));
//		copyRightMetaData.setCollaPattern(StringUtils.trim(row.getCell(15)
//				.getStringCellValue()));
	}

	public void createCopyMetadataRepeat(String creator, String source,
			String type, String title, HSSFRow row) {
		CopyrightRepeat copyrightRepeat = new CopyrightRepeat();
		copyrightRepeat.setCreator(creator);
		copyrightRepeat.setSource(source);
		copyrightRepeat.setType(type);
		copyrightRepeat.setTitle(title);
		copyrightRepeat.setContractCode(StringUtils.trim(row.getCell(4)
				.getStringCellValue()));
		copyrightRepeat.setAuthArea(StringUtils.trim(row.getCell(5)
				.getStringCellValue()));
		copyrightRepeat.setAuthChannel(StringUtils.trim(row.getCell(6)
				.getStringCellValue()));
		copyrightRepeat.setAuthTimeLimit(row.getCell(7).getNumericCellValue()
				+ "");
		copyrightRepeat.setAuthStartDate(DateUtil.convertDateToString(row
				.getCell(8).getDateCellValue()));
		copyrightRepeat.setAuthEndDate(DateUtil.convertDateToString(row
				.getCell(9).getDateCellValue()));
		copyrightRepeat.setAuthLanguage(StringUtils.trim(row.getCell(10)
				.getStringCellValue()));
		copyrightRepeat.setPermitRight(StringUtils.trim(row.getCell(11)
				.getStringCellValue()));
		copyrightRepeat.setAuthorizer(StringUtils.trim(row.getCell(12)
				.getStringCellValue()));
		copyrightRepeat.setCrtType(StringUtils.trim(row.getCell(13)
				.getStringCellValue()));
		copyrightRepeat.setCrtPerson(StringUtils.trim(row.getCell(14)
				.getStringCellValue()));
		copyrightRepeat.setCollaPattern(StringUtils.trim(row.getCell(15)
				.getStringCellValue()));
		create(copyrightRepeat);
	}
	@Override
	public void searchCopyrightWarning() {
		 Map<Object, Object> resTypeMap = SysResTypeCacheMap.getMapValue();
		 Iterator it = resTypeMap.entrySet().iterator();
		 String publishTypes = "";
		 while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				if(pairs.getKey()!=null && pairs.getValue()!=null){
					publishTypes = publishTypes+pairs.getKey()+",";
				}
				logger.info("----------------输出字段"+pairs.getKey()+"---"+pairs.getValue()+"--------------");
			}
		if(publishTypes.endsWith(",")){
			publishTypes = publishTypes.substring(0,publishTypes.length()-1);
		}
		SearchParamCa searchParamCa = new SearchParamCa();
		searchParamCa.setPublishType(publishTypes);
		Gson gson = new Gson();
		String paramStrs = gson.toJson(searchParamCa);
		HttpClientUtil http = new HttpClientUtil();
		String result = http.postJson(PUBLISH_QUERYBYPOST_URL, paramStrs);
		
		
		
	}
	public static void main(String args[]){
		SearchParamCa searchParamCa = new SearchParamCa();
		searchParamCa.setPublishType("1,37");
		searchParamCa.setInterfaceQuery("1");
		String metadataMap = "{\"authorEndTime\":\"2013\",\"authorStartTime\":\"2014\"}";
		searchParamCa.setMetadataMap(metadataMap);
//		searchParamCa.setTotal(100);
		Gson gson = new Gson();
		String paramStrs = gson.toJson(searchParamCa);
		HttpClientUtil http = new HttpClientUtil();
		String result = http.postJson(WebappConfigUtil.getParameter("PUBLISH_QUERYBYPOST_URL"), paramStrs);
	}
	@Override
	public void updateMysqlEntry() {
		HttpClientUtil http = new HttpClientUtil();
		String result = http.executeGet(WebappConfigUtil.getParameter("SEARCH_ENTRY_MONTH"));
		if(!result.equals("-1")){
			String array[] = result.split(",");
			String year= Calendar.getInstance().get(Calendar.YEAR)+"";
			String delHql = " delete from SearchEntryMonth";
			executeUpdate(delHql, null);
			SearchEntryMonth se = new SearchEntryMonth();
			se.setEntryYear(year);
			se.setEntryMonth(array[0]);
			se.setEntryDay(array[1]);
			create(se);
		}
	}
}
