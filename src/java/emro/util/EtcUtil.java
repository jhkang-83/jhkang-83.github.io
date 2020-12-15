package emro.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import wdf.Globals;
import wdf.core.framework.FCConfiguration;
import wdf.dataobject.BUMapObject;
import wdf.dataobject.BUObjectCache;
import flex.messaging.FlexContext;
import flex.messaging.io.ArrayCollection;
import flex.messaging.io.ArrayList;


public class EtcUtil
{
	/**  
	 * 모든 controller 클래스에서 사용할 log4j Logger.
	 */

	/**    
	 * controller 클래스의 Logger 인스턴스 생성.
	 */

	public EtcUtil()
	{
	}
	
	public static String toUpperCase(String st)
	{
		if(st == null) return "";
		return st.toUpperCase();
	}
	/**
	 * server.conf 에 존재하는 Value를 얻는다.
	 * @param key
	 * @return
	 */
	public static String getConfigValue(String key)
	{
		return FCConfiguration.getConfiguration().getConfig(key);
	}
	
	public static String getNextDegr(String st)
	{
		if(st == null || st.trim().equals("")) return "001";
		
		int cd = 0;
		try	{
			cd = Integer.parseInt(st);
			cd ++;
		}
		catch(Exception e) {
			cd = 1;
		}
		String cdst = String.valueOf(cd);
		if(cdst.length() == 1) return "00"+cdst;
		else if(cdst.length() == 2) return "0"+cdst;
		else return cdst;
	}

	public static BUObjectCache getDeleteList(Object list)
	{
		BUObjectCache result = new BUObjectCache();
		BUObjectCache source = null;
		if(list == null) return result;
		try {
			source = new BUObjectCache( (List)list );
		} catch(Exception e) {
			return result;
		}
		
		for(int i=0;i<source.size();i++) {
			Object obj = source.get(i);
			String status =(String)( ((Map)obj).get("editstatus"));
			if("D".equals(status) ) result.add(obj);
		}
		
		return result;
	}

	public static BUObjectCache getUpdateList(Object list)
	{
		BUObjectCache result = new BUObjectCache();
		BUObjectCache source = null;
		if(list == null) return result;
		try {
			source = new BUObjectCache( (List)list );
		} catch(Exception e) {
			return result;
		}
		
		for(int i=0;i<source.size();i++) {
			Object obj = source.get(i);
			String status =(String)( ((Map)obj).get("editstatus"));
			if("U".equals(status) ) result.add(obj);
		}
		
		return result;
	}

	public static BUObjectCache getInsertList(Object list)
	{
		BUObjectCache result = new BUObjectCache();
		BUObjectCache source = null;
		if(list == null) return result;
		try {
			source = new BUObjectCache( (List)list );
		} catch(Exception e) {
			return result;
		}
		
		for(int i=0;i<source.size();i++) {
			Object obj = source.get(i);
			String status =(String)( ((Map)obj).get("editstatus"));
			if("I".equals(status) ) result.add(obj);
		}
		
		return result;
	}

	public static BUObjectCache getMergeList(Object list)
	{
		BUObjectCache result = new BUObjectCache();
		BUObjectCache source = null;
		if(list == null) return result;
		try {
			source = new BUObjectCache( (List)list );
		} catch(Exception e) {
			return result;
		}
		
		for(int i=0;i<source.size();i++) {
			Object obj = source.get(i);
			String status =(String)( ((Map)obj).get("editstatus"));
			if("I".equals(status) || "U".equals(status)) result.add(obj);
		}
		
		return result;
	}
	
	public static void addToList(Object list,Object obj)
	{
		((List)list).add(obj); 
	}
	
	public static String getRealPath(String path)
	{
		return FCConfiguration.getConfiguration().getContextParam(Globals.WEB_INF_PATH);
	}

    public static BUObjectCache makeEmptyList()
    {
    	return new BUObjectCache();
    }
    
    public static BUMapObject makeCloneObject(Object obj)
    {
    	BUMapObject mobj = new BUMapObject((Map)obj);
    	return new BUMapObject((HashMap)mobj.clone());
    }
    
