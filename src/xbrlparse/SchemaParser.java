package xbrlparse;

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

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//XSDスキーマ用のパーサ
//JAXP1.1
public class SchemaParser {
	private Element elementDefinitions;

	//RoleTypeを保持するマップ→保持する必要、ある？
	private static Map<String, RoleType> RoleTypeMapping;
	private Map<String,String> namespaceMapping;

	//静的マップ。識別番号的にためられる。
	//【要検討】取得したいのは、そのスキーマパーサと関連するlinkbaseではないだろうか。
	//だとすると、静的なマップでuri参照をするのではなく、
	//リンクベースのroleを種別として、各リンクベースのuriを返すのがいいのではないか。
	//→やっぱりだめっぽい。ラベルリンクは英語と日本語二種類あるため、roleがキーで、重複すると、だめ。
	//DTSをすべて見て、urlから抜粋
	//Mapじゃなくてもいい。データ構造を検討する必要あり。
	private static Map<String, DiscoverableTaxonomySet> DTSMapping;
	//DTSも子を保持できるようになった。DTSマッピングは必要か？→一応。直接参照できるし。
	private DiscoverableXSD DXSD;
	public DiscoverableXSD getDXSD() {return DXSD;}
	public void setDXSD(DiscoverableXSD dXSD) {DXSD = dXSD;}

	//TODO elementのマッピングは、それぞれのDiscoverableXSDにしてしまったほうが合理的だと思う。
	private Map<String, ElementFromXSD> ElementMapping;

	public Map<String, String> getNamespaceMapping() {return namespaceMapping;}
	public void setNamespaceMapping(Map<String, String> namespaceMapping) {this.namespaceMapping = namespaceMapping;}

	public Map<String, ElementFromXSD> getElementMapping() {return ElementMapping;}
	public void setElementMapping(Map<String, ElementFromXSD> elementMapping) {ElementMapping = elementMapping;}

	public Element getElementDefinitions() {return elementDefinitions;}
	public void setElementDefinitions(Element elementDefinitions) {this.elementDefinitions = elementDefinitions;}

	public Map<String, DiscoverableTaxonomySet> getDTSMapping() {return DTSMapping;}
	public void setDTSMapping(Map<String, DiscoverableTaxonomySet> dTSMapping) {DTSMapping = dTSMapping;}

	/**
	 * @param schemaURL セットする schemaURL
	 */
	public void setSchemaURL(String schemaURL) {
		this.schemaURL = schemaURL;
	}
	/**
	 * @return schemaURL
	 */
	public String getSchemaURL() {
		return schemaURL;
	}

	private String schemaURL;

	/**
	 * コンストラクタ
	 * @param map
	 */
	public SchemaParser(){}
	public SchemaParser(String url){
		this.setSchemaURL(url);
		DXSD = new DiscoverableXSD(url);
	}

	//マップの印刷用
	public void printMap(Map<String, ?> map) {
		Set<String> set = map.keySet();
		Iterator<String> iterator = set.iterator();
		Object object;
		while(iterator.hasNext()){
			object = iterator.next();
			System.out.println(object + " = " + map.get(object));
		}
	}
	//テスト用
	public static void main(String args[]) throws URISyntaxException, IOException{
		new SchemaParser().process(args[0]);
	}

	//スキーマのurlを渡して、プロセスをはじめる。
	public void startProcess() throws URISyntaxException, IOException {
		System.out.println("*****************************スキーマファイル" + getSchemaURL() + "の分析開始**********************************");
		process(getSchemaURL());
	}

	public Boolean isHttpURL(String url){
		if (url.substring(0, 4).equals("http")){
			return true;
		} else {
			return false;
		}
	}

