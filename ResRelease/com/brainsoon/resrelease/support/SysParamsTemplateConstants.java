package com.brainsoon.resrelease.support;

import java.util.Map;

import com.brainsoon.appframe.support.ConstantsMap;
import com.brainsoon.appframe.support.ConstantsRepository;

/**
 * 
 * @author tanghui
 *
 */
public class SysParamsTemplateConstants {

	// 启用状态
	public static class ParamsTempStatus {
		public static final Integer STATUS_0 = 0;
		public static final String FEMALE_0_DESC = "禁用";
		public static final Integer STATUS_1 = 1;
		public static final String STATUS_1_DESC = "启用";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(STATUS_1, STATUS_1_DESC);
			map.putConstant(STATUS_0, FEMALE_0_DESC);
			ConstantsRepository.getInstance().register(ParamsTempStatus.class,
					map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	// 资源类型
	public static class ResourceType {
		public static final String STATUS_0 = "book";
		public static final String FEMALE_0_DESC = "图书";
		public static final String STATUS_1 = "education";
		public static final String STATUS_1_DESC = "教育资源";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(STATUS_1, STATUS_1_DESC);
			map.putConstant(STATUS_0, FEMALE_0_DESC);
			ConstantsRepository.getInstance().register(ResourceType.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	// 资源类型
	public static class ImageOrVideo {
		public static final String STATUS_0 = "image";
		public static final String FEMALE_0_DESC = "图片";
		public static final String STATUS_1 = "video";
		public static final String STATUS_1_DESC = "视频";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(STATUS_1, STATUS_1_DESC);
			map.putConstant(STATUS_0, FEMALE_0_DESC);
			ConstantsRepository.getInstance().register(ImageOrVideo.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	// 是否
	public static class JudgeWhether {
		public static final String JW_1 = "01";
		public static final String JW_1_DESC = "是";
		public static final String JW_0 = "02";
		public static final String JW_0_DESC = "否";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(JW_1, JW_1_DESC);
			map.putConstant(JW_0, JW_0_DESC);
			ConstantsRepository.getInstance().register(JudgeWhether.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	// 水印文字透明度
	public static class WmAlpha {
		public static final String WMALPHA_1 = "01";
		public static final String WMALPHA_1_DESC = "清晰";
		public static final String WMALPHA_2 = "02";
		public static final String WMALPHA_2_DESC = "半透明";
		public static final String WMALPHA_3 = "03";
		public static final String WMALPHA_3_DESC = "模糊";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(WMALPHA_1, WMALPHA_1_DESC);
			map.putConstant(WMALPHA_2, WMALPHA_2_DESC);
			map.putConstant(WMALPHA_3, WMALPHA_3_DESC);
			ConstantsRepository.getInstance().register(WmAlpha.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	//文本(pdf)的水印位置
	public static class WmTxtPosition {
		public static final String WMPOSITION_1 = "01";
		public static final String WMPOSITION_1_DESC = "左上-右下";
		public static final String WMPOSITION_2 = "02";
		public static final String WMPOSITION_2_DESC = "左下-右上";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(WMPOSITION_1, WMPOSITION_1_DESC);
			map.putConstant(WMPOSITION_2, WMPOSITION_2_DESC);
			ConstantsRepository.getInstance()
					.register(WmTxtPosition.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}


	//图片、视频的水印位置
	public static class WmPosition {
		public static final String WMPOSITION_1 = "0";
		public static final String WMPOSITION_1_DESC = "左上角";
		public static final String WMPOSITION_2 = "1";
		public static final String WMPOSITION_2_DESC = "右上角";
		public static final String WMPOSITION_3 = "2";
		public static final String WMPOSITION_3_DESC = "左下角";
		public static final String WMPOSITION_4 = "3";
		public static final String WMPOSITION_4_DESC = "右下角";
		// public static final String WMPOSITION_5 = "4";
		// public static final String WMPOSITION_5_DESC = "居中";
		// public static final String WMPOSITION_6 = "5";
		// public static final String WMPOSITION_6_DESC = "平铺";

		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(WMPOSITION_1, WMPOSITION_1_DESC);
			map.putConstant(WMPOSITION_2, WMPOSITION_2_DESC);
			map.putConstant(WMPOSITION_3, WMPOSITION_3_DESC);
			map.putConstant(WMPOSITION_4, WMPOSITION_4_DESC);
			// map.putConstant(WMPOSITION_5, WMPOSITION_5_DESC);
			// map.putConstant(WMPOSITION_6, WMPOSITION_6_DESC);
			ConstantsRepository.getInstance().register(WmPosition.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	// 水印文字大小
	public static class TextSize {
		public static final String TEXTSIZE_1 = "01";
		public static final String TEXTSIZE_1_DESC = "大";
		public static final String TEXTSIZE_2 = "02";
		public static final String TEXTSIZE_2_DESC = "中";
		public static final String TEXTSIZE_3 = "03";
		public static final String TEXTSIZE_3_DESC = "小";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(TEXTSIZE_1, TEXTSIZE_1_DESC);
			map.putConstant(TEXTSIZE_2, TEXTSIZE_2_DESC);
			map.putConstant(TEXTSIZE_3, TEXTSIZE_3_DESC);
			ConstantsRepository.getInstance().register(TextSize.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	// 水印文字字体
	public static class WmFont {
		public static final String TEXTFONT_1 = "01";
		public static final String TEXTFONT_1_DESC = "宋体";
		public static final String TEXTFONT_2 = "02";
		public static final String TEXTFONT_2_DESC = "仿宋";
		public static final String TEXTFONT_3 = "03";
		public static final String TEXTFONT_3_DESC = "新宋体";
		public static final String TEXTFONT_4 = "04";
		public static final String TEXTFONT_4_DESC = "楷体";
		public static final String TEXTFONT_5 = "05";
		public static final String TEXTFONT_5_DESC = "微软雅黑";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(TEXTFONT_1, TEXTFONT_1_DESC);
			map.putConstant(TEXTFONT_2, TEXTFONT_2_DESC);
			map.putConstant(TEXTFONT_3, TEXTFONT_3_DESC);
			map.putConstant(TEXTFONT_4, TEXTFONT_4_DESC);
			map.putConstant(TEXTFONT_5, TEXTFONT_5_DESC);
			ConstantsRepository.getInstance().register(WmFont.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	// 图片格式类型定义
	public static class ImgFormat {
		public static final String IMGFORMAT_0 = "";
		public static final String IMGFORMAT_0_DESC = "默认";
		public static final String IMGFORMAT_1 = "01";
		public static final String IMGFORMAT_1_DESC = "jpg";
		public static final String IMGFORMAT_2 = "02";
		public static final String IMGFORMAT_2_DESC = "png";
		public static final String IMGFORMAT_3 = "03";
		public static final String IMGFORMAT_3_DESC = "tiff";
		public static final String IMGFORMAT_4 = "04";
		public static final String IMGFORMAT_4_DESC = "bmp";
		public static final String IMGFORMAT_5 = "05";
		public static final String IMGFORMAT_5_DESC = "gif";
		 public static final String IMGFORMAT_6 = "06";
		 public static final String IMGFORMAT_6_DESC = "psd";
		 public static final String IMGFORMAT_7 = "07";
		 public static final String IMGFORMAT_7_DESC = "pcf";
		 public static final String IMGFORMAT_8 = "08";
		 public static final String IMGFORMAT_8_DESC = "tga";
		 public static final String IMGFORMAT_9 = "09";
		 public static final String IMGFORMAT_9_DESC = "dxf";
		 public static final String IMGFORMAT_10 = "10";
		 public static final String IMGFORMAT_10_DESC = "eps";
		 public static final String IMGFORMAT_11 = "11";
		 public static final String IMGFORMAT_11_DESC = "raw";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(IMGFORMAT_0, IMGFORMAT_0_DESC);
			map.putConstant(IMGFORMAT_1, IMGFORMAT_1_DESC);
			map.putConstant(IMGFORMAT_2, IMGFORMAT_2_DESC);
			//map.putConstant(IMGFORMAT_3, IMGFORMAT_3_DESC);
			map.putConstant(IMGFORMAT_4, IMGFORMAT_4_DESC);
			//map.putConstant(IMGFORMAT_5, IMGFORMAT_5_DESC);
			// map.putConstant(IMGFORMAT_6, IMGFORMAT_6_DESC);
			// map.putConstant(IMGFORMAT_7, IMGFORMAT_7_DESC);
			// map.putConstant(IMGFORMAT_8, IMGFORMAT_8_DESC);
			// map.putConstant(IMGFORMAT_9, IMGFORMAT_9_DESC);
			// map.putConstant(IMGFORMAT_10, IMGFORMAT_10_DESC);
			// map.putConstant(IMGFORMAT_11, IMGFORMAT_11_DESC);
			ConstantsRepository.getInstance().register(ImgFormat.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	// 视频格式类型定义
	public static class VideoFormat {
		public static final String VIDEOFORMAT_0 = "";
		public static final String VIDEOFORMAT_0_DESC = "默认";
		public static final String VIDEOFORMAT_1 = "01";
		public static final String VIDEOFORMAT_1_DESC = "mp4";
		public static final String VIDEOFORMAT_2 = "02";
		public static final String VIDEOFORMAT_2_DESC = "flv";//,swf
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(VIDEOFORMAT_0, VIDEOFORMAT_0_DESC);
			map.putConstant(VIDEOFORMAT_1, VIDEOFORMAT_1_DESC);
			map.putConstant(VIDEOFORMAT_2, VIDEOFORMAT_2_DESC);
			ConstantsRepository.getInstance().register(VideoFormat.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	// 文本格式类型定义 仅支持pdf加水印
	public static class TextFormat {
		/*
		 * public static final String TEXTFORMAT_1 = "01"; public static final
		 * String TEXTFORMAT_1_DESC = "doc"; public static final String
		 * TEXTFORMAT_2 = "02"; public static final String TEXTFORMAT_2_DESC =
		 * "docx";
		 */
		public static final String TEXTFORMAT_3 = "03";
		public static final String TEXTFORMAT_3_DESC = "pdf";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			/*
			 * map.putConstant(TEXTFORMAT_1, TEXTFORMAT_1_DESC);
			 * map.putConstant(TEXTFORMAT_2, TEXTFORMAT_2_DESC);
			 */
			map.putConstant(TEXTFORMAT_3, TEXTFORMAT_3_DESC);
			ConstantsRepository.getInstance().register(TextFormat.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	public static class PublishType {
		public static final String ON_LINE = "onLine";
		public static final String ON_LINE_DESC = "线上发布";
		public static final String OFF_LINE = "offLine";
		public static final String OFF_LINE_DESC = "线下发布";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(ON_LINE, ON_LINE_DESC);
			map.putConstant(OFF_LINE, OFF_LINE_DESC);
			ConstantsRepository.getInstance().register(PublishType.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
		//用户状态
		public static class userStatus {
			public static final String STATUS0 = "-1";
			public static final String STATUS0_DESC = "全部";
			public static final String STATUS1 = "1";
			public static final String STATUS1_DESC = "启用";
			public static final String STATUS2 = "0";
			public static final String STATUS2_DESC = "禁用";
			private static ConstantsMap map;
			static {
				map = new ConstantsMap();
				map.putConstant(STATUS0, STATUS0_DESC);
				map.putConstant(STATUS1, STATUS1_DESC);
				map.putConstant(STATUS2, STATUS2_DESC);
				ConstantsRepository.getInstance().register(userStatus.class, map);
				
			}

			public static String getValueByKey(Object key) {
				if (key == null) {
					return "";
				}
				return (String) map.getDescByValue(key);
			}
			
			public static Map getMap() {
				return  map.getEntryMap();
			}
		}
}
