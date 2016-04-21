package com.brainsoon.resource.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.po.UploadTaskDetail;
import com.brainsoon.resource.service.IBatchImportResService;
import com.brainsoon.resource.util.publishResConstants;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.system.support.SystemConstants.BatchImportDetaillType;
import com.brainsoon.system.support.SystemConstants.ImportStatus;

/**
 * 
* @ClassName: ImportResThread
* @Description: 资源导入线程
* @author brainsoon
* @date 2015年11月5日
*
 */
public class ImportResThread extends Thread {
	private static Logger logger = Logger.getLogger(ImportResThread.class);
	
	@Override
	public void run() {
		logger.info("【ImportUploadTaskThread】 资源导入线程->>>开始-----");
		
		while (true) {
			try {
				sleep(1000); //等待1s
				//=============================准备工作
				IBatchImportResService batchImportResService = (IBatchImportResService)BeanFactoryUtil.getBean("batchImportResService");
				
				//=============================获取主表数据
				String primaryHql = "from UploadTask t where t.status in (1,2)";
				List<UploadTask> uploadTasks=batchImportResService.query(primaryHql);
				logger.error("【ImportUploadTaskThread】 资源导入线程->>>获取主表数据-----》主表要处理的条数："+uploadTasks.size());
				
				if (uploadTasks.size()>0) {
					
					//=============================第一层循环	循环主表数据
					
					for (int i = 0; i < uploadTasks.size(); i++) {
						
						//=============================获取子表数据
						UploadTask uploadTask=uploadTasks.get(i);
						String sublistHql = "from UploadTaskDetail t where t.status in (1,2)  and t.task.id="+uploadTask.getId()+" order by t.createTime desc";
						List<UploadTaskDetail> uploadTaskDetails=batchImportResService.query(sublistHql);
						
						String totalHql = "from UploadTaskDetail t where t.task.id="+uploadTask.getId()+" order by t.createTime desc";
						List<UploadTaskDetail> alluploadTaskDetails=batchImportResService.query(totalHql);
						int total = alluploadTaskDetails.size();//主表每条对应字表的条数(所有状态的)
						uploadTask.setAllNum(total);
						batchImportResService.update(uploadTask); //更新总数
						int succNum = 0;
						int failNum = 0;
						
						//修改主表状态 加工中
						uploadTask.setStatus(ImportStatus.STATUS2);
						batchImportResService.saveOrUpdate(uploadTask);
						
						//=============================第二层循环	循环每条主表数据对应的字表数据						
						for (int j = 0; j < uploadTaskDetails.size(); j++) {
							
							List<Ca> saveCas = new ArrayList<Ca>();
							Map<String,String> checkRepeatMetadate = new HashMap<String,String>();
							Map<Integer,String> fileNotExistLog = new HashMap<Integer,String>();
							Map<String, String> maps = null;//解析excel或xml的返回的数据
							Map<Integer,String> resultLog = new HashMap<Integer,String>();//存放每行的日志信息
							
							UploadTaskDetail uploadTaskDetail=uploadTaskDetails.get(j);//获取待处理的该条数据
							
							logger.info("【ImportUploadTaskThread】 资源导入线程->>> 执行导入字表  该条数据Id："+uploadTaskDetail.getId());
							try {
								if(uploadTask.getFiletype()==1){
									//修改子表状态  待处理
									uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS2);
									batchImportResService.saveOrUpdate(uploadTaskDetail);
									maps = batchImportResService.doMySqlWithExcel(uploadTaskDetail);
								}else if(uploadTask.getFiletype()==2){
									List<Map<String, String>> list = new ArrayList<Map<String,String>>();
									uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS2);
									list=batchImportResService.savefile(uploadTaskDetail.getPaths(),uploadTask,uploadTaskDetail);
									maps=list.get(0);
									//uploadTaskDetail.setBody(maps.toString());
									batchImportResService.saveOrUpdate(uploadTaskDetail);
								}else if(uploadTask.getFiletype()==3){
							
									
									
								}
							} catch (Exception e) {
								e.printStackTrace();
								logger.error(e.getMessage());
								uploadTask.setFailNum(uploadTask.getFailNum()+1);
								batchImportResService.saveOrUpdate(uploadTask);
								uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS4);
								uploadTaskDetail.setRemark("文件解析出错,请检查!"+e.getMessage());
								batchImportResService.saveOrUpdate(uploadTaskDetail);
							}
							
							//=============================验证数据	
							try {
								batchImportResService.doValidateMetadata(maps, saveCas, uploadTask, resultLog,checkRepeatMetadate,uploadTaskDetail);
							} catch (Exception e) {
								logger.error("【ImportUploadTaskThread】 资源导入线程->>>异常-----》验证子表id为"+uploadTaskDetail.getId()+"的数据异常   主表id："+uploadTask.getId());
								uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS4);
								batchImportResService.saveOrUpdate(uploadTaskDetail);
							}
							
							//=============================导入逻辑
							Ca ca =null;
							try {
								ca = saveCas.get(0);
							} catch (Exception e) {
								uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS4);
								batchImportResService.saveOrUpdate(uploadTaskDetail);
								logger.error("【ImportUploadTaskThread】 资源导入线程->>>异常-----》导入子表id为"+uploadTaskDetail.getId()+"逻辑异常1   主表id："+uploadTask.getId());
							}
							
							String result = "";
							try {
								result = batchImportResService.doPublishTask(ca, uploadTask, uploadTaskDetail,resultLog,checkRepeatMetadate,fileNotExistLog);
							} catch (Exception e) {
								uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS4);
								batchImportResService.saveOrUpdate(uploadTaskDetail);
								logger.error("【ImportUploadTaskThread】 资源导入线程->>>异常-----》导入子表id为"+uploadTaskDetail.getId()+"逻辑异常2   主表id："+uploadTask.getId());
							}
							
