//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//기능 일반 //////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
var arrResizeGrids = "";

jQuery.ajaxSetup({cache:false});

// CONSOLE 오류방지
var console = window.console || {
    log: function() {
    }
};

$( window ).resize(function() {
	for (var i=0;i<arrResizeGrids.length;i++) {
		jQuery("#"+arrResizeGrids[i]).jqGrid( 'setGridWidth', jQuery("#"+arrResizeGrids[i]).closest('.jqGridArea').width() );
		//jQuery("#gridItem").jqGrid( 'setGridWidth', jQuery("#gridItem").parents('div').width() );
	}
});


$("document").on("click",".datepicker", function() {  
	// 추가선택자를 넣고 동적이벤트 바인딩을 할경우 선택자는 document로 지정한다.
	this.datepicker(optionsDatepicker);
});

/**
 * browser detect
 */
var browser = (function() {
    var s = navigator.userAgent.toLowerCase();
    var match = /(webkit)[ \/](\w.]+)/.exec(s) ||
        /(opera)(?:.*version)?[ \/](\w.]+)/.exec(s) ||
        /(msie) ([\w.]+)/.exec(s) ||
        /(edge)[ \/]([\w.]+)/.exec(s) ||
        /(chrome)[\/]([\w.]+)/.exec(s) ||
        !/compatible/.test(s) &&
        /(mozilla)(?:.*? rv:([\w.]+))?/.exec(s) ||
        [];
    return { name: match[1] || '', version: match[2] || '0' };
}());

/**
 * contextPath 구하기 (http://localhost:8080/test/request.do - "/test")
 */
var getContextPath = function() {
    var path = "";
    try {
        var hostIndex = location.href.indexOf(location.host) + location.host.length;
        path = location.href.substring(hostIndex, location.href.indexOf('/', hostIndex + 1));
        
        if (path != "/web") {
        	path = "";
        }      
    } catch (e) {
    }
    
    return path;
};

/**
 * 폼데이터를 객체화 (jqGrid의 postData 옵션에 사용)
 */
jQuery.fn.serializeObject = function() {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};




//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Design 팀 적용 스크립트 ////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
var optionsDatepicker = {
		showOn: "both", // 버튼과 텍스트 필드 모두 캘린더를 보여준다.
//		buttonImage: getContextPath()+"/assets/images/eval_icon_calender.png", // 버튼 이미지.
		dateFormat: "yy/mm/dd", // 텍스트 필드에 입력되는 날짜 형식.
		changeYear: true,
		changeMonth: true ,
		nextText: '다음 달', // next 아이콘의 툴팁.
		prevText: '이전 달', // prev 아이콘의 툴팁.
		monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
		monthNamesShort: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
		dayNames: ['일','월','화','수','목','금','토'],
		dayNamesShort: ['일','월','화','수','목','금','토'],
		dayNamesMin: ['일','월','화','수','목','금','토'],
		showButtonPanel: true,
		showMonthAfterYear: true,
		isRTL : false,
		showOtherMonths: false,
		currentText: '오늘' , // 오늘 날짜로 이동하는 버튼 패널
		closeText: '닫기',  // 닫기 버튼 패널
		onClose: function(dateText, inst) {
			
			/**
			 * ※ 날짜입력 유효성 체크
			 *  - 달력이용 및 수동입력시 날짜형식이 올바르게 입력되었는지 체크
			 *  - [선택체크] 시작일 / 종료일 체크가 자동으로 이루어지도록 설정가능 dateHolder 속성설정 bgn/end 로 지정
			 *    시작일이 종료일보다 크게 입력되면 종료일에 맞춰 짐
			 *    종료일이 시작일보다 작게 입력되면 시작일에 맞춰 짐
			 *    입력 예 ) <input type="text" name="TEST_BGN" class="datepicker w75 t_center" value="" dateHolder="bgn">
			 *             <input type="text" name="TEST_END" class="datepicker w75 t_center" value="" dateHolder="end">
			 */
			
			if ($.trim(dateText) == "") {
				$(this).val("");
				return;
			}
			
			var poshyOption = {
				content: "",
				className: "tip-yellow",
				alignTo: "target",
				alignX: 'inner-left',
				showOn: "none", // hover, focus
				offsetY: 5
			}
		
			//dateText 입력된 날짜 - 수동으로 입력된 날짜도 포함
			dateText = dateText.split("/").join("");
			
			if(isValidDate(dateText)) {
				
				//'-'를 제외한 8자리 올바르게 입력시 '/'삽입
				$(this).val(dateText.substr(0, 4)+"/"+dateText.substr(4, 2)+"/"+dateText.substr(6));
				
				//dateHolder 속성이 있다면 시작일-종료일 체크
				var dateHolder = $(this).attr("dateHolder");
				if (dateHolder != undefined) {
					
					if (dateHolder == "bgn") { 
						//선택한 날짜가 시작일일시
						var bgnDate = $(this).val();
						var endDate = $(this).nextAll(".datepicker:first").val();
						
						if ($.trim(endDate) != "") {
							bgnDate = bgnDate.split("/").join("");
							endDate = endDate.split("/").join("");
							
							if (bgnDate > endDate) {
								//시작일이 종료일보다 후의 날짜일시
								
								poshyOption.content = "시작일이 종료일보다 이후의 날짜로 지정되어 자동맞춤되었습니다.";
								$(this).poshytip(poshyOption);
								$(this).poshytip('show').poshytip('hideDelayed', 4000);
								$(this).val(endDate.substr(0, 4)+"/"+endDate.substr(4, 2)+"/"+endDate.substr(6));
							} 
						}
						
					} else if (dateHolder == "end") {
						//선택한 날짜가 종료일일시
						var bgnDate = $(this).prevAll(".datepicker:first").val();
						var endDate = $(this).val();
						
						if ($.trim(bgnDate) != "") {
							bgnDate = bgnDate.split("/").join("");
							endDate = endDate.split("/").join("");
							
							if (endDate < bgnDate) {
								//종료일이 시작일보다 작을시
								
								poshyOption.content = "종료일이 시작일보다 이전의 날짜로 지정되어 자동맞춤되었습니다.";
								$(this).poshytip(poshyOption);
								$(this).poshytip('show').poshytip('hideDelayed', 4000);
								$(this).val(bgnDate.substr(0, 4)+"/"+bgnDate.substr(4, 2)+"/"+bgnDate.substr(6));
							} 
						}
						
					}
				}
				
			} else {
				poshyOption.content = "날짜 형식이 올바르지 않습니다.<br/>입력 예) 2010/01/01";
				$(this).poshytip(poshyOption);
				$(this).poshytip('show').poshytip('hideDelayed', 4000);
				$(this).val("");
				$(this).select();
			}
		}
	};

$(function () {		

	// 달력 UI
	$(".datepicker").datepicker(optionsDatepicker);	

	// LNB메뉴
	$('#lnbMenu li.active').addClass('open').children('ul').show();
	$('#lnbMenu li.has-sub>a').on('click', function(){
		$(this).removeAttr('href');
		var element = $(this).parent('li');
		if (element.hasClass('open')) {
			element.removeClass('open');
			element.find('li').removeClass('open');
			element.find('ul').slideUp(200);
		}
		else {
			element.addClass('open');
			element.children('ul').slideDown(200);
			element.siblings('li').children('ul').slideUp(200);
			element.siblings('li').removeClass('open');
			element.siblings('li').find('li').removeClass('open');
			element.siblings('li').find('ul').slideUp(200);
		}
	});

	//텝메뉴
	$(".tabCont2").hide();
	$(".tabCont2:first").show();	
	
	$("ul.tabDesign li").click(function () {
		$("ul.tabDesign li").removeClass("on");
		$(this).addClass("on");
		$(".tabCont2").hide();
		var activeTab = $(this).attr("rel");
		$("#" + activeTab).fadeIn();
	});	

	//상단이동
	$(window).scroll(function(){
		if($(this).scrollTop() > 0){
			$('.btnTops').fadeIn();
		}else{
			$('.btnTops').fadeOut();
		}
	});
	
	//전체메뉴
	$(".siteMapBtn").click(function () {
		$(".siteMapArea").slideToggle("fast");
	});	
	$(".siteMapClose").click(function () {
		$(".siteMapArea").slideToggle("fast");
	});	
	
	$('.btnTops').click(function () {
		$('html, body').animate({scrollTop: 0}, 450);
		return false;
	});
	
	/*	//레이어팝업 colse 중복으로 인해 주석처리함.
  	$(".close").bind("click",function(e){
		if (confirm("창을 닫으시겠습니까?")) {
			self.close();
		}
	});*/
	
	$(".deleteicon").bind("click",function(e){
		$.each($(this.parentElement).find("input"),function(key, value){
			this.value = "";
		});
	});
});


//layer popup
function layer_open(el) {
	var temp = $('#' + el);
	var bg = temp.parents('bg');
	if (bg) {
		$('.layer').fadeIn();
	} else {
		temp.fadeIn();
	}

	temp.css('display', 'block');
	if (temp.outerHeight() < $(document).height()) temp.css('margin-top', '-' + temp.outerHeight() / 2 + 'px');
	else temp.css('top', '0px');
	if (temp.outerWidth() < $(document).width()) temp.css('margin-left', '-' + temp.outerWidth() / 2 + 'px');
	else temp.css('left', '0px');

	temp.find('.layerClose').click(function (e) {
		if (bg) {
			$('.layer').fadeOut();
		} else {
			temp.fadeOut();
		}
		e.preventDefault();
	});

	$('.layer .bg').click(function (e) {
		$('.layer').fadeOut();
		e.preventDefault();
	});
};
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//기능 문자열 함수 ////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
 * replaceAll
 */
String.prototype.replaceAll = function(oldStr, newStr) {
    if(!isNullValue(this)) {
        return this.split(oldStr).join(newStr);
    } else {
        return this;
    }
};

/*
 * 문자열의 길이가 n이 될때까지 왼쪽에 문자열 c를 채움
 * ex) alert('123'.lpad(5, '0')); -> 00123
 */
String.prototype.lPad = function (n,c) {
    var i; var a = this.split(''); for (i = 0; i < n - this.length; i++) {a.unshift (c);}; return a.join('');
};

/*
 * 문자열의 길이가 n이 될때까지 오른쪽에 문자열 c를 채움
 */
String.prototype.rPad = function (n,c) {
    var i; var a = this.split(''); for (i = 0; i < n - this.length; i++) {a.push (c);}; return a.join('');
};

