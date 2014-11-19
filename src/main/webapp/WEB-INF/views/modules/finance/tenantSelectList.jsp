<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户选择</title>
	<meta name="decorator" content="default"/>
<script type="text/javascript">
		$(document).ready(function() {
			$("input[name=id]").each(function(){
				var tenantSelect = null;
				if (top.mainFrame.financeMainFrame){
					tenantSelect = top.mainFrame.financeMainFrame.tenantSelect;
				}else{
					tenantSelect = top.mainFrame.tenantSelect;
				}
				for (var i=0; i<tenantSelect.length; i++){
					if (tenantSelect[i][0]==$(this).val()){
						this.checked = true;
					}
				}
				$(this).click(function(){
					var id = $(this).val(), title = $(this).attr("title"), telephone = $(this).attr("telephone"), card = $(this).attr("card");;
					if (top.mainFrame.financeMainFrame){
						top.mainFrame.financeMainFrame.tenantSelectAddOrDel(id, title, telephone, card);
					}else{
						top.mainFrame.tenantSelectAddOrDel(id, title ,telephone, card);
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
	<form:form id="searchForm" modelAttribute="customer" action="${ctx}/finance/customer/selectList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="listtype" name="listtype" type="hidden" value="${listtype}"/>
		<label>姓名 ：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<tr><th style="text-align:center;">选择</th>
			<th>名称</th>
			<th>性别</th>
			<th>电话号码</th>
			<th>身份证</th>
			<th>工作</th>
			<th>爱好</th>
			<th>备注</th>
		<tbody>
		<c:forEach items="${page.list}" var="customer">
			<tr>
				<td style="text-align:center;"><input type="radio" name="id" value="${customer.id}" title="${fns:abbr(customer.name,40)}" telephone="${customer.telephone}"  card="${customer.card}"/></td>
				<td>${customer.name}</td>
				<td>${fns:getDictLabel(customer.sex, 'sys_user_sex', '未知')}</td>
				<td>${customer.telephone}</td>
				<td>${customer.card}</td>
				<td>${customer.job}</td>
				<td>${customer.hobby}</td>
				<td>${customer.remark}</td>
				
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	</div>
</body>
</html>
