<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>${fns:getConfig('productName')} 登录</title>
	<meta name="decorator" content="default"/>
    <link rel="stylesheet" href="${ctxStatic}/common/typica-login.css">
	<style type="text/css">
		.control-group{border-bottom:0px;}
		body {background: url(${ctxStatic}/images/login_bg.jpg) no-repeat  fixed;}
		#title{color:white; font-size:45px; font-weight: bold; text-align: center; }
		.login-form label {font-size:14px;}
		.container{width:340px; margin-left: 680px; margin-top: 230px;}
		.alert{margin-bottom: 0px;position: absolute;top: 200px;}
	</style>

	<script type="text/javascript">
		$(document).ready(function() {

			$("#loginForm").validate({
				rules: {
					validateCode: {remote: "${pageContext.request.contextPath}/servlet/validateCodeServlet"}
				},
				messages: {
					username: {required: "请填写用户名."},password: {required: "请填写密码."},
					validateCode: {remote: "验证码不正确.", required: "请填写验证码."}
				},
				errorLabelContainer: "#messageBox",
				errorPlacement: function(error, element) {
					error.appendTo($("#loginError").parent());
				} 
			});
		});
		// 如果在框架中，则跳转刷新上级页面
		if(self.frameElement && self.frameElement.tagName=="IFRAME"){
			parent.location.reload();
		}
	</script>
</head>
<body >
   
<div id="title"></div>
    <div class="container">
		<!--[if lte IE 6]><br/><div class='alert alert-block' style="text-align:left;padding-bottom:10px;"><a class="close" data-dismiss="alert">x</a><h4>温馨提示：</h4><p>你使用的浏览器版本过低。为了获得更好的浏览体验，我们强烈建议您 <a href="http://browsehappy.com" target="_blank">升级</a> 到最新版本的IE浏览器，或者使用较新版本的 Chrome、Firefox、Safari 等。</p></div><![endif]-->
		<%String error = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);%>
		
		<div id="messageBox" class="alert alert-error <%=error==null?"hide":""%>"><button data-dismiss="alert" class="close">×</button>
			<label id="loginError" class="error"><%=error==null?"":"com.thinkgem.jeesite.modules.sys.security.CaptchaException".equals(error)?"验证码错误, 请重试.":"用户或密码错误, 请重试." %></label>
		</div>
        <div id="login-wraper1">
            <form id="loginForm"  class="form login-form" action="${ctx}/login" method="post">
                <div class="body">
					<div class="control-group">
						<div class="controls">
							<input type="text" id="username" name="username" class="required" value="${username}" placeholder="登录名">
						</div>
					</div>
					
					<div class="control-group">
						<div class="controls">
							<input type="password" id="password" name="password" class="required" placeholder="密码"/>
						</div>
					</div>
					<c:if test="${isValidateCodeLogin}"><div class="validateCode">
						<label for="password">密　码：</label>
						<tags:validateCode name="validateCode" inputCssStyle="margin-bottom:0;"/>
					</div></c:if>
                </div>
                <div class="footer">
                    <label class="checkbox inline">
                        <input type="checkbox" id="rememberMe" name="rememberMe"> <span >记住我</span>
                    </label>
                    <input class="btn btn-primary" style="background-color: #ff4510;" type="submit" value="登 录"/>
                </div>
				
            </form>
        </div>
    </div>
    <footer class="white navbar-fixed-bottom">
		Copyright &copy; 2012-${fns:getConfig('copyrightYear')} <a href="${pageContext.request.contextPath}${fns:getFrontPath()}">${fns:getConfig('productName')}</a> 
    </footer>
  </body>
</html>