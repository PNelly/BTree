import java.util.Arrays;

public class BTreeNode {

    private int numKeys;
    private int numChildren;
    private TreeObject[] keyList;
    private int[] childList;
    private int parent;
    private int byteOffset;

    // -- // Constructors // -- //

    public BTreeNode(int size){
        keyList = new TreeObject[size];
        childList = new int[size+1];
        numKeys = 0;
        numChildren = 0;
        parent = -1;      //We're root
        byteOffset = -1;    //Has yet to be determined
        setAllNegative(childList);
        setAllNegative(keyList);
    }

    public BTreeNode(int size, int parent){
        keyList = new TreeObject[size];
        childList = new int[size +1];
        numKeys = 0;
        numChildren = 0;
        setAllNegative(childList);
        setAllNegative(keyList);
        this.parent = parent;
    }

    public BTreeNode(int parent, TreeObject[] keys, int[] children, int byteOffset){
        this.parent = parent;
        this.keyList = keys;
        this.childList = children;
        this.byteOffset = byteOffset;
        // catch up counters and reset parents
        for(int i=0; i<keyList.length; i++){
            if(keyList[i] != null)
                numKeys++;
            if(childList[i] != -1){
                numChildren++;
            }
        }
        if(childList[childList.length-1] != -1) numChildren++;
    }

    // -- // Public Methods // -- //

    // -- // Status // -- //

    public boolean isLeaf(){
        return(numChildren == 0);
    }

    public boolean isFull(){
        return(numKeys == keyList.length);
    }

    public int numKeys() { return numKeys; }

    // -- // Insertions // -- //

    public int insertKey(Long t){
        try {
            int i = 0;
            while ((i < numKeys) && (t > keyList[i].getKey().longValue())) {
                i++;
            }
            numKeys++;
            if (keyList[i] == null) {
                keyList[i] = new TreeObject(t);
                return i;
            } else {
                // handle duplicates
                if (t == keyList[i].getKey().longValue()) {
                    keyList[i].incrementFrequency();
                    numKeys--;
                    return i;
                }
            }
            for (int j = numKeys - 1; j > i; j--) {
                keyList[j] = keyList[j - 1];
            }
            keyList[i] = new TreeObject(t);
            return i;
        } catch (Exception e) {
            System.out.println("Crashed inserting a key" + e);
        }
        return -1;
    }


    public int insertChild(BTreeNode n){
        //If our offset is 0, we haven't saved the node.  Take care of that now.

        //TODO: This is sloppy the check is currently inside this method call, need to extract
        n.getbyteOffset();

        int i = 0;
        while( (i<numKeys) && (n.getTreeObject(0) > keyList[i].getKey()) ){
            i++;
        }
        numChildren++;
        if(i == numKeys +1){
            childList[i] = n.getbyteOffset();
            return i;
        }
        for(int j = numKeys; j > i; j--){
            childList[j] = childList[j-1];
        }
        childList[i] = n.getbyteOffset();
        return i;
    }

    // -- // Getters and Setters // -- //

    public int getParentByte() {
        return parent;
    }

    public BTreeNode getParent() {
        if (parent == -1)
            return null; //We're the root

        return NodeStorage.loadNode(parent);
    }

    public void setParent(int parentByte) {
        parent = parentByte;
    }

    public void setParent(BTreeNode node){
        parent = node.getbyteOffset();
    }

    public void setbyteOffset(int i){
        byteOffset = i;
    }

    public int getbyteOffset(){
        //If byteOffset is -1, it's a new tree and we havent saved this node yet
        if (byteOffset == -1)
            byteOffset = NodeStorage.nextWritePos();
            //byteOffset = NodeStorage.saveNode(this);
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

    public int[] getChildList() {
        return childList;
    }

    public BTreeNode getChild(int i){
        if((i > numKeys +1) || (i < 0)){
            //error
        }
        return NodeStorage.loadNode(childList[i]);
    }

    public BTreeNode getChild(Long key) {
        int i=0;
        while( (i<numKeys) && (key>keyList[i].getKey()) )
            i++;
        return NodeStorage.loadNode(childList[i]);
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
        for(i = keyList.length/2+1; i < c.length; i++)
            c[i] = -1;
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

    private void setAllNegative(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = -1;
        }
    }

    private void setAllNegative(TreeObject[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null)
                arr[i].setKey(-1);
        }
    }

    private void cleanRightKeys(int i){
        for(int j = i; j<keyList.length; j++){
            keyList[j] = null;
            numKeys--;
        }
    }

    private void cleanRightChildren(int i){
        for(int j = i; j<childList.length; j++){
            if(childList[j] != -1){
                childList[j] = -1;
                numChildren--;
            }
        }
    }


    public boolean equals(BTreeNode n) {
        if (byteOffset != n.getbyteOffset()) {
            System.out.println("Byte offset does not match!");
            return false;
        }
        if (parent != n.getParentByte()) {
            System.out.println("Parent does not match!");
            return false;
        }
        if (numKeys != n.numKeys) {
            System.out.println("numKey mismatch");
            return false;
        }
        for (int i = 0; i < keyList.length && keyList[i] != null; i++) {
            if(n.findKey(keyList[i].getKey()) != keyList[i].getKey()) {
                System.out.println("Couldn't find key: "+keyList[i].getKey());
                return false;
            }
        }
        int[] nChild = n.getChildList();
        for (int i = 0; i < childList.length; i++) {
            if (nChild[i] != childList[i]) {
                System.out.println("Child mismatch on index: "+i);
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nNode Address: ");
        sb.append(byteOffset);
        sb.append("\nParent Address: ");
        sb.append(parent);
        sb.append("\nKeyList: \n");
        for(int i=0; i<keyList.length;i++){
            if(keyList[i]!=null){
                sb.append(keyList[i].getFrequency());
                sb.append(" ");
                sb.append(keyList[i].getKey());
                sb.append("\n");
            }
        }
        sb.append("ChildList: "+ Arrays.toString(childList));
        return sb.toString();
    }

}