    public static BUMapObject makeEmptyObj()
    {
    	return new BUMapObject();
    }
    
    
    public static String encryptRotemPass(String st)
    {
    	if(st == null || st.equals("")) return st;
    	String result= "";
    	return result;
    }
    

	public static String createRandomPassword(int size)
	{
		String source = "abcdefghijklmnopqrstuvwxyz";
		source += "012345678910";
		Random random = new Random();
		
		StringBuffer result = new StringBuffer();
		for(int i=0;i<size;i++) 
			result.append(source.charAt(random.nextInt(source.length())));
		return result.toString();
	}
	
	public static boolean  checkSt(Object st)
	{
		if(st instanceof String) {
			if(st == null || ((String)st).trim().equals("")) return false;
			return true;
		} else {
			if(st == null || (st.toString()).trim().equals("")) return false;
			return true;
		}
	}
	
	public static boolean isEmptyList(Object st)
	{
		if(st == null) return true;
		List lst = (List)st;
		if(lst.size() == 0) return true;
		return false;
	}
	
	public static String checkSt(String st,String st2)
	{
		if(st == null || st.trim().equals("")) return st2;
		return st;
	}
	
	public static BUObjectCache convertListToCache(List cache)
	{
		if(cache == null) return null;
		BUObjectCache result = new BUObjectCache();
		result.addAll(cache);
		return result;
	}

	/**
	 * 현재 날자를 리턴.
	 * @return
	 */
	public static String getTodayYYYYMMDDHHmm()
	{
		String result = getTodayYYYYMMDD();
		
		Calendar today = java.util.Calendar.getInstance();
		
		String hour = String.valueOf(today.get(Calendar.HOUR_OF_DAY));
		if(hour.length()==1) hour = "0" + hour;
		result += hour;
		
		String minute = String.valueOf(today.get(Calendar.MINUTE));
		if(minute.length()==1) minute = "0" + minute;
		
		result += minute;
		
		return result;
	}

	/**
	 * 현재 날자를 리턴.
	 * @return
	 */
	public static String getTodayYYYYMMDD()
	{
		String result = getTodayYYYY();
		result += getTodayMM();
		result += getTodayDD();
		return result;
	}

	/**
	 * 현재 년을 리턴.
	 * @return
	 */
	public static String getTodayYYYY()
	{
		Calendar today = java.util.Calendar.getInstance();
		return String.valueOf(today.get(Calendar.YEAR));
	}
	
	/**
	 * 현재 월을 리턴.
	 * @return
	 */
	public static String getTodayMM()
	{
		Calendar today = java.util.Calendar.getInstance();
		String MM = String.valueOf(today.get(Calendar.MONTH)+1);
		if(MM.length()==1) return "0"+MM;
		return MM;
	}
	
	/**
	 * 현재 날을 리턴.
	 * @return
	 */
	public static String getTodayDD()
	{
		Calendar today = java.util.Calendar.getInstance();
		String DD = String.valueOf(today.get(Calendar.DAY_OF_MONTH));
		if(DD.length()==1) return "0"+DD;
		return DD;
	}
	
	public static String getNotInPhaseOfkey(Object obj,String key)
	{
		if(obj == null) return null;
		String cname = obj.getClass().getName();

		ArrayList array = new ArrayList();

		if(cname.equals("wdf.dataobject.BUObjectCache")) {
			for(int i=0;i<((BUObjectCache)obj).size();i++) {
				array.add(((BUObjectCache)obj).get(i));
			}
		} else if(cname.equals("flex.messaging.io.ArrayCollection")) {
			for(int i=0;i<((ArrayCollection)obj).size();i++) {
				array.add(((ArrayCollection)obj).get(i));
			}
		}
		
		if(array.size() == 0) return null;

		StringBuffer inphase = new StringBuffer(" NOT IN (");
		
		for(int i=0;i<array.size();i++) {
			inphase.append( "'" );
			inphase.append( ((Map)array.get(i)).get(key) );
			inphase.append( "'" );
			if(i!=array.size()-1) inphase.append(",");
		}
		inphase.append(")");
		
		return inphase.toString();
	}
	
