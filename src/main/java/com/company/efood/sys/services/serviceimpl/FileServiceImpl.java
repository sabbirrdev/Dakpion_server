package com.company.efood.sys.services.serviceimpl;

import com.company.efood.sys.model.FileResponse;
import com.company.efood.sys.services.FileService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final Path fileStorageLocation;


    @Autowired
    public FileServiceImpl(@Value("${file.upload.dir}") String uploadDir) throws IOException {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.fileStorageLocation);
    }

    @Override
    public FileResponse storeFile(MultipartFile file) throws IOException {
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = FilenameUtils.getExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + "." + extension;

        Path targetLocation = fileStorageLocation.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/private/files/view/")
                .path(filename)
                .toUriString();

        return new FileResponse(filename, fileUrl);
    }

    @Override
    public List<FileResponse> storeMultipleFile(List<MultipartFile> fileList) throws IOException {
        return fileList.stream().map(file -> {
            try {
                return storeFile(file);
            } catch (IOException e) {
                throw new RuntimeException("Could not store file", e);
            }
        }).toList();
    }

    @Override
    public Resource loadFile(String filename) throws MalformedURLException {
        Path filePath = fileStorageLocation.resolve(filename).normalize();
        if (!filePath.startsWith(fileStorageLocation)) {
            throw new SecurityException("Access denied: Invalid file path");
        }
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() && resource.isReadable()) return resource;
        else throw new MalformedURLException("File not found: " + filename);
    }

    @Override
    public void deleteFile(String filename) throws IOException {
        Path filePath = fileStorageLocation.resolve(filename).normalize();
        if (!filePath.startsWith(fileStorageLocation)) {
            throw new SecurityException("Access denied: Invalid file path");
        }
        Files.deleteIfExists(filePath);
    }

    @Override
    public FileResponse updateFile(String oldFilename, MultipartFile newFile) throws IOException {
        // Delete old
        deleteFile(oldFilename);

        // Upload new
        return storeFile(newFile);
    }
}
