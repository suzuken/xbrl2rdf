package xbrlreader;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * @author suzuken
 *
 *　xpathのための簡単なnamespace contextの実装
 */
public class SimpleNamespaceContext implements NamespaceContext {
  
  private HashMap<String,String> prefixToUri=new HashMap<String,String>();
  private HashMap<String,String> UriToPrefix=new HashMap<String,String>();
  
  public void addMapping(String prefix, String uri) {
    prefixToUri.put(prefix,uri);
    UriToPrefix.put(uri,prefix);
  }
  
  public SimpleNamespaceContext() {
    addMapping(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
    addMapping(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
  }

  public String getNamespaceURI(String prefix) {
    if (prefix==null) throw new IllegalArgumentException();
    String result=prefixToUri.get(prefix);
    return result!=null? result : XMLConstants.NULL_NS_URI;
  }

  public String getPrefix(String namespaceURI) {
    if (namespaceURI==null) throw new IllegalArgumentException();
    String result=UriToPrefix.get(namespaceURI);
    return result!=null? result : null;
  }

  @SuppressWarnings("unchecked")
  public Iterator getPrefixes(String namespaceURI) {
    String prefix=getPrefix(namespaceURI);
    ArrayList<String> al=new ArrayList<String>();
    if (prefix!=null) al.add(prefix);
    return al.iterator();
  }
}
