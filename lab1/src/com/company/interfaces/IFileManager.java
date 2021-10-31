package com.company.interfaces;

import com.company.impls.File;

public interface IFileManager {
    File getFile(int fileId);
    File newFile(int fileId);
}
