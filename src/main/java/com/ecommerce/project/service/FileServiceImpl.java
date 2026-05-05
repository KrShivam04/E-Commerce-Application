package com.ecommerce.project.service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * Stores an uploaded image in the provided directory and returns generated filename.
     *
     * @param path destination directory path
     * @param file uploaded multipart file
     * @return generated file name
     * @throws IOException when file cannot be stored
     */
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // File name of current/ original file 
        String originalFileName = file.getOriginalFilename();
        logger.info("Uploading image originalFilename={}, size={} bytes", originalFileName, file.getSize());

        // generate a unique file name for overridding issue 
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator + fileName;

        // checking if the path exists if not create the path 
        File folder = new File(path);
        if (!folder.exists()) {
            logger.info("Creating image upload directory path={}", path);
            folder.mkdir();
        }

        // upload the image to the server
        Files.copy(file.getInputStream(), Paths.get(filePath));
        
        // return file name
        logger.info("Image uploaded successfully fileName={}, path={}", fileName, filePath);
        return fileName;
    }
}
