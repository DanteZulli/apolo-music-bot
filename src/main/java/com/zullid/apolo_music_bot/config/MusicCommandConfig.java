package com.zullid.apolo_music_bot.config;

import org.springframework.context.annotation.Configuration;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MusicCommandConfig {

    private final JDA jda;

    @PostConstruct
    public void registerCommands() {
        SlashCommandData joinCommand = Commands.slash("join", "Connects the bot to your voice channel");
        
        SlashCommandData leaveCommand = Commands.slash("leave", "Disconnects the bot from the voice channel");
        
        SlashCommandData playCommand = Commands.slash("play", "Plays a song from any source or adds it to the queue")
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.STRING, "query", "URL or song name", true);
        
        SlashCommandData pauseCommand = Commands.slash("pause", "Pauses the current playback");
        
        SlashCommandData resumeCommand = Commands.slash("resume", "Resumes the paused playback");
        
        SlashCommandData stopCommand = Commands.slash("stop", "Stops the playback and clears the queue");
        
        SlashCommandData skipCommand = Commands.slash("skip", "Skips the current song and moves to the next one in the queue");
        
        SlashCommandData queueCommand = Commands.slash("queue", "Displays the current song queue");
        
        SlashCommandData helpCommand = Commands.slash("help", "Displays a list of available commands and their usage");

        jda.updateCommands()
                .addCommands(
                    joinCommand,
                    leaveCommand,
                    playCommand,
                    pauseCommand,
                    resumeCommand,
                    stopCommand,
                    skipCommand,
                    queueCommand,
                    helpCommand
                )
                .queue(success -> log.info("Commands registered successfully"),
                        error -> log.error("Error registering commands: {}", error.getMessage()));
    }
}