<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>房屋选择</title>
	<meta name="decorator" content="default"/>
<script type="text/javascript">
		$(document).ready(function() {
			$("input[name=id]").each(function(){
				var houseSelect = null;
				if (top.mainFrame.financeMainFrame){
					houseSelect = top.mainFrame.financeMainFrame.houseSelect;
				}else{
					houseSelect = top.mainFrame.houseSelect;
				}
				for (var i=0; i<houseSelect.length; i++){
					if (houseSelect[i][0]==$(this).val()){
						this.checked = true;
					}
				}
				$(this).click(function(){
					var id = $(this).val(), title = $(this).attr("title");
					if (top.mainFrame.financeMainFrame){
						top.mainFrame.financeMainFrame.houseSelectAddOrDel(id, title);
					}else{
						top.mainFrame.houseSelectAddOrDel(id, title);
					}
				});
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
<div style="margin:10px;">
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
			<tr><th style="text-align:center;">选择</th>
			<th>业务编号</th>
			<th>地址</th>
			<th>楼盘</th>
			<th>房东姓名</th>
			<th>房东联系方式</th>
			<th>转账卡号</th>
			<th>租户姓名</th>
			<th>租户联系方式</th>
			<th>组长</th>
		<tbody>
		<c:forEach items="${page.list}" var="house">
			<tr>
				<td style="text-align:center;"><input type="radio" name="id" value="${house.id}" title="${fns:abbr(house.name,40)}" /></td>
				<td>${house.busi_id}</td>
				<td><a href="${ctx}/finance/house/form?id=${house.id}">${house.name}</a></td>
				<td>${house.houses}</td>
				<td>${house.landlord_name}</td>
				<td>${house.landlord_telephone}</td>
				<td>${house.debit_card}</td>
				<td>${house.tenant_name}</td>
				<td>${house.tenant_telephone}</td>
				<td>${house.team_leader}</td>
				
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	</div>
</body>
</html>
