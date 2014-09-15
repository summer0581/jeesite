<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>房屋明细管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				rules: {
					name: {remote: "${ctx}/finance/house/checkHouseExsits?oldHouseName=" + encodeURIComponent('${house.name}')}
				},
				messages: {
					name: {remote: "房屋名已存在"}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			//是否不能修改进房信息
			var notCanEditHouseInInfo = ${!allColumnShow and not empty house.rentin_user and house.rentin_user ne fns:getUser()};
			if(notCanEditHouseInInfo){
				$("select,input").not("#btnSubmit,#btnCancel").not("[name='tenant.telephone']").attr("disabled","disabled");
				$("[id^=relationButton]").not("#relationButton_tenant,#relationButton_tenantadd").hide();
				$("[onclick^=image]").hide();
			}
			//是否不能修改出房信息
			var notCanEditHouseOutInfo = ${!allColumnShow and not empty house.rentout_user and house.rentout_user ne fns:getUser()};
			if(notCanEditHouseOutInfo){
				$("[name='tenant.telephone']").attr("disabled","disabled");
				$("#relationButton_tenant,#relationButton_tenantadd").hide();
				
			}
			$("#relationButton_landlordadd").click(function(){
				$("#landlord_name").removeAttr("readonly");
				$("#landlord_name,#landlord_id,#landlord_telephone").val("");
				alert("请直接填写【房东姓名】和【房东号码】即可，会自动添加或引用系统中的一个客户");
			});
			
			$("#relationButton_tenantadd").click(function(){
				$("#tenant_name").removeAttr("readonly");
				$("#tenant_name,#tenant_id,#tenant_telephone").val("");
				alert("请直接填写【租户姓名】和【租户号码】即可，会自动添加或引用系统中的一个客户");
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/finance/house/">房屋明细列表</a></li>
		<li class="active"><a href="${ctx}/finance/house/form?id=${house.id}">房屋明细<shiro:hasPermission name="finance:house:edit">${not empty house.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="finance:house:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="house" action="${ctx}/finance/house/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">地址:</label>
			<div class="controls">
				<input id="oldName" name="oldName" type="hidden" value="${house.name}">
				<form:input path="name" htmlEscape="false" maxlength="200" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">楼盘:</label>
			<div class="controls">
				<form:input path="houses" htmlEscape="false" maxlength="255" class=""/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">区域:</label>
			<div class="controls">
				<form:select path="area">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('house_area')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<!-- 当租进业务员为空，或者租进业务员等于当前业务员时，才能查看 -->
		<c:if test="${allColumnShow or empty house.rentin_user or house.rentin_user eq fns:getUser()}">
		<div class="control-group">
			<label class="control-label">房东:</label>
			<div class="controls">
				<form:hidden id="landlord_id" path="landlord.id" htmlEscape="false" maxlength="64" class="input-xlarge"/>
				<input id="landlord_name" name="landlord.name" type="text" value="${house.landlord.name }" maxlength="64" class="input-xlarge" readonly="readonly"/>
				<a id="relationButton_landlord" href="javascript:" class="btn">选择房东</a>
				<a id="relationButton_landlordadd" href="javascript:" class="btn">添加房东</a>
				<script type="text/javascript">
					var landlordSelect = [];
					function landlordSelectAddOrDel(id,title,telephone){
						landlordSelect = []
						landlordSelect.push([id,title,telephone]);
						landlordSelectRefresh();
					}
					function landlordSelectRefresh(){
						$("#landlord_id").val("");
						$("#landlord_name").val("");
						$("#landlord_telephone").val("");
						if(landlordSelect.length > 0){
							$("#landlord_name").val(landlordSelect[landlordSelect.length-1][1]);
							$("#landlord_id").val(landlordSelect[landlordSelect.length-1][0]);
							$("#landlord_telephone").val(landlordSelect[landlordSelect.length-1][2]);
						}
					}
					$("#relationButton_landlord").click(function(){
						top.$.jBox.open("iframe:${ctx}/finance/customer/selectList?pageSize=15&listtype=landlord", "添加房东",$(top.document).width()-220,$(top.document).height()-180,{
							buttons:{"确定":true}, loaded:function(h){
								$(".jbox-content", top.document).css("overflow-y","hidden");
							}
						});
					});
				</script>
				
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">房东联系电话:</label>
			<div class="controls">
				<form:input id="landlord_telephone" path="landlord.telephone" htmlEscape="false" maxlength="64" class=""/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">转帐卡号:</label>
			<div class="controls">
				<form:input path="debit_card" htmlEscape="false" maxlength="64" class="input-xxlarge"/>
			</div>
		</div>
		</c:if>
		<!-- 当租出业务员为空，或者租出业务员等于当前业务员时，才能查看 -->
		<c:if test="${allColumnShow or empty house.rentout_user or house.rentout_user eq fns:getUser()}">
		<div class="control-group">
			<label class="control-label">租户:</label>
			<div class="controls">
				<form:hidden id="tenant_id" path="tenant.id" htmlEscape="false" maxlength="64" class="input-xlarge"/>
				<input id="tenant_name" name="tenant.name" type="text" value="${house.tenant.name }" maxlength="64" class="input-xlarge" readonly="readonly"/>
				<a id="relationButton_tenant" href="javascript:" class="btn">选择租户</a>
				<a id="relationButton_tenantadd" href="javascript:" class="btn">添加租户</a>
				<script type="text/javascript">
					var tenantSelect = [];
					function tenantSelectAddOrDel(id,title,telephone){
						tenantSelect = []
						tenantSelect.push([id,title,telephone]);
						tenantSelectRefresh();
					}
					function tenantSelectRefresh(){
						$("#tenant_id").val("");
						$("#tenant_name").val("");
						$("#tenant_telephone").val("");
						if(tenantSelect.length > 0){
							$("#tenant_name").val(tenantSelect[tenantSelect.length-1][1]);
							$("#tenant_id").val(tenantSelect[tenantSelect.length-1][0]);
							$("#tenant_telephone").val(tenantSelect[tenantSelect.length-1][2]);
						}
					}
					$("#relationButton_tenant").click(function(){
						top.$.jBox.open("iframe:${ctx}/finance/customer/selectList?pageSize=15&listtype=tenant", "添加租户",$(top.document).width()-220,$(top.document).height()-180,{
							buttons:{"确定":true}, loaded:function(h){
								$(".jbox-content", top.document).css("overflow-y","hidden");
							}
						});
					});
				</script>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">租户联系电话:</label>
			<div class="controls">
				<form:input id="tenant_telephone" path="tenant.telephone" htmlEscape="false" maxlength="64" class=""/>
			</div>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">是否可租:</label>
			<div class="controls">
				<form:select path="is_canrent">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否为兴业银行:</label>
			<div class="controls">
				<form:select path="is_xingyebank">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">收款户名:</label>
			<div class="controls">
			<form:input path="receive_username" htmlEscape="false" maxlength="64" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">收款银行及营业网点:</label>
			<div class="controls">
				<form:input path="receive_bank" htmlEscape="false" maxlength="64" class="input-xxlarge"/>	
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否同城:</label>
			<div class="controls">
				<form:select path="is_samecity">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">汇入地址:</label>
			<div class="controls">
				<form:input path="remit_address" htmlEscape="false" maxlength="64" class="input-xxlarge"/>	
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">房屋来源:</label>
			<div class="controls"> 
				<form:select path="house_source">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('finance_house_source')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否可卖:</label>
			<div class="controls">
				<form:select path="is_cansale">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">价格:</label>
			<div class="controls">
				<form:input path="sale_price" htmlEscape="false" maxlength="64" class=""/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">面积:</label>
			<div class="controls">
				<form:input path="measure" htmlEscape="false" maxlength="64" class=""/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">朝向:</label>
			<div class="controls">
				<form:input path="direction" htmlEscape="false" maxlength="64" class=""/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">年代:</label>
			<div class="controls">
				<form:input path="age" htmlEscape="false" maxlength="64" class=""/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">装修:</label>
			<div class="controls">
				<form:input path="decorate" htmlEscape="false" maxlength="64" class=""/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">产权证号:</label>
			<div class="controls">
				<form:input path="prop_certno" htmlEscape="false" maxlength="64" class=""/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">国土证号:</label>
			<div class="controls">
				<form:input path="land_certno" htmlEscape="false" maxlength="64" class=""/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">家电家具:</label>
			<div class="controls">
				<form:input path="house_elec" htmlEscape="false" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">水:</label>
			<div class="controls">
				<form:input path="water_num" htmlEscape="false" maxlength="64" class="input-mini digits"/>顿
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电:</label>
			<div class="controls">
				<form:input path="elec_num" htmlEscape="false" maxlength="64" class="input-mini digits"/>度
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">户型:</label>
			<div class="controls">
				<form:input path="house_layout" htmlEscape="false" maxlength="64" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物业用途:</label>
			<div class="controls">
				<form:input path="wy_useful" htmlEscape="false" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">付款方式:</label>
			<div class="controls">
				<form:select path="paytype">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('finance_rent_paytype')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">结构:</label>
			<div class="controls">
				<form:input path="structure" htmlEscape="false" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">周边环境:</label>
			<div class="controls">
				<form:input path="arrond_environ" htmlEscape="false" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">房屋设施:</label>
			<div class="controls">
				<form:input path="housing_facilities" htmlEscape="false" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">面积描述:</label>
			<div class="controls">
				<form:input path="areadescribe" htmlEscape="false" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">交通情况:</label>
			<div class="controls">
				<form:input path="traffic_condition" htmlEscape="false" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">委托门店:</label>
			<div class="controls">
				<form:input path="entrust_store" htmlEscape="false" maxlength="64" class=""/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">登记门店:</label>
			<div class="controls">
				<form:input path="regist_store" htmlEscape="false" maxlength="64" class=""/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否需要下定金:</label>
			<div class="controls">
				<form:select path="is_needdeposit">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否未拿进房源:</label>
			<div class="controls">
				<form:select path="is_norentin">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">钥匙存放地址:</label>
			<div class="controls">
				<form:input path="key_saveplace" htmlEscape="false" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">档案位置:</label>
			<div class="controls">
				<form:input path="doc_place" htmlEscape="false" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>

		<!-- 当租进业务员为空，或者租进业务员等于当前业务员时，才能查看 -->
		
		<div class="control-group">
			<label class="control-label">租进业务员:</label>
			<div class="controls">
			<c:choose>
				<c:when test="${empty house.rentin_user or isSuperEditRole}">
					<tags:treeselect id="rentin_user" name="rentin_user.id" notAllowSelectParent="true" value="${house.rentin_user.id}" labelName="rentin_user.name" labelValue="${house.rentin_user.name}"
					title="人员" url="/sys/user/treeData" cssClass=""/>
				</c:when>
				<c:otherwise>
					<input type="text" value="${house.rentin_user.name}" readonly/>
				</c:otherwise>
			</c:choose>
			</div>
		</div>
		
		<!-- 当租进业务员为空，或者租进业务员等于当前业务员时，才能查看 -->
		<div class="control-group">
			<label class="control-label">租出业务员:</label>
			<div class="controls">
			<c:choose>
				<c:when test="${empty house.rentout_user or isSuperEditRole}">
					<tags:treeselect id="rentout_user" name="rentout_user.id" notAllowSelectParent="true" value="${house.rentout_user.id}" labelName="rentout_user.name" labelValue="${house.rentout_user.name}"
					title="人员" url="/sys/user/treeData" cssClass=""/> 
				</c:when>
				<c:otherwise>
					<input type="text" value="${house.rentout_user.name}" readonly/>
				</c:otherwise>
			</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">创建者:</label>
			<div class="controls">
				<input type="text" value="${house.createBy.name}" readonly/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<c:if test="${not empty house.id}">
		<div class="control-group">
			<label class="control-label">房屋跟进:</label>
			<div class="controls" style="height:300px;">
				<iframe src="${ctx}/finance/houseIdea/innerList?house.id=${house.id}&type=genjin"  style="border:0px;width:100%;height:100%;"></iframe>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">房屋回访:</label>
			<div class="controls" style="height:300px;">
				<iframe src="${ctx}/finance/houseIdea/innerList?house.id=${house.id}&type=huifang"  style="border:0px;width:100%;height:100%;"></iframe>
			</div>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">户型图:</label>
			<div class="controls">
                <input type="hidden" id="image" name="image" value="${house.imageSrc}" />
				<tags:ckfinder input="image" type="thumb" uploadPath="/finance/house" selectMultiple="true"/>
			</div>
		</div>
		<div class="form-actions">
		<shiro:hasAnyPermissions name="finance:house:add" >
		<c:if test="${empty house.id }">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
		</c:if>
		</shiro:hasAnyPermissions>
		<shiro:hasAnyPermissions name="finance:house:edit" >
		<c:if test="${not empty house.id }">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
		</c:if>
		</shiro:hasAnyPermissions>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
