package volcano.wsproxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

public class WSClassFactory implements IWSFactory<Element> {

	public WSArgument createArgument(Element e, IWSContext context) {
		WSArgument arg = new WSArgument(context, e.attributeValue("ref"),
				e.attributeValue("class"));
		return arg;
	}

	public WSClass createClass(Element e, IWSContext context) {
		String name = e.attributeValue("name");
		String clazz = e.attributeValue("class");
		List<Element> methods = e.elements("method");
		Map<String,WSMethod> wsmethods = new HashMap<String,WSMethod>(methods.size());
		for (Element m : methods) {
			WSMethod o = createMethod(m, context);
			wsmethods.put(o.getName(), o);
		}
		List<Element> properties = e.elements("property");
		Map<String,WSProperty> wsproperties = new HashMap<String,WSProperty>(properties.size());
		for (Element p : properties) {
			WSProperty o = createProperty(p, context);
			wsproperties.put(o.getName(), o);
		}
		WSClass wsclass = new WSClass(context, clazz, name, wsmethods, wsproperties);
		return wsclass;
	}

	public WSMethod createMethod(Element e, IWSContext context) {
		String name = e.attributeValue("name");
		List<Element> arguments = e.elements("argument");
		List<WSArgument> wsarguments = new ArrayList<WSArgument>(arguments.size());
		for (Element arg : arguments) {
			wsarguments.add(createArgument(arg, context));
		}
		WSReturn ret = createReturn(e.element("return"), context);
		WSMethod method = new WSMethod(context, name, wsarguments, ret);
		return method;
	}

	public WSProperty createProperty(Element e, IWSContext context) {
		WSProperty property = new WSProperty(context, e.attributeValue("ref"),
				e.attributeValue("class"), e.attributeValue("name"),
				e.attributeValue("alias"), e.attributeValue("collection"));
		return property;
	}

	public WSReturn createReturn(Element e, IWSContext context) {
		WSReturn ret = new WSReturn(context, e.attributeValue("ref"),
				e.attributeValue("class"));
		return ret;
	}

}
