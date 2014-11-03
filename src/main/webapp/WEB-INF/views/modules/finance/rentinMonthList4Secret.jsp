<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>承租月记录管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出承租月记录数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/finance/rentMonth/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
		});
		
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
        function resets(){
			$("input[type=text]","#searchForm").val("");
			page();
		}
	</script>
</head>
<body>
<input id="rent.id" name="rent.id" type="hidden" value="${rentMonth.rent.id}"/>

		<form:form id="searchForm" modelAttribute="rentmonth" action="${ctx}/finance/rentMonth/?infotype=rentin&rent.id=${rent.id} " method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		</form:form>
	
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/finance/rentMonth/">承租月记录列表</a></li>
		<c:if test="${page.count == 0}">
			<shiro:hasPermission name="finance:rentMonth:edit"><li><a href="${ctx}/finance/rentMonth/rentinform?rent.id=${rentMonth.rent.id}&viewtype=noreturnlist">承租月记录添加</a></li></shiro:hasPermission>
		</c:if>
		
	</ul>

	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		<tr>
		<th>业务员</th>
		<th>付款方式</th>
		<th>承租时间</th>
		<th>月租金</th>
		<th>已付月份</th>
		<th class="sort nextpaydate">下次付租时间</th>
		<th>下次应付金额</th>
		<th>下次应付备注</th>
		<th>备注</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="rentMonth">
			<tr>
				<td>${rentMonth.person.name}</td>
				<td>${fns:getDictLabel(rentMonth.paytype, 'finance_rent_paytype', '')}</td>
				<td><fmt:formatDate value="${rentMonth.sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rentMonth.edate}" pattern="yyyy-MM-dd"/></td>
				<td>${rentMonth.rentmonth}</td>
				<td><fmt:formatDate value="${rentMonth.lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rentMonth.lastpayedate}" pattern="yyyy-MM-dd"/></td>
				<td>
				<fmt:formatDate value="${rentMonth.nextpaydate}"  pattern="yyyy-MM-dd" /></td>
				<td>${rentMonth.nextshouldamount}</td>
				<td>${rentMonth.nextshouldremark}</td>
				<td>${rentMonth.remarks}</td>

			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
