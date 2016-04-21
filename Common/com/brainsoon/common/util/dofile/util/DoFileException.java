package com.brainsoon.common.util.dofile.util;

/**
 * 
 * @ClassName: SystemException 
 * @Description:  
 * @author tanghui 
 * @date 2014-04-23 下午15：13：26 
 *
 */
public class DoFileException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DoFileException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DoFileException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public DoFileException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DoFileException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
