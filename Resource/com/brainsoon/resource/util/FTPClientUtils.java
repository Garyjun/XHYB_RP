package com.brainsoon.resource.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.HttpClientUtil;

/**
 * 
 * @author zuo
 *
 */
public class FTPClientUtils {
	private static Logger logger = Logger.getLogger(HttpClientUtil.class);
	/**
     * Description: 从FTP服务器下载文件
     * @param url FTP服务器hostname
     * @param port FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param remotePath FTP服务器上的相对路径
     * @param fileName 要下载的文件名
     * @param localPath 下载后保存到本地的路径
     * @return
     */
	public static boolean downloadFile(String url, int port, String username,
			String password, String remotePath, String fileName,
			String localPath) {
		boolean success = false;
		FTPClient ftp = new FTPClient();
		try {
			int reply;

			// 连接FTP服务器
			if (port > -1) {
				ftp.connect(url, port);
			} else {
				ftp.connect(url);
			}
			ftp.login(username, password);// 登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return success;
			}
			ftp.changeWorkingDirectory(new String(remotePath.getBytes("GB18030"),FTP.DEFAULT_CONTROL_ENCODING));// 转移到FTP服务器目录
			FTPFile[] fs = ftp.listFiles();
			for (FTPFile ff : fs) {
				String ffName=new String(ff.getName().getBytes(FTP.DEFAULT_CONTROL_ENCODING),"GB18030");
				if (ffName.equals(fileName)) {
					File localFile = new File(localPath + "/" + fileName);
					if(!localFile.getParentFile().exists()){
						new File(localPath).mkdirs();
					}
					OutputStream is = new FileOutputStream(localFile);
					ftp.retrieveFile(ff.getName(), is);
					is.close();
					success = true;
					break;
				}
			}

			ftp.logout();
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException e) {
					logger.error( e);
				}
			}
		}
		return success;
	}
	
	public static void main(String[] args) throws IOException {
		String url = WebappConfigUtil.getParameter("FTP_URL");
	  int port = WebappConfigUtil.getInteger("FTP_PORT",0);
	  String username = WebappConfigUtil.getParameter("FTP_USERNAME");
	  String password = WebappConfigUtil.getParameter("FTP_PASSWORD");
	  
	  //System.out.println(downloadFile(url, port, username, password, "初中化学/九年级下册第九单元 溶液/课题1 溶液的形成/教学视频", "初三化学：溶液的形成.mp4", "D:\\ftpDown"));
	}
}
