package com.brainsoon.dfa.service;

import java.util.List;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.dfa.model.Words;


public interface IWordsService extends IBaseService {
	public boolean addWordsFile(List<Words> wordsList,List<Words> wordsExist);
	
	public List getExistWordsByFile(List<String> list);
	
	public boolean exist(String content,Long id);
	
	public List getWordsByLevel(String[] levels); 
}
