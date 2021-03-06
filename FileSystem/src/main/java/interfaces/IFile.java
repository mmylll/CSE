package interfaces;
import impls.FileManager;

import java.io.IOException;

public interface IFile {
    int MOVE_CURR = -1; //只是光标的三个枚举值，具体数值⽆实际意义
    int MOVE_HEAD = 0;
    int MOVE_TAIL = -1;
    int getFileId();
    FileManager getFileManager();
    byte[] read(int length) throws IOException;
    void write(byte[] b) throws IOException;
    default int pos() {
        return move(0, MOVE_CURR);
    }
    int move(int offset, int where);//把⽂件光标移到距离where offset个byte的位置，并返回⽂件光标所在位置
    int getSize();
    void setSize(int newSize) throws IOException;
    //使⽤buffer的同学需要实现
    void close();
}
