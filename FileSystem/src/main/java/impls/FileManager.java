package impls;
import error.ErrorCode;
import init.InitFileSystem;
import interfaces.IFileManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileManager implements IFileManager {
    private int id;
    private String path;
    private ArrayList<Integer> fileArrayList = new ArrayList<>();

    public FileManager(int id) {
        this.id = id;
        path = "src/main/resources/data/root/fm-"+ id +"/";
    }

    @Override
    public File getFile(int fileId) {
        return null;
    }

    @Override
    public File newFile(int fileId) throws IOException {
        return null;
    }

    public File newFile(String filename) throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        fileSystem.setFilesNum(fileSystem.getFilesNum()+1);
        File file = new File(fileSystem.getFilesNum(),this,fileSystem.getDataBlockSize());
        file.setFileName(filename);
        creatFileFile(file);
        fileSystem.getFileFileManagerMap().put(file.getFileId(),this.id);
        return file;
    }

    public String getFileMetaPath(int fileId){
        return path + fileId + ".meta";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<Integer> getFileArrayList() {
        return fileArrayList;
    }

    public void addFileArrayList(int fileId){
        if(!fileArrayList.contains(fileId)){
            fileArrayList.add(fileId);
        }
    }

    public void creatFileFile(File file) throws IOException {
        new FileWriter(file.getMetaPath());
    }

    public void readFileMeta(File file) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file.getMetaPath()));
            int lines = 1;
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("\\s*", "");
                if (lines == 1) {
                    lines++;
                    continue;
                }
                String[] split = line.split(":");
                switch (lines) {
                    case 2:
                        file.setFileName(split[1]);
                        lines++;
                        break;
                    case 4:
                        file.setFileSize(Long.parseLong(split[1]));
                        lines++;
                        break;
                    default:
                        if ((lines >= 6) && (!line.equals("}"))) {
                            Pattern p = Pattern.compile("\\d+");//这个2是指连续数字的最少个数Pattern p = Pattern.compile("\\d{2,}");//这个2是指连续数字的最少个数
                            Matcher m = p.matcher(split[1]);
                            int i = 0;
                            ArrayList<Integer> arrayList = new ArrayList<>();
                            while (m.find()) {
                                arrayList.add(Integer.parseInt(m.group()));
                                i++;
                            }
                            file.getLogicBlocks().put(Integer.parseInt(split[0]), arrayList);
                        }
                        lines++;
                }
            }
            br.close();
        }catch (IOException ioException){
            throw new ErrorCode(ErrorCode.FILE_GET_META_FAILED);
        }
    }

    public void writeMeta(File file) throws IOException {
        java.io.File file1 = new java.io.File(file.getMetaPath());
        if (!file1.exists()){
            throw new ErrorCode(ErrorCode.FILE_LOSS);
        }
        try {
            FileWriter fw = new FileWriter(file.getMetaPath(), false);
            String string = "{\n" +
                    "  file_name: " + file.getFileName() + "\n" +
                    "  id: " + file.getFileId() + "\n" +
                    "  size: " + file.getFileSize() + "\n" +
                    "  logic block:\n";

            for (Integer key : file.getLogicBlocks().keySet()) {
                string = string + "    " + key + ": ";
                int i = 0;
                for (Integer tag : file.getLogicBlocks().get(key)) {

                    if (i % 2 == 0) {
                        string = string + "[" + tag + ",";
                    } else {
                        string = string + tag + "]" + " ";
                    }
                    i++;
                }
                string = string + "\n";
            }
            string = string + "}";
//            System.out.println(string);
            fw.write(string);
            fw.flush();
            fw.close();
        }catch (ErrorCode errorCode){
            throw new ErrorCode(ErrorCode.FILE_UPDATE_META_FAILED);
        }
    }

    //在这备份,写入的修改
    public void changeFileMeta(ArrayList<Block> blockArrayList,int begin,int oldLogicBlocksSize,File file) throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        Map<Integer,BlockManager> blockManagerMap = fileSystem.getBlockManagerMap();
        try {
            Map<Integer, ArrayList<Integer>> logicBlocks = file.getLogicBlocks();

            if (begin <= oldLogicBlocksSize) {
                    ArrayList<Integer> arrayList = logicBlocks.get(begin);
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (i % 2 == 0) {
                            blockManagerMap.get(arrayList.get(i)).getDiscardBlocks().add(arrayList.get(i + 1));
                        }
                    }
                for (int i = 0; i < oldLogicBlocksSize - begin; i++) {
                    logicBlocks.put(begin + i + blockArrayList.size(), logicBlocks.remove(begin + i + 1));
                }
                Block block = null;
                for (int i = 0; i < blockArrayList.size(); i++) {
                    ArrayList<Integer> integerArrayList = new ArrayList<>();
                    block = blockArrayList.get(i);

                    integerArrayList.add(block.getBlockManager().getId());
                    integerArrayList.add(block.getIndex());
                    Block[] blocks = duplicationBlock(block);
                    for (Block block1 : blocks) {
                        integerArrayList.add(block1.getBlockManager().getId());
                        integerArrayList.add(block1.getIndex());
                    }
                    logicBlocks.put(begin + i, integerArrayList);
                }
            } else {
                Block block = null;
                for (int i = 0; i < blockArrayList.size(); i++) {
                    ArrayList<Integer> integerArrayList = new ArrayList<>();
                    block = blockArrayList.get(i);
                    integerArrayList.add(block.getBlockManager().getId());
                    integerArrayList.add(block.getIndex());
                    Block[] blocks = duplicationBlock(block);
                    for (Block block1 : blocks) {
                        integerArrayList.add(block1.getBlockManager().getId());
                        integerArrayList.add(block1.getIndex());
                    }
                    logicBlocks.put(begin + i, integerArrayList);
                }
            }

            String string = "{\n" +
                    "  file_name: " + file.getFileName() + "\n" +
                    "  id: " + file.getFileId() + "\n" +
                    "  size: " + file.getFileSize() + "\n" +
                    "  logic block:\n";

            FileWriter fw = new FileWriter(file.getMetaPath(), false);
            for (Integer key : logicBlocks.keySet()) {
                string = string + "    " + key + ": ";
                int i = 0;
                for (Integer tag : logicBlocks.get(key)) {

                    if (i % 2 == 0) {
                        string = string + "[" + tag + ",";
                    } else {
                        string = string + tag + "]" + " ";
                    }
                    i++;
                }
                string = string + "\n";
            }
            string = string + "}";
