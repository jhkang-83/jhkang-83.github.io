<%@ page language="java" session="true" contentType="application/vnd.ms-excel; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.DecimalFormat"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
	String nm = "품의리스트 양식";
	DecimalFormat df = new DecimalFormat("00");
	Calendar currentCalendar = Calendar.getInstance();
	String strYear = Integer.toString(currentCalendar.get(Calendar.YEAR));
	String strMonth = df.format(currentCalendar.get(Calendar.MONTH)+1);
	String strDay = df.format(currentCalendar.get(Calendar.DATE));
	
	String docName = "";
	
	String header = request.getHeader("User-Agent");
	if (header.contains("Edge")){
		docName = URLEncoder.encode(nm + "_" + strYear + strMonth + strDay, "UTF-8").replaceAll("\\+", "%20");
	    response.setHeader("Content-Disposition", "attachment;filename=\"" + docName + "\".xls;");
	} else if (header.contains("MSIE") || header.contains("Trident")) { // IE 11버전부터 Trident로 변경되었기때문에 추가해준다.
		docName = URLEncoder.encode(nm + "_" + strYear + strMonth + strDay, "UTF-8").replaceAll("\\+", "%20");
	    response.setHeader("Content-Disposition", "attachment;filename=" + docName + ".xls;");
	} else if (header.contains("Chrome")) {
		docName = new String((nm + "_" + strYear + strMonth + strDay).getBytes("UTF-8"), "ISO-8859-1");
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + docName + "\".xls");
	} else if (header.contains("Opera")) {
		docName = new String((nm + "_" + strYear + strMonth + strDay).getBytes("UTF-8"), "ISO-8859-1");
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + docName + "\".xls");
	} else if (header.contains("Firefox")) {
		docName = new String((nm + "_" + strYear + strMonth + strDay).getBytes("UTF-8"), "ISO-8859-1");
	    response.setHeader("Content-Disposition", "attachment; filename=" + docName + ".xls");
	}
	
	response.setHeader("Content-Description","JSP Generated Data");
%>
    
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Style-Type" content="text/css">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Expires" content="0">
</head>

<body>
	<div class="gridArea m_t5 m_l10 m_r20">
		<table id="gridBox" class="tableStyle" border="1">
			<colgroup>
				<col style="width: ">
				<col style="width: ">
				<col style="width: ">
				<col style="width: ">
				<col style="width: ">
				<col style="width: ">
				<col style="width: ">
				<col style="width: ">
				<col style="width: ">
				<col style="width: ">
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
				</tr>
			</thead>
			
			<tbody>
				<tr>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</tbody>
		</table>
	</div>
</body>
</html>