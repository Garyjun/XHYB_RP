package com.brainsoon.dfa.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.Splitter;
import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * 把整个pdf,word,ppt,txt文件分割为单个页 。 切割后单个文件的命名规则：???
 * 
 * @author ruichao wen
 */
public class SplitUtil {

	private static final Logger logger = Logger.getLogger(SplitUtil.class);

	public static boolean split(String urlOrFileName, String outPath) {
		return split(urlOrFileName, new TreeMap<Integer, Integer>(), outPath);
	}
	
	public static boolean split(String urlOrFileName, TreeMap<Integer, Integer> navigations, String outPath) {
		File out = new File(outPath);
		if (!out.exists()) {
			out.mkdirs();
		}
		String mimeType = new Tika().detect(urlOrFileName);
		if (mimeType.equals("text/plain")) { // txt
			try {
				splitTXT(urlOrFileName, navigations, outPath);
			} catch (IOException e) {
				logger.error("TXT文档分割失败！");
				return false;
			}
		} else if (mimeType.equals("application/pdf")) { // pdf
			try {
				splitPDF(urlOrFileName, outPath);
			} catch (COSVisitorException e) {
				logger.error("PDF文档分割失败！");
				return false;
			} catch (IOException e) {
				logger.error("PDF文档分割失败！");
				return false;
			}
		} else if (mimeType.equals("application/msword")) { // word2003
			try {
				splitOldWord(urlOrFileName, outPath);
			} catch (IOException e) {
				logger.error("Word文档分割失败！");
				return false;
			} catch (SAXException e) {
				logger.error("Word文档分割失败！");
				return false;
			} catch (TikaException e) {
				logger.error("Word文档分割失败！");
				return false;
			}
		} else if (mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) { // word2007
			try {
				splitWord(urlOrFileName, outPath);
			} catch (IOException e) {
				logger.error("Word文档分割失败！");
				return false;
			} catch (SAXException e) {
				logger.error("Word文档分割失败！");
				return false;
			} catch (TikaException e) {
				logger.error("Word文档分割失败！");
				return false;
			}
		} else if (mimeType.equals("application/vnd.ms-powerpoint")) { // ppt
			try {
				splitPPT(urlOrFileName, outPath);
			} catch (IOException e) {
				logger.error("PPT文档分割失败！");
				return false;
			}
		} // other
		return true;
	}
	
	/**
	 * PDF文档分割
	 * 
	 * @param urlOrFileName
	 * @param outPath
	 * @throws IOException
	 * @throws COSVisitorException
	 */
	private static void splitPDF(String urlOrFileName, String outPath) throws IOException, COSVisitorException {
		Splitter splitter = new Splitter();
		splitter.setSplitAtPage(1); // 一个文档按几页分
		PDDocument document = null;
		try {
			document = PDDocument.load(urlOrFileName);
			List<PDDocument> documents = splitter.split(document);
			for (int i = 0; i < documents.size(); i++) {
				PDDocument doc = documents.get(i);
				doc.save(outPath + File.separator + getName(i + 1) + ".pdf");
				doc.close();
			}
		} finally {
			try {
				if (document != null) {
					document.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * Txt文档分割
	 * 
	 * @param urlOrFileName
	 * @param outPath
	 * @throws IOException
	 */
	private static void splitTXT(String urlOrFileName, TreeMap<Integer, Integer> navigations, String outPath) throws IOException {
		BufferedReader breader = null;
		try {
			breader = new BufferedReader(new InputStreamReader(new FileInputStream(urlOrFileName)));
			
			int k = 1;
			int lines = 1;
			for (Entry<Integer, Integer> navigation : navigations.entrySet()) {
				int beginLine = navigation.getKey();
				int endLine = navigation.getValue();
				
				BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath + File.separator + getName(k) + ".txt")));
				while (lines >= beginLine && lines <= endLine) {
					String content = breader.readLine();
					bwriter.write(content, 0, content.length());
					bwriter.newLine();
					lines++;
				}
				bwriter.close();
				k++;
			}
		} finally {
			try {
				if (breader != null) {
					breader.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * Word文档分割
	 * 
	 * @param urlOrFileName
	 * @param outPath
	 * @throws IOException
	 * @throws TikaException 
	 * @throws SAXException 
	 */
	private static void splitOldWord(String urlOrFileName, String outPath) throws IOException, SAXException, TikaException {
		InputStream istream = null;
		OutputStream ostream = null;
		try {
			istream = new BufferedInputStream(new FileInputStream(urlOrFileName));
			ostream = new BufferedOutputStream(new FileOutputStream(outPath + File.separator + getName(1) + ".doc"));
			Parser parser = new OfficeParser();
			ContentHandler handler = new BodyContentHandler(ostream);
			parser.parse(istream, handler, new Metadata(), new ParseContext());
		} finally {
			try {
				if (istream != null) {
					istream.close();
				}
				if (ostream != null) {
					ostream.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * @param urlOrFileName
	 * @param outPath
	 * @throws TikaException 
	 * @throws SAXException 
	 */
	private static void splitWord(String urlOrFileName, String outPath) throws IOException, SAXException, TikaException {
		InputStream istream = null;
		OutputStream ostream = null;
		try {
			istream = new BufferedInputStream(new FileInputStream(urlOrFileName));
			ostream = new BufferedOutputStream(new FileOutputStream(outPath + File.separator + getName(1) + ".docx"));
			XWPFDocument document = new XWPFDocument();
			XWPFTable table = document.createTable(3, 3);

			table.getRow(1).getCell(1).setText("EXAMPLE OF TABLE");

			XWPFParagraph p1 = document.createParagraph();

			XWPFRun r1 = p1.createRun();
			r1.setBold(true);
			r1.setText("The quick brown fox");
			r1.setBold(true);
			r1.setFontFamily("Courier");
			r1.setUnderline(UnderlinePatterns.DOT_DOT_DASH);
			r1.setTextPosition(100);

			table.getRow(0).getCell(0).setParagraph(p1);

			table.getRow(2).getCell(2).setText("only text");
			document.write(ostream);
//			Parser parser = new OOXMLParser();
//			ContentHandler handler = new BodyContentHandler(ostream);
//			parser.parse(istream, handler, new Metadata(), new ParseContext());
		} finally {
			try {
				if (istream != null) {
					istream.close();
				}
				if (ostream != null) {
					ostream.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * PPT文档分割
	 * 
	 * @param urlOrFileName
	 * @param outPath
	 * @throws IOException
	 */
	private static void splitPPT(String urlOrFileName, String outPath) throws IOException {
		HSLFSlideShow hslfSlideShow = new HSLFSlideShow(urlOrFileName);
		Record[] records = hslfSlideShow.getRecords();
		for (int i = 0; i < records.length; i++) {
			Record record = records[i];
			record.writeOut(new BufferedOutputStream(new FileOutputStream(outPath + File.separator + getName(i + 1) + ".ppt")));
		}
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
		return "page" + name;
	}

}
