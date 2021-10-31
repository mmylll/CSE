package tool;

import error.ErrorCode;
import impls.Block;
import impls.BlockManager;
import impls.File;
import impls.FileManager;
import init.InitFileSystem;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Tools {
    public Tools(){

    }

    public byte[] smartCat(int fileId) throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        FileManager fileManager = fileSystem.findFileManager(fileId);
        File file = new File(fileId,fileManager,fileSystem.getDataBlockSize());
        fileManager.readFileMeta(file);
        System.out.println("该文件长度为："+file.getSize());
        Scanner input = new Scanner(System.in);
        while(true){
            System.out.println("请输入起始位置：");
            String cursor = input.nextLine();
            if(!cursor.matches("^[0-9]+$")){
                System.out.println("起始位置格式错误");
                continue;
            }else{
                if((Integer.parseInt(cursor) > file.getSize())||(Integer.parseInt(cursor) < 0)){
                    System.out.println("起始位置范围错误");
                    continue;
                }
            }
            file.setCursor(Integer.parseInt(cursor));
            System.out.println("请输入需要获取的文件长度：");
            String length = input.nextLine();
            if(!length.matches("^[0-9]+$")){
                System.out.println("文件长度格式错误");
                continue;
            }else{
                if((Integer.parseInt(length) > file.getSize())||(Integer.parseInt(length) < 0)){
                    System.out.println("文件长度范围错误");
                    continue;
                }
            }
            return file.read(Integer.parseInt(length));
        }
    }

    public void smartHex(int blockManagerIndex,int blockId) throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        Block block = new Block(blockId,fileSystem.getBlockManagerMap().get(blockManagerIndex));
        StringBuilder builder = new StringBuilder();
        // 遍历byte[]数组，将每个byte数字转换成16进制字符，再拼接起来成字符串
        byte[] bytes = block.read();
        for (int i = 0; i < bytes.length; i++) {
            // 每个byte转换成16进制字符时，bytes[i] & 0xff如果高位是0，输出将会去掉，所以+0x100(在更高位加1)，再截取后两位字符
            builder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1)+" ");
        }
        System.out.println(builder.toString());

    }

    public void smartWrite(int offset, int where, int fileId) throws IOException {

        InitFileSystem fileSystem = InitFileSystem.getInstance();
        FileManager fileManager = fileSystem.findFileManager(fileId);
        File file = new File(fileId,fileManager,fileSystem.getDataBlockSize());
        fileManager.readFileMeta(file);
        file.move(offset,where);
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        file.write(input.getBytes());
    }

    public File smartCopy(int fileId) throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        FileCheckSum fileCheckSum = new FileCheckSum();
        Tools tools = new Tools();
        FileManager fileManager = fileSystem.findFileManager(fileId);
        File file = new File(fileId,fileManager,fileSystem.getDataBlockSize());
        fileManager.readFileMeta(file);
        FileManager copyFileManager = fileSystem.getAFileManager();
        File copyFile = copyFileManager.newFile(file.getFileName());
        for(int i = 1;i <= file.getLogicBlocks().size();i++){
            ArrayList<Integer> arrayList = new ArrayList<>();
            int aLogicBlocksSize = file.getLogicBlocks().get(i).size();
            for(int j = 0;j < aLogicBlocksSize;j++){
                if(j % 2 != 0){
                    if(file.getLogicBlocks().get(i).size() == 0){
                        java.io.File file1 = new java.io.File(copyFile.getMetaPath());
                        if(file1.exists()){
                            file1.delete();
                        }
                        throw new ErrorCode(ErrorCode.BLOCK_DAMAGE);
                    }
                    BlockManager blockManager = fileSystem.getBlockManager(file.getLogicBlocks().get(i).get(j-1));
                    Block block = new Block(file.getLogicBlocks().get(i).get(j),blockManager);
                    blockManager.readMeta(block);
                    if(!fileCheckSum.checksumMD5(block.getDataPath(),block.getChecksum())){
                        tools.handleDamageBlock(file,i,j-1,false);
                        continue;
                    }
                    BlockManager copyBlockManager = fileSystem.getABlockManager();
                    Block copyBlock = copyBlockManager.newBlock();
                    blockManager.copyBlock(block,copyBlock);
                    arrayList.add(copyBlockManager.getId());
                    arrayList.add(copyBlock.getIndex());
                }
            }
            copyFile.getLogicBlocks().put(i,arrayList);
        }
        copyFileManager.writeMeta(copyFile);
        copyFile.getFileManager().writeMeta(copyFile);
        System.out.println("复制生成文件id为："+copyFile.getFileId());
        return copyFile;
    }

    public void smartLs() throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        Map<Integer,FileManager> fileManagerMap = fileSystem.getFileManagerMap();
        Map<Integer,BlockManager> blockManagerMap = fileSystem.getBlockManagerMap();
        Map<Integer,Integer> fileFileManagerMap = fileSystem.getFileFileManagerMap();
        Map<Integer,ArrayList<Integer>> blockManagerBlockMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : fileFileManagerMap.entrySet()) {

        }
        System.out.println("File:");
        for (Map.Entry<Integer, Integer> entry : fileFileManagerMap.entrySet()) {
            File file = new File(entry.getKey(),fileManagerMap.get(entry.getValue()),fileSystem.getDataBlockSize());
            fileManagerMap.get(entry.getValue()).readFileMeta(file);
            fileManagerMap.get(entry.getValue()).addFileArrayList(entry.getKey());
            System.out.println("f"+entry.getKey()+": ");
            for(Map.Entry<Integer, ArrayList<Integer>> entry1 : file.getLogicBlocks().entrySet()){
                System.out.print("BLOCK"+entry1.getKey()+": ");
                for(int i = 0;i < entry1.getValue().size();i++){
                    if(i % 2 == 0){
                        if(!blockManagerBlockMap.containsKey(entry1.getValue().get(i))){
                            ArrayList<Integer> arrayList = new ArrayList<>();
                            arrayList.add(entry1.getValue().get(i+1));
                            blockManagerBlockMap.put(entry1.getValue().get(i),arrayList);
                        }else {
                            blockManagerBlockMap.get(entry1.getValue().get(i)).add(entry1.getValue().get(i+1));
                        }
                        System.out.print("BM"+entry1.getValue().get(i)+".b."+entry1.getValue().get(i+1)+"   ");
                    }
                }
                System.out.println();
            }

        }
        System.out.println("FileManager:");
        for(Map.Entry<Integer,FileManager> entry : fileManagerMap.entrySet()){
            System.out.print("FM"+entry.getKey()+": ");
            for(int i = 0;i < entry.getValue().getFileArrayList().size();i++){
                System.out.print("f"+entry.getValue().getFileArrayList().get(i)+"  ");
            }
            System.out.println();
        }

        for(Map.Entry<Integer,BlockManager> entry : blockManagerMap.entrySet()){
            if(!blockManagerBlockMap.containsKey(entry.getKey())){
                ArrayList<Integer> arrayList = new ArrayList<>();
                blockManagerBlockMap.put(entry.getKey(),arrayList);
            }
        }

        System.out.println("BlockManager: ");
        for(Map.Entry<Integer,ArrayList<Integer>> entry : blockManagerBlockMap.entrySet()){
            System.out.print("BM"+entry.getKey()+": ");
            for(int i = 0;i < entry.getValue().size();i++){
                System.out.print("b"+entry.getValue().get(i)+"   ");
            }
            System.out.println();
        }
    }

    public void smartQuit() throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        Map<Integer,FileManager> fileManagerMap = fileSystem.getFileManagerMap();
        Map<Integer,BlockManager> blockManagerMap = fileSystem.getBlockManagerMap();
        Map<Integer,Integer> fileFileManagerMap = fileSystem.getFileFileManagerMap();
        StringBuilder result = new StringBuilder("{\n" +
                "  metaBlockSize: 20\n" +
                "  dataBlockSize: "+fileSystem.getDataBlockSize()+"\n");
        result.append("  fileManager: ");

        for (Map.Entry<Integer, FileManager> entry : fileManagerMap.entrySet()) {
            result.append(entry.getKey()).append(",");
        }
        result.append("\n");
        result.append("  filesnum: ");
        result.append(fileSystem.getFilesNum());
        result.append("\n");
        result.append("  blockManager: ");
        int[] blocksNum = new int[blockManagerMap.size()];
        for (Map.Entry<Integer, BlockManager> entry : blockManagerMap.entrySet()) {
            result.append(entry.getKey()).append(",");
            blocksNum[entry.getKey()-1] = entry.getValue().getBlocksNum();
        }
        result.append("\n");
        result.append("  blocksnum: ");
        for(int i = 0; i < blocksNum.length;i++){
            result.append(blocksNum[i]).append(",");
        }
        result.append("\n");
        result.append("  file:\n");
        for (Map.Entry<Integer, Integer> entry : fileFileManagerMap.entrySet()) {
            result.append("    ").append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
        }
        result.append("}");
