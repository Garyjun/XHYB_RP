package com.brainsoon.resource.service.impl;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.jbpm.constants.ProcessConstants;
import com.brainsoon.jbpm.service.IJbpmExcutionService;
import com.brainsoon.resource.po.SubjectStore;
import com.brainsoon.resource.service.ISubjectService;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.resrelease.po.ResOrderDetail;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.system.model.User;
import com.brainsoon.system.support.SysOperateLogUtils;


/**
 * @ClassName: SubjectService
 * @Description: TODO
 * @author 
 * @date 2015年9月24日 下午4:07:50
 *
 */
@Service
public class SubjectService extends BaseService implements ISubjectService{
	@Autowired
	private IResOrderService resOrderService;
	@Autowired
	private IJbpmExcutionService jbpmExcutionService;

	@Override
	public SubjectStore save(SubjectStore store) {
		return (SubjectStore)this.getBaseDao().create(store);
	}

	@Override
	public String addSubjectRes(String resid,String id,String posttype,String publishTypes) {
		UserInfo userinfo = LoginUserUtil.getLoginUser();
		String ztkids[] = resid.split(",");
		String publishType[] = publishTypes.split(",");
		for (int i = 0; i < ztkids.length; i++) {
			//前台传过来的主题库id，解析完毕，根据id查询
			SubjectStore store=(SubjectStore) getBaseDao().getByPk(SubjectStore.class, Long.decode(ztkids[i]));
			//查询到的单条信息，查询该条信息的资源类型
			String type[]=store.getTemplate().getType().split(",");
			List<String> list = new ArrayList<String>();
			for (int j = 0; j < type.length; j++) {
				list.add(type[j]);
			}
			//依次循环判断前台传过来的资源类型是否存在该主题库中
			for (int j = 0; j < publishType.length; j++) {
				if(list.contains(publishType[j])){//判断选中的主题库中包含查询的资源类型的哪一种
					//查询选中的主题库其中一种类型的资源表中的数据
					List<ResOrderDetail> detailslist=resOrderService.getResOrderDetailByOrderIds(store.getId().toString(), posttype, publishType[j]);
					//将资源id循环放入List
					List<String> resOrderids = new ArrayList<String>();
					for (ResOrderDetail resOrderDetail : detailslist) {
						resOrderids.add(resOrderDetail.getResId());
					}
					if(resOrderids!=null){
						//查询要插入表的资源表中的数据，并把资源id放入list
						List<ResOrderDetail> iddetailslist = resOrderService.getResOrderDetailByOrderIds(id, posttype, publishType[j]);
						List<String> idresOrderids =new ArrayList<String>();
						for (ResOrderDetail idresOrderDetail : iddetailslist) {
							idresOrderids.add(idresOrderDetail.getResId());
						}
						//循环选中的主题库的资源id  List
						for (String resOrderFilesid : resOrderids) {
							if(idresOrderids.contains(resOrderFilesid)){
								//如果要插入的主题库的资源包含这种资源   则查询选中主题库的资源文件 ，并把资源文件id放入list
								List<ResFileRelation> oldfileRelations = resOrderService.queryFileDetail(store.getId(), resOrderFilesid, posttype);
								List<String> oldfiles = new ArrayList<String>();
								for (ResFileRelation resFileRelation : oldfileRelations) {
									oldfiles.add(resFileRelation.getFileId());
								}
								//查询要插入的主题库的资源文件id，并放入list
								List<ResFileRelation> fileRelations = resOrderService.queryFileDetail(Long.decode(id), resOrderFilesid, posttype);
								List<String> files = new ArrayList<String>();
								for (ResFileRelation resFileRelation : fileRelations) {
									files.add(resFileRelation.getFileId());
								}
								//循环选中主题库的资源文件id  list
								for (String oldfile : oldfiles) {
									if(!files.contains(oldfile)){
										ResFileRelation fileRelation = new ResFileRelation();
										fileRelation.setFileId(oldfile);
										fileRelation.setResId(resOrderFilesid);
										fileRelation.setOrderId(Long.decode(id));
										fileRelation.setRestype(publishType[j]);
										fileRelation.setPosttype(posttype);
										resOrderService.create(fileRelation);
									}
								}
							}else{
								ResOrderDetail orderDetail = new ResOrderDetail();
								orderDetail.setResId(resOrderFilesid);
								orderDetail.setOrderId(Long.decode(id));
								orderDetail.setPosttype(posttype);
								orderDetail.setResType(publishType[j]);
								User user = new User();
								user.setId(userinfo.getUserId());
								orderDetail.setCreateUser(user);
								orderDetail.setPlatformId(userinfo.getPlatformId());
								resOrderService.create(orderDetail);
								//查询该条资源的文件id 放入list
								List<ResFileRelation> oldfileRelations = resOrderService.queryFileDetail(store.getId(), resOrderFilesid, posttype);
								List<String> oldfiles = new ArrayList<String>();
								for (ResFileRelation resFileRelation : oldfileRelations) {
									oldfiles.add(resFileRelation.getFileId());
								}
								for (String oldfile : oldfiles) {
									ResFileRelation fileRelation = new ResFileRelation();
									fileRelation.setFileId(oldfile);
									fileRelation.setResId(resOrderFilesid);
									fileRelation.setOrderId(Long.decode(id));
									fileRelation.setRestype(publishType[j]);
									fileRelation.setPosttype(posttype);
									resOrderService.create(fileRelation);
								}
							}
						}
					}
				}
			}
		}
		SysOperateLogUtils.addLog("subject_addResource", "添加资源", userinfo);
		return "succ";
	}

