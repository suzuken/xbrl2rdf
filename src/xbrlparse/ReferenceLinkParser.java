package xbrlparse;

import java.net.URISyntaxException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ReferenceLinkParser extends LinkBaseParser {

	public ReferenceLinkParser(String url) {
		super(url);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public ReferenceLinkParser() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void analyzeElements(Element elementDefinitions, String url) throws URISyntaxException {
		String defaultNS = elementDefinitions.getAttribute("xmlns");
		System.out.println("名前空間[" + defaultNS + "]の定義が始まります。");

		NodeList listDocumentation = elementDefinitions.getElementsByTagName("documentation");
		for(int indexDocumentation = 0; indexDocumentation < listDocumentation.getLength(); indexDocumentation++){
			Element elementDocumentation = (Element) listDocumentation.item(indexDocumentation);
			String documentationName = elementDocumentation.getFirstChild().getNodeValue();
			System.out.println(documentationName + "の情報を取得します。");
		}
		//roleRefはないらしい。
/*		NodeList listRoleRef = elementDefinitions.getElementsByTagName("roleRef");
		for(int indexRoleRef =0; indexRoleRef < listRoleRef.getLength(); indexRoleRef++){
			Element elementRoleRef = (Element) listRoleRef.item(indexRoleRef);
			System.out.println("roleRefの情報を取得します");

			//roleRefの各attributeの情報を取得・解析し、roleRefクラスに格納するメソッド
			analyzeRoleRef(elementRoleRef, url);
		}
*/
		//ここから、referenceLink要素の解析
		NodeList listReferenceLink = elementDefinitions.getElementsByTagName("referenceLink");
		for(int indexReferenceLink = 0; indexReferenceLink < listReferenceLink.getLength(); indexReferenceLink++){
			Element elementReferenceLink = (Element) listReferenceLink.item(indexReferenceLink);
			System.out.println("referenceLinkの情報を取得します");
			//referenceLink要素の各属性
			//クラスに入れたほうがいいかも。
			//extended, ~linkだけど。
//			String elementLabelLinkType = elementReferenceLink.getAttributeNS(getNamespaceMapping().get("xlink"), "type");
//			String elementLabelLinkRole = elementReferenceLink.getAttributeNS(getNamespaceMapping().get("xlink"), "role");

			analyzeReferenceLink(elementReferenceLink, url);
		}

		printMap(getLocatorMapping());
		printMap(getResourceMapping());
		printMap(getArcMapping());

	}

	private void analyzeReferenceLink(Element elementReferenceLink, String url) {
		//locはlabelをキーにして、マップする。
		NodeList listLoc = elementReferenceLink.getElementsByTagName("loc");
		for(int indexLocator =0; indexLocator < listLoc.getLength(); indexLocator++){
			Element elementLoc = (Element) listLoc.item(indexLocator);
			analyzeLoc(elementLoc, url);
		}

		//referenceはlabelをキーにしてマップ。analyzeResourceをそのまま利用できる。
		NodeList listReference = elementReferenceLink.getElementsByTagName("reference");
		for(int indexReference =0; indexReference < listReference.getLength(); indexReference++){
			Element elementReference = (Element) listReference.item(indexReference);
			analyzeResource(elementReference);
		}
		//referenceArc
		NodeList listReferenceArc = elementReferenceLink.getElementsByTagName("referenceArc");
		for(int indexReferenceArc =0; indexReferenceArc < listReferenceArc.getLength(); indexReferenceArc++){
			Element elementReferenceArc = (Element) listReferenceArc.item(indexReferenceArc);
			analyzeArc(elementReferenceArc);
		}

	}



	@Override
	public void analyzeResource(Element elementReference){
		String type = elementReference.getAttributeNS(getNamespaceMapping().get("xlink"), "type");
		String label = elementReference.getAttributeNS(getNamespaceMapping().get("xlink"), "label");
		String role = elementReference.getAttributeNS(getNamespaceMapping().get("xlink"), "role");


		String publisher = analyzeElementReference(elementReference, "Publisher");
		String name = analyzeElementReference(elementReference, "Name");
		String number = analyzeElementReference(elementReference, "Number");
		String issueDate = analyzeElementReference(elementReference, "IssueDate");
		String appendix = analyzeElementReference(elementReference, "Appendix");
		String chapter= analyzeElementReference(elementReference, "Chapter");
		String article = analyzeElementReference(elementReference, "Article");
		String paragraph = analyzeElementReference(elementReference, "Paragraph");
		String subparagraph = analyzeElementReference(elementReference, "Subparagraph");


		//クラスへ格納
		ReferenceResource res = new ReferenceResource(label, role, type, publisher, name, number, issueDate, appendix, chapter, article, paragraph, subparagraph);
		getResourceMapping().put(label, res);
	}

	//ref用の使い捨てメソッド
	private String analyzeElementReference(Element element, String str){
		NodeList list = element.getElementsByTagNameNS(getNamespaceMapping().get("ref"), str);
		if (list.getLength() != 0){
			Element tgElm = (Element) list.item(0);
			return tgElm.getFirstChild().getNodeValue();
		}
		else {
			return null;
		}
	}
}