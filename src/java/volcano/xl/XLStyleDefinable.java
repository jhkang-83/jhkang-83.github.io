package volcano.xl;


import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;

public interface XLStyleDefinable
{
	public HSSFCellStyle define(HSSFCellStyle style, HSSFFont font, int datatype, short align, String formatString, XLStyle xlStyle);
}