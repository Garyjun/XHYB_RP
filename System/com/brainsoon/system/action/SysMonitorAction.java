package com.brainsoon.system.action;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.common.util.RemoteShellTool;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.ISysMonitorService;

/**
 * 通过shell执行服务器脚本，获取服务器信息
 * 
 * @author huangjun
 * @date 2015-08-05 15:15:20
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SysMonitorAction extends BaseAction {
	/** 默认命名空间 **/
	private static final String baseUrl = "/system/sysMonitor/";
	private static final String shellPath = "/home/sysMonitor";
	// 获取全部信息
	private static final String QUERY_4_ALL = "cd " + shellPath + ";./all.sh";

	@Autowired
	private ISysMonitorService monitorService;
	@Autowired
	private IDictNameService dictNameService;

/*	@RequestMapping(baseUrl + "queryAllInfo")
	@Token(save = true)
	public @ResponseBody String QueryAllInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.info("查询服务器信息");
		String result = "";

		// 获取监控服务器连接信息
		String ipAddr = null;
		String charset = Charset.defaultCharset().toString();
		String userName = null;
		String password = null;
		LinkedHashMap<String, String> metaInfoMap = dictNameService
				.getDictMapByName("服务器监控");
		for (Map.Entry<String, String> entry : metaInfoMap.entrySet()) {
			if ("ipAddr".equals(entry.getKey())) {
				ipAddr = entry.getValue();
			}
			if ("userName".equals(entry.getKey())) {
				userName = entry.getValue();
			}
			if ("password".equals(entry.getKey())) {
				password = entry.getValue();
			}
			if ("charset".equals(entry.getKey())) {
				charset = entry.getValue();
			}
		}

		RemoteShellTool remoteShellTool = new RemoteShellTool(ipAddr, userName,
				password, charset);
		logger.info("服务器连接信息：" + remoteShellTool.toString());
		String allInfo = remoteShellTool.exec(QUERY_4_ALL);
		result = monitorService.getAllInfo(allInfo);
		result = new String(result.getBytes(), "UTF-8");

		logger.info("返回的服务器信息：" + result);
		return result;
	}*/

	@RequestMapping(baseUrl + "queryServerLinkInfo")
	@Token(save = true)
	public @ResponseBody String QueryServerLinkInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.info("****进入查询服务器信息方法****");
		String result = "";
		LinkedHashMap<String, String> metaInfoMap = dictNameService
				.getDictMapByName("服务器监控");
		for (Map.Entry<String, String> entry : metaInfoMap.entrySet()) {
			result += entry.getValue()+"+"+entry.getKey()+"|";
		}
		
		if (result.endsWith("|")) {
			result=result.substring(0, result.length()-1);
		}
		logger.info("服务器列表："+result);
		return result;
	}

	@RequestMapping(baseUrl + "queryServerInfo")
	@Token(save = true)
	public @ResponseBody String QueryServerInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String serverLinkInfo = request.getParameter("linkInfo");
		logger.info("当前服务器连接信息："+serverLinkInfo);
		String[] strs = serverLinkInfo.split(";");
		
		String result = "";
		// 获取监控服务器连接信息
		String ipAddr = null;
		String charset = Charset.defaultCharset().toString();
		String userName = null;
		String password = null;
		for (int i = 0; i < strs.length; i++) {
			String[] linkStr =strs[i].split("=");
			if ("ipAddr".equals(linkStr[0])) {
				ipAddr = linkStr[1];
			}
			if ("userName".equals(linkStr[0])) {
				userName = linkStr[1];
			}
			if ("password".equals(linkStr[0])) {
				password = linkStr[1];
			}
			if ("charset".equals(linkStr[0])) {
				charset = linkStr[1];
			}
		}
		RemoteShellTool remoteShellTool = new RemoteShellTool(ipAddr, userName,password, charset);
		
		String allInfo = remoteShellTool.exec(QUERY_4_ALL);
		result = monitorService.getAllInfo(allInfo);
		//result = new String(result.getBytes(), "UTF-8");

		logger.info("返回的服务器信息：" + result);
		return result;
	}
	
	
	public static void main(String[] args) {
		RemoteShellTool remoteShellTool = new RemoteShellTool("10.130.39.1", "root",
				"qnsoft", "UTF-8");
		
		String allInfo = remoteShellTool.exec(QUERY_4_ALL);
		System.out.println(allInfo);
	}
}