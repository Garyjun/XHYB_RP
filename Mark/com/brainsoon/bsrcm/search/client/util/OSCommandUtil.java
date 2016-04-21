package com.brainsoon.bsrcm.search.client.util;

import java.io.IOException;

public class OSCommandUtil  {	
	
	private static String osName= System.getProperty("os.name").toUpperCase();
	private static String OS_WINDOWS = "WINDOWS";	

	public static void copyFile(String sourceFile, String destDir) throws IOException, InterruptedException {
		StringBuffer sb = new StringBuffer(256);
		if(osName.indexOf(OS_WINDOWS) >= 0){			
			sb.append("cmd.exe /C  copy ").append(sourceFile.replaceAll("/", "\\\\")).append(" ").append(destDir.replaceAll("/", "\\\\"));
		}else{
			sb.append("cp ").append(sourceFile).append(" ").append(destDir);
		}
		execSystemCommon(sb.toString());		
	}

	public static void mvFile(String sourceFile, String destDir) throws IOException, InterruptedException {
		StringBuffer sb = new StringBuffer(256);
		if(osName.indexOf(OS_WINDOWS) >= 0){
			sb.append("cmd.exe /C  move ").append(sourceFile.replaceAll("/", "\\\\")).append(" ").append(destDir.replaceAll("/", "\\\\"));
		}else{
			sb.append("mv ").append(sourceFile).append(" ").append(destDir);
		}
		execSystemCommon(sb.toString());
	}

	private static void execSystemCommon(String command) throws IOException, InterruptedException {
		try {
			Process proc = Runtime.getRuntime().exec(command);
			proc.waitFor();
		} catch (IOException e) {
			throw e;
		}
		catch(InterruptedException exp)
		{
			throw exp;
		}
	}	
	
	public static void main(String[] args) throws InterruptedException, IOException {
		String sourceFile = "J:/zx/xsm-source-zcb-svn/DispatchServer/dat/tmp/entPhone_20100107.txt";
		String destFile = "J:/zx/xsm-source-zcb-svn/DispatchServer/dat/entPhone/entPhone.txt";
		OSCommandUtil.copyFile(sourceFile, destFile);
	}

}





