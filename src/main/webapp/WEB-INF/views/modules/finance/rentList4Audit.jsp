<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>包租明细审核管理</title>
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
			$("#searchForm").attr("action","${ctx}/finance/rent/rentList4Audit");
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
		
		function openWindow(url,personname){
			top.$.jBox.open("iframe:"+url, "查看["+personname+"]房屋明细",$(top.document).width()-220,$(top.document).height()-80,{
				buttons:{"确定":true}, loaded:function(h){
					$(".jbox-content", top.document).css("overflow-y","hidden");
					$(".jbox", top.document).css("top","10px");
				}
			});
		}
		
		function openWindow4Rent(url,personname){
			top.$.jBox.open("iframe:"+url, "查看["+personname+"]房屋明细",$(top.document).width()-220,$(top.document).height()-80,{
				buttons:{"确定":true}, loaded:function(h){
					$(".jbox-content", top.document).css("overflow-y","hidden");
					$(".jbox", top.document).css("top","10px");
				}
			});
		}
		//每日做账导出
		function setAudited(rent_id){
			$("#rent_id").val(rent_id);
			top.$.jBox.confirm("确认要通过审核吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/finance/rent/setAudited");
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
	</script>
</head>
<body>

	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/finance/rent/">包租明细列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="rent" action="${ctx}/finance/rent/rentList4Audit" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="rent_id" name="rent_id" type="hidden" value=""/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
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
			<th rowspan="2" class="th_center">编号</th>
			<th rowspan="2" class="th_center">房屋地址</th>
			<th colspan="5" class="th_center">承租情况</th>
			<th colspan="8" class="th_center">出租情况</th>
			<shiro:hasPermission name="finance:rent:edit"><th rowspan="2" class="th_center">操作</th></shiro:hasPermission>
		</tr>
		<tr>
			<th>业务员</th>
			<th>承租时间</th>
			<th>月租金</th>
			<th>已付月份</th>
			<th >下次付租时间</th>
			
			<th>业务员</th>
			<th>付款方式</th>
			<th>出租时间</th>
			<th>押金</th>
			<th>月租金</th>
			<th>已付月份</th>
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
				<td><fmt:formatDate value="${rent.rentinMonths[0].sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentinMonths[0].edate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentinMonths[0].rentmonth}</td>
				<td><fmt:formatDate value="${rent.rentinMonths[0].lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentinMonths[0].lastpayedate}" pattern="yyyy-MM-dd"/></td>
				<td><fmt:formatDate value="${rent.rentinMonths[0].nextpaydate}"  pattern="yyyy-MM-dd" /></td>
				<td>${rent.rentoutMonths[0].person.name}</td>
				<td>${fns:getDictLabel(rent.rentoutMonths[0].paytype, 'finance_rent_paytype', '')}</td>
				<td><fmt:formatDate value="${rent.rentoutMonths[0].sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentoutMonths[0].edate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentoutMonths[0].deposit}</td>
				<td>${rent.rentoutMonths[0].rentmonth}</td>
				<td><fmt:formatDate value="${rent.rentoutMonths[0].lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentoutMonths[0].lastpayedate}" pattern="yyyy-MM-dd"/></td>
				<td><fmt:formatDate value="${rent.rentoutMonths[0].nextpaydate}"  pattern="yyyy-MM-dd" /></td>
				<td><fmt:formatDate value="${rent.rentoutMonths[0].cancelrentdate}"  pattern="yyyy-MM-dd" /></td>
				<td>
    				<a href="#" onclick="openWindow4Rent('${ctx}/finance/rent/form?id=${rent.id}','${rent.house.name}')">审核记录</a>
    				<br/>
    				<a href="#" onclick="setAudited('${rent.id}')">通过审核</a>
				</td>
			</tr>
		</c:forEach>		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
