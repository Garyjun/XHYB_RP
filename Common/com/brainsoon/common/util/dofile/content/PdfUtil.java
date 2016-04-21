package com.brainsoon.common.util.dofile.content;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;

import com.brainsoon.common.util.dofile.conver.OfficeToHtmlUtils;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 
 * @ClassName: PdfUtil
 * @Description: PDF处理工具类
 * @author tanghui
 * @date 2014-4-23 下午3:18:55
 * 
 */
public class PdfUtil {

	private static final Logger logger = Logger.getLogger(PdfUtil.class);

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 * 
	 * @Title: getPdfAllPageTotalNum
	 * @Description: 获取pdf总页数
	 * @param
	 * @return int
	 * @throws
	 */
	public static int getPdfAllPageTotalNum(String pdfPath) throws IOException {
		int pages = 0;
		// 读取总的PDF文件
		PdfReader reader = null;
		try {
			/* 文件的类型 */
			String fileType = pdfPath.substring(pdfPath.lastIndexOf(".") + 1);
			if (!"pdf".equals(fileType.toLowerCase())) {
				throw new DoFileException("无法获取到文件的格式或不支持非PDF的文件格式！");
			}
			// 读取总的PDF文件
			reader = new PdfReader(pdfPath);
			pages = reader.getNumberOfPages();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		// 总文件的页数
		return pages;
	}

	/**
	 * 
	 * @Title: getTxtFromPdfThroughPageNum
	 * @Description: 抽取PDF中起止页码的txt文档内容
	 * @param pdfPath
	 *            pdf绝对路径
	 * @param startPageNum
	 *            起始页码,从第一页开始
	 * @param endPageNum
	 *            结束页码
	 * @return String
	 * @throws
	 */
	public static String parsePdf(String pdfPath, int startPageNum,
			int endPageNum) throws Exception {
		String txt = "";
		// 加载pdf文件
		PDDocument document = null;
		try {
			int pageTotalNum = getPdfAllPageTotalNum(pdfPath); // pdf的总页数

			if (startPageNum < 1) {
				throw new DoFileException("开始页码{" + startPageNum
						+ "}必须大于等于{1}.");
			}

			if (endPageNum < 1) {
				throw new DoFileException("结束页码{" + endPageNum + "}必须大于等于{1}.");
			}

			if (endPageNum < startPageNum) {
				throw new DoFileException("结束页码{" + endPageNum + "}必须大于等于开始页码{"
						+ startPageNum + "}.");
			}

			if (pageTotalNum < endPageNum) {
				throw new DoFileException("结束页码{" + endPageNum
						+ "}不能大于pdf的总页码数{" + pageTotalNum + "}.");
				// throw new Exception("结束页码{" + pageTotalNum + "}不能大于pdf的总页码数{"
				// + pageTotalNum + "}.");
			}

			// 加载pdf文件
			document = PDDocument.load(pdfPath);

			// 提取文本对象
			PDFTextStripper stripper = new PDFTextStripper("UTF-8");
			stripper.setStartPage(startPageNum);
			stripper.setEndPage(endPageNum);
			stripper.setSortByPosition(false);
			txt = stripper.getText(document);
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (document != null) {
				document.close();
			}
		}
		return txt;
	}
	
	
	/**
	 * 
	 * @Title: parsePdfEachPage2TxtFile
	 * @Description: 抽取PDF中每页的txt文档并写到txt文件中，并按页码命名，如：第一页：1.txt
	 * @param pdfPath
	 *            pdf绝对路径
	 * @param txtPath
	 *            txt文件存放路径
	 * @return String
	 * @throws
	 */
	public static void parsePdfEachPage2TxtFile(String pdfPath,String txtPath) throws Exception {
		// 加载pdf文件
		PDDocument document = null;
		try {
			int pageTotalNum = getPdfAllPageTotalNum(pdfPath); // pdf的总页数

			// 加载pdf文件
			document = PDDocument.load(pdfPath);

			// 提取文本对象
			PDFTextStripper stripper = new PDFTextStripper("UTF-8");
			
			//创建目标路径
			DoFileUtils.mkdir(txtPath);
			
			for (int i = 1; i <= pageTotalNum; i++) {
				stripper.setStartPage(i);
				stripper.setEndPage(i);
				stripper.setSortByPosition(false);
				String txt = stripper.getText(document);
				if(StringUtils.isNotBlank(txt)){
					String txtPathStr = txtPath+"/" + i + ".txt";
					if(new File(txtPathStr).exists()){
						try {
							new File(txtPathStr).delete();
						} catch (Exception e) {
						}
					}
					DoFileUtils.createTxt(txtPathStr, txt);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (document != null) {
				document.close();
			}
		}
	}

	/**
	 * @throws Exception
	 * 
	 * @Title: getTxtFromPdfThroughPageNum
	 * @Description: 抽取PDF中某页的txt文档内容
	 * @param pdfPath
	 *            pdf绝对路径
	 * @param pageNum
	 *            pdf页码,从第一页开始
	 * @return String
	 * @throws
	 */
	public static String parsePdf(String pdfPath, int pageNum) throws Exception {
		return parsePdf(pdfPath, pageNum, pageNum);
	}

	/**
	 * 
	 * @Title: addPdfImgMark
	 * @Description: 给pdf文件添加图片水印
	 * 
	 * @param pdfPath
	 *            要加水印的原pdf文件路径
	 * @param outPdfFile
	 *            加了水印后要输出的路径
	 * @param markImagePath
	 *            水印图片绝对路径
	 * @return void
	 * @throws
	 */
	public static void addPdfImgMark(String pdfPath, String outPdfFile,
			String markImagePath) throws Exception {
		PdfStamper stamp = null;
		Document document = null;
		PdfReader reader = null;
		try {
			int pageTotalNum = getPdfAllPageTotalNum(pdfPath); // pdf的总页数
			reader = new PdfReader(pdfPath, "PDF".getBytes());
			document = new Document(reader.getPageSize(1));
			float rightNum = document.right();
			float topNum = document.top();
			stamp = new PdfStamper(reader, new FileOutputStream(outPdfFile));
			Image img = Image.getInstance(markImagePath);// 插入水印
			float width = img.getWidth();
			float height = img.getHeight();
			width = rightNum / 2 - width / 2;
			height = topNum / 2 - height / 2;
			img.setAbsolutePosition(width, height);
			// img.scaleAbsolute(30,100);
			img.setRotation(45);
			for (int i = 1; i <= pageTotalNum; i++) { // 循环每页都加，只加某页的话，可以直接去掉循环
				PdfContentByte under = stamp.getUnderContent(i);
				under.addImage(img);
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stamp != null) {
				stamp.close();// 关闭
			}
			if (document != null) {
				document.close();// 关闭
			}
			if (reader != null) {
				reader.close();// 关闭
			}
		}
	}

	/**
	 * 
	 * @Title: addPdfTxtMark
	 * @Description: 给pdf文件添加文字水印
	 * 
	 * @param pdfPath
	 *            要加水印的原pdf文件路径
	 * @param outPdfFile
	 *            加了水印后要输出的路径
	 * @param markTxt
	 *            水印文字
	 * @return void
	 * @throws
	 */
	public static void addPdfTxtMark(String pdfPath, String outPdfFile,
			String markTxt) throws Exception {
		PdfStamper stamp = null;
		Document document = null;
		PdfReader reader = null;
		try {
			int pageTotalNum = getPdfAllPageTotalNum(pdfPath); // pdf的总页数
			reader = new PdfReader(pdfPath, "PDF".getBytes());
			document = new Document(reader.getPageSize(1));
			float rightNum = document.right();
			float topNum = document.top();
			stamp = new PdfStamper(reader, new FileOutputStream(outPdfFile));
			float width = DoFileUtils.getLength(markTxt);
			float height = 30; // 文字高度
			float markWidth = rightNum / 2 - width / 2 - 10;
			float markHeight = topNum / 2 - height / 2;
			for (int i = 1; i <= pageTotalNum; i++) { // 循环每页都加，只加某页的话，可以直接去掉循环
				PdfContentByte over = stamp.getOverContent(i);
				over.beginText();
				BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
						BaseFont.WINANSI, BaseFont.EMBEDDED);
				over.setFontAndSize(bf, 18);
				over.setTextMatrix(width, height);
				over.showTextAligned(Element.ALIGN_LEFT, markTxt, markWidth,
						markHeight, 45);
				over.endText();
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stamp != null) {
				stamp.close();// 关闭
			}
			if (document != null) {
				document.close();// 关闭
			}
			if (reader != null) {
				reader.close();// 关闭
			}
		}
	}
	
	/**
	 * @throws IOException
	 * 
	 * @Title: pdfToImage
	 * @Description:PDF将某页转换为图片
	 * @param pdfPath
	 *            要转换成图片的pdf文件路径
	 * @param outImageFile
	 *            转换后的图片路径
	 * @param pageNum
	 *            要转换的pdf页码
	 * @return void
	 * @throws
	 */
	public static void pdfToImage(String pdfPath, String outImageFile,
			int pageNum){
		FileOutputStream document = null;
		java.nio.ByteBuffer  buf = null;
		try {
			File file = new File(pdfPath);  
	        RandomAccessFile raf = new RandomAccessFile(file, "r");  
	        FileChannel channel = raf.getChannel();  
	        buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());  
	        com.sun.pdfview.PDFFile pdffile = new com.sun.pdfview.PDFFile(buf);  
	        int pageTotalNum = pdffile.getNumPages(); // pdf的总页数
	        System.out.println("页数： " + pdffile.getNumPages());  
	        
			if (pageNum < 1) {
				throw new DoFileException("所选页码{" + pageNum + "}必须大于等于{1}.");
			}
	
			if (pageTotalNum < pageNum) {
				throw new DoFileException("所选页码{" + pageNum + "}不能大于pdf的总页码数{"
						+ pageTotalNum + "}.");
			}
	      
			
	       // for (int i = 1; i <= pdffile.getNumPages(); i++) {  
	            // draw the first page to an image  
			com.sun.pdfview.PDFPage page = pdffile.getPage(pageNum);  
	  
            // get the width and height for the doc at the default zoom  
            Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(), (int) page.getBBox().getHeight());  
            // generate the image 
            int w = Integer.parseInt(rect.width+"");
            int h = Integer.parseInt(rect.height+"");
            java.awt.Image img = page.getImage(w, h, // width &  
                    rect, // clip rect  
                    null, // null for the ImageObserver  
                    true, // fill background with white  
                    true // block until drawing is done  
                    );  
  
            BufferedImage tag = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);  
            tag.getGraphics().setFont(new Font("宋体", Font.BOLD, 12));
            tag.getGraphics().drawImage(img, 0, 0, w, h, null);  
            document = new FileOutputStream(outImageFile); // 输出到文件流  
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(document);  
            encoder.encode(tag); // JPEG编码  
//        }  
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (buf != null) {
				unmap(buf);//如果要在转图片之后删除pdf，就必须要这个关闭流和清空缓冲的方法
			}
		}
	}
	
	

	/**
	 * @throws IOException
	 * 
	 * @Title: pdfToImageW
	 * @Description:PDF将某页转换为图片
	 * @param pdfPath
	 *            要转换成图片的pdf文件路径
	 * @param outImageFile
	 *            转换后的图片路径
	 * @param pageNum
	 *            要转换的pdf页码
	 * @return void
	 * @throws
	 */
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	public static void pdfToImage1(String pdfPath, String outImageFile,
			int pageNum) throws IOException {
		PDDocument document = null;
		ImageOutputStream outImage = null;
		FileOutputStream out = null;
		ImageWriter writer = null;
		try {
			document = PDDocument.load(new File(pdfPath));
			int pageTotalNum = document.getPageCount(); 
			System.out.println(pageTotalNum);
			
			List<PDPage> pages = document.getDocumentCatalog().getAllPages();

			if (pageNum < 1) {
				throw new DoFileException("所选页码{" + pageNum + "}必须大于等于{1}.");
			}

			if (pageTotalNum < pageNum) {
				throw new DoFileException("所选页码{" + pageNum + "}不能大于pdf的总页码数{"
						+ pageTotalNum + "}.");
			}
			// 等比例suofang
			PDPage page = (PDPage) pages.get(pageNum - 1);
			BufferedImage image = page.convertToImage(BufferedImage.TYPE_INT_RGB, 144);
			Iterator iter = ImageIO.getImageWritersBySuffix("jpg");   
	        writer = (ImageWriter)iter.next();   
	        File outFile = new File(outImageFile);   
	        out = new FileOutputStream(outFile);   
	        outImage = ImageIO.createImageOutputStream(out);   
	        writer.setOutput(outImage);   
	        writer.write(new IIOImage(image,null,null));
		 }catch (Exception e) {
			 e.printStackTrace();
			logger.error("图转失败：" + e.getMessage());
		} finally {
			if (document != null) {
				document.close();
			}
			if(out != null){
				out.close();
			}
			if(outImage != null){
				 outImage.close();
			}
			if(writer != null){
				writer.dispose();
			}
		}
	}
	
	
	
	/**
	 * 
	 * @Title: unmap 
	 * @Description: 清空缓存
	 * @param   
	 * @return void 
	 * @throws
	 */
	@SuppressWarnings({"unchecked", "rawtypes" })
	private static void unmap(final Object buffer) {  
        // TODO Auto-generated method stub  
		java.security.AccessController.doPrivileged(new PrivilegedAction() {  
        public Object run() {  
	        try {  
		        Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);  
		        getCleanerMethod.setAccessible(true);  
		        sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(buffer, new Object[0]);  
		        cleaner.clean();  
	        } catch (Exception e) {  
	        	e.printStackTrace();  
	        }  
	        	return null;  
	        }  
        });  
    }  

	/**
	 * 改变图片的大小
	 * 
	 * @param source
	 *            源文件
	 * @param targetW
	 *            目标长
	 * @param targetH
	 *            目标宽
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage resize(BufferedImage source, int targetW,
			int targetH) throws IOException {
		BufferedImage target = null;
		try {
			int type = source.getType();
			double sx = (double) targetW / source.getWidth();
			double sy = (double) targetH / source.getHeight();
			// 这里想实现在targetW，targetH范围内实现等比缩放。如果不需要等比缩放
			// 则将下面的if else语句注释即可
			if (sx > sy) {
				sx = sy;
				targetW = (int) (sx * source.getWidth());
			} else {
				sy = sx;
				targetH = (int) (sy * source.getHeight());
			}
			if (type == BufferedImage.TYPE_CUSTOM) { // handmade
				ColorModel cm = source.getColorModel();
				WritableRaster raster = cm.createCompatibleWritableRaster(targetW,targetH);
				boolean alphaPremultiplied = cm.isAlphaPremultiplied();
				target = new BufferedImage(cm, raster, alphaPremultiplied, null);
			} else {
				// 固定宽高，宽高一定要比原图片大
				target = new BufferedImage(targetW, targetH, type);
			}
			Font font = new Font("宋体", Font.BOLD, 20);
			Graphics2D g = target.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
			g.setFont(font);
			g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
			g.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return target;
	}

	/**
	 * pdf是否可以提取出文本
	 * 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static boolean isCanPdfToTXT(String source) throws Exception {

		boolean flag = true;

		PDDocument document = null;
		String temp = "";
		try {
			temp = new File(source).getParent() + File.separator + "temp";
			int count = 0;
			int pageCnt = splitPdf50(source);

			PDFTextStripper stripper = new PDFTextStripper("UTF-8");

			stripper.setSortByPosition(false);

			for (int i = 0; i < pageCnt && (i < 50); i++) {
				document = PDDocument.load(temp + File.separator
						+ getName(i + 1) + ".pdf");
				// logger.info("页"+i+"文本："
				// +stripper.getText(document).length());
				int txtLen = stripper.getText(document).length();
				// 单页小于10个字符，进行统计
				if (txtLen < 10)
					count++;
			}

			if (pageCnt < 50) {
				// 如果实际页数就于50，则如果实际页数的一半都小于10个字符，由返回false;
				int midlPageCnt = pageCnt / 2;
				if (count > midlPageCnt) {
					flag = false;
				}
			} else {
				// 总页数为50，统计结果中有25页都小于10个字符，则返回flase
				if (count > 25)
					flag = false;
			}
		} finally {
			try {
				if (document != null) {
					document.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
			try {
				FileUtils.deleteDirectory(new File(temp));
				new File(temp).delete();
			} catch (IOException ioe) {
				logger.error("删除临时目录失败：" + temp);
			}
		}
		return flag;
	}

	/**
	 * 按页拆分PDF的前50页
	 * 
	 * @return 返回总
	 * @throws Exception
	 */
	public static int splitPdf50(String source) throws Exception {
		int pageCnt = 0;
		PdfReader reader = null;
		try {
			String pdfPath = new File(source).getParent() + File.separator + "temp/";
			DoFileUtils.mkdir(pdfPath);
			/* 读取总的PDF文件 */
			reader = new PdfReader(source);
			pageCnt = reader.getNumberOfPages();
			for (int i = 0; i < pageCnt && (i < 50); i++) {
				com.itextpdf.text.Document document = new com.itextpdf.text.Document(
						reader.getPageSizeWithRotation(i + 1));
				PdfCopy copy = new PdfCopy(document, new FileOutputStream(
						pdfPath + File.separator + getName(i + 1) + ".pdf"));
				copy.setCompressionLevel(9);
				copy.setFullCompression();
				document.open();
				PdfImportedPage page = copy.getImportedPage(reader, i + 1);
				copy.addPage(page);
				document.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			reader.close();
		}
		return pageCnt;
	}

	/**
	 * 提取文件中的书签信息
	 * 
	 * @param source
	 *            pdf原文件路径
	 * @return
	 */
	public static String loadPdfOutLine(String source) throws Exception {
		PdfReader reader = null;
		// 创建xml 文件夹
		String xmlPath = "";
		try {
			// 创建xml 文件夹
			xmlPath = new File(source).getParent() + File.separator
					+ java.util.UUID.randomUUID().toString() + File.separator;
			DoFileUtils.mkdir(xmlPath);
			// 原文件的路径信息
			String sourcePath = new File(source).getParent();
	
			String fileNameSource = new File(source).getName();
	
			// 原文件名称 没有格式名称
			String fileName = fileNameSource.substring(0,
					fileNameSource.lastIndexOf("."));
	
			// 文件的类型
			String fileType = fileNameSource.substring(fileNameSource
					.lastIndexOf(".") + 1);
			logger.info("文件类型：" + fileType);
	
			String sourcePDF = sourcePath + File.separator + "xml" + File.separator
					+ fileName + ".pdf";
	
			String xmlFilePath = xmlPath + fileName + ".xml";
	
			if ("doc".equals(fileType) || "docx".equals(fileType)
					|| "pdf".equals(fileType)) {
				// logger.info("支持doc、docx、pdf的文件格式！");
			} else {
				logger.info("不支持非doc、docx、pdf的文件格式！");
				return "";
			}
	
			if ("doc".equals(fileType) || "docx".equals(fileType)) {
				OfficeToHtmlUtils.docToPdfOrHtml(source, sourcePDF);
				source = sourcePDF;
			}
			reader = new PdfReader(source);
			List listBookmark = SimpleBookmark.getBookmark(reader);
			if (listBookmark != null && listBookmark.size() > 0) {
				FileOutputStream fileOutputStream = new FileOutputStream(
						xmlFilePath);
				SimpleBookmark.exportToXML(listBookmark, fileOutputStream,
						"UTF-8", false);
				listBookmark.clear();
				// 关闭文件流
				fileOutputStream.close();
				// 读取文件信息
				SAXReader saxReader = new SAXReader();

				org.dom4j.Document doc = saxReader.read(new File(xmlFilePath));

				boolean isCanStart = isPdfOutLineHasStart(doc.asXML());

				org.dom4j.Element root = doc.getRootElement(); // 得到跟元素。

				List titles = root.elements();

				// 得到所有开始页
				List<Integer> allStart = new ArrayList<Integer>();
				if (isCanStart) {
					allStart = getPdfStart(allStart, root);
				}

				// 递归处理问题
				pressCatalog(root, titles, isCanStart, source, allStart, 1);

				return doc.asXML();
			}

			/*
			 * for ( Iterator i = list.iterator () ; i.hasNext () ; ) {
			 * showBookmark (( Map ) i.next ()) ; }
			 */
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放资源
			if (reader != null) {
				reader.close();
			}
			try {
				FileUtils.deleteDirectory(new File(xmlPath));
				new File(xmlPath).delete();
			} catch (IOException ioe) {
				ioe.printStackTrace();
				logger.error("删除临时目录失败：" + xmlPath);
			}
		}
		return null;
	}

	/**
	 * 递归增加id
	 * 
	 * @param parent
	 * @param titles
	 * @param counts
	 * @return
	 */
	public static int pressCatalog(org.dom4j.Element parent, List titles,
			boolean isCanStart, String source, List<Integer> allStart,
			int counts) throws Exception {
		try {
			if (titles.size() > 0) {
				/* 循环结点 */
				for (int i = 0; i < titles.size(); i++) {

					org.dom4j.Element title = (org.dom4j.Element) titles.get(i);

					title.addAttribute("id", String.valueOf(counts));

					counts = counts + 1;

					if (isCanStart) {
						// title结点的page属性
						Attribute pageAttr = title.attribute("Page");
						if(pageAttr != null){
							String pageAttrTxt = pageAttr.getText();
							// 开始页数
							int start = 0;
							// 结束页数
							int end = 0;
							// 获得开始页
							if (pageAttrTxt.indexOf("FitH") > 0) {
								start = Integer.parseInt(pageAttrTxt.substring(0,
										pageAttrTxt.indexOf("FitH")).trim());
							}
							if (pageAttrTxt.indexOf("Fit") > 0) {
								start = Integer.parseInt(pageAttrTxt.substring(0,
										pageAttrTxt.indexOf("Fit")).trim());
							}
							if (pageAttrTxt.indexOf("XYZ") > 0) {
								start = Integer.parseInt(pageAttrTxt.substring(0,
										pageAttrTxt.indexOf("XYZ")).trim());
							}
							// 获得结束页
							for (int j = 0; j < allStart.size(); j++) {
								Integer startT = allStart.get(j);
								if (startT == start) {
									if (j + 1 < allStart.size()) {
										end = allStart.get(j + 1);
										break;
									} else {
										end = getPdfAllPageTotalNum(source) + 1;
									}
								}
							}

							title.addAttribute("start", String.valueOf(start));
							title.addAttribute("end", String.valueOf(end));
							
							
							List pageChildTitles = title.elements();
							if (pageChildTitles != null && pageChildTitles.size() > 0) {
								counts = pressCatalog(title, pageChildTitles, isCanStart,
										source, allStart, counts);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return counts;
	}

	/**
	 * 返回pdf导航的所有开始页
	 * 
	 * @param all
	 * @param root
	 * @return
	 */
	public static List<Integer> getPdfStart(List<Integer> all,
			org.dom4j.Element root) {
		List titles = root.elements();
		try {
			for (int i = 0; i < titles.size(); i++) {
				// title结点
				org.dom4j.Element titleEle = (org.dom4j.Element) titles.get(i);
				// title结点文本名称
//				String titleTxt = titleEle.getText().trim();
				// title结点的page属性
				Attribute pageAttr = titleEle.attribute("Page");
				if(pageAttr != null){
					String pageAttrTxt = pageAttr.getText();
//					if(pageAttrTxt.indexOf("532") != -1){
//						System.out.println(pageAttrTxt);
//					}
					// 开始页数
					int start = 0;
					// 结束页数
					int end = 0;
					// 获得开始页
					if (pageAttrTxt.indexOf("FitH") > 0) {
						start = Integer.parseInt(pageAttrTxt.substring(0,
								pageAttrTxt.indexOf("FitH")).trim());
					}
					if (pageAttrTxt.indexOf("Fit") > 0) {
						start = Integer.parseInt(pageAttrTxt.substring(0,
								pageAttrTxt.indexOf("Fit")).trim());
					}
					if (pageAttrTxt.indexOf("XYZ") > 0) {
						start = Integer.parseInt(pageAttrTxt.substring(0,
								pageAttrTxt.indexOf("XYZ")).trim());
					}
					// 设置开始页
					all.add(start);

					List childrens = titleEle.elements();
					if (childrens != null && childrens.size() > 0) {
						getPdfStart(all, titleEle);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return all;
	}

	/**
	 * 如果导航中提取不取开始页，则返回false
	 * 
	 * @param xml
	 * @return
	 */
	public static boolean isPdfOutLineHasStart(String xml) throws Exception {
		org.dom4j.Document doc = DocumentHelper.parseText(xml);
		// 得到跟元素。
		org.dom4j.Element root = doc.getRootElement();

		List titles = root.elements();

		for (int i = 0; i < titles.size(); i++) {
			org.dom4j.Element titleEle = (org.dom4j.Element) titles.get(i);
			// title结点的page属性
			Attribute pageAttr = titleEle.attribute("Page");
			if(pageAttr != null){
				String pageAttrTxt = pageAttr.getText();
				if (pageAttrTxt.indexOf("FitH") > 0
						|| pageAttrTxt.indexOf("Fit") > 0
						|| pageAttrTxt.indexOf("XYZ") > 0) {
					return true;

				}
			}
		}

		return false;
	}

	/**
	 * 返回pdf导航中的Page类型
	 * 
	 * @param pdfPath
	 * @return
	 */
	public static int getDestinationType(String pdfPath) throws Exception {
		String bookmark = loadPdfOutLine(pdfPath);
		org.dom4j.Document doc = DocumentHelper.parseText(bookmark);
		// 得到跟元素。
		org.dom4j.Element root = doc.getRootElement();
		List titles = root.elements();
		for (int i = 0; i < titles.size(); i++) {
			org.dom4j.Element titleEle = (org.dom4j.Element) titles.get(i);
			// title结点的page属性
			Attribute pageAttr = titleEle.attribute("Page");
			if(pageAttr != null){
				String pageAttrTxt = pageAttr.getText();
	
				if (pageAttrTxt.indexOf("FitH") > 0) {
					return PdfDestination.FITH;
				} else if (pageAttrTxt.indexOf("Fit") > 0) {
					return PdfDestination.FIT;
				} else if (pageAttrTxt.indexOf("XYZ") > 0) {
					return PdfDestination.XYZ;
				}
			}
		}
		return PdfDestination.FITH;
	}

	/**
	 * 分割后文件命名 规则：
	 */
	public static String getName(int n) {
		String name = String.valueOf(n);
		int len = 5 - name.length();
		for (int i = 0; i < len; i++) {
			name = "0" + name;
		}
		return name;
	}

	/**
	 * 
	 * @Title: main
	 * @Description: test
	 * @param
	 * @return void
	 * @throws
	 */
	public static void main(String[] args) throws Exception {
		String filename = "F:/Project素材/pdf/9787502039639（印刷）.pdf";
		// String outfilename = "D:/Project素材/video/22.pdf";
		// String markImagePath = "D:/Project素材/video/11.png";
		String pdfImage = "D:/小说欣赏11.png";
		// 获取文本
		//String txt = parsePdf(filename, 1);
		//System.out.println(txt);
		// 添加图片水印
		// addPdfImgMark(filename, outfilename, markImagePath);
		// 添加文字水印
		// addPdfTxtMark(filename, outfilename, "www.dajianet.com");
		// PDF将某页转换为图片
		//pdfToImage(filename, pdfImage, 1);
		parsePdfEachPage2TxtFile(filename, "F:/Project素材/pdf/");
		//splitPdf50(filename);
	}
}
