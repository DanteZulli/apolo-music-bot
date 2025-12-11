package com.zullid.apolo_music_bot.player.state;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import com.zullid.apolo_music_bot.player.Player;

/**
 * State representing the player is ready but not playing.
 * <p>
 * In this state, the player can start playing a new track, but cannot pause,
 * resume, stop (already stopped), or skip (nothing playing).
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
public class ReadyState extends PlayerState {

    public ReadyState(Player player) {
        super(player);
    }

    @Override
    public void onPlay(SlashCommandInteractionEvent event) {
        player.setState(new PlayingState(player));
        player.play(event);
    }

    @Override
    public void onPause(SlashCommandInteractionEvent event) {
        event.reply("Cannot pause: nothing is playing.").queue();
    }

    @Override
    public void onResume(SlashCommandInteractionEvent event) {
        event.reply("Cannot resume: nothing is playing.").queue();
    }

    @Override
    public void onStop(SlashCommandInteractionEvent event) {
        event.reply("Already stopped.").queue();
    }

    @Override
    public void onSkip(SlashCommandInteractionEvent event) {
        event.reply("Cannot skip: nothing is playing.").queue();
    }
}
