package com.brainsoon.jbpm.constants;

import java.util.HashMap;
import java.util.Map;

public class ProcessConstants {
	
	public static final String APPROVE = "approve";// 审批通过
	public static final String APPROVE_DESC = "通过";// 审批通过
	public static final String REJECT = "reject";// 审批驳回
	public static final String REJECT_DESC = "驳回";// 审批驳回
	public static final String SUBMIT = "提交";// 审批驳回
	public static final String END = "end";// 结束流程
	public static final String MAIN_TASKID = "mainTaskId";// 主任务Id变量名
	public static final String SUB_TASKID = "subTaskId";// 子任务任务Id变量名
	public static final String BUSI_DESC = "BUSI_DESC";// 业务描述
	public static final String APPLY_USER = "APPLY_USER";// 提交人

	public static class WFType {
		public static final String ORES_CHECK = "oresCheck";
		public static final String ORES_CHECK_CHECK_DESC = "原始资源审核";
		public static final String BRES_CHECK = "bresCheck";
		public static final String BRES_CHECK_CHECK_DESC = "标准资源审核";
		public static final String PRES_CHECK = "presCheck";
		public static final String PRES_CHECK_CHECK_DESC = "聚合资源审核";
		public static final String ORDER_CHECK = "orderCheck";
		public static final String ORDER_CHECK_DESC = "需求单审核";
		public static final String SUBJECT_CHECK = "subjectCheck";
		public static final String SUBJECT_CHECK_DESC = "主题库审核";

		// 出版资源审核流程
		public static final String PUB_ORES_CHECK = "pubresCheck";
		public static final String PUB_ORES_CHECK_DESC = "资源管理";
		public static final String PUB_ORDER_CHECK = "orderCheck";
		public static final String PUB_ORDER_CHECK_DESC = "需求单管理";
		public static final String PUB_SUBJECT_CHECK = "subjectCheck";
		public static final String PUB_SUBJECT_CHECK_DESC = "主题库管理";

		public static final Map<String, String> map;
		static {
			map = new HashMap<String, String>();
			map.put(ORES_CHECK, ORES_CHECK_CHECK_DESC);
			map.put(BRES_CHECK, BRES_CHECK_CHECK_DESC);
			map.put(PRES_CHECK, PRES_CHECK_CHECK_DESC);
			map.put(ORDER_CHECK, ORDER_CHECK_DESC);
			map.put(SUBJECT_CHECK, SUBJECT_CHECK_DESC);

			map.put(PUB_ORES_CHECK, PUB_ORES_CHECK_DESC);
			map.put(PUB_ORDER_CHECK, PUB_ORDER_CHECK_DESC);
			map.put(PUB_SUBJECT_CHECK, PUB_SUBJECT_CHECK_DESC);
		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return map.get(key);
		}
	}
}
