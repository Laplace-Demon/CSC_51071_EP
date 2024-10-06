public class OptiSet {
    private Node content = null;
    
    public OptiSet () {
        this.content = new Node(Integer.MIN_VALUE, new Node(Integer.MAX_VALUE));
    }

    public boolean validate (Node prev, Node curr) {
        Node node = this.content;
        while (node.key < prev.key) node = node.next;
        return node == prev && node.next == curr;
    }
    
    public boolean add (int key) {
        while (true) {
            Node prev, curr;
            prev = this.content;
            curr = prev.next;
            
            while (curr.key < key) {
                prev = curr;
                curr = curr.next;
            }
            prev.lock.lock();
            curr.lock.lock();
            
            try {
                if (validate(prev, curr)) {
                    if (curr.key == key) return false;
                    prev.next = new Node(key, curr);
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
            Node prev, curr;
            prev = this.content;
            curr = prev.next;
            
            while (curr.key < key) {
                prev = curr;
                curr = curr.next;
            }
            
            prev.lock.lock();
            curr.lock.lock();
            
            try {
                if (validate(prev, curr)) {
                    if (curr.key == key) {
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
        while (true) {
            Node prev, curr;
            prev = this.content;
            curr = prev.next;
            
            while (curr.key < key) {
                prev = curr;
                curr = curr.next;
            }
            
            prev.lock.lock();
            curr.lock.lock();
            
            try {
                if (validate(prev, curr)) {
                    if (curr.key == key) return true;
                    return false;
                }
            } finally {
                prev.lock.unlock();
                curr.lock.unlock();
            }
        }
    }
    
    public int size () {
        if (this.content == null) return 0;
        return this.content.size() - 2;
    }
    
    public static void main (String[] args) throws Exception {
        OptiSet s = new OptiSet();
        
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
