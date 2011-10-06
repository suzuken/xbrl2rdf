package xbrlreader;

import java.util.ArrayList;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

public interface Reader {
	
	
	/**
	 * schemaRefを取得するメソッド
	 * <link:schemaRef>の属性を取得し、SchemaRefLinkオブジェクトを返す。
	 * 
	 * @return
	 * @throws XPathExpressionException 
	 */
	public XLink getSchemaRef() throws XPathExpressionException;
	
	/**
	 * roleRef情報を取得するメソッド。
	 * <link:roleRef>の属性を取得し、RoleRerfLinkオブジェクトを返す。
	 * 
	 * @return XLink roleRef
	 * @throws XPathExpressionException
	 */
	public XLink getRoleRef() throws XPathExpressionException;
	
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
	 * 個別の勘定科目に関する情報を返す。namespaceとelementの名前は文字列で指定する。
	 * elementName, namespaceURI, unitRef, contextREf, decimals, id, 及び, valueが取得され、
	 * Accountオブジェクトとして返される。
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

	/**
	 * ContextRefを指定して、AccountオブジェクトのArrayListを返す関数
	 * 
	 * contextRefにはCurrentYearConsolidatedDurationなどが入る。
	 * 
	 * @param contextRef
	 * @return
	 * @throws XPathExpressionException
	 */
	ArrayList<Account> getAccountsByContext(String contextRef)
			throws XPathExpressionException;

	/**
	 * jpfr-di:に属するelement名を指定して、DocumentInfoオブジェクトを得る関数
	 * 
	 * <jpfr-di:ExtendedLinkRoleLabelNonconsolidatedBS contextRef="DocumentInfo">
	 * http://info.edinet-fsa.go.jp/jp/fr/gaap/role/NonConsolidated
	 * </jpfr-di:ExtendedLinkRoleLabelNonconsolidatedBS>
	 * などの要素からvalueとelementNameを取得してDocumentInfoオブジェクトを生成している。
	 * 
	 * @param elementName
	 * @return
	 * @throws XPathExpressionException
	 */
	DocumentInfo getDocumentInfo(String elementName)
			throws XPathExpressionException;

	/**
	 * すべての文書情報をDocumentInfoオブジェクトのArrayListとして返す
	 * 
	 * contextRef="DocumentInfo"となっているすべての要素について分析を行う。
	 * 
	 * @return DocumentInfoの配列
	 * @throws XPathExpressionException
	 */
	ArrayList<DocumentInfo> getAllDocumentInfo()
			throws XPathExpressionException;

}
