package xbrlparse;

public class UnitInfo {
	private String id = null;
	private String measureValue = null;
	private String measurePrefix = null;
	private String measureLocalName = null;

	public UnitInfo(){}
	public UnitInfo(String id)	{this.id = id;}
	public UnitInfo(String id, String measureValue){this.id = id; this.measureValue = measureValue; setMeasurePL(measureValue);}
	public UnitInfo(UnitInfo u)	{this(u.id, u.measureValue); setMeasurePL(u.measureValue);}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMeasureValue() {
		return measureValue;
	}
	public String getMeasurePrefix() {
		return measurePrefix;
	}
	public void setMeasurePrefix(String measurePrefix) {
		this.measurePrefix = measurePrefix;
	}
	public String getMeasureLocalName() {
		return measureLocalName;
	}
	public void setMeasureLocalName(String measureLocalName) {
		this.measureLocalName = measureLocalName;
	}
	public void setMeasureValue(String measureValue) {
		this.measureValue = measureValue;
	}

	public void setMeasurePL (String measureValue){
		String str[] = measureValue.split(":");
		this.measurePrefix = str[0];
		this.measureLocalName = str[1];

	}
	@Override
	public String toString() {
		return "UnitInfo [id=" + id + ", measureLocalName=" + measureLocalName
				+ ", measurePrefix=" + measurePrefix + ", measureValue="
				+ measureValue + "]";
	}

}
