package xbrlreader;

public class UnitImpl implements Unit {


	private String id = null;
	private String measureValue = null;
	private String measurePrefix = null;
	private String measureLocalName = null;
	private String measureNamespaceURI = null;

	public UnitImpl(){}
	public UnitImpl(String id)	{this.id = id;}
	public UnitImpl(String id, String measureValue){this.id = id; this.measureValue = measureValue; setMeasurePL(measureValue);}
	public UnitImpl(UnitImpl u)	{this(u.id, u.measureValue); setMeasurePL(u.measureValue);}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getMeasureValue() {
		return measureValue;
	}
	@Override
	public String getMeasurePrefix() {
		return measurePrefix;
	}
	@Override
	public void setMeasurePrefix(String measurePrefix) {
		this.measurePrefix = measurePrefix;
	}
	@Override
	public String getMeasureLocalName() {
		return measureLocalName;
	}
	@Override
	public void setMeasureLocalName(String measureLocalName) {
		this.measureLocalName = measureLocalName;
	}
	@Override
	public void setMeasureValue(String measureValue) {
		this.measureValue = measureValue;
	}

	@Override
	public void setMeasurePL (String measureValue){
		String str[] = measureValue.split(":");
		this.measurePrefix = str[0];
		this.measureLocalName = str[1];
	}
	
	@Override
	public String getMeasureNamespaceURI() {
		return measureNamespaceURI;
	}
	
	@Override
	public void setMeasureNamespaceURI(String measureNamespaceURI) {
		this.measureNamespaceURI = measureNamespaceURI;
	}

	@Override
	public String toString() {
		return "UnitImpl [id=" + id + ", measureLocalName=" + measureLocalName
				+ ", measurePrefix=" + measurePrefix + ", measureValue="
				+ measureValue + "]";
	}

}
