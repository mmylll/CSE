import interfaces.Block;
import interfaces.BlockManager;
import interfaces.Id;

import java.io.*;

public class myBlockManager implements BlockManager {
    private String path;
    private Id name;

    public String getPath() {
        return path;
    }
    public myBlockManager(String path,Id id){
        this.path = path;
        this.name = id;
    }

    @Override
    public Id getName() {
        return name;
    }

    @Override
    public Block getBlock(Id indexId){
        if(indexId instanceof StringId) {
            StringId sid = (StringId) indexId;
            String id = sid.getId();

            String dataPath = path + id + ".data";
            String metaPath = path + id + ".meta";

            java.io.File data1 = new java.io.File(dataPath);
            java.io.File meta1 = new java.io.File(metaPath);

            if(!data1.exists() || !meta1.exists()){
                return null;
            }
            return new myBlock(indexId,this,dataPath,metaPath);
        }
        return null;
    }

    @Override
    public Block newBlock(byte[] b){
        String idPath = path + "id.count";
        java.io.File idFile = new java.io.File(idPath);
        if(!idFile.exists()){
            throw new ErrorCode(ErrorCode.ID_FILE_ERROR);
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(idFile));
            String tmp;
            tmp = br.readLine();
            long newIndex = Long.parseLong(tmp);
            String dataPath = path + newIndex + ".data";
            String metaPath = path + newIndex + ".meta";

            java.io.File dataFile = new java.io.File(dataPath);
            java.io.File metaFile = new java.io.File(metaPath);
            if(dataFile.exists() || metaFile.exists()){
                throw new ErrorCode(ErrorCode.ID_FILE_ERROR);
            }

            dataFile.createNewFile();
            metaFile.createNewFile();

            Block newBlock = new myBlock(new StringId(tmp),this,dataPath,metaPath);

            //改写id.count
            newIndex++;
            String n = newIndex + "";
            BufferedWriter bw = new BufferedWriter(new FileWriter(idFile,false));
            bw.write(n);
            bw.flush();
            bw.close();

            //写入data
            FileOutputStream os_data = new FileOutputStream(dataFile,false);
            BufferedInputStream is_data = new BufferedInputStream(new ByteArrayInputStream(b));
            os_data.write(b);
            os_data.flush();
            is_data.close();
            os_data.close();

            //写入meta
            String md5 = MD5Util.getMD5String(b);

            BufferedWriter bw1 = new BufferedWriter(new FileWriter(metaFile,false));
            String x = "size:"+b.length+"\n" + "checksum:"+md5+"\n";
            bw1.write(x);
            bw1.close();

            return newBlock;
        }catch (IOException e){
            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
        }
    }
}
