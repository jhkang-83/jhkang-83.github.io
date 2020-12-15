package emro.util;

import java.util.*;

/**
 * <PRE>
 * Filename	: DocumentUtil.java <BR>
 * Package	: com.emro.util <BR>
 * Function	: <BR>
 * Comment	: Random값 추출에 유용한 각종 유틸리티 메쏘드 <BR>
 * History	: 2009/04/01,  v1.0, 최초작성 <BR>
 * </PRE>
 * @version	1.0
 * @author 	이창진 Copyright (c) 2009 by EMRO Corp. All Rights Reserved.
 *
 */

public class RandomUtil {
		
	/**
	 * 숫자를 이용하여 주어진 크기를 갖는 String을 만든다.
	 * 리턴된 문자열은 0으로 시작할 수도 있다.
	 */
	public static String getNumber(int size) {
		String chars = "0123456789";
		String ret = "";
		
		char[] ch = new char[chars.length()];
		double span = 1 / (double)chars.length();
		double[] spanStart = new double[chars.length()];
		double[] spanEnd = new double[chars.length()];
		
		for(int i=0; i<chars.length(); i++) {
			ch[i] = chars.charAt(i);
			spanStart[i] = span * (double)i;
			if( i == (chars.length()-1) )
				spanEnd[i] = 1;
			else 
				spanEnd[i] = span * (double)(i+1);
		}
	
		while( ret.length() < size ) {
			double rdNum = Math.random();
			ret = ret + ch[getRangeIndex(spanStart, spanEnd, rdNum)];
		}
	
		return ret;
	}

	/**
	 * 주어진 구간안에서(양끝 포함) 랜덤하게 선택된 날짜를 리턴한다.
	 * startStr과 endStr은 yyyy-m-d 형태이다.
	 * 날짜 구간의 입력이 제대로 들어가지 않으면 null이 리턴된다.
	 */
	public static java.util.Date getDate(String startStr, String endStr) {
		
		java.util.Date startDate = CalendarUtil.getDate(startStr);
		java.util.Date endDate = CalendarUtil.getDate(endStr);
		
		if (startStr == null || endStr == null) {
			return null;
		}
		
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		
		long span = endTime - startTime;
		long rdTime = startTime + (long) (Math.random() * (double) span + 0.5);
		return new java.util.Date(rdTime);
	}	
	
	/**
	 * chars를 이용하여 주어진 크기를 갖는 String을 만든다.
	 */
	public static String getString(int size, String chars) {
		String ret = "";
		
		char[] ch = new char[chars.length()];
		double span = 1 / (double)chars.length();
		double[] spanStart = new double[chars.length()];
		double[] spanEnd = new double[chars.length()];
		
		for(int i=0; i<chars.length(); i++) {
			ch[i] = chars.charAt(i);
			spanStart[i] = span * (double)i;
			if( i == (chars.length()-1) )
				spanEnd[i] = 1;
			else 
				spanEnd[i] = span * (double)(i+1);
		}
	
		while( ret.length() < size ) {
			double rdNum = Math.random();
			ret = ret + ch[getRangeIndex(spanStart, spanEnd, rdNum)];
		}
		
		return ret;
	}

	/**
	 * 주어진 숫자가 주어진 구간의 몇번째 index에 해당하는지 리턴
	 * 해당이 없는 경우에는 -1 리턴
	 */
	private static int getRangeIndex(double[] spanStart, double[] spanEnd, double point) {
		for(int i=0; i<spanStart.length; i++ ){
			if( spanStart[i] <= point && spanEnd[i] > point )
				return i;
		}
		return -1;
	}
	
	/**
	 * 주어진 startBoundary이상 endBoundary이하의 숫자중 랜덤하게 숫자를 선택하여 반환한다.
	 * 해당이 없는 경우에는 -1 리턴
	 */
	public static int getNumber(int startBoundary, int endBoundary) {
		int ret = startBoundary + (int) (Math.random() * (double) (endBoundary - startBoundary) + 0.5);
		return ret;
	}

}
	