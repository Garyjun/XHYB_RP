package com.brainsoon.statistics.po;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.statistics.service.IResourceOfWordService;
import com.brainsoon.system.service.IUserService;

/**
 * 资源的实体类
 * @author lenovo
 *
 */
@Entity
@Table(name = "sys_resource_word")
public class RespsOfResourceWord extends BaseHibernateObject{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Long id;
	
	@Column
	private String resourceId;     //资源id

	@Column(name = "resource_name")
	private String resourceName;   //资源名称
	
	@Column
	private String status = "未过滤";   //是否已经过滤敏感词
	
	@Column(name = "last_time")
	private Date lastTime;    //资源最后更新时间
	
	@Column
	private String resourceTypeId;   //所属于的资源的类型id
	
	@Column
	private String haveWord;
	
	@Column
	private String resourceWord;   //包含的敏感词

	

	@Transient
	private String isHave;   //判断该资源下的文件是否包含敏感词
	
	private IResourceOfWordService getResService(){
		IResourceOfWordService resourceOfWordService = null;
		try {
			resourceOfWordService = (IResourceOfWordService) BeanFactoryUtil.getBean("resourceOfWordService");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resourceOfWordService;
	} 
	
	
	/**
	 * 列表页面展示资源下的文件是否包含敏感词
	 * @return
	 */
	public String getIsHave(){
		String result = getResService().findWord(resourceId);
		return result;
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastName(Date lastTime) {
		this.lastTime = lastTime;
	}

	public String getResourceTypeId() {
		return resourceTypeId;
	}

	public void setResourceTypeId(String resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}
	
	public String getHaveWord() {
		return haveWord;
	}


	public void setHaveWord(String haveWord) {
		this.haveWord = haveWord;
	}
	
	@Override
	public String getObjectDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getResourceWord() {
		return resourceWord;
	}


	public void setResourceWord(String resourceWord) {
		this.resourceWord = resourceWord;
	}

}
