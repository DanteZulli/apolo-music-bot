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
/**
 * Configuration class for registering music bot commands with JDA.
 * 
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
public class PlayerCommandConfig {

    private final JDA jda;

    @PostConstruct
    public void registerCommands() {
        SlashCommandData playCommand = Commands.slash("play", "Plays a song from any source or adds it to the queue")
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.STRING, "query", "URL", true);
        
        SlashCommandData pauseCommand = Commands.slash("pause", "Pauses the current playback");
        
        SlashCommandData resumeCommand = Commands.slash("resume", "Resumes the paused playback");
        
        SlashCommandData stopCommand = Commands.slash("stop", "Stops the playback and clears the queue");
        
        SlashCommandData skipCommand = Commands.slash("skip", "Skips the current song and moves to the next one in the queue");

        jda.updateCommands()
                .addCommands(
                    playCommand,
                    pauseCommand,
                    resumeCommand,
                    stopCommand,
                    skipCommand
                )
                .queue(success -> log.info("Commands registered successfully"),
                        error -> log.error("Error registering commands: {}", error.getMessage()));
    }
}