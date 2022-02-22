import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MyAtomicity{

    private Tool tool;
    private volatile int lastTid;
    // 每个变量的HWM
    private final ConcurrentHashMap<Integer,Integer> HWM = new ConcurrentHashMap();

    public MyAtomicity() throws IOException {
        tool = new Tool();
        // 初始化HWM
        for(int i = 0;i < 10; i++){
            HWM.put(i,-1);
        }
        lastTid = tool.getLastTid();
    }

    public void update(char ch) throws IOException, InterruptedException {
        // 新线程
       new Thread(new Runnable() {
           @Override
           public void run() {
               int tid = getNewTid();
               // 开启事务
               System.out.println(tid + " :" + " " + "start");
               tool.doAffairs("start", tid);
               try {
                   // 先对数据检查恢复
                   recover();
                   for (int i = 0; i < 10; i++) {
                       // 查看HWM与tid看变量是否可用
                       if (!checkHWM(tid, i)) {
                           throw new Exception();
                       }
                       // 写
                       tool.doAffairs("write", tid, i, tool.readDB(i), ch);
                       Thread.sleep(1000);
                       tool.writeDB(i, ch);
                   }
                   // 执行完成
                   tool.doAffairs("commit", tid);
               } catch (Exception e) {
                   // 如果不可使用变量，重新开启新的线程完成该事务
                   tool.doAffairs("abort", tid);
                   try {
                       update(ch);
                   } catch (IOException | InterruptedException ex) {
                       ex.printStackTrace();
                   }
               } finally {
                   try {
                       // 保存日志文件
                       tool.saveLogs();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           }
       }).start();
    }

    private synchronized void recover() throws IOException {
        ArrayList<Log> logArrayList = tool.getLogArrayList();
        if(logArrayList.size() == 0){
            return;
        }
         // 反读日志
        for(int i = logArrayList.size()-1;i >= 0;i--){
            Log log = logArrayList.get(i);
            // commit说明之前的日志中的操作是成功完成了，到这就说明完成recover
            if(log.getOperate().equals("commit")){
                break;
            }
            // 没遇到commit，说明这些操作并没有完整地被完成，利用日志中的数据还原回去
            if(log.getOperate().equals("write")){
                System.out.println("recover : " + log.getTid() + " : " + log.getOperate() + " " + log.getOldValue() + "--->" + log.getNewValue());
                tool.writeDB(log.getDbIndex(),log.getOldValue());
            }
        }
    }

    // 检查HWM与tid
    private boolean checkHWM(int tid,int dbIndex){
        if(HWM.get(dbIndex) > tid){
            System.out.println(tid+ " :" + " abort");
            return false;
        }else{
            HWM.put(dbIndex,tid);
            return true;
        }
    }

    // abort后给新线程的编号
    private synchronized int getNewTid() {
        return ++lastTid;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        MyAtomicity myAtomicity = new MyAtomicity();
        myAtomicity.update('1');
        myAtomicity.update('2');
        myAtomicity.update('3');
    }
}