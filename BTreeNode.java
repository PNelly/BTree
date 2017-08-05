import java.io.Serializable;
import java.util.Arrays;

public class BTreeNode implements Serializable{

    private int numKeys;            //Current number of keys in the node
    private int numChildren;        //Current number of children in the node
    private TreeObject[] keyList;   //Array holding TreeObjects with keys/frequency count
    private int[] childList;        //Array holding pointers to child BTreeNode Objects
    private int parent;             //ByteOffset pointer to the parent. -1 indicates this the root node.
    private int byteOffset;         //ByteOffset pointer to this node

    // -- // Constructors // -- //
    
    public BTreeNode(int size){
    	keyList = new TreeObject[size];
        childList = new int[size+1];
        numKeys = 0;
        numChildren = 0;
        parent = -1; //We're a root!
        byteOffset = -1; //Freshly made node, hasn't been saved yet
    }

    public BTreeNode(int size, int parent){
    	keyList = new TreeObject[size];
        childList = new int[size +1];
        numKeys = 0;
        numChildren = 0;
        this.parent = parent;
    }

    public BTreeNode(int parent, TreeObject[] keys, int[] children){
        this.parent = parent;
        this.keyList = keys;
        this.childList = children;
        // catch up counters and reset parents
		for(int i=0; i < keyList.length; i++){
		    if(keyList[i] != null)
		    	numKeys++;
		    if(childList[i] != 0){
		    	numChildren++;
                //Not sure what the purpose of this was when it was an obj ref?
		    	//childList[i].setParent(byteOffset);
		    }
		}
        //One extra check since childList is 1 longer than keyList
        if(childList[childList.length-1] != 0) numChildren++;
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
        while( (i < numKeys) && (n.getTreeObject(0) > keyList[i].getKey()) ){
         i++;
        }
        numChildren++;

        //If we're past the edge of the keys, insert on the tail and return
        if(i == numKeys +1){
            childList[i] = n.getByteOffset();
            return i;
        }

        //Otherwise shift everything to open to correct index for the child
        for(int j = numKeys; j > i; j--){
            childList[j] = childList[j-1];
        }
        //Insert and return
        childList[i] = n.getByteOffset();
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

    public int getByteOffset(){
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

    public int[] getChildPointers() {
        return childList;
    }

    public int getChild(int i){
        if((i > numKeys +1) || (i < 0)){
            //error
        }
        return childList[i];
    }
    
    public int getChild(Long key) {
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
    
    public int[] getRightChildList(int i) {
        int[] c = new int[keyList.length+1];
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
			if( childList[j] != -1){
		        childList[j] = -1;
		        numChildren--;
			}
		}
    }
    
}