							if("1".equals(result)){  //导入成功
								succNum++;
								uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS3);
								batchImportResService.saveOrUpdate(uploadTaskDetail);
								//uploadTask.setFailNum(total-succNum);
								uploadTask.setAllNum(total);
								uploadTask.setSuccNum(succNum);
								batchImportResService.update(uploadTask);  //回写成功失败的状态
							}else {
								failNum++;
								uploadTask.setFailNum(failNum);
								batchImportResService.update(uploadTask);  //回写成功失败的状态
							}
							
						}
						//=============================第二层循环	循环每条主表数据对应的字表数据 结束
						
						uploadTask.setFailNum(total-succNum);
						uploadTask.setAllNum(total);
						uploadTask.setSuccNum(succNum);
						uploadTask.setFinishTime(new Date()); 
						
						if(succNum == 0){
							//全部失败
							uploadTask.setFailNum(total);
							uploadTask.setStatus(ImportStatus.STATUS5);
						}else if(total == succNum){
							//全部成功
							uploadTask.setStatus(ImportStatus.STATUS4);
						}else{
							//部分成功
							uploadTask.setFailNum(total-succNum);
							uploadTask.setStatus(ImportStatus.STATUS3);
						}
						batchImportResService.update(uploadTask);  //回写成功失败的状态
						
					}
					//=============================第一层循环	循环每条主表数据对应的字表数据 结束
				}else {
					logger.info("【ImportUploadTaskThread】 资源导入线程->>>主表中没有要处理的数据-》结束");
					break;
				}
				//=============================if判断主表数据 结束 结束
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("【ImportUploadTaskThread】 资源导入线程->>>异常-----》循环中断,继续下一循环");
			}
			
			//now = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());//更新时间
			logger.info("【ImportUploadTaskThread】 资源导入线程->>>结束-----");
		}
		//=============================while死循环结束
	}
	
	
}
