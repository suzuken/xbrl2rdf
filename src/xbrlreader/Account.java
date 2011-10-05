package xbrlreader;

public interface Account {

	public String getNamespaceURI();

	public void setNamespaceURI(String namespaceURI);

	public String getLocalName();

	public void setLocalName(String localName);

	public String getUnitRef();

	public void setUnitRef(String unitRef);

	public String getContextRef() ;

	public void setContextRef(String contextRef);

	public String getDecimals();
	
	public void setDecimals(String decimals);

	public String getId();

	public void setId(String id);

	public Long getValue();

	public void setValue(Long value);
	
}
