package com.AI_Inspection.AI_Inspection.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping(path="/file")
@AllArgsConstructor
public class FileUpload {

    private static final String RESOURCE_FOLDER = "src/main/resources/uploads/";


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestBody MultipartFile[] images,
                                            @RequestBody MultipartFile[] audio) {
        // Ensure the uploads directory exists
        File uploadDir = new File(RESOURCE_FOLDER+"audio/");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        File uploadDirImages = new File(RESOURCE_FOLDER+"images/");
        if (!uploadDirImages.exists()) {
            uploadDirImages.mkdirs();
        }

        try {
            for(MultipartFile file: audio) {
                // Save the file to the specified directory
                Path filePath = Paths.get(RESOURCE_FOLDER+"audio/" + file.getOriginalFilename());
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            for(MultipartFile file: images) {
                // Save the file to the specified directory
                Path filePath = Paths.get(RESOURCE_FOLDER+"images/" + file.getOriginalFilename());
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to upload file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return  new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
    }
}
