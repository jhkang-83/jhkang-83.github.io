package volcano.xl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;

public class XLRow
{
	private List<XLCell> cells;
	private int idx = -1;
	private int total = -1;
	private int height;
	
	public XLRow(){
		this(20);
	}
	
	public XLRow(int height){
		this.cells = new ArrayList<XLCell>();
		this.height = height;
	}
	
	public XLRow(XLCell cell){
		this();
		this.addCell(cell);
	}
	
	public XLRow(List<XLCell> cells){
		this(cells, 20);
	}
	
	public XLRow(List<XLCell> cells, int height){
		this.height = height;
		this.cells = cells;
	}
	
	public XLRow(String[] idxs, Map<String, ?> cells, String style, int height){
		this();
		for(int i=0 ; i < idxs.length ; i++){
			addCell(new XLCell(cells.get(idxs[i]),HSSFCell.CELL_TYPE_STRING,style));
		}
	}
	
	public XLRow(String[] idxs, Map<String, ?> cells, String style){
		this(idxs, cells, style, 20);
	}
	
	
	public XLRow(String[] values, String style, int height){
		this();
		for(int i=0 ; i < values.length ; i++){
			addCell(new XLCell(values[i],HSSFCell.CELL_TYPE_STRING,style));
		}
	}
	
	public XLRow(String[] values, String style){
		this(values, style, 20);
	}
	
	public XLRow(String[] idxs, Map<String, ?> cells, String style, Map<String, Integer> specifyCellTypes, int height){
		this();
		for(int i=0 ; i < idxs.length ; i++){
			if(!specifyCellTypes.containsKey(idxs[i]))
				addCell(new XLCell(cells.get(idxs[i]),HSSFCell.CELL_TYPE_STRING,style));
			else
				addCell(new XLCell(cells.get(idxs[i]),specifyCellTypes.get(idxs[i]),style));
		}
	}
	
	public XLRow(String[] idxs, Map<String, ?> cells, String style, Map<String, Integer> specifyCellTypes){
		this(idxs, cells, style, specifyCellTypes, 20);
		
	}
	
	public XLRow(String[] values, String style, int[] specifyCellTypes, int height){
		this();
		for(int i=0 ; i < values.length ; i++){
			addCell(new XLCell(values[i], specifyCellTypes[i], style));
		}
	}
	
	public XLRow(String[] values, String style, int[] specifyCellTypes){
		this(values, style, specifyCellTypes, 20);
	}

	public void addCell(XLCell cell){
		if(cell == null) return;
		total += cell.getMergeCell();
		this.cells.add(cell);
	}

	public XLCell getCell(){
		return cells.get(++idx);
	}
	
	public boolean hasCell(){
		return cells.size() > (idx + 1);
	}
	
	public int getIdx(){
		int cellidx = 0;
		for(int i=0 ; i < idx ; i++){
			XLCell c = cells.get(i);
			cellidx += c.getMergeCell();
		}
		return cellidx;
	}
	
	public int getHeight(){
		return height;
	}
}
