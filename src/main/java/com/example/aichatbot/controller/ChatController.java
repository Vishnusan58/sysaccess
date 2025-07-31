package com.example.aichatbot.controller;

import com.example.aichatbot.service.GeminiApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final GeminiApiService geminiApiService;

    @Autowired
    public ChatController(GeminiApiService geminiApiService) {
        this.geminiApiService = geminiApiService;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE}
    )
    public Mono<ResponseEntity<String>> chat(@RequestBody String userMessage) {
        return geminiApiService.getChatCompletion(userMessage)
                .map(response -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response))
                .onErrorResume(throwable -> {
                    String errorMessage = (throwable != null && throwable.getMessage() != null)
                            ? throwable.getMessage() : "Unknown error occurred";
                    System.err.println("Controller error: " + errorMessage);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(String.format("{\"error\": \"Failed to get response from AI: %s\"}",
                                    errorMessage.replace("\"", "\\\"")))
                    );
                });
    }
}