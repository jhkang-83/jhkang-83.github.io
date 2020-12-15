package volcano.xl;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;

public class XLCell
{
	private int mergeCell = 1;
	private int mergeRow= 1;
	private String style;
	private Object value;
	private int type;
	private short align = HSSFCellStyle.ALIGN_LEFT;
	private int actualColNum = 0;
	private int depth = 0;
	private String formatString = "";
	
	public XLCell(Object value, int datatype, String style, int mergeRow, int mergeCell, short align){
		this.value = value;
		this.type = datatype;
		this.style = style;
		this.align = align;
		setMergeCell(mergeCell);
		setMergeRow(mergeRow);
	}
	
	public XLCell(Object value, int datatype, String style, int mergeRow, int mergeCell, int actualColNum, int depth, short align) {
		this(value, datatype, style, mergeRow, mergeCell, actualColNum, depth, align, "");
	}
		
		
	public XLCell(Object value, int datatype, String style, int mergeRow, int mergeCell, int actualColNum, int depth, short align, String formatString) {
		this.value = value;
		this.type = datatype;
		this.style = style;
		this.align = align;
		this.actualColNum = actualColNum;
		this.depth = depth;
		this.formatString = formatString;
		setMergeCell(mergeCell);
		setMergeRow(mergeRow);
	}
	
	public XLCell(Object value, int datatype, String style){
		this.value = value;
		this.type = datatype;
		this.style = style;
	}
	
	public XLCell(Object value, String style){
		this(value, HSSFCell.CELL_TYPE_STRING, style);
	}
	
	public XLCell(Object value, int datatype){
		this(value, datatype, XLStyle.DEFAULT_STYLE_ID);
	}
	
	public XLCell(Object value){
		this(value, HSSFCell.CELL_TYPE_STRING, XLStyle.DEFAULT_STYLE_ID);
	}
	
	public void setMergeCell(int count){
		this.mergeCell = count > 1 ? count : 1;
	}
	
	public void setMergeRow(int count){
		this.mergeRow = count > 1 ? count : 1;
	}
	
	public int getMergeCell() {
		return mergeCell;
	}

	public int getMergeRow() {
		return mergeRow;
	}

	public String getStyle() {
		return style;
	}

	public Object getValue() {
		return value;
	}
	
	public short getAlign(){
		return align;
	}
	

	public int getType() {
		return type;
	}

	public int getActualColNum() {
		return actualColNum;
	}

	public int getDepth() {
		return depth;
	}
	
	public String getFormatString() {
		return formatString;
	}
}
