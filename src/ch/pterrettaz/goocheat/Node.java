package ch.pterrettaz.goocheat;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Node {

    private final Map<Integer, Node> children;
    private final Node parent;
    private final int key;
    private boolean hasData = false;
    
    public Node() {
    	this(null, -1);
    }
    
    public Node(Node parent, int key) {
		this.parent = parent;
		this.children = new LinkedHashMap<Integer, Node>();
		this.key = key;
    }
    
    public boolean isLeaf() {
    	return children.size() == 0;
    }
    
    public void add(String token) {
    	if (token.length() == 0) {
    	    hasData = true;
    	} else {
    	    int key;
    	    do {
    		key = token.charAt(0);
    		token = token.substring(1);
    	    } while (token.length() > 1 && key == ' ');
    	    
    	    Node child = children.get(key);
    	    if (child == null) {
    		child = new Node(this, key);
    	    }
    	    child.add(token);
    	    children.put(key, child);
    	}
        }
    
    public Node getParent() {
    	return parent;
    }
    
    public Collection<Node> getChildren() {
    	return children.values();
    }
    
    public Node getChild(int key) {
    	return children.get(key);
    }
    
    public int getKey() {
    	return key;
    }

    public boolean isRoot() {
    	return getKey() == -1;
    }
    
    public boolean hasPrefix(String s) {
    	if (s.length() > 0) {
			Node child = getChild(s.charAt(0));
			if (child != null)
				return child.hasPrefix(s.substring(1));
			return false;
		}
    	return true;
    }
    
    @Override
    public String toString() {
    	char k = (char)key;
    	return k + "";
    }

	public boolean exists(String s) {
		if (s.length() > 0) {
			Node child = getChild(s.charAt(0));
			if (child == null) {
				return false;
			} else {
				return child.exists(s.substring(1));
			}
		}
		return hasData;
	}
}
