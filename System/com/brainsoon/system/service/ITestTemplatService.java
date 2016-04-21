package com.brainsoon.system.service;

import java.util.List;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.TestTemplate;
import com.brainsoon.system.model.TestTemplateItem;

public interface ITestTemplatService extends IBaseService {
	public void addTestTemplatItem(TestTemplateItem testTemplateItem);
	public int doMaxCount(int pid);
	public void addTemplate(TestTemplate testTemplate);
	public void updTestTemplatService(TestTemplate testTemplate);
	public void deleteByIds(String ids);
	public void deleteItemByIds(String ids);
	public String doItemList(String id);
	public String getTemplatName(String id);

}
