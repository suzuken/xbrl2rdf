package xbrlparse;

public class InstantDocumentInfo extends ContextInfo {

	String periodInstant;
	String scenarioPrefix;
	String scenarioLocalName;



	public InstantDocumentInfo() {}
	public InstantDocumentInfo(String id, String scheme, String identifier) {super(id, scheme, identifier);}
	public InstantDocumentInfo(String id, String scheme, String identifier, String periodInstant, String scenarioPrefix, String scenarioLocalname){
		super(id, scheme, identifier); setPeriodInstant(periodInstant); setScenarioPrefix(scenarioPrefix); setScenarioLocalName(scenarioLocalname);
	}

	@Override
	public String toString() {
		return "id: " + getId() + ", scheme: " + getScheme() + ", identifier: " + getIdentifier() + ", periodInstant: " + getPeriodInstant() +
		", scenarioPrefix: " + getScenarioPrefix() + ", scenarioLocalName: " + getScenarioLocalName();
	}
	@Override
	public String getPeriodInstant() {
		return periodInstant;
	}

	public void setPeriodInstant(String periodInstant) {
		this.periodInstant = periodInstant;
	}

	@Override
	public String getScenarioPrefix() {
		return scenarioPrefix;
	}

	public void setScenarioPrefix(String scenarioPrefix) {
		this.scenarioPrefix = scenarioPrefix;
	}

	@Override
	public String getScenarioLocalName() {
		return scenarioLocalName;
	}

	public void setScenarioLocalName(String scenarioLocalName) {
		this.scenarioLocalName = scenarioLocalName;
	}

	public void set(String id, String scheme, String identifier, String periodInstant, String scenarioPrefix, String scenarioLocalname){
		setId(id); setScheme(scheme); setIdentifier(identifier);
		setPeriodInstant(periodInstant); setScenarioPrefix(scenarioPrefix); setScenarioLocalName(scenarioLocalname);
	}
}
