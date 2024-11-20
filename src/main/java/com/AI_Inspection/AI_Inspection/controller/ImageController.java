package com.AI_Inspection.AI_Inspection.controller;

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

    @GetMapping("/demo")
    public void uploadFile(@RequestParam("file") MultipartFile[] files) throws IOException, InterruptedException {

        imageService.imageJson();
    }

}
