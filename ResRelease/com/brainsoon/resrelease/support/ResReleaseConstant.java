package com.brainsoon.resrelease.support;

import com.brainsoon.appframe.support.ConstantsMap;
import com.brainsoon.appframe.support.ConstantsRepository;

public class ResReleaseConstant {

	public static String APPLYSUCCESS = "SUCCESS";
	public static String APPLYFAILURE = "FAILURE";
	public static String NORESOURCE = "NORESOURCE";
	/**
	 * 状态
	 */
	public static class OrderStatus {
		public static final String CREATED = "0";
		public static final String CREATED_DESC = "未提交";
		public static final String TO_AUDIT = "1";
		public static final String TO_AUDIT_DESC = "待审核";
		public static final String AUDITED = "2";
		public static final String AUDITED_DESC = "审核通过";
		public static final String AUDIT_REFUSE = "3";
		public static final String AUDIT_REFUSE_DESC = "审核驳回";
		public static final String WAITING_PROCESS = "4";
		public static final String WAITING_PROCESS_DESC = "待加工";
		public static final String PROCESSING = "5";
		public static final String PROCESSING_DESC = "加工中";
		public static final String PROCESSED = "6";
		public static final String PROCESSED_DESC = "加工成功";
		public static final String PROCESSEDWRONG = "7";
		public static final String PROCESSEDWRONG_DESC = "加工失败";
		public static final String WAITING_PUBLISH = "8";
		public static final String WAITING_PUBLISH_DESC = "待发布";
		public static final String PUBLISHING = "9";
		public static final String PUBLISHING_DESC = "发布中";
		public static final String PUBLISHED = "10";
		public static final String PUBLISHED_DESC = "已发布";
		public static final String ORDERADD = "11";
		public static final String ORDERADD_DESC = "可追加";
		
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(CREATED, CREATED_DESC);
			map.putConstant(TO_AUDIT, TO_AUDIT_DESC);
			map.putConstant(AUDITED, AUDITED_DESC);
			map.putConstant(AUDIT_REFUSE, AUDIT_REFUSE_DESC);
			map.putConstant(WAITING_PROCESS, WAITING_PROCESS_DESC);
			map.putConstant(PROCESSING, PROCESSING_DESC);
			map.putConstant(PROCESSED, PROCESSED_DESC);
			map.putConstant(PROCESSEDWRONG, PROCESSEDWRONG_DESC);
			map.putConstant(WAITING_PUBLISH, WAITING_PUBLISH_DESC);
			map.putConstant(PUBLISHING, PUBLISHING_DESC);
			map.putConstant(PUBLISHED, PUBLISHED_DESC);
			map.putConstant(ORDERADD, ORDERADD_DESC);
			ConstantsRepository.getInstance().register(OrderStatus.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	public static class TaskStatus {
		public static final String CREATED = "0";
		public static final String CREATED_DESC = "未提交";
		public static final String TO_PROCESS = "3";
		public static final String TO_PROCESS_DESC = "待发布";
		public static final String PROCESSING = "4";
		public static final String PROCESSING_DESC = "发布中";
		public static final String PROCESSED = "6";
		public static final String PROCESSED_DESC = "发布完成";
		public static final String UN_FINISHED = "7";
		public static final String UN_FINISHED_DESC = "发布未完成";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(CREATED, CREATED_DESC);
			map.putConstant(TO_PROCESS, TO_PROCESS_DESC);
			map.putConstant(PROCESSING, PROCESSING_DESC);
			map.putConstant(PROCESSED, PROCESSED_DESC);
			map.putConstant(UN_FINISHED, UN_FINISHED_DESC);
			ConstantsRepository.getInstance().register(TaskStatus.class, map);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	public static class DetailStatus {
		public static final String TEMP = "0";
		public static final String SAVED = "1";
		public static final String PROCESSED = "3";
		public static final String PROCESSING = "2";
		public static final String FAILED = "4";
		public static final String TEMP_DESC = "临时保存";
		public static final String SAVED_DESC = "等待发布";
		public static final String PROCESSED_DESC = "发布完成";
		public static final String PROCESSING_DESC = "发布中";
		public static final String FAILED_DESC = "发布失败";
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(TEMP, TEMP_DESC);
			map.putConstant(SAVED, SAVED_DESC);
			map.putConstant(PROCESSED, PROCESSED_DESC);
			map.putConstant(PROCESSING, PROCESSING_DESC);
			map.putConstant(FAILED, FAILED_DESC);
			ConstantsRepository.getInstance().register(DetailStatus.class, map);
		}
		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	public static class PublishStatus {
		public static final String PUBLISH_STATUS_0 = "0";
		public static final String PUBLISH_STATUS_1 = "1";
		
		public static final String PUBLISH_STATUS_0_DESC = "未发布";
		public static final String PUBLISH_STATUS_1_DESC = "已发布";
		
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(PUBLISH_STATUS_0, PUBLISH_STATUS_0_DESC);
			map.putConstant(PUBLISH_STATUS_0, PUBLISH_STATUS_1_DESC);
			ConstantsRepository.getInstance().register(PublishStatus.class, map);
		}
		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	public static class ResourceType{
		public static final String RESOURCE_TYPE_1 = "1";
		public static final String RESOURCE_TYPE_2 = "2";
		public static final String RESOURCE_TYPE_3 = "3";
		
		public static final String RESOURCE_TYPE_1_DESC = "标准资源";
		public static final String RESOURCE_TYPE_2_DESC = "原始资源";
		public static final String RESOURCE_TYPE_3_DESC = "应用资源";
		
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(RESOURCE_TYPE_1, RESOURCE_TYPE_1_DESC);
			map.putConstant(RESOURCE_TYPE_2, RESOURCE_TYPE_2_DESC);
			map.putConstant(RESOURCE_TYPE_3, RESOURCE_TYPE_3_DESC);
			ConstantsRepository.getInstance().register(ResourceType.class, map);
		}
		
		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	
	public static class WorkFlowInfo{
		public static final String  WORK_FLOW_INFO_1 = "1";
		public static final String WORK_FLOW_INFO_2 = "2";
		public static final String WORK_FLOW_INFO_3 = "3";
		
		public static final String WORK_FLOW_INFO_1_DESC = "需求单中没有任何资源，无法进行上报！";
		public static final String WORK_FLOW_INFO_2_DESC = "未上报的需求单才能进行上报！";
		public static final String WORK_FLOW_INFO_3_DESC = "上报成功！";
		
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(WORK_FLOW_INFO_1, WORK_FLOW_INFO_1_DESC);
			map.putConstant(WORK_FLOW_INFO_2, WORK_FLOW_INFO_2_DESC);
			map.putConstant(WORK_FLOW_INFO_3, WORK_FLOW_INFO_3_DESC);
		}
		
		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	public static class ProcessStatus{
		public static final String FAILED = "0";
		public static final String  PROCESSED = "1";
		public static final String  PART_PROCESSED = "2"; 
		
		
		public static final String FAILED_DESC = "失败";
		public static final String PROCESSED_DESC = "全部成功";
		public static final String  PART_PROCESSED_DESC = "部分成功"; 
		
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(FAILED, FAILED_DESC);
			map.putConstant(PROCESSED, PROCESSED_DESC);
			map.putConstant(PART_PROCESSED, PART_PROCESSED_DESC);
			ConstantsRepository.getInstance().register(ProcessStatus.class, map);
		}
		
		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	public static class ResReleaseDetailStatus{
		public static final String FAILED = "0";
		public static final String  PROCESSED = "1";
		
		
		public static final String FAILED_DESC = "失败";
		public static final String PROCESSED_DESC = "成功";
		
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(FAILED, FAILED_DESC);
			map.putConstant(PROCESSED, PROCESSED_DESC);
		}
		
		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	public static class ResTypeDesc{
		public static final String RESTYPE_T00 = "T00";
		public static final String RESTYPE_T01 = "T01";
		public static final String RESTYPE_T02 = "T02";
		public static final String RESTYPE_T03 = "T03";
		public static final String RESTYPE_T04 = "T04";
		public static final String RESTYPE_T05 = "T05";
		public static final String RESTYPE_T06 = "T06";
		public static final String RESTYPE_T07 = "T07";
		public static final String RESTYPE_T08 = "T08";
		public static final String RESTYPE_T09 = "T09";
		public static final String RESTYPE_T10 = "T10";
		public static final String RESTYPE_T11 = "T11";
		public static final String RESTYPE_T12 = "T12";
		
		public static final String RESTYPE_T00_DESC = "教育资源";
		public static final String RESTYPE_T01_DESC = "教学素材";
		public static final String RESTYPE_T02_DESC = "教学设计";
		public static final String RESTYPE_T03_DESC = "教学课件";
		public static final String RESTYPE_T04_DESC = "试卷习题";
		public static final String RESTYPE_T05_DESC = "微视频";
		public static final String RESTYPE_T06_DESC = "数字图书";
		public static final String RESTYPE_T07_DESC = "数字教材";
		public static final String RESTYPE_T08_DESC = "教学工具";
		public static final String RESTYPE_T09_DESC = "教育游戏";
		public static final String RESTYPE_T10_DESC = "网络课程";
		public static final String RESTYPE_T11_DESC = "教学案例";
		public static final String RESTYPE_T12_DESC = "教育网站";
		
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(RESTYPE_T00, RESTYPE_T00_DESC);
			map.putConstant(RESTYPE_T01, RESTYPE_T01_DESC);
			map.putConstant(RESTYPE_T02, RESTYPE_T02_DESC);
			map.putConstant(RESTYPE_T03, RESTYPE_T03_DESC);
			map.putConstant(RESTYPE_T04, RESTYPE_T04_DESC);
			map.putConstant(RESTYPE_T05, RESTYPE_T05_DESC);
			map.putConstant(RESTYPE_T06, RESTYPE_T06_DESC);
			map.putConstant(RESTYPE_T07, RESTYPE_T07_DESC);
			map.putConstant(RESTYPE_T08, RESTYPE_T08_DESC);
			map.putConstant(RESTYPE_T09, RESTYPE_T09_DESC);
			map.putConstant(RESTYPE_T10, RESTYPE_T10_DESC);
			map.putConstant(RESTYPE_T11, RESTYPE_T11_DESC);
			map.putConstant(RESTYPE_T12, RESTYPE_T12_DESC);
			ConstantsRepository.getInstance().register(ResTypeDesc.class, map);
		}
		
		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	//发布过程中资源的各个状态
	public static class PublishingStatus{
		public static final String WAITING_PUBLISH = "0";
		public static final String PUBLISHING = "1";
		public static final String SUCCESS_PUBLISH = "2";
		public static final String FAIL_PUBLISH = "3";
		
		
		public static final String WAITING_PUBLISH_DESC = "待发布";
		public static final String PUBLISHING_DESC = "发布中";
		public static final String SUCCESS_PUBLISH_DESC = "发布成功";
		public static final String FAIL_PUBLISH_DESC = "发布失败";
		
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(WAITING_PUBLISH, WAITING_PUBLISH_DESC);
			map.putConstant(PUBLISHING, PUBLISHING_DESC);
			map.putConstant(SUCCESS_PUBLISH, SUCCESS_PUBLISH_DESC);
			map.putConstant(FAIL_PUBLISH, FAIL_PUBLISH_DESC);
		}
		
		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	//发布过程中资源的各个状态
	public static class ResReleaseStatus{
		public static final String WAITING_PUBLISH = "0";
		public static final String PUBLISHING = "1";
		public static final String PUBLISHED = "2";
		
		
		public static final String WAITING_PUBLISH_DESC = "待发布";
		public static final String PUBLISHING_DESC = "发布中";
		public static final String PUBLISHED_DESC = "已发布";
		
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(WAITING_PUBLISH, WAITING_PUBLISH_DESC);
			map.putConstant(PUBLISHING, PUBLISHING_DESC);
			map.putConstant(PUBLISHED, PUBLISHED_DESC);
			ConstantsRepository.getInstance().register(ResReleaseStatus.class, map);
		}
		
		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	//文件发布失败
	public static class PublishResFileDesc{
		public static final int FILE_MISSING = 0;
		public static final int FILE_COPY_SUCCESS = 1;
		public static final int FILE_COPY_FAILED = 2;
		public static final int OTHER_FAILED = 3;
		
		public static final String FILE_MISSING_DESC = "文件不存在，拷贝文件失败";
		public static final String FILE_COPY_SUCCESS_DESC = "拷贝文件成功";
		public static final String FILE_COPY_FAILED_DESC = "拷贝文件失败";
		public static final String OTHER_FAILED_DESC = "其他原因";
		
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(FILE_MISSING, FILE_MISSING_DESC);
			map.putConstant(FILE_COPY_SUCCESS, FILE_COPY_SUCCESS_DESC);
			map.putConstant(FILE_COPY_FAILED, FILE_COPY_FAILED_DESC);
			map.putConstant(OTHER_FAILED, OTHER_FAILED_DESC);
		}
		
		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	public static class FileProcessStatus{
		public static final int FILE_PROCESS_NO = -1;
		public static final int FILE_PROCESS_FAILED = 0;
		public static final int FILE_PROCESS_SUCCESS = 1;
		
		
		public static final String FILE_PROCESS_NO_DESC = "待加工";
		public static final String FILE_PROCESS_FAILED_DESC = "加工失败";
		public static final String FILE_PROCESS_SUCCESS_DESC = "加工成功";
		
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(FILE_PROCESS_NO, FILE_PROCESS_NO_DESC);
			map.putConstant(FILE_PROCESS_FAILED, FILE_PROCESS_FAILED_DESC);
			map.putConstant(FILE_PROCESS_SUCCESS, FILE_PROCESS_SUCCESS_DESC);
			ConstantsRepository.getInstance().register(FileProcessStatus.class, map);
		}
		
		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	
	public static class FileProcessWatermarkStatus{
		public static final int FILE_WMPROCESS_NO = -1;
		public static final int FILE_WMPROCESS_FAILED = 0;
		public static final int FILE_WMPROCESS_SUCCESS = 1;
		
		
		public static final String FILE_WMPROCESS_NO_DESC = "待加水印";
		public static final String FILE_WMPROCESS_FAILED_DESC = "加水印失败";
		public static final String FILE_WMPROCESS_SUCCESS_DESC = "加水印成功";
		
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(FILE_WMPROCESS_NO, FILE_WMPROCESS_NO_DESC);
			map.putConstant(FILE_WMPROCESS_FAILED, FILE_WMPROCESS_FAILED_DESC);
			map.putConstant(FILE_WMPROCESS_SUCCESS, FILE_WMPROCESS_SUCCESS_DESC);
			ConstantsRepository.getInstance().register(FileProcessStatus.class, map);
		}
		
		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
	
	public static class ResPublishStatus{
		public static final int RES_PUBLISH_NO = -1;
		public static final int RES_PUBLISH_FAILED = 0;
		public static final int RES_PUBLISH_SUCCESS = 1;
		
		
		public static final String RES_PUBLISH_NO_DESC = "待发布";
		public static final String RES_PUBLISH_FAILED_DESC = "发布失败";
		public static final String RES_PUBLISH_SUCCESS_DESC = "发布成功";
		
		private static ConstantsMap map;
		static {
			map = new ConstantsMap();
			map.putConstant(RES_PUBLISH_NO, RES_PUBLISH_NO);
			map.putConstant(RES_PUBLISH_FAILED, RES_PUBLISH_FAILED_DESC);
			map.putConstant(RES_PUBLISH_SUCCESS, RES_PUBLISH_SUCCESS_DESC);
			ConstantsRepository.getInstance().register(ResPublishStatus.class, map);
		}
		
		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}
	}
	
}
