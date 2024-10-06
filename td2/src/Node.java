import java.util.concurrent.locks.*;

public class Node {
    public int key;
    public Node next;
    Lock lock;
    
    public Node (int key) {
        this.key = key;
        this.next = null;
        this.lock = new ReentrantLock();
    }
    
    public Node (int key, Node next) {
        this.key = key;
        this.next = next;
        this.lock = new ReentrantLock();
    }
    
    public int size () {
        if (this.next == null) return 1;
        return 1 + this.next.size();
    }
}
