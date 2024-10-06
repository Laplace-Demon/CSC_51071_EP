import java.util.concurrent.locks.*;

public class LazyNode {
    public int key;
    public LazyNode next;
    public boolean tag;
    Lock lock;
    
    public LazyNode (int key) {
        this.key = key;
        this.next = null;
        this.tag = false;
        this.lock = new ReentrantLock();
    }
    
    public LazyNode (int key, LazyNode next) {
        this.key = key;
        this.next = next;
        this.tag = false;
        this.lock = new ReentrantLock();
    }
    
    public int size () {
        if (this.next == null) return 1;
        return 1 + this.next.size();
    }
    
    @Override
    public String toString() {
        String temp = "(key: " + this.key + ", tag: " + tag + ")";
        if (this.next != null) temp += " " + this.next.toString();
        return temp;
    }
}
