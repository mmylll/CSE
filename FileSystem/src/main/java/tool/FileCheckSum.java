package tool;

import org.apache.commons.codec.digest.DigestUtils;
import java.io.FileInputStream;
import java.io.IOException;

public class FileCheckSum {
    public FileCheckSum(){

    }
    public boolean checksumSHA256(String dataPath,String checkSum) throws IOException {
        String checksumSHA256 = DigestUtils.sha256Hex(new FileInputStream(dataPath));
        return checkSum.equals(checksumSHA256);

    }
    public boolean checksumMD5(String dataPath,String checkSum) throws IOException {
        String checksumMD5 = DigestUtils.md5Hex(new FileInputStream(dataPath));
        return checkSum.equals(checksumMD5);
    }

    public String getChecksumSHA256(String dataPath) throws IOException {
        return DigestUtils.sha256Hex(new FileInputStream(dataPath));
    }

    public String getChecksumMD5(String dataPath) throws IOException {
        return DigestUtils.md5Hex(new FileInputStream(dataPath));
    }
}
