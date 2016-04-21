package com.brainsoon.system.support;

import java.util.LinkedHashMap;
import java.util.Map;

import com.brainsoon.appframe.support.ConstantsMap;
import com.brainsoon.appframe.support.ConstantsRepository;

public class SystemConstants {

	public static final String DEFAULT_PASS = "12345678";

	/**
	 * 日志类型
	 * 
	 * @author Administrator
	 * 
	 */
	public static class LogType {
		public static final String SYS_LOG = "1";
		public static final String BUSINESS_LOG = "2";
		public static final String EXCEPTION_LOG = "3";
		public static final String SYS_LOG_DESC = "系统日志";
		public static final String BUSINESS_LOG_DESC = "业务日志";
		public static final String EXCEPTION_LOG_DESC = "异常日志";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(SYS_LOG, SYS_LOG_DESC);
			map.putConstant(BUSINESS_LOG, BUSINESS_LOG_DESC);
			map.putConstant(EXCEPTION_LOG, EXCEPTION_LOG_DESC);
			ConstantsRepository.getInstance().register(LogType.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	/**
	 * 权限角色状态
	 */
	public static class Status {
		public static final String STATUS0 = "0";
		public static final String STATUS0_DESC = "禁用";
		public static final String STATUS1 = "1";
		public static final String STATUS1_DESC = "启用";
		public static Map<String, String> map;
		static {
			map = new LinkedHashMap<String, String>();
			map.put(STATUS1, STATUS1_DESC);
			map.put(STATUS0, STATUS0_DESC);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.get(key);
		}
	}

	/**
	 * 资源状态
	 * 
	 * @author zuo
	 */
	public static class ResourceStatus {
		public static final String STATUS0 = "0";
		public static final String STATUS0_DESC = "草稿";
		public static final String STATUS1 = "1";
		public static final String STATUS1_DESC = "待审核";
		public static final String STATUS2 = "2";
		public static final String STATUS2_DESC = "一审待审核";
		public static final String STATUS3 = "3";
		public static final String STATUS3_DESC = "已通过";
		public static final String STATUS4 = "4";
		public static final String STATUS4_DESC = "下线";
		public static final String STATUS5 = "5";
		public static final String STATUS5_DESC = "已驳回";
		public static final String STATUS6 = "6";
		public static final String STATUS6_DESC = "二审待审核";
		public static final String STATUS7 = "7";
		public static final String STATUS7_DESC = "加锁";
		public static final String STATUS8 = "8";
		public static final String STATUS8_DESC = "解锁";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(STATUS0, STATUS0_DESC);
			map.putConstant(STATUS1, STATUS1_DESC);
			map.putConstant(STATUS2, STATUS2_DESC);
			map.putConstant(STATUS6, STATUS6_DESC);
			map.putConstant(STATUS3, STATUS3_DESC);
			map.putConstant(STATUS4, STATUS4_DESC);
			map.putConstant(STATUS5, STATUS5_DESC);
			map.putConstant(STATUS7, STATUS7_DESC);
			map.putConstant(STATUS8, STATUS8_DESC);
			ConstantsRepository.getInstance().register(ResourceStatus.class, map);
			
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
	/**
	 * 出版资源状态
	 * 
	 * @author zuo
	 */
	public static class PublishResourceStatus {
		public static final String STATUS0 = "0";
		public static final String STATUS0_DESC = "草稿";
		public static final String STATUS2 = "1";
		public static final String STATUS2_DESC = "待审核";
		public static final String STATUS3 = "3";
		public static final String STATUS3_DESC = "已通过";
		public static final String STATUS4 = "4";
		public static final String STATUS4_DESC = "下线";
		public static final String STATUS5 = "5";
		public static final String STATUS5_DESC = "已驳回";
		public static final String STATUS6 = "8";
		public static final String STATUS6_DESC = "解锁";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(STATUS0, STATUS0_DESC);
			map.putConstant(STATUS2, STATUS2_DESC);
			map.putConstant(STATUS3, STATUS3_DESC);
			map.putConstant(STATUS4, STATUS4_DESC);
			map.putConstant(STATUS5, STATUS5_DESC);
			map.putConstant(STATUS6, STATUS6_DESC);
			ConstantsRepository.getInstance().register(PublishResourceStatus.class, map);
			
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
	/**
	 * 标签类型
	 * 
	 * @author zuo
	 */
	public static class TargetType {
		public static final String STATUS1 = "0";
		public static final String STATUS1_DESC = "通用标签";
		public static final String STATUS2 = "1";
		public static final String STATUS2_DESC = "标准标签";
		public static final String STATUS3 = "2";
		public static final String STATUS3_DESC = "原始标签";
		public static final String STATUS4 = "3";
		public static final String STATUS4_DESC = "聚合标签";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(STATUS1, STATUS1_DESC);
			map.putConstant(STATUS2, STATUS2_DESC);
			map.putConstant(STATUS3, STATUS3_DESC);
			map.putConstant(STATUS4, STATUS4_DESC);
			ConstantsRepository.getInstance().register(TargetType.class, map);
			
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
	/**
	 * 接口资源类型
	 * 
	 * @author zuo
	 */
	public static class CodeType {
		public static final String STATUS0 = "";
		public static final String STATUS0_DESC = "请选择";
		public static final String STATUS1 = "00";
		public static final String STATUS1_DESC = "资源类型";
		public static final String STATUS2 = "01";
		public static final String STATUS2_DESC = "分册";
		public static final String STATUS3 = "02";
		public static final String STATUS3_DESC = "学科";
		public static final String STATUS4 = "03";
		public static final String STATUS4_DESC = "年级";
		public static final String STATUS5 = "04";
		public static final String STATUS5_DESC = "学段";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(STATUS0, STATUS0_DESC);
			map.putConstant(STATUS1, STATUS1_DESC);
			map.putConstant(STATUS2, STATUS2_DESC);
			map.putConstant(STATUS3, STATUS3_DESC);
			map.putConstant(STATUS4, STATUS4_DESC);
			map.putConstant(STATUS5, STATUS5_DESC);
			ConstantsRepository.getInstance().register(CodeType.class, map);
			
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
	/**
	 * 教育标签类型
	 * 
	 * @author zuo
	 */
	public static class resTargetType {
		public static final String STATUS1 = "120";
		public static final String STATUS1_DESC = "资源标签";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(STATUS1, STATUS1_DESC);
			ConstantsRepository.getInstance().register(resTargetType.class, map);
			
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
	
	
	/**
	 * 标签状态
	 * 
	 * @author zuo
	 */
	public static class targetStatus {
//		public static final String STATUS0 = "";
//		public static final String STATUS0_DESC = "全部";
		public static final String STATUS1 = "1";
		public static final String STATUS1_DESC = "启用";
		public static final String STATUS2 = "2";
		public static final String STATUS2_DESC = "禁用";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
//			map.putConstant(STATUS0, STATUS0_DESC);
			map.putConstant(STATUS1, STATUS1_DESC);
			map.putConstant(STATUS2, STATUS2_DESC);
			ConstantsRepository.getInstance().register(targetStatus.class, map);
			
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
	/**
	 * 资源模块
	 */
	public static class ResourceMoudle {
		public static final String TYPE0 = "TB";
		public static final String TYPE0_DESC = "同步资源";
		public static final String TYPE1 = "ZT";
		public static final String TYPE1_DESC = "专题资源";
		public static final String TYPE2 = "ZS";
		public static final String TYPE2_DESC = "知识点资源";
		public static final String TYPE3 = "TZ";
		public static final String TYPE3_DESC = "拓展资源";
		public static final String TYPE4 = "JS";
		public static final String TYPE4_DESC = "教师专业发展";
		public static final String TYPE5 = "ST";
		public static final String TYPE5_DESC = "试题";
		public static final String TYPE6 = "SJ";
		public static final String TYPE6_DESC = "试卷";
		public static ConstantsMap map;

		static {
			map = new ConstantsMap();
			map.putConstant(TYPE0, TYPE0_DESC);
			map.putConstant(TYPE1, TYPE1_DESC);
			map.putConstant(TYPE2, TYPE2_DESC);
			map.putConstant(TYPE3, TYPE3_DESC);
			map.putConstant(TYPE4, TYPE4_DESC);
			map.putConstant(TYPE5, TYPE5_DESC);
			map.putConstant(TYPE6, TYPE6_DESC);
			ConstantsRepository.getInstance().register(ResourceMoudle.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}

		public static String getValueByDesc(Object value) {
			if (value == null) {
				return "";
			}
			return (String) map.getValueByDesc(value);
		}
	}

	/**
	 * 资源类型
	 */
	public static class ResourceType {
		public static final String TYPE0 = "T01";
		public static final String TYPE0_DESC = "教学素材";
		public static final String TYPE1 = "T02";
		public static final String TYPE1_DESC = "教学设计";
		public static final String TYPE2 = "T03";
		public static final String TYPE2_DESC = "课件";
		public static final String TYPE3 = "T04";
		public static final String TYPE3_DESC = "试卷习题";
		public static final String TYPE4 = "T05";
		public static final String TYPE4_DESC = "微视频";
		public static final String TYPE5 = "T06";
		public static final String TYPE5_DESC = "数字图书";
		public static final String TYPE6 = "T07";
		public static final String TYPE6_DESC = "数字教材";
		public static final String TYPE7 = "T08";
		public static final String TYPE7_DESC = "教学工具";
		public static final String TYPE8 = "T09";
		public static final String TYPE8_DESC = "教育游戏";
		public static final String TYPE9 = "T10";
		public static final String TYPE9_DESC = "网络课程";
		public static final String TYPE10 = "T11";
		public static final String TYPE10_DESC = "教学案例";
		public static final String TYPE11 = "T12";
		public static final String TYPE11_DESC = "教育网站";
		public static final String TYPE12 = "T00";
		public static final String TYPE12_DESC = "教育资源";
		public static final String TYPE13 = "T13";
		public static final String TYPE13_DESC = "学案";
		public static final String TYPE16 = "T16";
		public static final String TYPE16_DESC = "条目";
		public static final String TYPE17 = "T17";
		public static final String TYPE17_DESC = "论文";
		public static ConstantsMap map;

		static {
			map = new ConstantsMap();
			map.putConstant(TYPE0, TYPE0_DESC);
			map.putConstant(TYPE1, TYPE1_DESC);
			map.putConstant(TYPE2, TYPE2_DESC);
			map.putConstant(TYPE3, TYPE3_DESC);
			map.putConstant(TYPE4, TYPE4_DESC);
			map.putConstant(TYPE5, TYPE5_DESC);
			map.putConstant(TYPE6, TYPE6_DESC);
			map.putConstant(TYPE7, TYPE7_DESC);
			map.putConstant(TYPE8, TYPE8_DESC);
			map.putConstant(TYPE9, TYPE9_DESC);
			map.putConstant(TYPE10, TYPE10_DESC);
			map.putConstant(TYPE11, TYPE11_DESC);
			map.putConstant(TYPE12, TYPE12_DESC);
			map.putConstant(TYPE13, TYPE13_DESC);
			map.putConstant(TYPE16, TYPE16_DESC);
			map.putConstant(TYPE17, TYPE17_DESC);
			ConstantsRepository.getInstance().register(ResourceType.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}

		public static String getValueByDesc(Object value) {
			if (value == null) {
				return "";
			}
			return (String) map.getValueByDesc(value);
		}
	}
	/**
	 * 资源类型
	 */
	public static class ResourceCaType {
		
		public static final String TYPE9 = "T10";
		public static final String TYPE9_DESC = "网络课程";
		public static final String TYPE10 = "T11";
		public static final String TYPE10_DESC = "教学案例";
		public static ConstantsMap map;

		static {
			map = new ConstantsMap();
			map.putConstant(TYPE9, TYPE9_DESC);
			map.putConstant(TYPE10, TYPE10_DESC);
			ConstantsRepository.getInstance().register(ResourceCaType.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}

		public static String getValueByDesc(Object value) {
			if (value == null) {
				return "";
			}
			return (String) map.getValueByDesc(value);
		}
	}
	
	/**
	 * 教育阶段
	 */
	public static class EducationPeriod {
		public static final String TYPE0 = "K";
		public static final String TYPE0_DESC = "学前";
		public static final String TYPE1 = "P";
		public static final String TYPE1_DESC = "小学";
		public static final String TYPE2 = "M";
		public static final String TYPE2_DESC = "初中";
		public static final String TYPE3 = "H";
		public static final String TYPE3_DESC = "高中";
		public static final String TYPE4 = "A";
		public static final String TYPE4_DESC = "通用";
		public static ConstantsMap map;

		static {
			map = new ConstantsMap();
			map.putConstant(TYPE0, TYPE0_DESC);
			map.putConstant(TYPE1, TYPE1_DESC);
			map.putConstant(TYPE2, TYPE2_DESC);
			map.putConstant(TYPE3, TYPE3_DESC);
			map.putConstant(TYPE4, TYPE4_DESC);

			ConstantsRepository.getInstance().register(EducationPeriod.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}

		public static String getValueByDesc(Object value) {
			if (value == null) {
				return "";
			}
			return (String) map.getValueByDesc(value);
		}
	}

	/**
	 * 适用对象
	 */
	public static class Audience {
		public static final String TYPE0 = "XS";
		public static final String TYPE0_DESC = "学生";
		public static final String TYPE1 = "JS";
		public static final String TYPE1_DESC = "教师";
		public static final String TYPE2 = "JZ";
		public static final String TYPE2_DESC = "家长";
		public static final String TYPE3 = "GL";
		public static final String TYPE3_DESC = "教育管理者";
		public static final String TYPE4 = "QT";
		public static final String TYPE4_DESC = "全体";
		private static ConstantsMap map;

		static {
			map = new ConstantsMap();
			map.putConstant(TYPE0, TYPE0_DESC);
			map.putConstant(TYPE1, TYPE1_DESC);
			map.putConstant(TYPE2, TYPE2_DESC);
			map.putConstant(TYPE3, TYPE3_DESC);
			map.putConstant(TYPE4, TYPE4_DESC);

			ConstantsRepository.getInstance().register(Audience.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	/**
	 * 开放程度
	 */
	public static class OpeningRate {
		public static final String TYPE0 = "QJ";
		public static final String TYPE0_DESC = "全局";
		public static final String TYPE1 = "QY";
		public static final String TYPE1_DESC = "区域";
		public static final String TYPE2 = "XN";
		public static final String TYPE2_DESC = "校内";
		public static final String TYPE3 = "GR";
		public static final String TYPE3_DESC = "个人";
		private static ConstantsMap map;

		static {
			map = new ConstantsMap();
			map.putConstant(TYPE0, TYPE0_DESC);
			map.putConstant(TYPE1, TYPE1_DESC);
			map.putConstant(TYPE2, TYPE2_DESC);
			map.putConstant(TYPE3, TYPE3_DESC);

			ConstantsRepository.getInstance().register(OpeningRate.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}

		public static String getValueByDesc(Object value) {
			if (value == null) {
				return "";
			}
			return (String) map.getValueByDesc(value);
		}
	}

	/**
	 * 语种
	 * 
	 * @author zuo
	 */
	public static class Language {
		public static final String TYPE0 = "ZH";
		public static final String TYPE0_DESC = "中文";
		public static final String TYPE1 = "EN";
		public static final String TYPE1_DESC = "英文";
		public static final String TYPE2 = "OT";
		public static final String TYPE2_DESC = "其他";
		private static ConstantsMap map;

		static {
			map = new ConstantsMap();
			map.putConstant(TYPE0, TYPE0_DESC);
			map.putConstant(TYPE1, TYPE1_DESC);
			map.putConstant(TYPE2, TYPE2_DESC);

			ConstantsRepository.getInstance().register(Language.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}

		public static String getValueByDesc(Object value) {
			if (value == null) {
				return "";
			}
			return (String) map.getValueByDesc(value);
		}
	}

	/**
	 * 开放等级
	 * 
	 * @author zuo
	 */
	public static class ReleaseScope {
		public static final String TYPE0 = "1";
		public static final String TYPE0_DESC = "1级";
		public static final String TYPE1 = "2";
		public static final String TYPE1_DESC = "2级";
		public static final String TYPE2 = "3";
		public static final String TYPE2_DESC = "3级";
		private static ConstantsMap map;

		static {
			map = new ConstantsMap();
			map.putConstant(TYPE0, TYPE0_DESC);
			map.putConstant(TYPE1, TYPE1_DESC);
			map.putConstant(TYPE2, TYPE2_DESC);

			ConstantsRepository.getInstance().register(ReleaseScope.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	/**
	 * 性别
	 * 
	 * @author zuo
	 */
	public static class Gender {
		public static final String TYPE0 = "M";
		public static final String TYPE0_DESC = "男";
		public static final String TYPE1 = "F";
		public static final String TYPE1_DESC = "女";
		private static ConstantsMap map;

		static {
			map = new ConstantsMap();
			map.putConstant(TYPE0, TYPE0_DESC);
			map.putConstant(TYPE1, TYPE1_DESC);

			ConstantsRepository.getInstance().register(Gender.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}

	/**
	 * 节点类型
	 */
	public static class NodeType {
		public static final String TYPE0 = "0";
		public static final String TYPE0_DESC = "教材版本";
		public static final String TYPE1 = "1";
		public static final String TYPE1_DESC = "教育阶段";
		public static final String TYPE2 = "2";
		public static final String TYPE2_DESC = "适用年级";
		public static final String TYPE3 = "3";
		public static final String TYPE3_DESC = "学科";
		public static final String TYPE4 = "4";
		public static final String TYPE4_DESC = "分册";
		public static final String TYPE5 = "5";
		public static final String TYPE5_DESC = "图书目录";
		public static final String TYPE6 = "6";
		public static final String TYPE6_DESC = "资源节点";
		public static final String TYPE7 = "7";
		public static final String TYPE7_DESC = "资源模块";
		public static final String TYPE8 = "8";
		public static final String TYPE8_DESC = "资源类型";
		private static ConstantsMap map;

		static {
			map = new ConstantsMap();
			map.putConstant(TYPE0, TYPE0_DESC);
			map.putConstant(TYPE1, TYPE1_DESC);
			map.putConstant(TYPE2, TYPE2_DESC);
			map.putConstant(TYPE3, TYPE3_DESC);
			map.putConstant(TYPE4, TYPE4_DESC);
			map.putConstant(TYPE5, TYPE5_DESC);
			map.putConstant(TYPE6, TYPE6_DESC);
			map.putConstant(TYPE7, TYPE7_DESC);
			map.putConstant(TYPE8, TYPE8_DESC);

			ConstantsRepository.getInstance().register(NodeType.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}

		public static String getKeyByValue(Object value) {
			if (value == null) {
				return "";
			}
			return (String) map.getValueByDesc(value);
		}
	}

	/**
	 * 消费方式
	 */
	public static class ConsumeType {
		public static final String TYPE0 = "1";
		public static final String TYPE0_DESC = "免费";
		public static final String TYPE1 = "2";
		public static final String TYPE1_DESC = "积分";
		public static final String TYPE2 = "3";
		public static final String TYPE2_DESC = "云币";
		private static ConstantsMap map;

		static {
			map = new ConstantsMap();
			map.putConstant(TYPE0, TYPE0_DESC);
			map.putConstant(TYPE1, TYPE1_DESC);
			map.putConstant(TYPE2, TYPE2_DESC);

			ConstantsRepository.getInstance().register(ConsumeType.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}

		public static String getValueByDesc(Object value) {
			if (value == null) {
				return "";
			}
			return (String) map.getValueByDesc(value);
		}
	}

	/**
	 * 资源批量导入状态
	 * 
	 * @author zuo
	 * 
	 */
	public static class ImportStatus {
//		public static final int STATUS0 = 0;
//		public static final String STATUS0_DESC = "初始化";
		public static final int STATUS1 = 1;
		public static final String STATUS1_DESC = "待处理";
		public static final int STATUS2 = 2;
		public static final String STATUS2_DESC = "处理中";
		public static final int STATUS3 = 3;
		public static final String STATUS3_DESC = "部分成功";
		public static final int STATUS4 = 4;
		public static final String STATUS4_DESC = "全部成功";
		public static final int STATUS5 = 5;
		public static final String STATUS5_DESC = "全部失败";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
//			map.putConstant(STATUS0, STATUS0_DESC);
			map.putConstant(STATUS1, STATUS1_DESC);
			map.putConstant(STATUS2, STATUS2_DESC);
			map.putConstant(STATUS3, STATUS3_DESC);
			map.putConstant(STATUS4, STATUS4_DESC);
			map.putConstant(STATUS5, STATUS5_DESC);
			ConstantsRepository.getInstance().register(ImportStatus.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	/**
	 * 系统参数类型
	 * @author shea
	 *
	 */
	public static class Types {
		public static final int PARAMETER_TYPE0 = 0;
		public static final String PARAMETERT_TYPE0_DESC = "系统参数";
		public static final int PARAMETER_TYPE1 = 1;
		public static final String PARAMETERT_TYPE1_DESC = "配置参数";
		public static final int PARAMETER_TYPE2 = 2;
		public static final String PARAMETER_TYPE2_DESC = "业务参数";
		public static final int PARAMETER_TYPE3 = 3;
		public static final String PARAMETER_TYPE3_DESC = "其他";
		public static Map<Integer, String> map;
		static {
			map = new LinkedHashMap<Integer, String>();
			map.put(PARAMETER_TYPE0, PARAMETERT_TYPE0_DESC);
			map.put(PARAMETER_TYPE1, PARAMETERT_TYPE1_DESC);
			map.put(PARAMETER_TYPE2, PARAMETER_TYPE2_DESC);
			map.put(PARAMETER_TYPE3, PARAMETER_TYPE3_DESC);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.get(key);
		}
	}
	
	/**
	 * 资源库类型
	 * 
	 * @author zuo
	 * 
	 */
	public static class LibType {
		public static final String ORES_TYPE = "2";
		public static final String ORES_TYPE_DESC = "原始资源";
		public static final String BRES_TYPE = "1";
		public static final String BRES_TYPE_DESC = "标准资源";
		public static final String PRES_TYPE = "3";
		public static final String PRES_TYPE_DESC = "聚合资源";
		private static ConstantsMap map;
		
		static {
			map = new ConstantsMap();
			map.putConstant(ORES_TYPE, ORES_TYPE_DESC);
			map.putConstant(BRES_TYPE, BRES_TYPE_DESC);
			map.putConstant(PRES_TYPE, PRES_TYPE_DESC);
			ConstantsRepository.getInstance().register(LibType.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	/**
	 * 绩效考核：操作类型（职员）
	 * 
	 * @author Administrator
	 * 
	 */
	public static class OperatType {
		public static final String IMPORT_OPERATE_TYPE = "1";
		public static final String IMPORT_OPERATE_TYPE_DESC = "导入";
//		public static final String CREATE_OPERATE_TYPE = "2";
//		public static final String CREATE_OPERATE_TYPE_DESC = "创建";
		public static final String EDIT_OPERATE_TYPE = "3";
		public static final String EDIT_OPERATE_TYPE_DESC = "编辑";
		
		public static final String FIRST_CHECK_APPROVE = "4";
		public static final String FIRST_CHECK_APPROVE_DESC = "一审通过";
		public static final String FIRST_CHECK_REJECT = "5";
		public static final String FIRST_CHECK_REJECT_DESC = "一审驳回";
		public static final String SECOND_CHECK_APPROVE = "6";
		public static final String SECOND_CHECK_APPROVE_DESC = "二审通过";
		public static final String SECOND_CHECK_REJECT = "7";
		public static final String SECOND_CHECK_REJECT_DESC = "二审驳回";
		private static ConstantsMap map;

		static {
			map = new ConstantsMap();
			map.putConstant(IMPORT_OPERATE_TYPE, IMPORT_OPERATE_TYPE_DESC);
//			map.putConstant(CREATE_OPERATE_TYPE, CREATE_OPERATE_TYPE_DESC);
			map.putConstant(EDIT_OPERATE_TYPE, EDIT_OPERATE_TYPE_DESC);
			map.putConstant(FIRST_CHECK_APPROVE, FIRST_CHECK_APPROVE_DESC);
			map.putConstant(FIRST_CHECK_REJECT, FIRST_CHECK_REJECT_DESC);
			map.putConstant(SECOND_CHECK_APPROVE, SECOND_CHECK_APPROVE_DESC);
			map.putConstant(SECOND_CHECK_REJECT, SECOND_CHECK_REJECT_DESC);
			ConstantsRepository.getInstance().register(OperatType.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	/**
	 * 星级评价
	 * 
	 * @author Administrator
	 * 
	 */
	public static class StarLevel {
		public static final String LEVEL_1 = "1";
		public static final String LEVEL_1_DESC = "1星";
		public static final String LEVEL_2 = "2";
		public static final String LEVEL_2_DESC = "2星";
		public static final String LEVEL_3 = "3";
		public static final String LEVEL_3_DESC = "3星";
		public static final String LEVEL_4 = "4";
		public static final String LEVEL_4_DESC = "4星";
		public static final String LEVEL_5 = "5";
		public static final String LEVEL_5_DESC = "5星";
		private static ConstantsMap map;

		static {
			map = new ConstantsMap();
			map.putConstant(LEVEL_1, LEVEL_1_DESC);
			map.putConstant(LEVEL_2, LEVEL_2_DESC);
			map.putConstant(LEVEL_3, LEVEL_3_DESC);
			map.putConstant(LEVEL_4, LEVEL_4_DESC);
			map.putConstant(LEVEL_5, LEVEL_5_DESC);
			ConstantsRepository.getInstance().register(StarLevel.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	//加工状态
	public static class ProcessStatus {
		public static final int STATUS1 = 0;
		public static final String STATUS1_DESC = "未分配";
		public static final int STATUS2 = 1;
		public static final String STATUS2_DESC = "已分配";
		public static final int STATUS3 = 2;
		public static final String STATUS3_DESC = "已完成";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(STATUS1, STATUS1_DESC);
			map.putConstant(STATUS2, STATUS2_DESC);
			map.putConstant(STATUS3, STATUS3_DESC);
			ConstantsRepository.getInstance().register(ProcessStatus.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	//加工状态
		public static class ProcessSourceStatus {
			public static final int STATUS1 = 0;
			public static final String STATUS1_DESC = "未加工";
			public static final int STATUS2 = 1;
			public static final String STATUS2_DESC = "已加工";
			public static final int STATUS3 = 2;
			public static final String STATUS3_DESC = "已完成";
			private static ConstantsMap map;
			static {
				map = new ConstantsMap();
				map.putConstant(STATUS1, STATUS1_DESC);
				map.putConstant(STATUS2, STATUS2_DESC);
				map.putConstant(STATUS3, STATUS3_DESC);
				ConstantsRepository.getInstance().register(ProcessSourceStatus.class, map);
			}

			public static String getValueByKey(Object key) {
				if (key == null) {
					return "";
				}
				return (String) map.getDescByValue(key);
			}
		}
	
		//资源状态
		public static class SourceStatus {
			public static final int STATUS1 = 0;
			public static final String STATUS1_DESC = "未加工";
			public static final int STATUS3 = 2;
			public static final String STATUS3_DESC = "已完成";
			private static ConstantsMap map;
			static {
				map = new ConstantsMap();
				map.putConstant(STATUS1, STATUS1_DESC);
				map.putConstant(STATUS3, STATUS3_DESC);
				ConstantsRepository.getInstance().register(SourceStatus.class, map);
			}

			public static String getValueByKey(Object key) {
				if (key == null) {
					return "";
				}
				return (String) map.getDescByValue(key);
			}
		}
	//输入类型
		public static class FieldType {
			public static final int FieldType1 = 1;
			public static final String FieldType1_DESC = "文本";
			public static final int FieldType2 = 2;
			public static final String FieldType2_DESC = "选择框";
			public static final int FieldType3 = 3;
			public static final String FieldType3_DESC = "复选框";
			private static ConstantsMap map;
			static {
				map = new ConstantsMap();
				map.putConstant(FieldType1, FieldType1_DESC);
				map.putConstant(FieldType2, FieldType2_DESC);
				map.putConstant(FieldType3, FieldType3_DESC);
				ConstantsRepository.getInstance().register(FieldType.class, map);
			}

			public static String getValueByKey(Object key) {
				if (key == null) {
					return "";
				}
				return (String) map.getDescByValue(key);
			}
		}
		//值格式校验模式
		public static class ValidateModel {
			public static final int validateModel1 = 1;
			public static final String validateModel1_DESC = "数字";
			public static final int validateModel2 = 2;
			public static final String validateModel2_DESC = "字母";
			public static final int validateModel3 = 3;
			public static final String validateModel3_DESC = "日期";
			private static ConstantsMap map;
			static {
				map = new ConstantsMap();
				map.putConstant(validateModel1, validateModel1_DESC);
				map.putConstant(validateModel2, validateModel2_DESC);
				map.putConstant(validateModel3, validateModel3_DESC);
				ConstantsRepository.getInstance().register(ValidateModel.class, map);
			}

			public static String getValueByKey(Object key) {
				if (key == null) {
					return "";
				}
				return (String) map.getDescByValue(key);
			}
		}
		
		//特殊标识
				public static class Identifier {
					public static final int identifier1 = 1;
					public static final String identifier1_DESC = "书名";
					public static final int identifier2 = 2;
					public static final String identifier2_DESC = "资源名";
					public static final int identifier3 = 3;
					public static final String identifier3_DESC = "书号";
					private static ConstantsMap map;
					static {
						map = new ConstantsMap();
						map.putConstant(identifier1, identifier1_DESC);
						map.putConstant(identifier2, identifier2_DESC);
						map.putConstant(identifier3, identifier3_DESC);
						ConstantsRepository.getInstance().register(Identifier.class, map);
					}

					public static String getValueByKey(Object key) {
						if (key == null) {
							return "";
						}
						return (String) map.getDescByValue(key);
					}
				}
				//资源生命周期
				public static class ResLifeCycle {
					public static final String resLifeCycle1 = "1";
					public static final String resLifeCycle1_DESC = "原始资源";
					public static final String resLifeCycle2 = "2";
					public static final String resLifeCycle2_DESC = "基础资源";
					public static final String resLifeCycle3 = "3";
					public static final String resLifeCycle3_DESC = "标准资源";
					public static final String resLifeCycle4 = "4";
					public static final String resLifeCycle4_DESC = "产品资源";
					private static ConstantsMap map;
					static {
						map = new ConstantsMap();
						map.putConstant(resLifeCycle1, resLifeCycle1_DESC);
						map.putConstant(resLifeCycle2, resLifeCycle2_DESC);
						map.putConstant(resLifeCycle3, resLifeCycle3_DESC);
						map.putConstant(resLifeCycle4, resLifeCycle4_DESC);
						ConstantsRepository.getInstance().register(ResLifeCycle.class, map);
					}

					public static String getValueByKey(Object key) {
						if (key == null) {
							return "";
						}
						return (String) map.getDescByValue(key);
					}
				}
				//元数据类别
				public static class Type {
					public static final int type1 = 1;
					public static final String type1_DESC = "核心数据";
					public static final int type2 = 2;
					public static final String type2_DESC = "分类扩展元数据";
					public static final int type3 = 3;
					public static final String type3_DESC = "版权元数据";
					private static ConstantsMap map;
					static {
						map = new ConstantsMap();
						map.putConstant(type1, type1_DESC);
						map.putConstant(type2, type2_DESC);
						map.putConstant(type3, type3_DESC);
						ConstantsRepository.getInstance().register(Type.class, map);
					}

					public static String getValueByKey(Object key) {
						if (key == null) {
							return "";
						}
						return (String) map.getDescByValue(key);
					}
				}
				
				//导出等级
				public static class ExportLevel {
					public static final String exportLevel1 = "1";
					public static final String exportLevel1_DESC = "一级";
					public static final String exportLevel2 = "2";
					public static final String exportLevel2_DESC = "二级";
					public static final String exportLevel3 = "3";
					public static final String exportLevel3_DESC = "三级";
					public static final String exportLevel4 = "4";
					public static final String exportLevel4_DESC = "四级";
					public static final String exportLevel5 = "5";
					public static final String exportLevel5_DESC = "五级";
					public static final String exportLevel6 = "6";
					public static final String exportLevel6_DESC = "六级";
					public static final String exportLevel7 = "7";
					public static final String exportLevel7_DESC = "七级";
					private static ConstantsMap map;
					static {
						map = new ConstantsMap();
						map.putConstant(exportLevel1, exportLevel1_DESC);
						map.putConstant(exportLevel2, exportLevel2_DESC);
						map.putConstant(exportLevel3, exportLevel3_DESC);
						map.putConstant(exportLevel4, exportLevel4_DESC);
						map.putConstant(exportLevel5, exportLevel5_DESC);
						map.putConstant(exportLevel6, exportLevel6_DESC);
						map.putConstant(exportLevel7, exportLevel7_DESC);
						ConstantsRepository.getInstance().register(ExportLevel.class, map);
					}

					public static String getValueByKey(Object key) {
						if (key == null) {
							return "";
						}
						return (String) map.getDescByValue(key);
					}
				}
				//查询级别
				public static class OpenQuery {
					public static final String openQuery1 = "1";
					public static final String openQuery1_DESC = "不启用";
					public static final String openQuery2 = "2";
					public static final String openQuery2_DESC = "一般";
					public static final String openQuery3 = "3";
					public static final String openQuery3_DESC = "高级";
					private static ConstantsMap map;
					static {
						map = new ConstantsMap();
						map.putConstant(openQuery1, openQuery1_DESC);
						map.putConstant(openQuery2, openQuery2_DESC);
						map.putConstant(openQuery3, openQuery3_DESC);
						ConstantsRepository.getInstance().register(OpenQuery.class, map);
					}

					public static String getValueByKey(Object key) {
						if (key == null) {
							return "";
						}
						return (String) map.getDescByValue(key);
					}
				}

	public static class QueryModel {
		public static final int queryModel1 = 1;
		public static final String queryModel1_DESC = "不用于查询";
		public static final int queryModel2 = 2;
		public static final String queryModel2_DESC = "完全匹配查询";
		public static final int queryModel3 = 3;
		public static final String queryModel3_DESC = "模糊匹配查询";
		public static final int queryModel4 = 4;
		public static final String queryModel4_DESC = "区间查询";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(queryModel1, queryModel1_DESC);
			map.putConstant(queryModel2, queryModel2_DESC);
			map.putConstant(queryModel3, queryModel3_DESC);
			map.putConstant(queryModel4, queryModel4_DESC);
			ConstantsRepository.getInstance().register(QueryModel.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	//特殊标识
	public static class PublishSeparator {
		public static final String separator1 = "/";
		public static final String separator1_DESC = "/";
		public static final String separator2 = ".";
		public static final String separator2_DESC = ".";
		public static final String separator3 = "-";
		public static final String separator3_DESC = "-";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(separator1, separator1_DESC);
			map.putConstant(separator2, separator2_DESC);
			map.putConstant(separator3, separator3_DESC);
			ConstantsRepository.getInstance().register(PublishSeparator.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}		
	/**
	 * 标签状态
	 * 
	 * @author zuo
	 */
	public static class LifeCycle {
		public static final String STATUS0 = "ores";
		public static final String STATUS0_DESC = "原始资源";
		public static final String STATUS1 = "standRes";
		public static final String STATUS1_DESC = "标准资源";
		public static final String STATUS2 = "collectRes";
		public static final String STATUS2_DESC = "聚合资源";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(STATUS0, STATUS0_DESC);
			map.putConstant(STATUS1, STATUS1_DESC);
			map.putConstant(STATUS2, STATUS2_DESC);
			ConstantsRepository.getInstance().register(LifeCycle.class, map);
			
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
	/**
	 * 批量导入详细状态
	 * 
	 * @author zuo
	 */
	public static class BatchImportDetaillType {
		public static final int STATUS1 = 1;
		public static final String STATUS1_DESC = "待处理";
		public static final int STATUS2 = 2;
		public static final String STATUS2_DESC = "处理中";
		public static final int STATUS3 = 3;
		public static final String STATUS3_DESC = "成功";
		public static final int STATUS4 = 4;
		public static final String STATUS4_DESC = "失败";
		public static final int STATUS5 = 5;
		public static final String STATUS5_DESC = "重复";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(STATUS1, STATUS1_DESC);
			map.putConstant(STATUS2, STATUS2_DESC);
			map.putConstant(STATUS3, STATUS3_DESC);
			map.putConstant(STATUS4, STATUS4_DESC);
			map.putConstant(STATUS5, STATUS5_DESC);
			ConstantsRepository.getInstance().register(BatchImportDetaillType.class, map);
			
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
	
	//学研平台资源类别
	public static class ResearchPlatResType {
		public static final String TYPE0 = "0";
		public static final String TYPE0_DESC = "期刊";
		public static final String TYPE1 = "1";
		public static final String TYPE1_DESC = "文章";
		public static final String TYPE2 = "2";
		public static final String TYPE2_DESC = "大事辑览";
		public static final String TYPE3 = "3";
		public static final String TYPE3_DESC = "插图";
		public static final String TYPE4 = "4";
		public static final String TYPE4_DESC = "网页爬虫";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(TYPE0, TYPE0_DESC);
			map.putConstant(TYPE1, TYPE1_DESC);
			map.putConstant(TYPE2, TYPE2_DESC);
			map.putConstant(TYPE3, TYPE3_DESC);
			map.putConstant(TYPE4, TYPE4_DESC);
			ConstantsRepository.getInstance().register(ResearchPlatResType.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
}