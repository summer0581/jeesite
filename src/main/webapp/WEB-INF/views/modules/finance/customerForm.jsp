<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户信息管理</title>
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
		<li><a href="${ctx}/finance/customer/">客户信息列表</a></li>
		<li class="active"><a href="${ctx}/finance/customer/form?id=${customer.id}">客户信息<shiro:hasPermission name="finance:customer:edit">${not empty customer.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="finance:customer:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="customer" action="${ctx}/finance/customer/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">名称:</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="200" class="required"/>
			</div>
		</div>
		<shiro:hasPermission name="finance:customer:social_context">
		<div class="control-group">
			<label class="control-label">客户的社会背景:</label>
			<div class="controls">
				<form:textarea path="social_context" htmlEscape="false" maxlength="500" class=""/>
			</div>
		</div>
		</shiro:hasPermission>
		<div class="control-group">
			<label class="control-label">电话号码:</label>
			<div class="controls">
				<form:input path="telephone" htmlEscape="false" maxlength="64" class="phone"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">工作:</label>
			<div class="controls">
				<form:input path="job" htmlEscape="false" maxlength="100" class=""/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">爱好:</label>
			<div class="controls">
				<form:input path="hobby" htmlEscape="false" maxlength="64" class=""/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">性别:</label>
			<div class="controls">
				<form:select path="sex">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('sys_user_sex')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">所属部门:</label>
			<div class="controls">
				<tags:treeselect id="office" name="office.id" value="${customer.office.id}" labelName="office.name" labelValue="${customer.office.name}"
					title="部门" url="/sys/office/treeData?type=2" cssClass="required"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="finance:customer:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
