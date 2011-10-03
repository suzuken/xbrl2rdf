package xbrlparse;

public class Resource {
	private String label;
	private String role;
	private String type;

	public Resource(String label, String role, String type) {
		super();
		this.label = label;
		this.role = role;
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Resource [label=" + label + ", role=" + role + ", type=" + type
				+ "]";
	}

}
