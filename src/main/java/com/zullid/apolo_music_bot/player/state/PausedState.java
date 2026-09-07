package com.zullid.apolo_music_bot.player.state;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.zullid.apolo_music_bot.player.Player;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * State representing the player is paused.
 * <p>
 * In this state, the player can resume playback, stop and clear the queue,
 * but cannot play new tracks, pause (already paused), or skip.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
public class PausedState extends PlayerState {

    private final AudioPlayerService audioPlayerService;
    private final QueueService queueService;

    /**
     * Creates a paused state for the given player.
     *
     * @param player the player context
     */
    public PausedState(Player player) {
        super(player);
        this.audioPlayerService = player.getAudioPlayerService();
        this.queueService = player.getQueueService();
    }

    /**
     * Replies that new tracks cannot start while paused.
     *
     * @param event the slash command interaction
     */
    @Override
    public void onPlay(SlashCommandInteractionEvent event) {
        event.reply("Cannot play while paused. Use resume.").queue();
    }

    /**
     * Replies that the player is already paused.
     *
     * @param event the slash command interaction
     */
    @Override
    public void onPause(SlashCommandInteractionEvent event) {
        event.reply("Player is already paused!").queue();
    }

    /**
     * Resumes playback and transitions to {@code PlayingState}.
     *
     * @param event the slash command interaction
     */
    @Override
    public void onResume(SlashCommandInteractionEvent event) {
        AudioPlayer audioPlayer = audioPlayerService.getPlayer();
        audioPlayer.setPaused(false);
        event.reply("Resumed the current track").queue();
        player.setState(new PlayingState(player));
    }

    /**
     * Stops playback, clears the queue and transitions to {@code ReadyState}.
     *
     * @param event the slash command interaction
     */
    @Override
    public void onStop(SlashCommandInteractionEvent event) {
        queueService.clearQueue();
        audioPlayerService.getPlayer().stopTrack();
        event.reply("Stopped playback and cleared the queue").queue();
        player.setState(new ReadyState(player));
    }

    /**
     * Replies that the current track cannot be skipped while paused.
     *
     * @param event the slash command interaction
     */
    @Override
    public void onSkip(SlashCommandInteractionEvent event) {
        event.reply("Cannot skip while paused. Use resume.").queue();
    }
}
