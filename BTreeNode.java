import java.io.Serializable;

/**
 * Created by admin on 7/29/2017.
 */
public class BTreeNode implements Serializable {

    private int numKeys;
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
    }

    public BTreeNode(int size, BTreeNode  parent){
        keyList = new Long[size];
        childList = new BTreeNode[size +1];
        numKeys = 0;
        this.parent = parent;
    }

    public BTreeNode(BTreeNode parent, Long[] keys, BTreeNode[] children){
        this.parent = parent;
        keyList = keys;
        this.childList = children;
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
        System.arraycopy(keyList,i,l,keyList.length-1,keyList.length);
        return l;
    }

    public BTreeNode[] getRightChildList(int i) {
        BTreeNode[] c = new BTreeNode[keyList.length+1];
        System.arraycopy(childList,i,c,keyList.length, keyList.length+1);
        return c;
    }

    public boolean isLeaf(){
         return(childList.length == 0);
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

    public BTreeNode getParent() {
        return parent;
    }

    public void setParent(BTreeNode node){
        parent = node;
    }

    public BTreeNode getChild(Long key) {
        int i = 0;
        while((i < numKeys) && (key > keyList[i])){
            i++;
        }
        return childList[i + 1];
    }

    public void setBiteOffset(int i){
        biteOffset = i;
    }

    public int getBiteOffset(){
        return biteOffset;
    }
}
