package com.brainsoon.bsrcm.search.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.brainsoon.common.util.dofile.util.WebAppUtils;

public class FileUtils {	
	public static void marge(List<String> sourceFilePaths, String outputFile) throws FileNotFoundException,IOException
	{
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(outputFile));
			BufferedReader readerA = null;
			for(String sourceFilePath : sourceFilePaths) {
				try
				{
					readerA = new BufferedReader(new FileReader(sourceFilePath));	
					String line = "";
					while((line = readerA.readLine()) != null)
					{
						writer.write(line);							
					}
					
				} catch (Exception e) {
					System.out.println("读取文件异常：" + e.getMessage());
				} finally {
					if(readerA != null)
					{
						readerA.close();
						readerA = null;
					}
				}
			}
		}
		finally
		{
			if(writer != null)
			{
				writer.close();
				writer = null;
			}
		}
	}
	public static void marge(String sourceFileA, String sourceFileB, String outputFile) throws FileNotFoundException,IOException
	{
		BufferedReader readerA = new BufferedReader(new FileReader(sourceFileA));
		BufferedReader readerB = new BufferedReader(new FileReader(sourceFileB));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		
		try
		{
			String line = "";
			while((line = readerA.readLine()) != null)
			{
				writer.write(line);				
			}
			
			while((line = readerB.readLine()) != null)
			{
				writer.write(line);								
			}
		}
		finally
		{
			if(readerA != null)
			{
				readerA.close();
			}
			
			if(readerB != null)
			{
				readerB.close();
			}
			
			if(writer != null)
			{
				writer.close();
			}
		}

	}
	
	public static String getFileContent(String fileName) throws IOException {				
		File file = getFile(fileName);

		BufferedReader reader = new BufferedReader(new FileReader(file));		
		String content = "";
		try
		{	
			content = reader.readLine();
		}
		finally
		{
			if(reader != null)
			{
				reader.close();
			}			
		}		
		return content;
	}
	
	public static String getBaseFilePath() {
		return StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
	}
	
	public static String getBookFullPath(String bookPath) {
		return getBaseFilePath() + bookPath;
	}
	
	public static String getTempCombinePath(String bookPath) {
		return WebAppUtils.getWebAppRoot() + "/temp/" + bookPath;
	}
	
	public static File getFile(String fileName) {
		File file = new File(fileName);
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("创建文件异常：" + e.getMessage());				
			}			
		}
		return file;
	}
	
	public static String getFileName(String fullFileName) {
		return fullFileName.substring(fullFileName
				.lastIndexOf(File.separatorChar) + 1);
	}
	
	public static String getFilePath(String fullFileName) {
		return fullFileName.substring(0, fullFileName
				.lastIndexOf(File.separatorChar));
	}	

	public static String getFileNameName(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf("."));
	}
	
	public static String getFileSubfix(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}
			
}
