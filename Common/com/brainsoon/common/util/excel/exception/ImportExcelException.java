/*
 * author: jiaoyongjie
 * date:   2009年1月11号
 */
package com.brainsoon.common.util.excel.exception;

import java.util.*;


/**
 * 导入excel异常基类
 *
 */
public class ImportExcelException extends Exception {
	
	private List exceptions = new ArrayList();
	private boolean hasError=false;
	
	public void addException(Exception e){
		
		this.exceptions.add(e);
		
		
	}
	
	public ImportExcelException(){
		super();
	}
	
	public ImportExcelException(String message){
		super(message);
	
	}
	
	public ImportExcelException(Throwable cause){
		super(cause);
	}
	
	public ImportExcelException(String message, Throwable cause){
		super(message, cause);
	}

	public List getExceptions() {
		return exceptions;
	}

	public void setExceptions(List exceptions) {
		this.exceptions = exceptions;
	}

	public boolean isHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}
	
}
