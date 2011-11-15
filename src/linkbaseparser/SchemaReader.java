package linkbaseparser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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

import xbrlparse.ElementFromXSD;
import xbrlreader.SimpleNamespaceContext;

/**
 * @author suzuken
 *
 */
public class SchemaReader { 
	public XPath xpath;
	public NamespaceContext nsc;
	public String xsdurl;
	public Document doc;
	public Element elementDefinitions;
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
		long start = System.currentTimeMillis();

		SchemaReader s = new SchemaReader(args[0]);
		s.prepare();

		System.out.println(s.getAllElement());
		//各エレメントに対して参照リンクの情報を取得させる
		
		long stop = System.currentTimeMillis();
		System.out.println("実行時間は" + (stop - start) + "ミリ秒です。");
	}
	
	public SchemaReader(String url){
		this.xsdurl = url;
		this.nsc = new SimpleNamespaceContext();
		System.out.println(this.xsdurl);
	}
	
	public Boolean prepare() throws ParserConfigurationException, SAXException, IOException{
		//ファイルをパースしてDOMに変換
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		this.doc = builder.parse(this.xsdurl);

		Element root = this.doc.getDocumentElement();
		this.elementDefinitions = (Element) root;

		//nodeルートがxsd:schemaじゃなければ、問題あり。
		if (!elementDefinitions.getLocalName().equals("schema")) {
			System.out.println("schema要素がありません。処理をスキップします。");
		} else {
			System.out.println("schema要素の解釈を開始します。");
			_getNamespaceByRoot(this.elementDefinitions);
		}

		//ファクトリクラスを使ってXPathオブジェクトを作成
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(this.nsc);
		this.xpath = xpath;
		return true;
	}
	
	//指定したelementから属性を抽出し、itemオブジェクトを返す。
	private Item _getItem(Element el){
		String name = el.getAttribute("name");
		String id = el.getAttribute("id");
		String type = el.getAttribute("type");
		String substitutionGroup = el.getAttribute("substitutionGroup");
		Boolean abstractBoolean = Boolean.valueOf((el.getAttribute("abstract")));
		Boolean nillable = Boolean.valueOf((el.getAttribute("nillable")));
		String periodType = el.getAttributeNS(nsc.getNamespaceURI("xbrli"), "periodType");

		return new ItemImpl(name, id, type, substitutionGroup, abstractBoolean, nillable, periodType);
	}
	
	//すべてのelementを返す
	public Map<String, Item> getAllElement(){
		Map<String, Item> items = new HashMap<String, Item>();

		NodeList listElement = elementDefinitions.getElementsByTagName("element");
		if (listElement.getLength() != 0){
			for(int indexElement = 0; indexElement<listElement.getLength(); indexElement++){
				Item item = _getItem((Element) listElement.item(indexElement));
				if(item.getId() != null && item != null){
					items.put(item.getId(), item);
				}
			}
		}
		return items;
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

	
}
