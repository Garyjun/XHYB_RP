package com.brainsoon.docviewer.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.FilePathUtil;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.docviewer.model.ResConverfileTask;
import com.brainsoon.docviewer.model.ResConverfileTaskHistory;
import com.brainsoon.docviewer.service.IResConverfileTaskService;
import com.brainsoon.semantic.ontology.model.DoFileHistory;
import com.brainsoon.semantic.ontology.model.DoFileQueue;
import com.brainsoon.semantic.ontology.model.DoFileQueueList;
import com.brainsoon.semantic.ontology.model.JsonDataObject;


/**
 *
 * @ClassName: ResConverfileTaskService
 * @Description: 文件待转换任务队列相关处理
 * @author tanghui
 * @date 2014-6-11 下午6:14:13
 *
 */
@Transactional
@Service("resConverfileTaskService")
public class ResConverfileTaskService extends BaseService implements
		IResConverfileTaskService {

	/**
	 * 
	* @Title: insertQueue
	* @Description: 添加转换数据
	* @param doFileQueueList
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String insertQueue(DoFileQueueList doFileQueueList){
		try{
			for(DoFileQueue doFileQueue : doFileQueueList.getDoFileQueueList()){
				String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				doFileQueue.setTimestamp("new");
				doFileQueue.setPlatformId(1);
				doFileQueue.setCreateTime(time);
				getBaseDao().saveOrUpdate(doFileQueue);
				//getBaseDao().refresh(doFileQueue);
			}
		}catch(Exception e){
			e.printStackTrace();			
		}
		return "";
	}
	
	/**
	 * 
	* @Title: addQueue
	* @Description: 添加转换数据
	* @param doFileQueue
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String addQueue(DoFileQueue doFileQueue){
		try{
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			doFileQueue.setTimestamp("new");
			doFileQueue.setPlatformId(1);
			doFileQueue.setCreateTime(time);
			getBaseDao().saveOrUpdate(doFileQueue);
		}catch(Exception e){
			e.printStackTrace();			
		}
		return "";
	}
	
	/**
	 *
	 * @Title: saveResConverfileTask
	 * @Description: 保存文件到带转换队列中
	 * @param
	 * @return void
	 * @throws
	 */
	public void saveResConverfileTask(ResConverfileTask rcft) {
		try {
			if (rcft != null) {
				// 源文件相对路径
				String srcPath = rcft.getSrcPath();
				String resId = rcft.getResId();
				String doHasType = rcft.getDoHasType();
				String txtStr = rcft.getTxtStr();
				Integer platformId = rcft.getPlatformId();
				if (StringUtils.isNotBlank(srcPath)) {
					File file = new File(WebAppUtils.getWebAppBaseFileDirFR()+ srcPath);
					logger.info("======文件路径为：" + file);
					if(file.exists()){
						if (file.isDirectory()) {// 是否是目录，是目录则遍历
							File[] fileChild = file.listFiles();
							for (File file2 : fileChild) {
								if (file2.isFile()) {// 只对文件做处理，过滤到文件夹
									String srcRelPath = FilePathUtil.getFileRelPath(
											WebAppUtils.getWebAppBaseFileDirFR(),
											file2.getAbsolutePath());
									createResConverfileTask(srcRelPath, doHasType,resId,txtStr,platformId);
								}else if(file2.isDirectory()){
									findFiles2(file2,doHasType,resId,txtStr,platformId);
								}
							}
						} else {
							createResConverfileTask(srcPath,doHasType,resId,txtStr,platformId);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 递归查找指定类型文件
	 * @param file22 遍历目录
	 */
	public void findFiles2(File baseDirFile,String doHasType,String resId,String txtStr,Integer platformId) {
		// 判断目录是否存在
		if (baseDirFile.isDirectory()) {
			File[] fileChild = baseDirFile.listFiles();
			for (File file2 : fileChild) {
				if(file2.exists()){
					if (file2.isFile()) {// 只对文件做处理，过滤到文件夹
						String srcRelPath = FilePathUtil.getFileRelPath(
								WebAppUtils.getWebAppBaseFileDirFR(),
								file2.getAbsolutePath());
						createResConverfileTask(srcRelPath,doHasType,resId,txtStr,platformId);
					}else if(file2.isDirectory()){
						findFiles2(file2,doHasType,resId,txtStr,platformId);
					}
				}
			}
		}else{
			String srcRelPath = FilePathUtil.getFileRelPath(
					WebAppUtils.getWebAppBaseFileDirFR(),
					baseDirFile.getAbsolutePath());
			createResConverfileTask(srcRelPath,doHasType,resId,txtStr,platformId);
		}
	}


	/**
	 *
	 * @Title: createResConverfileTask
	 * @Description: 保存文件到待转换数据库队列中
	 * @param doHasType 如果为空，则调用 getDoHahType 方法去自动判断需要处理那些类型，否则直接用 doHasType的值
	 * @return void
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public void createResConverfileTask(String srcPath,String doHasType,String resId,String txtStr,Integer platformId) {
		if (StringUtils.isNotBlank(srcPath)) {
			// 文件类型
			String fileType = DoFileUtils.getExtensionName(srcPath); // 获取不带扩展名的文件名
			//判断是否是需要处理的类型
			if(DoFileUtils.checkFileIsSaveTo(fileType)){
				//如果传入了就使用，否则自动获取
				if(StringUtils.isBlank(doHasType)){
					doHasType = DoFileUtils.getDoHahType(fileType);
				}
				//如果为空，则不处理
				if(StringUtils.isNotBlank(doHasType)){
					boolean b =  true; //判断是否需要转换
					srcPath = srcPath.trim();
					//校验数据库中是否存在，存在并且转换成功，则不插入，否则转换不成功，则新插入一条并将原来的记录删除掉
					List<ResConverfileTask> ResConverfileTasks = getBaseDao().query("from ResConverfileTask r where r.srcPath='" + srcPath + "'");
					if(ResConverfileTasks != null && ResConverfileTasks.size() > 0){
						for (ResConverfileTask resConverfileTask : ResConverfileTasks) {
							if(StringUtils.isNotBlank(resConverfileTask.getDoResultType()) && resConverfileTask.getDoHasType().equals(resConverfileTask.getDoResultType())){
								b = false;
								break;
							}else{//则删除并重新插入一条记录
								getBaseDao().delete(resConverfileTask);
							}
						}
					}

					if(b){
						List<ResConverfileTaskHistory> resConverfileTasksHistorys = getBaseDao().query("from ResConverfileTaskHistory r where r.srcPath='" + srcPath + "'");
						if(resConverfileTasksHistorys != null && resConverfileTasksHistorys.size() > 0){
							for (ResConverfileTaskHistory resConverfileTaskHistory : resConverfileTasksHistorys) {
								if(StringUtils.isNotBlank(resConverfileTaskHistory.getDoResultType()) && resConverfileTaskHistory.getDoHasType().equals(resConverfileTaskHistory.getDoResultType())){
									b = false;
									break;
								}else{
									getBaseDao().delete(resConverfileTaskHistory);
								}
							}
						}
					}

					if(b){
							ResConverfileTask rcft = new ResConverfileTask();
							// 源路径
							rcft.setSrcPath(srcPath);
							// 资源id
							rcft.setResId(resId);
							// 类型
							rcft.setFileType(fileType);
							// 类型
							rcft.setPlatformId(platformId);
							// 文件转换后的路径
							// 转换后的文件路径(相对目录)
							String tarPath = FilePathUtil.getConverFileSaveRelPath(srcPath);
							rcft.setTarPath(tarPath);
							// 文件转换状态
							rcft.setStatus(0); // 待转换
							// 设置需要转换的类型
							rcft.setDoHasType(doHasType);
							//摘要
							rcft.setTxtStr(txtStr);
							rcft.setCreateTime(DateUtil.getToday());
							// 保存
							getBaseDao().create(rcft);
						}
			      }
			}
		 } else {
			logger.debug("不用做转换！未进入待处理队列....");
		}
	  }


	/**
	 *
	 * @Title: updateConverfileTask
	 * @Description: 更新待转换队列记录(必须在更新原始文件前调用本方法)
	 * @param
	 * @param  srcPath new资源根路径path
	 * @param  oldRelPath old资源path
	 * @param  oldSrcPath old资源根路径path
	 * @return void
	 * @throws
	 */
	public String updateConverfileTask(ResConverfileTask rcft) {
		String msg = "";
		String resid = rcft.getResId(); //资源id
		String srcPath = rcft.getSrcPath(); //新的源文件路径
		String oldSrcPath = rcft.getOldSrcPath(); //old的源文件路径
		try {
			if (rcft != null) {
				String doHasType = rcft.getDoHasType(); //处理类型
				File oldFile  = new File(DoFileUtils.connectFilePath(WebAppUtils.getWebAppBaseFileDirFR(),oldSrcPath));
				if(StringUtils.isNotBlank(oldSrcPath)  && StringUtils.isNotBlank(srcPath) && !oldSrcPath.equals(srcPath) && StringUtils.isNotBlank(resid)){
					if(oldFile.exists()){
						if (oldFile.isDirectory()) {// 是否是目录，是目录则遍历
							File[] fileChild = oldFile.listFiles();
							for (File file2 : fileChild) {
								if (file2.isFile()) {// 只对文件做处理，过滤到文件夹
									//相对路径 （获取源相对路径）
									String oldRelPath = FilePathUtil.getFileRelPath(WebAppUtils.getWebAppBaseFileDirFR(),file2.getAbsolutePath());
									msg += updateConverfileTaskSingleRes(resid,srcPath,doHasType,msg,oldRelPath,oldSrcPath);
								}else if(file2.isDirectory()){
									msg += updateConverfileTask1(resid,file2,doHasType,msg,oldSrcPath,srcPath);
								}
							}
						}else{
							msg += updateConverfileTaskSingleRes(resid,srcPath,doHasType,msg,oldSrcPath,oldSrcPath);
						}
					}else{
						msg = "要更新文件或目录不存在。";
					}
				}else{
					msg = "要更新的id或文件路径为空。";
				}
			}
		} catch (Exception e) {
			msg = "【" + resid + "、" + srcPath +"】异常了。" + e.getMessage();
			e.printStackTrace();
		}
		return msg;
	}


	/**
	 *
	 * @param srcPath 新目录
	 * @param oldSrcPath 老目录
	 * @param oldRelPath 相对路径
	 * @return
	 */
	public static String getNewPath(String srcPath,String oldSrcPath,String oldRelPath){
		oldSrcPath = oldSrcPath.replaceAll("\\\\", "/");
		srcPath = srcPath.replaceAll("\\\\", "/");
		String cpath = srcPath + "/" + oldRelPath.substring(oldSrcPath.length(), oldRelPath.length());
		cpath = cpath.replaceAll("//", "/");
		return cpath;
	}

	/**
	 *
	 * @Title: updateConverfileTask1
	 * @Description: 递归查找指定类型文件 ,并调用更新方法
	 * @param
	 * @return void
	 * @throws
	 */
	public String updateConverfileTask1(String resid,File file,String doHasType,String msg,String oldSrcPath,String srcPath) {
		// 相对路径
		if (file.isDirectory()) {
			File[] fileChild = file.listFiles();
			for (File file2 : fileChild) {
				if (file2.isFile()) {// 只对文件做处理，过滤到文件夹
					//相对路径 （通过转换后的相对路径来获取源相对路径）
					String oldRelPath = FilePathUtil.getFileRelPath(WebAppUtils.getWebAppBaseFileDirFR(),file2.getAbsolutePath());
					msg += updateConverfileTaskSingleRes(resid,srcPath,doHasType,msg,oldRelPath,oldSrcPath);
				}else if(file2.isDirectory()){
					msg += updateConverfileTask1(resid, file2, doHasType,msg,oldSrcPath,srcPath);
				}
			}
		}else{
			String relPath = FilePathUtil.getFileRelPath(WebAppUtils.getWebAppBaseFileDirFR(),file.getAbsolutePath());
			msg += updateConverfileTaskSingleRes(resid,relPath,doHasType,msg,oldSrcPath,oldSrcPath);
		}
		return msg;
	}


	/**
	 *
	 * @Title: updateConverfileTaskSingleRes
	 * @Description: 更新转换后的文件路径及转换队列表中的记录
	 * @param  resid 资源id
	 * @param  srcPath new资源根路径path
	 * @param  oldRelPath old资源path
	 * @param  oldSrcPath old资源根路径path
	 * @return void  返回空则说明成功，否则为失败
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public String updateConverfileTaskSingleRes(String resid,String srcPath,String doHasType,String msg,String oldRelPath,String oldSrcPath){
		try {
			if(StringUtils.isNotBlank(srcPath) && StringUtils.isNotBlank(resid)  && StringUtils.isNotBlank(oldRelPath)  && StringUtils.isNotBlank(oldSrcPath)){
				//老的转换后的绝对路径
				String baseOldTarPath  = DoFileUtils.connectFilePath(WebAppUtils.getWebAppBaseFileDirCFR(),FilePathUtil.getConverFileSaveRelPath(oldRelPath));
				//老的转换后的相对路径
				String oldRelCFRPath = FilePathUtil.getConverFileSaveRelPath(oldRelPath);
				//新的转换后的相对路径
				String newRelCFRPath = getNewPath(srcPath, oldSrcPath, oldRelCFRPath);
				//新的转换后的绝对路径
				String baseNewTarPath  = DoFileUtils.connectFilePath(WebAppUtils.getWebAppBaseFileDirCFR(),newRelCFRPath);
				//新的相对路径
				String newRelFRPath = getNewPath(srcPath, oldSrcPath, oldRelPath);
				// 第一步：查询并更新记录
				List<ResConverfileTask> resConverfileTasks = getBaseDao().query("from ResConverfileTask r where r.resId='" + resid + "' and r.srcPath='" + oldRelPath +"'");
				if(resConverfileTasks != null && resConverfileTasks.size() > 0){
					ResConverfileTask resConverfileTask = resConverfileTasks.get(0);
					resConverfileTask.setSrcPath(newRelFRPath); //新的相对路径
					resConverfileTask.setTarPath(newRelCFRPath); //新的转换后的相对路径
					resConverfileTask.setUpdateTime(DateUtil.getToday());
					getBaseDao().update(resConverfileTask);
				}else{
					List<ResConverfileTaskHistory> resConverfileTaskHistorys = getBaseDao().query("from ResConverfileTaskHistory r where r.resId='" + resid + "' and r.srcPath='" + oldRelPath +"'");
					if(resConverfileTaskHistorys != null && resConverfileTaskHistorys.size() > 0){
						ResConverfileTaskHistory resConverfileTaskHistory = resConverfileTaskHistorys.get(0);
						resConverfileTaskHistory.setSrcPath(newRelFRPath); //新的相对路径
						resConverfileTaskHistory.setTarPath(newRelCFRPath); //新的转换后的相对路径
						resConverfileTaskHistory.setUpdateTime(DateUtil.getToday());
						getBaseDao().update(resConverfileTaskHistory);
					}
				}
				//如果老的转换后的文件存在，则取拷贝并删除，否则不用操作
				if(new File(baseOldTarPath).exists()){
					if(!baseOldTarPath.equals(baseNewTarPath)){
						DoFileUtils.mkdir(baseNewTarPath);
						// 第二步：拷贝文件（老文件拷贝到新目录中）
						FileUtils.copyDirectory(new File(baseOldTarPath), new File(baseNewTarPath));
						// 第三步：删除文件（老文件夹及文件）
						DoFileUtils.deleteDir(baseOldTarPath+"/sss");
					}
				}
			}else{
				msg = "*更新的id或文件路径为空。";
			}
		} catch (Exception e) {
			msg = "【" + resid + "、" + srcPath +"】异常了。" + e.getMessage();
			e.printStackTrace();
		}
		return msg;
	}




	/**
	 *
	 * @Title: deleteConverfileTask
	 * @Description:删除转换后的文件路径及转换队列表中的记录(必须在删除原始文件前调用本方法)
	 * @param
	 * @return void
	 * @throws
	 */
	public String deleteConverfileTask(ResConverfileTask rcft) {
		String msg = "";
		String resid = rcft.getResId(); //资源id
		String srcPath = rcft.getSrcPath(); //新的源文件路径
		try {
			if (rcft != null) {
				File srcFile = new File(WebAppUtils.getWebAppBaseFileDirFR()+ srcPath);
				if(StringUtils.isNotBlank(srcPath) && StringUtils.isNotBlank(resid)){
					if(srcFile.exists()){
						if (srcFile.isDirectory()) {// 是否是目录，是目录则遍历
							File[] fileChild = srcFile.listFiles();
							for (File file2 : fileChild) {
								if (file2.isFile()) {// 只对文件做处理，过滤到文件夹
									//相对路径
									String relPath = FilePathUtil.getFileRelPath(WebAppUtils.getWebAppBaseFileDirFR(),file2.getAbsolutePath());
									msg += deleteConverfileTaskSingleRes(resid,relPath,msg);
								}else if(file2.isDirectory()){
									msg += deleteConverfileTask1(resid,file2,msg);
								}
							}
						}else{
							msg += deleteConverfileTaskSingleRes(resid,srcPath,msg);
						}
					}else{
						msg += deleteConverfileTaskSingleRes(resid,srcPath,msg);
					}
				}else{
					msg = "要删除的id或文件路径为空。";
				}
			}
		} catch (Exception e) {
			msg = "【" + resid + "、" + srcPath +"】异常了。" + e.getMessage();
			e.printStackTrace();
		}
		return msg;
	}


	/**
	 *
	 * @Title: deleteConverfileTask1
	 * @Description: 递归查找指定类型文件 ,并调用删除方法
	 * @param
	 * @return void
	 * @throws
	 */
	public String deleteConverfileTask1(String resid,File file,String msg) {
		// 相对路径
		if (file.isDirectory()) {
			File[] fileChild = file.listFiles();
			for (File file2 : fileChild) {
				if (file2.isFile()) {// 只对文件做处理，过滤到文件夹
					String relPath2 = FilePathUtil.getFileRelPath(WebAppUtils.getWebAppBaseFileDirFR(),file2.getAbsolutePath());
					msg += deleteConverfileTaskSingleRes(resid,relPath2,msg);
				}else if(file2.isDirectory()){
					msg += deleteConverfileTask1(resid, file2,msg);
				}
			}
		}else{
			String relPath = FilePathUtil.getFileRelPath(WebAppUtils.getWebAppBaseFileDirFR(),file.getAbsolutePath());
			msg += deleteConverfileTaskSingleRes(resid,relPath,msg);
		}
		return msg;
	}


	/**
	 *
	 * @Title: deleteConverfileTaskSingleRes
	 * @Description: 删除转换后的文件路径及转换队列表中的记录
	 * @param  resid  资源id
	 * @param  String srcPath 源文件路径
	 * @return String
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public String deleteConverfileTaskSingleRes(String resid,String srcPath,String msg){
		try {
			if(StringUtils.isNotBlank(srcPath) && StringUtils.isNotBlank(resid)){
				// 第一步：删除数据库记录
				List<ResConverfileTask> resConverfileTasks = getBaseDao().query("from ResConverfileTask r where r.resId='" + resid + "' and r.srcPath='" + srcPath +"'");
				if(resConverfileTasks != null && resConverfileTasks.size() > 0){
					for (ResConverfileTask resConverfileTask : resConverfileTasks) {
						getBaseDao().delete(resConverfileTask);
					}
				}else{
					List<ResConverfileTaskHistory> resConverfileTaskHistorys = getBaseDao().query("from ResConverfileTaskHistory r where r.resId='" + resid + "' and r.srcPath='" + srcPath +"'");
					for (ResConverfileTaskHistory resConverfileTaskHistory : resConverfileTaskHistorys) {
						getBaseDao().delete(resConverfileTaskHistory);
					}
				}

				// 第二步：删除文件
				// 转换后的文件路径(绝对路径)
				String baseTarPath  = DoFileUtils.connectFilePath(WebAppUtils.getWebAppBaseFileDirCFR(),FilePathUtil.getConverFileSaveRelPath(srcPath));
				if(new File(baseTarPath).exists()){//文件夹或文件存在则删除，否则不用删除
					DoFileUtils.deleteDir(baseTarPath);
				}
			}else{
				msg = "*删除的id或文件路径为空。";
			}
		} catch (Exception e) {
			msg = "【" + resid + "、" + srcPath +"】异常了。" + e.getMessage();
			e.printStackTrace();
		}
		return msg;
	}


	/**
	 *
	 * @Title: doTaskHistory
	 * @Description: 通过资源id进行重试
	 * @param
	 * @return void
	 * @throws
	 */
	@Override
	public String doTaskHistory(String ids) {
		try {

		String retryIds[] = ids.split(",");
		List<ResConverfileTaskHistory> listHistoryResIds = new LinkedList<ResConverfileTaskHistory>();
		List<ResConverfileTask> listResIds = new LinkedList<ResConverfileTask>();
		String hql= "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		for(int i =0;i<retryIds.length;i++){
			logger.info("查询待转换表和历史表，执行create创建记录");
			hql = "from ResConverfileTask where resId='"+retryIds[i]+"'";
			listResIds = (List<ResConverfileTask>) baseDao.query(hql);
			if(listResIds.size()<=0){
				hql = "from ResConverfileTaskHistory  where resId='"+retryIds[i]+"'";
				listHistoryResIds = (List<ResConverfileTaskHistory>) baseDao.query(hql);
			}
			if(listResIds.size()<=0){
				if(listHistoryResIds.size()>0){
				ResConverfileTask resConverfileTask = new ResConverfileTask();
				resConverfileTask.setRetryNum(0);
				resConverfileTask.setDescribes("");
				resConverfileTask.setDoHasType(listHistoryResIds.get(0).getDoHasType());
				resConverfileTask.setDoResultType(listHistoryResIds.get(0).getDoResultType());
				resConverfileTask.setImgDoStauts(listHistoryResIds.get(0).getImgDoStauts());
				resConverfileTask.setImgStauts(listHistoryResIds.get(0).getImgStauts());
				resConverfileTask.setTimestamp("");
				resConverfileTask.setFileId(listHistoryResIds.get(0).getFileId());
				resConverfileTask.setFileType(listHistoryResIds.get(0).getFileType());
				resConverfileTask.setStatus(listHistoryResIds.get(0).getStatus());
				resConverfileTask.setResId(listHistoryResIds.get(0).getResId());
				resConverfileTask.setSrcPath(listHistoryResIds.get(0).getSrcPath());
				resConverfileTask.setTarPath(listHistoryResIds.get(0).getTarPath());
				resConverfileTask.setTxtStauts(listHistoryResIds.get(0).getTxtStauts());
				resConverfileTask.setTxtStr(listHistoryResIds.get(0).getTxtStr());
				resConverfileTask.setTxtDoStauts(listHistoryResIds.get(0).getTxtDoStauts());
				resConverfileTask.setPlatformId(listHistoryResIds.get(0).getPlatformId());
				resConverfileTask.setPriority(listHistoryResIds.get(0).getPriority());
				resConverfileTask.setUpdateTime(DateUtil.getToday());
				baseDao.create(resConverfileTask);
				parameters.put("resId", retryIds[i]);
				hql = "delete from ResConverfileTaskHistory where resId=:resId";
				baseDao.executeUpdate(hql, parameters);
				//baseDao.delete(ResConverfileTaskHistory.class, retryIds[i]);
			}
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}

		return "1";
	}

	/**
	 *
	 * @Title: doTaskHistoryByPath
	 * @Description: 通过原路径进行重试
	 * @param
	 * @return void
	 * @throws
	 */
	@Override
	public String doTaskHistoryByPath(String srcPaths,String resId) {
		String reVal = "0";
		try {
			String srcPathStr[] = srcPaths.split(",");
			List<ResConverfileTaskHistory> listHistoryResIds = new LinkedList<ResConverfileTaskHistory>();
			List<ResConverfileTask> listResIds = new LinkedList<ResConverfileTask>();
			String hql= "";
			Map<String, Object> parameters = new HashMap<String, Object>();
			for(int i =0;i<srcPathStr.length;i++){
				hql = "from ResConverfileTask where srcPath ='"+srcPathStr[i]+"'";
				listResIds = (List<ResConverfileTask>) baseDao.query(hql);
				if(listResIds.size()<=0){
					hql = "from ResConverfileTaskHistory  where srcPath='"+srcPathStr[i]+"'";
					listHistoryResIds = (List<ResConverfileTaskHistory>) baseDao.query(hql);
				}
				if(listResIds.size()<=0){
					ResConverfileTask resConverfileTask = new ResConverfileTask();
					if(listHistoryResIds.size()>0){
						reVal="1";
						resConverfileTask.setRetryNum(0);
						resConverfileTask.setDescribes("");
						resConverfileTask.setDoHasType(listHistoryResIds.get(0).getDoHasType());
						resConverfileTask.setDoResultType(listHistoryResIds.get(0).getDoResultType());
						resConverfileTask.setImgDoStauts(listHistoryResIds.get(0).getImgDoStauts());
						resConverfileTask.setImgStauts(listHistoryResIds.get(0).getImgStauts());
						resConverfileTask.setTimestamp("");
						resConverfileTask.setFileId(listHistoryResIds.get(0).getFileId());
						resConverfileTask.setFileType(listHistoryResIds.get(0).getFileType());
						resConverfileTask.setStatus(listHistoryResIds.get(0).getStatus());
						resConverfileTask.setResId(listHistoryResIds.get(0).getResId());
						resConverfileTask.setSrcPath(listHistoryResIds.get(0).getSrcPath());
						resConverfileTask.setTarPath(listHistoryResIds.get(0).getTarPath());
						resConverfileTask.setTxtStauts(listHistoryResIds.get(0).getTxtStauts());
						resConverfileTask.setTxtStr(listHistoryResIds.get(0).getTxtStr());
						resConverfileTask.setTxtDoStauts(listHistoryResIds.get(0).getTxtDoStauts());
						resConverfileTask.setPlatformId(listHistoryResIds.get(0).getPlatformId());
						resConverfileTask.setPriority(listHistoryResIds.get(0).getPriority());
						resConverfileTask.setUpdateTime(DateUtil.getToday());
						baseDao.create(resConverfileTask);
						parameters.put("srcPath", srcPathStr[i]);
						hql = "delete from ResConverfileTaskHistory where srcPath=:srcPath";
						baseDao.executeUpdate(hql, parameters);
						//baseDao.delete(ResConverfileTaskHistory.class, retryIds[i]);
					}else{
						reVal="1";
						resConverfileTask.setPlatformId(LoginUserUtil.getLoginUser().getPlatformId());
						resConverfileTask.setSrcPath(srcPathStr[i]);
						resConverfileTask.setResId(resId);
						saveResConverfileTask(resConverfileTask);
					}
				}
				}
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
		return reVal;
	}


	/**
	 *
	 * @Title: doTaskCheckExitJL
	 * @Description:  通过txt文件校验记录是否存在
	 * @param
	 * @return String
	 * @throws
	 */
	@Override
	public String doTaskCheckExitJL() {
		String reVal = "执行完毕。";
		int oknum = 0;
		int errornum = 0;
		try {
			String path = WebAppUtils.getWebRootBaseDir("fileTemp") + "do.txt";
			List<String> listTxt =  null;
			File file = null;
			if(StringUtils.isNotBlank(path)){
				file = new File(path);
				if(!file.exists()){
					reVal = "文件不存在，无法继续。";
				}else{
					listTxt = DoFileUtils.readTxt(path, "");
				}
			}
			if(listTxt == null || listTxt.size() <= 0){
				reVal = "文件为空，无法继续。";
			}else{
				int num = 0;
				String pictureFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.pictureFormat);
				for (String str : listTxt) {
					String[] strs = str.split(",");
					int len = strs.length;
					if(strs != null && strs.length > 0){
						if(len == 2){
							try {
								String resId = strs[0];
								String srcPath = strs[1];
								if(StringUtils.isNotBlank(resId) && StringUtils.isNotBlank(srcPath)){
									String fileType = DoFileUtils.getExtensionName(srcPath);
									if(StringUtils.isNotBlank(resId) && !pictureFormat.contains(fileType)){
										ResConverfileTask resConverfileTask = new ResConverfileTask();
										resConverfileTask.setPlatformId(LoginUserUtil.getLoginUser().getPlatformId());
										resConverfileTask.setSrcPath(srcPath);
										resConverfileTask.setResId(resId);
										saveResConverfileTask(resConverfileTask);
										oknum ++;
									}else{
										errornum ++;
									}
								}else{
									errornum ++;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else{
							errornum ++;
						}
					}
					num ++;
					//改名
					if(listTxt.size() == num){
						file.renameTo(new File(path + ".bak"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			reVal = "执行失败，无法继续。";
		} finally{
			if(!reVal.equals("文件为空，无法继续。")){
			reVal = "执行成功";
			}
		}
		return reVal;
	}

	@Override
	public String createPageAll() {
		List<String> pageAllResId = new LinkedList<String>();
		logger.info("查询所有历史待重试数据");
		String hql = "select resId from ResConverfileTaskHistory r where r.doHasType<>r.doResultType";
		pageAllResId = (List<String>) baseDao.query(hql);
		String resIds="";
		if(pageAllResId.size()>0){
		for(int i = 0; i<pageAllResId.size();i++){
			resIds+=pageAllResId.get(i)+",";
		}
		}
		String a = doTaskHistory(resIds);
		return a;
	}
	
	/**
	 * 
	* @Title: deleteDoFileQueue
	* @Description: 删除转换记录（&历史表） 和转换后的文件
	* @param resId    参数 资源Id
	* @param resRootPath    参数 资源目录
	* @return void    返回类型
	* @throws
	 */
	public String deleteDoFileQueue(String resId,String resRootPath){
		String msg = "";
		try {
			if(StringUtils.isNotBlank(resRootPath) && StringUtils.isNotBlank(resId)){
				// 第一步：删除数据库记录
				List<DoFileQueue> doFileQueues = getBaseDao().query("from DoFileQueue d where d.resId='" + resId + "'");
				if(doFileQueues != null && doFileQueues.size() > 0){
					for (DoFileQueue doFileQueue : doFileQueues) {
						getBaseDao().delete(doFileQueue);
					}
				}else{
					List<DoFileHistory> doFileHistories= getBaseDao().query("from DoFileHistory d where d.resId='" + resId + "'");
					for (DoFileHistory doFileHistory : doFileHistories) {
						getBaseDao().delete(doFileHistory);
					}
				}

				// 第二步：删除文件
				// 转换后的资源文件路径(绝对路径)
				String baseResPath  = DoFileUtils.connectFilePath(WebAppUtils.getWebAppBaseFileDirCFR(),resRootPath);
				if(new File(baseResPath).exists()){//文件夹或文件存在则删除，否则不用删除
					DoFileUtils.deleteDir(baseResPath);
				}
			}else{
				msg = "*删除的id或文件路径为空。";
			}
		} catch (Exception e) {
			msg = "【" + resId + "、" + resRootPath +"】异常了。" + e.getMessage();
			e.printStackTrace();
		}
		return msg;
	}
	
	public List<DoFileQueue> queryByfileId(String objectId){
		List<DoFileQueue> doFileQueues = getBaseDao().query("from DoFileQueue d where d.objectId='" + objectId + "'");
		return doFileQueues;
	}

}
