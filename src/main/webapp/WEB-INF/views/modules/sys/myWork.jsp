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
              <c:forEach items="${rentinlist}" var="rent">
              	 <tr>
                  <td>${rent.house.busi_id }</td>
                  <td>${rent.house.name }</td>
                  <td><fmt:formatDate value="${rent.rentin_sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentin_edate}" pattern="yyyy-MM-dd"/></td>
                  <td><fmt:formatDate value="${rent.rentin_lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentin_lastpayedate}" pattern="yyyy-MM-dd"/></td>
                  <td><fmt:formatDate value="${rent.rentin_nextpaydate}"  pattern="yyyy-MM-dd" />
	                  <shiro:hasPermission name="finance:rent:edit">
	                  	<a href="${ctx}/sys/mywork/rentHandle?id=${rent.id}&handletype=payRent" onclick="return confirmx('确认要付给房东租金吗？', this.href)">付租</a>
	                  </shiro:hasPermission>
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
             <c:forEach items="${rentoutlist}" var="rent">
              	 <tr>
                  <td>${rent.house.busi_id }</td>
                  <td>${rent.house.name }</td>
                  <td><fmt:formatDate value="${rent.rentout_sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentout_edate}" pattern="yyyy-MM-dd"/></td>
                  <td><fmt:formatDate value="${rent.rentout_lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentout_lastpayedate}" pattern="yyyy-MM-dd"/></td>
                  <td><fmt:formatDate value="${rent.rentout_nextpaydate}"  pattern="yyyy-MM-dd" />
                  <shiro:hasPermission name="finance:rent:edit">
						<a href='${ctx}/sys/mywork/rentHandle?id=${rent.id}&handletype=receiveRent' onclick="return confirmx('确认要收取租客租金吗？', this.href)" >收租</a>
					</shiro:hasPermission>
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
	</div>

</body>
</html>