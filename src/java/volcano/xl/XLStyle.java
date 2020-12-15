package volcano.xl;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DataFormat;

public class XLStyle
{
	public static String DEFAULT_STYLE_ID = "Default";
	
	private HSSFWorkbook workbook;
	private Map<String, XLStyleDefinable> definers = new HashMap<String, XLStyleDefinable>();
	private Map<String, HSSFCellStyle> styles = new HashMap<String, HSSFCellStyle>();
	private DataFormat dataFormat;
	
	public XLStyle(HSSFWorkbook workbook){
		this.workbook = workbook;
		this.dataFormat = workbook.createDataFormat();

			
		definers.put(XLStyle.DEFAULT_STYLE_ID, new XLStyleDefinable(){

			public HSSFCellStyle define(HSSFCellStyle style, HSSFFont font, int datatype, short align, String formatString, XLStyle xlStyle) {
				font.setFontHeightInPoints((short)9);
		        font.setColor(HSSFColor.BLACK.index);
		        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);    
		        font.setFontName("굴림");
		        font.setItalic(false);
		        font.setStrikeout(false);
		        
		        style.setWrapText(false);
				style.setAlignment(align);
		        style.setFont(font);
		        
		        if(formatString != null && !formatString.equals("")) {
					style.setDataFormat(xlStyle.getDataFormat().getFormat(formatString));
				}
		        
				return style;
			}
			
		});
	}
	
	public HSSFFont createFont(){
		return workbook.createFont();
	}
	
	public HSSFCellStyle createCellStyle(){
		return workbook.createCellStyle();
	}
	
	private String styleName(String id, int datatype, short align, String formatString){
		return id + "_" + datatype + "_" + align + "_" + formatString;
	}
	
	public HSSFCellStyle getStyle(String id, int datatype, short align, String formatString){
		String name = styleName(id, datatype, align, formatString);
		if(styles.containsKey(name))
			return styles.get(name);
		else{
			XLStyleDefinable sd = definers.get(id);
			HSSFCellStyle style = sd.define(this.createCellStyle(), this.createFont(), datatype, align, formatString, this);
			styles.put(name, style);
			return style; 
		}
	}
	
	public void setStyle(String id, XLStyleDefinable definer){
		if(id.equals(DEFAULT_STYLE_ID)) return;
		definers.put(id, definer);
	}
	
	public DataFormat getDataFormat() {
		return dataFormat;
	}
	
}