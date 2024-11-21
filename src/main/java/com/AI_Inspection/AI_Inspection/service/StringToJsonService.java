package com.AI_Inspection.AI_Inspection.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public Map<String, List<String>> extractCategoryImages(String jsonString) throws JsonProcessingException {


        jsonString = getJsonString(jsonString);
        jsonString = jsonString.replaceFirst("^```json\\s*", "");

        System.out.println(jsonString);
        Map<String, List<String>> categoryImages = new HashMap<String, List<String>>();
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse JSON string
        JsonNode rootNode = objectMapper.readTree(jsonString);

        // Iterate over all categories
        rootNode.fieldNames().forEachRemaining(categoryName -> {

            if(categoryName.equalsIgnoreCase("SUMIDEROS")){
                List<String> images = new ArrayList<String>();

                // Extract image names for this category
                JsonNode categoryArray = rootNode.get(categoryName);
                for (JsonNode item : categoryArray) {
                    String imageName = item.get("Image Name").asText();
                    images.add(imageName);
                }
                categoryImages.put(categoryName, images);
            }
        });

        return categoryImages;
    }

    public  List<String> audioString(String jsonString) throws JsonProcessingException {

        List<String> result = new ArrayList<>();
        try {
            jsonString = jsonString.replaceFirst("^```json\\s*", "");
            // Create an ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Parse the JSON string into a JsonNode
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // Extract fields
            String descripcion = rootNode.get("estado").asText();
            String recomendacion = rootNode.get("recomendacion").asText();

            result.add(descripcion);
            result.add(recomendacion);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
