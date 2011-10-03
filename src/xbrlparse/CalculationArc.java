package xbrlparse;

public class CalculationArc extends Arc {

	private String order;
	private String weight;

	public CalculationArc(String type, String arcrole, String from, String to, String order, String weight) {
		super(type, arcrole, from, to);
		this.order = order;
		this.weight = weight;
	}

	public CalculationArc(String type, String arcrole, String from, String to) {
		super(type, arcrole, from, to);
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "CalculationArc [order=" + order + ", weight=" + weight
				+ ", toString()=" + super.toString() + "]";
	}

}