/**
 * null 체크
 *
 * @param event
 */
var isNullValue = function(inputValue) {
    if (inputValue == null || inputValue.length == 0 || inputValue == "") {
        return true;
    } else {
        return false;
    }
};

/**
 * 입력받은 값에서 양쪽 공백 지워주기
 *
 * @param event
 */
var trimValue = function(inputValue) {
    var sLeftTrimed = inputValue.replace(/^\s+/, "");
    var sBothTrimed = sLeftTrimed.replace(/\s+$/, "");
    return (sBothTrimed);
};





/**
 * String바이트 수 구하기
 *
 * @param szValue
 * @returns {Number}
 */
var calculateBytes = function(szValue, maxSize) {
    var tcount = 0;
    var tmpStr = new String(szValue);
    var temp = tmpStr.length;

    var onechar;

    if ("undefined"==maxSize) {
        for (var k = 0; k < temp; k++) {
            onechar = tmpStr.charAt(k);
            if (escape(onechar).length > 4) {
                tcount += 3;
            } else {
                tcount += 1;
            }
        }

        return tcount;
    } else {
        var rtnVal = new Array();

        rtnVal[0] = 0;
        rtnVal[1] = "";
        for (var k=0; k<temp; k++ ){
            onechar = tmpStr.charAt(k);

            if (escape(onechar).length > 4){
                tcount += 3;
            }else{
                tcount += 1;
            }

            if (tcount<=maxSize) {
                rtnVal[1] += onechar;
            }
        }

        rtnVal[0]= tcount;
        return rtnVal;
    }
};



//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//기능 날짜 관련 함수 ////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 날짜 차이 계산 함수
 *
 * @param 필드아이디1
 *            stndDateId : 기준 날짜(YYYY-MM-DD)
 * @param 필드아이디2
 *            targetDateId : 대상 날짜(YYYY-MM-DD)
 * @param 결과반환필드
 *            resultId
 * @returns
 */
var getDateDiff = function(stndDateId, targetDateId, resultId) {

    var date1 = $("#" + stndDateId).val();
    var date2 = $("#" + targetDateId).val();

    if (date1 == "" || date2 == "") { return; }

    var arrDate1 = date1.split("/");
    var getDate1 = new Date(parseInt(arrDate1[0]), parseInt(arrDate1[1]) - 1, parseInt(arrDate1[2]));
    var arrDate2 = date2.split("/");
    var getDate2 = new Date(parseInt(arrDate2[0]), parseInt(arrDate2[1]) - 1, parseInt(arrDate2[2]));

    var getDiffTime = getDate1.getTime() - getDate2.getTime();

    $("#" + resultId).val(Math.floor(getDiffTime / (1000 * 60 * 60 * 24)));
};


var getDateDiff2 = function(fromDate, toDate) {
    if (fromDate == "" || toDate == "") { return; }

    var arrDate1 = toDate.split("/");
    var getDate1 = new Date(parseInt(arrDate1[0]), parseInt(arrDate1[1]) - 1, parseInt(arrDate1[2]));
    var arrDate2 = fromDate.split("/");
    var getDate2 = new Date(parseInt(arrDate2[0]), parseInt(arrDate2[1]) - 1, parseInt(arrDate2[2]));

    var getDiffTime = getDate1.getTime() - getDate2.getTime();

    return Math.floor(getDiffTime / (1000 * 60 * 60 * 24));
};


/**
 * 날짜 차이 계산 함수
 *
 * @param 날짜1
 *            srcDateStr : 기준 날짜(YYYY-MM-DD)
 * @param 날짜2
 *            targetDateStr : 대상 날짜(YYYY-MM-DD)
 * @param 결과반환
 * @returns
 */
var getStrDateDiff = function(srcDateStr, targetDateStr) {	
	
	if (srcDateStr == "" || targetDateStr == "") { return; }
	
	var srcDate = new Date(srcDateStr.substring(0,4), srcDateStr.substring(4,6), srcDateStr.substring(6,8), srcDateStr.substring(8,10), srcDateStr.substring(10,12), srcDateStr.substring(12,14));
	
	var targetDate = new Date(targetDateStr.substring(0,4), targetDateStr.substring(4,6), targetDateStr.substring(6,8), targetDateStr.substring(8,10), targetDateStr.substring(10,12), targetDateStr.substring(12,14));
	
	
    var getDiffTime = srcDate.getTime() - targetDate.getTime();

    return Math.floor(getDiffTime / (60 * 60 * 24));
};


/**
 * 날짜 포맷 맞추기
 */
var getDateFormatData = function(flag, cnt, delimiter) {
	if (delimiter == undefined) delimiter = "/";
	
	var dateObj = new Date();
	
	var y1 = dateObj.getFullYear();
	var m1 = dateObj.getMonth();
	var d1 = dateObj.getDate();
	
	if (flag == "y") {
		dateObj.setYear(y1 + cnt);
		
	} else if (flag == "m") {
		dateObj.setMonth(m1 + cnt);
		
	} else if (flag = "d") {
		dateObj.setDate(d1 + cnt);
	}
	y1 = dateObj.getFullYear();
	m1 = dateObj.getMonth() + 1;
	d1 = dateObj.getDate();
	
	return y1 + delimiter + this.formatLen(m1) + delimiter + this.formatLen(d1);
};

/**
 * 날짜 길이 맞추기
 */
var formatLen = function(str) {
	return str = (""+str).length<2 ? "0"+str : str;
}

/**
 * 오늘 날짜
 */
var getToDay = function(delimiter) {
	return getDateFormatData("today", 0, delimiter);
};

/**
 * 계산된 날짜 
 * @param flag 날짜계산구분(y/m/d 중 입력)
 * @param cnt 계산되어야할날짜 (예: 10)
 * @param delimiter
 * 
 * 사용예시 : getDiffDay("m", -1) >>> 한달전 날짜를 조회 
 * 
 */
var getDiffDay = function(flag, cnt, delimiter) {
	return getDateFormatData(flag, cnt, delimiter);
}


/**
 * 입력한 날짜의 달의 첫째일을 리턴
 */
var firstDayByMonth = function(toDate, delimiter) {
	if (delimiter == undefined) delimiter = "/";
	
	var arrDate1 = toDate.split("/");
	var dateObj = new Date();
	dateObj.setFullYear(parseInt(arrDate1[0]));
	dateObj.setMonth(parseInt(arrDate1[1]) - 1);
	dateObj.setDate(1);
	
	return dateObj.getFullYear() + delimiter + this.formatLen(dateObj.getMonth()+1) + delimiter + this.formatLen(dateObj.getDate());
}


/**
 * 날짜포맷에 맞는지 검사
 */
function isDateFormat(d) {
	if (d.length != 8) {
		return false;
	}
	var df = /^(19[7-9][0-9]|20\d{2})(0[0-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])$/;
	return d.match(df);
}

/**
 * 윤년여부 검사
 */
function isLeaf(year) {
	var leaf = false;

	if(year % 4 == 0) {
		leaf = true;

		if(year % 100 == 0) {
			leaf = false;
		}

		if(year % 400 == 0) {
			leaf = true;
		}
	}
	return leaf;
}


/**
 * 날짜 유효성 검사 
 * @param d
 * @returns {Boolean}
 */
function isValidDate(d) {
	if(!isDateFormat(d)) {
		return false;
	}

	var month_day = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

	var year = Number(d.substr(0, 4));
	var month = Number(d.substr(4, 2));
	var day = Number(d.substr(6));

	// 날짜가 0이면 false
	if(day == 0) {
		return false;
	}

	var isValid = false;

	// 윤년일때
	if(isLeaf(year)) {
		if(month == 2) {
			if(day <= month_day[month-1] + 1) {
				isValid = true;
			}
		} else {
			if(day <= month_day[month-1]) {
				isValid = true;
			}
		}
	} else {
		if(day <= month_day[month-1]) {
			isValid = true;
		}
	}
	return isValid;
}



//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//기능 포멧 관련 함수 ////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
* 콤마제거
*
* @param data
*/
var removeComma = function(data) {
	try { 
		if (null==data) return '';
		return data.toString().replaceAll(/,/g, "");} 
	catch(e) {
		console.log("removeComma error %s %o",data,e);
		return "";}

};

/**
* 콤마 추가
*
* @param data
*/
var addComma = function(data) {
  var rtnVal = "";
  if( isNotEmpty(data)) {
	  data = removeComma(data);  
	  var arrNum = data.toString().split('.');
	    if (arrNum.length >= 2) {
	        rtnVal = arrNum[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",") + "." + arrNum[1];
	    } else {
	        rtnVal = arrNum[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	    }
  }
  return rtnVal;
};

var removeNAddComma = function (data) {
	return addComma(removeComma(data));
}

var removeNonNumber = function(str) {
	str = $.trim(str);
	str = str.replace(/[^0-9]/g,'');
	
	return str;
};

var setCellNumberFormat = function(num1) {
	num1 = removeNonNumber(num1);
	if (num1.length == 10) {
		num1 = num1.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3');
		
	} else if (num1.length == 11) {
		num1 = num1.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
	}
	return num1;
};

/**
 * 전화번호 검증 함수 
 * 휴대전화 / 일반전화 포함 
 */
var checkTelNumber = function(number) {
	var regExp = /^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})-?[0-9]{3,4}-?[0-9]{4}$/;
	
	if (isEmpty(number)) {
		return false;
		
	} else if (!regExp.test(number)) {
		return false;
	}
	return true;
};

/**
* 사업자등록번호 형식 지정
*
* @param cellvalue -
*            Cell 값
* @param options -
*            Cell Option 정보
* @param rowObject -
*            Row Obejct
*/
var bunsinessNoFormat = function(cellvalue, options, rowObject) {

  if (cellvalue.length == 10) { return cellvalue.substring(0, 3) + "-" + cellvalue.substring(3, 5) + "-" + cellvalue.substring(5); }
  return cellvalue;

};

/**
* @Description : 사업자등록번호 체크 
*
* @return : true, false
*/
var checkBizID = function(bizID) { 
  // bizID는 숫자만 10자리로 해서 문자열로 넘긴다. 
  var checkID = new Array(1, 3, 7, 1, 3, 7, 1, 3, 5, 1); 
  var tmpBizID, i, chkSum=0, c2, remander; 
  bizID = bizID.replace(/-/gi,''); 
  for (i=0; i<=7; i++) chkSum += checkID[i] * bizID.charAt(i); 
  c2 = "0" + (checkID[8] * bizID.charAt(8)); 
  c2 = c2.substring(c2.length - 2, c2.length); 
  chkSum += Math.floor(c2.charAt(0)) + Math.floor(c2.charAt(1)); 
  remander = (10 - (chkSum % 10)) % 10 ; 
  if (Math.floor(bizID.charAt(9)) == remander) return true ; // OK! 
  return false; 
};

