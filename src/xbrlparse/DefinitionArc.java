package xbrlparse;

public class DefinitionArc extends Arc {

	private String order;

	public DefinitionArc(String type, String arcrole, String from, String to) {
		super(type, arcrole, from, to);
	}

	@Override
	public String toString() {
		return "DefinitionArc [order=" + order + ", toString()="
				+ super.toString() + "]";
	}

	public DefinitionArc(String type, String arcrole, String from, String to,
			String order) {
		super(type, arcrole, from, to);
		this.order = order;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}


}
