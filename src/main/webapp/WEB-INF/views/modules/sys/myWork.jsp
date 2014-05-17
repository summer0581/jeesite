<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>我的工作</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
	.panel-div{width:48%;float:left;margin-right:2%;}
	.panel-title{margin-bottom:5px;margin-left:10px;}
	</style>
	<script type="text/javascript">
		
	</script>
</head>
<body><tags:message content="${message}"/>
	<div class="panel-div">
	<div class="panel-title"><a href="${ctx}/finance/rent/rentList">您有<span class="badge badge-important">${fn:length(rentinlist)}</span>条付租要处理</a></div>
			 <table class="table table-bordered">
              <thead>
                <tr>
                  <th>编号</th>
                  <th>房屋地址</th>
                  <th>承租时间</th>
                  <th>已付月份</th>
                  <th>下次付租时间</th>
                </tr>
              </thead>
              <tbody>
              <c:forEach items="${rentinlist}" var="rentMonth">
              	 <tr>
                  <td>${rentMonth.rent.house.busi_id }</td>
                  <td><a href="${ctx}/finance/rent/form?id=${rentMonth.rent.id}">${rentMonth.rent.house.name }</a></td>
                  <td><fmt:formatDate value="${rentMonth.sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rentMonth.edate}" pattern="yyyy-MM-dd"/></td>
                  <td><fmt:formatDate value="${rentMonth.lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rentMonth.lastpayedate}" pattern="yyyy-MM-dd"/></td>
                  <td><fmt:formatDate value="${rentMonth.nextpaydate}"  pattern="yyyy-MM-dd" />
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
	</div>
	<div class="panel-div">
	<div class="panel-title"><a href="${ctx}/finance/rent/rentList">您有<span class="badge badge-important">${fn:length(rentoutlist)}</span>条收租要处理</a></div>
			 <table class="table table-bordered">
              <thead>
                <tr>
                  <th>编号</th>
                  <th>房屋地址</th>
                  <th>出租时间</th>
                  <th>已收月份</th>
                  <th>下次收租时间</th>
                </tr>
              </thead>
              <tbody>
             <c:forEach items="${rentoutlist}" var="rentMonth">
              	 <tr>
                  <td>${rentMonth.rent.house.busi_id }</td>
                  <td><a href="${ctx}/finance/rent/form?id=${rentMonth.rent.id}">${rentMonth.rent.house.name }</a></td>
                  <td><fmt:formatDate value="${rentMonth.sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rentMonth.edate}" pattern="yyyy-MM-dd"/></td>
                  <td><fmt:formatDate value="${rentMonth.lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rentMonth.lastpayedate}" pattern="yyyy-MM-dd"/></td>
                  <td><fmt:formatDate value="${rentMonth.nextpaydate}"  pattern="yyyy-MM-dd" />
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
	</div>

</body>
</html>