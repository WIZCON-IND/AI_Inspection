package com.AI_Inspection.AI_Inspection.controller;

import com.AI_Inspection.AI_Inspection.entity.ImageJsonString;
import com.AI_Inspection.AI_Inspection.entity.ImageWeightage;
import com.AI_Inspection.AI_Inspection.entity.Message;
import com.AI_Inspection.AI_Inspection.repo.ImageJsonStringRepo;
import com.AI_Inspection.AI_Inspection.repo.ImageWeightageRepo;
import com.AI_Inspection.AI_Inspection.service.FinalImageJson;
import com.AI_Inspection.AI_Inspection.service.ImageService;
import com.AI_Inspection.AI_Inspection.service.StringToJsonService;
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

    @Autowired
    private ImageJsonStringRepo imageJsonStringRepo;

    @Autowired
    private StringToJsonService stringToJsonService;

    @Autowired
    private ImageWeightageRepo imageWeightageRepo;

    @GetMapping("/demo")
    public ResponseEntity<Message> uploadFile() throws IOException, InterruptedException {

        Boolean status = imageService.imageJson();
        Message msg;
        if(status)
            msg = Message.builder().status(status).message("Successfully got response").build();
        else
            msg = Message.builder().status(status).message("Failed to got response").build();

        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @GetMapping("/final-json")
    public ResponseEntity<Map<String, List<String>>> finalJson() throws IOException, InterruptedException {

//        return new ResponseEntity<>(finalImageJson.imageJson(), HttpStatus.OK);
        return new ResponseEntity<>(imageService.getTop4ImagesPerCategory(), HttpStatus.OK);
    }

    @GetMapping("/save-weightage")
    public ResponseEntity<String> saveWeightage() {

        List<ImageJsonString> jsonString = imageJsonStringRepo.findAll();
        for(ImageJsonString contentString: jsonString){
            List<ImageWeightage> imageWeightageList = stringToJsonService.saveDataFromJson(contentString.getImagejson());
            imageWeightageRepo.saveAll(imageWeightageList);
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
