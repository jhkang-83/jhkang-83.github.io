<%@page session="true"  language="java" contentType="text/html;charset=utf-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="volcano.custom.control.MainSummaryController"%>
<%@page import="java.util.Map"%>
<%@page import="eswf.managers.TransactionManager"%>
<%@page import="eswf.jdbc.*"%>
<%@page import="javax.servlet.http.HttpServletRequest" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<%
	//구매견적의뢰 : board_id = 37
	//공동제안사 선정용 : board_id = 41

	MainSummaryController summarycont = new MainSummaryController();
	//구매견적의뢰
	List<Map> summaryReqesti = new ArrayList();
	//공동제안사 선정용
	List<Map> summaryCoopOfferEsti = new ArrayList();
	//입찰공지
	List<Map> summaryBidding = new ArrayList();
	//공지사항
	List<Map> summaryNotice = new ArrayList();
	//사업협력 및 제안
	List<Map> summaryCowork = new ArrayList();
	if(summarycont != null) {
		summaryReqesti = summarycont.getSummaryReq();
		summaryCoopOfferEsti = summarycont.getSummaryCoopOfferEsti();
		summaryBidding = summarycont.getSummaryBidding();
		summaryNotice = summarycont.getSummaryNotice();
		summaryCowork = summarycont.getSummaryCowork();
	}
%>

<!--
*
*메인 썸네일
*권한 체크 : USER.role_list
*
 -->
<head>

<script type="text/javascript">
	function summaryClickEvent(e) {
		if(e.id == "btnReqesti") {
			var params = {board_id: "37"};
			fnPostGoto("/bp/emBoardList", params, "_self");
			return;
		}else if(e.id == "btnBidding") {
			var params = {};
			fnPostGoto("/bp/emBiddingList", params, "_self");
			return;
		}else if(e.id == "btnCowork") {
			var params = {};
			fnPostGoto("/bp/emCoworkList", params, "_self");
			return;
		}else if(e.id == "btnNotice") {
			var params = {};
			fnPostGoto("/bp/emNoticeMageList", params, "_self");
			return;
		}
	}

	function itemClickEvent(value, post_no, board_id) {
		if(value == "req") {
			var params = {post_no: post_no, board_id: board_id, mode: "view"};
			fnPostGoto("/bp/emBoardDetail", params, "_self");
			return;
		}else if(value == "bid") {
			var params = {bidding_no: post_no, mode:"view"};
			fnPostGoto("/bp/emBiddingDetail", params, "_self");
			return;
		}else if(value == "corwork") {
			var params = {idx: post_no, mode: "view"};
			fnPostGoto("/bp/emCoworkDetail", params, "_self");
			return;
		}else if(value == "notice") {
			var params = {idx: post_no, mode: "view"};
			fnPostGoto("/bp/emNoticeMgt", params, "_self");
			return;
		}
	}
