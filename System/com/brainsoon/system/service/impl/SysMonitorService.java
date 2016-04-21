package com.brainsoon.system.service.impl;

import java.io.UnsupportedEncodingException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.system.service.ISysMonitorService;

@Service
public class SysMonitorService extends BaseService implements
		ISysMonitorService {

	@Override
	public String getCpuInfo(String cpuInfo) {
		if (cpuInfo.isEmpty()) {
			return "";
		}
		// 去除所有空格
		cpuInfo = cpuInfo.replace(" ", "");

		// 截取有用信息
		cpuInfo = cpuInfo.replace("\\s{1,}+", "");
		return cpuInfo;
	}

	@Override
	public String getPhysicalMemory(String physicalMemory) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOSInfo(String osInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNetInfo(String netInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAllInfo(String all) throws UnsupportedEncodingException {
		if (all.isEmpty()) {
			return "-1";
		}
		//将用户之间用符号隔开
		//all = changeRootInfo(all);
		// 去除所有空格
		all = all.replaceAll(" ", "");

		// 截取有用信息
		all = all.replaceAll("\\s", "");
		
		String[] infos = all.split(",");
		JSONObject json = new JSONObject();
		for (int i = 0; i < infos.length; i++) {
			String[] info = infos[i].split(":");
			if (info.length==2) {
				json.put(info[0], info[1]);
			}else {
				continue;
			}
		}
		return json.toString();
	}

	/**
	 * 解决登录用户区分不开的问题 将用户之间用、分开
	 * @param all
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String changeRootInfo(String all) throws UnsupportedEncodingException{
		all = new String(all.getBytes(), "UTF-8");
		String[] msgs = all.split(",");
		for (int i = 0; i < msgs.length; i++) {
			String[] each = msgs[i].split("-");
			if ("用户".equals(each[0])) {
				String root = each[1].trim();
				each[1] = root.replace(" ", "、");
			}
			msgs[i] = each[0]+"-"+each[1];
		}
		String retStr = "";
		for (int i = 0; i < msgs.length; i++) {
			retStr += msgs[i]+",";
		}
		if (retStr.endsWith(",")) {
			retStr = retStr.substring(0, retStr.length()-1);
		}
		return retStr;
	}
	
	public static void main(String[] args) {
//		String string = "IP-10.96.130.254,MAC-1502518515,用户-root root root root,Tomcat进程ID-15232";
//		String ret = changeRootInfo(string);
//		System.out.println(ret);
		String all = "cpu_id:99.7,cpu_us:0.2,disk_sda3:20,load:0.32,mem:6.55,swap:0.07";
		//System.out.println(getAllInfo(all));
	}
	
	
}
