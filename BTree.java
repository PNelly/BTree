/**
 * BTree class for DNA parsing processing assignment
 * Depends on BTreeNode class
 *
 * @author Patrick Doudy
 * CS321 Summer 2017
 */

public class BTree {

    // ** Need degree -> size conversion
    // ** check cleanup of get right methods
    // ** getChild does not make null return

    private int degree;
    private BTreeNode root;

    public BTree(int degree){
	this.degree = degree;
	root = new BTreeNode(degree);
    }

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

    private void split(BTreeNode node){
	
	BTreeNode parent, right;
        parent = node.getParent();

	if( parent == null){
	    parent = new BTreeNode(degree);
	    node.setParent(parent);
	    root = parent;
	    parent.insertChild(node);
	}

	Long medianKey = node.getIthKey(degree/2); // **
	parent.insertKey(medianKey);
	right = new BTreeNode(degree,
			      parent,
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

	recursiveSearch(key, node.getChild(key)); // **

    }

}