# Docs Index

Start here. Follow the phases in order. Each file states its goal and prerequisites so it can be read standalone or expanded later.

| # | Phase | File | Outcome |
|---|-------|------|---------|
| 1 | Discord setup | [01-discord-setup.md](01-discord-setup.md) | Bot application created and invited to your server |
| 2 | Environment | [02-environment.md](02-environment.md) | `DISCORD_BOT_TOKEN` configured |
| 3 | Run | [03-run.md](03-run.md) | Bot running locally or in a container |
| 4 | Commands | [04-commands.md](04-commands.md) | Usage reference for all slash commands |
| 5 | Architecture | [05-architecture.md](05-architecture.md) | How the code fits together |
| 6 | Troubleshooting | [06-troubleshooting.md](06-troubleshooting.md) | Fixes for common failures |

Developer conventions (code style, JDA practices, testing) live in [AGENTS.md](../AGENTS.md) and are not duplicated here.

## Acknowledgments

Libraries that make this bot possible:

* [JDA](https://github.com/DV8FromTheWorld/JDA) - Java library for the Discord API
* [LavaPlayer](https://github.com/lavalink-devs/lavaplayer) - audio player library, with the [youtube-source](https://github.com/lavalink-devs/youtube-source) manager (`dev.lavalink.youtube:v2`)
  * Thanks to the [original LavaPlayer](https://github.com/sedmelluq/lavaplayer), which inspired this bot before the migration to the fork
* [JMusicBot](https://github.com/jagrosh/MusicBot) and [FredBoat](https://github.com/freyacodes/archived-bot/) - open-source references and learning resources

## License

GPL v3. See the `LICENSE` file for details.
