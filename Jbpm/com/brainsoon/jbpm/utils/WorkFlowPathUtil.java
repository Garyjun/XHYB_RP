package com.brainsoon.jbpm.utils;

import org.apache.commons.lang.ArrayUtils;

public class WorkFlowPathUtil {
    /**
     * 根据当前任务和流程运行轨迹,获取第一个任务
     * @param flowPath
     * @return
     */
	public static String getFirstTask(String flowPath) {
		String[] tasks = flowPath.split(",");
		return tasks[0];

	}
	 /**
     * 根据当前任务和流程运行轨迹,获取最后一个任务
     * @param flowPath
     * @return
     */
	public static String getEndTask(String flowPath) {
		String[] tasks = flowPath.split(",");
		return tasks[tasks.length - 1];
	}

	/**
	 * 根据当前任务和流程运行轨迹,获得下个任务
	 * 
	 * @param currentTask
	 * @param flowPath
	 * @return
	 */
	public static String getNextTask(String currentTask, String flowPath) {
		String[] tasks = flowPath.split(",");
		String nextTask = "";
		int index = ArrayUtils.indexOf(tasks, currentTask);
		if (index > -1) {
			if (index + 1 < tasks.length) {
				nextTask = tasks[index + 1];
			}
		}

		return nextTask;

	}

	/**
	 * 根据当前任务和流程运行轨迹,获得上个任务
	 * 
	 * @param currentTask
	 * @param flowPath
	 * @return
	 */
	public static String getLastTask(String currentTask, String flowPath) {
		String[] tasks = flowPath.split(",");
		int index = ArrayUtils.indexOf(tasks, currentTask);
		String lastTask = "";
		if (index > -1) {
			if (index - 1 >= 0) {
				lastTask = tasks[index - 1];

			}
		}

		return lastTask;

	}

	/**
	 * 根据当前任务和流程运行轨迹,获得上个任务
	 * 
	 * @param currentTask
	 * @param flowPath
	 * @return
	 */
	public static String getLastTaskForFork(String currentTask, String flowPath) {
		String[] tasks = flowPath.split(",");
		int index = ArrayUtils.indexOf(tasks, currentTask);
		String lastTask = "";
		if (index > -1) {
			if (index - 1 >= 0) {
				lastTask = tasks[index - 1];
			}
		}

		return lastTask;

	}

	public static void main(String[] args) {
		String flowPath = "ocr,metadataEdit,endCheck";
		String currentTask = "metadataEdit";
		System.out.println("nextTask ****  "
				+ getNextTask(currentTask, flowPath));
		System.out.println("lastTask ****  "
				+ getLastTask(currentTask, flowPath));
		System.out.println("EndTask ****  " + getEndTask(flowPath));

	}

}