	public void getNamespace(Element root) {
		// NamespaceInfo info = new NamespaceInfo();

		NamedNodeMap attributes = root.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {
			Node current = attributes.item(i);

			if (current.getPrefix() != null) {
				if (current.getPrefix().equals("xmlns")) {
					namespaceMapping.put(current.getLocalName(), current
							.getNodeValue());
				}
			}else if(current.getNodeName().equals("xmlns")){
				namespaceMapping.put(current.getNodeName(), current.getNodeValue());
			}
		}
	}
	//XSDがみつかればXSDを生成。Linkbaseが見つかれば、Linkkbaseを生成。
	//普通の感覚で行けば、ここにxsdがあるのだから、まず、DiscoverableXSDを生成
	/**
	 * XSDスキーマの分析プロセス。
	 * @author suzuken
	 *
	 */
	public void process(String url) throws URISyntaxException, IOException {
		//outputTargetがdomResult
		DOMResult domResult = new DOMResult();
		//このurlでインスタンスを作成。もちろんスキーマ。いろんな処理がおわったら、DTSMappingにputする。
		//this.DXSD = new DiscoverableXSD(url);
		setNamespaceMapping(new HashMap<String, String>());

		DTSMapping = new HashMap<String, DiscoverableTaxonomySet>();

		//これはいらないとおもう。保留。
		SchemaParser.RoleTypeMapping = new HashMap<String, RoleType>();

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

		Node nodeRoot = domResult.getNode().getFirstChild();
		if (nodeRoot == null || nodeRoot instanceof Element == false) {
			System.out.println("ルートノードが見つかりません.");
			return;
		}
		elementDefinitions = (Element) nodeRoot;

		//nodeルートがxsd:schemaじゃなければ、問題あり。
		if (!elementDefinitions.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema") || !elementDefinitions.getLocalName().equals("schema")) {
			System.out.println("スキーマ定義(schema)が見つかりません。処理スキップします。");
		} else {
			System.out.println("スキーマ定義(schema)を解釈開始します。");
			//rootノードから名前空間定義をマッピングして読み取る。
			getNamespace(elementDefinitions);

			//インスタンスにもいれておく。
			getDXSD().setNamespaceMapping(getNamespaceMapping());

			String defaultNS = elementDefinitions.getAttribute("targetNamespace");

			System.out.println("名前空間[" + defaultNS + "]の定義が始まります。");
			NodeList listAnnotation = elementDefinitions.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "annotation");
			for (int indexAnnotation = 0; indexAnnotation < listAnnotation.getLength(); indexAnnotation++) {
				Element elementAnnotation = (Element) listAnnotation.item(indexAnnotation);
				System.out.println("アノテーションの情報を取得します");
				NodeList listAppInfo = elementAnnotation.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "appinfo");
				for(int indexAppInfo = 0; indexAppInfo < listAppInfo.getLength(); indexAppInfo++){
					Element elementAppInfo = (Element) listAppInfo.item(indexAppInfo);
					System.out.println("appinfoの情報を取得します。");

					//linkbaseRefを格納
					NodeList listLinkBaseRef = elementAppInfo.getElementsByTagNameNS(getNamespaceMapping().get("link"), "linkbaseRef");
					for(int indexLinkBaseRef = 0; indexLinkBaseRef < listLinkBaseRef.getLength(); indexLinkBaseRef++){
						Element elementLinkBaseRef = (Element) listLinkBaseRef.item(indexLinkBaseRef);

						//linkbaseをマッピングして保持する。
						//resolveできないときがある。（スラッシュがないとき。）
						//一番後ろにスラッシュがなければ、defaultNSにスラッシュをつける。
						if (!defaultNS.substring(defaultNS.length()).equals("/")){
							defaultNS = defaultNS + "/";
						}

						String role = elementLinkBaseRef.getAttributeNS(getNamespaceMapping().get("xlink"), "role");
						String arcrole = elementLinkBaseRef.getAttributeNS(getNamespaceMapping().get("xlink"), "arcrole");
						String type = elementLinkBaseRef.getAttributeNS(getNamespaceMapping().get("xlink"), "type");
						//String absoluteURI = URIurl.resolve(elementLinkBaseRef.getAttributeNS("http://www.w3.org/1999/xlink", "href")).toString();
						String absoluteURI = resolveHref(elementLinkBaseRef.getAttributeNS(getNamespaceMapping().get("xlink"), "href"), url);
						DiscoverableLinkBase DLinkBase = new DiscoverableLinkBase(absoluteURI, type, role, arcrole);
						//ここで発見したリンクベースは、このスキーマインスタンスの子である！
						DXSD.add(DLinkBase);

						//DTSMappingには恒久的に全てのタクソノミ及びリンクベースを保持しておく。
						DTSMapping.put(absoluteURI, DLinkBase);
					}

					//link:roleTypeを格納
					//これ何に使うんだろう…
					NodeList listRoleType = elementAppInfo.getElementsByTagNameNS(getNamespaceMapping().get("link"), "roleType");
					for(int indexRoleType = 0; indexRoleType<listRoleType.getLength(); indexRoleType++){
						Element elementRoleType = (Element) listRoleType.item(indexRoleType);

						//マッピング

						String roleURI = elementRoleType.getAttribute("roleURI");
						String id = elementRoleType.getAttribute("id");

						RoleType RT = new RoleType(roleURI, id);

						NodeList listDefinition = elementRoleType.getElementsByTagNameNS(getNamespaceMapping().get("link"), "definition");
						for(int indexDefinition =0; indexDefinition<listDefinition.getLength(); indexDefinition++){
							Element elementDefinition = (Element) listDefinition.item(indexDefinition);

							String definition = elementDefinition.getFirstChild().getNodeValue();
							RT.setDefinition(definition);
						}

						NodeList listUsedOn = elementRoleType.getElementsByTagNameNS(getNamespaceMapping().get("link"), "usedOn");
						for(int indexUsedOn = 0;indexUsedOn<listUsedOn.getLength(); indexUsedOn++){
							Element elementUsedOn = (Element) listUsedOn.item(indexUsedOn);

							String usedOn = elementUsedOn.getFirstChild().getNodeValue();
							RT.addUsedOn(usedOn);
						}

						//roletypeのマッピングをDTSに追加。
						DXSD.getRoleTypeMapping().put(roleURI, RT);

						RoleTypeMapping.put(roleURI, RT);

					}
				}

				// sequenceのあるものかどうか
				//processSequence(elementAnnotation);

				// complexContentがあるかどうか
				//processComplexContent(elementAnnotation);
			}
			//importとincludeはあるかどうか
			processImport(elementDefinitions, url);


