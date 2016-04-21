package com.brainsoon.common.exception;

/**
 * 
 * @ClassName: BusinessException 
 * @Description:  
 * spring声明式事务管理默认对非检查型异常和运行时异常进行事务回滚，而对检查型异常则不进行回滚操作。
	什么是检查型异常，什么又是非检查型异常呢？
	最简单的判断点有两个：
	1.继承自runtimeexception或error的是非检查型异常，而继承自exception的则是检查型异常
	（当然，runtimeexception本身也是exception的子类）。
	2.对非检查型类异常可以不用捕获，而检查型异常则必须用try语句块进行处理或者把异常交给上级方法处理总之就是必须写代码处理它。
	所以必须在service捕获异常，然后再次抛出，这样事务方才起效。
	3.注意：spring 配置中的rollback-for=“BusinessException“的配置
	4.通过以上两点可知,让Spring管理事务处理（否则不会回滚）：
    1>>不捕获异常
    2>>在DAO层捕与不捕都行,在Service层捕获异常后抛出unChecked异常或自定义异常(要继承unChecked),
          然后在Web层捕获到此异常
 * @author tanghui 
 * @date 2013-8-16 上午9:14:49 
 *
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String code;

	public BusinessException() {
		super();
	}

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(String code, String message) {
		super(message);
		this.code = code;
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
