package linkbaseparser;

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

import xbrlreader.SimpleNamespaceContext;

/**
 * リンクベースをパースする
 *
 * 
 * 
 * @author suzuken
 */
public class LinkbaseReader {
	
	public XPath xpath;
	public NamespaceContext nsc;
	public String url;
	public Document doc;
	public Element elementDefinitions;
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		long start = System.currentTimeMillis();

		LinkbaseReader l = new LinkbaseReader(args[0]);
		l.prepare();
		
		long stop = System.currentTimeMillis();
		System.out.println("実行時間は" + (stop - start) + "ミリ秒です。");
	}
	
	public LinkbaseReader(String url){
		this.url = url;
		this.nsc = new SimpleNamespaceContext();
		System.out.println(this.url);
	}
	
	public Boolean prepare() throws ParserConfigurationException, SAXException, IOException{
		//ファイルをパースしてDOMに変換
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		this.doc = builder.parse(this.url);

		Element root = this.doc.getDocumentElement();
		this.elementDefinitions = (Element) root;

		//nodeルートがxsd:schemaじゃなければ、問題あり。
		if (!elementDefinitions.getLocalName().equals("linkbase")) {
			System.out.println("linkbase要素がありません。処理をスキップします。");
		} else {
			System.out.println("linkbase要素の解釈を開始します。");
			_getNamespaceByRoot(this.elementDefinitions);
		}

		//ファクトリクラスを使ってXPathオブジェクトを作成
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(this.nsc);
		this.xpath = xpath;
		return true;
	}
	
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
	
	//xpathからelementを取得するためのヘルパ関数
	protected Element _getElementByXPath(String xpathExpr) throws XPathExpressionException{
		Node result = (Node) this.xpath.evaluate(xpathExpr, this.doc, XPathConstants.NODE);
		Element el = (Element) result;
		return el;
	}
	
	protected NodeList _getElementsByXPath(String xpathExpr) throws XPathExpressionException{
		return (NodeList) this.xpath.evaluate(xpathExpr, this.doc, XPathConstants.NODESET);
	}

	
}
