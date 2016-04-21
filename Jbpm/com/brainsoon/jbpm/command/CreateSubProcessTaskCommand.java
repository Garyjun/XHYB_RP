package com.brainsoon.jbpm.command;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.TaskService;
import org.jbpm.api.cmd.Command;
import org.jbpm.api.cmd.Environment;
import org.jbpm.api.task.Task;
import org.jbpm.pvm.internal.task.OpenTask;

import com.brainsoon.jbpm.constants.ProcessConstants;
/**
 * 
 * 在一个流程的某个节点动态创建子任务流程，即一个子任务对应一个流程
 * 主要用于汇总，会签
 *
 */
public class CreateSubProcessTaskCommand  implements Command{
	private String pId;//父流程实例id
	private String mainTaskName;//父流程主任务名称
	private Map<String,String> bizInfos;//包含子任务相关业务主键，名称的集合
	private String subProces;//子流程名
	public CreateSubProcessTaskCommand(String pId,String mainTaskName,Map bizInfos,String subProces)
	{
		this.pId=pId;
		this.mainTaskName=mainTaskName;
		this.bizInfos=bizInfos;
		this.subProces=subProces;
		
	}
	@Override
	public Object execute(Environment environment) throws Exception {
		ExecutionService executionService=environment.get(ExecutionService.class);
		TaskService taskService=environment.get(TaskService.class);
		Task mainTask=taskService.createTaskQuery().processInstanceId(pId).activityName(mainTaskName).uniqueResult();
		Set<String> keys =bizInfos.keySet();
		for(String key:keys){
			Task subTask=((OpenTask)mainTask).createSubTask();
			subTask.setName(mainTaskName+"--"+bizInfos.get(key));
			Map<String, Object> paras=new HashMap<String, Object>();
			paras.put(ProcessConstants.MAIN_TASKID, mainTask.getId());
			paras.put(ProcessConstants.SUB_TASKID, subTask.getId());
			executionService.startProcessInstanceByKey(subProces, paras, key);
		}
		return null;
	}

}
