package com.brainsoon.common.util.dofile.view;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.brainsoon.common.util.dofile.code.Epub2Html;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.FilePathUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.dofile.zip.ZipOrRarUtil;


/**
 * 
 * @ClassName: ReadOnLineUtil 
 * @Description:  在线阅读工具类
 * @author tanghui 
 * @date 2014-5-5 下午1:43:00 
 *
 */
public class ReadOnLineUtil {

	private ReadOnLineUtil() {
	}

	
	
	/**
	 * 根据资源文件，获取目录
	 * 
	 * @param swfFilePackage
	 * @param id
	 * @param filePath
	 * @param fileType
	 * @return HashMap{"epubPath":"String","catalogList":"List<CatalogDTO>"}
	 * @throws Exception
	 */
	public static HashMap<String,Object> getEpubInfo(String filePath, String fileType) throws Exception {
		HashMap<String,Object> rtn = new HashMap<String,Object>();
		/**
		 * 1)epub资源处理方式：版式解压文件到swf目录后,读取opf文件找到对应pdf文件,流式处理,转换xml成html,
		 * 2)读取ncx文件目录 epub文件处理方式：解压文件到swf目录后，读取toc.ncx文件
		 *
		 * 1、释放资源到临时目录,判断book文件夹是否已经生成过，如果生成过则不在重新生成
		 */
		 //获取文件名称
		String fileName = DoFileUtils.getFileNameNoEx(filePath);
		
		//获取epub阅读的临时目录
		filePath = filePath.replaceAll("\\\\", "/");
		String bookTempPath = filePath.substring(0,filePath.lastIndexOf("."));
		
		File zipTempFile = new File(bookTempPath);
		String[] list = zipTempFile.list();
		if (list == null || list.length <= 0) {
			try {
				ZipOrRarUtil.unzip(filePath, bookTempPath,fileType,false);
			} catch (Exception e) {
				zipTempFile.delete();
				throw new DoFileException("zip文件解压时出错" + e.getMessage());
			}
		}
		
		rtn.put("epubPath", bookTempPath);
		
		
		/**
		 * 2、寻找目录文件ncx路径（根据 META-INF下的container.xml）
		 */
		File ncxFile = getNcxFileByBasePath(bookTempPath);
		
		
		/**
		 * 3、根据ncx文件生成目录结构（中间会处理需要转换的xml文件）
		 */
		if (ncxFile.exists()) {
			//在根目录下创建临时：chapters_html 目录，为了存放html文件
			String targetPath = ncxFile.getParentFile().getPath() + File.separator;
			List<CatalogDTO> catalogList = Epub2Html.createCatalogByNcx(ncxFile.getPath(), targetPath,new File(bookTempPath).getAbsolutePath());
			rtn.put("catalogList", catalogList);
		}
		return rtn;
	}
	
	
	/**
	 * 根据包的基础路径获取ncx文件,先根据container.xml获取opf文件，读取opf中的ncx文件
	 * @param basePath
	 * @return
	 */
	public static File getNcxFileByBasePath(String basePath){
		File ncx = null;
		File container = new File(basePath + File.separatorChar + "META-INF" + File.separatorChar + "container.xml");
		if(container.exists()){
			SAXReader reader = new SAXReader();
			Document doc = null;
			try {
				doc = reader.read(container);
			} catch (Exception e) {
				throw new DoFileException("未找到container.xml");
			}
			Element root = doc.getRootElement();
			Element element = root.element("rootfiles");
			List<Element> items = element.elements("rootfile");
			File opfFile = null;
			for (Element ele : items) {
				Attribute currentAri = ele.attribute("full-path");
				String fullPath = currentAri.getValue();
				if(!StringUtils.isEmpty(fullPath)){
					opfFile = new File(basePath + File.separatorChar + fullPath);
					if(!opfFile.exists()){
						throw new DoFileException("未找到opf文件");
					}
					break;
				}
			}
			try {
				doc = reader.read(opfFile);
			} catch (DocumentException e) {
				throw new DoFileException("opf文件格式错误");
			}
			root = doc.getRootElement();
			element = root.element("manifest");
			items = element.elements("item");
			for (Element ele : items) {
				if(StringUtils.equalsIgnoreCase(ele.attribute("id").getValue(),"ncx")||StringUtils.equalsIgnoreCase(ele.attribute("id").getValue(),"ncxtoc")){
					String ncxPath = ele.attribute("href").getValue();
					ncx = new File(opfFile.getParent() + File.separatorChar +  ncxPath);
					if(!ncx.exists()){
						throw new DoFileException("未找到ncx文件");
					}
					break;
				}
			}
		}
		return ncx;
	}
	/**
	 * 处理json中特殊字符
	 * @param s
	 * @return
	 */
	public static String stringToJson(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {

			char c = s.charAt(i);
			switch (c) {
			case '/':
				sb.append("\\/");
				break;
			case '\b': // 退格
				sb.append("\\b");
				break;
			case '\f': // 走纸换页
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n"); // 换行
				break;
			case '\r': // 回车
				sb.append("\\r");
				break;
			case '\t': // 横向跳格
				sb.append("\\t");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String StringPageToJSON(String ors) {
		ors = ors == null ? "" : ors;
		StringBuffer buffer = new StringBuffer(ors);
		int i = 0;
		while (i < buffer.length()) {
			if (buffer.charAt(i) == '\\') {
				buffer.insert(i, '\\');
				i += 2;
			} else {
				i++;
			}
		}
		return buffer.toString();
	}
}
