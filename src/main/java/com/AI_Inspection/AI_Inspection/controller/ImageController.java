package com.AI_Inspection.AI_Inspection.controller;

import com.AI_Inspection.AI_Inspection.service.FinalImageJson;
import com.AI_Inspection.AI_Inspection.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public void uploadFile(@RequestParam("file") MultipartFile[] files) throws IOException, InterruptedException {

        imageService.imageJson();
    }

    @GetMapping("/final-json")
    public void finalJson() throws IOException, InterruptedException {

        finalImageJson.imageJson();
    }

}
