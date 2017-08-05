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
		NodeStorage.setConfig(degree, "C:\\Users\\Moosejaw\\Desktop\\BTree\\test");
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
			parent.setbyteOffset(NodeStorage.saveNode(parent));
		    node.setParent(parent);
		    root   = parent;
		}
	
		int medianKeyIndex = degree -1;
		parent.insertKey(node.getTreeObject(medianKeyIndex));
		right = new BTreeNode(parent.getbyteOffset(),
				      node.rightOf(medianKeyIndex),
				      node.getRightChildList(medianKeyIndex),
				      NodeStorage.nextWritePos());
		NodeStorage.saveNode(right);
		parent.insertChild(right);
		NodeStorage.updateNode(parent);
		return parent;
    }

    private void recursiveInsert(Long key, BTreeNode node){
	
		if(node.isFull()){
		    node = split(node);
		    //node = node.getParent();
		}
	
		if(!node.isLeaf())
		    recursiveInsert(key, node.getChild(key));
		else
		    node.insertKey(key);

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