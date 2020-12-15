<%@page import="eswf.dataobject.Map"%>
<%@ page session="false" language="java" extends="eswf.servlet.BaseHttpJsp" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<% 
	Map USER = null;
	
	if(super.isLogin())
	{
		USER = new Map(super.session.getAttribute("[USER]"));
		System.out.println("user >>> " + USER.toString());
	}
	
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
		
		
		function menuClickEvent(value) {
			if(value == 'frmBid20') {
				$('#frmBoard_id').val('37');
				$('#frmBoard').attr("action", "/bp/emBoardList");
				$('#frmBoard').submit();
				return;
			}
			
			if(value == 'frmBid30') {
				$('#frmBoard_id').val('41');
				$('#frmBoard').attr("action", "/bp/emBoardList");
				$('#frmBoard').submit();
				return;
			}
			
			if(value == 'frmNot60') {
				$('#frmBoard_id').val('40');
				$('#frmBoard').attr("action", "/bp/emBoardList");
				$('#frmBoard').submit();
				return;
			}
			
			if(value == 'frmBBS10') {
				$('#frmBoard_id').val('34');
				$('#frmBoard').attr("action", "/bw/emBoardList");
				$('#frmBoard').submit();
				return;
			}
			
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
				<li id="HOM20">
					<a class="subMenuLink" href="/Main">메인화면</a>
				</li>
				
				<c:if test="${USER != null &&  USER.usr_cls == 'S'}">
					<li id="HOM40">
						<a class="subMenuLink" href="/bp/emVendorOwnInfo">자사정보관리</a>
					</li>
					
					<li id="HOM50">
						<a class="subMenuLink" href="/bp/emVendorUsrInfo">사용자정보관리</a>
					</li>
				</c:if>
				
				<c:if test="${USER != null &&  USER.usr_cls == 'B' && (USER.role_list == 'BUYER,BUYERADMIN' || USER.role_list == 'MASTER')}">
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
		
		<c:if test="${USER != null &&  USER.usr_cls == 'B'}">
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
