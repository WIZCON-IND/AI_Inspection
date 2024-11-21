package com.AI_Inspection.AI_Inspection.service;

import com.AI_Inspection.AI_Inspection.entity.ImageJsonString;
import com.AI_Inspection.AI_Inspection.repo.ImageJsonStringRepo;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FinalImageJson {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

//    private static final String API_KEY ="";

    @Autowired
    private ImageJsonStringRepo imageJsonStringRepo;
    @Autowired
    private StringToJsonService stringToJsonService;

    public Map<String, List<String>> imageJson() throws IOException, InterruptedException {

        List<ImageJsonString> imageJson = imageJsonStringRepo.findAll();

        String concatenatedJson = imageJson.stream()
                .map(ImageJsonString::getImagejson)
                .collect(Collectors.joining("\n"));

        JSONObject contentText = new JSONObject();
        contentText.put("type", "text");
        contentText.put("text",concatenatedJson+  "\nTake the above JSONs and create a single JSON containing all the categories, where each category includes " +
                "the top 4 images with the highest weightage for that category, along with their image name and description. Only give the json part" +
                "The json will be in the below format: \n" +
                "{\n" +
                "  \"CATEGORY_NAME\": [\n" +
                "    {\n" +
                "      \"Image Name\": \"STRING\",\n" +
                "      \"Weightage\": NUMBER,\n" +
                "      \"Description\": \"STRING\"\n" +
                "    }\n" +
                "  ]\n" +
                "}");

        // Add both content types to an array
        JSONArray contentArray = new JSONArray();
        contentArray.add(contentText);

        // Create the messages array with the user role and the content
        JSONArray messagesArray = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", contentArray);
        messagesArray.add(message);

        // Create the final request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", messagesArray);

        // Create the HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Build the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check the response status
        if (response.statusCode() == HttpStatus.OK.value()) {
            System.out.println(response);
        } else {
            System.out.println("Error: " + response.statusCode() + " - " + response.body());
        }

        Map<String, List<String>> result = stringToJsonService.extractCategoryImages(response.body());
        System.out.println(result);
        return result;
    }

}
