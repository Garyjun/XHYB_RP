package com.brainsoon.resrelease.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resrelease.support.FileUtil;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.File;
import com.brainsoon.semantic.ontology.model.Organization;
import com.brainsoon.semantic.ontology.model.OrganizationItem;
import com.brainsoon.system.service.ISysDirService;
import com.google.gson.Gson;

/**
 * @ClassName: CreateDir
 * @Description: 教育库创建目录并拷贝文件
 * @author xiehewei
 * @date 2014年10月10日 下午2:25:23
 *
 */
public class CreateDirAndCopyFile {
	private static ISysDirService sysDirService = null;
	//获得目录下的根路径
//	private static final String rootPath = WebappConfigUtil.getParameter(ConstantsDef.prodFile);
	//获得目录下的根路径
//	private static final String fileRoot = WebappConfigUtil.getParameter(ConstantsDef.fileRoot);
	
	//教育库ca创建目录结构
	public String createPath(ResOrder resOrder, Ca ca){
//		List<String> paths = new ArrayList<String>();
//		List<Organization> orgs = ca.getOrganizations();
//		for(Organization org:orgs){
//			List<OrganizationItem> items = org.getOrganizationItems();
//			createPathByNode(resOrder, items,ca);
//		}
		return "";
	}
	
	private static List<String> createPathByNode(ResOrder resOrder, List<OrganizationItem> list, Ca ca) {
		//获得目录下的根路径
		String rootPath = WebappConfigUtil.getParameter(ConstantsDef.prodFile);
		//资源根路径
		String fileRoot = WebappConfigUtil.getParameter(ConstantsDef.fileRoot);
		int num = 0;
		String pathContent = "";
		List<String> pathList = new ArrayList<String>();
		for(OrganizationItem item:list){
			if(num==0){
				num++;
				continue;
			}
			String str = "";
			String[] pathArr = item.getXpath().substring(2, item.getXpath().length()).split(",");
			for(String arr:pathArr){
				for(OrganizationItem cas: list){
					if(arr.equals(cas.getNodeId())){
						str += cas.getName() + "\\";
						break;
					}
				}
			}
			if( str.lastIndexOf("\\")!=-1){
				str = rootPath + java.io.File.separator+ str.substring(0, str.lastIndexOf("\\"));
				java.io.File file = new java.io.File(str);
				if(file.exists()){
					
				}
			}
			//String path = FileUtil.makeDir(rootPath, str);
			List<File> files = item.getFiles();
			String header = rootPath.substring(0, rootPath.length()-1) ;
			header = FileUtil.makeDir(header, DateUtil.getDate(new Date()) + "\\" + resOrder.getOrderId());
			for(File file:files){
				String srcPath = fileRoot.replace("/", "\\") + file.getPath().replace("/", "\\");
				pathContent = FileUtil.makeDir(header, ca.getName()+"_"+ca.getId().replace("-", "-").replace(":", "："));
				try {
					org.apache.commons.io.FileUtils.copyFileToDirectory(new java.io.File(srcPath), new java.io.File(pathContent));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			pathList.add(str.substring(0, str.length()-1));
		}
		return pathList;
	}
	
	
	//图书资源
	public static List<String> copyBookByPath(ResOrder resOrder, List<OrganizationItem> list, Ca ca, String rootPath, String fileRoot) {
		//资源根路径
		//String fileRoot = WebappConfigUtil.getParameter(ConstantsDef.fileRoot);
//		String fileRoot = "D:/eclipse_workspace/BSFW/WebRoot/fileDir/fileRoot";
		//获得目录下的根路径
		//String rootPath = WebappConfigUtil.getParameter(ConstantsDef.prodFile);
//		String rootPath = "D:/eclipse_workspace/BSFW/WebRoot/fileDir/prodFile";
		//资源根路径
		int num = 0;
		String ignorePath = "";
		for(OrganizationItem item:list){
			if(num==0){
				num++;
				ignorePath = item.getPath();
				continue;
			}
			String content = item.getPath().substring(ignorePath.substring(0,ignorePath.lastIndexOf("/")).length()+1, item.getPath().length());
			List<File> files = item.getFiles();
			String header = rootPath.substring(0, rootPath.length()) ;
			header = FileUtil.makeDir(header, DateUtil.getDate(new Date()) + "/" + resOrder.getOrderId()+"/"+ca.getCommonMetaData().getTitle());
			if(files!=null){
				for(File file:files){
					String[] path = content.split("/");
					String flag = getContent(path);
					if(!"".equals(flag)){
						String basePath = FileUtil.makeDir(header, content.substring(0, content.lastIndexOf("/")));
						try {
							FileUtils.copyFileToDirectory(new java.io.File(fileRoot.replace("\\", "/")+file.getPath()), new java.io.File(basePath));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}		
		return new ArrayList<String>();
	}
	
	public static String getContent(String[] pathArr){
		String str = "";
		try {
			sysDirService = (ISysDirService) BeanFactoryUtil.getBean("sysDirService");
			List<String> dirs = sysDirService.getDirByResType("cbbook");
//			List<String> dirs = new ArrayList<String>();
//			dirs.add("doc");
//			dirs.add("pdf");
//			dirs.add("txt");
			String[] sysDirs = new String[dirs.size()];
			for(int i=0;i<dirs.size();i++){
				sysDirs[i] = dirs.get(i); 
			}
			for(String sysDir :sysDirs){
				if(str!=""){
					break;
				}
				for(String arr: pathArr){
					if(sysDir.equals(arr)){
						str = sysDir;
						break;
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public static void main(String[] args) {
//		String path = "TB/T06/G00001/hsjc_TB_P_Vnull_G31_S01_T06_F10_343434/原始图书new/图书/doc";
//		String ignorePath = "TB/T06/G00001/hsjc_TB_P_Vnull_G31_S01_T06_F10_343434/原始图书new/图书";
//		System.out.println(path.substring(ignorePath.substring(0,ignorePath.lastIndexOf("/")).length()+1, path.length()));
		String path = "http://10.130.29.14:8090/semantic_index_server/ontologyDetailQuery/ca?id=urn:book-a1b40929-48f2-4550-a074-ec13843b612c";
		HttpClientUtil http = new HttpClientUtil();
		String resource = http.executeGet(path);
		Gson gson = new Gson();
		Ca ca = gson.fromJson(resource, Ca.class);
//		List<Organization> org = ca.getOrganizations();
//		List<OrganizationItem> list = org.get(0).getOrganizationItems();
	//	copyBookByPath(null, list, null,"","");
	}

}