/**
* 날짜 포멧 변경
*
* @param date
*/
var formatDate = function(txtDate) {
  // 공백인 경우는 정상으로 처리
  if (txtDate != "") {
      if (!isDate(txtDate)) {
          alert("날짜 형식이 맞지 않습니다.");
          return txtDate;
      }
      return txtDate.substring(0, 4) + "/" + txtDate.substring(4, 6) + "/" + txtDate.substring(6, 8);
  } else {
      return txtDate;
  }
};


/**
* 날짜 포멧 변경
*
* @param date
*/
var removeDashFromDateText = function(txtDate) {
  // 공백인 경우는 정상으로 처리
  if (isEmpty( txtDate) ) {
      return "";
  } else {
      return txtDate.replace(/-/g, "");
  } 
};


/**
* 날짜 포멧 변경
*
* @param date
*/
var unFormatDate = function(strFormatDate) {
  // 공백인 경우는 정상으로 처리
  if (strFormatDate != "") {
      if (!isDate(strFormatDate)) {
          alert("날짜 형식이 맞지 않습니다.");
          return txtDate;
      }

      return strFormatDate.replace(/-/g, "");
  } else {
      return strFormatDate;
  }
};

/**
 * Date를 String으로 변경
 * 
 * @param date
 * @param type [DATE:YYYYMMDD | TIME:HH24MISS | DATETIME:YYYYMMDDHH24MISS | default(DATETIME):YYYYMMDDHH24MISS]
 */
var fn_convertDateToString = function(date, type) {

	var strDt = null;
	
	if(type == 'DATE') {
		strDt = fn_paddingZero(date.getFullYear(), 4) + fn_paddingZero(date.getMonth() + 1, 2) + fn_paddingZero(date.getDate(), 2)
	} else if(type == 'TIME') {
		strDt = fn_paddingZero(date.getHours(), 2) + fn_paddingZero(date.getMinutes(), 2) + fn_paddingZero(date.getSeconds(), 2);
	} else {
		strDt = fn_paddingZero(date.getFullYear(), 4) + fn_paddingZero(date.getMonth() + 1, 2) + fn_paddingZero(date.getDate(), 2) + fn_paddingZero(date.getHours(), 2) + fn_paddingZero(date.getMinutes(), 2) + fn_paddingZero(date.getSeconds(), 2);
	}

    return strDt;
}

/**
 * 현재시간을 YYYYMMDDHH24MISS형태로 가져옴
 *
 * @returns
 */
var fn_getCurrentTime = function() {
    var toDay = new Date();

    var formatDate = fn_paddingZero(toDay.getFullYear(), 4) + fn_paddingZero(toDay.getMonth() + 1, 2) + fn_paddingZero(toDay.getDate(), 2) + fn_paddingZero(toDay.getHours(), 2) + fn_paddingZero(toDay.getMinutes(), 2) + fn_paddingZero(toDay.getSeconds(), 2);
    return formatDate;
};

/**
 * 현재시간을 YYYYMMDD형태로 가져옴
 *
 * @returns
 */
var fn_getCurrentDate = function() {
    var toDay = new Date();

    var formatDate = fn_paddingZero(toDay.getFullYear(), 4) + fn_paddingZero(toDay.getMonth() + 1, 2) + fn_paddingZero(toDay.getDate(), 2);
    return formatDate;
};

/**
 * 0값으로 자리수 메꾸기
 *
 * @param val
 * @param digits
 * @returns {String}
 */
var fn_paddingZero = function(val, digits) {
    var zeroVal = '';
    val = val.toString();

    if (val.length < digits) {
        for (var i = 0; i < digits - val.length; i++)
            zeroVal += '0';
    }
    return zeroVal + val;
};


/**
 * 숫자를 한글로 변환
 *
 * @param num
 * @returns hanStr
 */
var MoneyToHan = function(num) {
    arrayNum = new Array("", "일", "이", "삼", "사", "오", "육", "칠", "팔", "구");
    arrayUnit = new Array("", "십", "백", "천", "만", "십만", "백만", "천만", "억", "십억", "백억", "천억", "조", "십조", "백조");
    arrayStr = new Array()
    len = num.length;
    hanStr = "";
    for (i = 0; i < len; i++) {
        arrayStr[i] = num.substr(i, 1)
    }
    code = len;
    for (i = 0; i < len; i++) {
        code--;
        tmpUnit = "";
        if (arrayNum[arrayStr[i]] != "") {
            tmpUnit = arrayUnit[code];
            if (code > 4) {
                if ((Math.floor(code / 4) == Math.floor((code - 1) / 4) && arrayNum[arrayStr[i + 1]] != "") || (Math.floor(code / 4) == Math.floor((code - 2) / 4) && arrayNum[arrayStr[i + 2]] != "")) {
                    tmpUnit = arrayUnit[code].substr(0, 1);
                }
            }
        }
        hanStr += arrayNum[arrayStr[i]] + tmpUnit;
    }
    return hanStr;
};

/**
 * 전화번호, 폰번호 입력 포멧 맞춤
 *
 * @param num ,type
 * @returns formatNum
 */
var PhoneFomatter = function (num,type){
    var formatNum = '';
    
    if(num.length==11){
        if(type==0){
            formatNum = num.replace(/(\d{3})(\d{4})(\d{4})/, '$1-****-$3');
        }else{
            formatNum = num.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
        }
    }else if(num.length==8){
        formatNum = num.replace(/(\d{4})(\d{4})/, '$1-$2');
    }else{
        if(num.indexOf('02')==0){
            if(type==0){
                formatNum = num.replace(/(\d{2})(\d{4})(\d{4})/, '$1-****-$3');
            }else{
                formatNum = num.replace(/(\d{2})(\d{4})(\d{4})/, '$1-$2-$3');
            }
        }else{
            if(type==0){
                formatNum = num.replace(/(\d{3})(\d{3})(\d{4})/, '$1-***-$3');
            }else{
                formatNum = num.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3');
            }
        }
    }
    return formatNum;
}

/**
 * 이메일 형식 체크
 *
 * @param num ,type
 * @returns formatNum
 */
var emailFormatCheck = function(strValue, msgFlag)
{
	var regExp = /[0-9a-zA-Z][_0-9a-zA-Z-]*@[_0-9a-zA-Z-]+(\.[_0-9a-zA-Z-]+){1,2}$/;

	//입력을 안했으면
	if(strValue.lenght == 0){
		if (msgFlag != "N") {
			alert("이메일을 입력하셔야 합니다.");
		}
		return false;
	}

	//이메일 형식에 맞지않으면
	if (!strValue.match(regExp)){
		if (msgFlag != "N") {
			alert("이메일 형식에 맞지 않습니다.");
			$("#"+strValue).focus();
		}
		return false;
	}
	return true;
}


/**
 * 기안문서 번호 형식 체크
 * 
 * @param strValue, msgFlag
 * @return boolean 
 */
var docNoFormatchech = function(strValue, msgFlag)
{
	var regExp = /^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|\-\|]+$/;
	
	if(strValue.length == 0) {
		if (msgFlag != "N") {
			alert("기안문서 번호를 입력하셔야 합니다.");
		}
		return false;
	}
	
	if(!strValue.match(regExp)) {
		if (msgFlag != "N") {
			alert("기안문서 번호 형식에 맞지 않습니다.");
			$("#"+strValue).focus();
		}
		return false;
	}
	return true;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//기능 Ajax 통신 ////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * ajax post통신
 */
var ajaxJsonCall = function(url, param, successCallback, errorCallback) {
	var contentType;
    var data;
    var dataType;

    if (typeof param == "string") {
        contentType = "application/json;charset=UTF-8";
        data = param;
        dataType = "json"
    } else {
        //contentType = "application/x-www-form-urlencoded;charset=UTF-8";
    	contentType = "application/json;charset=UTF-8";
    	data = JSON.stringify(param);
        dataType = "json";
    }

    $.ajax({
    type : 'POST',
    url : url,
    contentType : contentType,
    data : data,
    cache: false,
    traditional : true,
    dataType : dataType,
    beforeSend:function(){
    	$('body').append('<div class="pageLoader"></div>');
    },
    complete:function(){
    	if (undefined==param.pageLoader || !param.pageLoader) $('.pageLoader').remove();
    },
    success : function(data) {
        try {
            if (data) {
                if (data["status"] == "SUCC") {
                    if (typeof successCallback !== 'undefined') {
                        successCallback(data);
                    }
                } else if (data["status"] == "FAIL"){
                    alert(data["errMsg"]);
                    if (typeof errorCallback !== 'undefined') {
                        errorCallback(data);
                    }
                    try{ $('.pageLoader').remove(); } catch(e){}
                } else {
                    if (typeof successCallback !== 'undefined') {
                        successCallback(data);
                    }
                }
            }
        } catch (e) {
            console.log(e.message);
            try{ $('.pageLoader').remove(); } catch(e){}
        }
    },
    error : function(xhr, status, error) {
        if (401 === xhr.status) {
            alert('<spring:message code="M1000015" javaScriptEscape="true"/>');
            location.href = "/";
        } else {
            alert(error);
        }
        try{ $('.pageLoader').remove(); } catch(e){}
        }
    });
};

var errorCallback = function() {
  alert("서버와의 연결이 해지되었습니다.");
};


