package interfaces;


import impls.Block;

import java.io.IOException;

public interface IBlockManager {
    Block getBlock(int index);
    Block newBlock(byte[] b) throws IOException;
    Block newEmptyBlock(int blockSize) throws IOException;
}
