package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;

@Entity
@Table(name = "metaDataCustom")
public class MetaData extends BaseHibernateObject {
	    private static final long serialVersionUID = 4858001351954128087L;
	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name = "id", nullable = false)
	    private Long id;
	    @Column
	    private Long pid;
	    @Column
	    private String version;
	    @Column
	    private String metadataId;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Long getPid() {
			return pid;
		}
		public void setPid(Long pid) {
			this.pid = pid;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public String getMetadataId() {
			return metadataId;
		}
		public void setMetadataId(String metadataId) {
			this.metadataId = metadataId;
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
