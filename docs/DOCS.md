# Apolo Music Bot — Setup Guide

Everything you need to get the bot running. For how the code fits together, see [Architecture](architecture.md).

## Index

- [Discord setup](#discord-setup)
- [Environment](#environment)
- [Run](#run)
  - [Local](#local-gradle)
  - [Container](#container-docker--podman)
- [Troubleshooting](#troubleshooting)

## Discord Setup

1. Go to the [Discord Developer Portal](https://discord.com/developers/applications).
2. Create a new application and configure the bot.
3. In the "Bot" section, enable these **Privileged Gateway Intents**: Presence Intent, Server Members Intent, Message Content Intent.
4. In the "OAuth2" section, generate an invite URL with scopes `bot` and `applications.commands`, and bot permissions View Channels, Send Messages, Connect, Speak, Use Voice Activity.
5. Open the generated URL to invite the bot to your server.

Detail: [JDA Getting Started](https://jda.wiki/using-jda/getting-started/), section [Creating a Discord Bot](https://jda.wiki/using-jda/getting-started/#creating-a-discord-bot).

## Environment

The bot needs a `DISCORD_BOT_TOKEN` for both local and container runs:

```bash
cp .envrc.sample .envrc
# then edit .envrc and set your token
```

```bash
export DISCORD_BOT_TOKEN=your_discord_token_here
```

With [direnv](https://direnv.net/) installed, run `direnv allow` once. `docker-compose.yml` reads the token from the environment, so no token belongs in properties or compose files.

## Run

Pick one option.

### Local (Gradle)

Requires Java 25.

```bash
./gradlew build
./gradlew bootRun
```

### Container (Docker / Podman)

```bash
# Build and start in background
docker compose up --build -d

# Using Podman
podman compose up --build -d
```

Image references use full registry paths so they work on [Podman](https://podman.io/) and environments without `unqualified-search-registries` (e.g. [Debian](https://wiki.debian.org/Podman)).

## Troubleshooting

**Bot runs an old version after code changes.** Rebuild the image (`compose up --build -d`) or remove the stale image first (`podman image rm apolo-music-bot:latest`).

**Voice connection fails.** Discord requires DAVE (end-to-end encryption); do not downgrade JDA below 6.3.2 or drop JDave, or voice will fail. Pinned versions are listed in [Architecture](architecture.md).
