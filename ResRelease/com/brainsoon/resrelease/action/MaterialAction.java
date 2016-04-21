package com.brainsoon.resrelease.action;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.OSUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.support.FtpCopyFileThread;
import com.brainsoon.resource.support.MeterialFileThread;
import com.brainsoon.resrelease.service.IResReleaseService;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.File;
import com.brainsoon.semantic.ontology.model.JsonDataObject;
import com.brainsoon.semantic.ontology.model.SearchResultCa;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.ISysParameterService;
import com.google.gson.Gson;

/**
 * @ClassName: MaterialAction
 * @Description: TODO
 * @author 
 * @date 2015年12月30日 上午10:20:55
 *
 */
@Controller
public class MaterialAction extends BaseAction{
	@Autowired
	private IResReleaseService resReleaseService;
	@Autowired
	private ISysParameterService sysParameterService ;
	//获取发布路径
	private static final String publishRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile).replaceAll("\\\\", "\\/");
	//获取资源路径
	private static final String fileRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileRoot).replaceAll("\\\\", "\\/");
	@RequestMapping(value="materialService/getMaterialRes", method = {RequestMethod.GET,RequestMethod.POST })
	public @ResponseBody void getMaterialRes(@RequestParam(value="startTime",required=false)String startTime,@RequestParam(value="endTime",required=false)String endTime){
		Gson gson = new Gson();
		Map<String,String> map = new HashMap<String, String>();
		String ip = OSUtil.getIpAddr(getRequest());
		String status = isRequestIp(ip);
		if(StringUtils.isNotBlank(status)&&!status.equals("1")){
			String returnDetail[] = status.split("=");
			outputResult(returnDetail[1]);
			return;
		}
		String method = getRequest().getMethod();
        if(!"GET".equals(method)){
        	map.put("请将提交方式更改为", "GET方式");
        	map.put("msg", "失败");
        	map.put("status", "-1");
			outputResult(gson.toJson(map));
			return;
//            throw new IllegalStateException("只接受 post 请求");
        }
		try {
//			//根据传过来的时间查询库中符合条件的数据
//			String souse = resReleaseService.getMaterialRes(startTime, endTime);
//			if(StringUtils.isNotBlank(souse)){
//				 SearchResultCa caList = gson.fromJson(souse, SearchResultCa.class);
//				 //获取时间
//				 String date = createdate();
//				 //拷贝文件
//				 copyFiles(caList,date);
//				 //创建json文件
//				 createJson(caList,date);
//			}
			new Thread(MeterialFileThread.getInstance(startTime,endTime)).start();
			map.put("msg", "正在处理中");
			map.put("status", "0");
		} catch (Exception e) {
			logger.error("素材资源对外接口异常--->"+e.getMessage());
			e.printStackTrace();
			map.put("msg", "失败");
			map.put("status", "-1");
			outputResult(gson.toJson(map));
		}
		outputResult(gson.toJson(map));
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
	//判断是否是请求的IP
	public String isRequestIp(String ip){
		String srcIp = "";
		String status = ip;
		List<SysParameter> sr = sysParameterService.findParaValue("requestInternetIpAddr");
		if (sr != null && sr.size() > 0) {
			if (sr.get(0) != null && sr.get(0).getParaValue() != null) {
				srcIp = sr.get(0).getParaValue();
			}
		}
		if(StringUtils.isNotBlank(srcIp)){
			String ipArray[] = srcIp.split(",");
			for(String arrip:ipArray){
				if(arrip.equals(ip)){
					status = "1";
					break;
				}
			}
		}
		Map<String,String> map = null;
		Gson gson = new Gson();
		if(!status.equals("1")){
			map = new HashMap<String, String>();
			map.put("status", "-1");
			map.put("msg", "Host-IP:"+ip+" is not in the request list!");
			map.put("data", "null");
			gson.toJson(map);
			String json  = gson.toJson(map);
			status = status+"="+json;
		}
		return status;
	}
}
