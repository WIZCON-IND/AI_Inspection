package com.AI_Inspection.AI_Inspection.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatChoiceResponse {
    @JsonProperty("index")
    private int index;
    @JsonProperty("message")
    private ChatMessageResponse message;
    @JsonProperty("finish_reason")
    private String finish_reason;
}