//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//기능 Controll 제어 ////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
* input시 글자수 제한
* inputObjId : 입력박스 아이디
* msgObjId : 글자수 표현할 메시지 박스 아이디
* maxSize : 제한 글자수
*/
var setTextLimit = function (inputObjId, maxSize) {

  var hanLen 		= maxSize/3;
  var _msgObjId 	= "_"+inputObjId+"_MSG";
  hanLen = parseInt(hanLen);

  //console.log($("#"+inputObjId).attr('type'));
  
  if ("text"==$("#"+inputObjId).attr('type')) {
  	$("#"+inputObjId).after("<div class='t_right d_none'><span id='"+_msgObjId+"'></span></div>");
  } else {
  	$("#"+inputObjId).after("<div class='t_right'><span id='"+_msgObjId+"'></span></div>");
  }
  
  if (_msgObjId!=null && _msgObjId!="") {
      var rtnVal = calculateBytes($("#"+inputObjId).val(), maxSize);

      $("#"+_msgObjId).empty();
      $("#"+_msgObjId).append(rtnVal[0]+"/"+maxSize);
  }
  
  $("#"+inputObjId).on("change keyup", function() {
      var rtnVal = calculateBytes($("#"+inputObjId).val(), maxSize);
      var currSize = rtnVal[0];
      if (currSize > maxSize) {
          alert("입력 글자수가 초과되었습니다.\n영문은 " + maxSize +"자, 한글은 " + hanLen + "자 만큼 입력가능합니다.");
          $("#"+inputObjId).val(rtnVal[1]);
          currSize = maxSize;
      }
      if (_msgObjId!=null && _msgObjId!="") {
          $("#"+_msgObjId).empty();
          $("#"+_msgObjId).append(currSize+"/"+maxSize);
      }

  });
};

/**
* 숫자만 입력받게 하고 콤마를 붙였다 뗐다함. 사용법 : setCommaFormat('id')
* 소숫점 포함
*
* @param numObjId
*/
var setCommaFormat = function(numObjId, scale) {

  // $("input[id=" + numObjId + "]").attr("style","text-align:right"); // 다른
  // Style까지 모두 무시되어 아래 CSS로 변경
  $("input[id=" + numObjId + "]").css("text-align", "right");
  $("input[id=" + numObjId + "]").keyup(function(event) {
  	
  	if( isNotEmpty(scale) && 0 != scale ) {
        var val = $(this).val().match(/[^0-9.]/g);
        if (val != null) {
            $(this).val($(this).val().replace(/[^0-9.]/g, ''));
        }
  	} else {
  		var val = $(this).val().match(/[^0-9]/g);
        if (val != null) {
            $(this).val($(this).val().replace(/[^0-9]/g, ''));
        }
  	}
  });
  $("input[id=" + numObjId + "]").focus(function(event) {
      $(this).val($(this).val().replace(/,/g, ""));
  });
  $("input[id=" + numObjId + "]").blur(function() {
  	//console.log( numObjId + " blur");
  	if ($(this).val() != null) {
  		//console.log(  "value = " + $(this).val());
  		if( isNotEmpty(scale) && 0 != scale ) {
            var arrNum = $(this).val().split('.');
            if (arrNum.length >= 2) {
            	var scaleValue = arrNum[1].substring(0, scale);
                $(this).val(arrNum[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",") + "." + scaleValue);
            } else {
                $(this).val(arrNum[0].replace(/\B(?=(\d{3})+(?!\d))/g, ","));
            }
    	} else {
            var arrNum = $(this).val().split('.');
            if (arrNum.length >= 2)
                $(this).val(arrNum[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",") );
            else
                $(this).val(arrNum[0].replace(/\B(?=(\d{3})+(?!\d))/g, ","));
    	}
  	}
  });
};

/**
* 날짜를 수기로 입력하게함 ,  사용법 : setDateFormat('id')
* 입력형식 : YYYY-MM-DD , YYYYMMDD
*
* @param numObjId
*/
var setDateFormat = function(numObjId) {

  $("input[id=" + numObjId + "]").keyup(function(event) {
		var val = $(this).val().match(/[^0-9-]/g);
		if (val != null) {
		    $(this).val($(this).val().replace(/[^0-9-]/g, ''));
		}
  	
  });
  $("input[id=" + numObjId + "]").blur(function() {
  	if ($(this).val() != null) {
  		var strDate = $(this).val().replace(/-/g, "");
  		
        if (!isDate(strDate)) {
            alert("날짜 형식이 맞지 않습니다.");
            $(this).val("");
        }else{
        	$(this).val(strDate.substring(0, 4) + "/" + strDate.substring(4, 6) + "/" + strDate.substring(6, 8));
        }
  	}
  });
};

/**
* 00 ~ 23시까지만 입력하게 함,  사용법 : setHourFormat('id')
*
* @param numObjId
*/
var setHourFormat = function(numObjId) {

  $("input[id=" + numObjId + "]").css("text-align", "right");
  $("input[id=" + numObjId + "]").keyup(function(event) {
      var val = $(this).val().match(/[^0-9]/g);
      if (val != null) {
          $(this).val($(this).val().replace(/[^0-9]/g, ''));
      }
  });

  $("input[id=" + numObjId + "]").blur(function() {
  	//console.log( numObjId + " blur");
      if ($(this).val() != null) {
      	var value = Number($(this).val());
      	//console.log(  "value = " + value);
      	
      	if( value < 0 || value > 23) {
      		alert( "00 ~ 23 시 까지만 가능합니다.");
      		$(this).val("23");
      		$(this).focus();
      	} else if( value < 10 ) {
      		$(this).val("0" + value);
      	}

      }
  });
};


/**
* 00 ~ 59분 까지만 입력하게 함,  사용법 : setMinuteFormat('id')
*
* @param numObjId
*/
var setMinuteFormat = function(numObjId) {

  $("input[id=" + numObjId + "]").css("text-align", "right");
  $("input[id=" + numObjId + "]").keyup(function(event) {
      var val = $(this).val().match(/[^0-9]/g);
      if (val != null) {
          $(this).val($(this).val().replace(/[^0-9]/g, ''));
      }
  });

  $("input[id=" + numObjId + "]").blur(function() {
      if ($(this).val() != null) {
      	var value = Number($(this).val());
      	if( value < 0 ||  value > 59) {
      		alert( "00 ~ 59 분 까지만 가능합니다.");
      		$(this).val("59");
      		$(this).focus();
      	} else if( value < 10 ) {
      		$(this).val("0" + value);
      	}
      }
  });
};

/**
* 엔터키 입력 시 특정 함수 호출
*
* @param width -
*            창크기(width)
* @param height -
*            창크기(height)
*/
var checkEnter = function(functionName) {
  if (event.keyCode == 13) {
      eval(functionName);
  }
};


/**
* 스페이스바 사용 불가
* 사용법 : <input type="text" onkeyup="noSpaceForm(this);" onchange="noSpaceForm(this);">
*
* @param numObjId
*/ 
var noSpaceForm = function (obj) { // 공백사용못하게
    var str_space = /\s/;  // 공백체크
    if(str_space.exec(obj.value)) { //공백 체크
        alert("해당 항목에는 공백을 사용할수 없습니다.\n공백은 자동적으로 제거 됩니다.");
        obj.focus();
        obj.value = obj.value.replace(' ',''); // 공백제거
        return false;
    }
}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//기능 INPUT PARAMS 제어 ////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * HTML Document에서 입력 Tag들의 값을 Object에 담아 반환
 * Input, Select, TextArea tag?값을 읽어서 param에 담음
 * @param isIncludeDisabled
 *            disabled된 입력 Tag 값을 가져올지 구분, 디폴트는 가져옴
 * @returns params Objects
 */
var fnGetParams = function(isIncludeDisabled) {
    isIncludeDisabled = isIncludeDisabled || true;
    var params = {};
    $(':input').each(function() {
        if (this.id.indexOf('jqg')>-1) {
            //나중에 그리드 INPUT 받아서 처리시에 추가
        } else {
        	if (this.id != '') {
	            if (!isIncludeDisabled) {
	                if ('disabled' != $(this).attr('disabled')) {
	                    params[this.id] = $.trim($(this).val());
	                }
	            } else {
	            	if (this.type=="checkbox") {
	            		if (this.checked) params[this.id] = $(this).val();
	            		else params[this.id] = "";
	            	} else if (this.type=="radio") {
	            		if( this.checked) params[this.id] = $(this).val();
	            	} else {
	            		params[this.id] = $.trim($(this).val());		
	            	}
	            }
        	}
        }
    });
    
    return params;
};


var fnGetMakeParams = function() {
	var params = {};
    $('#_MENU_FROM_ :input').each(function() {
        if (this.id.indexOf('jqg')>-1) {
            //나중에 그리드 INPUT 받아서 처리시에 추가
        } else {
           params[this.id] = $(this).val();
        }
    });
    
    return params;
};


/**
 * HTML Document에서 특정 Form(formid)입력 Tag들의 값을 Object에 담아 반환
 */
var fnGetFormParams = function(formId, isIncludeDisabled) {
    isIncludeDisabled = isIncludeDisabled || true;
    var params = {};
    $('#'+ formId + ' :input').each(function() {
    	if (this.id != '') {
	        if (this.id.indexOf('jqg')>-1) {
	            //나중에 그리드 INPUT 받아서 처리시에 추가
	        } else {
	            if (!isIncludeDisabled) {
	                if ('disabled' != $(this).attr('disabled')) {
	                    params[this.id] = $(this).val();
	                }
	            } else {
	            	if (this.type=="checkbox") {
	            		if (this.checked) params[this.id] = $(this).val();
	            		else params[this.id] = "";
	            	} else if (this.type=="radio") {
	            		if( this.checked) params[this.id] = $(this).val();
	            	} else {
	            		params[this.id] = $(this).val();		
	            	}
	            }
	        }
    	}
    });
    
    $.extend( params, fnGetMakeParams());
    
    return params;
};

/**
 * 입력 화면을 상세 조회 화면으로 사용할때
 * input, select, textarea에 대해서 공통으로 처리
 */
var SetReadOnly = function (btnYn) {
    'use strict';
    $(':input').attr('readonly', true);
    $(':input').addClass('readOnly');
    
    var btnShowYn = false;
    
    if(btnYn == undefined){
        btnShowYn = false;
    }else{
        if(btnYn){
            btnShowYn = true;
        }else{
            btnShowYn = false;
        }
    }
    $('.gBtn, button').hide();
    $('.tBtn, button').hide();
    $('.ico').hide();
    $('.searchicon').hide();
    if(!btnShowYn){       
        $('.btnL, button').hide();
    }
    
    /*  single file upload readonly */
    $('input[id$="_uploadFile"]').hide();
    $('.file-upload').hide();
    $('input[id$="_setReadOnlyFlag"]').val('Y');
    /*  single file upload readonly */
    
    $(".datepicker").each(function() {
    	$(this).hide();
    	$(this).after($(this).val());
    });
    
    $('input:radio').each(function() {
    	$(this).hide();
    	if (!$(this).is(':checked')) {try{$(this)[0].nextSibling.nodeValue = "";}catch(e){}}
    });
    $('input:checkbox').each(function() {
    	$(this).hide();
    	if (!$(this).is(':checked')) {try{$(this)[0].nextSibling.nodeValue = "";}catch(e){}}
    });
    $('select').each(function() {
        //if (!$(this).is(':selected')) fnHide($(this));
    	$(this).hide();
    	try{
    	if (""!=$(this)[0].options[$(this)[0].selectedIndex].value) {
    		$(this).after($(this)[0].options[$(this)[0].selectedIndex].text);
    	}}catch(e){}
    });
    $('textarea').each(function() {
        $(this).css('border', 'solid 1px #c6c6c6');
    });
    
};
/**
 * 입력 화면을 상세 조회 화면으로 사용할때
 * input, select, textarea에 대해서 공통으로 처리
 */
