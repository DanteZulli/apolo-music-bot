# 2. Environment

[Index](DOCS.md) | Prev: [Discord Setup](01-discord-setup.md) | Next: [Run](03-run.md)

Goal: provide the bot token to the application.

Prerequisites: phase 1 done (bot exists, you have its token). Required for both local and container runs.

## Setup

```bash
cp .envrc.sample .envrc
# then edit .envrc and set your token
```

`.envrc` content:

```bash
export DISCORD_BOT_TOKEN=your_discord_token_here
```

With [direnv](https://direnv.net/) installed, run `direnv allow` once. Without direnv, export the variable manually:

```bash
export DISCORD_BOT_TOKEN=your_discord_token_here
```

`docker-compose.yml` reads the token from the environment (`DISCORD_BOT_TOKEN=${DISCORD_BOT_TOKEN}`), so no token belongs in properties or compose files.

Next: [Run](03-run.md).
