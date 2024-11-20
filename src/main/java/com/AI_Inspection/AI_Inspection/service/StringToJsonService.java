package com.AI_Inspection.AI_Inspection.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class StringToJsonService {

    public String getJsonString(String jsonString) {
        String content ="";
        try {
            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert the JSON string to JsonNode
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // Navigate to the "choices" array and extract the "content" value
            JsonNode choicesNode = rootNode.path("choices");
            JsonNode messageNode = choicesNode.get(0).path("message");
             content = messageNode.path("content").asText();
        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }
}
