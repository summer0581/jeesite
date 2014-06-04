<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>房屋明细管理</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出房屋数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/finance/house/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#btnExport4Bank").click(function(){
				top.$.jBox.confirm("确认要导出房屋(批量转账)数据吗？","系统提示",function(v,h,f){ 
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/finance/house/export4bank");
						$("#searchForm").submit(); 
					} 
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});
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
	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/finance/house/import" method="post" enctype="multipart/form-data"
			style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('正在导入，请稍等...');"><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
			<a href="${ctx}/finance/house/import/template">下载模板</a>
		</form>
	</div>
	<ul class="nav nav-tabs">
		
		<li class=""><a href="${ctx}/finance/house/houseCancelRentlist">退租或已停租房屋明细列表</a></li>
		<li class="active"><a href="#">未租进的房屋明细列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="house" action="${ctx}/finance/house/houseNoRentinlist" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>地址 ：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>房东姓名 ：</label><form:input path="landlord.name" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
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
				<td><a href="${ctx}/finance/house/form?id=${house.id}">${house.name}</a></td>
				<td>${house.houses}</td>
				<td>${house.landlord.name}</td>
				<td>${house.landlord.telephone}</td>
				<td>${house.debit_card}</td>
				<td>${house.tenant.name}</td>
				<td>${house.tenant.telephone}</td>
				<td>${house.team_leader.name}</td>
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
