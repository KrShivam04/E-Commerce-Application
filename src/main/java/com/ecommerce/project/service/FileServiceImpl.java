package com.ecommerce.project.service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {
    // method to upload the image
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // File name of current/ original file 
        String originalFileName = file.getOriginalFilename();

        // generate a unique file name for overridding issue 
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator + fileName;

        // checking if the path exists if not create the path 
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }

        // upload the image to the server
        Files.copy(file.getInputStream(), Paths.get(filePath));
        
        // return file name
        return fileName;
    }
}
