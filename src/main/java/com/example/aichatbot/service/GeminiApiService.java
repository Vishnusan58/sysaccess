package com.example.aichatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class GeminiApiService {

    private final WebClient webClient;
    private final String apiKey;

    /**
     * Constructor for GeminiApiService.
     * Initializes the WebClient with the base URL for the Google AI API.
     * The API key is stored separately to be added to each request.
     *
     * @param apiKey The API key for the Gemini API, injected from application properties.
     */
    public GeminiApiService(@Value("${gemini.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Sends a user message to the Gemini API and retrieves the model's response.
     *
     * @param userMessage The message from the user to send to the chatbot.
     * @return A Mono<String> containing the JSON response from the API.
     */
    public Mono<String> getChatCompletion(String userMessage) {
        // Use the correct request body structure for Gemini API
        Map<String, List<Map<String, List<Map<String, String>>>>> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", userMessage)
                                )
                        )
                )
        );

        return webClient.post()
                // FIXED: Updated to use the correct current model name
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-1.5-flash:generateContent")
                        .queryParam("key", this.apiKey)
                        .build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> {
                    System.err.println("Error calling Gemini API: " + error.getMessage());
                });
    }
}

