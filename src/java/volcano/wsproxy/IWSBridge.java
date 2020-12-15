package volcano.wsproxy;

public interface IWSBridge {
	
	public Object factory(String name, String clazz, boolean isStub);

	public Object importValue(Object target, WSProperty property);

	public void exportValue(Object target, WSProperty property, Object value);

	public void fault(String name, String method, Object[] input, Throwable t);

	public void success(String name, String method, Object[] input,
			Object output);
	
	public void setProxyClazz(String proxyClazz);
	
	public String getProxyClazz();
	
	public void setProxyMethod(String proxyMethod);
	
	public String getProxyMethod();
}
