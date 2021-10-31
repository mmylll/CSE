package com.company;

import com.company.impls.BlockManager;
import com.company.impls.FileManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {





	// write your code here
        System.out.println("1234");
        System.out.println("1".getBytes(StandardCharsets.UTF_8));
        File file = new File("E:\\MyProject\\CSE\\lab1\\src\\data\\init.meta");
        InputStream in = null;
//        try {
//            System.out.println("以字节为单位读取文件内容，一次读一个字节：");
//            // 一次读一个字节
//            in = new FileInputStream(file);
//            int tempbyte;
//            while ((tempbyte = in.read()) != -1) {
//                System.out.write(tempbyte);
//            }
//            in.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }

        try {
            System.out.println("以字节为单位读取文件内容，一次读多个字节：");
            // 一次读多个字节
            byte[] tempbytes = new byte[100];
            int byteread = 0;
            in = new FileInputStream("E:\\MyProject\\CSE\\lab1\\src\\data\\init.meta");
            try {
                System.out.println("当前字节输入流中的字节数为:" + in.available());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 读入多个字节到字节数组中，byteread为一次读入的字节数
            while ((byteread = in.read(tempbytes)) != -1) {
                System.out.write(tempbytes, 0, byteread);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }

    }



//    public static FileManager findFileManager(int fileId){
//        FileManager fileManager = null;
//        Iterator<File> iterator = fileManagerArray.iterator();
//        while(iterator.hasNext()){
//            if(iterator.next().get)
//            System.out.println(it1.next());
//        }
//    }
}
