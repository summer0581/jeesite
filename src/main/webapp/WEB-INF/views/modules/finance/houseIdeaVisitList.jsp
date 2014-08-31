<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>房屋回访意见管理</title>
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
		function resets(){
			$("input[type=text],input[type=hidden],select","#searchForm").not("#pageNo,#pageSize").val("");
			page();
		}
        
		function showOrHidden(){
			$("#pro_search").fadeToggle("fast");
			if("true" != $("#showHighSearch").val()){
				$("#showHighSearch").val("true");
			}else{
				$("#showHighSearch").val("false");
			}
			
		}
		
		function addGenjin(id){
			top.$.jBox("iframe:${ctx}/finance/houseIdea/form?id="+id, {title:"回访修改",top:"25px", width: 800, height: 450,buttons:{"关闭":true},closed: function () { location.href = location.href }});
		}
	</script>
</head>
<body>

	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/finance/houseIdea/?type=genjin">房屋跟进意见列表</a></li>
		<li class="active"><a href="${ctx}/finance/houseIdea">房屋回访意见列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="houseIdea" action="${ctx}/finance/houseIdea/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="showHighSearch" name="showHighSearch" type="hidden" value="${paramMap.showHighSearch}"/>
		<input id="type" name="type" type="hidden" value="huifang"/>
		<label>回访内容 ：</label><input id="content" name="content" type="text" maxlength="50" class=" " value="${paramMap.content}"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		&nbsp;<input id="btnReset" class="btn btn-primary" onclick="resets()" type="button" value="重置"/>
		&nbsp;<input id="btnShow" class="btn btn-primary" onclick="showOrHidden()" type="button" value="高级查询"/>
		<div id="pro_search" style="margin-top:10px;${'true' eq paramMap.showHighSearch?'':'display:none;'}">
		<div style="margin-bottom:5px;">
			<label>回访房屋：</label>
			<input id="house_name" name="house_name" type="text" maxlength="20" class="input-small " value="${paramMap.house_name}"/>
			<label>回访人：</label>
			<tags:treeselect id="createBy_id" name="createBy_id" notAllowSelectParent="true" cssClass="input-small" value="${paramMap.createBy.id}" labelName="rentinperson.name" labelValue="${paramMap.createBy.name}"
					title="人员" url="/sys/user/treeData" />
			<label>回访时间：</label>
			<input id="createDate_sdate" name="createDate_sdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.createDate_sdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'createDate_edate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="createDate_edate" name="createDate_edate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.createDate_edate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'createDate_sdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:62%;">回访内容</th><th style="width:15%;">回访房屋</th><th style="width:12%;">回访人</th><th style="width:11%;">回访时间</th><shiro:hasPermission name="finance:houseIdea:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="houseIdea">
			<tr>
				<td >${houseIdea.content}</td>
				<td ><a href="${ctx}/finance/house/form?id=${houseIdea.house.id}">${houseIdea.house.name}</a></td>
				<td >${houseIdea.createBy.name}</td>
				<td ><fmt:formatDate value="${houseIdea.createDate}"  pattern="yyyy-MM-dd HH:mm:ss" /></td>
				<shiro:hasPermission name="finance:houseIdea:edit"><td>
    				<a href="javascript:void(0)" onclick="addGenjin('${houseIdea.id}')">修改</a>
    				<shiro:hasPermission name="finance:houseIdea:delete">
					<a href="${ctx}/finance/houseIdea/delete?id=${houseIdea.id}" onclick="return confirmx('确认要删除该房屋回访意见吗？', this.href)">删除</a>
					</shiro:hasPermission>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
