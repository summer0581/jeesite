<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>房屋区域权限设置管理</title>
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
			
			var areas_value = ","+$("#areas_value").val()+","; 
			$(":checkbox[name='areas']").each(function(i,e){
				if(areas_value.indexOf(","+$(this).val()+",") != -1){
					$(this).attr("checked","true");
				}
			})
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/finance/houseAreaRole/">房屋区域权限设置列表</a></li>
		<li class="active"><a href="${ctx}/finance/houseAreaRole/form?id=${houseAreaRole.id}">房屋区域权限设置<shiro:hasPermission name="finance:houseAreaRole:edit">${not empty houseAreaRole.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="finance:houseAreaRole:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="houseAreaRole" action="${ctx}/finance/houseAreaRole/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" id="areas_value" value="${ houseAreaRole.areas}"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">被授予人员:</label>
			<div class="controls">
				<tags:treeselect id="roleperson" name="roleperson" notAllowSelectParent="true" cssClass="input-small" value="${houseAreaRole.roleperson.id}" labelName="roleperson.name" labelValue="${houseAreaRole.roleperson.name}"
					title="人员" url="/sys/user/treeData" />
			</div>
			
		</div>
		<div class="control-group">
			<label class="control-label">授予的查看区域:</label>
			<div class="controls">
				<form:checkboxes path="areas" items="${allareas}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</div>
			
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="finance:houseAreaRole:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
