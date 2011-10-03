package xbrlparse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

//urlをうけて、パースをするクラス。
//ただし、label, reference, definition, calculation, presentationごとに、パースの仕方が違う。
//リンクベース用のパーサ。透明クラス。
/**
 * リンクベース用のパーサのための抽象クラス。
 * @author suzuken
 * startProcess(String url)によって、解析を開始することができる。
 *
 */
public abstract class LinkBaseParser {
	private Element elementDefinitions;
	// 使用されている名前空間をマップMap<prefix, uri>
	private Map<String, String> namespaceMapping;
	private Map<String, RoleRefInfo> roleRefMapping;
	private Map<String, Locator> locatorMapping;
	private Map<String, Resource> resourceMapping;
	private Map<String, Arc> arcMapping;
	private String LBURL;

	/**
	 * このリンクベースパーサで扱うリンクベースのクラス。メンバ変数として扱う。
	 */
	private DiscoverableLinkBase DLB;
	public DiscoverableLinkBase getDLB() {return DLB;}
	public void setDLB(DiscoverableLinkBase dLB) {DLB = dLB;}
	/**
	 * コンストラクタ
	 */
	public LinkBaseParser(){}
	public LinkBaseParser(String url){setLBURL(url); this.DLB = new DiscoverableLinkBase(url);}

