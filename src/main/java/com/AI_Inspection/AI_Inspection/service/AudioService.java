package com.AI_Inspection.AI_Inspection.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
//@AllArgsConstructor
public class AudioService {

    private static final String RESOURCE_FOLDER = "src/main/resources/uploads/";

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

//    private static final String API_KEY = "";


    public String openAISendRequest(MultipartFile file) {
        RestTemplate restTemplate = new RestTemplate();
        String finalTranscriptions = "";

        try {
            // Load all audio files from the resources folder
            File resourceFolder = new File("src/main/resources/uploads/audio");
            File[] audioFiles = resourceFolder.listFiles((dir, name) -> name.endsWith(".mp3") || name.endsWith(".wav"));

            if (audioFiles != null) {


                for (File audioFile : audioFiles) {
                    String transcription = transcribeAudio(restTemplate, file);
                    finalTranscriptions =  transcription + " " + finalTranscriptions;
                }
            } else {
                System.out.println("No audio files found in the folder.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalTranscriptions;
    }

    private static String transcribeAudio(RestTemplate restTemplate, MultipartFile audioFile) {
        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(API_KEY);

            // Prepare the file as a resource
//            FileSystemResource fileResource = new FileSystemResource(audioFile);
            File file = new File("/path/to/file");


            // Prepare the body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", "src/main/resources/uploads/audio/prueba_cliente.mp3");
            body.add("model", "whisper-1");

            // Create the request entity
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Send the POST request
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse the response (assuming it's JSON and contains the transcription text)
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
                return (String) responseMap.get("text");
            } else {
                System.err.println("Error: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Transcription failed";
    }
}
