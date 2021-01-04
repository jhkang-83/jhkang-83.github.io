<%@ page session="true" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@page import="volcano.custom.control.Helper"%>
<%@page import="eswf.dataobject.Map"%>
<%@page import="javax.servlet.http.HttpServletRequest" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%
	//session 가져오기
	Helper helper = new Helper(request, new eswf.dataobject.Map());
	Map USER = helper.getUser();
	request.setAttribute("USER", USER);
%>

<head>
	<script type="text/javascript">

		var xmlData;
		var listLength;
		var contentStr = "";
		var menuId = "";
		var isMenuOpen = false;

		$(document).ready(function(){
			bindEventGnb();
		});


		function onError(request, error) {alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);}

		// menu 마우스 function
		this.bindEventGnb = function(){
			$('.topMenuBgContainer').find('.topMenuBar').bind('mouseenter mouseleave', function(e) {
				if(e.type === 'mouseenter') {
					$(this).addClass('f_action');
				}else {
					$(this).removeClass('f_action');
				}
			});

			$('.subMenuList').bind('mouseenter mouseleave', function (e) {
				isMenuOpen = (e.type === 'mouseenter');
			});

			$('.topMenu').bind('mouseenter mouseleave', function (e) {
				if(e.type === 'mouseenter'){
					$(this).addClass('f_action_on');
				}else{
					$(this).removeClass('f_action_on');

				}
			});
		};

		//메뉴 클릭 이벤트 function
		function menuClickEvent(value) {
			//구매 견적 의뢰
			//파라미터 : board_id = 37
			if(value == 'frmBid20') {
				$('#frmBoard_id').val('37');
				$('#frmBoard').attr("action", "/bp/emBoardList");
				$('#frmBoard').submit();
				return;
			}

			//공동제안사 선정용 견적요청
			//파라미터 : board_id = 41
			if(value == 'frmBid30') {
				$('#frmBoard_id').val('41');
				$('#frmBoard').attr("action", "/bp/emBoardList");
				$('#frmBoard').submit();
				return;
			}

			//문의하기(Q＆A)
			//파라미터 : board_id = 40
			if(value == 'frmNot60') {
				$('#frmBoard_id').val('40');
				$('#frmBoard').attr("action", "/bp/emBoardList");
				$('#frmBoard').submit();
				return;
			}

			//게시판
			//파라미터 : board_id 34
			if(value == 'frmBBS10') {
				$('#frmBoard_id').val('34');
				$('#frmBoard').attr("action", "/bw/emBoardList");
				$('#frmBoard').submit();
				return;
			}

			//구매지식마당
			//파라미터 : board_id = 35
			if(value == 'frmBBS20') {
				$('#frmBoard_id').val('35');
				$('#frmBoard').attr("action", "/bw/emBoardList");
				$('#frmBoard').submit();
				return;
			}

		}


	</script>
</head>
<form id="frmBoard" method="post">
	<input type="hidden" id="frmBoard_id" name="board_id" value="">
</form>
<nav id="nav" class="topMenuBgContainer">
	<ul id="menuBar" class="topMenuBar">
		<li id="HOM" class="topMenu">
			<a id="menuLink" class="menuLink">HOME</a>
			<span class="menuBorder"></span>

			<ul class="subMenuList">
				<c:if test="${USER != null &&  USER.role_list == 'SUPPLIER'}">
					<li id="HOM20">
						<a class="subMenuLink" href="/Main">메인화면</a>
					</li>
				</c:if>
				<c:if test="${USER == null}">
					<li id="HOM20">
						<a class="subMenuLink" href="/loginSupplier">메인화면</a>
					</li>
				</c:if>

				<!-- 공급업체 권한시  -->
				<c:if test="${USER != null &&  USER.role_list == 'SUPPLIER'}">
					<li id="HOM40">
						<a class="subMenuLink" href="/bp/emVendorOwnInfo">자사정보관리</a>
					</li>

					<li id="HOM50">
						<a class="subMenuLink" href="/bp/emVendorUsrInfo">사용자정보관리</a>
					</li>
				</c:if>

				<!-- 구매팀 관리자, 관리자 권한시  -->
				<c:if test="${USER != null &&  (USER.role_list == 'BUYER,BUYERADMIN' || USER.role_list == 'MASTER')}">
					<li id="HOM30">
						<a class="subMenuLink" href="/bw/emVendorNewManageList">업체정보관리</a>
					</li>
				</c:if>
			</ul>
		</li>

		<li id="SPM" class="topMenu">
			<a id="menuLink" class="menuLink">Suppiler Portal Market</a>
			<span class="menuBorder"></span>

			<ul class="subMenuList">
				<li id="SPM10">
					<a class="subMenuLink" href="/bp/emSupplierManageList">Suppiler Portal Market</a>
				</li>
			</ul>
		</li>

		<li id="BID" class="topMenu">
			<a id="menuLink" class="menuLink">입찰 및 견적의뢰</a>
			<span class="menuBorder"></span>

			<ul class="subMenuList">
				<li id="BID10">
					<a class="subMenuLink" href="/bp/emBiddingList">입찰공지</a>
				</li>
				<li id="BID20">
					<a class="subMenuLink" href="javascript:menuClickEvent('frmBid20');">구매 견적 의뢰</a>
				</li>
				<li id="BID30">
					<a class="subMenuLink" href="javascript:menuClickEvent('frmBid30');">공동제안사 선정용 견적요청</a>
				</li>
			</ul>
		</li>

		<li id="NOT" class="topMenu">
			<a id="menuLink" class="menuLink">사업협력 및 제안</a>
			<span class="menuBorder"></span>

			<ul class="subMenuList">
				<!-- 공급업체 권한시  -->
				<c:if test="${USER != null}">
					<li id="NOT10">
						<a class="subMenuLink" href="/bp/emCoworkList">사업협력 및 제안접수</a>
					</li>
				</c:if>
				<li id="NOT30">
					<a class="subMenuLink" href="/bp/emNoticeMageList">공지사항</a>
				</li>
				<li id="NOT60">
					<a class="subMenuLink" href="javascript:menuClickEvent('frmNot60');">문의하기(Q＆A)</a>
				</li>
			</ul>
		</li>

		<!-- 현업 권한시  -->
		<c:if test="${USER != null &&  USER.role_list != 'SUPPLIER'}">
			<li id="PDS" class="topMenu">
				<a id="menuLink" class="menuLink">구매문서 저장소</a>
				<span class="menuBorder"></span>

				<ul class="subMenuList">
					<li id="PDS10">
						<a class="subMenuLink" href="/bw/emDocStorageList">구매문서 저장소</a>
					</li>
				</ul>
			</li>

			<li id="BBS" class="topMenu">
				<a id="menuLink" class="menuLink">구매광장</a>
				<span class="menuBorder"></span>

				<ul class="subMenuList">
					<li id="BBS10">
						<a class="subMenuLink" href="javascript:menuClickEvent('frmBBS10');">게시판</a>
					</li>
					<li id="BBS20">
						<a class="subMenuLink" href="javascript:menuClickEvent('frmBBS20');">구매지식마당</a>
					</li>
				</ul>
			</li>

			<li id="PRS" class="topMenu">
				<a id="menuLink" class="menuLink">선제적 구매지원</a>
				<span class="menuBorder"></span>

				<ul class="subMenuList">
					<li id="PRS10">
						<a class="subMenuLink" href="/bw/emPRStatusList">선제적 구매지원</a>
					</li>
				</ul>
			</li>
		</c:if>

	</ul>
</nav>
