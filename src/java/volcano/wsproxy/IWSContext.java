package volcano.wsproxy;

import java.util.Map;

public interface IWSContext {
	public Object exportClass(String name, Map data);
	public void exportProperty(WSProperty property, Object target, Map data); 
	public eswf.dataobject.Map importClass(Object o, WSRef ref);
	public void importProperty(WSProperty property, Object target, Map data);
	public void prepare(IWSBridge bridge, Map<String,IWSListBridge> collectionBridges, WSStub stub, Class defaultPropertyClass);
	public Object newInstance(String name, boolean isStub);
	public Object exportValue(WSRef property, Object value);
	public Object importValue(WSRef property, Object value);
	public Class refToClass(String ref);
}
