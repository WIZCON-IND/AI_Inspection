package com.AI_Inspection.AI_Inspection.service;

import com.AI_Inspection.AI_Inspection.entity.AiPostBody;
import com.AI_Inspection.AI_Inspection.entity.ChatRequest;
import com.AI_Inspection.AI_Inspection.entity.ChatResponse;
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

    private static final String API_URL_CHAT = "https://api.openai.com/v1/chat/completions";

    private static final String API_URL = "https://api.openai.com/v1/audio/transcriptions";

//    private static final String API_KEY ="";

    public String openAISendRequest() {
        RestTemplate restTemplate = new RestTemplate();
        StringBuilder finalTranscriptions = new StringBuilder();
        String result = "";

        try {
            // Load all audio files from the resources folder
            File resourceFolder = new File("src/main/resources/uploads/audio");
            File[] audioFiles = resourceFolder.listFiles((dir, name) -> name.endsWith(".mp3") || name.endsWith(".wav"));

            if (audioFiles != null) {


                for (File audioFile : audioFiles) {
                    String transcription = transcribeAudio(restTemplate, audioFile);
                    finalTranscriptions.append(" \n").append(transcription);
                }
                result = summaryChat(String.valueOf(finalTranscriptions));
            } else {
                return "No audio files found in the folder.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String transcribeAudio(RestTemplate restTemplate, File audioFile) {
        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(API_KEY);


            FileSystemResource fileResource = new FileSystemResource(audioFile);

            // Prepare the body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
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


    private String summaryChat( String transcribedText) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        ChatRequest chatRequest = new ChatRequest();
        String apiContent = "Eres un experto analista de textos. Los anexos contienen un resumen del estado y recomendaciones de los desagües de tejado de una nave industrial concreta. Por favor actúa como inspector técnico de techos de naves industriales y en base a estos audios escribe en formato json, uno para el estado actual y otro con recomendaciones. Es indispensable que utilice referencias y haga mención de los siguientes códigos o normas: CTE DB-HS-1 Sección 6. Mantenimiento y Conservación 2006 NTE QTG 1976 Revestimientos de techos galvanizados, Recomendaciones generales del Sindicato de Perfiladores 1991. Proporcione el resultado en formato json como se muestra a continuación y solo proporcione la parte json:\n" +
                "{\n" +
                "    \"estado\": \"texto de estado\",\n" +
                "    \"recomendacion\": \"texto de recomendación\"\n" +
                "}";


        ChatResponse response = new ChatResponse();
        chatRequest.addMessages("system", apiContent);
        chatRequest.addMessages("user", transcribedText);
        HttpEntity<ChatRequest> entity = new HttpEntity<ChatRequest>(chatRequest, headers);
        response = restTemplate.postForObject(API_URL_CHAT, entity, ChatResponse.class);

        return response.getChoices().get(0).getMessage().getContent();
    }
}