</script>
</head>
<article class="mainContent">
	<div>
		<img alt="" src="/assets/images/mainVisual.png">
	</div>

	<div class="mainContentNews">
		<div class="newsBox">
			<div class="newsListBox" role="button">
				<label class="newsListBoxTitle">구매견적 의뢰 / 공동제안사 선정용</label>
				<button class="btnGo" id="btnReqesti" onclick="summaryClickEvent(this);"></button>
			</div>

			<div class="newListLine"></div>
			<!-- 구매견적 의뢰 게시글 바로가기 -->
			<div class="newsListBox">
				<img alt="" src="/assets/images/icon_list.png">
				<label id="req_esti_title_1" onclick="javascript:itemClickEvent('req','<%=summaryReqesti.get(0).get("post_no") %>', '37');"
					class="newsListTxt" style="cursor: pointer;"><%=summaryReqesti.get(0).get("post_title") %></label>
			</div>

			<div class="newsListBox">
				<img alt="" src="/assets/images/icon_list.png">
				<label id="req_esti_title_2" onclick="javascript:itemClickEvent('req','<%=summaryReqesti.get(1).get("post_no") %>', '37');"
					class="newsListTxt" style="cursor: pointer;"><%=summaryReqesti.get(1).get("post_title") %></label>
			</div>

			<div class="newsListBox">
				<img alt="" src="/assets/images/icon_list.png">
				<label id="req_esti_title_3" onclick="javascript:itemClickEvent('req', '<%=summaryReqesti.get(2).get("post_no") %>', '37');"
					class="newsListTxt" style="cursor: pointer;"><%=summaryReqesti.get(2).get("post_title") %></label>
			</div>

			<!-- 공동제안사 선정용 게시글 바로가기 -->
			<div class="newsListBox">
				<img alt="" src="/assets/images/icon_list.png">
				<label id="req_esti_title_4" onclick="javascript:itemClickEvent('req','<%=summaryCoopOfferEsti.get(0).get("post_no") %>', '41');"
					class="newsListTxt" style="cursor: pointer;"><%=summaryCoopOfferEsti.get(0).get("post_title") %></label>
			</div>
		</div>

	<!--
	*공급업체 권한시
	*입찰공지 게시글 바로가기
	-->
	<c:if test="${USER == null || USER.role_list == 'SUPPLIER'}">
		<div class="newsBox">
			<div class="newsListBox" role="button">
				<label class="newsListBoxTitle">입찰공지</label>
				<button class="btnGo" id="btnBidding" onclick="summaryClickEvent(this);"></button>
			</div>

			<div class="newListLine"></div>
			<div class="newsListBox">
				<img alt="" src="/assets/images/icon_list.png">
				<label class="newsListTxt" style="cursor: pointer;" onclick="javascript:itemClickEvent('bid','<%=summaryBidding.get(0).get("bidding_no") %>', '');"><%=summaryBidding.get(0).get("bidding_title") %></label>
			</div>

			<div class="newsListBox">
				<img alt="" src="/assets/images/icon_list.png">
				<label class="newsListTxt" style="cursor: pointer;" onclick="javascript:itemClickEvent('bid','<%=summaryBidding.get(1).get("bidding_no") %>', '');"><%=summaryBidding.get(1).get("bidding_title") %></label>
			</div>

			<div class="newsListBox">
				<img alt="" src="/assets/images/icon_list.png">
				<label class="newsListTxt" style="cursor: pointer;" onclick="javascript:itemClickEvent('bid','<%=summaryBidding.get(2).get("bidding_no") %>', '');"><%=summaryBidding.get(2).get("bidding_title") %></label>
			</div>

			<div class="newsListBox">
				<img alt="" src="/assets/images/icon_list.png">
				<label class="newsListTxt" style="cursor: pointer;" onclick="javascript:itemClickEvent('bid','<%=summaryBidding.get(3).get("bidding_no") %>', '');"><%=summaryBidding.get(3).get("bidding_title") %></label>
			</div>
		</div>
	</c:if>

	<!--
	*공급업체 권한시
	*사업협력 및 제안 게시글 바로가기
	-->
	<c:if test="${USER != null &&  USER.role_list != 'SUPPLIER'}">
		<div class="newsBox">
			<div class="newsListBox" role="button">
				<label class="newsListBoxTitle">사업협력 및 제안</label>
				<button class="btnGo" id="btnCowork" onclick="summaryClickEvent(this);"></button>
			</div>

			<div class="newListLine"></div>
			<div class="newsListBox">
				<img alt="" src="/assets/images/icon_list.png">
				<label class="newsListTxt" style="cursor: pointer;" onclick="javascript:itemClickEvent('corwork','<%=summaryCowork.get(0).get("idx") %>', '');"><%=summaryCowork.get(0).get("title") %></label>
			</div>

			<div class="newsListBox">
				<img alt="" src="/assets/images/icon_list.png">
				<label class="newsListTxt" style="cursor: pointer;" onclick="javascript:itemClickEvent('corwork','<%=summaryCowork.get(1).get("idx") %>', '');"><%=summaryCowork.get(1).get("title") %></label>
			</div>

			<div class="newsListBox">
				<img alt="" src="/assets/images/icon_list.png">
				<label class="newsListTxt" style="cursor: pointer;" onclick="javascript:itemClickEvent('corwork','<%=summaryCowork.get(2).get("idx") %>', '');"><%=summaryCowork.get(2).get("title") %></label>
			</div>

			<div class="newsListBox">
				<img alt="" src="/assets/images/icon_list.png">
				<label class="newsListTxt" style="cursor: pointer;" onclick="javascript:itemClickEvent('corwork','<%=summaryCowork.get(3).get("idx") %>', '');"><%=summaryCowork.get(3).get("title") %></label>
			</div>
		</div>
	</c:if>

	<div class="newsBox">
		<div class="newsListBox" role="button">
			<label class="newsListBoxTitle">공지사항</label>
			<button class="btnGo" id="btnNotice" onclick="summaryClickEvent(this);"></button>
		</div>

		<!--
		*공지사항 게시글 바로가기
		-->
		<div class="newListLine"></div>
		<div class="newsListBox">
			<img alt="" src="/assets/images/icon_list.png">
			<label class="newsListTxt" style="cursor: pointer;" onclick="javascript:itemClickEvent('notice','<%=summaryNotice.get(0).get("idx") %>', '');"><%=summaryNotice.get(0).get("title") %></label>
		</div>

		<div class="newsListBox">
			<img alt="" src="/assets/images/icon_list.png">
			<label class="newsListTxt" style="cursor: pointer;" onclick="javascript:itemClickEvent('notice','<%=summaryNotice.get(1).get("idx") %>', '');"><%=summaryNotice.get(1).get("title") %></label>
		</div>

		<div class="newsListBox">
			<img alt="" src="/assets/images/icon_list.png">
			<label class="newsListTxt" style="cursor: pointer;" onclick="javascript:itemClickEvent('notice','<%=summaryNotice.get(2).get("idx") %>', '');"><%=summaryNotice.get(2).get("title") %></label>
		</div>
		
		<div class="newsListBox">
			<img alt="" src="/assets/images/icon_list.png">
			<label class="newsListTxt" style="cursor: pointer;" onclick="javascript:itemClickEvent('notice','<%=summaryNotice.get(3).get("idx") %>', '');"><%=summaryNotice.get(3).get("title") %></label>
		</div>
	</div>
</article>