//            System.out.println(string);
            fw.write(string);
            fw.flush();
            fw.close();
        }catch (IOException ioException){
            throw new ErrorCode(ErrorCode.FILE_UPDATE_META_FAILED);
        }
    }

    //在这备份，修改文件大小之后修改文件信息
    public void changeFileMeta(Block block,File file,int modifyBlockIndex) throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        try {
            Map<Integer, ArrayList<Integer>> logicBlocks = file.getLogicBlocks();
            Block[] blocks = duplicationBlock(block);
            for (Map.Entry<Integer, ArrayList<Integer>> entry : logicBlocks.entrySet()) {
                if (entry.getKey() > modifyBlockIndex) {
                    logicBlocks.remove(entry.getKey());
                } else if (entry.getKey() == modifyBlockIndex) {
                    ArrayList<Integer> arrayList = logicBlocks.get(modifyBlockIndex);
                    for(int i = 0;i < arrayList.size();i++){
                        if(i % 2 == 0){
                            fileSystem.getBlockManagerMap().get(arrayList.get(i)).getDiscardBlocks().add(arrayList.get(i+1));
                        }
                    }
                    ArrayList<Integer> integerArrayList = new ArrayList<>();
                    integerArrayList.add(block.getBlockManager().getId());
                    integerArrayList.add(block.getIndex());
                    for (int i = 0; i < blocks.length; i++) {
                        integerArrayList.add(blocks[i].getBlockManager().getId());
                        integerArrayList.add(blocks[i].getIndex());
                    }
                    logicBlocks.put(entry.getKey(), integerArrayList);
                }
            }
            String string = "{\n" +
                    "  file_name: " + file.getFileName() + "\n" +
                    "  id: " + file.getFileId() + "\n" +
                    "  size: " + file.getFileSize() + "\n" +
                    "  logic block:\n";

            FileWriter fw = new FileWriter(file.getMetaPath(), false);
            for (Integer key : logicBlocks.keySet()) {
                string = string + "    " + key + ": ";
                int i = 0;
                for (Integer tag : logicBlocks.get(key)) {

                    if (i % 2 == 0) {
                        string = string + "[" + tag + ",";
                    } else {
                        string = string + tag + "]" + " ";
                    }
                    i++;
                }
                string = string + "\n";
            }
            string = string + "}";
//            System.out.println(string);
            fw.write(string);
            fw.flush();
            fw.close();
        }catch (IOException ioException){
            throw new ErrorCode(ErrorCode.FILE_UPDATE_META_FAILED);
        }
    }

    public Block[] duplicationBlock(Block block) throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        Random random = new Random();
        //int randNumber =rand.nextInt(MAX - MIN + 1) + MIN; // randNumber 将被赋值为一个 MIN 和 MAX 范围内的随机数
        int randNumber = random.nextInt(2)+1; // randNumber 将被赋值为一个 MIN 和 MAX 范围内的随机数
        Block[] blocks = new Block[randNumber];
        for(int i = 1;i <= randNumber;i++){
            BlockManager blockManager = fileSystem.getABlockManager();
            Block block1 = blockManager.newBlock();
            blockManager.copyBlock(block,block1);
            blocks[i-1]  = block1;
        }
        return blocks;
    }
}
