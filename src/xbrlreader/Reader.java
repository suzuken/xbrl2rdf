package xbrlreader;

import java.util.Map;

public interface Reader {
	
	/**
	 * 名前空間をmapで返すメソッド
	 * @return
	 */
	public Map<String, String> getNameSpace();
	
	/**
	 * schemaRefを取得するメソッド
	 * 
	 * @return
	 */
	public String getSchemaRef();
	
	
	/**
	 * コンテキストオブジェクトを返すためのメソッド
	 * 
	 * @param contextId: contextIdを文字列で指定する。
	 * 例：DocumentInfo, Prior2YearNonConsolidatedInstant
	 * @return
	 */
	public Context getContext(String contextId);
	
	
	/**
	 * Unitについての情報を返すメソッド
	 * 
	 * @param unitId: unitIdを文字列で指定する
	 * 例：JPY
	 * @return
	 * 例：iso4217:JPY
	 */
	public String getUnit(String unitId);
	
	
	/**
	 * 名前空間名とエレメントの名前を入力して、エレメントに関する情報を返す関数
	 * 
	 * @param namespace
	 * @param elementName
	 * @return
	 */
	public String getValue(String namespace, String elementName);
	
	/**
	 * 名前空間名とエレメントの名前を入力して、エレメントがあるかどうかを調べる関数
	 * 
	 * @param namespace
	 * @param elementName
	 * @return
	 */
	public Boolean isExistElement(String namespace, String elementName);

}
