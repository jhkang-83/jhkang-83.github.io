package volcano.xl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Element;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;

import eswf.dataobject.List;
import eswf.dataobject.Map;
import eswf.exception.FoundationException;
import eswf.exception.GeneralException;
import eswf.jdbc.ResultSetList;
import eswf.managers.ServiceManager;
import eswf.service.FunctionDistributor;
import eswf.service.ServiceDefinition;
import eswf.util.XMLUtils;
import org.apache.log4j.Logger;
/**
 * Service ID를 호출하여 대용량 엑셀 Export 
 * 
 * @author EMRO(bonghwan.oh)
 * @version 2012-10-10
 * @since eswf 1.0
 */
public class LargeExcelExporter extends ResultSetList {
	
	protected static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
	private static Logger log = Logger.getLogger(LargeExcelExporter.class);
	private OutputStreamWriter writer;
	private Map data;
	private int totalRowNum = 0;
	private Function cellDataFunction; 
	private int headerRowCount;
	private int mergeCount = 0;
	private Styler styler; 
		
	private LinkedHashMap dataHeaders = new LinkedHashMap<String, Object>();
	
	public LargeExcelExporter(Map data) 
		throws FoundationException {
		this.data = data;
		this.data.put("largeExcelExporter", this);
	}
	
	public void appendResultObject(java.util.Map m) 
		throws FoundationException {
		try {
			writer.write("<row r=\""+(totalRowNum+headerRowCount+1)+"\">\n");
			
			Iterator it = dataHeaders.entrySet().iterator();
			int i = 0;
			while (it.hasNext()) {
				Map.Entry<String, String> e = (Map.Entry) it.next();
				String key = e.getKey();
				createCell(i, key, (String)m.get(key), m);
				i++;
			}
			writer.write("</row>");
			writer.flush();
			totalRowNum++;
		} catch(Exception e) {
			throw new GeneralException(e); 
		}	
	}
	
	public void terminateResultSet() 
		throws FoundationException {
		try {
			
		} catch(Exception e) {
			throw new GeneralException(e); 
		} finally {
		}	
	}
	
	private void composeHeader(List<List> headerRows, List headers)
		throws FoundationException {
		for (int i = 0; i < headers.size(); i++) {
			Map h = new Map(headers.get(i));
			int depth = Integer.parseInt(h.get("depth").toString());
			headerRows.get(depth).add(h);
			
			if (h.get("children") != null) {
				composeHeader(headerRows, new List(h.get("children")));
			} else {
				String dataField = h.get("dataField").toString();
				dataHeaders.put(dataField, h);
			}
		}
	}
	
	private void createHeader(List<List> headerRows)
		throws FoundationException {
		try {
			for (int i = 0; i < headerRows.size(); i++) {
				writer.write("<row r=\""+(i+1)+"\">\n");
				for (int j = 0; j < headerRows.get(i).size(); j++) {
					Map h = (Map)headerRows.get(i).get(j);
					String label = h.get("label").toString();
					if (label.equals("null")) {
						label = "";
					}
					int actualColNum = Integer.parseInt(h.get("actualColNum").toString());
					int depth = Integer.parseInt(h.get("depth").toString());
					
					int styleIndex = styler.getStyleIndex("HEADER", h);
					
					String ref = new CellReference(depth, actualColNum).formatAsString();
			        writer.write("<c r=\""+ref+"\" t=\"inlineStr\"");
			        if(styleIndex != -1) writer.write(" s=\""+styleIndex+"\"");
			        writer.write(">");
			        writer.write("<is><t>"+XMLUtils.exchangeNamedEntity(label)+"</t></is>");
			        writer.write("</c>");
			        mergeCount++;
			    }	
				writer.write("</row>");
				writer.flush();
			}
		} catch(IOException e) {
			throw new GeneralException(e); 
		} 	
	}
	
