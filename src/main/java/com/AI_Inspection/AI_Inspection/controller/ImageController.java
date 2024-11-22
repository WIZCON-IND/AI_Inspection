package com.AI_Inspection.AI_Inspection.controller;

import com.AI_Inspection.AI_Inspection.entity.Message;
import com.AI_Inspection.AI_Inspection.service.FinalImageJson;
import com.AI_Inspection.AI_Inspection.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping(path="/image")
@AllArgsConstructor
public class ImageController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private FinalImageJson finalImageJson;

    @GetMapping("/demo")
    public ResponseEntity<Message> uploadFile(@RequestParam("file") MultipartFile[] files) throws IOException, InterruptedException {

        boolean status = imageService.imageJson();
        Message msg;
        if(status)
            msg = Message.builder().status(status).message("Successfully got response").build();
        else
            msg = Message.builder().status(status).message("Failed to got response").build();

        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @GetMapping("/final-json")
    public ResponseEntity<Map<String, List<String>>> finalJson() throws IOException, InterruptedException {

        return new ResponseEntity<>(finalImageJson.imageJson(), HttpStatus.OK);
    }

}
