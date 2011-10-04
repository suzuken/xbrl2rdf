package xbrlparse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;


/**Xbrlファイル用のパーサ.
 *xbrlファイルをパースし、各種情報を抜き出す。<br>
 *
 *@author suzuken
 */
public class XbrlParser extends Thread{

	/**
	 * 引数にXBRLファイルの相対パスを渡す
	 * 
	 * @param args　XBRLファイルへの相対パス
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static void main(String[] args) throws URISyntaxException, IOException{
		XbrlParser xp = new XbrlParser(args[0]);
		xp.parseStart(args[0]);
	}

	public XbrlParser(String str){
		setXBRLURL(str);
	}
	@Override
		public void run(){
		try {
			this.parseStart(XBRLURL);
			System.out.println(Thread.currentThread());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}

	private Element elementDefinitions;
	public Element getElementDefinitions() {
		return elementDefinitions;
	}
	public void setElementDefinitions(Element elementDefinitions) {
		this.elementDefinitions = elementDefinitions;
	}
	/**
	 * 名前空間のマップ。キーはprefix, 値はuriを示す。
	 */
	private Map<String, String> namespaceMapping;

	/**
	 * 構成情報のマッピング。キーはuri。ContextInfoはInstantDocumentInfoとDurationDocumentInfoに分かれている。
	 */
	private Map<String, ContextInfo> contextInfoMapping;

	/**
	 * 通貨単位のマッピング。キーは<unit>要素のid属性。
	 */
	private Map<String, UnitInfo> unitInfoMapping;

	/**
	 * 文書情報のマッピング。名前空間jpfr-diに属する要素についてのマッピングである。キーはLocalName。
	 */
	private Map<String, DocumentInfo> documentInfoMapping;

	/**
	 * 金額情報のマッピング。jpfr-t-<任意の文字列>に関する情報を示す。キーはLocalName。
	 */
	private Map<String, InstanceInfo> InstanceInfoMapping;

	/**
	 * XBRLのURL
	 */
	private String XBRLURL;

	private String SchemaURI;

	/**
	 * namespacemappingのセッター
	 * @param namespaceMapping
	 */
	public void setNamespaceMapping(Map<String, String> namespaceMapping) {this.namespaceMapping = namespaceMapping;}
	/**
	 * contextInfoMappingのセッター
	 * @param contextInfoMapping
	 */
	public void setContextInfoMapping(Map<String, ContextInfo> contextInfoMapping) {this.contextInfoMapping = contextInfoMapping;}
	/**
	 * unitInfoMappingのセッター
	 * @param unitInfoMapping
	 */
	public void setUnitInfoMapping(Map<String, UnitInfo> unitInfoMapping) {this.unitInfoMapping = unitInfoMapping;}
	/**
	 * documentInfoMappingのセッター
	 * @param documentInfoMapping
	 */
	public void setDocumentInfoMapping(Map<String, DocumentInfo> documentInfoMapping) {this.documentInfoMapping = documentInfoMapping;}
	/**
	 * instanceInfoMappingのセッター
	 * @param instanceInfoMapping
	 */
	public void setInstanceInfoMapping(Map<String, InstanceInfo> instanceInfoMapping) {this.InstanceInfoMapping = instanceInfoMapping;}
	/**
	 * namespaceMappingのゲッター
	 */
	public Map<String, String> getNamespaceMapping() {return namespaceMapping;}
	/**
	 * contextInfoMappingのゲッター
	 */
	public Map<String, ContextInfo> getContextInfoMapping() {return contextInfoMapping;}
	/**
	 * documentInfoMappingのゲッター
	 */
	public Map<String, DocumentInfo> getDocumentInfoMapping() {return documentInfoMapping;}
	/**
	 * InstanceInfoMappingのゲッター
	 */
	public Map<String, InstanceInfo> getInstanceInfoMapping() {return InstanceInfoMapping;}
	/**
	 * unitInfoMappingのゲッター
	 */
	public Map<String, UnitInfo> getUnitInfoMapping() {return unitInfoMapping;}

	public void setXBRLURL(String url) {XBRLURL = url;}
	/**
	 * @param schemaURI セットする schemaURI
	 */
	public void setSchemaURI(String schemaURI) {SchemaURI = schemaURI;}
	/**
	 * @return schemaURI
	 */
	public String getSchemaURI() {return SchemaURI;}

	/**
	 * 各マップを印字するメソッド。
	 * @param map
	 */
	public void printMap(Map<String, ?> map) {
		Set<String> set = map.keySet();
		Iterator<String> iterator = set.iterator();
		Object object;
		while(iterator.hasNext()){
			object = iterator.next();
			System.out.println(object + " = " + map.get(object));
		}
	}

