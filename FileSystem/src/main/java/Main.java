import error.ErrorCode;
import init.InitFileSystem;
import tool.Tools;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        InitFileSystem fileSystem = InitFileSystem.getInstance();
        Tools tools = new Tools();
        System.out.println("File System:");
        System.out.println("请输入操作指令，help查看命令");
        System.out.print(">>");
        int quit = 0;
            while(true){
                if(quit == 1){
                    break;
                }
                try{
                Scanner input = new Scanner(System.in);
                String operate = input.nextLine();
                switch (operate){
                    case "help":
                        tools.handleHelp();
                        System.out.println("------------------------------------------------------------");
                        System.out.println("请输入操作指令");
                        System.out.print(">>");
                        break;
                    case "cat":
                        System.out.println("请输入文件id：");
                        String id = input.nextLine();
                        if(!id.matches("^[0-9]+$")){
                            System.out.println("文件id错误：");
                            break;
                        }
                        System.out.println(new String(tools.smartCat(Integer.parseInt(id))));
                        System.out.println("------------------------------------------------------------");
                        System.out.println("请输入操作指令");
                        System.out.print(">>");
                        break;
                    case "hex":
                        System.out.println("请输入blockManager id：");
                        String blockManagerId = input.nextLine();
                        if(!blockManagerId.matches("^[0-9]+$")){
                            System.out.println("blockManager id错误：");
                            break;
                        }
                        System.out.println("请输入block id：");
                        String blockId = input.nextLine();
                        if(!blockId.matches("^[0-9]+$")){
                            System.out.println("block id错误：");
                            break;
                        }
                        tools.smartHex(Integer.parseInt(blockManagerId),Integer.parseInt(blockId));
                        System.out.println("------------------------------------------------------------");
                        System.out.println("请输入操作指令");
                        System.out.print(">>");
                        break;
                    case "write":
                        System.out.println("请输入需要写入的文件id：");
                        String writeId = input.nextLine();
                        if(!writeId.matches("^[0-9]+$")){
                            System.out.println("文件id错误");
                            break;
                        }
                        System.out.println("请输入offset：");
                        String offset = input.nextLine();
                        if(!offset.matches("^[0-9]+$")){
                            System.out.println("offset错误");
                            break;
                        }
                        System.out.println("请输入where：");
                        String where = input.nextLine();
                        if(!where.matches("^[0-9]+$")){
                            System.out.println("where错误");
                            break;
                        }
                        System.out.println("请输入:");
                        tools.smartWrite(Integer.parseInt(offset),Integer.parseInt(where),Integer.parseInt(writeId));
                        System.out.println("------------------------------------------------------------");
                        System.out.println("请输入操作指令");
                        System.out.print(">>");
                        break;
                    case "copy":
                        System.out.println("请输入需要复制的文件id：");
                        String id1 = input.nextLine();
                        if(!id1.matches("^[0-9]+$")){
                            System.out.println("文件id错误：");
                            break;
                        }
                        tools.smartCopy(Integer.parseInt(id1));
                        System.out.println("------------------------------------------------------------");
                        System.out.println("请输入操作指令");
                        System.out.print(">>");
                        break;
                    case "ls":
                        tools.smartLs();
                        System.out.println("------------------------------------------------------------");
                        System.out.println("请输入操作指令");
                        System.out.print(">>");
                        break;
                    case "create":
                        tools.smartCreateFile();
                        System.out.println("------------------------------------------------------------");
                        System.out.println("请输入操作指令");
                        System.out.print(">>");
                        break;
                    case "set_size":
                        tools.smartSetFileSize();
                        System.out.println("------------------------------------------------------------");
                        System.out.println("请输入操作指令");
                        System.out.print(">>");
                        break;
                    case "clear":
                        tools.smartClear();
                        System.out.println("------------------------------------------------------------");
                        System.out.println("请输入操作指令");
                        System.out.print(">>");
                        break;
                    case "reset":
                        tools.smartResetFileSystem();
                        System.out.println("------------------------------------------------------------");
                        System.out.println("请输入操作指令");
                        System.out.print(">>");
                        break;
                    case "quit":
                        tools.smartQuit();
                        quit = 1;
                        break;
                    default:
                        System.out.println("请输入正确的操作命令!");
                        System.out.print(">>");
                        break;

                }
                }catch (ErrorCode e){
//                    throw new ErrorCode(ErrorCode.getErrorText(e.getErrorCode()));
                    System.out.println(ErrorCode.getErrorText(e.getErrorCode()));
                    System.out.println("请输入操作命令");
                    System.out.print(">>");
                }
            }
    }
}