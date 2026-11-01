package com.zullid.apolo_music_bot.listeners.commands;

import com.zullid.apolo_music_bot.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Listener for handling music-related slash commands.
 * <p>
 * This class extends {@link net.dv8tion.jda.api.hooks.ListenerAdapter} to listen
 * for slash command interactions in Discord guilds. It delegates command handling
 * to the {@link Player} instance based on the command name.
 * </p>
 *
 * @see Player
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerCommandListener extends ListenerAdapter {

    private static final String COMMAND_PLAY = "play";
    private static final String COMMAND_PAUSE = "pause";
    private static final String COMMAND_STOP = "stop";
    private static final String COMMAND_SKIP = "skip";
    private static final String COMMAND_RESUME = "resume";
    private static final String COMMAND_HELP = "help";
    private static final String COMMAND_QUEUE = "queue";
    private static final String MDC_KEY_COMMAND = "command";
    private static final String MDC_KEY_GUILD_ID = "guildId";
    private static final String MDC_KEY_USER_ID = "userId";
    private static final String MSG_UNKNOWN_COMMAND = "Command not recognized";
    private static final String LOG_UNKNOWN_COMMAND_FORMAT =
        "Unknown command received: {}";

    private final Player player;

    /**
     * Routes guild slash commands to the player and ignores non-guild or unknown commands.
     * <p>
     * Populates the logging context (command, guild and user ids) for the duration of the
     * handling so downstream logs can be correlated, then clears it because JDA reuses
     * listener threads.
     * </p>
     *
     * @param event the slash command interaction
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild()) return;

        MDC.put(MDC_KEY_COMMAND, event.getName());
        MDC.put(MDC_KEY_GUILD_ID, event.getGuild().getId());
        MDC.put(MDC_KEY_USER_ID, event.getUser().getId());
        try {
            switch (event.getName()) {
                case COMMAND_PLAY:
                    player.play(event);
                    break;
                case COMMAND_PAUSE:
                    player.pause(event);
                    break;
                case COMMAND_STOP:
                    player.stop(event);
                    break;
                case COMMAND_SKIP:
                    player.skip(event);
                    break;
                case COMMAND_RESUME:
                    player.resume(event);
                    break;
                case COMMAND_HELP:
                    player.help(event);
                    break;
                case COMMAND_QUEUE:
                    player.queue(event);
                    break;
                default:
                    log.warn(LOG_UNKNOWN_COMMAND_FORMAT, event.getName());
                    event
                        .reply(MSG_UNKNOWN_COMMAND)
                        .setEphemeral(true)
                        .queue();
            }
        } finally {
            MDC.clear();
        }
    }
}
