package com.example.aichatbot.controller;

import com.example.aichatbot.service.GeminiCliService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CliModeRunner implements CommandLineRunner {
    private final GeminiCliService geminiCliService;

    @Value("${app.cli-mode.enabled:false}")
    private boolean cliModeEnabled;

    public CliModeRunner(GeminiCliService geminiCliService) {
        this.geminiCliService = geminiCliService;
    }

    @Override
    public void run(String... args) {
        // Check if CLI mode is enabled via command line or properties
        boolean isCliMode = Arrays.stream(args)
                .anyMatch(arg -> arg.equalsIgnoreCase("--cli-mode")) || cliModeEnabled;

        if (isCliMode) {
            geminiCliService.startInteractiveCLI();
            // Exit application after CLI mode
            System.exit(0);
        }
    }
}