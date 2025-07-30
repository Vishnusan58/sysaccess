package com.example.aichatbot.controller;

import com.example.aichatbot.service.GeminiApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // Add if you need CORS support
public class ChatController {

    private final GeminiApiService geminiApiService;

    @Autowired
    public ChatController(GeminiApiService geminiApiService) {
        this.geminiApiService = geminiApiService;
    }

    // Option 1: Accept JSON object with message field (Recommended)
    @PostMapping(consumes = {"application/json", "text/plain"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> chat(@RequestBody String userMessage) {
        return geminiApiService.getChatCompletion(userMessage)
                .map(response -> ResponseEntity.ok()
                        .header("Content-Type", "application/json")
                        .body(response))
                .onErrorResume(throwable -> {
                    String errorMessage = (throwable != null && throwable.getMessage() != null)
                            ? throwable.getMessage() : "Unknown error occurred";
                    System.err.println("Controller error: " + errorMessage);
                    ResponseEntity<String> errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header("Content-Type", "application/json")
                            .body("{\"error\": \"Failed to get response from AI: " +
                                    errorMessage.replace("\"", "\\\"") + "\"}");
                    return Mono.just(errorResponse);
                });
    }

    // Option 2: Alternative endpoint that accepts plain text (if you prefer)
    @PostMapping("/text")
    public Mono<ResponseEntity<String>> chatWithText(@RequestBody String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest()
                    .body("{\"error\": \"Message cannot be empty\"}"));
        }

        return geminiApiService.getChatCompletion(userMessage)
                .map(response -> ResponseEntity.ok()
                        .header("Content-Type", "application/json")
                        .body(response))
                .onErrorResume(throwable -> {
                    String errorMessage = throwable.getMessage() != null ? throwable.getMessage() : "Unknown error";
                    System.err.println("Controller error: " + errorMessage);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("{\"error\": \"Failed to get response from AI: " +
                                    errorMessage.replace("\"", "\\\"") + "\"}"));
                });
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"status\": \"UP\"}");
    }
}