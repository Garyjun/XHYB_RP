package com.brainsoon.bsrcm.search.client.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;


public class FileUtils {
	public static List<String> getFileListBySubfix(String filePath,
			final String partFileName) {
		List<String> fileNameList = new ArrayList<String>();

		File path = new File(filePath);

		String[] files = path.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {				
				return name.endsWith(partFileName);
			}
		});

		if (files != null) {
			for (String fileName : files) {
				fileNameList.add(filePath + File.separator + fileName);
			}
		}
		return fileNameList;
	}
		
	public static List<String> getFileListByRegex(String filePath, String regex) {
		List<String> fileNameList = new ArrayList<String>();

		File path = new File(filePath);

		String[] files = path.list();

		List<String> fileList = Arrays.asList(files);
		
		Iterator<String> iterator = fileList.iterator();
		while (iterator.hasNext()){
			String fileName = iterator.next();
			if (!matchString(regex, fileName)){
				iterator.remove();
			}
		}
		
		if (!fileList.isEmpty()) {
			for (String fileName : files) {
				fileNameList.add(filePath + File.separator + fileName);
			}
		}
		return fileNameList;
	}
	
	private static boolean matchString(String regex, String filename) {
		Pattern pattern = Pattern.compile(regex);
		if (pattern.matcher(filename).matches()){
			return true;
		}
		return false;
	}
	
	public static String getFileName(String fileName, String subfix) {
		return fileName.substring(0, fileName.lastIndexOf(subfix));
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
	
	public static void writeFile(String fileName, String content, boolean append) throws IOException {  
		File file = new File(fileName);
        if (!file.exists()) {
	          file.getParentFile().mkdirs();
	        try {
		        file.createNewFile();
	        } catch (IOException exp) {
        		return;
	        }
        }

       FileWriter writer = null;
       try {
	         writer = new FileWriter(file, append);
	         writer.write(content + "\n");
	         writer.flush();
       } finally {
	         if (writer != null)
	        	 writer.close();
       }
    }
	
	public static void main(String[] args) {
		List<String> fileName = FileUtils.getFileListBySubfix("E:\\图书", "xml");
		System.out.println(fileName);
	}
	
}
