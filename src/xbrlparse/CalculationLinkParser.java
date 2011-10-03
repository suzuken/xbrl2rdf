package xbrlparse;

import java.net.URISyntaxException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CalculationLinkParser extends LinkBaseParser {

	public CalculationLinkParser(String url) {
		super(url);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public CalculationLinkParser() {}

	@Override
	public void analyzeElements(Element elementDefinitions, String url)
			throws URISyntaxException {
		String defaultNS = elementDefinitions.getAttribute("xmlns");
		System.out.println("名前空間[" + defaultNS + "]の定義が始まります。");

		NodeList listDocumentation = elementDefinitions.getElementsByTagName("documentation");
		for(int indexDocumentation = 0; indexDocumentation < listDocumentation.getLength(); indexDocumentation++){
			Element elementDocumentation = (Element) listDocumentation.item(indexDocumentation);
			String documentationName = elementDocumentation.getFirstChild().getNodeValue();
			System.out.println(documentationName + "の情報を取得します。");
		}
		NodeList listRoleRef = elementDefinitions.getElementsByTagName("roleRef");
		for(int indexRoleRef =0; indexRoleRef < listRoleRef.getLength(); indexRoleRef++){
			Element elementRoleRef = (Element) listRoleRef.item(indexRoleRef);
			System.out.println("roleRefの情報を取得します");

			//roleRefの各attributeの情報を取得・解析し、roleRefクラスに格納するメソッド
			analyzeRoleRef(elementRoleRef, url);
		}

		//ここから、labelLink要素の解析
		NodeList listCalculationLink = elementDefinitions.getElementsByTagName("calculationLink");
		for(int indexCalculationLink = 0; indexCalculationLink < listCalculationLink.getLength(); indexCalculationLink++){
			Element elementCalculationLink = (Element) listCalculationLink.item(indexCalculationLink);
			System.out.println("calculationLinkの情報を取得します");
			//labelLink要素の各属性
			//クラスに入れたほうがいいかも。
			//TODO calculationlinkは複数存在する場合がある。やはりちゃんとクラス化したほうがいいかもしれない。
			//参考）http://info.edinet-fsa.go.jp/jp/fr/gaap/r/fnd/an/2009-03-09/jpfr-fnd-an-2009-03-09-calculation.xml
//			String elementCalculationLinkType = elementCalculationLink.getAttributeNS(getNamespaceMapping().get("xlink"), "type");
//			String elementCalculationLinkRole = elementCalculationLink.getAttributeNS(getNamespaceMapping().get("xlink"), "role");

			analyzeCalculationLink(elementCalculationLink, url);
		}

		printMap(getRoleRefMapping());
		printMap(getLocatorMapping());
		printMap(getArcMapping());
	}

	private void analyzeCalculationLink(Element elementCalculationLink, String url) {
		//locはlabelをキーにして、マップする。
		NodeList listLoc = elementCalculationLink.getElementsByTagName("loc");
		for(int indexLocator =0; indexLocator < listLoc.getLength(); indexLocator++){
			Element elementLoc = (Element) listLoc.item(indexLocator);
			analyzeLoc(elementLoc, url);
		}

		//calculationArcはクラス作った後、locとlabelをつなげる。from:loc to:label
		NodeList listCalculationArc = elementCalculationLink.getElementsByTagName("calculationArc");
		for(int indexCalculationArc =0; indexCalculationArc < listCalculationArc.getLength(); indexCalculationArc++){
			Element elementCalculationArc = (Element) listCalculationArc.item(indexCalculationArc);
			analyzeArc(elementCalculationArc);

		}

	}

	@Override
	public void analyzeArc(Element elementCalculationArc){
		String type = elementCalculationArc.getAttributeNS(getNamespaceMapping().get("xlink"), "type");
		String arcrole = elementCalculationArc.getAttributeNS(getNamespaceMapping().get("xlink"), "arcrole");
		String from = elementCalculationArc.getAttributeNS(getNamespaceMapping().get("xlink"), "from");
		String to = elementCalculationArc.getAttributeNS(getNamespaceMapping().get("xlink"), "to");
		String order = elementCalculationArc.getAttribute("order");
		String weight = elementCalculationArc.getAttribute("weight");

		//クラスへ格納
		CalculationArc ca = new CalculationArc(type, arcrole, from, to, order, weight);
		//keyはfrom/to
		getArcMapping().put(from + "@" + to, ca);

	}

}
