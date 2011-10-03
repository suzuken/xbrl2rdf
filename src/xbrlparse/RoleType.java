package xbrlparse;

import java.util.ArrayList;
import java.util.List;

public class RoleType {

	private String roleURI;
	private String id;
	private String definition;
	private List<String>usedOn = new ArrayList<String>();

	public RoleType(String roleURI, String id) {
		super();
		this.roleURI = roleURI;
		this.id = id;
		this.definition = null;
	}

	public RoleType(String roleURI, String id, String definition,
			List<String> usedOn) {
		super();
		this.roleURI = roleURI;
		this.id = id;
		this.definition = definition;
		this.usedOn = usedOn;
	}

	@Override
	public String toString() {
		return "RoleType [definition=" + definition + ", id=" + id
				+ ", roleURI=" + roleURI + ", usedOn=" + usedOn + "]";
	}


	public String getRoleURI() {
		return roleURI;
	}

	public void setRoleURI(String roleURI) {
		this.roleURI = roleURI;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public List<String> getUsedOn() {
		return usedOn;
	}

	public void setUsedOn(List<String> usedOn) {
		this.usedOn = usedOn;
	}

	public void addUsedOn(String usedOn){
		this.usedOn.add(usedOn);
	}

}
