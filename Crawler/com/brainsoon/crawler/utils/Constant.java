package com.brainsoon.crawler.utils;

import java.util.Map;

import com.brainsoon.appframe.support.ConstantsMap;
import com.brainsoon.appframe.support.ConstantsRepository;

public class Constant {
	public static class TaskStatus {
		public static final String STOPED = "0";
		public static final String STOPED_DESC = "未启动";
		public static final String RUNING = "";
		public static final String RUNING_DESC = "运行中";
		private static ConstantsMap map;

		static {
			map = new ConstantsMap();
			map.putConstant(STOPED, STOPED_DESC);
			map.putConstant(RUNING, RUNING_DESC);
			ConstantsRepository.getInstance().register(TaskStatus.class, map);

		}

		public static String getValueByKey(Object key) {
			if (key == null) {
				return "";
			}
			return (String) map.getDescByValue(key);
		}

		public static Map getMap() {
			return map.getEntryMap();
		}
	}
}
