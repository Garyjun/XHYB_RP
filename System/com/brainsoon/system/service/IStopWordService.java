package com.brainsoon.system.service;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.StopWord;

public interface IStopWordService extends IBaseService {
	public void addStopWord(StopWord stopWord);
	public void updateStopWord(StopWord stopWord);
	public void deleteByIds(String ids);
	public PageResult listStopWord();
	
	public String postStopWord(String name);
	public String getAllName();
}
