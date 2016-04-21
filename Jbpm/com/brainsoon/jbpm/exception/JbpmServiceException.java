package com.brainsoon.jbpm.exception;

import com.brainsoon.common.exception.BusinessException;

public class JbpmServiceException extends BusinessException {

	private static final long serialVersionUID = 1L;

	public JbpmServiceException() {
		super();
	}

	public JbpmServiceException(String message) {
		super(message);
	}

	public JbpmServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public JbpmServiceException(Throwable cause) {
		super(cause);
	}
}
