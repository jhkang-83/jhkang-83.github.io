<%@page import="eswf.dataobject.Map"%>
<%@ page session="true" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@page import="volcano.custom.control.Helper"%>
<%@page import="emro.util.StringUtil"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
	//세션 가져오기
	Helper helper = new Helper(request, new eswf.dataobject.Map());
	Map USER = helper.getUser();
	request.setAttribute("USER", USER);
%>
<!-- 선제적구매지원 현황 글쓰기 화면 -->
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="ie=edge">
<meta http-equiv="Content-Style-Type" content="text/css">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Expires" content="0">
<title>SK Broadband Procument Portal</title>
<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.7.7/xlsx.core.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/xls/0.7.4-a/xls.core.min.js"></script>
<script type="text/javascript">
	window.onload = function() {
		$('#loading').hide();

		$(document).on("keydown","#amount",function(){
			console.log("esprstatus onkeydown~~");
			setOnlyNumber(this);
			setCommaFormat('amount');
		});
	}

	var resultData;


	function readExcel() {
		//확장자 정규식
		var regex = /^([a-zA-Z0-9가-힣\s_\\.\-:])+(.xlsx|.xls)$/;

		/*Checks whether the file is a valid excel file*/

		if (regex.test($("#excelfile").val().toLowerCase())) {

			var xlsxflag = false; /*Flag for checking whether excel is .xls format or .xlsx format*/
			if ($("#excelfile").val().toLowerCase().indexOf(".xlsx") > 0) {
				xlsxflag = true;
			}


			/*Checks whether the browser supports HTML5*/

			if (typeof(FileReader) != "undefined") {
				var reader = new FileReader();
				reader.onload = function(e) {
					var data = e.target.result;

					/*Converts the excel data in to object*/
					if (xlsxflag) {
						var workbook = XLSX.read(data, {
							type: 'binary'
						});

					} else {
						var workbook = XLS.read(data, {
							type: 'binary'
						});

					}

					/*Gets all the sheetnames of excel in to a variable*/

					var sheet_name_list = workbook.SheetNames;
					console.log("sheet_name_list >>> " + workbook.SheetNames);
					var cnt = 0; /*This is used for restricting the script to consider only first sheet of excel*/
					var excelData = [];
					var params;

					sheet_name_list.forEach(function(y) { /*Iterate through all sheets*/

						/*Convert the cell value to Json*/

						if (xlsxflag) {
							var exceljson = XLSX.utils.sheet_to_json(workbook.Sheets[y]);

						} else {
							var exceljson = XLS.utils.sheet_to_row_object_array(workbook.Sheets[y]);
						}

						if (exceljson.length > 0 && cnt == 0) {
							console.log(exceljson); //데이터를 가공하여 사용
							excelData.push(exceljson);
							cnt++;
						}

					});
					//엑셀 업로드 서블릿 ajax call
					ajaxJsonCall("/bw/excelUpload", excelData[0], fnUploadSuccess, fnUploadFail);
				}

				if (xlsxflag) { /*If excel file is .xlsx extension than creates a Array Buffer from excel*/
					reader.readAsArrayBuffer($("#excelfile")[0].files[0]);
				} else {
					reader.readAsBinaryString($("#excelfile")[0].files[0]);
				}

			} else {
				alert("Sorry! Your browser does not support HTML5!");
			}

		} else {
			alert("Please upload a valid Excel file!");
		}
	}

	//엑셀 업로드 sucess 콜백함수
	function fnUploadSuccess(data) {
		console.log(data.excelData);
		console.log(data.prStatusCD);
		console.log("excelData[0].approval_date >> " + data.excelData[0].approval_date);
		resultData = data;

		//엑셀 데이터 만큼 for문 돌면서 <tr><td> 추가
		for(var i = 0; i < data.excelData.length-1; i++) {
			$('tbody').append('<tr>'
			 				+ '<td id="col_approval_date" style="height: 20px;"><input type="text" id="approval_date" name="approval_date" class="SCTextInput" value="'+data.excelData[i].approval_date+'"></td>'
							+ '<td id="col_doc_no" style="height: 20px;"><input type="text" id="doc_no" name="doc_no" class="SCTextInput" value="'+data.excelData[i].doc_no+'"></td>'
			 				+ '<td id="col_approval_title" class="t_left" style="height: 20px;"><input type="text" id="approval_title" name="approval_title" class="SCTextInput t_left" value="'+data.excelData[i].approval_title+'"></td>'
				   			+ '<td id="col_budget_cls_nm" style="height: 20px;"><input type="text" id="budget_cls_nm" name="budget_cls_nm" class="SCTextInput" value="'+data.excelData[i].budget_cls_nm+'"></td>'
							+ '<td id="col_amount" class="t_right" style="height: 20px;"><input type="text" id="amount" name="amount" class="SCTextInput wp100" value="'+data.excelData[i].amount+'"></td>'
							+ '<td id="col_dept_nm" style="height: 20px;"><input type="text" id="dept_nm" name="dept_nm" class="SCTextInput" value="'+data.excelData[i].dept_nm+'"></td>'
							+ '<td id="col_dept_cd" style="height: 20px;"><input type="text" id="dept_cd" name="dept_cd" class="SCTextInput" value="'+data.excelData[i].dept_cd+'"></td>'
							+ '<td id="col_charge_name" style="height: 20px;"><input type="text" id="charge_name" name="charge_name" class="SCTextInput" value="'+data.excelData[i].charge_name+'"></td>'
							+ '<td id="col_approval_name" style="height: 20px;"><input type="text" id="approval_name" name="approval_name" class="SCTextInput" value="'+data.excelData[i].approval_name+'"></td>'
							+ '<td id="col_purc_grp_nm" style="height: 20px;"><input type="text" id="purc_grp_nm" name="purc_grp_nm" class="SCTextInput" value="'+data.excelData[i].purc_grp_nm+'"></td>'
							+ '<td style="height: 20px;">'+'<button type="button" id="attach_yn" class="w60">'+data.excelData[i].attach_yn+'</button>'+'</td>'
							+ '<td style="height: 20px;"><select name="pr_status_cd" class="wp90">'
							+ '</select></td>'
							+ '</tr>');
		}

		for(var j = 0; j < data.prStatusCD.length; j++) {
			$('select').append('<option id="pr_status_cd" class="wp90" value="'+data.prStatusCD[j].data+'">'+data.prStatusCD[j].pr_status+'</option>');
		}
		//alert("succ  : " + data.fields.result);
	}

	function fnUploadFail(data) {
		console.log(data);
		//alert("fail  : " + data["result"]);
	}

	function onClickEvent(e) {
		//행 추가 버튼
		if(e.id == "btnCellAdd") {
			var params = [];
			//구매검토 select 목록 ajax call
			ajaxJsonCall("/bw/excelUpload", params, function(obj){
				console.log(obj.prStatusCD);
				$('tbody').append('<tr style="height:20px;">'
								+ '<td id="col_approval_date" style="height: 20px;"><input type="text" id="approval_date" name="approval_date" class="SCTextInput"></td>'
				 				+ '<td id="col_doc_no" style="height: 20px;"><input type="text" id="doc_no" name="doc_no" class="SCTextInput" value=""></td>'
				 				+ '<td id="col_approval_title" class="t_left" style="height: 20px;"><input type="text" id="approval_title" name="approval_title" class="SCTextInput t_left"></td>'
								+ '<td id="col_budget_cls_nm" style="height: 20px;"><input type="text" id="budget_cls_nm" name="budget_cls_nm" class="SCTextInput"></td>'
								+ '<td id="col_amount" class="t_right" style="height: 20px;">'+'<input type="text" id="amount" name="amount" class="SCTextInput wp100">'+'</td>'
								+ '<td id="col_dept_nm" style="height: 20px;"><input type="text" id="dept_nm" name="dept_nm" class="SCTextInput"></td>'
								+ '<td id="col_dept_cd" style="height: 20px;"><input type="text" id="dept_cd" name="dept_cd" class="SCTextInput"></td>'
								+ '<td id="col_charge_name" style="height: 20px;"><input type="text" id="charge_name" name="charge_name" class="SCTextInput"></td>'
								+ '<td id="col_approval_name" style="height: 20px;"><input type="text" id="approval_name" name="approval_name" class="SCTextInput"></td>'
								+ '<td id="col_purc_grp_nm" style="height: 20px;"><input type="text" id="purc_grp_nm" name="purc_grp_nm" class="SCTextInput"></td>'
								+ '<td style="height:20px;">'+'<button type="button" id="attach_yn" class="w60">파일첨부</button>'+'</td>'
								+ '<td style="height:20px;"><select name="pr_status_cd" class="wp90">'
								+ '</select></td>'
								+ '</tr>');

				for(var i = 0; i < obj.prStatusCD.length; i++) {
					$('select').append('<option id="pr_status_cd" class="wp90" value="'+obj.prStatusCD[i].data+'">'+obj.prStatusCD[i].pr_status+'</option>');
				}
			});
		//저장
		}else if(e.id == "btnNSave") {
			if(confirm("저장 하시겠습니까?")) {
				console.log("tr length >> " + $('tbody').children().length);
				var formData = {"prStatusData" : $("#frmPRStatusList").serializeObject()};
				var params = [];
				var len = $('tbody').children().length;
				var arr = formData.prStatusData;

				console.log("arr >> " + arr);
				if(len > 1) {
					for(var i = 0; i < len; i++) {
						var objFromArr = {
										approval_date : arr.approval_date[i],
										doc_no : arr.doc_no[i],
										approval_title : arr.approval_title[i],
										budget_cls_nm : arr.budget_cls_nm[i],
										amount : removeComma(arr.amount[i]),
										dept_nm : arr.dept_nm[i],
										dept_cd : arr.dept_cd[i],
										charge_name : arr.charge_name[i],
										approval_name : arr.approval_name[i],
										purc_grp_nm : arr.purc_grp_nm[i],
										pr_status_cd : arr.pr_status_cd[i]
										};
						params.push(objFromArr);
					}
				}else {
					params.push(arr);
				}

				//저장 서블릿 ajax call
				ajaxJsonCall("/bw/prStatusInfoSave", params, fnSaveSuccess, fnSaveFail);
			}
		//닫기
		}else if(e.id == "btnNClose") {
			var params = {mode: "search", changeWriteMode:"view"};
			fnPostGoto("/bw/emPRStatusList", params, "_self");
		//부서코드 버튼
		}else if(e.id == "btnDepCodetDown") {
			var params = {fileNm: "esHRDeptGrid"};
			fnPostGoto("/excelExport", params, "_self");
		//양식다운로드 버튼
		}else if(e.id == "btnSampleDown") {
			var params = {fileNm: "esPRStatusSample"};
			fnPostGoto("/excelExport", params, "_self");
		}
	}

	//저장 ajax callback
	function fnSaveSuccess(data) {
		alert(data.message);
		var params = {mode: "search", changeWriteMode:"view"};
		fnPostGoto("/bw/emPRStatusList", params, "_self");
	}

	function fnSaveFail(data) {
		console.log(data);
	}

