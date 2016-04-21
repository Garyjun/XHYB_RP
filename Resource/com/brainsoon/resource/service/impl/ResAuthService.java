package com.brainsoon.resource.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resource.service.IResAuthService;
@Service
public class ResAuthService implements IResAuthService{
	  protected transient final Log logger = LogFactory.getLog(getClass());
	  /**
	    * 根据doi判断是否有访问资源权限
	    * @param doi
	    * @return
	 * @throws UnsupportedEncodingException 
	    */
	   public boolean  hasAuth(String doi) throws UnsupportedEncodingException{
		   boolean has=false;
		   String authTypes=LoginUserUtil.getAuthResTypes();
		   String authCodes=LoginUserUtil.getAuthResCodes();
		   logger.debug("authTypes *****"+authTypes);
		   logger.debug("authCodes *****"+authCodes);
		   has=hasAuth(doi,authCodes,authTypes);
		   return has;
		   
	   }
	   
	   /**
	    * 判断是否有访问资源权限
	    * @param doi
	    * @param authCodes
	    * @param authTypes
	    * @return
	 * @throws UnsupportedEncodingException 
	    */
	   public boolean  hasAuth(String doi,String authCodes,String authTypes) throws UnsupportedEncodingException{
		   boolean has=false;
		   HttpClientUtil http = new HttpClientUtil();
		   String url=WebappConfigUtil.getParameter("RES_AUTH_URL")+"?";
		   StringBuffer paraUrl=new StringBuffer("doi="+doi);
		   paraUrl.append("&privilage=").append(authCodes);
		   paraUrl.append("&authType=").append(authTypes);
		   String params = URLEncoder.encode(paraUrl.toString(), "utf8");
		   String result=http.executeGet(url+params);
		   if(result.equals("0")){
			   has=true;
		   }
		   return has;
	   }
}
