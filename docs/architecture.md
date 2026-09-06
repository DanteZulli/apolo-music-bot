# Architecture

[Index](DOCS.md)

Goal: orient developers in the codebase. Full diagram: [class-diagram.puml](class-diagram.puml).

## Player state pattern

- `Player` (context) delegates to a `PlayerState`.
- States: `ReadyState`, `PlayingState`, `PausedState`, each implementing `onPlay()`, `onPause()`, `onResume()`, `onStop()`, `onSkip()`.

## Package layout

```
src/main/java/com/zullid/apolo_music_bot/
├── ApoloMusicBotApplication.java
├── config/        # JDA, audio, command registration
├── services/      # AudioPlayerService, QueueService, VoiceChannelService
├── player/        # Player context
│   └── state/     # State implementations
├── listeners/
│   ├── commands/  # slash command listeners
│   └── events/    # JDA event listeners
└── handlers/      # LavaPlayer audio handlers
```

## Key versions

| Dependency | Version |
|------------|---------|
| Java | 25 |
| Spring Boot | 4.1.1 |
| JDA | 6.5.0 |
| LavaPlayer (`dev.arbjerg`) | 2.2.7 |
| YouTube source (`dev.lavalink.youtube:v2`) | 1.18.2 |
| JDave | 0.1.8 |

Voice requires DAVE encryption (JDA 6.3.2+ and JDave). Do not downgrade below these versions.

Code style and JDA practices live in [AGENTS.md](../AGENTS.md).
