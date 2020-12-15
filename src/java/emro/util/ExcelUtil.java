package emro.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

 
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;

import wdf.dataobject.BUMapObject;

import emro.util.DocumentProperties;

import eswf.dataobject.Map;  
/**
 * <PRE>
 * Filename	: XmlUtil.java <BR>
 * Package	: com.emro.util <BR>
 * Function	: <BR>
 * Comment	: XML을 다루는데 유용한 각종 유틸리티 메쏘드 <BR>
 * History	: 2009/04/01,  v1.0, 최초작성 <BR>
 * </PRE>
 * @version	1.0
 * @author 	이창진 Copyright (c) 2009 by EMRO Corp. All Rights Reserved.
 *
 */
public class ExcelUtil 
{   
	protected DecimalFormat	dformat	= new DecimalFormat("#########################0.#########");
	//GeneralEditor Load
	public void ebillExcelUploadForGrid(HttpServletRequest request) throws Exception 
	{
		request.setCharacterEncoding("UTF-8"); 
        DiskFileItemFactory factory = new DiskFileItemFactory(); 
        factory.setSizeThreshold(1024 * 1024 * 2); 
        ServletFileUpload upload = new ServletFileUpload(factory); 
        upload.setSizeMax(-1); 
        List list = upload.parseRequest(request);  
		ebillExcelUploadForGrid(list);
		 
	}
	/** 
	 * 그리드 추가를 위한 엑셀 업로드
	 * @param list
	 * @throws Exception
	 */
	public void ebillExcelUploadForGrid(List list) throws Exception {
		String file_name = "";
		String file_path =  "/files/ebill/";
		
		Map sqlParam = new Map();
		
		for (int i=0,ii=list.size();i<ii;i++) {
            FileItem fileItem = (FileItem)list.get(i);
            
            if("FileData".equals(fileItem.getFieldName())) {
        		file_name = fileItem.getName();
            
            	File file = new File(DocumentProperties.get("REAL_PATH") + file_path + file_name);
            	File upDir = file.getParentFile();
            	
            	if(!upDir.isFile()&&!upDir.isDirectory()) {
            		upDir.mkdirs();
            	}
            	fileItem.write(file);
            }
		}
		
		System.out.println("ebillExcelUploadForGrid");
	}
	
	/**
	 * @param obj	파일정보
	 * @return
	 * @throws Exception
	 */
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	protected SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyyMMddhhmmss");
	public eswf.dataobject.List readSheetData(Map items) throws Exception {
		String file_name = (String)items.get("file_name");
		String excelHeaderInfo = (String)items.get("headerRows");
		eswf.dataobject.List headerColumns = (eswf.dataobject.List)items.get("headerColumns");
		eswf.dataobject.List objCache = new eswf.dataobject.List();
		 

		String uploadDir = DocumentProperties.get("REAL_PATH") + "/files/ebill/";
		
		int headerRows = Integer.parseInt(excelHeaderInfo); 
		
		String dataField, headerText = null;

		File file = new File( uploadDir, file_name);
		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
		HSSFWorkbook workbook = new HSSFWorkbook(new POIFSFileSystem(bin));
		
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		
        int rows = sheet.getPhysicalNumberOfRows();
        
        for (int r = 1; r < rows; r++) {
            HSSFRow row   = sheet.getRow(r);
            BUMapObject data = new BUMapObject();              

            if (row != null) { 
//            	int cells = row.getLastCellNum();
            	int cells = sheet.getRow(0).getLastCellNum();

            	for (short c = 0; c < cells; c++) {
            		dataField = (String)((Map)headerColumns.get(c)).get("dataField");	
                     HSSFCell cell  = row.getCell(c);
                     
                     if (cell != null) 
                     { 
                    	 CellStyle cellStyle = cell.getCellStyle();
                    	 String value = null;

                        switch (cell.getCellType()) 
                        {
							case HSSFCell.CELL_TYPE_FORMULA :
								value = cell.getCellFormula();
								break;
							case HSSFCell.CELL_TYPE_NUMERIC :
								String formatString = cellStyle.getDataFormatString();
								if(formatString.toUpperCase().indexOf("YY") >= 0) {
									if(formatString.toUpperCase().indexOf("HH") >= 0) {
										value = datetimeFormat.format(cell.getDateCellValue());
									} else {
										value = dateFormat.format(cell.getDateCellValue());
									}
								} else {
									value = dformat.format(cell.getNumericCellValue());
								}
								break;
							case HSSFCell.CELL_TYPE_STRING :
								value = cell.getStringCellValue(); //String
								break;
							case HSSFCell.CELL_TYPE_BLANK :
								value = null;
								break;
							case HSSFCell.CELL_TYPE_BOOLEAN :
								value = dformat.format(cell.getBooleanCellValue()); 
								break;
							case HSSFCell.CELL_TYPE_ERROR :
								value = dformat.format(cell.getErrorCellValue()); 
								break;
							default :
                         }
                        
                        data.put(dataField, value); 	
                     } 
                     else
                     {
                    	 data.put(dataField, null);
                     }
                 } 
                 objCache.add(data);
            }	
        }
        bin.close();
		boolean result = file.delete();
        
		
		return objCache;
	}
}