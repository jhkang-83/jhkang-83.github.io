package volcano.wsproxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Node;

import eswf.description.ElementXMLDescriptor;
import eswf.managers.XMLManager;

public class WSProxyAgency {
	
	public Object execute(String stubName, String method, Object[] arguments) throws Throwable {
		ElementXMLDescriptor exd = XMLManager.getInstance().getDescriptor("ws-proxy");
		// define
		Element define = exd.getDescriptorElement("define");
		Node n = define.selectSingleNode("stub[@name='"+stubName+"']");
		// bridge class
		Class<IWSBridge> bridgeClass = (Class<IWSBridge>) Class.forName(exd.getDescriptorElement("bridge-class").getTextTrim());
		IWSBridge bridge = bridgeClass.newInstance();
		Map<String,IWSListBridge> cb = new HashMap<String, IWSListBridge>();
		// collection-bridge-classes
		List<Element> collectionBridgeClasses = exd.getDescriptorElement("list-bridge-classes").elements();
		for(Element cbc : collectionBridgeClasses){
			Class<IWSListBridge> cbClass = (Class<IWSListBridge>) Class.forName(cbc.getTextTrim());
			cb.put(cbc.getName(), cbClass.newInstance());
		}
		Class defaultPropertyClass = Class.forName(exd.getDescriptorElement("default-property-class").getTextTrim());
		
		
		Element wsEl = (Element)n;
		WSStubFactory factory = new WSStubFactory();
		WSContext context = new WSContext();
		WSStub stub = factory.createStub(wsEl, context);
		
		Element proxyEl = wsEl.element("proxy");
		if(proxyEl!=null) {
			List<Attribute> proxyAtts = proxyEl.attributes();
			for (Attribute attribute : proxyAtts) {
				if(attribute.getName().equals("clazz")) bridge.setProxyClazz(attribute.getValue());
				else if(attribute.getName().equals("method"))  bridge.setProxyMethod(attribute.getValue());
				else if(attribute.getName().equals("callmethod")) stub.setCallMethod(attribute.getValue());
			}
		}
		
		context.prepare(bridge, cb, stub, defaultPropertyClass);
		try{
			Object r = stub.invoke(method, arguments);
			bridge.success(stubName, method, arguments, r);
			return r;
		}catch(Throwable t){
			bridge.fault(stubName, method, arguments, t);
			throw t;
		}
	}
}
