public class Log {
    // log结构
    // tid + operate + dbIndex + oldValue + newValue

    private String operate;
    private char newValue;
    private char oldValue;
    private int tid;
    private int dbIndex;

    public Log(int tid,String operate,int dbIndex,char oldValue,char newValue){
        this.operate = operate;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.tid = tid;
        this.dbIndex = dbIndex;
    }

    public Log(int tid,String operate){
        this.tid = tid;
        this.operate = operate;
    }

    public Log(){

    }

    // 解析日志
    public void analysis(String log){
        String[] strs = log.split(" ");
        this.tid = Integer.parseInt(strs[0]);
        this.operate = strs[1];
        if(strs.length > 2){
            this.dbIndex = Integer.parseInt(strs[2]);
            this.oldValue = strs[3].charAt(0);
            this.newValue = strs[4].charAt(0);
        }
    }

    public String writeLog(){
        if(operate.equals("write")){
            return tid + " " + operate + " " + dbIndex + " " + oldValue + " " + newValue + "\n";
        }else {
            return tid + " " + operate + "\n";
        }
    }

    public String getOperate() {
        return operate;
    }

    public int getTid() {
        return tid;
    }

    public int getDbIndex() {
        return dbIndex;
    }

    public char getNewValue() {
        return newValue;
    }

    public char getOldValue() {
        return oldValue;
    }
}
