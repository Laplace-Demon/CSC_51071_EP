import java.util.concurrent.atomic.AtomicInteger;

public class ex4a2 {
    private LazyNode content = null;
    
    AtomicInteger time = new AtomicInteger(0);
    
    public ex4a2 () {
        this.content = new LazyNode(Integer.MIN_VALUE, new LazyNode(1, new LazyNode(2, new LazyNode(3, new LazyNode(Integer.MAX_VALUE)))));
    }
    
    public boolean validate(LazyNode prev, LazyNode curr) {
        return prev.next != null && curr.next != null && prev.next == curr;
    }
    
    public boolean remove (int key) throws InterruptedException {
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
                        LazyNode temp = curr.next;
                        curr.next = null;
                        Thread.sleep(1000);
                        prev.next = temp;
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
        while (curr != null && key > curr.key) curr = curr.next;
        return curr != null && curr.next != null && key == curr.key;
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
        ex4a2 s = new ex4a2();
        
        Thread t1 = new Thread(() -> {
            boolean result;
            String log = new String("");
            
            log += "t1: call@" + s.time.getAndIncrement() + " contains(3)\n";
            result = s.contains(3);
            log += "t1: rtn@" + s.time.getAndIncrement() + " contains(3, " + result + ")\n";
            
            System.out.println(log);
        });
        
        Thread t2 = new Thread(() -> {
            boolean result = true;
            String log = new String("");
            
            log += "t2: call@" + s.time.getAndIncrement() + " remove(2)\n";
            try {
                result = s.remove(2);
            } catch (InterruptedException e) {
            }
            log += "t2: rtn@" + s.time.getAndIncrement() + " remove(2, " + result + ")\n";
            
            System.out.println(log);
        });
        
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
