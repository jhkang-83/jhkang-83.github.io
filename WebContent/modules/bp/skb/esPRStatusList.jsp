<%@ page session="true" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@page import="eswf.dataobject.Map"%>
<%@page import="volcano.custom.control.Helper"%>
<%@page import="emro.util.StringUtil"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
	Helper helper = new Helper(request, new eswf.dataobject.Map());
	Map USER = helper.getUser();
	request.setAttribute("USER", USER);

	StringUtil stringUtil = new StringUtil();
	request.setAttribute("stringUtil", stringUtil);
%>

<!-- 선제적구매지원현황 리스트 -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Style-Type" content="text/css">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Expires" content="0">
<title>SK Broadband Procument Portal</title>

<script type="text/javascript">
	window.onload = function() {
		$('#loading').hide();
	}

	function onClickEvent(e) {
		//조회
		if(e.id == "btnSearch") {
			$('#mode').val('search');
			$('#changeWriteMode').val('view');
			$('#frmSearch').attr("action", "/bw/emPRStatusList");
			$('#frmSearch').submit();
			return;

		//작성
		}else if(e.id == "btnWrite") {
			$('#mode').val('writing');
			$('#changeWriteMode').val('writable');
			$('#frmSearch').attr("action", "/bw/emPRStatusWrite");
			$('#frmSearch').submit();

		//수정
		}else if(e.id == "btnNEdit") {
			$('#mode').val('writing');
			$('#changeWriteMode').val('editable');
			$('#frmSearch').attr("action", "/bw/emPRStatusList");
			$('#frmSearch').submit();
			return;

		//저장
		}else if(e.id == "btnNSave") {

			let itemListSize = ${resultList.size()};
			for(let i=0; i < itemListSize; i++) {
				if($('#col_approval_date'+i).val() == "") {
					alert("품의결재일을 입력 해주세요.");
					$('#col_approval_date'+i).focus();
					return false;
				}
				if($('#col_doc_no'+i).val() == "") {
					alert("문서번호를 입력 해주세요.");
					$('#col_doc_no'+i).focus();
					return false;
				}
				if($('#col_approval_title'+i).val() == "") {
					alert("품의명을 입력 헤주세요.");
					$('#col_approval_title'+i).focus();
					return false;
				}
				if($('#col_amount'+i).val() == "") {
					alert("금액을 입력 해주세요.");
					$('#col_amount'+i).focus();
					return false;
				}
				if($('#col_purc_grp_nm'+i).val() == "") {
					alert("구매담당자를 입력 해주세요.");
					$('#col_purc_grp_nm'+i).focus();
					return false;
				}
				if($('#col_dept_nm'+i).val() == "") {
					alert("담당부서를 입력 해주세요.");
					$('#col_dept_nm'+i).focus();
					return false;
				}
				if($('#col_charge_name'+i).val() == "") {
					alert("담당자를 입력 해주세요.");
					$('#col_charge_name'+i).focus();
					return false;
				}
				if($('#col_approval_name'+i).val() == "") {
					alert("결재자를 입력 해주세요.");
					$('#col_approval_name'+i).focus();
					return false;
				}
				if($('#col_budget_cls_nm'+i).val() == "") {
					alert("Capex/Opex를 입력 해주세요.");
					$('#col_budget_cls_nm'+i).focus();
					return false;
				}
			}

			if(confirm("수정 하시겠습니까?") == true) {
				$('#gridMode').val('save');
				$('#gridChangeWriteMode').val('view');

				var formData = $('#frmPRStatusList').serialize();
				$.ajax({
					type : "POST",
					url : "/bw/emPRStatusList",
					data : formData,
					cache: false,
					success : function(data){
	            		$('#loading').fadeOut();
	            		alert(data);

	            		$('#mode').val('search');
	        			$('#changeWriteMode').val('view');
	        			$('#frmSearch').attr("action", "/bw/emPRStatusList");
	        			$('#frmSearch').submit();
	            	},
	            	beforeSend:function(){
	    				$('#loading').show();
	    		    },
	    		    error : onError
				});
				return;

			}else {return false;}

		}else if(e.id == "btnNCancle") {
			$('#mode').val('search');
			$('#changeWriteMode').val('view');
			$('#frmSearch').attr("action", "/bw/emPRStatusList");
			$('#frmSearch').submit();
		}
	}

	function optionChangeEvent(item, index) {
		var selectIndex = item.options[item.options.selectedIndex];
		var selectText = item.options[item.options.selectedIndex].text;
		$('#pr_status'+index).val(selectText);
	}

	function onError(data, status){alert(data);}

