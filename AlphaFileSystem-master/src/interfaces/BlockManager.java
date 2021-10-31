package interfaces;

public interface BlockManager {
    Block getBlock(Id indexId);
    Block newBlock(byte[] b);
    Id getName();
    default Block newEmptyBlock(int blockSize){
        return newBlock(new byte[blockSize]);
    }
}
