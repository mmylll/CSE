import java.util.concurrent.locks.Lock;

public class Philosopher extends Thread{
        private String name;
        private long time;
        private int num;
        private Lock leftLock;
        private Lock rightLock;
 
        public Philosopher(String name, long time, int num,Lock[] locks) {
            this.name = name;
            this.time = time;
            this.num = num;
            leftLock = locks[num];
            rightLock = locks[(num+1)%locks.length];
        }
 
        @Override
        public void run() {
            while (true){
                System.out.println(num+"号哲学家"+" "+name+" "+"正在思考...");
                //模拟思考的过程
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(num+"号哲学家"+" "+name+" "+"饿了，想来吃饭...");
                if (leftLock.tryLock()){
                    try {
                        System.out.println(num+"号哲学家"+" "+name+" "+"拿到了左边的筷子！");
                        if (rightLock.tryLock()){
                            try {
                                System.out.println(num+"号哲学家"+" "+name+" "+"拿到了右边的筷子！");
                                System.out.println(num+"号哲学家"+" "+name+" "+"开始吃饭！");
                                //模拟哲学家吃饭的过程
                                Thread.sleep(time);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                System.out.println(num+"号哲学家"+" "+name+" "+"放下了右边的筷子！");
                                rightLock.unlock();
                            }
                        }else {
                            System.out.println(num+"号哲学家"+" "+name+" "+"没拿到右边的筷子！被迫思考...");
                        }
                    }finally {
                        System.out.println(num+"号哲学家"+" "+name+" "+"放下了左边的筷子！");
                        leftLock.unlock();
                    }
                }else {
                    System.out.println(num+"号哲学家"+" "+name+" "+"没拿到左边的筷子！被迫思考...");
                }
                System.out.println(num+"号哲学家"+" "+name+" "+"思考...");
                //模拟思考过程
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }