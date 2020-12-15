package volcano.xl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFRegionUtil;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.CellRangeAddress;

public class XLConstructor {
	
	public static int DEFAULT_CELL_WIDTH = 20;
	private HSSFWorkbook workbook;
	private HSSFSheet sheet;
	private XLStyle styler;
	private int rowCnt = -1;
	private Map<Short, Short> colWidth;
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	protected SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyyMMddhhmmss");
	
	public XLConstructor(HSSFWorkbook workbook, HSSFSheet sheet, XLStyle styler) {
		this.workbook = workbook;
		this.sheet = sheet;
		this.colWidth = new HashMap<Short, Short>();
		this.styler = styler;
	}

	private HSSFRow createRow() {
		return sheet.createRow(++rowCnt);
	}

	private void setRowHeight(HSSFRow row, int height) {
		row.setHeight((short)(height / ((double) 1 / 20)));
	}

	private void setSheetColWidth(int colindex, int width) {
		sheet.setColumnWidth(colindex, (int)((width) / ((double) 1 / 256)));
	}

	public void construct(XLSection section) {
		boolean isPassedfirstRow = false;
		while (section.hasRow()) {
			XLRow xlrow = section.getRow();
			HSSFRow row = createRow();
			setRowHeight(row, xlrow.getHeight());
			while (xlrow.hasCell()) {
				XLCell xlcell = xlrow.getCell();
				if (!isPassedfirstRow) {
					if (colWidth.containsKey(xlrow.getIdx())) {
						setSheetColWidth(xlrow.getIdx(), colWidth
								.get(xlrow.getIdx()));
					} else {
						setSheetColWidth(xlrow.getIdx(), DEFAULT_CELL_WIDTH);
					}
				}
				HSSFCell cell = row.createCell(xlrow.getIdx(),	xlcell.getType());
				if (xlcell.getMergeCell() + xlcell.getMergeRow() > 2) {
					sheet.addMergedRegion(new Region(rowCnt, (short)xlrow.getIdx(),
							rowCnt + xlcell.getMergeRow() - 1, (short)(xlrow.getIdx()+ xlcell.getMergeCell() - 1)));
				}
				cell.setCellStyle(styler.getStyle(xlcell.getStyle(), xlcell
						.getType(), xlcell.getAlign(), ""));
				this.setValue(cell, xlcell.getType(), xlcell.getValue(), "");
			}
			isPassedfirstRow = true;
		}
	}
	
	public void construct1(XLSection section, boolean visibleTitle) {
		DataFormat dataFormat = workbook.createDataFormat();
		int baseRowGap = 0;
		boolean isPassedfirstRow = false;
		List rows = new ArrayList();
		
		if(!visibleTitle) baseRowGap = -1;
			
		while (section.hasRow()) {
			section.getRow();
			rows.add(createRow());
		}
		section.idx = -1;
		while (section.hasRow()) {
			XLRow xlrow = section.getRow();
			HSSFRow row = (HSSFRow)rows.get(section.idx);
			setRowHeight(row, xlrow.getHeight());
			while (xlrow.hasCell()) {
				XLCell xlcell = xlrow.getCell();
				if (!isPassedfirstRow) {
					if (colWidth.containsKey(xlrow.getIdx())) {
						setSheetColWidth(xlrow.getIdx(), colWidth
								.get(xlrow.getIdx()));
					} else {
						setSheetColWidth(xlrow.getIdx(), DEFAULT_CELL_WIDTH);
					}
				}
				HSSFCell cell = row.createCell(xlcell.getActualColNum(),	xlcell.getType());
				
				if (xlcell.getMergeCell() + xlcell.getMergeRow() > 2) {
					CellRangeAddress cellRangeAddress = new CellRangeAddress(xlcell.getDepth()+1+baseRowGap, xlcell.getDepth()+xlcell.getMergeRow()+baseRowGap,
							xlcell.getActualColNum(), xlcell.getActualColNum()+xlcell.getMergeCell()-1);

					sheet.addMergedRegion(cellRangeAddress);
					HSSFRegionUtil.setBorderBottom(CellStyle.BORDER_THIN, cellRangeAddress, sheet, workbook);
					HSSFRegionUtil.setBorderRight(CellStyle.BORDER_THIN, cellRangeAddress, sheet, workbook);
					HSSFRegionUtil.setBorderLeft(CellStyle.BORDER_THIN, cellRangeAddress, sheet, workbook);
					HSSFRegionUtil.setBorderTop(CellStyle.BORDER_THIN, cellRangeAddress, sheet, workbook);
				}
				HSSFCellStyle style = styler.getStyle(xlcell.getStyle(), xlcell.getType(), xlcell.getAlign(), xlcell.getFormatString());
				cell.setCellStyle(style);
				
				this.setValue(cell, xlcell.getType(), xlcell.getValue(), xlcell.getFormatString());
			}
			isPassedfirstRow = true;
		}
	}
	
	private void setValue(HSSFCell cell, int type, Object value, String formatString) {
		switch (type) {
		case HSSFCell.CELL_TYPE_STRING:
			if(value != null) {
				cell.setCellValue(new HSSFRichTextString(value.toString()));
			} else {
				cell.setCellValue(new HSSFRichTextString(""));
			}	
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			if(formatString.indexOf("YY") >= 0) {
				cell.setCellType(type);
				try {
					if(formatString.indexOf("HH") >= 0) {
						cell.setCellValue(datetimeFormat.parse(value.toString()));
					} else {
						cell.setCellValue(dateFormat.parse(value.toString()));
					}	
				} catch(ParseException pex) {
					cell.setCellValue("");
				}	
			} else {
				if (value == null || value.toString().trim().equals("")) {
					cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
				} else {
					cell.setCellValue(Double.parseDouble(value.toString().replace(",", "")));
				}
			}	
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			cell.setCellFormula(value.toString());
			break;
		}
	}

	public void setColumnWidths(Map<Short, Short> cws) {
		this.colWidth = cws;
	}

}
