package com.brainsoon.system.service;

import java.io.UnsupportedEncodingException;

import net.sf.json.JSONObject;

import org.hyperic.sigar.SigarException;

import com.brainsoon.common.service.IBaseService;

public interface ISysMonitorService extends IBaseService {
	
	//CPU信息
	public String getCpuInfo(String cpuInfo);
	
	//物理内存信息
	public String getPhysicalMemory(String physicalMemory);
	
	//当前操作系统的信息
	public String getOSInfo(String osInfo);
	
	//网络信息
	public String getNetInfo(String netInfo);
	
	public String getAllInfo(String all) throws UnsupportedEncodingException;
	
}
