package volcano.wsproxy;

public class WSContextClient {
	protected IWSContext context;

	public WSContextClient(IWSContext context) {
		super();
		this.context = context;
	}

	public IWSContext getContext() {
		return context;
	}
	
}
