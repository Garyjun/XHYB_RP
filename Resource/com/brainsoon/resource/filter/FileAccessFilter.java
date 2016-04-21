package com.brainsoon.resource.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.resource.service.IResAuthService;

public class FileAccessFilter implements Filter{
	protected transient final Log logger = LogFactory.getLog(getClass());
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		logger.debug("********run at FileAccessFilter *********");
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse rep=(HttpServletResponse)response;
		try {
			IResAuthService resAuthService = (IResAuthService)BeanFactoryUtil.getBean("resAuthService");
			String authTypes=(String)req.getSession().getAttribute(LoginUserUtil.AUTH_RES_TYPES);
			String authCodes=(String)req.getSession().getAttribute(LoginUserUtil.AUTH_RES_CODES);
//			authCodes=URLEncoder.encode(authCodes, "utf8");
			String url = req.getRequestURL().toString();
			logger.debug("url ******* "+url);
			String doi="";
			String platm="hsjc_";
			boolean hasAuth=true;
//			if(url.indexOf(platm)!=-1){
//				url=url.replaceAll("\\\\", "/");
//				doi=url.substring(url.indexOf(platm));
//				doi=doi.substring(0,doi.indexOf("/"));
//				doi=doi.replaceAll("_",".");
////				doi=doi.replaceAll("-","/");
//				logger.debug(doi);
//				hasAuth=resAuthService.hasAuth(doi, authCodes, authTypes);
//			}
			if(hasAuth==false){
				rep.sendRedirect(req.getContextPath()+"/security/denied.jsp");
			}
			if(hasAuth){
				chain.doFilter(request, response);// 把处理发送到下一个过滤器
			}
		} catch (Exception e) {
			e.printStackTrace();
			rep.sendRedirect(req.getContextPath()+"/security/denied.jsp");
		}
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