var SetReadOnlyLine = function (btnYn) {
    'use strict';
    $(':input').attr('readonly', true);

    /*  single file upload readonly */
    $('input[id$="_uploadFile"]').hide();
    $('.file-upload').hide();
    $('input[id$="_setReadOnlyFlag"]').val('Y');
    /*  single file upload readonly */
    
    $(".datepicker").each(function() {
    	$(this).hide();
    	$(this).after($(this).val());
    });
    
    $('input:radio').each(function() {
    	$(this).hide();
    	if (!$(this).is(':checked')) {try{$(this)[0].nextSibling.nodeValue = "";}catch(e){}}
    });
    $('input:checkbox').each(function() {
    	$(this).attr("disabled","disabled");
    	//if (!$(this).is(':checked')) {try{$(this)[0].nextSibling.nodeValue = "";}catch(e){}}
    });
    $('select').each(function() {
        //if (!$(this).is(':selected')) fnHide($(this));
    	$(this).hide();
    	try{
    	if (""!=$(this)[0].options[$(this)[0].selectedIndex].value) {
    		$(this).after($(this)[0].options[$(this)[0].selectedIndex].text);
    	}}catch(e){}
    });
    $('textarea').each(function() {
        $(this).css('border', 'solid 1px #c6c6c6');
    });
};

var SetReadOnlyJqgrid = function(gridId) {
	'use strict';
    $('#'+gridId+' :input').attr('readonly', true);

    $('#'+gridId+' .datepicker').each(function() {
    	$(this).hide();
    	$(this).after($(this).val());
    });
    
	$('#'+gridId+' input:radio').each(function() {
    	$(this).hide();
    	if (!$(this).is(':checked')) {try{$(this)[0].nextSibling.nodeValue = "";}catch(e){}}
    });
    $('#'+gridId+' input:checkbox').each(function() {
    	$(this).attr("disabled","disabled");
    	//if (!$(this).is(':checked')) {try{$(this)[0].nextSibling.nodeValue = "";}catch(e){}}
    });
    $('#'+gridId+' select').each(function() {
        //if (!$(this).is(':selected')) fnHide($(this));
    	$(this).hide();
    	try{
    	if (""!=$(this)[0].options[$(this)[0].selectedIndex].value) {
    		$(this).after($(this)[0].options[$(this)[0].selectedIndex].text);
    	}}catch(e){}
    });
    $('#'+gridId+' textarea').each(function() {
        $(this).css('border', 'solid 1px #c6c6c6');
    });
    
	$("#"+gridId+" .btn").hide();
	$("#"+gridId+" .ui-datepicker-trigger").hide();
	$("#"+gridId).find('img').hide();
}

/**
 * form 의
 * input, select, textarea에 대해서 공통으로 처리
 */
var SetFormReadOnly = function (formId, btnYn) {
    'use strict';
    $('#' + formId + ' :input').attr('readonly', true);
    $('#' + formId + ' :input').addClass('readOnly');
    
    var btnShowYn = false;
    
    if(btnYn == undefined){
        btnShowYn = false;
    }else{
        if(btnYn){
            btnShowYn = true;
        }else{
            btnShowYn = false;
        }
    }
    
    $('#' + formId + ' .gBtn').hide();
    $('#' + formId + ' .tBtn').hide();
    $('#' + formId + ' :button').hide();
    $('#' + formId + ' .ico').hide();
    
    if(!btnShowYn){       
        $('#' + formId + ' .btnL').hide();
    }
    
    /*  single file upload readonly */
    $('#' + formId + ' input[id$="_uploadFile"]').hide();
    $('#' + formId + ' .file-upload').hide();
    $('#' + formId + ' input[id$="_setReadOnlyFlag"]').val('Y');
    /*  single file upload readonly */
    
    $('#' + formId + ' .datepicker').each(function() {
    	$(this).hide();
    	$(this).after($(this).val());
    });
    
    $('#' + formId + ' input:radio').each(function() {
    	$(this).hide();
		if (!$(this).is(':checked')) $(this)[0].nextSibling.nodeValue = "";
    });
    $('#' + formId + ' input:checkbox').each(function() {
    	$(this).hide();
    	if (!$(this).is(':checked')) $(this)[0].nextSibling.nodeValue = "";
    });
    $('#' + formId + ' select').each(function() {
        //if (!$(this).is(':selected')) fnHide($(this));
    	$(this).hide();
    	if (""!=$(this)[0].options[$(this)[0].selectedIndex].value) {
    		$(this).after($(this)[0].options[$(this)[0].selectedIndex].text);
    	}
    });
    $('#' + formId + ' textarea').each(function() {
        $(this).css('border', 'solid 1px #c6c6c6');
    });

};



/**
 * 특정 id의
 * input, select, textarea에 대해서 공통으로 처리
 */
var SetReadOnlyById = function (args) {
    'use strict';
    for( var i = 0, nSize = args.length; i < nSize; i++) {

    	var obj = $('#' + args[i]); 
    	/*
    	console.log( "args[i] = " + args[i]);
    	console.log( "obj attr type = " + obj.attr("type"));
    	console.log( "obj hasClass = " + obj.hasClass('datepicker') );
    	console.log( "obj is select= " + obj.is("select") );
    	console.log( "obj is textarea= " + obj.is("textarea") );
    	console.log( "obj.next().text()=" + obj.next().is("button") );
    	*/
    	
    	obj.attr('readonly', true);
    	obj.addClass('readOnly');
	    
    	if( obj.is("input")) {
    		var objType = obj.attr("type");
    		
    		if( objType == "text") {

    		    if( obj.hasClass('datepicker') ) {

    		    	if( obj.next().is("button")) {
    		    		if( obj.next().hasClass('ui-datepicker-trigger')) {
    		    			obj.next().hide();
    		    		}
    		    	}
    		    }
    		    
    		} else if( objType == "radio") {

    		} else if( objType == "checkbox") {
    			
    		} else {
    			
    		}
    	} else if( obj.is("select")) {
    		obj.each(function() {
    	    	obj.hide();
    	    	if (""!=obj[0].options[obj[0].selectedIndex].value) {
    	    		obj.after(obj[0].options[obj[0].selectedIndex].text);
    	    	}
    	    });
    	} else if( obj.is("textarea")) {
    		obj.css('border', 'solid 1px #c6c6c6');
    	} else if( obj.is("button")) {
    		obj.hide();
    	} else {
    		if( obj.hasClass('tBtn') 
    			|| obj.hasClass('gBtn')
    			|| obj.hasClass('ico')) {
    			obj.hide();
    		} 

    	}
    
    }
};


var SetDisabledById = function (args, isFlag) {
    'use strict';
    for( var i = 0, nSize = args.length; i < nSize; i++) {

    	var obj = $('#' + args[i]); 
    	/*
    	console.log( "args[i] = " + args[i]);
    	console.log( "obj attr type = " + obj.attr("type"));
    	console.log( "obj hasClass = " + obj.hasClass('datepicker') );
    	console.log( "obj is select= " + obj.is("select") );
    	console.log( "obj is textarea= " + obj.is("textarea") );
    	console.log( "obj.next().text()=" + obj.next().is("button") );
    	*/

    	if( isFlag ) {
	    	obj.attr('disabled', true);
	
	    	if( obj.is("input")) {
	    		var objType = obj.attr("type");
	    		if( objType == "text") {
	
	    		    if( obj.hasClass('datepicker') ) {
	    		    	if( obj.next().is("button")) {
	    		    		if( obj.next().hasClass('ui-datepicker-trigger')) {
	    		    			obj.next().hide();
	    		    		}
	    		    	}
	  		    	
	    		    }
	    		    
	    		} else if( objType == "radio") {
	    			$('input:radio[name="' + args[i] + '"]').each(function() {
	    		        $(this).attr('disabled', true);
	    			});
	
	    		} else if( objType == "checkbox") {
	    			
	    		} else {
	    		}
	    	} else if( obj.is("button")) {
	    		obj.hide();
	    	} else {
	    		if( obj.hasClass('tBtn') 
	    			|| obj.hasClass('gBtn')
	    			|| obj.hasClass('ico')) {
	    			obj.hide();
	    		} 
	    	}
    	} else {
    		obj.attr('disabled', false);
    		
	    	if( obj.is("input")) {
	    		var objType = obj.attr("type");
	    		if( objType == "text") {
	
	    		    if( obj.hasClass('datepicker') ) {
	    		    	if( obj.next().is("button")) {
	    		    		if( obj.next().hasClass('ui-datepicker-trigger')) {
	    		    			obj.next().show();
	    		    		}
	    		    	}
	  		    	
	    		    }
	    		}
	    	} else if( obj.is("button")) {
	    		obj.show();
	    	} else {
	    		if( obj.hasClass('tBtn') 
	    			|| obj.hasClass('gBtn')
	    			|| obj.hasClass('ico')) {
	    			obj.show();
	    		} 
	    	}
    	}
    }
};



//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//화면 제어 ////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 팝업 윈도우 가운데로 띄울 위치 구하기
 *
 * @param width -
 *            창크기(width)
 * @param height -
 *            창크기(height)
 */
var centerWindow = function(width, height) {

    var outx = screen.width;
    var outy = screen.height;
    var x = (outx - width) / 2;
    var y = (outy - height) / 2;
    dim = new Array(2);
    dim[0] = x;
    dim[1] = y;
    dim[2] = width;
    dim[3] = height;

    return dim;

};

/**
 * Post 방식으로 submit
 *
 * @param url
 *            submit할 주소
 * @param params
 *            Plain Object
 * @param target
 *            url이 적용되는 target 이름(현재 페이지는 '_self'
 */
