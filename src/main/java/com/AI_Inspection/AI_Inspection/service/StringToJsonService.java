package com.AI_Inspection.AI_Inspection.service;

import com.AI_Inspection.AI_Inspection.entity.ImageWeightage;
import com.AI_Inspection.AI_Inspection.repo.ImageWeightageRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StringToJsonService {

    @Autowired
    private ImageWeightageRepo imageWeightageRepo;

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

    public List<ImageWeightage> saveDataFromJson(String jsonString) {
        List<ImageWeightage> imageWeightageList = new ArrayList<>();
        try {
            jsonString = jsonString.replaceFirst("^```json\\s*", "");
            // Parse JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // Extract image name
            String imageName = rootNode.get("Image name").asText();

            // Extract categories array
            JsonNode categories = rootNode.get("Categories");

            // Iterate through categories and save to database
            for (JsonNode categoryNode : categories) {
                String category = categoryNode.get("category").asText();
                int weightage = categoryNode.get("weightage").asInt();
                String description = categoryNode.get("description").asText();

                // Create and save entity
                ImageWeightage imageData = new ImageWeightage();
                imageData.setImageName(imageName.strip());
                imageData.setCategory(category.strip());
                imageData.setWeightage(weightage);
                imageData.setDescription(description);

                imageWeightageList.add(imageData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageWeightageList;
    }
}
