package com.example.aichatbot.controller;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatRequest {
    // Getters and setters
    private String systemPrompt;
    private String userMessage;

}
