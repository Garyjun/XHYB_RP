<%@page import="com.brainsoon.system.service.IModuleService"%>
<%@page import="com.brainsoon.system.model.Module"%>
<%@page import="java.util.List"%>
<%@page import="com.brainsoon.common.util.BeanFactoryUtil"%>
<%@page import="com.brainsoon.system.service.impl.ModuleService"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%
String moduleId=request.getParameter("pMenuId");
if(moduleId!=null){
	IModuleService moduleService=(IModuleService)BeanFactoryUtil.getBean("moduleService");
	List<Module> modules=moduleService.getModulesByParentId(Long.valueOf(moduleId));
	request.setAttribute("subModules", modules); 
}
%>
     <div id="sideWrap">
		           <ul class="page-sidebar-menu"  id="sideMenu">
		           		<li class="by-item-first"></li>
					        <c:forEach var="m" items="${subModules}"> 
									<sec:authorize ifAnyGranted="${m.roles}">
										<c:if test="${m.dir=='1'}">
											<li class="active open">
										       <a href="javascript:;">
					                        		<i class="${m.css}"></i>
					                       				 <span class="title">${m.moduleName}</span>
					                        			 <span class="arrow open"></span>
					                   			</a>
					                   		   <ul class="sub-menu" >
					                   		     <c:forEach var="child" items="${m.children}">
													<sec:authorize ifAnyGranted="${child.roles}">
													     <li>
													    
													     <c:choose>
										     <c:when test="${pageContext.request.contextPath=='\'}">
										       <a href="${child.url}" target="work_main">${child.moduleName}</a>
										     </c:when>
					                	         <c:otherwise>
					                	          <a href="${pageContext.request.contextPath}/${child.url}" target="work_main">${child.moduleName}</a>
					                	         </c:otherwise>
					                	           </c:choose>
													     </li>
											      </sec:authorize>
												</c:forEach>
												</ul>
											 </li>
					                   			 
										</c:if>
										
									<c:if test="${m.dir!='1'}">
										     <li>
										     <c:choose>
										     <c:when test="${pageContext.request.contextPath=='\'}">
										      <a href="${m.url}" target="work_main">
					                		          <i class="${m.css}"></i>
					                		        <span class="title">${m.moduleName}</span>
					                	         </a>
										     </c:when>
					                	         <c:otherwise>
					                	          <a href="${pageContext.request.contextPath}/${m.url}" target="work_main">
					                		          <i class="${m.css}"></i>
					                		        <span class="title">${m.moduleName}</span>
					                	         </a>
					                	         </c:otherwise>
					                	           </c:choose>
					                         </li>
									</c:if>
										
								</sec:authorize>
						</c:forEach>   
				</ul> 
     </div>
     
     
