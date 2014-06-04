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
				top.$.jBox.confirm("确认要导出房屋包租数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/finance/rent/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});
			$("#allcheck").click(function(){
				$("input[name='rent_id']").attr("checked",$("#allcheck").attr("checked") == "checked"?true:false);
			})
			var rentidstemp = $("#rentids").val();

			$('input[name="rent_id"]').each(function(){ 
				if((","+rentidstemp+",").indexOf($(this).val()) != -1){
					$(this).attr("checked",true);
				}
			  }); 
			$("#autoPayfor").click(function(){
				top.$.jBox.confirm("确认要批量处理付租吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						  var s=''; 
						  $('input[name="rent_id"]:checked').each(function(){ 
						    s+=$(this).val()+','; 
						  }); 
						$("#searchForm").attr("action","${ctx}/finance/rent/batchProcessRentMonth");
						$("#rentids").val(s);
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');

			})
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function resets(){
			$("input[type=text],select","#searchForm").not("#pageNo,#pageSize").val("");
			$("#searchForm").submit();
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
		<li class="active"><a href="${ctx}/finance/rent/rentList4WillRentinPayfor">包租明细【即将要付租】列表</a></li>
		<li><a href="${ctx}/finance/rent/rentList4WillRentoutReceive">包租明细【即将要收租】列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="rent" action="${ctx}/finance/rent/rentList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="rentin_nextpayedate" name="rentin_nextpayedate" type="hidden" value="${paramMap.rentin_nextpayedate}"/>
		<input id="rentids" name="rentids" type="hidden" value="${paramMap.rentids}"/>
		<input id="infotype" name="infotype" type="hidden" value="rentin"/>
		<input id="autoPayfor" name="autoPayfor" class="btn btn-primary" type="button" value="批量付租"></>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th class="th_center">全选<input type="checkbox" id="allcheck" value=""  /></th>
			<th class="th_center">编号</th>
			<th class="th_center">房屋地址</th>
			<th class="th_center">业务员</th>
			<th class="th_center">付款方式</th>
			<th class="th_center">承租时间</th>
			<th class="th_center">已付月份</th>
			<th class="th_center">下次付租时间</th>
			<th class="th_center">月租金</th>
			<th class="th_center">收款户名</th>
			<th class="th_center">收款银行及营业网点</th>
			<th class="th_center">是否为兴业银行</th>
			<th class="th_center">应付金额</th>
			<shiro:hasPermission name="finance:rent:edit"><th rowspan="2" class="th_center">操作</th></shiro:hasPermission>
		</tr>

		
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="rent">
			<tr>
				<td><input type="checkbox" name="rent_id" value="${rent.id }"/></td>
				<td>${rent.business_num}</td>
				<td><a href="#" onclick="openWindow('${ctx}/finance/house/form?id=${rent.house.id}','${rent.house.name}')" title="${rent.name}">${fns:abbr(fns:replaceHtml(rent.name),25)}</a></td>
				<td>${rent.rentinMonths[0].person.name}</td>
				<td>${fns:getDictLabel(rent.rentinMonths[0].paytype, 'finance_rent_paytype', '')}</td>
				<td><fmt:formatDate value="${rent.rentinMonths[0].sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentinMonths[0].edate}" pattern="yyyy-MM-dd"/></td>
				<td><fmt:formatDate value="${rent.rentinMonths[0].lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentinMonths[0].lastpayedate}" pattern="yyyy-MM-dd"/></td>
				<td><fmt:formatDate value="${rent.rentinMonths[0].nextpaydate}"  pattern="yyyy-MM-dd" /></td>
				<td>${rent.rentinMonths[0].rentmonth}</td>
				<td>${rent.house.receive_username}</td>
				<td>${rent.house.receive_bank}</td>
				<td>${rent.house.is_xingyebank}</td>
				<td>${rent.rentinMonths[0].nextshouldamount}</td>
				<shiro:hasPermission name="finance:rent:edit"><td>
					<a href="${ctx}/finance/rent/form?id=${rent.id}">一键付租</a>
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
