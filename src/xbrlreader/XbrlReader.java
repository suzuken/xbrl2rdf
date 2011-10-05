package xbrlreader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XbrlReader implements Reader {
	
	public XPath xpath;
	
	public String xbrlurl;
	public Context context;
	public String schemaRef;
	public Map<String,String> namespace;
	
	//DOMのためのメンバ
	public DOMResult dom;
	//DOMの定義のためのメンバ
	private Element elementDefinitions;
	
	public XbrlReader(String xbrlurl) {
		super();
		this.xbrlurl = xbrlurl;
		this.dom = new DOMResult();
	}
	
	/**
	 * ファイルをパースするメソッド
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws TransformerException
	 * @throws XPathExpressionException 
	 */
	public Boolean parse() throws FileNotFoundException, TransformerException, XPathExpressionException{
		if(!this.prepare()){
			return false;
		}
		
		this.execute();
		
		return true;
	}
	
	/**
	 * 指定されたxbrlファイルのパースの準備をするメソッド
	 * 名前空間のセットを行う
	 * 
	 * @throws FileNotFoundException
	 * @throws TransformerException
	 */
	public Boolean prepare() throws FileNotFoundException, TransformerException{
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(new StreamSource(new FileInputStream(this.xbrlurl)), this.dom);
		
		//各種パラメータを取得する
		Node ts = this.dom.getNode();
		Node nodeRoot = _getRootNode(ts);
		elementDefinitions = (Element) nodeRoot;

		//nodeルートがxsd:schemaじゃなければ、問題あり。
		if (!elementDefinitions.getNamespaceURI().equals("http://www.xbrl.org/2003/instance") || !elementDefinitions.getLocalName().equals("xbrl")) {
			System.out.println("xbrli:xbrl要素が見つかりません。処理スキップします。");
		} else {
			System.out.println("xbrli:xbrl要素の解釈を開始します。");
			_getNamespaceByRoot(elementDefinitions);
		}
		
		return true;
	}
	
	/**
	 * elementのインスタンスであるrootnode(ルート要素)を得る。
	 * なかったらnull
	 * @param node
	 * @return
	 */
	private Node _getRootNode(Node node){
		NodeList nodelist = node.getChildNodes();
		Node r = null;
		for(int i=0;i<nodelist.getLength();i++){
			r=nodelist.item(i);
			if(r instanceof Element==true){break;}
			else{r=null;}
		}
		return r;
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
					setNamespace(current.getLocalName(), str);
				}
			}
		}
	}

	
	public void execute() throws XPathExpressionException{
		XPath xpath = XPathFactory.newInstance().newXPath();
		SimpleNamespaceContext nsc = new SimpleNamespaceContext();
		//現在のnamespaceを挿入
		for (Iterator it = namespace.entrySet().iterator(); it.hasNext();){
			Map.Entry e = (Map.Entry)it.next();
			nsc.addMapping((String)e.getKey(), (String)e.getValue());
		}
		xpath.setNamespaceContext(nsc);
		
		this.xpath = xpath;
	}

	@Override
	public Context getContext(String contextId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getNameSpace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSchemaRef() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUnit(String unitId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValue(String namespace, String elementName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isExistElement(String namespace, String elementName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setNamespace(String namespace, String url){
		this.namespace.put(namespace, url);
	}

}
