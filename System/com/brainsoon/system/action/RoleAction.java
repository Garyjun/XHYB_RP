package com.brainsoon.system.action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.po.tree.TreeNode;
import com.brainsoon.common.support.Constants;
import com.brainsoon.system.model.Role;
import com.brainsoon.system.service.IOrganizationService;
import com.brainsoon.system.service.IRoleResService;
import com.brainsoon.system.service.IRoleService;
import com.brainsoon.system.service.impl.RoleResService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants;
import com.brainsoon.system.support.SystemConstants.ResourceType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)  
public class RoleAction extends BaseAction{
     @Autowired
     private IRoleService  roleService ;
     
     @Autowired
     private IRoleResService  roleResService ;
     
     @Autowired
     private IOrganizationService organizationService;
     
     @RequestMapping(value="/role/list")
  	 public String list(Model model,Role role) {
  	    	 logger.debug("to list ");
  	         model.addAttribute(Constants.COMMAND, role);
  	  	     return "system/role/roleList";
  	 }
     
     @RequestMapping(value="/role/query")
	 @ResponseBody
	 public PageResult  query(Role role) {
	         	return roleService.queryRoles(getPageInfo(), role);
		    
	}
     @RequestMapping(value = "/role/toEdit")
     @Token(save=true)
	 public String toEdit(Model model,@RequestParam Long id) {
    	  if(!model.containsAttribute(Constants.COMMAND)){
    		  List<TreeNode> tree=new ArrayList<TreeNode>();
			   if(id>0) {
				    Role role=(Role)baseService.getByPk(Role.class, id);
				    tree=roleService.getAllPriviTree(role.getId());
				    model.addAttribute(Constants.COMMAND,role);
		       }else{
		    	   model.addAttribute(Constants.COMMAND,new Role());
		    	   model.addAttribute("selectedOrg", "");
		    	   tree=roleService.getAllPriviTree(new Long(-1));
		       } 
			   Gson gson=new Gson();
			   String treeJson=gson.toJson(tree);
			   logger.debug(treeJson);
	           model.addAttribute("treeJson",treeJson);
		   }
    	   model.addAttribute("statusMap",SystemConstants.Status.map);
    	   model.addAttribute(Constants.ID,id);
//    	   model.addAttribute("orgJson",organizationService.getOrganizationJson());
    	  logger.debug(" auth codes "+LoginUserUtil.getAuthResCodes());
	      return "system/role/roleEdit";
	 }
	 
    @RequestMapping(value = "/role/add")
    @Token(remove=true)
    public String add(Model model, @ModelAttribute("command")Role command,HttpServletResponse response) {
        try {
        	UserInfo userInfo = LoginUserUtil.getLoginUser();
        	String[] typesArray=command.getResTypesArray();
        	String resTypes= ArrayUtils.toString(typesArray);
        	resTypes=resTypes.replace("{","");
        	resTypes=resTypes.replace("}","");
        	command.setResTypes(resTypes);
        	command.setPlatformId(userInfo.getPlatformId());
        	roleService.doCreateRole(command);
        	String logName = userInfo.getPlatformId()==1?"role_add":"pud_role_add";
        	SysOperateLogUtils.addLog(logName, command.getRoleName(), userInfo);
		} catch (Exception e) {
			    logger.error(e.getMessage());
			    addActionError(e);
		}
        return "system/role/roleList";
    }
    
    @RequestMapping(value = "/role/update")
    @Token(remove=true)
    public String update(Model model, @ModelAttribute("command")Role command,HttpServletResponse response) {
        try {
        	UserInfo userInfo = LoginUserUtil.getLoginUser();
        	String[] typesArray=command.getResTypesArray();
        	String resTypes= ArrayUtils.toString(typesArray);
        	resTypes=resTypes.replace("{","");
        	resTypes=resTypes.replace("}","");
        	command.setResTypes(resTypes);
        	command.setPlatformId(userInfo.getPlatformId());
        	roleService.doUpdateRole(command);
        	String logName = userInfo.getPlatformId()==1?"role_modify":"pud_role_modify";
        	SysOperateLogUtils.addLog(logName, command.getRoleName(), userInfo);        	
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(e);
		}
        return "system/role/roleList";
    }
    
