import interfaces.Id;
import java.util.HashMap;
import java.util.Map;

public class config {
    public static Map<Id,myBlockManager> myBlockManagerMap;
    public static Map<Id,myFileManager> myFileManagerMap;
    public static Map<Id,Id> fileEmptyMap;
    static {
        myBlockManagerMap = new HashMap<>();
        myBlockManagerMap.put(new StringId("bm-1"),new myBlockManager("./data/bm/bm-1/",new StringId("bm-1")));
        myBlockManagerMap.put(new StringId("bm-2"),new myBlockManager("./data/bm/bm-2/",new StringId("bm-2")));
        myBlockManagerMap.put(new StringId("bm-3"),new myBlockManager("./data/bm/bm-3/",new StringId("bm-3")));

        myFileManagerMap = new HashMap<>();
        myFileManagerMap.put(new StringId("fm-1"),new myFileManager("./data/fm/fm-1/"));
        myFileManagerMap.put(new StringId("fm-2"),new myFileManager("./data/fm/fm-2/"));

        fileEmptyMap = new HashMap<>();
        fileEmptyMap.put(new StringId("FILE_EMPTY"),new StringId("FILE_EMPTY"));
    }
}
