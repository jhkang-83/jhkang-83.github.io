package volcano.wsproxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

public class WSStubFactory extends WSClassFactory implements IWSStubFactory<Element>{

	public WSStub createStub(Element e, IWSContext context) {
		WSClass clazz = super.createClass(e, context);
		List<Element> classes = e.elements("class");
		Map<String,WSClass> wsclasses = new HashMap<String,WSClass>(classes.size());
		for (Element c : classes) {
			WSClass o = super.createClass(c, context);
			wsclasses.put(o.getName(), o);
		}
		WSStub stub = new WSStub(clazz, wsclasses);
		return stub;
	}
}
