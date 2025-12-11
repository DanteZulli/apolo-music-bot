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
    protected Player player;

    protected PlayerState(Player player) {
        this.player = player;
    }

    public abstract void onPlay(SlashCommandInteractionEvent event);

    public abstract void onPause(SlashCommandInteractionEvent event);

    public abstract void onResume(SlashCommandInteractionEvent event);

    public abstract void onStop(SlashCommandInteractionEvent event);

    public abstract void onSkip(SlashCommandInteractionEvent event);
}
