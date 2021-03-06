import interfaces.Block;
import interfaces.BlockManager;
import interfaces.Id;

import java.io.*;

public class myBlock implements Block {
    private Id indexId;
    private BlockManager blockManager;
    private String dataPath;
    private String metaPath;

    public myBlock(Id indexId,BlockManager blockManager,String dataPath,String metaPath){
        this.indexId = indexId;
        this.blockManager = blockManager;
        this.dataPath = dataPath;
        this.metaPath = metaPath;
    }

    public Id getIndexId(){
        return indexId;
    };
    public BlockManager getBlockManager(){
        return blockManager;
    };
    public byte[] read(){
        java.io.File dataFile = new java.io.File(dataPath);
        java.io.File metaFile = new java.io.File(metaPath);

        if(!dataFile.exists() || !metaFile.exists()){
            throw new ErrorCode(ErrorCode.NO_SUCH_BLOCK);
        }

        byte[] data;
        String checkSum;
        try{
            BufferedReader br = new BufferedReader(new FileReader(metaFile));
            String tmp = br.readLine();
            long blockSize = Long.parseLong(tmp.split(":")[1]);
            tmp = br.readLine();
            checkSum = tmp.split(":")[1];

            data = new byte[(int)blockSize];
            new FileInputStream(dataFile).read(data);
        }catch (IOException e){
            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
        }

        boolean check;
        check = MD5Util.checkPassword(data,checkSum);

        if(check) {
            return data;
        } else {
            throw new ErrorCode(ErrorCode.BLOCK_CHECK_FAILED);
        }
    }

    public int blockSize(){
        java.io.File metaFile = new java.io.File(metaPath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(metaFile));
            String tmp = br.readLine();
            long blockSize = Long.parseLong(tmp.split(":")[1]);
            return (int)blockSize;
        }catch (IOException e){
            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
        }
    };
}
