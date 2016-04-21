package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.brainsoon.common.po.BaseHibernateObject;
/**
 * BSRCM应用com.brainsoon.bsrcm.dcore.po.SysMetadataType.java 创建时间：2011-12-7 创建者：
 * liusy DcType TODO
 * 
 */
@Entity
@Table(name = "running_number")
public class RunNumber extends BaseHibernateObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //编码名称
    @Column
    private String oresNums;
    @Column
    private String publishType;
    

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOresNums() {
		return oresNums;
	}

	public void setOresNums(String oresNums) {
		this.oresNums = oresNums;
	}

	public String getPublishType() {
		return publishType;
	}

	public void setPublishType(String publishType) {
		this.publishType = publishType;
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

}
