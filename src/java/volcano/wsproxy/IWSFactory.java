package volcano.wsproxy;

public interface IWSFactory<E> {
	
	public WSArgument createArgument(E e, IWSContext context);
	
	public WSClass createClass(E e, IWSContext context);
	
	public WSMethod createMethod(E e, IWSContext context);
	
	public WSProperty createProperty(E e, IWSContext context);
	
	public WSReturn createReturn(E e, IWSContext context);
}
