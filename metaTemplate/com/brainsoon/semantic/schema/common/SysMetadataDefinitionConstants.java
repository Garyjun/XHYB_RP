/**
 * 
 */
package com.brainsoon.semantic.schema.common;

import java.util.Map;
import java.util.Set;



/**
 * BSRCM应用com.brainsoon.bsrcm.metadata.po.support.SysMetadataDefinitionConstants
 * .java 创建时间：2011-12-8 创建者： liusy SysMetadataDefinitionConstants TODO
 * 
 */
public class SysMetadataDefinitionConstants {
	public static class YsType {
        public static final String YSTYPE1 = "图书有声产品";
        public static final String YSTYPE2 = "图书改编后有声产品";
        public static final String YSTYPE3 = "原创有声产品";
        public static final String YSTYPE1_DESC = "1";
        public static final String YSTYPE2_DESC = "2";
        public static final String YSTYPE3_DESC = "3";
        private static ConstantsMap map;
        static {
            map = new ConstantsMap();
            map.putConstant(YSTYPE1, YSTYPE1_DESC);
            map.putConstant(YSTYPE2, YSTYPE2_DESC);
            map.putConstant(YSTYPE3, YSTYPE3_DESC);
            ConstantsRepository.getInstance().register(YsType.class, map);
        }

        public static String getValueByKey(Object key) {
            if (key == null) {
                return "";
            }
            return (String) map.getDescByValue(key);
        }
    }
    public static class Type {
        public static final String TYPE1 = "1";
        public static final String TYPE2 = "2";
        public static final String TYPE3 = "3";
        public static final String TYPE4 = "4";
        public static final String TYPE5 = "5";
        public static final String TYPE1_DESC = "都柏林元数据";
        public static final String TYPE2_DESC = "基础元数据";
        public static final String TYPE3_DESC = "原始资源元数据";
        public static final String TYPE4_DESC = "标准资源元数据";
        public static final String TYPE5_DESC = "产品元数据";
        private static ConstantsMap map;
        static {
            map = new ConstantsMap();
            map.putConstant(TYPE1, TYPE1_DESC);
            map.putConstant(TYPE2, TYPE2_DESC);
            map.putConstant(TYPE3, TYPE3_DESC);
            map.putConstant(TYPE4, TYPE4_DESC);
            map.putConstant(TYPE5, TYPE5_DESC);
            ConstantsRepository.getInstance().register(Type.class, map);
        }

        public static String getValueByKey(Object key) {
            if (key == null) {
                return "";
            }
            return (String) map.getDescByValue(key);
        }
    }
    public static class JournalType {
        public static final String JOURNALTYPE1 = "1";
        public static final String JOURNALTYPE2 = "2";
        public static final String JOURNALTYPE3 = "3";
        public static final String JOURNALTYPE4 = "4";
        public static final String JOURNALTYPE1_DESC = "主刊";
        public static final String JOURNALTYPE2_DESC = "专刊";
        public static final String JOURNALTYPE3_DESC = "要文选编内参";
        public static final String JOURNALTYPE4_DESC = "作风建设动态参阅内参";
        private static ConstantsMap map;
        static {
            map = new ConstantsMap();
            map.putConstant(JOURNALTYPE1, JOURNALTYPE1_DESC);
            map.putConstant(JOURNALTYPE2, JOURNALTYPE2_DESC);
            map.putConstant(JOURNALTYPE3, JOURNALTYPE3_DESC);
            map.putConstant(JOURNALTYPE4, JOURNALTYPE4_DESC);
            ConstantsRepository.getInstance().register(JournalType.class, map);
        }

        public static String getValueByKey(Object key) {
            if (key == null) {
                return "";
            }
            return (String) map.getDescByValue(key);
        }
    }

    public static class FieldType {
        public static final String FIELDTYPE1 = "11";
        public static final String FIELDTYPE2 = "12";
        public static final String FIELDTYPE3 = "21";
        public static final String FIELDTYPE4 = "22";
        public static final String FIELDTYPE5 = "23";
        public static final String FIELDTYPE6 = "24";
        public static final String FIELDTYPE7 = "31";
        public static final String FIELDTYPE8 = "32";
        public static final String FIELDTYPE9 = "33";
        public static final String FIELDTYPE10 = "41";
        public static final String FIELDTYPE11 = "51";        
        public static final String FIELDTYPE1_DESC = "单文本";
        public static final String FIELDTYPE2_DESC = "文本域";
        public static final String FIELDTYPE3_DESC = "单选按钮";
        public static final String FIELDTYPE4_DESC = "复选按钮";
        public static final String FIELDTYPE5_DESC = "下拉单选";
        public static final String FIELDTYPE6_DESC = "下拉多选";
        public static final String FIELDTYPE7_DESC = "日期(yyyy-MM-dd)";
        public static final String FIELDTYPE8_DESC = "日期(yyyy-MM-dd HH:mm:ss)";
        public static final String FIELDTYPE9_DESC = "日期(H:m:s)";
        public static final String FIELDTYPE10_DESC = "URL";
        public static final String FIELDTYPE11_DESC = "分类树ID";

