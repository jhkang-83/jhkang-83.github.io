package volcano.wsproxy.service.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import volcano.wsproxy.WSProxyAgency;
import volcano.wsproxy.service.assist.WSProxyAdapterAssist;
import eswf.exception.FoundationException;
import eswf.exception.GeneralException;
import eswf.service.IFunction;
import eswf.service.ServiceDefinition;
import eswf.service.XMLCodeAssist;
/**
 * expression 속성이 'true' 값을 갖게 되면 자식 엘리멘트들을 실행시킴 
 * <pre>
 * <table border="1">
 * <caption>속성</caption>
 * <tr><td><code style="color:#00C">expression</code></td><td>실행여부 <code style="color:#C00">true</code> 값의 경우 실행</td></tr> 
 * </table> 
 * </pre>
 * @author EMRO(jaehun.ko)
 * @version 2009-09-29
 * @see eswf.service.IFunction
 * @since eswf 1.0
 */
public class WSProxyAdapter implements IFunction {
	private static final String STUB_ATTRIBUTE = "stub";
	private static final String METHOD_ATTRIBUTE = "method";
	private static final String INPUT_NODE = "input";
	private static final String OUTPUT_NODE = "output";
	private static final String INPUT_NODE_VALUE_ATTRIBUTE = "value";
	private static final String OUTPUT_NODE_TARGET_ATTRIBUTE = "target";
	private static final String OUTPUT_NODE_PROPERTY_ATTRIBUTE = "property";
	protected static Logger logger;
	
	protected WSProxyAgency agency = new WSProxyAgency();
	
	private void logFn(String stb){
		
	}
	
	private static XMLCodeAssist assist = new WSProxyAdapterAssist();
	
	public XMLCodeAssist getCodeAssist() {
		return assist;
	}

	public WSProxyAdapter() {
		if (logger == null)
			logger = Logger.getLogger(this.getClass());
	}

	public IFunction factory() {
		return new WSProxyAdapter();
	}

	public void call(ServiceDefinition element, Map param,
			Map properties) throws FoundationException {
		Object onReturn = properties.get("onReturn");
		if(Boolean.TRUE.equals(onReturn)) return;
		
		if (element.getAttribute(STUB_ATTRIBUTE, false) == null
		|| element.getAttribute(METHOD_ATTRIBUTE, false) == null){
			throw new GeneralException("ws proxy 태그에는 "+STUB_ATTRIBUTE+", "+METHOD_ATTRIBUTE+" 속성은 필수 입니다.");
		}
		String stub = element.getAttribute(STUB_ATTRIBUTE).toString();
		String method = element.getAttribute(METHOD_ATTRIBUTE).toString();
		Map<String,Object> ouputTarget = null;
		String outputProperty = null;
		Map<String,String> types = new HashMap<String, String>();
		
		/*
		 * Jennifer Log확인용
		 */
		logFn(stub);

		List<Object> args = new ArrayList<Object>();
		List<ServiceDefinition> childeren = element.getChildMember();
		for (int i = 0; i < childeren.size(); i++) {
			ServiceDefinition child = childeren.get(i);
			String nodeName = child.getServiceName();
			if(nodeName.equals(INPUT_NODE)){
				Object temp = child.getAttribute(INPUT_NODE_VALUE_ATTRIBUTE);
				if(temp == null)
					throw new GeneralException(INPUT_NODE + " 태그는 "+INPUT_NODE_VALUE_ATTRIBUTE+" 속성이 필수 입니다.");
				args.add(temp);
			}
			if(nodeName.equals(OUTPUT_NODE)){
				Object temp = child.getAttribute(OUTPUT_NODE_TARGET_ATTRIBUTE);
				if(temp == null)
					throw new GeneralException(OUTPUT_NODE+ " 태그는 "+OUTPUT_NODE_TARGET_ATTRIBUTE+", "+OUTPUT_NODE_PROPERTY_ATTRIBUTE+" 속성이 필수 입니다.");
				if(temp == null)
					throw new GeneralException(OUTPUT_NODE+ " 태그의 "+OUTPUT_NODE_TARGET_ATTRIBUTE + " 속성의 값은 null 이 될수 없습니다.");
				if(!(temp instanceof Map))
					throw new GeneralException(OUTPUT_NODE+ " 태그의 "+OUTPUT_NODE_TARGET_ATTRIBUTE + " 속성의 값은 java.util.Map 인터페이스를 구현해야 합니다.");
				ouputTarget = (Map<String,Object>)temp;
				temp = child.getAttribute(OUTPUT_NODE_PROPERTY_ATTRIBUTE);
				if(temp == null)
					throw new GeneralException(OUTPUT_NODE+ " 태그는 "+OUTPUT_NODE_TARGET_ATTRIBUTE+", "+OUTPUT_NODE_PROPERTY_ATTRIBUTE+" 속성이 필수 입니다.");
				outputProperty = temp.toString();
				
			}
		}
		Object o = null;
		try {
			o = agency.execute(stub, method, args.toArray());
			ouputTarget.put(outputProperty, o);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
