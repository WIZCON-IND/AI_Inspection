package com.AI_Inspection.AI_Inspection.entity;

import lombok.*;
import org.apache.catalina.webresources.FileResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    private String model = "gpt-4o-mini";

    private List<ChatGptRequestMessage> messages = new ArrayList<>();

    public void addMessages(String key, String value) {
        messages.add(ChatGptRequestMessage.builder().role(key).content(value).build());

    }

}
