package com.company.impls;

import com.company.interfaces.IFile;
import com.company.interfaces.IFileManager;

import java.util.ArrayList;
import java.util.Map;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class File implements IFile {
    private int fileId;
    private String fileName;
    private long fileSize;
    Multimap<String, String> logicBlocks;
    private String metaPath;
    private FileManager fileManager;

    public File(int fileId, String metaPath, Map<Integer,ArrayList<Integer>> logicBlocks) {
        this.fileId = fileId;
        this.metaPath = metaPath;
        this.logicBlocks = logicBlocks;
    }

    public File(int fileId,FileManager fileManager){
        this.fileId = fileId;
        this.fileManager = fileManager;
        metaPath = fileManager.getFileMetaPath(fileId);
    }

    public Map<Integer, ArrayList<Integer>> getLogicBlocks() {
        return logicBlocks;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int getFileId() {
        return this.fileId;
    }

    @Override
    public FileManager getFileManager() {
        return this.fileManager;
    }

    @Override
    public byte[] read(int length) {
        return new byte[0];
    }

    @Override
    public void write(byte[] b) {

    }

    @Override
    public int pos() {
        return IFile.super.pos();
    }

    @Override
    public int move(int offset, int where) {
        return 0;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void setSize(int newSize) {

    }

    @Override
    public void close() {

    }

    public void readMeta(){
        //todo 读取文件meta
    }

    public String getMetaPath() {
        return metaPath;
    }

    private void setFileManager(FileManager fileManager){
        this.fileManager = fileManager;
    }

}
