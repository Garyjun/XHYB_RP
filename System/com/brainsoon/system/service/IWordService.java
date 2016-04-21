package com.brainsoon.system.service;

import java.io.File;

import com.brainsoon.common.service.IBaseService;

public interface IWordService extends IBaseService {
	public boolean addWordTxt(File file,String status,String level);
	
	public boolean updateWord(String status,String level,String ids);
}