        private static ConstantsMap map;
        static {
            map = new ConstantsMap();
            map.putConstant(FIELDTYPE1, FIELDTYPE1_DESC);
            map.putConstant(FIELDTYPE2, FIELDTYPE2_DESC);
            map.putConstant(FIELDTYPE3, FIELDTYPE3_DESC);
            map.putConstant(FIELDTYPE4, FIELDTYPE4_DESC);
            map.putConstant(FIELDTYPE5, FIELDTYPE5_DESC);
            map.putConstant(FIELDTYPE6, FIELDTYPE6_DESC);
            map.putConstant(FIELDTYPE7, FIELDTYPE7_DESC);
            map.putConstant(FIELDTYPE8, FIELDTYPE8_DESC);
            map.putConstant(FIELDTYPE9, FIELDTYPE9_DESC);
            map.putConstant(FIELDTYPE10, FIELDTYPE10_DESC);
            map.putConstant(FIELDTYPE11, FIELDTYPE11_DESC);
            ConstantsRepository.getInstance().register(FieldType.class, map);
        }

        public static String getValueByKey(Object key) {
            if (key == null) {
                return "";
            }
            return (String) map.getDescByValue(key);
        }
        public static Set getConstantsMap(){
        	return map.getConstantsSet();
        }
    }

    public static class AllowNull {
        public static final String ALLOWNULL1 = "true";
        public static final String ALLOWNULL2 = "false";
        public static final String ALLOWNULL1_DESC = "允许";
        public static final String ALLOWNULL2_DESC = "不允许";
        private static ConstantsMap map;
        static {
            map = new ConstantsMap();
            map.putConstant(ALLOWNULL2, ALLOWNULL2_DESC);
            map.putConstant(ALLOWNULL1, ALLOWNULL1_DESC);
            ConstantsRepository.getInstance().register(AllowNull.class, map);
        }

        public static String getValueByKey(Object key) {
            if (key == null) {
                return "";
            }
            return (String) map.getDescByValue(key);
        }
    }
    
    public static class PublicType {
        public static final String PUBLICTYPE1 = "true";
        public static final String PUBLICTYPE2 = "false";
        public static final String PUBLICTYPE1_DESC = "是";
        public static final String PUBLICTYPE2_DESC = "否";
        private static ConstantsMap map;
        static {
            map = new ConstantsMap();
            map.putConstant(PUBLICTYPE1, PUBLICTYPE1_DESC);
            map.putConstant(PUBLICTYPE2, PUBLICTYPE2_DESC);
            ConstantsRepository.getInstance().register(PublicType.class, map);
        }

        public static String getValueByKey(Object key) {
            if (key == null) {
                return "";
            }
            return (String) map.getDescByValue(key);
        }
    }

