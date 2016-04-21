package com.brainsoon.common.util.dofile.vo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @ClassName: InitTreeData 
 * @Description:  用于PDF阅读，解析导航（书签）的对象类
 * @author tanghui 
 * @date 2014-1-23 上午10:40:22 
 *
 */
public class InitTreeData  implements Comparable<InitTreeData>, Serializable{

    private static final long serialVersionUID = 1L;

    // 信息主键
    private Long id;
    
    //父节点id
    private String pId;
    
    private boolean parent;
	private boolean open;
    
    //标识文件类型
    private String fileType;

    //对象Set集合信息
    private Set<InitTreeData> initTreeDatas;
    
    //父节点对象信息
    private InitTreeData initTreeData;

    // 章节名称
    private String title;

    // 章节开始位置
    private Integer start;

    // 章节结束位置
    private Integer end;

    // 位置单位
    private Integer unit;

    // 类型
    private String type;

    // 章节序号
    private Integer playOrder;

    // classValue
    private String classValue;

    // catalogValue
    private String catalogValue;
    
    //文件锚点
    private String fileIndexing;
    
    //导航对应文件的绝对路径
    private String fileAbsolutePath;
    
    //节点内容
    private String content;
    
	public InitTreeData() {
		super();
	}

	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Set<InitTreeData> getInitTreeDatas() {
		 if (null == initTreeDatas) {
			 initTreeDatas = new HashSet<InitTreeData>();
	     }
		return initTreeDatas;
	}

	public void setInitTreeDatas(Set<InitTreeData> initTreeDatas) {
		this.initTreeDatas = initTreeDatas;
	}

	public InitTreeData getInitTreeData() {
		return initTreeData;
	}

	public void setInitTreeData(InitTreeData initTreeData) {
		this.initTreeData = initTreeData;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	public Integer getUnit() {
		return unit;
	}

	public void setUnit(Integer unit) {
		this.unit = unit;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getPlayOrder() {
		return playOrder;
	}

	public void setPlayOrder(Integer playOrder) {
		this.playOrder = playOrder;
	}

	public String getClassValue() {
		return classValue;
	}

	public void setClassValue(String classValue) {
		this.classValue = classValue;
	}

	public String getCatalogValue() {
		return catalogValue;
	}

	public void setCatalogValue(String catalogValue) {
		this.catalogValue = catalogValue;
	}

	public String getFileIndexing() {
		return fileIndexing;
	}

	public void setFileIndexing(String fileIndexing) {
		this.fileIndexing = fileIndexing;
	}

	public String getFileAbsolutePath() {
		return fileAbsolutePath;
	}

	public void setFileAbsolutePath(String fileAbsolutePath) {
		this.fileAbsolutePath = fileAbsolutePath;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	@Override
	public int compareTo(InitTreeData o) {
		int flag =0;
 	   Long initTreeDataIds0 = 0l;
		   Long initTreeDataIds2 = 0l;
		   InitTreeData initTreeData0  =  o.getInitTreeData();
		   if(initTreeData0 != null){
			   initTreeDataIds0 = o.getId();
		   }
		   InitTreeData initTreeData2  =  this.getInitTreeData();
		   if(initTreeData2 != null){
			   initTreeDataIds2 = this.getId();
		   }
		   
	      //先比较父id,再比较排序
		   if(initTreeData0 != null && initTreeData2 != null){
			   if(initTreeDataIds0 == initTreeDataIds2){
			    	if (this.getPlayOrder() > o.getPlayOrder()) {
			    		flag= 1;
					}else{
						flag= -1;
					}
			    }else if(initTreeDataIds0 < initTreeDataIds2) {
				    	flag=1;
				}else {
				    	flag=-1;
				 }
		   }else if(initTreeData0 != null && initTreeData2 == null){
			   if (this.getPlayOrder() > o.getPlayOrder()) {
		    		flag= 1;
				}else{
					flag= -1;
				}
		   
		   }else{
			   if (this.getPlayOrder() > o.getPlayOrder()) {
		    		flag= 1;
				}else{
					flag= -1;
				}
		   }
	    return flag;
	}
    

}