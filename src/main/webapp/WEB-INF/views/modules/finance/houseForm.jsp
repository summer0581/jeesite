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
			<label class="control-label">业务编号:</label>
			<div class="controls">
				<form:input path="busi_id" htmlEscape="false" maxlength="200" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">地址:</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="200" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">楼盘:</label>
			<div class="controls">
				<form:input path="houses" htmlEscape="false" maxlength="255" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">房东姓名:</label>
			<div class="controls">
				<form:input path="landlord_name" htmlEscape="false" maxlength="200" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">房东联系方式:</label>
			<div class="controls">
				<form:input path="landlord_telephone" htmlEscape="false" maxlength="64" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">转帐卡号:</label>
			<div class="controls">
				<form:input path="debit_card" htmlEscape="false" maxlength="64" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">租户姓名:</label>
			<div class="controls">
				<form:input path="tenant_name" htmlEscape="false" maxlength="64" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">租户联系方式:</label>
			<div class="controls">
				<form:input path="tenant_telephone" htmlEscape="false" maxlength="64" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">组长:</label>
			<div class="controls">
				<tags:treeselect id="team_leader" name="team_leader.id" notAllowSelectParent="true" value="${house.team_leader.id}" labelName="team_leader.name" labelValue="${house.team_leader.name}"
					title="人员" url="/sys/user/treeData" cssClass="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="finance:house:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
