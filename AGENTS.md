# AGENTS.md - Apolo Music Bot Development Guide

This file provides guidelines and instructions for agentic coding agents working on this repository.

## Project Overview

Apolo Music Bot is a Discord music bot built with Java 25, Spring Boot 3.5.8, and Maven. It uses JDA for Discord API integration and LavaPlayer for audio streaming.

## Build & Run Commands

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ApoloMusicBotApplicationTests

# Run a single test method
./mvnw test -Dtest=ApoloMusicBotApplicationTests#contextLoads

# Package without running tests
./mvnw package -DskipTests

# Clean build
./mvnw clean
```

## Environment Configuration

The bot requires a `DISCORD_BOT_TOKEN` environment variable. The project uses `.envrc` with direnv for local development:

```bash
# .envrc
export DISCORD_BOT_TOKEN=your_discord_token_here
```

For Docker/Podman: Use `docker-compose.yml` or set environment variables in your deployment.

## Code Style Guidelines

### General Conventions

- **Package naming**: `com.zullid.apolo_music_bot.*`
- **Java version**: 25 (use latest Java features when appropriate)
- **Build tool**: Maven (use `./mvnw` wrapper, not system `mvn`)
- **Spring Boot version**: 3.5.8

### Project Structure

```
src/
├── main/java/com/zullid/apolo_music_bot/
│   ├── ApoloMusicBotApplication.java    # Main entry point
│   ├── config/                           # Configuration classes
│   ├── services/                         # Spring services
│   ├── player/                           # Player logic (state pattern)
│   ├── listeners/                        # JDA event listeners
│   └── handlers/                         # Audio handlers
└── test/java/                           # Test classes
```

### Imports

- Use explicit imports (no wildcard `.*` except for static imports)
- Order: standard Java → external libraries → project imports
- Use Lombok to reduce boilerplate

### Naming Conventions

- **Classes**: PascalCase (e.g., `AudioPlayerService`, `PlayerCommandListener`)
- **Methods/variables**: camelCase (e.g., `play()`, `addListener()`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_QUEUE_SIZE`)
- **Packages**: lowercase (e.g., `com.zullid.apolo_music_bot.services`)

### Annotations

The project uses Lombok and Spring extensively:

- `@Slf4j` - for logging
- `@Getter` / `@Setter` - for accessor methods
- `@RequiredArgsConstructor` - for constructor injection
- `@Component` / `@Service` - for Spring beans
- `@PostConstruct` - for initialization logic

### Logging

- Use Lombok's `@Slf4j` for all classes
- Use appropriate log levels: `log.info()`, `log.debug()`, `log.warn()`, `log.error()`
- Include contextual information in log messages

### Error Handling

- Use JDA's `.queue()` for async operations with callbacks
- Reply to user with helpful error messages for command failures
- Check preconditions before executing commands (e.g., `isFromGuild()`)

### State Pattern

The player uses a State pattern:
- `PlayerState` (interface/base)
- `ReadyState`, `PlayingState`, `PausedState` (implementations)
- Each state handles relevant commands via `onPlay()`, `onPause()`, etc.

### JDA Best Practices

- Always check `event.isFromGuild()` in guild commands
- Use `setEphemeral(true)` for help/error messages that should only be visible to the user
- Use `SlashCommandInteractionEvent.reply().queue()` for async responses

### Java Features

- Use Java text blocks (triple quotes) for multi-line strings
- Use records where immutable data containers are needed
- Use `switch` expressions for pattern matching on command names

### Testing

- Tests are located in `src/test/java/`
- Use `@SpringBootTest` for integration tests
- Run tests with `./mvnw test`
- Currently only a basic context load test exists

### Discord Commands

Available slash commands:
- `/play <query>` - Play a song or add to queue
- `/pause` - Pause playback
- `/resume` - Resume playback
- `/stop` - Stop and clear queue
- `/skip` - Skip current song
- `/help` - Show help message

### Key Dependencies

- **JDA 6.1.0** - Discord API wrapper
- **LavaPlayer 2.2.4** - Audio player library
- **LavaLink YouTube 1.14.0** - YouTube source
- **Spring Boot 3.5.8** - Application framework
- **Lombok** - Code generation