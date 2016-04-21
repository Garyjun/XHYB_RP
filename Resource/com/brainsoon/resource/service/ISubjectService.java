package com.brainsoon.resource.service;


import java.util.List;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.resource.po.SubjectStore;

/**
 * @ClassName: ISubjectService
 * @Description: TODO
 * @author 
 * @date 2015年9月24日 下午4:06:36
 *
 */
public interface ISubjectService extends IBaseService{
	public SubjectStore save(SubjectStore store);
	public String addSubjectRes(String resid,String id,String posttype,String publishTypes);
	public String deleteBatchSubject(String ids,String posttype);
	public List<SubjectStore> getSubjectByTemplateId(Long templateId);
}
