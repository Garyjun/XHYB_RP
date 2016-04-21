package com.brainsoon.system.action;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.system.model.Logo;
import com.brainsoon.system.service.ILogoService;
import com.brainsoon.system.support.SysOperateLogUtils;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LogoAction extends BaseAction{
	
	//获取图片要保存到的跟路径
	public final static String TEMPLATEFILE_TEMP = WebAppUtils.getWebRootBaseDir(ConstantsDef.logoPath);
	@Autowired
	private ILogoService  logoService;
	UserInfo userInfo = LoginUserUtil.getLoginUser();
	/**
	 * 进行页面跳转
	 * @param model
	 * @param logo
	 * @return
	 */
	@RequestMapping(value="/logo/list")
	public String list(Model model,Logo logo){
		model.addAttribute(Constants.COMMAND,logo);
		List<Logo> logoList  = logoService.queueLogo();
		model.addAttribute("logoList", logoList);
		return "system/logo/new";
	}
	
	
	/**
	 * 分页查询logo
	 * @param logo
	 * @return
	 */
	@RequestMapping(value = "/logo/query")
	@ResponseBody
	public PageResult query(Logo logo) {
		PageInfo pageInfo = getPageInfo();
		return logoService.queryLogos(pageInfo, logo);
	}
	
	/**
	 * 启用图片 
	 * @return
	 */
	@RequestMapping(value="/logo/qiyong")
	public @ResponseBody String qiyong(HttpServletRequest request,Model model){
		//根据启用图片的id将被启用的图片的状态修改为1，其余图片的状态修改为0
		
		//准备启用的图片的id
		String id = request.getParameter("id");
		int logoId = Integer.parseInt(id);
		logoService.doLogo(logoId);
		JSONArray array = new JSONArray();
		JSONObject json = new JSONObject();
		String a = "";
		json.put("success", a);
		array.add(json);
		SysOperateLogUtils.addLog("logo_qiyong", userInfo.getUsername(), userInfo);
		return array.toString();
	}
	
	
	/**
	 * 首页logo查询
	 * @return
	 */
	@RequestMapping(value="/logo/queryIndexLogo")
	public @ResponseBody String queryIndexLogo(HttpServletRequest request,Model model,String platformId){
		//查询出状态为1的logo的名称
		String logoName = "";
		logoName = logoService.queueStatus();
		JSONArray array = new JSONArray();
		JSONObject json = new JSONObject();
		json.put("logoName", logoName);
		array.add(json);
		return array.toString();
	}
	
	/**
	 * 上传logo图片
	 * @param logo
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/logo/upload")
	public @ResponseBody String upload(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String result = "恭喜！上传成功";
		try {
			//获取前台上传的文件
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			//获取前台传值
			Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
			MultipartFile multipartFile = null;
			for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
				//上传文件
				multipartFile = set.getValue();
			}
			//获取原文件名
			String fileName1 = multipartFile.getOriginalFilename();
			int index = fileName1.indexOf(".");
			fileName1 = (String) fileName1.subSequence(index, fileName1.length());
			String fileName = UUID.randomUUID()+fileName1;
			new File(TEMPLATEFILE_TEMP).mkdir();
			File image = new File(TEMPLATEFILE_TEMP + File.separator + fileName);
			try {
				multipartFile.transferTo(image);
			} catch (Exception e) {
				e.printStackTrace();
				result = "sorry！上传失败";
			}
			Logo logo = new Logo();
			logo.setCreateTime(new Date());
			logo.setUpdateTime(new Date());
			logo.setUpdateUserId(LoginUserUtil.getLoginUser().getUserId());
			//默认上传之后的状态为0，启用之后状态为1
			logo.setStatus(0);
			logo.setLogeName(fileName);
			logoService.create(logo);
			SysOperateLogUtils.addLog("logo_add", logo.getLogoName(),LoginUserUtil.getLoginUser());
		} catch (Exception e) {
			result = "sorry！上传失败";
		}
		
		return result;
	}
	
	
	@RequestMapping(value="/logo/del")
	@ResponseBody
	public String del(HttpServletRequest request){
		String logoId = request.getParameter("id");
		String message = "success";
		try{
			Logo logo = (Logo) logoService.getByPk(Logo.class, Integer.valueOf(logoId));
			logoService.delete(Logo.class, Integer.valueOf(logoId));
			
			String logoName = logo.getLogoName();
			
			File f = new File(TEMPLATEFILE_TEMP + File.separator + logoName);
			if (f.exists()) {
				// 删除文件
				File path = f.getParentFile();
				try {
					FileUtils.deleteDirectory(path);
				}catch(Exception e) {
					
				}
			}
			SysOperateLogUtils.addLog("logo_del", userInfo.getUsername(), userInfo);
		}catch(Exception e){
			message = "fail";
			e.printStackTrace();
		}
		
		return message;
	}
	
	@RequestMapping(value="logo/doDisabled")
	@ResponseBody
	public String disabled(HttpServletRequest request){
		String id = request.getParameter("id");
		String message = "success";
		try{
			logoService.doDisabled();
			int logoId = Integer.parseInt(id);
			Logo logo = (Logo) logoService.getByPk(Logo.class, Integer.valueOf(logoId));
			logo.setStatus(0);
			logoService.saveOrUpdate(logo);
			SysOperateLogUtils.addLog("logo_jinyong", userInfo.getUsername(), userInfo);
		}catch(Exception e){
			message = "fail";
			e.printStackTrace();
		}
		
		return message;
	}
}
