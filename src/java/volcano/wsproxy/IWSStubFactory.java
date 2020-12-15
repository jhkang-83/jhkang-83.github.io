package volcano.wsproxy;

public interface IWSStubFactory<E> extends IWSFactory<E> {
	public WSStub createStub(E e, IWSContext context);
}
