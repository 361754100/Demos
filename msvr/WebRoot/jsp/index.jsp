<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>HRO 移动服务端</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="stylesheet" type="text/css" href="css/index.css">
	
	<script type="text/javascript" src="javascript/common/StringUtil.js"></script>
	<script type="text/javascript" src="javascript/common/domUtil.js"></script>
	<script type="text/javascript" src="javascript/jquery/jquery-1.11.3.js"></script>
  
	<script type="text/javascript">
		var base = "<%=basePath%>";
	
		function submitForm(){
			var bean = formToBean(document.getElementById("form"));
			var jsonStr = JSON.stringify(bean);
			
			$.ajax({
			   type: 'POST',
			   url: base+'user/login.do',
			   data: jsonStr,
			   contentType: 'application/json',
			   dataType: 'json',
			   success: function(rtObj){
			      $('#alertMsg').html( rtObj.username );
			   }
			});
		}
	</script>  
  </head>
  
  
  <body>
  	<a href="javascript:void(0);" style="text-decoration: none;">
  		<img alt="" src="image/jeep_light.jpg" border="0">
  	</a>
  	</br>
    <form id="form">
    	<label>用户名：</label>
    	<input type="text" id="username" name="username" value="">
    	<label>密码：</label>
    	<input type="password" id="passwd" name="passwd" >
    	<input type="button" id="submitBtn" value="登录" onclick="submitForm();">
    </form>
    </br>
    <label id="alertMsg"></label>
  </body>
</html>
