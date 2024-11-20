package com.AI_Inspection.AI_Inspection.controller;

import com.AI_Inspection.AI_Inspection.entity.AiPostBody;
import com.AI_Inspection.AI_Inspection.entity.ChatResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.AI_Inspection.AI_Inspection.service.AudioService;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping(path="/audio")
@AllArgsConstructor
public class AudioController {

    @Autowired
    private AudioService audioService;

    @PostMapping("/demo")
    public ResponseEntity<String> getInstructionsFromSyntax(@RequestBody MultipartFile file) {

        String response =  audioService.openAISendRequest(file);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
