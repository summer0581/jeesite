<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>房屋跟进意见管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %> 
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/finance/houseIdea/");
			$("#searchForm").submit();
        	return false;
        }
		
		function addGenjin(){
			top.$.jBox("iframe:${ctx}/finance/houseIdea/innerForm?house.id=${houseIdea.house.id}", {title:"跟进新增",top:"25px", width: 800, height: 450,buttons:{"关闭":true},closed: function () { location.href = location.href }});
		}

	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="houseIdea" action="${ctx}/finance/houseIdea/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" onclick="addGenjin()" value="跟进新增"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:70%;">跟进内容</th><th style="width:15%;">跟进人</th><th style="width:15%;">跟进时间</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="houseIdea">
			<tr>
				<td >${houseIdea.content}</td>
				<td >${houseIdea.createBy.name}</td>
				<td ><fmt:formatDate value="${houseIdea.createDate}"  pattern="yyyy-MM-dd HH:mm:ss" /></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
