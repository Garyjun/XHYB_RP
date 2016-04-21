package com.brainsoon.docviewer.model;

import java.util.Date;

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
 * @ClassName: ResConverfileTask
 * @Description:
 * 建表SQL:

CREATE TABLE `res_converfile_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `fileId` varchar(100) DEFAULT NULL COMMENT '文件id',
  `status` tinyint(2) DEFAULT '0' COMMENT '0：待转换 1：转换中 2：转换成功 3：转换失败',
  `retryNum` int(11) DEFAULT '0' COMMENT '重试次数（针对失败的记录）',
  `resId` varchar(100) DEFAULT NULL COMMENT '资源id',
  `srcPath` varchar(2000) DEFAULT NULL COMMENT '源文件路径（相对）',
  `tarPath` varchar(2000) DEFAULT NULL COMMENT '目标文件路径（相对）',
  `priority` tinyint(2) DEFAULT '0' COMMENT '转换的优先级 0：默认 1：立即开始 2：低',
  `describes` text COMMENT '转换后的描述',
  `fileType` varchar(20) DEFAULT NULL COMMENT '文件格式',
  `doHasType` varchar(20) DEFAULT NULL COMMENT '处理类型：1，抽取图片，2抽取文本',
  `doResultType` varchar(20) DEFAULT NULL COMMENT '处理类型：0，转换 1，抽取图片，2抽取文本',
  `imgStauts` tinyint(2) DEFAULT '0' COMMENT '抽取图片状态 0：待抽取 1：抽取中 2：抽取成功 3：抽取失败',
  `txtStauts` tinyint(2) DEFAULT '0' COMMENT '抽取文本状态 0：待抽取 1：抽取中 2：抽取成功 3：抽取失败',
  `timestamp` varchar(50) DEFAULT NULL COMMENT '时间戳',
  `txtStr` text COMMENT '抽取的文本内容',
  `imgDoStauts` tinyint(4) DEFAULT '0' COMMENT '执行保存图片状态 0：待抽取 1：抽取中 2：抽取成功 3：抽取失败',
  `txtDoStauts` tinyint(4) DEFAULT '0' COMMENT '执行保存文本状态 0：待抽取 1：抽取中 2：抽取成功 3：抽取失败',
  `platformId` int(11) DEFAULT '1' COMMENT '教育:1 图书：2',
  `createTime` datetime DEFAULT NULL COMMENT '文件创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '文件修改时间',
  PRIMARY KEY (`id`),
  KEY `srcPath_index` (`srcPath`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8


 * @author tanghui
 * @date 2014-6-11 下午3:54:09
 *
 */
@Entity
@Table(name = "res_converfile_task")
public class ResConverfileTask extends BaseHibernateObject  implements Comparable{

	// Fields
	private static final long serialVersionUID = -1099178424494091126L;
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Long id;
	@Column
	private String fileId;     //文件id
	@Column
	private Integer status =0;    //文件转换状态 0：待转换 1：转换中 2：转换成功 3：转换失败
	@Column
	private Integer imgStauts =0;  //抽取封面文件 0：待转换 1：转换中 2：转换成功 3：转换失败
	@Column
	private Integer txtStauts =0;  //抽取文本文件 0：待转换 1：转换中 2：转换成功 3：转换失败
	@Column
	private Integer retryNum =0;  //重试次数（针对失败的记录）
	@Column
	private String resId;      //资源id
	@Column
	private String fileType;   //文件格式
	@Column
	private String srcPath;    //源文件路径（相对）
	@Column
	private String tarPath;    //目标文件路径（相对）
	@Column
	private Integer priority =0;   //转换的优先级 0：默认 1：立即开始 2：低
	@Column
	private String describes;    //转换后的描述
	@Transient
	private String oldSrcPath;    //老（原来的）源文件路径（用于更新是使用）
	@Transient
	private String baseSrcPath;    //源文件路径（绝对）
	@Transient
	private String baseTarPath;    //目标文件路径（绝对）
	@Column
	private String doHasType;   //处理类型：0，转换 1，抽取，2抽取文本
	@Column
	private String doResultType;   //处理类型：0，转换 1，抽取，2抽取文本
	@Column
	private String txtStr;   //抽取的文本内容
	@Column
	private String timestamp;   //时间戳
	@Column
	private Integer imgDoStauts =0;   //执行保存图片状态 0：待保存 1：保存中 2：保存成功 3：保存失败
	@Column
	private Integer txtDoStauts =0;   //执行保存文本状态 0：待保存 1：保存中 2：保存成功 3：保存失败
	@Transient
	private Integer flag =1;
	@Column
	private Integer platformId;
	@Column
	private Date createTime;
	@Column
	private Date updateTime;
	// Constructors

	/** default constructor */
	public ResConverfileTask() {}



	public ResConverfileTask(String srcPath) {
		super();
		this.srcPath = srcPath;
	}

	public ResConverfileTask(String resId,String srcPath,String doHasType) {
		super();
		this.resId = resId;
		this.srcPath = srcPath;
		this.doHasType = doHasType;
	}




	public ResConverfileTask(String srcPath, String tarPath) {
		super();
		this.srcPath = srcPath;
		this.tarPath = tarPath;
	}




	public ResConverfileTask(String fileId, String resId, String fileType,
			String srcPath, String tarPath) {
		super();
		this.fileId = fileId;
		this.resId = resId;
		this.fileType = fileType;
		this.srcPath = srcPath;
		this.tarPath = tarPath;
	}


	public ResConverfileTask(Long id, String fileId, Integer status,
			Integer imgStauts, Integer txtStauts, Integer retryNum,
			String resId, String fileType, String srcPath, String tarPath,
			Integer priority, String describes, String oldSrcPath,
			String baseSrcPath, String baseTarPath, String doHasType,
			String doResultType, String txtStr, String timestamp,
			Integer imgDoStauts, Integer txtDoStauts, Integer flag,
			Integer platformId, Date createTime,Date updateTime) {
		super();
		this.id = id;
		this.fileId = fileId;
		this.status = status;
		this.imgStauts = imgStauts;
		this.txtStauts = txtStauts;
		this.retryNum = retryNum;
		this.resId = resId;
		this.fileType = fileType;
		this.srcPath = srcPath;
		this.tarPath = tarPath;
		this.priority = priority;
		this.describes = describes;
		this.oldSrcPath = oldSrcPath;
		this.baseSrcPath = baseSrcPath;
		this.baseTarPath = baseTarPath;
		this.doHasType = doHasType;
		this.doResultType = doResultType;
		this.txtStr = txtStr;
		this.timestamp = timestamp;
		this.imgDoStauts = imgDoStauts;
		this.txtDoStauts = txtDoStauts;
		this.flag = flag;
		this.platformId = platformId;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}



	public Integer getImgDoStauts() {
		return imgDoStauts;
	}



	public void setImgDoStauts(Integer imgDoStauts) {
		this.imgDoStauts = imgDoStauts;
	}



	public Integer getTxtDoStauts() {
		return txtDoStauts;
	}



	public void setTxtDoStauts(Integer txtDoStauts) {
		this.txtDoStauts = txtDoStauts;
	}



	//比较
	@Override
	public int compareTo(Object obj){
		ResConverfileTask resConverfileTask = (ResConverfileTask)obj;
		return this.id.compareTo(resConverfileTask.getId());
    }


	// Property accessors
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileId() {
		return fileId;
	}



	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public Integer getStatus() {
		return status;
	}



	public void setStatus(Integer status) {
		this.status = status;
	}


	public Integer getRetryNum() {
		return retryNum;
	}



	public void setRetryNum(Integer retryNum) {
		this.retryNum = retryNum;
	}


	public String getResId() {
		return resId;
	}



	public void setResId(String resId) {
		this.resId = resId;
	}


	public String getFileType() {
		return fileType;
	}



	public void setFileType(String fileType) {
		this.fileType = fileType;
	}


	public String getSrcPath() {
		return srcPath;
	}



	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}


	public String getTarPath() {
		return tarPath;
	}



	public void setTarPath(String tarPath) {
		this.tarPath = tarPath;
	}


	public Integer getPriority() {
		return priority;
	}



	public void setPriority(Integer priority) {
		this.priority = priority;
	}



	public String getDescribes() {
		return describes;
	}



	public void setDescribes(String describes) {
		this.describes = describes;
	}



	public String getOldSrcPath() {
		return oldSrcPath;
	}



	public void setOldSrcPath(String oldSrcPath) {
		this.oldSrcPath = oldSrcPath;
	}


	public Integer getFlag() {
		return flag;
	}



	public void setFlag(Integer flag) {
		this.flag = flag;
	}



	@Override
	@Transient
	public String getObjectDescription() {
		// tanghui Auto-generated method stub
		return null;
	}



	@Override
	@Transient
	public String getEntityDescription() {
		// tanghui Auto-generated method stub
		return null;
	}



	public String getBaseSrcPath() {
		return baseSrcPath;
	}



	public void setBaseSrcPath(String baseSrcPath) {
		this.baseSrcPath = baseSrcPath;
	}



	public String getBaseTarPath() {
		return baseTarPath;
	}



	public void setBaseTarPath(String baseTarPath) {
		this.baseTarPath = baseTarPath;
	}



	public String getDoHasType() {
		return doHasType;
	}



	public void setDoHasType(String doHasType) {
		this.doHasType = doHasType;
	}



	public String getDoResultType() {
		return doResultType;
	}



	public void setDoResultType(String doResultType) {
		this.doResultType = doResultType;
	}



	public Integer getImgStauts() {
		return imgStauts;
	}



	public void setImgStauts(Integer imgStauts) {
		this.imgStauts = imgStauts;
	}



	public Integer getTxtStauts() {
		return txtStauts;
	}



	public void setTxtStauts(Integer txtStauts) {
		this.txtStauts = txtStauts;
	}



	public String getTxtStr() {
		return txtStr;
	}



	public void setTxtStr(String txtStr) {
		this.txtStr = txtStr;
	}



	public String getTimestamp() {
		return timestamp;
	}



	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}



	public Integer getPlatformId() {
		return platformId;
	}



	public void setPlatformId(Integer platformId) {
		this.platformId = platformId;
	}



	public Date getCreateTime() {
		return this.createTime;
	}



	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}



	public Date getUpdateTime() {
		return this.updateTime;
	}



	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}





}