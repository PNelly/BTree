import java.io.Serializable;

/**
 * Created by admin on 7/29/2017.
 */
public class BTreeNode implements Serializable{

    private int numKeys;
    private int numChildren;
    private Long[] keyList;
    private BTreeNode[] childList;
    private BTreeNode parent;
    private int biteOffset;
    /**
     *
     * @param size
     */
    public BTreeNode(int size){
        keyList = new Long[size];
        childList = new BTreeNode[size+1];
        numKeys = 0;
        numChildren = 0;
    }

    public BTreeNode(int size, BTreeNode  parent){
        keyList = new Long[size];
        childList = new BTreeNode[size +1];
        numKeys = 0;
        numChildren = 0;
        this.parent = parent;
    }

    public BTreeNode(BTreeNode parent, Long[] keys, BTreeNode[] children){
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


    /**
     *
     * @param t
     * @return
     */
    public int insertKey(Long t){
        int i = 0;
        while((i < numKeys) && (t > keyList[i])){
            i++;
        }
        numKeys++;
        if(keyList[i] == null){
            keyList[i] = t;
            return i;
        }
        for(int j = numKeys-1; j > i; j--){
            keyList[j] =keyList[j-1];
        }
        keyList[i] = t;
        return i;
    }


    /**
     *
     * @param n
     * @return
     */
    public int insertChild(BTreeNode n){
        int i = 0;
        while((i < numKeys) && (n.getTreeObject(0) > keyList[i])){
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


    /**
     *
     * @param i
     * @return
     */
    public Long getTreeObject(int i){
        if((i > numKeys) || (i < 0)){
            //error
        }
            return keyList[i];
    }


    /**
     *
     * @param i
     * @return
     */
    public BTreeNode getChild(int i){
        if((i > numKeys +1) || (i < 0)){
            //error
        }
        return childList[i];
    }

    public Long[] rightOf(int i){
        Long[] l = new Long[keyList.length];
        //System.arraycopy(keyList,i,l,keyList.length-1,keyList.length);
        System.arraycopy(keyList,i+1,l,0,keyList.length/2);
        cleanRightKeys(i);
        return l;
    }

    public BTreeNode[] getRightChildList(int i) {
        BTreeNode[] c = new BTreeNode[keyList.length+1];
        //System.arraycopy(childList,i,c,keyList.length, keyList.length+1);
        System.arraycopy(childList,i+1,c,0,keyList.length/2+1);
        cleanRightChildren(i+1);
        return c;
    }

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


    public boolean isLeaf(){
         return(numChildren == 0);
    }

    public boolean isFull(){
        return(numKeys == keyList.length);
    }

    public Long findKey(Long key){
        for(int i = 0; i < numKeys; i++){
            if(keyList[i] == key){
                 return key;
            }
        }
        return null;
    }

    public Long getIthKey(int i){
    	return keyList[i];
    }

    public BTreeNode getParent() {
        return parent;
    }

    public void setParent(BTreeNode node){
        parent = node;
    }

    public BTreeNode getChild(Long key) {
        /*int i = 0;
        while((i < numKeys) && (key > keyList[i])){
            i++;
        }
        return childList[i + 1];*/
		int i=0;
		while( (i<numKeys) && (key>keyList[i]) )
		    i++;
		return childList[i];
    }

    public void setBiteOffset(int i){
        biteOffset = i;
    }

    public int getBiteOffset(){
        return biteOffset;
    }
    
    // -- // Test Assistance // -- //
    
    public int getNumKeys(){
    	return numKeys;
    }
    
    public int getNumChildren(){
    	return numChildren;
    }
    
    public BTreeNode getIthChild(int i){
    	return childList[i];
    }
    
    @Override
    public String toString(){
    	return ("keys: "+keyList.toString()+" chx: "+childList.toString());
    }
}
