import java.util.concurrent.locks.*;

public class Counter {
    public int count = 0;
    Lock lock = new ReentrantLock();
    
    public void increment() {
        int temp;
        lock.lock();
        try {
            // begin of crucial part
            temp = count;
            temp = temp + 1;
            count = temp;
            // end of crucial part
        } finally {
            lock.unlock();
        }
    }
    
    public int read() {
        int temp = count;
        return temp;
    }
}
