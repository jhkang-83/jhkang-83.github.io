package volcano.proxy.gen;

import java.util.ArrayList;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import com.sun.org.apache.bcel.internal.classfile.Method;


public class ParamInfo {
	public static String[] returnParams(String path, String rootPath) {
		ClassParser parser;
		String[] resultVal = new String[0];
		try {
			String classPath = rootPath + path.replace(".", "/") + ".class";
			parser = new ClassParser( classPath );
			JavaClass clazz = parser.parse();

			for (Method m : clazz.getMethods()) {
				
				int size = m.getArgumentTypes().length;
				String methodName = m.getName();
				
				if(methodName.equals("<init>") && m.isPublic() && size>0) {
					ArrayList paramList = new ArrayList();
					for (int i = 1; i <= size; i++) {
						LocalVariable variable = m.getLocalVariableTable().getLocalVariable(i);
						paramList.add( variable.getName().toUpperCase() );
					}
					resultVal = (String[])paramList.toArray(new String[paramList.size()]);
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultVal;
	}

}
