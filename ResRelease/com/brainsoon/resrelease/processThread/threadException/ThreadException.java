package com.brainsoon.resrelease.processThread.threadException;

/**
 * @ClassName: ThreadException
 * @Description: TODO
 * @author xiehewei
 * @date 2015年1月15日 下午5:29:32
 *
 */
public class ThreadException extends Exception {

	private static final long serialVersionUID = -3948061788740052023L;

	public enum ErrorLevel
	{
		WARN("警告"),
		ERROR("错误");
		
		private String desc;
		private ErrorLevel(String desc)
		{
			this.desc = desc;
		}
		public String getDesc()
		{
			return this.desc;
		}
	}
	
	private ErrorLevel level;
	
	public ThreadException(ErrorLevel level, String msg) {
		super(msg);		
		this.level = level;
	}
	
	public ThreadException(ErrorLevel level, String msg, Throwable cause) {
		super(msg,cause);		
		this.level = level;
	}
	
	public ErrorLevel getErrorLevel() {
		return this.level;
	}
	
	@Override
	public String getMessage() {
		StringBuilder msg = new StringBuilder(100);
		msg.append("ERROR_LEVEL:").append(getErrorLevel().getDesc())
			.append("  ERROR_MSG:").append(super.getMessage());
		
		return msg.toString();
	}
}
