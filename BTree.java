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
    	return find(new Long(key));
    }

    // -- // Private Methods // -- //

    private void split(BTreeNode node){

		BTreeNode parent, right;
	    parent = node.getParent();
	
		if(parent == null){
		    parent = new BTreeNode(maxKeys);
		    node.setParent(parent);
		    root   = parent;
		    parent.insertChild(node);
		}
	
		int medianKeyIndex = degree / 2;
		parent.insertKey(node.getIthKey(medianKeyIndex));
		right = new BTreeNode(parent,
				      node.rightOf(medianKeyIndex),
				      node.getRightChildList(medianKeyIndex));
	
		parent.insertChild(right);

    }

    private void recursiveInsert(Long key, BTreeNode node){
	
		if(node.isFull()){
		    split(node);
		    node = node.getParent();
		}
	
		if(!node.isLeaf())
		    recursiveInsert(key, node.getChild(key));
		else
		    node.insertKey(key);

    }

    private Long recursiveSearch(Long key, BTreeNode node){

		if(node == null) return null;
	
		Long objective = node.findKey(key);
	
		if(objective != null)
		    return objective;
	
		return recursiveSearch(key, node.getChild(key));

    }
    
    // -- // Test Assistance // -- //
    
    public void testTraversal(){
    	testTraversal(root);
    }
    
    public void testTraversal(BTreeNode node){
    	System.out.println(node.toString());
    	for(int i=0;i<node.getNumChildren();i++){
    		if(node.getIthChild(i)!=null)
    			testTraversal(node.getIthChild(i));
    	}
    	
    }

}