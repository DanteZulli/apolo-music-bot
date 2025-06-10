<!-- PROJECT SHIELDS -->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![GPL v3 License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/DanteZulli/apolo-music-bot">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Apolo Music Bot</h3>
  <p align="center">
    <a href="https://www.java.com/"><img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"></a>
    <a href="https://spring.io/"><img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring"></a>
    <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white" alt="Maven"></a>
  </p>

  <p align="center">
     A self-hosted, feature-rich music bot for Discord, built with Java using <a href="https://github.com/DV8FromTheWorld/JDA">JDA</a> and <a href="https://github.com/lavalink-devs/lavaplayer">LavaPlayer</a>. Stream your favorite tunes seamlessly!
    <br />
    <a href="https://github.com/DanteZulli/apolo-music-bot"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/DanteZulli/apolo-music-bot/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    &middot;
    <a href="https://github.com/DanteZulli/apolo-music-bot/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>

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

## Project Setup

1. Clone the repository:
```bash
git clone https://github.com/DanteZulli/apolo-music-bot.git
cd apolo-music-bot
```

2. Create an `application.properties` file in `src/main/resources/` with your Discord bot token:
```properties
discord.bot.token=your_discord_token_here
```

> [!NOTE]
> For development purposes, you can create an `application-dev.properties` file (which is gitignored) to override the default configuration. This is useful for keeping your development token separate from the main configuration. You can also use environment variables or other Spring Boot configuration methods.

3. Build and run the project:
```bash
mvn clean install
mvn spring-boot:run
```

## Usage

- `/join`: Connects the bot to your voice channel
- `/leave`: Disconnects the bot from the voice channel
- `/play <query>`: Plays a song from any source or adds it to the queue
- `/pause`: Pauses the current playback
- `/resume`: Resumes the paused playback
- `/stop`: Stops the playback and clears the queue
- `/skip`: Skips the current song and moves to the next one in the queue
- `/queue`: Displays the current song queue
- `/help`: Displays a list of available commands and their usage

## Acknowledgments

If you like this bot or find the project interesting, don't forget to check out the libraries that made it possible and drop them a star.

* [JDA](https://github.com/DV8FromTheWorld/JDA) - The Java library for Discord API
* [LavaPlayer](https://github.com/lavalink-devs/lavaplayer) - Audio player library for Discord bots
* [Spring Boot](https://spring.io/projects/spring-boot) - The framework used for building the application
* [Discord Developer Portal](https://discord.com/developers/applications) - For bot creation and management

Special shoutout to the creators of [JMusicBot](https://github.com/jagrosh/MusicBot) and [FredBoat](https://github.com/freyacodes/archived-bot/), whose open-source projects were a great reference and learning resource.

## License

This project is licensed under the GPL v3 License. See the `LICENSE` file for details.

<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/DanteZulli/apolo-music-bot?style=for-the-badge
[contributors-url]: https://github.com/DanteZulli/apolo-music-bot/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/DanteZulli/apolo-music-bot?style=for-the-badge
[forks-url]: https://github.com/DanteZulli/apolo-music-bot/network/members
[stars-shield]: https://img.shields.io/github/stars/DanteZulli/apolo-music-bot?style=for-the-badge
[stars-url]: https://github.com/DanteZulli/apolo-music-bot/stargazers
[issues-shield]: https://img.shields.io/github/issues/DanteZulli/apolo-music-bot.svg?style=for-the-badge
[issues-url]: https://github.com/DanteZulli/apolo-music-bot/issues
[license-shield]: https://img.shields.io/github/license/DanteZulli/apolo-music-bot.svg?style=for-the-badge
[license-url]: https://github.com/DanteZulli/apolo-music-bot/blob/master/LICENSE
[linkedin-shield]: https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white
[linkedin-url]: https://www.linkedin.com/in/dante-zulli/ 
