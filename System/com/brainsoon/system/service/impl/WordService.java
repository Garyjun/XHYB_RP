package com.brainsoon.system.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.system.model.Word;
import com.brainsoon.system.service.IWordService;
@Service
public class WordService extends BaseService implements IWordService {
	//导入敏感词
	public boolean addWordTxt(File file,String status,String level){
		boolean result = false;
		try {
			String encoding = "UTF-8";
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				//建立一个输入流对象(file是文件路径)
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				//建立一个对象，它把文件内容转成计算机能读懂的语言
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while (!StringUtils.isBlank(lineTxt = bufferedReader.readLine())) {  //一次读入一行内容
					Word word = new Word();
					if(lineTxt.indexOf(";")!=-1){  //搜索;在字符串lineTxt中的起始位置，若没有返回-1
						String name = lineTxt.substring(0, lineTxt.indexOf(";"));
						String similarWord = lineTxt.substring(lineTxt.indexOf(";")+1, lineTxt.length());
						logger.info("敏感词name"+name+" "+"敏感词similarWord"+similarWord);
						word.setName(name);
						word.setSimilarWords(similarWord);
					}else{
						word.setName(lineTxt);
					}
					if(StringUtils.isNotBlank(status) && StringUtils.isNotBlank(level)){
						word.setLevel(level);
						word.setStatus(status);
					}else{
						word.setLevel("1");
						word.setStatus("1");
					}
					
					word.setPlatformId(LoginUserUtil.getLoginUser().getPlatformId());
					create(word);
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 批量修改敏感词状态和等级
	 */
	@Override
	public boolean updateWord(String status, String level, String ids) {
		String sql = "update Word SET status = '"+status+"',level='"+level+"' WHERE id in ("+ids+")";
		boolean s= false;
		try{
    	 s = getBaseDao().updateWithHql(sql);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return s;
	}
}
