<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>房屋明细管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/finance/house/">房屋明细列表</a></li>
		<shiro:hasPermission name="finance:house:edit"><li><a href="${ctx}/finance/house/form">房屋明细添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="house" action="${ctx}/finance/house/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>地址 ：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>房东姓名 ：</label><form:input path="landlord_name" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th>业务编号</th>
			<th>地址</th>
			<th>楼盘</th>
			<th>房东姓名</th>
			<th>房东联系方式</th>
			<th>转账卡号</th>
			<th>租户姓名</th>
			<th>租户联系方式</th>
			<th>组长</th>
			<shiro:hasPermission name="finance:house:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="house">
			<tr>
				<td>${house.busi_id}</td>
				<td><a href="${ctx}/finance/house/form?id=${house.id}">${house.name}</a></td>
				<td>${house.houses}</td>
				<td>${house.landlord_name}</td>
				<td>${house.landlord_telephone}</td>
				<td>${house.debit_card}</td>
				<td>${house.tenant_name}</td>
				<td>${house.tenant_telephone}</td>
				<td>${house.team_leader}</td>
				<shiro:hasPermission name="finance:house:edit"><td>
    				<a href="${ctx}/finance/house/form?id=${house.id}">修改</a>
					<a href="${ctx}/finance/house/delete?id=${house.id}" onclick="return confirmx('确认要删除该房屋明细吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
