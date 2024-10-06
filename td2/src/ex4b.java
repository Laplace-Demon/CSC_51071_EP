import java.util.concurrent.atomic.AtomicInteger;

public class ex4b {
    private LazyNode content = null;
    
    AtomicInteger time = new AtomicInteger(0);
    
    public ex4b () {
        this.content = new LazyNode(Integer.MIN_VALUE, new LazyNode(Integer.MAX_VALUE));
    }
    
    public boolean validate(LazyNode prev, LazyNode curr) {
        return !prev.tag && !curr.tag;
    }
    
    public boolean add (int key) {
        while (true) {
            LazyNode prev, curr;
            prev = this.content;
            curr = prev.next;
            
            while (curr.key < key) {
                prev = prev.next;
                curr = curr.next;
            }
            prev.lock.lock();
            curr.lock.lock();
            
            try {
                if (validate(prev, curr)) {
                    if (key == curr.key) return false;
                    prev.next = new LazyNode(key, curr);
                    return true;
                }
            } finally {
                prev.lock.unlock();
                curr.lock.unlock();
            }
        }
    }
    
    public boolean remove (int key) {
        while (true) {
            LazyNode prev, curr;
            prev = this.content;
            curr = prev.next;
            
            while (curr.key < key) {
                prev = prev.next;
                curr = curr.next;
            }
            prev.lock.lock();
            curr.lock.lock();
            
            try {
                if (validate(prev, curr)) {
                    if (key == curr.key) {
                        curr.tag = true;
                        prev.next = curr.next;
                        return true;
                    }
                    return false;
                }
            } finally {
                prev.lock.unlock();
                curr.lock.unlock();
            }
        }
    }
    
    public boolean contains (int key) {
        LazyNode curr;
        curr = this.content;
        while (key > curr.key) curr = curr.next;
        return !curr.tag && key == curr.key;
    }
    
    public int size() {
        if (this.content == null) return 0;
        return this.content.size() - 2;
    }
    
    @Override
    public String toString() {
        return this.content.toString();
    }
    
    public static void main(String[] args) throws Exception {
        ex4b s = new ex4b();
        
        Thread t1 = new Thread(() -> {
            boolean result;
            String log = new String("");
            
            log += "t1: call@" + s.time.getAndIncrement() + " add(1)\n";
            result = s.add(1);
            log += "t1: rtn@" + s.time.getAndIncrement() + " add(1, " + result + ")\n";
            
            System.out.println(log);
        });
        
        Thread t2 = new Thread(() -> {
            boolean result;
            String log = new String("");
            
            log += "t2: call@" + s.time.getAndIncrement() + " add(1)\n";
            result = s.add(1);
            log += "t2: rtn@" + s.time.getAndIncrement() + " add(1, " + result + ")\n";
            
            System.out.println(log);
        });
        
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
