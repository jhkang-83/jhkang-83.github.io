<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- 회원가입 -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Style-Type" content="text/css">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>SK Broadband Procument Portal</title>

<script type="text/javascript">


	//정규식 - 이메일 유효성 검사
	var regEmail = /([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;

	var userBizIdType = "";
	var vndInfo;

	var curTabNum = "1";	//현재 탭 번호 - 초기탭 1번
	var selectTabNum = "1";

	window.onload = function() {

		$(".tabCont2").hide();
		$(".tabCont2:first").show();


		//이벤트셋팅
		setEvent();

		//전화번호 국번 설정
		setPhoneNum();

	}

	function setPhoneNum(){

		var phone     = new Array();
		var cell      = new Array();

		phone.push('02','031','032','033','041','042','043','044','051','052','053','054','055','061','062','063','064','070','0505','010','011','016','017','018','019');
		cell.push('010','011','016','017','018','019');

		//업체 전화번호
		$("#S_REPRE_PHN_NO1").empty();
		for(var i = 0; i < 24; i++){
			$("#S_REPRE_PHN_NO1").append('<option value="'+phone[i]+'">'+phone[i]+'</option>');
		}

		//업체 팩스번호
		$("#S_REPRE_FAX_NO1").empty();
		for(var i = 0; i < 24; i++){
			$("#S_REPRE_FAX_NO1").append('<option value="'+phone[i]+'">'+phone[i]+'</option>');
		}

		//대표 사용자 전화번호
		$("#S_PHONE_NO1").empty();
		for(var i = 0; i < 24; i++){
			$("#S_PHONE_NO1").append('<option value="'+phone[i]+'">'+phone[i]+'</option>');
		}

		//대표 사용자 팩스번호
		$("#S_FAX_NO1").empty();
		for(var i = 0; i < 24; i++){
			$("#S_FAX_NO1").append('<option value="'+phone[i]+'">'+phone[i]+'</option>');
		}

		//대표 사용자 휴대폰번호
		$("#S_CELL_PHONE_NO1").empty();
		for(var i = 0; i < 6; i++){
			$("#S_CELL_PHONE_NO1").append('<option value="'+cell[i]+'">'+cell[i]+'</option>');
		}

	}


	//이벤트셋팅
	function setEvent() {
		//사용자 정보 저장
		$("#btnSave").click(function(e) {
			saveUserInfo();
		});
		//사용자 정보 저장
		$("#btnSaveI").click(function(e) {
			saveUserInfoI();
		});

		///////////////////////////////

		//약관동의 다음 버튼
		$("#btnNextTab1").click(function(e) {
			selectTab(2);
		});

		//사업자번호확인 다음 버튼
		$("#btnNextTab2").click(function(e) {
			selectTab(3);
		});

	}


	//////////////////////////////////////////////////////////////
	///////////////////// 탭이동 관련 ///////////////////////////

	function moveTabMenu(tabNum){
		console.log("moveTabMenu tabNum >>> " + tabNum);
		console.log("moveTabMenu curTabNum >>> " + curTabNum);
		console.log("moveTabMenu selectTabNum >>> " + selectTabNum);
		console.log("moveTabMenu tabCheckValue >>> " + $("#tabCheckValue" + curTabNum).val());
		$('.tabCont2').hide();
		$('.tabCont2:first').show();

		$("ul.tabDesign li").removeClass("on");

		$("#tabMenu" + tabNum).addClass("on");
		$('.tabCont2').hide();
		var activeTab = $("#tab" + tabNum).attr("rel");
		$("#tab" + tabNum).fadeIn();


		curTabNum = tabNum;
	}


	//탭 클릭시
	function selectTab(tabNum){

		if(curTabNum == tabNum){
			return;
		}
		console.log("==========   : " + tabNum);

		selectTabNum = tabNum;

		if(tabNum == 1){			//약관동의 탭 클릭시

			if(parseInt(curTabNum) > parseInt(tabNum) ){
				alert("이전 탭으로 이동하여 정보를 수정하면 입력한 내용이 초기화될 수 있습니다.");
				$("#empty_biz_reg_no_dup_chk").val("F");
				$("#tabCheckValue" + curTabNum).val("F");
			}
			//텝메뉴이동
			console.log("약관정보 탭 이동");
			moveTabMenu(tabNum);

		}else if(tabNum == 2){	//사업자번호 확인 탭 클릭시

			if(parseInt(curTabNum) > parseInt(tabNum) ){
				alert("이전 탭으로 이동하여 정보를 수정하면 입력한 내용이 초기화될 수 있습니다.");
				$("#empty_biz_reg_no_dup_chk").val("F");
				$("#tabCheckValue" + curTabNum).val("F");
			}

			$("#tabCheckValue1").val("F");	//재검사 하도록 검사값 초기화
			tabCheckValue1();

			//약관동의 통과했을경우  사업자번호 확인 탭 이동
			if($("#tabCheckValue1").val() == "S"){
				console.log("사업자번호 확인탭 이동");
				//텝메뉴이동
				moveTabMenu(tabNum);
			}else{
				tabChkMove(tabNum);
				return;
			}


		}else if(tabNum == 3){	//기본정보 탭 클릭시

			$("#empty_biz_reg_no_text").text($("#empty_first_biz_reg_no").val() + "-" + $("#empty_second_biz_reg_no").val()  + "-" + $("#empty_third_biz_reg_no").val());		//사업자번호
			$("#empty_biz_reg_nm_text").text($("#vd_nm_loc").val());				//사업자명

			$("#empty_corp_reg_no_text").text($("#empty_first_corp_reg_no").val() + "-" + $("#empty_second_corp_reg_no").val() );					//법인번호

			if(parseInt(curTabNum) > parseInt(tabNum) ){
				alert("이전 탭으로 이동하여 정보를 수정하면 입력한 내용이 초기화될 수 있습니다.");
			}

			$("#tabCheckValue1").val("F");	//재검사 하도록 검사값 초기화
			tabCheckValue1();							//약관동의 탭 입력 검사


			if($("#tabCheckValue1").val() == "S"){

				$("#tabCheckValue2").val("F");	//재검사 하도록 검사값 초기화
				tabCheckValue2();						//사업자번호 확인 탭 입력 검사
			}else {
				tabChkMove(tabNum);
				return;
			}

			if($("#tabCheckValue2").val() == "S"){
				console.log("==========  tabCheckValue2 >> " + $("#tabCheckValue2").val());
				if($("#empty_biz_reg_no_dup_chk").val() == "S"){	//사업자번호 중복확인
					if( $("#biz_reg_no").val() != $("#empty_biz_reg_no_ins_chk").val()){		//입력된 사업자번호 와 중복확인된 사업자번호 비교
						alert("사업자번호 중복확인이 필요합니다.");
						$("#empty_biz_reg_no_dup_chk").val("F");
						moveTabMenu(2);
						return;
					}
				}else{
					alert("사업자번호 중복확인이 필요합니다.");
					$("#empty_biz_reg_no_dup_chk").val("F");
					moveTabMenu(2);
					return;
				}

			}else {
				tabChkMove(tabNum);
				return;
			}


			//약관동의, 사업자번호 확인, 사업자번호 중복확인 통과했을경우  기본정보 유효성검사 탭 이동
			if($("#tabCheckValue1").val() == "S"  && $("#tabCheckValue2").val() == "S" && $("#empty_biz_reg_no_dup_chk").val() == "S"){

				//텝메뉴이동
				console.log("기본정보 탭 이동");

				//사업자기본정보 화면 설정
				fnSetVndInfoView();

				moveTabMenu(tabNum);
			}else{
				console.log("기본정보 탭 이동 불가");
				tabChkMove(tabNum);
				return;
			}

		}

	}

	function tabChkMove(tabNum){
		console.log("curTabNum   : " +curTabNum);
		console.log("tabCheckValue" +tabNum +   ": " +$("#tabCheckValue" + tabNum).val());
		console.log("tabNum   : " + tabNum);

		if($("#tabCheckValue1").val() == "F"){
		console.log("==========  tabChkMove 1" );
		//moveTabMenu(1);
			if(parseInt(tabNum) != 1){
				moveTabMenu(1);
			}
		}else if($("#tabCheckValue2").val() == "F" || $("#empty_biz_reg_no_dup_chk").val() == "F"){
		console.log("==========  tabChkMove 2");
			//moveTabMenu(2);
			if(parseInt(tabNum) != 2){
				moveTabMenu(2);
			}
		}else if($("#tabCheckValue3").val() == "F"){
		console.log("==========  tabChkMove 3");
			//moveTabMenu(3);
			if(parseInt(tabNum) != 3){
				moveTabMenu(3);
			}
		}
	}

	///////////////////// 탭이동 관련 ///////////////////////////



	///////////////// 약관동의 탭 관련 function ////////////
	//약관동의 탭 유효성 검사
	function tabCheckValue1(){

		if(!$("#C_TERMS_AGREE_YN").is(":checked") ){
			alert("약관에 동의해 주세요.");
			$("#C_TERMS_AGREE_YN").focus();
			$("#tabCheckValue1").val("F");
			return;
		}

		if(!$("#C_INFO_AGREE_YN").is(":checked") ){
			alert("개인정보 취급방침 및 정보공유에 동의해 주세요.");
			$("#C_INFO_AGREE_YN").focus();
			$("#tabCheckValue1").val("F");
			return;
		}

		if(!$("#C_AGREE_YN").is(":checked") ){
			alert("개인정보 수집 및 이용에 동의해 주세요.");
			$("#C_AGREE_YN").focus();
			$("#tabCheckValue1").val("F");
			return;
		}

		if( !$('#R_TERMS3_AGREE_YN').is(':checked') ) {
			alert("제3자 정보제공에 동의해 주세요.");
			$("#R_TERMS3_AGREE_YN").focus();
			$("#tabCheckValue1").val("F");
			return;
		}

		$("#tabCheckValue1").val("S");
	}



	///////////////// 사업자번호 확인 탭 관련 function ////////////

	//개인사업자, 법인사업자 선택시 화면 컨트롤
	function chgVndIdType(str){

		if(str == "B"){			//개인사업자라면
			$("#tab2View1").css('display', 'none');

			$("#tabCheckValue2").val("F");				//혹시모를 상황에 대비해 F 설정

			$("#tab3View1").css('display', 'none');		//기본정보탭 설정

		}else if(str == "A"){	//법인 사업자라면

			$("#tab2View1").css('display', '');

			$("#tabCheckValue2").val("F");				//혹시모를 상황에 대비해 F 설정

			$("#tab3View1").css('display', '');			//기본정보탭 설정
		}
	}

	//사업자 번호 확인 탭 유효성 검사
	function tabCheckValue2(){

		valueChkPobusi();			//사업자 확인

	}

	//사업자번호 중복체크
	function dupVndNo(){
/////////////////////// 사업자번호 중복확인 ////////////////////////


		if( ($("#vd_nm_loc").val()).trim()  == "" ){

			alert("사업자명을 입력하세요.");
			$("#vd_nm_loc").focus();
			$("#tabCheckValue2").val("F");
			return;
		}

		var regNo =   $("#empty_first_biz_reg_no").val() + "" + $("#empty_second_biz_reg_no").val()  + "" + $("#empty_third_biz_reg_no").val() ;

		$("#biz_reg_no").val(regNo);		//DB전송용 사업자번호입력

		if( !BusinessNumber (regNo)){		//유효하지않다면
			$("#empty_first_biz_reg_no").focus();
			$("#tabCheckValue2").val("F");
			return;
		}

		var params = {
				biz_reg_no : $("#biz_reg_no").val()
		};
		$.extend(params, fnGetParams());

		var valType = $(':radio[name="R_VENDOR_ID_TYPE"]:checked').val();
		var chkPobusi = $("#empty_first_biz_reg_no").val() + "" + $("#empty_second_biz_reg_no").val()  + "" + $("#empty_third_biz_reg_no").val();

		console.log("valType   : " + valType);
		console.log("chkPobusi   : " + chkPobusi);
		var params = {
				chk_mode   : "bizRegNo"
				,biz_reg_no : chkPobusi
				,comp_cd   : "SKB"
		};
		console.log("params   : " + params);
		//사업자번호 중복확인
		ajaxJsonCall("/sp/selectVndRegDupChk", params, function(obj){
			console.log("ajaxJsonCall obj  : " + obj);
			if(obj.member_cnt > 0){		//중복정보가 있을경우


				$("#empty_biz_reg_no_dup_chk").val("S");
				$("#empty_biz_reg_no_ins_chk").val( $("#biz_reg_no").val() );
				$("#empty_vendor_info_id_dup").val("Y");		//회원가입 상태 여부

				alert("입력하신 사업자등록번호는 이미 회원가입된 번호입니다.\n추가로 일반ID 신청을 하고자 하시면 확인 후 다음 버튼을 클릭해 주세요.");

				console.log(JSON.stringify(obj.resultMap));
				vndInfo = obj;		//가입된 사업자 업체정보 설정
				userBizIdType = "I";
				fnSetVndBaseInfo();

				//사업자번호 조회시 기본정보 내용 삭제
				$("#tab3Form").each(function(){
				    this.reset();
				});

			}else{		//중복정보가 없을경우

				alert("가입 가능한 사업자 번호입니다.");
				$("#empty_biz_reg_no_dup_chk").val("S");
				$("#empty_biz_reg_no_ins_chk").val( $("#biz_reg_no").val() );
				$("#empty_vendor_info_id_dup").val("N");		//회원가입 상태 여부

				userBizIdType = "S";

				//사업자번호 조회시 기본정보 내용 삭제
				$("#tab3Form").each(function(){
				    this.reset();
				});

			}
			console.log("ajaxJsonCall   : " + obj);

		});


		/////////////////////// 사업자번호 중복확인 ////////////////////////
	}


	//기존사업자 있을경우 사업자정보 화면 설정
	function fnSetVndBaseInfo(){

		if(vndInfo.vd_cls == "B"){

			$("#R_VENDOR_ID_TYPE1").trigger('click');
			$("#vd_nm_loc").val(vndInfo.vd_nm_loc);
		}else if(vndInfo.vd_cls == "A"){
			$("#R_VENDOR_ID_TYPE2").trigger('click');

			var CORP_NO = vndInfo.corp_reg_no;

			$("#vd_nm_loc").val(vndInfo.vd_nm_loc);
			$("#empty_first_corp_reg_no").val(CORP_NO.substr(0, 6));
			$("#empty_second_corp_reg_no").val(CORP_NO.substr(6, 7));

		}

	}

	//사업정보 화면 설정
	function fnSetVndInfoView(){
		if(userBizIdType == "S"){

			$(".biz").removeAttr("disabled");

			$("#bizTypeS").css('display', '');
			$("#bizTypeI").css('display', 'none');
			$("#etcSave").css('display', '');

		}else if(userBizIdType == "I"){		//일반아이디 가입상태라면

			$(".biz").attr("disabled","disabled");

			$("#bizTypeS").css('display', 'none');
			$("#bizTypeI").css('display', '');
			$("#etcSave").css('display', '');


			//기본정보 설정
			if(vndInfo.phone_no != null) {
				var REPRE_PHONE_NO = (vndInfo.phone_no).split('-');
				$("#phone_no1").val(REPRE_PHONE_NO[0]);					//대표자 전화번호
				$("#phone_no2").val(REPRE_PHONE_NO[1]);					//대표자 전화번호
				$("#phone_no3").val(REPRE_PHONE_NO[2]);					//대표자 전화번호
			}

			if(vndInfo.fax_no != null) {
				var REPRE_FAX_NO = (vndInfo.fax_no).split('-');
				$("#fax_no1").val(REPRE_FAX_NO[0]);						//대표자 팩스번호
				$("#fax_no2").val(REPRE_FAX_NO[1]);						//대표자 팩스번호
				$("#fax_no3").val(REPRE_FAX_NO[2]);						//대표자 팩스번호
			}

			$("#rep_nm_loc").val(vndInfo.rep_nm_loc);				//대표자명
			$("#empty_biz_reg_nm_text").text(vndInfo.vd_nm_loc);
			$('.vd_size option[value='+ vndInfo.vd_size +']').attr('selected', 'selected');		//기업규모
			$("#dtl_addr_1_loc").val(vndInfo.dtl_addr_1_loc);		//주소1
			$("#dtl_addr_2_loc").val(vndInfo.dtl_addr_2_loc);		//주소2
			$("#bos").val(vndInfo.bos);								//업태
			$("#tob").val(vndInfo.tob);								//업종
			$("#main_trad_item").val(vndInfo.main_trad_item);		//품목및서비스

		}

	}


	//사업자명, 사업자번호 확인
	function valueChkPobusi(){

		var valType = $(':radio[name="R_VENDOR_ID_TYPE"]:checked').val();

		if( ($("#vd_nm_loc").val()).trim()  == "" ){

			alert("사업자명을 입력하세요.");
			$("#vd_nm_loc").focus();
			$("#tabCheckValue2").val("F");
			return;
		}

		var regNo =   $("#empty_first_biz_reg_no").val() + "" + $("#empty_second_biz_reg_no").val()  + "" + $("#empty_third_biz_reg_no").val() ;

		$("#biz_reg_no").val(regNo);		//DB전송용 사업자번호입력

		if( !BusinessNumber (regNo)){		//유효하지않다면
			$("#empty_first_biz_reg_no").focus();
			$("#tabCheckValue2").val("F");
			return;
		}

		if(valType == "B"){				//개인사업자일경우


			$("#tabCheckValue2").val("S");
			$("#T_VENDOR_ID_TYPE").val("B");	//개인사업자

		}else if(valType == "A"){		//법인사업자일경우

			valueChkCorp();				//법인확인
		}
	}

	//법인명, 법인번호 확인
	function valueChkCorp(){
		var corpNo =   $("#empty_first_corp_reg_no").val() + "" + $("#empty_second_corp_reg_no").val() ;

		if(  $("#empty_first_corp_reg_no").val()  == "" ){

			alert("법인번호를 입력하세요.");
			$("#empty_first_corp_reg_no").focus();
			$("#tabCheckValue2").val("F");
			return;
		}
		if(  $("#empty_second_corp_reg_no").val()  == "" ){

			alert("법인번호를 입력하세요.");
			$("#empty_second_corp_reg_no").focus();
			$("#tabCheckValue2").val("F");
			return;
		}
		if( corpNo  == "" ){

			alert("법인번호를 입력하세요.");
			$("#empty_first_corp_reg_no").focus();
			$("#tabCheckValue2").val("F");
			return;
		}
		if( corpNo.length != 13){
			alert("법인번호 자릿수가 올바르지 않습니다.");
			$("#empty_first_corp_reg_no").focus();
			$("#tabCheckValue2").val("F");
			return;
		}


		$("#tabCheckValue2").val("S");
		$("#T_VENDOR_ID_TYPE").val("A");		//법인사업자
	}




	///////////////// 기본정보 탭 관련 function ////////////

	function tabCheckValue3(){

		if(($("#rep_nm_loc").val()).trim() == ''  ){
			alert("대표자명을 입력하세요.");
			$("#rep_nm_loc").focus();
			$("#tabCheckValue3").val("F");
			return;
		}

		if(($("#phone_no1").val()).trim() == ''  ){
			alert("대표 전화번호를 입력하세요.");
			$("#phone_no1").focus();
			$("#tabCheckValue3").val("F");
			return;
		}

		if(($("#phone_no2").val()).trim() == ''  ){
			alert("대표 전화번호를 입력하세요.");
			$("#phone_no2").focus();
			$("#tabCheckValue3").val("F");
			return;
		}

		if(($("#phone_no3").val()).trim() == ''  ){
			alert("대표 전화번호를 입력하세요.");
			$("#phone_no3").focus();
			$("#tabCheckValue3").val("F");
			return;
		}
		//대표 전화번호 합치기
		var phone_no = $("#phone_no1").val() + "-" + $("#phone_no2").val() + "-" + $("#phone_no3").val();
		$("#phone_no").val( phone_no );

		//대표 팩스번호 합치기
		var fax_no = $("#fax_no1").val() + "-" + $("#fax_no2").val() + "-" + $("#fax_no3").val();
		$("#fax_no").val( fax_no );


		if(($("#dtl_addr_1_loc").val()).trim() == ''  ){
			alert("주소를 입력하세요.");
			$("#dtl_addr_1_loc").focus();
			$("#tabCheckValue3").val("F");
			return;
		}

		if(($("#dtl_addr_2_loc").val()).trim() == ''  ){
			alert("주소를 입력하세요.");
			$("#dtl_addr_2_loc").focus();
			$("#tabCheckValue3").val("F");
			return;
		}

		if(($("#bos").val()).trim() == ''  ){
			alert("업태를 입력하세요.");
			$("#bos").focus();
			$("#tabCheckValue3").val("F");
			return;
		}

		if(($("#tob").val()).trim() == ''  ){
			alert("업종을 입력하세요.");
			$("#tob").focus();
			$("#tabCheckValue3").val("F");
			return;
		}

		tabCheckValue3_I();
	}

	function tabCheckValue3_I(){
		console.log("tabCheckValue3_I ========");

		if( ($("#charger_id").val()).trim() == ""){
			alert("아이디를 입력하세요.");
			$("#charger_id").focus();
			return false;
		}

		if( ($("#charger_id").val()).length < 5){
			alert("아이디는 최소 5자리 입니다.");
			$("#charger_id").focus();
			return false;
		}

		if( ($("#charger_id_dup_chk").val()).trim() != "S"){
			alert("아이디 중복확인이 필요합니다.");
			$("#charger_id").focus();
			return false;
		}

		if( $("#charger_id").val() != $("#charger_id_ins_chk").val()){
			alert("아이디 중복확인이 필요합니다.");
			$("#charger_id").focus();
			return false;
		}

		if( ($("#pw").val()).trim() == ""){
			alert("비밀번호를 입력하세요.");
			$("#pw").focus();
			return false;
		}

		if( ($("#pw").val()).length < 10 || ($("#pw").val()).length > 20){
			alert("비밀번호는 10~20 자리 입니다.");
			$("#pw").focus();
			return false;
		}

		var uid = $("#charger_id").val();
		var upw = $("#pw").val();
		var chk_num = upw.search(/[0-9]/g);
		var chk_eng = upw.search(/[a-zA-Z]/g);
		var chk_spe = upw.search(/[~!@#$%^&*()-]/g);

		var continuePass01 = 0;
		var continuePass02 = 0;
		var passChar0;
		var passChar1;
		var passChar2;

		//비밀번호 조건 : 10자 이상 숫자, 영문자, 특수문자 모두 포함
		//동일한 문자 3번 이상 사용 안됨
		//연속 된 문자 3자 이상 사용 안됨
		if (chk_num < 0 || chk_eng < 0 || chk_spe < 0) {
	    	alert('비밀번호는 숫자와 영문자, 특수문자를 혼용하여야 합니다.\n(ex: skb1Vendor!))');
	    	$("#pw").focus();
	    	return false;
		}

		if(!(/^(?=.*[a-zA-Z])(?=.*[~!@#$%^&*()-])(?=.*[0-9]).{8,20}$/).test(upw)) {
			alert('비밀번호는 영문/숫자/특수문자의 조합으로 8~20자리를 사용해야 합니다. \n사용 가능한 특수문자 :~!@#$%^&*()-');
			$("#pw").focus();
	    	return false;
		}

		if(upw.search(/\s/) != -1) {
			alert("비밀번호는 공백 없이 입력해주세요.");
			$("#pw").focus();
	    	return false;
		}

		if(/(\w)\1\1\1/.test(upw)) {
			alert("동일한 문자를 3번 이상 사용하실 수 없습니다. \n(ex: skbbb1Vendor!!!)");
			$("#pw").focus();
			return false;
		}

		for(var i = 0; i < upw.length; i++) {
			passChar0 = upw.charAt(i);
			passChar1 = upw.charAt(i+1);
			passChar2 = upw.charAt(i+2);

			if(passChar0.charCodeAt(0)-passChar1.charCodeAt(0) == 1 && passChar1.charCodeAt(0)-passChar2.charCodeAt(0) == 1) {
				continuePass01 = continuePass01 + 1;
			}

			if(passChar0.charCodeAt(0)-passChar1.charCodeAt(0) == -1 && passChar1.charCodeAt(0)-passChar2.charCodeAt(0) == -1) {
				continuePass02 = continuePass02 + 1;
			}

			if(continuePass01 > 1 || continuePass02 > 1) {
				alert("연속된 문자를 3자 이상 사용 할 수 없습니다. \n(ex: abcd, 1234 또는 dcba, 4321 또는 abc!123, bca!321)");
				return false;
			}
		}

		if(upw.search(uid.toLowerCase()) > -1) {
			alert("비밀번호에 아이디를 포함 할 수 없습니다.");
			$("#pw").focus();
			return false;
		}

		if( ($("#pw_confirm").val()).trim() == ""){
			alert("비밀번호 확인이 필요합니다.");
			$("#pw_confirm").focus();
			return false;
		}

		if( $("#pw").val() != $("#pw_confirm").val()){
			alert("입력하신 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
			$("#pw").focus();
			return false;
		}


		if( ($("#chr_usr_nm").val()).trim() == ""){
			alert("성명을 입력하세요.");
			$("#chr_usr_nm").focus();
			return false;
		}

		if( ($("#email").val()).trim() == ""){
			alert("이메일을 입력하세요.");
			$("#email").focus();
			return false;
		}

		if(($("#email").val()).trim() != ''  ){

			if(!regEmail.test($("#email").val())) {
		        alert("이메일 주소가 유효하지 않습니다");
		        $("#email").focus();
		        return;
			}
		}

		if(($("#chr_phone_no1").val()).trim() == ''  ){
			alert("전화번호를 입력하세요.");
			$("#chr_phone_no1").focus();
			$("#tabCheckValue3").val("F");
			return;
		}

		if(($("#chr_phone_no2").val()).trim() == ''  ){
			alert("전화번호를 입력하세요.");
			$("#chr_phone_no2").focus();
			$("#tabCheckValue3").val("F");
			return;
		}

		if(($("#chr_phone_no3").val()).trim() == ''  ){
			alert("전화번호를 입력하세요.");
			$("#chr_phone_no3").focus();
			$("#tabCheckValue3").val("F");
			return;
		}
		//대표 사용자정보  전화번호 합치기
		var chr_phone_no = $("#chr_phone_no1").val() + "-" + $("#chr_phone_no2").val() + "-" + $("#chr_phone_no3").val();
		$("#chr_phone_no").val( chr_phone_no  );

		if(($("#chr_mobile_no1").val()).trim() == ''  ){
			alert("핸드폰 번호를 입력하세요.");
			$("#chr_mobile_no1").focus();
			$("#tabCheckValue3").val("F");
			return;
		}

		if(($("#chr_mobile_no2").val()).trim() == ''  ){
			alert("핸드폰 번호를 입력하세요.");
			$("#chr_mobile_no2").focus();
			$("#tabCheckValue3").val("F");
			return;
		}

		if(($("#chr_mobile_no3").val()).trim() == ''  ){
			alert("핸드폰 번호를 입력하세요.");
			$("#chr_mobile_no3").focus();
			$("#tabCheckValue3").val("F");
			return;
		}
		//대표 사용자정보  핸드폰 번호 합치기
		var chr_mobile_no = $("#chr_mobile_no1").val() + "-" + $("#chr_mobile_no2").val() + "-" + $("#chr_mobile_no3").val();
		$("#chr_mobile_no").val(chr_mobile_no);


		$("#tabCheckValue3").val("S");

	}


	//////////////////////////////////////////////////////////////


	function fnParams() {
		var params = fnGetParams();
		return params;
	}

	//소문자변경
	function fnToUpper(r){

		r.value = r.value.toUpperCase();
	}


	//저장
	function saveUserInfo(){


		//유효성검사 실패시 탭이동
		if($("#tabCheckValue1").val() != "S"){
			selectTab('1');
		}else if($("#tabCheckValue2").val() != "S"){
			selectTab('2')
		}else if($("#tabCheckValue3").val() != "S"){
			selectTab('3')
		}


		//약관동의, 사업자번호 확인, 기본정보 탭 유효성검사 성공했을경우
		if($("#tabCheckValue1").val() == "S"  && $("#tabCheckValue2").val() == "S" && $("#tabCheckValue3").val() == "S"){

			if(confirm("회원가입 하시겠습니까?")){

				$("#charger_id").val( ($("#charger_id").val()).toUpperCase()  );		//유저아이디 소문자 설정

				var params = {};

				$.extend(params, fnGetParams());

				console.log(params);

				ajaxJsonCall("/sp/vendorJoinInfoSave", params, fnSaveSuccess, fnSaveFail);
			}
		}



	}

	//저장
	function saveUserInfoI(){
		var biz_reg_no = "";
		var vd_cls = "";
		var corp_reg_no = "";

		if(userBizIdType == "S") {
			tabCheckValue3();
			biz_reg_no = $("#biz_reg_no").val();
			vd_cls = $('#T_VENDOR_ID_TYPE').val();
			corp_reg_no = $('empty_corp_reg_no').val();

		}else if(userBizIdType == "I"){
			tabCheckValue3_I();
			biz_reg_no = vndInfo.biz_reg_no;
			vd_cls = vndInfo.vd_cls;
			corp_reg_no = vndInfo.corp_reg_no;
		}

		//유효성검사 실패시 탭이동
		if($("#tabCheckValue1").val() != "S"){
			selectTab('1');
		}else if($("#tabCheckValue2").val() != "S"){
			selectTab('2')
		}else if($("#tabCheckValue3").val() != "S"){
			selectTab('3')
		}


		//약관동의, 사업자번호 확인, 기본정보 탭 유효성검사 성공했을경우
		if($("#tabCheckValue1").val() == "S"  && $("#tabCheckValue2").val() == "S" && $("#tabCheckValue3").val() == "S"){
			var pw = $("#pw").val();
			$("#pw").val(encpw(pw));

			if(confirm("회원가입 하시겠습니까?")){

				$("#charger_id").val( ($("#charger_id").val()).toUpperCase()  );		//유저아이디 소문자 설정

				var params = {
						biz_reg_no : biz_reg_no
						,corp_reg_no : corp_reg_no
						,vd_cls : vd_cls
						,vendor_term_title1 : "가입약관"
						,vendor_term_title2 : "개인정보취급방침및정보공유동의약관"
						,vendor_term_title3 : "개인정보 수집 및 이용에 대한 동의"
						,vendor_term_title4 : "개인정보 제3자 제공에 대한 동의"
				};

				$.extend(params, fnGetParams());

				ajaxJsonCall("/sp/vendorJoinInfoSave", params, fnSaveSuccess, fnSaveFail);
			}
		}
	}

	function fnSaveSuccess(data) {
		console.log(data);
		//alert("succ  : " + data.fields.result);
		if(data.result == "S"){

			var status = data.status;


			if(status == "INSERT"){
				alert("회원가입 되었습니다. \n로그인 후 이용해 주세요.");
			}

			fnPostGoto("/loginSupplier", "view", "_self");
		}
	}

	function fnSaveFail(data) {
		console.log(data);
		//alert("fail  : " + data["result"]);
	}


	//중복아이디 조회
	function dupId(){

		//아이디 유효성 검사 (영문소문자, 숫자만 허용)
		var charger_id = $("#charger_id").val();
      for (i = 0; i < charger_id.length; i++) {
          ch = charger_id.charAt(i)
          if (!(ch >= '0' && ch <= '9') && !(ch >= 'a' && ch <= 'z')&&!(ch >= 'A' && ch <= 'Z')) {
              alert("아이디는 대소문자, 숫자만 입력가능합니다.")
              $("#charger_id").focus();
              $("#charger_id").select();
              return false;
          }
      }
      //아이디에 공백 사용하지 않기
      if (charger_id.indexOf(" ") >= 0) {
          alert("아이디에 공백을 사용할 수 없습니다.")
          $("#charger_id").focus();
          return false;
      }

		if( ($("#charger_id").val()).trim() == ""){
			alert("아이디를 입력하세요.");
			$("#charger_id").focus();
			return false;
		}

		if( ($("#charger_id").val()).length < 5){
			alert("아이디는 최소 5자리 입니다.");
			$("#charger_id").focus();
			return false;
		}

		$("#charger_id").val( ($("#charger_id").val()).toUpperCase()  );		//유저아이디 소문자 설정

		var params = {
				chk_mode  : "ID",
				charger_id : $("#charger_id").val()
		};
		$.extend(params, fnGetParams());

		ajaxJsonCall("/sp/selectVndRegDupChk", params, function(obj){

			if(obj.dupid_yn == "Y"){
				alert("중복된 아이디입니다.");
				$("#charger_id_dup_chk").val("F");
				$("#charger_id_ins_chk").val( "" );		//저장전에 바뀌었는지 확인용 임시저장값
				return;
			}else{
				alert("사용가능 한 아이디입니다.");
				$("#charger_id_dup_chk").val("S");
				$("#charger_id_ins_chk").val( $("#charger_id").val() );		//저장전에 바뀌었는지 확인용 임시저장값
				return;
			}

		});
	}

	function encpw(value) {
        var encPass = hex_sha1(value);
        return encPass.replace(/0/gi,'');
    }



</script>
</head>
<body>
	<div class="registerWrap">
		<div class="registerHead fixed">
			<jsp:include page="/top_header.jsp"></jsp:include>
			<div class="registerHead title">
				<h2>회원가입</h2>
			</div>
		</div>

		<div id="registerCon" class="registerCon fixed">
			<ul id="tabDesign" class="tabDesign">
				<li class="on" rel="tab1" id="tabMenu1">
					<a href="javascript:selectTab(1);">약관동의</a>
				</li>
				<li rel="tab2" id="tabMenu2">
					<a href="javascript:selectTab(2);">사업자번호 확인</a>
				</li>
				<li rel="tab3" id="tabMenu3">
					<a href="javascript:selectTab(3);">기본정보</a>
				</li>
			</ul>

			<!-- 가입약관  -->
			<div class="tabCont2" id="tab1">
				<div class="sub_tit">
					<h3>가입약관</h3>
				</div>

				<textarea readonly="readonly" rows="5">${c_terms_agree.agre_desc}</textarea>
				<p class="btmCheck">
					<label>
						<input type="checkbox" id="C_TERMS_AGREE_YN" name="C_TERMS_AGREE_YN">
						약관에 동의 합니다.
					</label>
				</p>

				<div class="sub_tit">
					<h3>개인정보 취급방침 및 정보공유 동의</h3>
				</div>
				<textarea readonly="readonly" rows="8">${c_info_agree.agre_desc}</textarea>
				<p class="btmCheck">
					<label>
						<input type="checkbox" id="C_INFO_AGREE_YN" name="C_INFO_AGREE_YN">
						개인정보 취급방침 및 정보공유에 동의합니다.
					</label>
				</p>

				<div class="sub_tit">
					<h3>개인정보 수집 및 이용에 대한 동의</h3>
				</div>
				<textarea readonly="readonly" rows="10">${c_agree_1.agre_desc}

${c_agree_2.agre_desc}
				</textarea>
				<p class="btmCheck">
					<label>
						<input type="checkbox" id="C_AGREE_YN" name="C_AGREE_YN">
						개인정보 수집 및 이용에 동의합니다.
					</label>
				</p>

				<div class="sub_tit">
					<h3>개인정보 제3자 제공에 대한 동의</h3>
				</div>

				<textarea readonly="readonly" rows="10" >${r_terma3_agree_1.agre_desc}

${r_terma3_agree_2.agre_desc}
				</textarea>
				<p class="btmCheck">
					<label>
						<input type="checkbox" id="R_TERMS3_AGREE_YN" name="R_TERMS3_AGREE_YN">
						개인정보 제3자 제공에 동의합니다.
					</label>
					<input type="hidden" id="tabCheckValue1" name="tabCheckValue1" value="S">
				</p>

				<div class="registerBtnArea">
					<a class="btnTop" href="#none" id="btnNextTab1">다음</a>
				</div>
			</div>

			<!-- 사업자번호 확인     -->
			<div class="tabCont2" id="tab2" style="display: none;">
				<div class="sub_tit m_t0">
					<h3>사업자 유형</h3>
				</div>
				<div class="radioCheck m_t30">
					<input type="radio" id="R_VENDOR_ID_TYPE1" name="R_VENDOR_ID_TYPE" value="B" onchange="chgVndIdType('B')">
					<label for="R_VENDOR_ID_TYPE1">개인사업자</label>
					<input type="radio" id="R_VENDOR_ID_TYPE2" name="R_VENDOR_ID_TYPE" value="A" onchange="chgVndIdType('A')" checked="checked">
					<label for="R_VENDOR_ID_TYPE2">법인사업자</label>
					<input type="hidden" id="T_VENDOR_ID_TYPE" name="T_VENDOR_ID_TYPE" value="">
				</div>

				<p class="'markicon">
					주의사항 : 자신이 근무중인 회사를 위해 업무 목적의 ID 발급을 필요로 하는 개인은 회사가 해당하는 사업자로 회원가입 형태를 선택해야 합니다.
				</p>

				<table class="tableStyle m_t15">
					<colgroup>
						<col style="width: 15%;">
						<col style="widtth: *">
					</colgroup>
					<tbody>
						<tr>
							<th>
								<span class="req">사업자명</span>
							</th>
							<td>
								<input type="text" class="w200" id=vd_nm_loc name="vd_nm_loc" onkeydown="javascript:setTextLimit('vd_nm_loc', 100);">
								<div class="t_right d_none">
									<span id="EMPTY_BIZREG_NM_MSG">15/100</span>
								</div>
							</td>
						</tr>
						<tr>
							<th>
								<span class="req">사업자번호</span>
							</th>
							<td>
								<input type="text" class="w50" id="empty_first_biz_reg_no" name="empty_first_biz_reg_no" onkeydown="javascript:setOnlyNumber(this);setTextLimit('empty_first_biz_reg_no', 3);">
								<div class="t_right d_none">
									<span id="empty_first_biz_reg_no_msg"></span>
								</div>
								&nbsp;
								<em>-</em>
								&nbsp;
								<input type="text" class="w40" id="empty_second_biz_reg_no" name="empty_second_biz_reg_no" onkeydown="javascript:setOnlyNumber(this);setTextLimit('empty_second_biz_reg_no', 2);">
								<div class="t_right d_none">
									<span id="empty_second_biz_reg_no_msg">2/2</span>
								</div>
								&nbsp;
								<em>-</em>
								&nbsp;
								<input type="text" class="w80" id="empty_third_biz_reg_no" name="empty_third_biz_reg_no" onkeydown="javascript:setOnlyNumber(this);setTextLimit('empty_third_biz_reg_no', 5);">
								<div class="t_right d_none">
									<span id="empty_third_biz_reg_no_msg">5/5</span>
								</div>
								<input type="hidden" class="w200" id="biz_reg_no" name="biz_reg_no">
								<a href="#none" class="tBtn m_l10" onclick="dupVndNo();">중복확인</a>
								<input type="hidden" id="empty_biz_reg_no_dup_chk" name="empty_biz_reg_no_dup_chk">
								<input type="hidden" id="empty_biz_reg_no_ins_chk" name="empty_biz_reg_no_ins_chk">
								<input type="hidden" id="empty_vendor_info_id_dup" name="empty_vendor_info_id_dup">
							</td>
						</tr>

						<tr id="tab2View1" >
							<th>
								<span class="req">법인번호</span>
							</th>
							<td>
								<input type="text" class="w80" id="empty_first_corp_reg_no" name="empty_first_corp_reg_no" onkeydown="javascript:setOnlyNumber(this);setTextLimit('empty_first_corp_reg_no', 6);">
								&nbsp;
								<em>-</em>
								&nbsp;
								<input type="text" class="w100" id="empty_second_corp_reg_no" name="empty_second_corp_reg_no" onkeydown="javascript:setOnlyNumber(this);setTextLimit('empty_second_corp_reg_no', 7);">
								<input type="hidden" class="w200" id="empty_corp_reg_no" name="empty_corp_reg_no">
							</td>
						</tr>
					</tbody>
				</table>
				<div class="registerBtnArea">
					<a class="btnTop" id="btnNextTab2" href="#none">다음</a>
					<input type="hidden" id="tabCheckValue2" name="tabCheckValue2" value="F">
				</div>
			</div>

			<!-- 기본정보 -->
			<div class="tabCont2" id="tab3" style="display: none;">
				<div class="sub_tit m_t0">
					<h3>
						기본정보
						<em class="reqinfo">필수입력</em>
					</h3>
				</div>
				<form id="tab3Form" name="tab3Form">
					<table class="tableStyle">
						<colgroup>
							<col style="with:16%;">
							<col style="with:*;">
							<col style="with:16%;">
							<col style="with:*;">
						</colgroup>
						<tbody>
							<tr>
								<th>사업자등록번호</th>
								<td id="empty_biz_reg_no_text"></td>
								<th>사업자명</th>
								<td id="empty_biz_reg_nm_text"></td>
							</tr>

							<tr id="tab3View1">
								<th>
									<span class="req">법인등록번호</span>
								</th>
								<td colspan="3" id="empty_corp_reg_no_text">-</td>
							</tr>

							<tr>
								<th>
									<span class="req">대표자성명</span>
								</th>
								<td>
									<input type="text" class="w100 biz" id="rep_nm_loc" name="rep_nm_loc">
								</td>
								<th>
									<span class="req">기업규모</span>
								</th>
								<td>
									<select id="vd_size" name="vd_size" class="w148 biz vd_size">
										<option value="A">대기업</option>
										<option value="B">중소기업</option>
									</select>
								</td>
							</tr>

							<tr>
								<th>
									<span class="req">대표 전화번호</span>
								</th>
								<td>
									<input type="text" class="w50 biz" id="phone_no1" name="phone_no1" onkeydown="javascript:setOnlyNumber(this);" maxlength="4">
									&nbsp;
									<em>-</em>
									&nbsp;
									<input type="text" class="w50 biz" id="phone_no2" name="phone_no2" onkeydown="javascript:setOnlyNumber(this);" maxlength="4">
									&nbsp;
									<em>-</em>
									&nbsp;
									<input type="text" class="w50 biz" id="phone_no3" name="phone_no3" onkeydown="javascript:setOnlyNumber(this);" maxlength="4">
									<input type="hidden" id="phone_no" name="phone_no">
								</td>
								<th>대표 팩스번호</th>
								<td>
									<input type="text" class="w50 biz" id="fax_no1" name="fax_no1" onkeydown="javascript:setOnlyNumber(this);" maxlength="4">
									&nbsp;
									<em>-</em>
									&nbsp;
									<input type="text" class="w50 biz" id="fax_no2" name="fax_no2" onkeydown="javascript:setOnlyNumber(this);" maxlength="4">
									&nbsp;
									<em>-</em>
									&nbsp;
									<input type="text" class="w50 biz" id="fax_no3" name="fax_no3" onkeydown="javascript:setOnlyNumber(this);" maxlength="4">
									<input type="hidden" id="fax_no" name="fax_no">

								</td>
							</tr>

							<tr>
								<th>
									<span class="req">주소</span>
								</th>
								<td>
									<input type="text" class="wp100 biz" id="dtl_addr_1_loc" name="dtl_addr_1_loc" onkeydown="javascript:setTextLimit('dtl_addr_1_loc', 100);">
								</td>
								<th>
									<span class="req">상세주소</span>
								</th>
								<td>
									<input type="text" class="wp100 biz" id="dtl_addr_2_loc" name="dtl_addr_2_loc" onkeydown="javascript:setTextLimit('dtl_addr_2_loc', 100);">
								</td>
							</tr>

							<tr>
								<th>
									<span class="req">업태</span>
								</th>
								<td>
									<input type="text" class="wp100 biz" id="bos" name="bos" onkeydown="javascript:setTextLimit('bos', 100);">
								</td>
								<th>
									<span class="req">업종</span>
								</th>
								<td>
									<input type="text" class="wp100 biz" id="tob" name="tob" onkeydown="javascript:setTextLimit('tob', 100);">
								</td>
							</tr>

							<tr>
								<th>주요취급품목</th>
								<td colspan="3">
									<input type="text" class="wp100 biz" id="main_trad_item" name="main_trad_item" onkeydown="javascript:setTextLimit('main_trad_item', 100);">
								</td>
							</tr>
						</tbody>
					</table>
				</form>

				<div class="sub_tit" id="bizTypeS" style="display: none;">
					<h3>
						대표 사용자 정보
					<em class="reqinfo">필수입력</em>
					</h3>
				</div>
				<div class="sub_tit" id="bizTypeI" style="display: none;">
					<h3>
						일반 사용자 정보
						<em class="reqinfo">필수입력</em>
					</h3>
				</div>
				<input type="hidden" id="biz_id_type" name="biz_id_type">
				<table class="tableStyle">
					<colgroup>
						<col style="width:16%;">
						<col style="width:*;">
						<col style="width:16%;">
						<col style="width:*;">
					</colgroup>

					<tbody>
						<tr>
							<th>
								<span class="req">ID</span>
							</th>
							<td colspan="3">
								<input type="text" class="w110" id="charger_id" name="charger_id" onblur="fnToUpper(this);" onkeyup="noSpaceForm(this);" onchange="noSpaceForm(this);" onkeydown="javascript:setTextLimit('charger_id', 20);">
								<a href="javascript:dupId();" class="tBtn m_l10">중복확인</a>
								<input type="hidden" id="charger_id_dup_chk" name="charger_id_dup_chk">
								<input type="hidden" id="charger_id_ins_chk" name="charger_id_ins_chk">
							</td>
						</tr>

						<tr>
							<th>
								<span class="req">비밀번호</span>
							</th>
							<td colspan="3">
								<input type="password" class="w110" id="pw" name="pw" onkeyup="noSpaceForm(this);" onchange="noSpaceForm(this);" maxlength="20">
								※ 10~20자리, 영문/숫자/특수문자 혼용, 3자 이상 연속 영문/숫자/특수문자 조합 불가, 3자 이상 동일한 영문/숫자/특수문자 조합 불가
							</td>
						</tr>
						<tr>
							<th>
								<span class="req">비밀번호 확인</span>
							</th>
							<td colspan="3">
								<input type="password" class="w110" maxlength="20" id="pw_confirm" name="pw_confirm">
							</td>
						</tr>

						<tr>
							<th>
								<span class="req">담당자명</span>
							</th>
							<td>
								<input type="text" class="w100" id="chr_usr_nm" name="chr_usr_nm" onkeyup="noSpaceForm(this);" onchange="noSpaceForm(this);" onkeydown="javascript:setTextLimit('chr_usr_nm', 50);">
							</td>
							<th>
								<span class="req">이메일</span>
							</th>
							<td>
								<input type="text" class="w100" id="email" name="email" onkeyup="noSpaceForm(this);" onchange="noSpaceForm(this);" onkeydown="javascript:setTextLimit('email', 50);">
							</td>
						</tr>

						<tr>
							<th>
								<span class="req">전화번호</span>
							</th>
							<td>
								<input type="text" class="w50" id="chr_phone_no1" name="chr_phone_no1" onkeydown="javascript:setOnlyNumber(this);" maxlength="4">
								&nbsp;
								<em>-</em>
								&nbsp;
								<input type="text" class="w50" id="chr_phone_no2" name="chr_phone_no2" onkeydown="javascript:setOnlyNumber(this);" maxlength="4">
								&nbsp;
								<em>-</em>
								&nbsp;
								<input type="text" class="w50" id="chr_phone_no3" name="chr_phone_no3" onkeydown="javascript:setOnlyNumber(this);" maxlength="4">
								<input type="hidden" id="chr_phone_no" name="chr_phone_no">
							</td>
							<th>
								<span class="req">핸드폰</span>
							</th>
							<td>
								<input type="text" class="w50" id="chr_mobile_no1" name="chr_mobile_no1" onkeydown="javascript:setOnlyNumber(this);" maxlength="4">
								&nbsp;
								<em>-</em>
								&nbsp;
								<input type="text" class="w50" id="chr_mobile_no2" name="chr_mobile_no2" onkeydown="javascript:setOnlyNumber(this);" maxlength="4">
								&nbsp;
								<em>-</em>
								&nbsp;
								<input type="text" class="w50" id="chr_mobile_no3" name="chr_mobile_no3" onkeydown="javascript:setOnlyNumber(this);" maxlength="4">
								<input type="hidden" id="chr_mobile_no" name="chr_mobile_no">
							</td>
						</tr>
					</tbody>
				</table>

				<div class="registerBtnArea" id="etcSave">
					<a href="#none" class="btnTop" id="btnSaveI">등록</a>
					<input type="hidden" id="tabCheckValue3" name="tabCheckValue3" value="">
				</div>
			</div>
		</div>
	</div>
</body>
</html>
