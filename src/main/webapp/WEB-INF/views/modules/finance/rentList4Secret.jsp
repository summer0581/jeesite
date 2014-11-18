<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>包租明细管理</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<style type="text/css">
	.table thead th.th_center{
		text-align: center;
		vertical-align: middle;
	}
	</style>
	
	<script type="text/javascript">
		$(document).ready(function() {
			$("#searchForm").validate();
			$.initSortTable("contentTable",page);
			
		});
		
		
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/finance/rent/rentList");
			$("#searchForm").submit();
        	return false;
        }
		function resets(){
			$("input[type=text],input[type=hidden],select","#searchForm").not("#pageNo,#pageSize").val("");
			page();
		}
		
		function openWindow(url,personname){
			top.$.jBox.open("iframe:"+url, "查看["+personname+"]房屋明细",$(top.document).width()-220,$(top.document).height()-80,{
				buttons:{"确定":true}, loaded:function(h){
					$(".jbox-content", top.document).css("overflow-y","hidden");
					$(".jbox", top.document).css("top","10px");
				}
			});
		}
		
		
	</script>
</head>
<body>

	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/finance/rent/rentList?viewtype=rent4Secret">包租明细列表</a></li>
		<!--<shiro:hasPermission name="finance:rent:edit"><li><a href="${ctx}/finance/rent/form?viewtype=noreturnlist">包租明细添加</a></li></shiro:hasPermission>-->
	</ul>
	<form:form id="searchForm" modelAttribute="rent" action="${ctx}/finance/rent/rentList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="viewtype" name="viewtype" type="hidden" value="rent4Secret"/>
		<input id="notcancelrentonly" name="notcancelrentonly" type="hidden" value="${paramMap.notcancelrentonly}"/>
		<input id="order" name="order" type="hidden" value="${paramMap.order}"/>
		<input id="showHighSearch" name="showHighSearch" type="hidden" value="${paramMap.showHighSearch}"/>
		<div><label>房屋地址：</label>
		<form:input path="name" htmlEscape="false" maxlength="50" class="input-small" value="${paramMap.name}"/>
		<label>编号：</label>
		<input name="business_num" maxlength="50" type="text" class="input-small digits" value="${paramMap.business_num}"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" onclick="return page();" type="submit" value="查询"/>
		&nbsp;<input id="btnReset" class="btn btn-primary" onclick="resets()" type="button" value="重置"/>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th  class="th_center">编号</th>
			<th  class="th_center">房屋地址</th>
			<shiro:hasPermission name="finance:rent:edit"><th  class="th_center">操作</th></shiro:hasPermission>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="rent">
			<tr>
				<td>${rent.business_num}</td>
				<td><a href="#" onclick="openWindow('${ctx}/finance/house/form?viewtype=noreturnlist&id=${rent.house.id}','${rent.house.name}')" title="${rent.house.name}">${fns:abbr(fns:replaceHtml(rent.house.name),25)}</a></td>
				<shiro:hasPermission name="finance:rent:edit"><td>
    				<a href="${ctx}/finance/rent/form?viewtype=noreturnlist&id=${rent.id}">修改</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
