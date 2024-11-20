package com.AI_Inspection.AI_Inspection.entity;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    private String model = "gpt-4";

    private MultipartFile file;

}
