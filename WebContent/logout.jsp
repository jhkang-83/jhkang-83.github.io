<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@page import="eswf.dataobject.Map"%>
<%@page import="volcano.custom.control.Helper"%>
<%
	//2016.11.21 cache 방지코드 추가 
	response.setDateHeader("Expires",-1);
	response.setHeader("Pragma","no-cache");   
	response.setHeader("Cache-Control","no-cache"); 
	response.setHeader("Cache-Control","no-store"); 
	session.invalidate();
	//session
	Helper helper = new Helper(request, new eswf.dataobject.Map());
	helper.logout();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Content-Style-Type" content="text/css">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Expires" content="-1">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Cache-Control" content="store">
<title>SK Broadband Procument Portal</title>
<link rel="shortcut icon" type="image/vnd.Microsoft.icon" href="/sk_logo.ico">
<style type="text/css" >
    body{margin:0;padding-top:150px}
    img {border:0;}
    h2{margin:0;padding:10px 20px}
    .wrapper{width:704px;margin:0 auto}
    .contain{width:704px;height:307px;background:url(/assets/images/logout.jpg) no-repeat top left}

</style>
</head>

<body>
<div class="wrapper">
    <h2><img src="/assets/images/logo.png" alt="" /></h2>
    <div class="contain">&nbsp;</div>
    
</div>


</body>
</html>
