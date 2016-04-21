<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>
<html>
    <head>
        <title>编辑</title>
        <style type="text/css">
			.cascade_drop_down {width:220px;}
		</style>
        
        <script type="text/javascript">
        	var waitPanel;
	        function edit(act){
				document.form1.action=act; 
				jQuery('#form1').submit();
			}
			jQuery(document).ready(function(){
				$('#form1').validationEngine('attach', {
					relative: true,
					overflownDIV:"#divOverflown",
					promptPosition:"centerRight",
					maxErrorsPerField:1,
					onValidationComplete:function(form,status){
						if(status){
							save();
						}
					}
				});
				
			});
			
			function save(){
				waitPanel = $.waitPanel('正在保存...',false);
				$('#form1').ajaxSubmit({
	 				method: 'post',//方式
	 				success:(function(response){
	 					callback(response);
	           		})
	 			});
			}
			function callback(data){
				waitPanel.close();
				top.index_frame.work_main.freshDataTable('data_div');
				$.closeFromInner();
			}
			
			 function changerSel(id,val) {
				    var idArray=id.split('_');
		        	var prefix=idArray[0];
		        	var level=parseInt(idArray[1]);
		        	var nextLevel=level+1;
		            var next = prefix+'_'+nextLevel;
		            if (next == null || next == '') {
		                return;
		            }
		           
		            //清空所有下级菜单数据
		            for(var i=nextLevel;i<5;i++){
		            	var theId=prefix+"_"+i;
		            	var theDiv=theId+'_div';
		            	$("#"+theId).empty();
		            	$("#"+theDiv).hide();
		            }
		            
		            $.ajax({
		                type:'post',
		                url: '${path}/module/getNextMenu.action',
		                data:'parentId='+val,
		                dataType:'json',
		                async : false,
		                success:function(data){
		                	if(data.length>1){
		                		$("#"+next+'_div').show();
		                	}
		                	for(var i=0;i<data.length;i++){
		                		 var module=data[i];
		                		 var opt=new Option(module.moduleName, module.id);
		                		 $("#" + next).append(opt);
		                	}
		                	$("#"+id).val(val);
		                },
		                error:function(){
		                    alert("failed.");
		                }
		            });
		        };
			
			  $(document).ready(function(){
			    $(".cascade_drop_down").change(
			    		function (){
			    			var id = $(this).attr("id") ;
			    			var val=$(this).val();
				    		changerSel(id,val);
			    		}
			    		
			    );
			    
			    //初始化上级菜单
			    var xpath=$("#xpath").val();
			    if(xpath!=''){
			    	var xpaths=xpath.split(",");
			    	for(var j=1;j<xpaths.length-1;j++){
			    		var selId="sel_"+(j);
			    		changerSel(selId,xpaths[j]);
			    		
			    	}
			    	
			    	
			    }
			    
			});
			  
		  //添加时重置方法
		  function clean(){
			  jQuery('#form1')[0].reset();
			  $('#moduleName').val("");
			  $('#displayOrder').val("");
			  $('#url').text("");
			  $('#sel_2').empty();
			  $('#sel_2_div').hide();
		  }
		  
		  //修改时重置方法
		  function updateClean(){
			  var moduleName = $('#newName').val();
			  var displayOrder = $('#newdis').val();
			  var url = $('#newUrl').val();
			  var css = $('#newCss').val();
			  $('#moduleName').val(moduleName);
			  $('#displayOrder').val(displayOrder);
			  $('#url').val(url);
			  $('#css').val(css);
			//初始化上级菜单
		     var xpath=$("#xpath").val();
		     if(xpath!=''){
		    	var xpaths=xpath.split(",");
		    	for(var j=1;j<xpaths.length-1;j++){
		    		var selId="sel_"+(j);
		    		changerSel(selId,xpaths[j]);
		    	}
		     }
		  }
		</script>

    </head>
     <body>
      	<div class="form-wrap">
      	<div>
      		<form:form action="${path}/module/add.action" name="form1" id="form1" method="POST" class="form-horizontal">
      		<form:hidden path="id"  />
      		<form:hidden path="dir"  />
      	    <input type="hidden" name="xpath" id="xpath" value="${xpath}"/>
        	 <div class="form-group">
		                <label for="moduleName" class="col-sm-4 control-label text-right" ><i class="must">*</i>菜单名</label>
		                 <div class="col-sm-5">
		                 <input type="hidden" id="newName" value="${command.moduleName}"/>
		                 <input id="moduleName" name="moduleName" class="form-control validate[required,maxSize[20]] text-input" value="${command.moduleName}"/>
		                 </div>  
			</div>	
			 <div class="form-group" id="sel_1_div">
		                <label for="parentId" class="col-sm-4 control-label text-right" >上级菜单</label>
		                   <div class="col-sm-5">
				                <%--  <form:select path="parentModule.id"  id="parentModule.id"  cssClass="form-control" >  
	   									 <form:option value="-1" label="无"/>  
	    						 			<form:options items="${parentModules}" itemValue="id" itemLabel="moduleName"/>  
	    						</form:select>   --%>
	    						
	    						<select name="lastMenu" id="sel_1" class="cascade_drop_down form-control">
	    						     <option value="-1">无</option>
                                      <c:forEach items="${parentModules}" var="item">  
                                         <option value="${item.id}">${item.moduleName}</option>
                                      </c:forEach>
	    						</select>
		                    </div>
		                   
			</div>	
			
			<div class="form-group" id="sel_2_div" style="display:none">
		                <label for="parentId" class="col-sm-4 control-label text-right" ></label>
		                    <div class="col-sm-5">
		                        <select name="lastMenu" id="sel_2" class="cascade_drop_down form-control">
		                          
		                    	</select>
		                    </div>
	    						
			</div>	
			<div class="form-group" id="sel_3_div" style="display:none">
		                <label for="parentId" class="col-sm-4 control-label text-right" ></label>
		                    <div class="col-sm-5">
		                        <select name="lastMenu" id="sel_3" class="cascade_drop_down form-control">
	    						
	    						</select>
		                    </div>
	    						
			</div>	
			
			
			 <div class="form-group">
		                <label for="displayOrder" class="col-sm-4 control-label text-right"><i class="must">*</i>显示顺序</label>
		                 <div class="col-sm-5">
		                <%--  <form:input  path="displayOrder"  class="form-control validate[required] text-input" /> --%>
		                 <input type="hidden" id="newdis" value="${command.displayOrder}"/>
		                 <input id="displayOrder" name="displayOrder" class="form-control validate[required] text-input" value="${command.displayOrder}"/>
		                 </div>  
			</div>	
			
			 <div class="form-group">
		                <label for="icon" class="col-sm-4 control-label text-right">图标样式</label>
		                 <div class="col-sm-5">
		                 <input id="newCss" value="${command.css}" type="hidden"/>
		                 <input id="css" name="css" value="${command.css}" class="form-control text-input"/>
		                 <%-- <form:input  path="css"  class="form-control text-input" /> --%>
		                 </div>  
			</div>	
			
			 <div class="form-group">
		                <label for="url" class="col-sm-4 control-label text-right" >url</label>
		                 <div class="col-sm-5">
		                 <%--  <form:textarea rows="3" cols="5" path="url" class="form-control text-input"/> --%>
		                  <input type="hidden" id="newUrl" value="${command.url}"/>
		                  <textarea rows="3" cols="5" class="form-control text-input" id="url" name="url">${command.url}</textarea>
		                 </div>
		                  <span class="help-block text-bottom">url示例:/module/list</span>
			</div>	
  		   <div class="form-group">
  		 		 <div class="col-sm-offset-4 col-sm-5">
			           		<input type="hidden" name="token" value="${token}" />
			           		<c:if test="${id>-1}">
   					  			 <button type="button"  class="btn btn-primary" onclick="edit('${path}/module/update.action')">保存</button>
   							 </c:if>
   							
   							  <c:if test="${id==-1}">
   							   <button type="button"  class="btn btn-primary" onclick="edit('${path}/module/add.action')">保存</button>
   							 </c:if>
   							  &nbsp;
   							  <c:if test="${id==-1}">
   							  	<button onclick="clean()" class="btn btn-primary">重置</button>
   							  </c:if>
   							 <c:if test="${id>-1}">
      							<input onclick="updateClean()" class="btn btn-primary" type="button" value="重置"/>
      						 </c:if> 
   							   &nbsp;
			            	 <input type="button" class="btn btn-primary"  value="关闭 " onclick="javascript:$.closeFromInner();"/>
				</div>
         </div>  
        </form:form>   
      	</div>
        
        
        </div>
        
        
    </body>
</html>