package com.brainsoon.taskprocess.model;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.taskprocess.service.ITaskProcessService;

/**
 * @ClassName: TaskProcessorRelation
 * @Description: 
 * @author xiehewei
 * @date 2015年6月19日 下午1:17:43
 *
 */
@Entity
@Table(name = "task_processor_relation")
public class TaskProcessorRelation extends BaseHibernateObject {
	
	private static final long serialVersionUID = 2152850575949254091L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Long id;
	
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn (name= "task_id" )
	private TaskProcess taskProcess;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn (name= "processor_id" )
	private User processor;
	
	@Transient
	private int distResNum;
	
	@Transient
	private String exportResUrl;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TaskProcess getTaskProcess() {
		return taskProcess;
	}

	public void setTaskProcess(TaskProcess taskProcess) {
		this.taskProcess = taskProcess;
	}

	public User getProcessor() {
		return processor;
	}

	public void setProcessor(User processor) {
		this.processor = processor;
	}

	public int getDistResNum() {
		int distributedResNum = 0;
		TaskProcess taskProcess = getTaskProcess();
		User user = getProcessor();
		Long taskId = null;
		Long userId = null;
		if(taskProcess!=null){
			taskId = taskProcess.getId();
		}
		if(user!=null){
			userId = user.getId();
		}
		if(taskId!=null&&userId!=null){
			distributedResNum = getTaskProcessService().getNumByTaskIdAndUserId(taskId, userId);
		}
		return distributedResNum;
	}

	public void setDistResNum(int distResNum) {
		this.distResNum = distResNum;
	}
	
	public String getExportResUrl() {
		TaskProcess taskProcess = getTaskProcess();
		User user = getProcessor();
		//资源下载路径    processExportRoot/{资源类型}/{加工单ID}/创建时间/{加工员ID}/资源名/文件夹/文件 2015-10-8 15:38:50 huangjun
		//只展示     /{加工单ID}/创建时间/{加工员ID}/
		
//		String resType = taskProcess.getResType();//资源类型ID
//		String restype = "";
//		//若是用户在数据字典里面配置了{资源类型}  则替换路径
//    	IDictNameService dictNameService = null;
//		try {
//			dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
//			LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
//			map = dictNameService.getDictMapByName("模板导入资源类型目录");
//			if(!map.isEmpty() && map.get(resType)!=null){
//				restype = map.get(resType);
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		
		
		//String processExportRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.processExportRoot).replaceAll("\\\\", "\\/"); //资源文件下载根路径
		String time2Str = DateUtil.convertDateTimeToString(taskProcess.getCreateTime()).replace(":", "").replace(" ", "");
		String exportResUrlDesc = "/" +taskProcess.getId() + "/" + time2Str + "/";
		if(user!=null){
			exportResUrlDesc += user.getId() ;
		}
		return exportResUrlDesc;
	}

	public void setExportResUrl(String exportResUrl) {
		this.exportResUrl = exportResUrl;
	}

	private ITaskProcessService getTaskProcessService(){
		ITaskProcessService taskProcessService = null;
		try {
			taskProcessService = (ITaskProcessService) BeanFactoryUtil.getBean("taskProcessService");
		} catch (Exception e) {
			logger.debug("bean['taskProcessService']尚未装载到容器中！");
			e.printStackTrace();
		}
		return taskProcessService;
	}

	@Override
	public String getObjectDescription() {
		return null;
	}

	@Override
	public String getEntityDescription() {
		return null;
	}

}
