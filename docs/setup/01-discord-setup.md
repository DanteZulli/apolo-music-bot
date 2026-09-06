# 01. Discord Setup

[Setup](../setup.md) | Next: [Environment](02-environment.md)

Goal: create the Discord application and invite the bot to your server.

Prerequisites: a Discord account with permission to add bots to a server.

Detail: [JDA Getting Started](https://jda.wiki/using-jda/getting-started/), section [Creating a Discord Bot](https://jda.wiki/using-jda/getting-started/#creating-a-discord-bot).

## Steps

1. Go to the [Discord Developer Portal](https://discord.com/developers/applications).
2. Create a new application and configure the bot.
3. In the "Bot" section, enable these **Privileged Gateway Intents**:
   - Presence Intent
   - Server Members Intent
   - Message Content Intent
4. In the "OAuth2" section, generate an invite URL:
   - **Scopes**: `bot`, `applications.commands`
   - **Bot Permissions**: View Channels, Send Messages, Connect, Speak, Use Voice Activity
5. Open the generated URL to invite the bot to your server.

Next: [Environment](02-environment.md) to configure the token.
