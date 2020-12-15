package volcano.wsproxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WSPOJOBridge implements IWSBridge{
	
	private String proxyClazz = null;
	private String proxyMethod = null;

	public Object factory(String name, String clazz, boolean isStub) {
		Class c;
		Method m;
		Object o = null;
		
		try {
			if(isStub) {
				Object portTypeProxy;
				if(this.getProxyClazz()!=null && this.getProxyMethod()!=null) {
					c = Class.forName(getProxyClazz()); 
					portTypeProxy = c.newInstance(); 
					m = c.getMethod(getProxyMethod());
				} else {
					c = Class.forName("ws.sap."+ name +"." + name + "SoapProxy"); 
					portTypeProxy = c.newInstance(); 
					m = c.getMethod("get" + name + "Soap");
				}

				o = m.invoke(portTypeProxy, null); 
			} else {
				c = Class.forName(clazz);
				o = c.newInstance();
			}	
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}

	public Object importValue(Object target, WSProperty property) {
		try {
			Method m = target.getClass().getMethod("get" + property.getName());
			Object o = m.invoke(target, null);
			return o;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void exportValue(Object target, WSProperty property, Object value) {
		try {
			if(value != null){
				for(Method m : target.getClass().getMethods()){
					if(m.getName().equals("set" + property.getName())){
						try{
							Object o = m.invoke(target, new Object[]{value});
							break;
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	public void fault(String name, String method, Object[] input, Throwable t) {
		t.printStackTrace();
	}

	
	public void success(String name, String method, Object[] input,
			Object output) {

	}

	//@Override
	public void setProxyClazz(String proxyClazz) {
		this.proxyClazz = proxyClazz;
	}

	//@Override
	public String getProxyClazz() {
		return proxyClazz;
	}
	
	//@Override
	public void setProxyMethod(String proxyMethod) {
		this.proxyMethod = proxyMethod;
	}
	
	
	//@Override
	public String getProxyMethod() {
		return this.proxyMethod;
	}
}
