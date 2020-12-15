package volcano.wsproxy;

import java.util.Map;

public class WSClass extends WSContextClient{
	
	protected String clazz;

	protected Object instance;
	
	protected Map<String,WSMethod> methods;
	
	protected String name;
	
	private String callMethod = null;

	protected Map<String,WSProperty> properties;

	public WSClass(IWSContext context, String clazz,
			String name, Map<String, WSMethod> methods, 
			Map<String, WSProperty> properties) {
		super(context);
		this.clazz = clazz;
		this.methods = methods;
		this.name = name;
		this.properties = properties;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WSClass) {
			WSClass o = (WSClass) obj;
			if (this.name == null)
				return false;
			return this.name.equals(o.name);
		}
		return false;
	}

	public String getClazz() {
		return clazz;
	}
	
	public Object getInstance(){
		if(instance == null){
			instance = context.newInstance(getName(), false);
		}
		return instance;
	}

	public Map<String, WSMethod> getMethods() {
		return methods;
	}
	
	public String getName() {
		return name;
	}
	
	public Map<String, WSProperty> getProperties() {
		return properties;
	}
	
	public void setCallMethod(String callMethod) {
		this.callMethod = callMethod;
	}
	
	public String getCallMethod() {
		return this.callMethod;
	}
}
