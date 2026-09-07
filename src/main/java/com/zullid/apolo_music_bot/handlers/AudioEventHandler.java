package com.zullid.apolo_music_bot.handlers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.zullid.apolo_music_bot.player.Player;
import com.zullid.apolo_music_bot.player.state.ReadyState;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler for audio player events.
 * <p>
 * This class extends {@link com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter}
 * to listen for audio events such as track start, end, pause, and resume.
 * </p>
 *
 * @see QueueService
 * @see AudioPlayerService
 * @see Player
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AudioEventHandler extends AudioEventAdapter {

    private final QueueService queueService;
    private final AudioPlayerService audioPlayerService;
    private final Player player;

    /**
     * Advances the queue when the ended track allows a next track.
     * <p>
     * When the queue is drained and nothing is playing, the player returns to
     * {@code ReadyState}. End reasons that do not start a next track (for example
     * {@code STOPPED} or {@code REPLACED}) are ignored.
     * </p>
     *
     * @param audioPlayer the LavaPlayer instance
     * @param track the track that ended
     * @param endReason why the track ended
     */
    @Override
    public void onTrackEnd(
        AudioPlayer audioPlayer,
        AudioTrack track,
        AudioTrackEndReason endReason
    ) {
        if (endReason.mayStartNext) {
            queueService.playNextTrack();
            if (
                queueService.isQueueEmpty() &&
                audioPlayerService.getPlayer().getPlayingTrack() == null
            ) {
                this.player.setState(new ReadyState(this.player));
            }
        }
    }

    /**
     * Logs the start of a track.
     *
     * @param player the LavaPlayer instance
     * @param track the track that started
     */
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        log.info("Started playing: {}", track.getInfo().title);
    }

    /**
     * Logs when playback is paused.
     *
     * @param player the LavaPlayer instance
     */
    @Override
    public void onPlayerPause(AudioPlayer player) {
        log.info("Player paused");
    }

    /**
     * Logs when playback is resumed.
     *
     * @param player the LavaPlayer instance
     */
    @Override
    public void onPlayerResume(AudioPlayer player) {
        log.info("Player resumed");
    }
}
