<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>出租月记录管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出出租月记录数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/finance/rentMonth/export");
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
        function resets(){
			$("input[type=text]","#searchForm").val("");
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/finance/rentMonth/import" method="post" enctype="multipart/form-data"
			style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('正在导入，请稍等...');"><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
			<a href="${ctx}/finance/rentMonth/import/template">下载模板</a>
		</form>
	</div>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/finance/rentMonth/?infotype=rentout&rent.id=${rentMonth.rent.id}">出租月记录列表</a></li>
		<shiro:hasPermission name="finance:rentMonth:edit"><li><a href="${ctx}/finance/rentMonth/rentoutform?rent.id=${rentMonth.rent.id}">出租月记录添加</a></li></shiro:hasPermission>
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
		<th>下次应收金额</th>
		<th>已收金额</th>
		<shiro:hasPermission name="finance:rentMonth:edit"><th>操作</th></shiro:hasPermission></tr></thead>
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
				<td>${rentMonth.amountreceived}</td>
				<shiro:hasPermission name="finance:rentMonth:edit"><td>
    				<a href="${ctx}/finance/rentMonth/rentoutform?id=${rentMonth.id}">修改</a>
					<a href="${ctx}/finance/rentMonth/delete?id=${rentMonth.id}" onclick="return confirmx('确认要删除该承租月记录吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>	<div class="pagination">${page}</div>
</body>
</html>
