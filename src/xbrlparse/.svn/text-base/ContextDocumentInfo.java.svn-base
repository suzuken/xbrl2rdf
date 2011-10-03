package xbrlparse;

public class ContextDocumentInfo extends ContextInfo {

	//提出日
	String periodInstant;

	public ContextDocumentInfo() {}
	public ContextDocumentInfo(String id, String scheme, String identifier) {super(id, scheme, identifier);}
	public ContextDocumentInfo(String id, String scheme, String identifier, String periodInstant) {super(id, scheme, identifier); setPeriodInstant(periodInstant);}

	@Override
	public String toString() {
		return "id: " + getId() + ", scheme: " + getScheme() + ", identifier: " + getIdentifier() + ", instant: " + getPeriodInstant();
	}

	public String getPeriodInstant() {
		return periodInstant;
	}
	public void setPeriodInstant(String periodInstant) {
		this.periodInstant = periodInstant;
	}
	public void set(String id, String scheme, String identifier, String periodInstant){
		setId(id); setScheme(scheme); setIdentifier(identifier); setPeriodInstant(periodInstant);
	}
}
