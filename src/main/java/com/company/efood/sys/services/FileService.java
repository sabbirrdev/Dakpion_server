package com.company.efood.sys.services;

import com.company.efood.sys.model.FileResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public interface FileService {
    FileResponse storeFile(MultipartFile file) throws IOException;
    List<FileResponse> storeMultipleFile(List<MultipartFile> fileList) throws IOException;
    Resource loadFile(String filename) throws MalformedURLException;

    void deleteFile(String filename) throws IOException;

    FileResponse updateFile(String oldFilename, MultipartFile newFile) throws IOException;
}
