<%@ page session="true" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@page import="eswf.dataobject.Map"%>
<%@page import="volcano.custom.control.Helper"%>
<%@page import="javax.servlet.RequestDispatcher"%>
<%@ page import="java.net.InetAddress" %>
<%@ page import="java.net.UnknownHostException" %>
<%@ page import="java.util.StringTokenizer" %>
<%@page import="javax.servlet.http.HttpServletRequest" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
	String url = request.getRequestURL().toString();
	if( url.startsWith("http://") && url.indexOf("localhost") < 0 )
	{
		url = url.replace("http://", "https://");
		response.sendRedirect(url);
	}

	//session
	Helper helper = new Helper(request, new eswf.dataobject.Map());
	Map USER = helper.getUser();
	request.setAttribute("USER", USER);

	final String localhost = InetAddress.getLocalHost().toString();
	final StringTokenizer st = new StringTokenizer( localhost, "/" );
	final String s_host = st.nextToken();
	final String s_ip = st.nextToken();
%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Style-Type" content="text/css">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>SK Broadband Procument Portal</title>

<script type="text/javascript">

	//logout function
	//공급업체시 메인페이지 호출
	//현업시 logout 페이지 호출
	function logoutProcess(){
		var role_list = "${USER.role_list}";

		if(role_list == "SUPPLIER") {
			document.location.href = "loginSupplier";

		}else {
			document.location.href = "Logout";
		}
	}

	//left 배너 클릭 이벤트
	function onClickEvent(e) {
		//자사정보관리
		if(e.id == "bannerUsrInfo") {
			document.location.href = "/bp/emVendorUsrInfo";
		//supplier portal market
		}else if(e.id == "bannerSMP") {
			document.location.href = "/bp/emSupplierManageList";
		//사업협력 및 제안
		}else if(e.id == "bannerCowork") {
			document.location.href = "/bp/emCoworkList";
		//문의하기(Q&A)
		//파라미터 : board_id = 40
		}else if(e.id == "bannerQnA") {
			var params = {board_id: "40"};
			fnPostGoto("/bp/emBoardList.do", params, "_self");
		//구매문서저장소
		}else if(e.id == "bannerDoc") {
			document.location.href = "/bw/emDocStorageList";
		//선제적구매지원현황
		}else if(e.id == "bannerPRStat") {
			document.location.href = "/bw/emPRStatusList";
		//게시판
		//파라미터 : board_id = 34
		}else if(e.id == "bannerBoard") {
			var params = {board_id: "34"};
			fnPostGoto("/bw/emBoardList", params, "_self");
		}
	}
</script>
</head>

<body>
<div id="wrapper">
		<jsp:include page="/top_header.jsp"></jsp:include>

		<!-- 네비 -->
		<jsp:include page="/top_menu.jsp"></jsp:include>

		<section id="container">
			<!-- left -->
			<aside id="aside">
				<div class="mainLoginBox">
					<div class="mainBtnBox">
						<button class="btnLogout" type="button" id="btnLogout" onclick="logoutProcess()">로그아웃</button>
					</div>
					<div class="linkBtnBox">
						<img src="/assets/images/login_user.png">
						<h5 class="logoutTxt">
							<%if(USER != null) {%>
								<%=USER.get("usr_nm") %>
							<%}else { response.sendRedirect("/loginSupplier");%> <%} %>
							님 환영합니다.
						</h5>
					</div>
				</div>

				<!-- 공급업체 권한시 -->
				<c:if test="${USER != null && USER.role_list == 'SUPPLIER'}">
					<div class="mainBannerBox" id="bannerUsrInfo" onclick="onClickEvent(this);">
						<img alt="" src="/assets/images/icon_mainLeft03.png" role="button">
						<label>자사정보관리</label>
					</div>
				</c:if>

				<div class="mainBannerBox" id="bannerSMP" onclick="onClickEvent(this);">
					<img alt="" src="/assets/images/icon_mainLeft05.png" role="button">
					<label>Supplier Portal Market</label>
				</div>

				<c:if test="${USER != null && USER.role_list == 'SUPPLIER'}">
					<div class="mainBannerBox" id="bannerCowork" onclick="onClickEvent(this);">
						<img alt="" src="/assets/images/icon_mainLeft02.png" role="button">
						<label>사업협력 및 제안</label>
					</div>

					<div class="mainBannerBox" id="bannerQnA" onclick="onClickEvent(this);">
						<img alt="" src="/assets/images/icon_mainLeft01.png" role="button">
						<label>문의하기(Q＆A)</label>
					</div>
				</c:if>

				<!-- 현업 권한시 -->
				<c:if test="${USER != null && USER.role_list != 'SUPPLIER'}">
					<div class="mainBannerBox" id="bannerDoc" onclick="onClickEvent(this);">
						<img alt="" src="/assets/images/icon_mainLeft08.png" role="button">
						<label>구매문서 저장소</label>
					</div>
					<div class="mainBannerBox" id="bannerPRStat" onclick="onClickEvent(this);">
						<img alt="" src="/assets/images/icon_mainLeft06.png" role="button">
						<label>선제적 구매지원</label>
					</div>
					<div class="mainBannerBox" id="bannerBoard" onclick="onClickEvent(this);">
						<img alt="" src="/assets/images/icon_mainLeft01.png" role="button">
						<label>게시판</label>
					</div>
				</c:if>

				<div class="mainOpen4uBanner" onclick="onClickEvent(this);">
					<img alt="" src="/assets/images/open4u_banner2.png" role="button">
				</div>
			</aside>

			<!-- right -->
			<jsp:include page="/main_right.jsp"></jsp:include>
		</section>

		<!-- 푸터 -->
		<jsp:include page="/footer.jsp"></jsp:include>
	</div>
</body>
</html>
