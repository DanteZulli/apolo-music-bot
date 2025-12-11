package com.zullid.apolo_music_bot.listeners.commands;

import org.springframework.stereotype.Component;

import com.zullid.apolo_music_bot.player.Player;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild())
            return;

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
            default:
                event.reply("Command not recognized").setEphemeral(true).queue();
        }
    }

}