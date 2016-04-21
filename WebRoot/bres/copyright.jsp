<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script type="text/javascript">
function setAuthEndDate(){
	var authStartDate = document.getElementById("authStartDate").value;
	var authEndDate = document.getElementById("authEndDate").value;
	var authTimeLimit = document.getElementById("authTimeLimit").value; //授权时限
	if(isNaN(authTimeLimit) || authTimeLimit == null || authTimeLimit == ""){
		 return false;	
	}else{
		if(authTimeLimit == 0){
			$.alert("【授权期限】不能为0,请重新填写.");
			document.getElementById("authTimeLimit").value= "";
			document.getElementById("authEndDate").val("");
			return false;	
		}
		if(authTimeLimit > 100){
			if(confirm("【授权期限】太大,请确认是否填写正确？【确认】正确；【取消】重填")){
				return true;
			}else{
				document.getElementById("authTimeLimit").value= "";
				document.getElementById("authTimeLimit").focus();
				return false;
			}
		}
	}
	if(authStartDate != null && authStartDate !=""){
		$('#authEndDate').val(DateAdd("y",authTimeLimit,authStartDate));
	//	document.getElementById("authEndDate").value = ;
	}
}
</script>
<div class="portlet">
	                    <div class="portlet-title">
	                        <div class="caption">版权信息 <a href="javascript:;" onclick="togglePortlet(this)"><i
	                                class="fa fa-angle-up"></i></a></div>
	                    </div>
	                    <input type="hidden" id="copyrightObjectId" name="copyRightMetaData.objectId" value=""/>
	                    <div class="portlet-body">
	                        <div class="container-fluid">
	                            <div class="row">
	                                <div class="col-md-6">
	                                        <div class="form-group">
	                                            <label class="control-label col-md-4">版权类型：</label>
	                                            
	                                            <div class="col-md-8">
	                                                <app:select name="copyRightMetaData.crtType" indexTag="crtType" id="crtType"  selectedVal="${crtType}" headName="请选择"  headValue=""  />
	                                            </div>
	                                        </div>
	                                </div>
	                                <div class="col-md-6 filed" >
	                                    <div class="form-group">
	                                        <label class="control-label col-md-4">授权地区：</label>
	
	                                        <div class="col-md-8">
	                                            <app:select name="copyRightMetaData.authArea" indexTag="authArea" id="authArea"  selectedVal="" headName="请选择"  headValue=""  />
	                                        </div>
	                                    </div>
	                                </div>
	                                <div class="col-md-6 filed" >
	                                    <div class="form-group">
	                                        <label class="control-label col-md-4">授权渠道：</label>
	
	                                        <div class="col-md-8">
	                                            <app:select name="copyRightMetaData.authChannel" indexTag="authChannel" id="authChannel"  selectedVal="" headName="请选择"  headValue=""  />
	                                        </div>
	                                    </div>
	                                </div>
	                                <div class="col-md-6 filed" >
	                                    <div class="form-group">
	                                        <label class="control-label col-md-4">授权语言：</label>
	
	                                        <div class="col-md-8">
	                                            <app:select name="copyRightMetaData.authLanguage" indexTag="authLanguage" id="authLanguage"  selectedVal="" headName="请选择"  headValue=""  />
	                                        </div>
	                                    </div>
	                                </div>
	                                <div class="col-md-6 filed" >
	                                    <div class="form-group">
	                                        <label class="control-label col-md-4">许可权利种类：</label>
	
	                                        <div class="col-md-8">
	                                            <app:select name="copyRightMetaData.permitRight" indexTag="permitRight" id="permitRight"  selectedVal="" headName="请选择"  headValue=""  />
	                                        </div>
	                                    </div>
	                                </div>
	                                <div class="col-md-6 filed" >
	                                    <div class="form-group">
	                                        <label class="control-label col-md-4">合作模式：</label>
	
	                                        <div class="col-md-8">
	                                            <app:select name="copyRightMetaData.collaPattern" indexTag="collaPattern" id="collaPattern"  selectedVal="" headName="请选择"  headValue=""  />
	                                        </div>
	                                    </div>
	                                </div>
	                            </div>
	                            <div class="row">
	                                <div class="col-md-6">
	                                    <div class="form-group">
	                                        <label class="control-label col-md-4">授权时限：</label>
	
	                                        <div class="col-md-8">
	                                            <input type="text" id="authTimeLimit" name="copyRightMetaData.authTimeLimit" class="form-control" />
	                                        </div>
	                                    </div>
	                                </div>
	                                <div class="col-md-6">
	                                    <div class="form-group">
	                                        <label class="control-label col-md-4">授权时间：</label>
	
	                                        <div class="col-md-8">
	                                            <app:datetime property="copyRightMetaData.authStartDate" id="authStartDate" onFocus="WdatePicker({onpicked:setAuthEndDate(),maxDate:'#F{$dp.$D(\\'authEndDate\\')}'})" style="width: 47.3%;display:inline;"  cssClass="form-control Wdate" />&nbsp;-&nbsp;
				  								<app:datetime property="copyRightMetaData.authEndDate" id="authEndDate" onFocus="WdatePicker({minDate:'#F{$dp.$D(\\'authStartDate\\')}'})" style="width: 47.3%;display:inline;" cssClass="form-control Wdate"/>
	                                        </div>
	                                    </div>
	                                </div>
	                            </div>
	                            <div class="row">
	                                <div class="col-md-6">
	                                    <div class="form-group">
	                                        <label class="control-label col-md-4">版权人：</label>
	
	                                        <div class="col-md-8">
	                                            <input type="text" id="crtPerson" name="copyRightMetaData.crtPerson"  class="form-control" />
	                                        </div>
	                                    </div>
	                                </div>
	                                <div class="col-md-6">
	                                    <div class="form-group">
	                                        <label class="control-label col-md-4">版权授权人：</label>
	
	                                        <div class="col-md-8">
	                                            <input type="text" id="authorizer" name="copyRightMetaData.authorizer" class="form-control" />
	                                        </div>
	                                    </div>
	                                </div>
	                            </div>
	                            <div class="row">
	                                <div class="col-md-6">
	                                    <div class="form-group">
	                                        <label class="control-label col-md-4">合同编号：</label>
	
	                                        <div class="col-md-8">
	                                            <input type="text" id="contractCode" name="copyRightMetaData.contractCode" class="form-control" />
	                                        </div>
	                                    </div>
	                                </div>
	                            </div>
	                        </div>
	                    </div>
	                </div>