/*
 * author: jiaoyongjie
 * date:   2014年6月23号
 */
package com.brainsoon.common.util.excel;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brainsoon.common.util.date.DateUtil;
import jxl.*;

public class ExcelImportUtil {
	protected  Logger logger = LoggerFactory.getLogger(getClass());
	public static String datePattern = "yyyy-MM-dd";
	private static final String errDate="3000-01-01";
	private Map<String, String> poExcelMap;
	public ExcelImportUtil(Map<String, String> poExcelMap){
		this.poExcelMap=poExcelMap;
	}
	/**
	 * 获取po属性名
	 * @param cell
	 * @return
	 */
	public String getPropertyNameByCellHeader(Cell cell){
		if (poExcelMap == null) {
			return "";
		}
		if (cell == null) {
			return "";
		}
		String value = cell.getContents().trim();

		if (StringUtils.isBlank(value)) {
			return "";
		}
		for (String key : poExcelMap.keySet()) {
			if (value.equals(poExcelMap.get(key))) {
				return key;
			}
		}
		return "";
	}
	/**
	 *  给类的属性赋值
	 * @param instance
	 * @param propertyName
	 * @param valCell
	 */
	public void setPropertyVal(Object instance, String propertyName,Cell valCell) {
		try {
			Object value = null;
			if (StringUtils.containsIgnoreCase(propertyName, "date")&&StringUtils.isNotBlank(valCell.getContents().trim())) {
					if(valCell.getType() == CellType.DATE){
						DateCell dateCell = (DateCell)valCell;
						value = dateCell.getDate();
					}else if(GenericValidator.isDate(valCell.getContents().trim(), datePattern, false)){
						value = DateUtil.convertStringToDate(valCell.getContents().trim(), datePattern);
					}else{
						 value=DateUtil.convertStringToDate(errDate);
					}

			} else if (valCell.getType() == CellType.NUMBER || valCell.getType() == CellType.NUMBER_FORMULA) {
				NumberCell numberCell = (NumberCell)valCell;
				value = new Double(numberCell.getValue());
			}else {
				value = valCell.getContents().trim();
			}
			if (value != null) {
				if(instance instanceof Map){
					((Map) instance).put(propertyName, valCell.getContents().trim());
				}else{
					BeanUtils.setProperty(instance, propertyName, value);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			
		}
	}
	
	
	
}
