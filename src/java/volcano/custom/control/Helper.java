package volcano.custom.control;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Element;

import emro.util.Crypt;
import eswf.constant.Global;
import eswf.dataobject.List;
import eswf.dataobject.Map;
import eswf.exception.FoundationException;
import eswf.managers.PolicyManager;
import eswf.managers.ServiceManager;
import eswf.service.FunctionDistributor;
import eswf.service.ServiceDefinition;
/**
 * contoller 내의 스크립트 실행시 편리 기능을 제공한다.
 * 
 * @author EMRO(jaehun.ko)
 * @version 2009-09-29
 * @since eswf 1.0
 */
public class Helper implements Global{
	
	protected HttpServletRequest request;
	protected Map data;
	
	public Helper(HttpServletRequest request, Map data) {
		super(); 
		this.request = request;
		this.data = data;
	}

	/**
	 * 기준 파라미터를 기준으로 하여 request.getParameterValues() 의 길이가 동일한 파라미터들을
	 * 순서대로 리스트에 담아 반환.
	 * @param basisField 기준 파라미터  
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map> toList(String basisField) {
		List<Map> result = new List<Map>();
		ArrayList<String> parameters = new ArrayList<String>();
		HashMap<String,String[]> fields = new HashMap<String,String[]>();
		int basisSize = 0;
		if(request.getParameterValues(basisField) != null)
			basisSize = request.getParameterValues(basisField).length;
		
		Enumeration parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()){
			String name = (String) parameterNames.nextElement();
			String[] value = request.getParameterValues(name);
			if(value.length == basisSize)
				fields.put(name, value);
		}
		
		parameters.addAll(fields.keySet());
		int length = parameters.size();
		Map o = null;
		String name = null;
		for (int i = 0; i < basisSize; i++) {
			o = new Map();
			for (int j=0; j < length ; j++) {
				name = parameters.get(j);
				o.put(name, fields.get(name)[i]);
			}
			result.add(o);
		}
		return result;
	}
	
	/**
	 * request.getParameterNames()에서 반환하는 파라미터 명과 그값을 Map에 담아 반환
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map toMap () {
		Map params = new Map();
		Enumeration<String> names = request.getParameterNames();

		while(names.hasMoreElements()){
			String name = (String) names.nextElement();
			String value = request.getParameter(name);
			params.put (name, value);
		}
		return params;
	}
	
	/**
	 * 세션에 로그인 처리를 한다. 
	 * @param user
	 * @throws FoundationException
	 */
	public void login(Map user) throws FoundationException{
		PolicyManager.getInstance().getSessionAuthenticatePolicy().setSessionAuthenticated(request.getSession(), true);
		request.getSession().setAttribute(SESSION_USER_ATTRIBUTE, user);
	}
	
	public Map getUser(){
		return (Map)request.getSession().getAttribute(SESSION_USER_ATTRIBUTE);
	}
	/**
	 * 세션에 로그아웃 처리를 한다.
	 * @throws FoundationException
	 */
	public void logout() throws FoundationException{
		PolicyManager.getInstance().getSessionAuthenticatePolicy().setSessionAuthenticated(request.getSession(),false);
		request.getSession().removeAttribute(SESSION_USER_ATTRIBUTE);
		request.getSession().invalidate();
	}
	
	/**
	 * 파라미터에 해당하는 service-descriptor를 호출한다.
	 * @param descriptor desciprtor id
	 * @param serviceId service id
	 * @throws FoundationException
	 */
	public void forwardService(String descriptor, String serviceId) throws FoundationException{
		Element sElement = ServiceManager.getInstance().getService(descriptor, serviceId);
		eswf.dataobject.Map properties = new eswf.dataobject.Map();
		properties.put("descriptor", descriptor);
		FunctionDistributor.getInstance().start(new ServiceDefinition(sElement, data), data, properties);
	}
	/*
	 * 
	 */
	public String decrypt(String s)
			throws Exception {
		return Crypt.decrypt(s);
	}
	
	public String toUpperCase(String s)
			throws Exception {
		return s.toUpperCase();
	}
}