</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/top_header.jsp"></jsp:include>
		<jsp:include page="/top_menu.jsp"></jsp:include>
	</div>

	<div class="content">
		<div class="container bb_none">
			<div class="titArea bb_solid shadw p_b20">
				<h2>품의List 등록</h2>
				<div class="f_right v_middle">
					<c:if test="${USER.role_list == 'BUYER' || USER.role_list == 'BUYER,BUYERADMIN' || USER.role_list == 'MASTER'}">
						<button type="button" class="saveBtn" id="btnNSave" onclick="onClickEvent(this);">저장</button>
						<button type="button" class="btnNClose" id="btnNClose" onclick="onClickEvent(this);">닫기</button>
					</c:if>
				</div>
			</div>

			<div class="sub_con">
				<div class="sub_tit m_t0">
					<h3>일반정보</h3>
					<div class="f_right v_middle">
						<button type="button" class="" id="btnDepCodetDown" onclick="onClickEvent(this);">부서코드 다운로드</button>
						<button type="button" class="" id="btnSampleDown" onclick="onClickEvent(this);">양식다운로드</button>
						<input type="file" id="excelfile" name="excelfile" onchange="readExcel()" style="display: none;" accept=".xlsx, .xls">
						<button type="button" onclick="onchange=document.all.excelfile.click()">엑셀업로드</button>
						<button type="button" class="" id="btnCellAdd" onclick="onClickEvent(this);">행추가</button>
					</div>
				</div>
				<form id="frmPRStatusList" name="frmPRStatusList" method="post" autocomplete="off">

					<div class="boxList m_t30 m_l10 m_r20 wp100" style="height: 500px;" >
						<div id="grid" class="gridArea o_auto" style="height: 500px;">
							<table id="gridBox" class="tableStyle m_b15" style="width: 100%;">
								<colgroup>
									<col style="width: 7%">
									<col style="width: 10%">
									<col style="width: 25%">
									<col style="width: 5%">
									<col style="width: 5%">
									<col style="width: ">
									<col style="width: ">
									<col style="width: ">
									<col style="width: ">
									<col style="width: ">
									<col style="width: 7%">
									<col style="width: 15%">
								</colgroup>

								<thead>
									<tr>
										<th>품의결재일</th>
										<th>문서번호</th>
										<th>품의명</th>
										<th>Capex/Opex</th>
										<th>금액</th>
										<th>담당부서</th>
										<th>담당부서코드</th>
										<th>담당자</th>
										<th>결재자</th>
										<th>구매담당자</th>
										<th>구매내역서첨부</th>
										<th>구매검토</th>
									</tr>
								</thead>

								<tbody class="">
								</tbody>
							</table>
						</div>
					</div>
				</form>
			</div>
		</div>
		<div id="loading" class="loading">
			<img id="loading-image" class="loading-image" src="/assets/images/loading2.gif" />
		</div>
	</div>
</body>
</html>
