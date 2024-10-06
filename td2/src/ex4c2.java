import java.util.concurrent.atomic.AtomicInteger;

public class ex4c2 {
    private LazyNode content = null;
    
    AtomicInteger time = new AtomicInteger(0);
    
    public ex4c2 () {
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
    
    public boolean remove (int key) throws InterruptedException {
        while (true) {
            LazyNode prev, curr;
            prev = this.content;
            curr = prev.next;
            
            while (curr.key < key) {
                prev = prev.next;
                curr = curr.next;
            }
            curr.lock.lock();
            
            try {
                if (validate(prev, curr)) {
                    Thread.sleep(1000);
                    if (key == curr.key) {
                        curr.tag = true;
                        prev.next = curr.next;
                        return true;
                    }
                    return false;
                }
            } finally {
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
        ex4c2 s = new ex4c2();
        
        Thread t1 = new Thread(() -> {
            boolean result;
            String log = new String("");
            
            log += "t1: call@" + s.time.getAndIncrement() + " add(1)\n";
            result = s.add(1);
            log += "t1: rtn@" + s.time.getAndIncrement() + " add(1, " + result + ")\n";
            
            log += "t1: call@" + s.time.getAndIncrement() + " remove(1)\n";
            try {
                result = s.remove(1);
            } catch (InterruptedException e) {
            }
            log += "t1: rtn@" + s.time.getAndIncrement() + " remove(1, " + result + ")\n";
            
            System.out.println(log);
        });
        
        Thread t2 = new Thread(() -> {
            boolean result;
            String log = new String("");
            
            log += "t2: call@" + s.time.getAndIncrement() + " add(2)\n";
            result = s.add(2);
            log += "t2: rtn@" + s.time.getAndIncrement() + " add(2, " + result + ")\n";
            
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
        
        System.out.println("Set elements: { " + s.toString() + " }");
    }
}
