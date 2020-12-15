package emro.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SelectMaker
{
	private HttpServletRequest	request;
	private HttpServletResponse	response;
	
	public SelectMaker(HttpServletRequest request, HttpServletResponse response)
	{
		this.request = request;
		this.response = response;
	}

	public void responseOptions(String[] values, String[] texts) throws IOException{
		StringBuffer options = new StringBuffer();
	    for (int i = 0; i < values.length; i++) {
	        options.append("<option value='"+values[i]+"'>"+texts[i]+"</option>\r\n");
		}
	    response.setContentType("text/html; charset=utf-8");
	    response.getOutputStream().print(options.toString());
	    response.flushBuffer();
	}
	
	
	public void responseOptions(String[] values, String[] texts, String selectedCd) throws IOException{
		StringBuffer options = new StringBuffer();
	    for (int i = 0; i < values.length; i++) {
	    	options.append("<option value='"+values[i]+"' " + ((selectedCd != null && selectedCd.equals(new String(values[i].getBytes(),"UTF-8"))) ? "selected" : "")+">"+texts[i]+"</option>\r\n");
		}
	    response.setContentType("text/html; charset=utf-8");
	    response.getOutputStream().print(options.toString());
	    response.flushBuffer();
	}
}
