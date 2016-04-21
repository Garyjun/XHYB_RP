package com.brainsoon.resource.support;

import com.brainsoon.semantic.ontology.model.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resrelease.service.IResReleaseService;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.SearchResultCa;
import com.google.gson.Gson;

public class MeterialFileThread implements Runnable {
	private static Logger logger = Logger.getLogger(MeterialFileThread.class);
	//获取发布路径
	private static final String publishRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile).replaceAll("\\\\", "\\/");
	//获取资源路径
	private static final String fileRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileRoot).replaceAll("\\\\", "\\/");
	private String startTime;
	private String endTime;
	private IResReleaseService resReleaseService = null;
	private static MeterialFileThread ftpCopyFileThread = null;
	public MeterialFileThread() {}
	
	public MeterialFileThread(String startTime, String endTime) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public static synchronized MeterialFileThread getInstance(String startTime,String endTime ) {
//      if(ftpCopyFileThread == null) {
    	  ftpCopyFileThread = new MeterialFileThread(startTime,endTime);
//      }
//    	  System.out.println(Thread.currentThread().getName()+"开始");
      return ftpCopyFileThread;
	 }
	@Override
	public void run() {
		String souse = "";
		try {
			resReleaseService = (IResReleaseService)BeanFactoryUtil.getBean("resReleaseService");
			souse = resReleaseService.getMaterialRes(startTime, endTime);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Gson gson = new Gson();
		if (StringUtils.isNotBlank(souse)) {
			 SearchResultCa caList = gson.fromJson(souse, SearchResultCa.class);
			 String date = "";
			 try {
				 date = createdate();
				 //拷贝文件
				 copyFiles(caList,date);
				 //创建json文件
				 createJson(caList,date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		 System.out.println(Thread.currentThread().getName()+"结束");
	}
	//获取当前时间
		public String createdate(){
			Date date = new Date();
			String time2Str = DateUtil.convertDateTimeToString(date).replace(":", "").replace(" ", "").replace("-", "");
			time2Str = time2Str.substring(0, time2Str.length()-2);
			return time2Str;
		}
		//拷贝文件
		public void copyFiles(SearchResultCa caList,String date){
			try {
				if(caList!=null){
					List<Ca> calListStr = caList.getRows();
					if(calListStr != null && calListStr.size() > 0){
						//循环遍历ca
						for (Ca ca : calListStr) {
							List<File> files = ca.getRealFiles();
							if(files!=null && files.size()>0){
								String pid = "";
								String paths = "";
								for (File file : files) {
									if(file.getIsDir().equals("1")){
										if(file.getPid().equals("-1")){
											pid = file.getId();
										}
										if(file.getPid().equals(pid)){
											if(file.getPath().contains("成品")){
												paths = file .getPath();
											}
										}
									}
								}
								java.io.File file = new java.io.File(fileRoot+paths);
								if(file.exists()){
									java.io.File file2[] = file.listFiles();
									String fileEncode = System.getProperty("file.encoding");
									String number = ca.getMetadataMap().get("scOrNumber");
									logger.info("素材资源中资源原始资源编号为------>"+number);
									if(StringUtils.isNotBlank(number)){
										String newfiles = publishRoot+"resRelease/material/"+date+"/"+number+"/";
										for (int i = 0; i < file2.length; i++) {
											if(file2[i].exists()){
												String filepath = file2[i].getPath().replaceAll("\\\\", "\\/");
												filepath = filepath.substring(filepath.lastIndexOf("/")+1, filepath.length());
												java.io.File newfile = new java.io.File(new String((newfiles+filepath).getBytes("UTF-8"), fileEncode));
												if(file2[i].isDirectory()){
													FileUtils.copyDirectory(file2[i], newfile);
												}else{
													FileUtils.copyFile(file2[i], newfile);
												}
											}
										}
									}else{
										throw new ServiceException("素材资源中资源原始资源编号为空！请检查");
									}
									
								}
							}
						}
					}
				}
			} catch (Exception e) {
				throw new ServiceException("拷贝文件异常！");
			}
		}
		//创建JSON文件
		public void createJson(SearchResultCa caList,String date){
			try {
				if(caList!=null){
					List<Ca> calListStr = caList.getRows();
					if(calListStr != null && calListStr.size() > 0){
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("releaseId", date);
						jsonObject.put("totalNum", String.valueOf(calListStr.size()));
						JSONArray jsonArray = new JSONArray();
						for (int i=0;i<calListStr.size();i++) {
							Ca ca = calListStr.get(i);
							JSONObject objects = new JSONObject();
							JSONObject object = JSONObject.fromObject(ca);
							JSONObject object2 = object.getJSONObject("metadataMap");
							Iterator it = object2.keys();
							while (it.hasNext()) {
								String keys = (String) it.next();
								if(!keys.equals("type")){
									objects.put(keys, object2.getString(keys));
								}
							}
							//循环遍历拷贝后的文件查找文件路径
							String newfiles = publishRoot+"resRelease/material/"+date+"/"+object2.getString("scOrNumber")+"/";
							java.io.File file = new java.io.File(newfiles);
							String file2path = "";
							if(file.exists()){
								java.io.File[] files =file.listFiles();
								for (java.io.File file2 : files) {
									if(file2.isDirectory()){
										//是文件夹
										file2path = file2.getPath();
										file2path = file2path.substring(file2path.lastIndexOf(object2.getString("scOrNumber")), file2path.length());
										file2path = file2path.replaceAll("\\\\", "\\/");
										file2path = file2path+"/";
									}else{
										//不是文件夹
										file2path = file2.getPath();
										file2path = file2path.substring(file2path.lastIndexOf(object2.getString("scOrNumber")), file2path.length());
										file2path = file2path.replaceAll("\\\\", "\\/");
									}
								}
							}
							if(StringUtils.isNotBlank(file2path)){
								objects.put("path", file2path);
							}else{
								objects.put("path", object2.getString("scOrNumber")+"/");
							}
							logger.info("-----------------单个资源拼接Json完成------------------------");
							jsonArray.add(i, objects);
						}
						jsonObject.put("resList", jsonArray);
						//生成文件，并放在指定目录
						String paths = publishRoot+"resRelease/material/"+date+"/matedata.json";
						java.io.File file = new java.io.File(paths);
						if(!file.exists()){
							file.createNewFile();
						}
						
						byte[] b = jsonObject.toString().getBytes("UTF-8");
						FileOutputStream outputStream = new FileOutputStream(file);
						outputStream.write(b, 0, b.length);
						outputStream.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("创建JOSN文件失败！");
			}
			
		}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
}
