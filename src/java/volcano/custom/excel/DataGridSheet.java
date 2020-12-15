package volcano.custom.excel;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;

import volcano.xl.XLCell;
import volcano.xl.XLRow;
import volcano.xl.XLSection;
import volcano.xl.XLSheetable;
import volcano.xl.XLStyle;
import volcano.xl.XLStyleDefinable;
import eswf.dataobject.List;

public class DataGridSheet extends XLSheetable {
	private int headerColCount = 0;
	private int headerRowCount = 0;
	private List<XLRow> headerRows = new List<XLRow>();
	private List<ColumnInfo> dataColumns = new List<ColumnInfo>();
	
	public DataGridSheet(String title, int headerRowCount, int headerColCount, List<Map> fields, List<Map> datas, boolean visibleTitle) {
		super.setVisibleTitle(visibleTitle);
		
		this.headerRowCount = headerRowCount;
		this.headerColCount = headerColCount;
		for (int i = 0; i < headerRowCount; i++) {
			headerRows.add(new XLRow());
		}
		composeHeader(fields);
		if(visibleTitle) {
			makeTitleSection(title);
		}	
		addSection(new XLSection(headerRows));
		makeDataSection(datas);
	}

	protected void calculateHeader(List<Map> fields, int depth) {
		for (int i = 0; i < fields.size(); i++) {
			Map h = fields.get(i);
			if (h.get("children") != null) {
				calculateHeader((List<Map>) h.get("children"), ++depth);
			} else {
				headerColCount++;
			}
		}
		if (headerRowCount < depth) {
			headerRowCount = depth;
		}
	}

	protected void composeHeader(List<Map> fields) {
		for (int i = 0; i < fields.size(); i++) {
			Map h = fields.get(i);
			XLRow row = headerRows.get((Integer) h.get("depth"));
			String label = h.get("label").toString();
			if (label.equals("null")) {
				label = "";
			}
			row.addCell(new XLCell(label, HSSFCell.CELL_TYPE_STRING, 
					"HEADER", 
					(Integer) h.get("rowSpan"), 
					(Integer) h.get("colSpan"), 
					(Integer) h.get("actualColNum"),
					(Integer) h.get("depth"),
					HSSFCellStyle.ALIGN_CENTER));
			if (h.get("children") != null) {
				composeHeader((List<Map>) h.get("children"));
			} else {
				if (h.get("dataField") != null) {
					if (!h.get("dataField").toString().equals("null")) {
						ColumnInfo ci = new ColumnInfo(h.get("dataField").toString(),h.get("align").toString(), h.get("formatString").toString(), h.get("dataType").toString());
						dataColumns.add(ci);
					}
				}
			}	
			
			
		}
	}

	public void makeTitleSection(String title) {
		if (title == null)
			return;
		XLCell titleCell = new XLCell(title, HSSFCell.CELL_TYPE_STRING,
				"TITLE", 1, headerColCount, 0, -1, HSSFCellStyle.ALIGN_CENTER);
		addSection(new XLSection(new XLRow(titleCell)));
	}

	public void makeDataSection(List<Map> datas) {
		XLSection dataSection = new XLSection();
		int sizeOfList = datas.size();
		int sizeOfKey = dataColumns.size();
		for (int i = 0; i < sizeOfList; i++) {
			Map bo = datas.get(i);
			XLRow row = new XLRow();
			for (int j = 0; j < sizeOfKey; j++) {
				row.addCell(new XLCell(bo.get(dataColumns.get(j).getDataField()),
						Integer.parseInt(dataColumns.get(j).getDataType()), "CONTENT",1,1,j,0,castAlign(dataColumns.get(j).getAlign()),dataColumns.get(j).getFormatString()));
			}
			dataSection.addRow(row);
		}
		addSection(dataSection);
	}
	
	private short castAlign(Object align){
		if(align == null)
			return HSSFCellStyle.ALIGN_LEFT;
		if("center".equalsIgnoreCase(align.toString()))
			return HSSFCellStyle.ALIGN_CENTER;
		else if("right".equalsIgnoreCase(align.toString()))
			return HSSFCellStyle.ALIGN_RIGHT;
		else 
			return HSSFCellStyle.ALIGN_LEFT;
			
	}
	
	public void init(XLStyle style) {
		XLStyle style1 = style;
		style.setStyle("TITLE", new XLStyleDefinable() {
			public HSSFCellStyle define(HSSFCellStyle style, HSSFFont font,
					int datatype, short align, String formatString, XLStyle xlStyle) {
				font.setFontHeightInPoints((short) 20);
				font.setColor(HSSFColor.BLACK.index);
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				font.setFontName("굴림");
				font.setItalic(false);
				font.setStrikeout(false);
				style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				style.setFont(font);
				
				return style;
			}
		});

		style.setStyle("HEADER", new XLStyleDefinable() {
			public HSSFCellStyle define(HSSFCellStyle style, HSSFFont font,
					int datatype, short align, String formatString, XLStyle xlStyle) {
				font.setFontHeightInPoints((short) 9);
				font.setColor(HSSFColor.BLACK.index);
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				font.setFontName("굴림");
				font.setItalic(false);
				font.setStrikeout(false);
				style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
				style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				style.setFont(font);
				
				style.setBorderBottom(CellStyle.BORDER_THIN);
		        style.setBorderRight(CellStyle.BORDER_THIN);
		        style.setBorderLeft(CellStyle.BORDER_THIN);
		        style.setBorderTop(CellStyle.BORDER_THIN);
				return style;
			}
		});

		style.setStyle("COMMENT", new XLStyleDefinable() {
			public HSSFCellStyle define(HSSFCellStyle style, HSSFFont font,
					int datatype, short align, String formatString, XLStyle xlStyle) {
				font.setFontHeightInPoints((short) 9);
				font.setColor(HSSFColor.BLACK.index);
				font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
				font.setFontName("굴림");
				font.setItalic(false);
				font.setStrikeout(false);
				style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				style.setFont(font); // 스타일에 적용
				
				return style;
			}
		});

		style.setStyle("CONTENT", new XLStyleDefinable() {
			public HSSFCellStyle define(HSSFCellStyle style, HSSFFont font,
					int datatype, short align, String formatString, XLStyle xlStyle) {
				style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				style.setAlignment(align);
				style.setWrapText(false);
				
				if(formatString != null && !formatString.equals("")) {
					style.setDataFormat(xlStyle.getDataFormat().getFormat(formatString));
				}	
				return style;
			}
		});
	}
}

class ColumnInfo {
	
	private String dataField;
	private String align;
	private String formatString;
	private String dataType;
	
	public ColumnInfo(String dataField, String align, String formatString, String dataType) {
		super();
		this.align = align;
		this.dataField = dataField;
		this.formatString = formatString;
		this.dataType = dataType;
	}
	public String getDataField() {
		return dataField;
	}
	public String getAlign() {
		return align;
	}
	public String getFormatString() {
		return formatString;
	}
	public String getDataType() {
		return dataType;
	}
}
