package xbrlparse;

public class ElementFromXSD {

	private String name;
	private String id;
	private String type;
	private String substitutionGroup;
	private Boolean abstractBoolean;
	private Boolean nillable;
	private String periodType;

	public ElementFromXSD(String name, String id, String type,
			String substitutionGroup, Boolean abstractBoolean,
			Boolean nillable, String periodType) {
		super();
		this.name = name;
		this.id = id;
		this.type = type;
		this.substitutionGroup = substitutionGroup;
		this.abstractBoolean = abstractBoolean;
		this.nillable = nillable;
		this.periodType = periodType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ElementFromXSD [abstractBoolean=" + abstractBoolean + ", id="
				+ id + ", name=" + name + ", nillable=" + nillable
				+ ", periodType=" + periodType + ", substitutionGroup="
				+ substitutionGroup + ", type=" + type + "]";
	}

	public String getSubstitutionGroup() {
		return substitutionGroup;
	}

	public void setSubstitutionGroup(String substitutionGroup) {
		this.substitutionGroup = substitutionGroup;
	}

	public Boolean getAbstractBoolean() {
		return abstractBoolean;
	}

	public void setAbstractBoolean(Boolean abstractBoolean) {
		this.abstractBoolean = abstractBoolean;
	}

	public Boolean getNillable() {
		return nillable;
	}

	public void setNillable(Boolean nillable) {
		this.nillable = nillable;
	}

	public String getPeriodType() {
		return periodType;
	}

	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}



}
