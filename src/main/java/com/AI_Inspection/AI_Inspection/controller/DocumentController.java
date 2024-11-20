package com.AI_Inspection.AI_Inspection.controller;


import com.AI_Inspection.AI_Inspection.service.CreateDocxPages;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;
import java.io.File;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping(path="/doc")
@AllArgsConstructor
public class DocumentController {

    @Autowired
    private CreateDocxPages createDocxPages;

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadDocx() {
        try {
            // Generate the document in the service layer
            File generatedDoc = createDocxPages.createdocx();

            // Prepare the file as InputStreamResource for download
            ByteArrayInputStream inputStream = new ByteArrayInputStream(org.apache.commons.io.FileUtils.readFileToByteArray(generatedDoc));

            // Prepare the HTTP response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=InspectionReport.docx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate the document", e);
        }
    }
}
