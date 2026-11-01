package com.zullid.apolo_music_bot.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for registering music bot commands with JDA.
 * <p>
 * This class defines and registers slash commands for the music bot,
 * including play, pause, resume, stop, skip, queue and help, using the JDA API.
 * Commands are registered globally upon application startup.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class PlayerCommandConfig {

    private static final String COMMAND_PLAY = "play";
    private static final String DESC_PLAY =
        "Plays a song from any source or adds it to the queue";
    private static final String OPTION_QUERY = "query";
    private static final String DESC_QUERY_OPTION = "URL";
    private static final String COMMAND_PAUSE = "pause";
    private static final String DESC_PAUSE = "Pauses the current playback";
    private static final String COMMAND_RESUME = "resume";
    private static final String DESC_RESUME = "Resumes the paused playback";
    private static final String COMMAND_STOP = "stop";
    private static final String DESC_STOP =
        "Stops the playback and clears the queue";
    private static final String COMMAND_SKIP = "skip";
    private static final String DESC_SKIP =
        "Skips the current song and moves to the next one in the queue";
    private static final String COMMAND_HELP = "help";
    private static final String DESC_HELP =
        "Shows available commands and their descriptions";
    private static final String COMMAND_QUEUE = "queue";
    private static final String DESC_QUEUE = "Shows the current queue";
    private static final String LOG_REGISTERED =
        "Commands registered successfully";
    private static final String LOG_REGISTER_FAILED_FORMAT =
        "Error registering commands: {}";

    private final JDA jda;

    /**
     * Registers the global slash commands on application startup.
     */
    @PostConstruct
    public void registerCommands() {
        SlashCommandData playCommand = Commands.slash(
            COMMAND_PLAY,
            DESC_PLAY
        ).addOption(OptionType.STRING, OPTION_QUERY, DESC_QUERY_OPTION, true);

        SlashCommandData pauseCommand = Commands.slash(
            COMMAND_PAUSE,
            DESC_PAUSE
        );

        SlashCommandData resumeCommand = Commands.slash(
            COMMAND_RESUME,
            DESC_RESUME
        );

        SlashCommandData stopCommand = Commands.slash(
            COMMAND_STOP,
            DESC_STOP
        );

        SlashCommandData skipCommand = Commands.slash(
            COMMAND_SKIP,
            DESC_SKIP
        );

        SlashCommandData helpCommand = Commands.slash(
            COMMAND_HELP,
            DESC_HELP
        );

        SlashCommandData queueCommand = Commands.slash(
            COMMAND_QUEUE,
            DESC_QUEUE
        );

        jda.updateCommands()
            .addCommands(
                playCommand,
                pauseCommand,
                resumeCommand,
                stopCommand,
                skipCommand,
                helpCommand,
                queueCommand
            )
            .queue(
                success -> log.info(LOG_REGISTERED),
                error ->
                    log.error(
                        LOG_REGISTER_FAILED_FORMAT,
                        error.getMessage()
                    )
            );
    }
}
