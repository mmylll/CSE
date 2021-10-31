package impls;

import error.ErrorCode;
import init.InitFileSystem;
import interfaces.IBlock;
import tool.FileCheckSum;
import tool.Tools;

import java.io.*;
import java.io.File;
import java.util.Random;

public class Block implements IBlock {
    private int index;
    private int blockSize;
    private BlockManager blockManager;
    private String metaPath;
    private String dataPath;
    private InitFileSystem fileSystem;
    private String checksum;

    public Block(int index,BlockManager blockManager) throws IOException {
        this.index = index;
        this.blockManager = blockManager;
        this.metaPath = blockManager.getPath() + index + ".meta";
        this.dataPath = blockManager.getPath() + index + ".data";
        fileSystem = InitFileSystem.getInstance();
        blockSize = fileSystem.getDataBlockSize();
    }

    @Override
    public byte[] read() throws IOException {
        blockManager.readMeta(this);
        Tools tools = new Tools();
        File file = new File(dataPath);
        if(!file.exists()){
            throw new ErrorCode(ErrorCode.BLOCK_LOSS);
        }
        BufferedReader br = new BufferedReader(new FileReader(dataPath));
        StringBuilder result = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
           result.append(line);

        }
        br.close();
        return result.toString().getBytes();
    }

    //写进去就修改meta
    public void write(byte[] b) throws IOException {
        try {
            try (OutputStream out = new BufferedOutputStream(new FileOutputStream(dataPath, false))) {
                out.write(b);
            }
            FileCheckSum fileCheckSum = new FileCheckSum();
            blockManager.writeMeta(fileCheckSum.getChecksumMD5(dataPath), this);
        }catch (ErrorCode errorCode){
            throw new ErrorCode(ErrorCode.BLOCK_GET_META_FAILED);
        }
    }

    public String getMetaPath() {
        return metaPath;
    }

    public void setMetaPath(String metaPath) {
        this.metaPath = metaPath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public BlockManager getBlockManager() {
        return this.blockManager;
    }

    @Override
    public int getSize() throws IOException {
        return read().length;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getChecksum(){
        return this.checksum;
    }
}
