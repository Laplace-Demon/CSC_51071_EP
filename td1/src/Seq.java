import java.util.concurrent.atomic.AtomicInteger;

public class Seq {

    public static void main(String[] args) throws Exception {
        Counter c = new Counter();
        
        AtomicInteger time = new AtomicInteger(0);
    
        Thread t1 = new Thread(() -> {
            String log = new String("");
            for (int i = 0; i < 100; i++) {
                log += "t1: call@" + time.getAndIncrement() + " ";
                c.increment();
                log += "t1: ret@" + time.getAndIncrement() + " ";
            }
            System.out.println(log);
        });
        
        Thread t2 = new Thread(() -> {
            String log = new String("");
            for (int i = 100; i < 200; i++) {
                log += "t2: call@" + time.getAndIncrement() + " ";
                c.increment();
                log += "t2: ret@" + time.getAndIncrement() + " ";
            }
            System.out.println(log);
        });
        
        t1.start();
        t2.start();
        t1.join();
        t2.join();
                
        assert c.read() == 200 : "the counter should be 200";
    }
}
