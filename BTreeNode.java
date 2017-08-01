/**
 * Created by admin on 7/29/2017.
 */
public class BTreeNode {

    private int numKeys;
    private TreeObject[] keyList;
    private BTreeNode[] childList;

    /**
     *
     * @param size
     */
    public BTreeNode(int size){
        keyList = new TreeObject[size];
        childList = new BTreeNode[size+1];
        numKeys = 0;
    }


    /**
     *
     * @param t
     * @return
     */
    public int insertKey(TreeObject t){
        int i = 0;
        while((i < numKeys) && (t.getKey() < keyList[i].getKey())){
            i++;
        }
        numKeys++;
        if(keyList[i] == null){
            keyList[i] = t;
            return i;
        }
        for(int j = numKeys; j > i; j--){
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
        while((i < numKeys+1) && (n.getTreeObject(0).getKey() < keyList[i].getKey())){
         i++;
        }
        if(i == numKeys +1){
            childList[i] = n;
            return i;
        }
        for(int j = numKeys +1; j > i; j--){
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
    public TreeObject getTreeObject(int i){
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
