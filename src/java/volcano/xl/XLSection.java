package volcano.xl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class XLSection 
{
	private List<XLRow> rows;
	public int idx = -1;
	
	public XLSection(){
		rows = new ArrayList<XLRow>();
	}
	
	public XLSection(XLRow row){
		this();
		this.addRow(row);
	}
	
	public XLSection(List<XLRow> rows){
		this.rows = rows;
	}
	
	public XLSection(String[][] values, String style){
		this();
		for(int i=0 ; i < values.length ; i++){
			this.rows.add(new XLRow(values[i],style));
		}
	}
	
	public XLSection(String[][] values, String style[]){
		this();
		for(int i=0 ; i < values.length ; i++){
			this.rows.add(new XLRow(values[i],style[i]));
		}
	}

	public void addRow(XLRow row){
		this.rows.add(row);
	}

	public XLRow getRow(){
		if(rows.size() > (idx + 1)){
			return rows.get(++idx); 
		}
		return null;
	}
	
	public boolean hasRow(){
		return rows.size() > (idx + 1);
	}
}
