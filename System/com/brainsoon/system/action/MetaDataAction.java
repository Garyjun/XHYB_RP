package com.brainsoon.system.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.common.support.MetadataDefinitionGroupCacheMap;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.MetaDataFileModelGroup;
import com.brainsoon.system.model.MetadataDefinitionGroup;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IFLTXService;
import com.brainsoon.system.service.IMetaDataModelService;
import com.brainsoon.system.service.impl.FLTXService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.util.MetadataSupport;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MetaDataAction extends BaseAction {
	/** 默认命名空间 **/
	private static final String baseUrl = "/system/metaData/";
	@Autowired
	private IMetaDataModelService metaDataModelService;
	@Autowired
	private IFLTXService FLTXService;
	UserInfo userInfo = LoginUserUtil.getLoginUser();

	/**
	 * 跳转到添加页面
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "add")
	@Token(save = true)
	public String add(
			@ModelAttribute("frmSourceGroup") MetaDataModelGroup metaDataModelGroup,
			ModelMap model) throws Exception {
		return baseUrl + "addMetaData";
	}
	/**
	 * 跳转到主页面
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "returnMain")
	@Token(save = true)
	public String returnMain() throws Exception {
		return baseUrl + "main";
	}

	/**
	 * 执行添加操作
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "addAction")
	@Token(save = true)
	public @ResponseBody String addAction(
			HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute("frmSourceGroup") MetaDataModelGroup metaDataModelGroup,
			Model model) {
		logger.debug("进入添加方法");
		String name = metaDataModelGroup.getTypeName();
		try {
			String hql = "from MetaDataModelGroup";
			//用于排序字段加1
			List<MetaDataModelGroup> countList = metaDataModelService.query(hql);
			metaDataModelGroup.setMaxOrder(countList.size()+1);
			metaDataModelService.addMetaDataModelGroup(metaDataModelGroup);
			String typeName  = metaDataModelGroup.getTypeName();
			String id = ""+metaDataModelGroup.getId();
			Map<String, String> map = userInfo.getResTypes();
			map.put(id, typeName);
			userInfo.setResTypes(map);
			List<User> list = metaDataModelService.query("from User where loginName = 'admin'");
			User admin = list.get(0);
			String resTypes = admin.getResType();
			resTypes += id + ",";
			admin.setResType(resTypes);
			metaDataModelService.update(admin);
			LoginUserUtil.saveLoginUser(userInfo);
			SysOperateLogUtils.addLog("metaDataModelGroup_add",metaDataModelGroup.getTypeName(), userInfo);
		} catch (Exception e) {
			addActionError(e);
		}
		return name;
	}

	/**
	 * 执行查询操作
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "queryGroup")
	@Token(save = true)
	public @ResponseBody String queryGroup(HttpServletRequest request,
			HttpServletResponse response) {
		/*
		 * List<MetaDataModelGroup> typeName =
		 * metaDataModelService.doTypeName(); JSONArray array = new JSONArray();
		 * for(MetaDataModelGroup metaDataModelGroup : typeName){ JSONObject
		 * json = new JSONObject(); json.put("name",
		 * metaDataModelGroup.getTypeName()); json.put("id",
		 * metaDataModelGroup.getId()); array.add(json); }
		 */
		Map<String, String> map = userInfo.getResTypes();
		JSONArray array = new JSONArray();
		if (map != null) {
			Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				JSONObject json = new JSONObject();
				json.put("name", entry.getValue());
				json.put("id", entry.getKey());
				array.add(json);
			}
		}
		return array.toString();
	}

	/**
	 * 执行查询操作
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "queryFileGroup")
	@Token(save = true)
	public @ResponseBody String queryFileGroup(HttpServletRequest request,
			HttpServletResponse response) {
		List<MetaDataFileModelGroup> fileName = metaDataModelService
				.doFileTypeName();
		JSONArray array = new JSONArray();
		for (MetaDataFileModelGroup metaDataFileModelGroup : fileName) {
			JSONObject json = new JSONObject();
			json.put("name", metaDataFileModelGroup.getTypeName());
			json.put("id", metaDataFileModelGroup.getId());
			array.add(json);
		}
		return array.toString();
	}
	
	

	/**
	 * 跳转分类详细页面
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "detailMetaDataType")
	@Token(save = true)
	public String MetaDataTypeDetail(HttpServletRequest request,
			HttpServletResponse response, Model model) {
		String typeName = (String) request.getParameter("typeName");
		if(StringUtils.isNotBlank(typeName)){
			try {
				typeName = URLDecoder.decode(typeName,"UTF-8");
			} catch (Exception e) {
			}
		}
		String id = (String) request.getParameter("id");
		model.addAttribute("typeName", typeName);
		model.addAttribute("id", id);
		return baseUrl + "metaDataType";
	}

	/**
	 * 跳转分类详细页面
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "detailFileMetaDataType")
	@Token(save = true)
	public String detailFileMetaDataType(HttpServletRequest request,
			HttpServletResponse response, Model model) {
		String typeName = (String) request.getParameter("typeName");
		String id = (String) request.getParameter("id");
		model.addAttribute("typeName", typeName);
		model.addAttribute("id", id);
		return baseUrl + "metaFileDataType";
	}

	/**
	 * 跳转到添加页面
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "addFile")
	@Token(save = true)
	public String add(
			@ModelAttribute("frmFileGroup") MetaDataFileModelGroup metaDataFileModelGroup,
			ModelMap model) throws Exception {
		return baseUrl + "addFileMetaData";
	}

	/**
	 * 执行添加操作
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "addFileAction")
	@Token(save = true)
	public @ResponseBody String addFileAction(
			HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute("frmFileGroup") MetaDataFileModelGroup metaDataFileModelGroup,
			Model model) {
		logger.debug("进入添加方法");
		String name = metaDataFileModelGroup.getTypeName();
		try {
			metaDataModelService
					.addMetaDataFileModelGroup(metaDataFileModelGroup);
			SysOperateLogUtils.addLog("metaDataFileModelGroup_add",metaDataFileModelGroup.getTypeName(), userInfo);
		} catch (Exception e) {
			addActionError(e);
		}
		return name;
	}
	
	
	/**
	 * 执行修改操作
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "editFileMetaDataTypeAction")
	@Token(save = true)
	public String editFileMetaDataTypeAction(
			HttpServletRequest request,
			HttpServletResponse response,@ModelAttribute("frmFileGroup") MetaDataFileModelGroup metaDataFileModelGroup) {
		metaDataModelService.updFileMetaDataType(metaDataFileModelGroup);
		SysOperateLogUtils.addLog("metaDataFileModelGroup_edit",metaDataFileModelGroup.getTypeName(), userInfo);
		return baseUrl + "main";
	}
	/**
	 * 跳转到文件分类编辑页面
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "editFileMetaDataType")
	@Token(save = true)
	public String editFileMetaDataType(
			HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		String id = (String) request.getParameter("id");
		MetaDataFileModelGroup metaDataFileModelGroup = (MetaDataFileModelGroup)metaDataModelService.getByPk(MetaDataFileModelGroup.class, Long.valueOf(id));
		model.addAttribute("frmFileGroup", metaDataFileModelGroup);
		return baseUrl + "editFileMetaDataType";
	}
	
	/**
	 * 跳转到资源分类编辑页面
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "editMetaDataType")
	@Token(save = true)
	public String editMetaDataType(
			HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		String id = (String) request.getParameter("id");
		MetaDataModelGroup metaDataModelGroup = (MetaDataModelGroup)metaDataModelService.getByPk(MetaDataModelGroup.class, Long.valueOf(id));
		model.addAttribute("frmSourceGroup", metaDataModelGroup);
		return baseUrl + "editMetaDataType";
	}
	
	/**
	 * 执行修改操作
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "editMetaDataTypeAction")
	@Token(save = true)
	public String editMetaDataTypeAction(
			HttpServletRequest request,
			HttpServletResponse response,@ModelAttribute("frmSourceGroup") MetaDataModelGroup metaDataModelGroup) {
		metaDataModelService.updMetaDataType(metaDataModelGroup);
		SysOperateLogUtils.addLog("metaDataModelGroup_edit",metaDataModelGroup.getTypeName(), userInfo);
		String typeName  = metaDataModelGroup.getTypeName();
		String id = ""+metaDataModelGroup.getId();
		Map<String, String> map = userInfo.getResTypes();
		map.put(id, typeName);
		userInfo.setResTypes(map);
		LoginUserUtil.saveLoginUser(userInfo);
		return baseUrl + "main";
	}

	/**
	 * 跳转到详细页面
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "centerMeta")
	@Token(save = true)
	public @ResponseBody String centerMetaList() throws Exception {
		JSONArray array = new JSONArray();
		List<MetadataDefinition> list = MetadataSupport.getCommonMetadatas();
		if(list!=null) {
			for (MetadataDefinition metadataDefinition : list) {
				String uri = metadataDefinition.getUri();
				JSONObject jsonChild = new JSONObject();
				jsonChild.put("id", uri);
				jsonChild.put("pId", "a1");
				jsonChild.put("name", metadataDefinition.getFieldZhName());
				array.add(jsonChild);
			}
		}
		return array.toString();
	}

	/**
	 * 跳转到详细页面
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "queryMetaType")
	@Token(save = true)
	public @ResponseBody String queryMetaTypeList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = (String) request.getParameter("sysResMetadataTypeId");
		int sysResMetadataTypeIdd = Integer.parseInt(id);
		List<MetadataDefinitionGroup> listChild = metaDataModelService
				.doTypeChildList(sysResMetadataTypeIdd);
		JSONArray array = new JSONArray();
		for (MetadataDefinitionGroup metadataDefinitionGroup : listChild) {
			String groupId = metadataDefinitionGroup.getId() + "";
			/*if("1".equals(id)) {
				if(!"1".equals(groupId)&&!"2".equals(groupId)){
					JSONObject json = new JSONObject();
					//String groupId = metadataDefinitionGroup.getId() + "";
					json.put("id", metadataDefinitionGroup.getId());
					json.put("pId", "a" + id);
					json.put("name", metadataDefinitionGroup.getFieldZhName());
					array.add(json);
					List<MetadataDefinition> list = MetadataSupport
							.getMetadataByGroupId(groupId);
					// int count = 0;
					for (MetadataDefinition metadataDefinition : list) {
						// count++;
						String uri = metadataDefinition.getUri();
						JSONObject jsonChild = new JSONObject();
						jsonChild.put("id", uri);
						jsonChild.put("pId", groupId);
						jsonChild.put("name", metadataDefinition.getFieldZhName());
						array.add(jsonChild);
					}
				}
			} else {*/
				JSONObject json = new JSONObject();
				//String groupId = metadataDefinitionGroup.getId() + "";
				json.put("id", metadataDefinitionGroup.getId());
				json.put("pId", "a" + id);
				json.put("name", metadataDefinitionGroup.getFieldZhName());
				json.put("open", "true");
				array.add(json);
				List<MetadataDefinition> list = MetadataSupport
						.getMetadataByGroupId(groupId);
				// int count = 0;
				for (MetadataDefinition metadataDefinition : list) {
					// count++;
					String uri = metadataDefinition.getUri();
					JSONObject jsonChild = new JSONObject();
					jsonChild.put("id", uri);
					jsonChild.put("pId", groupId);
					String zhName = metadataDefinition.getFieldZhName(); //中文名称
					Integer allowNull = metadataDefinition.getAllowNull(); //是否允许为空
					String duplicateCheck = metadataDefinition.getDuplicateCheck(); //是否为查重项
					jsonChild.put("name", zhName);
					jsonChild.put("allowNull", allowNull);
					jsonChild.put("duplicateCheck", duplicateCheck);
					array.add(jsonChild);
				}
			/*}*/
			
			
		}
		return array.toString();
	}
	
	
	/**
	 * 获取树节点数据
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "queryFileAllData")
	@Token(save = true)
	public @ResponseBody String queryFileAllDataList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = (String) request.getParameter("sysResMetadataTypeId");
		JSONArray array = new JSONArray();
		List<MetadataDefinition> list = MetadataSupport.getFileMetadataById(id);
		if(list!=null) {
			for (MetadataDefinition metadataDefinition : list) {
				String uri = metadataDefinition.getUri();
				JSONObject jsonChild = new JSONObject();
				jsonChild.put("id", uri);
				jsonChild.put("pId", "a" + id);
				jsonChild.put("name", metadataDefinition.getFieldZhName());
				array.add(jsonChild);
			}
		}
		return array.toString();
	}
	
	

	/**
	 * 跳转到详细页面
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "detailMetaDefinition")
	@Token(save = true)
	public @ResponseBody String detailMetaDefinition(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String idd = (String) request.getParameter("id");
		Long id = Long.parseLong(idd);
		MetadataDefinitionGroup metadataDefinitionGroup = (MetadataDefinitionGroup) metaDataModelService
				.getByPk(MetadataDefinitionGroup.class, id);
		JSONObject json = new JSONObject();
		json.put("fieldName", metadataDefinitionGroup.getFieldName());
		json.put("fieldZhName", metadataDefinitionGroup.getFieldZhName());
		return json.toString();
	}

	/**
	 * 跳转到编辑页面
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "editDefinition")
	@Token(save = true)
	public String editDefinition(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws Exception {
		String uri = (String) request.getParameter("id");
		String groupId = (String) request.getParameter("groupId");
		String typeName = (String) request.getParameter("typeName");
		String sysResMetadataTypeId = (String) request
				.getParameter("sysResMetadataTypeId");
		String delType = (String) request.getParameter("delType");//delType:为1时表示删除核心元数据；2时表示资源元数据;3表示文件元数据
		model.addAttribute("groupId", groupId);
		model.addAttribute("typeName", typeName);
		model.addAttribute("id", sysResMetadataTypeId);
		MetadataDefinition metadataDefinition = MetadataSupport
				.getMetadataDefineByGroupId(groupId, uri,delType);
		model.addAttribute("metadataDefinition", metadataDefinition);
		model.addAttribute("delType", delType);
		return baseUrl + "editDefinitionData";
	}

	/**
	 * 执行元数据更新操作
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "editDefinitionAction")
	@Token(save = true)
	public String editDefinitionAction(HttpServletRequest request,
			HttpServletResponse response,
			MetadataDefinition metadataDefinition, Model model) {
		String id = (String) request.getParameter("typeId");
		String groupId = (String) request.getParameter("groupId");
		String typeName = (String) request.getParameter("typeName");
		String uri = (String) request.getParameter("uri");
		String fieldName = (String) request.getParameter("fieldName");
		 try {
			 typeName=  URLDecoder.decode(typeName,"UTF-8").trim();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		if(metadataDefinition.getIdentifier()==14){
			metadataDefinition.setOrderNum(200);
		}
		if(metadataDefinition.getIdentifier()==15){
			metadataDefinition.setOrderNum(201);
		}
		String delType = (String) request.getParameter("delType");//delType:为1时表示删除核心元数据；2时表示资源元数据
		String valueRange = (String) request.getParameter("valueRange");
		metadataDefinition.setUri(uri);
		metadataDefinition.setValueRange(valueRange);
		metadataDefinition.setFieldName(fieldName);
		metadataDefinition.setResType(id);
		MetadataSupport.updBaseMetadata(metadataDefinition,delType);
		model.addAttribute("typeName", typeName);
		model.addAttribute("id", id);
		model.addAttribute("delType", delType);
		model.addAttribute("groupId", groupId);
		SysOperateLogUtils.addLog("metadataDefinition_edit",metadataDefinition.getFieldZhName(), userInfo);
		return baseUrl + "editDefinitionData";
	}

	/**
	 * 添加组操作
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "addMetaGroupAction")
	@Token(save = true)
	public @ResponseBody String addMetaGroupAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = (String) request.getParameter("sysResMetadataTypeId");
		Long sysResMetadataTypeId = Long.parseLong(id);
		String fieldName = (String) request.getParameter("fieldName");
		String fieldZhName = (String) request.getParameter("fieldZhName");
		MetadataDefinitionGroup metadataDefinitionGroup = new MetadataDefinitionGroup();
		metadataDefinitionGroup.setFieldName(fieldName);
		metadataDefinitionGroup.setFieldZhName(fieldZhName);
		metadataDefinitionGroup.setSysResMetadataTypeId(sysResMetadataTypeId);
		metaDataModelService.addMetaDefinition(metadataDefinitionGroup);
		SysOperateLogUtils.addLog("metadataDefinitionGroup_add",fieldZhName, userInfo);
		MetadataDefinitionGroupCacheMap.putKey(metadataDefinitionGroup.getId()+"",metadataDefinitionGroup.getFieldZhName());
		return "1";
	}

	/**
	 * 执行更新组操作
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "updMetaGroupAction")
	@Token(save = true)
	public @ResponseBody String updMetaGroupAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = (String) request.getParameter("sysResMetadataTypeId");
		Long sysResMetadataTypeId = Long.parseLong(id);
		String idDefinition = (String) request.getParameter("id");
		Long definitionID = Long.parseLong(idDefinition);
		String fieldName = (String) request.getParameter("fieldName");
		String fieldZhName = (String) request.getParameter("fieldZhName");
		if(StringUtils.isNotBlank(fieldZhName)){
			try {
				fieldZhName= URLDecoder.decode(fieldZhName,"UTF-8");
			} catch (Exception e) {
			}
		}
		MetadataDefinitionGroup metadataDefinitionGroup = new MetadataDefinitionGroup();
		metadataDefinitionGroup.setFieldName(fieldName);
		metadataDefinitionGroup.setId(definitionID);
		metadataDefinitionGroup.setFieldZhName(fieldZhName);
		metadataDefinitionGroup.setSysResMetadataTypeId(sysResMetadataTypeId);
		metaDataModelService.updMetaDefinition(metadataDefinitionGroup);
		SysOperateLogUtils.addLog("metadataDefinitionGroup_edit",fieldZhName, userInfo);
		MetadataDefinitionGroupCacheMap.putKey(metadataDefinitionGroup.getId()+"",metadataDefinitionGroup.getFieldZhName());
		return "1";
	}

	/**
	 * 执行删除操作
	 * 
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(baseUrl + "delMetaDefinition")
	public @ResponseBody String delMetaDefinition(HttpServletResponse response,HttpServletRequest request,
			@RequestParam("id") String id) throws IOException {
		logger.debug("进入删除方法");
		try {
			String groupName = (String) request.getParameter("groupName");
			metaDataModelService.deleteById(id);
			SysOperateLogUtils.addLog("metadataDefinitionGroup_del",groupName, userInfo);
			//根据组id删除组下的元数据，调用张鹏的接口
		} catch (Exception e) {
			return "0";
		}

		return "1";
	}

	/**
	 * 执行删除操作
	 * 
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(baseUrl + "delDefinition")
	public @ResponseBody String delDefinition(HttpServletResponse response,HttpServletRequest request,
			@RequestParam("id") String uri) throws IOException {
		logger.debug("进入删除方法");
		try {
			String delType = (String) request.getParameter("delType");//delType:为1时表示删除核心元数据；2时表示资源元数据
			String name = (String) request.getParameter("name");
			MetadataDefinition metadataDefinition = new MetadataDefinition();
			metadataDefinition.setUri(uri);
			MetadataSupport.deleBaseMetadata(metadataDefinition,delType);
			SysOperateLogUtils.addLog("metadataDefinition_del",name, userInfo);
		} catch (Exception e) {
			return "0";
		}

		return "1";
	}
	
	/**
	 * 执行删除文件分类操作
	 * 
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(baseUrl + "delFileOrSourceMetaDataType")
	@Token(save = true)
	public  @ResponseBody String delFileOrSourceMetaDataType(HttpServletResponse response,HttpServletRequest request) throws IOException {
		String flagType = (String)request.getParameter("flagType");
		String id = (String) request.getParameter("id");
		String typeName = (String) request.getParameter("typeName");
		if("2".equals(flagType)){
			metaDataModelService.delMetaDataType(id);
			Map<String, String> map = userInfo.getResTypes();
			map.remove(id);
			userInfo.setResTypes(map);
			List<User> list = metaDataModelService.query("from User where loginName = 'admin'");
			User admin = list.get(0);
			String resTypes = admin.getResType();
			String newResTypes = "";
			String[] resTypesArray = resTypes.split(",");
			for(int i=0;i<resTypesArray.length;i++) {
				if(id.equals(resTypesArray[i])) {
					resTypesArray[i]="";
				}else{
					newResTypes+=resTypesArray[i]+",";
				}
			}
			admin.setResType(newResTypes);
			metaDataModelService.update(admin);
			LoginUserUtil.saveLoginUser(userInfo);
			SysOperateLogUtils.addLog("metaDataModelGroup_del",typeName, userInfo);
			return "2";

		}else if("3".equals(flagType)){
			 metaDataModelService.delFileMetaDataType(id);
			 SysOperateLogUtils.addLog("metaDataFileModelGroup_del",typeName, userInfo);
			 return "3";
		}
		return baseUrl + "main";
	}

	/**
	 * 跳转到添加页面
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "addSysDefinition")
	@Token(save = true)
	public String addSysDefinition(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws Exception {
		String id = (String) request.getParameter("id");
		String groupId = (String) request.getParameter("groupId");
		String typeName = (String) request.getParameter("name");
		model.addAttribute("flag", "metaDefinition");
		model.addAttribute("id", id);
		model.addAttribute("groupId", groupId);
		model.addAttribute("typeName", typeName);
		return baseUrl + "addDefinitionData";
	}

	/**
	 * 执行添加操作
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "addSysDefinitionAction")
	@Token(save = true)
	public String addSysDefinitionAction(HttpServletRequest request,
			HttpServletResponse response,
			MetadataDefinition metadataDefinition, Model model) {
		String id = (String) request.getParameter("typeId");
		String groupId = (String) request.getParameter("groupId");
		String typeName = (String) request.getParameter("typeName");
		String groupName = (String) request.getParameter("groupName");
		String valueRange = (String) request.getParameter("valueRange");
		String flag =  (String) request.getParameter("flag");
		 try {
			 typeName=  URLDecoder.decode(typeName,"UTF-8");
			 groupName=  URLDecoder.decode(groupName,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		metadataDefinition.setValueRange(valueRange);
		/*if(StringUtils.isNoneBlank(valueRange)){
			metadataDefinition.setValueRange(valueRange);
		}*/
		metadataDefinition.setGroupId(groupId);
		metadataDefinition.setResType(id);
		String uuid = UUID.randomUUID().toString();
		metadataDefinition.setUri(uuid);
		MetadataSupport.addBaseMetadata(metadataDefinition,groupName);
		SysOperateLogUtils.addLog("metadataDefinitionfenl_add",metadataDefinition.getFieldZhName(), userInfo);
		model.addAttribute("typeName", typeName);
		model.addAttribute("id", id);
		model.addAttribute("uuid", uuid);
		model.addAttribute("flag", flag);
		model.addAttribute("groupId", groupId);
		model.addAttribute("groupName", groupName);
		model.addAttribute("treeName", metadataDefinition.getFieldZhName());
		return baseUrl + "addDefinitionData";
	}

	/**
	 * 获取组下面的元数据
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "queryGroupChild")
	@Token(save = true)
	public @ResponseBody String queryGroupChild(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String groupId = (String) request.getParameter("groupId");
		List<MetadataDefinition> list = MetadataSupport
				.getMetadataByGroupId(groupId);
		JSONArray array = new JSONArray();
		int count = 0;
		for (MetadataDefinition metadataDefinition : list) {
			count++;
			JSONObject json = new JSONObject();
			json.put("id", count);
			json.put("pId", "a" + metadataDefinition.getGroupId());
			json.put("name", metadataDefinition.getFieldZhName());
			array.add(json);
		}
		return array.toString();
	}

	/**
	 * 跳转到添加核心元数据页面页面
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "addCenterMetaData")
	@Token(save = true)
	public String addCenterMetaData(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws Exception {
		model.addAttribute("flag", "centerMeta");
		return baseUrl + "addDefinitionData";
	}

	/**
	 * 执行添加操作
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "addCenterMetaDataAction")
	@Token(save = true)
	public String addCenterMetaDataAction(HttpServletRequest request,
			HttpServletResponse response,
			MetadataDefinition metadataDefinition, Model model) {
		String uuid = UUID.randomUUID().toString();
		String valueRange = (String) request.getParameter("valueRange");
		String flag =  (String) request.getParameter("flag");
		model.addAttribute("uuid", uuid);
		model.addAttribute("flag", flag);
		model.addAttribute("treeCenterName", metadataDefinition.getFieldZhName());
		metadataDefinition.setValueRange(valueRange);
		metadataDefinition.setUri(uuid);
		metadataDefinition.setGroupId("1");//1代表核心元数据组id
		MetadataSupport.addCommonMetadata(metadataDefinition);
		SysOperateLogUtils.addLog("metadataDefinitionCenter_add",metadataDefinition.getFieldZhName(), userInfo);
		return baseUrl + "addDefinitionData";
	}
	
	
	/**
	 * 查询树形输入类型下的取值范围数据
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "getTreeByFieldType")
	@Token(save = true)
	public @ResponseBody String getTreeByFieldType(HttpServletRequest request,
			HttpServletResponse response) {
		String array = FLTXService.getFLTXMenu();
		return array;
	}
	
	
	/**
	 * 跳转到添加文件元数据页面页面
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "addFileDefinitionData")
	@Token(save = true)
	public String addFileDefinitionData(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws Exception {
		String sysResMetadataTypeId = (String) request.getParameter("id");
		String typeName = (String) request.getParameter("typeName");
		model.addAttribute("typeId", sysResMetadataTypeId);
		model.addAttribute("typeName", typeName);
		model.addAttribute("flag", "addFileMetaData");
		return baseUrl + "addDefinitionData";
	}
	
	
	/**
	 * 执行添加操作
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "addFileDefinitionDataAction")
	@Token(save = true)
	public String addFileDefinitionDataAction(HttpServletRequest request,
			HttpServletResponse response,
			MetadataDefinition metadataDefinition, Model model) {
		String id = (String) request.getParameter("typeId");
		String typeName = (String) request.getParameter("typeName");
		String valueRange = (String) request.getParameter("valueRange");
		String flag =  (String) request.getParameter("flag");
		metadataDefinition.setValueRange(valueRange);
		metadataDefinition.setResType(id);
		metadataDefinition.setGroupId("2");//2代表文件元数据组id
		String uuid = UUID.randomUUID().toString();
		metadataDefinition.setUri(uuid);
		MetadataSupport.addFileMetadata(metadataDefinition);
		model.addAttribute("typeName", typeName);
		model.addAttribute("uuid", uuid);
		model.addAttribute("id", id);
		model.addAttribute("flag", flag);
		model.addAttribute("treeFileName", metadataDefinition.getFieldZhName());
		SysOperateLogUtils.addLog("metadataDefinitionfile_add",metadataDefinition.getFieldZhName(), userInfo);
		return baseUrl + "addDefinitionData";
	}
	
	/**
	 * 查询树形输入类型下的取值范围数据
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "getDictName")
	@Token(save = true)
	public @ResponseBody String getDictName(HttpServletRequest request,
			HttpServletResponse response) {
		JSONArray array = new JSONArray();
		List<DictName> list = metaDataModelService.getDictName();
		for (DictName dictName : list) {
			JSONObject json = new JSONObject();
			json.put("indexTag", dictName.getIndexTag());
			json.put("name", dictName.getName());
			array.add(json);
		}
		return array.toString();
	}
	
	/**
	 * 跳转到详细页面
	 * 
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "queryBatchFlag")
	@Token(save = true)
	public @ResponseBody String queryBatchFlagList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JSONArray array = new JSONArray();
		String publishType = (String) request.getParameter("publishType");
		List<MetadataDefinition> list = MetadataSupport.getBatchQueryMetadateDefineList(publishType);
		if(list!=null) {
			for (MetadataDefinition metadataDefinition : list) {
				JSONObject jsonChild = new JSONObject();
				jsonChild.put("id", metadataDefinition.getFieldName());
				jsonChild.put("name", metadataDefinition.getFieldZhName());
				jsonChild.put("fileType",metadataDefinition.getFieldType());
				jsonChild.put("dictKey", metadataDefinition.getValueRange());
				array.add(jsonChild);
			}
		}
		return array.toString();
	}
	
	
	/**
	 * 拖拽排序功能
	 * 参数
	 * targetId：要拖拽位置的节点id
	 * uriId：	被拖拽的节点的id
	 * groupId：	元数据所在组（即节点的Pid，如作者的Pid为书目元数据）
	 * delType：	delType:为1时表示删除核心元数据；2时表示资源元数据;3表示文件元数据
	 * moveType：inner 成为子节点;prev 成为同级前一个节点;next 成为同级后一个节点
	 * @param order
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "ztreeOrder")
	@Token(save = true)
	public @ResponseBody String ztreeOrder(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String,MetadataDefinition> allMap = new HashMap<String,MetadataDefinition>();
		Map<String,MetadataDefinition> orderMap = new HashMap<String,MetadataDefinition>();
		String targetId = (String) request.getParameter("targetId");
		String moveType = (String) request.getParameter("moveType");
		String typeId = (String) request.getParameter("typeId");
		String uriId = (String) request.getParameter("uri");
		String groupId = (String) request.getParameter("groupId");
		String delType = (String) request.getParameter("delType");//delType:为1时表示删除核心元数据；2时表示资源元数据;3表示文件元数据
		List<MetadataDefinition> list = new ArrayList<MetadataDefinition>();
		if(delType.equals("1")) {
			list = MetadataSupport.getCommonMetadatas();
		}else if(delType.equals("2")) {
			list = MetadataSupport.getMetadataByGroupId(groupId);
		}else if(delType.equals("3")){
			list = MetadataSupport.getFileMetadataById(typeId);
		}
		//获取当前树的所有节点
		for(int i=0;i<list.size();i++) {
			int orderNum = i+1;
		    MetadataDefinition metadataDefinition = list.get(i);
		    metadataDefinition.setOrderNum(orderNum);
		    allMap.put(metadataDefinition.getUri(), metadataDefinition);
		}
		if (allMap != null) {
			
			if (StringUtils.isNotEmpty(moveType) && "prev".equals(moveType)) {//prev 成为同级前一个节点
				
				int targetOrderNum = allMap.get(targetId).getOrderNum();//该位置之前的不变
				int oldOrderNum = allMap.get(uriId).getOrderNum();//该位置之后的不变
				if (targetOrderNum < oldOrderNum) {//后面的节点向前移动
					Iterator<Map.Entry<String, MetadataDefinition>> it = allMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, MetadataDefinition> entry = it.next();
						int index = entry.getValue().getOrderNum();
						if (index >= targetOrderNum && index < oldOrderNum) {
							entry.getValue().setOrderNum(index+1);
						}else if (index == oldOrderNum) {
							entry.getValue().setOrderNum(targetOrderNum);
						}
						orderMap.put(entry.getKey(), entry.getValue());
					}
					
				}else {//前面的节点向后移动
					Iterator<Map.Entry<String, MetadataDefinition>> it = allMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, MetadataDefinition> entry = it.next();
						int index = entry.getValue().getOrderNum();
						if (index == oldOrderNum) {
							entry.getValue().setOrderNum(targetOrderNum-1);
						}else if (index > oldOrderNum && index < targetOrderNum) {
							entry.getValue().setOrderNum(index-1);
						}
						orderMap.put(entry.getKey(), entry.getValue());
					}
				}
				
			} else if (StringUtils.isNoneEmpty(moveType) && "next".equals(moveType)) {//next 成为同级后一个节点
				int targetOrderNum = allMap.get(targetId).getOrderNum();//该位置之前的不变
				int oldOrderNum = allMap.get(uriId).getOrderNum();//该位置之后的不变
				if (targetOrderNum < oldOrderNum) {//后面的节点向前移动
					Iterator<Map.Entry<String, MetadataDefinition>> it = allMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, MetadataDefinition> entry = it.next();
						int index = entry.getValue().getOrderNum();
						if (index > targetOrderNum && index < oldOrderNum) {
							entry.getValue().setOrderNum(index+1);
						}else if (index == oldOrderNum) {
							entry.getValue().setOrderNum(targetOrderNum+1);
						}
						orderMap.put(entry.getKey(), entry.getValue());
					}
					
				}else {//前面的节点向后移动
					Iterator<Map.Entry<String, MetadataDefinition>> it = allMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, MetadataDefinition> entry = it.next();
						int index = entry.getValue().getOrderNum();
						if (index == oldOrderNum) {
							entry.getValue().setOrderNum(targetOrderNum);
						}else if (index > oldOrderNum && index <= targetOrderNum) {
							entry.getValue().setOrderNum(index-1);
						}
						orderMap.put(entry.getKey(), entry.getValue());
					}
				}
			}
			/*Iterator<Map.Entry<String, MetadataDefinition>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, MetadataDefinition> entry = it.next();
				int num = map.get(uriId).getOrderNum();
				if(uriId.equals(entry.getKey())) {
					entry.getValue().setOrderNum(map.get(targetId).getOrderNum());
					map.get(targetId).setOrderNum(num);
				}
			}*/
		}
		boolean flag = MetadataSupport.updBaseMetadatas(orderMap, delType);
		if(flag==true) {
			return "success";
		}
		return "false";
	}
}
