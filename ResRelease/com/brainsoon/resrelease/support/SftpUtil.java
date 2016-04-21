package com.brainsoon.resrelease.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

/**
 * @ClassName: SFtpUpload
 * @Description: sftp上传
 * @author xiehewei
 * @date 2015年4月17日 下午1:45:07
 *
 */
public class SftpUtil {

	private static final Logger logger = Logger.getLogger(SftpUtil.class);

	private ChannelSftp sftp = null;
	
	private Channel channel = null;
	
	private Session session = null;

	/* FTP地址URL */
	private String targetHost;
	/* FTP地址端口 */
	private String targetPort;
	/* 用户名 */
	private String userName;
	/* 密码 */
	private String password;

	public SftpUtil(String targetHost, String targetPort, String userName,
			String password) {

		this.targetHost = targetHost;
		this.targetPort = targetPort;
		this.userName = userName;
		this.password = password;
	}
	
	/**
	 * 得到SFTP连接
	 * 
	 * @return
	 */
	public Boolean loadSFTPConnect() {

		try {
			int port = Integer.parseInt(targetPort);

			JSch jsch = new JSch();
			/* 创建Session */
			jsch.getSession(userName, targetHost, port);
			session = jsch.getSession(userName, targetHost, port);

			/* 输入密码进行有效连接 */
			session.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			session.setConfig(sshConfig);
			session.connect();

			/* 得到sftp通道对象 */
			channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;

		} catch (Exception e) {
			logger.error("Connected to " + targetHost + " Failed:"+ e.getMessage());
			return false;
		} 

		return true;
	}


	/**
	 * 关闭连接
	 */
	public void closeSFTPConnect() {
		if (sftp!=null ) {
			sftp.disconnect();
		}
		if (channel!= null ) {
			channel.disconnect();
		}
		if (session!=null ) {
			session.disconnect();
		}
	}

	/**
	 * 创建文件目录，如果该目录存在，则不创建
	 * 
	 * @param sftp
	 * @param sourcePath
	 *            prod/import/
	 * @param targetPath
	 *            20120316150200
	 * @return
	 */
	public Boolean mkSFTPDir(String sourcePath, String targetPath) {
		
		Boolean canCrteDir = true;
		try {
			String overPath = sourcePath + targetPath ;
			Vector vectorDirs = sftp.ls(sourcePath);
			for (int i = 0; i < vectorDirs.size(); i++) {
				LsEntry oneFile = (LsEntry) vectorDirs.get(i);

				if (targetPath.equals(oneFile.getFilename())) {
					canCrteDir = false;
					break;
				}
			}
			if (canCrteDir) {
				sftp.mkdir(overPath);
				logger.debug("Create dir: " + overPath);
			} else {
				logger.debug("Exist  dir: " + overPath);
			}

		} catch (SftpException se) {
			logger.error("Mk Dir Exception：" +  sourcePath + targetPath,se);
			return false;
		} 
		return true;
	}
	
	/**
	 * 判断目录是否为空
	 * @param sourcePath
	 * @return
	 */
	public Boolean oneDirIsEmpty(String sourcePath) {
		
		List<String> fileNames = new ArrayList<String>();
		try {
			Vector vectorDirs = sftp.ls(sourcePath);
			for (int i = 0; i < vectorDirs.size(); i++) {
				LsEntry oneFile = (LsEntry) vectorDirs.get(i);
				
				if(StringUtils.equals(".", oneFile.getFilename())||
						StringUtils.equals("..", oneFile.getFilename())) {
					continue;
				}
				SftpATTRS attr = oneFile.getAttrs();
				if(attr.isDir()) {
					return false;
				}
				fileNames.add(oneFile.getFilename());
			}
			if(fileNames.size()>0) {
				return false;
			} else {
				return true;
			}
		} catch (SftpException se) {
			logger.error("Load one dir file name Failed , dir is not exist：" + sourcePath,se);
			return false;
		} 
	}

	/**
	 * 文件上传到某一服务器目录下
	 * 
	 * @param sftp
	 * @param source
	 *            要上传的文件路径
	 * @param target
	 *            上传到服务器的目录路径
	 */
	public Boolean uploadFile(String source, String target) {
		
		FileInputStream out = null;
		
		try {

			sftp.cd(target);

			File file = new File(source);

			out =  new FileInputStream(file);
			
			sftp.setFilenameEncoding("GBK");
			sftp.put(out, file.getName());
			

		} catch (SftpException se) {
			logger.error("Upload file Exception：" + source,se);
			return false;
		} catch (Exception fe) {
			logger.error("Can not found File：" + source,fe);
			return false;
		} finally {
			if(out!=null) {
				try {
					out.close();
				}catch(IOException io) {}
			}
		}
		logger.debug("Upload file Success:" + target 
				+ new File(source).getName());
		return true;
	}

