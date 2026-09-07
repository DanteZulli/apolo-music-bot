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

        MDC.put("command", event.getName());
        MDC.put("guildId", event.getGuild().getId());
        MDC.put("userId", event.getUser().getId());
        try {
            switch (event.getName()) {
                case "play":
                    player.play(event);
                    break;
                case "pause":
                    player.pause(event);
                    break;
                case "stop":
                    player.stop(event);
                    break;
                case "skip":
                    player.skip(event);
                    break;
                case "resume":
                    player.resume(event);
                    break;
                case "help":
                    player.help(event);
                    break;
                case "queue":
                    player.queue(event);
                    break;
                default:
                    log.warn("Unknown command received: {}", event.getName());
                    event
                        .reply("Command not recognized")
                        .setEphemeral(true)
                        .queue();
            }
        } finally {
            MDC.clear();
        }
    }
}
