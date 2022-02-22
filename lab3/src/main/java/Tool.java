import java.io.*;
import java.util.ArrayList;

public class Tool {
    private final String dbPath = "db.txt";
    private final String logPath = "log";
    private RandomAccessFile dbRandomAccessFile;
    private ArrayList<Log> logArrayList = new ArrayList<>();

    //读取log文件转化为log对象
    public Tool() throws IOException {
        File dbFile = new File(dbPath);
        dbRandomAccessFile = new RandomAccessFile(dbFile,"rw");
        File logFile = new File("log");

        BufferedReader bufferedReader = null;
        try{
            bufferedReader = new BufferedReader(new FileReader(logFile));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                Log log = new Log();
                log.analysis(line);
                logArrayList.add(log);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 保存日志
    public synchronized void saveLogs() throws IOException {
        File logFile = new File(logPath);
        FileWriter fileWritter = new FileWriter(logFile.getName(),false);
        for (Log log : logArrayList) {
            fileWritter.write(log.writeLog());
        }
        fileWritter.flush();
        fileWritter.close();
    }

    // 向日志写入write
    public Log doAffairs(String operate,int tid,int dbIndex,char oldValue,char newValue){
        Log log = new Log(tid,operate,dbIndex,oldValue,newValue);
        System.out.println(tid+" :"+ " " + operate + " " + dbIndex + " " + oldValue + "--->" + newValue);
        logArrayList.add(log);
        return log;
    }

    // 向日志写入start,commit,abort
    public Log doAffairs(String operate,int tid){
        Log log = new Log(tid,operate);
        System.out.println(tid + " :" + " " + operate);
        logArrayList.add(log);
        return log;
    }

    // 读取db.txt指定行数据
    public synchronized char readDB(int dbIndex) throws IOException {
        dbRandomAccessFile.seek(dbIndex * 2L);
        return (char) dbRandomAccessFile.read();
    }

    // 向db.txt指定行写入数据
    public synchronized void writeDB(int dbIndex, char newValue) throws IOException {
        dbRandomAccessFile.seek(dbIndex * 2L);
        dbRandomAccessFile.write((newValue + "\n").getBytes());
    }

    // 根据日志中的信息得到上次运行完成后最大的线程编号，用于recover
    public int getLastTid(){
        int tid = 0;
        for (Log log : logArrayList) {
            tid = Math.max(log.getTid(), tid);
        }
        return tid;
    }

    public ArrayList<Log> getLogArrayList() {
        return logArrayList;
    }
}
