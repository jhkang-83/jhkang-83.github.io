package volcano.wsproxy;

public class WSProperty extends WSRef{
	
	private String name;
	
	private String alias;
	
	private String collection;
	
	public WSProperty(IWSContext context, String ref, String clazz, String name,
			String alias, String collection) {
		super(context, ref, clazz);
		this.name = name;
		this.alias = alias;
		this.collection = collection;
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

	public String getCollection() {
		return collection;
	}
	
	public boolean isCollection(){
		return collection != null && collection.trim().length() > 0; 
	}
}
