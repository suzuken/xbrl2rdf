package xbrlparse;

public class DurationDocumentInfo extends ContextInfo {

	private String startDate;
	private String endDate;
	private String scenarioPrefix;
	private String scenarioLocalName;

	public DurationDocumentInfo() {}
	public DurationDocumentInfo(String id, String scheme, String identifier) {super(id, scheme, identifier);}
	public DurationDocumentInfo(String id, String scheme, String identifier, String startDate, String endDate, String scenarioPrefix, String scenarioLocalName){
		super(id, scheme, identifier);
		setStartDate(startDate); setEndDate(endDate); setScenarioPrefix(scenarioPrefix); setScenarioLocalName(scenarioLocalName);
	}

	@Override
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	@Override
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
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
	@Override
	public String toString() {
		return "id: " + getId() + ", scheme: " + getScheme() + ", identifier: " + getIdentifier() + ", startDate: " + getStartDate() + ", endDate: " + getEndDate() +
		", scenarioPrefix: " + getScenarioPrefix() + ", scenarioLocalName: " + getScenarioLocalName();
	}
	public void set(String id, String scheme, String identifier, String startDate, String endDate, String scenarioPrefix, String scenarioLocalName){
		setId(id); setScheme(scheme); setIdentifier(identifier);
		setStartDate(startDate); setEndDate(endDate); setScenarioPrefix(scenarioPrefix); setScenarioLocalName(scenarioLocalName);
	}
}
