package com.example.aichatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GeminiCliService {
    private static final Logger logger = LoggerFactory.getLogger(GeminiCliService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    public void startInteractiveCLI() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("🚀 Gemini CLI Mode Activated");
            System.out.println("Type 'exit' to quit, 'help' for commands");

            while (true) {
                System.out.print("➜ ");
                String input = scanner.nextLine().trim();

                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("Exiting Gemini CLI Mode...");
                    break;
                }

                if ("help".equalsIgnoreCase(input)) {
                    displayHelp();
                    continue;
                }

                try {
                    String result = executeSystemCommand(input);
                    System.out.println("\n📋 Command Output:");
                    System.out.println(result);
                } catch (IOException e) {
                    handleIOException(e);
                } catch (InterruptedException e) {
                    handleInterruptedException(e);
                } catch (SecurityException e) {
                    handleSecurityException(e);
                } catch (Exception e) {
                    handleGeneralException(e);
                }
            }
        }
    }

    public String executeSystemCommand(String command) throws IOException, InterruptedException {
        // Validate and sanitize input
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Command cannot be empty");
        }

        ProcessBuilder processBuilder = new ProcessBuilder();

        // Use appropriate shell based on operating system
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            processBuilder.command("cmd.exe", "/c", command);
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
            processBuilder.command("bash", "-c", command);
        } else {
            throw new UnsupportedOperationException("Unsupported operating system");
        }

        try {
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Use try-with-resources to ensure stream closure
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String output = reader.lines()
                        .collect(Collectors.joining("\n"));

                // Wait for the process to complete with a timeout
                boolean completed = process.waitFor(30, TimeUnit.SECONDS);
                
                if (!completed) {
                    process.destroyForcibly(); // Kill the process if it takes too long
                    throw new TimeoutException("Command execution timed out");
                }

                int exitCode = process.exitValue();

                // Log the command and its exit code
                logger.info("Command '{}' executed with exit code: {}", command, exitCode);

                // Add exit code information
                return String.format("Command: %s\nExit Code: %d\nOutput:\n%s", 
                    command, exitCode, output);
            }
        } catch (TimeoutException e) {
            logger.error("Command execution timed out", e);
            throw new RuntimeException("Command took too long to execute", e);
        }
    }

    private void handleIOException(IOException e) {
        System.err.println("❌ I/O Error: " + e.getMessage());
        logger.error("I/O error during command execution", e);
    }

    private void handleInterruptedException(InterruptedException e) {
        System.err.println("⏸️ Command was interrupted: " + e.getMessage());
        logger.error("Command was interrupted", e);
        Thread.currentThread().interrupt(); // Preserve interrupt status
    }

    private void handleSecurityException(SecurityException e) {
        System.err.println("🔒 Security violation: " + e.getMessage());
        logger.warn("Security exception during command execution", e);
    }

    private void handleGeneralException(Exception e) {
        System.err.println("❗ Unexpected error: " + e.getMessage());
        logger.error("Unexpected error during command execution", e);
    }

    private void displayHelp() {
        System.out.println("\n📘 Gemini CLI Mode Help:");
        System.out.println("Available Commands:");
        System.out.println("  - Regular system commands (ls, pwd, etc.)");
        System.out.println("  - help: Show this help menu");
        System.out.println("  - exit: Exit Gemini CLI Mode");
        System.out.println("\n🔒 Security Note: Use commands carefully.");
    }
}