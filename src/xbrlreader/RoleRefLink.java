package xbrlreader;

public class RoleRefLink implements XLink {

	public String href;
	public String roleURI;
	public String type;
	
	public RoleRefLink(String href, String roleURI, String type) {
		super();
		this.href = href;
		this.roleURI = roleURI;
		this.type = type;
	}

	@Override
	public String getHref() {
		return this.href;
	}

	@Override
	public String getType() {
		return this.type;
	}
	
	public String getRoleURI(){
		return this.roleURI;
	}

	@Override
	public String toString() {
		return "RoleRefLink [href=" + href + ", roleURI=" + roleURI + ", type="
				+ type + "]";
	}

}