	public Map<String, String> getNamespaceMapping() {return namespaceMapping;}
	public void setNamespaceMapping(Map<String, String> namespaceMapping) {this.namespaceMapping = namespaceMapping;}
	public Map<String, RoleRefInfo> getRoleRefMapping() {return roleRefMapping;}
	public void setRoleRefMapping(Map<String, RoleRefInfo> roleRefMapping) {this.roleRefMapping = roleRefMapping;}
	public Map<String, Locator> getLocatorMapping() {return locatorMapping;}
	public void setLocatorMapping(Map<String, Locator> locatorMapping) {this.locatorMapping = locatorMapping;}
	public Map<String, Arc> getArcMapping() {return arcMapping;}
	public void setArcMapping(Map<String, Arc> arcMapping) {this.arcMapping = arcMapping;}
	public Map<String, Resource> getResourceMapping() {return resourceMapping;}
	public void setResourceMapping(Map<String, Resource> resourceMapping) {this.resourceMapping = resourceMapping;}
	public String getLBURL() {return LBURL;}
	public void setLBURL(String lBURL) {LBURL = lBURL;}

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
			}
		}
	}

	public void printMap(Map<String, ?> map) {
		Set<String> set = map.keySet();
		Iterator<String> iterator = set.iterator();
		Object object;
		while (iterator.hasNext()) {
			object = iterator.next();
			System.out.println(object + " = " + map.get(object));
		}
	}

	public Boolean isHttpURL(String url) {
		if (url.substring(0, 4).equals("http")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 解析をスタートする。終了したら、各マッピング結果をDiscoverableLinkBaseクラスに引き渡す。
	 * @param url
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void startProcess() throws MalformedURLException,IOException {
		process(getLBURL());
		//確認
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!リンクベース出力確認");
		System.out.println(DLB);
	}

	/**
	 * processを開始するメソッド。
	 * @param url
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void process(String url) throws MalformedURLException, IOException {
		DOMResult domResult = new DOMResult();
		// 各種マップを生成
		setNamespaceMapping(new HashMap<String, String>());
		setRoleRefMapping(new HashMap<String, RoleRefInfo>());
		setLocatorMapping(new HashMap<String, Locator>());
		setResourceMapping(new HashMap<String, Resource>());
		setArcMapping(new HashMap<String, Arc>());

		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			// ローカルではないファイルにも対応したい。
			// urlがhttpでも、企業別のタクソノミはローカルにある。
			// というより、urlをローカルパスで投げればいいのか。
			if (isHttpURL(url)) {
				URL httpURL = new URL(url);
				HttpURLConnection http = (HttpURLConnection) httpURL
						.openConnection();
				http.setRequestMethod("GET");
				http.connect();
				transformer.transform(new StreamSource(http.getInputStream()),
						domResult);
			} else {
				transformer.transform(
						new StreamSource(new FileInputStream(url)), domResult);
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
		// nodeルートがlinkbaseじゃなければ、問題あり。
		if (!elementDefinitions.getLocalName().equals("linkbase")) {
			System.out.println("リンクベース定義(linkbase)が見つかりません。処理スキップします。");
		} else {
			System.out.println("リンクベース定義(linkbase)を解釈開始します。");
			getNamespace(elementDefinitions);
			printMap(this.namespaceMapping);
			getDLB().setNamespaceMapping(getNamespaceMapping());
			try {
				analyzeElements(elementDefinitions, url);
			} catch (URISyntaxException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	// Elementの分析は、各パーサに任せる。
	/**
	 * 各リンクベースのエレメント要素を解析する抽象メソッド。
	 */
	public abstract void analyzeElements(Element elementDefinitions, String url) throws URISyntaxException;
	/**
	 * RoleRef要素を解析するメソッド。
	 * @param elementRoleRef
	 * @param url
	 * @throws URISyntaxException
	 */
	public void analyzeRoleRef(Element elementRoleRef, String url)
			throws URISyntaxException {
		String roleURI = elementRoleRef.getAttribute("roleURI");
		String type = elementRoleRef.getAttributeNS(getNamespaceMapping().get(
				"xlink"), "type");
		// hrefに対してresolveしなければならない。
		// baseのuriは、自分自身。
		// このdocument自身のuriでresolve
		// hrefがhttpで始まっていたら、そのままhref使う。
		String href = elementRoleRef.getAttributeNS(getNamespaceMapping().get(
				"xlink"), "href");
		/*
		 * java.net.URI documentURL = new java.net.URI(url); String absoluteHref
		 * = documentURL.resolve(href).toString();
		 */
		String absoluteHref = resolveHref(href, url);
		String strArray[] = absoluteHref.split("#");
		String uri = strArray[0]; // hrefの#より前の部分
		String fragment = strArray[1]; // hrefの#より後ろの部分

		// クラスへ格納。
		RoleRefInfo rrInfo = new RoleRefInfo(roleURI, type, absoluteHref, uri,
				fragment);
		roleRefMapping.put(roleURI, rrInfo);
		getDLB().setRoleRefMapping(getRoleRefMapping());

		// 確認
		// printMap(this.roleRefMapping);
	}

	/**
	 * Loc要素を解析するメソッド
	 * @param elementLoc
	 * @param url
	 */
	public void analyzeLoc(Element elementLoc, String url) {
		String type = elementLoc.getAttributeNS(getNamespaceMapping().get(
				"xlink"), "type");
		String href = elementLoc.getAttributeNS(getNamespaceMapping().get(
				"xlink"), "href");
		String label = elementLoc.getAttributeNS(getNamespaceMapping().get(
				"xlink"), "label");
		// hrefをresolveしたい
		String absoluteHref = resolveHref(href, url);
		// fragmentとuriも得たい
		String strArray[] = absoluteHref.split("#");
		String uri = strArray[0]; // hrefの#より前の部分
		String fragment = strArray[1]; // hrefの#より後ろの部分
		// クラスへ格納
		Locator loc = new Locator(label, absoluteHref, type, uri, fragment);
		locatorMapping.put(label, loc);
		getDLB().setLocatorMapping(getLocatorMapping());
	}

	/**
	 * Resouce要素を解析するメソッド
	 * @param elementResource
	 */
	public void analyzeResource(Element elementResource) {
		String type = elementResource.getAttributeNS(getNamespaceMapping().get(
				"xlink"), "type");
		String label = elementResource.getAttributeNS(getNamespaceMapping()
				.get("xlink"), "label");
		String role = elementResource.getAttributeNS(getNamespaceMapping().get(
				"xlink"), "role");

		// クラスへ格納
		Resource res = new Resource(label, role, type);
		resourceMapping.put(label, res);
		getDLB().setResourceMapping(getResourceMapping());
	}

	/**
	 * Arc要素を解析するメソッド
	 * @param elementArc
	 */
	public void analyzeArc(Element elementArc) {
		String type = elementArc.getAttributeNS(getNamespaceMapping().get(
				"xlink"), "type");
		String arcrole = elementArc.getAttributeNS(getNamespaceMapping().get(
				"xlink"), "arcrole");
		String from = elementArc.getAttributeNS(getNamespaceMapping().get(
				"xlink"), "from");
		String to = elementArc.getAttributeNS(getNamespaceMapping()
				.get("xlink"), "to");

		// クラスへ格納
		Arc la = new Arc(type, arcrole, from, to);
		// keyはfrom/to
		arcMapping.put(from + "@" + to, la);
		getDLB().setArcMapping(getArcMapping());
	}

	/**
	 * 第一引数の文字列を、第二引数のurlによって解決するメソッド。第二引数には、通常、baseURIを指定する。
	 * @param href
	 * @param url
	 * @return
	 */
	public String resolveHref(String href, String url) {
		java.net.URI documentURL = null;
		try {
			// hrefが絶対urlじゃなかったら
			if (!isHttpURL(href)) {
				if (isHttpURL(url)) {
					documentURL = new java.net.URI(url);
					return documentURL.resolve(href).toString();
				} else {
					String urlslash = url.replace("\\", "/");
					documentURL = new java.net.URI(urlslash);
					return documentURL.resolve(href).toString();
				}
			} else {
				// そのままreturn
				return href;
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	// ローカル用
	/*
	 * public String getBaseURI(String url){ System.out.println("url: " + url);
	 * String strArray[] = url.split("\\\\"); return strArray[strArray.length -
	 * 2]; }
	 */

}
