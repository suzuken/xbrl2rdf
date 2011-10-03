package xbrlparse;

public class PresentationArc extends Arc {

	private String order;
	private String preferredLabel;



	@Override
	public String toString() {
		return "PresentationArc [order=" + order + ", preferredLabel="
				+ preferredLabel + ", toString()=" + super.toString() + "]";
	}

	public PresentationArc(String type, String arcrole, String from, String to) {
		super(type, arcrole, from, to);
	}

	public PresentationArc(String type, String arcrole, String from, String to, String order) {
		super(type, arcrole, from, to);
		this.order = order;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getPreferredLabel() {
		return preferredLabel;
	}

	public void setPreferredLabel(String preferredLabel) {
		this.preferredLabel = preferredLabel;
	}

}
