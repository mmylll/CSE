package interfaces;

import impls.BlockManager;
import java.io.IOException;

public interface IBlock {
    int getIndex();
    BlockManager getBlockManager();
    byte[] read() throws IOException;
    int getSize() throws IOException;
}
