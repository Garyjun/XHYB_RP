package com.brainsoon.common.util.dofile.code;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

public class CharacterEndingInstance {
	
	private static final Log logger = LogFactory.getLog(CharacterEndingInstance.class);
	private static CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
	
	static {
		//ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于 指示是否显示探测过程的详细信息，为false不显示。
		detector.add(new ParsingDetector(false));
		// ASCIIDetector用于ASCII编码测定
		detector.add(ASCIIDetector.getInstance());
		// UnicodeDetector用于Unicode家族编码的测定
		detector.add(UnicodeDetector.getInstance());
		detector.add(JChardetFacade.getInstance());
	}
	
	public static String characterEnding(File file) {
		
		String fileCharacterEnding = "UTF-8";
		Charset charset = null;

		try {
			charset = detector.detectCodepage(file.toURL());
		} catch (Exception e) {
			logger.error("获得文件编码异常,返回UTF-8：" ,e);
		}
		if (charset != null) {
			fileCharacterEnding = charset.name();
			if(fileCharacterEnding.equalsIgnoreCase("big5")){
				fileCharacterEnding = "GBK";
			}
		}

		return fileCharacterEnding;
	}

	public static void main(String[] args) {
		System.out.println(characterEnding(new File("D:\\xxxx.sql")));
	}
	
}
