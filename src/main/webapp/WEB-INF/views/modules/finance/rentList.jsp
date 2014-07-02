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
			
			$("#btnExport").click(function(){
				exportExcel4base();
			});
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});
			$("#btnLuhetong").click(function(){
				$.jBox("iframe:${ctx}/finance/rent/quickluruhetongform", {title:"快捷录合同",top:"25px", width: 1300, height: 630,buttons:{"关闭":true}});
			})
		});
		
		//基本导出
		function exportExcel4base(){
			top.$.jBox.confirm("确认要导出房屋包租数据吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					//var tempaction = $("#searchForm").attr();
					$("#searchForm").attr("action","${ctx}/finance/rent/export");
					$("#searchForm").submit();
					//$("#searchForm").attr("action",tempaction);
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');

		}
		
		//每日做账导出
		function exportExcel4perdaymake(){
			top.$.jBox.confirm("确认要导出房屋包租数据吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/finance/rent/export4bank");
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/finance/rent/rentList");
			$("#searchForm").submit();
        	return false;
        }
		function resets(){
			$("input[type=text],select","#searchForm").not("#pageNo,#pageSize").val("");
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
	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/finance/rent/import" method="post" enctype="multipart/form-data"
			style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('正在导入，请稍等...');"><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
			<a href="${ctx}/finance/rent/import/template">下载模板</a>
		</form>
	</div>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/finance/rent/">包租明细列表</a></li>
		<shiro:hasPermission name="finance:rent:edit"><li><a href="${ctx}/finance/rent/form">包租明细添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="rent" action="${ctx}/finance/rent/rentList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="showHighSearch" name="showHighSearch" type="hidden" value="${paramMap.showHighSearch}"/>
		<div><label>房屋地址：</label>
		<form:input path="name" htmlEscape="false" maxlength="50" class="input-small" value="${paramMap.name}"/>
		<label>编号：</label>
		<input name="business_num" maxlength="50" type="text" class="input-small digits" value="${paramMap.business_num}"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" onclick="return page();" type="submit" value="查询"/>
		&nbsp;<input id="btnReset" class="btn btn-primary" onclick="resets()" type="button" value="重置"/>
		&nbsp;<input id="btnShow" class="btn btn-primary" onclick="showOrHidden()" type="button" value="高级查询"/>
		<shiro:hasPermission name="finance:house:view">
			&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
		</shiro:hasPermission>
		<shiro:hasPermission name="finance:house:edit">
			&nbsp;<input id="btnImport" class="btn btn-primary" type="button" value="导入"/>
		</shiro:hasPermission>
		<shiro:hasPermission name="finance:house:view">
			&nbsp;<input id="btnLuhetong" class="btn btn-primary" type="button" value="快捷录合同"/>
		</shiro:hasPermission>		
		</div>
		<div id="pro_search" style="margin-top:10px;${'true' eq paramMap.showHighSearch?'':'display:none;'}">
		<div style="margin-bottom:5px;">
			<label>承租租金：</label>
			<input id="rentin_rentmonthmin" name="rentin_rentmonthmin" type="text" maxlength="10" class="input-small digits" value="${paramMap.rentin_rentmonthmin}"/>
			至
			<input id="rentin_rentmonthmax" name="rentin_rentmonthmax" type="text" maxlength="10" class="input-small digits" value="${paramMap.rentin_rentmonthmax}"/>
			<label>出租租金：</label>
			<input id="rentout_rentmonthmin" name="rentout_rentmonthmin" type="text" maxlength="10" class="input-small digits" value="${paramMap.rentout_rentmonthmin}"/>
			至
			<input id="rentout_rentmonthmax" name="rentout_rentmonthmax" type="text" maxlength="10" class="input-small digits" value="${paramMap.rentout_rentmonthmax}"/>
			<label>出租付款方式：</label>
				<select id="rentout_paytype" name="rentout_paytype" class="input-small" >
					<option value="" label="请选择"/>
					<c:forEach items="${fns:getDictList('finance_rent_paytype')}" var="dict">
						<c:choose >
							<c:when test="${dict.value == paramMap.rentout_paytype }">
								<option value="${dict.value}" selected="selected" label="">${dict.label}</option>
							</c:when>
							<c:otherwise>
								<option value="${dict.value}" label="">${dict.label}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
		</div>
		<div style="margin-bottom:5px;">
			<label>承租开始时间：</label>
			<input id="rentin_sdatesdate" name="rentin_sdatesdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentin_sdatesdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentin_sdateedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="rentin_sdateedate" name="rentin_sdateedate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentin_sdateedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentin_sdatesdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<label>承租结束时间：</label>
					<input id="rentin_edatesdate" name="rentin_edatesdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentin_edatesdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentin_edateedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="rentin_edateedate" name="rentin_edateedate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentin_edateedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentin_edatesdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		</div>
		<div style="margin-bottom:5px;">
			<label>出租开始时间：</label>
			<input id="rentout_sdatesdate" name="rentout_sdatesdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_sdatesdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentout_sdateedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="rentout_sdateedate" name="rentout_sdateedate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_sdateedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentout_sdatesdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<label>出租结束时间：</label>
					<input id="rentout_edatesdate" name="rentout_edatesdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_edatesdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentout_edateedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="rentout_edateedate" name="rentout_edateedate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_edateedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentout_edatesdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		</div>
		<div>
			<label>承租下次付租时间：</label>
			<input id="rentin_nextpaysdate" name="rentin_nextpaysdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentin_nextpaysdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentin_nextpayedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="rentin_nextpayedate" name="rentin_nextpayedate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentin_nextpayedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentin_nextpaysdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<label>出租下次付租时间：</label>
					<input id="rentout_nextpaysdate" name="rentout_nextpaysdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_nextpaysdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentout_nextpayedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="rentout_nextpayedate" name="rentout_nextpayedate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_nextpayedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentout_nextpaysdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		</div>
		<div>
			<label>出租提前退租时间：</label>
			<input id="rentout_cancelrentsdate" name="rentout_cancelrentsdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_cancelrentsdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentout_cancelrentedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="rentout_cancelrentedate" name="rentout_cancelrentedate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_cancelrentedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentout_cancelrentsdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th rowspan="2" class="th_center">编号</th>
			<th rowspan="2" class="th_center">房屋地址</th>
			<th colspan="6" class="th_center">承租情况</th>
			<th colspan="8" class="th_center">出租情况</th>
			<th rowspan="2" class="th_center">每月利润</th>
			<shiro:hasPermission name="finance:rent:edit"><th rowspan="2" class="th_center">操作</th></shiro:hasPermission>
		</tr>
		<tr>
			<th>业务员</th>
			<th>付款方式</th>
			<th>承租时间</th>
			<th>月租金</th>
			<th>已付月份</th>
			<th >下次付租时间</th>
			
			<th>业务员</th>
			<th>付款方式</th>
			<th>出租时间</th>
			<th>月租金</th>
			<th>已付月份</th>
			<th>已收金额</th>
			<th >下次收租时间</th>
			<th >提前退租时间</th>
		</tr>
		
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="rent">
			<tr>
				<td>${rent.business_num}</td>
				<td><a href="#" onclick="openWindow('${ctx}/finance/house/form?id=${rent.house.id}','${rent.house.name}')" title="${rent.house.name}">${fns:abbr(fns:replaceHtml(rent.house.name),25)}</a></td>
				<td>${rent.rentinMonths[0].person.name}</td>
				<td>${fns:getDictLabel(rent.rentinMonths[0].paytype, 'finance_rent_paytype', '')}</td>
				<td><fmt:formatDate value="${rent.rentinMonths[0].sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentinMonths[0].edate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentinMonths[0].rentmonth}</td>
				<td><fmt:formatDate value="${rent.rentinMonths[0].lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentinMonths[0].lastpayedate}" pattern="yyyy-MM-dd"/></td>
				<td><fmt:formatDate value="${rent.rentinMonths[0].nextpaydate}"  pattern="yyyy-MM-dd" /></td>
				<td>${rent.rentoutMonths[0].person.name}</td>
				<td>${fns:getDictLabel(rent.rentoutMonths[0].paytype, 'finance_rent_paytype', '')}</td>
				<td><fmt:formatDate value="${rent.rentoutMonths[0].sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentoutMonths[0].edate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentoutMonths[0].rentmonth}</td>
				<td><fmt:formatDate value="${rent.rentoutMonths[0].lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentoutMonths[0].lastpayedate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentoutMonths[0].amountreceived}</td>
				<td><fmt:formatDate value="${rent.rentoutMonths[0].nextpaydate}"  pattern="yyyy-MM-dd" /></td>
				<td><fmt:formatDate value="${rent.rentoutMonths[0].cancelrentdate}"  pattern="yyyy-MM-dd" /></td>
				<td>${rent.rentoutMonths[0].rentmonth-rent.rentinMonths[0].rentmonth}</td>
				<shiro:hasPermission name="finance:rent:edit"><td>
    				<a href="${ctx}/finance/rent/form?id=${rent.id}">修改</a>
    				<br/>
					<a href="${ctx}/finance/rent/delete?id=${rent.id}" onclick="return confirmx('确认要删除该包租明细吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
