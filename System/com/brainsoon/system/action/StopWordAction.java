package com.brainsoon.system.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.system.model.StopWord;
import com.brainsoon.system.model.Word;
import com.brainsoon.system.service.IStopWordService;
import com.brainsoon.system.support.SysOperateLogUtils;
@Controller
public class StopWordAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/system/dataManagement/stopWord/";
	@Autowired
	public IStopWordService stopWordService;
	
	//停用词列表
	@RequestMapping(baseUrl + "listStopWord")
	public @ResponseBody PageResult list(HttpServletRequest request,HttpServletResponse response){
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		return stopWordService.query4Page(StopWord.class, conditionList);
	}
	//停用词编辑
	@RequestMapping(baseUrl + "stopWordUpd")
	@Token(save=true)
	public String stopWordUpd(HttpServletRequest request,HttpServletResponse response,ModelMap model){
		logger.info("进入修改/添加页面");
		String id = request.getParameter("id");
		StopWord stopWord = new StopWord();
		if(id!=null){
			stopWord = (StopWord)stopWordService.
					getByPk(StopWord.class, Long.parseLong(id));
		}
		model.addAttribute("frmStopWord", stopWord);
		return baseUrl + "stopWordEdit";
	}
	
	//保存修改或添加停用词
	@RequestMapping(baseUrl + "upd")
	@Token(save=true)
	public void upd(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmStopWord") StopWord stopWord,ModelMap model){
		logger.info("进入修改/添加方法");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		try {
			if(stopWord.getId()!=null){
				stopWordService.updateStopWord(stopWord);
				SysOperateLogUtils.addLog("stopWord_update", stopWord.getName(), userInfo);
			}else{
				stopWordService.addStopWord(stopWord);
				SysOperateLogUtils.addLog("stopWord_add", stopWord.getName(), userInfo);
			}
			String name = stopWordService.getAllName();
			String json = stopWordService.postStopWord(name);
		} catch (Exception e) {
			e.printStackTrace();
			addActionError(e);
		}
	}
	//查看停用词
	@RequestMapping(baseUrl + "detail")
	@Token(save=true)
	public String detail(@RequestParam("id") Long id,HttpServletResponse response,ModelMap model){
		logger.info("查看停用词");
		StopWord stopWord = (StopWord) stopWordService.getByPk(StopWord.class, id);
		model.addAttribute("frmStopWord", stopWord);
		return baseUrl + "stopWordDetail";
	}
	//删除停用词
	@RequestMapping(baseUrl + "delAction")
	public String delete(HttpServletRequest request,HttpServletResponse response){
		logger.info("删除停用词");
		String ids = request.getParameter("ids");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		try {
			stopWordService.delete(StopWord.class, ids);
			String name = stopWordService.getAllName();
			String json = stopWordService.postStopWord(name);
			SysOperateLogUtils.addLog("stopWord_delete", "...", userInfo);
		} catch (Exception e) {
			e.printStackTrace();
			addActionError(e);
		}
		return baseUrl + "stopWordList";
	}
}
