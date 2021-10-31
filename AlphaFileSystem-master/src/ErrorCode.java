import java.util.HashMap;
import java.util.Map;

public class ErrorCode extends RuntimeException {
    public static final int IO_EXCEPTION = 1;
    public static final int ID_FILE_ERROR = 2;
    public static final int MD5_INIT_FAILED = 3;

    public static final int NO_SUCH_BLOCK = 4;
    public static final int MEMORY_ERROR = 5;
    public static final int BLOCK_CHECK_FAILED = 6;

    public static final int CURSOR_ERROR = 7;
    public static final int FILE_ALREADY_EXISTED = 8;
    public static final int NO_SUCH_FILE = 9;
    public static final int READ_LENGTH_ERROR = 10;

    public static final int NEW_SIZE_ERROR = 11;
    public static final int NO_OPERATING_FILE = 12;
    public static final int COMMAND_ERROR = 13;
    public static final int NO_THIS_CURSOR_TYPE = 14;
    public static final int NO_FILE_MANAGER = 15;
    public static final int NO_BLOCK_MANAGER = 16;

    private static final Map<Integer,String> ErrorCodeMap = new HashMap<>();
    static {
        ErrorCodeMap.put(IO_EXCEPTION,"IO exception");
        ErrorCodeMap.put(ID_FILE_ERROR,"id count file error");
        ErrorCodeMap.put(MD5_INIT_FAILED,"MD5 initiate error");

        ErrorCodeMap.put(NO_SUCH_BLOCK,"Block not found");
        ErrorCodeMap.put(MEMORY_ERROR,"some blocks error");
        ErrorCodeMap.put(BLOCK_CHECK_FAILED,"block checksum check failed");

        ErrorCodeMap.put(CURSOR_ERROR,"file cursor error");
        ErrorCodeMap.put(FILE_ALREADY_EXISTED,"file already existed");
        ErrorCodeMap.put(NO_SUCH_FILE,"file not found");
        ErrorCodeMap.put(READ_LENGTH_ERROR,"read length error");

        ErrorCodeMap.put(NEW_SIZE_ERROR,"invalid new size");
        ErrorCodeMap.put(NO_OPERATING_FILE,"you should cat one file first");
        ErrorCodeMap.put(COMMAND_ERROR,"invalid cmd");
        ErrorCodeMap.put(NO_THIS_CURSOR_TYPE,"invalid cursor move strategy(cur,head,tail)");
        ErrorCodeMap.put(NO_FILE_MANAGER,"no such fileManager");
        ErrorCodeMap.put(NO_BLOCK_MANAGER,"no such blockManager");
    }

    public static String getErrorText(int errorCode){
        return ErrorCodeMap.getOrDefault(errorCode,"invalid");
    }
    private int errorCode;
    public ErrorCode(int errorCode){
        super(String.format("error code '%d' \"%s\"", errorCode,getErrorText(errorCode)));
        this.errorCode = errorCode;
    }
    public int getErrorCode(){
        return errorCode;
    }

}
