package xbrlreader;

import java.io.IOException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
//import org.w3c.dom.xpath.XPathExpression;
import org.xml.sax.SAXException;

public class XbrlReader implements Reader {
	
	public XPath xpath;
	public Document doc;	//DOM
	
	public String xbrlurl;
	public Context context;
	public String schemaRef;
	public NamespaceContext nsc;
	
	//DOMのためのメンバ
	public DOMResult dom;
	//DOMの定義のためのメンバ
	private Element elementDefinitions;
	
	//テスト用
	//xbrlのurlを渡して起動
	public static void main(String[] args) throws XPathExpressionException, TransformerException, SAXException, IOException, ParserConfigurationException{
		XbrlReader x = new XbrlReader(args[0]);
		x.prepare();
		
		//値の取得テスト
		System.out.println(x.getAccount("jpfr-t-cte", "CapitalStock"));
		System.out.println(x.getContext("DocumentInfo"));
		System.out.println(x.getContext("Prior1YearNonConsolidatedDuration"));
		System.out.println(x.getContext("Prior1YearNonConsolidatedInstant"));
		System.out.println(x.getContext("CurrentYearNonConsolidatedDuration"));
		System.out.println(x.getContext("CurrentYearNonConsolidatedInstant"));
		System.out.println(x.getSchemaRef());
		System.out.println(x.getRoleRef());
		System.out.println(x.getUnit("JPY"));
	}
	
	public XbrlReader(String xbrlurl) {
		super();
		this.xbrlurl = xbrlurl;
		this.dom = new DOMResult();
//		this.namespace = new HashMap<String, String>();
		this.nsc = new SimpleNamespaceContext();
	}
	
	
	/**
	 * 指定されたxbrlファイルのパースの準備をするメソッド
	 * 名前空間のセットを行う
	 * 
	 * @throws TransformerException
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public Boolean prepare() throws TransformerException, SAXException, IOException, ParserConfigurationException{
		
		//ファイルをパースしてDOMに変換
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		this.doc = builder.parse(this.xbrlurl);
		
		Element root = this.doc.getDocumentElement();
		elementDefinitions = (Element) root;

		//nodeルートがxsd:schemaじゃなければ、問題あり。
		if (!elementDefinitions.getNamespaceURI().equals("http://www.xbrl.org/2003/instance") || !elementDefinitions.getLocalName().equals("xbrl")) {
			System.out.println("xbrli:xbrl要素が見つかりません。処理スキップします。");
		} else {
			System.out.println("xbrli:xbrl要素の解釈を開始します。");
			_getNamespaceByRoot(elementDefinitions);
		}

		//ファクトリクラスを使ってXPathオブジェクトを作成
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(this.nsc);
		this.xpath = xpath;

		return true;
	}
	
	/**
	 * 指定されたエレメント要素から、名前空間を得るメソッド。通常はrootエレメントを指定。
	 * @param root
	 */
	private void _getNamespaceByRoot(Element root){
		//NamespaceInfo info = new NamespaceInfo();

		NamedNodeMap attributes = root.getAttributes();

		for (int i=0; i<attributes.getLength(); i++){
			Node current = attributes.item(i);

			if(current.getPrefix() != null){
				if(current.getPrefix().equals("xmlns")){
					String str = current.getNodeValue();
					((SimpleNamespaceContext) this.nsc).addMapping(current.getLocalName(), str);
				}
			}
		}
	}

	@Override
	public Context getContext(String contextId) throws XPathExpressionException {
		String identifier = this.xpath.evaluate(
				"/xbrli:xbrl/xbrli:context[@id='" + contextId +"']/xbrli:entity/xbrli:identifier", this.doc);
		String identifierScheme = this.xpath.evaluate(
				"/xbrli:xbrl/xbrli:context[@id='" + contextId +"']/xbrli:entity/xbrli:identifier/@scheme", this.doc);
		String periodInstant = this.xpath.evaluate(
				"/xbrli:xbrl/xbrli:context[@id='" + contextId +"']/xbrli:period/xbrli:instant", this.doc);
		String periodStartDate = this.xpath.evaluate(
				"/xbrli:xbrl/xbrli:context[@id='" + contextId +"']/xbrli:period/xbrli:startDate", this.doc);
		String periodEndDate = this.xpath.evaluate(
				"/xbrli:xbrl/xbrli:context[@id='" + contextId +"']/xbrli:period/xbrli:endDate", this.doc);
		String scenario = this.xpath.evaluate(
				"/xbrli:xbrl/xbrli:context[@id='" + contextId +"']/xbrli:period/xbrli:endDate", this.doc);
		
		return new ContextImpl(contextId, identifier, identifierScheme, periodInstant,
				periodEndDate, periodStartDate, scenario);
	}
	
	
	private Element _getElementByXPath(String xpathExpr) throws XPathExpressionException{
		Node result = (Node) this.xpath.evaluate(xpathExpr, this.doc, XPathConstants.NODE);
		Element el = (Element) result;
		return el;
	}

	@Override
	public XLink getSchemaRef() throws XPathExpressionException  {
		String href = this.xpath.evaluate("/xbrli:xbrl/link:schemaRef/@xlink:href", this.doc);
		String type = this.xpath.evaluate("/xbrli:xbrl/link:schemaRef/@xlink:type", this.doc);
		return new SchemaRefLink(href, type);
	}

	public XLink getRoleRef() throws XPathExpressionException  {
		String href = this.xpath.evaluate("/xbrli:xbrl/link:roleRef/@xlink:href", this.doc);
		String roleURI = this.xpath.evaluate("/xbrli:xbrl/link:roleRef/@roleURI", this.doc);
		String type = this.xpath.evaluate("/xbrli:xbrl/link:roleRef/@xlink:type", this.doc);
		return new RoleRefLink(href, roleURI, type);
	}

	@Override
	public String getUnit(String unitId) throws XPathExpressionException {
		String measure = this.xpath.evaluate("/xbrli:xbrl/xbrli:unit[@id='" + unitId + "']/xbrli:measure", this.doc);
		return measure;
	}
	
	public Account getAccount(String namespace, String elementName) throws XPathExpressionException{
		Element el = _getElementByXPath("/xbrli:xbrl/" + namespace + ":" + elementName);
		String unitRef = el.getAttributeNode("unitRef").getValue();
		String namespaceURI = this.nsc.getNamespaceURI(namespace);
		String localName = elementName;
		String contextRef = el.getAttributeNode("contextRef").getValue();
		String decimals = el.getAttributeNode("decimals").getValue();
		String id = el.getAttributeNode("id").getValue();
		Long value = Long.parseLong(el.getFirstChild().getNodeValue());
		return new AccountImpl(namespaceURI, localName, unitRef, contextRef, decimals, id, value);
	} 
	

	@Override
	public Boolean isExistElement(String namespace, String elementName) {
		
		return null;
	}

	public void setNamespaceContext(String namespace, String uri){
		((SimpleNamespaceContext) this.nsc).addMapping(namespace, uri);
	}

}
