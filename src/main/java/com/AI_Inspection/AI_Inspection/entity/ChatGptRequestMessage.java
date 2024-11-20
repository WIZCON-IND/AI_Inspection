package com.AI_Inspection.AI_Inspection.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatGptRequestMessage {
    private String role;
    private String content;
}
