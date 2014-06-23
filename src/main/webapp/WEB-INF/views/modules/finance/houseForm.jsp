<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>房屋明细管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate({
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

		<div class="control-group">
			<label class="control-label">房东:</label>
			<div class="controls">
				<form:hidden id="landlord_id" path="landlord.id" htmlEscape="false" maxlength="64" class="input-xlarge"/>
				<input id="landlord_name" type="text" value="${house.landlord.name }" maxlength="64" class="input-xlarge" readonly="readonly"/>
				<a id="relationButton_landlord" href="javascript:" class="btn">选择房东</a>
				<script type="text/javascript">
					var landlordSelect = [];
					function landlordSelectAddOrDel(id,title){
						landlordSelect = []
						landlordSelect.push([id,title]);
						landlordSelectRefresh();
					}
					function landlordSelectRefresh(){
						$("#landlord_id").val("");
						$("#landlord_name").val("");
						if(landlordSelect.length > 0){
							$("#landlord_name").val(landlordSelect[landlordSelect.length-1][1]);
							$("#landlord_id").val(landlordSelect[landlordSelect.length-1][0]);
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
			<label class="control-label">转帐卡号:</label>
			<div class="controls">
				<form:input path="debit_card" htmlEscape="false" maxlength="64" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">租户:</label>
			<div class="controls">
				<form:hidden id="tenant_id" path="tenant.id" htmlEscape="false" maxlength="64" class="input-xlarge"/>
				<input id="tenant_name" type="text" value="${house.tenant.name }" maxlength="64" class="input-xlarge" readonly="readonly"/>
				<a id="relationButton_tenant" href="javascript:" class="btn">选择房屋</a>
				<script type="text/javascript">
					var tenantSelect = [];
					function tenantSelectAddOrDel(id,title){
						tenantSelect = []
						tenantSelect.push([id,title]);
						tenantSelectRefresh();
					}
					function tenantSelectRefresh(){
						$("#tenant_id").val("");
						$("#tenant_name").val("");
						if(tenantSelect.length > 0){
							$("#tenant_name").val(tenantSelect[tenantSelect.length-1][1]);
							$("#tenant_id").val(tenantSelect[tenantSelect.length-1][0]);
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
			<label class="control-label">是否需要下定金:</label>
			<div class="controls">
				<form:select path="is_needdeposit">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">组长:</label>
			<div class="controls">
				<tags:treeselect id="team_leader" name="team_leader.id" notAllowSelectParent="true" value="${house.team_leader.id}" labelName="team_leader.name" labelValue="${house.team_leader.name}"
					title="人员" url="/sys/user/treeData" cssClass=""/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">户型图:</label>
			<div class="controls">
                <input type="hidden" id="image" name="image" value="${house.imageSrc}" />
				<tags:ckfinder input="image" type="thumb" uploadPath="/finance/house" selectMultiple="true"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="finance:house:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
