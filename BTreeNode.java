import java.io.Serializable;

public class BTreeNode implements Serializable{

    private int numKeys;
    private int numChildren;
    private TreeObject[] keyList;
    private BTreeNode[] childList;
    private BTreeNode parent;
    private int biteOffset;

    // -- // Constructors // -- //
    
    public BTreeNode(int size){
    	keyList = new TreeObject[size];
        childList = new BTreeNode[size+1];
        numKeys = 0;
        numChildren = 0;
    }

    public BTreeNode(int size, BTreeNode  parent){
    	keyList = new TreeObject[size];
        childList = new BTreeNode[size +1];
        numKeys = 0;
        numChildren = 0;
        this.parent = parent;
    }

    public BTreeNode(BTreeNode parent, TreeObject[] keys, BTreeNode[] children){
        this.parent = parent;
        this.keyList = keys;
        this.childList = children;
        // catch up counters and reset parents
		for(int i=0; i<keyList.length; i++){
		    if(keyList[i] != null) 
		    	numKeys++;
		    if(childList[i] != null){ 
		    	numChildren++;
		    	childList[i].setParent(this);
		    }
		}
		if(childList[childList.length-1] != null) numChildren++;
    }
    
    // -- // Public Methods // -- //
    
    	// -- // Status // -- //
    
    public boolean isLeaf(){
        return(numChildren == 0);
   }

   public boolean isFull(){
       return(numKeys == keyList.length);
   }

    	// -- // Insertions // -- //
    
    public int insertKey(Long t){
        int i = 0;
        while( (i<numKeys) && (t > keyList[i].getKey().longValue())){
            i++;
        }
        numKeys++;
        if(keyList[i] == null){
        	keyList[i] = new TreeObject(t);
            return i;
        } else {
        	// handle duplicates
            if(t == keyList[i].getKey().longValue()){
            	keyList[i].incrementFrequency();
            	numKeys--;
            	return i;
            }
        }
        for(int j = numKeys-1; j > i; j--){
            keyList[j] =keyList[j-1];
        }
        keyList[i] = new TreeObject(t);
        return i;
        
    }


    public int insertChild(BTreeNode n){
        int i = 0;
        while( (i<numKeys) && (n.getTreeObject(0) > keyList[i].getKey()) ){
         i++;
        }
        numChildren++;
        if(i == numKeys +1){
            childList[i] = n;
            return i;
        }
        for(int j = numKeys; j > i; j--){
            childList[j] = childList[j-1];
        }
        childList[i] = n;
        return i;
    }

     	// -- // Getters and Setters // -- //
    
    public BTreeNode getParent() {
        return parent;
    }

    public void setParent(BTreeNode node){
        parent = node;
    }

    public void setBiteOffset(int i){
        biteOffset = i;
    }

    public int getBiteOffset(){
        return biteOffset;
    }
    
    public Long getTreeObject(int i){
        if((i > numKeys) || (i < 0)){
            //error
        }
        return keyList[i].getKey();
    }


    public BTreeNode getChild(int i){
        if((i > numKeys +1) || (i < 0)){
            //error
        }
        return childList[i];
    }
    
    public BTreeNode getChild(Long key) {
		int i=0;
		while( (i<numKeys) && (key>keyList[i].getKey()) )
		    i++;
		return childList[i];
    }

    public TreeObject[] rightOf(int i){
        TreeObject[] l = new TreeObject[keyList.length];
        System.arraycopy(keyList,i+1,l,0,keyList.length/2);
        cleanRightKeys(i);
        return l;
    }
    
    public BTreeNode[] getRightChildList(int i) {
        BTreeNode[] c = new BTreeNode[keyList.length+1];
        System.arraycopy(childList,i+1,c,0,keyList.length/2+1);
        cleanRightChildren(i+1);
        return c;
    }

    public Long findKey(Long key){
        for(int i = 0; i < numKeys; i++){
            if(keyList[i].getKey() == key.longValue()){
                 return key;
            }
        }
        return null;
    }
    
    // -- // Private Methods // -- //
    
    private void cleanRightKeys(int i){
		for(int j = i; j<keyList.length; j++){
		    keyList[j] = null;
		    numKeys--;
		}
    }

    private void cleanRightChildren(int i){
		for(int j = i; j<childList.length; j++){
			if(childList[j]!=null){
		        childList[j] = null;
		        numChildren--;
			}
		}
    }
    
}
