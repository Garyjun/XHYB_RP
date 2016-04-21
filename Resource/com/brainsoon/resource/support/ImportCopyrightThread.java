package com.brainsoon.resource.support;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.resource.service.IPublishResService;
import com.brainsoon.resource.util.CaUtil;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.JsonDataObject;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;


public class ImportCopyrightThread implements Runnable {
	private static Logger logger = Logger.getLogger(ImportCopyrightThread.class);
	
	private String jsonOkLogNumStr  ="";
	private String jsonErrorLogNumStr  ="";
	private String jsonLogStr  ="";
	
	@Override
	public void run() {
		while (true) {
			try {
				// 获取任务队列
				CopyrightTaskQueue queue = CopyrightTaskQueue.getInst();
				// 启动任务，取一个任务
				ImportData task = queue.getMessage();
				//总数
				int jsonLogAllNumi = task.getAllNum();
				String sjName = task.getSjName();
				jsonLogStr = sjName + "_jsonLogStr";
				jsonOkLogNumStr = sjName + "_jsonOkLogNum";
				jsonErrorLogNumStr = sjName + "_jsonErrorLogNum";
				Map<String,String> map =  new HashMap<String,String>();
				Gson gson = new Gson();
				String filePath = task.getSyncJsonFileName();
				String countLogPath = DoFileUtils.getParentFileDir(filePath) + sjName;
				//计数器
				int num  = doCount(jsonLogAllNumi, countLogPath);
				Object jsonLogStrs = GlobalAppCacheMap.getValue(jsonLogStr);
				if (null != task) {
					//获取业务类处理
					IPublishResService publishResService = (IPublishResService)BeanFactoryUtil.getBean("publishResService");
					ISysOperateService sysOperateService = (ISysOperateService)BeanFactoryUtil.getBean("sysOperateService");
					try {
						//获取缓存中的json数据
						Ca ca = CaUtil.convertJsonToCa(task.getJsonDate(),task.getPublishType(),task.getRepeatType());
						map = ca.getMetadataMap();
						Map<String, Map<String,String>> fileMetadataFlag = null;
						String objectId = publishResService.saveImportPublishRes(ca, "",task.getRepeatType(),fileMetadataFlag);
						UserInfo userInfo = new UserInfo();
						String checkOpinion = "";
						sysOperateService.saveHistory(
								WorkFlowUtils.getExecuId(objectId, "pubresCheck"),
								checkOpinion,"资源草稿" , "添加", new Date(), Long.parseLong("180"));
						userInfo.setUserId(Long.parseLong("180"));
						userInfo.setUsername("systest");
						userInfo.setPlatformId(1);
						SysOperateLogUtils.addLog("publish_create",
								MetadataSupport.getTitle(ca), userInfo);
						if(StringUtils.isNotBlank(objectId) && map!=null && map.size()>0){
							map.put("identifier", objectId);
							map.put("msg", "成功");
							map.put("status", "0");
							//成功总数
							countNum(jsonOkLogNumStr);
						}else{
							map = gson.fromJson(task.getJsonDate(), Map.class);
							map.put("msg","失败,资源创建失败.");
							map.put("status", "-1");
							//失败总数
							countNum(jsonErrorLogNumStr);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						map = gson.fromJson(task.getJsonDate(), Map.class);
						map.put("msg","失败原因："+e.getMessage());
						map.put("status", "-1");
						//失败总数
						countNum(jsonErrorLogNumStr);
					}
					
					//记录日志
					if(jsonLogStrs == null || jsonLogStrs == ""){
						jsonLogStrs = gson.toJson(map);
					}else{
						jsonLogStrs += ","  + gson.toJson(map);
					}
				}
				
				try {
					if(num == 0){
						JsonDataObject resultJsonValue = new JsonDataObject();
						Object jsonOkLogNum = GlobalAppCacheMap.getValue(jsonOkLogNumStr);
						Object jsonErrorLogNum = GlobalAppCacheMap.getValue(jsonErrorLogNumStr);
						
						//写日志
						if(jsonLogAllNumi == 0){
							resultJsonValue.setStatus("-1");
							resultJsonValue.setMsg("资源创建失败.无资源数据");
							resultJsonValue.setData("");
						}else{
							int jsonErrorLogNumi = 0;
							if(jsonErrorLogNum!=null){
								jsonErrorLogNumi = Integer.parseInt(jsonErrorLogNum+"");
							}
							int jsonOkLogNumi = 0;
							if(jsonOkLogNum!=null){
								jsonOkLogNumi = Integer.parseInt(jsonOkLogNum+"");
							}
							if(jsonLogAllNumi == jsonErrorLogNumi){
								resultJsonValue.setStatus("-1");
								resultJsonValue.setMsg("全部操作失败，总数【"+ jsonLogAllNumi  +"】,失败数【"+ jsonErrorLogNumi  +"】");
							}else if(jsonLogAllNumi == jsonOkLogNumi){
								resultJsonValue.setStatus("0");
								resultJsonValue.setMsg("全部操作成功，总数【"+ jsonLogAllNumi  +"】,成功数【"+ jsonOkLogNumi +"】");
							}else{
								resultJsonValue.setStatus("-1");
								resultJsonValue.setMsg("部分操作成功，总数【"+ jsonLogAllNumi  +"】,成功数【"+ jsonOkLogNumi +"】,失败数【" + jsonErrorLogNumi  + "】");
							}
						}
						
						String json = gson.toJson(resultJsonValue);
						if(jsonLogStrs != null && jsonLogStrs !=""){
							json =  "{\"data\":[" + jsonLogStrs.toString() + "]," + json.substring(1, json.length());
						}
						
						//写文件
						RDFFileUtil.writeTxtFile(json, task.getSyncJsonFileName());
						//清空计数文件
						DoFileUtils.deleteDir(countLogPath);
						//清空缓存
						cleanHC();
					}else{
						GlobalAppCacheMap.putKey(jsonLogStr, jsonLogStrs);
					}
				} catch (IOException e) {
					e.printStackTrace();
					//清空缓存
					cleanHC();
				}
			}catch (Exception e) {
				logger.error(e);
				//清空缓存
				cleanHC();
			}
		}
	}

	//计算总数
	public void countNum (String key){
		int num = 0;
		Object jsonLogNum = GlobalAppCacheMap.getValue(key);
		if(jsonLogNum == null){
			num = 1;
		}else{
			num = Integer.parseInt(jsonLogNum +"") + 1;
		}
		GlobalAppCacheMap.putKey(key, num);
	}
	
		//清空缓存
		public void cleanHC(){
			GlobalAppCacheMap.removeKey(getJsonOkLogNumStr());
			GlobalAppCacheMap.removeKey(getJsonErrorLogNumStr());
			GlobalAppCacheMap.removeKey(getJsonLogStr());
		}

		
		public  synchronized  int doCount(int ijsonLogAllNum,String path) {
			int writeNum =  ijsonLogAllNum-1;
			int num = writeNum;
			List<String> snum = DoFileUtils.readTxt(path, "");
			if(snum !=null && snum.size() > 0){
				String s1 = snum.get(0);
				if(StringUtils.isNoneBlank(s1) && !s1.equals("0")){
					num = Integer.parseInt(s1)-1;
				}else{
					num = Integer.parseInt(s1);
				}
			}
			DoFileUtils.createTxt(path,num+"");
			return num;
		}
		public String getJsonOkLogNumStr() {
			return jsonOkLogNumStr;
		}

		public void setJsonOkLogNumStr(String jsonOkLogNumStr) {
			this.jsonOkLogNumStr = jsonOkLogNumStr;
		}

		public String getJsonErrorLogNumStr() {
			return jsonErrorLogNumStr;
		}

		public void setJsonErrorLogNumStr(String jsonErrorLogNumStr) {
			this.jsonErrorLogNumStr = jsonErrorLogNumStr;
		}

		public String getJsonLogStr() {
			return jsonLogStr;
		}

		public void setJsonLogStr(String jsonLogStr) {
			this.jsonLogStr = jsonLogStr;
		}

		
		
}
