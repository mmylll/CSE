package interfaces;

import impls.File;

import java.io.IOException;

public interface IFileManager {
    File getFile(int fileId);
    File newFile(int fileId) throws IOException;
}
