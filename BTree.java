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
		NodeStorage.setSize(degree);
		NodeStorage.setMetaData();
    }

    // -- // Public Methods // -- //
    
    public void insert(Long key){
    	recursiveInsert(key, root);
    }

    public void insert(long key){
    	insert(new Long(key));
    }

    public Long find(Long key){
    	//return recursiveSearch(key, root);
		return searchTree(key, root);
    }

    public Long find(long key){
    	return find(new Long(key));
    }

    // -- // Private Methods // -- //

    private void split(BTreeNode node){

		BTreeNode parent, right;
	    int pAddress = node.getParent();
	
		if(pAddress == -1){
		    parent = new BTreeNode(maxKeys);
			parent.insertChild(node);
			//TODO: Error handling for bad saves (return -1)
			parent.setbyteOffset(saveNode(parent));
		    node.setParent(parent.getByteOffset());
		    root   = parent;

		} else {
			parent = loadNode(node.getParent());
		}
	
		int medianKeyIndex = degree -1;
		parent.insertKey(node.getTreeObject(medianKeyIndex));
		right = new BTreeNode(parent.getByteOffset(),
				      node.rightOf(medianKeyIndex),
				      node.getRightChildList(medianKeyIndex));
		right.setbyteOffset(saveNode(right));
		parent.insertChild(right);
		updateNode(parent);
		updateNode(node);

    }

    private void recursiveInsert(Long key, BTreeNode node){
	
		if(node.isFull()){
		    split(node);
		    node = loadNode(node.getParent());
		}
	
		if(!node.isLeaf())
		    recursiveInsert(key, loadNode(node.getChild(key)));
		else
		    node.insertKey(key);
		updateNode(node);
    }

	private Long searchTree(Long key, BTreeNode node) {
		if (node == null)
			return null;
		Long objective;
		while ((objective = node.findKey(key)) == null) {
			node = loadNode(node.getChild(key));
			if (node.isLeaf()) {
				System.out.println("FUCK");
				System.exit(-1);
			}
		}
		return objective;
	}

	/*
    private Long recursiveSearch(Long key, BTreeNode node){

		if(node == null)
			return null;
	
		Long objective = node.findKey(key);
	
		if(objective != null)
		    return objective;
	
		return recursiveSearch(key, loadNode(node.getChild(key)));

    }
    */

	private BTreeNode loadNode(int byteAddress) {
		return NodeStorage.readAt(byteAddress);
	}

	private int saveNode(BTreeNode n) {
		return (int)NodeStorage.writeNext(n);
	}

	private void updateNode(BTreeNode n) {
		//Make sure to update our offset in the event this is a new node we're inserting on
		n.setbyteOffset((int)NodeStorage.writeAt(n.getByteOffset(), n));
	}

}