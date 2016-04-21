package com.brainsoon.search.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resource.po.FileDownName;
import com.brainsoon.resource.po.FileDownValue;
import com.brainsoon.resource.support.FtpCopyFileThread;
import com.brainsoon.search.service.IAcrossStoreSearchService;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;

@Service
public class AcrossStoreSearchService extends BaseService implements IAcrossStoreSearchService {
	
	
	@Autowired
	private ISysParameterService sysParameterService;
	
	//查询资源详细
	private static final String PUBLISH_DETAIL_URL = WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL");
	
	
	@Override
	public String queryFormList(String metadataMap,String page,String size) {
		HttpClientUtil http = new HttpClientUtil();
		String url = WebappConfigUtil.getParameter("PUBLISH_QUERY_URL");
		UserInfo user = LoginUserUtil.getLoginUser();
		if(page==null) {
			page="";
		}
		if(size==null) {
			size="";
		}
		String publishType = "";
		if(StringUtils.isNotBlank(metadataMap)){
			url+="?metadataMap="+metadataMap+"&page="+page+"&size="+size+"&queryType=1";
		}else{
			url+="?page="+page+"&size="+size+"&queryType=1";
		}
		if(user!=null && user.getResTypes()!=null&&!"".equals(user.getResTypes())){
			Iterator it = user.getResTypes().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				if(pairs.getKey()!=null){
					publishType = publishType+pairs.getKey()+",";
				}
			}
			if(publishType.endsWith(",")){
				publishType = publishType.substring(0,publishType.length()-1);
			}
			url+="&publishType="+publishType;
		}
		String userIds = user.getDeptUserIds();
		int isPrivate = user.getIsPrivate();
		if (isPrivate == 1) {
			if(StringUtils.isNotBlank(userIds)){
			}else{
				userIds = user.getUserId()+",";
			}
		}
		if(LoginUserUtil.getLoginUser().getResTypes()!=null){
			if (StringUtils.isNotBlank(userIds)) {
				if(userIds.endsWith(",")){
					userIds = userIds.substring(0,userIds.length()-1);
				}
				url+="&creator="+userIds;
			}else{
				url+="&creator="+user.getUserId();
			}
		}else{
			url+="&creator=-2";
		}
		String formList = http.executeGet(url);
		return formList;
	}

	
	
	/**
	 * 根据选中的资源的id 通过ftp下载对应资源下的文件
	 * fengda 2015年11月9日
	 * @param ids  选中的资源id
	 * @param encryptPwd	加密密匙
	 * @param ftpFlag		下载方式1.http下载 2.ftp下载但此处只支持ftp下载
	 * @param encryptZip	下载后文件的相对路径
	 * @param isComplete	是否压缩  1.是 2.不是
	 * @return
	 */
	@Override
	public String createFtpDownload(String ids, String encryptPwd,
			String ftpFlag, String encryptZip, String isComplete) {
		try{
			if(StringUtils.isNotBlank(ids)){
				String uuidPath = encryptZip;
				int num = 0;   //防止资源名称重名，为了加以区分
				String title = "";  // 资源名称
				Long fileSize = 0L;  //文件大小
				Long totalSize = 0L;
				String idsArray[] = ids.split(",");  //资源ids
				List<String> list = new ArrayList<String>();
				HttpClientUtil http = new HttpClientUtil();
				
				FileDownName fileTask = new FileDownName();  //资源文件下载任务的主表实体类
				FileDownValue fileValue = null;				//资源文件下载任务的从表实体类
				fileTask.setStatus("3");  			//表示待处理
				fileTask.setFtpPath(uuidPath);
				fileTask.setPwd(encryptPwd);
				fileTask.setCreateTime(new Date());
				fileTask.setDownloadType(isComplete);
				sysParameterService.create(fileTask);
				for(int i=0;i<idsArray.length;i++){
					fileSize = 0L;
					fileValue = new FileDownValue();
					fileValue.setResId(idsArray[i]);
					try{
						String resourceDetail = http.executeGet(PUBLISH_DETAIL_URL+"?id="+idsArray[i]);
						Gson gson = new Gson();
						//将每个资源的详细信息注入到ca中
						Ca ca = gson.fromJson(resourceDetail, Ca.class);
						title = MetadataSupport.getTitle(ca);
						if(list.contains(title)){
							num++;
							title = title +"(" + num + ")";
						}
						fileValue.setResName(title);
						fileValue.setStatus("3");
						fileValue.setTask(fileTask);
						fileValue.setFtpPath(uuidPath);
						fileValue.setPwd(encryptPwd);
						list.add(title);
						
						
						if(ca != null && ca.getRealFiles() != null){
							int realNum = ca.getRealFiles().size();
							for(int j = 0; j <realNum; j++){
								if(ca.getRealFiles().get(j).getFileByte() != null
										&& ca.getRealFiles().get(j).getIsDir().equals("2")){
									totalSize = totalSize + Long.parseLong(ca.getRealFiles().get(j).getFileByte());
									fileSize = fileSize + Long.parseLong(ca.getRealFiles().get(j).getFileByte());
								}
							}
						}
						String resName = "";
						for(String li : list){
							resName = resName + li + ",";
						}
						
						fileValue.setTotalFileSize(fileSize + "");
						fileTask.setFileSize(totalSize + "");
						fileTask.setResName(resName);
						sysParameterService.update(fileTask);
						sysParameterService.create(fileValue);
					}catch(Exception e){
						e.printStackTrace();
					}
					
				}
			}
			new Thread(new FtpCopyFileThread()).start();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return ftpFlag;
	}

	
	/**
	 * fengda 2015年11月9日
	 * 根据分页（输入起始页结束页）下载对应资源下的文件
	 * @param cas    根据页数查出的资源列表
	 * @param flag	   下载方式1.http下载 2.ftp下载但此处只支持ftp下载
	 * @param encryptZip   下载后文件的相对路径
	 * @param encryptPwd    加密密匙
	 * @param isComplete   是否压缩  1.是 2.不是
	 * @return
	 */
	@Override
	public String createByPageFtpDownload(List<Ca> cas, String flag,
			String encryptZip, String encryptPwd, String isComplete) {
		try {
			if (cas != null && cas.size() > 0) {
				String uuidPath = encryptZip;
				int num = 0;			//防止资源名称重名，为了加以区分
				String title = "";		// 资源名称
				Long fileSize = 0L;		//文件大小
				Long totalSize = 0L;
				List<String> list = new ArrayList<String>();
				FileDownName fileTask = new FileDownName();		//资源文件下载任务的主表实体类
				FileDownValue fileValue = null;					//资源文件下载任务的从表实体类
				fileTask.setStatus("3");						//表示待处理
				fileTask.setFtpPath(uuidPath);
				fileTask.setPwd(encryptPwd);
				fileTask.setCreateTime(new Date());
				fileTask.setDownloadType(isComplete);
				sysParameterService.create(fileTask);
				
				
				//循环资源下的文件的结果集
				for (int i = 0; i < cas.size(); i++) {
					fileSize = 0L;
					fileValue = new FileDownValue();
					fileValue.setResId(cas.get(i).getObjectId());
					HttpClientUtil http = new HttpClientUtil();
					//查询资源详细
					String resourceDetail = http.executeGet(WebappConfigUtil
							.getParameter("PUBLISH_DETAIL_URL")
							+ "?id="+ cas.get(i).getObjectId());
					Gson gson = new Gson();
					Ca ca = gson.fromJson(resourceDetail, Ca.class);
					title = MetadataSupport.getTitle(ca);
					if (list.contains(title)) {
						num++;
						title = title + "(" + num + ")";
					}
					fileValue.setResName(title);
					fileValue.setStatus("3");
					fileValue.setTask(fileTask);
					fileValue.setFtpPath(uuidPath);
					fileValue.setPwd(encryptPwd);
					list.add(title);
					
					
					//获取每个资源下的所有的文件
					if (ca != null) {
						if (ca.getRealFiles() != null) {
							//获取资源下的文件信息 
							int realNum = ca.getRealFiles().size();
							for (int j = 0; j < realNum; j++) {
								if (ca.getRealFiles().get(j).getFileByte() != null
										&& ca.getRealFiles().get(j).getIsDir().equals("2")) {
									totalSize = totalSize + Long.parseLong(ca.getRealFiles()
													.get(j).getFileByte());
									fileSize = fileSize + Long.parseLong(ca.getRealFiles()
													.get(j).getFileByte());
								}
							}
						}
						
						//主表中由于是按批次处理，所以所有的资源名称以逗号隔开存在主表中
						String resName = "";
						for (String li : list) {
							resName = resName + li + ",";
						}
						
						
						fileValue.setTotalFileSize(fileSize + "");
						fileTask.setFileSize(totalSize + "");
						fileTask.setResName(resName);
						sysParameterService.update(fileTask);
						sysParameterService.create(fileValue);
					}
				}
			}
			new Thread(new FtpCopyFileThread()).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

}
