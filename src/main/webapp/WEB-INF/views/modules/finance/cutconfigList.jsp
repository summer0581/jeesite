<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>包租提成设置管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出包租提成设置数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/finance/cutconfig/export");
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
			page();
		}
	</script>
</head>
<body>
	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/finance/cutconfig/import" method="post" enctype="multipart/form-data"
			style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('正在导入，请稍等...');"><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
			<a href="${ctx}/finance/cutconfig/import/template">下载模板</a>
		</form>
	</div>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/finance/cutconfig/">包租提成设置列表</a></li>
		<shiro:hasPermission name="finance:cutconfig:edit"><li><a href="${ctx}/finance/cutconfig/form">包租提成设置添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="cutconfig" action="${ctx}/finance/cutconfig/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>提成名称 ：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>类型：</label><form:select id="cut_code" path="cut_code" class="input-small"><form:option value="" label=""/><form:options items="${cutcodeList}" htmlEscape="false"/></form:select>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		&nbsp;<input id="btnReset" class="btn btn-primary" onclick="resets()" type="button" value="重置"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		<th>提成名称</th>
		<th>提成标示符</th>
		<th>提成类别</th>
		<th>对应身份</th>
		<th>提成系数</th>
		<th>备注</th>
		<shiro:hasPermission name="finance:cutconfig:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="cutconfig">
			<tr>
				<td><a href="${ctx}/finance/cutconfig/form?id=${cutconfig.id}">${cutconfig.name}</a></td>
				<td>${cutconfig.cut_code}</td>
				<td>${fns:getDictLabel(cutconfig.cut_type, 'finance_cutconfig_cuttype', '')}</td>
				<td>${fns:getDictLabel(cutconfig.person, 'finance_cutconfig_person', '')}</td>
				<td>${cutconfig.cut_num}</td>
				<td>${cutconfig.remarks}</td>
				<shiro:hasPermission name="finance:cutconfig:edit"><td>
    				<a href="${ctx}/finance/cutconfig/form?id=${cutconfig.id}">修改</a>
					<a href="${ctx}/finance/cutconfig/delete?id=${cutconfig.id}" onclick="return confirmx('确认要删除该包租提成设置吗？', this.href)">删除</a>
					<a href="${ctx}/finance/cutconfig/form?name=${cutconfig.name}&cut_code=${cutconfig.cut_code}&cut_type=${cutconfig.cut_type}">添加新项</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
