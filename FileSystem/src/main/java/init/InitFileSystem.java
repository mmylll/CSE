package init;

import error.ErrorCode;
import impls.BlockManager;
import impls.FileManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InitFileSystem {
    private int metaBlockSize;
    private int dataBlockSize;
    private Map<Integer,FileManager> fileManagerMap = new HashMap<>();
    private Map<Integer,BlockManager> blockManagerMap = new HashMap<>();
    private Map<Integer,Integer> fileFileManagerMap = new HashMap<>();
    private static volatile InitFileSystem instance = null;
    private int filesNum;

    private InitFileSystem() throws IOException {
        readInit();
    }

    //运行时加载对象
    public static InitFileSystem getInstance() throws IOException {
        if (instance == null) {
            synchronized(InitFileSystem.class){
                if(instance == null){
                    instance = new InitFileSystem();
                }
            }
        }
        return instance;
    }

    public int getMetaBlockSize() {
        return metaBlockSize;
    }

    public void setMetaBlockSize(int metaBlockSize) {
        this.metaBlockSize = metaBlockSize;
    }

    public int getDataBlockSize() {
        return dataBlockSize;
    }

    public void setDataBlockSize(int dataBlockSize) {
        this.dataBlockSize = dataBlockSize;
    }

    public Map<Integer, Integer> getFileFileManagerMap() {
        return fileFileManagerMap;
    }

    public void setFileFileManagerMap(Map<Integer, Integer> fileFileManagerMap) {
        this.fileFileManagerMap = fileFileManagerMap;
    }

    public Map<Integer, BlockManager> getBlockManagerMap() {
        return blockManagerMap;
    }

    public void setBlockManagerMap(Map<Integer, BlockManager> blockManagerMap) {
        this.blockManagerMap = blockManagerMap;
    }

    public Map<Integer, FileManager> getFileManagerMap() {
        return fileManagerMap;
    }

    public void setFileManagerMap(Map<Integer, FileManager> fileManagerMap) {
        this.fileManagerMap = fileManagerMap;
    }

    public int getFilesNum() {
        return this.filesNum;
    }

    public void setFilesNum(int filesNum) {
        this.filesNum = filesNum;
    }

    private void readInit() throws IOException {
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/data/init.meta"));
            int lines = 1;
            String line = null;
            while ((line = br.readLine()) != null) {
                if (lines == 1) {
                    lines++;
                    continue;
                }
                line = line.replaceAll("\\s*", "");
                String[] split = line.split(":");

                switch (lines) {
                    case 2:
                        metaBlockSize = Integer.parseInt(split[1]);
                        lines++;
                        break;
                    case 3:
                        dataBlockSize = Integer.parseInt(split[1]);
                        lines++;
                        break;
                    case 4:
                        int[] fileManagers = initReadManagers(split[1]);
                        for (int manager : fileManagers) {
                            FileManager fileManager = new FileManager(manager);
                            fileManagerMap.put(manager, fileManager);
                        }
                        lines++;
                        break;
                    case 5:
                        filesNum =  Integer.parseInt(split[1]);
                        lines++;
                        break;
                    case 6:
                        int[] blockManagers = initReadManagers(split[1]);
                        for (int manager : blockManagers) {
                            BlockManager blockManager = new BlockManager(manager);
                            blockManagerMap.put(manager, blockManager);
                        }
                        lines++;
                        break;
                    case 7:
                        int[] blockManagersSum = initReadManagers(split[1]);
                        int j = 1;
                        for (int manager : blockManagersSum) {
                            blockManagerMap.get(j).setBlocksNum(manager);
                            j++;
                            if (j > blockManagerMap.size()) {
                                break;
                            }
                        }
                        lines++;
                        break;
                    default:
                        if (split.length > 1) {
                            fileFileManagerMap.put(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                        }
                        lines++;
                        break;
                }
            }
            br.close();
        }catch (IOException ioException){
            throw new ErrorCode(ErrorCode.SYSTEM_INIT_FAILED);
        }
    }

    private int[] initReadManagers(String managers){
        String[] split = managers.split(",");
        return Arrays.stream(split).mapToInt(Integer::parseInt).toArray();
    }

    public FileManager findFileManager(int fileId){
        if (fileFileManagerMap.containsKey(fileId)){
            return fileManagerMap.get(fileFileManagerMap.get(fileId));
        }else{
            throw new ErrorCode(ErrorCode.UNKNOWN_FILEMANAGER_FILE);
        }
    }

    public FileManager getFileManager(int index){
        return fileManagerMap.get(index);
    }

    public BlockManager getBlockManager(int index){
        return blockManagerMap.get(index);
    }

    public BlockManager getABlockManager() throws IOException {
        Random random = new Random();
        //int randNumber =rand.nextInt(MAX - MIN + 1) + MIN; // randNumber 将被赋值为一个 MIN 和 MAX 范围内的随机数
        int randNumber = random.nextInt(blockManagerMap.size())+1;
        return getBlockManager(randNumber);
    }

    public FileManager getAFileManager() throws IOException {
        Random random = new Random();
        //int randNumber =rand.nextInt(MAX - MIN + 1) + MIN; // randNumber 将被赋值为一个 MIN 和 MAX 范围内的随机数
        int randNumber = random.nextInt(fileManagerMap.size())+1;
        return getFileManager(randNumber);
    }
}
