package impls;
import error.ErrorCode;
import init.InitFileSystem;
import interfaces.IFile;
import tool.FileCheckSum;
import tool.Tools;
import java.io.IOException;
import java.util.*;

public class File implements IFile {
    private int fileId;
    private String fileName;
    private long fileSize;
    Map<Integer,ArrayList<Integer>> logicBlocks = new HashMap<>();
    private String metaPath;
    private FileManager fileManager;
    private long cursor;//光标位置
    private int blockSize;

    public File(int fileId,FileManager fileManager,int blockSize){
        this.fileId = fileId;
        this.fileManager = fileManager;
        this.blockSize = blockSize;
        metaPath = fileManager.getFileMetaPath(fileId);
    }

    public  Map<Integer,ArrayList<Integer>> getLogicBlocks() {
        return logicBlocks;
    }

    public long getFileSize() throws IOException {
        fileSize = 0;
        for(Integer key:logicBlocks.keySet()) {
            fileSize = fileSize + getALogicBlock(key).getSize();
        }
        return fileSize;
    }

    public void setFileSize(long fileSize) throws IOException {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int getFileId() {
        return this.fileId;
    }

    @Override
    public FileManager getFileManager() {
        return this.fileManager;
    }

    @Override
    public byte[] read(int length) throws IOException {
        byte[] bytes = read();
        if(cursor + length > getSize()){
            length = (int) (getSize() - cursor);
        }
        byte[] tmp = new byte[length];
        System.arraycopy(bytes, (int) cursor,tmp,0,length);
        return tmp;
    }

    public byte[] read() throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        Tools tools = new Tools();
        ArrayList<Integer> list;
        Random rand = new Random();
        StringBuilder result = new StringBuilder();
        FileCheckSum fileCheckSum = new FileCheckSum();
        for(int i = 1;i <= logicBlocks.size();i++){
            list = logicBlocks.get(i);
            //int randNumber =rand.nextInt(MAX - MIN + 1) + MIN; // randNumber 将被赋值为一个 MIN 和 MAX 范围内的随机数
            if(list.size() == 0){
                throw new ErrorCode(ErrorCode.BLOCK_DAMAGE);
            }
            int randNumber = rand.nextInt(list.size());
            if(randNumber%2 == 0){
                BlockManager blockManager = fileSystem.getBlockManager(list.get(randNumber));
                Block block = blockManager.newBlock(list.get(randNumber+1),false);
                byte[] bytes;
                try {
                    bytes = block.read();
                }catch (ErrorCode errorCode){
                    tools.handleDamageBlock(this,i,randNumber,true);
                    i--;
                    continue;
                }
                if(fileCheckSum.checksumMD5(block.getDataPath(),block.getChecksum())){
                    result.append(new String(bytes));
                }else{
                    tools.handleDamageBlock(this,i,randNumber,false);
                    i--;
                }
            }else{
                BlockManager blockManager = fileSystem.getBlockManager(list.get(randNumber-1));
                Block block = blockManager.newBlock(list.get(randNumber),false);
                byte[] bytes;
                try{
                    bytes = block.read();
                }catch (ErrorCode errorCode){
                    tools.handleDamageBlock(this,i,randNumber-1,true);
                    i--;
                    continue;
                }
                if(fileCheckSum.checksumMD5(block.getDataPath(),block.getChecksum())){
                    result.append(new String(bytes));
                }else{
                    tools.handleDamageBlock(this,i,randNumber-1,false);
                    i--;
                }
            }
        }
        return result.toString().getBytes();
    }

