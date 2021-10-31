package com.company;

import com.company.impls.BlockManager;
import com.company.impls.FileManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class InitFileSystem {
    private int metaBlockSize;
    private int dataBlockSize;
    private ArrayList<FileManager> fileManagerArray;
    private ArrayList<BlockManager> blockManagerArray;
    private Map<Integer,Integer> fileFileManagerMap;
    private static volatile InitFileSystem instance = null;

    private InitFileSystem() throws IOException {
        readInit();
    }

    //运行时加载对象
    public static InitFileSystem getInstance() throws IOException {
        if (instance == null) {
            synchronized(InitFileSystem.class){
                if(instance == null){
                    instance = new InitFileSystem();
                }
            }
        }
        return instance;
    }

    public int getMetaBlockSize() {
        return metaBlockSize;
    }

    public void setMetaBlockSize(int metaBlockSize) {
        this.metaBlockSize = metaBlockSize;
    }

    public int getDataBlockSize() {
        return dataBlockSize;
    }

    public void setDataBlockSize(int dataBlockSize) {
        this.dataBlockSize = dataBlockSize;
    }

    public ArrayList<FileManager> getFileManagerArray() {
        return fileManagerArray;
    }

    public void setFileManagerArray(ArrayList<FileManager> fileManagerArray) {
        this.fileManagerArray = fileManagerArray;
    }

    public ArrayList<BlockManager> getBlockManagerArray() {
        return blockManagerArray;
    }

    public void setBlockManagerArray(ArrayList<BlockManager> blockManagerArray) {
        this.blockManagerArray = blockManagerArray;
    }

    public Map<Integer, Integer> getFileFileManagerMap() {
        return fileFileManagerMap;
    }

    public void setFileFileManagerMap(Map<Integer, Integer> fileFileManagerMap) {
        this.fileFileManagerMap = fileFileManagerMap;
    }

    private void readInit() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("E:\\MyProject\\CSE\\lab1\\src\\data\\init.meta"));

        int lines = 1;
        String line = null;
        while ((line = br.readLine()) != null) {
            if(lines == 1){
                lines++;
                continue;
            }
            System.out.println(line);
            String[] split = line.split(":");
            switch (lines) {
                case 2 :
                    metaBlockSize = Integer.parseInt(split[1]);
                    lines++;
                    break;
                case 3 :
                    dataBlockSize = Integer.parseInt(split[1]);
                    lines++;
                    break;
                case 4 :
                    int[] fileManagers = initReadManagers(split[1]);
                    int fileManager_num = fileManagers.length;
                    for (int manager : fileManagers) {
                        FileManager fileManager = new FileManager(manager);
                        fileManagerArray.add(fileManager);
                    }
                    lines++;
                    break;
                case 5:
                    int[] blockManagers = initReadManagers(split[1]);
                    int blockManager_num = blockManagers.length;
                    for (int manager : blockManagers) {
                        BlockManager blockManager = new BlockManager(manager);
                        blockManagerArray.add(blockManager);
                    }
                    lines++;
                    break;
                default:
                    if(split.length > 1){
                        fileFileManagerMap.put(Integer.parseInt(split[0]),Integer.parseInt(split[1]));
                    }
                    lines++;
                    break;
            }
        }
        br.close();

    }

    private int[] initReadManagers(String managers){
        String[] split = managers.split(",");
        return Arrays.stream(split).mapToInt(Integer::parseInt).toArray();
    }

    public FileManager findFileManager(int fileId){
        if (fileFileManagerMap.containsKey(fileId)){
            return new FileManager(fileId);
        }
        return null;
    }
}
