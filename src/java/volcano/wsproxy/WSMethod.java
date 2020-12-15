package volcano.wsproxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class WSMethod extends WSContextClient {

	private List<WSArgument> args;

	private String name;

	private WSReturn ret;

	public WSMethod(IWSContext context, String name, List<WSArgument> args,
			WSReturn ret) {
		super(context);
		this.name = name;
		this.args = args;
		this.ret = ret;
	}

	public List<WSArgument> getArguments() {
		return args;
	}

	public String getName() {
		return name;
	}

	public WSReturn getRet() {
		return ret;
	}
	
	public Object invoke(WSClass clazz, Object[] arguments) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException{
		Object o = clazz.getInstance();
		Object[] changedArgs = new Object[args.size()];
		Class[] argClasses = new Class[args.size()];
		for(int i=0 ; i < args.size() ; i++){
			Object eo = (context.exportValue(args.get(i), arguments[i]));
			changedArgs[i] = eo; 
			argClasses[i] = eo.getClass();
		}
		Method callMethod = null;
		for(Method m : Class.class.getMethods()){
			if(m.getName().equals("getMethod")){
				try {
					callMethod = (Method)m.invoke(o.getClass(), new Object[]{getName(),argClasses});
				} catch (InvocationTargetException ite) {
					if(clazz.getCallMethod()!=null) {
						callMethod = (Method)m.invoke(o.getClass(), new Object[]{clazz.getCallMethod(),argClasses});
						break;
					} 
					throw ite;
				} 
			}
		}
		if(callMethod != null){
			Object result = callMethod.invoke(o, changedArgs);
			return context.importClass(result, ret);
		}
		return null;
	}
	
	public static void main(String[] args) throws SecurityException, NoSuchMethodException {
		
	}
}
