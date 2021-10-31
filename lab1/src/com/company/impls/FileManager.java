package com.company.impls;

import com.company.interfaces.IFile;
import com.company.interfaces.IFileManager;

public class FileManager implements IFileManager {

    private int id;
    private String path;

    public FileManager(int id) {
        this.id = id;
        path = "src/data/root/fm-"+ id +"/";
    }


    @Override
    public File getFile(int fileId) {
        return null;
    }

    @Override
    public File newFile(int fileId) {
        return null;
    }

    public String getFileMetaPath(int fileId){
        return path + fileId + ".meta";
    }
}
