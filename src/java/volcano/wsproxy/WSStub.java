package volcano.wsproxy;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class WSStub extends WSClass{
	
	private Map<String,WSClass> classes;
	
	public WSStub(WSClass clazz, Map<String,WSClass> classes) {
		super(clazz.context, clazz.clazz, clazz.name, clazz.methods, clazz.properties);
		this.classes = classes;
	}
	
	
	public WSStub(IWSContext context, String clazz,
			String name, Map<String, WSMethod> methods, 
			Map<String, WSProperty> properties, Map<String, WSClass> classes) {
		super(context, clazz, name, methods, properties);
		this.classes = classes;
	}

	public Object invoke(String name, Object[] arguments) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException{
		WSMethod m = methods.get(name);
		if(m == null)
			throw new RuntimeException("NoSuchMethod " + getName() + "(" + getClazz() + ")#" + name);
		Object o = m.invoke(this, arguments);
		
		return o;
	}

	public Map<String,WSClass> getClasses() {
		return classes;
	}
	
	@Override
	public Object getInstance(){
		if(instance == null){
			instance = context.newInstance(getName(), true);
		}
		return instance;
	}
}
