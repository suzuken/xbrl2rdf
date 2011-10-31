package xbrlreader;

/**
 * Contextの実装
 * 
 * @author suzuken
 *
 */
public class ContextImpl implements Context {

	public String id;
	public String identifier;
	public String identifierScheme;
	public String periodInstant;

	public String periodEndDate;
	public String periodStartDate;
	public String scenario;
	public String scenarioPrefix;
	public String scenarioLocalName;
	public String scenarioNamespaceURI;
	
	public String getScenarioNamespaceURI() {
		return scenarioNamespaceURI;
	}
	
	@Override
	public void setScenarioNamespaceURI(String scenarioNamespaceURI) {
		this.scenarioNamespaceURI = scenarioNamespaceURI;
	}
	public ContextImpl(){}
	public ContextImpl(String id,
			String identifier,
			String identifierScheme,
			String periodInstant,
			String periodEndDate,
			String periodStartDate,
			String scenario
			){
		setId(id);
		setIdentifier(identifier);
		setIdentifierScheme(identifierScheme);
		setPeriodInstant(periodInstant);
		setPeriodEndDate(periodEndDate);
		setPeriodStartDate(periodStartDate);
		setScenario(scenario);
		setScenarioPL(scenario);
	}
	
	
	@Override
	public void setScenarioPL (String measureValue){
		if(measureValue !=null){
			String str[] = measureValue.split(":");
			this.scenarioPrefix = str[0];
			this.scenarioLocalName = str[1];
		}
	}


	public void setId(String id) {
		this.id = id;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setIdentifierScheme(String identifierScheme) {
		this.identifierScheme = identifierScheme;
	}

	public void setPeriodInstant(String periodInstant) {
		this.periodInstant = periodInstant;
	}

	public void setPeriodEndDate(String periodEndDate) {
		this.periodEndDate = periodEndDate;
	}

	public void setPeriodStartDate(String periodStartDate) {
		this.periodStartDate = periodStartDate;
	}

	public void setScenario(String scenario) {
		this.scenario = scenario;
	}


	@Override
	public String getId() {
		return id;
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
	@Override
	public String getIdentifierScheme() {
		return identifierScheme;
	}
	@Override
	public String getPeriodInstant() {
		return periodInstant;
	}
	@Override
	public String getPeriodEndDate() {
		return periodEndDate;
	}
	@Override
	public String getPeriodStartDate() {
		return periodStartDate;
	}
	@Override
	public String getScenario() {
		return scenario;
	}
	@Override
	public String toString() {
		return "ContextImpl [id=" + id + ", identifier=" + identifier
				+ ", identifierScheme=" + identifierScheme + ", periodEndDate="
				+ periodEndDate + ", periodInstant=" + periodInstant
				+ ", periodStartDate=" + periodStartDate + ", scenario="
				+ scenario + "]";
	}
	@Override
	public String getScenarioPrefix() {
		return scenarioPrefix;
	}
	@Override
	public void setScenarioPrefix(String scenarioPrefix) {
		this.scenarioPrefix = scenarioPrefix;
	}
	@Override
	public String getScenarioLocalName() {
		return scenarioLocalName;
	}
	@Override
	public void setScenarioLocalName(String scenarioLocalName) {
		this.scenarioLocalName = scenarioLocalName;
	}

}