/**
 * 
 */
package xbrlreader;

/**
 * @author suzuken
 *
 */
public class AccountImpl implements Account {
	/**
	 * 勘定科目のprefix
	 */
	private String namespaceURI;
	/**
	 * 勘定科目のエレメント名
	 */
	private String localName;
	private String unitRef;
	private String contextRef;
	private String decimals;
	private String id;
	/**
	 * この勘定科目の金額
	 */
	private Long value;

	public AccountImpl(String namespaceURI, String localName, String unitRef,
			String contextRef, String decimals, String id, Long value) {
		super();
		this.namespaceURI = namespaceURI;
		this.localName = localName;
		this.unitRef = unitRef;
		this.contextRef = contextRef;
		this.decimals = decimals;
		this.id = id;
		this.value = value;
	}

	public String getNamespaceURI() {
		return namespaceURI;
	}

	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getUnitRef() {
		return unitRef;
	}

	public void setUnitRef(String unitRef) {
		this.unitRef = unitRef;
	}

	public String getContextRef() {
		return contextRef;
	}

	public void setContextRef(String contextRef) {
		this.contextRef = contextRef;
	}

	public String getDecimals() {
		return decimals;
	}

	public void setDecimals(String decimals) {
		this.decimals = decimals;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "InstanceInfo [contextRef=" + contextRef + ", decimals="
				+ decimals + ", id=" + id + ", localName=" + localName
				+ ", namespaceURI=" + namespaceURI + ", unitRef=" + unitRef
				+ ", value=" + value + "]";
	}

}
