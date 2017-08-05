import java.io.Serializable;
import java.util.Arrays;

public class BTreeNode implements Serializable{

    private int numKeys;            //Current number of keys in the node
    private int numChildren;        //Current number of children in the node
    private TreeObject[] keyList;   //Array holding TreeObjects with keys/frequency count
    private BTreeNode[] childList;  //Array holding pointers to child BTreeNode Objects
    private int parent;             //ByteOffset pointer to the parent
    private int byteOffset;         //ByteOffset pointer to this node

    // -- // Constructors // -- //
    
    public BTreeNode(int size){
    	keyList = new TreeObject[size];
        childList = new BTreeNode[size+1];
        numKeys = 0;
        numChildren = 0;
    }

    public BTreeNode(int size, int parent){
    	keyList = new TreeObject[size];
        childList = new BTreeNode[size +1];
        numKeys = 0;
        numChildren = 0;
        this.parent = parent;
    }

    public BTreeNode(int parent, TreeObject[] keys, BTreeNode[] children){
        this.parent = parent;
        this.keyList = keys;
        this.childList = children;
        // catch up counters and reset parents
		for(int i=0; i < keyList.length; i++){
		    if(keyList[i] != null)
		    	numKeys++;
            //TODO: Reimplement this when we can do better checks
            /*
		    if(childList[i] != null){
		    	numChildren++;
		    	childList[i].setParent(byteOffset);
		    }
		    */
		}
        //TODO: Uncomment this when we can do better checks
		//if(childList[childList.length-1] != null) numChildren++;
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
        while( (i < numKeys) && (t > keyList[i].getKey().longValue())){
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
    
    public int getParent() {
        return parent;
    }

    public void setParent(int byteLocation){
        parent = byteLocation;
    }

    public void setbyteOffset(int i){
        byteOffset = i;
    }

    public int getbyteOffset(){
        return byteOffset;
    }

    public TreeObject[] getTreeObjects() {
        return keyList;
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Parent Address: ");
        sb.append(parent);
        sb.append("\n");
        sb.append("KeyList: \n");
        for (int i = 0; keyList[i] != null; i++) {
            sb.append(keyList[i].getFrequency());
            sb.append(" ");
            sb.append(keyList[i].getKey());
            sb.append("\n");
        }
        sb.append("\nChildList: "+Arrays.toString(childList));
        return sb.toString();
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