	/**
	 * 文件从某一服务器目录下下载到本地
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public Boolean downloadFile(String source, String target) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(target);
			sftp.setFilenameEncoding("GBK");
			sftp.get(source, out);
		} catch (SftpException se) {
			logger.error("Download file Exception：" + source,se);
			return false;
		} catch (FileNotFoundException fe) {
			logger.error("Can not found File：" + target,fe);
			return false;
		} finally {
			if(out!=null) {
				try {
					out.close();
				}catch(IOException io) {
					logger.error(io);
				}
			}
		}
		logger.debug("Download file Success:" + target);
		return true;
	}

	/**
	 * 获得FTP服务器上某一目录下的所有文件的名称
	 * 
	 * @param sourcePath
	 * @return
	 */
	public List<String> loadOneDirFileNames(String sourcePath) {
		List<String> fileNames = new ArrayList<String>();
		try {
			Vector vectorDirs = sftp.ls(sourcePath);
			for (int i = 0; i < vectorDirs.size(); i++) {
				LsEntry oneFile = (LsEntry) vectorDirs.get(i);
				
				if(StringUtils.equals(".", oneFile.getFilename())||
						StringUtils.equals("..", oneFile.getFilename()))
					continue;
				
				SftpATTRS attr = oneFile.getAttrs();
				if(attr.isDir()) 
					continue;

				fileNames.add(oneFile.getFilename());
			}
		} catch (SftpException se) {
			logger.error("Load one dir file name Failed , dir is not exist：" + se.getMessage());
			return fileNames;
		} 
		logger.debug("Load one dir file name Success：" + sourcePath);
		return fileNames;
	}

	/**
	 * 删除文件
	 * 
	 * @param source
	 * @return
	 */
	public Boolean deleteFile(String source) {

		try {
			sftp.rm(source);
		} catch (SftpException se) {
			logger.error("delete file Exception：" + se.getMessage());
			return false;
		} 
		logger.debug("delete file Success:" + source);
		return true;
	}
	
	/**
	 * 删除目录
	 * 
	 * @param source
	 * @return
	 */
	public Boolean deleteDir(String source) {

		try {
			
			sftp.rmdir(source);
		} catch (SftpException se) {
			logger.error("delete dir Exception：" + source,se);
			return false;
		} 
		logger.debug("delete dir Success:" + source);
		return true;
	}
	
	/**
	 * 递归删除SFTP某一目录下的所有文件
	 * @param source /root/res/import/
	 */
	public void deleteAllDir(String source) {
		try {
			Vector vectorDirs = sftp.ls(source);
			
			for (int i = 0; i < vectorDirs.size(); i++) {
				LsEntry oneFile = (LsEntry) vectorDirs.get(i);
				
				if(StringUtils.equals(".", oneFile.getFilename())||
						StringUtils.equals("..", oneFile.getFilename()))
					continue;
				
				SftpATTRS attr = oneFile.getAttrs();
				
				if(attr.isDir()) {
					iterDeleteFile(source + "/" + oneFile.getFilename());
				} else {
					sftp.rm(source + "/" + oneFile.getFilename());
				}
			}
			/* 删除当前目录 */
			sftp.rmdir(source);
			logger.debug("delete dir all Success:" + source);
		} catch (SftpException se) {
			logger.error("delete dir all Exception：" + source,se);
		}
	}
	
	/**
	 * 递归删除SFTP目录下的所有文件2(内部调用)
	 * @param source
	 */
	private void iterDeleteFile(String source ) {
		try {
			Vector vectorDirs = sftp.ls(source);
			
			for (int i = 0; i < vectorDirs.size(); i++) {
				LsEntry oneFile = (LsEntry) vectorDirs.get(i);
				
				if(StringUtils.equals(".", oneFile.getFilename())||
						StringUtils.equals("..", oneFile.getFilename()))
					continue;
				
				SftpATTRS attr = oneFile.getAttrs();
				
				if(attr.isDir()) {
					iterDeleteFile(source + "/" + oneFile.getFilename());
				} else {
					sftp.rm(source + "/" + oneFile.getFilename());
				}
				
			}
			/* 删除当前目录 */
			sftp.rmdir(source);
		} catch (SftpException se) {
			logger.error(se);
		}
	}
	
	/*public static void main(String[] args) {
		SftpUtil util = new SftpUtil("10.130.29.28","22","root","qnsoft");
		if(util.loadSFTPConnect()) {
			util.deleteFile("/root/res/imports/23423.xml");
		}
		util.closeSFTPConnect();
	}*/
}
