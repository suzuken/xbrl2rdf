package xbrlparse;

import java.net.URISyntaxException;
//import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//名称リンク用のパーサ
//単一のlabelリンクファイル用。
//TODO loc, label, labelarcの情報がマップされる。それを利用して、labelを得ればおｋ。
//TODO elementと名称を結びつける作業
public class LabelLinkParser extends LinkBaseParser {
//	private static Map<String, String> labelLinkMapping;
	public Boolean NSflag = null;

	public LabelLinkParser(String url) {
		super(url);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public LabelLinkParser() {
		// TODO 自動生成されたコンストラクター・スタブ

	}

	@Override
	public void analyzeElements(Element elementDefinitions, String url) throws URISyntaxException {
		//defaultのNSがない場合もある。そういう場合は逐一変えなきゃならない。flagで対応しておく。

		String defaultNS = elementDefinitions.getAttribute("xmlns");
		if(defaultNS != ""){
			NSflag = true;
			System.out.println("名前空間[" + defaultNS + "]の定義が始まります。");
		}else{
			NSflag = false;
			System.out.println("デフォルトの名前空間はありません。");
		}

		NodeList listDocumentation = elementDefinitions.getElementsByTagName("documentation");
		for(int indexDocumentation = 0; indexDocumentation < listDocumentation.getLength(); indexDocumentation++){
			Element elementDocumentation = (Element) listDocumentation.item(indexDocumentation);
			String documentationName = elementDocumentation.getFirstChild().getNodeValue();
			System.out.println(documentationName + "の情報を取得します。");
		}

		NodeList listRoleRef = null;

		if(NSflag){listRoleRef = elementDefinitions.getElementsByTagName("roleRef");}
		else{listRoleRef = elementDefinitions.getElementsByTagNameNS(getNamespaceMapping().get("link"), "roleRef");}
		for(int indexRoleRef =0; indexRoleRef < listRoleRef.getLength(); indexRoleRef++){
			Element elementRoleRef = (Element) listRoleRef.item(indexRoleRef);
			System.out.println("roleRefの情報を取得します");

			//roleRefの各attributeの情報を取得・解析し、roleRefクラスに格納するメソッド
			analyzeRoleRef(elementRoleRef, url);
		}

		//ここから、labelLink要素の解析
		NodeList listLabelLink =null;

		//なぜかlabelLinkが取れない！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
		if(NSflag){listLabelLink = elementDefinitions.getElementsByTagName("labelLink");}
		else{listLabelLink = elementDefinitions.getElementsByTagNameNS(getNamespaceMapping().get("link"),"labelLink");}
		for(int indexLabelLink = 0; indexLabelLink < listLabelLink.getLength(); indexLabelLink++){
			Element elementLabelLink = (Element) listLabelLink.item(indexLabelLink);
			System.out.println("labelLinkの情報を取得します");
			//labelLink要素の各属性
			//クラスに入れたほうがいいかも。
//			String elementLabelLinkType = elementLabelLink.getAttributeNS(getNamespaceMapping().get("xlink"), "type");
//			String elementLabelLinkRole = elementLabelLink.getAttributeNS(getNamespaceMapping().get("xlink"), "role");

			analyzeLabelLink(elementLabelLink, url);
		}

		printMap(getRoleRefMapping());
		printMap(getLocatorMapping());
		printMap(getResourceMapping());
		printMap(getArcMapping());
	}


	//このクラス独自のメソッド。
	//labelLinkを解析して、クラスに入れる。
	//labelLinkMappingに、ラベルリンクの解析結果をマップしてく。
	private void analyzeLabelLink(Element elementLabelLink, String url){
		//locはlabelをキーにして、マップする。
		NodeList listLoc = null;

		if(NSflag){listLoc = elementLabelLink.getElementsByTagName("loc");}
		else{listLoc = elementLabelLink.getElementsByTagNameNS(getNamespaceMapping().get("link"), "loc");}
		for(int indexLocator =0; indexLocator < listLoc.getLength(); indexLocator++){
			Element elementLoc = (Element) listLoc.item(indexLocator);
			analyzeLoc(elementLoc, url);
		}

		NodeList listLabel = null;

		//labelはlabelをキーにしてマップ。labelresourceへ。
		if(NSflag){listLabel = elementLabelLink.getElementsByTagName("label");}
		else{listLabel = elementLabelLink.getElementsByTagNameNS(getNamespaceMapping().get("link"),"label");}
		for(int indexLabel =0; indexLabel < listLabel.getLength(); indexLabel++){
			Element elementLabel = (Element) listLabel.item(indexLabel);
			analyzeResource(elementLabel);
		}

		NodeList listLabelArc =null;

		//labelArcはクラス作った後、locとlabelをつなげる。from:loc to:label
		if(NSflag){listLabelArc = elementLabelLink.getElementsByTagName("labelArc");}
		else{listLabelArc = elementLabelLink.getElementsByTagNameNS(getNamespaceMapping().get("link"),"labelArc");}
		for(int indexLabelArc =0; indexLabelArc < listLabelArc.getLength(); indexLabelArc++){
			Element elementLabelArc = (Element) listLabelArc.item(indexLabelArc);
			analyzeArc(elementLabelArc);

		}

	}

	@Override
	public void analyzeArc(Element elementArc){
		String type = elementArc.getAttributeNS(getNamespaceMapping().get("xlink"), "type");
		String arcrole = elementArc.getAttributeNS(getNamespaceMapping().get("xlink"), "arcrole");
		String from = elementArc.getAttributeNS(getNamespaceMapping().get("xlink"), "from");
		String to = elementArc.getAttributeNS(getNamespaceMapping().get("xlink"), "to");

		//クラスへ格納
		LabelArc la = new LabelArc(type, arcrole, from, to);
		//keyはfrom/to
		getArcMapping().put(from + "@" + to, la);

	}

	@Override
	public void analyzeResource(Element elementLabel){
		String type = elementLabel.getAttributeNS(getNamespaceMapping().get("xlink"), "type");
		String label = elementLabel.getAttributeNS(getNamespaceMapping().get("xlink"), "label");
		String role = elementLabel.getAttributeNS(getNamespaceMapping().get("xlink"), "role");
		String lang = elementLabel.getAttributeNS("http://www.w3.org/XML/1998/namespace", "lang");
		String id = elementLabel.getAttribute("id");

		//子ノードのテキストがラベル名称
		String labelText = elementLabel.getFirstChild().getNodeValue();

		//クラスへ格納
		LabelResource res = new LabelResource(label, role, type, lang, id, labelText);
		getResourceMapping().put(label, res);
	}
}
