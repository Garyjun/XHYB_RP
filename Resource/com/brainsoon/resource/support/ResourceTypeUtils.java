package com.brainsoon.resource.support;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.resource.util.RandomNumberGenerator;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;

/**
 * 动态判断取值范围
 * @author zuo
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ResourceTypeUtils {
	
	private final static Set modules = new HashSet();
	private final static Set types = new HashSet();
	private final static Set educational_phases = new HashSet();
	private final static Map<String,String> fileTypes = new HashMap<String,String>();
	static{
		modules.add("TB");
		
		types.add("T01");
		types.add("T02");
		types.add("T03");
		types.add("T04");
		types.add("T05");
		types.add("T07");
		types.add("T08");
		types.add("T09");
		types.add("T10");
		types.add("T11");
		types.add("T13");
		
		educational_phases.add("P");
		educational_phases.add("M");
		educational_phases.add("H");
		educational_phases.add("A");
		
		String txt = "F01";
		String ppt = "F02";
		String audio = "F03";
		String video = "F04";
		String animation = "F05";
		String pic = "F06";
		String program = "F07";
		String pdfbook = "F08";
		String epubbook = "F09";
		fileTypes.put(".txt", txt);
		
		fileTypes.put(".ppt", ppt);
		fileTypes.put(".pptx", ppt);
		fileTypes.put(".mp3", audio);
		
		fileTypes.put(".flv", video);
		fileTypes.put(".avi", video);
		fileTypes.put(".rmvb", video);
		fileTypes.put(".mp4", video);
		fileTypes.put(".mpg", video);
		fileTypes.put(".wmv", video);
		fileTypes.put(".asf", video);
		
		fileTypes.put(".swf", animation);
		
		fileTypes.put(".jpg", pic);
		fileTypes.put(".jpeg", pic);
		fileTypes.put(".png", pic);
		fileTypes.put(".gif", pic);
		fileTypes.put(".bmp", pic);
		fileTypes.put(".tif", pic);
		fileTypes.put(".tiff", pic);
		
		fileTypes.put(".exe", program);
		
		fileTypes.put(".pdf", pdfbook);
		fileTypes.put(".epub", epubbook);
		
		
	}
	/**
	 * 判断分类体系取值。
	 * @param module
	 * @param type
	 * @param educational_phase
	 * @return 0 版权树 1分类树
	 */
	public static int getDomainType(String module,String type,String educational_phase){
//		if(modules.contains(module) && types.contains(type) && educational_phases.contains(educational_phase)){
		if(modules.contains(module) && educational_phases.contains(educational_phase)){
			return 0;
		}
		return 1;
	}
	/**
	 * 通过文件名后缀返回文件编码
	 * //文本：F01、演示文稿：F02、音频：F03、视频：F04、动画：F05、图片：F06、程序：F07、版式电子书：F08、流式电子书：F09、其他:F10
	 * @param ext 如： ".jpg"
	 * @return String
	 */
	public static String getFileTypeCode(String ext){
		String code = fileTypes.get(ext);
		if(StringUtils.isBlank(code)){
			code = "F10";
		}
		return code;
	}
	
	/**
	 * 生成资源统一标识符，每个节之间采用点分隔
	 * @param asset
	 * @return
	 */
	public static String getDOIByAsset(Asset asset){
		String d1 = "hsjc";
		Map<String, String> commonMetaDatas = asset.getCommonMetaData().getCommonMetaDatas();
//		asset.getExtendMetaData().getExtendMetaDatas();
		Map<String, String> extendMetaData = new HashMap<String, String>();
		String d2 = commonMetaDatas.get("module");
		String d7 = commonMetaDatas.get("type");
		String d3 = commonMetaDatas.get("educational_phase");
		if(StringUtils.equalsIgnoreCase(d7, "T12")){
			d3 = "A" + extendMetaData.get("location");
			return d1 + "." + d2 + "." + d3 + "...."+d7+"..";
		}
		int domainType = getDomainType(d2, d7, d3);
		String d4 = "V00";
		String d6 = commonMetaDatas.get("subject");
		String d5 = "G00";
		if(StringUtils.equalsIgnoreCase(d2, "TB")){
			d5 = commonMetaDatas.get("grade");//年级/分册
			String fascicule = commonMetaDatas.get("fascicule");
			if(null != fascicule && StringUtils.isNotBlank(fascicule)){
				d5 += "-" + fascicule;
			}
		}
		String d8 = getFileTypeCode(commonMetaDatas.get("format"));
		if(domainType == 0){
			//版权体系
			d4 = commonMetaDatas.get("version");
		}
		String d9 = "";
		if(StringUtils.equalsIgnoreCase(d7, "T06")){
			//取ISBN
			d9 = extendMetaData.get("ISBN");
		//	d4 = "V" + RandomNumberGenerator.generateNumber3(); //V+两位序号
			d4 = "V" + commonMetaDatas.get("res_version");
		}else{
			//8位序号
			d9 = RandomNumberGenerator.generateNumber();
		}
		return d1 + "." + d2 + "." + d3 + "." + d4 + "." + d5 + "." + d6 + "." + d7 + "." + d8 + "." + d9;
	}
	
	/**
	 * 出版资源 生成资源统一标识符，每个节之间采用点分隔
	 * @param asset
	 * @return
	 */
	public static String getPublishDOI(Ca ca){
		String d1 = "hsjc"+new Date().getTime();
		//Map<String, String> commonMetaDatas = ca.getCommonMetaData().getCommonMetaDatas();
		//Map<String, String> extendMetaData = ca.getExtendMetaData().getExtendMetaDatas();
		//String isbn = extendMetaData.get("ISBN");
//		if(isbn == null || "".equals(isbn)){
//			return d1+"."+RandomNumberGenerator.generateNumber();
//		}
		//String resVersion = "V" + commonMetaDatas.get("res_version");
//		if(StringUtils.equalsIgnoreCase(d7, "T06")){
//			//取ISBN
//			d9 = extendMetaData.get("ISBN");
//		//	d4 = "V" + RandomNumberGenerator.generateNumber3(); //V+两位序号
//			d4 = "V" + commonMetaDatas.get("res_version");
//		}else{
//			//8位序号
//			d9 = RandomNumberGenerator.generateNumber();
//		}
//		return d1 + "." + d2 + "." + d3 + "." + d4 + "." + d5 + "." + d6 + "." + d7 + "." + d8 + "." + d9;
		return d1;
	}
	
	/**
	 * 根据资源类型和文件后缀获取扩展元数据标识
	 * 首先以资料类型为准,匹配不到后,再按文件后缀匹配
	 * 一共八种 依次为 图片 1,音频 2,视频 3,动画 4,教学软件 5,网络课程 6,试卷习题 7,学习网站 8   （注意与文件格式区分）
	 * 2014年5月12日 加 图书资源（书目）元数据 9
	 * @param type
	 * @param ext
	 * @return String meta1....8
	 */
	public static String getExtendMetaDataFlag(String type,String ext){
		if(StringUtils.equalsIgnoreCase(type, "T06")){
			return "meta9";
		}else if(StringUtils.equalsIgnoreCase(type, "T08")){
			return "meta5";
		}else if(StringUtils.equalsIgnoreCase(type, "T10")){
			return "meta6";
		}else if(StringUtils.equalsIgnoreCase(type, "T05")){
			return "meta3";
		}else if(StringUtils.equalsIgnoreCase(type, "T04")){
			return "meta7";
		}else if(StringUtils.equalsIgnoreCase(type, "T12")){
			return "meta8";
		}else if(StringUtils.equalsIgnoreCase(type, "T14")){
			return "meta10";
		}else if(StringUtils.equalsIgnoreCase(type, "T15")){
			return "meta11";
		}
		String code = getFileTypeCode(ext);
		if(StringUtils.equalsIgnoreCase(code, "F06")){
			return "meta1";
		}else if(StringUtils.equalsIgnoreCase(code, "F03")){
			return "meta2";
		}else if(StringUtils.equalsIgnoreCase(code, "F04")){
			return "meta3";
		}else if(StringUtils.equalsIgnoreCase(code, "F05")){
			return "meta4";
		}
		//按文件匹配
		return "";
	}
	
	public static String getHtmlContent(String metaId,String educational_phaseName,String subjectName,HttpServletRequest request){
		StringBuffer sbuffer = new StringBuffer();
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		if(StringUtils.equalsIgnoreCase(metaId, "meta1")){
			sbuffer.append("<div class=\"portlet meta1\">");
			sbuffer.append("	<div class=\"portlet-title\">");
			sbuffer.append("		<div class=\"caption\">");
			sbuffer.append("			图片 <a href=\"javascript:;\" onclick=\"togglePortlet(this)\"><i");
			sbuffer.append("				class=\"fa fa-angle-up\"></i>");
			sbuffer.append("			</a>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("	<div class=\"portlet-body\">");
			sbuffer.append("		<div class=\"container-fluid\">");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-12\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-2\">图片分辨率(像素)：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-3\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['resolution']\" id=\"resolution\" class=\"form-control\" style=\"width: 90%;\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("</div>");
		}else if(StringUtils.equalsIgnoreCase(metaId, "meta2")){
			sbuffer.append("<div class=\"portlet meta2\">");
			sbuffer.append("	<div class=\"portlet-title\">");
			sbuffer.append("		<div class=\"caption\">");
			sbuffer.append("			音频 <a href=\"javascript:;\" onclick=\"togglePortlet(this)\"><i");
			sbuffer.append("				class=\"fa fa-angle-up\"></i>");
			sbuffer.append("			</a>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("	<div class=\"portlet-body\">");
			sbuffer.append("		<div class=\"container-fluid\">");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">采样频率(KHZ)：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['sampling']\" id=\"sampling\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">声道数：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['acoustic_channel']\" id=\"acoustic_channel\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">播放时间(秒)：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['duration']\" id=\"duration\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("</div>");
		}else if(StringUtils.equalsIgnoreCase(metaId, "meta3")){
			sbuffer.append("<div class=\"portlet meta3\">");
			sbuffer.append("	<div class=\"portlet-title\">");
			sbuffer.append("		<div class=\"caption\">");
			sbuffer.append("			视频 <a href=\"javascript:;\" onclick=\"togglePortlet(this)\"><i");
			sbuffer.append("				class=\"fa fa-angle-up\"></i>");
			sbuffer.append("			</a>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("	<div class=\"portlet-body\">");
			sbuffer.append("		<div class=\"container-fluid\">");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">规格(像素)：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['specification']\" id=\"specification\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">播放时间(秒)：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['duration']\" id=\"duration\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">采样频率(MHZ)：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['sampling']\" id=\"sampling\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("</div>");
		}else if(StringUtils.equalsIgnoreCase(metaId, "meta4")){
			sbuffer.append("<div class=\"portlet meta4\">");
			sbuffer.append("	<div class=\"portlet-title\">");
			sbuffer.append("		<div class=\"caption\">");
			sbuffer.append("			动画 <a href=\"javascript:;\" onclick=\"togglePortlet(this)\"><i");
			sbuffer.append("				class=\"fa fa-angle-up\"></i>");
			sbuffer.append("			</a>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("	<div class=\"portlet-body\">");
			sbuffer.append("		<div class=\"container-fluid\">");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">帧数：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['frame_count']\" id=\"frame_count\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">帧规格(像素，如640*480)：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['specification']\" id=\"specification\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">播放时间(秒)：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['duration']\" id=\"duration\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("</div>");
		}else if(StringUtils.equalsIgnoreCase(metaId, "meta5")){
			sbuffer.append("<div class=\"portlet meta5\">");
			sbuffer.append("	<div class=\"portlet-title\">");
			sbuffer.append("		<div class=\"caption\">");
			sbuffer.append("			教学软件 <a href=\"javascript:;\" onclick=\"togglePortlet(this)\"><i");
			sbuffer.append("				class=\"fa fa-angle-up\"></i>");
			sbuffer.append("			</a>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("	<div class=\"portlet-body\">");
			sbuffer.append("		<div class=\"container-fluid\">");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">运行平台：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['platform']\" id=\"platform\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">运行要求：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['requirement']\" id=\"requirement\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">软件版本：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['software_version']\" id=\"software_version\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">教学类型：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['teaching_type']\" id=\"teaching_type\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("</div>");
		}else if(StringUtils.equalsIgnoreCase(metaId, "meta6")){
			sbuffer.append("<div class=\"portlet meta6\">");
			sbuffer.append("	<div class=\"portlet-title\">");
			sbuffer.append("		<div class=\"caption\">");
			sbuffer.append("			网络课程 <a href=\"javascript:;\" onclick=\"togglePortlet(this)\"><i");
			sbuffer.append("				class=\"fa fa-angle-up\"></i>");
			sbuffer.append("			</a>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("	<div class=\"portlet-body\">");
			sbuffer.append("		<div class=\"container-fluid\">");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">音频素材数量：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['audio_count']\" id=\"audio_count\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">视频素材数量：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['video_count']\" id=\"video_count\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">动画文件数量：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['animation_count']\" id=\"animation_count\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">页面数量：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['page_count']\" id=\"page_count\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">文本素材数量：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['text_count']\" id=\"text_count\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">图片素材数量：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['figure_count']\" id=\"figure_count\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">运行环境：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['platform']\" id=\"platform\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">课程制作版本：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['edition']\" id=\"edition\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("</div>");
		}else if(StringUtils.equalsIgnoreCase(metaId, "meta7")){
			sbuffer.append("<div class=\"portlet meta7\">");
			sbuffer.append("	<div class=\"portlet-title\">");
			sbuffer.append("		<div class=\"caption\">");
			sbuffer.append("			试卷习题 <a href=\"javascript:;\" onclick=\"togglePortlet(this)\"><i");
			sbuffer.append("				class=\"fa fa-angle-up\"></i>");
			sbuffer.append("			</a>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("	<div class=\"portlet-body\">");
			sbuffer.append("		<div class=\"container-fluid\">");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-12\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-2\">评分标准：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-10\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['criterion']\" id=\"criterion\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">建议的考试时间：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['typical_testing_time']\" id=\"typical_testing_time\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">建议的考试得分：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['score']\" id=\"score\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">审订人：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['auditor']\" id=\"auditor\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">审订日期：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['audit_date']\" id=\"audit_date\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">实测难度：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['difficulty']\" id=\"difficulty\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("				<div class=\"col-md-6\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-4\">实测区分度：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-8\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['differentiate']\" id=\"differentiate\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("</div>");
		}else if(StringUtils.equalsIgnoreCase(metaId, "meta8")){
			sbuffer.append("<div class=\"portlet meta8\">");
			sbuffer.append("	<div class=\"portlet-title\">");
			sbuffer.append("		<div class=\"caption\">");
			sbuffer.append("			学习网站 <a href=\"javascript:;\" onclick=\"togglePortlet(this)\"><i");
			sbuffer.append("				class=\"fa fa-angle-up\"></i>");
			sbuffer.append("			</a>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("	<div class=\"portlet-body\">");
			sbuffer.append("		<div class=\"container-fluid\">");
			sbuffer.append("			<div class=\"row\">");
			sbuffer.append("				<div class=\"col-md-12\">");
			sbuffer.append("					<div class=\"form-group\">");
			sbuffer.append("						<label class=\"control-label col-md-2\">访问链接：</label>");
			sbuffer.append("");
			sbuffer.append("						<div class=\"col-md-10\">");
			sbuffer.append("							<input type=\"text\" name=\"extendMetaData.extendMetaDatas['location']\" id=\"location\" class=\"form-control\"/>");
			sbuffer.append("						</div>");
			sbuffer.append("					</div>  ");
			sbuffer.append("				</div>");
			sbuffer.append("			</div>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("</div>");
		}else if(StringUtils.equalsIgnoreCase(metaId, "meta9")){
			sbuffer.append("<div class=\"portlet meta9\">");
			sbuffer.append("    <div class=\"portlet-title\">");
			sbuffer.append("	<div class=\"caption\">图书资源元数据 <a href=\"javascript:;\" onclick=\"togglePortlet(this)\"><i");
			sbuffer.append("		class=\"fa fa-angle-up\"></i></a></div>");
			sbuffer.append("    </div>");
			sbuffer.append("    <div class=\"portlet-body\">");
			sbuffer.append("	<div class=\"container-fluid\">");
			sbuffer.append("	    <div class=\"row\">");
			if(userInfo.getPlatformId()==1){
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			//<i class=\"must\">*</i>
			sbuffer.append("			<label class=\"control-label col-md-4\">ISBN：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			// validate[required,custom[checkISBN]]
			sbuffer.append("			    <input type=\"text\" id=\"ISBN\" name=\"extendMetaData.extendMetaDatas['ISBN']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			}
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">书名拼音：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"alphabetTitle\" name=\"extendMetaData.extendMetaDatas['alphabetTitle']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">交替题名：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"altTitle\" name=\"extendMetaData.extendMetaDatas['altTitle']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">并列题名：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"parTitle\" name=\"extendMetaData.extendMetaDatas['parTitle']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">其他题名：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"otherTitle\" name=\"extendMetaData.extendMetaDatas['otherTitle']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">丛书名称：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"serialname\" name=\"extendMetaData.extendMetaDatas['serialname']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
	//		sbuffer.append("			<label class=\"control-label col-md-4\"><i class=\"must\">*</i>中图分类号：</label>");
			sbuffer.append("			<label class=\"control-label col-md-4\">中图分类号：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"cbclass\" name=\"extendMetaData.extendMetaDatas['cbclass']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">页数：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"pages\" name=\"extendMetaData.extendMetaDatas['pages']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">CIP核字号：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"cip\" name=\"extendMetaData.extendMetaDatas['cip']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">纸质图书价格：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"pprice\" name=\"extendMetaData.extendMetaDatas['pprice']\" class=\"form-control validate[custom[numberOrPoint]]\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">印次：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"prtcnt\" name=\"extendMetaData.extendMetaDatas['prtcnt']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">册号：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"volnum\" name=\"extendMetaData.extendMetaDatas['volnum']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">版次：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"edicnt\" name=\"extendMetaData.extendMetaDatas['edicnt']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">著作方式：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"dctype\" name=\"extendMetaData.extendMetaDatas['dctype']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">丛书作者姓名：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"clb\" name=\"extendMetaData.extendMetaDatas['clb']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">主题词：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"dcsubject\" name=\"extendMetaData.extendMetaDatas['dcsubject']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			//sbuffer.append("			<label class=\"control-label col-md-4\"><i class=\"must\">*</i>出版社：</label>");
			sbuffer.append("			<label class=\"control-label col-md-4\"><i class=\"\"></i>出版社：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"dcpublisher\" name=\"extendMetaData.extendMetaDatas['dcpublisher']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">出版时间：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"publishdate\" name=\"extendMetaData.extendMetaDatas['publishdate']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("	    </div>");
			sbuffer.append("	</div>");
			sbuffer.append("    </div>");
			sbuffer.append("</div>");
		}else if(StringUtils.equalsIgnoreCase(metaId, "meta10")){
			sbuffer.append("<div class=\"portlet meta10\">");
			sbuffer.append("    <div class=\"portlet-title\">");
			sbuffer.append("	<div class=\"caption\">试题元数据 <a href=\"javascript:;\" onclick=\"togglePortlet(this)\"><i");
			sbuffer.append("		class=\"fa fa-angle-up\"></i></a></div>");
			sbuffer.append("    </div>");
			sbuffer.append("    <div class=\"portlet-body\">");
			sbuffer.append("	<div class=\"contaer-fluid\">");
			sbuffer.append("	    <div class=\"row\">");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">评分标准：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"exam_criterion\" name=\"extendMetaData.extendMetaDatas['exam_criterion']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">难度：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"exam_difficulty\" name=\"extendMetaData.extendMetaDatas['exam_difficulty']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">区分度：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"discrimination\" name=\"extendMetaData.extendMetaDatas['discrimination']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">曝光时间：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"exposaldate\" name=\"extendMetaData.extendMetaDatas['exposaldate']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">实测难度：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"modifieddifficulty\" name=\"extendMetaData.extendMetaDatas['modifieddifficulty']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">实测区分度：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"modifieddiscrimination\" name=\"extendMetaData.extendMetaDatas['modifieddiscrimination']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
	//		sbuffer.append("			<label class=\"control-label col-md-4\"><i class=\"must\">*</i>中图分类号：</label>");
			sbuffer.append("			<label class=\"control-label col-md-4\">试题编号：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"number\" name=\"extendMetaData.extendMetaDatas['number']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">试题要求：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"exam_requirement\" name=\"extendMetaData.extendMetaDatas['exam_requirement']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">建议得分：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"exam_score\" name=\"extendMetaData.extendMetaDatas['exam_score']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">保密度：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"secrecy\" name=\"extendMetaData.extendMetaDatas['secrecy']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\"><i class=\"must\">*</i>题型：</label>");
			sbuffer.append("");
			LinkedHashMap<String, String> childMap = OperDbUtils.queryMapByName("分类定义"+educational_phaseName+subjectName); 
			
			sbuffer.append("			<div class=\"col-md-8\">");
			//
			sbuffer.append("			    <select id=\"section\" name=\"extendMetaData.extendMetaDatas['section']\" class=\"form-control validate[required]\">");
			sbuffer.append("						<option value=\"\">请选择</option>");
			if(childMap != null){
	        	for(Iterator it = childMap.entrySet().iterator();it.hasNext();){
		            Entry<Object, String> entry = (Entry<Object, String>)it.next();  
		            sbuffer.append("<option value='").append(entry.getKey()).append("'");
//		            //判断是否选中
//		            if(StringUtils.isNotBlank(selectedVal)) {
//		                if(selectedVal.equals(entry.getKey())) {
//		                    sb.append(" selected ");
//		                }
//		            } 
//		            else {
//		                if(index == 0 && headName == null) {
//		                    sb.append(" selected ");
//		                }
//		            }
		            sbuffer.append(">").append(entry.getValue()).append("</option>");
		        }
	        }
			sbuffer.append("			    </select>");
			sbuffer.append("		    </div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">施测类型：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"testtype\" name=\"extendMetaData.extendMetaDatas['testtype']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">空数：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"blankCount\" name=\"extendMetaData.extendMetaDatas['blankCount']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">小题数：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"accessoryCount\" name=\"extendMetaData.extendMetaDatas['accessoryCount']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">试题知识点编号：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"code\" name=\"extendMetaData.extendMetaDatas['code']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">认知能力：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"abilityCode\" name=\"extendMetaData.extendMetaDatas['abilityCode']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"container\">");
			sbuffer.append("			<label class=\"control-label col-md-1\"></label>");
			sbuffer.append("			<div class=\"col-md-1\">");
			sbuffer.append("				<input type=\"button\" id=\"addTitle\" value=\"添加题目\" onclick=\"addQuestion()\" class=\"btn btn-primary\" />");
			sbuffer.append("			</div>");
			sbuffer.append("			<div class=\"col-md-2\">");
			sbuffer.append("				<input type=\"button\" id=\"editTitle\" value=\"修改题目\" onclick=\"editQuestion()\" class=\"btn btn-primary red\" />");
			sbuffer.append("			</div>");
			sbuffer.append("		</div>");
								//添加题目
			sbuffer.append("		<div class=\"col-md-9\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-2\">题目内容：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <textarea name=\"questionJson\" id=\"questionJson\" readonly = \"readonly\" style=\"width: 133%;height:200px;\"></textarea>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
											//解析描述
			sbuffer.append("		<div class=\"col-md-12\" style=\"display:none;\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\" >");
			sbuffer.append("			    <textarea name=\"extendMetaData.extendMetaDatas['analysis_description']\" id=\"analysis_description\" style=\"width: 100%;height:200px;\"></textarea>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
											//答案描述
			sbuffer.append("		<div class=\"col-md-12\" style=\"display:none;\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <textarea name=\"extendMetaData.extendMetaDatas['answer_description']\" id=\"answer_description\" style=\"width: 100%;height:200px;\"></textarea>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
											//答案内容 style=\"display:none;\"
			sbuffer.append("		<div class=\"col-md-12\" style=\"display:none;\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <textarea name=\"extendMetaData.extendMetaDatas['answer_json']\" id=\"answer_json\" style=\"width: 100%;height:200px;\"></textarea>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
											//问题json串
			sbuffer.append("		<div class=\"col-md-12\" style=\"display:none;\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <textarea name=\"extendMetaData.extendMetaDatas['content_json']\" id=\"content_json\" class=\"form-control\" rows=\"5\"></textarea>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("	</div>");
			sbuffer.append("    </div>");
			sbuffer.append("</div>");
			sbuffer.append("<script type=\"text/javascript\">");
			sbuffer.append("function addQuestion(){");
			sbuffer.append("		var questionType = $(\"#section\").find(\"option:selected\").text();");
			sbuffer.append("		 $.openWindow(\"bres/testPapers.jsp?questionType=\"+questionType,'试卷',1000,650); }");
			sbuffer.append("function editQuestion(){");
			sbuffer.append("		var content_json = $('#content_json').val();");
			sbuffer.append("		var analysis_description = encodeURIComponent($('#analysis_description').val());");
			sbuffer.append("		var answer_description = encodeURIComponent($('#answer_description').val());");
			sbuffer.append("		var answer_json = encodeURIComponent($('#answer_json').val());");
			sbuffer.append("		var url = encodeURIComponent($('#answer_json').val());");
			String path = request.getContextPath();
			String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
			sbuffer.append("		$.post('"+basePath+"/bres/testPapers.action',{content_json:content_json,analysis_description:analysis_description,answer_description:answer_description,answer_json:answer_json},function(data){");
//			sbuffer.append("		url:\"bres/testPapers.action?content_json=\"+content_json+\"&analysis_description=\"+analysis_description+\"&answer_description=\"+answer_description+\"&answer_json=\"+answer_json,");
			sbuffer.append("		data = eval('('+data+')');");
			sbuffer.append("		if(data.value='1'){");
			sbuffer.append("		$.openWindow(\"bres/testPapers.jsp\",'试卷',1000,650);");
//			sbuffer.append("		type: 'post',");
//			sbuffer.append("		type: 'post',");
//			sbuffer.append("		datatype: 'text',");
//			sbuffer.append("		success: function (returnValue) {");
//			sbuffer.append("		if(returnValue!=undefined||returnValue!=''){");
			sbuffer.append("		}");
			sbuffer.append("		});");
			sbuffer.append("		}");
			//sbuffer.append("		 $.openWindow(\"bres/testPapers.action?content_json=\"+content_json+\"&analysis_description=\"+analysis_description+\"&answer_description=\"+answer_description+\"&answer_json=\"+answer_json,'试卷',1000,650); }");
			sbuffer.append("$(function(){");
			sbuffer.append("		if($('#content_json').val()!=null&&$('#content_json').val()!=''){");
			sbuffer.append("		var json = decodeURIComponent($('#content_json').val());");
			sbuffer.append("		queryTestQuestions(json);");
			sbuffer.append("		}});");
			sbuffer.append("function queryTestQuestions(json){");
			sbuffer.append("		json = eval('('+json+')');");
			sbuffer.append("		var material='材料：'+json.material;");
			sbuffer.append("		var questionText = '';");
			sbuffer.append("	for(var i=0;i<json.samllQuesions.length;i++) {");
			sbuffer.append("		var jsonObj = json.samllQuesions[i];");
			sbuffer.append("		var questions = jsonObj.questionDesc;");
			sbuffer.append("		questionText = questionText+questions;");
			sbuffer.append("		var option = jsonObj.questionOption;");
			sbuffer.append("	if(option!=''||option!=null){");
			sbuffer.append("	for(var j=0;j<option.length;j++) {");
			sbuffer.append("		var jsonOption = option[j];");
			sbuffer.append("		var index =jsonOption.index;");
			sbuffer.append("		var desc = jsonOption.desc;");
			sbuffer.append("		var desc = desc.replace('p','span');");
			sbuffer.append("		var desc = desc.replace('/p','/span');");
			sbuffer.append("		var txt =index+\":\"+desc+'<br>';");
			sbuffer.append("		questionText=questionText+txt;");
			sbuffer.append("	$('#questionJson').val(material+questionText)");
			sbuffer.append("		}");
			sbuffer.append("		}");
			sbuffer.append("	$('#questionJson').val(material+questionText)");
			sbuffer.append("		}");
			sbuffer.append("		var questionJson = UE.getEditor('questionJson',{toolbars: [['fullscreen']],readonly:true});");
//			sbuffer.append("		var analysis_description = UE.getEditor('analysis_description');");
//			sbuffer.append("        var answer_description = UE.getEditor('answer_description');");
//			sbuffer.append("        var answer_json = UE.getEditor('answer_json');");
			sbuffer.append("		}");
			sbuffer.append("</script>");
		}else if(StringUtils.equalsIgnoreCase(metaId, "meta11")){
			sbuffer.append("<div class=\"portlet meta10\">");
			sbuffer.append("    <div class=\"portlet-title\">");
			sbuffer.append("	<div class=\"caption\">试卷元数据 <a href=\"javascript:;\" onclick=\"togglePortlet(this)\"><i");
			sbuffer.append("		class=\"fa fa-angle-up\"></i></a></div>");
			sbuffer.append("    </div>");
			sbuffer.append("    <div class=\"portlet-body\">");
			sbuffer.append("	<div class=\"contaer-fluid\">");
			sbuffer.append("	    <div class=\"row\">");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">测试类型：</label>");
			sbuffer.append("");
			LinkedHashMap<String, String> childMap = OperDbUtils.queryMapByName("测试类型定义"); 
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <select id=\"paper_category\" name=\"extendMetaData.extendMetaDatas['paper_category']\" class=\"form-control\">");
			sbuffer.append("						<option value=\"\">请选择</option>");
			if(childMap != null){
	        	for(Iterator it = childMap.entrySet().iterator();it.hasNext();){
		            Entry<Object, String> entry = (Entry<Object, String>)it.next();  
		            sbuffer.append("<option value='").append(entry.getKey()).append("'");
//		            //判断是否选中
//		            if(StringUtils.isNotBlank(selectedVal)) {
//		                if(selectedVal.equals(entry.getKey())) {
//		                    sb.append(" selected ");
//		                }
//		            } 
//		            else {
//		                if(index == 0 && headName == null) {
//		                    sb.append(" selected ");
//		                }
//		            }
		            sbuffer.append(">").append(entry.getValue()).append("</option>");
		        }
	        }
			sbuffer.append("			    </select>");
			sbuffer.append("		    </div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">区域：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_area\" name=\"extendMetaData.extendMetaDatas['paper_area']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">难度：</label>");
			sbuffer.append("");
			LinkedHashMap<String, String> childMap1 = OperDbUtils.queryMapByName("试题难度定义");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <select id=\"paper_difficulty\" name=\"extendMetaData.extendMetaDatas['paper_difficulty']\" class=\"form-control\">");
			sbuffer.append("						<option value=\"\">请选择</option>");
			if(childMap1 != null){
	        	for(Iterator it = childMap1.entrySet().iterator();it.hasNext();){
		            Entry<Object, String> entry = (Entry<Object, String>)it.next();  
		            sbuffer.append("<option value='").append(entry.getKey()).append("'");
//		            //判断是否选中
//		            if(StringUtils.isNotBlank(selectedVal)) {
//		                if(selectedVal.equals(entry.getKey())) {
//		                    sb.append(" selected ");
//		                }
//		            } 
//		            else {
//		                if(index == 0 && headName == null) {
//		                    sb.append(" selected ");
//		                }
//		            }
		            sbuffer.append(">").append(entry.getValue()).append("</option>");
		        }
	        }
			sbuffer.append("			    </select>");
			sbuffer.append("		    </div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">平均区分度：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_discrimination\" name=\"extendMetaData.extendMetaDatas['paper_discrimination']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">考试要求：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_requirement\" name=\"extendMetaData.extendMetaDatas['paper_requirement']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">考试答案：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_answer\" name=\"extendMetaData.extendMetaDatas['paper_answer']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
	//		sbuffer.append("			<label class=\"control-label col-md-4\"><i class=\"must\">*</i>中图分类号：</label>");
			sbuffer.append("			<label class=\"control-label col-md-4\">评分标准：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_criterion\" name=\"extendMetaData.extendMetaDatas['paper_criterion']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">试卷分数：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_score\" name=\"extendMetaData.extendMetaDatas['paper_score']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">试卷保密度：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_secrecy\" name=\"extendMetaData.extendMetaDatas['paper_secrecy']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">信度：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_reliability\" name=\"extendMetaData.extendMetaDatas['paper_reliability']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">效度：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_validity\" name=\"extendMetaData.extendMetaDatas['paper_validity']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">实测难度：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_modifieddifficulty\" name=\"extendMetaData.extendMetaDatas['paper_modifieddifficulty']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">实测区分度：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_modifieddivision\" name=\"extendMetaData.extendMetaDatas['paper_modifieddivision']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">实测信度：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_modifiedreliability\" name=\"extendMetaData.extendMetaDatas['paper_modifiedreliability']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">实测效度：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_modifiedvalidity\" name=\"extendMetaData.extendMetaDatas['paper_modifiedvalidity']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">使用次数：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_usedtime\" name=\"extendMetaData.extendMetaDatas['paper_usedtime']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">曝光时间：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_exposaldate\" name=\"extendMetaData.extendMetaDatas['paper_exposaldate']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">开始使用年份：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_starttime\" name=\"extendMetaData.extendMetaDatas['paper_starttime']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">停止使用年份：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_endtime\" name=\"extendMetaData.extendMetaDatas['paper_endtime']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
			sbuffer.append("		<div class=\"col-md-6\">");
			sbuffer.append("		    <div class=\"form-group\">");
			sbuffer.append("			<label class=\"control-label col-md-4\">试卷质量：</label>");
			sbuffer.append("");
			sbuffer.append("			<div class=\"col-md-8\">");
			sbuffer.append("			    <input type=\"text\" id=\"paper_classify\" name=\"extendMetaData.extendMetaDatas['paper_classify']\" class=\"form-control\"/>");
			sbuffer.append("			</div>");
			sbuffer.append("		    </div>");
			sbuffer.append("		</div>");
		}
		return sbuffer.toString();
	}
}
