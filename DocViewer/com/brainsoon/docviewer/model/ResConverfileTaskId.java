package com.brainsoon.docviewer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.brainsoon.common.po.BaseHibernateObject;


/**
 * 
 * @ClassName: ResConverfileTaskId 
 * @Description:  
 * 建表SQL:
 
	 CREATE TABLE `res_converfile_task_id` (
	  `id` int(12) NOT NULL,
	  `ipAddr` varchar(128) DEFAULT NULL,
	  `sqlparam` varchar(1500) DEFAULT 'id > 0' COMMENT 'sql参数',
	  `status` tinyint(2) DEFAULT '1' COMMENT '状态 1：运行中 0：停用',
	  `updateTime` varchar(50) DEFAULT NULL COMMENT '最后更新时间',
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8



 * @author tanghui 
 * @date 2014-6-11 下午3:54:09 
 *
 */
@Entity
@Table(name = "res_converfile_task_id")
public class ResConverfileTaskId extends BaseHibernateObject  implements Comparable{

	// Fields
	private static final long serialVersionUID = -1059078424494091126L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Long id;
	@Column
	private String ipAddr;     //ip地址
	@Column
	private Integer status = 0;    //运行状态 1：运行中  0：停用
	@Column
	private String sqlparam = "id > 0";      //sql参数
	@Column
	private String updateTime;   //最后更新时间
	@Column
	private String singleGroupIdParmNumber;
	@Column
	private String groupIdParmNumber;
	// Constructors

	/** default constructor */
	public ResConverfileTaskId() {}
	
	
	public ResConverfileTaskId(String ipAddr, Integer status, String sqlparam,String updateTime) {
		super();
		this.ipAddr = ipAddr;
		this.status = status;
		this.sqlparam = sqlparam;
		this.updateTime = updateTime;
	}




	public ResConverfileTaskId(Long id, String ipAddr, Integer status,
			String sqlparam, String updateTime) {
		super();
		this.id = id;
		this.ipAddr = ipAddr;
		this.status = status;
		this.sqlparam = sqlparam;
		this.updateTime = updateTime;
	}



	//比较
	@Override
	public int compareTo(Object obj){
		ResConverfileTaskId resConverfileTaskId = (ResConverfileTaskId)obj;
		return this.id.compareTo(resConverfileTaskId.getId());
    }


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getIpAddr() {
		return ipAddr;
	}


	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}


	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}


	public String getSqlparam() {
		return sqlparam;
	}



	public void setSqlparam(String sqlparam) {
		this.sqlparam = sqlparam;
	}


	public String getUpdateTime() {
		return updateTime;
	}


	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}


	public String getSingleGroupIdParmNumber() {
		return singleGroupIdParmNumber;
	}


	public void setSingleGroupIdParmNumber(String singleGroupIdParmNumber) {
		this.singleGroupIdParmNumber = singleGroupIdParmNumber;
	}


	public String getGroupIdParmNumber() {
		return groupIdParmNumber;
	}


	public void setGroupIdParmNumber(String groupIdParmNumber) {
		this.groupIdParmNumber = groupIdParmNumber;
	}


	@Override
	public String getObjectDescription() {
		// tanghui Auto-generated method stub
		return null;
	}


	@Override
	public String getEntityDescription() {
		// tanghui Auto-generated method stub
		return null;
	}
	
}