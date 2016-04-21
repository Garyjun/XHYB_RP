package com.brainsoon.resrelease.support;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;

import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.po.SubjectStore;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.semantic.ontology.model.OrganizationItem;
import com.brainsoon.system.service.IDictNameService;

public class FileUtil {
	
	//获得发布的根路径
	private static final String publishRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile).replaceAll("\\\\", "\\/");
	public  static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");

	/** 
	 * 创建绝对路径(包含多级) 
	 *  
	 * @param header 
	 *            绝对路径的前半部分(已存在) 
	 * @param tail 
	 *            绝对路径的后半部分(第一个和最后一个字符不能是/，格式：123/258/456) 
	 * @return 新创建的绝对路径 
	 */  
	public static String makeDir(String header, String tail) {
		if(tail.trim()!=null&&tail.length()>0){
			tail = tail.replaceAll("\\\\", "/");
		}
	    String[] sub = tail.split("/"); 
	    File dir = new File(header);  
	    for (int i = 0; i < sub.length; i++) {  
	        if (!dir.exists()) {  
	            dir.mkdir();  
	        }  
	        File dir2 = new File(dir + File.separator + sub[i]);  
	        if (!dir2.exists()) {  
	            dir2.mkdir();  
	        }  
	        dir = dir2;  
	    }  
	    return dir.toString();  
	}  
	// 初始化n，用于计数
	//static int n = 0;
	/**
	 * 列出指定目录下的所有文件
	 * @param file
	 */
	public static List<String> getFile(File file) {
		List<String> list = new ArrayList<String>();
		try {
			if (file.exists()) {
				// 判断文件是否是文件，如果是文件，获取路径，并计数
				if (file.isFile()) {
					list.add(file.getAbsolutePath());
					System.out.println(file.getAbsolutePath());
				} else {
					// 如果是文件夹，声明一个数组放文件夹和他的子文件
					File[] f = file.listFiles();
					// 遍历文件件下的文件，并获取路径
					if(f!=null){
						for (File file2 : f) {
							getFile(file2);
						}
					}
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return list;
	}

	// 获得指定能目录下的一级文件
	public static List<String> getFirstLevelFile(File file) {
		List<String> list = new ArrayList<String>();
		try {
			// 如果是文件夹，声明一个数组放文件夹和他的子文件
			if (!file.isFile()) {
				File[] files = file.listFiles();
				// 遍历文件件下的文件，如果是文件，获取路径并放入集合中
				if(files!=null){
					for (File f : files) {
						if (f.isFile()) {
							list.add(f.getAbsolutePath());
							System.out.println(f.getAbsolutePath());
						}
					}
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/*public String getFile(File file){
		if (!file.isFile()) {
			File[] files = file.listFiles();
			// 遍历文件件下的文件，如果是文件，获取路径并放入集合中
			for (File f : files) {
				if (f.isFile()) {
					list.add(f.getAbsolutePath());
					System.out.println(f.getAbsolutePath());
				}
			}
		} else {
			
		}
	}*/
	
	public String getDirectory(List<OrganizationItem> items){
		String xpathParent = "";
		String nameParent = "";
		String fileContent = "";
		String absoluPath = "";
		String fileContentpath = "";
		List<String> paths = new ArrayList<String>();
		for(int i=1;i<items.size();i++){
			String xpathChild = items.get(i).getXpath();
			if(xpathChild.contains(xpathParent)){
				String name = items.get(i).getName();
				fileContent = nameParent+File.separator+name+items.get(i);
				List<com.brainsoon.semantic.ontology.model.File> files = items.get(i).getFiles();
				if(files!=null){
					for(com.brainsoon.semantic.ontology.model.File file:files){
						absoluPath = file.getPath();
						fileContentpath = fileContent+File.separator+absoluPath;
						paths.add(fileContentpath);
					}
				}
				
			}else{
				fileContent = nameParent;
				List<com.brainsoon.semantic.ontology.model.File> files = items.get(i).getFiles();
				if(files!=null){
					for(com.brainsoon.semantic.ontology.model.File file:files){
						absoluPath = file.getPath();
						fileContentpath = fileContent+File.separator+absoluPath;
						paths.add(fileContentpath);
					}
				}
			}
			xpathParent = items.get(i).getXpath();
			nameParent = items.get(i).getName();
		}
		
		return "";
	}
	
	public static boolean isFileExist(String path){
		boolean flag = true;
		File file = new File(path);
		if(file.exists()&&file.isFile()){
			flag = true;
		}else{
			flag = false;
		}
		return flag;
	}
	
	
	/**
     * Used to list the files / subdirectories in a given directory.
     * @param dir Directory to start listing from
     */
    private static void doSimpleFileListing(String dirName) {
 
        System.out.println();
        System.out.println("Simple file listing...");
        System.out.println("----------------------");
 
        File dir = new File(dirName);
        
        String[] children = dir.list();
 
        printFiles(children, dirName);
 
    }
 
 
    /**
     * Used to list the files / subdirectories in a given directory and also
     * provide a filter class.
     * @param dir Directory to start listing from
     * @param ff  A string that can be used to filter out files from the
     *            returned list of files. In this example, the String
     *            values is used to only return those values that start
     *            with the given String.
     */
    private static void doFileFilterListing(String dirName, String ff) {
 
        System.out.println("Filter file listing...");
        System.out.println("----------------------");
 
        final String fileFilter = ff;
 
        File           dir     = new File(dirName);
        FilenameFilter filter  = null;
 
        if (fileFilter != null) {
 
            // It is also possible to filter the list of returned files.
            // This example uses the passed in String value (if any) to only
            // list those files that start with the given String.
            filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith(fileFilter);
                }
            };
        }
 
        String[] children = dir.list(filter);
 
        printFiles(children, dirName);
 
    }
 
 
    /**
     * Used to list the files / subdirectories in a given directory and also
     * provide a filter class that only lists the directories.
     * @param dir Directory to start listing from
     */
    private static void doFileFilterDirectoryListing(String dirName) {
 
        System.out.println("Filter Directory listing...");
        System.out.println("---------------------------");
 
        File dir = new File(dirName);
 
        // The list of files can also be retrieved as File objects. In this
        // case, we are just listing all files in the directory. For the sake
        // of this example, we will not print them out here.
        File[] files = (new File(dirName)).listFiles();
 
        // This filter only returns directories
        FileFilter dirFilter = new FileFilter() {
            public boolean accept(File dir) {
                return dir.isDirectory();
            }
        };
 
        files = dir.listFiles(dirFilter);
 
        for (int i=0; i<files.length; i++) {
            System.out.println("[D] : " + files[i]);
        }
        System.out.println();
 
 
    }
 
 
    /**
     * Utility method to print the list of files to the terminal
     * @param children A String array of the file names to print out
     * @param dirName  The given directory to start the listing at.
     */
    private static void printFiles(String[] children, String dirName) {
 
        if (children == null) {
            System.out.println("Error with " + dirName);
            System.out.println("Either directory does not exist or is not a directory");
        } else {
            for (int i=0; i<children.length; i++) {
                // Get filename of file or directory
                String filename = children[i];
                if ((new File(dirName + File.separatorChar + filename)).isDirectory()) {
                    System.out.print("[D] : ");
                } else {
                    System.out.print("[F] : ");
                }
                System.out.println(dirName + File.separatorChar + filename);
            }
        }
        System.out.println();
 
    }
    
    
    public static void listDict(String location, List<String> nameList) {   
        File fileList = new File(location);  
        if (fileList.isDirectory()) {  
            File[] files = fileList.listFiles();  
            if(files!=null){
	            for (File f : files) {  
	                if (f.isDirectory()) {  
	                    listDict(f.getPath(), nameList); // 递归  
	                } else {  
	                    String fullpath = f.getPath();  
	                    String name = f.getName();  
	                    String ext = name.substring(name.indexOf(".") + 1); // 文件扩展名  
	                    if ("dic".equalsIgnoreCase(ext)) { // 过滤只要dic的文件  
	                        nameList.add(fullpath);                       
	                    }  
	                }  
	            }
            }
        }  
    }  
	
 // 文件所在的层数  
    private static int fileLevel;  

    /** 
     * 生成输出格式 
     * @param name 输出的文件名或目录名 
     * @param level 输出的文件名或者目录名所在的层次 
     * @return 输出的字符串 
     */  
    public static String createPrintStr(String name, int level) {  
        // 输出的前缀  
        String printStr = "";  
        // 按层次进行缩进  
        for (int i = 0; i < level; i ++) {  
            printStr  = printStr + "  ";  
        }  
        printStr = printStr + "- " + name;  
        return printStr;  
    }  

    /** 
     * 输出初始给定的目录 
     * @param dirPath 给定的目录 
     */  
    public static void printDir(String dirPath){  
        // 将给定的目录进行分割  
        String[] dirNameList = dirPath.split("\\\\");  
        // 设定文件level的base  
        fileLevel = dirNameList.length;  
        // 按格式输出  
        for (int i = 0; i < dirNameList.length; i ++) {  
        	System.out.println(createPrintStr(dirNameList[i], i));  
        }  
    }  
    
    
    /** 
     * 输出初始给定的目录 
     * @param dirPath 给定的目录 
     * @return 
     */  
    public static String getPublishPath(ResOrder resOrder) throws Exception{ 
    	/*String resType = resOrder.getTemplate().getType();//资源ID
    	String restype = "";
    	IDictNameService dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
    	LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
		map = dictNameService.getDictMapByName("模板导入资源类型目录");
		if(!map.isEmpty() && map.get(resType)!=null){
			restype = map.get(resType);
		}*/
    	//当前时间
		//String time2Str = DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replace(":", "").replace(" ", "");
		//发布路径= ../fileDir/prodFile/offLine(onLine)/{需求单编号} or{{需求单名称}}/
		String publishDir = publishRoot  + resOrder.getTemplate().getPublishType() + "/" + resOrder.getOrderId() + "/";//源文件发布路径
		java.io.File publishFile = new java.io.File(publishDir);
		if(!publishFile.exists()){
			publishFile.mkdirs();
		}
		return publishDir;
    }  
    /** 
     * 修改后的输出初始给定的目录 (需求单)
     * @param dirPath 给定的目录 
     * @return 
     */  
    public static String getPublishPathsxqd(ResOrder resOrder) throws Exception{ 
		//发布路径= ../fileDir/prodFile/offLine(onLine)/{需求单编号} or{{需求单名称}}/
		String publishDir = publishRoot + resOrder.getTemplate().getPublishType() +"/"+ "xqd" + "/"+ resOrder.getOrderId() + "/";//源文件发布路径
		java.io.File publishFile = new java.io.File(publishDir);
		if(!publishFile.exists()){
			publishFile.mkdirs();
		}
		return publishDir;
    }  
    /** 
     * 修改后的输出初始给定的目录 (主题库)
     * @param dirPath 给定的目录 
     * @return 
     */  
    public static String getPublishPathsztk(SubjectStore subjectStore) throws Exception{ 
		//发布路径= ../fileDir/prodFile/offLine(onLine)/{主题库编号} or{{主题库名称}}/
		String publishDir = publishRoot + subjectStore.getTemplate().getPublishType() + "/"  +  "ztk" + "/"+ subjectStore.getId() + "/";//源文件发布路径
		java.io.File publishFile = new java.io.File(publishDir);
		if(!publishFile.exists()){
			publishFile.mkdirs();
		}
		return publishDir;
    }  
    

    /** 
     * 输出给定目录下的文件，包括子目录中的文件 
     * @param dirPath 给定的目录 
     */  
    public static void readFile(String dirPath) {  
	    // 建立当前目录中文件的File对象  
	    File file = new File(dirPath);  
	    // 取得代表目录中所有文件的File对象数组  
	    File[] list = file.listFiles();  
	    // 遍历file数组  
	    if(list!=null){
		    for (int i = 0; i < list.length; i++) {  
		        if (list[i].isDirectory()) {  
	                System.out.println(createPrintStr(list[i].getName(), fileLevel));  
	                fileLevel ++;  
	                // 递归子目录  
	                readFile(list[i].getPath());  
	                fileLevel --;  
		        } else {  
	                System.out.println(createPrintStr(list[i].getName(), fileLevel));  
		        }  
		    } 
	    }
    }  
    
}
