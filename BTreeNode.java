import java.io.Serializable;

/**
 * Created by admin on 7/29/2017.
 */
public class BTreeNode implements Serializable {

    private int numKeys;
    private BTreeNode parent;
    private Long[] keyList;
    private BTreeNode[] childList;

    /**
     *
     * @param size
     */
    public BTreeNode(int size){
        this(size, null);
    }

    public BTreeNode(int size, BTreeNode parent) {
        this.parent = parent;
        keyList = new Long[size];
        childList = new BTreeNode[size+1];
        numKeys = 0;
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
        while((i < numKeys+1) && (n.getTreeObject(0) > keyList[i])){
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

}
