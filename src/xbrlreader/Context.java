package xbrlreader;

/**
 * コンテキストのためのインタフェース
 * 
 * @author suzuken
 *
 */
public interface Context {
	
	//各パラメータの取得
	public String getId();
	public String getIdentifier();
	public String getIdentifierScheme();
	public String getPeriodInstant();
	public String getPeriodStartDate();
	public String getPeriodEndDate();
	
	/**
	 * Scenarioを返す
	 * jpfr-oe:NonConsolidatedなど。
	 * @return
	 */
	public String getScenario();
	public String[] getPeriod();

}