	public static String getIntNotInPhaseOfkey(Object obj,String key)
	{
		if(obj == null) return null;
		String cname = obj.getClass().getName();

		ArrayList array = new ArrayList();

		if(cname.equals("wdf.dataobject.BUObjectCache")) {
			for(int i=0;i<((BUObjectCache)obj).size();i++) {
				array.add(((BUObjectCache)obj).get(i));
			}
		} else if(cname.equals("flex.messaging.io.ArrayCollection")) {
			for(int i=0;i<((ArrayCollection)obj).size();i++) {
				array.add(((ArrayCollection)obj).get(i));
			}
		}
		
		if(array.size() == 0) return null;

		StringBuffer inphase = new StringBuffer(" NOT IN (");
		
		for(int i=0;i<array.size();i++) {
			inphase.append( ((Map)array.get(i)).get(key) );
			if(i!=array.size()-1) inphase.append(",");
		}
		inphase.append(")");
		
		return inphase.toString();
	}
	

	public static String getInPhaseOfkey(Object obj,String key)
	{
		String cname = obj.getClass().getName();

		ArrayList array = new ArrayList();

		if(cname.equals("eswf.dataobject.List")) {
			for(int i=0;i<((List)obj).size();i++) {
				array.add(((List)obj).get(i));
			}
		}else if(cname.equals("wdf.dataobject.BUObjectCache")) {
			for(int i=0;i<((BUObjectCache)obj).size();i++) {
				array.add(((BUObjectCache)obj).get(i));
			}
		}else if(cname.equals("flex.messaging.io.ArrayCollection")) {
			for(int i=0;i<((ArrayCollection)obj).size();i++) {
				array.add(((ArrayCollection)obj).get(i));
			}
		}

		StringBuffer inphase = new StringBuffer("IN (");
		
		for(int i=0;i<array.size();i++) {
			inphase.append( "'" );
			inphase.append( ((Map)array.get(i)).get(key) );
			inphase.append( "'" );
			if(i!=array.size()-1) inphase.append(",");
		}
		inphase.append(")");
		
		return inphase.toString();
	}
	
	public static boolean isNull(Object obj) 
	{
		if(obj == null) return true;
		return false;
	}
	

	public static String getInPhaseOfPrePlanSql(Object obj)
	{
		String cname = obj.getClass().getName();

		ArrayList itgrps = new ArrayList();
		

		if(cname.equals("wdf.dataobject.BUObjectCache")) {
			for(int i=0;i<((BUObjectCache)obj).size();i++) {
				itgrps.add(((BUObjectCache)obj).get(i));
			}
		} else if(cname.equals("flex.messaging.io.ArrayCollection")) {
			for(int i=0;i<((ArrayCollection)obj).size();i++) {
				itgrps.add(((ArrayCollection)obj).get(i));
			}
		}
		

		StringBuffer inphase = new StringBuffer("IN (");
		
		for(int i=0;i<itgrps.size();i++) {
			inphase.append( "'" );
			inphase.append( ((Map)itgrps.get(i)).get("itgrp_cd") );
			inphase.append( "_" );
			inphase.append( ((Map)itgrps.get(i)).get("ver") );
			inphase.append( "'" );
			if(i!=itgrps.size()-1) inphase.append(",");
		}
		inphase.append(")");
		
		return inphase.toString();
	}
	
	/**
	 * 반각 기준으로 주어진 size 크기가 되도록 input을 지우고 끝을 ... 처리하여 반환한다
	 * 만약 주어진 사이즈보다 작은 경우에는 그대로 반환한다
	 * 만약 null이 들어오면 ""으로 반환한다
	 */
	public static String limit(String input, int size) {
		if (input == null) {
			return "";
		}
		if (input.length() <= size) {
			return input;
		}
		else {
			StringBuffer sb = new StringBuffer(input);
			return sb.delete(size, input.length()).append("...").toString();
		}
	}
	
	/**
	 * 공급업체 회원가입시 클라이언트 ip 주소 조회
	 */
	public static String getClientIp() {
		HttpServletRequest request = FlexContext.getHttpRequest();
		return request.getRemoteAddr().toString();
	}
	
