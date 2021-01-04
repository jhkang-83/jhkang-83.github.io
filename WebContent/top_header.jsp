<%@ page  session="true" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@page import="eswf.dataobject.Map"%>
<%@page import="volcano.custom.control.Helper"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
	//session 가져오기
	Helper helper = new Helper(request, new eswf.dataobject.Map());
	Map USER = helper.getUser();
	request.setAttribute("USER", USER);
%>
<!DOCTYPE html>
<html>
<link rel="stylesheet" type="text/css" href="/assets/css/application.css?ver=1">
<link rel="stylesheet" type="text/css" href="/assets/css/style.css?ver=1">
<link rel="stylesheet" type="text/css" href="/assets/css/ContractCss.css?ver=1">
<link rel="stylesheet" type="text/css" href="/assets/css/common.css?ver=1">
<link rel="stylesheet" type="text/css" href="/assets/css/jquery-ui.css?ver=1">
<link rel="stylesheet" type="text/css" href="/assets/css/jquery-ui.theme.css?ver=1">
<link rel="stylesheet" type="text/css" href="/assets/css/jquery.mCustomScrollbar.css?ver=1">
<link rel="stylesheet" type="text/css" href="/assets/css/jquery.fileupload.css?ver=1">
<link rel="stylesheet" type="text/css" href="/assets/css/tip-yellow.css?ver=1">
<head>
	<script src="/js/jquery-1.7.2.min.js"></script>
	<script src="/js/jquery-1.7.2.js"></script>
	<script src="/js/jquery-ui.min.js"></script>
	<script src="/js/jquery.form.js"></script>
	<script src="/js/poshytip/jquery.poshytip.js"></script>
	<script src="/js/common.js"></script>
	<script src="/js/fileupload/jquery.fileupload.js"></script>
	<script src="/js/fileupload/jquery.fileupload-process.js"></script>
	<script src="/js/fileupload/jquery.fileupload-ui.js"></script>
	<script src="/js/fileupload/jquery.fileupload-jquery-ui.js"></script>
	<script src="/js/fileupload/jquery.blueimp-gallery.min.js"></script>
	<script src="/js/fileupload/jquery.fileupload-validate.js"></script>
	<script src="/js/fileupload/jquery.iframe-transport.js"></script>
	<script src="/js/fileupload/main.js"></script>
	<script src="/js/popup.js"></script>
	<script type="text/javascript" src="/js/sha1.js"></script>
	<script type="text/javascript" src="/js/security.js"></script>
	<script type="text/javascript">
		function goOpen4U(){
			window.open("https://open4u.sk.com");
		}
	</script>
</head>
<header class="gnbHolder">
	<div class="headerLogo">
		<ul>
			<c:choose>
				<c:when test="${USER != null && USER.role_list != null}">
					<a href="/Main">
				</c:when>
				<c:otherwise>
					<a href="/loginSupplier">
				</c:otherwise>
			</c:choose>
			<c:if test="${USER != null}">
			</c:if>
			<img alt="" src="/assets/images/logo.png">
			</a>
		</ul>
	</div>

	<div class="topOpen4u">
		<a type="button" onclick="javacript:goOpen4U();">
			<img alt="" src="/assets/images/btn_topOpen4uGo.png">
		</a>
	</div>
</header>
</html>