    @Override
    public void write(byte[] b) throws IOException {
        if(b.length == 0){
            return;
        }

        int oldLogicBlocksSize = logicBlocks.size();
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        byte[] bytes = null;
        ArrayList<Block> temBlocks = new ArrayList<>();
        int correctBlockNum = 0;

        if(cursor >= fileSize){
            correctBlockNum = getModifyBlockId((int) cursor)-1;
            bytes = new byte[(int) (cursor-fileSize)+b.length];
            for(int i = 0;i < (cursor-fileSize);i++){
                bytes[i] = 0x00;
            }
            for(int i = 0;i < b.length;i++){
                bytes[(int) ((cursor-fileSize)+i)] = b[i];
            }
            int blockNum = (int) Math.ceil((float)bytes.length/fileSystem.getDataBlockSize());
            byte[] tmp = new byte[blockSize];
            for(int i = 0;i < blockNum;i++){
                BlockManager blockManager = fileSystem.getABlockManager();
                if(i == blockNum-1){
                    tmp = new byte[bytes.length - i*blockSize];
                    System.arraycopy(bytes,i*blockSize,tmp,0,bytes.length - i*blockSize);
                }else{
                    System.arraycopy(bytes,i*blockSize,tmp,0,blockSize);
                }
                temBlocks.add( blockManager.newBlock(tmp));
            }
            fileManager.changeFileMeta(temBlocks,correctBlockNum+1,oldLogicBlocksSize,this);
        }else if(cursor < fileSize){
            int beginIndex = getModifyBlockId((int) cursor);
            byte[] bytes1 = getALogicBlock(beginIndex).read();
            byte[] tmp = new byte[bytes1.length+b.length];
            System.arraycopy(bytes1,0,tmp,0, (int) (cursor - getFileSizeAt(beginIndex-1)));
            System.arraycopy(b,0,tmp,(int) (cursor - getFileSizeAt(beginIndex-1)),b.length);
            System.arraycopy(bytes1,(int) (cursor - getFileSizeAt(beginIndex-1)),tmp,(int) (cursor - getFileSizeAt(beginIndex-1))+b.length,bytes1.length-(int) (cursor - getFileSizeAt(beginIndex-1)));
            int blockNum = (int) Math.ceil((float)tmp.length/blockSize);
            byte[] tmp1 = new byte[blockSize];
            for(int i = 0;i < blockNum;i++){
                BlockManager blockManager = fileSystem.getABlockManager();
//                System.out.println(Arrays.toString(tmp));
                if(i == blockNum-1){
                    tmp1 = new byte[tmp.length-i*blockSize];
                    System.arraycopy(tmp,i*blockSize,tmp1,0,tmp.length-i*blockSize);
                }else {
                    System.arraycopy(tmp,i*blockSize,tmp1,0,blockSize);
                }
                Block block = blockManager.newBlock(tmp1);
                temBlocks.add(block);
            }
            fileManager.changeFileMeta(temBlocks,beginIndex,oldLogicBlocksSize,this);
        }
    }

    @Override
    public int pos() {
        return IFile.super.pos();
    }

    @Override
    public int move(int offset, int where) {
        cursor = offset + where;
        return (int) cursor;
    }

    @Override
    public int getSize() {
        return (int) this.fileSize;
    }

    @Override
    public void setSize(int newSize) throws IOException {
        int oldFileSize = (int) getFileSize();
        if(newSize >= oldFileSize) {
            move(oldFileSize, 0);
            byte[] bytes = new byte[newSize - oldFileSize];
            for(int i = 0;i < (newSize - oldFileSize);i++){
                bytes[i] = 0x00;
            }
            write(bytes);
        }else {
            InitFileSystem fileSystem = InitFileSystem.getInstance();
            int modifyBlockIndex = getModifyBlockId(newSize);
            Block block = getALogicBlock(modifyBlockIndex);
            byte[] bytes = block.read();
            byte[] tmp = new byte[newSize - getFileSizeAt(modifyBlockIndex-1)];
            System.arraycopy(bytes,0,tmp,0,tmp.length);
            BlockManager blockManager = fileSystem.getABlockManager();
            Block block1 = blockManager.newBlock(tmp);
            fileManager.changeFileMeta(block1,this,modifyBlockIndex);
        }
    }

    @Override
    public void close() {

    }

    public String getMetaPath() {
        return metaPath;
    }

    private void setFileManager(FileManager fileManager){
        this.fileManager = fileManager;
    }

    public void setCursor(long cursor) {
        this.cursor = cursor;
    }

    public Block getALogicBlock(int index) throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        ArrayList<Integer> list = logicBlocks.get(index);
        Random random = new Random();
        //int randNumber =rand.nextInt(MAX - MIN + 1) + MIN; // randNumber 将被赋值为一个 MIN 和 MAX 范围内的随机数
        if(list.size() == 0){
            throw new ErrorCode(ErrorCode.BLOCK_DAMAGE);
        }
        int randNumber = random.nextInt(list.size());
        if(randNumber%2 == 0){
            BlockManager blockManager = fileSystem.getBlockManager(list.get(randNumber));
            return blockManager.newBlock(list.get(randNumber+1),false);
        }else{
            BlockManager blockManager = fileSystem.getBlockManager(list.get(randNumber-1));
            return blockManager.newBlock(list.get(randNumber),false);
        }
    }

    public int getFileSizeAt(int index) throws IOException {
        if(index == 0){
            return 0;
        }
        int result = 0;
        for(Integer key:logicBlocks.keySet()) {
            result = result + getALogicBlock(key).getSize();
            if(index == key){
                return result;
            }
        }
        return -1;
    }

    public int getModifyBlockId(int modifyIndex) throws IOException {
        int result = 0;
        int modifyBlockId = 0;
        for(Map.Entry entry:logicBlocks.entrySet()){
            result = result + getALogicBlock((Integer) entry.getKey()).getSize();
            if((modifyIndex >= result)&&((logicBlocks.size() == 1))){
                return 2;
            }
            if(modifyIndex < result){
                return (int) entry.getKey();
            }
            modifyBlockId = (int) entry.getKey();
        }
        return modifyBlockId+1;
    }

}
