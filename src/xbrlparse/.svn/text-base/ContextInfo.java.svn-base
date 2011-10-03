package xbrlparse;

/**文書の構成情報を示す抽象クラス.
 *
 * @author suzuken
 *
 * idはDocumentInfo、Prior2YearNonConsolidatedInstantなど、contextの種類を端的に示す。
 * schemeは<xbrli:identifier>要素の属性値である、scheme要素を示す。
 * identifierは同じく<xbrli:identifier>要素の、テキスト値を示す。
 *
 */
public abstract class ContextInfo {
	//必須のものをメンバ変数として提示
	private String id;
	private String scheme;
	private String identifier;

	private String edinetCode;	//edinetコード
	private String count;	//開示回数

	public ContextInfo() {}
	public ContextInfo(String id, String scheme, String identifier){setId(id); setScheme(scheme); setIdentifier(identifier); separateIdentifier(identifier);}

	public abstract String toString();


	//setterとgetterはサブクラスに継承される
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getScheme() {
		return scheme;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getEdinetCode() {
		return edinetCode;
	}
	public void setEdinetCode(String edinetCode) {
		this.edinetCode = edinetCode;
	}
	public String getCount() {
		return count;
	}

	public String getPeriodInstant() {return null;}
	public String getScenarioPrefix() {return null;}
	public String getScenarioLocalName() {return null;}
	public String getStartDate() {return null;}
	public String getEndDate() {return null;}


	public void setCount(String count) {
		this.count = count;
	}
	public void set(String id, String scheme, String identifier){
		this.id = id;
		this.scheme = scheme;
		this.identifier = identifier;
	}

	public void separateIdentifier(String identifier){
		String str[] = identifier.split("-");
		setEdinetCode(str[0]);
		setCount(str[1]);
	}
}
