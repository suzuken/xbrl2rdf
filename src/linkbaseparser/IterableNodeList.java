package linkbaseparser;

/* IterableNodeList.java */
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IterableNodeList implements Iterable<Node>, NodeList {
    private NodeList nodeList;
    
    public IterableNodeList(NodeList nodeList) {
        this.nodeList = nodeList;
    }
    
    public java.util.Iterator<Node> iterator() {
        return new NodeListIterator(nodeList);
    }
    
    public Node item(int index) {
        return nodeList.item(index);
    }
    
    public int getLength() {
        return nodeList.getLength();
    }
}