/*
 * Copyright (c) 2009 Kongzhong.com
 * Last Modified By $ @ $
 * Current CVS Version Number: $
 */
package com.brainsoon.common.web.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import java.util.*;

/**
 * 
 * @ClassName: FwCNViewResolver 
 * @Description:  
    -----------------------------------------------------------------------------------------------------
  	RESTful服务中很重要的一个特性即是同一资源,
   	多种表述我们使用ContentNegotiatingViewResolver就可以做到，
  	这个视图解析器允许你用同样的内容数据来呈现不同的view
   	如下面描述的三种方式:
   	-----------------------------------------------------------------------------------------------------
	方式1  使用扩展名
	http://www.test.com/user.xml    呈现xml文件
	http://www.test.com/user.json   呈现json格式
	http://www.test.com/user        使用默认view呈现，比如jsp等
	-----------------------------------------------------------------------------------------------------
	方式2  使用http request header的Accept
	GET /user HTTP/1.1
	Accept:application/xml
	GET /user HTTP/1.1
	Accept:application/json
	….
	-----------------------------------------------------------------------------------------------------
	方式3  使用参数
	http://www.test.com/user?format=xml
	http://www.test.com/user?format=json
	-----------------------------------------------------------------------------------------------------
	如何使用ContentNegotiatingViewResolver？
	假设我们有这么一个目标：
	/user/{userid}.json    用于返回一个描述User的JSON
	/user/{userid}         用于返回一个展示User的JSP页面
	/user/{userid}.xml     用于返回一个展示User的XML文件
	-----------------------------------------------------------------------------------------------------
	配置文件说明   （具体例子下篇文章放上）
	我们知道有accept header,扩展名以及参数这三种方式，配置文件中
	这里是解析器的执行顺序，如果有多个的话（前面多次解释过）
	<property name="order" value="1">property>
	-----------------------------------------------------------------------------------------------------
	如果所有的mediaType都没匹配上，就会使用defaultContentType
	<property name="defaultContentType" value="text/html" />
	这里是是否启用扩展名支持，默认就是true
	例如  /user/{userid}.json
	<property name="favorPathExtension" value="true"></property>
	这里是是否启用参数支持，默认就是true
	例如  /user/{userid}?format=json
	<property name="favorParameter" value="false"></property>
	这里是否忽略掉accept header，默认就是false
	例如     GET /user HTTP/1.1
	Accept:application/json
	<property name="ignoreAcceptHeader" value="true"></property>
	我们的例子是采用.json , .xml结尾的,所以关掉两个
	-----------------------------------------------------------------------------------------------------
	这里是扩展名到mimeType的映射,
	例如 /user/{userid}.json  中的   .json  就会映射到   application/json
	<property name="mediaTypes">
	           <map>
	              <entry key="json" value="application/json" />
	              <entry key="xml" value="application/xml"/>                     </map>
	</property>
	注:
	ContentNegotiatingViewResolver是根据客户提交的MimeType(如 text/html,application/xml)来跟服务端的一组viewResover的MimeType相比较,
	如果符合,即返回viewResover的数据.
	而 /user/123.xml, ContentNegotiatingViewResolver会首先将 .xml 根据mediaTypes属性将其转换成 application/xml,然后完成前面所说的比较.
 * @author tanghui 
 * @date 2013-8-10 下午8:57:25 
 *
 */
public class FwCNViewResolver extends ContentNegotiatingViewResolver {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FwCNViewResolver.class);
    private List<View> defaultViews;
    private List<ViewResolver> viewResolvers;

    @Override
    public void setDefaultViews(List<View> defaultViews) {
        super.setDefaultViews(defaultViews);
        this.defaultViews = defaultViews;
    }

    @Override
    public void setViewResolvers(List<ViewResolver> viewResolvers) {
        super.setViewResolvers(viewResolvers);
        this.viewResolvers = viewResolvers;
    }

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        Assert.isInstanceOf(ServletRequestAttributes.class, attrs);
        ServletRequestAttributes servletAttrs = (ServletRequestAttributes) attrs;
        List<MediaType> requestedMediaTypes = getMediaTypes(servletAttrs.getRequest());
        Collections.sort(requestedMediaTypes);

        SortedMap<MediaType, View> views = new TreeMap<MediaType, View>();
        List<View> candidateViews = new ArrayList<View>();
        for (ViewResolver viewResolver : viewResolvers) {
            View view = viewResolver.resolveViewName(viewName, locale);
            if (view != null) {
                candidateViews.add(view);
            }
        }
        if (!CollectionUtils.isEmpty(defaultViews)) {
            candidateViews.addAll(defaultViews);
        }
        for (View candidateView : candidateViews) {
            MediaType viewMediaType = MediaType.parseMediaType(candidateView.getContentType());
            for (MediaType requestedMediaType : requestedMediaTypes) {
                if (includes(requestedMediaType, viewMediaType)) {
                    if (!views.containsKey(requestedMediaType)) {
                        views.put(requestedMediaType, candidateView);
                        break;
                    }
                }
            }
        }
        if (!views.isEmpty()) {
            MediaType mediaType = views.firstKey();
            View view = views.get(mediaType);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Returning [" + view + "] based on requested media type '" + mediaType + "'");
            }
            return view;
        } else {
            return null;
        }
    }

    private boolean includes(MediaType first, MediaType other) {
        if (first == other) {
            return true;
        }
        if (first.getType().equals(other.getType())) {
            if (first.getSubtype().equals(other.getSubtype())) {
                return true;
            }
        }
        return false;
    }
}
