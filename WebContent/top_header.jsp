<%@ page language="java" extends="eswf.servlet.BaseHttpJsp" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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
<!-- <link rel="stylesheet" type="text/css" href="/assets/css/blueimp-gallery.min.css?ver=1"> -->
<head>
	<script src="/js/jquery-1.7.2.min.js"></script>
	<script src="/js/jquery-1.7.2.js"></script>
	<script src="/js/jquery-ui.min.js"></script>
	<script src="/js/jquery.form.js"></script>
	<script src="/js/poshytip/jquery.poshytip.js"></script>
	<script src="/js/common.js"></script>
	<script src="/js/fileupload/jquery.fileupload.js"></script>
	<script src="/js/fileupload/jquery.fileupload-process.js"></script>
<!-- 	<script src="/js/fileupload/jquery.fileupload-image.js"></script> -->
	<script src="/js/fileupload/jquery.fileupload-ui.js"></script>
	<script src="/js/fileupload/jquery.fileupload-jquery-ui.js"></script>
	<script src="/js/fileupload/jquery.blueimp-gallery.min.js"></script>
	<script src="/js/fileupload/jquery.fileupload-validate.js"></script>
	<script src="/js/fileupload/jquery.iframe-transport.js"></script>
<!-- 	<script src="/js/fileupload/load-image.all.min.js"></script> -->
<!-- 	<script src="/js/fileupload/canvas-to-blob.min.js"></script> -->
	<script src="/js/fileupload/main.js"></script>
<!-- 	<script src="/js/fileupload/tmpl.min.js"></script> -->
	<script type="text/javascript" src="/js/sha1.js"></script>
	<script type="text/javascript" src="/js/security.js"></script>
	<script type="text/javascript">
// 		function goIndex(){
// 			document.location.href = "/loginSupplier";
// 		}
		
		function goOpen4U(){
			window.open("https://open4u.sk.com");
			//document.location.href = "https://open4u.sk.com";
		}
	</script>
</head>
<header class="gnbHolder">
	<div class="headerLogo">
		<ul>
			<%if(!super.isLogin()) {%>
			<a href="/loginSupplier"> 
			<%}else {%>
			<a href="/Main"> 
			<%} %>
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