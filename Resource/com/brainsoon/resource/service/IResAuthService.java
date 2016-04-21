package com.brainsoon.resource.service;

import java.io.UnsupportedEncodingException;

public interface IResAuthService {
   /**
    * 根据doi判断是否有访问资源权限
    * @param doi
    * @return
 * @throws UnsupportedEncodingException 
    */
   public boolean  hasAuth(String doi) throws UnsupportedEncodingException ;
   
   /**
    * 判断是否有访问资源权限
    * @param doi
    * @param authCodes
    * @param authTypes
    * @return
 * @throws UnsupportedEncodingException 
    */
   public boolean  hasAuth(String doi,String authCodes,String authTypes) throws UnsupportedEncodingException;
}