	/**
	 * 指定されたurlがhttpで始まるかどうかを調べるメソッド
	 * @param url
	 * @return
	 */
	public Boolean isHttpURL(String url){
		if (url.substring(0, 4).equals("http")){
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 指定されたエレメント要素から、名前空間を得るメソッド。通常はrootエレメントを指定。
	 * @param root
	 */
	public void getNamespace(Element root){
		//NamespaceInfo info = new NamespaceInfo();

		NamedNodeMap attributes = root.getAttributes();

		for (int i=0; i<attributes.getLength(); i++){
			Node current = attributes.item(i);

			if(current.getPrefix() != null){
				if(current.getPrefix().equals("xmlns")){
					String str = current.getNodeValue();
					this.namespaceMapping.put(current.getLocalName(),str);
				}
			}
		}
	}

	/**
	 * XBRLのパースを開始するメソッド
	 * @param url
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public void parseStart(String url)throws URISyntaxException, IOException{
		DOMResult domResult = new DOMResult();
		this.namespaceMapping = new HashMap<String, String>();
		this.contextInfoMapping = new HashMap<String, ContextInfo>();
		this.documentInfoMapping = new HashMap<String, DocumentInfo>();
		this.InstanceInfoMapping = new HashMap<String, InstanceInfo>();
		this.unitInfoMapping = new HashMap<String, UnitInfo>();

		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			//ローカルではないファイルにも対応したい。
			if(isHttpURL(url)){
				URL httpURL = new URL(url);
				HttpURLConnection http = (HttpURLConnection)httpURL.openConnection();
				http.setRequestMethod("GET");
				http.connect();
				transformer.transform(new StreamSource(http.getInputStream()), domResult);
			}else{
				transformer.transform(new StreamSource(new FileInputStream(url)), domResult);
			}
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (TransformerException e) {
			e.printStackTrace();
			return;
		}


		//TODO コメントがあっても繰り返し検索してルートノードを検出できるようにする。
		Node ts = domResult.getNode();
		Node nodeRoot = getRootNode(ts);
		if (nodeRoot == null || nodeRoot instanceof Element == false) {
			System.out.println("ルートノードが見つかりません.");
			return;
		}
		elementDefinitions = (Element) nodeRoot;

		//nodeルートがxsd:schemaじゃなければ、問題あり。
		if (!elementDefinitions.getNamespaceURI().equals("http://www.xbrl.org/2003/instance") || !elementDefinitions.getLocalName().equals("xbrl")) {
			System.out.println("xbrli:xbrl要素が見つかりません。処理スキップします。");
		} else {
			System.out.println("xbrli:xbrl要素の解釈を開始します。");
			getNamespace(elementDefinitions);
		}
		try {
			this.analyzeElements(elementDefinitions, url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}


/*		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
*/		// XML Scehmaを認識する
		//argsで得たurlから、スキーマファイル名を設定。ピリオドより後ろを書き換えればおｋ
		//String xsd = url.replaceAll("\\.xbrl", ".xsd");
/*
		factory.setAttribute(
				"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
		"http://www.w3.org/2001/XMLSchema");
		factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", xsd);
		// DOM Documentインスタンス用ファクトリの生成
		DocumentBuilder builder = factory.newDocumentBuilder();
		// エラー・ハンドラの登録
		builder.setErrorHandler(new MyHandler());

		//********************************************
		//発見可能なタクソノミ集合(DTS)を得る。
		//xlinkの情報を取得
		//********************************************
		this.DTSMappingMap = new HashMap<String, Map<String,DiscoverableTaxonomySet>>();
*/
		//TODO 静的メソッドか、インスタンス化するか。→インスタンス化する。得たマップの要素を、こっちのマップに格納しておく。
		//スキーマパーサのインスタンスを生成

		//SchemaParser SParser = new SchemaParser();
		//SParser.startProcess(xsd);

	}

	/**
	 * elementのインスタンスであるrootnode(ルート要素)を得る。
	 * なかったらnull
	 * @param node
	 * @return
	 */
	private Node getRootNode(Node node){
		NodeList nodelist = node.getChildNodes();
		Node r = null;
		for(int i=0;i<nodelist.getLength();i++){
			r=nodelist.item(i);
			if(r instanceof Element==true){break;}
			else{r=null;}
		}
		return r;
	}

	public void analyzeElements(Element elementDefinition, String url) throws URISyntaxException{
		NodeList listSchemaRef = elementDefinitions.getElementsByTagNameNS(getNamespaceMapping().get("link"), "schemaRef");
		for(int indexSchemaRef = 0;indexSchemaRef < listSchemaRef.getLength(); indexSchemaRef++){
			Element elementSchemaRef = (Element) listSchemaRef.item(indexSchemaRef);
			this.analyzeSchemaRef(elementSchemaRef, url);
		}

		NodeList listContext = elementDefinitions.getElementsByTagNameNS(getNamespaceMapping().get("xbrli"), "context");
		for(int indexContext= 0; indexContext < listContext.getLength(); indexContext++){
			Element elementContext = (Element) listContext.item(indexContext);
			this.analyzeContext(elementContext, url);
		}
		NodeList listUnit = elementDefinitions.getElementsByTagNameNS(getNamespaceMapping().get("xbrli"), "unit");
		for(int indexUnit= 0; indexUnit < listUnit.getLength(); indexUnit++){
			Element elementUnit = (Element) listUnit.item(indexUnit);
			this.analyzeUnit(elementUnit, url);
		}

		//ここからInstanceInfoの分析
		//jpfr-t-*の形のやつ。
		//名前空間のキーを取得
		Set<String> keys = namespaceMapping.keySet();
		Iterator<String> ite = keys.iterator();     //実際のプログラム中に、null判定を忘れずに
		//コレクションの反復子iterator
		while(ite.hasNext()) {              //ループ
			String str = ite.next();        //該当オブジェクト取得
			//勘定科目に関するprefix
				if(str.equals("jpfr-di")){
					NodeList listDocument = elementDefinitions.getElementsByTagNameNS(getNamespaceMapping().get(str), "*");
					for(int indexDocument =0; indexDocument < listDocument.getLength(); indexDocument++){
						Element elementDocument = (Element) listDocument.item(indexDocument);
						this.analyzeDocument(elementDocument, url);
					}
				}else if(str.equals("jpfr-oe")){
					//特に無し。
				}else if(str.matches("jpfr-.*")){
					//jpfr-di, jpfr-oe以外のjpfr-*のもの。
					NodeList listInstance = elementDefinitions.getElementsByTagNameNS(getNamespaceMapping().get(str), "*");
					for(int indexInstance =0; indexInstance < listInstance.getLength(); indexInstance++){
						Element elementInstance = (Element) listInstance.item(indexInstance);
						this.analyzeInstance(elementInstance, url);
					}
				}

		}

		System.out.println("xsdのuri: " + SchemaURI);
		printMap(getNamespaceMapping());
		printMap(getContextInfoMapping());
		printMap(getUnitInfoMapping());
		printMap(getInstanceInfoMapping());
		printMap(getDocumentInfoMapping());
	}

	private void analyzeSchemaRef(Element elementSchemaRef, String url) {
		String type = null;
		String href = null;

		type = elementSchemaRef.getAttributeNS(getNamespaceMapping().get("xlink"),"type");
		href = elementSchemaRef.getAttributeNS(getNamespaceMapping().get("xlink"), "href");

		//とりあえずメンバ変数にしちゃって、xsdのURIとして処理。
		setSchemaURI(resolveHref(href, url));
	}

	public String resolveHref(String href, String url){
		java.net.URI documentURL = null;
		try {
			//hrefが絶対urlじゃなかったら
			if (!isHttpURL(href)){
				if(isHttpURL(url)){
					documentURL = new java.net.URI(url);
					return documentURL.resolve(href).toString();
				}else{
					String urlslash = url.replace("\\", "/");
					documentURL = new java.net.URI(urlslash);
					return documentURL.resolve(href).toString();
				}
			}else{
				//そのままreturn
				return href;
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void analyzeInstance(Element elementInstance, String url) {
		String namespaceURI = null;
		String localName= null;
		String unitRef= null;
		String contextRef= null;
		String decimals= null;
		String id= null;
		Long value = null;

		namespaceURI = getNamespaceMapping().get(elementInstance.getPrefix());
		localName = elementInstance.getLocalName();
		unitRef = elementInstance.getAttribute("unitRef");
		contextRef = elementInstance.getAttribute("contextRef");
		decimals = elementInstance.getAttribute("decimals");
		id = elementInstance.getAttribute("id");
		if (elementInstance.hasChildNodes()){
			value = Long.valueOf(elementInstance.getFirstChild().getNodeValue());
		}else{
			value = null;
		}
		InstanceInfo ii = new InstanceInfo(namespaceURI, localName, unitRef, contextRef, decimals, id, value);
		this.InstanceInfoMapping.put(localName, ii);
	}

	private void analyzeDocument(Element elementDocument, String url) {
		String localName = null;
		String contextRef = null;
		String value = null;

		localName = elementDocument.getLocalName();
		contextRef = elementDocument.getAttribute("contextRef");
		value = elementDocument.getFirstChild().getNodeValue();

		DocumentInfo di = new DocumentInfo(localName, contextRef, value);
		this.documentInfoMapping.put(localName, di);
	}

	private void analyzeUnit(Element elementUnit, String url) {
		String id = elementUnit.getAttribute("id");
		String measureValue = null;

		NodeList listMeasure = elementUnit.getElementsByTagNameNS(getNamespaceMapping().get("xbrli"), "measure");
		for(int indexMeasure =0; indexMeasure<listMeasure.getLength(); indexMeasure++){
			Element elementMeasure = (Element) listMeasure.item(indexMeasure);
			measureValue = elementMeasure.getFirstChild().getNodeValue();
		}

		UnitInfo ui = new UnitInfo(id, measureValue);
		this.unitInfoMapping.put(id, ui);
	}

	private void analyzeContext(Element elementContext, String url) {
		String id = null;
		String scheme = null;
		String identifier = null;
		String periodInstant = null;
		String startDate = null;
		String endDate = null;
		String scenarioPrefix = null;
		String scenarioLocalName = null;

//		ContextInfo ci = null;

		id = elementContext.getAttribute("id");
		NodeList listEntity = elementContext.getElementsByTagNameNS(getNamespaceMapping().get("xbrli"), "entity");
		for(int indexEntity =0; indexEntity < listEntity.getLength(); indexEntity++){
			Element elementEntity = (Element) listEntity.item(indexEntity);
			NodeList listIdentifier = elementEntity.getElementsByTagNameNS(getNamespaceMapping().get("xbrli"), "identifier");
			for(int indexIdentifier = 0; indexIdentifier < listIdentifier.getLength(); indexIdentifier++){
				Element elementIdentifier = (Element) listIdentifier.item(indexIdentifier);
				scheme = elementIdentifier.getAttribute("scheme");
				//子ノードのテキストがidentifier
				identifier = elementIdentifier.getFirstChild().getNodeValue();
			}
		}

		NodeList listPeriod = elementContext.getElementsByTagNameNS(getNamespaceMapping().get("xbrli"), "period");
		for(int indexPeriod = 0; indexPeriod<listPeriod.getLength(); indexPeriod++){
			Element elementPeriod = (Element) listPeriod.item(indexPeriod);
			NodeList listInstant = elementPeriod.getElementsByTagNameNS(getNamespaceMapping().get("xbrli"), "instant");
			for(int indexInstant=0; indexInstant<listInstant.getLength(); indexInstant++){
				Element elementInstant = (Element) listInstant.item(indexInstant);
				periodInstant = elementInstant.getFirstChild().getNodeValue();
			}
			NodeList listStart = elementPeriod.getElementsByTagNameNS(getNamespaceMapping().get("xbrli"), "startDate");
			for(int indexStart=0; indexStart<listStart.getLength(); indexStart++){
				Element elementStart = (Element) listStart.item(indexStart);
				startDate= elementStart.getFirstChild().getNodeValue();
			}
			NodeList listEnd = elementPeriod.getElementsByTagNameNS(getNamespaceMapping().get("xbrli"), "endDate");
			for(int indexEnd=0; indexEnd<listEnd.getLength(); indexEnd++){
				Element elementEnd = (Element) listEnd.item(indexEnd);
				endDate = elementEnd.getFirstChild().getNodeValue();
			}
		}

		NodeList listScenario = elementContext.getElementsByTagNameNS(getNamespaceMapping().get("xbrli"), "scenario");
		for(int indexScenario=0; indexScenario<listScenario.getLength(); indexScenario++){
			Element elementScenario = (Element) listScenario.item(indexScenario);
			NodeList listConsol = elementScenario.getElementsByTagNameNS(getNamespaceMapping().get("jpfr-oe"), "*");
			for(int indexConsol =0; indexConsol<listConsol.getLength(); indexConsol++){
				Element elementConsol = (Element)listConsol.item(indexConsol);
				scenarioPrefix = elementConsol.getPrefix();
				scenarioLocalName = elementConsol.getLocalName();
			}
		}

		if (id.equals("DocumentInfo")){
			ContextInfo ci = new ContextDocumentInfo(id, scheme, identifier, periodInstant);
			this.contextInfoMapping.put(id, ci);
		}
		else if(id.endsWith("Instant")){
			ContextInfo ci = new InstantDocumentInfo(id, scheme, identifier, periodInstant, scenarioPrefix, scenarioLocalName);
			this.contextInfoMapping.put(id, ci);
		}
		else if(id.endsWith("Duration")){
			ContextInfo ci = new DurationDocumentInfo(id, scheme, identifier, startDate, endDate, scenarioPrefix, scenarioLocalName);
			this.contextInfoMapping.put(id, ci);
		}
	}

}