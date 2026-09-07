# Apolo Music Bot — Setup Guide

Everything you need to get the bot running. For how the code fits together, see [Architecture](architecture.md).

## Index

- [Bot setup on Discord Developer Portal](#bot-setup-on-discord-developer-portal)
- [Environment](#environment)
- [Run](#run)
  - [Local execution](#local-execution)
  - [Docker / Podman](#docker--podman)
- [Troubleshooting](troubleshooting.md)

## Bot Setup on Discord Developer Portal

> [!IMPORTANT]
> For a more detailed guide on setting up your Discord bot, we recommend checking out the [JDA Getting Started Guide](https://jda.wiki/using-jda/getting-started/), specifically the [Creating a Discord Bot](https://jda.wiki/using-jda/getting-started/#creating-a-discord-bot) section.

1. Go to the [Discord Developer Portal](https://discord.com/developers/applications)
2. Create a new application and configure the bot
   > **Note:** Feel free to customize your bot's name, avatar, and description to match your preferences!
3. In the "Bot" section, enable the following **Privileged Gateway Intents**:
   - Presence Intent
   - Server Members Intent
   - Message Content Intent
4. In the "OAuth2" section, generate an invite URL with the following permissions:
   - **Scopes**: `bot`, `applications.commands`
   - **Bot Permissions**:
     - View Channels
     - Send Messages
     - Connect
     - Speak
     - Use Voice Activity
5. Use the generated URL to invite the bot to your server

## Environment

1. Clone the repository:
```bash
git clone https://github.com/DanteZulli/apolo-music-bot.git
cd apolo-music-bot
```

2. Configure your environment:

We recommend using [direnv](https://direnv.net/) with a `.envrc` file for a more convenient setup (over than overriding token values in `docker-compose.yml` or `.properties` files). This configuration is required for both local execution and Docker/Podman.

```bash
# .envrc
export DISCORD_BOT_TOKEN=your_discord_token_here
```

## Run

Pick one option.

### Local Execution

Build and run the project using Gradle:
```bash
./gradlew build
./gradlew bootRun
```

### Docker / Podman

The project is fully dockerized. You can build and run the bot using Docker Compose or Podman Compose:

```bash
# Build and start in background
docker compose up --build -d

# Using Podman
podman compose up --build -d
```

Stuck? See [Troubleshooting](troubleshooting.md).
