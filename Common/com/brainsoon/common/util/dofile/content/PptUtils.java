package com.brainsoon.common.util.dofile.content;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;

import com.brainsoon.common.util.dofile.conver.OfficeToPdfUtils;
import com.brainsoon.common.util.dofile.util.DateTools;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PinyingUtil;


/**
 * 
 * @ClassName: PptUtils 
 * @Description: ppt处理工具类 
 * @author tanghui 
 * @date 2014-4-29 下午2:45:04 
 *
 */
public class PptUtils {

	private static final Logger logger = Logger.getLogger(PptUtils.class);

	/**
	 * @throws IOException
	 * 
	 * @Title: getPdfPageTotalNum
	 * @Description: 获取ppt总页数,自动判断扩展名
	 * @param
	 * @return int
	 * @throws
	 */
	public static int getAllPageTotalNum(String pptPath) throws IOException {
		int pages = 0;
		try{
			if (StringUtils.isNotBlank(pptPath)) {
				// 扩展名
				String extensionName = DoFileUtils.getExtensionName(pptPath);
				if (StringUtils.isNotBlank(extensionName)) {
					if (extensionName.toLowerCase().equals("pptx")) {// 07版本
						pages = getPpt07(pptPath);
					} else if (extensionName.toLowerCase().equals("ppt")) {// 97版本
						pages = getPpt97(pptPath);
					} else {
						throw new DoFileException("不支持该文件类型。");
					}
				} else {
					throw new DoFileException("未获取到文件扩展名，请检查文件是否正确。");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pages;
	}

	/**
	 * @throws IOException
	 * 
	 * @Title: getPpt97
	 * @Description: 获取97ppt总页数
	 * @param
	 * @return int
	 * @throws
	 */
	public static int getPpt97(String pptPath) throws IOException {
		int pages = 0;
		FileInputStream is = null;
		try {
			is = new FileInputStream(pptPath);
			SlideShow ppt = new SlideShow(new HSLFSlideShow(is));
			if (ppt != null) {
				Slide[] slides = ppt.getSlides();
				pages = slides.length;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (is != null) {
				is.close();
			}
		}
		return pages;
	}

	/**
	 * @throws IOException
	 * 
	 * @Title: getPpt07
	 * @Description: 获取07ppt总页数
	 * @param
	 * @return int
	 * @throws
	 */
	public static int getPpt07(String pptPath) throws IOException {
		int pages = 0;
		FileInputStream is = null;
		try {
			is = new FileInputStream(pptPath);
			XMLSlideShow xmlSlideShow = new XMLSlideShow(is);
			XSLFSlide[] slides = xmlSlideShow.getSlides();
			pages = slides.length;
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (is != null) {
				is.close();
			}
		}
		return pages;
	}

	
	/**
	 * @throws OpenXML4JException 
	 * @throws XmlException 
	 * 
	 * @Title: parsePpt
	 * @Description: 读取03/07 ppt
	 * @param pptPath
	 *            ppt绝对路径
	 * @param startPageNum
	 *            起始页码,从第一页开始
	 * @param endPageNum
	 *            结束页码
	 * @return String
	 * @throws
	 */
	public static String parsePpt(String pptPath, int startPageNum,
			int endPageNum) throws IOException, XmlException, OpenXML4JException {
		String txt = "";
		try{
			if (StringUtils.isNotBlank(pptPath)) {
				// 扩展名
				String extensionName = DoFileUtils.getExtensionName(pptPath);
				if (StringUtils.isNotBlank(extensionName)) {
					if (extensionName.toLowerCase().equals("pptx")) {// 07版本
						txt = parsePpt2007(pptPath, startPageNum, endPageNum);
					} else if (extensionName.toLowerCase().equals("ppt")) {// 97版本
						txt = parsePpt97(pptPath, startPageNum, endPageNum);
					} else {
						throw new DoFileException("不支持该文件类型。");
					}
				} else {
					throw new DoFileException("未获取到文件扩展名，请检查文件是否正确。");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txt;
	}
	
	
	
	
	/**
	 * 
	 * @Title: parsePpt97
	 * @Description: 读取03/97ppt
	 * @param pptPath
	 *            ppt绝对路径
	 * @param startPageNum
	 *            起始页码,从第一页开始
	 * @param endPageNum
	 *            结束页码
	 * @return String
	 * @throws
	 */
	public static String parsePpt97(String pptPath, int startPageNum,
			int endPageNum) throws IOException {
		String txt = "";
		FileInputStream is = null;
		try {
			is = new FileInputStream(pptPath);
			SlideShow ppt = new SlideShow(new HSLFSlideShow(is));
			// Dimension pgsize = ppt.getPageSize(); //ppt的尺寸
			Slide[] slides = ppt.getSlides();
			int pageTotalNum = slides.length; // 总页数

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
						+ "}不能大于ppt的总页码数{" + pageTotalNum + "}.");
			}

			StringBuilder sb = null;
			int count = 1; // 计数器
			for (Slide slide : slides) {

				if (sb == null) {
					sb = new StringBuilder();
				}

				// 获取备注
				// Notes notesSheet = slide.getNotesSheet();
				// if (notesSheet != null) {
				// if (notesSheet.getTextRuns() != null) {
				// System.out.println("备注："
				// + notesSheet.getTextRuns()[0].getText());
				// }
				// }

				// 获取文本标题及内容
				if (startPageNum == count
						|| (startPageNum != endPageNum && endPageNum >= count && count >= startPageNum)) {
					TextRun[] truns = slide.getTextRuns();
					for (TextRun textRun : truns) {
						String content = textRun.getText();
						if (StringUtils.isNotBlank(content)) {
							sb.append(content.trim() + "\n");
						}

						// RichTextRun[] rtruns = textRun.getRichTextRuns();
						// for (RichTextRun richTextRun : rtruns) {
						// // richTextRun.setFontIndex(1);
						// // richTextRun.setFontName("宋体");
						// // 获取文本列表
						// System.out.println("==== " + richTextRun.getText());
						// }
					}

					// 如果开始==结束或者结束==计数器
					if (endPageNum == startPageNum || endPageNum == count) {
						break;
					}
				}
				count++;
			}
			if (sb != null) {
				txt = sb.toString();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (is != null) {
				is.close();
			}
		}
		return txt;
	}
	
	
	/**
	 * @throws OpenXML4JException 
	 * @throws XmlException 
	 * @Title: parsePpt
	 * @Description: 通过页码读取03/07ppt
	 * @param pptPath
	 *            ppt绝对路径
	 * @param pageNum
	 *            页码
	 * @return String
	 * @throws
	 */
	public static String parsePpt(String pptPath, int pageNum) throws IOException, XmlException, OpenXML4JException {
		String txt = "";
		try{
			if (StringUtils.isNotBlank(pptPath)) {
				// 扩展名
				String extensionName = DoFileUtils.getExtensionName(pptPath);
				if (StringUtils.isNotBlank(extensionName)) {
					if (extensionName.toLowerCase().equals("pptx")) {// 07版本
						txt = parsePpt2007(pptPath,pageNum);
					} else if (extensionName.toLowerCase().equals("ppt")) {// 97版本
						txt = parsePpt97(pptPath,pageNum);
					} else {
						throw new DoFileException("不支持该文件类型。");
					}
				} else {
					throw new DoFileException("未获取到文件扩展名，请检查文件是否正确。");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txt;
	}
	
	/**
	 * 
	 * @Title: parsePpt97
	 * @Description: 通过页码读取03/97ppt
	 * @param pptPath
	 *            ppt绝对路径
	 * @param pageNum
	 *            页码
	 * @return String
	 * @throws
	 */
	public static String parsePpt97(String pptPath, int pageNum) throws IOException {
		return parsePpt97(pptPath, pageNum, pageNum);
	}

	/**
	 * 
	 * @Title: parsePpt2007
	 * @Description: 读取pptx(07)
	 * @param pptPath
	 *            ppt绝对路径
	 * @param startPageNum
	 *            起始页码,从第一页开始
	 * @param endPageNum
	 *            结束页码
	 * @return String
	 * @throws
	 */
	public static String parsePpt2007(String pptPath, int startPageNum,
			int endPageNum) throws IOException, XmlException,
			OpenXML4JException {
		// 直接获取全部内容
		// return new
		// XSLFPowerPointExtractor(POIXMLDocument.openPackage(pptPath)).getText();
		String txt = "";
		FileInputStream is = null;
		try {
			is = new FileInputStream(pptPath);
			XMLSlideShow xmlSlideShow = new XMLSlideShow(is);
			XSLFSlide[] slides = xmlSlideShow.getSlides();

			int pageTotalNum = slides.length; // 总页数

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
						+ "}不能大于ppt的总页码数{" + pageTotalNum + "}.");
			}

			StringBuilder sb = null;
			int count = 1; // 计数器
			for (XSLFSlide slide : slides) {

				if (sb == null) {
					sb = new StringBuilder();
				}

				// 获取文本标题及内容
				if (startPageNum == count
						|| (startPageNum != endPageNum && endPageNum >= count && count >= startPageNum)) {
					CTSlide rawSlide = slide.getXmlObject();
					CTGroupShape gs = rawSlide.getCSld().getSpTree();
					CTShape[] shapes = gs.getSpArray();
					for (CTShape shape : shapes) {
						CTTextBody tb = shape.getTxBody();
						if (null == tb) {
							continue;
						}
						CTTextParagraph[] paras = tb.getPArray();
						for (CTTextParagraph textParagraph : paras) {
							CTRegularTextRun[] textRuns = textParagraph
									.getRArray();
							for (CTRegularTextRun textRun : textRuns) {
								sb.append(textRun.getT() + "\n");
							}
						}
					}
					// 如果开始==结束或者结束==计数器
					if (endPageNum == startPageNum || endPageNum == count) {
						break;
					}
				}

				count++;
			}
			if (sb != null) {
				txt = sb.toString();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (is != null) {
				is.close();
			}
		}
		return txt;
	}

	/**
	 * @throws OpenXML4JException 
	 * @throws XmlException 
	 * @throws IOException 
	 * 
	 * @Title: parsePpt2007
	 * @Description: 通过页码读取pptx(07)
	 * @param pptPath
	 *            ppt绝对路径
	 * @param pageNum
	 *            页码
	 * @return String
	 * @throws
	 */
	public static String parsePpt2007(String pptPath, int pageNum) throws IOException, XmlException, OpenXML4JException {
		return parsePpt2007(pptPath, pageNum, pageNum);
	}
	
	
	
	/**
	 * 
	 * @Title: addImgMark
	 * @Description: 给ppt文件添加图片水印
	 * 
	 * @param pptPath
	 *            要加水印的原ppt文件路径
	 * @param outpptFile
	 *            加了水印后要输出的路径
	 * @param markImagePath
	 *            水印图片绝对路径
	 * @return void
	 * @throws
	 */
	public static void addImgMark(String pptPath, String outpptFile,
			String markImagePath) throws Exception {

	}

	/**
	 * 
	 * @Title: addTxtMark
	 * @Description: 给ppt文件添加文字水印
	 * 
	 * @param docPath
	 *            要加水印的原ppt文件路径
	 * @param outpptFile
	 *            加了水印后要输出的路径
	 * @param markTxt
	 *            水印文字
	 * @return void
	 * @throws
	 */
	public static void addTxtMark(String pptPath, String outpptFile,
			String markTxt) throws Exception {

	}

	
	/**
	 * @throws Exception 
	 * 
	 * @Title: pptToImage
	 * @Description:ppt将某页转换为图片
	 * @param pptPath
	 *            要转换成图片的ppt/pptx文件路径
	 * @param outImageFile
	 *            转换后的图片路径
	 * @param pageNum
	 *            要转换的ppt/pptx页码
	 * @return void
	 * @throws
	 */
	public static void pptToImage(String pptPath, String outImageFile, int pageNum) throws Exception {
		//第一步：转换doc为pdf,将其做为临时文件放置到指定位置，处理完毕之后清除掉
		String outPdfTempFile = parsePpt2Pdf(pptPath);
		//第二步：生成缩略图
		PdfUtil.pdfToImage(outPdfTempFile, outImageFile, pageNum);
		//第三步：删除临时文件,暂时不删除，等自动清理,以备不停地抽取图片之用
		//FileUtils.forceDelete(new File(outPdfTempFile));
	}
	
	
	/**
	 * @throws Exception 
	 * 
	 * @Title: parsePpt2Pdf
	 * @Description: 转换ppt到pdf
	 * @param docPath
	 *            doc绝对路径
	 * @return String
	 * @throws
	 */
	public static String parsePpt2Pdf(String pptPath) throws Exception {
		//获取不带扩展名的文件名
	    String fileName = DoFileUtils.getFileNameNoEx(pptPath); 
		String outPdfTempFile = DoFileUtils.getFileConverTempDir() + "ppt2pdf/" + PinyingUtil.spellNoneTone(fileName) + ".pdf";
		//转换doc为pdf,将其做为临时文件放置到指定位置
		OfficeToPdfUtils.convertToPdf(pptPath,outPdfTempFile);
		return outPdfTempFile;
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
		//测试开始
		long ss = DateTools.getStartTime();
		String filename1 = "D:/马老师分享文档/公司宣传PPT/Scrum漫谈_生动入门教程.ppt";
		String filename2 = "D:/马老师分享文档/公司宣传PPT/展会宣讲ＰＰＴ：新增_new.pptx";
		// String outfilename = "D:/Project素材/video/22.doc";
		// String markImagePath = "D:/Project素材/video/11.png";
		String docImage = "D:/资源管理平台/华师京城/文档/服务支撑子系统接口调用说明文档.png";
//		System.out.println("总页数为：" + getAllPageTotalNum(filename1));
//		System.out.println("97版本txt为：\n" + parsePpt97(filename1, 1, 2));
//		System.out.println("07版本txt为：\n" + parsePpt2007(filename2, 2, 2));
		// 添加图片水印
		// addPdfImgMark(filename, outfilename, markImagePath);
		// 添加文字水印
		// addPdfTxtMark(filename, outfilename, "www.dajianet.com");
		// PDF将某页转换为图片
		pptToImage(filename1, docImage, 1);
		 //测试结束
      	DateTools.getTotaltime(ss);
	}
}
