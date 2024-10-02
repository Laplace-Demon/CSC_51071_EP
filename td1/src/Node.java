public class Node {
    public int key;
    public Node next;
    
    public Node(int key) {
        this.key = key;
        this.next = null;
    }
    
    public Node(int key, Node next) {
        this.key = key;
        this.next = next;
    }
    
    public int size() {
        if (this.next == null) return 1;
        return 1 + this.next.size();
    }
    
    @Override
    public String toString() {
        if (this.next != null) return key + ", " + this.next.toString();
        return key + "";
    }
}
