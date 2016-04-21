package com.brainsoon.resource.support;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.dao.IBaseDao;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.resource.po.UploadTaskDetail;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.IBatchImportResService;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.resource.util.publishResConstants;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CommonMetaData;
import com.brainsoon.semantic.ontology.model.ExtendMetaData;
import com.brainsoon.semantic.schema.QueryImportCode;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.statistics.service.IEffectNumService;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.IFLTXService;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants.Audience;
import com.brainsoon.system.support.SystemConstants.BatchImportDetaillType;
import com.brainsoon.system.support.SystemConstants.ConsumeType;
import com.brainsoon.system.support.SystemConstants.ImportStatus;
import com.brainsoon.system.support.SystemConstants.Language;
import com.brainsoon.system.support.SystemConstants.OpeningRate;
import com.brainsoon.system.support.SystemConstants.OperatType;
import com.brainsoon.system.support.SystemConstants.ResourceMoudle;
import com.brainsoon.system.util.FieldValidator;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;


public class ImportResThread_bak implements Runnable {
	private static Logger logger = Logger.getLogger(ImportResThread_bak.class);
	private final static String FTP_LOCAL_MAPPING = WebappConfigUtil.getParameter("FTP_LOCAL_MAPPING");
	private ISysParameterService sysParameterService = null;
	private IFLTXService fLTXService;
	private IDictNameService dictNameService = null;
	
