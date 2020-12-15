package volcano.wsproxy;

public abstract class WSRef extends WSContextClient{

	private String ref;

	private String clazz;

	public WSRef(IWSContext context, String ref, String clazz) {
		super(context);
		this.ref = ref;
		this.clazz = clazz;
	}

	public String getRef() {
		return ref;
	}

	public String getClazz() {
		return clazz;
	}
}