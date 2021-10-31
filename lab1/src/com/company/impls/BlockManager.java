package com.company.impls;

import com.company.interfaces.IBlock;
import com.company.interfaces.IBlockManager;

import java.util.Map;

public class BlockManager implements IBlockManager {
    private int id;
    private Map<Integer, Block> blocks;

    public BlockManager(int id, Map<Integer, Block> blocks) {
        this.id = id;
        this.blocks = blocks;
    }

    public BlockManager(int id){
        this.id = id;
    }

    public BlockManager(int id,String metaPath){
        this.id = id;
    }

    @Override
    public Block getBlock(int index) {
        if(blocks.containsKey(index)){
            return blocks.get(index);
        }
        return null;
    }

    @Override
    public Block newBlock(byte[] b) {
        Block block = new Block(1);
        return block;
    }

    @Override
    public Block newEmptyBlock(int blockSize) {
        return new Block(1);
    }
}