//        System.out.println(result);
        FileWriter fw = new FileWriter("src/main/resources/data/init.meta",false);
        fw.write(String.valueOf(result));
        fw.flush();
        fw.close();

        for (Map.Entry<Integer, BlockManager> entry : fileSystem.getBlockManagerMap().entrySet()) {
            entry.getValue().writeManagerBlockMeta();
        }
    }

    public void smartCreateFile() throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        Map<Integer,FileManager> fileManagerMap = fileSystem.getFileManagerMap();
        Scanner input = new Scanner(System.in);
        while(true) {
            System.out.println("请选择一个fileManager");
            System.out.print("FM：");
            for (Map.Entry<Integer, FileManager> entry : fileManagerMap.entrySet()) {
                System.out.print(entry.getKey() + "   ");
            }
            System.out.println();
            String fileManagerId = input.nextLine();
            if(!fileManagerId.matches("^[0-9]+$")||!fileManagerMap.containsKey(Integer.parseInt(fileManagerId))){
                System.out.println("请输入正确的FileManager");
                continue;
            }else{
                FileManager fileManager = fileManagerMap.get(Integer.parseInt(fileManagerId));
                System.out.println("请输入文件名：");
                String filename = input.nextLine();
                File file = fileManager.newFile(filename);
                fileManager.writeMeta(file);
                break;
            }
        }

    }

    public void smartSetFileSize() throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        Scanner input = new Scanner(System.in);
        while (true){
            System.out.println("请输入文件id：");
            String fileId = input.nextLine();
            if(!fileId.matches("^[0-9]+$")){
                System.out.println("文件id错误");
            }else{
                FileManager fileManager = fileSystem.findFileManager(Integer.parseInt(fileId));
                File file = new File(Integer.parseInt(fileId),fileManager,fileSystem.getDataBlockSize());
                fileManager.readFileMeta(file);
                System.out.print("当前文件大小为：");
                System.out.println(file.getFileSize());
                System.out.println("请输入文件大小：");
                String newSize = input.nextLine();
                if(!newSize.matches("^[0-9]+$")){
                    System.out.println("文件大小错误");
                    continue;
                }
                file.setSize(Integer.parseInt(newSize));
                break;
            }
        }
    }

    public void smartResetFileSystem() throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        fileSystem.setFilesNum(0);
        delAllFile("src/main/resources/data/root/bm-1");
        System.out.println("已删除");
        delAllFile("src/main/resources/data/root/bm-2");
        System.out.println("已删除");
        delAllFile("src/main/resources/data/root/bm-3");
        System.out.println("已删除");
        delAllFile("src/main/resources/data/root/fm-1");
        System.out.println("已删除");
        delAllFile("src/main/resources/data/root/fm-2");
        System.out.println("已删除");
        delAllFile("src/main/resources/data/root/fm-3");
        System.out.println("已删除");
        String str = "{\n" +
                "  metaBlockSize: 20\n" +
                "  dataBlockSize: "+fileSystem.getDataBlockSize()+ "\n" +
                "  fileManager: 1,2,3,\n" +
                "  filesnum: 0\n" +
                "  blockManager: 1,2,3,\n" +
                "  blocksnum: 0,0,0,\n" +
                "  file:\n" +
                "}";
        FileWriter fw = new FileWriter("src/main/resources/data/init.meta",false);
        System.out.println(str);
        fw.write(str);
        fw.flush();
        fw.close();
        new FileWriter("src/main/resources/data/root/bm-1/managerBlock.meta");
        new FileWriter("src/main/resources/data/root/bm-2/managerBlock.meta");
        new FileWriter("src/main/resources/data/root/bm-3/managerBlock.meta");
        System.exit(1);
    }

    public void smartClear() throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        Map<Integer,BlockManager> blockManagerMap = fileSystem.getBlockManagerMap();
        for (Map.Entry<Integer, BlockManager> entry : blockManagerMap.entrySet()) {
            ArrayList<Integer> discardBlocks = entry.getValue().getDiscardBlocks();
            for(int discardBlockId : discardBlocks){
                entry.getValue().getFreeBlocks().add(discardBlockId);
            }
            entry.getValue().getDiscardBlocks().clear();
            entry.getValue().writeManagerBlockMeta();
        }
    }

    public void handleDamageBlock(File file,int index,int damageBlockIndex,boolean blockLoss) throws IOException {
        System.out.println("发现坏块");
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        FileCheckSum fileCheckSum = new FileCheckSum();
        if(blockLoss&&(file.getLogicBlocks().get(index).size() == 0)){
            throw new ErrorCode(ErrorCode.BLOCK_LOSS);
        }
        int blockManager1Id = file.getLogicBlocks().get(index).remove(damageBlockIndex);
        int block1Id = file.getLogicBlocks().get(index).remove(damageBlockIndex);
        if(file.getLogicBlocks().get(index).size() == 0){
            System.out.println("文件已损坏，无法打开");
            throw new ErrorCode(ErrorCode.BLOCK_LOSS);
        }
        file.getFileManager().writeMeta(file);
        if(!blockLoss) {
            if (file.getLogicBlocks().get(index).size() == 0) {
                throw new ErrorCode(ErrorCode.BLOCK_DAMAGE);
            }
            if (file.getLogicBlocks().get(index).size() == 2) {
                BlockManager blockManager = fileSystem.getBlockManager(file.getLogicBlocks().get(index).get(0));
                Block block = new Block(file.getLogicBlocks().get(index).get(1), blockManager);
                blockManager.readMeta(block);
                if (fileCheckSum.checksumMD5(block.getDataPath(), block.getChecksum())) {
                    BlockManager blockManager1 = fileSystem.getBlockManager(blockManager1Id);
                    Block block1 = blockManager1.newBlock(block1Id, false);
                    blockManager1.copyBlock(block, block1);
                    file.getLogicBlocks().get(index).add(blockManager1.getId());
                    file.getLogicBlocks().get(index).add(block1.getIndex());
                    file.getFileManager().writeMeta(file);
                    System.out.println("已经修复好了逻辑块");
                } else {
                    file.getLogicBlocks().get(index).remove(0);
                    file.getLogicBlocks().get(index).remove(0);
                    file.getFileManager().writeMeta(file);
                    throw new ErrorCode(ErrorCode.BLOCK_DAMAGE);
                }
            }
        }
    }


    public void handleHelp(){
        System.out.println("help: "+"查看命令");
        System.out.println("cat: "+"获取File的File内容,能够从⽂件指定位置,读取指定⻓度的内容并且打印在控制台");
        System.out.println("hex: "+"读取block的data并⽤16进制的形式打印到控制台");
        System.out.println("write: "+"将写⼊指针移动到指定位置后,开始读取⽤户数据,并且写⼊到⽂件中");
        System.out.println("copy: "+"复制File到另⼀个File");
        System.out.println("ls: "+"查看⽂件系统结构,包括每个FileManager下管理的⽂件,每个BlockManager下管理的block及其duplication,和每个file使⽤的block");
        System.out.println("create: "+"创建文件");
        System.out.println("set_size: "+"设置文件大小");
        System.out.println("clear: "+"清空丢弃了的block");
        System.out.println("reset: "+"重置文件系统");
        System.out.println("quit: "+"退出");
    }

    //删除文件夹
    public void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除文件夹下所有文件
    public boolean delAllFile(String path) {
        boolean flag = false;
        java.io.File file = new java.io.File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        java.io.File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(java.io.File.separator)) {
                temp = new java.io.File(path + tempList[i]);
            } else {
                temp = new java.io.File(path + java.io.File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

}