var fnPostGoto = function(url, params, target) {
    var f = document.createElement('form');
    var obj, value;
    for ( var key in params) {
        value = params[key];
        obj = document.createElement('input');
        obj.setAttribute('type', 'hidden');
        obj.setAttribute('name', key);
        obj.setAttribute('value', value);
        f.appendChild(obj);
    }
    if (target) f.setAttribute('target', target);
    f.setAttribute('method', 'post');
    f.setAttribute('action', url);
    document.body.appendChild(f);
    f.submit();
};

/**
 * Post 방식으로 Popup
 *
 * @param url
 *            submit할 주소
 * @param params
 *            Plain Object
 * @param target
 *            url이 적용되는 target 이름(현재 페이지는 '_self')
 * @param width
 *            팝업창 넓이
 * @param width
 *            팝업창 높이
 */
var fnPostPopup = function(url, params, target, width, height, scrollbar, resizable) {
	if ("_self"==target) {
		fnPostGoto(url, params, target);
    } else {
	    scrollbar = scrollbar;
	    resizable = resizable;
	    var pos = centerWindow(width, height);
	    
	    if (null==target || undefined==target || ""==target) {
	    	target = fnGetRandomText(20);
	    }
	    
	    var popup = window.open(url, target, "width=" + width + ",height=" + height + ",left=" + pos[0] + ",top=" + pos[1] + ",status=no, scrollbars=" + scrollbar + ", resizable=" + resizable );
//	    fnPostGoto(url, params, target);
	    
	    popup.focus();
	    return popup;
    }
};

var fnGetRandomText = function(textSize) {
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for( var i=0; i < textSize; i++ )
        text += possible.charAt(Math.floor(Math.random() * possible.length));

    return text;	
};

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Validation 체크 ////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
* 숫자여부 
*/
function isNumCheck(num)
{
  var objPattern =  /^[-]?\d?\d*\.?\d*$/;
  var str = num;
  if(str == "" || str == null) {
      return true;
  } else {
      if (!objPattern.test(str)) {
          return false;
      } else {
          return true;
      }
  }
}

/**
* 비밀번호 유효성 검증
*
* @param uidObjId -
*            사용자 아이디 Object ID
* @param upwObjId -
*            입력된 비밀번호 Object ID
*/
var checkPasswordValidate = function(uidObjId, upwObjId) {

  var uid = $("#" + uidObjId).val();
  var upw = $("#" + upwObjId).val();

  if (!/^[a-zA-Z0-9]{8,20}$/.test(upw)) {
      alert('비밀번호는 숫자와 영문자 조합으로 8자리 이상을 사용해야 합니다.');
      return false;
  }

  var chk_num = upw.search(/[0-9]/g);
  var chk_eng = upw.search(/[a-z]/ig);

  if (chk_num < 0 || chk_eng < 0) {
      alert('비밀번호는 숫자와 영문자를 혼용하여야 합니다.');
      return false;
  }

  if (/(\w)\1\1\1/.test(upw)) {
      alert('비밀번호에 같은 문자를 4번 이상 사용하실 수 없습니다.');
      return false;
  }

  if (upw.search(uid) > -1) {
      alert('ID가 포함된 비밀번호는 사용하실 수 없습니다.');
      return false;
  }

  return true;

};

/**
* 값이 다른지 비교
*
* @param objId -
*            Object Id
* @param compareData -
*            비교할 값
*/
var isNotEqual = function(objId, compareData) {
  if ($("#" + objId).val() == compareData) { return false; }
  return true;
};

/**
* 값이 동일한지 비교
*
* @param objId -
*            Object Id
* @param compareData -
*            비교할 값
*/
var isEqual = function(objId, compareData) {
  if ($("#" + objId).val() == compareData) { return true; }
  return false;
};

/**
* 빈값체크 후 메시지 처리 및 포커스
*
* @param objId -
*            Object Id
* @param fieldText -
*            필드명
*/
var isEmpty = function(fieldText) {

  if (null==fieldText || "undefined" ==  fieldText || fieldText.length == 0)	return true;
  else return false;
};

var isEmptyToStr = function(fieldText, str) {
	if (isEmpty(fieldText)) return str;
	else return fieldText;
};


var isNotEmpty = function(fieldText) {
	if( isEmpty(fieldText) ) {
		return false;
	} else {
		return true;
	}
};

var isBlank = function(fieldText) {
	if (null==fieldText || "undefined" == fieldText || trimValue(fieldText).length == 0)	return true;
  else return false;
};

var isNotBlank = function(fieldText) {
	if( isBlank(fieldText) ) {
		return false;
	} else {
		return true;
	}
};


/**
* 빈값체크 후 메시지 처리 및 포커스
*
* @param objId -
*            Object Id
* @param msgText -
*            빈값일 경우 출력할 메세지
*/
var isEmptyMsg = function(objId, msgText) {

  if ($("#" + objId).val() == "") {
      alert(msgText);
      $("#" + objId).focus();
      return true;
  }
  return false;

};

/**
* 값이 다른지 비교
*
* @param formId -
*            Form Id
* @param objId -
*            Object Id
* @param compareData -
*            비교할 값
*/
var isNotEqualToArr = function(formId, objId, compareData, idx) {
  if ($("#" + formId + " #" + objId).eq(idx).val() == compareData) { return false; }
  return true;
};

/**
* 배열객체의 특정 순번의 필드 값이 동일한지 비교
*
* @param formId -
*            Form Id
* @param objId -
*            Object Id
* @param compareData -
*            비교할 값
*/
var isEqualToArr = function(formId, objId, compareData, idx) {
  if ($("#" + formId + " #" + objId).eq(idx).val() == compareData) { return true; }
  return false;
};

/**
* 빈값체크 후 메시지 처리 및 포커스
*
* @param formId -
*            Form Id
* @param objId -
*            Object Id
* @param fieldText -
*            필드명
*/
var isEmptyToArr = function(formId, objId, fieldText, idx) {

  if ($("#" + formId + " #" + objId).eq(idx).val() == "") {
      if (typeof fieldText !== 'undefined') {
          alert(fieldText + "을(를) 입력하세요.");
          $("#" + formId + " #" + objId).eq(idx).focus();
      }
      return true;
  }
  return false;
};

/**
* 빈값체크 후 메시지 처리 및 포커스
*
* @param formId -
*            Form Id
* @param objId -
*            Object Id
* @param msgText -
*            빈값일 경우 출력할 메세지
*/
var isEmptyMsgToArr = function(formId, objId, msgText, idx) {

  if ($("#" + formId + " #" + objId).eq(idx).val() == "") {
      alert(msgText);
      $("#" + formId + " #" + objId).eq(idx).focus();
      return true;
  }
  return false;

};

/**
* 라디오 빈값체크 후 메시지 처리 및 포커스
*
* @param email -
*            이메일주소
*/
var isEmptyRadio = function(objId, fieldText) {
  if ($('input[name=' + objId + ']:radio:checked').length == 0) {
      alert(fieldText );
      $("#" + objId).focus();
      return true;
  } else {
      return false;
  }
};

/**
 * 날짜가 맞는 날인지 확인
 *
 * @param date
 */
var isDate = function(txtDate) {
    var currVal = txtDate;

    if (currVal == '') return false;

    if (currVal.length == 10) {
        currVal = currVal.replace(/-/g, "");
    }

    if (currVal.length < 8) return false;

    var dtArray = currVal.match(/^[0-9]{4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])/);

    if (dtArray == null) return false;

    // Checks for mm/dd/yyyy format. yyyymmdd
    dtYear = dtArray[3];
    dtMonth = dtArray[5];
    dtDay = dtArray[7];

    if (dtMonth < 1 || dtMonth > 12)
        return false;
    else if (dtDay < 1 || dtDay > 31)
        return false;
    else if ((dtMonth == 4 || dtMonth == 6 || dtMonth == 9 || dtMonth == 11) && dtDay == 31)
        return false;
    else if (dtMonth == 2) {
        var isleap = (dtYear % 4 == 0 && (dtYear % 100 != 0 || dtYear % 400 == 0));
        if (dtDay > 29 || (dtDay == 29 && !isleap)) return false;
    }
    return true;
};

/**
 * From / To 날짜 비교 '-' 가 들어있는 날짜여야 함
 *
 * @param date
 */
var betweenDate = function(fromDate, toDate) {
    if (!isDate(fromDate)) {
        alert("시작 날짜가 형식이 맞지 않습니다.");
        return false;
    }

    if (!isDate(toDate)) {
        alert("종료 날짜가 형식이 맞지 않습니다.");
        return false;
    }

    var fromDay = getDateObj(fromDate);
    var toDay = getDateObj(toDate);

    if ((toDay.getTime() - fromDay.getTime()) < 0)
        return false;
    else {

    }
    return true;
};


/**
 * From / To 시간 비교,  '/' 가 들어있는 날짜여야 함
 *
 * 날짜: _DD, 시간: _HH, 분: _MM 이 붙어 있는 id 임
 * @param date
 */
var betweenDt = function(fromDtId, toDtId) {
	var fromDate = $("#" + fromDtId + "_DD").val();
	var toDate = $("#" + toDtId + "_DD").val();
	
	if (!isDate(fromDate) ) { 
		alert("시작 날짜가 형식이 맞지 않습니다.");
		return false;
	}
	
	if (!isDate(toDate) ) { 
		alert("종료 날짜가 형식이 맞지 않습니다.");
		return false;
	}
	var fromArray = fromDate.split("/"); 
	var toArray = toDate.split("/"); 
	
	fromDate = new Date(fromArray[0], Number(fromArray[1])-1, fromArray[2], $("#" + fromDtId + "_HH").val(), $("#" + fromDtId + "_MM").val());
	toDate = new Date(toArray[0], Number(toArray[1])-1, toArray[2], $("#" + toDtId + "_HH").val(), $("#" + toDtId + "_MM").val());
	
	if((toDate.getTime() - fromDate.getTime()) < 0){ 
		return false;
	}
	return true;
};

/**
 * From / To 시간 비교,  '/' 가 들어있는 날짜여야 함
 *
 * 날짜: _DD, 시간: _HH, 분: _MM 이 붙어 있는 id 임
 * @param date
 */
