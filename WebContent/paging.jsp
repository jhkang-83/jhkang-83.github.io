<%@page import="java.util.Map"%>
<%@ page session="true" language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<% 
	Map resultTotal = (Map) request.getAttribute("resultTotal");	

	//현재 페이지 번호
	int currentPage = 1;
	// 총 게시물 갯수
	int total = 1;
	// 페이지 번호
	int pageGroupSize = 10;
	//게시물에 나타나는 목록 갯수
	int listSize = 10;
	//게시물의 첫번째 글 번호
	int startRow = (currentPage - 1) * listSize + 1;
	//게시물의 마지막번째 글 번호
	int endRow = currentPage * listSize;
	
	if(request.getAttribute("currentPage") != null) {
		currentPage = (Integer) request.getAttribute("currentPage");
	}
	
	if(resultTotal.get("total") != null) {
		total = Integer.valueOf((String) resultTotal.get("total"));
	}
	
	if(request.getParameter("pageGroupSize") != null) {
		pageGroupSize = Integer.valueOf((String) request.getParameter("pageGroupSize"));
	}
	
	if(request.getParameter("listSize") != null) {
		listSize = Integer.valueOf((String) request.getParameter("listSize"));
	}
	
	//	//페이지 그룹 갯수
	int pageGroupCount = total / (listSize * pageGroupSize) + (total % (listSize * pageGroupSize) == 0 ? 0 : 1);
	//현재 그룹페이지 번호
	int nowPageGroup = (int) Math.ceil((double) currentPage / pageGroupSize);
	int pageCount = total / listSize + (total % listSize == 0 ? 0 : 1);
	
	
	System.out.println("total >>>> " + total);
	System.out.println("pageCount >>>> " + pageCount);
	System.out.println("nowPageGroup >>>> " + nowPageGroup);
	System.out.println("currentPage >>>> " + currentPage);

	request.setAttribute("currentPage", currentPage);
	request.setAttribute("pageGroupSize", pageGroupSize);
	request.setAttribute("nowPageGroup", nowPageGroup);
	request.setAttribute("pageCount", pageCount);
%>

<!DOCTYPE>
<html>
<head>

<!-- 
*
* 게시글 페이징 처리 
* 필수 파라미터
* 현재 페이지 번호 : currentPage
* 총 게시물 갯수 : total
* 페이지 번호 : pageGroupSize
* 게시물에 나타나는 목록 갯수: listSize
*
 -->
<script type="text/javascript">

	var startRow = 0;
	var endRow = 0;
	
	
	
	function goPageClickEvent(value) {
		startRow = (value -1) * <%= listSize %> + 1;
		endRow = value * <%= listSize %>;
		
		$('#startRow').val(startRow);
		$('#endRow').val(endRow);
		$('#currentPage').val(value);
		$('#frmSearch').submit();
	}
</script>

</head>

<div id="pagerList" class="pagingBtnArea">
	<div id="boardNavigation">
		
		<c:set var="totalCount" value="${resultTotal.total}"/>
		<c:set var="totalPage" value="${pageCount}"/>
		<c:set var="startPage" value="${pageGroupSize * (nowPageGroup-1) + 1}"/>
		<c:set var="endPage" value="${startPage + pageGroupSize-1}"/>
		<c:if test="${endPage gt totalPage}">
			<c:set var="endPage" value="${totalPage}"/>
		</c:if>
		
		<!-- 이전 페이지 -->
		<c:if test="${totalCount != 0}">
			<c:if test="${currentPage > pageGroupSize}">
				<span class="prevPage" onclick="javascript:goPageClickEvent('${1}');">◀◀</span>
				<span class="prevPage" onclick="javascript:goPageClickEvent('${currentPage - 1}');">◀</span>
			</c:if>
			
			<!-- 현재 페이지 -->
			<c:forEach var="i" begin="${startPage}" end="${endPage}" step="1">
				<c:choose>
					<c:when test="${i eq currentPage}">
						<span class="nowPage">${i}</span>
					</c:when>
					<c:otherwise>
						<span class="pageListNum" onclick="javascript:goPageClickEvent('${i}');">${i}</span>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			
			<!-- 다음 페이지 -->
			<c:if test="${pageGroupSize < pageCount && currentPage != pageCount}">
				<span class="nextPage" onclick="javascript:goPageClickEvent('${currentPage + 1}');">▶</span>
				<span class="nextPage" onclick="javascript:goPageClickEvent('${totalPage}');">▶▶</span>
			</c:if>
		</c:if>
		
	</div>
</div>

</html>