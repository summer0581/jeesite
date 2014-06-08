<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>我的工作</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
	.panel-div{width:48%;float:left;margin-right:2%;}
	.panel-title{margin-bottom:5px;margin-left:10px;}
	.clear{clear: both;} 
	</style>
	<script type="text/javascript">
		
	</script>
</head>
<body><tags:message content="${message}"/>
	<div class="panel-div">
	<div class="panel-title"><a href="${ctx}/finance/rent/rentList4WillRentinPayfor">您有<span class="badge badge-important">${rentinlistcount}</span>条付租要处理</a></div>
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
              <c:forEach items="${rentinlist.list}" var="rent">
              	 <tr>
                  <td>${rent.business_num }</td>
                  <td><a href="${ctx}/finance/rent/form?id=${rent.id}">${rent.house.name }</a></td>
                  <td><fmt:formatDate value="${rent.rentin_sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentin_edate}" pattern="yyyy-MM-dd"/></td>
                  <td><fmt:formatDate value="${rent.rentin_lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentin_lastpayedate}" pattern="yyyy-MM-dd"/></td>
                  <td><fmt:formatDate value="${rent.rentin_nextpaydate}"  pattern="yyyy-MM-dd" />
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
	</div>
	<div class="panel-div">
	<div class="panel-title"><a href="${ctx}/finance/rent/rentList4WillRentoutReceive">您有<span class="badge badge-important">${rentoutlistcount}</span>条收租要处理</a></div>
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
             <c:forEach items="${rentoutlist.list}" var="rent">
              	 <tr>
                  <td>${rent.business_num }</td>
                  <td><a href="${ctx}/finance/rent/form?id=${rent.id}">${rent.house.name }</a></td>
                  <td><fmt:formatDate value="${rent.rentout_sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentout_edate}" pattern="yyyy-MM-dd"/></td>
                  <td><fmt:formatDate value="${rent.rentout_lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentout_lastpayedate}" pattern="yyyy-MM-dd"/></td>
                  <td><fmt:formatDate value="${rent.rentout_nextpaydate}"  pattern="yyyy-MM-dd" />
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
	</div>
	<div class="clear"></div>
	<div class="panel-div">
	<div class="panel-title"><a href="${ctx}/finance/rent/rentList?rentin_edateedate=${rentwarndate}">您有<span class="badge badge-important">${rentinWRElistcount}</span>套租进房子将要到期，是否续签</a></div>
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
              <c:forEach items="${rentinWRElist}" var="rentMonth">
              	 <tr>
                  <td>${rentMonth.rent.business_num }</td>
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
	<div class="panel-title"><a href="${ctx}/finance/rent/rentList?rentout_edateedate=${rentwarndate}">您有<span class="badge badge-important">${rentoutWRElistcount}</span>套出租房子将要到期，是否续租</a></div>
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
             <c:forEach items="${rentoutWRElist}" var="rentMonth">
              	 <tr>
                  <td>${rentMonth.rent.business_num }</td>
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