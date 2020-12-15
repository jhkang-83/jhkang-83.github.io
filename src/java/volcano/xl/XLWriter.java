package volcano.xl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class XLWriter
{
	String[]			sheet_names	= new String[] { "undefined" };
	XLStyle				styler 		= null;
	HttpServletRequest	request		= null;
	HttpServletResponse	response	= null;
	HSSFWorkbook		workbook	= new HSSFWorkbook();
	int					indexSheet	= -1;
		
	public XLWriter(HttpServletRequest request, HttpServletResponse response)
	{
		this.request = request;
		this.response = response;
		this.styler = new XLStyle(workbook);
	}
	
	public void addSheetData(XLSheetable data, Map<Short, Short> colwidths)
	{
		HSSFSheet sheet = workbook.createSheet();
		workbook.setSheetName(++indexSheet, data.getName());
		XLConstructor xlc = new XLConstructor(workbook, sheet, styler);
		xlc.setColumnWidths(colwidths);
		data.init(styler);
		List<XLSection> sections = data.getSections();
		for(int i=0 ; i < sections.size() ; i++)
			xlc.construct(sections.get(i));
	}
	
	public void addSheetData(XLSheetable data)
	{
		addSheetData(data, new HashMap<Short, Short>());
	}
	
	public void addSheetData1(XLSheetable data)
	{
		addSheetData1(data, new HashMap<Short, Short>());
	}
	
	public void addSheetData1(XLSheetable data, Map<Short, Short> colwidths)
	{
		HSSFSheet sheet = workbook.createSheet();
		workbook.setSheetName(++indexSheet, data.getName());
		XLConstructor xlc = new XLConstructor(workbook, sheet, styler);
		xlc.setColumnWidths(colwidths);
		data.init(styler);
		List<XLSection> sections = data.getSections();
		for(int i=0 ; i < sections.size() ; i++)
			xlc.construct1(sections.get(i), data.getVisibleTitle());
	}
	
	public void write(String fileName)
	{
		try{
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment;filename=" +  java.net.URLEncoder.encode(fileName,"UTF-8") + ".xls");
			response.setHeader("Content-Description", "JSP Generated Data");
			workbook.write(response.getOutputStream());
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public byte[] getFileBytes(){
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		try{
			workbook.write(baos);
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return baos.toByteArray();
	}


	private void printError(String errorMessage)
	{
	}
	
}
