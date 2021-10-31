package impls;

import error.ErrorCode;
import interfaces.IBlockManager;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BlockManager implements IBlockManager {
    private int id;
    private Map<Integer, Block> blocks = new HashMap<>();
    private String path;
    private ArrayList<Integer> freeBlocks = new ArrayList<>();
    private ArrayList<Integer> discardBlocks = new ArrayList<>();
    private String managerBlocksMetaPath;
    private int blocksNum;

    public BlockManager(int id) throws IOException {
        this.id = id;
        path = "src/main/resources/data/root/bm-"+id+"/";
        managerBlocksMetaPath = path + "managerBlock.meta";
        readManagerBlockMeta();
    }

    //blockManager自己新建块,并向其中写入内容，需要创建文件
    @Override
    public Block newBlock(byte[] b) throws IOException {
        int index = 0;
        if(freeBlocks.size()>0){
            index = freeBlocks.remove(0);
        }else{
            index = blocksNum + 1;
            blocksNum++;
        }
        Block block = new Block(index,this);
        blocks.put(block.getIndex(),block);
        createBlockFile(block);
        block.write(b);
        readMeta(block);
        return block;
    }

    //blockManager自己新建块，需要创建文件
    public Block newBlock() throws IOException {
        int index = 0;
        if(freeBlocks.size()>0){
            index = freeBlocks.remove(0);
        }else{
            index = blocksNum + 1;
            blocksNum++;
        }
        Block block = new Block(index,this);
        blocks.put(block.getIndex(),block);
        createBlockFile(block);
        return block;
    }

    @Override
    public Block newEmptyBlock(int blockSize) throws IOException {
        return new Block(1,this);
    }

    //用于获取一个逻辑块
    public Block newBlock(int blockId,boolean file) throws IOException {
        Block block = new Block(blockId,this);
        blocks.put(blockId,block);
        if(file) {
            createBlockFile(block);
        }
        return block;
    }

    @Override
    public Block getBlock(int index) {
        if(blocks.containsKey(index)){
            return blocks.get(index);
        }
        return null;
    }

    public byte[] readBlock(int blockId) throws IOException {
        return blocks.get(blockId).read();
    }

    public byte[] readBlock(Block block) throws IOException {
        return block.read();
    }

    public ArrayList<Integer> getFreeBlocks() {
        return freeBlocks;
    }

    public ArrayList<Integer> getDiscardBlocks() {
        return discardBlocks;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<Integer, Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(Map<Integer, Block> blocks) {
        this.blocks = blocks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBlocksNum() {
        return blocksNum;
    }

    public void setBlocksNum(int blocksNum) {
        this.blocksNum = blocksNum;
    }

    public void copyBlock(Block block,Block copyBlock) throws IOException {
        File blockFile = new File(block.getDataPath());
        File copyBlockFile = new File(copyBlock.getDataPath());
        FileUtils.copyFile(blockFile, copyBlockFile);
        copyBlock.getBlockManager().writeMeta(block.getChecksum(),copyBlock);
    }

    public void readMeta(Block block) throws IOException {
        File file = new File(block.getMetaPath());
        if(!file.exists()){
            throw new ErrorCode(ErrorCode.BLOCK_LOSS);
        }
        BufferedReader br = new BufferedReader(new FileReader(block.getMetaPath()));
        int lines = 0;
        String line = null;
        while ((line = br.readLine()) != null) {
            lines++;
            if (lines == 1) {
                continue;
            }
            line = line.replaceAll("\\s*", "");
            String[] split = line.split(":");
            if (lines == 2) {
                block.setIndex(Integer.parseInt(split[1]));
                continue;
            }
            if (lines == 3) {
                block.setDataPath(split[1]);
                continue;
            }
            if(lines == 4){
                block.setChecksum(split[1]);
                break;
            }

        }
        br.close();
    }

    public void writeMeta(String checkSum,Block block) throws IOException {
//        try {
            FileWriter fw = new FileWriter(block.getMetaPath(), false);
            fw.write("{\n" +
                    "  id: " + block.getIndex() + "\n" +
                    "  physical path: " + block.getDataPath() + "\n" +
                    "  checksum: " + checkSum + "\n" +
                    "}");
            fw.flush();
            fw.close();
//        }catch (IOException ioException){
//            throw new ErrorCode(ErrorCode.BLOCK_UPDATE_META_FAILED);
//        }
    }

    public void createBlockFile(Block block) throws IOException {
        new FileWriter(block.getMetaPath());
        new FileWriter(block.getDataPath());
    }

    public void readManagerBlockMeta() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(managerBlocksMetaPath));
        String line = null;
        int lines = 0;
        while ((line = br.readLine()) != null&& !line.equals("")) {
            lines++;
            if(lines == 1){
                String[] split = line.split(" ");
                for(int i = 0;i < split.length;i++){
                    freeBlocks.add(Integer.parseInt(split[i]));
                }
            }else if(lines == 2){
                String[] split = line.split(" ");
                for(int i = 0;i < split.length;i++){
                    discardBlocks.add(Integer.parseInt(split[i]));
                }
            }

        }
    }

    public void writeManagerBlockMeta() throws IOException {
        FileWriter fileWriter = new FileWriter(managerBlocksMetaPath,false);
        StringBuilder managerBlockMeta = new StringBuilder();
        for(int i = 0;i < freeBlocks.size();i++){
            managerBlockMeta.append(freeBlocks.get(i)).append(" ");
        }
        managerBlockMeta.append("\n");
        for(int i = 0;i < discardBlocks.size();i++){
            managerBlockMeta.append(discardBlocks.get(i)).append(" ");
        }
        fileWriter.write(String.valueOf(managerBlockMeta));
        fileWriter.flush();
        fileWriter.close();
    }
}