</script>

</head>

<body>
	<div id="wrapper">
		<div class="headerCon fixed">
			<jsp:include page="/top_header.jsp"></jsp:include>
			<jsp:include page="/top_menu.jsp"></jsp:include>
		</div>
	</div>

	<div class="content">
		<div class="container bb_none">
			<div class="titArea bb_solid shadw p_b20">
				<h2>선제적 구매지원 현황</h2>
				<div class="f_right v_middle">
					<c:if test="${changeWriteMode eq 'view'}">
						<button type="button" class="searchBtn" id="btnSearch" onclick="onClickEvent(this);">조회</button>
					</c:if>
					<c:if test="${USER.role_list == 'BUYER' || USER.role_list == 'BUYER,BUYERADMIN' || USER.role_list == 'MASTER'}">
						<c:if test="${changeWriteMode eq 'view'}">
							<button type="button" class="btnNWrite w65" id="btnWrite" onclick="onClickEvent(this);">등록</button>
							<button type="button" class="btnNModify" id="btnNEdit" onclick="onClickEvent(this);">수정</button>
						</c:if>

						<c:if test="${changeWriteMode eq 'editable'}">
							<button type="button" class="btnNClose" id="btnNCancle" onclick="onClickEvent(this);">취소</button>
							<button type="button" class="saveBtn" id="btnNSave" onclick="onClickEvent(this);">저장</button>
							<button type="button" class="deleteBtn" id="btnNDelete" onclick="onClickEvent(this);">삭제</button>
						</c:if>
					</c:if>
				</div>
			</div>

			<div class="searchWrap m_t10 m_l10 m_r20">
				<div class="searchTableArea">
					<form id="frmSearch" name="frmSearch" method="post" autocomplete="off">
						<input type="hidden" id="startRow" name="startRow" value="0">
						<input type="hidden" id="endRow" name="endRow" value="0">
						<input type="hidden" id="currentPage" name="currentPage" value="0">
						<input type="hidden" id="mode" name="mode" value="">
						<input type="hidden" id="changeWriteMode" name="changeWriteMode" value="${changeWriteMode}">

						<table class="searchTable p_l20">
							<colgroup>
								<col style="width: 10%;">
								<col style="width: 40%;">
								<col style="width: 10%;">
								<col style="width: 40%;">
							</colgroup>

							<tbody>
								<tr>
									<th>
										<strong class="stit">품의명</strong>
									</th>
									<td>
										<input type="text" id="approval_title" name="approval_title" maxlength="200">
									</td>

									<th>
										<strong class="stit">담당자</strong>
									</th>
									<td>
										<input type="text" id="charge_name" name="charge_name" maxlength="20">
									</td>
								</tr>

								<tr>
									<th>
										<strong class="stit">구매담당자</strong>
									</th>
									<td>
										<input type="text" id="purc_grp_nm" name="purc_grp_nm" maxlength="20">
									</td>

									<th>
										<strong class="stit">진행상태</strong>
									</th>
									<td>
										<select name="prsts_gb">
											<option id="prsts_gb" value="" selected="selected">전체</option>
											<!-- 구매팀 권한 -->
											<c:if test="${USER.role_list eq 'BUYER' || USER.role_list eq 'BUYER,BUYERADMIN' || USER.role_list eq 'ADMIN'}">
												<c:forEach items="${prStatusCode}" var="code" varStatus="status">
													<c:choose>
														<c:when test="${code.data eq selectedPRStatusCD}">
															<option id="prsts_gb" value="${code.data}" selected="selected">${code.label}</option>
														</c:when>
														<c:otherwise>
															<option id="prsts_gb" value="${code.data}">${code.label}</option>
														</c:otherwise>
													</c:choose>
												</c:forEach>
											</c:if>

											<!-- 구매팀 외 현업 권한 -->
											<c:if test="${USER.role_list != 'BUYER' && USER.role_list != 'BUYER,BUYERADMIN' && USER.role_list != 'ADMIN'}">
												<c:forEach items="${prStatusCode}" var="code" varStatus="status" begin="0" end="3">
													<c:choose>
														<c:when test="${code.data eq selectedPRStatusCD}">
															<option id="prsts_gb" value="${code.data}" selected="selected">${code.label}</option>
														</c:when>
														<c:otherwise>
															<option id="prsts_gb" value="${code.data}">${code.label}</option>
														</c:otherwise>
													</c:choose>
												</c:forEach>
											</c:if>
										</select>
									</td>
								</tr>

								<tr>
									<th>
										<strong class="stit">문서번호</strong>
									</th>
									<td colspan="3">
										<input type="text" id="doc_no" name="doc_no" maxlength="200">
									</td>
								</tr>
							</tbody>
						</table>
					</form>
				</div>
			</div>

			<form id="frmPRStatusList" name="frmPRStatusList" method="post" autocomplete="off">
				<input type="hidden" id="gridStartRow" name="startRow" value="0">
				<input type="hidden" id="gridEndRow" name="endRow" value="0">
				<input type="hidden" id="gridCurrentPage" name="currentPage" value="0">
				<input type="hidden" id="gridMode" name="mode" value="">
				<input type="hidden" id="gridChangeWriteMode" name="changeWriteMode" value="${changeWriteMode}">

				<div class="gridArea m_t5 m_l10 m_r20">
					<table id="gridBox" class="tableStyle">
						<colgroup>
							<col style="width: 3%">
							<col style="width: 10%">
							<col style="width: 11%">
							<col style="width: 20%">
							<col style="width: 7%">
							<col style="width: 9%">
							<col style="width: ">
							<col style="width: 10%">
							<col style="width: ">
							<col style="width: ">
							<col style="width: ">
							<col style="width: ">
						</colgroup>

						<thead>
							<tr>
								<th>번호</th>
								<th>품의결재일</th>
								<th>문서번호</th>
								<th>품의명</th>
								<th>금액</th>
								<th>구매검토</th>
								<th>구매담당자</th>
								<th>담당부서</th>
								<th>담당자</th>
								<th>결재자</th>
								<th>Capex/Opex</th>
								<th>구매내역서첨부</th>
							</tr>
						</thead>

						<tbody>
							<c:if test="${resultList.isEmpty() || resultList == null}">
								<tr>
									<td colspan="12">조회된 데이터가 없습니다.</td>
								</tr>
							</c:if>

							<c:if test="${resultList != null && resultList.size() > 0}">
								<c:forEach items="${resultList}" var="item" varStatus="i">
									<tr>
										<td id="no"><input name="no" class="SCTextInput" value="${item.no}"></td>
										<td id="approval_date">
											<input type="text" id="col_approval_date${i.index}" name="approval_date" value="${item.approval_date}" <c:if test="${changeWriteMode eq 'editable'}">class="SCTextInput datepicker wp70 -m_r5 p_0"</c:if> <c:if test="${changeWriteMode eq 'view'}">class="SCTextInput" readonly="readonly"</c:if>>
										</td>
										<td id="doc_no"><input id="col_doc_no${i.index}" name="doc_no" class="SCTextInput" value="${item.doc_no}" <c:if test="${changeWriteMode eq 'view'}">readonly="readonly"</c:if>></td>
										<td id="approval_title" class="t_left"><input id="col_approval_title${i.index}" name="approval_title" class="SCTextInput t_left" value="${item.approval_title}" <c:if test="${changeWriteMode eq 'view'}">readonly="readonly"</c:if>></td>
										<td id="amount" class="t_right">
											<input id="col_amount${i.index}" name="amount" class="SCTextInput t_right" value="${stringUtil.formatNum(item.amount)}" <c:if test="${changeWriteMode eq 'view'}">readonly="readonly"</c:if> onkeyup="noSpaceForm(this);" onkeydown="javascript:setOnlyNumber(this); setCommaFormat('col_amount${i.index}');">
										</td>
										<td id="pr_status">
											<c:if test="${changeWriteMode eq 'view'}">
												<input class="SCTextInput" value="${item.pr_status}" readonly="readonly">
											</c:if>
											<c:if test="${changeWriteMode eq 'editable'}">
												<input type="hidden" id="pr_status${i.index}" name="pr_status" value="${item.pr_status}">
												<select name="col_prsts_gb" class="wp100" onchange="optionChangeEvent(this, '${i.index}');">
													<c:forEach items="${prStatusDetailCD}" var="code" varStatus="status">
														<c:choose>
															<c:when test="${code.data eq item.pr_status_cd}">
																<option id="col_prsts_gb" value="${code.data}" selected="selected">${code.pr_status}</option>
															</c:when>
															<c:otherwise>
																<option id="col_prsts_gb" value="${code.data}">${code.pr_status}</option>
															</c:otherwise>
														</c:choose>
													</c:forEach>
												</select>
											</c:if>
										</td>
										<td id="purc_grp_nm"><input id="col_purc_grp_nm${i.index}" name="purc_grp_nm" class="SCTextInput" value="${item.purc_grp_nm}" <c:if test="${changeWriteMode eq 'view'}">readonly="readonly"</c:if>></td>
										<td id="dept_nm">
											<input id="col_dept_nm${i.index}" name="dept_nm" class="SCTextInput" value="${item.dept_nm}" <c:if test="${changeWriteMode eq 'view'}">readonly="readonly"</c:if>>
											<input type="hidden" name="dept_cd" value="${item.dept_cd}" readonly="readonly">
										</td>
										<td id="charge_name"><input id="col_charge_name${i.index}" name="charge_name" class="SCTextInput" value="${item.charge_name}" <c:if test="${changeWriteMode eq 'view'}">readonly="readonly"</c:if>></td>
										<td id="approval_name"><input id="col_approval_name${i.index}" name="approval_name" class="SCTextInput" value="${item.approval_name}" <c:if test="${changeWriteMode eq 'view'}">readonly="readonly"</c:if>></td>
										<td id="budget_cls_nm"><input id="col_budget_cls_nm${i.index}" name="budget_cls_nm" class="SCTextInput" value="${item.budget_cls_nm}" <c:if test="${changeWriteMode eq 'view'}">readonly="readonly"</c:if>></td>
										<td id="attach_yn">
											<input id="col_attach_yn" name="attach_yn" class="SCTextInput w20" value="${item.attach_yn}" readonly="readonly">
											<c:if test="${item.attach_yn eq 'Y'}">
												<img class="v_middle m_l5" src="/assets/images/datagrid/clip.png">
											</c:if>
										</td>
									</tr>
								</c:forEach>
							</c:if>
						</tbody>
					</table>
				</div>
			</form>
			<!-- 페이징 -->
			<jsp:include page="/paging.jsp">
				<jsp:param value="10" name="pageGroupSize"/>
				<jsp:param value="15" name="listSize"/>
			</jsp:include>
		</div>

		<!-- 첨부파일 -->
		<div id="loading" class="loading">
			<img id="loading-image" class="loading-image" src="/assets/images/loading2.gif" />
		</div>
	</div>
</body>
</html>