	private void createCell(int columnIndex, String key, String value1, java.util.Map m)
		throws FoundationException {
		try {
			Map dataHeader = (Map)dataHeaders.get(key);
			int styleIndex = styler.getStyleIndex("CONTENT", dataHeader);
			
			String formatString = dataHeader.get("formatString").toString();
			int dataType = Integer.parseInt(dataHeader.get("dataType").toString());
			
			
			String value = "";
			
			if( value1 == null )
				value = "";
			else
				value = value1;
			
			if(cellDataFunction != null) {
				Object[] args = { m, key };
				value = (String)cellDataFunction.call(Context.getCurrentContext(), ScriptableObject.getTopLevelScope(cellDataFunction), cellDataFunction, args);
			}
			String ref = new CellReference(totalRowNum+headerRowCount, columnIndex).formatAsString();
			
			switch (dataType) {
				case HSSFCell.CELL_TYPE_STRING:
					writer.write("<c r=\""+ref+"\" t=\"inlineStr\"");
			        if(styleIndex != -1) writer.write(" s=\""+styleIndex+"\"");
			        writer.write(">");
			        writer.write("<is><t>"+XMLUtils.exchangeNamedEntity(value)+"</t></is>");
			        writer.write("</c>");
			        break;
				case HSSFCell.CELL_TYPE_NUMERIC:
					writer.write("<c r=\""+ref+"\" t=\"n\"");
			        if(styleIndex != -1) writer.write(" s=\""+styleIndex+"\"");
			        writer.write(">");
			        if(formatString.indexOf("YY") >= 0) {
			        	if(value != null && !value.equals("")) {
			        		writer.write("<v>"+DateUtil.getExcelDate(dateFormat.parse(value))+"</v>");
			        	}	
			        } else {
			        	writer.write("<v>"+value+"</v>");
			        }	
			        writer.write("</c>");
			        break;
			}    
		} catch(IOException e) {
			throw new GeneralException(e); 
		}  catch(ParseException e) {
			throw new GeneralException(e); 
		} finally {
		}	
    }
	
	private void createMerge(List<List> headerRows)
		throws FoundationException {
		try {
			writer.write("<mergeCells count=\""+(mergeCount)+"\">\n");
			for (int i = 0; i < headerRows.size(); i++) {
				for (int j = 0; j < headerRows.get(i).size(); j++) {
					Map h = (Map)headerRows.get(i).get(j);
					
					int actualColNum = Integer.parseInt(h.get("actualColNum").toString());
					int depth = Integer.parseInt(h.get("depth").toString());
					int rowSpan = Integer.parseInt(h.get("rowSpan").toString());
					int colSpan = Integer.parseInt(h.get("colSpan").toString());
					
					
					String refBegin = new CellReference(depth, actualColNum).formatAsString();
					String refEnd = new CellReference(depth+rowSpan-1, actualColNum+colSpan-1).formatAsString();
			        writer.write("<mergeCell ref=\""+refBegin+":"+refEnd+"\"");
			        writer.write("/>");
			    }	
			}
			writer.write("</mergeCells>");
			writer.flush();
		} catch(IOException e) {
			throw new GeneralException(e); 
		} 	
	}

	private void composeStyle(XSSFWorkbook wb, List<List> headerRows) {
		styler = new Styler(wb);
        
        for (int i = 0; i < headerRows.size(); i++) {
			for (int j = 0; j < headerRows.get(i).size(); j++) {
				Map h = (Map)headerRows.get(i).get(j);
				String align = h.get("align").toString();
				String formatString = h.get("formatString").toString();
				int dataType = Integer.parseInt(h.get("dataType").toString());
				styler.createStyles("HEADER", dataType, styler.castAlign(align), formatString);
				
				if (h.get("children") == null && h.get("dataField") != null) {
					styler.createStyles("CONTENT", dataType, styler.castAlign(align), formatString);
				}
				
				
		    }	
		}
    }
	
	public void exportExcel(OutputStream os) 
		throws FoundationException {
		try {
			
			List headerRows = new List();
			headerRowCount = Integer.parseInt(data.get("headerRowCount").toString());
			for (int i = 0; i < headerRowCount; i++) {
				headerRows.add(new List());
			}
			List headers = new List(data.get("headers"));
			composeHeader(headerRows, headers);
			 
			
			
			ZipOutputStream zos = new ZipOutputStream(os);
			writer = new OutputStreamWriter(zos, "UTF-8");
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XSSFWorkbook wb = new XSSFWorkbook();
	        XSSFSheet sheet = wb.createSheet("Big Grid");
	        
	        composeStyle(wb, headerRows);
	        
	        wb.write(baos);
	        byte[] byteWorkBook = baos.toByteArray();
	        baos.close();
	        
	        ByteArrayInputStream bais = new ByteArrayInputStream(byteWorkBook);
	        ZipInputStream zis = new ZipInputStream(bais);
	        ZipEntry ze;
	        while ((ze = zis.getNextEntry()) != null) {
	            if(!ze.getName().equals("xl/worksheets/sheet1.xml")){
	            	zos.putNextEntry(ze);
	            	int size;
	                byte[] buffer = new byte[2048];
	                while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
	                	zos.write(buffer, 0, size);
	                }
	            }
	        }
	        zis.close();
	        bais.close();
	        
	        
			zos.putNextEntry(new ZipEntry("xl/worksheets/sheet1.xml"));
			
			writer.write("<?xml version=\"1.0\" encoding=\""+"UTF-8"+"\"?>");
			writer.write("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">" );
			writer.write("<sheetData>\n");
	        
			createHeader(headerRows);
			
			executeService(); 
			
			writer.write("</sheetData>");
			
			createMerge(headerRows);
			
			writer.write("</worksheet>");
            
			writer.close();
			zos.close();
			writer = null;
		} catch(IOException e) {
			throw new GeneralException(e); 
		} finally {
		}	
		
	}
	
