package com.example.aichatbot.controller;

import com.example.aichatbot.service.GeminiApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final GeminiApiService geminiApiService;

    @Autowired
    public ChatController(GeminiApiService geminiApiService)
    {
        this.geminiApiService = geminiApiService;
    }

    @PostMapping
    public Mono<ResponseEntity<String>> chat(@RequestBody String userMessage) {
        return geminiApiService.getChatCompletion(userMessage)
                .map(ResponseEntity::ok);
    }
}

