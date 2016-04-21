package com.brainsoon.common.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 
 * @ClassName: IndexController 
 * @Description:  跳转首页控制器
 * @author tanghui 
 * @date 2013-8-10 下午8:41:18 
 *
 */
@Controller("indexController")
@RequestMapping(value="/bswf") 
public class IndexController {
    
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request){
        return "index";
    }
    
    @RequestMapping(value = "/success")
    public String success(HttpServletRequest request){
        return "/inc/success";
    }

}