	@RequestMapping(value = "/role/delete/{id}")
	@ResponseBody
	public String delete(@PathVariable Long id) {
		String result = "0";
		try {
			Role delRole = (Role) roleService.getByPk(Role.class, id);
			String roleName = delRole.getRoleName();
			roleService.delete(Role.class, id);
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			String logName = userInfo.getPlatformId()==1?"role_delete":"pud_role_delete";
			SysOperateLogUtils.addLog(logName, roleName,userInfo);
		} catch (Exception e) {
			result = "-1";
		}
		return result;
	}
    
	@RequestMapping(value = "/role/batchDelete/{ids}")
	@ResponseBody
	public String batchDelete(@PathVariable String ids) {
		logger.debug("*********batchDelete*********");
		String result = "0";
		try {
			String[] idArray = ids.split(",");
			for (int i = 0; i < idArray.length; i++) {
				roleService.delete(Role.class, new Long(idArray[i]));
			}
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			String logName = userInfo.getPlatformId()==1?"role_delete":"pud_role_delete";
			SysOperateLogUtils.addLog(logName, "批量删除",
					userInfo);
		} catch (Exception e) {
			result = "-1";
		}
		return result;
	}
    
    @RequestMapping(value="/role/view")
    public String view(Model model,@RequestParam Long id) {
    	Role role=(Role)baseService.getByPk(Role.class, id);
    	model.addAttribute(Constants.COMMAND,role);
    	List<TreeNode> tree=new ArrayList<TreeNode>();
	    tree=roleService.getAllPriviTree(role.getId());
		Gson gson=new Gson();
		String treeJson=gson.toJson(tree);
		logger.debug(treeJson);
        model.addAttribute("treeJson",treeJson);
//        String treeInfo= roleResService.getResTree();
//  	    String[] treeInfoArr=treeInfo.split("##");
//  	    String resTree= treeInfoArr[0];
//  	    model.addAttribute("resTree",resTree);
//  	    String textBook= treeInfoArr[1];
//  	    model.addAttribute("textBook",textBook);
	    Map  resTypeMap=new HashMap<Object,Object>();
	    String roleTypes= role.getResTypes();
	    if(StringUtils.isNotBlank(roleTypes)){
	    	String[] types=roleTypes.split(",");
	    	for(String type:types){
	    		resTypeMap.put(type, ResourceType.getValueByKey(type));
	    	}
	    }
	    model.addAttribute("resTypeMap", resTypeMap);
        return "system/role/roleView";
    }
    
     @RequestMapping(value="/role/queryPriviTree")
	 @ResponseBody
	 public String queryPriviTree(Long roleId,String id) {
    	        if(id==null){
    	        	id="m_"+-1l;
    	        }
	         	String jsonStr=roleService.getPrivilegeTreeJson(id, roleId);
		        logger.debug("json:"+jsonStr);
		        return jsonStr;
		    
	}
     
     
	   
 	@RequestMapping(value = "/role/batchDisabled/{ids}")
 	public String batchDisabled(@PathVariable String ids){
 		logger.debug("*********batchDisabled*********");
 		try {
 			UserInfo user = LoginUserUtil.getLoginUser();
 			String[] idArray = ids.split(",");
 			roleService.doDisabled(idArray);
 			String logName = user.getPlatformId()==1?"role_disable":"pud_role_disable";
 			SysOperateLogUtils.addLog(logName, "批量禁用", user);
 		} catch (Exception e) {
 			logger.error(e.getMessage());
 		}
 		return "redirect:/role/list.action";
 	}
 	
 	@RequestMapping(value = "/role/batchEnabled/{ids}")
 	public String batchEnabled(@PathVariable String ids){
 		logger.debug("*********batchDisabled*********");
 		try {
 			UserInfo user = LoginUserUtil.getLoginUser();
 			String[] idArray = ids.split(",");
 			roleService.doEnabled(idArray);
 			String logName = user.getPlatformId()==1?"role_enable":"pud_role_enable";
 			SysOperateLogUtils.addLog(logName, "批量启用", user);
 		} catch (Exception e) {
 			logger.error(e.getMessage());
 		}
 		return "redirect:/role/list.action";
 	}     
}
