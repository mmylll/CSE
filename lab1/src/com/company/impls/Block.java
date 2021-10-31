package com.company.impls;

import com.company.interfaces.IBlock;
import com.company.interfaces.IBlockManager;

public class Block implements IBlock {
    private int index;
    private int blockSize = 20;
    private BlockManager blockManager;
    private String metaPath;


    public Block(int index,int blockSize) {
        this.index = index;
        this.blockSize = blockSize;
    }

    public Block(int index){
        this.index = index;
    }

    public Block(int index, int blockSize, BlockManager blockManager, String metaPath) {
        this.index = index;
        this.blockSize = blockSize;
        this.blockManager = blockManager;
        this.metaPath = metaPath;
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
    public byte[] read() {
        //todo 根据meta去读取整个块
        return new byte[0];
    }

    @Override
    public int getSize() {
        return this.blockSize;
    }


//    public void setData(byte[] datas) {
//        System.arraycopy(datas, 0, this.data, 0, datas.length);
//    }
    public void readMeta(){

    }
}
