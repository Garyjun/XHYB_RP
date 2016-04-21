/**
 * @FileName: ITaskProcessService.java
 * @Package:com.brainsoon.taskprocess.service
 * @Description:
 * @author: tanghui
 * @date:2015-3-31 上午10:53:12
 * @version V1.0
 * Modification History:
 * Date         Author      Version       Description
 * ------------------------------------------------------------------
 * 2015-3-31       y.nie        1.0         1.0 Version
 */
package com.brainsoon.taskprocess.service;

import java.util.List;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.taskprocess.model.TaskDetail;
import com.brainsoon.taskprocess.model.TaskProcess;
import com.brainsoon.taskprocess.model.TaskProcessorRelation;
import com.brainsoon.taskprocess.model.TaskResRelation;

/**
 * @ClassName: ITaskProcessService
 * @Description:   加工任务单管理服务层接口类
 * @author: tanghui
 * @date:2015-3-31 上午10:53:12
 */
public interface ITaskProcessService extends IBaseService {

	public TaskProcess save(TaskProcess taskProcess);

	/**
	 * 分页查询:加工任务单
	 *
	 * @param pageInfo
	 * @param taskProcess
	 * @return
	 */
	public PageResult query(PageInfo pageInfo,TaskProcess taskProcess) throws ServiceException;
	
	/**
	 * 分配资源和加工员
	 * @param makerIds 加工员id
	 * @param resourceIds 资源id
	 * @param taskProcessId 任务单id
	 */
	public void addResource(String resourceIds,List<String> resIdList,TaskProcess taskProcess);
	
	public void addMakers(String makerIds,String taskProcessId);
	
	public void finishTaskDetail(String sysResDirectoryId);
	
	public void deleteRelativeResource(String taskProcessId);
	
	public String resourceDetail(String resId);
	
	public String queryResourcesByTaskIdAndProcessor(String taskId, String processorId, String page, String size);
	
	public String queryResourcesByTaskIdAndStatus(String taskId, String status, String page, String size);
	
	public void batchDeleteResourceByTaskIdAndResDetailIds(Long taskId, String resDeatilIds);
	
	public int getNumByTaskIdAndUserId(Long taskId, Long userId);
	
	public void delProcessor(Long taskId, String processordIds);

	public List<TaskDetail> getTaskDetailByTaskIdAndStatus(Long taskId, Integer status);
	
	public List<TaskProcessorRelation> getTaskProcessorRelationByTaskId(Long taskId);
	public List<TaskProcessorRelation> getRelationByTaskIdAndProcessorIds(Long taskId , String processorIds);
	
	public String averageDist(List<TaskDetail> taskDetailList, List<TaskProcessorRelation> taskProcessorList);
	
	public TaskResRelation getResListByDetailId(Long taskDetailId);
	
	public void deleteResourceByTaskIdAndResDetailId(Long taskId, String taskDetailId);
	
	public String getTaskResPkByTaskIdAndProcessorIds(Long taskId, String processorIds);
	
	public String copyProcessFiles(Long taskId, String processorIds);
	
	public void saveAllResByCondition(Long taskId, String conditions);
	
	public String canDeleteUser(String userIds);
	
	public void saveAverageDist(List<TaskDetail> taskList, List<TaskProcessorRelation> processorList);
	
	public String getTaskResIdsByTaskIdAndProcessorIds(Long taskId, String processorIds);
	
	public void deleteRelativeProcessor(String taskProcessId);
	
	/**
	 * 通过taskProcessId获取加工任务详细信息
	 * @param taskProcessId
	 * @return
	 */
	public TaskProcess getTaskProcessInfo(Long taskProcessId);
	
	/**
	 * 通过taskProcessId 查询本加工任务下的所有的资源元数据
	 * @param taskProcessId
	 * @return
	 */
	public List<String> getAllSysResDirectoryList(Long taskProcessId);
	
	/**
	 * 根据表id和资源类型获取资源Id
	 * @param ids
	 * @param publishType
	 * @return
	 */
	public String getResIdsByIdAndPublishType(String ids , String publishType,String status);
	/**
	 * 根据page获取资源Id
	 * @param ids
	 * @param publishType
	 * @return
	 */
	public String getResIdsByPage(String page , String page1 , String pageSize , String resName , String userName , String taskName , String publishType);
	
	
	/**
	 * 取消分配的资源
	 * @param taskId
	 * @param processorId
	 * @param resIds
	 * @return
	 */
	public String removeResToProcessor(String taskId,String processorId,String resIds);
}
