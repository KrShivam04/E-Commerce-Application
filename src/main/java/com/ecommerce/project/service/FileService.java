package com.ecommerce.project.service;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
     /**
      * Uploads an image file into the target directory.
      */
     String uploadImage(String path, MultipartFile file) throws IOException;
}
