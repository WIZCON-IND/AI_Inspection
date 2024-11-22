package com.AI_Inspection.AI_Inspection.controller;

import com.AI_Inspection.AI_Inspection.entity.DocumentDetails;
import com.AI_Inspection.AI_Inspection.entity.Message;
import com.AI_Inspection.AI_Inspection.repo.DocumentDetailsRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private DocumentDetailsRepo documentDetailsRepo;


    @PostMapping("/upload")
    public ResponseEntity<Message> uploadFile(@RequestParam("images") MultipartFile[] images,
                                              @RequestParam("audio") MultipartFile[] audio,
                                              @RequestParam("documentName") String documentName,
                                              @RequestParam("address") String address) {
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

            documentDetailsRepo.save(DocumentDetails.builder().documentName(documentName).Address(address).build());

        } catch (IOException e) {
            e.printStackTrace();
            Message msg = Message.builder().message("Failed to upload file").status(Boolean.FALSE).build();
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }


        Message msg = Message.builder().message("File uploaded successfully").status(Boolean.TRUE).build();
        return  new ResponseEntity<>(msg, HttpStatus.OK);
    }
}
