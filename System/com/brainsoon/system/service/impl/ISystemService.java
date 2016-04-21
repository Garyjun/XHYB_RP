package com.brainsoon.system.service.impl;

public interface ISystemService {

	/**
	 * 获取系统通知条目数
	 * 
	 * @return JSON 数据结构 {"taskNum":0,"copyrightNum":0,"total":0}
	 * "taskNum"：待办任务条目数
	 * "copyrightNum"：版权预警条目数
	 * "total":总条目数
	 */
	public String getInformNums();

}
