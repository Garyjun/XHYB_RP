package com.brainsoon.common.util.date;

import org.apache.commons.lang.StringUtils;
/**
 * 生成日期控件
 * @author zhangpeng
 *
 */
public class DatePickerUtil {
	/**
	 * my97picker日期控件(注意自定义事件onFocus从my97官网拷出来的代码的单斜杠用双斜杠替换)
	 * @param propertyName   属性名
	 * @param propertyValue  属性值
	 * @param cssClass       控件类型
	 * @param style          控件样式
	 * @param onFocus        点击事件 
	 * @param isReadOnly     是否只读
	 * @param dateFmt        日期格式（yyyy-MM-dd，yyyy-MM，yyyy-MM-dd HH:mm:ss，yyyy-MM-dd HH:mm,H:m:s,MMMM d,yyyy）
	 * @param showMode       主要是控制日期的格式
	 * @return
	 */
	public static String createDatePicker(String propertyName,String propertyId,String propertyValue,String cssClass,
			String style,String onFocus,boolean isReadOnly,String dateFmt,String showMode,String callBackJS){
		StringBuffer textBuf = new StringBuffer(50);
		textBuf.append("<input type=\"text\"")
			   .append(" name=\"").append(propertyName).append("\"")
			   .append(" id=\"").append(propertyId).append("\"");
        textBuf.append(" value=\"").append(propertyValue).append("\"");
		
		//设置默认样式为：Wdate
//		if(!cssClass.equals("Wdate")){
//			cssClass = "Wdate";
//		}
		if(cssClass == null || "".equals(cssClass)){
			textBuf.append(" class=\"form-control Wdate\"");
		}else{
			textBuf.append(" class=\"").append(cssClass).append("\"");
		}
		//拼style属性
		if(StringUtils.isNotBlank(style)){
			textBuf.append(" style=\"").append(style).append("\"");
		}else{
			textBuf.append(" style=\"").append(buildWidth(dateFmt)).append("\"");
		}
		//拼onFocus属性
		textBuf.append(getOnFocusStr(onFocus,isReadOnly,dateFmt,showMode));
		if(StringUtils.isNotBlank(callBackJS)){
			textBuf.append(" onclick=\"").append(callBackJS).append("\"");
		}
		textBuf.append("/>");
		
		return textBuf.toString();
	}
	
	private static String getOnFocusStr(String onFocus,boolean isReadOnly,String dateFmt,String showMode){
		StringBuffer resultStr = new StringBuffer();
		//onFocus = "var d5222=$dp.$('d5222')";
		if(StringUtils.isNotBlank(onFocus) && onFocus.contains("WdatePicker")){
			onFocus = onFocus.substring(0,onFocus.length()-2).trim();
			resultStr.append(onFocus);
			if(!onFocus.contains("dateFmt")){
				resultStr.append(",dateFmt:'"+ buildDateFmt(dateFmt,showMode) +"'");
			}
			if(!onFocus.contains("readOnly")){
				if(isReadOnly){
					resultStr.append(",readOnly:true");
				}else{
					resultStr.append(",readOnly:false");
				}
			}
		}else{
			if(StringUtils.isNotBlank(onFocus)){
				onFocus = onFocus.trim();
				resultStr.append(onFocus);
				if(!onFocus.substring(onFocus.length()-1, onFocus.length()).equals(";")){
					resultStr.append(";");
			    }
			}
			resultStr.append("WdatePicker({dateFmt:'"+ dateFmt +"'");
			if(isReadOnly){
				resultStr.append(",readOnly:true");
			}else{
				resultStr.append(",readOnly:false");
			}
		 }
		resultStr.append(",isShowClear:true");
		//System.out.println("result:" + " onFocus=\"" + resultStr.toString() + "})\"");
		return " onFocus=\"" + resultStr.toString() + "})\"";
	 }
	private static String buildDateFmt(String dateFmt,String showMode) {
		if(StringUtils.isNotBlank(showMode)){
			if(showMode.equals("1")){
				dateFmt = "yyyy-MM-dd";
			}else if(showMode.equals("2")){
				dateFmt = "yyyy-MM";
			}else if(showMode.equals("3")){
				dateFmt = "yyyy-MM-dd HH:mm:ss";
			}else if(showMode.equals("4")){
				dateFmt = "yyyy-MM-dd HH:mm";
			}else if(showMode.equals("5")){
				dateFmt = "H:m:s";
			}else if(showMode.equals("6")){
				dateFmt = "MMMM d,yyyy";
			}else if(showMode.equals("7")){
				dateFmt = "yyyy";
			}else{
				dateFmt = "yyyy-MM-dd";
			}
		}
		return dateFmt;
	}
	private static String buildWidth(String dateFmt) {
		String width = "";
		if("yyyy-MM-dd".equals(dateFmt)){
			width = "width:105px;";
		}else if("yyyy-MM".equals(dateFmt)){
			width = "width:65px;";
		}else if("yyyy-MM-dd HH:mm:ss".equals(dateFmt)){
			width = "width:135px;";
		}else if("yyyy-MM-dd HH:mm".equals(dateFmt)){
			width = "width:125px;";
		}else if("H:m:s".equals(dateFmt)){
			width = "width:40px;";
		}else if("MMMM d,yyyy".equals(dateFmt)){
			width = "width:60px;";
		}else if("yyyy".equals(dateFmt)){
			width = "width:50px;";
		}else{
			width = "width:85px;";
		}
		return width;
	}
}