var betweenDtStrObj = function(fromDtStr, toDtId) {
	var fromDate = fromDtStr;
	var toDate = $("#" + toDtId + "_DD").val();
	
	if (!isDate(fromDate) ) { 
		alert("시작 날짜가 형식이 맞지 않습니다.");
		return false;
	}
	
	if (!isDate(toDate) ) { 
		alert("종료 날짜가 형식이 맞지 않습니다.");
		return false;
	}
	var fromArray = fromDate.split("/"); 
	var toArray = toDate.split("/"); 
	
	fromDate = getDateObj(fromDate);
	toDate = new Date(toArray[0], Number(toArray[1])-1, toArray[2], $("#" + toDtId + "_HH").val(), $("#" + toDtId + "_MM").val());
	
	if((toDate.getTime() - fromDate.getTime()) < 0){ 
		return false;
	}
	return true;
};


/**
 * 숫자여부(정수) true, false
 * 
 * @param date
 */
function is_integer(varNum) {
	var reg = /^[-|+]?\d+$/;
    return reg.test(varNum);
}


var booleanCheck = function(input, def){
    var flag;
    if(input == undefined){
        flag = def;
    }else{
        if(input){
            flag = true;
        }else{
            flag = false;
        }
    }
    return flag;
};;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//기능 파일 //////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
* 파일업로드 공통팝업
* options 항목 target, url, callback, dataFormat, width, height, readOnly
*/
var openFileUplaod = function(options) {
  options = options || {};
  if (options["KEY_ID"] === 'undefined') {
      alert('필수값 KEY_ID가 없습니다.');
      return;
  }
  var defaults = {TARGET:'FileUpload', url:getContextPath() + '/upload.do', CALLBACK:'fnCallBackFileUpload', DATA_FORMAT:'raw', width:800, height:500, READ_ONLY:false};
  for(var prop in defaults) {
      options[prop] = typeof options[prop] !== 'undefined' ? options[prop] : defaults[prop];
  }
  
  $.extend(options,fnGetMakeParams());
  
  options.APP_SEQ = $("#" + options.KEY_ID).val();
  fnPostPopup(options.url, options, options.TARGET, 600, 300);
  
};

var openAllFileDownload = function(options) {
  options = options || {};
  if (options["KEY_ID"] === 'undefined') {
      alert('필수값 KEY_ID가 없습니다.');
      return;
  }
  var defaults = {target:'downloadFrame', url:getContextPath() + '/download.do', callback:'fnCallBackFileUpload', dataFormat:'raw', width:800, height:500, readOnly:false};
  for(var prop in defaults) {
      options[prop] = typeof options[prop] !== 'undefined' ? options[prop] : defaults[prop];
  }
  options.APP_SEQ = $("#" + options.KEY_ID).val();
  if (options.APP_SEQ === '') {
      alert('다운로드 파일이 없습니다');
      return;
  }
  $('#downloadFrame').remove();
  $('body').append('<iframe id="downloadFrame" style="display:none"></iframe>');
  $('#downloadFrame').get(0).contentWindow.location.href = options.url + "?APP_SEQ=" + options.APP_SEQ;
};

var oneFileDownload = function(appSeq, attachmentSeq) {
	if (appSeq === undefined || appSeq=="") {
		alert("파일 관리키가 없습니다.");
		return false;
	}
	var url = getContextPath() + "/download.do";
	url += "?APP_SEQ=" + appSeq;
	if (isNotBlank(attachmentSeq)) {
		url += "&ATTACHMENT_SEQ=" + attachmentSeq;
	}
	
	$('#downloadFrame').remove();
	$('body').append('<iframe id="downloadFrame" style="display:none"></iframe>');
	$('#downloadFrame').get(0).contentWindow.location.href = url;
};

var displayFileUpload = function(options) {
	options = options || {};
	if (options["KEY_ID"] === 'undefined') {
	    alert('필수 파라미터 KEY_ID가 없습니다.');
	    return;
	}
	var defaults = {url:getContextPath() + '/upload/file.do', CALLBACK:'fnCallBackFileUpload', DATA_FORMAT:'raw', DELETE_INIT:'Y'};
	for(var prop in defaults) {
	    options[prop] = typeof options[prop] !== 'undefined' ? options[prop] : defaults[prop];
	}
	options.APP_SEQ = $("#" + options.KEY_ID).val();
	
	var fnCallBack = window[options.CALLBACK];
	
	$.ajax({
	    type:'GET',
	    url:options.url,
	    data:{APP_SEQ:options.APP_SEQ},
	    dataType : 'json',
	    cache: false,
	    success : function(result) {
	        try {
	            if (result && result.files) {
	                if (fnCallBack) {
	                    var rdata;
	                    if ("table" === options.DATA_FORMAT) {
	                    	var strTable	= "";
	                    	strTable	+= "<table class='tableStyle t_center topLineNo leftLine'>	";
	                    	strTable	+= "<colgroup>                                            	";
	                    	strTable	+= "	<col style='width:8%;'>                   			";
	                    	strTable	+= "	<col style='width:*;'>                   	 		";
	                    	strTable	+= "	<col style='width:15%;'>                  			";
	                    	strTable	+= "	<col style='width:10%;'>                  			";
	                    	strTable	+= "</colgroup>                                           	";
	                    	strTable	+= "<thead>                                               	";
	                    	strTable	+= "	<tr>                                              	";
	                    	strTable	+= "		<th>순번</th>                                   	";
	                    	strTable	+= "		<th>파일명</th>                               	";
	                    	strTable	+= "		<th>등록일자</th>                             	";
	                    	strTable	+= "		<th>파일크기</th>                             	";
	                    	strTable	+= "	</tr>                                             	";
	                    	strTable	+= "</thead>                                              	";
	                    	strTable	+= "<tbody>                                               	";
	
	                    	if (0 === result.files.length) {
	                    			strTable	+= "<tr><td colspan='4' class='t_center' style='height:60px;'>파일이 없습니다.</td></tr>";
	                    		
	                    	} else {
	                    		for(var i in result.files) {
	                    			strTable	+= "	<tr>											";
	                    			strTable	+= "		<td>"+(parseInt(i)+1)+"</td>				";
	                    			
	                    			var _downLinkUrl = "";
	                    			if ("msie"==browser.name)
	                    				_downLinkUrl = "<a href='javascript:oneFileDownload(\""+result.files[i].APP_SEQ+"\",\""+result.files[i].ATTACHMENT_SEQ+"\")' title='"+result.files[i].name+"' style='color:blue;'>"+result.files[i].name+"</a>";
	                    			else 
	                    				_downLinkUrl = "<a href='"+getContextPath() + "/" + result.files[i].url + "' title='"+result.files[i].name+"' style='color:blue;' download>"+result.files[i].name+"</a>";
	                    				
	                    			strTable	+= "		<td class='t_left'>"+_downLinkUrl+"</td>											";
	                    			strTable	+= "		<td class='t_center'>"+formatDate(result.files[i].inputDt.substring(0, 8))+"</td>	";
	                    			strTable	+= "		<td class='t_right'>"+formatFileSize(parseInt(result.files[i].size))+"</td>			";
	                    			strTable	+= "	</tr>											";
	                    		}
	                    	}
	
	                    	strTable	+= "</tbody>												";
	                    	strTable	+= "</table>												";
	
	                    	rdata = strTable;
	                    } else if ("table2" === options.DATA_FORMAT) {
	                    	var strTable	= "";
	                    	strTable	+= "<table class='tableStyle' style='border-right:0px;'>	";
	                    	strTable	+= "<colgroup>                                            	";
	                    	strTable	+= "	<col style='width:7%;'>                   			";
	                    	strTable	+= "	<col style='width:*;'>                   	 		";
	                    	strTable	+= "	<col style='width:15%;'>                  			";
	                    	strTable	+= "</colgroup>                                           	";
	                    	strTable	+= "<tbody>                                               	";
	
	                    	if (0 === result.files.length) {
	                    			strTable	+= "<tr><td colspan='3' class='t_center' style='height:60px;'>파일이 없습니다.</td></tr>";
	                    		
	                    	} else {
	                    		for(var i in result.files) {
	                    			strTable	+= "	<tr>											";
	                    			strTable	+= "		<td align='center'>"+(parseInt(i)+1)+"</td>				";
	                    			
	                    			var _downLinkUrl = "";
	                    			if ("msie"==browser.name)
	                    				_downLinkUrl = "<a href='javascript:oneFileDownload(\""+result.files[i].APP_SEQ+"\",\""+result.files[i].ATTACHMENT_SEQ+"\")' title='"+result.files[i].name+"' style='color:blue;'>"+result.files[i].name+"</a>";
	                    			else 
	                    				_downLinkUrl = "<a href='"+getContextPath() + "/" + result.files[i].url + "' title='"+result.files[i].name+"' style='color:blue;' download>"+result.files[i].name+"</a>";
	                    				
	                    			strTable	+= "		<td class='t_left'>"+_downLinkUrl+"</td>";
	                    			strTable	+= "		<td class='t_center' style='border-right:1px solid #d7d7d7'>"+formatDate(result.files[i].inputDt.substring(0, 8))+"</td>	";
	                    			strTable	+= "	</tr>										";
	                    		}
	                    	}
	
	                    	strTable	+= "</tbody>";
	                    	strTable	+= "</table>";
	
	                    	rdata = strTable;
	                    } else if ("vTable" === options.DATA_FORMAT) {
	                    	var strTable	= "<ul class='file-Upload-List'>";
	                    	if (0 === result.files.length) {
	                    			strTable	+= "";
	                    	} else {
	                    		for(var i in result.files) {
	                    			var _downLinkUrl = "";
	                    			if ("msie"==browser.name)
	                    				_downLinkUrl = "<a href='javascript:oneFileDownload(\""+result.files[i].APP_SEQ+"\",\""+result.files[i].ATTACHMENT_SEQ+"\")' title='"+result.files[i].name+"' style='color:blue;'><p>"+result.files[i].name+"</p></a>";
	                    			else 
	                    				_downLinkUrl = "<a href='"+getContextPath() + "/" + result.files[i].url + "' title='"+result.files[i].name+"' style='color:blue;' download><p>"+result.files[i].name+"</p></a>";
	                    				
	                    			strTable	+= "		<li>"+_downLinkUrl+"</li>";
	                    		}
	                    	}
	                    	strTable += "</ul>";
	                    	rdata = strTable;
	                    } else if ("raw" === options.DATA_FORMAT) {
	                        rdata = result.files;
	                    } else if("bTable" === options.DATA_FORMAT){
	                        var table = "";
	                            for(var i in result.files) {
	                                table  += "<a href='javascript:oneFileDownload(\"" + getContextPath() + "/" + result.files[i].url + "\");' class='downLink'>"+result.files[i].name+"</a>";
	                            }
	                        rdata=table;
	                    }
	                    if (0 === result.files.length && options.DELETE_INIT == 'Y') {
	                    	$("#" + options.KEY_ID).val("");
	                    }
	                    fnCallBack(rdata);
	                }
	            }
	        } catch (e) {
	            alert('첨부파일 조회 중 오류가 발생하였습니다.');
	            console.log(e.message);
	        }
	    },
	    error : errorCallback
	});
};

