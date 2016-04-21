package com.brainsoon.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

/**
 * 远程Shell脚本执行工具
 * 
 * @author Administrator
 */
public class RemoteShellTool {
	private Connection conn;
	private String ipAddr;
	private String charset = Charset.defaultCharset().toString();
	private String userName;
	private String password;
	private String shellPath;

	public RemoteShellTool(String ipAddr, String userName, String password,
			String charset) {
		this.ipAddr = ipAddr;
		this.userName = userName;
		this.password = password;
		if (charset != null) {
			this.charset = charset;
		}
	}

	/**
	 * 登录远程Linux主机
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean login() throws IOException {
		conn = new Connection(ipAddr);
		conn.connect(); // 连接
		return conn.authenticateWithPassword(userName, password); // 认证
	}

	/**
	 * 执行Shell脚本或命令
	 * 
	 * @param cmds
	 *            命令行序列
	 * @return
	 */
	public String exec(String cmds) {
		InputStream in = null;
		String result = "";
		try {
			if (this.login()) {
				Session session = conn.openSession(); // 打开一个会话
				session.execCommand(cmds);
				in = session.getStdout();
				result = this.processStdout(in, this.charset);
				conn.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return result;
	}

	/**
	 * 解析流获取字符串信息
	 * 
	 * @param in
	 *            输入流对象
	 * @param charset
	 *            字符集
	 * @return
	 */
	public String processStdout(InputStream in, String charset) {
		StringBuffer sb = new StringBuffer();
		try {
			byte b[] = new byte[1024];   
	        int len = 0;   
	        int temp=0;          //所有读取的内容都使用temp接收   
	        while((temp=in.read())!=-1){    //当没有读取完时，继续读取   
	            b[len]=(byte)temp;   
	            len++;   
	        }   
	        sb.append(new String(b,0,len));
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	
	
	public String getShellPath() {
		return shellPath;
	}

	public void setShellPath(String shellPath) {
		this.shellPath = shellPath;
	}

	//test
	public static void main(String[] args) {
		String shellPath = "/home/szgl/test";
//		String cmds = "cd " + shellPath + ";./1.sh";
		String cmds = "cd /home/szgl/test;./cpu.sh";
//		String cmds = "IP=$(/sbin/ifconfig eth0|sed -n \"2p\"|awk -F \"[ ]+|:\" '{print $4}') echo \"IP:$IP\"";
		RemoteShellTool remoteShellTool = new RemoteShellTool("10.130.39.1","root","qnsoft","utf-8");
		String str = remoteShellTool.exec(cmds);
		System.out.println("======str:================ \n" + str);
	}
}