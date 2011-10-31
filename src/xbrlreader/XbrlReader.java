package xbrlreader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

import org.apache.xpath.NodeSet;
import org.kohsuke.args4j.Option;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//import org.w3c.dom.xpath.XPathExpression;
import org.xml.sax.SAXException;

import xbrlparse.InstanceInfo;

/**
 * @author suzuken
 *
 */
public class XbrlReader implements Reader {
	
	public XPath xpath;
	public Document doc;	//DOM
	
	@Option(name="-xbrl",usage="Set a path for XBRL file")
	public String xbrlurl;
	
	public Context context;
	public String schemaRef;
	public NamespaceContext nsc;
	
	//DOMの定義のためのメンバ
	private Element elementDefinitions;
	
	/**
	 * テスト用
	 * xbrlのurlを渡して起動
	 * 
	 * -xbrl: XBRLファイルのパスを指定
	 * 
	 * @param args
	 * @throws XPathExpressionException
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static void main(String[] args) throws XPathExpressionException, TransformerException, SAXException, IOException, ParserConfigurationException{
		
		long start  = System.currentTimeMillis(); //start
		
		XbrlReader x = new XbrlReader(args[0]);
		x.prepare();
		
		//値の取得テスト
		System.out.println(x.getAccount("jpfr-t-cte", "CapitalStock"));
		System.out.println(x.getAccountsByContext("Prior2YearNonConsolidatedDuration"));
		System.out.println(x.getAccountsByContext("Prior2YearNonConsolidatedInstant"));
		System.out.println(x.getAccountsByContext("Prior1YearNonConsolidatedDuration"));
		System.out.println(x.getAccountsByContext("Prior1YearNonConsolidatedInstant"));
		System.out.println(x.getAccountsByContext("CurrentYearNonConsolidatedDuration"));
		System.out.println(x.getAccountsByContext("CurrentYearNonConsolidatedInstant"));
		System.out.println(x.getAccountsByContext("Prior1YearNonConsolidatedDuration"));
		System.out.println(x.getDocumentInfo("FirstColumnNonconsolidatedBS"));
		System.out.println(x.getAllDocumentInfo());
		System.out.println(x.getContext("DocumentInfo"));
		System.out.println(x.getContext("Prior1YearNonConsolidatedDuration"));
		System.out.println(x.getContext("Prior1YearNonConsolidatedInstant"));
		System.out.println(x.getContext("CurrentYearNonConsolidatedDuration"));
		System.out.println(x.getContext("CurrentYearNonConsolidatedInstant"));
		System.out.println(x.getSchemaRef());
		System.out.println(x.getRoleRef());
		System.out.println(x.getUnit("JPY"));
		
		long stop = System.currentTimeMillis();
		
		System.out.println("実行時間は" + (stop - start) + "ミリ秒です。");
	}
	
	public XbrlReader(String xbrlurl) {
		super();
		this.xbrlurl = xbrlurl;
		this.nsc = new SimpleNamespaceContext();
		System.out.println(this.xbrlurl);
	}
	
	public XbrlReader(File xbrl){
		super();
		this.xbrlurl = xbrl.getAbsolutePath();
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
		this.elementDefinitions = (Element) root;

		//nodeルートがxbrli:xbrlじゃなければ問題あり。
		if (!this.elementDefinitions.getNamespaceURI().equals("http://www.xbrl.org/2003/instance") || !elementDefinitions.getLocalName().equals("xbrl")) {
			System.out.println("xbrli:xbrl要素が見つかりません。処理をスキップします。");
		} else {
			System.out.println("xbrli:xbrl要素の解釈を開始します。");
			_getNamespaceByRoot(this.elementDefinitions);
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
		Element jpoe;
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
		if(this.nsc.getNamespaceURI("jpfr-oe") != null){
//				&& this.xpath.evaluate("/xbrli:xbrl/xbrli:context[@id='"
//						+ contextId +"']/xbrli:scenario", this.doc) != null){
//			jpoe = _getElementByXPath("/xbrli:xbrl/xbrli:context[@id='" + contextId +"']/xbrli:scenario/jpfr-oe:*");
			jpoe = _getElementByXPath("/xbrli:xbrl/xbrli:context[@id='" + contextId +"']/xbrli:scenario/*");
		}else{
			jpoe = null;
		}
		String scenario = (jpoe != null) ? jpoe.getNodeName(): null;
		Context c = new ContextImpl(contextId, identifier, identifierScheme, periodInstant,
				periodEndDate, periodStartDate, scenario);
		if(c.getScenario() !=null && this.nsc.getNamespaceURI("jpfr-oe") != null){
			c.setScenarioNamespaceURI(this.nsc.getNamespaceURI(c.getScenarioPrefix()));
		}
		return c;

	}
	

	@Override
	public XLink getSchemaRef() throws XPathExpressionException  {
		String href = this.xpath.evaluate("/xbrli:xbrl/link:schemaRef/@xlink:href", this.doc);
		String type = this.xpath.evaluate("/xbrli:xbrl/link:schemaRef/@xlink:type", this.doc);
		return new SchemaRefLink(href, type);
	}

	@Override
	public XLink getRoleRef() throws XPathExpressionException  {
		String href = this.xpath.evaluate("/xbrli:xbrl/link:roleRef/@xlink:href", this.doc);
		String roleURI = this.xpath.evaluate("/xbrli:xbrl/link:roleRef/@roleURI", this.doc);
		String type = this.xpath.evaluate("/xbrli:xbrl/link:roleRef/@xlink:type", this.doc);
		return new RoleRefLink(href, roleURI, type);
	}

	@Override
	public Unit getUnit(String unitId) throws XPathExpressionException {
		String measure = this.xpath.evaluate("/xbrli:xbrl/xbrli:unit[@id='" + unitId + "']/xbrli:measure", this.doc);
		Unit u = new UnitImpl(unitId, measure);
		u.setMeasureNamespaceURI(this.nsc.getNamespaceURI(u.getMeasurePrefix()));
		return u;
	}

	@Override
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
	public ArrayList<Account> getAccountsByContext(String contextRef) throws XPathExpressionException{
		ArrayList<Account> ret = new ArrayList<Account>();
		NodeList nl = (NodeList) this.xpath.evaluate("/xbrli:xbrl/*[@contextRef='" + contextRef + "']",
				this.doc, XPathConstants.NODESET);
		for(int indexInstance =0; indexInstance < nl.getLength(); indexInstance++){
			Element elementInstance = (Element) nl.item(indexInstance);
			Account a = this._createInstance(elementInstance);
			ret.add(a);
		}
		return ret;
	}

	@Override
	public DocumentInfo getDocumentInfo(String elementName) throws XPathExpressionException{
		String value = this.xpath.evaluate("/xbrli:xbrl/jpfr-di:" + elementName, this.doc);
		String contextRef = this.xpath.evaluate("/xbrli:xbrl/jpfr-di:" + elementName + "/@contextRef", this.doc);
		return new DocumentInfo(elementName, contextRef, value);
	}
	
	@Override
	public ArrayList<DocumentInfo> getAllDocumentInfo() throws XPathExpressionException{
		ArrayList<DocumentInfo> ret = new ArrayList<DocumentInfo>();
		NodeList nl = (NodeList) this.xpath.evaluate("/xbrli:xbrl/*[@contextRef='DocumentInfo']",
				this.doc, XPathConstants.NODESET);
		for(int indexInstance =0; indexInstance < nl.getLength(); indexInstance++){
			Element elementInstance = (Element) nl.item(indexInstance);
			DocumentInfo a = this._createDocumentInfo(elementInstance);
			ret.add(a);
		}
		return ret;
	}

	@Override
	public Boolean isExistElement(String namespace, String elementName) {
		//TODO implementation
		return null;
	}

	/**
	 * xpath用の名前空間をメンバに追加する関数
	 * 
	 * XPathで名前空間を取り扱うにはNamespaceContextを実装する必要がある。
	 * そのため、SimpleNamespaceContextを利用し、名前空間を取り扱う。
	 * 
	 * @param namespace
	 * @param uri
	 */
	public void setNamespaceContext(String namespace, String uri){
		((SimpleNamespaceContext) this.nsc).addMapping(namespace, uri);
	}
	
