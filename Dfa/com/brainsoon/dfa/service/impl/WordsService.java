package com.brainsoon.dfa.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.dfa.model.Words;
import com.brainsoon.dfa.service.IWordsService;

@Service
public class WordsService extends BaseService implements IWordsService {

	@Override
	public boolean addWordsFile(List<Words> wordsList, List<Words> wordsExist) {
		// TODO Auto-generated method stub
		for (Words word : wordsList) {
			Object o = this.getBaseDao().create(word);
			if (o == null) {
				return false;
			}
		}
		if (wordsExist != null) {
			for (Words word : wordsExist) {
				try {
					this.getBaseDao().update(word);
				} catch (Exception e) {
					logger.error(e.toString());
					return false;
				}

			}
		}
		return true;
	}

	@Override
	public List getExistWordsByFile(List<String> list) {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled()) {
			logger.debug("进入WordsService.getExistWordsByFile()...");
		}
		StringBuffer queryHql = new StringBuffer();
		queryHql.append("from Words where content in (");
		for (int i = 0; i < list.size(); i++) {
			queryHql.append(list.get(i));
			if (i < list.size() - 1) {
				queryHql.append(",");
			}
		}
		queryHql.append(")");
		return this.getBaseDao().query(queryHql.toString());
	}

	@Override
	public boolean exist(String content, Long id) {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled()) {
			logger.debug("进入WordsService.exist()...");
		}
		QueryConditionList conditions = new QueryConditionList();
		conditions.addCondition(new QueryConditionItem("content",
				Operator.EQUAL, content));
		if(id != null && id!=0l){
			conditions.addCondition(new QueryConditionItem("id", Operator.NOTEQUAL,
					id));
		}
		PageResult result = query4Page(Words.class, conditions);
		if(result.getTotal()>0)
			return true;
		else
			return false;
	}

	@Override
	public List getWordsByLevel(String[] levels) {
		// TODO Auto-generated method stub
		StringBuilder array = new StringBuilder();
		array.append("(");
		for(String s : levels)
			array.append("'"+s+"'"+",");
		String params = array.toString().substring(0, array.length()-1) + ")";
		String hql = "from Words where status='1' and level in " + params;
		List list = query(hql);
		List<String> words=new ArrayList<String>();
		for(int index=0; index<list.size(); index++){
			Words word = (Words) list.get(index);
			words.add(word.getName());
			if(word.getSimilarWords()!=null){
			String[] ws=word.getSimilarWords().split(",");
			for(int i=0;i<ws.length;i++){
				if(ws[i].length()>0){
					words.add(ws[i]);
				}
			}
			}
		}
		return words;
	}

}
