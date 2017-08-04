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
	find(new Long(key));
    }

    // -- // Private Methods // -- //

    private void split(BTreeNode node){
	
	BTreeNode parent, right;
        parent = node.getParent();

	if( parent == null){
	    parent = new BTreeNode(maxKeys);
	    node.setParent(parent);
	    root   = parent;
	    parent.insertChild(node);
	}

	Long medianKey = node.getIthKey(degree/2);
	parent.insertKey(medianKey);
	right = new BTreeNode(parent,
			      node.rightOf(medianKey),
			      node.getRightChildList(medianKey));

	parent.insertChild(right);

    }

    private void recursiveInsert(Long key, BTreeNode node){
	
	if(node.isFull()){
	    split(node);
	    node = node.getParent();
	}

	if(!node.isLeaf())
	    recursiveInsert(key, node.getChild(key));

	node.insertKey(key);

    }

    private Long recursiveSearch(Long key, BTreeNode node){

	if(node == null) return null;

	Long objective = node.findKey(key);

	if(objective != null)
	    return objective;

	return recursiveSearch(key, node.getChild(key));

    }

}