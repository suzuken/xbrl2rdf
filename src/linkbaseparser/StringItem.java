package linkbaseparser;

public class StringItem implements Item {
	private String name;
	private String id;
	private String type;
	private String substitutionGroup;
	private Boolean abstractBoolean;
	private Boolean nillable;
	private String periodType;
	public StringItem(String name, String id, String type,
			String substitutionGroup, Boolean abstractBoolean, Boolean nillable,
			String periodType) {
		super();
		this.name = name;
		this.id = id;
		this.type = type;
		this.substitutionGroup = substitutionGroup;
		this.abstractBoolean = abstractBoolean;
		this.nillable = nillable;
		this.periodType = periodType;
	}

	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String getId() {
		return id;
	}
	@Override
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String getType() {
		return type;
	}
	@Override
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String getSubstitutionGroup() {
		return substitutionGroup;
	}
	@Override
	public void setSubstitutionGroup(String substitutionGroup) {
		this.substitutionGroup = substitutionGroup;
	}
	@Override
	public Boolean getAbstractBoolean() {
		return abstractBoolean;
	}
	@Override
	public void setAbstractBoolean(Boolean abstractBoolean) {
		this.abstractBoolean = abstractBoolean;
	}
	@Override
	public Boolean getNillable() {
		return nillable;
	}
	@Override
	public void setNillable(Boolean nillable) {
		this.nillable = nillable;
	}
	@Override
	public String getPeriodType() {
		return periodType;
	}
	@Override
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
}
