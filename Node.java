/**
 * Linked List Node Class
 *
 * @author Michael Burke
 * CS321 Summer 2017
 */
public class Node {
    private Node next;
    private Node prev;
    private BTreeNode data;

    public Node(BTreeNode data) {
        this(data, null, null);
    }

    public Node(BTreeNode data, Node next) {
        this(data, next, null);
    }

    public Node(BTreeNode data, Node next, Node prev) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    public BTreeNode getValue(){
        return this.data;
    }

    public int getByteOffset() { return this.data.getbyteOffset(); }

    public void setParentOffset(int offset) { this.data.setParent(offset); }

    public void setValue(BTreeNode data){
        this.data = data;
    }

    public Node getNext() {
        return this.next;
    }

    public Node getPrev() {
        return this.prev;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public void removeSelf() {
        this.prev.next = this.next;
        this.next.prev = this.prev;
    }
}