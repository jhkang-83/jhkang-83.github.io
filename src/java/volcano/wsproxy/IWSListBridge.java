package volcano.wsproxy;

import java.util.List;


public interface IWSListBridge {
	
	public List exportFrom(Object o, WSRef property);
	
	public Object exportTo(List l, WSRef property);

	public List importFrom(Object o, WSRef property);
	
	public Object importTo(List o, WSRef property);
}
