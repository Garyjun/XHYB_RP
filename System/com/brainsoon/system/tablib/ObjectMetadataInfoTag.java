package com.brainsoon.system.tablib;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspWriter;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.util.MetadataUtil;

public class ObjectMetadataInfoTag extends RequestContextAwareTag {
    private static final long serialVersionUID = 1L;
    private Object object;
    
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}



	@Override
	protected int doStartTagInternal() throws Exception {
		CustomMetaData customMetaData = new CustomMetaData();
	
		JspWriter writer = pageContext.getOut();
		ServletRequest request = pageContext.getRequest();
		String path = getRequestContext().getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		StringBuffer sb = new StringBuffer(); 
		sb.append("<div class=\"portlet\">");
		sb = MetadataUtil.createTitle(sb, "资源信息");
		sb.append("   <div class=\"portlet-body\">");
		sb.append("       <div class=\"container-fluid\">");
		sb.append("           <div class=\"row\">");
 		List<MetadataDefinition> metadataDefinitions = customMetaData.getCustomPropertys();
		for(MetadataDefinition metadataDefinition:metadataDefinitions){
			int fieldType = metadataDefinition.getFieldType();
			switch(fieldType){
			    case 1:
			    	sb = MetadataUtil.createInput(sb,"", metadataDefinition,object,true);
			    	break;
			    case 2:	
			    	sb = MetadataUtil.createSelect(sb,"", metadataDefinition,object,false,false);
			    	break;
			    case 3:
			    	sb = MetadataUtil.createCheckbox(sb,"", metadataDefinition,object,false);
			    	break;
			    case 4:	
			    	sb = MetadataUtil.createRadio(sb,"", metadataDefinition,object,false);
			    	break;
			    case 5:	
			    	sb = MetadataUtil.createTextarea(sb,"", metadataDefinition,object,false);
			    	break;
			    case 6:	
			    	sb = MetadataUtil.createLookup(sb,"", metadataDefinition,object,true,basePath);
			    	break;
			    case 7:	
			    	sb = MetadataUtil.createDateTime(sb,"", metadataDefinition,object,true,"yyyy-MM-dd");
			    	break;
			    case 8:	
			    	sb = MetadataUtil.createURL(sb,"", metadataDefinition,object,true);
			    	break;
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
