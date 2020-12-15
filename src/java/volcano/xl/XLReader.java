package volcano.xl;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.lang.String;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class XLReader {
	private List<HSSFWorkbook> workbooks = new ArrayList<HSSFWorkbook>();
	protected DecimalFormat dformat = new DecimalFormat("#########################0.#########");
	private static DiskFileItemFactory factory = new DiskFileItemFactory();
	private Map<String,String> parameters;
	
	private static DiskFileItemFactory getDiskFileItemFactory(){
		if(factory == null)
			factory = new DiskFileItemFactory();
		return factory;
	}

	public XLReader(HttpServletRequest request) throws IOException {
		parameters = new HashMap<String,String>();
		try {
			byte[] file = null;
			ServletFileUpload upload = new ServletFileUpload(getDiskFileItemFactory());
			upload.setHeaderEncoding("utf-8");
			List<FileItem> items = upload.parseRequest(request);
			Iterator<FileItem> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = iter.next();
				if (item.isFormField()) {
					parameters.put(item.getFieldName(),item.getString());
				}else{
					file = item.get();
				}
			}
			workbooks.add(new HSSFWorkbook(new POIFSFileSystem(
					new ByteArrayInputStream(file))));
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	
	public XLReader(InputStream is) throws IOException {
		try {
			workbooks.add(new HSSFWorkbook(new POIFSFileSystem(is)));
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public String getParameter(String key) {
		if(parameters == null) 
			return null;
		else
			return parameters.get(key);
	}

	private List<Map> readSheet(HSSFSheet sheet, int startRow, String[] fields, Class<? extends Map> classname) throws Exception {
		List<Map> result = new ArrayList<Map>();
		for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
			boolean isBreak = false;
			Map obj = classname.newInstance();
			HSSFRow row = sheet.getRow(i);
			HSSFCell cell = null;

			for (int j = 0; j < fields.length; j++) {
				cell = row.getCell(j);
				String indexString = fields[j];

				if (cell == null) {
					if (indexString != null)
						continue;
					else {
						isBreak = true;
						break;
					}
				}
				switch (cell.getCellType()) {
				case HSSFCell.CELL_TYPE_FORMULA:
					obj.put(indexString,cell.getCellFormula());
					break;
				case HSSFCell.CELL_TYPE_NUMERIC:
					obj.put(indexString, dformat.format(cell.getNumericCellValue()));
					break;
				case HSSFCell.CELL_TYPE_STRING:
					obj.put(indexString, cell.getRichStringCellValue().getString());
					break;
				case HSSFCell.CELL_TYPE_BOOLEAN:
					obj.put(indexString, String.valueOf(cell.getBooleanCellValue()));
					break;
				case HSSFCell.CELL_TYPE_ERROR:
					obj.put(indexString, String.valueOf(cell.getErrorCellValue()));
					break;
				default:
					obj.put(indexString, cell.getRichStringCellValue().getString());
				}
			}
			if (isBreak)
				break;
			result.add(obj);
		}
		return result;
	}
	
	public List<Map> readSheetData(int startRow, String[] fields, Class<? extends Map> classname) throws Exception{
		return readSheetData(0, startRow, fields, classname);
	}
	
	public List<List<Map>> readSheetData(int startRow, String[][] fields, Class<? extends Map>[] classnames) throws Exception{
		return readSheetData(0, startRow, fields, classnames); 
	}
	
	public List<Map> readSheetData(int fileIdx, int startRow, String[] fields, Class<? extends Map> classname) throws Exception{
		HSSFWorkbook workb = workbooks.get(fileIdx);
		return readSheet(workb.getSheetAt(0), startRow, fields, classname);
	}
	
	public List<List<Map>> readSheetData(int fileIdx, int startRow, String[][] fields, Class<? extends Map>[] classnames) throws Exception{
		List<List<Map>> results = new ArrayList<List<Map>>();
		HSSFWorkbook workb = workbooks.get(fileIdx);
		
		for(int i=0 ; i < classnames.length ; i++)
			results.add(readSheet(workb.getSheetAt(i), startRow, fields[i], classnames[i]));
		return results; 
	}

}