	public void executeService() 
		throws FoundationException {
		String descriptor = this.data.get("__descriptor").toString();
		String serviceId = this.data.get("__serviceId").toString();
		Element sElement = ServiceManager.getInstance().getService(descriptor, serviceId);
		eswf.dataobject.Map properties = new eswf.dataobject.Map();
		properties.put("descriptor", descriptor);
		FunctionDistributor.getInstance().start(new ServiceDefinition(sElement, data), data, properties);
	}
	
	public void test1(Object o, Object thisObj)
		throws Exception {
		Function fc = (Function)o;
		fc.call(Context.getCurrentContext(), ScriptableObject.getTopLevelScope(fc), fc, null);
		
	}
	
	public void setCellDataFunction(Object o) {
		cellDataFunction = (Function)o;
	}
	
	class StyleDefine {
		public XSSFCellStyle createStyle(XSSFCellStyle style, XSSFFont font, int datatype, short align, String formatString) {
			return null;
		}
	}
	
	class Styler {
		
		public Map defineStyles = new Map(); 
		public Map styles = new Map(); 
		private XSSFWorkbook wb;
		private XSSFDataFormat fmt;
		
		public Styler(XSSFWorkbook wb) {
			this.wb = wb;
			this.fmt = wb.createDataFormat();
			this.createDefineStyle();
	    }
		
		public void createDefineStyle() {
			defineStyles.put("HEADER", new StyleDefine() {
	        	public XSSFCellStyle createStyle(XSSFCellStyle style, XSSFFont font, int datatype, short align, String formatString) {
	        		font.setFontHeightInPoints((short) 9);
	        		font.setColor(HSSFColor.BLACK.index);
	        		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
	        		font.setFontName("굴림");
	        		font.setItalic(false);
	        		font.setStrikeout(false);
	        		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
	        		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
	        		style.setFont(font);
	        		return style;
	        	}
	        });
	        
	        defineStyles.put("CONTENT", new StyleDefine() {
	        	public XSSFCellStyle createStyle(XSSFCellStyle style, XSSFFont font, int datatype, short align, String formatString) {
	        		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
					style.setAlignment(align);
					style.setWrapText(false);
					style.setDataFormat(fmt.getFormat(formatString));
	                return style;
	        	}
	        });
		}
		
		private String styleId(String defineStyleId, int datatype, short align, String formatString){
			return defineStyleId + "_" + datatype + "_" + align + "_" + formatString;
		}
		
		public void createStyles(String defineStyleId, int datatype, short align, String formatString) {
			String styleId = styleId(defineStyleId, datatype, align, formatString);
			if(!styles.containsKey(styleId)) {
				XSSFCellStyle style = wb.createCellStyle();
				XSSFFont font = wb.createFont();
				style = ((StyleDefine)defineStyles.get(defineStyleId)).createStyle(style, font, datatype, align, formatString);
				styles.put(styleId, style);
			}	
		}
		
		public XSSFCellStyle getStyle(String defineStyleId, int datatype, short align, String formatString) {
			String styleId = styleId(defineStyleId, datatype, align, formatString);
			return (XSSFCellStyle)styles.get(styleId);
		}
		
		public int getStyleIndex(String defineStyleId, int datatype, short align, String formatString) {
			String styleId = styleId(defineStyleId, datatype, align, formatString);
			return ((XSSFCellStyle)styles.get(styleId)).getIndex();
		}
		
		public int getStyleIndex(String defineStyleId, Map header) {
			short align = castAlign(header.get("align").toString());
			String formatString = header.get("formatString").toString();
			int dataType = Integer.parseInt(header.get("dataType").toString());
			
			String styleId = styleId(defineStyleId, dataType, align, formatString);
			return ((XSSFCellStyle)styles.get(styleId)).getIndex();
		}
		
		public short castAlign(Object align){
			if(align == null)
				return XSSFCellStyle.ALIGN_LEFT;
			if("center".equalsIgnoreCase(align.toString()))
				return XSSFCellStyle.ALIGN_CENTER;
			else if("right".equalsIgnoreCase(align.toString()))
				return XSSFCellStyle.ALIGN_RIGHT;
			else 
				return XSSFCellStyle.ALIGN_LEFT;
				
		}
	}
}