/**
* 파일 사이즈 표시
*/
var formatFileSize = function(bytes) {
  if (typeof bytes !== 'number') {
      return '';
  }
  if (bytes >= 1000000000) {
      return (bytes / 1000000000).toFixed(2) + ' GB';
  }
  if (bytes >= 1000000) {
      return (bytes / 1000000).toFixed(2) + ' MB';
  }
  return (bytes / 1000).toFixed(2) + ' KB';
};

/*
 * 첨부파일명만 리턴하는 함수
 */
function getAttachExcelFileName(full_path, target){
	
	// 파일명
	var file_nm = full_path.substring(full_path.lastIndexOf("\\")+1, full_path.length);
 	// 확장자
 	var ext = file_nm.substring(file_nm.lastIndexOf(".")+1, file_nm.length);
 	
 	if(isValidFileExcelExt(ext)){
 		$("#"+target).val(file_nm);
 	}else{
 		alert("첨부할 수 없는 확장자를 가진 파일입니다.");
 		return;
 	}
}

/*
 * 첨부파일 확장자 체크 함수
 */
function isValidFileExcelExt(ext){
	
	var isFlag = false;
	
	switch(ext.toLowerCase()){
		case 'xls' :
			isFlag = true;
			break;
		case 'xlsx' :
			isFlag = true;
			break;
		default :
			isFlag = false;
			break;
	}
	
	return isFlag;
}

/**
 * 말풍선 기본 옵션 정의 함수
 * focus : 포커스인 이벤트 발생시 말풍선 표시
 * hover : 포커스인 일시 말풍선 표시 및 아웃일시 표시삭제
 * none : 기본적으로 표시 하지 않음
 * jQueryObject.poshytip('show'), jQueryObject.poshytip('hide') 로 표시 수동조절 
 */
var getPoshyOption = function(msg, showOpt) {
	if (isEmpty(showOpt)) {
		showOpt = "none";
	}
	
	var poshyOption = {
		content: msg,
		className: "tip-yellow",
		alignTo: "target",
		alignX: 'inner-left',
		showOn: showOpt, // none, hover, focus
		offsetY: 0
	}
	return poshyOption;
};


/**
 * input text 숫자만 입력받기  [ex : onkeydown="javascript:setOnlyNumber(this)"  ]
 */

var setOnlyNumber = function(obj){
	$(obj).keyup(function(){
        $(this).val($(this).val().replace(/[^0-9]/g,""));
   });
};

/**
 * 숫자형으로 캐스팅
 */
var toNumber = function(intObj) {
	if (isEmpty(intObj) == true) {
		return 0;
	} else {
		intObj = removeComma(intObj);
		if (isNaN(intObj) == true) {
			return 0;
		} else {
			return Number(intObj);
		}
	}
}

var setCheckbox = function(objId, checked) {
	document.getElementById(objId).checked = checked;
}


$(document).ready(function() {
	/**
	 * setOnlyNumber 함수 Class 정의버전
	 * 사용예) <input type="text" id="NON_PRICE_RATE" class="onlyNum"/>
	 */
	$(".formNum").keyup(function(){
	    $(this).val($(this).val().replace(/[^0-9]/g,""));
	});
	$(".telNum").keyup(function(){
		$(this).val($(this).val().replace(/[^0-9|-]/g,""));
	});
	
	/**
	 * setCommaFormat 함수 Class 정의버전
	 * 사용예) 총금액: <input type="text" id="AMT" class="onlyAmt"/>
	 */
	$(".formAmt").each(function() {
		setCommaFormat($(this).attr("id"));
	});
	
	//기본닫기 버튼 이벤트 정의
	$(".close").on("click", function() {
		window.close();
	});
	
	//입력변경시 동일 td내에 input필드를 모두 클리어
	$(".clearInTd").on("change", function() {
		$(this).closest("td").find("input").not(this).val("");
	});
	
	var placeholderTarget = $('.form-group input[type="text"], .form-group textarea');
	//포커스시 
	placeholderTarget.on('focus', function(){ $(this).siblings('label').fadeOut('fast'); }); 
	
	//포커스아웃시 
	placeholderTarget.on('focusout', function(){ if($(this).val() == ''){ $(this).siblings('label').fadeIn('fast'); } });
	
});

/* 숫자입력체크 숫자면 true */
var NumberChecked  = function (num) {
    var numCode;
    for (i = 0; i < num.length; i++) {
        numCode = num.charCodeAt(i);
        if (numCode <= 57 && numCode >= 48) {
            return true;
        } else {
            alert("숫자만 입력이 가능합니다.");
            return false;
        }
    }
}

//사업자 번호 체크
var BusinessNumber  = function (str) {                                                                                                                  
    // 사업자번호 오류검증                                                                                                                   
    // 아래 공식으로 계산후 10의 배수가 나오면 검증일치                                                                                      
    var num = new Array();                                                                                                                   
        num[0] = 1;                                                                                                                          
        num[1] = 3;                                                                                                                          
        num[2] = 7;                                                                                                                          
        num[3] = 1;                                                                                                                          
        num[4] = 3;                                                                                                                          
        num[5] = 7;                                                                                                                          
        num[6] = 1;                                                                                                                          
        num[7] = 3;                                                                                                                          
        num[8] = 5;                                                                                                                          
    var totalNumber = 0;                                                                                                                     
    var _num        = 0;                                                                                                                     
    for (i = 0; i < str.length-1; i++) {                                                                                                     
        _num = parseInt(str.charAt(i)) * num[i];                                                                                             
        _num = "" + _num;                                                                                                                    
        if (i < 8) {                                                                                                                         
            totalNumber = totalNumber + parseInt(_num.charAt(_num.length-1));                                                                
        } else {                                                                                                                             
            totalNumber = (_num.charAt(_num.length-2) == "") ? totalNumber + 0 : totalNumber + parseInt(_num.charAt(_num.length-2));         
            totalNumber = totalNumber + parseInt(_num.charAt(_num.length-1));                                                                
        }                                                                                                                                    
    }                                                                                                                                        
    totalNumber = totalNumber + parseInt(str.charAt(str.length-1));                                                                          
    var num1    = str.substring(0,3);                                                                                                        
    var num2    = str.substring(3,5);                                                                                                        
    var num3    = str.substring(5,10);                                                                                                       
    if (str == "") {                                                                                                                         
        alert("사업자번호를 입력하세요.");                                                                                                   
        return false;                                                                                                                        
    } else if (num1.length != 3 || num2.length != 2 || num3.length != 5) {                                                                   
        alert("유효하지 않은 사업자 번호입니다.");                                                                                           
        return false;                                                                                                                        
    } else if (!this.NumberChecked(str)) {                                                                                                   
        alert("유효하지 않은 사업자 번호입니다.");                                                                                           
        return false;                                                                                                                        
    } else if (totalNumber%10 != 0) {                                                                                                        
        alert("유효하지 않은 사업자 번호입니다.");                                                                                           
        return false;                                                                                                                        
    } else {                                                                                                                                 
        return true;                                                                                                                         
    }                                                                                                                                        
}                                                                                                                                            
	
//법인번호 검사
function checkCorNo(sRegNo) {
     var re = /-/g;
     sRegNo = sRegNo.replace('-', '');
     if (sRegNo.length != 13) {
           return false;
     }
     var arr_regno = sRegNo.split("");
     var arr_wt = new Array(1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2);
     var iSum_regno = 0;
     var iCheck_digit = 0;
     for (i = 0; i < 12; i++) {
           iSum_regno += eval(arr_regno[i]) * eval(arr_wt[i]);
     }
     iCheck_digit = 10 - (iSum_regno % 10);
     iCheck_digit = iCheck_digit % 10;
     if (iCheck_digit != arr_regno[12]) {
           return false;
     }
     return true;
}



/* 법인번호 체크 */                                                                                                                          
var CorporationNumber  = function (str) {                                                                                                          
    // 법인번호 오류검증 공식                                                                                                                
    // 법인번호에서 마지막 자리를 제외한                                                                                                     
    // 앞에 모든 자리수를 1과 2를 순차적으로 곱한다.                                                                                         
    // 예) 1234567890123                                                                                                                     
    //     ************                                                                                                                      
    //     121212121212                                                                                                                      
    //     각각 곱한 수를 모든 더하고 10으로 나눈 나머지 수를 구한다.                                                                        
    //     (각각더한수 % 10)                                                                                                                 
    //     나눈 나머지 수와 법인번호 마지막 번호가 일치하면 검증일치                                                                         
    var totalNumber = 0;                                                                                                                     
    var num = 0;                                                                                                                             
    for (i = 0; i < str.length-1; i++) {                                                                                                     
        if (((i + 1) % 2) == 0) {                                                                                                            
            num = parseInt(str.charAt(i)) * 2;                                                                                               
        } else {                                                                                                                             
            num = parseInt(str.charAt(i)) * 1;                                                                                               
        }                                                                                                                                    
        if (num > 0) {                                                                                                                       
            totalNumber = totalNumber + num;                                                                                                 
        }                                                                                                                                    
    }                                                                                                                                        
    totalNumber = (totalNumber%10 < 10) ? totalNumber%10 : 0;                                                                                
    if (str == "") {                                                                                                                         
        alert("법인번호를 입력하세요.");                                                                                                     
        return false;                                                                                                                        
    } else if (str.length != 13) {                                                                                                           
        alert("유효하지 않은 법인 번호입니다.");                                                                                             
        return false;                                                                                                                        
    } else if (!this.NumberChecked(str)) {                                                                                                   
        alert("유효하지 않은 법인 번호입니다.");                                                                                             
        return false;                                                                                                                        
    } else if (totalNumber != str.charAt(str.length-1)) {                                                                                    
        alert("유효하지 않은 법인 번호입니다.");                                                                                             
        return false;                                                                                                                        
    } else {                                                                                                                                 
        return true;                                                                                                                         
    }                                                                                                                                        
}     