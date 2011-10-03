package xbrlparse;

import java.net.URISyntaxException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//定義リンク用のパーサ
//なんか、ないみたい（というか、提出時に削除されるみたい。
//というわけで、保留。
public class DefinitionLinkParser extends LinkBaseParser {

	public DefinitionLinkParser() {}
	public DefinitionLinkParser(String url) {super(url);}

	@Override
	public void analyzeElements(Element elementDefinitions, String url) throws URISyntaxException {
		/*String defaultNS = elementDefinitions.getAttribute("xmlns");
		System.out.println("名前空間[" + defaultNS + "]の定義が始まります。");
*/
/*		NodeList listDocumentation = elementDefinitions.getElementsByTagName("documentation");
		for(int indexDocumentation = 0; indexDocumentation < listDocumentation.getLength(); indexDocumentation++){
			Element elementDocumentation = (Element) listDocumentation.item(indexDocumentation);
			String documentationName = elementDocumentation.getFirstChild().getNodeValue();
			System.out.println(documentationName + "の情報を取得します。");
		}
*/		NodeList listRoleRef = elementDefinitions.getElementsByTagNameNS(getNamespaceMapping().get("link"),"roleRef");
		for(int indexRoleRef =0; indexRoleRef < listRoleRef.getLength(); indexRoleRef++){
			Element elementRoleRef = (Element) listRoleRef.item(indexRoleRef);
			System.out.println("roleRefの情報を取得します");

			//roleRefの各attributeの情報を取得・解析し、roleRefクラスに格納するメソッド
			analyzeRoleRef(elementRoleRef, url);
		}

		//ここからdefinitionリンクの解析
		NodeList listDefinitionLink = elementDefinitions.getElementsByTagNameNS(getNamespaceMapping().get("link"), "definitionLink");
		for(int indexDefinitionLink =0; indexDefinitionLink < listDefinitionLink.getLength(); indexDefinitionLink++){
			Element elementDefinitionLink = (Element) listDefinitionLink.item(indexDefinitionLink);
			System.out.println("definitionLinkの情報を取得します。");

//			String elementDefinitionLinkType = elementDefinitionLink.getAttributeNS(getNamespaceMapping().get("xlink"), "type");
//			String elementDefinitionLinkRole = elementDefinitionLink.getAttributeNS(getNamespaceMapping().get("xlink"), "role");

			analyzeDefinitionLink(elementDefinitionLink, url);
		}



		printMap(getRoleRefMapping());
		printMap(getLocatorMapping());
		printMap(getArcMapping());

	}

	private void analyzeDefinitionLink(Element elementDefinitionLink, String url) {
		// TODO 自動生成されたメソッド・スタブ
		//locはlabelをキーにして、マップする。
		NodeList listLoc = elementDefinitionLink.getElementsByTagNameNS(getNamespaceMapping().get("link"),"loc");
		for(int indexLocator =0; indexLocator < listLoc.getLength(); indexLocator++){
			Element elementLoc = (Element) listLoc.item(indexLocator);
			analyzeLoc(elementLoc, url);
		}

		//labelArcはクラス作った後、locとlabelをつなげる。from:loc to:label
		NodeList listDefinitionArc = elementDefinitionLink.getElementsByTagNameNS(getNamespaceMapping().get("link"),"definitionArc");
		for(int indexDefinitionArc =0; indexDefinitionArc < listDefinitionArc.getLength(); indexDefinitionArc++){
			Element elementDefinitionArc = (Element) listDefinitionArc.item(indexDefinitionArc);
			analyzeArc(elementDefinitionArc);

		}
	}

	@Override
	public void analyzeArc(Element elementDefinitionArc){
		String type = elementDefinitionArc.getAttributeNS(getNamespaceMapping().get("xlink"), "type");
		String arcrole = elementDefinitionArc.getAttributeNS(getNamespaceMapping().get("xlink"), "arcrole");
		String from = elementDefinitionArc.getAttributeNS(getNamespaceMapping().get("xlink"), "from");
		String to = elementDefinitionArc.getAttributeNS(getNamespaceMapping().get("xlink"), "to");
		String order = elementDefinitionArc.getAttribute("order");

		//クラスへ格納
		DefinitionArc da = new DefinitionArc(type, arcrole, from, to, order);
		//keyはfrom/to
		getArcMapping().put(from + "@" + to, da);

	}

}
