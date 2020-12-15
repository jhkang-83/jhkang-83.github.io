package volcano.wsproxy.service.assist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eswf.service.XMLCodeAssist;

public class WSProxyAdapterAssist implements XMLCodeAssist {
	
	private Map<String, XMLCodeAssist> children = new HashMap<String, XMLCodeAssist>();
	private Set<String> attributes = new HashSet<String>();
	private Set<String> tags = new HashSet<String>();
	
	public WSProxyAdapterAssist(){
		tags.add("then");
		tags.add("else");
		attributes.add("expression");
	}
	
	public Set<String> getAllowTagNames() {
		return tags;
	}

	public Set<String> getAttributes() {
		return attributes;
	}

	public Map<String, XMLCodeAssist> getChildCodeAssists() {
		return children;
	}

	public String getPostCode(String alias) {
		StringBuffer code = new StringBuffer();
		code.append("</");
		code.append(alias);
		code.append(">");
		return code.toString();
	}

	public String getPreCode(String alias, Map<String, String> attributeValues) {
		StringBuffer code = new StringBuffer();
		code.append("<");
		code.append(alias);
		code.append(" expression=\"");
		if (attributeValues != null)
			code.append(attributeValues.get("expression"));
		code.append("\">");
		return code.toString();
	}

}
