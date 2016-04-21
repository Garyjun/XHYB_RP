package com.brainsoon.common.util.dofile.code;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.FilePathUtil;
import com.brainsoon.common.util.dofile.view.CatalogDTO;

/**
 * 
 * @ClassName: Epub2Html 
 * @Description:  epub to HTML
 * @author tanghui 
 * @date 2014-5-5 下午12:44:55 
 *
 */
public class Epub2Html {
	
	private static final Log logger = LogFactory.getLog(Epub2Html.class);
	
    /**
	 * 要替换的特殊字符串
	 */
	public static HashMap<String,String> specialCharacter;
	
	/**
	 * 在线阅读支持的格式
	 */
	public static ArrayList<String> onLineSupportType;
	static {
		specialCharacter = new HashMap<String,String>();
		specialCharacter.put(".", "。");
		specialCharacter.put(",", "，");
		specialCharacter.put("\"", "“");
		specialCharacter.put("]", "】");
		specialCharacter.put("[", "【");
		specialCharacter.put(":", "：");
		
		onLineSupportType = new ArrayList<String>();
		onLineSupportType.add("pdf");
		onLineSupportType.add("doc");
		onLineSupportType.add("docx");
		onLineSupportType.add("epub");
		onLineSupportType.add("cdp");
	}

	private Epub2Html() {
	}

