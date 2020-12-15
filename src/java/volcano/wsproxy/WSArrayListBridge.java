package volcano.wsproxy;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.ListCellRenderer;

public class WSArrayListBridge implements IWSListBridge{

	public List exportFrom(Object o, WSRef ref) {
		if(o instanceof List)
			return (List)o;
		if(o instanceof Object[]){
			return Arrays.asList(o);
		}
		return null;
	}

	public Object exportTo(List l, WSRef ref) {
		if(l != null && l.size() != 0){
			Object[] array = (Object[])Array.newInstance(ref.getContext().refToClass(ref.getRef()), l.size());
			for(int i=0 ; i < l.size() ; i++){
				array[i] = l.get(i);
			}
			return array;
		}
		return null;
	}

	public List importFrom(Object o, WSRef ref) {
		if(o instanceof Object[]){
			Object [] array = (Object[])o;
			List list = new ArrayList();
			for(Object item : array){
				list.add(item);
			}
			return list;
		}
		return null;
	}

	public Object importTo(List o, WSRef ref) {
		if(o != null){
			eswf.dataobject.List l =  new eswf.dataobject.List();
			l.addAll(o);
			return l;
		}
		return null;
	}
	
}
