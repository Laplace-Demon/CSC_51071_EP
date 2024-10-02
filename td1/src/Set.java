import java.util.concurrent.locks.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class Set {
    private Node content = null;
    
    Lock lock = new ReentrantLock();
    
    public boolean add(int k) {
        lock.lock();
        try {
            if (this.content == null) {
                this.content = new Node(k);
                return true;
            }
        
            Node currentNode;
            currentNode = this.content;
            if (k < currentNode.key) {
                this.content = new Node(k, currentNode);
                return true;
            }
        
            if (k == currentNode.key) return false;
        
            Node nextNode;
            nextNode = currentNode.next;
            while (nextNode != null && nextNode.key < k) {
                currentNode = nextNode;
                nextNode = nextNode.next;
            }
            if (nextNode != null && nextNode.key == k) return false;
        
            currentNode.next = new Node(k, nextNode);
            return true;
        } finally {
            lock.unlock();
        }
    }
    
    public boolean remove(int k) {
        lock.lock();
        try {
            if (this.content == null) return false;
            
            Node currentNode;
            currentNode = this.content;
            if (k < currentNode.key) return false;
            
            if (k == currentNode.key) {
                this.content = currentNode.next;
                return true;
            }
            
            Node nextNode;
            nextNode = currentNode.next;
            while (nextNode != null && nextNode.key < k) {
                currentNode = nextNode;
                nextNode = nextNode.next;
            }
            if (nextNode != null && nextNode.key == k) {
                currentNode.next = nextNode.next;
                return true;
            }
            
            return false;
        } finally {
            lock.unlock();
        }
    }
    
    public boolean contains(int k) {
        if (this.content == null) return false;
        
        Node currentNode;
        currentNode = this.content;
        if (k < currentNode.key) return false;
        
        if (k == currentNode.key) return true;
        
        Node nextNode;
        nextNode = currentNode.next;
        while (nextNode != null && nextNode.key < k) {
            currentNode = nextNode;
            nextNode = nextNode.next;
        }
        if (nextNode != null && nextNode.key == k) return true;
        
        return false;
    }
    
    public int size() {
        if (this.content == null) return 0;
        return this.content.size();
    }
    
    @Override
    public String toString() {
        if (this.content == null) return "{ }";
        return "{ " + this.content.toString() + " }";
    }
    
    public static void main(String[] args) throws Exception {
        Set s_our = new Set();
        
        Thread t1_our = new Thread(() -> {
            for (int i = 0; i < 10000; ++i) s_our.add(i);
            for (int i = 0; i < 10000; ++i) s_our.contains(i);
            for (int i = 0; i < 10000; ++i) s_our.remove(i);
        });
        
        Thread t2_our = new Thread(() -> {
            for (int i = 10000; i < 20000; ++i) s_our.add(i);
            for (int i = 10000; i < 20000; ++i) s_our.contains(i);
            for (int i = 10000; i < 20000; ++i) s_our.remove(i);
        });
        
        ConcurrentSkipListSet<Integer> s_java = new ConcurrentSkipListSet<>();
        
        Thread t1_java = new Thread(() -> {
            for (int i = 0; i < 10000; ++i) s_java.add(i);
            for (int i = 0; i < 10000; ++i) s_java.contains(i);
            for (int i = 0; i < 10000; ++i) s_java.remove(i);
        });
        
        Thread t2_java = new Thread(() -> {
            for (int i = 10000; i < 20000; ++i) s_java.add(i);
            for (int i = 10000; i < 20000; ++i) s_java.contains(i);
            for (int i = 10000; i < 20000; ++i) s_java.remove(i);
        });
        
        long start_our = System.currentTimeMillis();
        t1_our.start();
        t2_our.start();
        t1_our.join();
        t2_our.join();
        long finish_our = System.currentTimeMillis();
        long timeElapsed_our = finish_our - start_our;

        long start_java = System.currentTimeMillis();
        t1_java.start();
        t2_java.start();
        t1_java.join();
        t2_java.join();
        long finish_java = System.currentTimeMillis();
        long timeElapsed_java = finish_java - start_java;

        System.out.println("Time elapsed using our implementation: " + timeElapsed_our + "ms");
        System.out.println("Time elapsed using java's implementation: " + timeElapsed_java + "ms");
    }
}
