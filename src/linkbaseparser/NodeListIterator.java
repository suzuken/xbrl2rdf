package linkbaseparser;

/* NodeListIterator.java */
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListIterator implements Iterator<Node> {
    private NodeList nodeList;
    private int index = 0;
    private Node cache = null;
    
    public NodeListIterator(NodeList nodeList) {
        this.nodeList = nodeList;
    }
    
    public boolean hasNext() {
        return index < nodeList.getLength();
    }
    
    public Node next() {
        cache = nodeList.item(index++);
        if (cache == null) throw new NoSuchElementException();
        return cache;
    }
    
    public void remove() {
        if (cache == null) throw new IllegalStateException();
        cache.getParentNode().removeChild(cache);
        cache = null;
        index--;
    }
}