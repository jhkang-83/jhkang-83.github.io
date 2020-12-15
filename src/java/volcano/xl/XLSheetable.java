package volcano.xl;

import java.util.ArrayList;
import java.util.List;

public abstract class XLSheetable {
	private List<XLSection> sections = new ArrayList<XLSection>();
	private String sheetName = "undefined";
	private boolean visibleTitle = true;
	
	public List<XLSection> getSections() {
		return sections;
	}

	protected void addSection(XLSection section) {
		sections.add(section);
	}

	public String getName() {
		return sheetName;
	}

	public void setName(String name) {
		sheetName = name;
	}

	public abstract void init(XLStyle style);
	
	public boolean getVisibleTitle() {
		return visibleTitle;
	}

	public void setVisibleTitle(boolean value) {
		visibleTitle = value;
	}

}