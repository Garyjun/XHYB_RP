<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>
<html>
    <head>
        <title>编辑</title>
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
	            	//删除匹配的元素集合中所有的子节点。
	            	$("#"+theId).empty();
	            	//隐藏显示的元素
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
	        	console.info('${modules}');
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
			    	for(var j=1;j<xpaths.length;j++){
			    		var selId="sel_"+(j);
			    		changerSel(selId,xpaths[j]);
			    		
			    		
			    	}
			    	
			    	
			    }
			    
			    
			});
	     
	    //添加时重置方法 
		function clean(){
			jQuery('#form1')[0].reset();
			$('#privilegeName').val("");
			$('#displayOrder').val("");
			$('#urls').text("");
			$('#sel_2').empty();
			$('#sel_2_div').hide();
		}
		//修改时重置方法
		function updateClean(){
			var privilegeName = $('#newName').val();
			var displayOrder = $('#newdis').val();
			var urls = $('#newUrl').val();
			$('#privilegeName').val(privilegeName);
			$('#displayOrder').val(displayOrder);
			$('#urls').val(urls);
			
			 var xpath=$("#xpath").val();
			    if(xpath!=''){
			    	var xpaths=xpath.split(",");
			    	for(var j=1;j<xpaths.length;j++){
			    		var selId="sel_"+(j);
			    		changerSel(selId,xpaths[j]);
			    	}
			    }
		}
		</script>

    </head>
     <body>
      	<div class="form-wrap">
      		<form:form action="${path}/privilege/add.action" name="form1" id="form1" method="POST" class="form-horizontal">
      		<form:hidden path="id"/>
      		<input type="hidden" name="xpath" id="xpath" value="${xpath}"/>
        	  <div class="form-group">
		                <label for="privilegeName" class="col-sm-4 control-label text-right" ><i class="must">*</i>权限名</label>
		                 <div class="col-sm-5">
		                 <input type="hidden" id="newName" value="${command.privilegeName}">
		                 <input id="privilegeName" name="privilegeName" class="form-control validate[required,maxSize[20]] text-input" value="${command.privilegeName}"/>
		                 <%-- <form:input  path="privilegeName" id="privilegeName"  class="form-control validate[required,maxSize[20]] text-input" /> --%>
		                 </div>  
			</div>	
			<%--  <div class="form-group">
		                <label for="command.fullName" class="col-sm-4 control-label text-right" >所属菜单</label>
		                   <div class="col-sm-5">
			                   <form:select path="module.id"  id="module.id"  class="form-control"  >  
	    						 			<form:options items="${modulesMap}" />  
	    						</form:select> 
		                    </div>
			</div>	 --%>
			 <div class="form-group" id="sel_1_div">
		                <label for="parentId" class="col-sm-4 control-label text-right" >所属菜单</label>
		                   <div class="col-sm-5">
				                <%--  <form:select path="parentModule.id"  id="parentModule.id"  cssClass="form-control" >  
	   									 <form:option value="-1" label="无"/>  
	    						 			<form:options items="${parentModules}" itemValue="id" itemLabel="moduleName"/>  
	    						</form:select>   --%>
	    						
	    						<select name="menu" id="sel_1" class="cascade_drop_down form-control">
                                      <c:forEach items="${modules}" var="item">  
                                      	<c:if test="${item.platformId == APP_USER_SESSION_KEY.platformId}">
	                                         <option value="${item.id}">${item.moduleName}</option>
                                      	</c:if>
                                      </c:forEach>
	    						</select>
		                    </div>
		                   
			</div>	
			
			<div class="form-group" id="sel_2_div" style="display:none">
		                <label for="parentId" class="col-sm-4 control-label text-right" ></label>
		                    <div class="col-sm-5">
		                        <select name="menu" id="sel_2" class="cascade_drop_down form-control">
		                          
		                    	</select>
		                    </div>
	    						
			</div>	
			<div class="form-group" id="sel_3_div" style="display:none">
		                <label for="parentId" class="col-sm-4 control-label text-right" ></label>
		                    <div class="col-sm-5">
		                        <select name="menu" id="sel_3" class="cascade_drop_down form-control">
	    						
	    						</select>
		                    </div>
	    						
			</div>	
			<div class="form-group" id="sel_4_div" style="display:none">
		                <label for="parentId" class="col-sm-4 control-label text-right" ></label>
		                    <div class="col-sm-5">
		                        <select name="menu" id="sel_4" class="cascade_drop_down form-control">
	    						
	    						</select>
		                    </div>
	    						
			</div>	
			
			
			 <div class="form-group">
		                <label for="displayOrder" class="col-sm-4 control-label text-right"><i class="must">*</i>显示顺序</label>
		                 <div class="col-sm-5">
		                 <input type="hidden" id="newdis" value="${command. displayOrder}"/>
		                 <input id="displayOrder" name="displayOrder" class="form-control validate[required]" value="${command. displayOrder}"/>
		                 <%-- <form:input  path="displayOrder"  class="form-control validate[required]" /> --%>
		                 </div>  
			</div>	
			
			 <div class="form-group">
		                <label for="urls" class="col-sm-4 control-label text-right" ><i class="must">*</i>url</label>
		                  <div class="col-sm-5">
		                  <input type="hidden" value="${command.urls}" id="newUrl"/>
		                  <textarea rows="5" cols="" class="form-control text-input validate[required]" id="urls" name="urls" >${command.urls}</textarea>
		                 <%-- <form:textarea rows="5"  path="urls" class="form-control text-input validate[required]" ></form:textarea> --%>
		                 </div>
                         <span class="help-block">url示例:/privilege/list</span>

			</div>	
			
			<div class="form-group">
   					 <div class="col-sm-offset-4 col-sm-5" align="center">
   					 <input type="hidden" name="token" value="${token}" />
   					 <c:if test="${id>-1}">
   					   <button type="button"  class="btn btn-primary" onclick="edit('${path}/privilege/update.action')">保存</button>
   					 </c:if>
   					  <c:if test="${id==-1}">
   					   <button type="button"  class="btn btn-primary" onclick="edit('${path}/privilege/add.action')">保存</button>
   					 </c:if>
   					 &nbsp;
   					 <c:if test="${id==-1}">
      					<button onclick="clean()" class="btn btn-primary">重置</button>
      				</c:if>
      				<c:if test="${id>-1}">
      					<input onclick="updateClean()" class="btn btn-primary" type="button" value="重置"/>
      				</c:if> 
   					&nbsp; 
			            <input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
			       
   					 </div>
  		  </div>
           
        </form:form>   
        
        </div>
    </body>
</html>