	/**
	 * 서버의 아이피 정보를 리턴한다.
	 * @return
	 */
	public static String getHostIp() {
		String serverip = "";
		try {
			InetAddress inet = InetAddress.getLocalHost();
			serverip = inet.getHostAddress();
			if ("127.0.0.1".equals(serverip)) {
				Enumeration<NetworkInterface> e_ = NetworkInterface.getNetworkInterfaces();
				while(e_.hasMoreElements()) {
					NetworkInterface if_ = (NetworkInterface)e_.nextElement();
					Enumeration<InetAddress> e2_ = if_.getInetAddresses();
					while(e2_.hasMoreElements()) {
						InetAddress addr_ = (InetAddress)e2_.nextElement();
						if(!addr_.isLoopbackAddress()) {
							serverip = addr_.getHostAddress();
							break;
						}
					}
				}
			}
		} catch (Exception e) {}
		
		return serverip;
	}
	
	
	/**
	 *  XSS 방지
	 */
	public static String removeXSS(String str) {
		return removeXSS(str, true);
	}
	
	/**
	 *  XSS 방지
	 */
	public static String removeXSS(String str, boolean use_html) {
	    String str_low = "";
	    
	    use_html = use_html || false;
	    
	    if(str == null ) return "";
	    
	    if(use_html){ 
	        str_low= str.toLowerCase();
	        if( str_low.contains("javascript") || str_low.contains("script")     || str_low.contains("iframe") || 
	                str_low.contains("document")   || str_low.contains("vbscript")   || str_low.contains("applet") || 
	                str_low.contains("embed")      || str_low.contains("object")     || str_low.contains("frame") || 
	                str_low.contains("grameset")   || str_low.contains("layer")      || str_low.contains("bgsound") || 
	                str_low.contains("alert")      || str_low.contains("onblur")     || str_low.contains("onchange") || 
	                str_low.contains("onclick")    || str_low.contains("ondblclick") || str_low.contains("enerror") ||  
	                str_low.contains("onfocus")    || str_low.contains("onload")     || str_low.contains("onmouse") || 
	                str_low.contains("onscroll")   || str_low.contains("onsubmit")   || str_low.contains("onunload"))
	        {
	            str = str_low;
	            str = str.replaceAll("javascript", "x-javascript");
	            str = str.replaceAll("script", "x-script");
	            str = str.replaceAll("iframe", "x-iframe");
	            str = str.replaceAll("document", "x-document");
	            str = str.replaceAll("vbscript", "x-vbscript");
	            str = str.replaceAll("applet", "x-applet");
	            str = str.replaceAll("embed", "x-embed");
	            str = str.replaceAll("object", "x-object");
	            str = str.replaceAll("frame", "x-frame");
	            str = str.replaceAll("grameset", "x-grameset");
	            str = str.replaceAll("layer", "x-layer");
	            str = str.replaceAll("bgsound", "x-bgsound");
	            str = str.replaceAll("alert", "x-alert");
	            str = str.replaceAll("onblur", "x-onblur");
	            str = str.replaceAll("onchange", "x-onchange");
	            str = str.replaceAll("onclick", "x-onclick");
	            str = str.replaceAll("ondblclick","x-ondblclick");
	            str = str.replaceAll("enerror", "x-enerror");
	            str = str.replaceAll("onfocus", "x-onfocus");
	            str = str.replaceAll("onload", "x-onload");
	            str = str.replaceAll("onmouse", "x-onmouse");
	            str = str.replaceAll("onscroll", "x-onscroll");
	            str = str.replaceAll("onsubmit", "x-onsubmit");
	            str = str.replaceAll("onunload", "x-onunload");
	        }
	    }else{ 
	        str = str.replaceAll("\"","&gt;");
	        str = str.replaceAll("&", "&amp;");
	        str = str.replaceAll("<", "&lt;");
	        str = str.replaceAll(">", "&gt;");
	        str = str.replaceAll("%00", null);
	        str = str.replaceAll("\"", "&#34;");
	        str = str.replaceAll("\'", "&#39;");
	        str = str.replaceAll("%", "&#37;");    
	        str = str.replaceAll("../", "");
	        str = str.replaceAll("..\\\\", "");
	        str = str.replaceAll("./", "");
	        str = str.replaceAll("%2F", "");
	    }
	    return str;
	}
	
	
}