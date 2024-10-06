import java.util.concurrent.atomic.AtomicInteger;

public class ex4d {
    private LazyNode content = null;
    
    AtomicInteger time = new AtomicInteger(0);
    
    public ex4d () {
        this.content = new LazyNode(Integer.MIN_VALUE, new LazyNode(Integer.MAX_VALUE));
    }
    
    public boolean validate(LazyNode prev, LazyNode curr) {
        return !prev.tag && !curr.tag && prev.next == curr;
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
    
    @Override
    public String toString() {
        return this.content.toString();
    }
    
    public static void main(String[] args) throws Exception {
        ex4d s = new ex4d();
        
        Thread t1 = new Thread(() -> {
            boolean result;
            String log = new String("");
            log += "t1: call@" + s.time.getAndIncrement() + " add(1)\n";
            result = s.add(1);
            log += "t1: rtn@" + s.time.getAndIncrement() + " add(1, " + result + ")\n";
            
            System.out.println(s.toString());
            
            log += "t1: call@" + s.time.getAndIncrement() + " remove(1)\n";
            result = s.remove(1);
            log += "t1: rtn@" + s.time.getAndIncrement() + " remove(1, " + result + ")\n";
            
            System.out.println(s.toString());
            
            log += "t1: call@" + s.time.getAndIncrement() + " add(1)\n";
            result = s.add(1);
            log += "t1: rtn@" + s.time.getAndIncrement() + " add(1, " + result + ")\n";
            
            System.out.println(s.toString());
            
            System.out.println(log);
        });
        
        t1.start();
        t1.join();
        
    }
}
