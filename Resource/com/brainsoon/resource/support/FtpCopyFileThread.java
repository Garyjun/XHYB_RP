package com.brainsoon.resource.support;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.util.FileUtil;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.zip.ZipDecryptionUtil;
import com.brainsoon.common.util.dofile.zip.ZipUtil;
import com.brainsoon.resource.action.BresAction;
import com.brainsoon.resource.po.FileDownName;
import com.brainsoon.resource.po.FileDownValue;
import com.brainsoon.resource.service.IBatchImportResService;
import com.brainsoon.system.support.SysOperateLogUtils;

public class FtpCopyFileThread implements Runnable {
	private static Logger logger = Logger.getLogger(FtpCopyFileThread.class);
	private IBatchImportResService batchImportResService = null;
	private static FtpCopyFileThread ftpCopyFileThread = null;
	public FtpCopyFileThread() {}
	public static synchronized FtpCopyFileThread getInstance() {
      if(ftpCopyFileThread == null) {
    	  ftpCopyFileThread = new FtpCopyFileThread();
      }
      return ftpCopyFileThread;
	 }
	@Override
	public void run() {
		try {
			batchImportResService = (IBatchImportResService)BeanFactoryUtil.getBean("batchImportResService");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String hql = " from FileDownName where status in('2') or status in ('3') order by id desc";
		UserInfo userInfo = new UserInfo();
		List<FileDownName> fileDownNamelist = batchImportResService.query(hql);
		while (fileDownNamelist!=null && fileDownNamelist.size()>0) {
			FileDownValue fileValue = null;
			try {
				//根据主表找从表
				for(FileDownName fileTask:fileDownNamelist){
					Long fileTotalSize = 0L;
					logger.info("================================================文件大小:"+fileTotalSize+"=========================================================");
					//找从表数据 status in('2') or status in ('3') and 
					hql = " from FileDownValue where task.id="+fileTask.getId();
					List<FileDownValue> fileValuelist = batchImportResService.query(hql);
				if (null != fileValuelist && fileValuelist.size()>0) {
					//获取业务类处理
					logger.info("cccccccc=============");
					for(FileDownValue list:fileValuelist){
						//循环每条文件大小累加此方法用于多个线程同时处理文件下载   文件大小为空的情况
						fileTotalSize = fileTotalSize+Long.parseLong(list.getTotalFileSize());
						//如果是处理完的则continue;不处理
						if(list.getStatus().equals("1")){
							continue;
						}
//						HttpClientUtil http = new HttpClientUtil();
//						String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + list.getResId());
						userInfo.setUsername(fileTask.getDownloadUser());
						userInfo.setName(fileTask.getLoginUser());
						userInfo.setPlatformId(1);
						//找出从表信息
						fileValue = list;
//						Gson gson = new Gson();
//						Ca ca = gson.fromJson(resourceDetail, Ca.class);
						if(fileValue.getFilePath()!=null && !"".equals(fileValue.getFilePath())){
//							if(ca.getRealFiles()!=null){
//								String rootPath = ca.getRootPath();
//								rootPath = rootPath.replaceAll("\\\\", "/");
								String title = fileValue.getResName();
								SysOperateLogUtils.addLog("res_file_down", title,
										userInfo);
								//获得单个资源的文件path
								String path = fileValue.getFilePath();
								Date date = new Date();
								String time =  DateUtil.convertDateTimeSssToString(date)+"/";
								//生成子表文件目录
								String parentPath = BresAction.FILE_DOWN;
								parentPath = parentPath+fileTask.getFtpPath()+time;
								//isComplete=1压缩
								parentPath = parentPath.replaceAll("\\\\", "/");
								path = path.replaceAll("\\\\", "/");
								File dir = new File(parentPath);
								//创建路径
								if (!dir.exists()) {
									dir.mkdirs();
								}
								if(fileTask.getIsComplete().equals("1")){
									try {
										//创建压缩文件 + list.getId()
										parentPath =parentPath+title + ".zip";
										if (StringUtils.isNotBlank(fileValue.getPwd())) {//等修改密码
											// 不支持汉字
											ZipDecryptionUtil.encryptZipFile(path, parentPath, fileValue.getPwd(), false, false);
											//处理中状态
											fileTask.setStatus("2");
											//文件大小减去之前的累加文件，添加压缩之后文件
											fileTotalSize = fileTotalSize-Long.parseLong(list.getTotalFileSize());
											Long size = Long.parseLong(new File(parentPath).length()+"");
											fileTotalSize = fileTotalSize+size;
											//存储压缩后文件大小
											fileValue.setTotalFileSize(size+"");
											//处理完成状态
											fileValue.setStatus("1");
											fileValue.setFtpPath(time);
											//创建日期
											fileValue.setCreateTime(new Date());
											batchImportResService.update(fileValue);
											batchImportResService.update(fileTask);
										} else {
											ZipUtil.zipFileOrFolder(path, parentPath, null);
											//处理中状态
											fileTask.setStatus("2");
											//文件大小减去之前的累加文件，添加压缩之后文件
											fileTotalSize = fileTotalSize-Long.parseLong(list.getTotalFileSize());
											Long size = Long.parseLong(new File(parentPath).length()+"");
											fileTotalSize = fileTotalSize+size;
											//存储压缩后文件大小
											fileValue.setTotalFileSize(size+"");
											//处理完成状态
											fileValue.setStatus("1");
											fileValue.setFtpPath(time);
											//设置创建日期
											fileValue.setCreateTime(new Date());
											batchImportResService.update(fileValue);
											batchImportResService.update(fileTask);
										}
									} catch (Exception e) {
										logger.error("压缩出现问题" + e.getMessage());
										fileValue.setStatus("1");
										fileValue.setFtpPath("文件为空或压缩出现问题");
										//设置创建日期
										fileValue.setCreateTime(new Date());
										//单个文件大小
										fileValue.setTotalFileSize(list.getTotalFileSize());
										batchImportResService.update(fileValue);
										continue;
									}
								
								}else{
									try {
										FileUtil.copyDir(new File(path), new File(parentPath));
									} catch (Exception e) {
										e.printStackTrace();
										continue;
									}
									//设置处理中状态
									fileTask.setStatus("2");
									//设置完成状态
									fileValue.setStatus("1");
									fileValue.setFtpPath(time);
									//设置创建日期
									fileValue.setCreateTime(new Date());
									//单个文件大小
									fileValue.setTotalFileSize(list.getTotalFileSize());
									batchImportResService.update(fileValue);
									batchImportResService.update(fileTask);
								}

//							}
						}
					}
					//设置处理完状态
					fileTask.setStatus("1");
					fileTask.setFileSize(fileTotalSize+"");
					batchImportResService.update(fileTask);
				}else{
					//没有设置处理完状态
					fileTask.setStatus("1");
					fileTask.setFileSize(fileTotalSize+"");
					batchImportResService.update(fileTask);
				}
			}
				break;
			}catch (Exception e) {
				logger.info("wwwwwwwwwwwwwwwww"+e.getMessage());
				e.printStackTrace();
				break;
			}
		}
	}
		
	/**
	 * 创建路径
	 * @param path
	 */
	public void createRandomDir(String path) {
		Random random = new Random();
		int value = random.nextInt(10);
		File file = new File(path);
		if (file.exists()) {
			path = path + "_"+ value;
			createRandomDir(path);
		}else{
			file.mkdirs();
		}
	}
	/**
	 * 文件改名
	 * @param oldFilePath
	 * @param mapName
	 * @throws Exception
	 */
	public void changeFileName(File oldFilePath, Map<String, String> mapName)
			throws Exception {
		String oldName ="";
		String oldPath = "";
		String newName = ""; 
		String newPath = "";
		if(oldFilePath.isDirectory()){
			File[] files = oldFilePath.listFiles();
			if(files!=null && files.length>0){
				for(File file:files){
					if (!file.isDirectory()) {// 如果是文件，直接更名
						oldName = file.getName();
						oldPath = file.getPath();
						if(mapName.get(oldName)!=null){
							newName = mapName.get(oldName);
						}else{
							break;
						}
						oldPath = oldPath.replaceAll("\\\\", "/");
						newPath = oldPath.substring(0, oldPath.lastIndexOf("/") + 1)
								+ newName;
						file.renameTo(new File(newPath));
					} else {// 如果是文件夹，
						oldName = file.getName();
						oldPath = file.getPath();
						if(mapName.get(oldName)!=null){
							newName = mapName.get(oldName);
						}else{
							break;
						}
						oldPath = oldPath.replaceAll("\\\\", "/");
						newPath = oldPath.substring(0, oldPath.lastIndexOf("/") + 1)
								+ newName;
						changeFileName(file, mapName);// 递归
						file.renameTo(new File(newPath));
						// 循环完后，把该目录更名。
					}
				}
			}
		}else{
			oldName = oldFilePath.getName();
			oldPath = oldFilePath.getPath();
			newName = mapName.get(oldName);
			oldPath = oldPath.replaceAll("\\\\", "/");
			newPath = oldPath.substring(0, oldPath.lastIndexOf("/") + 1)
					+ newName;
			oldFilePath.renameTo(new File(newPath));
		}
	}
	
}
