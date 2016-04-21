package com.brainsoon.common.web.view;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.view.JstlView;

/**
 * 
 * @ClassName: FwJstlView 
 * @Description:  添加对于FwCNViewResolver.java 的content-type支持
 * @author tanghui 
 * @date 2013-8-10 下午9:04:05 
 *
 */
public class FwJstlView extends JstlView {
    
    @Override
    protected void exposeHelpers(HttpServletRequest request) throws Exception {
        super.exposeHelpers(request);
    }
    
}