	@Override
	public String deleteBatchSubject(String ids, String posttype) {
		String result = "1";
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String[] idArray = ids.split(",");
		for (int i = 0; i < idArray.length; i++) {
			SubjectStore subjectStore = (SubjectStore) getBaseDao().getByPk(SubjectStore.class, Long.valueOf(idArray[i]));
			if(subjectStore.getStatus().equals(ResReleaseConstant.OrderStatus.CREATED)||subjectStore.getStatus().equals(ResReleaseConstant.OrderStatus.AUDIT_REFUSE)){
				if(subjectStore.getStatus().equals(ResReleaseConstant.OrderStatus.AUDIT_REFUSE)){
					//暂时先不删除流程数据
					List<Task> tasks = jbpmExcutionService.getCurrentTasks(WorkFlowUtils.getExecuId(idArray[i], "sub"));
					jbpmExcutionService.endTask(tasks.get(0).getId(),ProcessConstants.SUBMIT, userInfo.getUserId().toString(),subjectStore.getTemplate().getType());
					Map<String, String> map = getWorkFlowInfo(WorkFlowUtils.getExecuId(idArray[i], "sub"));
					String wfTaskId = map.get("wfTaskId");
					jbpmExcutionService.doApprove(wfTaskId, userInfo.getUserId().toString(),subjectStore.getTemplate().getType());
				}
				//删除明细
				List<ResOrderDetail> detailList = resOrderService.getResOrderDetailByOrderIdAndtype(idArray[i], posttype);
				if(detailList!=null){
					for(ResOrderDetail detail : detailList){
						resOrderService.delete(detail);
					}
				}
				//删除关联表
				List<ResFileRelation> fileList = resOrderService.queryFileByOrdeIdAndposttype(Long.valueOf(idArray[i]),posttype);
				if(fileList!=null){
					for(ResFileRelation file: fileList){
						resOrderService.delete(file);
					}
				}
				//删除主表之前先删除LOGO
				String paths = WebAppUtils.getWebAppRoot()+subjectStore.getLogo();
				File file = new File(paths);
				if(file.exists()){
					file.delete();
				}
				//删除主表
				getBaseDao().delete(SubjectStore.class, idArray[i]);
				
				//写日志
				SysOperateLogUtils.addLog("subject_del", subjectStore.getName(), userInfo);
				
			}
		}
		return result;
	}
	public Map<String, String> getWorkFlowInfo(String execuId) {
		Map<String, String> map = new HashMap<String, String>();
		List<Task> taskList = jbpmExcutionService.getCurrentTasks(execuId);
		Task task = null;
		if (taskList != null && taskList.size() > 0) {
			task = taskList.get(0);
		}
		if (task != null) {
			map.put("execuId", task.getExecutionId());
			map.put("wfTaskId", task.getId());
		}
		return map;
	}

	@Override
	public List<SubjectStore> getSubjectByTemplateId(Long templateId) {
		String hql = " from SubjectStore where template.id="+templateId;
		List<SubjectStore> stores = getBaseDao().query(hql);
		return stores;
	}
}
