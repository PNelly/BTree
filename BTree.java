import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * BTree class for DNA parsing processing assignment
 * Depends on BTreeNode class
 *
 * @author Patrick Doudy
 * CS321 Summer 2017
 */

public class BTree {

    // -- // Fields // -- //
	
    private int degree;
    private int maxKeys;
    private BTreeNode root;

    // -- // Constructor // -- //
    
    public BTree(int degree){
		this.degree  = degree;
		this.maxKeys = degree*2 -1;
		this.root    = new BTreeNode(maxKeys);
    }

    public BTree(String filename) throws IOException {
    	int p2 = filename.lastIndexOf(".");
    	int p1 = filename.lastIndexOf(".", p2-1);
    	degree = Integer.parseInt(filename.substring(p1+1,p2));		//probably need to handle NumberFormatException here, but IntelliJ didn't say anything
    	maxKeys = degree*2 -1;
    	NodeStorage.setConfig(degree, filename);
    	root = NodeStorage.loadNode(NodeStorage.getRoot());
	}

    // -- // Public Methods // -- //
    
    public void insert(Long key){
    		recursiveInsert(key, root);
    }

    public void insert(long key){
    	insert(new Long(key));
    }

    public Long find(Long key){
    	return recursiveSearch(key, root);
    }

    public Long find(long key){
    	return find(new Long(key));
    }

    // -- // Private Methods // -- //

	//On split we want to reassign to the new parent node, so return that
    private BTreeNode split(BTreeNode node){

		BTreeNode parent, right;
	    parent = node.getParent();
	
		if(parent == null){
		    parent = new BTreeNode(maxKeys);
			parent.insertChild(node);
			//parent.setbyteOffset(NodeStorage.saveNode(parent));
            parent.setbyteOffset(NodeStorage.nextWritePos());
		    node.setParent(parent.getbyteOffset());
		    root   = parent;
		}
	
		int medianKeyIndex = degree -1;
		parent.insertKey(node.getTreeObject(medianKeyIndex));
		right = new BTreeNode(parent.getbyteOffset(),
				      node.rightOf(medianKeyIndex),
				      node.getRightChildList(medianKeyIndex),
				      NodeStorage.nextWritePos()+NodeStorage.getNodeSize());
		//NodeStorage.saveNode(right);
		//right.equals(NodeStorage.loadNode(right.getbyteOffset()));
		parent.insertChild(right);
		//NodeStorage.updateNode(node);
		//NodeStorage.updateNode(parent);
		if(root.getbyteOffset() == parent.getbyteOffset()) {
			NodeStorage.setRootLocation(root.getbyteOffset());
			root = parent;
		}
		NodeStorage.saveManyNodes(new BTreeNode[] {node, parent, right}, right.getChildList(), right.getbyteOffset());
		//NodeStorage.updateChildren(right.getChildList(), right.getbyteOffset());

		return parent;
    }

    private void recursiveInsert(Long key, BTreeNode node){
	
		if(node.isFull()){
		    node = split(node);
		    //node = node.getParent();
		}
	
		if(!node.isLeaf())
		    recursiveInsert(key, node.getChild(key));
		else {
			node.insertKey(key);
			NodeStorage.updateNode(node);
		}

    }

    private Long recursiveSearch(Long key, BTreeNode node){

		if(node == null)
			return null;
	
		Long objective = node.findKey(key);
	
		if(objective != null)
		    return objective;
	
		return recursiveSearch(key, node.getChild(key));

    }

}