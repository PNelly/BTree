import java.util.Random;

public class BTreeNodeTest {

    private static int nodesize = 4;
    public static void main(String[] args){
        BTreeNode node = new BTreeNode(nodesize);
        for(int i = 0; i < nodesize; i++){
            node.insertKey(randomTObject());
        }

        System.out.println("Print out of the keys of each object in test node");
        for(int j =  0; j < nodesize; j++){
            System.out.println(node.getTreeObject(j).getKey());
        }
        System.out.println();

        for(int i = 0; i < nodesize; i++){
            node.insertChild(randomBTreeNode());
        }

        for(int j =  0; j < nodesize; j++){
            System.out.println("Print out of the keys of each object in child " + j + " of test node");
            for(int k = 0; k < nodesize; k++) {
                System.out.println(node.getChild(j).getTreeObject(k).getKey());
            }
            System.out.println();
        }

    }

    private static TreeObject randomTObject(){
        Random rand = new Random();
        TreeObject t = new TreeObject(rand.nextLong());
        return t;
    }

    private static BTreeNode randomBTreeNode(){
        BTreeNode node = new BTreeNode(nodesize);
        for(int i = 0; i < nodesize; i++){
            node.insertKey(randomTObject());
        }
        return node;
    }


}
