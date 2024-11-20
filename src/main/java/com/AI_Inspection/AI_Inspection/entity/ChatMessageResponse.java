package com.AI_Inspection.AI_Inspection.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageResponse {
    @JsonProperty("role")
    private String role;
    @JsonProperty("content")
    private String content;
}