    public static class ValidateModel {
        public static final String VALIDATEMODEL1 = "1";
        public static final String VALIDATEMODEL2 = "2";
        public static final String VALIDATEMODEL3 = "3";
        public static final String VALIDATEMODEL4 = "4";
        public static final String VALIDATEMODEL5 = "5";
        public static final String VALIDATEMODEL6 = "6";
        public static final String VALIDATEMODEL7 = "7";
        public static final String VALIDATEMODEL1_DESC = "不校验";
        public static final String VALIDATEMODEL2_DESC = "数字";
        public static final String VALIDATEMODEL3_DESC = "字母";
        public static final String VALIDATEMODEL4_DESC = "数字及字母";
        public static final String VALIDATEMODEL5_DESC = "汉字";
        public static final String VALIDATEMODEL6_DESC = "邮箱";
        public static final String VALIDATEMODEL7_DESC = "url或资源仓库地址";
        private static ConstantsMap map;
        static {
            map = new ConstantsMap();
            map.putConstant(VALIDATEMODEL1, VALIDATEMODEL1_DESC);
            map.putConstant(VALIDATEMODEL2, VALIDATEMODEL2_DESC);
            map.putConstant(VALIDATEMODEL3, VALIDATEMODEL3_DESC);
            map.putConstant(VALIDATEMODEL4, VALIDATEMODEL4_DESC);
            map.putConstant(VALIDATEMODEL5, VALIDATEMODEL5_DESC);
            map.putConstant(VALIDATEMODEL6, VALIDATEMODEL6_DESC);
            map.putConstant(VALIDATEMODEL7, VALIDATEMODEL7_DESC);
            ConstantsRepository.getInstance().register(ValidateModel.class, map);
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

    public static class Status {
        public static final String STATUS1 = "true";
        public static final String STATUS2 = "false";
        public static final String STATUS1_DESC = "启用";
        public static final String STATUS2_DESC = "不启用";
        private static ConstantsMap map;
        static {
            map = new ConstantsMap();
            map.putConstant(STATUS2, STATUS2_DESC);
            map.putConstant(STATUS1, STATUS1_DESC);
            ConstantsRepository.getInstance().register(Status.class, map);
        }

        public static String getValueByKey(Object key) {
            if (key == null) {
                return "";
            }
            return (String) map.getDescByValue(key);
        }
    }

    public static class ViewPriority {
        public static final String VIEWPRIORITY1 = "1";
        public static final String VIEWPRIORITY2 = "2";
        public static final String VIEWPRIORITY3 = "3";
        public static final String VIEWPRIORITY1_DESC = "仅详细页展示";
        public static final String VIEWPRIORITY2_DESC = "精简信息展示        ";
        public static final String VIEWPRIORITY3_DESC = "列表页展示 ";
        private static ConstantsMap map;
        static {
            map = new ConstantsMap();
            map.putConstant(VIEWPRIORITY1, VIEWPRIORITY1_DESC);
            map.putConstant(VIEWPRIORITY2, VIEWPRIORITY2_DESC);
            map.putConstant(VIEWPRIORITY3, VIEWPRIORITY3_DESC);
            ConstantsRepository.getInstance().register(ViewPriority.class, map);
        }

        public static String getValueByKey(Object key) {
            if (key == null) {
                return "";
            }
            return (String) map.getDescByValue(key);
        }
    }

    public static class QueryModel {
        public static final String QUERYMODEL1 = "1";
        public static final String QUERYMODEL2 = "2";
        public static final String QUERYMODEL3 = "3";
        public static final String QUERYMODEL4 = "4";
        public static final String QUERYMODEL1_DESC = "不用于查询 ";
        public static final String QUERYMODEL2_DESC = "完全匹配查询";
        public static final String QUERYMODEL3_DESC = "模糊匹配查询";
        public static final String QUERYMODEL4_DESC = "时间区间查询        ";
        private static ConstantsMap map;
        static {
            map = new ConstantsMap();
            map.putConstant(QUERYMODEL1, QUERYMODEL1_DESC);
            map.putConstant(QUERYMODEL2, QUERYMODEL2_DESC);
            map.putConstant(QUERYMODEL3, QUERYMODEL3_DESC);
            map.putConstant(QUERYMODEL4, QUERYMODEL4_DESC);
            ConstantsRepository.getInstance().register(QueryModel.class, map);
        }

        public static String getValueByKey(Object key) {
            if (key == null) {
                return "";
            }
            return (String) map.getDescByValue(key);
        }
    }
    
    public static class ValidatePattern{
        public static final String VALIDATEPATTERN1 = "1";
        public static final String VALIDATEPATTERN2 = "2";
        public static final String VALIDATEPATTERN3 = "3";
        public static final String VALIDATEPATTERN4 = "4";
        public static final String VALIDATEPATTERN5 = "5";
        public static final String VALIDATEPATTERN6 = "6";
        public static final String VALIDATEPATTERN7 = "7";
        public static final String VALIDATEPATTERN1_DESC = "不效验";
        public static final String VALIDATEPATTERN2_DESC = "/^\\d+$/";
        public static final String VALIDATEPATTERN3_DESC = "/^[a-zA-Z]+$/";
        public static final String VALIDATEPATTERN4_DESC = "/^[a-zA-Z0-9]+$/";
        public static final String VALIDATEPATTERN5_DESC = "/^[\\u4e00-\\u9fa5]+$/";
        public static final String VALIDATEPATTERN6_DESC = "/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$/";
        public static final String VALIDATEPATTERN7_DESC = "/^(http:\\/\\/.*)*(.*DS.*)*$/";
        private static ConstantsMap map;
        static {
            map = new ConstantsMap();
            map.putConstant(VALIDATEPATTERN1, VALIDATEPATTERN1_DESC);
            map.putConstant(VALIDATEPATTERN2, VALIDATEPATTERN2_DESC);
            map.putConstant(VALIDATEPATTERN3, VALIDATEPATTERN3_DESC);
            map.putConstant(VALIDATEPATTERN4, VALIDATEPATTERN4_DESC);
            map.putConstant(VALIDATEPATTERN5, VALIDATEPATTERN5_DESC);
            map.putConstant(VALIDATEPATTERN6, VALIDATEPATTERN6_DESC);
            map.putConstant(VALIDATEPATTERN7, VALIDATEPATTERN7_DESC);
            ConstantsRepository.getInstance()
                    .register(ValidatePattern.class, map);
        }

        public static String getValueByKey(Object key) {
            if (key == null) {
                return "";
            }
            return (String) map.getDescByValue(key);
        }
    }
    
    public static class Identifier {
        public static final String STATUS0 = "0";
        public static final String STATUS1 = "1";
        public static final String STATUS2 = "2";
        public static final String STATUS3 = "3";
        public static final String STATUS4 = "4";
        public static final String STATUS5 = "5";
        public static final String STATUS6 = "6";
        public static final String STATUS7 = "7";
        public static final String STATUS8 = "8";
        public static final String STATUS9 = "9";
        public static final String STATUS10 = "10";
        public static final String STATUS0_DESC = "无";
        public static final String STATUS1_DESC = "唯一性标识";
        public static final String STATUS2_DESC = "简介";
        public static final String STATUS3_DESC = "书名";
        public static final String STATUS4_DESC = "作者";
        public static final String STATUS5_DESC = "封面";
        public static final String STATUS6_DESC = "ISBN";
        public static final String STATUS7_DESC = "出版时间";
        public static final String STATUS8_DESC = "主题";
        public static final String STATUS9_DESC = "出版方";
        public static final String STATUS10_DESC = "标签";
        private static ConstantsMap map;
        static {
            map = new ConstantsMap();
            map.putConstant(STATUS0, STATUS0_DESC);
            map.putConstant(STATUS1, STATUS1_DESC);
            map.putConstant(STATUS2, STATUS2_DESC);
            map.putConstant(STATUS3, STATUS3_DESC);
            map.putConstant(STATUS4, STATUS4_DESC);
            map.putConstant(STATUS5, STATUS5_DESC);
            map.putConstant(STATUS6, STATUS6_DESC);
            map.putConstant(STATUS7, STATUS7_DESC);
            map.putConstant(STATUS8, STATUS8_DESC);
            map.putConstant(STATUS9, STATUS9_DESC);
            map.putConstant(STATUS10, STATUS10_DESC);
            ConstantsRepository.getInstance().register(Identifier.class, map);
        }

        public static String getValueByKey(Object key) {
            if (key == null) {
                return "";
            }
            return (String) map.getDescByValue(key);
        }
    }
    public static class OpenBlur {
        public static final String TYPE0 = "0";
        public static final String TYPE1 = "1";
        public static final String TYPE3 = "3";
        public static final String TYPE0_DESC = "不开启";
        public static final String TYPE1_DESC = "开启";
        private static ConstantsMap map;
        static {
            map = new ConstantsMap();
            map.putConstant(TYPE0, TYPE0_DESC);
            map.putConstant(TYPE1, TYPE1_DESC);
            ConstantsRepository.getInstance().register(OpenBlur.class, map);
        }

        public static String getValueByKey(Object key) {
            if (key == null) {
                return "";
            }
            return (String) map.getDescByValue(key);
        }
    }
    
    public static class ReadOnly {
        public static final String TYPE0 = "0";
        public static final String TYPE1 = "1";
        public static final String TYPE0_DESC = "否";
        public static final String TYPE1_DESC = "是";
        private static ConstantsMap map;
        static {
            map = new ConstantsMap();
            map.putConstant(TYPE0, TYPE0_DESC);
            map.putConstant(TYPE1, TYPE1_DESC);
            ConstantsRepository.getInstance().register(ReadOnly.class, map);
        }

        public static String getValueByKey(Object key) {
            if (key == null) {
                return "";
            }
            return (String) map.getDescByValue(key);
        }
    }
}
