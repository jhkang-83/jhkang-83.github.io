package volcano.custom.excel;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelToMXLServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("utf-8");
		List<FileItem> fileitems;
		try {
			fileitems = upload.parseRequest(request);
		} catch (FileUploadException e) {
			throw new ServletException(e);
		}
		response.resetBuffer();
		response.setContentType("text/xml");
		response.setCharacterEncoding("utf-8");
		PrintWriter writer = response.getWriter();

		writer.print("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		Iterator<FileItem> iter = fileitems.iterator();
		while (iter.hasNext()) {
			FileItem fileitem = (FileItem) iter.next();
			if(fileitem.isFormField())
				continue;
			Workbook workbook = null;
			String fileName = fileitem.getName();
			String extension = fileName
					.substring(fileName.lastIndexOf(".") + 1);
			if(extension.equals("xls"))
				workbook = new HSSFWorkbook(new BufferedInputStream(new ByteArrayInputStream(fileitem.get())));
			else if (extension.equals("xlsx"))
				workbook = new XSSFWorkbook(
						new BufferedInputStream(
								new ByteArrayInputStream(
										fileitem.get()
										)
								)
						);
			
			if(workbook != null){
				FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
				writer.print("<excel ");
				writer.print("name=\"");
				writer.print(fileName);
				writer.print("\" >");
				int sheetCount = workbook.getNumberOfSheets();
				for(int sheetIndex = 0 ; sheetIndex < sheetCount ; sheetIndex++){
					String sheetName = workbook.getSheetName(sheetIndex);
					//sheet 시작표시
					writer.print("<sheet ");
					writer.print("name=\"");
					writer.print(sheetName);
					writer.print("\" >");
					Sheet sheet = workbook.getSheetAt(sheetIndex);
					List<CellRangeAddress> mergedList = new java.util.ArrayList<CellRangeAddress>();					
					int length = sheet.getNumMergedRegions(); 
					for(int i=0; i<length; i++){
						mergedList.add(sheet.getMergedRegion(i));
					}					
					int rowCount = sheet.getLastRowNum();
					for(int rowIndex = 0 ; rowIndex <= rowCount ; rowIndex++)
					{
						//row 시작표시
						Row row = sheet.getRow(rowIndex);
						if(row == null)
							continue;
						writer.print("<r><![CDATA[");
						int cellCount = row.getLastCellNum();
						for(int cellIndex = 0 ; cellIndex < cellCount ; cellIndex++)
						{
							Cell cell = null;
							CellRangeAddress cra = isMergedColumn(mergedList, rowIndex, cellIndex);
							if(cra != null) {
								Row tempRow = sheet.getRow(cra.getFirstRow());
								cell = tempRow.getCell(cra.getFirstColumn());
							}
							else {
								//cell value
								cell = row.getCell(cellIndex);
							}
							String text = extractCellValue(cell, evaluator);
							writer.print(text);
							//cell separator
							if(cellIndex < cellCount - 1){
								writer.print("");
							}
						}
						//row 종료표시
						writer.print("]]></r>");
					}
					//sheet 종료표시
					writer.print("</sheet>");
				}
				// file 종료표시
				writer.print("</excel>");
			}else{
				writer.print("<excel></excel>");
			}
			break;
		}
	}
	
	protected CellRangeAddress isMergedColumn(List<CellRangeAddress> mergedList, int rowIndex, int cellIndex) {
		java.util.Iterator<CellRangeAddress> iter = mergedList.iterator();
		while(iter.hasNext()) {
			CellRangeAddress cra = iter.next();
			if(cra.getFirstRow() <= rowIndex && cra.getLastRow() >= rowIndex && 
			   cra.getFirstColumn() <= cellIndex && cra.getLastColumn() >= cellIndex) {
				return cra;
			}
		}
		return null;
	}
	
	protected String extractCellValue(Cell cell, FormulaEvaluator evaluator){
		if(cell == null)
			return null;
		String result = null;
		CellStyle cellStyle = cell.getCellStyle();
		int cellType = cell.getCellType();
		if(cellType == Cell.CELL_TYPE_FORMULA){
			cellType = evaluator.evaluateFormulaCell(cell);
		}
		switch( cellType)
		{
			case Cell.CELL_TYPE_NUMERIC :
				String formatString = cellStyle.getDataFormatString();
				if(formatString.toUpperCase().indexOf("YY") >= 0) {
					if(formatString.toUpperCase().indexOf("HH") >= 0) {
						result = datetimeFormat.format(cell.getDateCellValue());
					} else {
						result = dateFormat.format(cell.getDateCellValue());
					}
				} else {
					result = dformat.format(cell.getNumericCellValue());
				}
				break;
			case Cell.CELL_TYPE_STRING : 
				result = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_BLANK : 
				result = "";
				break;
			case Cell.CELL_TYPE_ERROR : 
				result = dformat.format(cell.getErrorCellValue());
				break;
		}
		return result;
		
	}
	
	protected DecimalFormat dformat = new DecimalFormat("#########################0.#########");
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	protected SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyyMMddhhmmss");

}