	/*
	 * 以下、ヘルパ関数
	 */
	
	//xpathからelementを取得するためのヘルパ関数
	private Element _getElementByXPath(String xpathExpr) throws XPathExpressionException{
		Node result = (Node) this.xpath.evaluate(xpathExpr, this.doc, XPathConstants.NODE);
		Element el = (Element) result;
		return el;
	}
	
	//elementからDocumentInfoを作成するヘルパ関数
	private DocumentInfo _createDocumentInfo(Element elementInstance){
		String value = null;
		String localName = elementInstance.getLocalName();
		String contextRef = elementInstance.getAttribute("contextRef");
		if (elementInstance.hasChildNodes()){
			value = elementInstance.getFirstChild().getNodeValue();
		}else{
			value = null;
		}
		return new DocumentInfo(localName, contextRef, value);
	}
	
	//elementからAccountを作成するヘルパ関数
	private Account _createInstance(Element elementInstance) {
		Long value = null;
		String namespaceURI = elementInstance.getNamespaceURI();
		String localName = elementInstance.getLocalName();
		String unitRef = elementInstance.getAttribute("unitRef");
		String contextRef = elementInstance.getAttribute("contextRef");
		String decimals = elementInstance.getAttribute("decimals");
		String id = elementInstance.getAttribute("id");
		if (elementInstance.hasChildNodes()){
			value = Long.valueOf(elementInstance.getFirstChild().getNodeValue());
		}else{
			value = null;
		}
		return new AccountImpl(namespaceURI, localName, unitRef, contextRef, decimals, id, value);
	}


}
