package com.example.aichatbot.controller;

import com.example.aichatbot.service.GeminiCliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/cli")
public class CliModeController {
    private final GeminiCliService geminiCliService;

    @Autowired
    public CliModeController(GeminiCliService geminiCliService) {
        this.geminiCliService = geminiCliService;
    }

    @PostMapping(value = "/execute",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> executeCliCommand(@RequestBody CliCommandRequest request) {
        // Validate input with more robust checks
        if (request == null || !isValidCommand(request.getCommand())) {
            return Mono.just(ResponseEntity.badRequest()
                    .body("{\"error\": \"Invalid or unauthorized command\"}"));
        }

        return Mono.fromCallable(() -> geminiCliService.executeSystemCommand(request.getCommand()))
                .map(response -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response))
                .onErrorResume(this::handleError)
                .subscribeOn(Schedulers.boundedElastic()); // Move to separate thread
    }

    private boolean isValidCommand(String command) {
        // Implement more robust command validation
        if (command == null || command.trim().isEmpty()) {
            return false;
        }
        
        // Example of a simple whitelist (customize as needed)
        List<String> allowedCommands = Arrays.asList("ls", "pwd", "whoami", "date");
        String baseCommand = command.trim().split("\\s+")[0];
        
        return allowedCommands.contains(baseCommand);
    }

    private Mono<ResponseEntity<String>> handleError(Throwable throwable) {
        String errorMessage = (throwable != null && throwable.getMessage() != null)
                ? throwable.getMessage() : "Unknown error occurred";
        System.err.println("Controller error: " + errorMessage);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(String.format("{\"error\": \"Failed to execute command: %s\"}",
                        errorMessage.replace("\"", "\\\""))));
    }
}