import java.util.ArrayList;

/**
 * Author: Michael Burke
 * Class:  CS321
 */
public class Cache {
    private Node head;
    private Node tail;
    private int maxSize;
    private int curSize;
    private int hits;
    private int refs;

    public Cache(int maxSize) {
        head = tail = null;
        curSize = hits = refs = 0;
        this.maxSize = maxSize;
    }

    public void updateNode(BTreeNode data) {
        Node toUpdate = findNode(data);
        if (toUpdate == null) {
            addObject(data);
        } else {
            toUpdate.setValue(data);
            moveToFront(toUpdate);
        }
    }

    public void updateChildNode(int byteOffset, int parentOffset) {
        Node child = findNode(byteOffset);
        if (child == null) {
            BTreeNode bNode = NodeStorage.loadNode(byteOffset);
            bNode.setParent(parentOffset);
            addObject(bNode);
        } else {
            child.setParentOffset(parentOffset);
            moveToFront(child); //Not sure if this is worth having or not?
        }
    }

    public Node findNode(int byteOffset) {
        if (head == null)
            return null;
        Node walk = head;
        while (!(walk.getByteOffset() == byteOffset) && walk.getNext() != null)
            walk = walk.getNext();
        if (walk.getByteOffset() != byteOffset)
            return null; //This is a dumb bug and I can't be bothered to fix it more elegantly
        return walk;
    }

    /**
     findNode takes in an Object and searches linearly through the
     linked list, and returns the node that contains the data
     or a null pointer if it is not found
     */
    public Node findNode(BTreeNode data) {
        return findNode(data.getbyteOffset());
    }

    /**
     getObject returns true or false based on whether or not the object
     exists within the cache.  If it does, it calls moveToFront.
     If it does not, it calls addObject.
     */
    public boolean getObject(BTreeNode data) {
        refs++;
        Node walk = findNode(data);
        if(walk == null) {
            addObject(data);
            return false;
        }
        if (walk.getValue().equals(data)) {
            if (walk != head)
                moveToFront(walk);
            hits++;
            return true;
        } else {
            addObject(data);
            return false;
        }
    }

    /**
     addObject takes in an Object and puts it at the front of the cache
     */
    public void addObject(BTreeNode data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = tail = newNode;
            curSize++;
        } else {

            //Add new nodes to the front
            newNode.setNext(head);
            head.setPrev(newNode);
            head = newNode;

            if(curSize == maxSize) {
                //Remove the tail (least accessed) node
                NodeStorage.saveFromCache(tail.getValue());
                tail.getPrev().setNext(null);
                tail = tail.getPrev();
            } else {
                curSize++; //Only increment if we're not already full
            }
        }
    }

    /**
     moveToFront takes in a node to shift to the front of the cache
     */
    public void moveToFront(Node moveNode) {
        if(head == moveNode)
            return;
        if(tail == moveNode) {
            tail.getPrev().setNext(null);
            tail = tail.getPrev();
        } else {
            moveNode.removeSelf();
        }
        moveNode.setNext(head);
        moveNode.setPrev(null);
        head.setPrev(moveNode);
        head = moveNode;
    }

    public void dumpNodesToFile() {
        Node node = head;
        if (head == null) //Condition for if the file never had a sequence
            return;
        ArrayList<BTreeNode> bNodeList = new ArrayList<>();
        while(node.getNext() != null) {
            bNodeList.add(node.getValue());
            node = node.getNext();
        }

        bNodeList.add(tail.getValue());
        NodeStorage.dumpNodes(bNodeList.toArray(new BTreeNode[bNodeList.size()]));
    }

    public int getHits(){
        return hits;
    }

    public int getRefs(){
        return refs;
    }

}