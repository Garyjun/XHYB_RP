package com.brainsoon.resource.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;

/**
 * 资源相关常量类
 * @author huagnjun
 * @date 2015-10-26 16:54:27
 *
 */
public class publishResConstants {
	public final static String FILE_COVER = WebAppUtils.getWebAppRelFileDir()+ConstantsDef.fileCover+"/";
	public static final Map<String,String> coverPath = new LinkedHashMap<String,String>();
	
	static{
		//文件扩展名   图片地址
		//视频
		coverPath.put("accdb",FILE_COVER+"accdb.png");
		coverPath.put("avi",FILE_COVER+"avi.png");
		coverPath.put("bmp",FILE_COVER+"bmp.png");
		coverPath.put("css",FILE_COVER+"css.png");
		coverPath.put("docx",FILE_COVER+"docx_win.png");
		coverPath.put("doc",FILE_COVER+"doc.png");
		coverPath.put("eml",FILE_COVER+"eml.png");
		coverPath.put("eps",FILE_COVER+"eps.png");
		coverPath.put("epub",FILE_COVER+"epub.png");
		coverPath.put("fla",FILE_COVER+"fla.png");
		coverPath.put("flv",FILE_COVER+"flv.png");
		coverPath.put("gif",FILE_COVER+"gif.png");
		coverPath.put("html",FILE_COVER+"html.png");
		coverPath.put("ind",FILE_COVER+"ind.png");
		coverPath.put("ini",FILE_COVER+"ini.png");
		coverPath.put("jpeg",FILE_COVER+"jpeg.png");
		coverPath.put("java",FILE_COVER+"java.png");
		coverPath.put("jsf",FILE_COVER+"jsf.png");
		coverPath.put("midi",FILE_COVER+"midi.png");
		coverPath.put("mov",FILE_COVER+"mov.png");
		coverPath.put("mp3",FILE_COVER+"mp3.png");
		coverPath.put("mp4",FILE_COVER+"mp4.png");
		coverPath.put("pdf",FILE_COVER+"pdf.png");
		coverPath.put("png",FILE_COVER+"png.png");
		coverPath.put("pptx",FILE_COVER+"pptx_win.png");
		coverPath.put("ppt",FILE_COVER+"pptx_win.png");
		coverPath.put("proj",FILE_COVER+"proj.png");
		coverPath.put("psd",FILE_COVER+"psd.png");
		coverPath.put("pst",FILE_COVER+"pst.png");
		coverPath.put("pub",FILE_COVER+"pub.png");
		coverPath.put("rar",FILE_COVER+"rar.png");
		coverPath.put("readme",FILE_COVER+"readme.png");
		coverPath.put("setting",FILE_COVER+"settings.png");
		coverPath.put("text",FILE_COVER+"text.png");
		coverPath.put("txt",FILE_COVER+"txt.png");
		coverPath.put("tiff",FILE_COVER+"tiff.png");
		coverPath.put("url",FILE_COVER+"url.png");
		coverPath.put("vsd",FILE_COVER+"vsd.png");
		coverPath.put("wav",FILE_COVER+"wav.png");
		coverPath.put("wma",FILE_COVER+"wma.png");
		coverPath.put("wmv",FILE_COVER+"wmv.png");
		coverPath.put("xlsx",FILE_COVER+"xlsx_win.png");
		coverPath.put("xls",FILE_COVER+"xls.png");
		coverPath.put("zip",FILE_COVER+"zip.png");
		
	}
	/*
	videoFormat=avi,rmvb,asf,mov,rm,mp4,3gp,flv,mpg,wmv,ogv,mpeg,mpeg2,f4v,f5v,f6v,webm,mp2,mpeg4,vob,mkv
	audioFormat=mp3,wav,ape,flac,ac3,ogg,wma,ram,mid,asx,midi
	documentFormat=doc,docx,ppt,pptx,pptm,xls,xlsx,pdf,txt,xml,opf,ncx,css,js,php,java,properties,dtd,log,tld,jsp,sql,c,ini,bat,py,cgi,asp,chm,rtf,html,htm,mht,zip,rar,iso
	animaFormat=swf*/
	
}
