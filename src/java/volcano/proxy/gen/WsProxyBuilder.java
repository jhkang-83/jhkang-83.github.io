package volcano.proxy.gen;

import java.io.FileOutputStream;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


public class WsProxyBuilder {
	private void viewInfo(String targetNm, String clazzFullPath, String outXmlPath , String callMethod, String className, String compCd, String[] classes) throws Exception {
		String sbClass = classes[0].replace("[$CLASS$]", targetNm);
		String fnClass = classes[1].replace("[$CLASS$]", targetNm);
		String frClass = classes[2].replace("[$CLASS$]", targetNm);
		
		
		Element stub = DocumentHelper.createElement("stub");
		stub.addAttribute("name", targetNm);
		stub.addAttribute("class", sbClass);
		
		Element method = stub.addElement("method").addAttribute("name", targetNm);
		Element argument = method.addElement("argument").addAttribute("ref", targetNm);
		Element returnEl = method.addElement("return").addAttribute("ref", targetNm+"Response");
		
		Element proxy = stub.addElement("proxy");
		proxy.addAttribute("clazz", "ws.sap."+className+"."+compCd+"."+targetNm+"SoapProxy");
		proxy.addAttribute("method", "get"+targetNm+"Soap");
		proxy.addAttribute("callmethod", callMethod);
				
				
		//request class	
		buildEl(fnClass, targetNm, stub, false, clazzFullPath);
		//response class
		buildEl(frClass, targetNm, stub, true, clazzFullPath);
		
		OutputFormat format = new OutputFormat("    ", true);
		format.setEncoding("UTF-8");
		format.setLineSeparator("\r\n");
		format.setTrimText(true);
		
		StringWriter sw = new StringWriter();
		XMLWriter xmlWr = new XMLWriter(sw, format);
		xmlWr.write(stub);

		String outputXml = sw.getBuffer().toString();
		
		xmlWr.setOutputStream( new FileOutputStream(outXmlPath) );
		xmlWr.write(stub);
		
	}
	
	public void buildEl(String fnClass, String targetNm, Element rootEl, boolean isResponse, String clazzFullPath) throws ClassNotFoundException {
		
		Element classEl = rootEl.addElement("class")
						  .addAttribute("name", targetNm +  (isResponse ? "Response" : ""))
						  .addAttribute("class", fnClass);
							
		Class reqCls = Class.forName(fnClass);		
		Constructor[] reqClsCons = reqCls.getDeclaredConstructors();

		String packagenm =  reqCls.getPackage().getName();
		Map<String, String[]> classMap = new LinkedHashMap<String, String[]>();
		for(int i=0;i<reqClsCons.length;i++) {
			Class[] paramClass = reqClsCons[i].getParameterTypes();
			
			if(paramClass.length>0) {
				String[] values = ParamInfo.returnParams(fnClass, clazzFullPath);
				for (int j = 0; j < paramClass.length; j++) {
					String simpleName  = paramClass[j].getSimpleName();
					
					Element propertyEl = classEl.addElement("property")
										.addAttribute("name", values[j])
										.addAttribute("alias", values[j].toLowerCase());
					
					if(simpleName.endsWith("[]")){
						String elName = "";
						simpleName = simpleName.replace("[]", "");

						propertyEl.addAttribute("collection", "Array");
						
						if(isResponse){
							elName = simpleName + "Response";
							propertyEl.addAttribute("ref", simpleName + "Response");
							classMap.put(elName, new String[]{packagenm + "." + simpleName, simpleName});
						} else {
							elName = simpleName;
							propertyEl.addAttribute("ref", simpleName);
							classMap.put(elName, new String[]{packagenm + "." + simpleName, simpleName});
						}
					} else {
						propertyEl.addAttribute("class", paramClass[j].getName());
					}
				}
			}
		}
		
		Iterator itr = classMap.keySet().iterator();
		while(itr.hasNext()) {
			String key = itr.next().toString();
			String[] clazzInfo = classMap.get(key);
			buildEl(clazzInfo[0], clazzInfo[1], rootEl, isResponse, clazzFullPath);
		}
	}
	
	public static void main(String[] args) {
		WsProxyBuilder st = new WsProxyBuilder();
		try {
			
			String[] classes = new String[3];
			classes[0] = args[0];
			classes[1] = args[1];
			classes[2] = args[2];
			
			st.viewInfo(args[3], args[4], args[5], args[6], args[7], args[8], classes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
