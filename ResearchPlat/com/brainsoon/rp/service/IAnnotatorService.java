package com.brainsoon.rp.service;

import java.util.List;

import com.brainsoon.rp.model.annotation.Annotation;

/**
 * <dl>
 * <dt>IAnnotatorService.java</dt>
 * <dd>Function: {!!一句话描述功能}</dd>
 * <dd>Description:{详细描述该类的功能}</dd>
 * <dd>Copyright: Copyright (C) 2010</dd>
 * <dd>Company: 北京博云易迅技术有限公司</dd>
 * <dd>CreateUser: xujie</dd>
 * <dd>CreateDate: 2016年3月7日</dd>
 * </dl>
 */
public interface IAnnotatorService {

	void create(Annotation annotation);

	/**
	 * @param limit
	 * @param uri
	 * @return
	 */
	List<Annotation> findAnnotations(int limit, String uri);

}
