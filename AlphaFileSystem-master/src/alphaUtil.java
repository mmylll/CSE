import interfaces.Block;
import interfaces.BlockManager;
import interfaces.File;
import interfaces.FileManager;

import java.util.ArrayList;

public class alphaUtil {
    private static ArrayList<File> openedFile = new ArrayList<>();
    private static File operatingFile;

    public static void alphaCat(String fmName, String fileName){
        FileManager fm = config.myFileManagerMap.get(new StringId(fmName));
        File file = fm.getFile(new StringId(fileName));
        operatingFile = file;
        file.move(0,File.MOVE_HEAD);
        System.out.println(new String(file.read((int)file.size())));
    }

    public static void alphaHex(String bmName, int blockIndex){
        BlockManager bm = config.myBlockManagerMap.get(new StringId(bmName));
        Block block = bm.getBlock(new StringId(blockIndex+""));
        if(block==null){
            throw new ErrorCode(ErrorCode.NO_SUCH_BLOCK);
        }
        
        byte[] bytes = block.read();
        for(int i = 0; i < bytes.length;i++){
            System.out.print("0x"+Integer.toHexString(bytes[i])+" ");
            if(i%16 == 15)
                System.out.print("\n");
        }
        System.out.print("\n");
    }

    public static void alphaWrite(int offset, String where, String content){
        int cursorWhere;
        if(operatingFile == null)
            throw new ErrorCode(ErrorCode.NO_OPERATING_FILE);
        if("cur".equals(where))
            cursorWhere = File.MOVE_CURR;
        else if("head".equals(where))
            cursorWhere = File.MOVE_HEAD;
        else if("tail".equals(where))
            cursorWhere = File.MOVE_TAIL;
        else
            throw new ErrorCode(ErrorCode.NO_THIS_CURSOR_TYPE);

        operatingFile.move(offset,cursorWhere);
        operatingFile.write(content.getBytes());
        if(!openedFile.contains(operatingFile))
            openedFile.add(operatingFile);
    }

    public static void alphaCopy(String fmNameOld, String fileNameOld, String fmNameNew, String fileNameNew){
        FileManager fmOld = config.myFileManagerMap.get(new StringId(fmNameOld));
        File fileOld = fmOld.getFile(new StringId(fileNameOld));
        FileManager fmNew = config.myFileManagerMap.get(new StringId(fmNameNew));
        File fileNew = fmNew.newFile(new StringId(fileNameNew));

        openedFile.add(fileNew);
        fileNew.write(fileOld.read((int)fileOld.size()));
    }

    public static void finish(){
        for(File f : openedFile){
            f.close();
        }
    }

    public static void alphaCreate(String fmName, String fileName){
        File file;
        FileManager fm = config.myFileManagerMap.get(new StringId(fmName));
        file = fm.newFile(new StringId(fileName));
        operatingFile = file;
    }

    public static void alphaSetSize(int newSize){
        if(operatingFile == null)
            throw new ErrorCode(ErrorCode.NO_OPERATING_FILE);
        operatingFile.setSize(newSize);
    }

    public static void alphaHelp(){
        StringBuilder sb = new StringBuilder();
        sb.append("cat [fm] [fileName]\n");
        sb.append("hex [bm] [blockName]\n");
        sb.append("write [offset] [cur/head/tail] context   --(Please cat first!)\n");
        sb.append("copy [oldFm] [oldFileName] [newFm] [newFileName]\n");
        sb.append("create [fm] [fileName]\n");
        sb.append("setSize [size]   --(Please cat first!)\n");
        sb.append("help      print help information.\n");
        System.out.print(sb.toString());
    }


}
