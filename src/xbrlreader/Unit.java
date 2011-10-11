package xbrlreader;

/**
 * @author Kenta Suzuki <suzuken326@gmail.com>
 * 
 * 通貨単位の情報を扱うためのインタフェース
 *
 */
public interface Unit {

	String getId();

	void setId(String id);

	String getMeasureValue();

	String getMeasurePrefix();

	void setMeasurePrefix(String measurePrefix);

	String getMeasureLocalName();

	void setMeasureLocalName(String measureLocalName);

	void setMeasureValue(String measureValue);

	void setMeasurePL(String measureValue);

	String getMeasureNamespaceURI();

	void setMeasureNamespaceURI(String measureNamespaceURI);

}
