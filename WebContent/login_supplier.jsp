<%@page import="java.util.List"%>
<%@page import="volcano.custom.control.MainSummaryController"%>
<%@page import="java.util.Map"%>
<%@page import="eswf.managers.TransactionManager"%>
<%@page session="false"  language="java" extends="eswf.servlet.BaseHttpJsp" contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@page import="eswf.jdbc.*"%>
<%@page import="javax.servlet.http.HttpServletRequest" %>

<%
	response.setContentType("text/html");
	request.setCharacterEncoding("utf-8");
	response.setHeader("Pragma","no-cache");   
	response.setDateHeader("Expires",-1);
	response.setHeader("Cache-Control","no-store"); 
	
	String url = request.getRequestURL().toString();
	
	if(url.startsWith("http://") && url.indexOf("localhost") < 0) {
		url = url.replaceAll("http://", "https://");
		response.sendRedirect(url);
	}


%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Style-Type" content="text/css">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Expires" content="-1">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<title>SK Broadband Procument Portal</title>
<link rel="stylesheet" type="text/css" href="/assets/css/application.css?ver=1">
<link rel="stylesheet" type="text/css" href="/assets/css/style.css?ver=1">

<script type="text/javascript">
	
	var key = "12345678901234567890123456789012";
	
	function onKeyupEvent(event) {
		if(event.keyCode == 13) {
			loginProcess();
		}
	}
	
	function loginProcess(){
		var id = $('#usrid').val();
		var pwd = $('#usrpw').val();
		
		if(id == ""){
			alert("아이디를 입력하세요.");
			$('#usrid').focus();
			return false;
		}
		
		if(pwd == ""){
			alert("비밀번호를 입력하세요.");
			$('#usrpw').focus();
			return false;
		}
		
		$('#principal').val(encode(id));
		
		$('#credential').val(encpw(pwd));
		
		$('#loginForm').submit();
	}
	
	function onClickEvent(e) {
		if(e.id == "btnRegister") {
			fnPostGoto("/bp/emRegisterVendor", "view", "_self");
		}else if(e.id == "btnIdPwFind") {
			document.location.href = "FindIdPw";
		}else if(e.id == "bannerUsrInfo") {
			alert("로그인 후 사용해주세요.");
		}else if(e.id == "bannerSMP") {
			document.location.href = "/bp/emSupplierManageList";
		}else if(e.id == "bannerCowork") {
			document.location.href = "/bp/emCoworkList";
		}else if(e.id == "bannerQnA") {
			var params = {board_id: "40"};
			fnPostGoto("/bp/emBoardList", params, "_self");
		}
			
	}
	
	function encpw(value){
		var encPass = hex_sha1(value);
		return encPass.replace(/0/gi,'');
	}
	
	function encode(value) {
    	var encValue = AES_Encode(value);
    	return encValue;
    }
    
    function decode(value) {
    	var decValue = AES_Decode(value);
    	return decValue;
    }
    
    function AES_Encode(plain_text)
    {

        GibberishAES.size(256); 

        return GibberishAES.aesEncrypt(plain_text, key);

    }
    
    function AES_Decode(plain_text)
    {

        GibberishAES.size(256); 

        return GibberishAES.aesDecrypt(plain_text, key);

    }
	
</script>

</head>

<body>
	<div id="wrapper">
		<div class="headerCon fixed">
			<jsp:include page="top_header.jsp"></jsp:include>
	
			<!-- 네비 -->
			<jsp:include page="top_menu.jsp"></jsp:include>
		</div>

		<section id="container">
			<!-- left -->
			<aside id="aside">
				<div class="mainLoginBox">
					<form id="loginForm" name="loginForm" action="volcanoLogin" method="POST">
						<div>
							<span> 
								<input class="loginInput" type="text" id="usrid" name="usrid" placeholder="ID" title="아이디" onkeyup="javascript:onKeyupEvent(event);">
								<input class="loginInput" type="hidden" id="principal" name="principal">
							</span>
						</div>
						<div>
							<span> 
								<input class="loginInput" type="password" id="usrpw" name="usrpw" placeholder="PASSWORD" title="패스워드" onkeyup="javascript:onKeyupEvent(event);">
								<input class="loginInput" type="hidden" id="credential" name="credential">
							</span>
						</div>
						<button class="btnLogin" type="button" id="btnLogin" onclick="loginProcess()">
							<span title="로그인" class="btnText">로그인</span>
						</button>
					</form>
					<div class="linkBtnBox">
						<button id="btnRegister" class="loginLinkBtn" role="button" onclick="onClickEvent(this);">회원가입</button>
						<button id="btnRegisterInfo" class="loginLinkBtn" type="button">가입안내</button>
						<button id="btnIdPwFind" class="loginLinkBtn" type="button" onclick="onClickEvent(this);">ID/PW 찾기</button>
					</div>
				</div>

				<div class="mainBannerBox" id="bannerUsrInfo" onclick="onClickEvent(this);">
					<img alt="" src="assets/images/icon_mainLeft03.png" role="button">
					<label>자사정보관리</label>
				</div>

				<div class="mainBannerBox" id="bannerSMP" onclick="onClickEvent(this);">
					<img alt="" src="assets/images/icon_mainLeft05.png" role="button">
					<label>Supplier Portal Market</label>
				</div>

				<div class="mainBannerBox" id="bannerCowork" onclick="onClickEvent(this);">
					<img alt="" src="assets/images/icon_mainLeft02.png" role="button">
					<label>사업협력 및 제안</label>
				</div>

				<div class="mainBannerBox" id="bannerQnA" onclick="onClickEvent(this);">
					<img alt="" src="assets/images/icon_mainLeft01.png" role="button">
					<label>문의하기(Q＆A)</label>
				</div>

				<div class="mainOpen4uBanner">
					<img alt="" src="assets/images/open4u_banner2.png" role="button">
				</div>
			</aside>

			<!-- right -->
			<jsp:include page="main_right.jsp"></jsp:include>
		</section>

		<!-- 푸터 -->
		<jsp:include page="footer.jsp"></jsp:include>
	</div>
</body>
</html>