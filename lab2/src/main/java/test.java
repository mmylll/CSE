import java.util.concurrent.CountDownLatch;

public class test {
    public static void main(String[] args){
        MyBakeryLock lock;
        int threadNumber = 5;
        lock =  new MyBakeryLock(threadNumber);
        //TODO: initialize the lock
        testA1(lock,threadNumber);
    }
    static int cnt = 0;
    public static void testA1(MyBakeryLock lock,int threadNumber) {
        System.out.println("Test A start");
        final CountDownLatch cdl = new CountDownLatch(threadNumber);//参数为 线程个数
        Thread[] threads = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++){
            threads[i] = new Thread(() -> {
                lock.lock();
                int tmp = cnt;
                try {
                    Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cnt = tmp + 1;
            lock.unlock();
            cdl.countDown();//此方法是CountDownLatch的线程数-1
        });
    }
        for (int i = 0; i < threadNumber; i++){
            threads[i].start();
        }
        //线程启动后调用countDownLatch方法
        try{
            cdl.await();//需要捕获异常，当其中线程数为0时这里才会继续运行
            String res = cnt == 5 ? "Test A passed" : "Test A failed,cnt should be 5";
            System.out.println(res);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
