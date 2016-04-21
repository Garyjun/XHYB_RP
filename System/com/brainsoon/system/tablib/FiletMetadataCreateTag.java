package com.brainsoon.system.tablib;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.resource.service.IBatchImportResService;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.semantic.schema.store.jena.service.MetaService;
import com.brainsoon.system.service.IMetaDataModelService;
import com.brainsoon.system.util.MetadataSupport;
import com.brainsoon.system.util.MetadataUtil;

public class FiletMetadataCreateTag extends RequestContextAwareTag {
    private static final long serialVersionUID = 1L;
    private Object object;
    private String fileType;
    private String publishType;
    private String flag;
    private String fileName;
    private String fileSize;
	public String getPublishType() {
		return publishType;
	}

	public void setPublishType(String publishType) {
		this.publishType = publishType;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	protected int doStartTagInternal() throws Exception {
		IMetaDataModelService metaDataModelService = (IMetaDataModelService)BeanFactoryUtil.getBean("metaDataModelService");
		JspWriter writer = pageContext.getOut();
		ServletRequest request = pageContext.getRequest();
		String path = getRequestContext().getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		List<MetadataDefinition>  fileMetaDatas = metaDataModelService.queryMetaByFormat(fileType);
		StringBuffer sb = new StringBuffer(); 
		sb.append("<div class=\"portlet\">");
		sb.append("   <div class=\"portlet-body\">");
		sb.append("       <div class=\"container-fluid\">");
		sb.append("           <div class=\"row\">");
		sb.append("               <div class=\"").append("col-md-6").append("\">");
		sb.append("                   <div class=\"").append("form-group").append("\">");
		sb.append("                     <label class=\"").append("control-label").append(" ").append("col-md-4").append("\">");
		sb.append("						文件名称").append(" :</label>");
		sb.append("                           <div class=\"").append("col-md-8").append("\">");
		sb.append(" 					<input type=\"text\" name=\"name\" id=\"").append("name").append("\" class=\"").append("form-control");
		sb.append(					"\" value=\"").append(fileName);
		sb.append("\"");
		sb.append("           </div>");
		sb.append("       </div>");
		sb.append("      </div>");
		sb.append("       	</div>");
		sb.append("       	<div class=\"").append("col-md-6").append("\">");
		sb.append("           	 <div class=\"").append("form-group").append("\">");
		sb.append("                     <label class=\"").append("control-label").append(" ").append("col-md-4").append("\">");
		sb.append("						文件大小").append(" :</label>");
		sb.append("                           <div class=\"").append("col-md-8").append("\">");
		sb.append(" 				<input type=\"text\" name=\"fileByte\" id=\"").append("fileByte").append("\" class=\"").append("form-control");
		sb.append(					"\" value=\"").append(fileSize);
		sb.append("\" 				readonly=\"readonly\"");
		sb.append("           		</div>");
		sb.append("          	 </div>");
		sb.append("       		</div>");
		sb.append("       	  </div>");
		sb.append("       	</div>");
		sb.append("         <div class=\"row\">");
		for(MetadataDefinition fileMetaData:fileMetaDatas){
			if(fileMetaData!=null && fileMetaDatas.size()>0){
					String viewPriority = fileMetaData.getViewPriority();
					if(StringUtils.isNotBlank(viewPriority) && viewPriority.indexOf("3")>=0){
						int fieldType = fileMetaData.getFieldType();
						switch(fieldType){
						    case 1:
						    	sb = MetadataUtil.createInput(sb,flag, fileMetaData,object,true);
						    	break;
						    case 2:	
						    	sb = MetadataUtil.createSelect(sb,flag, fileMetaData,object,true,false);
						    	break;
						    case 3:
						    	sb = MetadataUtil.createCheckbox(sb,flag, fileMetaData,object,true);
						    	break;
						    case 4:	
						    	sb = MetadataUtil.createRadio(sb,flag, fileMetaData,object,true);
						    	break;
						    case 5:	
						    	sb = MetadataUtil.createTextarea(sb,flag, fileMetaData,object,true);
						    	break;
						    case 6:	
						    	sb = MetadataUtil.createLookup(sb,flag, fileMetaData,object,true,basePath);
						    	break;
						    case 7:	
						    	sb = MetadataUtil.createDateTime(sb,flag, fileMetaData,object,true,"yyyy-MM-dd");
						    	break;
						    case 8:	
						    	sb = MetadataUtil.createURL(sb,flag, fileMetaData,object,true);
						    	break;
						    case 9:	
						    //	sb = MetadataUtil.createSelect(sb, metadataDefinition,object,true,false);
						    	break;
						}
					}
			}
		}
		sb.append("           </div>");
		sb.append("       </div>");
		sb.append("   </div>");
		sb.append("</div>");
		writer.append(sb.toString());
		writer.flush();
		return 0;
	}

}
