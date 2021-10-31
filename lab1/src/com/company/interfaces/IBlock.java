package com.company.interfaces;

import com.company.impls.BlockManager;

public interface IBlock {
    int getIndex();
    BlockManager getBlockManager();
    byte[] read();
    int getSize();
}
