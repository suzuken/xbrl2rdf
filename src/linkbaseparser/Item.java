package linkbaseparser;

public interface Item {

	String getName();

	void setName(String name);

	String getId();

	void setId(String id);

	String getType();

	void setType(String type);

	String getSubstitutionGroup();

	void setSubstitutionGroup(String substitutionGroup);

	Boolean getAbstractBoolean();

	void setAbstractBoolean(Boolean abstractBoolean);

	Boolean getNillable();

	void setNillable(Boolean nillable);

	String getPeriodType();

	void setPeriodType(String periodType);

}
