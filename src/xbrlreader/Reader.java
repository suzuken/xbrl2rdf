package xbrlreader;

import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

public interface Reader {
	
	
	/**
	 * schemaRefを取得するメソッド
	 * 
	 * @return
	 * @throws XPathExpressionException 
	 */
	public String getSchemaRef() throws XPathExpressionException;
	
	
	/**
	 * コンテキストオブジェクトを返すためのメソッド
	 * 
	 * @param contextId: contextIdを文字列で指定する。
	 * 例：DocumentInfo, Prior2YearNonConsolidatedInstant
	 * @return
	 * @throws XPathExpressionException 
	 */
	public Context getContext(String contextId) throws XPathExpressionException;
	
	
	/**
	 * Unitについての情報を返すメソッド
	 * 
	 * @param unitId: unitIdを文字列で指定する
	 * 例：JPY
	 * @return
	 * 例：iso4217:JPY
	 * @throws XPathExpressionException 
	 */
	public String getUnit(String unitId) throws XPathExpressionException;
	
	
	/**
	 * 名前空間名とエレメントの名前を入力して、エレメントに関する情報を返す関数
	 * 
	 * @param namespace
	 * @param elementName
	 * @return
	 * @throws XPathExpressionException 
	 */
	public Account getAccount(String namespace, String elementName) throws XPathExpressionException;
	
	/**
	 * 名前空間名とエレメントの名前を入力して、エレメントがあるかどうかを調べる関数
	 * 
	 * @param namespace
	 * @param elementName
	 * @return
	 */
	public Boolean isExistElement(String namespace, String elementName);

}
