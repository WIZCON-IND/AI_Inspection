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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.*;

@Service
public class ImageService {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

//    private static final String API_KEY = "";


    @Autowired
    private ImageJsonStringRepo imageJsonStringRepo;

    @Autowired
    private StringToJsonService stringToJsonService;


    public  void imageJson() throws IOException, InterruptedException {
        // Construct the JSON request body


        Map<String, String> imageURLs = encodeImageToBase64();

        for(Map.Entry<String, String> entry: imageURLs.entrySet()) {

            JSONObject contentText = new JSONObject();
            contentText.put("type", "text");
            contentText.put("text", "Based on the following categories:\n" +
                    "\n" +
                    "ACCESOS Y CIRCULACIÓN\n" +
                    "LÍNEAS DE VIDA. (SEÑALÉTICA, REVISIONES…)\n" +
                    "BARANDILLAS\n" +
                    "REJAS ANTICAÍDA. (ENSAYOS, FICHAS TÉCNICAS…)\n" +
                    "LIMPIEZA GENERAL\n" +
                    "CANALONES / LIMAHOYAS\n" +
                    "SUMIDEROS\n" +
                    "GÁRGOLAS / REBOSADEROS\n" +
                    "LÁMINA IMPERMEABLE / CHAPA GRECADA / PANEL. (IMPERMEABILIZACIÓN)\n" +
                    "FIJACIONES TORNILLERÍA\n" +
                    "JUNTAS Y SELLADOS (SOLAPES) / LONGITUDINALES O TRANSVERSALES\n" +
                    "PUNTOS SINGULARES (PASO DE INSTALACIONES, JUNTAS ESTRUCTURALES…)\n" +
                    "PETOS PERIMETRALES\n" +
                    "REMATES. (CUMBRERAS…)\n" +
                    "LUCERNARIOS Y CLARABOYAS\n" +
                    "AIREADORES / EXUTORIOS\n" +
                    "PARARRAYOS\n" +
                    "INSTALACIÓN FOTOVOLTAICA\n" +
                    "VENTILACIÓN / CLIMATIZACIÓN\n" +
                    "OTRAS INSTALACIONES\n" +
                    "BANCADAS\n" +
                    "I need you to analyze my image and assign a weightage from 0-100 based on the condition of each of the above categories. " +
                    "Additionally, I want the output in json format and only the json and with the fields: \"Image Name : " + entry.getKey()  + "(Keep the image Name " +
                    "in the main object instead of repeating it), \"Category\", \"Weightage\", and \"Description\". I want the json structure as below\n" +
                    "{\n" +
                    "  \"Image name\": \"dfsf\",  // The name of the image (e.g., 'dfsf')\n" +
                    "  \n" +
                    "  \"Categories\": [  // An array to hold the category objects, this allows multiple categories\n" +
                    "    {\n" +
                    "      \"category\": \"fd\",  // The name of the category (e.g., 'fd')\n" +
                    "      \"weightage\": 56,    // The weightage or importance of the category (e.g., 56)\n" +
                    "      \"description\": \"fddgdf\"  // A description of the category (e.g., 'fddgdf')\n" +
                    "    }\n" +
                    "  ]\n" +
                    "} ");

            // Create image URL content
            JSONObject contentImageUrl = new JSONObject();
            contentImageUrl.put("type", "image_url");

            JSONObject image_url = new JSONObject();
            image_url.put("url", entry.getValue());
            contentImageUrl.put("image_url", image_url);
            // Add both content types to an array
            JSONArray contentArray = new JSONArray();
            contentArray.add(contentText);
            contentArray.add(contentImageUrl);

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
                saveImageJsonToRepo(response.body(), entry.getKey());
                System.out.println(response);
            } else {
                System.out.println("Error: " + response.statusCode() + " - " + response.body());
            }

        }
    }

    private void saveImageJsonToRepo(String json, String imagename){
        String contentString = stringToJsonService.getJsonString(json);
        imageJsonStringRepo.save(ImageJsonString.builder().imageName(imagename).imagejson(contentString).build());
    }

    public static Map<String, String> encodeImageToBase64() throws IOException {
        File resourceFolder = new File("src/main/resources/uploads/images");
        File[] imageFiles = resourceFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpeg"));

        Map<String, String> imageURLs = new HashMap<>();

        if (imageFiles != null) {
            for (File imagefile : imageFiles) {

                byte[] fileContent = Files.readAllBytes(imagefile.toPath());
                imageURLs.put(imagefile.getName(), "data:image/jpeg;base64,"+Base64.getEncoder().encodeToString(fileContent));
            }

        }
        return imageURLs;
    }
}
