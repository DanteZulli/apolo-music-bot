package com.zullid.apolo_music_bot.player.state;

import com.zullid.apolo_music_bot.player.Player;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Abstract base class for player states in the State pattern.
 * <p>
 * Defines the interface for handling slash commands based on the current playback state.
 * Each concrete state implements these methods to provide state-specific behavior.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
public abstract class PlayerState {

    /** Player context whose behavior this state controls. */
    protected Player player;

    /**
     * Creates a state bound to the given player context.
     *
     * @param player the player whose behavior this state controls
     */
    protected PlayerState(Player player) {
        this.player = player;
    }

    /**
     * Handles the play command in the current state.
     *
     * @param event the slash command interaction
     */
    public abstract void onPlay(SlashCommandInteractionEvent event);

    /**
     * Handles the pause command in the current state.
     *
     * @param event the slash command interaction
     */
    public abstract void onPause(SlashCommandInteractionEvent event);

    /**
     * Handles the resume command in the current state.
     *
     * @param event the slash command interaction
     */
    public abstract void onResume(SlashCommandInteractionEvent event);

    /**
     * Handles the stop command in the current state.
     *
     * @param event the slash command interaction
     */
    public abstract void onStop(SlashCommandInteractionEvent event);

    /**
     * Handles the skip command in the current state.
     *
     * @param event the slash command interaction
     */
    public abstract void onSkip(SlashCommandInteractionEvent event);
}
