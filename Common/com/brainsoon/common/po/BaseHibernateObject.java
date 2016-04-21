package com.brainsoon.common.po;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BaseHibernateObject implements Serializable {

	private static final long serialVersionUID = 7109557417281190114L;
	protected static final Log logger = LogFactory.getLog(BaseHibernateObject.class);


	/**
	 * 返回该业务实体对象的描述信息，属于动态信息，子类必须实现此方法
	 * 
	 * @return
	 */
	public abstract String getObjectDescription();

	/**
	 * 返回该业务实体类的描述信息，属于静态信息，子类必须实现此方法
	 * 
	 * @return
	 */
	public abstract String getEntityDescription();
	
}