	@Override
	public void run() {
		while (true) {
			try {
				IBatchImportResService batchImportResService = (IBatchImportResService)BeanFactoryUtil.getBean("batchImportResService");
				IEffectNumService iEffectNumService = (IEffectNumService)BeanFactoryUtil.getBean("effectNumService");
				
				//------------------- 获取主表中待处理和处理中的数据
				String primaryHql = "from UploadTask t where t.status in (1,2)";
				List<UploadTask> uploadTasks=batchImportResService.query(primaryHql);
				
				if(uploadTasks.size()>0){
					
					List<Ca> saveCas = new ArrayList<Ca>();
					Map<String,String> checkRepeatMetadate = new HashMap<String,String>();
					Map<Integer,String> fileNotExistLog = new HashMap<Integer,String>();
					//------------------- 循环主表  处理每个excel或每批次的资源
					int total = 0;//主表每条对应字表的条数
					for (int i = 0; i < uploadTasks.size(); i++) {
						UploadTask uploadTask=uploadTasks.get(i);
						String sublistHql = "from UploadTaskDetail t where t.status in (1,2)  and t.task.id="+uploadTask.getId()+" order by t.createTime";
						List<UploadTaskDetail> uploadTaskDetails=batchImportResService.query(sublistHql);
						Map<String, String> maps = null;
						List<Map<String, String>> list=null;
						String resultLog = null;
						String sbs=null;
						total = uploadTaskDetails.size();
						for(int j=0;j<uploadTaskDetails.size();j++){
							UploadTaskDetail resTaskDetail=uploadTaskDetails.get(j);
							try {
								//修改主表状态 加工中
								uploadTask.setStatus(ImportStatus.STATUS2);
								batchImportResService.saveOrUpdate(uploadTask);
								if(uploadTask.getFiletype()==1){
									maps = new HashMap<String, String>();
									//修改子表状态  待处理
									resTaskDetail.setStatus(BatchImportDetaillType.STATUS2);
									maps = batchImportResService.doMySqlWithExcel(resTaskDetail);
								}else if(uploadTask.getFiletype()==2){
									maps = new HashMap<String, String>();
									list = new ArrayList<Map<String,String>>();
									resTaskDetail.setStatus(BatchImportDetaillType.STATUS2);
									batchImportResService.saveOrUpdate(resTaskDetail);
									list=batchImportResService.savefile(resTaskDetail.getPaths(),uploadTask,resTaskDetail);
									maps=list.get(0);
								}else if(uploadTask.getFiletype()==3){
							
									
									
								}
							} catch (Exception e) {
								e.printStackTrace();
								logger.error(e.getMessage());
								uploadTask.setFailNum(uploadTask.getFailNum()+1);
								batchImportResService.saveOrUpdate(uploadTask);
								//uploadTask.setStatus(BatchImportDetaillType.STATUS4);
								resTaskDetail.setRemark("文件解析出错,请检查!");
								batchImportResService.saveOrUpdate(resTaskDetail);
								continue;
							}
							/*resTask.setSuccNum(resTask.getSuccNum()+1);
							batchImportResService.saveOrUpdate(resTask);
							resTaskDetail.setStatus(Csuccess);
							batchImportResService.saveOrUpdate(resTaskDetail);
							//创建一个文件夹。把解析成功的文件放进去
							String paths=resTaskDetail.getPaths().substring(0, resTaskDetail.getPaths().lastIndexOf("/"));
							paths = paths+"files";
							File file = new File(paths);
							if(!file.exists()){
								file.mkdirs();
							}
							File file2 = new File(resTaskDetail.getPaths());
							String path=resTaskDetail.getPaths().substring(resTaskDetail.getPaths().lastIndexOf("/"),resTaskDetail.getPaths().length());
							file2.renameTo(new File(paths+path));*/
							logger.info("-----------------------开始执行校验程序----------------------");
							//转换解析数据到bean
							
							
							System.out.println("----------");
							/*resTask.setSuccNum(resTask.getSuccNum()+1);
							batchImportResService.saveOrUpdate(resTask);
							resTaskDetail.setStatus(Csuccess);
							batchImportResService.saveOrUpdate(resTaskDetail);
							//创建一个文件夹。把解析成功的文件放进去
							String paths=resTaskDetail.getPaths().substring(0, resTaskDetail.getPaths().lastIndexOf("/"));
							paths = paths+"files";
							File file = new File(paths);
							if(!file.exists()){
								file.mkdirs();
							}
							File file2 = new File(resTaskDetail.getPaths());
							String path=resTaskDetail.getPaths().substring(resTaskDetail.getPaths().lastIndexOf("/"),resTaskDetail.getPaths().length());
							file2.renameTo(new File(paths+path));*/
							logger.info("-----------------------开始执行校验程序----------------------");
							//转换解析数据到bean
							validateMetadata(maps, saveCas, uploadTask, resultLog,batchImportResService,checkRepeatMetadate,resTaskDetail);
							System.out.println("----------");
						}
						//------------------循环子表结束 即结束主表一条记录
						
						
						//------------------开始导入逻辑----开始
						int saveLen = 0;//要保存的资源ca数据  即一条主表对应的子表的数据条数（验证成功的）
						int failNum1 = 0;//失败的条数（行数）
						int succNum = 0;
						int failNum = 0;
						saveLen = saveCas.size();
						Integer rowNum = 0;//该条资源对应excel的行数
						UploadTaskDetail uploadTaskDetail;
						int numIng = 0;
						for (int k = 0; k < saveLen; k++) {
							String result = "";
							Ca caTemp = saveCas.get(k);
							caTemp.setPublishType(uploadTask.getLibType());
							numIng = k+failNum1+1;
							uploadTask.setNumIng(numIng);//暂时不知何用
							rowNum = caTemp.getExcelNum();
							try {
							//解析添加，覆盖		
								//result = batchImportResService.doPublishTask(caTemp, taskData, uploadTask,resultLog,checkRepeatMetadate,fileNotExistLog);
							} catch (Exception e) {
								e.printStackTrace();
								uploadTaskDetail = new UploadTaskDetail();
								uploadTaskDetail.setTask(uploadTask);
								uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS4);//失败4
								uploadTaskDetail.setExcelNum(caTemp.getExcelNum());
								uploadTaskDetail.setRemark("第【" + (caTemp.getExcelNum()) + "】行导入错误"+e+"，已忽略");
								uploadTaskDetail.setImportStatus("导入错误"+e+"已忽略");
								batchImportResService.create(uploadTaskDetail);
							}
							if("1".equals(result)){  //导入成功
								succNum++;
								//succRowNums.add(rowNum);
							}
							uploadTask.setFailNum(total-succNum);
							uploadTask.setSuccNum(succNum);
							//uploadTask.setStatus(ImportStatus.STATUS1);//资源导入状态  处理中
							batchImportResService.update(uploadTask);  //回写成功失败的状态
						}
						logger.info("pppppppppppppppppppppppppp---------------------");
						uploadTask.setFinishTime(new Date()); 
						if(succNum == 0){
							//全部失败
							uploadTask.setFailNum(total);
							uploadTask.setStatus(ImportStatus.STATUS4);
						}else if(total == succNum){
							//全部成功
							uploadTask.setStatus(ImportStatus.STATUS2);
						}else{
							//部分成功
							//task.setFailNum(total-succNum);
							uploadTask.setStatus(ImportStatus.STATUS3);
						}
						//------------------开始导入逻辑----结束
						
						
					}
					//-------------------循环主表结束
					
						
				}else{
					logger.info("资源导入-》主表中没有要处理的数据-》线程结束");
					break;
				}
				
				/*// 获取任务队列
				TaskQueue queue = TaskQueue.getInst();
				// 启动任务
				ImportResExcelFile taskData = queue.getMessage();
				if (null != taskData) {
					IBatchImportResService batchImportResService = (IBatchImportResService)BeanFactoryUtil.getBean("batchImportResService");
					sysParameterService = (ISysParameterService)BeanFactoryUtil.getBean("sysParameterService");
					IEffectNumService iEffectNumService = (IEffectNumService)BeanFactoryUtil.getBean("effectNumService");
					fLTXService = (IFLTXService)BeanFactoryUtil.getBean("FLTXService");
					dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
					//获取业务类处理
					logger.info("cccccccc=============");
					UserInfo user = new UserInfo(); 
					user.setUserId(taskData.getUserId());
					user.setLoginIp(taskData.getLoginIp());
					user.setPlatformId(taskData.getPlatformId());
					user.setUsername(taskData.getUsername());
					File excel = taskData.getExcel();
					String module = taskData.getModule();
					//表单上传显示
					UploadResTask task = null;
					int succNum = 0;
					int failNum = 0;
					if(excel.exists()){
						task = new UploadResTask();
						List<Integer> succRowNums = new ArrayList<Integer>();
						Map<Integer,String> resultLog = new HashMap<Integer,String>();
						Map<Integer,String> fileNotExistLog = new HashMap<Integer,String>();
						//解析excel，填充数据
						ExcelData excelData = new ExcelData();
						task.setExcelName(excel.getName());
						try{
							batchImportResService.fillData(excel.getAbsolutePath(), excelData,resultLog);
						}catch(Exception e){
							e.printStackTrace();
							logger.info(e.getMessage());
							task.setStatus(ImportStatus.STATUS4);
							task.setCreateTime(new Date());
							task.setFinishTime(new Date());
							batchImportResService.create(task);
							ResTaskDetail detailInfo = new ResTaskDetail();
							detailInfo.setCreateTime(new Date());
							detailInfo.setTask(task);
							detailInfo.setRemark("解析Excel出错，请检查上传的Excel表格");
							batchImportResService.create(detailInfo);
							continue;
						}
						//进行写库处理，先写入清单
						//String name = excelData.getqName();
						//String batchNum = excelData.getBatchNum();
						//logger.debug("清单："+ name + "  批次：" + batchNum);
						//task.setName(name);
						task.setType(taskData.getUserId()+"");
						task.setCreateTime(new Date());
						task.setExcelPath(excel.getAbsolutePath());
						task.setRemark(taskData.getRemark());
						task.setLibType(taskData.getModule());
						task.setPlatformId(taskData.getPlatformId());
						task.setFailNum(failNum);
						task.setSuccNum(succNum);
						task.setName(taskData.getName());
						task.setModule(taskData.getModule());
						int total = excelData.getRows().size();
						task.setAllNum(total);
						task.setStatus(ImportStatus.STATUS1);
						batchImportResService.create(task);
						logger.info("开始导入cccccccccccccccccccccccccccc————————————————————————————————————");
						//转换解析数据到bean
						List<Ca> saveCas = new ArrayList<Ca>();
						int failNum1 = 0;
						//Map  allXpathName = new HashMap();
						int saveLen = 0;
						Map<String,String> checkRepeatMetadat = new HashMap<String,String>();
						//初级判断excel中的文件是否存在,并保存需要查重的字段checkRepeatMetadat，和要处理的数据到saveCas中
						String fileRoot = taskData.getFileDir();
						String uuid = UUID.randomUUID().toString();
						String excelDir = fileRoot+"failExcelDir"+File.separator+uuid+File.separator;
						String dir = excel.getAbsolutePath().replaceAll("\\\\", "/");
						task.setExcelPath(dir);
						task.setUuid(uuid);
						File excelDirFile = new File(excelDir);
						if(!excelDirFile.exists()){
							excelDirFile.mkdirs();
						}
						doParseData2Ca(excelData, saveCas, task,resultLog,batchImportResService,checkRepeatMetadat);
						saveLen = saveCas.size();
						failNum1 = resultLog.size();
						logger.info("开始导入ddddddddddddddddddddddddddd————————————————————————————————————");
						ResTaskDetail detailInfo;
						if(excelData.getRows().size() == 0){
							task.setStatus(ImportStatus.STATUS4);
							task.setFinishTime(new Date());
							batchImportResService.update(task);
							detailInfo = new ResTaskDetail();
							detailInfo.setCreateTime(new Date());
							detailInfo.setTask(task);
							detailInfo.setExcelNum(0);
							detailInfo.setStatus(2);
							detailInfo.setRemark("Excel文件没有合法数据！");
							batchImportResService.create(detailInfo);
							continue;
						}
						Integer rowNum = 0;
						int numIng = 0;
						for (int i = 0; i < saveLen; i++) {
							String result = "";
							Ca caTemp = saveCas.get(i);
							caTemp.setPublishType(taskData.getModule());
							numIng = i+failNum1+1;
							task.setNumIng(numIng);
							rowNum = caTemp.getExcelNum();
							try {
							//解析添加，覆盖
								result = batchImportResService.doPublishTask(caTemp, taskData, task,resultLog,checkRepeatMetadat,fileNotExistLog);
							} catch (Exception e) {
								e.printStackTrace();
								detailInfo = new ResTaskDetail();
								detailInfo.setTask(task);
								detailInfo.setStatus(2);
								detailInfo.setExcelNum(caTemp.getExcelNum());
								detailInfo.setRemark("第【" + (caTemp.getExcelNum()) + "】行导入错误"+e+"，已忽略");
								detailInfo.setImportStatus("导入错误"+e+"已忽略");
								batchImportResService.create(detailInfo);
							}
							if("1".equals(result)){  //导入成功
								succNum++;
								succRowNums.add(rowNum);
							}
							task.setFailNum(total-succNum);
							task.setSuccNum(succNum);
							task.setStatus(ImportStatus.STATUS1);
							batchImportResService.update(task);  //回写成功失败的状态
						}
						logger.info("pppppppppppppppppppppppppp---------------------");
						task.setFinishTime(new Date()); 
						if(succNum == 0){
							//全部失败
							task.setFailNum(total);
							task.setStatus(ImportStatus.STATUS4);
						}else if(total == succNum){
							//全部成功
							task.setStatus(ImportStatus.STATUS2);
						}else{
							//部分成功
							//task.setFailNum(total-succNum);
							task.setStatus(ImportStatus.STATUS3);
						}
//						if(task.getStatus() != 2){
//							String fileRoot = taskData.getFileDir();
//							String uuid = UUID.randomUUID().toString();
//							String excelDir = fileRoot+"failExcelDir"+File.separator+uuid+File.separator;
//							File excelDirFile = new File(excelDir);
//							if(!excelDirFile.exists()){
//								excelDirFile.mkdirs();
//							}
//							String excelDir = task.getExcelDir();
//							String uuid = task.getUuid();
//							if(resultLog.size()>0){
//								fileRoot = taskData.getFileDir();
//								String excelDir = task.getExcelDir();
//								String uuid = task.getUuid();
//								uuid = UUID.randomUUID().toString();
								//String excelDir1 = fileRoot+"failExcelDir"+File.separator+uuid+File.separator;
//								String tempExcelPath = excelDir+"失败日志(不包含文件不存在).xls";
//								ExcelUtil.insertFailLog(excel.getAbsolutePath(),tempExcelPath,resultLog,total);
//								String failExcelPath = excelDir+uuid+".xls";
//								ExcelUtil.removeBlankRow(tempExcelPath,failExcelPath);
//								failExcelPath = failExcelPath.replaceAll("\\\\", "/");
//								tempExcelPath = tempExcelPath.replaceAll("\\\\", "/");
//								task.setFailExcelPath(failExcelPath);
//							}
//							if(fileNotExistLog.size()>0){
//								//uuid = UUID.randomUUID().toString();
//								String fileNotExistTemp = excelDir+"文件不存在日志.xls";
//								ExcelUtil.insertFileNotExistLog(excel.getAbsolutePath(),fileNotExistTemp,fileNotExistLog,total);
//								uuid = UUID.randomUUID().toString();
//								String fileNotExistPath = excelDir+uuid+".xls";
//								ExcelUtil.removeBlankRow(fileNotExistTemp,fileNotExistPath);
//								fileNotExistPath = fileNotExistPath.replaceAll("\\\\", "/");
//								fileNotExistTemp = fileNotExistTemp.replaceAll("\\\\", "/");
//								task.setFileNotExistPath(fileNotExistPath);
//							}
//						}
						batchImportResService.update(task);
						iEffectNumService.doPiecework(user.getUserId(), OperatType.IMPORT_OPERATE_TYPE, taskData.getLibType(), "", succNum);
						logger.info("5555555555555555555---------------------");
						//写入业务日志
						SysOperateLogUtils.addLog("publish_import", "批量导入资源，成功 ："+ succNum+"个", user);
						logger.info("6666666666666666---------------------");
					}
				}*/
			}catch (Exception e) {
				logger.info("wwwwwwwwwwwwwwwww"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	
	public void sortMeta(List<ExcelDataCell> cells) {
		Collections.sort(cells);
	}
	/**
	 * 转换分类体系为英文code
	 * 
	 * @param cells
	 */
	public void transCN2ENCode(List<ExcelDataCell> cells,IBaseSemanticSerivce baseSemanticSerivce,Asset asset) {
		String version = "";
		String domainType = "1";
		List<String> ar = new ArrayList<String>(cells.size());
		for (ExcelDataCell excelDataCell : cells) {
			if (domainType.equals("1")
					&& StringUtils.equalsIgnoreCase("version",
							excelDataCell.getName())) {
				version = excelDataCell.getData();
				if (StringUtils.isNotBlank(version)) {
					domainType = "0";
				}
			}
			try {
				if (excelDataCell.getData() != null
						&& !"".equals(excelDataCell.getData().trim())) {
					String dataValue = excelDataCell.getData();
					if("0".equals(domainType)&& (excelDataCell.getOrder()==7 ||excelDataCell.getOrder()==8 || excelDataCell.getOrder()==9)){
						if(dataValue.contains(" ")){
							dataValue = dataValue.replaceAll(" "," ");
							dataValue = dataValue.trim();
						}
						if(dataValue.contains("　")){
							dataValue = dataValue.replaceAll("　"," ");
							dataValue = dataValue.trim();
						}
						if(dataValue.contains("　")){
							dataValue = dataValue.replaceAll("　"," ");
							dataValue = dataValue.trim();
						}
						if(dataValue.contains("？")){
							dataValue = dataValue.replaceAll("？","?");
						}
						if(dataValue.contains("＇")){
							dataValue = dataValue.replaceAll("＇","'");
						}
						if(dataValue.contains("＇")){
							dataValue = dataValue.replaceAll("＇","'");
						}
						if(dataValue.contains("（")){
							dataValue = dataValue.replaceAll("（","(");
						}
						if(dataValue.contains("）")){
							dataValue = dataValue.replaceAll("）",")");
						}
						if(dataValue.contains("──")){
							dataValue = dataValue.replaceAll("──","-");
						}
						if(dataValue.contains("“")){
							dataValue = dataValue.replaceAll("“","\"");
						}
						if(dataValue.contains("”")){
							dataValue = dataValue.replaceAll("”","\"");
						}
						if(dataValue.contains("！")){
							dataValue = dataValue.replaceAll("！","!");
						}
						if(dataValue.contains("。")){
							dataValue = dataValue.replaceAll("。",".");
						}
						logger.info("nnnnnnnnnnnnnnnnnnnnnn====="+dataValue);
					}
					if(dataValue.indexOf("ω")>0 || dataValue.indexOf("ψ")>0 || dataValue.indexOf("*")>0){
						dataValue = dataValue.replaceAll("ω","\"");
						dataValue = dataValue.replaceAll("ψ","\"");
						dataValue = dataValue.replaceAll("\\*","\"");
					}
					ar.add(URLEncoder.encode(dataValue, "utf-8"));
				}
			} catch (UnsupportedEncodingException e) {
				logger.error("编码出错" + e.getMessage());
			}
		}
		String codes = StringUtils.join(ar, ",");
		String returnData = null;
	//	Object codesValue = allXpathName.get(codesTemp);
	//	if("1".equals(domainType)){
			returnData =  baseSemanticSerivce.queryImportCode(codes,
					domainType);
//		}else{
//			if(codesValue!=null && "0".equals(codesValue.toString())){
//				logger.info("ssssssssssssssssssssss====="+codesTemp);
//				returnData =  baseSemanticSerivce.queryImportCode(codes,
//							domainType);
//			}
//		}
		if (StringUtils.isNotBlank(returnData)) {
			Gson gson = new Gson();
			logger.info("cccccccccccc=========="+returnData);
			QueryImportCode qCodes = gson.fromJson(returnData,
					QueryImportCode.class);
			logger.info("aaaaaaaaaaaa=========="+returnData);
			// 给code赋值
			String xPath = qCodes.getXpath();
			if("0".equals(domainType)){
				String xPathTemp = "";
				if(xPath.indexOf("V")>0){
					xPathTemp = xPath.substring(xPath.indexOf("V"),xPath.length());
				}
				asset.setImportXpath(xPathTemp);
			}
			String[] xPathArray = StringUtils.split(xPath, ",");
			for (int i = 0; i < xPathArray.length; i++) {
				cells.get(i).setDataCode(xPathArray[i]);
			}
			// 处理单元
			String unitId = qCodes.getUnitId();
			String unitName = qCodes.getUnitName();
			if (StringUtils.isNotBlank(unitId)) {
				ExcelDataCell unit = new ExcelDataCell(-1, unitId, "unit", 0);
				unit.setDataCode(unitId);
				ExcelDataCell unitNameCell = new ExcelDataCell(-1, unitName,
						"unitName", 0);
				unitNameCell.setDataCode(unitName);
				cells.add(unit);
				cells.add(unitNameCell);
			}
		} else {
			try {
				throw new ServiceException("数据【"
						+ URLDecoder.decode(codes, "utf-8") + "】，找不到匹配路径。");
			} catch (UnsupportedEncodingException e) {
				throw new ServiceException(e.getMessage());
			}
		}
	}
	/**
	 * 填充分类体系数据
	 * 
	 * @param cells
	 * @param commonMetaDatas
	 * @param extendMetaDatas
	 */
	public void fillEnCode(List<ExcelDataCell> cells,
			Map<String, String> commonMetaDatas,
			Map<String, String> extendMetaDatas) {
		if (null != cells) {
			int referClass;
			String name;
			String code;
			for (ExcelDataCell excelDataCell : cells) {
				referClass = excelDataCell.getReferClass();
				name = excelDataCell.getName();
				code = excelDataCell.getDataCode();
				if (referClass == 0) {
					commonMetaDatas.put(name, code);
				} else if (referClass == 1) {
					extendMetaDatas.put(name, code);
				}
			}
		}
	}
	/**
	 * 转换数据
	 * 
	 * @param excelData
	 * @param module
	 *            模块code
	 * @param needSaves
	 *            需要保存的asset
	 * @param task
	 * @param asset
	 */
	public void doParseData2Ca(ExcelData excelData, List<Ca> needSaves,
			UploadTask task,Map<Integer,String> resultLog,IBatchImportResService batchImportResService,Map<String,String> checkRepeatMetadat) {
		if (null == excelData)
			return;
		Ca ca =null;
		String  tab = "";
		String tabNum = "";
		boolean status = true;
		ExcelDataDetailMK[] markers = excelData.getMarkers();
		List<ExcelDataRow> rows = excelData.getRows();
		int maxRow = rows.size();
		String[] datas;
		String currentData;
		int datasLen = 0;
		ExcelDataDetailMK mk;
		String name;
		List<ExcelDataCell> cells = new ArrayList<ExcelDataCell>();
		Map<String,MetadataDefinition> metadataDefinitions = MetadataSupport.getAllMetadataDefinition();
		//Map<String,MetadataDefinition> metadataDefinitionMap =  MetadataSupport.getAllMetadataDefinition();
		UploadTaskDetail detailInfo;
		String uploadFile ="";
		String filePath = "";
		LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
		map = dictNameService.getDictMapByName("模板导入资源类型目录");
		String fileResType = "";
		if(!map.isEmpty() && map.get(task.getModule())!=null){
			fileResType = map.get(task.getModule());
		}
		for (int i = 0; i < maxRow; i++) {
			tabNum = "";
			status = true;
			ExcelDataRow row = rows.get(i);
			datas = row.getDatas();
			datasLen = datas.length;
			cells.clear();
			detailInfo = new UploadTaskDetail();
			detailInfo.setCreateTime(new Date());
			detailInfo.setTask(task);
			int rowNum = row.getNum();
			detailInfo.setExcelNum(rowNum);
			String error = "第【" + row.getNum() + "】行导入失败，";
			ca = new Ca();
			ca.setNum(0);//赋初始值doi字段查询
			boolean successData = true;// 数据验证是否通过
			for (int j = 0; j < datasLen; j++) {
				mk = markers[j];
				currentData = datas[j];
				if(StringUtils.isNotBlank(currentData)){
					currentData = currentData.trim();
				}
				name = mk.name;
				if(StringUtils.isNotBlank(name)){
					name = name.trim();
				}
				if("filePath".equals(name)){
					filePath = 	currentData;
					continue;
				}
				if("resVersion".equals(name)){
					ca.setResVersion(currentData);
					continue;
				}
				
				try {
//					if(name.equals("objectId")){
//						System.out.println(name);
//					}
					ca = ResUtils.doCheckAndSetValue(ca, name, currentData, metadataDefinitions,1);
				} catch (Exception e) {
					if(StringUtils.isNotBlank(tabNum)){
						tabNum += e.getMessage();
					}else{
						tabNum += e.getMessage();
					}
				}
				
				
//				//查重字段
//				String inputDictValue = "";
//				MetadataDefinition metadataDefinition = MetadataSupport.getMetadataDefinitionByName(name);
//				if(metadataDefinition!=null && metadataDefinition.getFieldType()==6){
//					if(currentData!=null && !"".equals(currentData)){
//						if(StringUtils.isNotBlank(metadataDefinition.getValueRange())){
//						String graph = fLTXService.getFLTXNodeByName(Long.parseLong(metadataDefinition.getValueRange()), currentData);
//						ca.getMetadataMap().put(name, graph);
//						}
//					}
//				}else if(metadataDefinition!=null && metadataDefinition.getIdentifier()==11){
//					if(StringUtils.isNotBlank(metadataDefinition.getDefaultValue())){
//						ca.getMetadataMap().put(name, metadataDefinition.getDefaultValue());
//					}else{
//						ca.getMetadataMap().put(name, "00");
//					}
//				}else if(metadataDefinition!=null && metadataDefinition.getFieldType()==9){
//					 LinkedHashMap<String, String> childMap = OperDbUtils.queryValueByKey(metadataDefinition.getValueRange());
//					 Iterator it = childMap.entrySet().iterator();
//					 while (it.hasNext()) {
//							Map.Entry pairs = (Map.Entry) it.next();
//							if(StringUtils.isNotBlank(pairs.getKey().toString())&&StringUtils.isNotBlank(pairs.getValue().toString())){
//								if(pairs.getValue().toString().equals(currentData)){
//									inputDictValue = currentData;
//								}
//							}
//							logger.info("----------------判断数据字典是否符合规范"+pairs.getValue().toString()+"--------------");
//						}
//				}else if(StringUtils.isNotBlank(currentData)&& StringUtils.isNotBlank(name)){
//					ca.getMetadataMap().put(name, currentData);
//				}
//				//验证字段是否符合规范
//				 if(StringUtils.isNotBlank(inputDictValue)){
//					 tabNum = "输入值【"+inputDictValue+"】不符合数据字典规范";
//				 }
//				 if(name!=null&&currentData!=null){
//					    tab = FieldValidator.checkFieldHasError(metadataDefinitions, name, currentData);
//					    if(!"".equals(tab)){
//					    	tabNum = tab+tabNum;
//					    }
//				 }
				//cellContent
			}
//			if(!StringUtils.isNotBlank(filePath)){
//				uploadFile = "上传文件为空";
//				tabNum += "," + uploadFile;
//			}
			//不符合规范的直接加入到日志resultLog并successData为false，使之不能加入到导入数据中
			if(!"".equals(tabNum)){
				UploadTaskDetail detailImport = new UploadTaskDetail();
				detailImport.setImportStatus(tabNum);
				detailImport.setExcelNum(row.getNum());
				detailImport.setRemark("第【" + row.getNum() + "】行，导入失败，失败原因："+tabNum);
				detailImport.setCreateTime(new Date());
				detailImport.setStatus(2);
				detailImport.setTask(task);
				sysParameterService.create(detailImport);
				resultLog.put(rowNum,tabNum);
				successData = false;
			}
			if (successData) {
				try {
					// 手动赋值
					ca.setExcelNum(row.getNum());
					if(ca.getResVersion()==null || "".equals(ca.getResVersion())){
						ca.setResVersion("00");
					}
					// 赋值文件路径
					String src = "";
					List<SysParameter> sr = sysParameterService.findParaValue("bachImportSrcPath");
					if(sr!=null && sr.size()>0){
						if(sr.get(0)!=null && sr.get(0).getParaValue()!=null){
							src = sr.get(0).getParaValue();
						}
					}
					
					src = src.replaceAll("\\\\", "/");
					if(!src.endsWith("/")){
						src = src+"/";
					}
					if(StringUtils.isNotBlank(fileResType)){
						src = src+fileResType+"/";
					}
					if(StringUtils.isNotBlank(filePath)){
						uploadFile = src+filePath ;
						if (!new File(uploadFile).exists()) {
							detailInfo.setRemark(error + "资源文件不存在，路径【"
									+ uploadFile + "】");
							detailInfo.setStatus(2);
							logger.info(error + "，资源文件不存在，路径【" + uploadFile + "】");
							batchImportResService.create(detailInfo);
							resultLog.put(rowNum, "资源文件不存在，路径【"
									+ uploadFile + "】");
							continue;
						}
					}else{
						uploadFile = "";
					}
					// 判断是否存在文件
					if(status){
					ca.setUploadFile(uploadFile);
					needSaves.add(ca);
					}
				} catch (Exception e) {
					detailInfo.setRemark(error + e.getMessage());
					logger.info(error + e.getMessage());
					resultLog.put(rowNum, e.getMessage());
					batchImportResService.create(detailInfo);
				}
			}

			}
	}
	
	
	public  static void main(String[] args){
		/*String dataValue = "浙江省温州市六校2012-2013学年七年级上学期期中联考思想品德试题政治";
		try{
			dataValue = dataValue.replaceAll("[:\\[\\]()]", "*");
			System.out.println("----"+dataValue);
		}catch(Exception e){
			e.printStackTrace();
		}*/
		
		Thread t = Thread.currentThread();
        String name = t.getName();
        System.out.println("name = " + name);
		
		
	}
	
	/**
	 * 
	* @Title: validateMetadata
	* @Description: 验证元数据的正确性 （包括必填项，格式等）
	* @param @param maps	从excel中读取的数据/从xml中读取的数据
	* @param @param needSaves	验证通过即保存到该list中
	* @param @param task	当前任务（即主表信息）
	* @param @param resultLog	日志信息
	* @param @param batchImportResService
	* @param @param checkRepeatMetadat	查重字段
	* @param @param resTaskDetail   子表信息（即要验证的该条资源）
	* @return void    返回类型
	* @throws
	 */
	public void validateMetadata(Map<String, String> maps,List<Ca> needSaves,UploadTask task,String resultLog,IBatchImportResService batchImportResService,Map<String,String> checkRepeatMetadat,UploadTaskDetail resTaskDetail){
		/*if(null==list){
			return;
		}
		String tabNum = "";
		Ca ca =null;
		Map<String,MetadataDefinition> metadataDefinitions = MetadataSupport.getAllMetadataDefinition();
		ResTaskDetail detailInfo;
		for(int i=0;i<list.size();i++){
			tabNum = "";
			detailInfo = new ResTaskDetail();
			detailInfo.setCreateTime(new Date());
			detailInfo.setTask(task);
			ca = new Ca();
			ca.setNum(0);//赋初始值doi字段查询
			Map<String, String> map=list.get(i);
			Set<String> set = map.keySet();*/

		if(null==maps){
			return;
		}
		String tabNum = "";
		Ca ca =null;
		Map<String,MetadataDefinition> metadataDefinitions = MetadataSupport.getAllMetadataDefinition();
		Set<String> sets = maps.keySet();
			tabNum = "";
			ca = new Ca();
			ca.setNum(0);//赋初始值doi字段查询
			boolean successData = true;// 数据验证是否通过
			Set<String> set = maps.keySet();
			for (String ma : set) {
				String values=maps.get(ma);
				if(StringUtils.isNotBlank(ma)){
					ma = ma.trim();
				}
				if(StringUtils.isNotBlank(values)){
					values = values.trim();
				}
				if("date".equals(ma)){
					MetadataDefinition metadataDefinition = MetadataSupport.getMetadataDefinitionByName(ma);
					SimpleDateFormat format=new SimpleDateFormat(metadataDefinition.getValueRange());
					Date dates = new Date(values);
					values=format.format(dates);
				}
				try {
					ca = ResUtils.doCheckAndSetValue(ca, ma, values, metadataDefinitions,1);
				} catch (Exception e) {
					if(StringUtils.isNotBlank(tabNum)){
						tabNum += e.getMessage();
					}else{
						tabNum += e.getMessage();
					}
				}
			}
			if(!"".equals(tabNum)){
				resTaskDetail.setImportStatus(tabNum);
				resTaskDetail.setRemark("文件导入失败，失败原因："+tabNum);
				resTaskDetail.setStatus(BatchImportDetaillType.STATUS4);
				batchImportResService.saveOrUpdate(resTaskDetail);
				resultLog=tabNum;
				successData = false;
			}
			if(successData){
				needSaves.add(ca);
			}
			
		
	} 
	
	
}
