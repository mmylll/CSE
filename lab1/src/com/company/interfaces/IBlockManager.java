package com.company.interfaces;

import com.company.impls.Block;

public interface IBlockManager {
    Block getBlock(int index);
    Block newBlock(byte[] b);
    Block newEmptyBlock(int blockSize);
}
