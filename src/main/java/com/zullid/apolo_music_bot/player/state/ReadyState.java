package com.zullid.apolo_music_bot.player.state;

import com.zullid.apolo_music_bot.player.Player;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

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

    /**
     * Creates a ready state for the given player.
     *
     * @param player the player context
     */
    public ReadyState(Player player) {
        super(player);
    }

    /**
     * Transitions to {@code PlayingState} and delegates playback to it.
     *
     * @param event the slash command interaction
     */
    @Override
    public void onPlay(SlashCommandInteractionEvent event) {
        player.setState(new PlayingState(player));
        player.play(event);
    }

    /**
     * Replies that nothing can be paused while idle.
     *
     * @param event the slash command interaction
     */
    @Override
    public void onPause(SlashCommandInteractionEvent event) {
        event.reply("Cannot pause: nothing is playing.").queue();
    }

    /**
     * Replies that nothing can be resumed while idle.
     *
     * @param event the slash command interaction
     */
    @Override
    public void onResume(SlashCommandInteractionEvent event) {
        event.reply("Cannot resume: nothing is playing.").queue();
    }

    /**
     * Replies that playback is already stopped.
     *
     * @param event the slash command interaction
     */
    @Override
    public void onStop(SlashCommandInteractionEvent event) {
        event.reply("Already stopped.").queue();
    }

    /**
     * Replies that nothing can be skipped while idle.
     *
     * @param event the slash command interaction
     */
    @Override
    public void onSkip(SlashCommandInteractionEvent event) {
        event.reply("Cannot skip: nothing is playing.").queue();
    }
}
