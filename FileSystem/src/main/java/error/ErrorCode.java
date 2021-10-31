package error;

import java.util.HashMap;
import java.util.Map;

public class ErrorCode extends RuntimeException {
    public static final int IO_EXCEPTION = 1;
    public static final int CHECKSUM_CHECK_FAILED = 2;
    public static final int FILE_CREATE_FAILED = 3;
    public static final int BLOCK_CREATE_FAILED = 4;
    public static final int BLOCK_READ_FAILED = 5;
    public static final int BLOCK_LOSS = 6;
    public static final int FILE_LOSS = 7;
    public static final int BLOCK_DAMAGE = 8;
    public static final int SYSTEM_INIT_FAILED = 9;
    public static final int UNKNOWN_FILEMANAGER_FILE = 10;
    public static final int FILE_GET_META_FAILED = 11;
    public static final int FILE_UPDATE_META_FAILED = 12;
    public static final int BLOCK_GET_META_FAILED = 13;
    public static final int BLOCK_UPDATE_META_FAILED = 14;
 // ... and more
    public static final int UNKNOWN = 1000;
 
    private static final Map<Integer, String> ErrorCodeMap = new HashMap<>();
 
    static {
       ErrorCodeMap.put(IO_EXCEPTION, "IO exception");
       ErrorCodeMap.put(CHECKSUM_CHECK_FAILED, "block checksum check failed");
       ErrorCodeMap.put(FILE_CREATE_FAILED,"file creation failed");
       ErrorCodeMap.put(BLOCK_CREATE_FAILED,"Create block failed");
       ErrorCodeMap.put(BLOCK_READ_FAILED,"Read block failed");
       ErrorCodeMap.put(BLOCK_LOSS,"Block loss"+".The block is missing");
       ErrorCodeMap.put(FILE_LOSS,"File loss"+".File related information is missing");
       ErrorCodeMap.put(BLOCK_DAMAGE,"Block damage"+".A damaged block is found and there are no logical blocks or all logical blocks are damaged");
       ErrorCodeMap.put(SYSTEM_INIT_FAILED,"System initialization failed");
       ErrorCodeMap.put(UNKNOWN_FILEMANAGER_FILE,"Unknown fileManager"+".This fileManager is not registered or the file does not exist, please check");
       ErrorCodeMap.put(FILE_GET_META_FAILED,"Failed to get file information");
       ErrorCodeMap.put(FILE_UPDATE_META_FAILED,"Failed to update file information");
       ErrorCodeMap.put(BLOCK_GET_META_FAILED,"Failed to get block information");
       ErrorCodeMap.put(BLOCK_UPDATE_META_FAILED,"Failed to update block information");
       //... and more
       ErrorCodeMap.put(UNKNOWN, "unknown");
    }
 
    public static String getErrorText(int errorCode) {
       return ErrorCodeMap.getOrDefault(errorCode, "invalid");
    }
 
    private int errorCode;
 
    public ErrorCode(int errorCode) {
       super(String.format("error code '%d' \"%s\"", errorCode, getErrorText(errorCode)));
       this.errorCode = errorCode;
    }
 
    public int getErrorCode() {
 return errorCode;
 } }