<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户选择</title>
	<meta name="decorator" content="default"/>
<script type="text/javascript">
		$(document).ready(function() {
			$("input[name=id]").each(function(){
				var customerSelect = null;
				if (top.mainFrame.financeMainFrame){
					customerSelect = top.mainFrame.financeMainFrame.customerSelect;
				}else{
					customerSelect = top.mainFrame.customerSelect;
				}
				for (var i=0; i<customerSelect.length; i++){
					if (customerSelect[i][0]==$(this).val()){
						this.checked = true;
					}
				}
				$(this).click(function(){debugger;
					var id = $(this).val(), title = $(this).attr("title"), telephone = $(this).attr("telephone");
					if (top.mainFrame.financeMainFrame){
						top.mainFrame.financeMainFrame.customerSelectAddOrDel(id, title, telephone);
					}else{
						top.mainFrame.customerSelectAddOrDel(id, title, telephone);
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
			<th>工作</th>
			<th>爱好</th>
			<th>备注</th>
		<tbody>
		<c:forEach items="${page.list}" var="customer">
			<tr>
				<td style="text-align:center;"><input type="radio" name="id" value="${customer.id}" title="${fns:abbr(customer.name,40)}" telephone="${customer.telephone}"/></td>
				<td>${customer.name}</td>
				<td>${fns:getDictLabel(customer.sex, 'sys_user_sex', '未知')}</td>
				<td>${customer.telephone}</td>
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
