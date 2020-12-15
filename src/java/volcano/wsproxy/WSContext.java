package volcano.wsproxy;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WSContext implements IWSContext {

	private IWSBridge bridge;

	private Map<String, WSClass> canonicalClasses;

	private Map<String, WSClass> classes;
	
	private Map<String,IWSListBridge> collectionBridges;
	
	private WSStub stub;
	
	private Class defaultPropertyClass;

	public Object exportClass(String name, Map data) {
		
		WSClass c = classes.get(name);
		if (c == null)
			throw new RuntimeException("NoSuchClass " + name);
		Object o = newInstance(name, false);
		Map<String, WSProperty> properties = c.getProperties();
		Iterator<String> it = properties.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			WSProperty p = properties.get(key);
			exportProperty(p, o, data);
		}
		return o;
	}

	public void exportProperty(WSProperty property, Object target, Map data){
		Object value = null;
		if(property.isCollection()){
			IWSListBridge cb = collectionBridges.get(property.getCollection());
			if (cb == null)
				throw new RuntimeException("NoSupportedCollection " + property.getCollection());
			List l = cb.exportFrom(data.get(property.getAlias()),property);
			List cl = new ArrayList();
			if(l != null){
				for(Object item : l){
					cl.add(exportClass(property.getRef(), (Map)item));
				}
			}
			value = cb.exportTo(cl,property);
			bridge.exportValue(target, property, value);
		}else{
			value = data.get(property.getAlias());
			bridge.exportValue(target, property, exportValue(property, value));
		}
		
	}

	public eswf.dataobject.Map importClass(Object o, WSRef ref) {
		eswf.dataobject.Map data = new eswf.dataobject.Map();
		WSClass c = classes.get(ref.getRef());
		if (c == null)
			throw new RuntimeException("NoSuchClass " + o.getClass().getName());
		Map<String, WSProperty> properties = c.getProperties();
		Iterator<String> it = properties.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			WSProperty p = properties.get(key);
			importProperty(p, o, data);
		}
		return data;
	}

	public void importProperty(WSProperty property, Object target, Map data) {
		
		if(property.isCollection()){
			Object value = bridge.importValue(target, property);
			IWSListBridge cb = collectionBridges.get(property.getCollection());
			if (cb == null)
				throw new RuntimeException("NoSupportedCollection " + property.getCollection());
			List l = cb.importFrom(value,property);
			List cl = new ArrayList();
			if(l != null){
				for(Object item : l){
					cl.add(importClass(item, property));
				}
			}
			value = cb.importTo(cl,property);
			data.put(property.getAlias(), value);
		}else{
			Object value = bridge.importValue(target, property);
			data.put(property.getAlias(), importValue(property, value));
		}
	}

	public void prepare(IWSBridge bridge, Map<String,IWSListBridge> collectionBridges, WSStub stub, Class defaultPropertyClass) {
		this.stub = stub;
		this.defaultPropertyClass = defaultPropertyClass;
		this.bridge = bridge;
		this.classes = stub.getClasses();
		this.collectionBridges = collectionBridges;
		this.canonicalClasses = new HashMap<String, WSClass>();
		Iterator<WSClass> it = classes.values().iterator();
		while (it.hasNext()) {
			WSClass c = it.next();
			this.canonicalClasses.put(c.getClazz(), c);
		}
	}

	public Object newInstance(String name, boolean isStub) {
		WSClass c = null;
		if(isStub)
			c = stub;
		else
			c = classes.get(name);
		if (c == null)
			throw new RuntimeException("NoSuchClass " + name);
		Object o = bridge.factory(name, c.getClazz(), isStub);
		return o;
	}

	public Object exportValue(WSRef property, Object value) {
		if(value == null)
			return null;
		if(property.getRef() != null && property.getRef().trim().length() > 0){
			return exportClass(property.getRef(), (Map)value); 
		}
		Class c = null;
		if(property.getClazz() != null && property.getClazz().trim().length() > 0){
			try{
				c = Class.forName(property.getClazz());
			}catch(Exception e){}
		}
		if(c == null){
			c = defaultPropertyClass;
		}
		Object r = null;
		for(Constructor cons : c.getConstructors()){
			try{
				r = cons.newInstance(value);
				return r;
			}catch(Exception e){
				if(value.getClass().getName().equals(java.math.BigDecimal.class.getName()))
					return String.valueOf(value);
				else if(value.getClass().getName().equals(org.apache.axis.types.UnsignedByte.class.getName()))
					return String.valueOf(value);
			}
		}
		return null;
	}

	public Object importValue(WSRef property, Object value) {
		if(value == null)
			return null;
		if(property.getRef() != null && property.getRef().trim().length() > 0){
			return importClass(value, property); 
		}
		Class c = null;
		if(property.getClazz() != null && property.getClazz().trim().length() > 0){
			try{
				c = Class.forName(property.getClazz());
			}catch(Exception e){}
		}
		if(c == null){
			c = defaultPropertyClass;
		}
		Object r = null;
		for(Constructor cons : c.getConstructors()){
			try{
				r = cons.newInstance(value);
				return r;
			}catch(Exception e){
				if(value.getClass().getName().equals(java.math.BigDecimal.class.getName()))
					return String.valueOf(value);
				else if(value.getClass().getName().equals(org.apache.axis.types.UnsignedByte.class.getName()))
					return String.valueOf(value);
			}
		}
		return null;
	}
	
	public Class refToClass(String ref){
		WSClass c = classes.get(ref);
		if (c == null)
			throw new RuntimeException("NoSuchClass " + ref);
		try {
			return Class.forName(c.getClazz());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
