package com.brainsoon.system.action;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.system.model.StopWord;
import com.brainsoon.system.model.Word;
import com.brainsoon.system.service.IWordService;
import com.brainsoon.system.support.SysOperateLogUtils;
@Controller
public class WordAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/system/word/";
	@Autowired
	private IWordService wordService;
	
	UserInfo userInfo = LoginUserUtil.getLoginUser();
	@RequestMapping(baseUrl + "list")
	public @ResponseBody PageResult list(HttpServletRequest request,HttpServletResponse response){
		logger.info("查询敏感词列表");
		QueryConditionList conditionList = getQueryConditionList();
		return wordService.query4Page(Word.class, conditionList);
	}
	
	@RequestMapping(baseUrl + "upd")
	public String upd(HttpServletRequest request,HttpServletResponse response,ModelMap model){
		logger.info("进入修改/添加敏感词页面");
		String id = request.getParameter("id");
		Word word = new Word();
		if(id!=null){
			word = (Word) wordService.getByPk(Word.class, Long.parseLong(id));
		}
		model.addAttribute("frmWord", word);
		return baseUrl + "wordEdit";
	}
	
	@RequestMapping(baseUrl + "updWord")
	@Token(save=true)
	public String updWord(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmWord") Word word,ModelMap model){
		logger.info("进入保存敏感词方法");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		try {
			word.setPlatformId(LoginUserUtil.getLoginUser().getPlatformId());
			if(word.getId()!=null){
				wordService.update(word);
				String logName = userInfo.getPlatformId()==1?"word_update":"pud_word_update";
				SysOperateLogUtils.addLog(logName, word.getName(), userInfo);
			}else{
				wordService.create(word);
				String logName = userInfo.getPlatformId()==1?"word_add":"pud_word_add";
				SysOperateLogUtils.addLog(logName, word.getName(), userInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+baseUrl+"wordList.jsp";
	}
	@RequestMapping(baseUrl + "detail")
	public String detail(@RequestParam("id") Long id,HttpServletResponse response,ModelMap model){
		logger.info("查看敏感词");
		Word word = (Word) wordService.getByPk(Word.class, id);
		model.addAttribute("frmWord", word);
		return baseUrl + "wordDetail";
	}
	
	@RequestMapping(baseUrl + "delete")
	public String delete(HttpServletRequest request,HttpServletResponse response){
		logger.info("删除敏感词");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String ids = request.getParameter("ids");
		wordService.delete(Word.class, ids);
		String logName = userInfo.getPlatformId()==1?"word_delete":"pud_word_delete";
		SysOperateLogUtils.addLog(logName, "批量删除", userInfo);
		return baseUrl + "wordList";
	}
	
	@RequestMapping(baseUrl + "importWord")
	public @ResponseBody String importWord(HttpServletRequest request,HttpServletResponse response){
		String result = "";
		String status = request.getParameter("status");
		String level = request.getParameter("level");
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		MultipartFile multipartFile = null;
		for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
			multipartFile = set.getValue();// 文件名
		}
		String fileName = multipartFile.getOriginalFilename();
		File txt = new File(WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp) + fileName);
		try {
			multipartFile.transferTo(txt);
			wordService.addWordTxt(txt,status,level);
		} catch (Exception e) {
			e.printStackTrace();
			result = "error";
		}
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		SysOperateLogUtils.addLog("word_import", "批量导入", userInfo);
		return result;
	}
	
	//下载模板
	/**
	 * 下载模板
	 * @return
	 * @throws IOException
	 */
    @RequestMapping(value = baseUrl + "downloadTemplete")
    public ResponseEntity<byte[]> download() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = URLEncoder.encode("敏感词模板.txt", "UTF-8");
        headers.setContentDispositionFormData("attachment", filename);
        File excel = new File(WebAppUtils.getWebAppRoot() + "system/word/word.txt");
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(excel),headers, HttpStatus.OK);
    }
    
    
    @RequestMapping(baseUrl + "updMany")
	public String updMany(HttpServletRequest request,HttpServletResponse response){
		logger.info("进入修改/添加敏感词页面");
		String ids = request.getParameter("ids");
		request.setAttribute("ids", ids);
		return baseUrl + "updateMany";
    }
    /**
     * 批量修改敏感词的状态和等级
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(baseUrl + "updateMany")
	@Token(save=true)
	public String updateMany(HttpServletRequest request,HttpServletResponse response){
    	String status = request.getParameter("status");
    	String level = request.getParameter("level");
    	String ids = request.getParameter("words");
    	try{
    		boolean reult = wordService.updateWord(status, level, ids);
    		SysOperateLogUtils.addLog("pud_word_update", userInfo.getUsername(), userInfo);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return "redirect:"+baseUrl+"wordList.jsp";
	}
}
