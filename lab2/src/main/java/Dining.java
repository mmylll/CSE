import java.util.concurrent.locks.Lock;

public class Dining {

    static final Lock[] locks=new Lock[5];
    static {
        for (int i=0;i<locks.length;i++){
            locks[i] = new MyBakeryLock(locks.length);
        }
    }

    public static void main(String[] args) {
        Philosopher philosopher0 = new Philosopher("0", 1000, 0,locks);
        Philosopher philosopher1 = new Philosopher("1", 800, 1,locks);
        Philosopher philosopher2 = new Philosopher("2", 500, 2,locks);
        Philosopher philosopher3 = new Philosopher("3", 2000, 3,locks);
        Philosopher philosopher4 = new Philosopher("4", 2000, 4,locks);
        philosopher0.start();
        philosopher1.start();
        philosopher2.start();
        philosopher3.start();
        philosopher4.start();
        while (true) {
        }
    }
}