	/**
	 * 根据ncx文件创建目录，并生成对应的html文件
	 * @param fullFilePath ncx文件全路径
	 * @param targetPath html文件生成的路径
	 * @param xmlFilePath 临时目录下xml文件所在的目录，用于获取内容文件所在的相对路径
	 * @return List<CatalogDTO>
	 */
	public static List<CatalogDTO> createCatalogByNcx(String filePath,String targetPath,String xmlFilePath) {
		File ncxFile = new File(filePath);
		if (!ncxFile.exists()) {
			return new ArrayList<CatalogDTO>();
		}
		boolean isCreate = true;// 测试改为false
		// 判断ncx文件同级chapters_html文件夹是否存在,如果存在,则不需要重新创建html文件
		String srcFilePath = filePath.replace(ncxFile.getName(), "chapters_html");
		File htmlFile = new File(srcFilePath);
		if (htmlFile.exists()) {
			isCreate = false;
		}

		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(ncxFile);
		} catch (Exception e) {
			throw new DoFileException("未找到ncx文件");
		}
		Element root = doc.getRootElement();
		// 获取需要解析内容的根节点
		Element element = root.element("navMap");
		List<CatalogDTO> catalogList = new ArrayList<CatalogDTO>();
		// 取得ncx的父路径
		String fileParentPath = ncxFile.getParentFile().getPath();
		// 递归遍历
		parseElement(element, fileParentPath, catalogList,targetPath, isCreate,xmlFilePath);
		// ncx解析后数据
		List<CatalogDTO> catalogListD = parseToTreeData(catalogList);
		return catalogListD;
	}
	
	/**
	 * 拷贝资源下的images或者css到临时目录swf目录，供阅读使用
	 * @param basePath
	 * @param realTarget
	 * @param parentFile
	 * @param times 迭代次数 要按照images储存的结构层级调整，如果设置为0则迭代所有，效率会低
	 * @throws IOException
	 */
	protected static void copyImagesOrCssToSwf(String basePath,String realTarget,File parentFile,int times) throws IOException {
		if(times == 0 || --times >= 0){
			File [] childs = parentFile.listFiles();
			for (File cfile : childs) {
				if(cfile.isDirectory()){
					if(StringUtils.equalsIgnoreCase(cfile.getName(), "images") || StringUtils.equalsIgnoreCase(cfile.getName(), "css")){
						String fullPath = cfile.getAbsolutePath();
						fullPath = StringUtils.replace(fullPath, basePath, "",1);
						String destStr = realTarget + File.separator + "chapters_html" + File.separator + fullPath;
						FileUtils.copyDirectory(cfile, new File(destStr));
					}else{
						copyImagesOrCssToSwf(basePath,realTarget, cfile, times);
					}
				}
			}
		}
	}
	/**
	 * 遍历ncx文件，生成目录结构，并创建相应的html
	 * @param baseEle 根元素
	 * @param fileParentPath ncx文件的父路径
	 * @param catalogList 目录
	 * @param targetPath 生成html文件的路径
	 * @param xmlFilePath 临时目录下xml文件所在的目录，用于获取内容文件所在的相对路径
	 * @param isCreate 是否生成html文件
	 */
	@SuppressWarnings("unchecked")
	private static void parseElement(Element baseEle, String fileParentPath,
			List<CatalogDTO> catalogList,String targetPath,boolean isCreate,String xmlFilePath) {
		String id = "";
		String pid = "0";
		String name = "";
		String url = "";
		String css = "";
		String startPage = "";
		String endPage = "";

		List<Element> items = baseEle.elements("navPoint");

		Attribute currentAri = null;
		for (Element ele : items) {
			currentAri = ele.attribute("id");
			if(null != currentAri){
				id = currentAri.getValue();
			}
			currentAri = ele.attribute("class");
			if(null != currentAri){
				css = currentAri.getValue();
			}
			// 判断父级别
			String parentName = ele.getParent().getName();
			if (parentName.equals("navPoint")) {
				currentAri = ele.getParent().attribute("id");
				if(null != currentAri){
					pid = currentAri.getValue();
				}
			}
			List<Element> child = ele.elements("navLabel");
			for (Element currentChild : child) {
				name = currentChild.elementText("text");
				startPage = currentChild.elementText("navStartPage");
				endPage = currentChild.elementText("navEndPage");
				if (isCreate && startPage != null) {
					// 版本cdp，不需要创建html
					isCreate = false;
				}
			}
			child = ele.elements("content");
			String realXMLFilePath = "";
			for (Element chele : child) {
				currentAri = chele.attribute("src");
				if(null != currentAri){
					url = currentAri.getValue();
				}
				String[] urls = url.split("/");
				String xmlFileName = urls[urls.length - 1];
				try {
					if (isCreate) {
						// 创建html
						// 文件名要去掉#
						String[] cuXmlFileName = xmlFileName.split("#");
						String realXMLFileName = cuXmlFileName[0];
//						String realXMLFileName = cuXmlFileName[cuXmlFileName.length - 1];
//						createHTMLContent(url, newFileName, fileParentPath);
						realXMLFilePath = fileParentPath;
						if(urls.length > 1){
							for(int i = 0;i < urls.length-1;i++){
								realXMLFilePath = realXMLFilePath + File.separator + urls[i].toString();
							}
						}
						realXMLFilePath = realXMLFilePath + File.separator + realXMLFileName;
						createHTMLContent(realXMLFilePath,targetPath);
						
						//处理xml内容中的图片
//						SAXReader saxReader = new SAXReader();
						SAXReader reader;
						Document xmlDocument = null;
						try {
						InputStream is = Epub2Html.class.getResourceAsStream(realXMLFilePath);
//						InputStream is =  new FileInputStream(realXMLFilePath);
						reader = new SAXReader();
//						reader.setDefaultHandler(this);
						xmlDocument = reader.read(is);
						} catch (DocumentException e) {
//							e.printStackTrace();
						}
//						Document xmlDocument = saxReader.read(realXMLFilePath);
						processImageSrc(realXMLFilePath, xmlDocument.asXML(),targetPath);
					}
				} catch (Exception e) {
				}
				if (xmlFileName.indexOf(".xml") > -1) {
					url = xmlFileName.replace(".xml", ".html");
				}
			}
			//替换name
			name = transformSpecialCharacter(name);
//			String rootPath = StringUtils.replace(fileParentPath, xmlFilePath, "");
//			rootPath = StringUtils.replace(rootPath, File.separator, "/");
//			if(!StringUtils.isEmpty(rootPath)){
//				url = rootPath + "/" + url;
//			}
			String baseUrl = targetPath.substring(targetPath.lastIndexOf("fileDir"), targetPath.length());
			catalogList.add(new CatalogDTO(id, pid, name, baseUrl +url, css, startPage,
					endPage,""));
			parseElement(ele, fileParentPath, catalogList, targetPath,isCreate,xmlFilePath);

		}
	}
	
	private static void processImageSrc(
			String xmlPath, 
			String xmlContent,
			String targetImagePath) {   
		//String xmlContent = FileUtil.readerFile(xmlPath);
        Pattern p = Pattern.compile("<resource\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic)\\b)[^>]*>", Pattern.CASE_INSENSITIVE);       
        Matcher m = p.matcher(xmlContent);   
        String base = new File(xmlPath).getParent().replaceAll("\\\\", "/");
        
        while (m.find()) {       
        	String quote = m.group();       
            String src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("\\s+")[0] : m.group(2); 
            if(base.indexOf("chapters")!=-1 && src.indexOf("chapters")!=-1){
            	base = base.substring(0,base.lastIndexOf("/"));
            }
            String imagePath = base + File.separator + src;
            File imageFile = new File(imagePath);
            String imageName = imageFile.getName();
            
            String imageSubPath = new File(src).getParent();
            if(StringUtils.isNotEmpty(imageSubPath)) {
            	targetImagePath  = targetImagePath + File.separator + imageSubPath;
            }
            DoFileUtils.mkdir(targetImagePath);
            try {
            	FileUtils.copyFile(new File(imagePath), new File(targetImagePath +  File.separator + imageName));
            } catch(IOException ex) {
            }
        }  
    } 

	/**
	 * 根据定义的特殊字符串，替换
	 * 
	 * @param str
	 * @return String
	 */
	public static String transformSpecialCharacter(String str) {
		if (null != specialCharacter && !StringUtils.isEmpty(str)) {
			Iterator itr = specialCharacter.entrySet().iterator();
			String key = "";
			String value = "";
			while (itr.hasNext()) {
				java.util.Map.Entry entry = (java.util.Map.Entry) itr.next();
				key = entry.getKey().toString();
				value = entry.getValue().toString();
				if (str.indexOf(key) > -1) {
					str = str.replace(key, value);
				}
			}
		}
		return str;
	}

	/**
	 * 转换成树形结构
	 * 
	 * @param lstOrg
	 * @return
	 */
	private static List<CatalogDTO> parseToTreeData(List<CatalogDTO> lstOrg) {
		if (lstOrg == null && lstOrg.size() == 0) {
			return null;
		}
		List<CatalogDTO> treeData = new ArrayList<CatalogDTO>();
		for (CatalogDTO o : lstOrg) {
			if (o.getpId().equals("0")) {
				// 取得子分类
				o.setSons(getSubCategory(lstOrg, o.getId()));
				treeData.add(o);
			}
		}

		return treeData;
	}

	/**
	 * 获取子分类
	 * 
	 * @param lstOrg
	 * @param parentId
	 * @return
	 */
	private static List<CatalogDTO> getSubCategory(List<CatalogDTO> lstOrg,
			String parentId) {
		if (lstOrg == null || lstOrg.size() == 0 || parentId == null) {
			return null;
		}
		List<CatalogDTO> treeData = new ArrayList<CatalogDTO>();
		for (CatalogDTO rc : lstOrg) {
			if (!rc.getpId().equals("0") && rc.getpId().equals(parentId)) {
				rc.setSons(getSubCategory(lstOrg, rc.getId()));
				treeData.add(rc);
			}
		}

		return treeData;
	}

	/**
	 * 创建html文件
	 * @param fullFilePath
	 * @param targetPath
	 * @throws Exception
	 */
	public static void createHTMLContent(String fullFilePath,String targetPath) throws Exception {

		try {
			SAXReader saxReader = new SAXReader();
			File docFile = new File(fullFilePath);
			if (docFile.exists()) {
				// html文件的最后路径
				String htmlFilePath = targetPath;
				DoFileUtils.mkdir(htmlFilePath);
				String fileName = docFile.getName();
				// html body中的内容
				StringBuffer content = new StringBuffer();
				//判断是否为xml文件
				String fileType = StringUtils.substringAfterLast(fileName,".");
				if(fileType.equals("xml")){
					// 读取xml信息
					Document xmlDocument = saxReader.read(docFile);
					//转换xml内容
					String xmlContent = xmlDocument.asXML();
					//处理titleofvolume等标签
					xmlContent = HtmlRegexpUtil.replaceHtmlTag(xmlContent, "titleofvolume", "", "h2", "" ,"","","");
					xmlContent = HtmlRegexpUtil.replaceHtmlTag(xmlContent, "titleofchapter", "", "h3", "" ,"","","");
					xmlContent = HtmlRegexpUtil.replaceHtmlTag(xmlContent, "titleofsection", "", "h4", "" ,"","","");
					xmlContent = HtmlRegexpUtil.replaceHtmlTag(xmlContent, "titleofsubsection", "", "h5", "" ,"","","");
					xmlContent = HtmlRegexpUtil.replaceHtmlTag(xmlContent, "titleofpreface", "", "h3", "" ,"","","");
					xmlContent = HtmlRegexpUtil.replaceHtmlTag(xmlContent, "titleofpostscript", "", "h3", "" ,"","","");
					//处理resource
					xmlContent = HtmlRegexpUtil.replaceHtmlTag(xmlContent, "resource", "src", "img", "" ,"src",".xml",".html","size","width,height");
					//处理a
					xmlContent = HtmlRegexpUtil.replaceHtmlTag(xmlContent, "a", "href", "a", "" ,"href",".xml",".html");
					content.append(xmlContent);
				}else if(fileType.equals("jpg")){
					//图片拷贝到targetPath
					File jpgFile = new File(htmlFilePath + File.separator + fileName);
					org.apache.commons.io.FileUtils.copyFile(docFile, jpgFile);
					content.append("<img src = '").append(fileName).append("'/>");
				}
				if(fileType.equals("jpg") || fileType.equals("xml")){
					// 生成html文件
					fileName = fileName.substring(0, fileName.indexOf("."));
					File htmlFile = new File(htmlFilePath);
					if (!htmlFile.exists()) {
						DoFileUtils.createdir(targetPath, "chapters_html");
					}
					htmlFilePath = htmlFilePath + File.separator + fileName + ".html";
					createHTMLPage(fileName, content.toString(), htmlFilePath);
				}
			}

		} catch (DocumentException ex) {
			logger.error("解析xml出错", ex);
			throw new Exception("解析xml出错" + fullFilePath);
		}
	}

	/**
	 * 处理标题标签
	 * 此方法作废
	 * @param buffer
	 * @param elem
	 * @return
	 */
	private static StringBuffer processContentTitle(StringBuffer content,
			Element elem) {

		String elemName = elem.getName();

		String id = elem.attributeValue("id");

		/* 卷标题 */
		if (StringUtils.equals("titleofvolume", elemName)) {
			if (!StringUtils.isEmpty(id) && !StringUtils.equals("null", id)) {
				content.append("<h2 id=\"" + elem.attributeValue("id") + "\">"
						+ elem.getText() + "</h2>\n");
			} else {
				content.append("<h2>" + elem.getText() + "</h2>\n");
			}
			return content;
		}
		/* 章标题 /版权 */
		if (StringUtils.equals("titleofchapter", elemName)
				|| StringUtils.equals("titleofcopyright", elemName)) {

			if (!StringUtils.isEmpty(id) && !StringUtils.equals("null", id)) {
				content.append("<h3 id=\"" + elem.attributeValue("id") + "\">"
						+ elem.getText() + "</h3>\n");
			} else {
				content.append("<h3>" + elem.getText() + "</h3>\n");
			}
			return content;
		}
		/* 节标题 */
		if (StringUtils.equals("titleofsection", elemName)) {

			if (!StringUtils.isEmpty(id) && !StringUtils.equals("null", id)) {
				content.append("<h4 id=\"" + elem.attributeValue("id") + "\">"
						+ elem.getText() + "</h4>\n");
			} else {
				content.append("<h4>" + elem.getText() + "</h4>\n");
			}
			return content;
		}
		/* 子节标题 */
		if (StringUtils.equals("titleofsubsection", elemName)) {
			if (!StringUtils.isEmpty(id) && !StringUtils.equals("null", id)) {
				content.append("<h5 id=\"" + elem.attributeValue("id") + "\">"
						+ elem.getText() + "</h5>\n");
			} else {
				content.append("<h5>" + elem.getText() + "</h5>\n");
			}
			return content;
		}

		/* 文前文标题/文后文标题 */
		if (StringUtils.equals("titleofpreface", elemName)
				|| StringUtils.equals("titleofpostscript", elemName)) {
			if (!StringUtils.isEmpty(id) && !StringUtils.equals("null", id)) {
				content.append("<h3 id=\"" + elem.attributeValue("id") + "\">"
						+ elem.getText() + "</h3>\n");
			} else {
				content.append("<h3>" + elem.getText() + "</h3>\n");
			}
			return content;
		}

		return content;
	}
	/**
	 * 转换resource
	 * 此方法作废
	 * @param content
	 * @param elem
	 * @param cFile
	 * @param htmlFilePath
	 * @return
	 */
	private static StringBuffer replaceCdpXMLTag(StringBuffer content,
			Element elem,File cFile,String htmlFilePath) {
		// 元素的名称
		String elemName = elem.getName();
		String currentImgPath = "";
		String [] imgsPath;
		// 封面
		if ("resource".equals(elemName)) {
			elemName = "img";
			/* 元素属性的列表 */
			List attributes = elem.attributes();
			/* 如果有属性，则增加属性 */
			if (attributes != null && attributes.size() > 0) {
				String attr = "";
				String wd = "";
				String hg = "";
				String aname = "";
				String[] size = {};
				String srcImg = "";
				for (int j = 0; j < attributes.size(); j++) {
					Attribute attribute = (Attribute) attributes.get(j);
					aname = attribute.getName();
					if ("size".equals(aname)) {
						size = attribute.getValue().split(",");
						if(size.length >= 2){
							wd = size[0];
							hg = size[1];
							attr = attr + " width =\"" + wd + "\" height =\"" + hg + "\"";
						}
					} else if ("src".equals(aname)) {
						srcImg = attribute.getValue();
						if(null != srcImg){
							imgsPath = srcImg.split("/");
							currentImgPath = cFile.getParentFile().getPath();
							if(null != imgsPath){
//								String temp = "";
//								for(int i = 0;i < imgsPath.length;i ++){
//									if(i == 0){
//										temp = imgsPath[i];
//									}else{
//										temp = temp + File.separator + imgsPath[i];
//									}
//								}
								currentImgPath = currentImgPath + File.separator + imgsPath[imgsPath.length-1];
							}
							htmlFilePath = htmlFilePath + File.separator +  imgsPath[imgsPath.length-1];
							//拷贝文件
							File jpgFile = new File(currentImgPath);
							File mjpgFile = new File(htmlFilePath);
							try {
								org.apache.commons.io.FileUtils.copyFile(jpgFile, mjpgFile);
								srcImg = imgsPath[imgsPath.length-1];
							} catch (IOException e) {
							}
						}
						
						attr = attr + " " + aname + "=\"" + srcImg + "\"";
					} else {
						attr = attr + " " + aname + "=\""
								+ attribute.getValue() + "\"";
					}
				}
				content.append("<" + elemName + " " + attr + "/>");
			} else {
				content.append("<" + elemName + "/>" + elem.getText());
			}
		}

		return content;
	}

	/**
	 * 创建HTML页面
	 * 
	 * @param fileName
	 *            文件名称
	 * @param fileContent
	 *            页面内容
	 * @param FilePath
	 *            页面存放的路径
	 * @throws Exception
	 */
	private static void createHTMLPage(String fileName, String fileContent,
			String FilePath) throws Exception {

		// 存放HTML内容
		StringBuffer htmlBuffer = new StringBuffer();

		htmlBuffer
				.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"zh-CN\">\n");
		htmlBuffer.append("<head>\n");
		htmlBuffer
				.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n");
		htmlBuffer.append("<title>" + fileName + "</title>\n");
		htmlBuffer.append("</head>\n");
		htmlBuffer.append("<body>\n");
		htmlBuffer.append(fileContent + "\n");
		htmlBuffer.append("</body>\n");
		htmlBuffer.append("</html>");

		// 写HTML文件
		txtFileWriter(FilePath, htmlBuffer.toString());
	}
	/**
	 * 迭代P标签下的子标签，替换路径
	 * @param ulRoot
	 * @return
	 */
	private static Element pressPCatalog(Element ulRoot) {
		StringBuffer catalog = new StringBuffer();

		/* 得到目录xml文档下的P根标签下的所有a标签 */
		List lis = ulRoot.elements("a");
		if (lis.size() > 0) {
			/* 循环生成结点 */
			for (int i = 0; i < lis.size(); i++) {
				Element child = (Element) lis.get(i);
				if(null != child){
					Attribute href = child.attribute("href");
					if(null != href){
						/* href节点的值 */
						String hrefValue = href.getValue();
						hrefValue = StringUtils.substringBeforeLast(href.getValue(),".");
						String hrefValueSub = StringUtils.substringAfterLast(href.getValue(), "#");
						if (StringUtils.isNotEmpty(hrefValueSub)) {
							hrefValue = hrefValue + ".html#" + hrefValueSub;
						} else {
							hrefValue = hrefValue + ".html";
						}
						/* 修改href结点的值 */
						href.setValue(hrefValue);
					}
				}
				Element ulchild = child.element("p");
				if (ulchild != null) {
					pressCatalog(ulchild);
				}
			}
		}
		return ulRoot;

	}
	/**
	 * 递归生成ul标签下的内容 把href值中的xml后缀名修改为html后缀名
	 * 
	 * @param ulRoot
	 *            xml目录文件中的根ul结点
	 */
	private static Element pressCatalog(Element ulRoot) {
		StringBuffer catalog = new StringBuffer();

		/* 得到目录xml文档下的根ul标签下的所有li标签 */
		List lis = ulRoot.elements();
		if (lis.size() > 0) {
			/* 循环生成结点 */
			for (int i = 0; i < lis.size(); i++) {
				Element li = (Element) lis.get(i);
				/* 得到a标签的href属性结点 */
				Attribute href = li.element("a").attribute("href");
				/* href结点的值 */
				String hrefValue = href.getValue();
				hrefValue = StringUtils.substringBeforeLast(href.getValue(),
						".");
				String hrefValueSub = StringUtils.substringAfterLast(
						href.getValue(), "#");
				if (StringUtils.isNotEmpty(hrefValueSub)) {
					hrefValue = hrefValue + ".html#" + hrefValueSub;
				} else {
					hrefValue = hrefValue + ".html";
				}

				/* 修改href结点的值 */
				href.setValue(hrefValue);
				/* li结点下的ul结点 */
				Element ulchild = li.element("ul");
				/* 如果li结点下有ul结点,则递归调用 */
				if (ulchild != null) {
					pressCatalog(ulchild);
				}
			}
		}
		return ulRoot;

	}

	/**
	 * 写文本文件
	 * 
	 * @param fileName文本
	 *            文件目录路径包括文件名
	 * @param document
	 *            文本 文件内容
	 * @throws IOException
	 */
	private static void txtFileWriter(String fileName, String content)
			throws IOException {
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName), "UTF-8"));
			bufferedWriter.write(content);
		} finally {
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
		}
	}

	/**
	 * 根据 版式cdp的opf文件，获取pdf内容文件名称路径
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getPdfCdpManifestHref(String filePath) {
		String rtnPath = "";
		File tempFile = new File(filePath);
		if (tempFile.exists()) {
			// 读取opf文件获取标示
			SAXReader reader = new SAXReader();
			Document doc = null;
			try {
				doc = reader.read(tempFile);
			} catch (Exception e) {
			}
			Element root = doc.getRootElement();
			Element element = root.element("manifest");
			@SuppressWarnings("unchecked")
			List<Element> items = element.elements("item");
			boolean hasConver = false;
			boolean hasBody = false;
			for (Element ele : items) {
				if (!hasBody
						&& "body".equals(ele.attribute("class").getValue())) {
					String bookPdfId = ele.attribute("href").getValue();
					if (!StringUtils.isEmpty(bookPdfId)) {
						rtnPath = tempFile.getParentFile().getPath()
								+ File.separator + bookPdfId;
					}
				}
			}
		}
		return rtnPath;
	}
	/**
	 * 根据文件路径,获取目标文件编码格式
	 * @param path
	 * @return
	 */
	public static String getFileCharsetByPath(String path){
		String codetype = "";
		File cfile = new File(path);
		if(cfile.exists()){
			return CharacterEndingInstance.characterEnding(cfile);
		}else{
			return "void";
		}
		
	}
	
	public static void main(String[] args) {
		// new Cdp2Html().createCatalogByNcx("E:\\test\\cdp\\xml\\book.ncx");
		// System.out.println(Cdp2Html.transformSpecialCharacter("o.o]o[oo\"o"));
		try {
			System.out.println(getFileCharsetByPath("D:\\workspace\\BSPP\\WebRoot\\swf\\ores\\1231\\book_temp\\xml\\article_12701_1.html"));
			File sss = new File("D:/channelSpace/bres/type21/125128/3cb86641-bd34-4e49-ae05-85160df69388/xml/");
			String [] aa = {"*"};
			Collection<File> c1 = FileUtils.listFiles(sss, aa, true);
			for (File file : c1) {
				System.out.println(file.getAbsolutePath());
			}
//			Cdp2Html.createHTMLContent(
//					"D:\\workspace\\BSRCM\\WebRoot\\swf\\ores\\356\\cdp_temp\\xml\\chapters\\backcover1.jpg","D:\\workspace\\BSRCM\\WebRoot\\swf\\ores\\356\\cdp_temp\\xml\\test");
//			Cdp2Html.createCatalogByNcx(
//					"D:\\channelSpace\\ores\\type1\\unKownDcIdentifier\\20111213173227\\xml\\book.ncx","D:\\workspace\\BSRCM\\WebRoot\\swf\\ores\\356\\cdp_temp\\xml\\test");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
