public class LazySet {
    private LazyNode content = null;
    
    public LazySet () {
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
    
    public static void main(String[] args) throws Exception {
        LazySet s = new LazySet();
        
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; ++i) s.add(i);
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 100; i < 200; ++i) s.add(i);
        });
        
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        
        System.out.println("Size: " + s.size());
    }
}
