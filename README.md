
# AI Chatbot with Gemini API

[![Java Version](https://img.shields.io/badge/Java-21-brightgreen.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)

A Spring Boot application that integrates with Google's Gemini 1.5 Flash AI model to provide both a web-based chat interface and a CLI (Command Line Interface) for interacting with the AI.

## Features

- **Web Chat Interface**: RESTful API for chat interactions with Gemini AI
- **CLI Mode**: Interactive command-line interface for Gemini AI
- **System Command Execution**: Execute system commands through the CLI mode
- **Cross-Origin Support**: Configured for integration with various frontends

## Prerequisites

- Java 21
- Maven 3.6+ or the included Maven wrapper
- Google Gemini API key

## Configuration

1. Create an `application.properties` file in `src/main/resources/` with your Gemini API key:

```properties
gemini.api.key=your-api-key-here
```

## Building the Application

Use Maven to build the application:

```bash
./mvnw clean package
```

## Running the Application

### Standard Web Mode

Start the application in standard web mode:

```bash
./mvnw spring-boot:run
```

The REST API will be available at `http://localhost:8080/api/chat`.

### CLI Mode

Start the application in CLI mode:

```bash
./mvnw spring-boot:run --args="--cli-mode"
```

This will start an interactive CLI where you can:
- Type system commands to execute
- Interact with Gemini AI
- Type 'help' for available commands
- Type 'exit' to quit

## API Endpoints

### Chat API

```
POST /api/chat
Content-Type: application/json

"Your message to Gemini here"
```

### CLI Command Execution API

```
POST /api/cli/execute
Content-Type: application/json

{
    "command": "ls -l"
}
```

## Security Considerations

- The CLI mode executes system commands, so use with caution
- API key should be protected and not committed to version control
- Consider implementing proper authentication for production use

## Development

This project uses:
- Spring Boot for the web framework
- Spring WebFlux for reactive programming
- JPA for database support (configured for MySQL)
- Lombok for reducing boilerplate code

## License

[License details to be added]

## Contact

[Contact information to be added]