			//elementはあるかどうか
			processElement(elementDefinitions);
		}
		//Mapping完了！！確認。
		System.out.println("*************************SchemaParserによる抽出情報***********************");
		System.out.println("*************************名前空間の情報***********************");
		printMap(getNamespaceMapping());
		System.out.println("*************************DTSの情報***********************");
		printMap(DTSMapping);
		System.out.println("*************************RoleTypeの情報***********************");
		printMap(RoleTypeMapping);
		System.out.println("*************************SchemaParserによる抽出情報ここまで***************");


		System.out.println("*************************インスタンスDXSDの印刷テスト***************");
		System.out.println(DXSD);

		//DTSMappingをXbrlParserクラスのDTSMappingMapに格納
		//は、しない。というか、とりあえず、発見したDTSに関して、スキーマパーサを起動する。
		//リンクベースパーサは、全部のスキーマを見つけた後にパースする。
		//ということは、DTSMappingはやっぱりクラス変数にしてしまったほうが楽。ってことか。

		//restartProcess(getDTSMapping());
		//DTSに対してパース命令を出すのは、もっとトップのレイヤでやる。

	}

	private void processImport(Element elementDefinitions, String url) throws URISyntaxException{
		NodeList listImport = elementDefinitions.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "import");
		if (listImport.getLength() != 0){
			for(int indexImport = 0; indexImport < listImport.getLength(); indexImport++){
				Element elementImport = (Element) listImport.item(indexImport);
				String namespace = elementImport.getAttribute("namespace");
				String schemaLocation = elementImport.getAttribute("schemaLocation");

				if(!namespace.substring(namespace.length()).equals("/")){
					namespace = namespace + "/";
				}
				//根本的な間違い。解決する対象はnamespaceURIではなく、このdocumentのurlに対して。
				//java.net.URI namespaceURI = new java.net.URI(namespace);
				String absoluteSchemaLocURI = resolveHref(schemaLocation, url);


				DiscoverableXSD DiscoverableXSD = new DiscoverableXSD(absoluteSchemaLocURI,namespace);
				DXSD.add(DiscoverableXSD);
				DTSMapping.put(absoluteSchemaLocURI, DiscoverableXSD);
			}
		}
	}

	private void processElement(Element elementDefinitions){
		NodeList listElement = elementDefinitions.getElementsByTagName("element");
		if (listElement.getLength() != 0){
			this.ElementMapping = new HashMap<String, ElementFromXSD>();
			for(int indexElement = 0; indexElement<listElement.getLength(); indexElement++){
				Element elementElement = (Element) listElement.item(indexElement);
				String name = elementElement.getAttribute("name");
				String id = elementElement.getAttribute("id");
				String type = elementElement.getAttribute("type");
				String substitutionGroup = elementElement.getAttribute("substitutionGroup");
				Boolean abstractBoolean = checkString(elementElement.getAttribute("abstract"));
				Boolean nillable = checkString(elementElement.getAttribute("nillable"));
				String periodType = elementElement.getAttributeNS(getNamespaceMapping().get("xbrli"), "periodType");

				ElementFromXSD EXSD = new ElementFromXSD(name, id, type, substitutionGroup, abstractBoolean, nillable, periodType);
				ElementMapping.put(id, EXSD);
			}
			//elementmappingをDXSDにいれる。
			DXSD.setElementMapping(getElementMapping());
			//確認
			System.out.println("*************************Elementの情報***********************");
			printMap(ElementMapping);
		}
	}

	public Boolean checkString (String str){
		if(str.equals("true")){
			return true;
		}else if(str.equals("false")){
			return false;
		}else if(str == ""){
			return null;
		}else{
			//エラー
			System.out.println("【エラー】値はtrueまたはfalseでなければなりません。");
			return null;
		}
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


	/**
	 * @deprecated
	 * @param map
	 */
/*	public void restartProcess(Map<String, DiscoverableTaxonomySet> map){
		//iteratorでDTSMappingをチェック
		//スキーマがあれば、巡回。スキーマパーサをもう一回生成。再帰。
		map = getDTSMapping();

		Set<String> set = map.keySet();
		Iterator<String> iterator = set.iterator();
		Object object;
		while(iterator.hasNext()){
			object = iterator.next();
			//map.get(object)が、DiscoverableXSDなのか、DiscoverableLinkBaseなのか
			String classname = map.get(object).getClass().getName();
			System.out.println("クラス名: "+ classname);
			System.out.println(object + " = " + map.get(object));

			//TODO 個々の処理はsuperclassであるDTSParserがあったほうが楽だった。
			if (classname.equals("xbrlparse.DiscoverableLinkBase")){
				//リンクベースパーサに渡す
				//リンクベースパーサはabstractだから、
				//どうすればいいのだろう？
				//リンクベースの種別を判別して、
				//各パーサに渡せばいい。

				try {
					startLinkBaseParse(map.get(object));
				} catch (MalformedURLException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}


			}else if(classname.equals("xbrlparse.DiscoverableXSD")){
				//URI_を得て、もう一回パース
				try {
					SchemaParser sp = new SchemaParser(map.get(object).getValue());
					sp.startProcess();
				} catch (URISyntaxException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}

		}
	}*/

	/**
	 * 機能しないメソッド。リンクベースをタイプわけして、それぞれことなるリンクベースパーサに渡したいらしい。
	 * @deprecated
	 * @param DTS
	 * @throws MalformedURLException
	 * @throws IOException
	 */
/*	public void startLinkBaseParse(DiscoverableTaxonomySet DTS) throws MalformedURLException, IOException{
		String ROLETYPE = "";

		if (ROLETYPE.equals("PRESENTATION_LINKBASE")){
			LinkBaseParser LBP = new PresentationLinkParser();
			LBP.startProcess(DTS.getValue());
		}else if(ROLETYPE.equals("LABEL_LINKBASE")){
			LinkBaseParser LBP = new LabelLinkParser();
			LBP.startProcess(DTS.getValue());
		}else if(ROLETYPE.equals("DEFINITION_LINKBASE")){
			LinkBaseParser LBP = new DefinitionLinkParser();
			LBP.startProcess(DTS.getValue());
		}else if(ROLETYPE.equals("CALCULATION_LINKBASE")){
			LinkBaseParser LBP = new CalculationLinkParser();
			LBP.startProcess(DTS.getValue());
		}else if(ROLETYPE.equals("REFERENCE_LINKBASE")){
			LinkBaseParser LBP = new ReferenceLinkParser();
			LBP.startProcess(DTS.getValue());
		}else{
			System.out.println("エラー："+ ROLETYPE);
		}
	}
*/}