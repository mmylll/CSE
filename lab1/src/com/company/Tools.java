package com.company;

import com.company.impls.File;
import com.company.impls.FileManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class Tools {
    public static byte[] smartCat(int fileId) throws IOException {
        InitFileSystem fileSystem = InitFileSystem.getInstance();
        FileManager fileManager = fileSystem.findFileManager(fileId);
        File file = new File(fileId,fileManager);


    }

    public static void readFileMeta(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file.getMetaPath()));
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
                case 2:
                    file.setFileName(split[1]);
                    break;
                case 4:
                    file.setFileSize(Long.parseLong(split[1]));
                    break;
                case 6:
                    Pattern p= Pattern.compile("^[1-9]\\d*$");
                    String[] strings = p.split(split[1]);
                    for(int i = 0;i < strings.length;i = i+2){

                    }
                    file.getLogicBlocks().put(Integer.parseInt(split[0]),Integer.parseInt(strings[]))


            }
        }
    }
}
